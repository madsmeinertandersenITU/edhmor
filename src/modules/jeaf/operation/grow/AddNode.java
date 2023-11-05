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
package modules.jeaf.operation.grow;

import es.udc.gii.common.eaf.util.EAFRandom;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.overlapping.CollisionDetector;
import modules.evaluation.overlapping.CollisionDetectorFactory;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.SimulationConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class AddNode extends GrowMutationOperation {

    FileOutputStream archivo;
    PrintStream printDebug = null;

    public AddNode() {
        if (SimulationConfiguration.isDebug()) {
            try {
                archivo = new FileOutputStream("addNode_debug", true);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AddNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            printDebug = new PrintStream(archivo);
        }
    }

    @Override
    public void run(TreeIndividual tree) {

        //Calculate the number of nodes in the tree
        tree.getRootNode().setModulesSubTree();

        super.setIsWorking(false);

        //only run the add module method if the final tree has equal or less nodes than the maximum number of nodes
        if (tree.getRootNode().getNModulesSubTree() < SimulationConfiguration.getMaxModules()) {

            //Set the tree as a "father" before to add modifications 
            this.setFatherIndividual(tree);

            for (int i = 0; i < 10; i++) { //try to add a node 10 times

                //Choose the node where we are going to add a child node
                Node dadNode = tree.getRandomNodeWithFreeConnections();
                if (dadNode == null) {
                    //there is no node with free conections
                    super.setIsWorking(false);
                    return;
                }

                //Create the node to add (child node)
                //Variables de inicializacion del arbol
                int nTypeMax = SimulationConfiguration.getMaxTypeModules();
                int nTypeMin = SimulationConfiguration.getMinTypeModules();
                int nType = nTypeMax - nTypeMin + 1;

                //Seleccionamos el tipo del  nodo
                int type = EAFRandom.nextInt(nType) + nTypeMin;

                Node newNode = new Node(type, dadNode);

                //add the node (it added in a random face and in a random orientation )
                this.addNode(dadNode, newNode);
                tree.modifyChromosome();    //modify the chromosome
                CollisionDetector collisionDetector = CollisionDetectorFactory.getCollisionDetector();
                collisionDetector.loadTree(tree);
                if (collisionDetector.isFeasible()) {
                    super.setIsWorking(true);
                    return;
                }
                dadNode.eliminateChild(newNode);
                tree.modifyChromosome();    //modify the chromosome

            }

        }else{
            System.out.println("AddNode not working, modulesInd" +
                    tree.getRootNode().getNModulesSubTree() + 
                    " , maxModules" +SimulationConfiguration.getMaxModules());
        }
    }

    @Override
    public void repair(TreeIndividual tree) {

        double diferenciaFitness = tree.getFitness() - tree.getFatherFitness();
        //The new node has increased the fitness
        if (tree.getFitness() > tree.getFatherFitness() /* *(1+this.incrementoSignificativo) */) {

            if (SimulationConfiguration.isDebug()) {
                printDebug.println("\nEste individuo ha aumentado su fitness, ningun problema: ");
                printDebug.println("Orden poblacional" + tree.getPopullationOrder() + "arbolFitness: " + tree.getFitness() + "; arbolPadreFitness: " + tree.getFatherFitness());
            }
            //Llamamos a aumentar el fitnessContribution delnuevo modulo y borrar los isActiveContribution
            tree.getRootNode().addUpperBranchFitnessContribution(diferenciaFitness);
        } else {// El nuevo nodo no aumento el fitness

            if (tree.isIsProtected()) {
                //Esta protegido, por tanto reparamos el arbol
                if (SimulationConfiguration.isDebug()) {
                    printDebug.println("\nOrden poblacional" + tree.getPopullationOrder() + "Este individuo esta protegido:");
                    printDebug.println(tree.detailedToString());
                }
                //DEBUG:
                if (tree.getFatherRootNode() == null) {
                    System.err.print("Vamos a poner rootNode igual a null xq fatherRN es null");
                    System.err.print("fitness: " + tree.getFitness() + "FatherFitness: " + tree.getFatherFitness());
                    System.err.println("Arbol: " + tree.toString());
                }
                tree.setRootNode(tree.getFatherRootNode());
                tree.setFitness(tree.getFatherFitness());

                //Aumentamos el fitnessContribution
                tree.getRootNode().addFitnessContributionAndResetOperationalActive(0);

                //actualizamos al individuo
                tree.modifyChromosome();

                if (SimulationConfiguration.isDebug()) {
                    printDebug.println("Arbol reparado: ");
                    printDebug.println(tree.detailedToString() + "\n");
                }

            } else {
                //Hemos reducido el fitness
                if (SimulationConfiguration.isDebug()) {
                    printDebug.println("\nEste individuo NO ha aumentado su fitness pero no está protegido, ningun problema: ");
                    printDebug.println("Orden poblacional" + tree.getPopullationOrder() + "arbolFitness: " + tree.getFitness() + "; arbolPadreFitness: " + tree.getFatherFitness());
                }
                tree.getRootNode().addFitnessContributionAndResetOperationalActive(diferenciaFitness);
            }

        }
        this.removeFatherIndividual(tree);
    }

    @Override
    public boolean isMandatory() {
        return true;
    }

    @Override
    public String toString() {
        String str = "AddNode";
        if (this.isWorking()) {
            str += "   (isWorking)";
        } else {
            str += "   (NOT Working)";
        }
        return str;
    }

    @Override
    public void configure(Configuration conf) {
    }
}
