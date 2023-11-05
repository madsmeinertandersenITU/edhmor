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
package modules.jeaf.application.de;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.parallel.ParallelEvolutionaryAlgorithm;
import es.udc.gii.common.eaf.facade.EAFFacade;
import es.udc.gii.common.eaf.stoptest.StopTest;
import es.udc.gii.common.eaf.util.EAFRandom;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.util.SimulationConfiguration;
import mpi.MPI;

/**
 *
 * @author fai
 */
public class DEMainClass {

    public static void main(String[] args) {
        try {
            long iniTime = 0;
            long endTime = 0;
            long exeTime = 0;
            double totalTime = 0;
            double evaluationTime = 0;
            iniTime = System.currentTimeMillis();

            if (SimulationConfiguration.isUseMPI()) {
                int jobId = Integer.parseInt(args[args.length - 1]);
                SimulationConfiguration.setJobId(jobId);
                System.out.println("The JOB_ID is: " + jobId);

                MPI.Init(args);
            }
            
            EAFFacade facade = new EAFFacade();
            EvolutionaryAlgorithm algorithm;
            StopTest stopTest;
            EAFRandom.init();
            algorithm = facade.createAlgorithm("" + "de_walk_config.xml");
            stopTest = facade.createStopTest("./" + "de_walk_config.xml");
            facade.resolve(stopTest, algorithm);
            if (SimulationConfiguration.isUseMPI()) {
                MPI.Finalize();
            }
            FileOutputStream file;
            PrintStream printDebug = null;
            file = new FileOutputStream("de_results.txt", true);
            printDebug = new PrintStream(file);
            if (algorithm instanceof ParallelEvolutionaryAlgorithm) {
                ParallelEvolutionaryAlgorithm parallelAlg = (ParallelEvolutionaryAlgorithm) algorithm;
                if (parallelAlg.isAMaster()) {

                    printDebug.println( algorithm.getAlgorithmID() + " has finished!!!!!");
                    printDebug.println("Number of evaluations: " + algorithm.getFEs());
                    endTime = System.currentTimeMillis();
                    exeTime = endTime - iniTime;
                    totalTime = (double) exeTime / 1000;
                    evaluationTime = (double) (exeTime / 1000) / algorithm.getFEs();
                    printDebug.println("Total time: " + totalTime + " s");
                    printDebug.println("Evaluation time: " + evaluationTime + " s");
                }
            } else {
                printDebug.println( algorithm.getAlgorithmID() + " has finished!!!!!");
                printDebug.println("Number of evaluations: " + algorithm.getFEs());
                endTime = System.currentTimeMillis();
                exeTime = endTime - iniTime;
                totalTime = (double) exeTime / 1000;
                evaluationTime = (double) (exeTime / 1000) / algorithm.getFEs();
                printDebug.println("Total time: " + totalTime + " s");
                printDebug.println("Evaluation time: " + evaluationTime + " s");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DEMainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
