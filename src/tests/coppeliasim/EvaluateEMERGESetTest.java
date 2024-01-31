package tests.coppeliasim;

import java.util.ArrayList;
import java.util.List;

import coppelia.IntW;
import coppelia.remoteApi;
import coppelia.examples.complexCommandTest;
import es.udc.gii.common.eaf.util.EAFRandom;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.evaluation.CoppeliaSimulator;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.SensorTree;
import modules.individual.TreeIndividual;
import modules.util.ChromoConversion;
import modules.util.SimulationConfiguration;

public class EvaluateEMERGESetTest {

    public static void main(String[] args) {

        // Set a random seed for the aleatory number generator
        EAFRandom.init();

        String moduleSet = "Emerge18Modules1-ConeProximitySensor";
        SimulationConfiguration.setModuleSet(moduleSet);
        ModuleSetFactory.reloadModuleSet();
        // Create a random robot (using the parameters specified in
        // simulationControl.xml)
        TreeIndividual robotIndividual = new TreeIndividual();
        robotIndividual.init(141);
        robotIndividual.generate();

        // Node rootNode = new Node(1, null);

        // Create the root node (base node)
        Node rootNode = new Node(0, null);

        Node rightLeg1 = new Node(1, rootNode);
        Connection rightLeg1connection = new Connection(rootNode, rightLeg1, 6, 1);
        rootNode.addChildren(rightLeg1, rightLeg1connection);

        // Node rightLeg2 = new Node(1, rightLeg1);
        // Connection rightLeg2connection = new Connection(rightLeg1, rightLeg2, 2, 1);
        // rightLeg1.addChildren(rightLeg2, rightLeg2connection);

        // Node rightLeg3 = new Node(1, rightLeg1);
        // Connection rightLeg3connection = new Connection(rightLeg2, rightLeg3, 3, 1);
        // rightLeg1.addChildren(rightLeg3, rightLeg3connection);

        // Node leftLeg1 = new Node(1, rootNode);
        // Connection connection = new Connection(rootNode, leftLeg1, 2, 1);
        // rootNode.addChildren(leftLeg1, connection);

        // Node leftLeg2 = new Node(1, leftLeg1);
        // Connection connection2 = new Connection(leftLeg1, leftLeg2, 3, 0);
        // leftLeg1.addChildren(leftLeg2, connection2);

        // Node nodeType3 = new Node(2, rootNode);
        // Connection connection3 = new Connection(rootNode, nodeType3, 4, 1);
        // rootNode.addChildren(nodeType3, connection3);

        robotIndividual.setRootNode(rootNode);
        robotIndividual.modifyChromosome();

        // Set the correct module set to employ

        // Connect to CoppeliaSim
        CoppeliaSimulator coppeliaSim = new CoppeliaSimulator();
        System.out.println("connecting to CoppeliaSim...");
        coppeliaSim.connect2CoppeliaSim();

        remoteApi coppeliaSimApi = coppeliaSim.getCoppeliaSimApi();
        int clientID = coppeliaSim.getClientID();

        // Assemble the robot in Coppelia and evaluate it
        CoppeliaSimEvaluator evaluador = new CoppeliaSimEvaluator(robotIndividual.getChromosomeAt(0));
        double fitness = evaluador.evaluate();

        System.out.println("The fitness of the robot is " + fitness);

        IntW pingTime = new IntW(0);
        coppeliaSimApi.simxGetPingTime(clientID, pingTime);

        coppeliaSim.disconnect();
    }

}
