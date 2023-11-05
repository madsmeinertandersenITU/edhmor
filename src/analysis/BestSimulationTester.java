package analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import coppelia.remoteApi;
import modules.ModuleSetFactory;
import modules.evaluation.HardwareEvaluator;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.evaluation.CoppeliaSimulator;
import modules.gui.RealWorldTestGUI;
import modules.util.SimulationConfiguration;

public class BestSimulationTester {

    public static final String path = "./logs/";
    public static final int N_EXP = 30;
    
    public static void main(String[] args) {
        
        
        CoppeliaSimulator coppeliaSimulator = new CoppeliaSimulator();
        SimulationConfiguration.setCoppeliaSim(coppeliaSimulator);
        coppeliaSimulator.getPsUX();
        coppeliaSimulator.start();
        //coppeliaSimulator.connect2CoppeliaSim();;
       
        
        
        
        CoppeliaSimEvaluator evaluator;
        
        for (int i = 1; i <= N_EXP; i++) {
            String file = path + "log" + i + "/best.txt";
            
            BestRobot best = new BestRobot(file);
            coppeliaSimulator.getPsUX();
            
            evaluator = new CoppeliaSimEvaluator(best.getChromo());
            
            for (int j = 0; j < 10; j++) {
                
                double fitness = evaluator.evaluate();
                int brokenConn = evaluator.getDynamicFeatures().getBrokenConnections();
                
                FileOutputStream resultsFile = null;
                
                
                
                
                try {
                    resultsFile = new FileOutputStream(path + "bestSimResults.txt", true);
                    PrintStream printResults = new PrintStream(resultsFile);
                    printResults.println(i + " * " + j + " * "+ fitness + " * " + best.getFitness()+ " * "+brokenConn);
                    printResults.close();
                    resultsFile.close();

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
            
        }
        coppeliaSimulator.stop();
    }
}


