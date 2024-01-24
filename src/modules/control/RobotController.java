/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2015 GII (UDC) and REAL (ITU)
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
import java.util.List;
import modules.evaluation.CoppeliaSimCreateRobot;

/**
 * RobotController.java
 * Created on 09/11/2015
 * 
 * @author Andres Faiña <anfv at itu.dk>
 */
public abstract class RobotController {

    protected remoteApi coppeliaSimApi;
    protected int clientID;
    protected CoppeliaSimCreateRobot robot;
    protected List<Integer> moduleHandlers;

    protected RobotController(remoteApi api, int clientID, CoppeliaSimCreateRobot robot) {
        this.coppeliaSimApi = api;
        this.clientID = clientID;
        this.robot = robot;
        moduleHandlers = robot.getModuleHandlers();
    }

    public abstract boolean updateJoints(double time);

}
