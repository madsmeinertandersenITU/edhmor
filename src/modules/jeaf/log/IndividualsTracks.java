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
package modules.jeaf.log;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import java.util.List;
import java.util.Observable;
import modules.individual.TreeIndividual;

/**
 *
 * @author fai
 */
public class IndividualsTracks extends IndividualsLogs{

    @Override
    public void update(Observable o, Object arg) {

        EvolutionaryAlgorithm algorithm = (EvolutionaryAlgorithm)o;
        List<Individual> individuals;

        super.update(o, arg);

        if (algorithm.getState() == EvolutionaryAlgorithm.SELECT_STATE) {
            individuals = algorithm.getPopulation().getIndividuals();



            int count = 0;
            for (Individual i : individuals) {
                super.getLog(count).println("GENERACION: " + algorithm.getGenerations() +"\n");
                super.getLog(count).println("SELECT_STATE:");
                TreeIndividual t = (TreeIndividual) i;
                super.getLog(count).println(t.detailedToString() +"\n");
                count++;
            }
        }

        if (algorithm.getState() == EvolutionaryAlgorithm.REPLACE_STATE) {
            individuals = algorithm.getPopulation().getIndividuals();
     
            int count = 0;
            for (Individual i : individuals) {


                TreeIndividual t = (TreeIndividual) i;
                String operationStr = "";
                if(t.getOperation() != null)
                    operationStr = t.getOperation().toString();
                super.getLog(count).println("OPERATION: " + operationStr + "\n");
                super.getLog(count).println("REPLACE_STATE:");
                super.getLog(count).println(t.detailedToString() + "\n");
                super.getLog(count).println("------------------------------------------"+ "\n");
                count++;
            }
        }


    }

    @Override
    public String toString() {
        return "individualstracks";
    }

}
