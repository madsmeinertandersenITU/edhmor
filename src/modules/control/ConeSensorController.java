package modules.control;

import coppelia.BoolW;
import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.StringWA;
import coppelia.remoteApi;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.util.SimulationConfiguration;
import mpi.MPI;

public class ConeSensorController extends RobotController {

    boolean usePhaseControl = SimulationConfiguration.isUsePhaseControl();
    boolean useAngularFreqControl = SimulationConfiguration.isUseAngularFControl();
    boolean useAmplitudeControl = SimulationConfiguration.isUseAmplitudeControl();

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

        for (int module = 0; module < moduleHandlers.size(); module++) {
            // FIXME: could have more control parameters than actuators with modules with
            // more dof
            maxAmplitude[module] = ModuleSetFactory.getModulesSet().getModulesMaxAmplitude(moduleType[module]);
            maxAngFreq[module] = ModuleSetFactory.getModulesSet().getModulesMaxAngularFrequency(moduleType[module]);
        }
        if (SimulationConfiguration.isUseMPI()) {
            rank = MPI.COMM_WORLD.Rank();
        }
    }

    @Override
    public boolean updateJoints(double time) {
        // TODO: Remove the first module (as it is a base module without actuators)

        coppeliaSimApi.simxPauseCommunication(clientID, true);

        for (int module = 0; module < moduleHandlers.size(); module++) {
            // TODO: this iterates for each module but we should also iterate for
            // each actuator in the module. For now, just suppose that all the
            // modules have only one dof.

            float targetPosition;
            if (usePhaseControl && !useAngularFreqControl && !useAmplitudeControl) {
                targetPosition = (float) (maxAmplitude[module]
                        * Math.sin(maxAngFreq[module] * time + phaseControl[module] / 180. * Math.PI));
            } else {
                double amplitude = maxAmplitude[module] * amplitudeControl[module];
                double angularFreq = maxAngFreq[module] * andularFreqControl[module];
                targetPosition = (float) (amplitude
                        * Math.sin(angularFreq * time + phaseControl[module] / 180. * Math.PI));
            }
            int ret;

            ret = coppeliaSimApi.simxSetJointTargetPosition(clientID, moduleHandlers.get(module) + 2,
                    targetPosition, remoteApi.simx_opmode_oneshot);
            if (ret == remoteApi.simx_return_ok || ret == remoteApi.simx_return_novalue_flag) {
                System.out.format("Updating module: " + module);
            } else {
                System.out.format(
                        "%d: updateJoints Function: Remote API function call returned with error code %d when updating joint %d at time%f\n",
                        rank, ret, module, time);

                return false;
            }
        }

        coppeliaSimApi.simxPauseCommunication(clientID, false);
        return true;
    }

}
