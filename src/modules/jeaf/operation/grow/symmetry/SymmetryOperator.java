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
package modules.jeaf.operation.grow.symmetry;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.ModuleSet;
import modules.ModuleSetFactory;
import modules.evaluation.overlapping.CollisionDetector;
import modules.evaluation.overlapping.CollisionDetectorFactory;
import modules.jeaf.operation.grow.GrowMutationOperation;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.ModuleRotation;
import modules.util.SimulationConfiguration;
import modules.util.exceptions.InconsistentDataException;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author fai
 */
public abstract class SymmetryOperator extends GrowMutationOperation {

    FileOutputStream file;
    PrintStream printDebug = null;

    public SymmetryOperator() {
        try {
            file = new FileOutputStream("symmetry_debug", true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SymmetryOperator.class.getName()).log(Level.SEVERE, null, ex);
        }
        printDebug = new PrintStream(file);
    }

    protected abstract boolean areSymmetricalVectors(Vector3D a, Vector3D b);

    protected Node getBranchToDoSymmetry(TreeIndividual arbol) {
        Node selectedChildren = null;
        Node rootNode = arbol.getRootNode();
        double fitnessC = Double.NEGATIVE_INFINITY;
        for (Node child : rootNode.getChildren()) {
            if (child.getFitnessContribution() > fitnessC) {
                selectedChildren = child;
                fitnessC = child.getFitnessContribution();
            }
        }
        return selectedChildren;
    }

