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
package modules.jeaf.comparator;

import es.udc.gii.common.eaf.algorithm.fitness.comparator.FitnessComparator;
import es.udc.gii.common.eaf.algorithm.fitness.comparator.MaximizingFitnessComparator;
import es.udc.gii.common.eaf.config.Configurable;
import modules.individual.TreeIndividual;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class MaximizingFitnessMinimizingNodesComparator<T extends TreeIndividual> extends FitnessComparator<T> implements Configurable{

    private static double fitnessSignificativo = 0.1;

    @Override
    public int compare(T o1, T o2) {

        double f1 = o1.getFitness();
        double f2 = o2.getFitness();
        double aux;
        MaximizingFitnessComparator maxFitness = new MaximizingFitnessComparator();

        //Hacemos que f2 sea el de mayor fitness
        if (f1 > f2) {
            aux = f1;
            f1 = f2;
            f2 = aux;
        }

        if (f1 > f2 * (1 - fitnessSignificativo)) {
            //El fitness es comparable el mejor es el que menos modulos tenga
            if (o1.getRootNode().getNumberModulesBranch() < o2.getRootNode().getNumberModulesBranch()) {
                return -1;
            } else if (o1.getRootNode().getNumberModulesBranch() > o2.getRootNode().getNumberModulesBranch()) {
                return 1;
            } else {
                return maxFitness.compare(o1, o2);
            }
        } else {
            return maxFitness.compare(o1, o2);
        }

    }

    @Override
    public String toString() {
        return "Maximizing Fitness Minimizing Nodes Comparator";
    }

    public void configure(Configuration conf) {
        MaximizingFitnessMinimizingNodesComparator.fitnessSignificativo = conf.getDouble("fitnessSignificativo");
    }

    
}
