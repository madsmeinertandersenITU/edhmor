/* 
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2015 GII (UDC) and REAL (ITU)
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
package modules.evaluation;

import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.vecmath.Point3d;
import modules.ModuleSet;
import modules.ModuleSetFactory;
import modules.util.ModuleRotation;
import modules.util.SimulationConfiguration;
import mpi.MPI;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author fai
 */
public class CoppeliaSimCreateRobot {

    private String worldbase = "base.world";

    protected remoteApi coppeliaSimApi;
    protected int clientID;
    private Map<Integer, Integer> moduleHandlers;
    private List<Integer> forceSensorHandlers;
    private List<Integer> proximitySensorHandlers;
    protected ModuleSet moduleSet;
    protected CalculateModulePositions robotFeatures;
    private boolean pauseandshow = false;
    private boolean guiOn = false;
    protected String scene;

    private static final int WORLD_COORD = -1;
    private int rank = 0;

    /**
     * Class constructor to build a robot in the CoppeliaSim simulator based on the
     * chromosome, giving the possibility of pausing between each added module
     * <p>
     *
     * @param api              the remote API library of the CoppeliaSim simulator
     * @param clientID         the CoppeliaSim client ID to communicate
     * @param chromosomeDouble the chromosome where the morphology and the
     *                         control parameters are stored as doubles
     * @param scene            the scene to load in the simulator
     * @param pauseandshow     if true will pause every time a module is added and
     *                         show its control parameters on the java console
     *
     */
    public CoppeliaSimCreateRobot(remoteApi api, int clientID, double[] chromosomeDouble, String scene,
            boolean pauseandshow) {
        this(clientID, chromosomeDouble, scene);
        this.coppeliaSimApi = api;
        this.clientID = clientID;
        this.pauseandshow = pauseandshow;
    }

    public CoppeliaSimCreateRobot(remoteApi api, int clientID, double[] chromosomeDouble, String scene,
            boolean pauseandshow, boolean guiOn) {
        this(api, clientID, chromosomeDouble, scene);
        this.pauseandshow = pauseandshow;
        this.guiOn = guiOn;
    }

    /**
     * Class constructor to build a robot in the CoppeliaSim simulator based on the
     * chromosome and specifying a fitness parameter
     * <p>
     *
     * @param api              the remote API library of the CoppeliaSim simulator
     * @param clientID         the CoppeliaSim client ID to communicate
     * @param chromosomeDouble the chromosome where the morphology and the
     *                         control parameters are stored as doubles
     * @param scene            the scene to load in the simulator
     * @param fP               the value of the fitness parameter. It is used to
     *                         change some
     *                         properties of the environment
     *
     */
    CoppeliaSimCreateRobot(remoteApi api, int clientID, double[] chromosomeDouble, String scene) {
        this(clientID, chromosomeDouble, scene);
        this.coppeliaSimApi = api;
        this.clientID = clientID;

    }

    /**
     * Class constructor to build a robot in the CoppeliaSim simulator based on the
     * chromosome
     * <p>
     *
     * @param clientID the CoppeliaSim client ID to communicate
     * @param chromo   the chromosome where the morphology and the control
     *                 parameters are stored as doubles
     * @param scene    the scene to load in the simulator
     *
     */
    CoppeliaSimCreateRobot(int clientID, double[] chromo, String scene) {

        this.scene = scene;
        if (SimulationConfiguration.isUseMPI()) {
            this.rank = MPI.COMM_WORLD.Rank();
        }

        // Load the module set
        moduleSet = ModuleSetFactory.getModulesSet();

        robotFeatures = new CalculateModulePositions(chromo);

    }

    /**
     * Creates the robot in the CoppeliaSim simulator based on the chromosome array.
     * <p>
     * 
     * @return true if the robot has been built correctly in the simulator,
     *         false otherwise.
     */
    public boolean createRobot() {

        // load a new scene
        boolean success = loadScene();

        // calculate the rotation and position of the modules and the force
        // sensors. Load them in CoppeliaSim simulator. We also calcualte the dimensions
        // of the robot, the center of mass and other useful features of the
        // robot
        success &= robotAssembly();
        return success;
    }

