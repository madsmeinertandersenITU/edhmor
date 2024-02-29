package modules.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import coppelia.BoolW;
import coppelia.FloatW;
import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.StringWA;
import coppelia.remoteApi;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.individual.CPG;
import modules.individual.Leg;
import modules.individual.Module;
import modules.util.SimulationConfiguration;
import mpi.MPI;

public class ConeSensorController extends RobotController {
    // DataLogger dataLogger = new DataLogger(
    // "C:/ITU/ResearchProject/EMERGEAdittion/emergeAddition/EDHMOR/edhmor/src/modules/control/joint_and_sensor_data.csv",
    // "Time,Joint,ProximitySensor");

    DataLogger sensorValueLogger = new DataLogger(
            "C:\\ITU\\ResearchProject\\EMERGEAdittion\\emergeAddition\\EDHMOR\\edhmor\\src\\modules\\control\\leg.csv",
            "TopPosition,BottomPosition");
    boolean usePhaseControl = SimulationConfiguration.isUsePhaseControl();
    boolean useAngularFreqControl = SimulationConfiguration.isUseAngularFControl();
    boolean useAmplitudeControl = SimulationConfiguration.isUseAmplitudeControl();
    double cpgPhaseOffset = Math.PI / 2; // Phase offset between joints (for demonstration)
    boolean isObjectDetected = false;
    ArrayList<Leg> legs = new ArrayList<>();
    int[] phaseControl, moduleType;
    double[] amplitudeControl, andularFreqControl;

    double[] maxAmplitude = new double[moduleHandlers.size()]; // FIXME: could have more control parameters than
                                                               // actuators with modules with more dof
    double[] maxAngFreq = new double[moduleHandlers.size()]; // FIXME: could have more control parameters than actuators
                                                             // with modules with more dof

    private int rank = 0;

    public ConeSensorController(remoteApi api, int clientID, CoppeliaSimCreateRobot robot) {
        super(api, clientID, robot);
        phaseControl = robot.getPhaseControl();
        moduleType = robot.getModuleType();
        amplitudeControl = robot.getAmplitudeControl();
        andularFreqControl = robot.getAngularFreqControl();

        for (modules.individual.Module module : moduleHandlers) {
            if (module.type == 1) { // Assuming type 1 is for emerge modules with actuators
                // Initialize CPGs with individual parameters if needed
                if (module.connectedModules.size() == 1 && module.type == 1) {
                    legs.add(new Leg(module.connectedModules.getFirst(), module));
                }
            }
        }

        System.out.println("Current Legs Configuration:");
        for (Leg leg : legs) {
            leg.topPart.phase = 0;
            leg.bottomPart.phase = Math.PI;
            System.out.println(leg.toString());
        }

        for (int module = 0; module < moduleHandlers.size(); module++) {
            // FIXME: could have more control parameters than actuators with modules with
            // more dof
            maxAmplitude[module] = ModuleSetFactory.getModulesSet().getModulesMaxAmplitude(moduleType[module]);
            maxAngFreq[module] = ModuleSetFactory.getModulesSet().getModulesMaxAngularFrequency(moduleType[module]);
        }
        if (SimulationConfiguration.isUseMPI()) {
            rank = MPI.COMM_WORLD.Rank();
        }
        // this.removeBase();
    }

    @Override
    public boolean updateJoints(double time) {
        // TODO: Remove the first module (as it is a base module without actuators)

        coppeliaSimApi.simxPauseCommunication(clientID, true);
        double dt = 0.05; // Define your timestep

        coppeliaSimApi.simxSetJointTargetPosition(clientID, 18 + 2,
                -180, remoteApi.simx_opmode_oneshot);

        for (Leg leg : legs) {
            modules.individual.Module topModule = leg.topPart;
            modules.individual.Module bottomModule = leg.bottomPart;

            // Simplified parameters for demonstration
            double omega = 1.0; // Natural frequency
            double K = 0.6; // Coupling strength
            double phi = Math.PI / 1.5; // Desired phase difference

            // Update phases with RK4
            updatePhaseWithRK4(topModule, omega, K, phi, bottomModule.phase, dt);
            updatePhaseWithRK4(bottomModule, omega, K, phi, topModule.phase, dt);

            // Now compute target positions based on updated phases
            // You can use your existing calcTargetPosition method,
            // or modify it to use the phase directly
            float topTargetPosition = calcTargetPositionBasedOnPhase(topModule);
            float bottomTargetPosition = calcTargetPositionBasedOnPhase(bottomModule);

            sensorValueLogger.logPosition(topTargetPosition, bottomTargetPosition);

            int retTop = coppeliaSimApi.simxSetJointTargetPosition(clientID, topModule.id + 2,
                    topTargetPosition, remoteApi.simx_opmode_oneshot);

            int retBottom = coppeliaSimApi.simxSetJointTargetPosition(clientID, bottomModule.id + 2,
                    bottomTargetPosition, remoteApi.simx_opmode_oneshot);

            if ((retTop == remoteApi.simx_return_ok && retBottom == remoteApi.simx_return_ok)
                    || (retTop == remoteApi.simx_return_novalue_flag
                            && retBottom == remoteApi.simx_return_novalue_flag)) {
                // System.out.println("Updating module: " + keyNumber + 2);
            } else {
                // System.out.format(
                // "%d: updateJoints Function: Remote API function call returned with error code
                // %d when updating joint %d at time%f\n",
                // rank, ret, module, time);

                return false;
            }

        }

        sensorValueLogger.flush();

        coppeliaSimApi.simxPauseCommunication(clientID, false);
        return true;
    }

