/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2016 GII (UDC) and REAL (ITU)
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
package analysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.HardwareEvaluator;
import modules.gui.RealWorldTestGUI;

/**
 * NumberOfModules.java Created on 15/04/2021
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class NumberOfModules {

    public static final String path = "C:/fai/documents/papers/2021_Frontiers_emerge/HPCResults/";
    public static final int N_EXP = 30;

    public static void main(String args[]) {
        for (int i = 1; i <= N_EXP; i++) {
            String file = path + "log" + i + "/best.txt";

            BestRobot best = new BestRobot(file);

            //Write assembly time
            FileOutputStream resultsFile = null;
            try {
                resultsFile = new FileOutputStream(path + "numberOfModules.txt", true);
                PrintStream printResults = new PrintStream(resultsFile);
                printResults.println(i + " * " + best.getNumberModules() + " * " + best.getFitness());
                printResults.close();
                resultsFile.close();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
