package modules;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class EmergeProximitySensorModuleSet extends ModuleSet {

    public EmergeProximitySensorModuleSet() {
        this(3, 1, 1);
    }

    public EmergeProximitySensorModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {
        super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);
        // TODO Auto-generated constructor stub

        // The name of this set of modules (it has to match with the folder
        // where the modules are stored in the edhmor folder)
        moduleSetName = "emergeModules";

        // The name of the different modules (they have to match with the name of
        // the file to load in the simulator)
        // moduleName[0] = "initialemergeModule";
        moduleName[0] = "Emerge18Modules1-ConeProximitySensor";
        // define the different properties
        // number of faces for each type of module

        modulesFacesNumber[0] = 1; // Number of faces for base module

        modulesBaseFacesNumber[0] = 1; // Number of faces in the base for Emerge module (all)

        // number of possible orientations when a module is joined to other module
        moduleOrientations[0] = 1; // Number of orientations of the module base 4 in Evolution

        connectionFaceForEachOrientation[1][0] = 0; // connected in face 0

        rotationAboutTheNormalForEachOrientation[1][0] = 0; // 0 degrees

        // mass of each module in Kg
        modulesMass[0] = 0.06; // Mass of the base module

        // Control parameters

        // max amplitude
        modulesMaxAmplitude[0] = 0.5 * Math.PI; // Max amplitude of the base

        // max angular frequency
        modulesMaxAngularFrequency[0] = 2.0; // Max angular frequency of the base

        /**********************************************************************/
        /************************** originFaceVector **************************/
        /**********************************************************************/
        // This are the coordinates of the vector from the origin of the module
        // to the face of the module for each face and module type

        // Emerge module
        originFaceVector[0][0] = new Vector3D(0, 0, -0.015);

        /**********************************************************************/
        /************************** normalFaceVector **************************/
        /**********************************************************************/
        // This are the coordinates of an unit vector normal to the face
        // for each face and module type
        // Base module:
        normalFaceVector[0][0] = new Vector3D(0, 0, -1); // Face 1

        /**********************************************************************/
        /************************** boundingBox **************************/
        /**********************************************************************/
        // This are the size of boundingBox for each module type, it is
        // represented as (width,height,length); (Y, Z, X) in CoppeliaSim. Units: meter
        // Base module:
        boundingBox[0] = new Vector3D(0.055, 0.058, 0.030);

    }

}
