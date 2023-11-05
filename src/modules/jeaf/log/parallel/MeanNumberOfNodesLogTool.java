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
package modules.jeaf.log.parallel;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.parallel.ParallelEvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.log.parallel.ParallelLogTool;
import java.util.List;
import java.util.Observable;
import modules.individual.TreeIndividual;

/**
 *
 * @author fai
 */
public class MeanNumberOfNodesLogTool extends ParallelLogTool {

    @Override
    public void update(Observable o, Object arg) {
        ParallelEvolutionaryAlgorithm pea = (ParallelEvolutionaryAlgorithm) o;

        if (pea.getCurrentObservable() instanceof EvolutionaryAlgorithm) {
            super.update(o, arg);
            EvolutionaryAlgorithm algorithm = (EvolutionaryAlgorithm) pea.getCurrentObservable();

            if (algorithm.getState() == EvolutionaryAlgorithm.REPLACE_STATE && arg == null) {

                List<Individual> individuals = algorithm.getPopulation().getIndividuals();
                double media=0, dispersion=0;
                int[] non = new int[individuals.size()];
                int i=0;
                for (Individual ind : individuals) {
                    TreeIndividual tree = (TreeIndividual) ind;

                    non[i]=tree.getRootNode().getNumberModulesBranch();
                    media += non[i];
                    i++;
                }
                media /= individuals.size();
                for(int j=0; j < non.length; j++)
                    dispersion += (non[j] - media) * (non[j] - media);

                dispersion = Math.sqrt(dispersion / individuals.size());
                super.getLog().println(algorithm.getGenerations() + " - " + media + " - " + dispersion);
            }
        }
    }

    @Override
    public String getLogID() {
        return "ParallelMeanNumberOfNodesLogTool";
    }
}
