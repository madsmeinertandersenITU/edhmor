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
package apriltag;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import manipulator.EmergeDimensions;

/**
 * TagUtils.java
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public final class TagUtils {

    final private static int START_TAG_FOR_LINKS = 15 * 4 + 1;
    final private static int BASE_TAG = 0;
    final private static int MAX_TAG_ID = START_TAG_FOR_LINKS + 50;

    
    

    public static boolean isTagMale(int tag) {
        return (tag % 4 == 1);
    }
    
    public static boolean isTagOppositeFromMale(int tag) {
        return (tag % 4 == 3);
    }
    
    public static int Tag2DynamixelId(int faceTagID) {
        if (faceTagID == BASE_TAG) {
            //This a base module, no actuator
            return 0;
        }
        if (faceTagID >= START_TAG_FOR_LINKS) {
            //This a link module, no actuator
            return 0;
        }

        //It is a actuator module, return id
        return Math.floorDiv(faceTagID - 1, 4) + 1;
    }

    public static int getMAX_TAG_ID() {
        return MAX_TAG_ID;
    }

    public static int getSTART_TAG_FOR_LINKS() {
        return START_TAG_FOR_LINKS;
    }

    public static int getBASE_TAG() {
        return BASE_TAG;
    }

    public static int[] getFaceTagIDs(int moduleID) {
        if (moduleID == 0) {
            int[] faceTags = {BASE_TAG};
            return faceTags;
        }
        //TODO: add module tag conversor for link tags

        int[] faceTags = {1 + (moduleID - 1) * 4, 2 + (moduleID - 1) * 4, 3 + (moduleID - 1) * 4, 4 + (moduleID - 1) * 4};

        return faceTags;
    }

    public static int[] getAllFaceTagIDsFromSingleTagID(int faceTagID) {
        return getFaceTagIDs(Tag2DynamixelId(faceTagID));
    }

    /*Returns the vector to the edge where the module is connected. This method
    assumes that the module is straight (pos=0 degrees)*/
    public static Vector3D getDistanceToConnectionToBreak(int tag) {
        switch (tag % 4) {
            case 1:
                System.err.println("This connector is a male one. They are next to the edge!");
                return null;
            case 0:
                return new Vector3D(-EmergeDimensions.TOPCONN2MALEEDGE_DISTANCE, 0, 0);
            case 2:
                return new Vector3D(EmergeDimensions.TOPCONN2MALEEDGE_DISTANCE, 0, 0);
            case 3:
                System.err.println("This connector is a female but it is opossite to the connection to break. "
                        + "We need the direction to turn and it has not been implemented!");
                return null;
        }
        return null;
    }

    public static Vector3D getAxisToConnectionToBreak(int tag) {
        switch (tag % 4) {
            case 1:
                System.err.println("This connector is a male one. They are next to the edge!");
                return null;
            case 0:
                return new Vector3D(0, 1, 0);
            case 2:
                return new Vector3D(0, -1, 0);
            case 3:
                System.err.println("This connector is a female but it is opossite to the connection to break. "
                        + "We need the direction to turn and it has not been implemented!");
                return null;
        }
        return null;
    }

    public static String getCoppeliaObject(int tag) {
        if (tag == BASE_TAG) {
            return "flatBaseID0";
        }

        if (tag < START_TAG_FOR_LINKS) {
            //It is an emerge module with a motor
            String id = "ID";
            id += Tag2DynamixelId(tag);
            if (isTagMale(tag)) {
                id += "_1tag";
            } else {
                id += "_3tag";
            }
            return id;
        }
        System.err.println("Links not implemented in CoppeliaSim");
        return null;
    }


}
