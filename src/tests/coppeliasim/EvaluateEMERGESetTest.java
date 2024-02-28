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

        // CONNECTIONS TO rootNode
        Node rightLeg1 = new Node(1, rootNode);
        Connection rightLeg1connection = new Connection(rootNode, rightLeg1, 6, 1);
        rootNode.addChildren(rightLeg1, rightLeg1connection);

        Node leftLeg1 = new Node(1, rootNode);
        Connection connection = new Connection(rootNode, leftLeg1, 2, 1);
        rootNode.addChildren(leftLeg1, connection);

        Node frontLeg = new Node(1, rootNode);
        Connection frontLegConnection = new Connection(rootNode, frontLeg, 4, 1);
        rootNode.addChildren(frontLeg, frontLegConnection);

        Node backLeg = new Node(1, rootNode);
        Connection backLegConnection = new Connection(rootNode, backLeg, 8, 1);
        rootNode.addChildren(backLeg, backLegConnection);

        // Node Sensor1 = new Node(2, rootNode);
        // Connection connectionS1 = new Connection(rootNode, Sensor1, 1, 1);
        // rootNode.addChildren(Sensor1, connectionS1);

        // Node Sensor2 = new Node(2, rootNode);
        // Connection connectionS2 = new Connection(rootNode, Sensor2, 3, 1);
        // rootNode.addChildren(Sensor2, connectionS2);

        // Node Sensor3 = new Node(2, rootNode);
        // Connection connectionS3 = new Connection(rootNode, Sensor3, 5, 1);
        // rootNode.addChildren(Sensor3, connectionS3);

        // Node Sensor4 = new Node(2, rootNode);
        // Connection connectionS4 = new Connection(rootNode, Sensor4, 7, 1);
        // rootNode.addChildren(Sensor4, connectionS4);

        // CONNECTIONS TO rightLeg1
        Node rightLeg2 = new Node(1, rightLeg1);
        Connection rightLeg2connection = new Connection(rightLeg1, rightLeg2, 3, 0);
        rightLeg1.addChildren(rightLeg2, rightLeg2connection);

        // CONNECTIONS TO frontLeg
        Node frontLeg2 = new Node(1, frontLeg);
        Connection frontLegConnection2 = new Connection(frontLeg, frontLeg2, 3, 1);
        frontLeg.addChildren(frontLeg2, frontLegConnection2);

        // CONNECTIONS TO leftLeg1
        Node leftLeg2 = new Node(1, leftLeg1);
        Connection connection2 = new Connection(leftLeg1, leftLeg2, 3, 0);
        leftLeg1.addChildren(leftLeg2, connection2);

        // CONNECTIONS TO backLeg
        Node backLeg2 = new Node(1, backLeg);
        Connection backLegConnection2 = new Connection(backLeg, backLeg2, 3, 1);
        backLeg.addChildren(backLeg2, backLegConnection2);

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
