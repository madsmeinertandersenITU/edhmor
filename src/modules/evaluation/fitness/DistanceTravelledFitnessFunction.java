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
package modules.evaluation.fitness;

import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.StringWA;
import coppelia.remoteApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Vector3d;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.util.SimulationConfiguration;

/**
 * DistanceTravelledFitnessFunction.java Created on 20/10/2016
 *
 * @author Andres Faiña <anfv at itu.dk>
 */
public class DistanceTravelledFitnessFunction extends FitnessFunction {

    protected Vector3d finalPos = null, initialPos = null;

    public DistanceTravelledFitnessFunction(remoteApi api, int clientID, CoppeliaSimCreateRobot robot) {
        super(api, clientID, robot);
    }

    @Override
    public boolean init() {
        if (SimulationConfiguration.getTimeIniFitness() < 0) {
            initialPos = getPose();
            if (initialPos == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean update(double time) {
        if (initialPos == null) {
            if (time >= SimulationConfiguration.getTimeIniFitness()) {
                initialPos = getPose();
                if (initialPos == null) {
                    return false;
                }
            }
        }

        if (SimulationConfiguration.getTimeEndFitness() > 0) {
            if (finalPos == null) {
                if (time >= SimulationConfiguration.getTimeEndFitness()) {
                    finalPos = getPose();
                    if (finalPos == null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean end() {
        if (initialPos == null) {
            System.err.println("Initial pose not yet defined at the end of the evaluation!");
            System.err.println("Check timeIni and timeEnd variables in the configuration file.");
            System.err.println("Currently they are: timeIni = " + SimulationConfiguration.getTimeIniFitness()
                    + "timeEnd = " + SimulationConfiguration.getTimeEndFitness()
                    + "simulationTime = " + SimulationConfiguration.getMaxSimulationTime());
            return false;
        }

        if (finalPos == null) {
            finalPos = getPose();
            if (finalPos == null) {
                return false;
            }
        }
        this.fitness = Math.pow(finalPos.x - initialPos.x, 2) + Math.pow(finalPos.y - initialPos.y, 2);
        this.fitness = Math.sqrt(fitness);
        return true;
    }

    private Vector3d getPose() {
        if (SimulationConfiguration.getPoseFitness().toLowerCase().contains("base")) {
            // System.out.println("BASE selected: " +
            // SimulationConfiguration.getPoseFitness());
            return getBasePose();
        } else if (SimulationConfiguration.getPoseFitness().toLowerCase().contains("com")) {
            // System.out.println("COM selected: " +
            // SimulationConfiguration.getPoseFitness());
            return getCenterOfMass();
        } else {
            System.err.println("Pose for the Fitness Function not well defined!");
            System.err.println("Choose COM for center of mass or BASE for the base module");
            System.err.println("Currently it is defined as: " + SimulationConfiguration.getPoseFitness());
            System.exit(-1);
        }
        return null;
    }

    private Vector3d getBasePose() {
        // int simxGetObjectPosition(int clientID,int objectHandle, int
        // relativeToObjectHandle, FloatWA position, int operationMode)
        Set<Integer> keys = robot.getModuleHandlers().keySet();
        List<Integer> keyList = new ArrayList<>(keys);
        Integer keyNumber = keyList.get(keyList.size() - 1);

        int baseHandle = keyNumber + 1;
        FloatWA position = new FloatWA(3);
        int ret = coppeliaSimApi.simxGetObjectPosition(clientID, baseHandle, -1 /* Absolute position */, position,
                remoteApi.simx_opmode_oneshot_wait);
        if (ret == remoteApi.simx_return_ok) {
            // System.out.format("Obtaining positions of the objects: \n");
        } else {
            System.out.format("getBasePos Function: Remote API function call returned with error code: %d\n", ret);
            return null;
        }

        return new Vector3d(position.getArray()[0], position.getArray()[1], position.getArray()[2]);
    }

    private Vector3d getCenterOfMass() {

        Map<Integer, Integer> modules = robot.getModuleHandlers();
        IntWA handles = new IntWA(1);

        FloatWA floatData = new FloatWA(modules.size() * 3);
        int ret = coppeliaSimApi.simxGetObjectGroupData(clientID, remoteApi.sim_object_shape_type,
                3 /* Absolute position */, handles, new IntWA(1) /* intData */, floatData,
                new StringWA(1) /* stringData */, remoteApi.simx_opmode_oneshot_wait);
        if (ret == remoteApi.simx_return_ok) {
            // System.out.format("Obtaining positions of the objects: \n");
        } else {
            System.out.format("getCenterOfMass Function: Remote API function call returned with error code: %d\n", ret);
            return null;
        }

        // Order the handles array
        int[] orderedHandles = handles.getArray();
        Arrays.sort(orderedHandles);

        int[] modulesType = robot.getModuleType();
        double x = 0, y = 0, z = 0, robotMass = 0;
        // TODO: The center of mass needs to take into account the two parts of
        // the module (base and actuator). We need to know the mass for
        for (int i = 0; i < modules.size(); i++) {
            double moduleMass = ModuleSetFactory.getModulesSet().getModulesMass(modulesType[i]);

            int index = Arrays.binarySearch(orderedHandles, modules.get(i) + 1);
            x += floatData.getArray()[3 * index] * moduleMass;
            y += floatData.getArray()[3 * index + 1] * moduleMass;
            z += floatData.getArray()[3 * index + 2] * moduleMass;
            robotMass += moduleMass;
        }

        Vector3d com = new Vector3d(x / robotMass, y / robotMass, z / robotMass);
        // System.out.println("Center of mass coordinates: " + com.toString());
        return com;

    }

}
