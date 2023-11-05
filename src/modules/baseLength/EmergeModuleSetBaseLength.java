package modules.baseLength;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import modules.EmergeModuleSet;

public class EmergeModuleSetBaseLength extends EmergeModuleSet {

	private double lengthMultiplier = 2;

	public EmergeModuleSetBaseLength(double lengthMultiplier) {
		super();
		this.lengthMultiplier = lengthMultiplier;
		// The name of this set of modules (it has to match with the folder
		// where the modules are stored in the edhmor folder)
		if(lengthMultiplier == 1.25) {
			moduleSetName = "emergeModules/1-25TimesLength";
		}else if(lengthMultiplier == 1.5) {
			moduleSetName = "emergeModules/1-5TimesLength";
		}else if(lengthMultiplier == 1.75) {
			moduleSetName = "emergeModules/1-75TimesLength";
		}else {
			moduleSetName = "emergeModules/"+this.lengthMultiplier+"TimesLength";
		}

		// mass of each module in Kg
		modulesMass[0] = 0.06; // Mass of the base module
		modulesMass[1] = 0.06; // Mass of the Emerge module

		/**********************************************************************/
		/************************** originFaceVector **************************/
		/**********************************************************************/
		// This are the coordinates of the vector from the origin of the module
		// to the face of the module for each face and module type
		// Base module:

		originFaceVector[0][0] = new Vector3D(0.0385, 0, 0); // Face 1
		originFaceVector[0][1] = new Vector3D(0.008, -0.0305, 0); // Face 2
		originFaceVector[0][2] = new Vector3D(0.008, 0.0305, 0); // Face 3

		// Emerge module
		originFaceVector[1][0] = new Vector3D(-0.0385 * lengthMultiplier, 0, 0); // Face 0
		originFaceVector[1][1] = new Vector3D(0.0385, 0, 0); // Face 1
		originFaceVector[1][2] = new Vector3D(0.008, -0.0305, 0); // Face 2
		originFaceVector[1][3] = new Vector3D(0.008, 0.0305, 0); // Face 3

		/**********************************************************************/
		/************************** boundingBox **************************/
		/**********************************************************************/
		// This are the size of boundingBox for each module type, it is
		// represented as (width,height,length); (Y, Z, X) in CoppeliaSim. Units: meter

		double lenght = 0.0385;
		// Base module:
		boundingBox[0] = new Vector3D(0.061, 0.055, lenght + lenght * this.lengthMultiplier);

		// Emerge module
		boundingBox[1] = new Vector3D(0.061, 0.055, lenght + lenght * this.lengthMultiplier);

	}

}
