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

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.parallel.ParallelEvolutionaryAlgorithm;
import es.udc.gii.common.eaf.facade.EAFFacade;
import es.udc.gii.common.eaf.stoptest.StopTest;
import es.udc.gii.common.eaf.util.EAFRandom;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.CoppeliaSimulator;
import modules.util.SimulationConfiguration;
import mpi.MPI;
import org.apache.commons.lang.SystemUtils;

/**
 *
 * @author fai
 */
public class MainClass {

    

    public static void main(String[] args) {
        Process simulator = null;
        try {
            long iniTime = 0;
            long endTime = 0;
            long exeTime = 0;
            double totalTime = 0;
            double evaluationTime = 0;
            
            System.out.println("Evolutionary Designer of Heterogeneous Modular Robots (EDHMOR)");
            System.out.println("Using TMP directory: " + System.getProperty("java.io.tmpdir"));

            int jobId = 0;
            if (SimulationConfiguration.isUseMPI()) {
                if (args.length > 0) {
                    System.out.println("args: " + args[0]);
                    //jobId = Integer.parseInt(args[args.length - 1]);
                }
                SimulationConfiguration.setJobId(jobId);
                System.out.println("The JOB_ID is: " + jobId);

                MPI.Init(args);
            }
            CoppeliaSimulator coppeliaSimulator = new CoppeliaSimulator();
            SimulationConfiguration.setCoppeliaSim(coppeliaSimulator);
            coppeliaSimulator.start(jobId);
            
            EAFFacade facade = new EAFFacade();
            EvolutionaryAlgorithm algorithm;
            StopTest stopTest;
            EAFRandom.init();
            
            String eaConfig = SimulationConfiguration.getEaConfigFile();
            System.out.println("Using EA config file: " + eaConfig);
            algorithm = facade.createAlgorithm("./" + eaConfig);
            stopTest = facade.createStopTest("./" + eaConfig);
            
            
            iniTime = System.currentTimeMillis();
            facade.resolve(stopTest, algorithm);

            coppeliaSimulator.stop();

            FileOutputStream archivo;
            PrintStream printDebug = null;
            archivo = new FileOutputStream("results.txt", true);
            printDebug = new PrintStream(archivo);
            if (algorithm instanceof ParallelEvolutionaryAlgorithm) {
                ParallelEvolutionaryAlgorithm parallelAlg = (ParallelEvolutionaryAlgorithm) algorithm;
                if (parallelAlg.isAMaster()) {

                    printDebug.println(algorithm.getAlgorithmID() + " has finished!!!!!");
                    printDebug.println("Number of evaluations: " + algorithm.getFEs());
                    endTime = System.currentTimeMillis();
                    exeTime = endTime - iniTime;
                    totalTime = (double) exeTime / 1000;
                    evaluationTime = (double) (exeTime / 1000) / algorithm.getFEs();
                    printDebug.println("Total time: " + totalTime + " s");
                    printDebug.println("Evaluation time: " + evaluationTime + " s");
                    printDebug.close();
                    try {
                        archivo.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                printDebug.println(algorithm.getAlgorithmID() + " has finished!!!!!");
                printDebug.println("Number of evaluations: " + algorithm.getFEs());
                endTime = System.currentTimeMillis();
                exeTime = endTime - iniTime;
                totalTime = (double) exeTime / 1000;
                evaluationTime = (double) (exeTime / 1000) / algorithm.getFEs();
                printDebug.println("Total time: " + totalTime + " s");
                printDebug.println("Evaluation time: " + evaluationTime + " s");
                printDebug.close();
                    try {
                        archivo.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
            
            try {
                Thread.sleep(5000); //wait 
            } catch (InterruptedException ex) {
                Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        if (SimulationConfiguration.isUseMPI()) {
            System.out.println("Rank " + MPI.COMM_WORLD.Rank() + " is finalizing MPI");
            MPI.Finalize();
        }

    }

    


}
