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

import es.udc.gii.common.eaf.util.EAFRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Matrix3d;
import modules.ModuleSetFactory;
import modules.util.SimulationConfiguration;
import modules.util.exceptions.InconsistentDataException;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author fai
 */
public class Node implements Cloneable {

    private int constructionOrder;
    private int type;
    private double fitnessContribution = 0;
    private boolean isOperationalActive = false;
    private Node dad;
    private List<Node> children = new ArrayList<Node>();
    private List<Connection> connections = new ArrayList<Connection>();
    private int nFaces;
    private int nOrientations;
    private int nModulesSubTree = 1;
    private List<Integer> usedFaces = new ArrayList<Integer>();
    private Vector3D xyz, rpy;
    private Matrix3d globalRpy;
    private int level = 0;

    //Control parameters
    private double controlPhase;
    private double controlAmplitude;
    private double controlAngularFreq;

    public Node(int type, double amplitudeControl, double angularFreqControl, double controlOffset, double amplitudeModulator, double frequencyModulator, Node dad) {
        this.type = type;
        this.controlAmplitude = amplitudeControl;
        this.controlAngularFreq = angularFreqControl;
        this.controlPhase = controlOffset;
        this.dad = dad;

        nFaces = ModuleSetFactory.getModulesSet().getModulesFacesNumber(type);
        nOrientations = ModuleSetFactory.getModulesSet().getModuleOrientations(type);
    }

    public Node(int type, Node dad) {
        this.type = type;
        this.dad = dad;

        double maxControlOffset = SimulationConfiguration.getMaxPhaseControl();
        double minControlOffset = SimulationConfiguration.getMinPhaseControl();
        this.controlPhase = (EAFRandom.nextDouble() * (maxControlOffset - minControlOffset)) + minControlOffset;

        double maxAmplitudeControl = SimulationConfiguration.getMaxAmplitudeControl();
        double minAmplitudeControl = SimulationConfiguration.getMinAmplitudeControl();
        this.controlAmplitude = (EAFRandom.nextDouble() * (maxAmplitudeControl - minAmplitudeControl)) + minAmplitudeControl;

        double maxAngularFreqControl = SimulationConfiguration.getMaxAngularFreqControl();
        double minAngularFreqControl = SimulationConfiguration.getMinAngularFreqControl();
        this.controlAngularFreq = (EAFRandom.nextDouble() * (maxAngularFreqControl - minAngularFreqControl)) + minAngularFreqControl;

        nFaces = ModuleSetFactory.getModulesSet().getModulesFacesNumber(type);
        nOrientations = ModuleSetFactory.getModulesSet().getModuleOrientations(type);
    }

    /**
     * Selects a face where a child module will be attached. It chooses randomly
     * a face until the selected face is available.
     *
     * @return the face where the child module will be attached
     */
    public int setFaceParent() {

        if (usedFaces.size() != this.nFaces) {

            int face;
            boolean repeat;
            int count = 0;
            do {
                face = EAFRandom.nextInt(this.nFaces);
                repeat = false;
                for (Integer usedFace : usedFaces) {
                    if (face == usedFace) {
                        repeat = true;
                    }
                }
                count++;
                if (count > 10000) {
                    System.err.println("Infinite loop: setFaceParent in Node.java");
                    System.exit(-1);
                }
            } while (repeat);
            usedFaces.add(face);
            return face;
        } else {
            try {
                throw new InconsistentDataException("We are trying to add a child in a module with all its faces used.");
            } catch (InconsistentDataException ex) {
                System.err.println("parent module type: " + this.type);
                System.err.print("Faces used: ");
                String str = "";
                for (int uf : usedFaces) {
                    str += " " + uf;
                }
                System.err.println(str);
                System.err.println("Node: " + this.toString());
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }

            return -1;
        }

    }

