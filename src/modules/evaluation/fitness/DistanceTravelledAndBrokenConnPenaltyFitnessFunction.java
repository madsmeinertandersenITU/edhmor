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
import coppelia.remoteApi;
import java.math.BigInteger;
import java.util.List;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.evaluation.dynamicFeatures.DynamicFeatures;
import modules.util.SimulationConfiguration;

/**
 * DistanceTravelledAndBrokenConnPenaltyFitnessFunction.java
 * Created on 20/10/2016
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class DistanceTravelledAndBrokenConnPenaltyFitnessFunction extends 
        DistanceTravelledFitnessFunction{

    DynamicFeatures dFeatures;
    public DistanceTravelledAndBrokenConnPenaltyFitnessFunction(remoteApi api, int clientID, CoppeliaSimCreateRobot robot, DynamicFeatures dFeatures) {
        super(api, clientID, robot);
        this.dFeatures = dFeatures;
    }
    
    @Override
    public boolean end() {
        //Calculate the fitness
        boolean success = super.end();
        
        //Apply the penalty based on the number of broken connections
        int nbc = dFeatures.getBrokenConnections();
        double penalty = SimulationConfiguration.getPenaltyFitness();
        this.fitness *= (Math.pow(penalty,nbc));
        return success;   
    }
    
    
}
