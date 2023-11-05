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
import modules.evaluation.overlapping.CollisionDetector;
import modules.evaluation.overlapping.CollisionDetectorFactory;
import modules.jeaf.operation.decrease.DeleteAllNodes;
import modules.jeaf.operation.grow.AddNode;
import modules.jeaf.operation.ShakingControl;
import modules.jeaf.operation.morphological.ShakingModule;
import modules.individual.TreeIndividual;
import modules.jeaf.stoptest.HwEvalWallTime;
import modules.util.SimulationConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class ControllerExplorationImprover implements IndividualImprover {

    private int nEvalAdaptControl = 5;
    private double ctrlMutationProbability = 0.1;
    private double sigmaGaussianMutation = 0.2;

    @Override
    public boolean doesEvaluate() {
        return false;
    }

    @Override
    public Improvement improve(EvolutionaryAlgorithm alg, Individual seed) {

        List<Individual> indVariations = new ArrayList();

        indVariations = this.shakingControlOperationVariations(seed,
                ctrlMutationProbability, sigmaGaussianMutation);
        
        //Increase time
        double time = SimulationConfiguration.getMaxSimulationTime() * (indVariations.size());

        HwEvalWallTime.increaseElapsedWallTime(time);

        return new Improvement(indVariations, 0);
    }

    private List<Individual> shakingControlOperationVariations(Individual ind, double p, double sigma) {
        List<Individual> variations = new ArrayList();
        for (int i = 0; i < nEvalAdaptControl; i++) {
            TreeIndividual tree = (TreeIndividual) ind.clone();
            tree.shakeControl(p, sigma);
            //TODO: check that we have mutated at least one parameter???
            variations.add(tree);
        }
        return variations;
    }

    @Override
    public void configure(Configuration conf) {
        this.nEvalAdaptControl = conf.getInt("nEvalAdaptControl");
        this.ctrlMutationProbability = conf.getDouble("CtrlMutationProbability");
        this.sigmaGaussianMutation = conf.getDouble("SigmaGaussianMutation");
    }

}
