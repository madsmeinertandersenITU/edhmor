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
package modules;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import modules.numberOfFaces.RodrigoModuleSet;

/**
 * EmergeAndCuboidBaseModuleSet.java Created on 21/10/2016
 * 
 * @author Andres Faina <anfv at itu.dk>
 * @author Rodrigo Moreno <rmorenoga at unal.edu.co>
 */
public class EmergeAndCuboidBaseModuleSet extends EmergeModuleSet {

	public EmergeAndCuboidBaseModuleSet() {
    //8 types of module with five connection faces and 2 orientations
    this(8, 5, 2); 
	}
	
	public EmergeAndCuboidBaseModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {
		// 8 types of module with 5 connection faces and 2 orientations
		super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);
		// The parent class sets all the properties of the normal module. Here, we only
		// update the
		// base module to use a cuboid base and add the extra base length modules.

		// The name of the different modules (they have to match with the name of
		// the file to load in the simulator)
		moduleName[0] = "cuboidBase";

		moduleName[2] = "1-25TimesLength/emergeModule";
		moduleName[3] = "1-5TimesLength/emergeModule";
		moduleName[4] = "1-75TimesLength/emergeModule";
		moduleName[5] = "2TimesLength/emergeModule";
		moduleName[6] = "4TimesLength/emergeModule";
		moduleName[7] = "8TimesLength/emergeModule";

		// define the different properties
		// number of faces for each type of module
		modulesFacesNumber[0] = 5; // Number of faces for cuboid module

		modulesFacesNumber[2] = 4;// Number of faces for 1-25TimesLength Emerge module
		modulesFacesNumber[3] = 4;// Number of faces for 1-5TimesLength Emerge module
		modulesFacesNumber[4] = 4;// Number of faces for 1-75TimesLength Emerge module
		modulesFacesNumber[5] = 4;// Number of faces for 2TimesLength Emerge module
		modulesFacesNumber[6] = 4;// Number of faces for 4TimesLength Emerge module
		modulesFacesNumber[7] = 4;// Number of faces for 8TimesLength Emerge module

		// Number of faces in the base body for each type of module; the other faces are
		// in the actuator body
		modulesBaseFacesNumber[0] = 5; // Number of faces in the cuboid module (all)

		modulesBaseFacesNumber[2] = 1; // Number of faces in the base for 1-25TimesLength Emerge module (all)
		modulesBaseFacesNumber[3] = 1; // Number of faces in the base for 1-5TimesLength Emerge module (all)
		modulesBaseFacesNumber[4] = 1; // Number of faces in the base for 1-75TimesLength Emerge module (all)
		modulesBaseFacesNumber[5] = 1; // Number of faces in the base for 2TimesLength Emerge module (all)
		modulesBaseFacesNumber[6] = 1; // Number of faces in the base for 4TimesLength Emerge module (all)
		modulesBaseFacesNumber[7] = 1; // Number of faces in the base for 8TimesLength Emerge module (all)

		// number of possible orientations when a module is joined to other module
		moduleOrientations[0] = 0; // Number of orientations of the cuboid module

		moduleOrientations[2] = 2; // Number of orientations of 1-25TimesLength Emerge module
		moduleOrientations[3] = 2; // Number of orientations of 1-5TimesLength Emerge module
		moduleOrientations[4] = 2; // Number of orientations of 1-75TimesLength Emerge module
		moduleOrientations[5] = 2; // Number of orientations of 2TimesLength Emerge module
		moduleOrientations[6] = 2; // Number of orientations of 4TimesLength Emerge module
		moduleOrientations[7] = 2; // Number of orientations of 8TimesLength Emerge module

