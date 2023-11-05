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
package modules.individual;

import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.util.EAFRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.dynamicFeatures.DynamicFeatures;
import modules.evaluation.overlapping.CollisionDetector;
import modules.evaluation.overlapping.CollisionDetectorFactory;
import modules.jeaf.operation.MutationOperation;
import modules.jeaf.stoptest.HwEvalWallTime;
import modules.util.SimulationConfiguration;
import modules.util.exceptions.InconsistentDataException;
import org.apache.commons.math.util.DoubleArray;
import org.apache.commons.math.util.ResizableDoubleArray;

/**
 *
 * @author fai
 */
public class TreeIndividual extends Individual {

    private Node rootNode = null;
    private Node fatherRootNode = null;
    private double fatherFitness;
    private MutationOperation operation = null;
    private double bestFitness = Double.NEGATIVE_INFINITY;
    private Node bestRootNode = null;
    private int iterWithoutImprovement = 0;
    private int popullationOrder = 0;
    private boolean isProtected = false;
    private DynamicFeatures dFeatures = null;

    public void init(int chromosomesNumber) {
        if (super.getChromosomes() == null) {
            ResizableDoubleArray[] c = new ResizableDoubleArray[1];
            c[0] = new ResizableDoubleArray(chromosomesNumber);
            c[0].setNumElements(chromosomesNumber);
            super.setChromosomes(c);
        }
    }

    @Override
    public void generate() {
        super.generate();
        dFeatures = null;

        fatherRootNode = null;
        operation = null;

        this.bestFitness = Double.NEGATIVE_INFINITY;
        this.isProtected = false;

        int maxModulesIni;
        int maxModules = SimulationConfiguration.getMaxModules();
        if (SimulationConfiguration.getNMaxModulesIni() < maxModules) {
            maxModulesIni = SimulationConfiguration.getNMaxModulesIni();
        } else {
            maxModulesIni = maxModules;
        }

        List<Node> nodes = new ArrayList<Node>();
        List<Integer> connectionsIni = new ArrayList<Integer>();

        //Variables initialization of the tree
        int nTypeMax = SimulationConfiguration.getMaxTypeModules();
        int nTypeMin = SimulationConfiguration.getMinTypeModules();
        int nType = nTypeMax - nTypeMin + 1;
        int maxConnections = SimulationConfiguration.getNMaxConnections();

        //Select the type of the first node according to whether or not the first module is a base
        int type;
        if (SimulationConfiguration.isFistModulesBase()) {
            type = 0;
        } else {
            type = EAFRandom.nextInt(nType) + nTypeMin;
        }

        rootNode = new Node(type, null);

        int connections;
        if (SimulationConfiguration.getFirstNumConnections() == 0) {
            connections = EAFRandom.nextInt(Math.min(maxConnections, rootNode.getMaxChildren()) + 1);
        } else {
            connections = Math.min(SimulationConfiguration.getFirstNumConnections(), rootNode.getMaxChildren());
        }

        //If the number of children is greater than the number of modules in the initialization, reduce the number of children
        if (connections >= maxModulesIni) {
            connections = EAFRandom.nextInt(maxModulesIni);
        }

        nodes.add(rootNode);
        connectionsIni.add(connections);

        Node newNode;
        int totalConnections = connections;

        for (int j = 0; j < nodes.size(); j++) {
            Node node = nodes.get(j);
            for (int i = 0; i < connectionsIni.get(j); i++) {
                if (nodes.size() < maxModulesIni) {

                    type = EAFRandom.nextInt(nType) + nTypeMin;

                    newNode = new Node(type, node);
                    connections = EAFRandom.nextInt(Math.min(maxConnections, newNode.getMaxChildren()) + 1);
                    if (totalConnections + connections >= (maxModulesIni - 1)) {
                        connections = (maxModulesIni - 1) - totalConnections;
                    }
                    totalConnections += connections;
                    connectionsIni.add(connections);

                    node.addChildren(newNode);
                    nodes.add(newNode);
                } else {
                    System.err.println("The number of maximum nodes has been exceeded.");
                    System.exit(-1);
                }
            }
        }
        //We calculate the number of modules hanging from each
        rootNode.setModulesSubTree();
        this.modifyChromosome(nodes);

        //Check if the robot is feasible and have enough modules
        CollisionDetector collisionDetector = CollisionDetectorFactory.getCollisionDetector();
        collisionDetector.loadTree(this);
        if (!collisionDetector.isFeasible()
                || nodes.size() < SimulationConfiguration.getNMinModulesIni()) {
            this.generate(); //If not, generate another robot
        } else {
            HwEvalWallTime.increaseElapsedWallTime(
                    SimulationConfiguration.getAssemblyTimePerModule() * nodes.size());
        }

    }

