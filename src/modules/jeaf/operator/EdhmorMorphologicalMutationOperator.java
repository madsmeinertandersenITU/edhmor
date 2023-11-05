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
package modules.jeaf.operator;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.fitness.FitnessUtil;
import es.udc.gii.common.eaf.algorithm.operator.reproduction.mutation.MutationOperator;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.exception.ConfigurationException;
import es.udc.gii.common.eaf.util.EAFRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.jeaf.operation.MutationOperation;
import modules.jeaf.operation.decrease.DeleteAllNodes;
import modules.jeaf.operation.decrease.DeleteBranch;
import modules.jeaf.operation.decrease.DeleteNode;
import modules.jeaf.operation.decrease.DeleteNodesWithLowFitnessContribution;
import modules.jeaf.operation.grow.AddNode;
import modules.jeaf.operation.morphological.ChangeOrientation;
import modules.jeaf.operation.morphological.ChangePosition;
import modules.jeaf.operation.ShakingControl;
import modules.jeaf.operation.morphological.ShakingModule;
import modules.individual.TreeIndividual;
import modules.util.exceptions.InconsistentDataException;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class EdhmorMorphologicalMutationOperator extends MutationOperator {

    private double[] probabilities;
    private int nAddNode = 3;
    private int nAdaptMorf = 2;
    private int nAdaptContr = 1;
    private int nPoda = 1;
    private int nCiclosExplor = nAddNode + nAdaptMorf + nAdaptContr + nPoda;
    
    private double ctrlMutProb = 0.5;   //Probability of mutation for each parameter
    private double sigmaControlMutation = 0.2;


    @Override
    protected List<Individual> mutation(EvolutionaryAlgorithm algorithm, Individual individual) {

        int generation = algorithm.getGenerations();
        int iter = generation % nCiclosExplor;

        //TODO: Implement the different mutation problabilities based on the 
        //success of each operation
        TreeIndividual arbolInd = (TreeIndividual) individual;

        if (arbolInd.getBestFitness() < arbolInd.getFitness()) {
            arbolInd.setIterWithoutImprovement(0);
            try {
                arbolInd.setBestRootNode(arbolInd.getRootNode().clone());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(EdhmorMorphologicalMutationOperator.class.getName()).log(Level.SEVERE, null, ex);
            }
            arbolInd.setBestFitness(arbolInd.getFitness());
        } else {
            arbolInd.setIterWithoutImprovement(arbolInd.getIterWithoutImprovement() + 1);
        }

        //Create the operation based on the phase that we are:
        //1. Growing (AddNode)
        //2. Morphological Mutation 
        //3. Control Mutation
        //4. Pruning
        MutationOperation operation = null;

        if (iter < nAddNode) {
            operation = new AddNode();
        } else {
            if (iter < (nAddNode + nAdaptMorf)) {
                operation = new ShakingModule();
            } else {
                if (iter < (nAddNode + nAdaptMorf + nAdaptContr)) {
                    operation = new ShakingControl(ctrlMutProb, sigmaControlMutation);
                } else {
                    if (iter < (nAddNode + nAdaptMorf + nAdaptContr + nPoda)) {
                        operation = new DeleteAllNodes();
                    } else {
                        try {
                            throw new InconsistentDataException("Something went wrong. Phase of EDHMOR is out of sync.");
                        } catch (InconsistentDataException ex) {
                            Logger.getLogger(EdhmorMorphologicalMutationOperator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            }
        }

        arbolInd.setOperation(operation);

        //Run the operation
        if (operation != null) {
            operation.run(arbolInd);
        }

        List<Individual> mutated_individual = new ArrayList<>();
        mutated_individual.add(arbolInd);
        return mutated_individual;
    }

    @Override
    public void configure(Configuration conf) {
        super.configure(conf);

        this.nAddNode = conf.getInt("nAddNode");
        this.nAdaptMorf = conf.getInt("nAdaptMorf");
        this.nAdaptContr = conf.getInt("nAdaptContr");
        this.nPoda = conf.getInt("nPoda");
        nCiclosExplor = nAddNode + nAdaptMorf + nAdaptContr + nPoda;
        
        this.ctrlMutProb = conf.getDouble("CtrlMutationProbability");
        this.sigmaControlMutation = conf.getDouble("SigmaGaussianMutation");
        //TODO: Get the operations from the configuration
//        List ops = conf.getList("Operation.Class");
//        for (int i = 0; i < ops.size(); i++) {
//            try {
//                MutationOperation mo = (MutationOperation) Class.forName((String) ops.get(i)).newInstance();
//                mo.configure(conf.subset("Operation(" + i + ")"));
//            } catch (Exception ex) {
//                throw new ConfigurationException(
//                        "Wrong reproduction operator configuration for " + (String) ops.get(i) + " ?" + " \n Thrown exception: \n" + ex);
//            }
//
//        }
    }
}
