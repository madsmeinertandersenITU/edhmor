package modules;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class EmergeProximitySensorModuleSet extends Emerge18FlatBaseModuleSet {

    public EmergeProximitySensorModuleSet() {
        this(8, 8, 2);
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
        // moduleName[0] = "flatBase";
        moduleName[2] = "ConeProximitySensorModule";
        // define the different properties
        // number of faces for each type of module

        // modulesFacesNumber[0] = 8; // Number of faces for base module
        modulesFacesNumber[2] = 1; // Number of faces for base module
        modulesBaseFacesNumber[2] = 1;

        // number of possible orientations when a module is joined to other module
        // moduleOrientations[0] = 0; // Number of orientations of the module base 4 in
        // Evolution
        moduleOrientations[2] = 1; // Number of orientations of the module base 4 in Evolution
        // Evolution

        // connectionFaceForEachOrientation[0][0] = 0; // connected in face 0
        connectionFaceForEachOrientation[2][0] = 0; // connected in face 0

        rotationAboutTheNormalForEachOrientation[2][0] = 0;// 90 degrees

        modulesMass[2] = 0.01; // Mass of the base module

        /**********************************************************************/
        /************************** originFaceVector **************************/
        /**********************************************************************/
        // This are the coordinates of the vector from the origin of the module
        // to the face of the module for each face and module type

        originFaceVector[2][0] = new Vector3D(0, 0, -0.015);

        normalFaceVector[2][0] = new Vector3D(0, 0, -1); // Face 1

        /**********************************************************************/
        /************************** boundingBox **************************/
        /**********************************************************************/
        // This are the size of boundingBox for each module type, it is
        // represented as (width,height,length); (Y, Z, X) in CoppeliaSim. Units: meter

        boundingBox[2] = new Vector3D(0.0055, 0.0058, 0.0030);

    }

}