    /*Function that returns a randomly selected node from all nodes in the tree.*/
    public Node getRandomNode() {
        Node randomNode = null;
        List<Node> nodes = createListNodes();
        randomNode = nodes.get(EAFRandom.nextInt(nodes.size()));
        return randomNode;
    }

    /**
     * Function that returns a randomly selected node from all nodes in the tree
     * without the base node (module 0).
     */
    public Node getRandomNodeWithout0() {
        Node randomNode = null;
        List<Node> nodes = createListNodes();
        if (nodes.size() > 1) {
            int moduloNumber = EAFRandom.nextInt(nodes.size() - 1);
            randomNode = nodes.get(moduloNumber + 1);
        } else {
            randomNode = null;
        }

        return randomNode;
    }

    /**
     * Function that returns a randomly selected node between those who have
     * free faces (and below the specified maximum number of connections). It is
     * used to find nodes which can be added other nodes
     *
     * @return a Node with free faces or null if i does not exist.*
     */
    public Node getRandomNodeWithFreeConnections() {
        Node randomNodeFreeConnectios = null;
        List<Node> nodes = createListNodes();

        List<Node> nodesFreeConnections = new ArrayList<>();

        for (Node node : nodes) {
            if (node.getFreeFaces() > 0 && node.getNConnections() < SimulationConfiguration.getNMaxConnections()) {
                nodesFreeConnections.add(node);
            }
        }
        if (nodesFreeConnections.size() > 0) {
            randomNodeFreeConnectios = nodesFreeConnections.get(EAFRandom.nextInt(nodesFreeConnections.size()));
            return randomNodeFreeConnectios;
        } else {
            return null;
        }
    }

    /**
     * Function that returns a randomly selected node between those who have no
     * children hanging from it. It is used to find nodes that can be removed
     * without affecting other nodes
     *
     * @return the node without children hanging from it.
     */
    public Node getRandomNodeWithoutChildrens() {
        Node randomNodeWithoutChildrens = null;
        List<Node> nodes = createListNodes();

        List<Node> nodesWithoutChildrens = new ArrayList<>();

        for (Node node : nodes) {
            if (node.getNConnections() == 0) {
                nodesWithoutChildrens.add(node);
            }
        }

        randomNodeWithoutChildrens = nodesWithoutChildrens.get(EAFRandom.nextInt(nodesWithoutChildrens.size()));
        return randomNodeWithoutChildrens;
    }

    public Node getWorstNodeWithoutChildrens() {
        Node worstNodeWithoutChildrens = null;
        List<Node> nodes = createListNodes();

        double worstFitnessContribution = Double.POSITIVE_INFINITY;
        for (Node node : nodes) {
            if (node.getNConnections() == 0) {
                if (worstFitnessContribution > node.getFitnessContribution()) {
                    worstNodeWithoutChildrens = node;
                }
            }
        }
        return worstNodeWithoutChildrens;
    }

