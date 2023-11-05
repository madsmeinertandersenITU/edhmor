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
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.util.SimulationConfiguration;

/**
 * FixedSinusoidalController.java Created on 31/10/2017
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class FixedSinusoidalController_0_90_180_270 extends SinusoidalController {

    
            
    public FixedSinusoidalController_0_90_180_270(remoteApi api, int clientID, CoppeliaSimCreateRobot robot) {
        super(api, clientID, robot);
        
        for (int module = 0; module < moduleHandlers.size(); module++) {
            //FIXME: could have more control parameters than actuators with modules with more dof
            if(module%4 == 0)
                phaseControl[module] = 0;
            if(module%4 == 1)
                phaseControl[module] = 90;
            if(module%4 == 2)
                phaseControl[module] = 180;
            if(module%4 == 3)
                phaseControl[module] = 270;
            amplitudeControl[module] = 1.0;
            andularFreqControl[module] = 1.0;
        }
    }
}
