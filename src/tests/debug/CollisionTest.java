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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import coppelia.IntW;
import coppelia.remoteApi;
import modules.ModuleSet;
import modules.ModuleSetFactory;
import modules.evaluation.overlapping.BoundingBoxCollisionDetector;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.evaluation.CoppeliaSimulator;
import modules.evaluation.overlapping.CollisionDetector;
import modules.evaluation.overlapping.CollisionDetectorFactory;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.String2Tree;
import modules.individual.TreeIndividual;
import modules.util.ChromoConversion;
import modules.util.SimulationConfiguration;

/**
 * CollisionTest.java Created on 09/02/2016
 *
 * @author Ceyue Liu <celi at itu.dk>
 */
public class CollisionTest {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		Object[] obj2 = { "OldEdhmorModules", "RodrigoModules_1_2_3", "AtronModules" };
		String s = (String) JOptionPane.showInputDialog(null, "Please choose the module:\n", "collisiontest",
				JOptionPane.PLAIN_MESSAGE, new ImageIcon("icon.png"), obj2, "OldEdhmorModules");
		SimulationConfiguration.setModuleSet(s);
		ModuleSetFactory.reloadModuleSet();
		ModuleSet moduleSet = ModuleSetFactory.getModulesSet();

//        int type = 1; //type of  the parent module
		int orientation = 0;
		Node rootNode = null;
		String str = "";
		TreeIndividual tree = null;

		if (s == "OldEdhmorModules") {
//           rootNode = new Node(2, 0, 0, 0, 0, 0, 0, null);
//           Node node1 = new Node(1, 0, 0, 0, 0, 0, 0, rootNode);
//           Connection conn = new Connection(rootNode, node1, 4, orientation);
//           rootNode.addChildren(node1, conn);
//        
//           Node node2 = new Node(1, 0, 0, 0, 0, 0, 0, node1);
//           Connection conn2 = new Connection(rootNode, node2, 2, orientation);
//           rootNode.addChildren(node2, conn2);
//        
//           Node node3 = new Node(1, 0, 0, 0, 0, 0, 0, rootNode);
//           Connection conn3 = new Connection(rootNode, node3, 0, orientation);
//           rootNode.addChildren(node3, conn3);
//           
//           Node node4 = new Node(1, 0, 0, 0, 0, 0, 0, rootNode);
//           Connection conn4 = new Connection(rootNode, node4, 5, orientation);
//           rootNode.addChildren(node4, conn4);

			rootNode = new Node(2, 0, 0, 0, 0, 0, null);
			Node node1 = new Node(1, 0, 0, 0, 0, 0, rootNode);
			Connection conn = new Connection(rootNode, node1, 1, orientation);
			rootNode.addChildren(node1, conn);

			Node node2 = new Node(1, 0, 0, 0, 0, 0, rootNode);
			Connection conn2 = new Connection(rootNode, node2, 6, orientation);
			rootNode.addChildren(node2, conn2);

			Node node3 = new Node(1, 0, 0, 0, 0, 0, node1);
			Connection conn3 = new Connection(node1, node3, 12, orientation);
			node1.addChildren(node3, conn3);

		}

		if (s == "AtronModules") {
			// Symmetric Atron cross Robot
			str = "0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,"
					+ "                                                4.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
					+ "                                                0.0, 4.0, 6.0, 7.0, 6.0, 6.0, 6.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
					+ "                                                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
					+ "                                                0.5455963591611365, 0.11371233636543576, 0.036290954196614544, 0.3903656017030154, 0.00679176572264828, 0.08146566559447732, 0.07535369694622207, 0.7730905441161025, 0.2500507281219915, 0.40313639025411774, 0.003973608347822122, 0.7781750911928096, 0.3899374167547881, 0.9600134640670561, 0.7250404836902793, 0.8445273695804674, 0.6467894800807368, 0.16030850687424925, 0.2018541954218076, 0.49567921698965856, 0.9019485291134028, 0.20829306079896515, 0.06612457688359719, 0.49610490207718816, 0.7488436794002526, 0.9645264481423355, 0.5163520317251132, 0.2457172378705993, 0.41377103759169165, 0.12842989434914487, 0.35238585296706193, 0.22133798867604992, 344.4679226211596, 309.281008589332, 266.8072439685561, 134.9338321211479, 12.159645687248428, 57.23409787318109, 232.8997112761175, 90.64154969188141, 121.74772800789312, 252.27177708222214, 66.17009812495084, 11.886793305793866, 11.792446546844415, 205.59952213863193, 79.29145620359417, 30.7604219708471, 0.4378857762257965, -0.4539603364820609, -0.018494123142167918, 0.49687190619165233, -0.43881005244237103, 0.39648466172584684, -0.304664078161781, -0.4413269361659312, 0.2201318672469349, -0.39377771322649235, -0.3754566640525431, -0.3969155009689903, 0.017185949030072756, -0.21401444995093033, 0.1949855550960401, -0.3794066650961495, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";

		}

		if (s == "RodrigoModules_1_2_3") {

			rootNode = new Node(1, 0, 0, 0, 0, 0, null);
			Node node1 = new Node(1, 0, 0, 0, 0, 0, rootNode);
			Connection conn = new Connection(rootNode, node1, 1, 0);
			rootNode.addChildren(node1, conn);

			Node node2 = new Node(1, 0, 0, 0, 0, 0, node1);
			Connection conn2 = new Connection(node1, node2, 2, 0);
			node1.addChildren(node2, conn2);

			Node node3 = new Node(1, 0, 0, 0, 0, 0, node2);
			Connection conn3 = new Connection(node2, node3, 2, orientation);
			node2.addChildren(node3, conn3);

			Node node4 = new Node(1, 0, 0, 0, 0, 0, node3);
			Connection conn4 = new Connection(node3, node4, 2, orientation);
			node3.addChildren(node4, conn4);
		}

		if (s == "AtronModules") {

			double[] chromosomeDouble = ChromoConversion.str2double(str);
			String2Tree str2Tree = new String2Tree(str);
			tree = str2Tree.toTree();

			CoppeliaSimulator coppeliaSim = new CoppeliaSimulator();
			System.out.println("connecting to CoppeliaSim...");
			coppeliaSim.connect2CoppeliaSim();

			remoteApi coppeliaSimApi = coppeliaSim.getCoppeliaSimApi();
			int clientID = coppeliaSim.getClientID();

			CoppeliaSimCreateRobot robot = new CoppeliaSimCreateRobot(coppeliaSimApi, clientID, chromosomeDouble, "", false);
			robot.createRobot();

			IntW pingTime = new IntW(0);
			coppeliaSimApi.simxGetPingTime(clientID, pingTime);

			coppeliaSim.disconnect();

		} else {
			tree = new TreeIndividual();
			tree.init(141);
			tree.setRootNode(rootNode);
			tree.modifyChromosome();

			CoppeliaSimEvaluator evaluator = new CoppeliaSimEvaluator(tree.getChromosomeAt(0), "");
		}
                
                
		CollisionDetector collisionDetector = CollisionDetectorFactory.getCollisionDetector();
		collisionDetector.loadTree(tree);
		if(collisionDetector.isFeasible()){
                    System.out.println("There are no collisions, the robot is feasible\n");
                }else{
                    System.out.println("There are collisions, the robot is NOT feasible\n");
                }

		try {
			Thread.sleep(2000);
		} catch (InterruptedException ex) {
			Logger.getLogger(TestDebugNewOrientationMethod.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
