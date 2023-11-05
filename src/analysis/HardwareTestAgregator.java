/* 
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2021 REAL (ITU)
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.HardwareEvaluator;
import modules.gui.RealWorldTestGUI;

/**
 * HardwareTestAgregator.java Created on 15/04/2021
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class HardwareTestAgregator {

    public static void main(String[] args) {
        String path = "C:/fai/documents/papers/2021_Frontiers_emerge/HPCResults/";
        int N_ROBOTS = 30;
        String resultFile = path + "resultsAgregated.txt";

        for (int robot = 1; robot <= N_ROBOTS; robot++) {
            String dirRobot = path + "log" + robot + "/";
            File dir = new File(dirRobot);
            FilenameFilter dirFilter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.toLowerCase().charAt(1) == '_' && dir.isDirectory());
                }
            };

            System.out.println("Robot folder: " + dirRobot);

            File[] directoryListing = dir.listFiles(dirFilter);

            if (directoryListing != null) {
                Arrays.sort(directoryListing);
                int run = 1;
                for (File child : directoryListing) {
                    System.out.println("Experiment folder: " + child.getAbsolutePath());
                    String expResult = robot + " * " + run + " * ";
                    expResult += readResult(child.getAbsolutePath());
                    run++;
                    writeResults(expResult, resultFile);

                }
            }

        }
    }

    private static String readResult(String dirName) {
        String result, fitness = "", initTime = "", endTime = "", startTime = "", disconnected = "0";
        BufferedReader input;
        String last = "", line;
        int lineNumber = 0;
        String fileName = dirName + "/results2.txt";
        File tmpDir = new File(fileName);
        if (!tmpDir.exists()) {
            fileName = dirName + "/results.txt";
        }
        try {
            input = new BufferedReader(new FileReader(fileName));
            int linesCount = 0;
            while ((line = input.readLine()) != null) {
                if (!line.isBlank()) {
                    linesCount++;
                    if (line.toLowerCase().contains("fitness")) {
                        fitness = line.substring(line.indexOf(':') + 2);
                    } else {
                        if (line.toLowerCase().contains("initialpos")) {
                            initTime = line.substring(line.lastIndexOf(':') + 2);
                        } else {
                            if (line.toLowerCase().contains("finalpos")) {
                                endTime = line.substring(line.lastIndexOf(':') + 2);
                            } else {
                                if (line.toLowerCase().contains("disco")) {
                                    disconnected = line.substring(line.lastIndexOf(':') + 2);
                                    System.out.println("Disconnectd found");
                                } else {
                                    if (line.toLowerCase().contains("startpos")) {
                                        startTime = line.substring(line.lastIndexOf(':') + 2);
                                    } else {
                                        System.err.println("Line not recognized: " + line);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (linesCount > 5) {
                System.err.println("Chech file: " + fileName);
                System.err.println("File contains too many lines: ");
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(RealWorldTestGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RealWorldTestGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fitness + " * " + initTime + " * " + endTime + " * " + disconnected;
    }

    private static void writeResults(String results, String file) {
        FileOutputStream resultsFile = null;
        try {
            resultsFile = new FileOutputStream(file, true);
            PrintStream printResults = new PrintStream(resultsFile);
            printResults.println(results);
            System.out.println(results);

            printResults.close();
            resultsFile.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
