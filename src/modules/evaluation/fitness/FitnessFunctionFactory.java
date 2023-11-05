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

import coppelia.remoteApi;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.evaluation.dynamicFeatures.DynamicFeatures;
import modules.evaluation.dynamicFeatures.DynamicFeaturesEvaluator;
import modules.util.SimulationConfiguration;

/**
 * FitnessFunctionFactory.java
 * Created on 20/10/2016
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class FitnessFunctionFactory {
    
    /**
    * getFitnessFunction gives the fitness function method based on the 
    * specified fitness function in the SimulationConfiguration file. It reads 
    * the string stored in SimulationConfiguration and creates a new controller 
    * according to this value.
     * @return a fitness function object 
    */
    public static FitnessFunction  getFitnessFunction(remoteApi api, int clientID, CoppeliaSimCreateRobot robot, DynamicFeatures dFeatures) {
        FitnessFunction ff = null;
        
        String fitnessFunctionName = SimulationConfiguration.getFitnessFunctionStr();
        
        if (fitnessFunctionName.contentEquals("distanceTravelled")) {
            return new DistanceTravelledFitnessFunction(api, clientID, robot);
        }else if(fitnessFunctionName.contentEquals("distanceTravelledAndBrokenConnPenalty")){
            return new DistanceTravelledAndBrokenConnPenaltyFitnessFunction(api, clientID, robot, dFeatures);
        }
        
        System.out.println("");
        System.exit(-1);
        return ff;
    }

    
}
