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
package modules.jeaf.application.edhmor;

//import es.udc.gii.common.eaf.algorithm.fitness.ObjectiveFunction;
import es.udc.gii.common.eaf.problem.objective.ObjectiveFunction;
import java.util.ArrayList;
import java.util.List;
import modules.evaluation.CalculateModulePositions;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.evaluation.dynamicFeatures.DynamicFeatures;
import modules.util.SimulationConfiguration;
import mpi.MPI;

/**
 *
 * @author fai
 */
public class RoboticObjetiveFunction extends ObjectiveFunction {

    public RoboticObjetiveFunction() {
        if (SimulationConfiguration.isUseMPI()) {
            rank = MPI.COMM_WORLD.Rank();
        }
    }
    private int nEval = 1;

    public static final int OBJECTIVE_FUNCTION = 0;
    public static final int OBJECTIVE_N_MODULES = 1;
    public static final int OBJECTIVE_FUNCTION_WORLD_1 = 2;
    public static final int OBJECTIVE_BROCKEN_CONN_WORLD_1 = 3;
    public static final int DFEATURES_BALANCE = 4;
    private int rank = 0;

    public List<Double> evaluate(double[] values) {

        double fitness = 0.0;
        int nModules = 0;
        List<Double> objectives = new ArrayList<Double>();
        double worldFitness = 0.0;
        List<String> worldsBase = SimulationConfiguration.getWorldsBase();
        double minFitness = Double.MAX_VALUE, meanFitness = 0;
        int evaluations = 0;
        for (String world : worldsBase) {

            CoppeliaSimEvaluator evaluator = new CoppeliaSimEvaluator(values, world);
            evaluator.setGuiOn(false);
            long evalTime = System.currentTimeMillis();
            worldFitness = evaluator.evaluate();
            evalTime = System.currentTimeMillis() - evalTime;
            DynamicFeatures dFeatures = evaluator.getDynamicFeatures();
            CalculateModulePositions robotFeatures = evaluator.getRobotFeatures();
            System.out.println(rank + ": Evaluation " + nEval + " of the world " + world
                    + ", evalTime: " + evalTime);
            evaluations++;
            objectives.add(worldFitness);
            nModules = robotFeatures.getnModules();
            objectives.add((double) dFeatures.getBrokenConnections());
            objectives.add(dFeatures.getBalance());

            if (worldFitness < minFitness) {
                minFitness = worldFitness;
            }
            meanFitness += worldFitness;

        }
        this.nEval++;

        if (worldsBase.size() > 1) {
            String functionToEvaluateWorlds = SimulationConfiguration.getFunctionToEvaluateWorlds();
            if (functionToEvaluateWorlds.contains("min")) {
                fitness = minFitness;
            } else {
                if (functionToEvaluateWorlds.contains("mean")) {
                    fitness = meanFitness / evaluations;
                } else {
                    String str = "RoboticObjetiveFunction: Error, there are more "
                            + "than world or parameters but the function to "
                            + "calculate the final fitness is not defined "
                            + "(minimum or mean)."
                            + "\nfunctionToEvaluateWorlds:  " + functionToEvaluateWorlds;
                    System.out.println(str);
                    System.err.println(str);
                    System.exit(-1);
                }
            }
        } else {
            fitness = worldFitness;
        }

        //Put the overall fitness
        objectives.add(OBJECTIVE_FUNCTION, fitness);
        objectives.add(OBJECTIVE_N_MODULES, (double) nModules);
        return objectives;
    }

    public void reset() {
    }
}
