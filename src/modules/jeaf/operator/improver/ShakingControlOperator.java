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
package modules.jeaf.operator.improver;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.plugin.evaluation.IndividualImprover;
import java.util.ArrayList;
import java.util.List;
import modules.individual.TreeIndividual;
import modules.jeaf.stoptest.HwEvalWallTime;
import modules.util.SimulationConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class ShakingControlOperator implements IndividualImprover {

    private int nEvalControl = 1;
    private double pMutation = 0.2;
    private double sigmaMutation = 0.2;

    @Override
    public void configure(Configuration conf) {
        this.nEvalControl = conf.getInt("NEvalControl");
        this.pMutation = conf.getDouble("PMutation");
        this.sigmaMutation = conf.getDouble("PMutation");
    }

    @Override
    public Improvement improve(EvolutionaryAlgorithm alg, Individual seed) {

        List<Individual> variacionesIndividuo = new ArrayList();
        System.out.println("ShakingControlOperator: prob=" + pMutation + " sigmaMutation=" + sigmaMutation);
        for (int i = 0; i < nEvalControl; i++) {
            TreeIndividual a = (TreeIndividual) seed.clone();
            a.shakeControl(pMutation, sigmaMutation);
            variacionesIndividuo.add(a);
        }
        
        // We need to evaluate the new controllers, add evaluation time
        double time = SimulationConfiguration.getMaxSimulationTime() * variacionesIndividuo.size();
        HwEvalWallTime.increaseElapsedWallTime(time);
        return new Improvement(variacionesIndividuo, 0);
    }

    @Override
    public boolean doesEvaluate() {
        return false;
    }
}
