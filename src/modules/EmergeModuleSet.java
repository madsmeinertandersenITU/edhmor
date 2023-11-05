package modules;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;


/**
*
* @author fai
* Modified by
* @author Rodrigo
*/
public class EmergeModuleSet extends ModuleSet {

   /*Constructor for the EmergeModuleSet class*/
   public EmergeModuleSet() {
       //two types of module with four connection faces and 2 orientations
       this(2, 4, 2); 
   }
   /**
    * This is the general constructor for derived classes 
    */
   public EmergeModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {
               
       super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);

       //The name of this set of modules (it has to match with the folder
       //where the modules are stored in the edhmor folder)
       moduleSetName = "emergeModules";
       
       //The name of the different modules (they have to match with the name of
       //the file to load in the simulator)
       //moduleName[0] = "initialemergeModule";
       moduleName[0] = "emergeModule";
       moduleName[1] = "emergeModule";
       //define the different properties
       //number of faces for each type of module
       
       modulesFacesNumber[0] = 3; //Number of faces for base module
       modulesFacesNumber[1] = 4;//Number of faces for Emerge module
       
       //Number of faces in the base body for each type of module; the other faces are in the actuator body
       modulesBaseFacesNumber[0] = 0; //Number of faces in the base for the base module (all)
       modulesBaseFacesNumber[1] = 1; //Number of faces in the base for Emerge module (all)
       
       //number of possible orientations when a module is joined to other module
       moduleOrientations[0] = 2; //Number of orientations of the module base 4 in Evolution
       moduleOrientations[1] = 2; //Number of orientations of Emerge module 4 in Evolution
       
       connectionFaceForEachOrientation[1][0] = 0;        //connected in face 0
       connectionFaceForEachOrientation[1][1] = 0;        //connected in face 0
       //connectionFaceForEachOrientation[1][2] = 0;        //connected in face 0
       //connectionFaceForEachOrientation[1][3] = 0;        //connected in face 0
       
       rotationAboutTheNormalForEachOrientation[1][0] = 0;          //0 degrees
       rotationAboutTheNormalForEachOrientation[1][1] = Math.PI*0.5;//90 degrees
       //rotationAboutTheNormalForEachOrientation[1][2] = Math.PI;//180 degrees
       //rotationAboutTheNormalForEachOrientation[1][3] = -Math.PI*0.5;//-90 degrees
       
       //mass of each module in Kg
       modulesMass[0] = 0.06;  //Mass of the base module
       modulesMass[1] = 0.06;  //Mass of the Emerge module
       
       //Control parameters
       
       //max amplitude
       modulesMaxAmplitude[0] = 0.5*Math.PI ; //Max amplitude of the base 
       modulesMaxAmplitude[1] = 0.5*Math.PI ; //Max amplitude of the Emerge module
       
       //max angular frequency
       modulesMaxAngularFrequency[0] = 2.0;  //Max angular frequency of the base
       modulesMaxAngularFrequency[1] = 2.0;   //Max angular frequency of the Emerge module

       /**********************************************************************/
       /************************** originFaceVector **************************/
       /**********************************************************************/
       // This are the coordinates of the vector from the origin of the module 
       // to the face of the module for each face and module type
       //Base module:
       
       originFaceVector[0][0] = new Vector3D(0.0385,0,0);       //Face 1
       originFaceVector[0][1] = new Vector3D(0.008, -0.0305, 0);  //Face 2
       originFaceVector[0][2] = new Vector3D(0.008, 0.0305, 0);  //Face 3
       
       //Emerge module
       originFaceVector[1][0] = new Vector3D(-0.0385, 0, 0);   //Face 0
       originFaceVector[1][1] = new Vector3D( 0.0385, 0, 0);       //Face 1
       originFaceVector[1][2] = new Vector3D(0.008, -0.0305, 0);  //Face 2
       originFaceVector[1][3] = new Vector3D(0.008, 0.0305, 0);  //Face 3
       
       /**********************************************************************/
       /************************** normalFaceVector **************************/
       /**********************************************************************/
       // This are the coordinates of an unit vector normal to the face 
       // for each face and module type
       //Base module:
       normalFaceVector[0][0] = new Vector3D(1,0,0);     //Face 1
       normalFaceVector[0][1] = new Vector3D(0, -1, 0);    //Face 2
       normalFaceVector[0][2] = new Vector3D(0, 1, 0);     //Face 3
       
       //Emerge module
       normalFaceVector[1][0] = new Vector3D(-1, 0, 0);     //Face 0
       normalFaceVector[1][1] = new Vector3D(1,0,0);     //Face 1
       normalFaceVector[1][2] = new Vector3D(0, -1, 0);    //Face 2
       normalFaceVector[1][3] = new Vector3D(0, 1, 0);     //Face 3
       
       /**********************************************************************/
       /************************** boundingBox **************************/
       /**********************************************************************/
       // This are the size of boundingBox for each module type, it is 
       //represented as (width,height,length); (Y, Z, X) in CoppeliaSim. Units: meter
       //Base module: 
       boundingBox[0] = new Vector3D(0.061,0.055,0.077);
       
       //Emerge module
       //boundingBox[1] = new Vector3D(0.055,0.061,0.077);
       boundingBox[1] = new Vector3D(0.061,0.055,0.077);
       
       /**********************************************************************/
       /*************************** symmetricFace  ***************************/
       /**********************************************************************/
       
       //TODO:
       symmetricFace[0][0] = -1;
       symmetricFace[0][1] = 2;
       symmetricFace[0][2] = 1;
 
       
       symmetricFace[1][0] = 1;
       symmetricFace[1][1] = 0;
       symmetricFace[1][2] = 3;
       symmetricFace[1][3] = 2;


   }

}