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
package modules.evaluation.overlapping;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3d;
import modules.ModuleSet;
import modules.ModuleSetFactory;
import modules.util.ModuleRotation;
import modules.util.SimulationConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import modules.individual.TreeIndividual;
import static java.lang.Math.abs;
import modules.evaluation.CalculateModulePositions;

/**
 * BoundingBoxCollisionDetector.java Created on 09/02/2016
 *
 * @author Ceyue Liu  <celi at itu.dk>
 */
public class SphereCollisionDetector extends CollisionDetector {

   
    /**
     * Class constructor to check overlapping in the robot using spheres.
     * <p>
     */
    public SphereCollisionDetector() {
    }

    

    /**************************************************************************/
    /************************** checkCollisions *******************************/
    /**************************************************************************/
    /* This method checks for collisins using a spheres. Basically, two modules
    /* collides if the distance between their centers is less than the diameter 
    /* of the bounding shere (diameter of the module) 
    ***************************************************************************/
    private boolean checkCollisions() {
        int nModules = robotFeatures.getnModules();
        int [] moduleType = robotFeatures.getModuleType();
        Vector3D[] modulePosition = robotFeatures.getModulePosition();
        double moduleDiameter = moduleSet.getBoundingSphereDiameter();
        for (Vector3D pos1 : modulePosition) {
            for (Vector3D pos2 : modulePosition) {
                if(pos1==pos2)
                    continue;
                if(pos1.distance(pos2)< moduleDiameter)
                    return false;
            }
        }
        return true;
    }

    

    @Override
    public boolean isFeasible() {
        //check collision of the modular robot. 
        return checkCollisions();
    }
}