    public boolean loadScene() {
        // TODO: select the correct scene for the simulation (flat terrain, obstacles,
        // paint wall, etc...)
        String scenePath;
        if (scene.isBlank()) {
            scenePath = "scenes/edhmor/default.ttt";
        } else {
            scenePath = "scenes/edhmor/" + scene + ".ttt";
        }

        int nAttemps = SimulationConfiguration.getAttempts();
        int ret;
        for (int i = 0; i < nAttemps; i++) {
            ret = coppeliaSimApi.simxLoadScene(clientID, scenePath, 0, remoteApi.simx_opmode_oneshot_wait);
            if (ret == remoteApi.simx_return_ok) {
                // System.out.format("Scene loaded correctly: \n");
                return true;
            } else {
                System.err.format(
                        "CoppeliaSimCreateRobot (" + rank
                                + "). Error loading the scene: Remote API function call returned with error code: %d\n",
                        ret);
                System.err.println(
                        "CoppeliaSimCreateRobot. Check that the CoppeliaSim simulator is running and listening in the correct port.");
                System.err.println("CoppeliaSimCreateRobot. Check also the scene path: " + scenePath);

            }
        }
        return false;
    }

    private boolean robotAssembly() {
        double initialHeight = (Math.abs(robotFeatures.getMinPos().z) + 0.001);
        double[] posIni = { 0, 0, initialHeight };
        double[] poszero = { 0, 0, 0 };

        boolean success = initAssembly(posIni, poszero);

        for (int module = 1; module < robotFeatures.getnModules(); module++) {

            success &= addAndConnectModule(module, posIni, poszero);
            if (!success) {
                break;
            }
        }

        if (guiOn) {
            // Add a frame reference to have a scale
            IntW moduleHandle = new IntW(0);
            int ret = coppeliaSimApi.simxLoadModel(clientID, "models/other/reference frame.ttm", 0, moduleHandle,
                    remoteApi.simx_opmode_oneshot_wait);
            double[] pos = { 1, 1, 0 };
            moveModule(moduleHandle.getValue(), -1, pos);

            // Add a second reference frame very far away to deselect the other one
            ret = coppeliaSimApi.simxLoadModel(clientID, "models/other/reference frame.ttm", 0, moduleHandle,
                    remoteApi.simx_opmode_oneshot_wait);
            double[] pos2 = { 100, 100, 100 };
            moveModule(moduleHandle.getValue(), -1, pos2);
        }

        success &= finishAssembly(posIni, initialHeight);
        if (!success) {
            System.out.println(rank + ": CoppeliaSimCreateRobot. Robot assemled with errors.");
        }

        System.out.println("FINAL HANDLERS: " + moduleHandlers);

        return success;
    }

    protected boolean initAssembly(double[] posIni, double[] poszero) {
        moduleHandlers = new HashMap<>(); // Change the type to Map
        forceSensorHandlers = new ArrayList<Integer>();

        int rootModuleHandler = addModule(0);
        moduleHandlers.put(rootModuleHandler, 0); // Store in the map
        System.out.println("ADDING ROOT 232: " + rootModuleHandler);

        if (pauseandshow) {
            Set<Integer> keys = moduleHandlers.keySet();
            List<Integer> keyList = new ArrayList<>(keys);
            Integer keyNumber = keyList.get(keyList.size() - 1);
            moveModule(keyNumber, -1, posIni);
            System.out.println("Just added module: 0");
            System.out.println("Amplitude control, AngularFreqControl, PhaseControl");
            System.out.print(robotFeatures.getAmplitudeControl()[0]);
            System.out.print(", " + robotFeatures.getAngularFreqControl()[0]);
            System.out.println(", " + robotFeatures.getPhaseControl()[0]);
            promptEnterKey();
            moveModule(keyNumber, -1, poszero);
        }

        return rootModuleHandler >= 0;
    }

