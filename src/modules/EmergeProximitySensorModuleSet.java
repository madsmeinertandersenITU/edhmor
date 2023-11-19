package modules;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class EmergeProximitySensorModuleSet extends ModuleSet {

    public EmergeProximitySensorModuleSet() {
        this(4, 8, 2);
    }

    public EmergeProximitySensorModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {
        super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);
        // TODO Auto-generated constructor stub

        // The name of this set of modules (it has to match with the folder
        // where the modules are stored in the edhmor folder)
        moduleSetName = "emergeModules/ConeProximitySensor";

        // The name of the different modules (they have to match with the name of
        // the file to load in the simulator)
        // moduleName[0] = "initialemergeModule";
        moduleName[0] = "flatBase";
        moduleName[1] = "emergeModule";
        moduleName[2] = "emergeModule";
        moduleName[3] = "ConeProximitySensorModule";
        // define the different properties
        // number of faces for each type of module

        modulesFacesNumber[0] = 8; // Number of faces for base module
        modulesFacesNumber[1] = 3; // Number of faces for base module
        modulesFacesNumber[2] = 4;// Number of faces for Emerge module
        modulesFacesNumber[3] = 1; // Number of faces for base module

        modulesBaseFacesNumber[0] = 8; // Number of faces in the base for Emerge module (all)
        modulesBaseFacesNumber[1] = 0; // Number of faces in the base for the base module (all)
        modulesBaseFacesNumber[2] = 1; // Number of faces in the base for Emerge
        // module (all)
        modulesBaseFacesNumber[3] = 1; // Number of faces in the base for Emerge
        // module (all)

        // number of possible orientations when a module is joined to other module
        moduleOrientations[0] = 0; // Number of orientations of the module base 4 in Evolution
        moduleOrientations[1] = 2; // Number of orientations of the module base 4 in Evolution
        moduleOrientations[2] = 2; // Number of orientations of Emerge module 4 in
        // Evolution
        moduleOrientations[3] = 1; // Number of orientations of the module base 4 in
        // Evolution

        // connectionFaceForEachOrientation[0][0] = 0; // connected in face 0
        connectionFaceForEachOrientation[1][0] = 0; // connected in face 0
        connectionFaceForEachOrientation[2][1] = 0; // connected in face 0
        connectionFaceForEachOrientation[3][0] = 0; // connected in face 0

        rotationAboutTheNormalForEachOrientation[1][0] = 0; // 0 degrees
        rotationAboutTheNormalForEachOrientation[2][1] = Math.PI * 0.5;// 90 degrees
        rotationAboutTheNormalForEachOrientation[3][0] = 0;// 90 degrees
        // mass of each module in Kg
        modulesMass[0] = 0.65; // Mass of the flatBase module
        modulesMass[1] = 0.06; // Mass of the base module
        modulesMass[2] = 0.06; // Mass of the Emerge module
        modulesMass[3] = 0.03; // Mass of the base module

        // Control parameters

        // max amplitude
        modulesMaxAmplitude[0] = 0.5 * Math.PI; // Max amplitude of the base
        modulesMaxAmplitude[1] = 0.5 * Math.PI; // Max amplitude of the base
        modulesMaxAmplitude[2] = 0.5 * Math.PI; // Max amplitude of the Emerge module
        modulesMaxAmplitude[3] = 0.5 * Math.PI; // Max amplitude of the base

        // max angular frequency
        modulesMaxAngularFrequency[0] = 0; // Max angular frequency of the flatBase module
        modulesMaxAngularFrequency[1] = 2.0; // Max angular frequency of the base
        modulesMaxAngularFrequency[2] = 2.0; // Max angular frequency of the Emerge
        modulesMaxAngularFrequency[3] = 2.0; // Max angular frequency of the base

        /**********************************************************************/
        /************************** originFaceVector **************************/
        /**********************************************************************/
        // This are the coordinates of the vector from the origin of the module
        // to the face of the module for each face and module type

        // Emerge module
        originFaceVector[0][0] = new Vector3D(0.0747, 0.043, 0); // Face 1
        originFaceVector[0][1] = new Vector3D(0.043, 0.0747, 0); // Face 2

        originFaceVector[0][2] = new Vector3D(-0.043, 0.0747, 0); // Face 3
        originFaceVector[0][3] = new Vector3D(-0.0747, 0.043, 0); // Face 4

        originFaceVector[0][4] = new Vector3D(-0.0747, -0.043, 0); // Face 5
        originFaceVector[0][5] = new Vector3D(-0.043, -0.0747, 0); // Face 6

        originFaceVector[0][6] = new Vector3D(0.043, -0.0747, 0); // Face 7
        originFaceVector[0][7] = new Vector3D(0.0747, -0.043, 0); // Face 8

        originFaceVector[1][0] = new Vector3D(0.0385, 0, 0); // Face 1
        originFaceVector[1][1] = new Vector3D(0.008, -0.0305, 0); // Face 2
        originFaceVector[1][2] = new Vector3D(0.008, 0.0305, 0); // Face 3

        // Emerge module
        originFaceVector[2][0] = new Vector3D(-0.0385, 0, 0); // Face 0
        originFaceVector[2][1] = new Vector3D(0.0385, 0, 0); // Face 1
        originFaceVector[2][2] = new Vector3D(0.008, -0.0305, 0); // Face 2
        originFaceVector[2][3] = new Vector3D(0.008, 0.0305, 0); // Face 3

        originFaceVector[3][0] = new Vector3D(0, 0, -0.015);

        /**********************************************************************/
        /************************** normalFaceVector **************************/
        /**********************************************************************/
        // This are the coordinates of an unit vector normal to the face
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

        normalFaceVector[1][0] = new Vector3D(1, 0, 0); // Face 1
        normalFaceVector[1][1] = new Vector3D(0, -1, 0); // Face 2
        normalFaceVector[1][2] = new Vector3D(0, 1, 0); // Face 3

        // Emerge module
        normalFaceVector[2][0] = new Vector3D(-1, 0, 0); // Face 0
        normalFaceVector[2][1] = new Vector3D(1, 0, 0); // Face 1
        normalFaceVector[2][2] = new Vector3D(0, -1, 0); // Face 2
        normalFaceVector[2][3] = new Vector3D(0, 1, 0); // Face 3

        normalFaceVector[3][0] = new Vector3D(0, 0, -1); // Face 1

        /**********************************************************************/
        /************************** boundingBox **************************/
        /**********************************************************************/
        // This are the size of boundingBox for each module type, it is
        // represented as (width,height,length); (Y, Z, X) in CoppeliaSim. Units: meter
        // Base module:
        boundingBox[0] = new Vector3D(0.1494, 0.055023, 0.1494);
        boundingBox[1] = new Vector3D(0.061, 0.055, 0.077);
        boundingBox[2] = new Vector3D(0.061, 0.055, 0.077);
        boundingBox[3] = new Vector3D(0.055, 0.058, 0.030);

    }

}