    private List<Node> createListNodes() {
        int maxModules = SimulationConfiguration.getMaxModules();
        List<Node> nodes = new ArrayList<>();
        nodes.add(rootNode);
        rootNode.setConstructionOrder(0);
        rootNode.setLevel(0);
        for (int j = 0; j < nodes.size(); j++) {
            Node node = nodes.get(j);
            node.setConstructionOrder(j);
            if (node.getDad() != null) {
                node.setLevel(node.getDad().getLevel() + 1);
            }
            for (int i = 0; i < node.getNConnections(); i++) {
                if (nodes.size() < maxModules) {
                    nodes.add(node.getChildren().get(i));
                } else {
                    try {
                        throw new InconsistentDataException("Este arbol tiene excesivos nodos: MaxModules: " + maxModules);
                    } catch (InconsistentDataException ex) {
                        System.err.print(this.detailedToString(nodes));
                        Logger.getLogger(TreeIndividual.class.getName()).log(Level.SEVERE, null, ex);
                        System.exit(-1);
                    }

                }
            }
        }
        return nodes;
    }

    public List<Node> getListNode() {
        return createListNodes();
    }

    public void modifyChromosome() {
        modifyChromosome(createListNodes());
    }

    public void modifyChromosome(List<Node> nodes) {
        DoubleArray[] cromosoma = super.getChromosomes();

        boolean base = false;
        int maxModules = SimulationConfiguration.getMaxModules();

        //Delete the chromosome
        for (int i = 0; i < cromosoma.length; i++) {
            for (int j = 0; j < cromosoma[i].getNumElements(); j++) {
                cromosoma[i].setElement(j, 0);
            }
        }

        int i = 0, j = 0;
        int nModuloGlobal = 0;
        for (Node node : nodes) {

            try {
                if (i < maxModules) {
                    cromosoma[0].setElement(i, node.getType());
                } else {
                    throw new InconsistentDataException("Index error while trying to modufying the chromosome (changing the type of module)");
                }
                if (i < maxModules - 1) {
                    cromosoma[0].setElement(i + maxModules, node.getNConnections());
                }
                for (int k = 0; k < nodes.get(i).getNConnections(); k++) {
                    if (j < maxModules - 1) {
                        cromosoma[0].setElement(j + 2 * maxModules - 1, nodes.get(i).getConnections().get(k).getDadFace());
                        cromosoma[0].setElement(j + 3 * maxModules - 2, nodes.get(i).getConnections().get(k).getChildrenOrientation());
                    } else {
                        throw new InconsistentDataException("Index error while trying to modufying the chromosome (changing the parent face and orientation)");
                    }
                    j++;
                }
            } catch (InconsistentDataException ex) {
                System.err.print(ex);
                System.exit(-1);
            }

            i++;
            //Amplitude control
            cromosoma[0].setElement(4 * maxModules - 3 + nModuloGlobal, node.getControlAmplitude());
            cromosoma[0].setElement(5 * maxModules - 3 + nModuloGlobal, node.getControlAngularFreq());
            cromosoma[0].setElement(6 * maxModules - 3 + nModuloGlobal, node.getControlPhase());
            cromosoma[0].setElement(7 * maxModules - 3 + nModuloGlobal, 0.0);
            cromosoma[0].setElement(8 * maxModules - 3 + nModuloGlobal++, 0.0);
        }

        super.setChromosomes(cromosoma);
    }

