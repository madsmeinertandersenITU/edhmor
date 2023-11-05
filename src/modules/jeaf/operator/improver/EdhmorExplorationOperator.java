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
public class EdhmorExplorationOperator implements IndividualImprover {

    private int nEvalAddNode = 5;
    private int nEvalAdaptMorf = 5;
    private int nEvalAdaptControl = 5;

    @Override
    public boolean doesEvaluate() {
        return false;
    }

    @Override
    public Improvement improve(EvolutionaryAlgorithm alg, Individual seed) {

        List<Individual> indVariations = new ArrayList();

        TreeIndividual treeSeed = (TreeIndividual) seed;
        if (treeSeed.getOperation() instanceof AddNode) {
            AddNode addNodeOperation = (AddNode) treeSeed.getOperation();
            if (addNodeOperation.isWorking()) {
                indVariations = this.addNodeVariations(seed);
                //Increase assembly time
                double assTimeperMod = SimulationConfiguration.getAssemblyTimePerModule();
                double time = assTimeperMod + SimulationConfiguration.getMaxSimulationTime();
                if (!indVariations.isEmpty()) {
                    time += assTimeperMod * (indVariations.size() + 1)
                            + //We have to restore the best 
                            SimulationConfiguration.getMaxSimulationTime() * indVariations.size();
                }

                HwEvalWallTime.increaseElapsedWallTime(time);
            }
        }

        if (treeSeed.getOperation() instanceof ShakingModule) {
            ShakingModule shakingModuleOperation = (ShakingModule) treeSeed.getOperation();
            if (shakingModuleOperation.isWorking()) {
                indVariations = this.shakingModuleOperationVariations(seed);

                //Increase assembly time
                double assTimeperMod = SimulationConfiguration.getAssemblyTimePerModule();
                double time = assTimeperMod + SimulationConfiguration.getMaxSimulationTime();
                if (!indVariations.isEmpty()) {
                    time += assTimeperMod * (indVariations.size() + 1)
                            + //We have to restore the best 
                            SimulationConfiguration.getMaxSimulationTime() * indVariations.size();
                }

                HwEvalWallTime.increaseElapsedWallTime(time);
            }
        }

        if (treeSeed.getOperation() instanceof ShakingControl) {
            ShakingControl shakingControlOperation = (ShakingControl) treeSeed.getOperation();
            if (shakingControlOperation.isWorking()) {
                indVariations = this.shakingControlOperationVariations(seed, 
                        shakingControlOperation.getProb(), shakingControlOperation.getSigmaGausianMutation());
                //Increase time
                double time = SimulationConfiguration.getMaxSimulationTime() * (indVariations.size() + 1);

                HwEvalWallTime.increaseElapsedWallTime(time);
            }
        }

        if (treeSeed.getOperation() instanceof DeleteAllNodes) {
            DeleteAllNodes deleteAllNodesOperation = (DeleteAllNodes) treeSeed.getOperation();
            if (deleteAllNodesOperation.isWorking()) {
                indVariations = deleteAllNodesOperation.generateCandidates(seed);

                //Increase time
                double assTimeperMod = SimulationConfiguration.getAssemblyTimePerModule();
                double time = SimulationConfiguration.getMaxSimulationTime() * (indVariations.size());
                //It is the last phase, we do not need to reset the robot
                time += (treeSeed.getListNode().size() - SimulationConfiguration.getNMinModulesIni()) 
                        * assTimeperMod;
                
                HwEvalWallTime.increaseElapsedWallTime(time);
            }
        }

        return new Improvement(indVariations, 0);
    }

    private List<Individual> shakingControlOperationVariations(Individual ind, double p, double sigma) {
        List<Individual> variaciones = new ArrayList();
        for (int i = 0; i < nEvalAdaptControl; i++) {
            TreeIndividual tree = (TreeIndividual) ind.clone();
            tree.shakeControl(p, sigma);
            //TODO: Comprobar que no sean iguales los distintos cromosomas???
            variaciones.add(tree);
        }
        return variaciones;
    }

    private List<Individual> addNodeVariations(Individual ind) {
        List<Individual> variaciones = new ArrayList();
        for (int i = 0; i < nEvalAddNode; i++) {
            CollisionDetector collisionDetector = CollisionDetectorFactory.getCollisionDetector();
            TreeIndividual tree = (TreeIndividual) ind.clone();
            tree.shakeDadFaceAndOrientation();
            tree.modifyChromosome();
            collisionDetector.loadTree(tree);
            if (collisionDetector.isFeasible()) {
                //TODO: Comprobar que no sean iguales los distintos cromosomas???
                variaciones.add(tree);
            } else {
                System.out.println("Could not create an addNodeVariation due to a collision");
            }
        }
        return variaciones;
    }

    private List<Individual> shakingModuleOperationVariations(Individual ind) {
        List<Individual> variaciones = new ArrayList();

        for (int i = 0; i < nEvalAdaptMorf; i++) {
            CollisionDetector collisionDetector = CollisionDetectorFactory.getCollisionDetector();
            TreeIndividual tree = (TreeIndividual) ind.clone();
            tree.shakeDadFaceAndOrientation();
            tree.modifyChromosome();
            collisionDetector.loadTree(tree);
            if (collisionDetector.isFeasible()) {

                //TODO: Comprobar que no sean iguales los distintos cromosomas???
                variaciones.add(tree);

            }
        }
        return variaciones;
    }

    @Override
    public void configure(Configuration conf) {
        this.nEvalAddNode = conf.getInt("nEvalAddNode");
        this.nEvalAdaptMorf = conf.getInt("nEvalAdaptMorf");
        this.nEvalAdaptControl = conf.getInt("nEvalAdaptControl");
    }

}
