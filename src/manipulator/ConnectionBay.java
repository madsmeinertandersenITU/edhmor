/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2022 Andres Faiña <anfv at itu.dk> (ITU)
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
package manipulator;

import apriltag.TagUtils;
import modules.ModuleSet;
import modules.ModuleSetFactory;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * ConnectionBay created on Nov 7, 2022
 *
 * @author Andres Faiña <anfv at itu.dk>
 */
public class ConnectionBay {
    
    private static final Vector3D[] bayPosition = {
        new Vector3D(-0.520, -0.3825, 0.153),
        new Vector3D(-0.630, -0.3825, 0.153),
        new Vector3D(-0.740, -0.3825, 0.153)};
    private final static Vector3D bayNormal = new Vector3D(0, 1, 0);
    private final static Rotation bayRotation = new Rotation(new Vector3D(1, 0, 0), Math.PI/2);
    
    public static Vector3D getBayPosition(int bay) {
        return bayPosition[bay];
    }
    
    public final static Vector3D getEndEffectorPos(int tagID, int bay) {
        //TODO: get into account the normal

        //Find the face ID where the module is connected
        int faceID;
        if (TagUtils.isTagMale(tagID)) {
            faceID = 0;
            System.out.println("We cannot grasp a tag from its male connector.");
            System.exit(-1);
        }
        if (TagUtils.isTagOppositeFromMale(tagID)) {
            //We are using the face opposite to the male connector
            faceID = 1;
        } else {
            //We not care if it is face 1 or 3, we want the upper vector from 
            //the male connector to the upper face as the manipulator will be aways up
            faceID = 3;
        }
        
        Rotation moduleRotation = new Rotation(new Vector3D(0,1,0), Math.PI/2).applyTo(new Rotation(new Vector3D(0,0,1), Math.PI/2));
        
        Vector3D offset_bay = ModuleSetFactory.getModulesSet().getOriginFaceVector(1, faceID).subtract(
                ModuleSetFactory.getModulesSet().getOriginFaceVector(1, 0));
        System.out.println("offset_bay: " + offset_bay);
        Vector3D offset_world = moduleRotation.applyTo(offset_bay);
        System.out.println("offset_world: " + offset_world);
        return bayPosition[bay].add(offset_world);
    }
    public static void main (String[] args){
        Vector3D a = getEndEffectorPos(4, 0);
        System.out.println(a);
    }
    
    public static Vector3D getBayNormal() {
        return bayNormal;
    }
    
    public static Rotation getBayRotation() {
        return bayRotation;
    }
    
    public static Vector3D getBayHorizontalBreakAxis() {
        return new Vector3D(1,0,0);
    }
    
    public static Vector3D getBayVerticalBreakAxis() {
        return new Vector3D(0,0,1);
    }
    
    public static Vector3D getAxisToBreak(int tag) {
        switch (tag % 4) {
            case 1:
                System.err.println("This connector is a male one. They are next to the edge!");
                return null;
            case 0:
            case 2:
                //TODO: There are more posibilities
                return new Vector3D(0, -1, 0);
            case 3:
                return new Vector3D(1, 0, 0);
        }
        return null;
    }
    
    /*Returns the vector to the edge where the module is connected. This method
    assumes that the module is straight (pos=0 degrees)*/
    public static Vector3D getDistanceToBreak(int tag, int bay) {
        switch (tag % 4) {
            case 1:
                System.err.println("This connector is a male one. They are next to the edge!");
                return null;
            case 0:
            case 2:
                //TODO: There are more posibilities
                return new Vector3D(EmergeDimensions.TOPCONN2MALEEDGE_DISTANCE, 0, 0).add(getEndEffectorPos(tag, bay));
            case 3:
                return null;//new Vector3D(TagUtils.getCONN2EDGE_DISTANCE(), 0, 0);
        }
        return null;
    }
}
