/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2016 GII (UDC) and REAL (ITU)
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

import modules.ModuleSet;
import modules.ModuleSetFactory;

/**
 * CollisionDetectorFactory.java
 * Created on 16/02/2016
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class CollisionDetectorFactory {


    
    /**
    * getCollisionDetector gives the collision detector method based on the 
    * specified collision detector in the module set. It reads the string stored in  
    * SimulationConfiguration and creates a new module set according to this 
    * value.
     * @return he collision detector for the current module set
    */
    public static CollisionDetector  getCollisionDetector() {
        CollisionDetector cd = null;
        ModuleSet set = ModuleSetFactory.getModulesSet();
        
        switch(set.getBoundingMethod()){
            case SPHERE:
                return new SphereCollisionDetector();
            case BOX:
                return new BoundingBoxCollisionDetector();
        }
        System.err.println("There is no collision detection algorithm for the "
                + "bounding method of the module set.");
        System.err.println("Bounding method that has been defined is: "
        + set.getBoundingMethod());
        System.exit(-1);
        return null;
    }

}