		connectionFaceForEachOrientation[2][0] = 0; // connected in face 0
		connectionFaceForEachOrientation[2][1] = 0; // connected in face 0
		connectionFaceForEachOrientation[3][0] = 0; // connected in face 0
		connectionFaceForEachOrientation[3][1] = 0; // connected in face 0
		connectionFaceForEachOrientation[4][0] = 0; // connected in face 0
		connectionFaceForEachOrientation[4][1] = 0; // connected in face 0
		connectionFaceForEachOrientation[5][0] = 0; // connected in face 0
		connectionFaceForEachOrientation[5][1] = 0; // connected in face 0
		connectionFaceForEachOrientation[6][0] = 0; // connected in face 0
		connectionFaceForEachOrientation[6][1] = 0; // connected in face 0
		connectionFaceForEachOrientation[7][0] = 0; // connected in face 0
		connectionFaceForEachOrientation[7][1] = 0; // connected in face 0

		rotationAboutTheNormalForEachOrientation[2][0] = 0; // 0 degrees
		rotationAboutTheNormalForEachOrientation[2][1] = Math.PI * 0.5;// 90 degrees
		rotationAboutTheNormalForEachOrientation[3][0] = 0; // 0 degrees
		rotationAboutTheNormalForEachOrientation[3][1] = Math.PI * 0.5;// 90 degrees
		rotationAboutTheNormalForEachOrientation[4][0] = 0; // 0 degrees
		rotationAboutTheNormalForEachOrientation[4][1] = Math.PI * 0.5;// 90 degrees
		rotationAboutTheNormalForEachOrientation[5][0] = 0; // 0 degrees
		rotationAboutTheNormalForEachOrientation[5][1] = Math.PI * 0.5;// 90 degrees
		rotationAboutTheNormalForEachOrientation[6][0] = 0; // 0 degrees
		rotationAboutTheNormalForEachOrientation[6][1] = Math.PI * 0.5;// 90 degrees
		rotationAboutTheNormalForEachOrientation[7][0] = 0; // 0 degrees
		rotationAboutTheNormalForEachOrientation[7][1] = Math.PI * 0.5;// 90 degrees

		// mass of each module in Kg
		modulesMass[0] = 0.1; // Mass of the cuboid module

		modulesMass[2] = 0.06; // Mass of the 1-25TimesLength Emerge module
		modulesMass[3] = 0.06; // Mass of the 1-5TimesLength Emerge module
		modulesMass[4] = 0.06; // Mass of the 1-75TimesLength Emerge module
		modulesMass[5] = 0.06; // Mass of the 2TimesLength Emerge module
		modulesMass[6] = 0.06; // Mass of the 4TimesLength Emerge module
		modulesMass[7] = 0.06; // Mass of the 8TimesLength Emerge module

		// max amplitude
		modulesMaxAmplitude[0] = 0; // Max amplitude of the cuboid module

		modulesMaxAmplitude[2] = 0.5 * Math.PI; // Max amplitude of the 1-25TimesLength Emerge module
		modulesMaxAmplitude[3] = 0.5 * Math.PI; // Max amplitude of the 1-5TimesLength Emerge module
		modulesMaxAmplitude[4] = 0.5 * Math.PI; // Max amplitude of the 1-75TimesLength Emerge module
		modulesMaxAmplitude[5] = 0.5 * Math.PI; // Max amplitude of the 2TimesLength Emerge module
		modulesMaxAmplitude[6] = 0.5 * Math.PI; // Max amplitude of the 4TimesLength Emerge module
		modulesMaxAmplitude[7] = 0.5 * Math.PI; // Max amplitude of the 8TimesLength Emerge module

		// max angular frequency
		modulesMaxAngularFrequency[0] = 0; // Max angular frequency of the cuboid module

		modulesMaxAngularFrequency[2] = 2.0; // Max angular frequency of the 1-25TimesLength Emerge module
		modulesMaxAngularFrequency[3] = 2.0; // Max angular frequency of the 1-5TimesLength Emerge module
		modulesMaxAngularFrequency[4] = 2.0; // Max angular frequency of the 1-75TimesLength Emerge module
		modulesMaxAngularFrequency[5] = 2.0; // Max angular frequency of the 2TimesLength Emerge module
		modulesMaxAngularFrequency[6] = 2.0; // Max angular frequency of the 4TimesLength Emerge module
		modulesMaxAngularFrequency[7] = 2.0; // Max angular frequency of the 8TimesLength Emerge module

