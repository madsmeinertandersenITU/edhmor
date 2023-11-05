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
package modules.jeaf.operator;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.operator.reproduction.ReproductionOperator;
import es.udc.gii.common.eaf.algorithm.operator.reproduction.crossover.CrossOverOperator;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.exception.OperatorException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.overlapping.CollisionDetector;
import modules.evaluation.overlapping.CollisionDetectorFactory;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.jeaf.stoptest.HwEvalWallTime;
import modules.util.SimulationConfiguration;

/**
 * GaCrossOverOperator created on Nov 8, 2021
 *
 * This class only updates the wall time when performing physical evaluations 
 * as we need to build and disassemble the robots. The individuals are not 
 * modified (they have just been modified by the other reproduction operators). 
 * @author Andres Faiña <anfv at itu.dk>
 */
public class GaHwEvalTimeOperator extends ReproductionOperator {

    @Override
    public List<Individual> operate(EvolutionaryAlgorithm algorithm,
            List<Individual> individuals) throws OperatorException {

        if (individuals == null) {
            throw new OperatorException("Mutation - Empty Individuals");
        }
        //Increase the wall time because of the assembly time
        for (int i = 0; i < individuals.size(); i++) {
            TreeIndividual tree = (TreeIndividual) individuals.get(i);
            double modules = tree.getListNode().size();
            //x2 as we need to disassemble them and assemble the new morphologies 
            double time = SimulationConfiguration.getAssemblyTimePerModule() * modules  * 2 + 
                    SimulationConfiguration.getMaxSimulationTime();
            HwEvalWallTime.increaseElapsedWallTime(time);
        }
        return individuals;
    }

}
