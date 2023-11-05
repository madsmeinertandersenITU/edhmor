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
package modules;


import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author fai
 */
public class RealEdhmorModuleSet extends EdhmorModuleSet {

    public RealEdhmorModuleSet() {
        //six types of modules, up to 14 faces in a module and up to 7 orientations
        super(6, 14, 7); 

        //The name of this set of modules (it has to match with the folder
        //where the modules are stored in the edhmor folder)
        moduleSetName = "realEdhmorModules";
        
        //The name of the different modules (they have to match with the name of
        //the file to load in the simulator)
        moduleName[0] = "base";
        moduleName[1] = "slider";
        moduleName[2] = "telescopic";
        moduleName[3] = "rotational";
        moduleName[4] = "hinge";
        moduleName[5] = "endEffector";
        
        //define the different properties
        
        //number of faces for each type of module
        modulesFacesNumber[0] = 12; //Number of faces for the base module
        modulesFacesNumber[1] = 14; //Number of faces for the slider module
        modulesFacesNumber[2] = 10; //Number of faces for the telescopic module
        modulesFacesNumber[3] = 10; //Number of faces for the rotational module
        modulesFacesNumber[4] = 2;  //Number of faces for the hinge module
        modulesFacesNumber[5] = 1;  //Number of faces for the end effector module

        //Number of faces in the base body for each type of module; the other faces are in the actuator body
        modulesBaseFacesNumber[0] = 12; //Number of faces in the base for the base module (all)
        modulesBaseFacesNumber[1] = 10; //Number of faces in the base for the slider module
        modulesBaseFacesNumber[2] = 5;  //Number of faces in the base for the telescopic module
        modulesBaseFacesNumber[3] = 5;  //Number of faces in the base for the rotational module
        modulesBaseFacesNumber[4] = 1;  //Number of faces in the base for the hinge module
        modulesBaseFacesNumber[5] = 1;  //Number of faces in the base for the end effector module

        //number of posible orientations when a module is joined to other module
        moduleOrientations[0] = 3; //Number of orientations of the module base
        moduleOrientations[1] = 7; //Number of orientations of the slider module
        moduleOrientations[2] = 5; //Number of orientations of the telescopic module
        moduleOrientations[3] = 5; //Number of orientations of the rotational module
        moduleOrientations[4] = 2; //Number of orientations of the hinge module
        moduleOrientations[5] = 1; //Number of orientations of the end effector module
        

        //mass of each module in Kg
        modulesMass[0] = 0.4;  //Mass of the base module
        modulesMass[1] = 0.4;  //Mass of the slider module
        modulesMass[2] = 0.4;  //Mass of the telescopic module
        modulesMass[3] = 0.2;  //Mass of the rotational module
        modulesMass[4] = 0.2;  //Mass of the hinge module
        modulesMass[5] = 0.2;  //Mass of the end effector module
        
        
        //control parameters
        
        //max amplitude
        modulesMaxAmplitude[0] = 0;  //The base doesnt have any actuator
        modulesMaxAmplitude[1] = 0.3;//Max amplitude of the slider
        modulesMaxAmplitude[2] = 0.2;//Max amplitude of the telescopic
        modulesMaxAmplitude[3] = Math.PI * 0.5;//Max amplitude of the rotational
        modulesMaxAmplitude[4] = Math.PI / 3;  //Max amplitude of the hinge
        modulesMaxAmplitude[5] = 0;  //The end effector doesnt have any actuator

        //max angular frequency
        modulesMaxAngularFrequency[0] = 0;  //The base doesnt have any actuator
        modulesMaxAngularFrequency[1] = 2.0;//Max angular freq of the slider
        modulesMaxAngularFrequency[2] = 2.0;//Max angular freq of the telescopic
        modulesMaxAngularFrequency[3] = 2.0;//Max angular freq of the rotational
        modulesMaxAngularFrequency[4] = 2.0;//Max angular freq of the hinge
        modulesMaxAngularFrequency[5] = 0;  //The end effector has no actuator


        /**********************************************************************/
        /************************** originFaceVector **************************/
        /**********************************************************************/
        // This are the coordinates of the vector from the origon of the module to
        // the face of the module for each face and module type
        
        //Base module:
        originFaceVector[0][0] = new Vector3D(-0.45, 0.45, 0.05);   //Face 0
        originFaceVector[0][1] = new Vector3D(-0.45, 0.5, 0);       //Face 1
        originFaceVector[0][2] = new Vector3D(-0.45, 0.45, -0.05);  //Face 2
        originFaceVector[0][3] = new Vector3D(-0.45, -0.45, 0.05);
        originFaceVector[0][4] = new Vector3D(-0.45, -0.5, 0);
        originFaceVector[0][5] = new Vector3D(-0.45, -0.45, -0.05);
        originFaceVector[0][6] = new Vector3D(0.45, -0.45, 0.05);
        originFaceVector[0][7] = new Vector3D(0.45, -0.5, 0);
        originFaceVector[0][8] = new Vector3D(0.45, -0.45, -0.05);
        originFaceVector[0][9] = new Vector3D(0.45, 0.45, 0.05);
        originFaceVector[0][10] = new Vector3D(0.45, 0.5, 0);
        originFaceVector[0][11] = new Vector3D(0.45, 0.45, -0.05);  //Face 11

        //Slider module
        originFaceVector[1][0] = new Vector3D(-0.5, 0, 0);        //Face 0   
        originFaceVector[1][1] = new Vector3D(-0.45, 0, 0.05);    //Face 1
        originFaceVector[1][2] = new Vector3D(-0.45, -0.05, 0);   //Face 2
        originFaceVector[1][3] = new Vector3D(-0.45, 0, -0.05);
        originFaceVector[1][4] = new Vector3D(-0.45, 0.05, 0);
        originFaceVector[1][5] = new Vector3D(0.5, 0, 0);
        originFaceVector[1][6] = new Vector3D(0.45, 0, 0.05);
        originFaceVector[1][7] = new Vector3D(0.45, -0.05, 0);
        originFaceVector[1][8] = new Vector3D(0.45, 0, -0.05);
        originFaceVector[1][9] = new Vector3D(0.45, 0.05, 0);
        originFaceVector[1][10] = new Vector3D(0, 0, 0.05);     //Face 10
        originFaceVector[1][11] = new Vector3D(0, -0.05, 0);    //Face 11
        originFaceVector[1][12] = new Vector3D(0, 0, -0.05);    //Face 12
        originFaceVector[1][13] = new Vector3D(0, 0.05, 0);     //Face 13      
        
        //Telescopic module
        originFaceVector[2][0] = new Vector3D(-0.4, 0, 0);
        originFaceVector[2][1] = new Vector3D(-0.35, 0, 0.05);
        originFaceVector[2][2] = new Vector3D(-0.35, -0.05, 0);
        originFaceVector[2][3] = new Vector3D(-0.35, 0, -0.05);
        originFaceVector[2][4] = new Vector3D(-0.35, 0.05, 0);
        originFaceVector[2][5] = new Vector3D(0.4, 0, 0);
        originFaceVector[2][6] = new Vector3D(0.35, 0, 0.05);
        originFaceVector[2][7] = new Vector3D(0.35, -0.05, 0);
        originFaceVector[2][8] = new Vector3D(0.35, 0, -0.05);
        originFaceVector[2][9] = new Vector3D(0.35, 0.05, 0);
        
        //Rotational module
        originFaceVector[3][0] = new Vector3D(-0.1, 0, 0);
        originFaceVector[3][1] = new Vector3D(-0.05, 0, 0.05);
        originFaceVector[3][2] = new Vector3D(-0.05, -0.05, 0);
        originFaceVector[3][3] = new Vector3D(-0.05, 0, -0.05);
        originFaceVector[3][4] = new Vector3D(-0.05, 0.05, 0);
        originFaceVector[3][5] = new Vector3D(0.1, 0, 0);
        originFaceVector[3][6] = new Vector3D(0.05, 0, 0.05);
        originFaceVector[3][7] = new Vector3D(0.05, -0.05, 0);
        originFaceVector[3][8] = new Vector3D(0.05, 0, -0.05);
        originFaceVector[3][9] = new Vector3D(0.05, 0.05, 0);
        
        //Hinge module
        originFaceVector[4][0] = new Vector3D(-0.05, 0, 0);
        originFaceVector[4][1] = new Vector3D(0.15, 0, 0);

         //End effector module
        originFaceVector[5][0] = new Vector3D(-0.05, 0, 0);
        
        /**********************************************************************/
        /************************** normalFaceVector **************************/
        /**********************************************************************/
        // This are the coordinates of an unitary vector normal to the face 
        // for each face and module type
        
        //Base module:
        normalFaceVector[0][0] = new Vector3D(0, 0, 1);     //Face 0
        normalFaceVector[0][1] = new Vector3D(0, 1, 0);     //Face 1
        normalFaceVector[0][2] = new Vector3D(0, 0, -1);    //Face 2
        normalFaceVector[0][3] = new Vector3D(0, 0, 1);     
        normalFaceVector[0][4] = new Vector3D(0, -1, 0);    
        normalFaceVector[0][5] = new Vector3D(0, 0, -1);
        normalFaceVector[0][6] = new Vector3D(0, 0, 1);
        normalFaceVector[0][7] = new Vector3D(0, -1, 0);
        normalFaceVector[0][8] = new Vector3D(0, 0, -1);   //Face 8   
        normalFaceVector[0][9] = new Vector3D(0, 0, 1);    //Face 9
        normalFaceVector[0][10] = new Vector3D(0, 1, 0);   //Face 10
        normalFaceVector[0][11] = new Vector3D(0, 0, -1);  //Face 11

        //Slider module
        normalFaceVector[1][0] = new Vector3D(-1, 0, 0);
        normalFaceVector[1][1] = new Vector3D(0, 0, 1);
        normalFaceVector[1][2] = new Vector3D(0, -1, 0);
        normalFaceVector[1][3] = new Vector3D(0, 0, -1);
        normalFaceVector[1][4] = new Vector3D(0, 1, 0);
        normalFaceVector[1][5] = new Vector3D(1, 0, 0);
        normalFaceVector[1][6] = new Vector3D(0, 0, 1);
        normalFaceVector[1][7] = new Vector3D(0, -1, 0);
        normalFaceVector[1][8] = new Vector3D(0, 0, -1);
        normalFaceVector[1][9] = new Vector3D(0, 1, 0);
        normalFaceVector[1][10] = new Vector3D(0, 0, 1);
        normalFaceVector[1][11] = new Vector3D(0, -1, 0);
        normalFaceVector[1][12] = new Vector3D(0, 0, -1);
        normalFaceVector[1][13] = new Vector3D(0, 1, 0);
        
        //Telescopic module
        normalFaceVector[2][0] = new Vector3D(-1, 0, 0);
        normalFaceVector[2][1] = new Vector3D(0, 0, 1);
        normalFaceVector[2][2] = new Vector3D(0, -1, 0);
        normalFaceVector[2][3] = new Vector3D(0, 0, -1);
        normalFaceVector[2][4] = new Vector3D(0, 1, 0);
        normalFaceVector[2][5] = new Vector3D(1, 0, 0);
        normalFaceVector[2][6] = new Vector3D(0, 0, 1);
        normalFaceVector[2][7] = new Vector3D(0, -1, 0);
        normalFaceVector[2][8] = new Vector3D(0, 0, -1);
        normalFaceVector[2][9] = new Vector3D(0, 1, 0);

        //Rotational module
        normalFaceVector[3][0] = new Vector3D(-1, 0, 0);
        normalFaceVector[3][1] = new Vector3D(0, 0, 1);
        normalFaceVector[3][2] = new Vector3D(0, -1, 0);
        normalFaceVector[3][3] = new Vector3D(0, 0, -1);
        normalFaceVector[3][4] = new Vector3D(0, 1, 0);
        normalFaceVector[3][5] = new Vector3D(1, 0, 0);
        normalFaceVector[3][6] = new Vector3D(0, 0, 1);
        normalFaceVector[3][7] = new Vector3D(0, -1, 0);
        normalFaceVector[3][8] = new Vector3D(0, 0, -1);
        normalFaceVector[3][9] = new Vector3D(0, 1, 0);

        //Hinge module
        normalFaceVector[4][0] = new Vector3D(-1, 0, 0);
        normalFaceVector[4][1] = new Vector3D(1, 0, 0);

        //End effector module
        normalFaceVector[5][0] = new Vector3D(-1, 0, 0);
        
    }

}