    protected boolean finishAssembly(double[] posIni, double initialHeight) {
        if (this.worldbase.contains("sueloRugoso")) {
            initialHeight += 0.15;
        }

        if (this.worldbase.contains("manipulator")) {
            initialHeight = 1;
        }

        // System.out.println("initialHeight: "+ initialHeight);
        if (initialHeight < (0.055 / 2)) {
            initialHeight += (0.055 / 2);
        }
        // Move the robot up
        // double[] posIni = {0, 0, initialHeight};
        // System.out.println("Moving robot up");
        Set<Integer> keys = moduleHandlers.keySet();
        List<Integer> keyList = new ArrayList<>(keys);

        Integer lastKey = keyList.get(keyList.size() - 1);
        System.out.println("MOVING MODULE 263 " + lastKey);
        return moveModule(lastKey, -1, posIni);
    }

    protected void shiftBaseTemp(double[] posIni, double[] poszero, boolean reset) {
        Set<Integer> keys = moduleHandlers.keySet();
        List<Integer> keyList = new ArrayList<>(keys);
        Integer keyNumber = keyList.get(keyList.size() - 1);
        if (reset) {
            moveModule(keyNumber, -1, poszero);
        } else {
            moveModule(keyNumber, -1, posIni);
        }

    }

    protected boolean addAndConnectModule(int module, double[] posIni, double[] poszero) {

        Vector3D[] modulePosition = robotFeatures.getModulePosition();
        Rotation[] moduleRotation = robotFeatures.getModuleRotation();

        int moduleHandler = addModule(module);
        boolean success = moduleHandler >= 0;

        int[] moduleType = robotFeatures.getModuleType();
        int[] parentModule = robotFeatures.getParentModule();
        int modType = moduleType[module];
        moduleHandlers.put(moduleHandler, modType);
        System.out.println("ADDING MODULE 286 " + moduleHandler);
        int parentModuleType = moduleType[parentModule[module]];
        int conectionFace = robotFeatures.getDadFace()[module - 1] % moduleSet.getModulesFacesNumber(parentModuleType);
        int orientation = robotFeatures.getChildOrientation()[(module - 1)] % moduleSet.getModuleOrientations(modType);

        int parentModuleValue = parentModule[module];

        Integer result = null;

        for (Map.Entry<Integer, Integer> entry : moduleHandlers.entrySet()) {
            if (entry.getValue().equals(parentModuleValue)) {
                result = entry.getKey();
                break;
            }
        }

        // Get the vector which is normal to the face of the parent
        Vector3D normalParentFace = moduleSet.getNormalFaceVector(parentModuleType, conectionFace);

        // Get the vector from the origin of the module to the connection
        // face (in the parent) Origin parent -> Face parent (OFP)
        Vector3D ofp = moduleSet.getOriginFaceVector(parentModuleType, conectionFace);

        // Face of the child to attach the module
        int childFace = moduleSet.getConnectionFaceForEachOrientation(modType, orientation);

        // Rotate module to the correct orientation in CoppeliaSim
        success &= rotateModule(moduleHandler, WORLD_COORD,
                new ModuleRotation(moduleRotation[module]).getEulerAngles());

        // Move module to the correct position in CoppeliaSim
        success &= moveModule(moduleHandler, WORLD_COORD, modulePosition[module]);

        // Add Force Sensor in CoppeliaSim
        int forceSensor = addForceSensor();
        forceSensorHandlers.add(forceSensor);
        success &= (forceSensor >= 0);

        if (normalParentFace.getZ() == 0) {
            double[] forceSensorOrientation = { 0, 0, 0 };
            if (normalParentFace.getY() == 0) {
                forceSensorOrientation[1] = Math.PI / 2;
            } else {
                forceSensorOrientation[0] = Math.PI / 2;
            }
            // Rotate Force Sensor in CoppeliaSim
            System.out.println("ROTATING MODULE 329 " + result);

            success &= rotateModule(forceSensor, result, forceSensorOrientation);
        }
        // Move Force Sensor in CoppeliaSim
        double posForceSensor[] = ofp.toArray();

        Set<Integer> keys = moduleHandlers.keySet();
        List<Integer> keyList = new ArrayList<>(keys);

        System.out.println("MOVING MODULE 335 " + result);

        success &= moveModule(forceSensor, result, posForceSensor);

        // Set the force sensor as a child of the parent module
        // FIXME: This code will not work in modules with 2 or more dof
        // First, calculate how many shapes and joints has a module
        // Atron modules have 6 (4 shapes, 1 joint and 1 dummy)
        // The rest have 4 (2 shapes, 1 joint and 1 dummy)
        // We first calculate the number of shapes of the parent module
        // substract one to remove force sensor between modules
        Integer keyNumber = keyList.get(module - 1);
        System.out.println(keyList);
        System.out.println("CALC numberOfShapesAndJoints 344  GET " + keyNumber);

        System.out.println("CALC numberOfShapesAndJoints 344  GET PARENT " + result);

        int numberOfShapesAndJoints = keyNumber - result - 1;
        int offset = 0;
        if (!moduleSet.faceBelongsToBasePart(moduleType[parentModule[module]], conectionFace)) {
            offset = numberOfShapesAndJoints / 2;
        }
        System.out.println("SET OBJECT PARENT 355 " + result);

        success &= setObjectParent(forceSensor, result + 1 + offset);

        // Set the child module as a child of the foce sensor
        // FIXME: This code will not work in modules with 2 or more dof
        if (moduleSet.faceBelongsToBasePart(modType, childFace)) {
            success &= setObjectParent(moduleHandler + 1, forceSensor);
        } else {
            numberOfShapesAndJoints = forceSensor - moduleHandlers.get(keyNumber);
            if (numberOfShapesAndJoints == 4) {
                success &= setObjectParent(moduleHandler + 3, forceSensor);
                success &= setObjectParent(moduleHandler + 2, moduleHandler + 3);
                success &= setObjectParent(moduleHandler + 1, moduleHandler + 2);
            } else {
                // If there are 6 objects, the visual shape is alredy set as
                // a child of the convex shape, skip them to speed up
                success &= setObjectParent(moduleHandler + 4, forceSensor);
                success &= setObjectParent(moduleHandler + 3, moduleHandler + 4);
                success &= setObjectParent(moduleHandler + 1, moduleHandler + 3);
            }

        }
        if (pauseandshow) {
            Integer keyZero = keyList.get(keyList.size() - 1);
            moveModule(keyZero, -1, posIni);
            System.out.println("Just added module: " + module);
            System.out.println("Amplitude control, AngularFreqControl, PhaseControl");
            System.out.print(robotFeatures.getAmplitudeControl()[module]);
            System.out.print(", " + robotFeatures.getAngularFreqControl()[module]);
            System.out.println(", " + robotFeatures.getPhaseControl()[module]);
            promptEnterKey();
            moveModule(keyZero, -1, poszero);
        }
        return success;
        // And calculate the offset of the shape to attach, if not attached to the base
        // part
    }

