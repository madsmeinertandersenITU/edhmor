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

import modules.individual.Node;
import modules.individual.TreeIndividual;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class DeleteBranch extends DecreaseMutationOperation {

    @Override
    public void run(TreeIndividual arbol) {

        //Elegimos el nodo a eliminar 
        Node toRemove = arbol.getRandomNodeWithout0();

        //solo ejecutamos si el nodo tiene un padre (caso de que solo exista un módulo)
        if (toRemove != null) {

            toRemove.setLowerBranchIsOperationalActive(true);

            //Hay que poner el arbol como "padre" antes de modificarlo
            this.setFatherIndividual(arbol);

            //eliminar el nodo
            this.removeBranch(toRemove);

            //actualizamos al individuo
            arbol.modifyChromosome();
        }
    }

    @Override
    public void repair(TreeIndividual arbol) {

        //Eliminar el nodo aumento el fitness
        if (arbol.getFitness() >= ( arbol.getFatherFitness() /**(1-this.incrementoSignificativo)*/ ) ) {
            //Borramos los is ActiveContribution y no aumentamos la contribucion
            //TODO: Llamar a ¿¿actualizar los valores de fitnessContribution de todos los modulos??
            arbol.getRootNode().addFitnessContributionAndResetOperationalActive(0);
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
            arbol.getRootNode().addFitnessContributionAndResetOperationalActive(1);

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
        String str = "DeleteBranch";
        if(this.isWorking())
            str += "   (isWorking)";
        else
            str += "   (NOT Working)";
        return str;
    }

    public void configure(Configuration conf) {

    }


}
