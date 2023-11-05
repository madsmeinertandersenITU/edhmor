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
import es.udc.gii.common.eaf.log.LogTool;
import java.util.List;
import java.util.Observable;
import modules.RobotFeatures;

/**
 *
 * @author fai
 */
public class RobotsMeanFeaturesLogTool extends LogTool {

    @Override
    public void update(Observable o, Object arg) {

        EvolutionaryAlgorithm algorithm = (EvolutionaryAlgorithm) o;
        List<Individual> individuals;
        super.update(o, arg);

        if (algorithm.getState() == EvolutionaryAlgorithm.REPLACE_STATE && arg == null) {

            individuals = algorithm.getPopulation().getIndividuals();
            RobotFeatures features = new RobotFeatures(individuals);
            String typePercStr = "";
            double[] typePercentage = features.getTypePerc();
            for (int i = 0; i < typePercentage.length; i++) {
                typePercStr += typePercentage[i];
                typePercStr += " - ";
            }
            super.getLog().println(
                    algorithm.getGenerations() + " - "
                    + features.getnModulos() + " - "
                    + typePercStr
                    + features.getMass() + " - "
                    + features.getMeanBrokenConn() + " - "
                    + features.getMeanDimX() + " - "
                    + features.getMeanDimY() + " - "
                    + features.getMeanDimZ() + " - "
                    + features.getDispDimX() + " - "
                    + features.getDispDimY() + " - "
                    + features.getDispDimZ() + " - "
                    + features.getMeanIx() + " - "
                    + features.getMeanIy() + " - "
                    + features.getMeanIz() + " - "
                    + features.getDispIx() + " - "
                    + features.getDispIy() + " - "
                    + features.getDispIz() + " - "
                    + features.getMeanConnectionsPerModule() + " - "
                    + features.getDispConnectionsPerModule() + " - "
                    + features.getMeanDispConnectionsPerModule()

            );
        }

    }

    @Override
    public String getLogID() {
        return "besttree";
    }

}
