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
package tests.debug;

import es.udc.gii.common.eaf.util.EAFRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import modules.ModuleSet;
import modules.ModuleSetFactory;
import modules.evaluation.CalculateModulePositions;
import modules.evaluation.overlapping.BoundingBoxCollisionDetector;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.ChromoConversion;
import modules.util.ModuleRotation;
import modules.util.SimulationConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

/**
 * CollisionTest.java Created on 09/02/2016
 *
 * @author Ceyue Liu  <celi at itu.dk>
 */
public class RotationTest {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        EAFRandom.init();
        SimulationConfiguration.setModuleSet("RodrigoModules_2_3");
        ModuleSetFactory.reloadModuleSet();
        ModuleSet moduleSet = ModuleSetFactory.getModulesSet();
        
        
       
        TreeIndividual tree = new TreeIndividual();
        tree.init(141);
        tree.generate();
        double[] chromosomeDouble = ChromoConversion.str2double("0.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 1.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 2.0 1.0 1.0 0.0 2.0 1.0 1.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.0 1.0 1.0 1.0 2.0 2.0 2.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0 1.0 1.0 1.0 1.0 1.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.9827503984531508 0.6656600664873293 0.20351435515046867 0.05905961833974949 0.6471230869341015 0.3676888141677407 0.8118187196536172 0.12115884180656411 0.2056668509459454 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.6474877236321975 0.2265208069682345 0.0865501675227418 0.7475812262736474 0.8128365402951846 0.5678448760724052 0.6574285142031353 0.5791625997525364 0.5066545909806237 0.0 0.0 0.0 0.0 0.0 0.0 0.0 71.82910123874441 298.32315670760977 77.45820010528455 248.43991361412978 266.828268605085 120.83099669899835 265.3352728871928 113.46249415230878 334.2255658299579 0.0 0.0 0.0 0.0 0.0 0.0 0.0 -0.1214903748705235 -0.27150334636811335 -0.4771813116264846 -0.16118352156629479 -0.09473218668051342 0.1363657827205721 0.0662392862232346 0.10441131808227044 -0.394066536718388 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0");
        tree.setChromosomeAt(0, chromosomeDouble);
        //tree.modifyChromosome();

        CoppeliaSimEvaluator evaluator = new CoppeliaSimEvaluator(tree.getChromosomeAt(0), "");
        
        
//        CalculateModulePositions robotFeatures = new CalculateModulePositions(tree.getChromosomeAt(0));
//        Rotation[] rotations = robotFeatures.getModuleRotation();
//        for(int i = 0; i < robotFeatures.getnModules(); i++){
//            new ModuleRotation(rotations[i]).getEulerAngles();
//        }
        System.out.println("\n\n");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestDebugNewOrientationMethod.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

