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
package modules.test.operations;

import es.udc.gii.common.eaf.util.EAFRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.evaluation.CoppeliaSimulator;
import modules.evaluation.staticFeatures.SymmetryFeature;
import modules.evaluation.staticFeatures.YSymmetryEvaluator;
import modules.jeaf.operation.MutationOperation;
import modules.jeaf.operation.grow.symmetry.YReflectionSymmetry;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.SimulationConfiguration;

/**
 *
 * @author fai
 */
public class CoppeliaSimTestSymmetry {

    public static void main(String[] args) {

        SimulationConfiguration.setModuleSet("OldEdhmorModules");
        ModuleSetFactory.reloadModuleSet();
        
        EAFRandom.init();
        TreeIndividual tree = new TreeIndividual();
        tree.init(141);

        //Construyo el arlbol o descomento la linea de abajo
        tree.generate();
        Node rootNode = new Node(0, null);
        Node slider1Node = new Node(1, rootNode);
        Connection conexion = new Connection(rootNode, slider1Node, 4, 3);
        rootNode.addChildren(slider1Node, conexion);

        Node tele1Node = new Node(2, slider1Node);
        conexion = new Connection(slider1Node, tele1Node, 10, 2);
        slider1Node.addChildren(tele1Node, conexion);

        Node slider2Node = new Node(1, tele1Node);
        conexion = new Connection(tele1Node, slider2Node, 5, 3);
        tele1Node.addChildren(slider2Node, conexion);
        Node tele2Node = new Node(2, rootNode);
        conexion = new Connection(rootNode, tele2Node, 10, 0);
        rootNode.addChildren(tele2Node, conexion);
//
//        Node tele3Node = new Node(2, tele2Node);
//        conexion = new Connection(tele2Node, tele3Node, 5, 0);
//        tele2Node.addChildren(tele3Node, conexion);
        tree.setRootNode(rootNode);
        tree.modifyChromosome();

        //String str = "0.0, 1.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 5.0, 11.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 69.24735028681324, 258.39017119851275, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
        ///String str = "0.0, 3.0, 3.0, 1.0, 4.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 7.0, 11.0, 7.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 6.0, 1.0, 5.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 338.6768364576931, 32.19388164330659, 134.83269977567954, 184.6111251784785, 61.47740774274864, 55.909001633456455, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
        //String str = "0.0, 1.0, 3.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 12.0, 7.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 6.0, 2.0, 6.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 31.830220182297808, 292.58161814199116, 43.9123942953938, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
//        String str = "0.0, 1.0, 1.0, 2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 6.0, 9.0, 5.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 5.0, 5.0, 3.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 197.75628015817284, 336.9559251444789, 74.9206102628252, 197.81365615880006, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.22625184189365777, 0.11070011863015516, -0.21733794325296207, -0.3016253695459623, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
//        String2Tree str2Tree = new String2Tree(str);
//        TreeIndividual tree = str2Tree.toTree();

        System.out.println("\nCromosoma (Objetive function): " + tree.toString());
        //System.out.println("\n" + tree.detailedToString());

        CoppeliaSimulator coppeliaSim = new CoppeliaSimulator();
        coppeliaSim.setGuiOn(true);
        coppeliaSim.start();
        SimulationConfiguration.setCoppeliaSim(coppeliaSim);
//        CoppeliaSimEvaluator evaluadorIni = new CoppeliaSimEvaluator(valuesIni);
        
//        double calidadIni = evaluadorIni.evaluate();
//        System.out.println("Calidad = " + calidadIni);
        SymmetryFeature symmetryFeature = new YSymmetryEvaluator();
        double symmetryValue;
        symmetryValue = symmetryFeature.getSymmetryMeasurement(tree);
        System.out.println("YSymmetry before applying the symmetry is : " + symmetryValue);
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(CoppeliaSimTestSymmetry.class.getName()).log(Level.SEVERE, null, ex);
//        }

        //MutationOperation op = new RotationalSymmetry();
        MutationOperation op = new YReflectionSymmetry();
        op.run(tree);

        System.out.println("\nCromosoma (Objetive function): " + tree.toString());
        //System.out.println("\n" + tree.detailedToString());
        
        symmetryValue = symmetryFeature.getSymmetryMeasurement(tree);
        System.out.println("\nYSymmetry2 after applying the symmetry is : " + symmetryValue);
        
        CoppeliaSimEvaluator evaluador = new CoppeliaSimEvaluator(tree.getChromosomeAt(0));
        
        try {
            Thread.sleep(1000);   //To dont send the two commands too close
        } catch (InterruptedException ex) {
            Logger.getLogger(CoppeliaSimCreateRobot.class.getName()).log(Level.SEVERE, null, ex);
        }
        //double calidad = evaluador.evaluate();
        //System.out.println("Calidad = " + calidad);
        //double calidad = evaluador.evaluate();
        //System.out.println("Calidad = " + calidad);

    }
}