    private float calcTargetPosition(Module module, double time) {
        float targetPosition;
        if (usePhaseControl && !useAngularFreqControl && !useAmplitudeControl) {
            targetPosition = (float) (maxAmplitude[moduleHandlers.indexOf(module)]
                    * Math.sin(maxAngFreq[moduleHandlers.indexOf(module)] * time
                            + phaseControl[moduleHandlers.indexOf(module)] / 180. * Math.PI));
        } else {
            double amplitude = maxAmplitude[moduleHandlers.indexOf(module)]
                    * amplitudeControl[moduleHandlers.indexOf(module)];
            double angularFreq = maxAngFreq[moduleHandlers.indexOf(module)]
                    * andularFreqControl[moduleHandlers.indexOf(module)];
            targetPosition = (float) (amplitude
                    * Math.sin(angularFreq * time + phaseControl[moduleHandlers.indexOf(module)] / 180. * Math.PI));
        }

        return targetPosition;
    }

    // Inside ConeSensorController class

    // Add a method to compute the differential equation for the CPG
    private double dThetaDt(Module module, double omega, double K, double phi, double thetaOther) {
        // Example calculation for dTheta/dt = omega + K * sin(thetaOther - theta - phi)
        // For simplicity, we're assuming a direct coupling with just one other module
        // in the leg.
        // `thetaOther` is the phase of the other module in the same leg.
        double theta = module.phase; // Assuming `Module` class has a `phase` field to store its current phase
        return omega + K * Math.sin(thetaOther - theta - phi);
    }

    private void updatePhaseWithRK4(Module module, double omega, double K, double phi, double thetaOther, double dt) {
        // Phase velocity difference adjustment
        double theta = module.phase; // Current phase of this module
        double thetaVelocity = omega; // For simplification, using omega as base phase velocity
        double thetaOtherVelocity = omega; // Assuming you calculate this for the other module similarly

        // Calculate the RK4 terms, including the coupling effect and phase difference
        double k1 = dThetaDt(module, omega, K, phi, thetaOther) - thetaVelocity + thetaOtherVelocity;
        double k2 = dThetaDt(module, omega, K, phi, thetaOther + dt / 2.0 * k1) - thetaVelocity + thetaOtherVelocity;
        double k3 = dThetaDt(module, omega, K, phi, thetaOther + dt / 2.0 * k2) - thetaVelocity + thetaOtherVelocity;
        double k4 = dThetaDt(module, omega, K, phi, thetaOther + dt * k3) - thetaVelocity + thetaOtherVelocity;

        // Incorporate the phase velocity difference into the phase update
        double dTheta = (k1 + 2 * k2 + 2 * k3 + k4) / 6.0;
        module.phase += dTheta * dt; // Adjust the phase based on the velocity difference
    }

    private float calcTargetPositionBasedOnPhase(Module module) {
        // Assuming the phase is already being updated elsewhere in your code

        // Calculate the target position as a sine wave, scaled to the joint's motion
        // range
        // First, normalize the phase to a value between 0 and 2*PI to ensure it's
        // within a single cycle
        double normalizedPhase = module.phase % (2 * Math.PI);

        // Then, use the sine of this phase to generate a wave-like pattern
        double wavePattern = Math.sin(normalizedPhase);

        // Scale this pattern to fit the joint's motion range (-180 to 180 degrees, or
        // -PI to PI radians)
        // Note: The range of Math.sin() is from -1 to 1, so multiplying by PI directly
        // scales to -PI to PI
        // Convert radians to degrees if necessary, depending on your simulation's
        // requirements
        float targetPositionRadians = (float) (wavePattern * Math.PI);

        // If your simulation expects degrees, convert from radians to degrees
        float targetPositionDegrees = (float) Math.toDegrees(targetPositionRadians);

        return targetPositionDegrees;
    }

}
