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
import es.udc.gii.common.eaf.algorithm.productTrader.IndividualsProductTrader;
import es.udc.gii.common.eaf.algorithm.productTrader.specification.BestIndividualSpecification;
import es.udc.gii.common.eaf.log.LogTool;
import java.util.Observable;
import modules.individual.TreeIndividual;

/**
 *
 * NumberEvaluationsLogTool.java Created on 04/11/2019
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class NumberEvaluationsLogTool  extends LogTool {

    @Override
    public void update(Observable o, Object arg) {

        EvolutionaryAlgorithm algorithm = (EvolutionaryAlgorithm) o;
        
        super.update(o, arg);

        if (algorithm.getState() == EvolutionaryAlgorithm.REPLACE_STATE && arg == null) {
            super.getLog().println(
                    algorithm.getGenerations() + " ; " +
                    algorithm.getFEs() );
            System.out.println("Generation " + algorithm.getGenerations() +
                    ": Number of eval: " + algorithm.getFEs());
        }

    }

    @Override
    public String getLogID() {
        return "numbereval";
    }
}
