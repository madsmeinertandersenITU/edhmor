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
public class BoundingBoxCollisionDetector extends CollisionDetector {

    private final Vector3D V_X = new Vector3D(1, 0, 0);
    private final Vector3D V_Z = new Vector3D(0, 0, 1);
    
    /**
     * Class constructor to check overlapping in the robot using bounding boxes
     * algorithm.
     * <p>
     * It only works if the rotations of the modules are multiple of 90º
     */
    public BoundingBoxCollisionDetector() {

    }

    
    public void check() {

        //check collision of the modular robot.
        checkCollisions();

    }

    /**************************************************************************/
    /************************** checkCollisions *******************************/
    /**************************************************************************/
    // This method is based on 2D boundingBox collision detector method, but for each type of module it is 3D. 
    // Because all the rotation degree forms of each type of module are multiples of 90 degrees, and the shape of each type of module is cuboid.
    // Clearly, for each type of module it has three forms projection which is rectangle in the coordinate plane of xoy,zox and yoz. 
    // We define the boundingBox size for each type of module according to its actual value, it is represented as (width,height,length). 
    // Then according to the rotation information of each module, we can get the width and height of the projection in the coordinate plane of xoy,zox and yoz.
    // We fistly compare the projection of two modules in xoy, if collision occurs in xoy, then we compare the projection of two modules in zox. 
    // If there also a collision occurs in zox, we can conclude that the two modules collide with each other. 
    // The key point of this method is that which form of projection should be uesd in xoy and zox. 
    // I define two vectors, V_X=(1,0,0) and V_Z=(0,0,1) for each type of module. They will also rotate according to the rotation information of each module.
    // After rotation, V_X converted into a new vector V1, V_Z converted into a new vector V2.
    // Then analyzing these four vectors parallel relationship to choose the one of six form projection.
    // We divide parallel relationship into six cases.
    private boolean checkCollisions() {
        //x1,y1,rect1_w,rect1_h represent the first rectangle infromation in xoy.
        //x2,y2,rect2_w,rect2_h represent the second rectangle infromation in xoy.
        //z1,x1,rect1_l,rect1_h represent the first rectangle infromation in zox.
        //z2,x2,rect2_l,rect2_h represent the second rectangle infromation in zox.
        //t means the threshold to deal with the double decimal variation problem.
        double x1 = 0.0, y1 = 0.0, z1 = 0.0, x2 = 0, y2 = 0, z2 = 0, rect1_w = 0, rect1_h = 0, rect2_w = 0, rect2_h = 0, rect1_l = 0, rect2_l = 0, t = 0.001;
        Vector3D[] compare = new Vector3D[4];  // Using to store the vector after the V_X and V_Z rotated based on rotation information of each type of module.
        int module = 0, j = 0;                      // Respectively represent two modules
        boolean flag_xoy = false, flag_zox = false, flag_collision = false, //
                flag_decimal = true;

        int nModules = robotFeatures.getnModules();
        int [] moduleType = robotFeatures.getModuleType();
        Vector3D[] modulePosition = robotFeatures.getModulePosition();
        Rotation[] moduleRotation = robotFeatures.getModuleRotation();
        if (nModules > 2) {        //If the number of modules is greater than 2, a collision may occur. 
            for (module = 0; module < nModules; module++) {   //for the loop, one of the module will compare to the other modules. "module" represents the one of the modules, "j" represents the other modules.                         
                compare[0] = moduleRotation[module].applyTo(V_X);  // Using to store the vector after the V_X and V_Z rotated based on rotation information of each type of module.
                compare[1] = moduleRotation[module].applyTo(V_Z);
                double gb_m_X = moduleSet.getboundingBox(moduleType[module]).getX(), //Using to store the value of boundingBox of different types of module
                        gb_m_Y = moduleSet.getboundingBox(moduleType[module]).getY(),
                        gb_m_Z = moduleSet.getboundingBox(moduleType[module]).getZ();

                if (parallel(compare[0], V_X) && parallel(compare[1], V_Z)) {        // case 1
                    x1 = modulePosition[module].getX() - gb_m_Z / 2;              //according to the center position of the module to calculate the coordinate,width and height of the projection rectangle  in xoy
                    y1 = modulePosition[module].getY() - gb_m_X / 2;
                    z1 = modulePosition[module].getZ() - gb_m_Y / 2;              //according to the center position of the module to calculate the coordinate,width and height of the projection rectangle  in zox
                    rect1_w = gb_m_X;
                    rect1_h = gb_m_Z;                                         //the height of rectamgles in both xoy and zox are the same.
                    rect1_l = gb_m_Y;
                }
                if (parallel(compare[0], V_X) && (!parallel(compare[1], V_Z))) {     //case 2
                    x1 = modulePosition[module].getX() - gb_m_Z / 2;
                    y1 = modulePosition[module].getY() - gb_m_Y / 2;
                    z1 = modulePosition[module].getZ() - gb_m_X / 2;
                    rect1_w = gb_m_Y;
                    rect1_h = gb_m_Z;
                    rect1_l = gb_m_X;
                }
                if (parallel(compare[0], V_Z) && parallel(compare[1], V_X)) {        //case 3
                    x1 = modulePosition[module].getX() - gb_m_Y / 2;
                    y1 = modulePosition[module].getY() - gb_m_X / 2;
                    z1 = modulePosition[module].getZ() - gb_m_Z / 2;
                    rect1_w = gb_m_X;
                    rect1_h = gb_m_Y;
                    rect1_l = gb_m_Z;
                }
                if (parallel(compare[0], V_Z) && (!parallel(compare[1], V_X))) {     //case 4
                    x1 = modulePosition[module].getX() - gb_m_X / 2;
                    y1 = modulePosition[module].getY() - gb_m_Y / 2;
                    z1 = modulePosition[module].getZ() - gb_m_Z / 2;
                    rect1_w = gb_m_Y;
                    rect1_h = gb_m_X;
                    rect1_l = gb_m_Z;
                }
                if (parallel(compare[1], V_Z) && (!parallel(compare[0], V_X))) {     //case 5
                    x1 = modulePosition[module].getX() - gb_m_X / 2;
                    y1 = modulePosition[module].getY() - gb_m_Z / 2;
                    z1 = modulePosition[module].getZ() - gb_m_Y / 2;
                    rect1_w = gb_m_Z;
                    rect1_h = gb_m_X;
                    rect1_l = gb_m_Y;
                }
                if (parallel(compare[1], V_X) && (!parallel(compare[0], V_Z))) {     //case 6
                    x1 = modulePosition[module].getX() - gb_m_Y / 2;
                    y1 = modulePosition[module].getY() - gb_m_Z / 2;
                    z1 = modulePosition[module].getZ() - gb_m_X / 2;
                    rect1_w = gb_m_Z;
                    rect1_h = gb_m_Y;
                    rect1_l = gb_m_X;
                }

                for (j = 0; j < nModules; j++) {
                    if (j == module) {
                        continue;
                    }
                    compare[2] = moduleRotation[j].applyTo(V_X); // Using to store the vector after the V_X and V_Z rotated based on rotation information of each type of module.
                    compare[3] = moduleRotation[j].applyTo(V_Z);
                    double gb_j_X = moduleSet.getboundingBox(moduleType[j]).getX(), //Using to store the value of boundingBox of different types of module
                            gb_j_Y = moduleSet.getboundingBox(moduleType[j]).getY(),
                            gb_j_Z = moduleSet.getboundingBox(moduleType[j]).getZ();

                    if (parallel(compare[2], V_X) && parallel(compare[3], V_Z)) {       //case 1                       
                        x2 = modulePosition[j].getX() - gb_j_Z / 2;
                        y2 = modulePosition[j].getY() - gb_j_X / 2;
                        z2 = modulePosition[j].getZ() - gb_j_Y / 2;
                        rect2_w = gb_j_X;
                        rect2_h = gb_j_Z;
                        rect2_l = gb_j_Y;
                    }
                    if (parallel(compare[2], V_X) && (!parallel(compare[3], V_Z))) {    //case 2
                        x2 = modulePosition[j].getX() - gb_j_Z / 2;
                        y2 = modulePosition[j].getY() - gb_j_Y / 2;
                        z2 = modulePosition[j].getZ() - gb_j_X / 2;
                        rect2_w = gb_j_Y;
                        rect2_h = gb_j_Z;
                        rect2_l = gb_j_X;
                    }
                    if (parallel(compare[2], V_Z) && parallel(compare[3], V_X)) {      //case 3                       
                        x2 = modulePosition[j].getX() - gb_j_Y / 2;
                        y2 = modulePosition[j].getY() - gb_j_X / 2;
                        z2 = modulePosition[j].getZ() - gb_j_Z / 2;
                        rect2_w = gb_j_X;
                        rect2_h = gb_j_Y;
                        rect2_l = gb_j_Z;
                    }
                    if (parallel(compare[2], V_Z) && (!parallel(compare[3], V_X))) {   //case 4
                        x2 = modulePosition[j].getX() - gb_j_X / 2;
                        y2 = modulePosition[j].getY() - gb_j_Y / 2;
                        z2 = modulePosition[j].getZ() - gb_j_Z / 2;
                        rect2_w = gb_j_Y;
                        rect2_h = gb_j_X;
                        rect2_l = gb_j_Z;
                    }
                    if (parallel(compare[3], V_Z) && (!parallel(compare[2], V_X))) {   //case 5                      
                        x2 = modulePosition[j].getX() - gb_j_X / 2;
                        y2 = modulePosition[j].getY() - gb_j_Z / 2;
                        z2 = modulePosition[j].getZ() - gb_j_Y / 2;
                        rect2_w = gb_j_Z;
                        rect2_h = gb_j_X;
                        rect2_l = gb_j_Y;
                    }
                    if (parallel(compare[3], V_X) && (!parallel(compare[2], V_Z))) {   //case 6
                        x2 = modulePosition[j].getX() - gb_j_Y / 2;
                        y2 = modulePosition[j].getY() - gb_j_Z / 2;
                        z2 = modulePosition[j].getZ() - gb_j_X / 2;
                        rect2_w = gb_j_Z;
                        rect2_h = gb_j_Y;
                        rect2_l = gb_j_X;
                    }
                        //for the double type decimal division, a data may represent in different values. 
                    //For example,0.15, it can reprensent with 0.1500000002 or 0.149999999992.
                    //the variation of value will have influence on the analyzing conditions of 2D boundingBox collision method.
                    //So I define a threshold t_hold=0.001 to deal with this problem.

                    if ((abs(y1 - (y2 + rect2_w)) < t) || (abs(y1 + rect1_w - y2) < t) || (abs(x1 - (x2 + rect2_h)) < t) || (abs(rect1_h + x1 - x2) < t)) {
                        flag_decimal = false;
                    }
                    //the four analyzing conditions of 2D boundingBox collision method in xoy
                    if ((y1 < y2 + rect2_w) && (y1 + rect1_w > y2) && (x1 < x2 + rect2_h) && (rect1_h + x1 > x2) && flag_decimal) {
                        flag_xoy = true;                       //in xoy, there is a collision
                        flag_decimal = true;
                    }
                    if (flag_xoy == true) {
                        //for the double type decimal division, a data may represent in different values. 
                        //For example,0.15, it can reprensent with 0.1500000002 or 0.149999999992.
                        //the variation of value will have influence on the analyzing conditions of 2D boundingBox collision method.
                        //So I define a threshold t_hold=0.001 to deal with this problem.    

                        if (abs(z1 - (z2 + rect2_l)) < t || abs(z1 + rect1_l - z2) < t || abs(x1 - (x2 + rect2_h)) < t || abs(rect1_h + x1 - x2) < t) {
                            flag_decimal = false;
                        }
                        //the four analyzing conditions of 2D boundingBox collision method in zox
                        if ((z1 < z2 + rect2_l) && (z1 + rect1_l > z2) && (x1 < x2 + rect2_h) && (rect1_h + x1 > x2) && flag_decimal) {
                            flag_zox = true;                   //in zox, there is a collision
                            flag_decimal = true;
                        }
                    }
                    if (flag_xoy == true && flag_zox == true) {      //if a collision occurs in both xoy and zox, then two of the modules collide with eachother.
                        //System.out.println("Collision: module" + module + " collided with module" + j); //print out the number of coliision modules
                        flag_collision = true;
                        j = nModules;
                        module = nModules;//finish the loop
                    }
                    flag_decimal = true;
                    flag_xoy = false;
                    flag_zox = false;
                }
            }
            if (flag_collision == false) {
                //System.out.println("No collision");
            }
        } else {
            //System.out.println("No collision");
        }
        return !flag_collision;
    }

    //Analyzing the parallel relationship between the two vectors
    private boolean parallel(Vector3D V1, Vector3D V2) {
        boolean result = false;
        double t = 0.001;
        if (abs(V1.getX()) - abs(V2.getX()) < t
                && abs(V1.getY()) - abs(V2.getY()) < t
                && abs(V1.getZ()) - abs(V2.getZ()) < t) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public boolean isFeasible() {
        //check collision of the modular robot. 
        return checkCollisions();
    }
}
