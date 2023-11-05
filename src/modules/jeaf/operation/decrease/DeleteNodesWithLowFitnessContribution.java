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

import java.util.List;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class DeleteNodesWithLowFitnessContribution extends DecreaseMutationOperation {

    static private double threshold = 0;

    public void setThreshold(double threshold) {
        DeleteNodesWithLowFitnessContribution.threshold = threshold;
    }

    public void configure(Configuration conf) {
        double th = conf.getDouble("threshold");
        this.setThreshold(th);
    }

    @Override
    public void run(TreeIndividual arbol) {

        this.setIsWorking(false);
        List<Node> nodes = arbol.getListNode();
        System.out.println("Entramos en " + this.toString());
        for (Node nod : nodes) {
            if (nod.getFitnessContribution() < DeleteNodesWithLowFitnessContribution.threshold) {

                if (nod.getDad() != null) {
                    this.setIsWorking(true);
                    nod.setLowerBranchIsOperationalActive(true);
                }
            }
        }

        //Hay que poner el arbol como "padre" antes de modificarlo
        this.setFatherIndividual(arbol);
        System.out.println("Nodos activos marcados");

        boolean repeat;
        do {
            System.out.println("Inicio del bucle");
            repeat = false;
            nodes = arbol.getListNode();
            for (Node nod : nodes) {
                if (nod.isIsOperationalActive()) {
                    //eliminar el nodo
                    this.removeBranch(nod);

                    repeat = true;
                    System.out.println("Nodo activo");
                    break;
                }
            }
            System.out.println("Fin del bucle");

        } while (repeat);
        System.out.println("Vamos a modifica el cromosoma");
        //actualizamos al individuo
        arbol.modifyChromosome();

        System.out.println("Acabamos " + this.toString());
    }

    @Override
    public void repair(TreeIndividual arbol) {
        double diferenciaFitness = arbol.getFitness() - arbol.getFatherFitness();
        //Eliminar el nodo aumento el fitness
        if (arbol.getFitness() >= (arbol.getFatherFitness() /* * (1 - this.incrementoSignificativo) */ )) {
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
            arbol.getRootNode().addFitnessContributionAndResetOperationalActive((-1) * diferenciaFitness);

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
        String str = "DeleteNodesWithLowFitnessContribution (threshold: "+ threshold +")";
        if(this.isWorking())
            str += "  (isWorking)";
        else
            str += "  (NOT Working)";

        return str;
    }
}
