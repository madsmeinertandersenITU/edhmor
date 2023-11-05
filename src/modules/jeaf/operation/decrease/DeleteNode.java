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
package modules.jeaf.operation.decrease;

import es.udc.gii.common.eaf.exception.ConfigurationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.jeaf.operation.morphological.ShakingModule;
import modules.util.SimulationConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class DeleteNode extends DecreaseMutationOperation {

    private static boolean useWorstNode = false;
    private static boolean useRandomNode = true;

    @Override
    public void run(TreeIndividual tree) {

        //Save the current individula as its father before modifying it
        this.setFatherIndividual(tree);

        //try to delete a node 10 times or until one feasible solution is found
        for (int i = 0; i < 10; i++) {

            //Choose a node to remove
            Node toRemove = null;
            if (useWorstNode) {
                toRemove = tree.getWorstNodeWithoutChildrens();
            } else {
                if (useRandomNode) {
                    toRemove = tree.getRandomNodeWithout0();
                } else {
                    System.err.println("There is no method to select the node "
                            + "to remove. Use useWorstNode or useRandomNode ");
                    System.exit(-1);
                }
            }

            //only remove the node if the node has a parent 
            //(avoid to remove the root node if robot only has one module)
            if (toRemove.getDad() != null) {
                toRemove.setIsOperationalActive(true);

                //eliminar el nodo
                this.removeBranch(toRemove);

                //actualizamos al individuo
                tree.modifyChromosome();
                if (tree.getListNode().size() >= SimulationConfiguration.getNMinModulesIni()) {
                    this.setIsWorking(true);
                    return; //Feasible robot
                } else {
                    //Restore the tree
                    try {
                        tree.setRootNode(tree.getFatherRootNode().clone());
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(ShakingModule.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    tree.setFitness(tree.getFatherFitness());
                    tree.modifyChromosome();
                }
            }

            //We could not perform the deleting of a node
            this.setIsWorking(false);
        }
    }

    @Override
    public void repair(TreeIndividual tree) {
        double difFitness = tree.getFitness() - tree.getFatherFitness();
        //Eliminar el nodo aumento el fitness
        if (tree.getFitness() >= (tree.getFatherFitness() /* * (1 - this.incrementoSignificativo)*/)) {
            //TODO: Llamar a ¿¿actualizar los valores de fitnessContribution de todos los modulos??
            tree.getRootNode().addFitnessContributionAndResetOperationalActive(0);
        } else {

            //TODO: Llamar a actualizar los valores de fitnessContribution de los moduloseliminados, borrar los isActiveContribution
            //DEBUG:
            if (tree.getFatherRootNode() == null) {
                System.err.print("Vamos a poner rootNode igual a null xq fatherRN es null");
                System.err.print("fitness: " + tree.getFitness() + "FatherFitness: " + tree.getFatherFitness());
                System.err.println("Arbol: " + tree.toString());
            }
            tree.setRootNode(tree.getFatherRootNode());
            tree.setFitness(tree.getFatherFitness());

            //Aumentamos el fitnessContribution
            tree.getRootNode().addFitnessContributionAndResetOperationalActive((-1) * difFitness);

            //actualizamos al individuo
            tree.modifyChromosome();

        }
        this.removeFatherIndividual(tree);
    }

    @Override
    public boolean isMandatory() {
        return false;
    }

    @Override
    public String toString() {
        String str = "DeleteNode";
        if (this.isWorking()) {
            str += "   (isWorking)";
        } else {
            str += "   (NOT Working)";
        }
        return str;
    }

    public void configure(Configuration conf) {
        //TODO: This does not work as it the class is created dynamically and destroyed
        //everytime is used. We should fix this. For now, use random node.
//        useWorstNode = conf.containsKey("UseWorstNode");
//        useRandomNode = conf.containsKey("UseRandomNode");
//
//        if ((useWorstNode&&useRandomNode)||(!useWorstNode&&!useRandomNode)) {
//            throw new ConfigurationException("We could not configure Delete Node:\n"
//                    + "useWorstNode: " + useWorstNode
//                    + "\nuseRandomNode: " + useRandomNode);
//        }
    }
}