    /**
     * Set a face where a child module will be connected. This function just
     * checks that that face is not used.
     *
     * @param face the face where the module will be attached
     * @return the face where the module will be attached (it does not change)
     */
    public int setFaceParent(int face) {
        for (Integer usedFace : usedFaces) {
            if (face == usedFace) {
                try {
                    throw new InconsistentDataException("We are trying to add a child in a face which is already used.");
                } catch (InconsistentDataException ex) {
                    System.err.println("Face already used: " + face);
                    System.err.println("Node: " + this.toString());
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(-1);
                }
            }
        }
        usedFaces.add(face);
        return face;
    }

    /**
     * Chooses and sets one orientation for a child module. It sets the used
     * face as used.
     *
     * @return the orientation of the child module
     */
    public int setChildrenOrientation() {

        int orientation = EAFRandom.nextInt(this.nOrientations);
        int trueFace = ModuleSetFactory.getModulesSet().getConnectionFaceForEachOrientation(type, orientation);

        //We have to guarantee that the first value in the list usedFaces is the face which is used to connect to its parent module 
        if (usedFaces.size() > 0) {
            usedFaces.set(0, trueFace);
        } else {
            usedFaces.add(trueFace);
        }
        return orientation;
    }

    /**
     * Sets an orientation for a child module. It sets the used face as used.
     *
     * @param orientation the orientation to set
     * @return the orientation set
     */
    public int setChildrenOrientation(int orientation) {

        int trueFace = ModuleSetFactory.getModulesSet().getConnectionFaceForEachOrientation(type, orientation);

        //We have to guarantee that the first value in the list usedFaces is the face which is used to connect to its parent module 
        if (usedFaces.size() > 0) {
            usedFaces.set(0, trueFace);
        } else {
            usedFaces.add(trueFace);
        }

        return orientation;
    }

    public boolean isFaceFree(int face) {
        return !this.usedFaces.contains(face);
    }

