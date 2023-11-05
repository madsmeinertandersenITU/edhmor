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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector3d;
import modules.ModuleSetFactory;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.SimulationConfiguration;
import modules.util.exceptions.InconsistentDataException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author fai
 */
public class XReflectionSymmetry extends SymmetryOperator {

    
    

    public XReflectionSymmetry() {
        super();
    }

    @Override
    protected Node getBranchToDoSymmetry(TreeIndividual arbol) {
        return getBranchToDoXReflectionSymmetry(arbol);
    }
    
    protected Node getBranchToDoXReflectionSymmetry(TreeIndividual arbol) {
        Node selectedChildren = null;
        Node rootNode = arbol.getRootNode();
        double fitnessC = Double.NEGATIVE_INFINITY;
        for (Node child : rootNode.getChildren()) {
            Connection con = child.getDad().getConnection(child);
            Vector3D v = ModuleSetFactory.getModulesSet().getOriginFaceVector(child.getDad().getType(), con.getDadFace());
            boolean canDoSymmetry = SymmetryTools.canSymmetryBeApplied_XReflection(v);

            if (child.getFitnessContribution() > fitnessC && canDoSymmetry) {
                selectedChildren = child;
                fitnessC = child.getFitnessContribution();
            }
        }
        return selectedChildren;
    }

    @Override
    public void repair(TreeIndividual arbol) {

        double diferenciaFitness = arbol.getFitness() - arbol.getFatherFitness();
        //El nuevo nodo aumento el fitness
        if (arbol.getFitness() > (arbol.getFatherFitness() /* *(1+this.incrementoSignificativo) */)) {
            //Llamamos a aumentar el fitnessContribution delnuevo modulo y borrar los isActiveContribution
            arbol.getRootNode().addFitnessContributionAndResetOperationalActive(diferenciaFitness);
        } else {// El nuevo nodo no aumento el fitness

            //No aumentamos el fitnessContribution borrarmos los isActiveContribution
            arbol.getRootNode().addFitnessContributionAndResetOperationalActive(0);
        }
        this.removeFatherIndividual(arbol);
    }

    @Override
    public boolean isMandatory() {
        return true;
    }

    @Override
    protected boolean areSymmetricalVectors(Vector3D a, Vector3D b) {
        return SymmetryTools.areXReflectionVectors(a, b);
    }

    @Override
    public String toString() {
        String str = "XReflectionSymmetry";
        if (this.isWorking()) {
            str += "   (isWorking)";
        } else {
            str += "   (NOT Working)";
        }
        return str;
    }

    @Override
    public void configure(Configuration conf) {
    }
}