    protected int addModule(int moduleNumber) {
        String modelPath = modelPath(robotFeatures.getModuleType()[moduleNumber]);

        IntW moduleHandle = new IntW(0);
        // clientID,final String modelPathAndName, int options, IntW baseHandle, int
        // operationMode
        int ret = coppeliaSimApi.simxLoadModel(clientID, modelPath, 0, moduleHandle,
                remoteApi.simx_opmode_oneshot_wait);

        if (ret == remoteApi.simx_return_ok) {
            // System.out.format("Model loaded correctly: %d\n",
            // parentModuleHandle.getValue());
            return moduleHandle.getValue();
        } else {
            System.err.format(rank
                    + ": CoppeliaSimCreateRobot, addModule Function: Remote API function call returned with error code: %d\n",
                    ret);
            System.err
                    .println(rank
                            + ": CoppeliaSimCreateRobot, addModule Function: Check that the model path is correct: "
                            + modelPath);
            return -1;
        }
    }

    private int addForceSensor() {
        String modelPath = forceSensorPath();

        IntW forceSensorHandle = new IntW(0);
        // clientID,final String modelPathAndName, int options, IntW baseHandle, int
        // operationMode
        int ret = coppeliaSimApi.simxLoadModel(clientID, modelPath, 0, forceSensorHandle,
                remoteApi.simx_opmode_oneshot_wait);

        if (ret == remoteApi.simx_return_ok) {
            // System.out.format("Model loaded correctly: %d\n",
            // parentModuleHandle.getValue());
            return forceSensorHandle.getValue();
        } else {
            System.out.format(
                    "%d: CoppeliaSimCreateRobot, addForceSensor Function: Remote API "
                            + "function call returned with error code: %d\n",
                    rank, ret);
            System.err.println(rank + ": CoppeliaSimCreateRobot, addForceSensor Function: "
                    + "Check that the force sensor path is correct: "
                    + modelPath);
            return -1;
        }
    }

