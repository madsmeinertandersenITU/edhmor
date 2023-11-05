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
import es.udc.gii.common.eaf.algorithm.operator.reproduction.crossover.CrossOverOperator;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.overlapping.CollisionDetector;
import modules.evaluation.overlapping.CollisionDetectorFactory;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.SimulationConfiguration;

/**
 * GaCrossOverOperator created on Nov 8, 2021
 *
 * @author Andres Faiña <anfv at itu.dk>
 */
public class GaCrossOverOperator extends CrossOverOperator {

    @Override
    protected List<Individual> crossOver(EvolutionaryAlgorithm ea, List<Individual> individuals) {
        try {
            TreeIndividual ind0 = (TreeIndividual) individuals.get(0);
            TreeIndividual ind1 = (TreeIndividual) individuals.get(1);
            
            //Generate copies of the individuals to apply the crossover
            TreeIndividual newInd0 = ind0.clone();
            TreeIndividual newInd1 = ind1.clone();
            
            //Select a branch in each robot and save their dads
            Node node0 = newInd0.getRootNode().getChildren().get(0); //.getRandomNodeWithout0();
            Node node1 = newInd1.getRootNode().getChildren().get(0);//.getRandomNodeWithout0();
            Node dadNode0 = node0.getDad();
            Node dadNode1 = node1.getDad();

            //Get the connections and swap the orientations
            Connection conn0 = dadNode0.getConnection(node0).clone();
            Connection conn1 = dadNode1.getConnection(node1).clone();
            int orient0 = conn0.getChildrenOrientation();
            conn0.setChildrenOrientation(conn1.getChildrenOrientation());
            conn1.setChildrenOrientation(orient0);

            //Remove nodes from parents
            dadNode0.eliminateChild(node0);
            dadNode1.eliminateChild(node1);
            node0.disconnectFromParent();
            node1.disconnectFromParent();

            //Add the new braches
            dadNode0.addChildren(node1, conn0);
            dadNode1.addChildren(node0, conn1);
            //Check if the individulas are feasible
            List<Individual> newInds = new ArrayList();
            if (feasibleInd(newInd0)) {
                newInd0.modifyChromosome();
                newInds.add(newInd0);
            } else {
                newInds.add(ind0);
            }
            if (feasibleInd(newInd1)) {
                newInd1.modifyChromosome();
                newInds.add(newInd1);
            } else {
                newInds.add(ind1);
            }
            return newInds;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(GaCrossOverOperator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //Check the individuals:
    private boolean feasibleInd(TreeIndividual tree) {
        int modulesInd = tree.getRootNode().getNumberModulesBranch();
        if (modulesInd < SimulationConfiguration.getNMinModulesIni()) {
            return false;
        }
        if (modulesInd > SimulationConfiguration.getNMaxModulesIni()) {
            return false;
        }

        CollisionDetector collisionDetector = CollisionDetectorFactory.getCollisionDetector();
        collisionDetector.loadTree(tree);
        return collisionDetector.isFeasible();
    }

}
