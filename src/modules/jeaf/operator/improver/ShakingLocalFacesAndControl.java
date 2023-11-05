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
package modules.jeaf.operator.improver;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.plugin.evaluation.IndividualImprover;
import java.util.ArrayList;
import java.util.List;
import modules.individual.TreeIndividual;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class ShakingLocalFacesAndControl implements IndividualImprover{

    private int nEval = 1;

    public Improvement improve(EvolutionaryAlgorithm alg, Individual seed) {
        List<Individual> variacionesIndividuo = new ArrayList();

        //System.out.println("ShakingControlOperator, numero evaluaciones: "+alg.getFEs());
        for (int i = 0; i < nEval; i++) {
            TreeIndividual a = (TreeIndividual) seed.clone();
            a.shakeDadFaceAndOrientation();

            //TODO: Comprobar que no sean iguales los distintos cromosomas???
            variacionesIndividuo.add(a);
        }

        return new Improvement(variacionesIndividuo, 0);
    }

    public void configure(Configuration conf) {
        this.nEval = conf.getInt("NEval");
    }

    public boolean doesEvaluate() {
        return false;
    }

}