    private boolean rotateModule(int moduleHandler, int parentHandler, double[] rotation) {
        // int simxSetObjectPosition(int clientID,int objectHandle, int
        // relativeToObjectHandle, final FloatWA position, int operationMode)

        FloatWA rot = new FloatWA(3);
        rot.getArray()[0] = (float) (rotation[0]);
        rot.getArray()[1] = (float) (rotation[1]);
        rot.getArray()[2] = (float) (rotation[2]);

        int ret = coppeliaSimApi.simxSetObjectOrientation(clientID, moduleHandler, parentHandler, rot,
                remoteApi.simx_opmode_oneshot);

        if (ret > remoteApi.simx_return_novalue_flag) {
            System.out.format("rotateModule Function: Remote API function call returned with error code: %d\n", ret);
            return false;
        }
        return true;
    }

    private boolean moveModule(int moduleHandler, int parentHandler, double[] position) {
        // int simxSetObjectPosition(int clientID,int objectHandle, int
        // relativeToObjectHandle, final FloatWA position, int operationMode)
        FloatWA pos = new FloatWA(3);
        pos.getArray()[0] = (float) position[0];
        pos.getArray()[1] = (float) position[1];
        pos.getArray()[2] = (float) position[2];

        int ret = coppeliaSimApi.simxSetObjectPosition(clientID, moduleHandler, parentHandler, pos,
                remoteApi.simx_opmode_oneshot);
        if (ret > remoteApi.simx_return_novalue_flag) {
            System.out.format("moveModule Function: Remote API function call returned with error code: %d\n", ret);
            return false;
        }
        return true;
    }

    private boolean moveModule(int moduleHandler, int parentHandler, Vector3D vec) {
        double pos[] = vec.toArray();
        return moveModule(moduleHandler, parentHandler, pos);
    }

    private boolean setObjectParent(int moduleHandler, int parentHandler) {
        // int simxSetObjectParent(int clientID,int objectHandle,int
        // parentObject,boolean keepInPlace,int operationMode);

        int ret = coppeliaSimApi.simxSetObjectParent(clientID, moduleHandler, parentHandler, true,
                remoteApi.simx_opmode_oneshot);
        if (ret > remoteApi.simx_return_novalue_flag) {
            System.out.format("%d: setObjectParent Function: Remote API function"
                    + " call returned with error code: %d\n", rank, ret);
            return false;
        }
        return true;
    }

    protected String modelPath(int moduleType) {
        String path = "models/edhmor/";
        path += moduleSet.getModuleSetName() + "/"; // moduleSetName
        path += moduleSet.getModuleName(moduleType) + ".ttm"; // moduleName
        return path;
    }

    private String forceSensorPath() {
        String path = "models/edhmor/";
        path += moduleSet.getModuleSetName() + "/"; // moduleSetName
        path += "forceSensor.ttm"; // moduleName
        return path;
    }

    private void promptEnterKey() {
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public Map<Integer, Integer> getModuleHandlers() {
        return moduleHandlers;
    }

    public List<Integer> getForceSensorHandlers() {
        return forceSensorHandlers;
    }

    public List<Integer> getProximitySensorHandlers() {
        return proximitySensorHandlers;
    }

    public double[] getAmplitudeControl() {
        return robotFeatures.getAmplitudeControl();
    }

    public double[] getAngularFreqControl() {
        return robotFeatures.getAngularFreqControl();
    }

    public int[] getPhaseControl() {
        return robotFeatures.getPhaseControl();
    }

    public int[] getModuleType() {
        return robotFeatures.getModuleType();
    }

    public boolean isPauseandshow() {
        return pauseandshow;
    }

    public void setPauseandshow(boolean pauseandshow) {
        this.pauseandshow = pauseandshow;
    }

    public CalculateModulePositions getRobotFeatures() {
        return robotFeatures;
    }
}
