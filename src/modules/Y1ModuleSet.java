package modules;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Y1ModuleSet extends ModuleSet{

	public Y1ModuleSet() {
		// 1 types of module with 2 connection faces and 2 orientations
		this(1, 2, 2);
	}

	public Y1ModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {
		super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);
		
		//The name of this set of modules (it has to match with the folder
	       //where the modules are stored in the edhmor folder)
	       moduleSetName = "y1Modules";

		// The name of the different modules (they have to match with the name of
		// the file to load in the simulator)
		moduleName[0] = "y1Module";

		// define the different properties
		// number of faces for each type of module

		modulesFacesNumber[0] = 2;// Number of faces for polybot g2 module

		// Number of faces in the base body for each type of module; the other faces are
		// in the actuator body
		modulesBaseFacesNumber[0] = 1; // Number of faces in the base for polybot g2 module (all)

		// number of possible orientations when a module is joined to other module
		moduleOrientations[0] = 2; // Number of orientations of polybot module 2 in Evolution


		rotationAboutTheNormalForEachOrientation[0][0] = 0; // 0 degrees
		rotationAboutTheNormalForEachOrientation[0][1] = Math.PI * 0.5;// 90 degrees
		rotationAboutTheNormalForEachOrientation[0][1] = Math.PI;// 180 degrees
		rotationAboutTheNormalForEachOrientation[0][1] = -Math.PI * 0.5;// 180 degrees

		// mass of each module in Kg
		modulesMass[0] = 0.06; // Mass of the polybot g2 module

		// Control parameters

		// max amplitude
		modulesMaxAmplitude[0] = 0.5 * Math.PI; // Max amplitude of the polybot g2 module

		// max angular frequency
		modulesMaxAngularFrequency[0] = 2.0; // Max angular frequency of the polybot g2 module

		/**********************************************************************/
		/************************** originFaceVector **************************/
		/**********************************************************************/
		// This are the coordinates of the vector from the origin of the module
		// to the face of the module for each face and module type

		// polybot g2 module
		originFaceVector[0][0] = new Vector3D(-0.0361, 0, 0); // Face 0
		originFaceVector[0][1] = new Vector3D(0.0361, 0, 0); // Face 1
		

		/**********************************************************************/
		/************************** normalFaceVector **************************/
		/**********************************************************************/
		// This are the coordinates of an unit vector normal to the face
		// for each face and module type

		// polybot g2 module
		normalFaceVector[0][0] = new Vector3D(-1, 0, 0); // Face 0
		normalFaceVector[0][1] = new Vector3D(1, 0, 0); // Face 1

		/**********************************************************************/
		/************************** boundingBox **************************/
		/**********************************************************************/
		// This are the size of boundingBox for each module type, it is
		// represented as (width,height,length); (Y, Z, X) in CoppeliaSim. Units: meter

		// polybot g2 module
		boundingBox[0] = new Vector3D(0.0722, 0.052, 0.052);

		/**********************************************************************/
		/*************************** symmetricFace ***************************/
		/**********************************************************************/

		// TODO:
		symmetricFace[0][0] = 0;



	}
}
