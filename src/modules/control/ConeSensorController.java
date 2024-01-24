package modules.control;

import java.util.ArrayList;
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
import modules.util.SimulationConfiguration;
import mpi.MPI;

public class ConeSensorController extends RobotController {
    private boolean isModule1PositionSet = false;
    // DataLogger dataLogger = new DataLogger(
    // "C:/ITU/ResearchProject/EMERGEAdittion/emergeAddition/EDHMOR/edhmor/src/modules/control/joint_and_sensor_data.csv",
    // "Time,Joint,ProximitySensor");

    DataLogger sensorValueLogger = new DataLogger(
            "C:\\ITU\\ResearchProject\\EMERGEAdittion\\emergeAddition\\EDHMOR\\edhmor\\src\\modules\\control\\distance.csv",
            "Time,Distance");
    boolean usePhaseControl = SimulationConfiguration.isUsePhaseControl();
    boolean useAngularFreqControl = SimulationConfiguration.isUseAngularFControl();
    boolean useAmplitudeControl = SimulationConfiguration.isUseAmplitudeControl();
    boolean isObjectDetected = false;

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

        int index = 0;
        Set<Integer> keys = moduleHandlers.keySet();
        List<Integer> keyList = new ArrayList<>(keys);
        for (Map.Entry<Integer, Integer> entry : moduleHandlers.entrySet()) {
            int module = index;
            if (entry.getValue() == null) {
                continue;
            }
            int moduleHandler = entry.getValue();

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
            IntW detectedObjectHandle = new IntW(0);
            FloatWA detectedPoint = new FloatWA(6);
            BoolW detectionState = new BoolW(true);
            FloatWA detectedSurfaceNormalVector = new FloatWA(6);
            int operationMode = remoteApi.simx_opmode_continuous;
            Integer keyNumber = keyList.get(module);

            if (moduleHandler == 1) {
                // if (isObjectDetected) {
                // float jointTargetPosition = 1.55f;
                // float module1Position = getJointPosition(moduleHandlers.get(1) + 2);
                // float module2Position = getJointPosition(moduleHandlers.get(2) + 2);

                // if (!isModule1PositionSet && !areFloatsEqual(module1Position,
                // jointTargetPosition)) {
                // setTargetPosition(1, jointTargetPosition);
                // } else if (areFloatsEqual(module1Position, jointTargetPosition)
                // && !areFloatsEqual(module2Position, jointTargetPosition)) {
                // isModule1PositionSet = true;
                // setTargetPosition(2, jointTargetPosition);
                // } else if (areFloatsEqual(module2Position, jointTargetPosition)
                // && !areFloatsEqual(module1Position, -jointTargetPosition)) {
                // setTargetPosition(1, -jointTargetPosition);
                // } else if (areFloatsEqual(module1Position, -jointTargetPosition)
                // && !areFloatsEqual(module2Position, -jointTargetPosition)) {
                // setTargetPosition(2, -jointTargetPosition);
                // } else if (areFloatsEqual(module2Position, -jointTargetPosition)
                // && !areFloatsEqual(module1Position, jointTargetPosition)) {
                // setTargetPosition(1, jointTargetPosition);
                // }

                // // dataLogger.logData(System.currentTimeMillis(),
                // // getJointPosition(moduleHandlers.get(module) + 2),
                // // 1);

                ret = coppeliaSimApi.simxSetJointTargetPosition(clientID, keyNumber + 2,
                        targetPosition, remoteApi.simx_opmode_oneshot);
                if (ret == remoteApi.simx_return_ok || ret == remoteApi.simx_return_novalue_flag) {
                    // System.out.format("Updating module: " + module);
                } else {
                    System.out.format(
                            "%d: updateJoints Function: Remote API function call returned with error code %d when updating joint %d at time%f\n",
                            rank, ret, module, time);

                    return false;
                }
                // } else {
                // dataLogger.logData(System.currentTimeMillis(),
                // getJointPosition(moduleHandlers.get(module) + 2),
                // 0);
                // }
            } else if (moduleHandler == 2) {
                ret = coppeliaSimApi.simxReadProximitySensor(clientID,
                        keyNumber + 2,
                        detectionState,
                        detectedPoint, detectedObjectHandle, detectedSurfaceNormalVector,
                        operationMode);

                if (ret == remoteApi.simx_return_ok || ret == remoteApi.simx_return_novalue_flag) {
                    if (detectionState.getValue()) {
                        sensorValueLogger.logDistance(System.currentTimeMillis(), detectedPoint.getArray()[2]);

                        isObjectDetected = true;
                    } else {
                        isObjectDetected = false;
                        // System.out.println("No object detected in front of the sensor.");
                    }
                } else {
                    System.out.format(
                            "%d: readProximity Function: Remote API function call returned with error code %d when updating joint %d at time%f\n",
                            rank, ret, module, time);
                }
            }

            index++;
        }
        sensorValueLogger.flush();

        coppeliaSimApi.simxPauseCommunication(clientID, false);
        return true;
    }

    private void setTargetPosition(int module, float targetPosition) {
        int ret;
        ret = coppeliaSimApi.simxSetJointTargetPosition(clientID, moduleHandlers.get(module) + 2,
                targetPosition, remoteApi.simx_opmode_oneshot);
        if (ret == remoteApi.simx_return_ok || ret == remoteApi.simx_return_novalue_flag) {
            // System.out.format("Updating module: " + module);
        } else {
            System.out.format(
                    "%d: updateJoints Function: Remote API function call returned with error code %d when updating joint %d\n",
                    rank, ret, module);
        }
    }

    private float getJointPosition(int jointHandle) {

        FloatW jointPosition = new FloatW(1);
        int ret = coppeliaSimApi.simxGetJointPosition(clientID, jointHandle, jointPosition,
                remoteApi.simx_opmode_streaming);
        if (ret == remoteApi.simx_return_ok || ret == remoteApi.simx_return_novalue_flag) {
            return jointPosition.getValue();
        } else {
            System.out.format("Error getting position for joint %d\n", jointHandle);
            return Float.NaN; // Return NaN to indicate an error
        }
    }

    private static boolean areFloatsEqual(float float1, float float2) {
        return Math.abs(float1 - float2) <= 1f;
    }
}