		/**********************************************************************/
		/************************** originFaceVector **************************/
		/**********************************************************************/
		// This are the coordinates of the vector from the origon of the module
		// to the face of the module for each face and module type
		// Base module:

		originFaceVector[0][0] = new Vector3D(-0.0275, 0, 0); // Face 1
		originFaceVector[0][1] = new Vector3D(0.0275, 0, 0); // Face 2
		originFaceVector[0][2] = new Vector3D(0, -0.0275, 0); // Face 3
		originFaceVector[0][3] = new Vector3D(0, 0.0275, 0); // Face 4
		originFaceVector[0][4] = new Vector3D(0, 0, 0.0275); // Face 5

		// 1-25TimesLength Emerge module
		originFaceVector[2][0] = new Vector3D(-0.0385 * 1.25, 0, 0); // Face 0
		originFaceVector[2][1] = new Vector3D(0.0385, 0, 0); // Face 1
		originFaceVector[2][2] = new Vector3D(0.008, -0.0305, 0); // Face 2
		originFaceVector[2][3] = new Vector3D(0.008, 0.0305, 0); // Face 3

		// 1-5TimesLength Emerge module
		originFaceVector[3][0] = new Vector3D(-0.0385 * 1.5, 0, 0); // Face 0
		originFaceVector[3][1] = new Vector3D(0.0385, 0, 0); // Face 1
		originFaceVector[3][2] = new Vector3D(0.008, -0.0305, 0); // Face 2
		originFaceVector[3][3] = new Vector3D(0.008, 0.0305, 0); // Face 3

		// 1-75TimesLength Emerge module
		originFaceVector[4][0] = new Vector3D(-0.0385 * 1.75, 0, 0); // Face 0
		originFaceVector[4][1] = new Vector3D(0.0385, 0, 0); // Face 1
		originFaceVector[4][2] = new Vector3D(0.008, -0.0305, 0); // Face 2
		originFaceVector[4][3] = new Vector3D(0.008, 0.0305, 0); // Face 3

		// 2TimesLength Emerge module
		originFaceVector[5][0] = new Vector3D(-0.0385 * 2, 0, 0); // Face 0
		originFaceVector[5][1] = new Vector3D(0.0385, 0, 0); // Face 1
		originFaceVector[5][2] = new Vector3D(0.008, -0.0305, 0); // Face 2
		originFaceVector[5][3] = new Vector3D(0.008, 0.0305, 0); // Face 3

		// 4TimesLength Emerge module
		originFaceVector[6][0] = new Vector3D(-0.0385 * 4, 0, 0); // Face 0
		originFaceVector[6][1] = new Vector3D(0.0385, 0, 0); // Face 1
		originFaceVector[6][2] = new Vector3D(0.008, -0.0305, 0); // Face 2
		originFaceVector[6][3] = new Vector3D(0.008, 0.0305, 0); // Face 3

		// 8TimesLength Emerge module
		originFaceVector[7][0] = new Vector3D(-0.0385 * 8, 0, 0); // Face 0
		originFaceVector[7][1] = new Vector3D(0.0385, 0, 0); // Face 1
		originFaceVector[7][2] = new Vector3D(0.008, -0.0305, 0); // Face 2
		originFaceVector[7][3] = new Vector3D(0.008, 0.0305, 0); // Face 3

		/**********************************************************************/
		/************************** normalFaceVector **************************/
		/**********************************************************************/
		// This are the coordinates of an unitary vector normal to the face
		// for each face and module type
		// Base module:
		normalFaceVector[0][0] = new Vector3D(-1, 0, 0); // Face 1
		normalFaceVector[0][1] = new Vector3D(1, 0, 0); // Face 2
		normalFaceVector[0][2] = new Vector3D(0, -1, 0); // Face 3
		normalFaceVector[0][3] = new Vector3D(0, 1, 0); // Face 4
		normalFaceVector[0][4] = new Vector3D(0, 0, 1); // Face 5

