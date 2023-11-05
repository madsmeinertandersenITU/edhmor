package modules;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class AtronModuleSet extends ModuleSet {

	public AtronModuleSet()  {
		//one type of module with eight connection faces and 1 orientations
	       this(1, 8, 1); 
	}
	
	
	
	public AtronModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {
		
		
		super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);
		
		//The name of this set of modules (it has to match with the folder
	       //where the modules are stored in the edhmor folder)
	       moduleSetName = "atronModules";
		
	     //The name of the different modules (they have to match with the name of
	       //the file to load in the simulator)
	       moduleName[0] = "Atron";

	       //define the different properties
	       //number of faces for each type of module
	       
	       modulesFacesNumber[0] = 8; //Number of faces
	       
	     //Number of faces in the base body for each type of module; the other faces are in the actuator body
	       modulesBaseFacesNumber[0] = 4; //Number of faces for the module (all)

	       
	     //number of possible orientations when a module is joined to other module
	       moduleOrientations[0] = 1; //Number of orientations of the module in Evolution
	       

	       
	     //mass of each module in Kg
	       modulesMass[0] = 0.85;  //Mass

	       
	     //max amplitude
	       modulesMaxAmplitude[0] = Math.PI ; //Max amplitude
	   
	       
	       //max angular frequency
	       modulesMaxAngularFrequency[0] = 2.0;  //Max angular frequency
	
	       
	       /**********************************************************************/
	       /************************** originFaceVector **************************/
	       /**********************************************************************/
	       // This are the coordinates of the vector from the origin of the module 
	       // to the face of the module for each face and module type
	       
	       //Atron module
	       originFaceVector[0][0] = new Vector3D( 0.0396, 0, 0.0382);   //Face 0
	       originFaceVector[0][1] = new Vector3D( 0, 0.0396, 0.0382);       //Face 1
	       originFaceVector[0][2] = new Vector3D( -0.0396, 0, 0.0382);  //Face 2
	       originFaceVector[0][3] = new Vector3D( 0, -0.0396, 0.0382);  //Face 3
	       originFaceVector[0][4] = new Vector3D( 0.0396, 0, -0.0382);  //Face 4
	       originFaceVector[0][5] = new Vector3D( 0, 0.0396, -0.0382);  //Face 5
	       originFaceVector[0][6] = new Vector3D( -0.0396, 0, -0.0382);  //Face 6
	       originFaceVector[0][7] = new Vector3D( 0, -0.0396, -0.0382);  //Face 7
	       
	       /**********************************************************************/
	       /************************** normalFaceVector **************************/
	       /**********************************************************************/
	       // This are the coordinates of an unit vector normal to the face 
	       // for each face and module type
	       
	       //Atron module
	       normalFaceVector[0][0] = new Vector3D(1, 0, 1);     //Face 0
	       normalFaceVector[0][1] = new Vector3D(0, 1, 1);     //Face 1
	       normalFaceVector[0][2] = new Vector3D(-1, 0, 1);    //Face 2
	       normalFaceVector[0][3] = new Vector3D(0, -1, 1);     //Face 3
	       normalFaceVector[0][4] = new Vector3D(1, 0, -1);     //Face 4
	       normalFaceVector[0][5] = new Vector3D(0, 1, -1);     //Face 5
	       normalFaceVector[0][6] = new Vector3D(-1, 0, -1);     //Face 6
	       normalFaceVector[0][7] = new Vector3D(0, -1, -1);     //Face 7
               
               /**********************************************************************/
	       /************************** coplanarFaceVector **************************/
	       /**********************************************************************/
	       // This are the coordinates of an unit vector coplanar to the connector 
               // face for each face and module type
	       
	       //Atron module
	       coplanarFaceVector[0][0] = new Vector3D(-1, 0, 1);     //Face 0
	       coplanarFaceVector[0][1] = new Vector3D(0, -1, 1);     //Face 1
	       coplanarFaceVector[0][2] = new Vector3D(1, 0, 1);    //Face 2
	       coplanarFaceVector[0][3] = new Vector3D(0, 1, 1);     //Face 3
	       coplanarFaceVector[0][4] = new Vector3D(-1, 0, -1);     //Face 4
	       coplanarFaceVector[0][5] = new Vector3D(0, -1, -1);     //Face 5
	       coplanarFaceVector[0][6] = new Vector3D(1, 0, -1);     //Face 6
	       coplanarFaceVector[0][7] = new Vector3D(0, 1, -1);     //Face 7
	       
	       /**********************************************************************/
	       /************************** boundingBox **************************/
	       /**********************************************************************/
	       // This are the size of boundingBox for each module type, it is 
	       //represented as (width,height,length); (Y, Z, X) in CoppeliaSim. Units: meter
	       //Atron module: 
	       //boundingBox[0] = new Vector3D(0.0792,0.0792,0.0895);
	       //boundingBox[0] = new Vector3D(0.01,0.01,0.01);
               boundingMethod = BoundingMethod.SPHERE;
               boundingSphereDiameter = 0.054; //Aprox
	       
	       /**********************************************************************/
	       /*************************** symmetricFace  ***************************/
	       /**********************************************************************/
	       
	       //TODO:
		
		  symmetricFace[0][0] = 0;
		 
		 
		
		
	}

}
