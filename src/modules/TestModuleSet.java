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
 * TestModules.java
 * Created on 11/11/2015
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class TestModuleSet extends ModuleSet{
    
    public TestModuleSet(){
        //Only one type of module with two connection faces and two orientations
        super(1, 2, 2); 

        //The name of this set of modules (it has to match with the folder
        //where the modules are stored in the edhmor folder)
        moduleSetName = "testModules";
        
        //The name of the different modules (they have to match with the name of
        //the file to load in the simulator)
        moduleName[0] = "testModule";
        
        //TODO: Set correctly the properties!!!!
        //define the different properties
        //number of faces for each type of module
        modulesFacesNumber[0] = 2; //Number of faces for Rodrigo´s module

        //Number of faces in the base body for each type of module; the other faces are in the actuator body
        modulesBaseFacesNumber[0] = 1; //Number of faces in the base for the base module (all)

        //number of posible orientations when a module is joined to other module
        moduleOrientations[0] = 2; //Number of orientations of the module base
        
        connectionFaceForEachOrientation[0][0] = 0;        //connected in face 0
        connectionFaceForEachOrientation[0][1] = 0;        //connected in face 1

        rotationAboutTheNormalForEachOrientation[0][0] = 0;          //0 degrees
        rotationAboutTheNormalForEachOrientation[0][1] = Math.PI*0.5;//90 degrees
        //mass of each module in Kg
        modulesMass[0] = 0.4;  //Mass of the base module
        
        //Control parameters
        modulesMaxAmplitude[0] = Math.PI * 0.5; //Max amplitude
        modulesMaxAngularFrequency[0] = 2.0;    //Max angular frequency

        /**********************************************************************/
        /************************** originFaceVector **************************/
        /**********************************************************************/
        // This are the coordinates of the vector from the origon of the module 
        // to the face of the module for each face and module type
        //Base module:
        originFaceVector[0][0] = new Vector3D(-0.125, 0, 0);   //Face 0
        originFaceVector[0][1] = new Vector3D(0.125, 0, 0);    //Face 1


        /**********************************************************************/
        /************************** normalFaceVector **************************/
        /**********************************************************************/
        // This are the coordinates of an unitary vector normal to the face 
        // for each face and module type
        //Base module:
        normalFaceVector[0][0] = new Vector3D(-1, 0, 0);    //Face 0
        normalFaceVector[0][1] = new Vector3D(1, 0, 0);     //Face 1


    }

}