    /**
     * Depreciated and with a bug!, changes the orientation of one module. It
     * changes the orientation of one module. Therefore, the orientation of its
     * children are also changed. Now, we are using shake modules function.
     * FIXME, BUG: If the module already has children, it would be possible that
     * the new orientation uses an used face.
     *
     * @param toChange the node of the child to change the orientation
     * @return if it was possible to find the child node as a children and
     * change the orientation
     */
    public boolean changeOrientation(Node toChange) {

        for (int i = 0; i < this.children.size(); i++) {
            Node child = this.children.get(i);
            if (child == toChange) {

                int oldOrientation = this.connections.get(i).getChildrenOrientation();
                int newOrientation;
                if (toChange.nOrientations > 1) {
                    int count = 0;
                    do {
                        newOrientation = EAFRandom.nextInt(toChange.nOrientations);
                        if (count++ > 1000) {
                            System.err.println("ERROR CHANGING THE ORIENTATION");
                            System.exit(-1);
                        }

                    } while (oldOrientation == newOrientation);

                    this.connections.get(i).setChildrenOrientation(newOrientation);
                    int trueFace = ModuleSetFactory.getModulesSet().getConnectionFaceForEachOrientation(child.getType(), newOrientation);
                    toChange.usedFaces.set(0, trueFace);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Depreciated, Changes the face where one child is connected to its parent.
     * Now, we are using shake modules function.
     *
     * @param toChange the node of the child to change the connection face
     * @return if it was possible to find the child node as a children and
     * change the connection face
     */
    public boolean changePosition(Node toChange) {

        for (int i = 0; i < this.children.size(); i++) {
            Node child = this.children.get(i);
            if (child == toChange) {

                int newPosition;

                //if there are free faces
                if (usedFaces.size() < this.nFaces) {
                    do {
                        newPosition = EAFRandom.nextInt(this.nFaces);
                    } while (usedFaces.contains(newPosition));

                    this.connections.get(i).setDadFace(newPosition);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public int getNumberModulesBranch() {
        int number = 0;
        for (Node child : children) {
            number += child.getNumberModulesBranch();
        }
        return number + 1;
    }

    /**
     * Function that initializes the variables in all the modules and makes that
     * all the modules call addModulesSubTree. Therefore, each node of the tree
     * will get the number of nodes that are hanging from each node (including
     * that node)
     */
    public void setModulesSubTree() {
        nModulesSubTree = 0;
        this.addModulesSubTree();
        for (int i = 0; i < this.children.size(); i++) {
            this.children.get(i).setModulesSubTree();
        }
    }

    public int getNModulesSubTree() {
        return nModulesSubTree;
    }

    /**
     * Function that calls the father modules for the calculation of the number
     * of nodes hanging from each node (including that node).
     */
    private void addModulesSubTree() {
        this.nModulesSubTree++;
        if (this.dad != null) {
            dad.addModulesSubTree();
        }
    }

    public void addChildren(Node children) {
        this.children.add(children);
        Connection conexion = new Connection(this, children);
        connections.add(conexion);
    }

    /*
     * Function that adds a child to a father in a specific position
     * (that you should provide)
     */
    public void addChildren(Node children, Connection conexion) {
        this.children.add(children);
        connections.add(conexion);
    }

    public List<Node> getChildren() {
        return children;
    }

    public double getControlPhase() {
        return controlPhase;
    }

    public double getControlAmplitude() {
        return controlAmplitude;
    }

    public double getControlAngularFreq() {
        return controlAngularFreq;
    }

    public void setControlOffset(double controlOffset) {
        this.controlPhase = controlOffset;
    }

    public Node getDad() {
        return dad;
    }

    public void setDad(Node dad) {
        this.dad = dad;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getFitnessContribution() {
        return fitnessContribution;
    }

    public void setFitnessContribution(double fitnessContribution) {
        this.fitnessContribution = fitnessContribution;
    }

    public Vector3D getRpy() {
        return rpy;
    }

    public void setRpy(Vector3D rpy) {
        this.rpy = rpy;
    }

    public Vector3D getXyz() {
        return xyz;
    }

    public void setXyz(Vector3D xyz) {
        this.xyz = xyz;
    }

    public Matrix3d getGlobalRpy() {
        return globalRpy;
    }

    public void setGlobalRpy(Matrix3d globalRpy) {
        this.globalRpy = globalRpy;
    }

    public int getAttachFaceToDad() throws InconsistentDataException {
        if (dad != null) {
            return usedFaces.get(0);
        } else {
            throw new InconsistentDataException("Trying to find the face where the module attaches to its father but this module does not have a father...");
        }
    }

    public boolean isIsOperationalActive() {
        return isOperationalActive;
    }

    public void setIsOperationalActive(boolean isOperationalActive) {
        this.isOperationalActive = isOperationalActive;
    }

    public void setLowerBranchIsOperationalActive(boolean isOperationalActive) {
        this.isOperationalActive = isOperationalActive;
        for (Node child : children) {
            child.setLowerBranchIsOperationalActive(isOperationalActive);
        }
    }

    public void setUpperBranchIsOperationalActive(boolean isOperationalActive) {
        this.isOperationalActive = isOperationalActive;
        if (dad != null) {
            dad.setUpperBranchIsOperationalActive(isOperationalActive);
        }
    }

    public void addLowerBranchFitnessContribution(double contribution) {
        if (this.isOperationalActive) {
            this.fitnessContribution += contribution;
            this.setLowerBranchIsOperationalActive(true);
        }
        this.isOperationalActive = false;
        for (Node child : children) {
            child.addLowerBranchFitnessContribution(contribution);
        }
    }

    public void addFitnessContributionAndResetOperationalActive(double contribution) {
        if (this.isOperationalActive) {
            this.fitnessContribution += contribution;
        }
        this.isOperationalActive = false;
        for (Node child : children) {
            child.addFitnessContributionAndResetOperationalActive(contribution);
        }
    }

    public void addUpperBranchFitnessContribution(double contribution) {
        if (this.isOperationalActive) {
            this.fitnessContribution += contribution;
            if (this.getDad() != null) {
                this.getDad().setIsOperationalActive(true);
                this.getDad().addUpperBranchFitnessContribution(contribution);
            }

            this.isOperationalActive = false;
            return;
        }
        this.isOperationalActive = false;
        for (Node child : children) {
            child.addUpperBranchFitnessContribution(contribution);
        }
    }

    public void setConstructionOrder(int constructionOrder) {
        this.constructionOrder = constructionOrder;
    }

    public int getConstructionOrder() {
        return this.constructionOrder;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int levelAux) {
        this.level = levelAux;
    }

    /* Function that returns the maximum number of children that this module can 
    have */
    public int getMaxChildren() {
        if (dad == null) {
            return nFaces;
        } else {
            return (nFaces - 1);
        }
    }

    /* Function that returns the number of free faces of the module */
    public int getFreeFaces() {
        return (this.getMaxChildren() - children.size());
    }

    /*Returns the number of connections of the node. That is, the number of 
    children that it has. It is always equal to connections.size() */
    public int getNConnections() {
        if (connections != null) {
            return connections.size();
        } else {
            return 0;
        }
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public Connection getConnection(Node child) {

        for (int i = 0; i < children.size(); i++) {
            Node node = children.get(i);
            if (node == child) {
                return connections.get(i);
            }
        }
        return null;
    }

    @Override
    public Node clone() throws CloneNotSupportedException {
        Node node = (Node) super.clone();
        List<Node> childrensClone = new ArrayList<Node>();
        List<Connection> connectionsClone = new ArrayList<Connection>();

        for (int i = 0; i < this.connections.size(); i++) {
            Node nod = this.children.get(i).clone(node);
            childrensClone.add(nod);
            Connection con = this.connections.get(i).clone();
            connectionsClone.add(con);
            //connectionsClone
        }
        //This should only be run in the rootNode
        if (dad == null) {
            node.dad = null;
        } else {
            System.err.print("Recursive calls to clone, ¡CHECK THIS!");
            System.exit(-1);
            //node.dad = dad.clone();
        }

        List<Integer> usedFacesClone = new ArrayList<Integer>();
        for (int a : usedFaces) {
            usedFacesClone.add(a);
        }

        node.children = childrensClone;
        node.connections = connectionsClone;
        node.usedFaces = usedFacesClone;

        //Lets put the data that depends on their position in the robot as nulls
        node.xyz = null;
        node.rpy = null;
        node.globalRpy = null;

        return node;
    }

    public Node clone(Node dadNode) throws CloneNotSupportedException {
        Node node = (Node) super.clone();
        List<Node> childrensClone = new ArrayList<Node>();
        List<Connection> connectionsClone = new ArrayList<Connection>();

        for (int i = 0; i < this.connections.size(); i++) {
            Node nod = this.children.get(i).clone(node);
            childrensClone.add(nod);
            Connection con = this.connections.get(i).clone();
            connectionsClone.add(con);
            //connectionsClone
        }

        node.dad = dadNode;

        List<Integer> usedFacesClone = new ArrayList<>();
        for (int a : usedFaces) {
            usedFacesClone.add(a);
        }

        node.children = childrensClone;
        node.connections = connectionsClone;
        node.usedFaces = usedFacesClone;

        //Lets put the data that depends on their position in the robot as nulls
        node.xyz = null;
        node.rpy = null;
        node.globalRpy = null;

        return node;
    }

    public Node generateLowSubTree(int addNModules) {
        try {

            if (this.nModulesSubTree == addNModules) {
                return this.clone(null);
            }
            Node n = null;
            for (int i = 0; i < children.size(); i++) {
                n = this.children.get(i).generateLowSubTree(addNModules);
                if (n != null) {
                    return n;
                }
            }
            return null;
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Function that replaces a branch if from that branch there are the same
     * number of modules as delNModules.Returns true if they are replaced
     * successfully, and false otherwise.
     *
     * @param delNModules the number of modules to remove
     * @param lowSubTree the branch to replace the modules from
     * @return success or not
     */
    public boolean replaceNode(int delNModules, Node lowSubTree) {
        if (this.nModulesSubTree == delNModules) {
            this.dad = null;
            this.children = lowSubTree.children;
            this.connections = lowSubTree.connections;
            this.controlPhase = lowSubTree.controlPhase;
            this.nFaces = lowSubTree.nFaces;
            this.nModulesSubTree = lowSubTree.nModulesSubTree;
            this.nOrientations = lowSubTree.nOrientations;
            this.type = lowSubTree.type;
            this.usedFaces = lowSubTree.usedFaces;
            return true;
        }

        for (int i = 0; i < children.size(); i++) {
            if (this.children.get(i).nModulesSubTree == delNModules) {

                if (this.dad == null) {
                    this.usedFaces.remove(i);
                } else {
                    this.usedFaces.remove(i + 1);
                }

                this.children.get(i).dad = null;
                this.children.remove(i);
                this.connections.remove(i);
                this.addChildren(lowSubTree);
//                    this.childrens.add(lowSubTree);
//                    Connection conexion = new Connection(this, lowSubTree);
//                    this.connections.add(conexion);
                return true;
            }
        }
        for (int i = 0; i < children.size(); i++) {
            boolean result = this.children.get(i).replaceNode(delNModules, lowSubTree);
            if (result) {
                return true;
            }
        }
        return false;
    }

    /**
     * Function that removes a child from this node(the reference from the
     * father to this child is not removed)
     *
     * @param toRemove the child to be removed
     * @return success
     */
    public boolean eliminateChild(Node toRemove) {

        for (int i = 0; i < this.children.size(); i++) {
            Node child = this.children.get(i);
            if (child == toRemove) {

                if (dad == null) {
                    usedFaces.remove(i);
                } else {
                    usedFaces.remove(i + 1);
                }

                children.remove(i);
                connections.remove(i);

                return true;
            }
        }
        return false;
    }

    /**
     * Function that removes a branch. The node and their children are removed,
     * but the father still has the reference to this child.
     */
    public void eliminateBranch() {
        //Llamamos recursivamente a eliminar los hijos
        for (int i = 0; i < children.size(); i++) {
            this.children.get(i).eliminateBranch();
        }

        this.dad = null;
        this.usedFaces = null;
        this.children = null;
        this.connections = null;
    }
    
    /**
     * Function that disconnects a node from its parent. Used in crossover
     * operations. Remember that the dad still has the reference to this child. 
     */
    public void disconnectFromParent(){
        if(dad != null) {
            usedFaces.remove(0); //Remove face where this module is connected to dad
            this.dad = null;
        }
    }

    public boolean isSubTree(int delNModules) {
        if (this.nModulesSubTree == delNModules) {
            return true;
        }
        for (int i = 0; i < children.size(); i++) {
            boolean result = this.children.get(i).isSubTree(delNModules);
            if (result) {
                return true;
            }
        }
        return false;
    }

    private double bounceBack(double param, double min, double max) {
        if (param < min) {
            return min + (min - param);
        }
        if (param > max) {
            return max - (param - max);
        }
        return param;
    }
    
    private double circularBounceBack(double param, double min, double max) {
        if (param < min) {
            return max - (min - param);
        }
        if (param > max) {
            return min + (param - max);
        }
        return param;
    }

    public void shakeControl(double prob, double sigma) {
        
        if (SimulationConfiguration.isUseAmplitudeControl() && EAFRandom.nextDouble() < prob) {
            double maxAmplitudeControl = SimulationConfiguration.getMaxAmplitudeControl();
            double minAmplitudeControl = SimulationConfiguration.getMinAmplitudeControl();
            controlAmplitude += (EAFRandom.nextGaussian() * sigma * (maxAmplitudeControl - minAmplitudeControl));
            controlAmplitude = bounceBack(controlAmplitude, minAmplitudeControl, maxAmplitudeControl);
        }

        if (SimulationConfiguration.isUseAngularFControl() && EAFRandom.nextDouble() < prob) {
            double maxAngularFreqControl = SimulationConfiguration.getMaxAngularFreqControl();
            double minAngularFreqControl = SimulationConfiguration.getMinAngularFreqControl();
            controlAngularFreq += (EAFRandom.nextGaussian() * sigma * (maxAngularFreqControl - minAngularFreqControl));
            controlAngularFreq = bounceBack(controlAngularFreq, minAngularFreqControl, maxAngularFreqControl);
        }

        if (SimulationConfiguration.isUsePhaseControl() && EAFRandom.nextDouble() < prob) {
            double maxPhaseControl = SimulationConfiguration.getMaxPhaseControl();
            double minPhaseControl = SimulationConfiguration.getMinPhaseControl();
            controlPhase += (EAFRandom.nextGaussian() * sigma * (maxPhaseControl - minPhaseControl));
            controlPhase = circularBounceBack(controlPhase, minPhaseControl, maxPhaseControl);
        }

        //Mutate the control parameters of the children
        for (Node node : children) {
            node.shakeControl(prob, sigma);
        }
    }

    public void shakeDadFaceAndOrientation() {
        for (int i = 0; i < children.size(); i++) {
            Node child = this.children.get(i);

            //If the child node is operational active, shake it:
            //    change the face in the parent where the module is connected
            //    change the orientation
            if (child.isIsOperationalActive()) {

                //change the face in the parent where the module is connected
                if (usedFaces.size() < this.nFaces) {
                    //Calculo de la nueva cara del padre
                    int newParentFace;
                    boolean repeat;
                    int count = 0;
                    do {
                        newParentFace = EAFRandom.nextInt(this.nFaces);
                        repeat = false;
                        for (Integer usedFace : usedFaces) {
                            if (newParentFace == usedFace) {
                                repeat = true;
                            }
                        }
                        count++;
                        if (count > 10000) {
                            System.out.println("Infinite Loop: shakeLocalFacesAndControl (Shaking the face in the parent where the module is connected) in Node.java");
                            System.err.println("Infinite Loop: shakeLocalFacesAndControl (Shaking the face in the parent where the module is connected) in Node.java");
                            System.exit(-1);
                        }
                    } while (repeat);
                    if (dad == null) {
                        this.usedFaces.set(i, newParentFace);
                    } else {
                        this.usedFaces.set(i + 1, newParentFace);
                    }
                    this.connections.get(i).setDadFace(newParentFace);
                }

                //Shaking the orientation
                boolean repeat;
                int newOrientation, newChildrenTrueFace;
                int count = 0;

                do {
                    newOrientation = EAFRandom.nextInt(child.nOrientations);
                    newChildrenTrueFace = ModuleSetFactory.getModulesSet().getConnectionFaceForEachOrientation(child.getType(), newOrientation);
                    repeat = false;

                    for (int f = 0; f < child.usedFaces.size(); f++) {
                        int usedFace = child.usedFaces.get(f);
                        if (newChildrenTrueFace == usedFace && f != 0) {
                            repeat = true;
                        }
                    }
                    count++;
                    if (count > 10000) {
                        System.out.println("Infinite Loop: shakeLocalFacesAndControl (Shaking the orientation) in Node.java");
                        System.err.println("Infinite Loop: shakeLocalFacesAndControl (Shaking the orientation) in Node.java");
                        System.exit(-1);
                    }
                } while (repeat);

                child.usedFaces.set(0, newChildrenTrueFace);
                this.connections.get(i).setChildrenOrientation(newOrientation);

            }
            //Mutate the children
            child.shakeDadFaceAndOrientation();
        }

    }

    public void shakeDadFaceAndOrientation(double pMutation) {
        for (int i = 0; i < children.size(); i++) {
            if (pMutation > EAFRandom.nextDouble()) {
                if (usedFaces.size() < this.nFaces) {

                    //Calculate the new face of the parent
                    int newParentFace;
                    int count = 0;
                    boolean repeat;
                    do {
                        newParentFace = EAFRandom.nextInt(this.nFaces);
                        repeat = false;
                        for (Integer usedFace : usedFaces) {
                            if (newParentFace == usedFace) {
                                repeat = true;
                            }
                        }
                        count++;
                        if (count > 10000) {
                            System.out.println("Endless Loop: shakeDadFaceAndOrientation function in Node.java");
                            System.err.println("Endless Loop: shakeDadFaceAndOrientation function in Node.java");
                            System.exit(-1);
                        }

                    } while (repeat);
                    if (dad == null) {
                        this.usedFaces.set(i, newParentFace);
                    } else {
                        this.usedFaces.set(i + 1, newParentFace);
                    }
                    this.connections.get(i).setDadFace(newParentFace);

                    int newOrientation = EAFRandom.nextInt(this.children.get(i).nOrientations);
                    int newChildrenTrueFace = ModuleSetFactory.getModulesSet().getConnectionFaceForEachOrientation(type, newOrientation);
                    this.children.get(i).usedFaces.set(0, newChildrenTrueFace);
                    this.connections.get(i).setChildrenOrientation(newOrientation);
                }
            }
            children.get(i).shakeDadFaceAndOrientation(pMutation);
        }
    }

    /**
     * Function employed to change the used face by a child ( used in symmetry
     * operations). The symmetry operation changes the value of the connection
     * and calls to this function to change the value of the used face.
     *
     * @param toChange child node which is going to be changed to a different
     * face in the parent node
     * @param newFace the face whether the child is connected now
     */
    public void changeUsedFace(Node toChange, int newFace) {

        for (int i = 0; i < this.children.size(); i++) {
            Node child = this.children.get(i);
            if (child == toChange) {
                //if it has a parent
                if (dad == null) {
                    this.usedFaces.set(i, newFace);
                } else {
                    this.usedFaces.set(i + 1, newFace);
                }
            }
        }
    }

    public String branchToString() {
        String str = this.toString();
        for (Node child : children) {
            str += "\n" + child.branchToString();
        }
        return str;
    }

    @Override
    public String toString() {

        String strIni = "";
        for (int i = 0; i < this.getLevel(); i++) {
            strIni += "\t\t";
        }

        String str = "\n" + strIni + "Node: " + this.constructionOrder;
        String tipoStr = ModuleSetFactory.getModulesSet().getModuleName(type);
        str += "\n" + strIni + "Type: " + tipoStr;
        if (this.dad != null) {
            str += "\n" + strIni + "Parent: " + this.dad.constructionOrder;
        } else {
            str += "\n" + strIni + "No parent";
        }
        str += "\n" + strIni + "Amplitude: " + this.controlAmplitude;
        str += "\n" + strIni + "Angular Frequency: " + this.controlAngularFreq;
        str += "\n" + strIni + "Phase: " + this.controlPhase;
        //str += "\n" + strIni +"Control Offset del Padre: " + this.dad.getControlOffset();
        str += "\n" + strIni + "Number of children: " + this.children.size();
        str += "\n" + strIni + "Faces occupied: ";
        for (int f : usedFaces) {
            str += f + " ";
        }
        str += "\n" + strIni + "Fitness contribution: " + this.fitnessContribution;
        if (this.dad != null) {
            if (this.getDad().getConnection(this) != null) {
                str += "\n" + strIni + "Dad face: " + this.getDad().getConnection(this).getDadFace();
                str += ",  Orientation: " + this.getDad().getConnection(this).getChildrenOrientation();
            }
        }
        //str += "\n" + strIni +"Ponderacion: " + this.ponderacion;
        //str += "\n" + strIni +"is Active: " + this.isOperationalActive;
        //str += "\n" + strIni +"xyz: " + this.xyz;
        //str += "\n" + strIni +"Rpy: " + this.rpy;
        //str += "\n" + strIni +"Global Rpy: " + this.globalRpy;
        str += "\n";
        return str;
    }
}
