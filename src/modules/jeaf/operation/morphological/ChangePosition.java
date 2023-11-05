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

import modules.jeaf.operation.MutationOperation;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class ChangePosition extends MutationOperation {

    @Override
    public void run(TreeIndividual arbol) {
        //Elegimos el nodo a cambiar de posicion (cambiar la cara del padre donde se conecta)
        Node toChange = arbol.getRandomNodeWithout0();

        //solo ejecutamos si existe un módulo
        if (toChange != null) {

            //marcamos esa rama como activa
            toChange.setLowerBranchIsOperationalActive(true);

            //Hay que poner el arbol como "padre" antes de modificarlo
            this.setFatherIndividual(arbol);

            //cambiamos la orientacion de la rama
            Node dad = toChange.getDad();
            dad.changePosition(toChange);

            //actualizamos al individuo
            arbol.modifyChromosome();
        }
    }

    @Override
    public void repair(TreeIndividual arbol) {

        //Cambiar de posición el nodo aumento el fitness
        if (arbol.getFitness() >= arbol.getFatherFitness()) {
            //Llamamos a actualizar los valores de fitnessContribution y borrarmos los isActiveContribution
            if(arbol.getFitness() >= arbol.getFatherFitness() /* *(1+incrementoSignificativo) */ )
                arbol.getRootNode().addFitnessContributionAndResetOperationalActive(1);
            else
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

            //Llamamos a actualizar los valores de fitnessContribution y borrarmos los isActiveContribution
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
        String str = "ChangePosition";
        if(this.isWorking())
            str += "   (isWorking)";
        else
            str += "   (NOT Working)";

        return str;
    }

    @Override
    public void configure(Configuration conf) {}
}
