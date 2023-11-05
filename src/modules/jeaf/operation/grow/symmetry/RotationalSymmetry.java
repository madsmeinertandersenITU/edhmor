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
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.DepreciatedModuleRotation;
import modules.util.SimulationConfiguration;
import modules.util.exceptions.InconsistentDataException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.math.util.ResizableDoubleArray;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author fai
 */
public class RotationalSymmetry extends SymmetryOperator {


    @Override
    public void repair(TreeIndividual arbol) {
        arbol.getRootNode().addFitnessContributionAndResetOperationalActive(0);
    }

    @Override
    public boolean isMandatory() {
        return true;
    }


    @Override
    public String toString() {
        String str = "RotationalSymmetry";
        if(this.isWorking())
            str += "   (isWorking)";
        else
            str += "   (NOT Working)";

        return str;
    }

    @Override
    protected boolean areSymmetricalVectors(Vector3D a, Vector3D b) {
        return SymmetryTools.areRotationalVectors(a, b);
    }
    
    public RotationalSymmetry() {
        super();
    }

    @Override
    public void configure(Configuration conf) {}
}
