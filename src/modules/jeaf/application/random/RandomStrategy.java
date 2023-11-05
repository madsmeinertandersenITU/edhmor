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
package modules.jeaf.application.random;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.population.Population;

/**
 *
 * @author fai
 */
public class RandomStrategy extends EvolutionaryAlgorithm {

    /** Creates a new instance of RandomStrategy */
    public RandomStrategy() {
    }

    @Override
    public String getAlgorithmID() {
        return "RandomStrategy";
    }

    @Override
    protected void select(Population toPopulation) {
        //!!NO tienen seleccion!!!
        toPopulation.setIndividuals(this.getPopulation().getIndividualsCopy());
    }

    @Override
    protected void reproduce(Population population) {
        //population.setIndividuals(
        population.generate();
    }

    @Override
    protected void replace(Population toPopulation) {
        this.getPopulation().setIndividuals(toPopulation.getIndividuals());
    }
}
