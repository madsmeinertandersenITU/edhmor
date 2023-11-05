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
package modules.jeaf.operation.grow.symmetry;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * SymmetryTools is a collection of functions to perform symmetry calculations.
 * <p>
 * This class contains a lot of static methods that can calculate if two vectors 
 * are symmetric (X reflection, Y reflection and rotational symmetry) or if one
 * vector can be used to create a symmetry branch.
 * 
 * SymmetryTools.java Created on 31/08/2020
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class SymmetryTools {
    public static boolean areXReflectionVectors(Vector3D a, Vector3D b) {
        double tolerancia = 0.0001, toleranciaP = tolerancia, toleranciaN = (-1) * tolerancia;

        double difx = a.getX() - b.getX();
        double dify = a.getY() + b.getY();
        double difz = a.getZ() - b.getZ();

        if (difx < toleranciaP) {
            if (difx > toleranciaN) {
                if (dify < toleranciaP) {
                    if (dify > toleranciaN) {
                        if (difz < toleranciaP) {
                            if (difz > toleranciaN) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean canSymmetryBeApplied_XReflection(Vector3D v){
            boolean canDoSymmetry;
            if (v.getY() < -0.01) {
                canDoSymmetry = true;
            } else canDoSymmetry = v.getY() > 0.01;
            return canDoSymmetry;
    }
    
        public static boolean canSymmetryBeApplied_YReflection(Vector3D v){
            boolean canDoSymmetry;
            if (v.getX() < -0.01) {
                canDoSymmetry = true;
            } else canDoSymmetry = v.getX() > 0.01;
            return canDoSymmetry;
    }
    
    public static boolean areYReflectionVectors(Vector3D a, Vector3D b) {
        double tolerancia = 0.0001, toleranciaP = tolerancia, toleranciaN = (-1) * tolerancia;

        double difx = a.getX() + b.getX();
        double dify = a.getY() - b.getY();
        double difz = a.getZ() - b.getZ();

        if (difx < toleranciaP) {
            if (difx > toleranciaN) {
                if (dify < toleranciaP) {
                    if (dify > toleranciaN) {
                        if (difz < toleranciaP) {
                            if (difz > toleranciaN) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean areRotationalVectors(Vector3D a, Vector3D b) {
        double tolerancia = 0.0001, toleranciaP = tolerancia, toleranciaN = (-1) * tolerancia;

        double difx = a.getX() + b.getX();
        double dify = a.getY() + b.getY();
        double difz = a.getZ() - b.getZ();

        if (difx < toleranciaP) {
            if (difx > toleranciaN) {
                if (dify < toleranciaP) {
                    if (dify > toleranciaN) {
                        if (difz < toleranciaP) {
                            if (difz > toleranciaN) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
