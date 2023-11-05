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
package modules.jeaf.operation.morphological;

import es.udc.gii.common.eaf.util.EAFRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.overlapping.CollisionDetector;
import modules.evaluation.overlapping.CollisionDetectorFactory;
import modules.jeaf.operation.MutationOperation;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class ShakingModule extends MutationOperation {

    private int nivel = 0;

    public ShakingModule(int nivel) {
        this.nivel = nivel;
    }

    public ShakingModule() {
    }

    @Override
    public void run(TreeIndividual tree) {

        //Set the tree as a "father" before to add modifications 
        this.setFatherIndividual(tree);

        this.setIsWorking(true);

        CollisionDetector collisionDetector = CollisionDetectorFactory.getCollisionDetector();

        for (int i = 0; i < 10; i++) { //try to shake a node 10 times or until find one feasible solution

            //Select a module randomly
            Node toChange = tree.getRandomNodeWithout0();

            if (toChange != null) {
                toChange.setIsOperationalActive(true);

                tree.shakeDadFaceAndOrientation();

                tree.modifyChromosome();    //modify the chromosome
                collisionDetector.loadTree(tree);
                if (collisionDetector.isFeasible()) {
                    return;
                }
                try {
                    tree.setRootNode(tree.getFatherRootNode().clone());
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(ShakingModule.class.getName()).log(Level.SEVERE, null, ex);
                }
                tree.setFitness(tree.getFatherFitness());
            }
        }
        tree.modifyChromosome();    //modify the chromosome
        this.setIsWorking(false);

    }

    @Override
    public void repair(TreeIndividual arbol) {

        double diferenciaFitness = arbol.getFitness() - arbol.getFatherFitness();
        //Agitar el nodo aumento el fitnes
        if (arbol.getFitness() >= arbol.getFatherFitness()) {
            //Actualizamos la rama inferior y el modulo que hemos cambiado
            arbol.getRootNode().addLowerBranchFitnessContribution(diferenciaFitness);
        } else {

            //TODO: Llamar a actualizar los valores de fitnessContribution de los moduloseliminados, borrar los isActiveContribution
            //DEBUG:
            if (arbol.getFatherRootNode() == null) {
                System.err.print("Vamos a poner rootNode igual a null xq fatherRN es null");
                System.err.print("fitness: " + arbol.getFitness() + "FatherFitness: " + arbol.getFatherFitness());
                System.err.println("Arbol: " + arbol.toString());
            }
            arbol.setRootNode(arbol.getFatherRootNode());
            arbol.setFitness(arbol.getFatherFitness());

            //Aumentamos el fitnessContribution
            arbol.getRootNode().addFitnessContributionAndResetOperationalActive(0);

            //actualizamos al individuo
            arbol.modifyChromosome();

        }
        this.removeFatherIndividual(arbol);

    }

    @Override
    public boolean isMandatory() {
        return false;
    }

    @Override
    public String toString() {
        String str = "ShakingModule";
        if (this.isWorking()) {
            str += "   (isWorking)";
        } else {
            str += "   (NOT Working)";
        }
        return str;
    }

    public void configure(Configuration conf) {
    }

}
