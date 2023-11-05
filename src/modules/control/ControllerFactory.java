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

package modules.control;

import coppelia.remoteApi;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.util.SimulationConfiguration;

/**
 * ControllerFactory.java
 * Created on 31/10/2017
 * 
 * @author Andres Faiña <anfv at itu.dk>
 */
public class ControllerFactory {

    /**
     * getRobotController gives the robot controller method based on the
     * specified controller in the SimulationConfiguration file. It reads the
     * string stored in SimulationConfiguration and creates a new controller
     * according to this value.
     * 
     * @return a controller to be used when evaluating the robots
     */
    public static RobotController getRobotController(remoteApi api, int clientID, CoppeliaSimCreateRobot robot) {
        RobotController rc = null;

        String fitnessFunctionName = SimulationConfiguration.getRobotControllerStr();

        if (fitnessFunctionName.contentEquals("sinusoidalController")) {
            return new SinusoidalController(api, clientID, robot);
        }
        if (fitnessFunctionName.contentEquals("sinusoidalControllerWNoise")) {
            return new SinusoidalControllerWNoise(api, clientID, robot, SimulationConfiguration.getNoiseStd(),
                    SimulationConfiguration.getIndivNoise());
        }
        if (fitnessFunctionName.contentEquals("fixedSinusoidalController_0")) {
            return new FixedSinusoidalController_0(api, clientID, robot);
        }
        if (fitnessFunctionName.contentEquals("fixedSinusoidalController_0_180")) {
            return new FixedSinusoidalController_0_180(api, clientID, robot);
        }
        if (fitnessFunctionName.contentEquals("fixedSinusoidalController_0_90_180_270")) {
            return new FixedSinusoidalController_0_90_180_270(api, clientID, robot);
        }
        if (fitnessFunctionName.contentEquals("fixedSinusoidalController_Random")) {
            return new FixedSinusoidalController_Random(api, clientID, robot);
        }
        if (fitnessFunctionName.contentEquals("coneSensorController")) {
            return new ConeSensorController(api, clientID, robot);
        }

        System.out.println("No controller matches with the specified controller name");
        System.exit(-1);
        return rc;
    }

}
