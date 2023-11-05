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
public class GaMutationOperator extends MutationOperator {

    private double addNodeProb = 0.333;
    private double shakeNodeProb = 0.333;
    private double deleteNodeProb = 1 - addNodeProb - shakeNodeProb;

    
    private double morphMutProb = 0.4;
    private double ctrlMutProb = 0.5;   //Probability of mutation for each parameter
    private double sigmaGaussianMutation = 0.2; //For controller mutation

    @Override
    protected List<Individual> mutation(EvolutionaryAlgorithm algorithm, Individual individual) {
        
        System.out.println("Trying to mutate individual!");

        TreeIndividual treeInd = (TreeIndividual) individual;
        if (treeInd.getBestFitness() < treeInd.getFitness()) {
            treeInd.setIterWithoutImprovement(0);
            try {
                treeInd.setBestRootNode(treeInd.getRootNode().clone());
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(GaMutationOperator.class.getName()).log(Level.SEVERE, null, ex);
            }
            treeInd.setBestFitness(treeInd.getFitness());
        } else {
            treeInd.setIterWithoutImprovement(treeInd.getIterWithoutImprovement() + 1);
        }

        MutationOperation operation = null;
        double random = EAFRandom.nextDouble();
        if (random < morphMutProb) {
            //We perform a morphological mutation

            //Choose the operation randomly. Operations:
            //AddNodeShakeModule or DeleteNode
            random = EAFRandom.nextDouble();
            if (random < addNodeProb) {
                operation = new AddNode();
            } else {
                if (random < (addNodeProb + shakeNodeProb)) {
                    operation = new ShakingModule();
                } else {
                    operation = new DeleteNode();
                }
            }

            //Try to apply the mutation 10 times
            for (int i = 0; i < 10; i++) {
                treeInd.setOperation(operation);
                operation.run(treeInd);
                if (operation.isWorking()) {
                    break;
                } else {
                    //Restore the tree
                    try {
                        treeInd.setRootNode(treeInd.getFatherRootNode().clone());
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(ShakingModule.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    treeInd.setFitness(treeInd.getFatherFitness());
                    treeInd.modifyChromosome();
                }
            }
        }

        //Mutate the control
        
        operation = new ShakingControl(ctrlMutProb, sigmaGaussianMutation);
        treeInd.setOperation(operation);
        operation.run(treeInd);

        List<Individual> mutated_individual = new ArrayList<>();
        mutated_individual.add(treeInd);
        return mutated_individual;
    }

    @Override
    public void configure(Configuration conf) {
        super.configure(conf);

        this.ctrlMutProb = conf.getDouble("CtrlMutationProbability");
        if (ctrlMutProb < 0 || ctrlMutProb > 1) {
            throw new ConfigurationException("Wrong value for CtrlMutationProbability parameter."
                    + " CtrlMutationProbability should be in range [0,1]"
                    + "\nCtrlMutationProbability = " + ctrlMutProb);
        }
        this.morphMutProb = conf.getDouble("MorphMutationProbability");
        if (morphMutProb < 0 || morphMutProb > 1) {
            throw new ConfigurationException("Wrong value for MorphMutationProbability parameter."
                    + " MorphMutationProbability should be in range [0,1]"
                    + "\nMorphMutationProbability = " + morphMutProb);
        }

        this.addNodeProb = conf.getDouble("AddNodeProbability");
        this.shakeNodeProb = conf.getDouble("ShakeNodeProbability");
        this.deleteNodeProb = conf.getDouble("DeleteNodeProbablility");

        if (Math.abs(1 - addNodeProb - shakeNodeProb - deleteNodeProb) > 0.001) {
            throw new ConfigurationException("Wrong mutation parameter probabilities."
                    + " AddNodeProbability +  ShakeNodeProbability and DeleteNodeProbablility should be equal to 1 "
                    + "\nAddNodeProbability = " + addNodeProb
                    + "\nShakeNodeProbability = " + shakeNodeProb
                    + "\nDeleteNodeProbablility = " + deleteNodeProb);
        }
        
        this.sigmaGaussianMutation = conf.getDouble("SigmaGaussianMutation");

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
