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
import es.udc.gii.common.eaf.algorithm.population.Individual;

/**
 *
 * @author LCY
 * Adaptation of the class MaximizingFitnessComparator in JEAF to use the
 * Double.comparator funtion.Avoids problems with NaN and float point 
 * arithmetics
 */
/**
 * This class implements a comparator for comparing two instances i1 and i2 of
 * Individual based on their fitness value. This class is used in maximizing problems.
 *
 * @author Grupo Integrado de Ingeniería (<a href="http://www.gii.udc.es">www.gii.udc.es</a>)
 * @since 1.0
 */
public class MaximizingFitnessComparator<T extends Individual> extends FitnessComparator<T> {

    public MaximizingFitnessComparator() {
    }

    /**
     *
     * Compares two individuals based on their fitness value.
     *
     * @param o1 First individual to compare.
     * @param o2 Second individual to compare.
     * @return If the fitness value of o1 is greater than the fitness value of o2 returns -1. If
     * the fitness value of o2 is greater than the fitness value of o1 returns 1. If the fitness value
     * of o1 is equals than the fitness value of o2 returns 0.
     */
    @Override
    public int compare(T o1, T o2) {
//        if (o1.getFitness() > o2.getFitness()) {
//            return -1;
//        } else if (o1.getFitness() < o2.getFitness()) {
//            return 1;
//        } else {
//            return 0;
//        }
        
        return Double.compare(o2.getFitness(), o1.getFitness());
    }

    @Override
    public String toString() {

        return "Maximizing Fitness Comparator";

    }
}