		// 1-25TimesLength Emerge module
		normalFaceVector[2][0] = new Vector3D(-1, 0, 0); // Face 0
		normalFaceVector[2][1] = new Vector3D(1, 0, 0); // Face 1
		normalFaceVector[2][2] = new Vector3D(0, -1, 0); // Face 2
		normalFaceVector[2][3] = new Vector3D(0, 1, 0); // Face 3

		// 1-5TimesLength Emerge module
		normalFaceVector[3][0] = new Vector3D(-1, 0, 0); // Face 0
		normalFaceVector[3][1] = new Vector3D(1, 0, 0); // Face 1
		normalFaceVector[3][2] = new Vector3D(0, -1, 0); // Face 2
		normalFaceVector[3][3] = new Vector3D(0, 1, 0); // Face 3

		// 1-75TimesLength Emerge module
		normalFaceVector[4][0] = new Vector3D(-1, 0, 0); // Face 0
		normalFaceVector[4][1] = new Vector3D(1, 0, 0); // Face 1
		normalFaceVector[4][2] = new Vector3D(0, -1, 0); // Face 2
		normalFaceVector[4][3] = new Vector3D(0, 1, 0); // Face 3

		// 2TimesLength Emerge module
		normalFaceVector[5][0] = new Vector3D(-1, 0, 0); // Face 0
		normalFaceVector[5][1] = new Vector3D(1, 0, 0); // Face 1
		normalFaceVector[5][2] = new Vector3D(0, -1, 0); // Face 2
		normalFaceVector[5][3] = new Vector3D(0, 1, 0); // Face 3

		// 4TimesLength Emerge module
		normalFaceVector[6][0] = new Vector3D(-1, 0, 0); // Face 0
		normalFaceVector[6][1] = new Vector3D(1, 0, 0); // Face 1
		normalFaceVector[6][2] = new Vector3D(0, -1, 0); // Face 2
		normalFaceVector[6][3] = new Vector3D(0, 1, 0); // Face 3

		// 8TimesLength Emerge module
		normalFaceVector[7][0] = new Vector3D(-1, 0, 0); // Face 0
		normalFaceVector[7][1] = new Vector3D(1, 0, 0); // Face 1
		normalFaceVector[7][2] = new Vector3D(0, -1, 0); // Face 2
		normalFaceVector[7][3] = new Vector3D(0, 1, 0); // Face 3

		/**********************************************************************/
		/************************** boundingBox **************************/
		/**********************************************************************/
		// This are the size of boundingBox for each module type, it is
		// represented as (width,height,length); (Y, Z, X) in CoppeliaSim. Units: meter
		double lenght = 0.0385;

		// Base module:
		boundingBox[0] = new Vector3D(0.055, 0.055, 0.055);

		// 1-25TimesLength Emerge module
		boundingBox[2] = new Vector3D(0.061, 0.055, lenght + lenght * 1.25);

		// 1-5TimesLength Emerge module
		boundingBox[3] = new Vector3D(0.061, 0.055, lenght + lenght * 1.5);

		// 1-75TimesLength Emerge module
		boundingBox[4] = new Vector3D(0.061, 0.055, lenght + lenght * 1.75);

		// 2TimesLength Emerge module
		boundingBox[5] = new Vector3D(0.061, 0.055, lenght + lenght * 2);

		// 4TimesLength Emerge module
		boundingBox[6] = new Vector3D(0.061, 0.055, lenght + lenght * 4);

		// 8TimesLength Emerge module
		boundingBox[7] = new Vector3D(0.061, 0.055, lenght + lenght * 8);

		/**********************************************************************/
		/*************************** symmetricFace ***************************/
		/**********************************************************************/

		// TODO:
		symmetricFace[0][0] = 1;
		symmetricFace[0][1] = 0;
		symmetricFace[0][2] = 3;
		symmetricFace[0][3] = 2;
		symmetricFace[0][4] = -1;

	}

}
