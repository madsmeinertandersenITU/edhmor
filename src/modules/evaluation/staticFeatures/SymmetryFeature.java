/* 
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2020 GII (UDC) and REAL (ITU)
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
package modules.evaluation.staticFeatures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import modules.ModuleSet;
import modules.ModuleSetFactory;
import modules.evaluation.CalculateModulePositions;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * SymmetryFeature is a generic class to calculate if an individual is
 * symmetric.
 * <p>
 * This class is an abstract class that implements the evaluation of the
 * symmetry of an individual in a generic way. There are three types of symmetry
 * evaluations that can be performed.
 *
 * SymmetryFeature.java Created on 31/08/2020
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public abstract class SymmetryFeature {

    protected abstract boolean areSymmetricalVectors(Vector3D a, Vector3D b);

    protected abstract boolean canSymmetryBeApplied(Vector3D a);

    private CalculateModulePositions modulePositions;
    private static ModuleSet moduleSet;

    public double getSymmetryMeasurement(TreeIndividual treeAux) {
        TreeIndividual tree = treeAux.clone();
        int totalModules = treeAux.getRootNode().getNumberModulesBranch();
        
        //If there is only one module, it cannot be symetrical
        if(totalModules == 1)
            return 0;
        
        Node rootNode = tree.getRootNode();
        modulePositions = new CalculateModulePositions(tree);
        moduleSet = ModuleSetFactory.getModulesSet();
        double symmetricModules = testBranchSymmetry(rootNode, rootNode);
        
        //We divide by totalModules - 1 because the first module cannot have symmetry.
        return symmetricModules / (totalModules - 1);
    }

    private int testBranchSymmetry(Node node1, Node node2) {
        int symmetricModules = 0;
        List<Node> children1 = node1.getChildren();
        List<Node> children2 = node2.getChildren();

        for (Iterator<Node> iterator = children1.iterator(); iterator.hasNext();){
            Node child = iterator.next();
            Connection con = child.getDad().getConnection(child);
            //Vector3D v = moduleSet.getOriginFaceVector(child.getDad().getType(), con.getDadFace());
            Vector3D v = modulePositions.getModulePosition()[child.getConstructionOrder()];
            boolean canDoSymmetry = canSymmetryBeApplied(v);
            //If there is a module that the is not coplanar with the symmetry plane
            if (canDoSymmetry) {
                //Lets try to find a symmetrical module
                Node symmetricalNode = null;

                for (Node child2 : children2) {
                    if (child.getType() != child2.getType()) {
                        continue;
                    }
                    Connection con2 = child2.getDad().getConnection(child2);
                    //Vector3D v2 = moduleSet.getOriginFaceVector(child2.getDad().getType(), con2.getDadFace());
                    Vector3D v2 = modulePositions.getModulePosition()[child2.getConstructionOrder()];
                    if (!areSymmetricalVectors(v, v2)) {
                        continue;
                    }
                    int childConnFace = moduleSet.getConnectionFaceForEachOrientation(child.getType(), con.getChildrenOrientation());
                    int node2ConnFace = moduleSet.getConnectionFaceForEachOrientation(child2.getType(), con2.getChildrenOrientation());
                    Vector3D ofv = moduleSet.getOriginFaceVector(child.getType(), childConnFace);
                    Vector3D ofv2 = moduleSet.getOriginFaceVector(child2.getType(), node2ConnFace);
                    Vector3D vv = modulePositions.getModuleRotation()[child.getConstructionOrder()].applyTo(ofv);
                    Vector3D vv2 = modulePositions.getModuleRotation()[child2.getConstructionOrder()].applyTo(ofv2);
                    if (areSymmetricalVectors(vv, vv2)) {
                        //The modules are symmetrical 
                        symmetricModules += 1;
                        symmetricalNode = child2;
                        symmetricModules += testBranchSymmetry(child, child2);
                        //Ideally, we should remove them and count two symmetric modules,
                        //but we raise a concurrent modifcation exception of the collection
                        //children1.remove(child);
                        //children2.remove(child2);
                        break;
                    }
                }
            } else {
                //The module is positined in the symmetry plane, remove it
                iterator.remove();
            }
        }

        return symmetricModules;
    }

}
