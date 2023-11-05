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

import java.util.logging.Level;
import java.util.logging.Logger;
import modules.ModuleSet;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.SimulationConfiguration;

/**
 * TestDebugNewOrientationMethod.java
 * Created on 17/11/2015
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class TestDebugNewOrientationMethod {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SimulationConfiguration.setModuleSet("OldEdhmorModules");
        ModuleSetFactory.reloadModuleSet();
        ModuleSet moduleSet = ModuleSetFactory.getModulesSet();
        
        int type = 2; //type of  the parent module
        int typeChild = 2; //type of  child module
        
        for (int face = 0; face < moduleSet.getModulesFacesNumber(type); face++) {
            for (int orientation = 0; orientation < moduleSet.getModuleOrientations(typeChild); orientation++) {
                //int orientation = 2;
                System.out.println("Testing face: " + face + " and orientation: " + orientation);
                Node rootNode = new Node(type, 0, 0, 0, 0, 0, null);
                Node childNode = new Node(typeChild, 0, 0, 0, 0, 0, rootNode);
                Connection conn = new Connection(rootNode, childNode, face, orientation);
                rootNode.addChildren(childNode, conn);
                TreeIndividual tree = new TreeIndividual();
                tree.init(141);
                tree.setRootNode(rootNode);
                tree.modifyChromosome();

                CoppeliaSimEvaluator evaluator = new CoppeliaSimEvaluator(tree.getChromosomeAt(0), "");
                System.out.println("\n\n");
                
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TestDebugNewOrientationMethod.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            
        }
    }
    
}