    protected int[] createSymmetry(Node branchToDoSymmetry, Node symmetricalBranch) {

        //The face and orientation of the connection for the first connection 
        //between the new branch and the other node
        int[] faceAndOrientation = new int[2];

        //Build the list of nodes to replicate
        TreeIndividual treeToDoSymmetry = new TreeIndividual();
        treeToDoSymmetry.setRootNode(branchToDoSymmetry);
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(branchToDoSymmetry.getDad());
        nodes.addAll(treeToDoSymmetry.getListNode());

        Vector3D[] modulePositionToDoSymmetry = new Vector3D[SimulationConfiguration.getMaxModules()];
        Rotation[] moduleRotationToDoSymmetry = new Rotation[SimulationConfiguration.getMaxModules()];
        modulePositionToDoSymmetry[0] = new Vector3D(0, 0, 0);
        moduleRotationToDoSymmetry[0] = Rotation.IDENTITY;

        //Build the list of nodes for the new branch
        TreeIndividual symmetricalTree = new TreeIndividual();
        symmetricalTree.setRootNode(symmetricalBranch);
        List<Node> symmetricalNodes = new ArrayList<Node>();
        symmetricalNodes.add(symmetricalBranch.getDad());
        symmetricalNodes.addAll(symmetricalTree.getListNode());

        Vector3D[] modulePositionSymmetrical = new Vector3D[SimulationConfiguration.getMaxModules()];
        Rotation[] moduleRotationSymmetrical = new Rotation[SimulationConfiguration.getMaxModules()];
        modulePositionSymmetrical[0] = new Vector3D(0, 0, 0);
        moduleRotationSymmetrical[0] = Rotation.IDENTITY;

        int[] parentModule = new int[SimulationConfiguration.getMaxModules()];

        int nodeIndex = 0;
        for (Node node : nodes) {

            Node symmetricalNode = symmetricalNodes.get(nodeIndex);

            int childTypeToDoSymmetry = node.getType();
            int childTypeSymmetrical = symmetricalNode.getType();

            if (node.getDad() != null) {
                try {
                    parentModule[nodeIndex] = nodes.indexOf(node.getDad());

                    int dadTypeToDoSymmetry = node.getDad().getType();
                    int dadTypeSymmetrical = symmetricalNode.getDad().getType();

                    Connection conexToDoSymmetry = node.getDad().getConnection(node);
                    Connection conexSymmetrical = symmetricalNode.getDad().getConnection(symmetricalNode);

                    int dadFaceToDoSymmetry = conexToDoSymmetry.getDadFace();
                    int orientationToDoSymmetry = conexToDoSymmetry.getChildrenOrientation();

                    int dadFaceSymmetrical, orientationSymmetrical;
                    if (conexSymmetrical != null) {
                        dadFaceSymmetrical = conexSymmetrical.getDadFace();
                        orientationSymmetrical = conexSymmetrical.getChildrenOrientation();
                    } else {
                        dadFaceSymmetrical = dadFaceToDoSymmetry;
                        orientationSymmetrical = orientationToDoSymmetry;
                    }

                    //Check that everything goes well. I think that this check could be removed
                    if (dadTypeToDoSymmetry != dadTypeSymmetrical || childTypeToDoSymmetry != childTypeSymmetrical || dadFaceToDoSymmetry != dadFaceSymmetrical || orientationToDoSymmetry != orientationSymmetrical) {
                        throw new InconsistentDataException("The branches to perfom the symmetry do not match:\n"
                                + "dadType: " + dadTypeToDoSymmetry + ", symmetricalDadType: " + dadTypeSymmetrical
                                + "childType: " + childTypeToDoSymmetry + ", symmetricalChildType: " + childTypeSymmetrical
                                + "dadFace: " + dadFaceToDoSymmetry + ", symmetricalDadFace: " + dadFaceSymmetrical
                                + "orientation: " + orientationToDoSymmetry + ", symmetricalOrientation: " + orientationSymmetrical);
                    }

                    ModuleSet moduleSet = ModuleSetFactory.getModulesSet();

                    //Get the vectors to the face of the parent (normal and coplanar)
                    Vector3D normalParentFaceToDoSymmetry = moduleSet.getNormalFaceVector(dadTypeToDoSymmetry, dadFaceToDoSymmetry);
                    Vector3D coplanarParentFaceToDoSymmetry = moduleSet.getCoplanarFaceVector(dadTypeToDoSymmetry, dadFaceToDoSymmetry);

                    //Calculate the rotation of the child to align both normals and 
                    //rotate them according to the orientation of the child
                    ModuleRotation rotToDoSymmetry = new ModuleRotation(childTypeToDoSymmetry, orientationToDoSymmetry, normalParentFaceToDoSymmetry, coplanarParentFaceToDoSymmetry);

                    //Get the vector from the origin of the module to the connection 
                    //face (in the parent) Origin parent -> Face parent (OFP)
                    Vector3D ofpToDoSymmetry = moduleSet.getOriginFaceVector(dadTypeToDoSymmetry, dadFaceToDoSymmetry);

                    //Face of the child to attach the module
                    int childFaceToDoSymmetry = moduleSet.getConnectionFaceForEachOrientation(childTypeToDoSymmetry, orientationToDoSymmetry);

                    //Get the vector from the origin of the module to the connection 
                    //face (in the child) Origin child -> Face child (OFC)
                    Vector3D ofcToDoSymmetry = moduleSet.getOriginFaceVector(childTypeToDoSymmetry, childFaceToDoSymmetry);

                    //Rotate the ofc vector 
                    Vector3D ofcRotatedToDoSymmetry = rotToDoSymmetry.getRotation().applyTo(ofcToDoSymmetry);
                    ofcRotatedToDoSymmetry = ofcRotatedToDoSymmetry.negate(); //Negate it                                             

                    //Obtain the position of the chid in the coord. system of the parent
                    Vector3D posVectorToDoSymmetry = ofcRotatedToDoSymmetry.add(ofpToDoSymmetry);

                    //Rotation and pos of the module in the coord. system of the world
                    moduleRotationToDoSymmetry[nodeIndex] = moduleRotationToDoSymmetry[parentModule[nodeIndex]].applyTo(rotToDoSymmetry.getRotation());
                    modulePositionToDoSymmetry[nodeIndex] = moduleRotationToDoSymmetry[parentModule[nodeIndex]].applyTo(posVectorToDoSymmetry);
                    modulePositionToDoSymmetry[nodeIndex] = modulePositionToDoSymmetry[nodeIndex].add(modulePositionToDoSymmetry[parentModule[nodeIndex]]);

                    //Calculate the ofp 
                    Vector3D globalOFPToDoSymmetry = moduleRotationToDoSymmetry[parentModule[nodeIndex]].applyTo(ofpToDoSymmetry);
                    //Calculate the vector Origin Face of the child in the world coordinates
                    Vector3D globalOFCToDoSymmetry = moduleRotationToDoSymmetry[nodeIndex].applyTo(ofcToDoSymmetry);

                    Vector3D ofpSymmetrical = null;
                    boolean feasible = false;
                    for (int face = 0; face < moduleSet.getModulesFacesNumber(dadTypeSymmetrical); face++) {
                        dadFaceSymmetrical = face;
                        ofpSymmetrical = moduleSet.getOriginFaceVector(dadTypeSymmetrical, dadFaceSymmetrical);
                        Vector3D globalOFPSymmetrical = moduleRotationSymmetrical[parentModule[nodeIndex]].applyTo(ofpSymmetrical);
                        if (this.areSymmetricalVectors(globalOFPToDoSymmetry, globalOFPSymmetrical)) {
                            feasible = true;
                            break;
                        }
                    }
                    if (!feasible) {
                        return null;    //we could not find a symmetrical branch
                    }

                    //Get the vector which is normal to the face of the parent
                    Vector3D normalParentFaceSymmetrical = moduleSet.getNormalFaceVector(dadTypeToDoSymmetry, dadFaceSymmetrical);
                    Vector3D coplanarParentFaceSymmetrical = moduleSet.getCoplanarFaceVector(dadTypeToDoSymmetry, dadFaceSymmetrical);

                    feasible = false;
                    for (int orientation = 0; orientation < moduleSet.getModuleOrientations(childTypeSymmetrical); orientation++) {
                        orientationSymmetrical = orientation;

                        //Calculate the rotation of the child to align both normals and 
                        //rotate them according to the orientation of the child
                        ModuleRotation rotSymmetrical = new ModuleRotation(childTypeSymmetrical, orientationSymmetrical, normalParentFaceSymmetrical, coplanarParentFaceSymmetrical);

                        //Face of the child to attach the module
                        int childFaceSymmetrical = moduleSet.getConnectionFaceForEachOrientation(childTypeSymmetrical, orientationSymmetrical);

                        //Get the vector from the origin of the module to the connection 
                        //face (in the child) Origin child -> Face child (OFC)
                        Vector3D ofcSymmetrical = moduleSet.getOriginFaceVector(childTypeSymmetrical, childFaceSymmetrical);

                        //Rotate the ofc vector 
                        Vector3D ofcRotatedSymmetrical = rotSymmetrical.getRotation().applyTo(ofcSymmetrical);
                        ofcRotatedSymmetrical = ofcRotatedSymmetrical.negate(); //Negate it                                             

                        //Obtain the position of the chid in the coord. system of the parent
                        Vector3D posVectorSymmetrical = ofcRotatedSymmetrical.add(ofpSymmetrical);

                        //Rotation and pos of the module in the coord. system of the world
                        moduleRotationSymmetrical[nodeIndex] = moduleRotationSymmetrical[parentModule[nodeIndex]].applyTo(rotSymmetrical.getRotation());
                        modulePositionSymmetrical[nodeIndex] = moduleRotationSymmetrical[parentModule[nodeIndex]].applyTo(posVectorSymmetrical);
                        modulePositionSymmetrical[nodeIndex] = modulePositionSymmetrical[nodeIndex].add(modulePositionSymmetrical[parentModule[nodeIndex]]);

                        //Calculo de la orientacion en coordenadas globales de och
                        Vector3D globalOFCSymmetrical = moduleRotationSymmetrical[nodeIndex].applyTo(ofcSymmetrical);

                        if (this.areSymmetricalVectors(globalOFCToDoSymmetry, globalOFCSymmetrical)) {
                            feasible = true;
                            break;
                        }
                    }
                    if (!feasible) {
                        return null;    //we could not find a symmetrical branch
                    }

                    //Change the values of the connection (dad face and 
                    //orientation) and in case that this doesnt exist (new 
                    //connection of the branch), they stored in the array to 
                    //return them 
                    if (conexSymmetrical == null) {
                        faceAndOrientation[0] = dadFaceSymmetrical;
                        faceAndOrientation[1] = orientationSymmetrical;
                    } else {
                        conexSymmetrical.setDadFace(dadFaceSymmetrical);
                        symmetricalNode.getDad().changeUsedFace(symmetricalNode, dadFaceSymmetrical);
                        conexSymmetrical.setChildrenOrientation(orientationSymmetrical);
                    }

                    //Mark this node as an active
                    symmetricalNode.setIsOperationalActive(true);

                } catch (InconsistentDataException ex) {

                    Logger.getLogger(RotationalSymmetry.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.print(treeToDoSymmetry.detailedToString());
                    System.exit(-1);
                }
            }

            nodeIndex++;
        }
        return faceAndOrientation;
    }

    @Override
    public void run(TreeIndividual tree) {

        if (SimulationConfiguration.isDebug()) {
            printDebug.println("\n\n");
            printDebug.println("Start symmetry: " + this.toString());
            printDebug.println("Tree to build the symmetry");
            printDebug.println(tree.detailedToString() + "\n");
        }

        Node branchToDoSymmetry = this.getBranchToDoSymmetry(tree);
        if (branchToDoSymmetry == null) {
            if (SimulationConfiguration.isDebug()) {
                printDebug.println("Not working: BranchToDoSymmetry is null ");
            }
            this.setIsWorking(false);
            return;
        }

        //Set the tree as a "father" before to add modifications 
        this.setFatherIndividual(tree);

        Node dad = branchToDoSymmetry.getDad();
        int nModulesOriginalTree = tree.getRootNode().getNumberModulesBranch();
        int nModulesBranchToDoSymmetry = branchToDoSymmetry.getNumberModulesBranch();
        if (SimulationConfiguration.isDebug()) {
            printDebug.println("nModulesOriginalTree: " + nModulesOriginalTree + "; nModulesBranchToDoSymmetry: " + nModulesBranchToDoSymmetry);
            printDebug.println("Branch to do the symmetry:");
            printDebug.println(branchToDoSymmetry.toString() + "\n");
        }
        //Set the tree as a father before to modify it
        this.setFatherIndividual(tree);
        try {
            Node symmetricalBranch = branchToDoSymmetry.clone(dad);

            int[] faceAndOrientation = this.createSymmetry(branchToDoSymmetry, symmetricalBranch);

            if (faceAndOrientation == null) {
                if (SimulationConfiguration.isDebug()) {
                    printDebug.println("Not working: faceAndOrientation is null ");
                }
                this.setIsWorking(false);
                return;
            }

            int dadFace = faceAndOrientation[0];

            //Check that that face is unoccupied
            if (!dad.isFaceFree(dadFace)) {
                Node childToEliminate = null;
                for (Node children : dad.getChildren()) {
                    if (dad.getConnection(children).getDadFace() == dadFace) {
                        childToEliminate = children;
                    }
                }
                //If there are a lot of modules, do not eliminate anything and cancel the operation
                if (nModulesOriginalTree - childToEliminate.getNumberModulesBranch() + nModulesBranchToDoSymmetry > SimulationConfiguration.getMaxModules()) {
                    if (SimulationConfiguration.isDebug()) {
                        printDebug.println("We do not perform the symmetry (too many modules in the resulting robot): nModulesOriginalTree: " + nModulesOriginalTree + "; nModulesBranchToDoSymmetry: " + nModulesBranchToDoSymmetry + "childToEliminate: " + childToEliminate.getNumberModulesBranch());
                    }
                    this.setIsWorking(false);
                    return;
                }
                boolean success = dad.eliminateChild(childToEliminate);
                if (!success) {
                    try {
                        throw new InconsistentDataException("We tried to remove a branch, but there was a problem.");
                    } catch (InconsistentDataException ex) {
                        if (SimulationConfiguration.isDebug()) {
                            printDebug.println("Error: Check the log");
                        }
                        Logger.getLogger(RotationalSymmetry.class.getName()).log(Level.SEVERE, null, ex);
                        System.exit(-1);
                    }
                }
                childToEliminate.eliminateBranch();
            }
            //Check that the modified tree doesnt have a lot of modules
            if (nModulesOriginalTree + nModulesBranchToDoSymmetry > SimulationConfiguration.getMaxModules()) {
                if (SimulationConfiguration.isDebug()) {
                    printDebug.println("We do not perform the symmetry (too many modules in the resulting robot): " + nModulesOriginalTree + "; nModulesBranchToDoSymmetry: " + nModulesBranchToDoSymmetry);
                }
                this.setIsWorking(false);
                return;
            }
            Connection newConnection = new Connection(symmetricalBranch.getDad(), symmetricalBranch, faceAndOrientation[0], faceAndOrientation[1]);
            //Add the new branch to the tree 
            this.addNode(branchToDoSymmetry.getDad(), symmetricalBranch, newConnection);
            //modificamos el cromosoma
            tree.modifyChromosome();
            this.setIsWorking(true);
            printDebug.println("Tree with symmetry: " + tree.detailedToString());

            CollisionDetector collisionDetector = CollisionDetectorFactory.getCollisionDetector();
            collisionDetector.loadTree(tree);
            if (collisionDetector.isFeasible()) {
                this.setIsWorking(true);
                return;
            }
            tree.setRootNode(tree.getFatherRootNode());
            tree.modifyChromosome();
            this.setIsWorking(false);

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(RotationalSymmetry.class.getName()).log(Level.SEVERE, null, ex);
            printDebug.println("Error: check the log");
        }
    }

}
