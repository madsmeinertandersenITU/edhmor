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
package modules.evaluation.staticFeatures;

import modules.jeaf.operation.grow.symmetry.SymmetryTools;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * YSymmetryEvaluator is class to calculate the symmetry around the plane YZ.
 * <p>
 * This class implements the evaluation of the symmetry of an individual 
 * using the methods provided in SymmetryFeature. 
 * 
 * YSymmetryEvaluator.java Created on 01/09/2020
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class YSymmetryEvaluator extends SymmetryFeature {

    @Override
    protected boolean areSymmetricalVectors(Vector3D a, Vector3D b) {
        return SymmetryTools.areYReflectionVectors(a, b);
    }

    @Override
    protected boolean canSymmetryBeApplied(Vector3D a) {
        return SymmetryTools.canSymmetryBeApplied_YReflection(a);
    }
}