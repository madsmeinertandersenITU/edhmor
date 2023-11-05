package modules;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class BAtronModuleSet extends AtronModuleSet {

	public BAtronModuleSet()  {
		//two types of module with four connection faces and 2 orientations
	       this(1, 8, 1); 
	}
	
	
	
	public BAtronModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {
		
		
		super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);
		
		//The name of this set of modules (it has to match with the folder
	       //where the modules are stored in the edhmor folder)
	       moduleSetName = "bAtronModules";
		
	     //The name of the different modules (they have to match with the name of
	       //the file to load in the simulator)
	       moduleName[0] = "bAtron";
	       
	
	       
	       /**********************************************************************/
	       /************************** originFaceVector **************************/
	       /**********************************************************************/
	       // This are the coordinates of the vector from the origin of the module 
	       // to the face of the module for each face and module type
	       
	       
	       //BAtron module
	       originFaceVector[0][0] = new Vector3D( 0.0579, 0, 0.0267);   //Face 0
	       originFaceVector[0][1] = new Vector3D( 0, 0.0579, 0.0267);       //Face 1
	       originFaceVector[0][2] = new Vector3D( -0.0579, 0, 0.0267);  //Face 2
	       originFaceVector[0][3] = new Vector3D( 0, -0.0579, 0.0267);  //Face 3
	       originFaceVector[0][4] = new Vector3D( 0.0579, 0, -0.0267);  //Face 4
	       originFaceVector[0][5] = new Vector3D( 0, 0.0579, -0.0267);  //Face 5
	       originFaceVector[0][6] = new Vector3D( -0.0579, 0, -0.0267);  //Face 6
	       originFaceVector[0][7] = new Vector3D( 0, -0.0579, -0.0267);  //Face 7
	       
	       /**********************************************************************/
	       /************************** normalFaceVector **************************/
	       /**********************************************************************/
	       // This are the coordinates of an unit vector normal to the face 
	       // for each face and module type
	       
	       //BAtron module
	       normalFaceVector[0][0] = new Vector3D(1, 0, 1);     //Face 0
	       normalFaceVector[0][1] = new Vector3D(0, 1, 1);     //Face 1
	       normalFaceVector[0][2] = new Vector3D(-1, 0, 1);    //Face 2
	       normalFaceVector[0][3] = new Vector3D(0, -1, 1);     //Face 3
	       normalFaceVector[0][4] = new Vector3D(1, 0, -1);     //Face 4
	       normalFaceVector[0][5] = new Vector3D(0, 1, -1);     //Face 5
	       normalFaceVector[0][6] = new Vector3D(-1, 0, -1);     //Face 6
	       normalFaceVector[0][7] = new Vector3D(0, -1, -1);     //Face 7
	       
	       /**********************************************************************/
	       /************************** boundingBox **************************/
	       /**********************************************************************/
	       // This are the size of boundingBox for each module type, it is 
	       //represented as (width,height,length); (Y, Z, X) in CoppeliaSim. Units: meter
	       //BAtron module: 
	       boundingBox[0] = new Vector3D(0.0792,0.0792,0.09);
	       
	       /**********************************************************************/
	       /*************************** symmetricFace  ***************************/
	       /**********************************************************************/
	       
	       //TODO:
		
		  symmetricFace[0][0] = 0;
		 
		 
		
		
	}

}
