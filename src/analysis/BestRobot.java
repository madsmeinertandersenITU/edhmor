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
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.CalculateModulePositions;
import modules.gui.RealWorldTestGUI;
import modules.util.ChromoConversion;

/**
 * BestRobot.java Created on 15/04/2021
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class BestRobot {

    double[] chromo;
    double fitness;
    CalculateModulePositions robotFeatures;

    public BestRobot(String fileName) {

        BufferedReader input;
        String last = "", line;
        try {
            input = new BufferedReader(new FileReader(fileName));
            while ((line = input.readLine()) != null) {
                if (!line.isBlank()) {
                    last = line;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RealWorldTestGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RealWorldTestGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        String genes = last.substring(3, last.indexOf("]"));
        System.out.println("GENES: " + genes);
        chromo = ChromoConversion.str2double(genes);
        String fitStr = last.substring(last.indexOf("]")+3, last.indexOf(",", last.indexOf("]")+3));
        System.out.println("Fitness: " + fitStr);
        fitness = Double.valueOf(fitStr);
        robotFeatures = new CalculateModulePositions(chromo);
    }

    public int getNumberModules() {
        return robotFeatures.getnModules();
    }

    public double[] getChromo() {
        return chromo;
    }

    public double getFitness() {
        return fitness;
    }

}
