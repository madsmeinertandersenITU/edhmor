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
package modules.jeaf.operation;

import modules.individual.TreeIndividual;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class ShakingControl extends MutationOperation{

    private double sigma = 0.2;
    private double prob = 0.2;

    public ShakingControl(double probability, double sigmaMutation) {
        super();
        this.prob = probability;
        this.sigma = sigmaMutation;
    }

    @Override
    public void run(TreeIndividual arbol) {
        
        System.out.println("Mutating control with sigma : " + sigma);

        //Hay que poner el arbol como "padre" antes de modificarlo
        this.setFatherIndividual(arbol);

        this.setIsWorking(true);

        arbol.shakeControl(prob, sigma);
        
    }

    @Override
    public void repair(TreeIndividual arbol) {

        //Agitar el nodo aumento el fitnes
        if (arbol.getFitness() >= arbol.getFatherFitness() ) {
            //Actualizamos la rama inferior y el modulo que hemos cambiado
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
        String str = "ShakingControl";
        if(this.isWorking())
            str += "   (isWorking)";
        else
            str += "   (NOT Working)";
        return str;
    }

    @Override
    public void configure(Configuration conf) {
        //TODO: Fix this. Does not work as the operation is being instanciated  
        // several times. Make it static?
        //this.sigma = conf.getDouble("SigmaGausianMutation");
    }

    public double getSigmaGausianMutation() {
        return sigma;
    }

    public double getProb() {
        return prob;
    }
}
