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

/**
 * FitnessFunction.java
 * Created on 20/10/2016
 * @author Andres Faiña <anfv  at itu.dk>
 */
public abstract class FitnessFunction {
    
    protected double fitness = 0;
    protected remoteApi coppeliaSimApi;
    protected int clientID;
    protected CoppeliaSimCreateRobot robot;
    
    public FitnessFunction(remoteApi api, int clientID, CoppeliaSimCreateRobot robot){
        this.coppeliaSimApi = api;
        this.clientID = clientID;
        this.robot = robot;
    }
    public abstract boolean init();
    public abstract boolean update(double time);
    public abstract boolean end();
    
    public double getFitness(){
        return fitness;
    }
}
