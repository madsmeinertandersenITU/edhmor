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

    // DataLogger sensorValueLogger = new DataLogger(
    // "C:\\ITU\\ResearchProject\\EMERGEAdittion\\emergeAddition\\EDHMOR\\edhmor\\src\\modules\\control\\distance.csv",
    // "Time,Distance");
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
                if (module.connectedModules.size() == 1) {
                    legs.add(new Leg(module.connectedModules.getFirst(), module));
                }
            }
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

        for (Leg leg : legs) {
            modules.individual.Module topModule = leg.topPart;
            modules.individual.Module bottomModule = leg.bottomPart;

            // Assuming Module class has fields like `int id` and `int type`
            int retTop;
            int retBottom;
            // IntW detectedObjectHandle = new IntW(0);
            // FloatWA detectedPoint = new FloatWA(6);
            // BoolW detectionState = new BoolW(true);
            // FloatWA detectedSurfaceNormalVector = new FloatWA(6);
            // int operationMode = remoteApi.simx_opmode_continuous;

            float topTargetPosition = calcTargetPosition(topModule, time);

            float bottomTargetPosition = calcTargetPosition(bottomModule, time);

            retTop = coppeliaSimApi.simxSetJointTargetPosition(clientID, topModule.id + 2,
                    topTargetPosition, remoteApi.simx_opmode_oneshot);

            retBottom = coppeliaSimApi.simxSetJointTargetPosition(clientID, bottomModule.id + 2,
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
}
