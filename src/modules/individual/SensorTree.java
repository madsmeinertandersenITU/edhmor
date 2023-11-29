package modules.individual;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.util.DoubleArray;
import org.apache.commons.math.util.ResizableDoubleArray;

import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.util.EAFRandom;
import modules.evaluation.dynamicFeatures.DynamicFeatures;
import modules.evaluation.overlapping.CollisionDetector;
import modules.evaluation.overlapping.CollisionDetectorFactory;
import modules.jeaf.operation.MutationOperation;
import modules.jeaf.stoptest.HwEvalWallTime;
import modules.util.SimulationConfiguration;
import modules.util.exceptions.InconsistentDataException;

public class SensorTree extends Individual {
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

        List<Node> nodes = new ArrayList<>();

        // Create the root node (base node)
        rootNode = new Node(0, null);
        nodes.add(rootNode);
        // Create a child node of type 1
        Node nodeType1 = new Node(1, rootNode);
        rootNode.addChildren(nodeType1);
        nodes.add(nodeType1);

        Node nodeType2 = new Node(2, nodeType1);
        nodeType1.addChildren(nodeType2);
        nodes.add(nodeType2);

        Node nodeType3 = new Node(3, nodeType2);
        nodeType2.addChildren(nodeType3);
        nodes.add(nodeType3);

        // Additional nodes or modifications as needed...

        // Modify the chromosome based on the generated tree structure
        rootNode.setModulesSubTree();
        this.modifyChromosome(nodes);

        // Check if the robot is feasible and has enough modules
        CollisionDetector collisionDetector = CollisionDetectorFactory.getCollisionDetector();
        collisionDetector.loadSensorTree(this);
        if (!collisionDetector.isFeasible() || nodes.size() < SimulationConfiguration.getNMinModulesIni()) {
            this.generate(); // If not, generate another robot
        } else {
            HwEvalWallTime.increaseElapsedWallTime(
                    SimulationConfiguration.getAssemblyTimePerModule() * nodes.size());
        }
    }

    public void modifyChromosome(List<Node> nodes) {
        DoubleArray[] cromosoma = super.getChromosomes();

        boolean base = false;
        int maxModules = SimulationConfiguration.getMaxModules();

        // Delete the chromosome
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
                    throw new InconsistentDataException(
                            "Index error while trying to modify the chromosome (changing the type of module)");
                }
                if (i < maxModules - 1) {
                    cromosoma[0].setElement(i + maxModules, node.getNConnections());
                }
                for (int k = 0; k < nodes.get(i).getNConnections(); k++) {
                    if (j < maxModules - 1) {
                        cromosoma[0].setElement(j + 2 * maxModules - 1,
                                nodes.get(i).getConnections().get(k).getDadFace());
                        cromosoma[0].setElement(j + 3 * maxModules - 2,
                                nodes.get(i).getConnections().get(k).getChildrenOrientation());
                    } else {
                        throw new InconsistentDataException(
                                "Index error while trying to modufying the chromosome (changing the parent face and orientation)");
                    }
                    j++;
                }
            } catch (InconsistentDataException ex) {
                System.err.print(ex);
                System.exit(-1);
            }

            i++;
            // Amplitude control
            cromosoma[0].setElement(4 * maxModules - 3 + nModuloGlobal, node.getControlAmplitude());
            cromosoma[0].setElement(5 * maxModules - 3 + nModuloGlobal, node.getControlAngularFreq());
            cromosoma[0].setElement(6 * maxModules - 3 + nModuloGlobal, node.getControlPhase());
            cromosoma[0].setElement(7 * maxModules - 3 + nModuloGlobal, 0.0);
            cromosoma[0].setElement(8 * maxModules - 3 + nModuloGlobal++, 0.0);
        }

        super.setChromosomes(cromosoma);
    }

}