    @Override
    public TreeIndividual clone() {
        try {
            TreeIndividual clone = null;
            clone = (TreeIndividual) super.clone();
            if (rootNode != null) {
                Node rootN = rootNode.clone();
                clone.setRootNode(rootN);
            }

            Node fatherRN = null;
            if (this.fatherRootNode != null) {
                fatherRN = this.fatherRootNode.clone();
            }
            clone.setFatherRootNode(fatherRN);

            Node bestRN = null;
            if (this.bestRootNode != null) {
                bestRN = this.bestRootNode.clone();
            }
            clone.setBestRootNode(bestRN);

            clone.setIsProtected(isProtected);
            clone.setOperation(operation);

            if (this.dFeatures != null) {
                clone.setdFeatures(this.dFeatures.clone());
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(TreeIndividual.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void copyGenotype(Individual other) {
        super.copyGenotype(other);
        TreeIndividual otherTree = (TreeIndividual) other;
        otherTree.setFatherFitness(this.fatherFitness);
        try {
            if (this.dFeatures != null) {
                otherTree.setdFeatures(this.dFeatures.clone());
            }
            otherTree.setRootNode(this.rootNode.clone());
            if (this.fatherRootNode != null) {
                otherTree.setFatherRootNode(this.fatherRootNode.clone());
            } else {
                otherTree.setFatherRootNode(null);
            }
            otherTree.setOperation(operation);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(TreeIndividual.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    public Node getFatherRootNode() {
        return fatherRootNode;
    }

    public void setFatherRootNode(Node fatherRootNode) {
        this.fatherRootNode = fatherRootNode;
    }

    public MutationOperation getOperation() {
        return operation;
    }

    public void setOperation(MutationOperation operation) {
        this.operation = operation;
    }

    public double getFatherFitness() {
        return fatherFitness;
    }

    public void setFatherFitness(double fatherFitness) {
        this.fatherFitness = fatherFitness;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }

    public Node getBestRootNode() {
        return bestRootNode;
    }

    public void setBestRootNode(Node bestRootNode) {
        this.bestRootNode = bestRootNode;
    }

    public int getPopullationOrder() {
        return popullationOrder;
    }

    public void setPopullationOrder(int popullationOrder) {
        this.popullationOrder = popullationOrder;
    }

    public boolean isIsProtected() {
        return isProtected;
    }

    public void setIsProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }

    public int getIterWithoutImprovement() {
        return iterWithoutImprovement;
    }

    public void setIterWithoutImprovement(int iter) {
        this.iterWithoutImprovement = iter;
    }

    public String detailedToString() {
        String str = new String("\nTree:");
        str += "\nChromosome and fitness: " + this.toString();
        str += "\nBestFitness: " + this.bestFitness;
        str += "\nIters without improvement: " + this.iterWithoutImprovement;
        str += "\nPopullation order: " + this.popullationOrder;
        str += "\nIs protected: " + this.isProtected;
        str += "\nNodes of the tree:";
        List<Node> nodes = this.createListNodes();
        str += this.getRootNode().branchToString();
        /*for (Node node: nodes){
        str += "\n" + node.toString();
        }*/
        str += "\n";
        return str;
    }

    public String detailedToString(List<Node> nodes) {
        String str = "\nTree:";
        str += "\nChromosome and fitness: " + this.toString();
        str += "\nNodes of the tree:\n";
        for (Node node : nodes) {
            str += "\n" + node.toString();
        }
        str += "\n";
        return str;
    }

    public String nodosToString() {

        return this.getRootNode().branchToString();
    }

    public void shakeDadFaceAndOrientation() {
        rootNode.shakeDadFaceAndOrientation();
        this.modifyChromosome();
    }

    public DynamicFeatures getdFeatures() {
        return dFeatures;
    }

    public void setdFeatures(DynamicFeatures dFeatures) {
        this.dFeatures = dFeatures;
    }

    public void shakeControl(double prob, double sigma) {
        rootNode.shakeControl(prob, sigma);
        this.modifyChromosome();
    }

    //Depreciated
    public void shakeFaces(double pMutation) {
        rootNode.shakeDadFaceAndOrientation(pMutation);
        this.modifyChromosome();
    }

    public boolean isSubTree(int delNModules) {
        return this.rootNode.isSubTree(delNModules);
    }

    public TreeIndividual generateLowSubTree(int addNModules) {
        TreeIndividual subTree = this.clone();
        subTree.rootNode = rootNode.generateLowSubTree(addNModules);
        if (subTree.rootNode == null) {
            return null;
        } else {
            subTree.modifyChromosome();
            return subTree;
        }
    }
}
