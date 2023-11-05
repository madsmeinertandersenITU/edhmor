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
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.evaluation.CoppeliaSimulator;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.SimulationConfiguration;

/**
 *
 * @author fai
 */
public class TestSymmetry {

    public static void main(String[] args) {

        EAFRandom.init();
        SimulationConfiguration.setModuleSet("EmergeAndCuboidBaseModules");
        ModuleSetFactory.reloadModuleSet();
        int orientation = 0;
        Node rootNode = null;
        rootNode = new Node(0, 0, 0, 0, 0, 0, null);
        
        double amplitude = 1;
        double phase = 0;
        double frequency = 0.5;
        
        Node node1 = new Node(1, amplitude, frequency, phase, 0, 0, rootNode);
        Connection conn = new Connection(rootNode, node1, 4, orientation);
        rootNode.addChildren(node1, conn);
        
        //Node(int type, double amplitudeControl, double angularFreqControl, double controlOffset, double amplitudeModulator, double frequencyModulator, Node dad) {
        Node node2 = new Node(1, amplitude, frequency, phase, 0, 0, node1);
        Connection conn2 = new Connection(node1, node2, 3, 1);
        node1.addChildren(node2, conn2);
        
        Node node3 = new Node(1, amplitude, frequency, phase, 0, 0, node2);
        Connection conn3 = new Connection(node2, node3, 1, orientation);
        node2.addChildren(node3, conn3);
        
        Node node4 = new Node(1, amplitude, frequency, phase, 0, 0, node3);
        Connection conn4 = new Connection(node3, node4, 1, orientation);
        node3.addChildren(node4, conn4);
        
        Node node5 = new Node(1, amplitude, frequency, phase, 0, 0, node1);
        Connection conn5 = new Connection(node1, node5, 2, 1);
        node1.addChildren(node5, conn5);
        
        TreeIndividual tree = new TreeIndividual();
        tree.init(141);
        tree.setRootNode(rootNode);
        tree.modifyChromosome();
        
        
        CoppeliaSimulator coppeliaSim = new CoppeliaSimulator();
        coppeliaSim.setGuiOn(true);
        coppeliaSim.start();
        SimulationConfiguration.setCoppeliaSim(coppeliaSim);
        CoppeliaSimEvaluator evaluator = new CoppeliaSimEvaluator(tree.getChromosomeAt(0), "");
        evaluator.getCoppeliaSimPing();
        evaluator.getCoppeliaSimPing();
        double fitness = evaluator.evaluate();
        System.out.println("Fitness: " + fitness);
//        Node node2S = new Node(1, 0, 0, 0, 0, 0, 0, node1);
//        Node node3S = new Node(1, 0, 0, 0, 0, 0, 0, node2S);
//        Node node4S = new Node(1, 0, 0, 0, 0, 0, 0, node3S);
//        
//        int face2connect = ModuleSetFactory.getModulesSet().getSymmetricFace(node2.getType(), conn2.getDadFace());
//        Connection conn2S = new Connection(node1, node2S, face2connect, conn2.getChildrenOrientation());
//        Connection conn3S = new Connection(node2S, node3S, conn3.getDadFace(), conn3.getChildrenOrientation());
//        Connection conn4S = new Connection(node3S, node4S, conn4.getDadFace(), conn4.getChildrenOrientation());
//        node1.addChildren(node2S, conn2S);
//        node2S.addChildren(node3S, conn3S);
//        node3S.addChildren(node4S, conn4S);
//        tree.modifyChromosome();
//        
//        double[] values = tree.getChromosomes()[0].getElements();
//        CoppeliaSimEvaluator evaluador = new CoppeliaSimEvaluator(values);

        

    }
}
