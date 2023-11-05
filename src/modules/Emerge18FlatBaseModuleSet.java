package modules;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Emerge18FlatBaseModuleSet extends Emerge18AndCuboidBaseModuleSet {
	
	public Emerge18FlatBaseModuleSet() {
		this(8, 8, 2);
		// TODO Auto-generated constructor stub
	}
	
	public Emerge18FlatBaseModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {

		super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);

		// The name of the different modules (they have to match with the name of
		// the file to load in the simulator)
		moduleName[0] = "flatBase";

		// define the different properties
		// number of faces for each type of module
		modulesFacesNumber[0] = 8; // Number of faces for flatBase module

		// Number of faces in the base body for each type of module; the other faces are
		// in the actuator body
		modulesBaseFacesNumber[0] = 8; // Number of faces in the flatBase module (all)

		// number of possible orientations when a module is joined to other module
		moduleOrientations[0] = 0; // Number of orientations of the flatBase module

		// mass of each module in Kg
		modulesMass[0] = 0.65; // Mass of the flatBase module

		// max amplitude
		modulesMaxAmplitude[0] = 0; // Max amplitude of the flatBase module

		// max angular frequency
		modulesMaxAngularFrequency[0] = 0; // Max angular frequency of the flatBase module
		
		/**********************************************************************/
		/************************** originFaceVector **************************/
		/**********************************************************************/
		// This are the coordinates of the vector from the origon of the module
		// to the face of the module for each face and module type
		// Base module:

		originFaceVector[0][0] = new Vector3D(0.0747, 0.043, 0); // Face 1
		originFaceVector[0][1] = new Vector3D(0.043, 0.0747, 0); // Face 2
		
		originFaceVector[0][2] = new Vector3D(-0.043, 0.0747, 0); // Face 3		
		originFaceVector[0][3] = new Vector3D(-0.0747, 0.043, 0); // Face 4		
	
		originFaceVector[0][4] = new Vector3D(-0.0747, -0.043, 0); // Face 5		
		originFaceVector[0][5] = new Vector3D(-0.043, -0.0747, 0); // Face 6		
	
		originFaceVector[0][6] = new Vector3D(0.043, -0.0747, 0); // Face 7		
		originFaceVector[0][7] = new Vector3D(0.0747, -0.043, 0); // Face 8
		
		
		/**********************************************************************/
		/************************** normalFaceVector **************************/
		/**********************************************************************/
		// This are the coordinates of an unitary vector normal to the face
		// for each face and module type
		// Base module:
		normalFaceVector[0][0] = new Vector3D(1, 0, 0); // Face 1
		normalFaceVector[0][1] = new Vector3D(0, 1, 0); // Face 2		
		
		normalFaceVector[0][2] = new Vector3D(0, 1, 0); // Face 5		
		normalFaceVector[0][3] = new Vector3D(-1, 0, 0); // Face 6		
		
		normalFaceVector[0][4] = new Vector3D(-1, 0, 0); // Face 9		
		normalFaceVector[0][5] = new Vector3D(0, -1, 0); // Face 10		
		
		normalFaceVector[0][6] = new Vector3D(0, -1, 0); // Face 13		
		normalFaceVector[0][7] = new Vector3D(1, 0, 0); // Face 14		

		
		/**********************************************************************/
		/************************** boundingBox **************************/
		/**********************************************************************/
		// This are the size of boundingBox for each module type, it is
		// represented as (width,height,length); (Y, Z, X) in CoppeliaSim. Units: meter

		// Base module:
		boundingBox[0] = new Vector3D(0.1494, 0.055023, 0.1494);

	}

}
