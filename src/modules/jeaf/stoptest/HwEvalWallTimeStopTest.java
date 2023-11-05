/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2021 Andres Faiña <anfv at itu.dk> (ITU)
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

package modules.jeaf.stoptest;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.stoptest.StopTest;
import modules.util.SimulationConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 * HwEvalWallTimeStopTest created on Nov 8, 2021
 * 
 * @author Andres Faiña <anfv at itu.dk>
 */
public class HwEvalWallTimeStopTest implements StopTest{

    /* Maximum wall clock time that the evolution is allowed to run.
    It takes into account the time to build the robots and evaluate 
    them in a physical arena. */
    private double maxWallTime = 1000;
    
    /**
     * Returns <tt>true</tt> if the problem had employed more time than
     * the time determined by this concrete objective.
     * @return <tt>true</tt> if the problem has taken longer than a specified 
     * time, <tt>false</tt> in other case.
     * @param algorithm the algorithm wich has to reach the objective
     */
    @Override
    public boolean isReach(EvolutionaryAlgorithm algorithm) {
        return HwEvalWallTime.getElapsedWallTime() >= maxWallTime;
    }

    @Override
    public void reset(EvolutionaryAlgorithm arg0) {
    }

    @Override
    public void configure(Configuration conf) {
        maxWallTime = conf.getDouble("MaxWallTime");
    }

}
