/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2016 GII (UDC) and REAL (ITU)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package modules.evaluation.dynamicFeatures;

import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Vector3d;

import modules.evaluation.CoppeliaSimCreateRobot;
import modules.util.SimulationConfiguration;
import mpi.MPI;

/**
 * DynamicFeaturesEvaluator.java Created on 23/10/2019
 *
 * @author Andres Faiña <anfv at itu.dk>
 */
public class DynamicFeaturesEvaluator {

    protected double accumRoll = 0, accumPitch = 0, time = 0;

    protected DynamicFeatures dFeatures;
    protected remoteApi coppeliaSimApi;
    protected int clientID;
    protected CoppeliaSimCreateRobot robot;
    private int rank = 0;

    public DynamicFeaturesEvaluator(remoteApi api, int clientID, CoppeliaSimCreateRobot robot) {
        this.dFeatures = new DynamicFeatures();
        this.coppeliaSimApi = api;
        this.clientID = clientID;
        this.robot = robot;
        if (SimulationConfiguration.isUseMPI()) {
            rank = MPI.COMM_WORLD.Rank();
        }
    }

    public boolean init() {
        // Start the streaming of the base
        return getBaseRotation(remoteApi.simx_opmode_streaming) != null;
    }

    public boolean update(double time) {
        Vector3d baseOrientation = getBaseRotation(remoteApi.simx_opmode_buffer);
        if (baseOrientation != null) {
            accumRoll = accumRoll + Math.abs(baseOrientation.x);
            accumPitch = accumPitch + Math.abs(baseOrientation.y);
            this.time = time;
            return true;
        }
        return false;
    }

    public boolean end() {
        // Stop the streaming of the base
        boolean success = getBaseRotation(remoteApi.simx_opmode_discontinue) != null;
        // We need a blocking operation after this, it is done in getBrokenConnections

        int nbc = getBrokenConnections();
        this.dFeatures.setBrokenConnections(nbc);

        double balance = (accumRoll + accumPitch) / this.time;
        this.dFeatures.setBalance(balance);
        return (success && nbc >= 0);
    }

    public DynamicFeatures getDynamicFeatures() {
        return this.dFeatures;
    }

    private int getBrokenConnections() {
        // int simxReadForceSensor(int clientID,int forceSensorHandle,IntWA
        // state,FloatWA forceVector,FloatWA torqueVector,int operationMode)

        int nbc = 0;
        List<Integer> forceSensors = robot.getForceSensorHandlers();

        for (int forceSensorHandle : forceSensors) {

            FloatWA forceVector = new FloatWA(3);
            FloatWA torqueVector = new FloatWA(3);
            IntW state = new IntW(0);
            int ret = coppeliaSimApi.simxReadForceSensor(clientID, forceSensorHandle, state, forceVector, torqueVector,
                    remoteApi.simx_opmode_oneshot_wait);
            if (ret == remoteApi.simx_return_ok) {
                boolean isBroken = BigInteger.valueOf(state.getValue()).testBit(1);
                if (isBroken) {
                    nbc++;
                }

            } else {
                System.out.format("%d: getBrokenConnections Function: "
                        + "Remote API function call returned with error code %d "
                        + "in force sensor %d\n", rank, ret, forceSensorHandle);

                String handleString = rank + ": Force sensor handles: ";
                for (int handles : forceSensors) {
                    handleString += handles + " ";
                }
                System.out.println(handleString);
                handleString = rank + ": Module handles: ";
                for (modules.individual.Module handles : robot.getModuleHandlers()) {
                    handleString += handles.id + " ";
                }
                System.out.println(handleString);

                // And check the handles of the scene
                // Now try to retrieve data in a blocking fashion (i.e. a service call):
                IntWA objectHandles = new IntWA(1);
                ret = coppeliaSimApi.simxGetObjects(clientID, remoteApi.sim_handle_all, objectHandles,
                        remoteApi.simx_opmode_blocking);
                if (ret == remoteApi.simx_return_ok) {
                    System.out.format("%d: Number of objects in the scene: %d\n", rank,
                            objectHandles.getArray().length);
                    handleString = rank + ": Object handles in the scene: ";
                    for (int i = 0; i < objectHandles.getArray().length; i++) {
                        handleString += objectHandles.getArray()[i] + " ";
                        System.out.println(handleString);
                    }
                } else {
                    System.out.format(
                            "%d: Unable to get the object handles in the scene. Remote API function call returned with error code: %d\n",
                            rank, ret);
                }
                return -1;
            }
        }
        // System.out.println(nbc + " connections broken.");
        return nbc;
    }

    private Vector3d getBaseRotation(int mode) {
        // int simxGetObjectPosition(int clientID,int objectHandle, int
        // relativeToObjectHandle, FloatWA position, int operationMode)

        Integer firstKey = robot.getModuleHandlers().get(robot.getModuleHandlers().size() - 1).id;
        int baseHandle = firstKey + 1;

        FloatWA orientation = new FloatWA(3);

        int ret = coppeliaSimApi.simxGetObjectOrientation(clientID, baseHandle, -1 /* Absolute position */, orientation,
                mode);
        if ((ret != remoteApi.simx_return_ok && mode == remoteApi.simx_opmode_buffer)
                || ret > remoteApi.simx_return_novalue_flag) {
            System.out.format("%d:  getBaseRotation Function: Remote API function call returned with error code: %d\n",
                    rank, ret);
            return null;
        }

        return new Vector3d(orientation.getArray()[0], orientation.getArray()[1], orientation.getArray()[2]); // alpha,
                                                                                                              // beta,
                                                                                                              // gamma
    }
}
