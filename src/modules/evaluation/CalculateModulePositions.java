/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2016 GII (UDC) and REAL (ITU)
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
import coppelia.remoteApi;
import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3d;
import modules.ModuleSet;
import modules.ModuleSetFactory;
import modules.evaluation.overlapping.BoundingBoxCollisionDetector;
import modules.evaluation.overlapping.SphereCollisionDetector;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.ModuleRotation;
import modules.util.SimulationConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * CalculateModulePositions.java Created on 25/03/2016
 *
 * @author Andres Faiña <anfv at itu.dk>
 */
public class CalculateModulePositions {

    private int nModulesMax;
    private int nModules = 1;
    private int[] chromosomeInt;
    private double[] chromosomeDouble;
    private int[] moduleType;
    private int[] dadFace;
    private String pathBase = "/home/fai/svn/tesis/programacion/gazebo/tesis/";
    private String worldbase = "base.world";
    private int[] childOrientation;
    private double amplitudeControl[];
    private double angularFreqControl[];
    private int phaseControl[];
    private int[] parentModule;
    private int[][] occupiedFaces;

    private double[] typePercentage;
    private Point3d minPos, maxPos, dimensions;
    private double coverage;
    private Vector3D com;
    private List<Vector3D> supportFaces;
    private double robotMass = 0.0;
    private double initialHeight = 0.0;
    private boolean useSoporte = false;
    private double fitnessParameter = 0.0;

    private Vector3D inertia;
    private double averageConnectionsPerModule;
    private double dispersionConnectionsPerModule;

    // Descriptors
    private double limbDescriptor;
    private double limbLengthDescriptor;

    private Vector3D[] modulePosition;
    private Rotation[] moduleRotation;
    private ModuleSet moduleSet;

    /**
     * Class constructor to calculate the position and orientation of the
     * modules based on the chromosome. This class is used to build the robot in
     * the simulator, find the features of the robot and check the overlapping
     * between modules
     * <p>
     *
     * @param tree the tree individual where the morphology and the control
     *             parameters are stored
     *
     */
    public CalculateModulePositions(TreeIndividual tree) {
        // Load the module set
        moduleSet = ModuleSetFactory.getModulesSet();
        this.nModulesMax = (tree.getChromosomeAt(0).length + 3) / 9;
        this.nModules = tree.getRootNode().getNumberModulesBranch();

        initArrays();
        treeAnalysis(tree);

        // calculate the rotation and position of the modules and the force
        // sensors. Load them in CoppeliaSim simulator. We also calcualte the dimensions
        // of the robot, the center of mass and other useful features of the
        // robot
        calculate();
    }

    /**
     * Class constructor to calculate the position and orientation of the
     * modules based on the chromosome. This class is used to build the robot in
     * the simulator, find the features of the robot and check the overlapping
     * between modules
     * <p>
     *
     * @param chromo the chromosome where the morphology and the control
     *               parameters are stored as doubles
     *
     */
    public CalculateModulePositions(double[] chromo) {

        // Load the module set
        moduleSet = ModuleSetFactory.getModulesSet();

        // Convert the doubles to integers
        chromosomeInt = new int[chromo.length];
        for (int i = 0; i < chromo.length; i++) {
            chromosomeInt[i] = (int) Math.floor(chromo[i]);
        }

        // Calculate the maximum number of modules that this chromosome allows.
        // This code is campatible with all the different chromosomes lengths
        // TODO: remove the oldest chromosome versions
        if ((chromo.length + 3) % 9 == 0) {
            this.nModulesMax = (chromo.length + 3) / 9;
        } else {

            if ((chromo.length + 3) % 6 == 0) {
                this.nModulesMax = (chromo.length + 3) / 6;

            } else {
                if ((chromo.length + 3) % 5 == 0) {
                    this.nModulesMax = (chromo.length + 3) / 5;

                } else {
                    if (chromo.length % 5 == 0) {
                        // Depreciated
                        System.err.println(
                                "CalculateModulePositions: Are you sure? This lenght for the chromosome is deprecated. Chromosome lenght: "
                                        + chromo.length + ", in other words, chromosome%5=0");

                        // Metamodule base as the beginning of morphological tree
                        this.nModulesMax = (chromo.length / 5) + 1;

                    } else {
                        System.err.println("CalculateModulePositions: Error in the lenght of the chromosome; length: "
                                + chromo.length);
                        for (int i = 0; i < chromo.length; i++) {
                            System.err.print(chromo[i] + ", ");
                        }
                        System.exit(-1);
                    }

                }
                if (SimulationConfiguration.isDebug()) {
                    for (int i = 0; i < this.chromosomeInt.length; i++) {
                        System.out.print(this.chromosomeInt[i] + ", ");
                    }
                }
            }
        }

        this.chromosomeDouble = chromo;

        initArrays();

        if ((chromo.length + 3) % 9 != 0) {
            for (int i = 0; i < nModulesMax; i++) {
                this.amplitudeControl[i] = 0.0;
                this.angularFreqControl[i] = 0.0;
            }
        }

        // Now, analysis the chromosome
        this.chromosomeAnalysis();

        // calculate the rotation and position of the modules and the force
        // sensors. Load them in CoppeliaSim simulator. We also calcualte the dimensions
        // of the robot, the center of mass and other useful features of the
        // robot
        calculate();
    }

    private void initArrays() {
        this.moduleType = new int[nModulesMax];

        this.dadFace = new int[nModulesMax - 1];
        this.childOrientation = new int[nModulesMax - 1];
        this.amplitudeControl = new double[nModulesMax];
        this.angularFreqControl = new double[nModulesMax];
        this.phaseControl = new int[nModulesMax];
        this.parentModule = new int[nModulesMax];
        this.occupiedFaces = new int[nModulesMax][16];

        this.maxPos = new Point3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        this.minPos = new Point3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        this.dimensions = new Point3d(0, 0, 0);
        this.com = new Vector3D(0, 0, 0);
        this.supportFaces = new ArrayList<Vector3D>();

        typePercentage = new double[moduleSet.getModulesTypeNumber()];
        for (int i = 0; i < typePercentage.length; i++) {
            typePercentage[i] = 0;
        }
    }

    private void calculate() {

        // Start with the first module
        int type = this.moduleType[0];
        typePercentage[type]++;
        this.robotMass += moduleSet.getModulesMass(type);
        switch (moduleSet.getBoundingMethod()) {
            case SPHERE:
                double diameter = moduleSet.getBoundingSphereDiameter();
                this.coverage = Math.pow(diameter * 0.5, 3) * Math.PI * 4 / 3;
                break;
            case BOX:
                Vector3D volModule = moduleSet.getboundingBox(type);
                this.coverage = volModule.getX() * volModule.getY() * volModule.getZ();
                break;
        }

        Vector3D facePos;
        for (int face = 0; face < moduleSet.getModulesFacesNumber(type); face++) {
            facePos = moduleSet.getOriginFaceVector(type, face);

            if (this.maxPos.x < facePos.getX()) {
                this.maxPos.x = facePos.getX();
            }
            if (this.maxPos.y < facePos.getY()) {
                this.maxPos.y = facePos.getY();
            }
            if (this.maxPos.z < facePos.getZ()) {
                this.maxPos.z = facePos.getZ();
            }

            if (this.minPos.x > facePos.getX()) {
                this.minPos.x = facePos.getX();
            }
            if (this.minPos.y > facePos.getY()) {
                this.minPos.y = facePos.getY();
            }
            if (this.minPos.z > facePos.getZ()) {
                this.minPos.z = facePos.getZ();
                supportFaces.clear();
            }
            // Add supports
            if (facePos.getZ() - this.minPos.z < 0.15) {
                supportFaces.add(facePos);
            }
        }

        modulePosition = new Vector3D[nModules];
        moduleRotation = new Rotation[nModules];
        modulePosition[0] = new Vector3D(0, 0, 0);
        moduleRotation[0] = Rotation.IDENTITY;

        for (int module = 1; module < nModules; module++) {

            int modType = moduleType[module];
            typePercentage[modType]++;
            int parentModuleType = moduleType[parentModule[module]];
            int conectionFace = dadFace[module - 1] % moduleSet.getModulesFacesNumber(parentModuleType);
            int orientation = childOrientation[(module - 1)] % moduleSet.getModuleOrientations(modType);

            switch (moduleSet.getBoundingMethod()) {
                case SPHERE:
                    double diameter = moduleSet.getBoundingSphereDiameter();
                    this.coverage += Math.pow(diameter * 0.5, 3) * Math.PI * 4 / 3;
                    break;
                case BOX:
                    Vector3D volModule = moduleSet.getboundingBox(type);
                    this.coverage += volModule.getX() * volModule.getY() * volModule.getZ();
                    break;
            }

            // Check that the face of the parent where we are going to attach the
            // child ist available
            while (occupiedFaces[parentModule[module]][conectionFace] == 1) {
                System.err.println("Error in CalculateModulePositions: face already occupied. Individual:");
                for (int i = 0; i < chromosomeInt.length; i++) {
                    System.out.print(chromosomeInt[i] + " ");
                }
                for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
                    System.out.println(ste);
                }
                System.exit(-1);
                conectionFace = ++conectionFace % moduleSet.getModulesFacesNumber(parentModuleType);
            }
            occupiedFaces[parentModule[module]][conectionFace] = 1;

            // Get the vector which is normal to the face of the parent
            Vector3D normalParentFace = moduleSet.getNormalFaceVector(parentModuleType, conectionFace);
            // Get the vector which is coplanar to the face of the parent
            Vector3D coplanarParentFace = moduleSet.getCoplanarFaceVector(parentModuleType, conectionFace);

            // Calculate the rotation of the child to align both normals and
            // roate them according to the orientation of the child
            ModuleRotation rot = new ModuleRotation(modType, orientation, normalParentFace, coplanarParentFace);

            // Get the vector from the origin of the module to the connection
            // face (in the parent) Origin parent -> Face parent (OFP)
            Vector3D ofp = moduleSet.getOriginFaceVector(parentModuleType, conectionFace);

            // Face of the child to attach the module
            int childFace = moduleSet.getConnectionFaceForEachOrientation(modType, orientation);

            // Get the vector from the origin of the module to the connection
            // face (in the child) Origin child -> Face child (OFC)
            Vector3D ofc = moduleSet.getOriginFaceVector(modType, childFace);
            occupiedFaces[module][childFace] = 1; // Set that face occupied

            // Rotate the ofc vector
            Vector3D ofcRotated = rot.getRotation().applyTo(ofc);
            ofcRotated = ofcRotated.negate(); // Negate it

            // Obtain the position of the chid in the coord. system of the parent
            Vector3D posVector = ofcRotated.add(ofp);

            // Rotation and pos of the module in the coord. system of the world
            moduleRotation[module] = moduleRotation[parentModule[module]].applyTo(rot.getRotation());
            modulePosition[module] = moduleRotation[parentModule[module]].applyTo(posVector);
            modulePosition[module] = modulePosition[module].add(modulePosition[parentModule[module]]);

            // Calculate the center of mass of the robot
            Vector3D aux = new Vector3D(modulePosition[module].getX(), modulePosition[module].getY(),
                    modulePosition[module].getZ());
            double s = moduleSet.getModulesMass(modType);
            this.robotMass += s;
            aux = aux.scalarMultiply(s);
            this.com = this.com.add(aux);

            if (SimulationConfiguration.isDebug()) {
                System.out.println("Total mass of the robot: " + this.robotMass + "; cdm: " + this.com);
            }

            // We check the position in wold coodinates for all the faces of the
            // modules. We find the lowest position in Z axis and the we added
            // this distance to the root module. Then, all the modules are above
            // the ground level and at least the robot has one point of support.
            // FIXME: Hinge modules in Edhmor and Rodrigo´s modules in one
            // orientation dont have faces in all the directions, then they could
            // be partially buried in the ground.
            int nFacesToLook = moduleSet.getModulesFacesNumber(modType);
            for (int face = 0; face < nFacesToLook; face++) {
                ofc = moduleSet.getOriginFaceVector(modType, face);
                ofcRotated = moduleRotation[module].applyTo(ofc);
                ofcRotated = ofcRotated.add(modulePosition[module]);

                if (this.maxPos.x < ofcRotated.getX()) {
                    this.maxPos.x = ofcRotated.getX();
                }
                if (this.maxPos.y < ofcRotated.getY()) {
                    this.maxPos.y = ofcRotated.getY();
                }
                if (this.maxPos.z < ofcRotated.getZ()) {
                    this.maxPos.z = ofcRotated.getZ();
                }

                if (this.minPos.x > ofcRotated.getX()) {
                    this.minPos.x = ofcRotated.getX();
                }
                if (this.minPos.y > ofcRotated.getY()) {
                    this.minPos.y = ofcRotated.getY();
                }
                if (this.minPos.z > ofcRotated.getZ()) {
                    this.minPos.z = ofcRotated.getZ();
                    supportFaces.clear();
                }
                // Add supports
                if (ofcRotated.getZ() - this.minPos.z < 0.15) {
                    supportFaces.add(ofcRotated);
                }
            }

        }

        this.dimensions.add(this.maxPos);
        this.dimensions.sub(this.minPos);
        this.com = this.com.scalarMultiply(1 / this.robotMass);
        this.coverage /= (this.dimensions.x * this.dimensions.y * this.dimensions.z);

        // Calculate the moment of inertia
        double Ix = 0, Iy = 0, Iz = 0;
        for (int module = 1; module < nModules; module++) {
            Vector3D posModule = new Vector3D(modulePosition[module].getX(), modulePosition[module].getY(),
                    modulePosition[module].getZ());

            double axisZdistance = Math.pow(posModule.getX() - this.com.getX(), 2)
                    + Math.pow(posModule.getY() - this.com.getY(), 2);
            axisZdistance = Math.sqrt(axisZdistance);
            double axisXdistance = Math.pow(posModule.getY() - this.com.getY(), 2)
                    + Math.pow(posModule.getZ() - this.com.getZ(), 2);
            axisXdistance = Math.sqrt(axisXdistance);
            double axisYdistance = Math.pow(posModule.getX() - this.com.getX(), 2)
                    + Math.pow(posModule.getZ() - this.com.getZ(), 2);
            axisYdistance = Math.sqrt(axisYdistance);

            Ix += axisXdistance * moduleSet.getModulesMass(moduleType[module]);
            Iy += axisYdistance * moduleSet.getModulesMass(moduleType[module]);
            Iz += axisZdistance * moduleSet.getModulesMass(moduleType[module]);

        }
        inertia = new Vector3D(Ix, Iy, Iz);

        if (SimulationConfiguration.isDebug()) {
            System.out.println(
                    "min_pos: " + this.minPos + "; max_pos: " + this.maxPos + "; dimensions: " + this.dimensions);
            System.out.println("Total mass of the robot: " + this.robotMass + "; cdm: " + this.com);
        }

        initialHeight = (Math.abs(this.minPos.z) + 0.01);

        for (int i = 0; i < typePercentage.length; i++) {
            typePercentage[i] /= nModules;
        }

    }

    private void treeAnalysis(TreeIndividual tree) {
        List<Node> nodes = tree.getListNode();

        int count = 0;
        for (Node node : nodes) {
            moduleType[count] = node.getType();

            if (node.getDad() != null) {
                dadFace[count - 1] = node.getDad().getConnection(node).getDadFace();
                childOrientation[count - 1] = node.getDad().getConnection(node).getChildrenOrientation();
            }

            amplitudeControl[count] = node.getControlAmplitude();
            angularFreqControl[count] = node.getControlAngularFreq();
            phaseControl[count] = (int) node.getControlPhase();

            if (node.getDad() != null) {
                parentModule[count] = node.getDad().getConstructionOrder();
            } else {
                parentModule[count] = 0;
            }
            count++;
        }

    }

    private void chromosomeAnalysis() {

        for (int i = 0; i < nModulesMax; i++) {
            moduleType[i] = chromosomeInt[i];
        }
        int[] connections;
        connections = new int[nModulesMax - 1];
        for (int i = 0; i < (nModulesMax - 1); i++) {
            connections[i] = chromosomeInt[nModulesMax + i];
        }

        for (int i = 0; i < (nModulesMax - 1); i++) {
            dadFace[i] = chromosomeInt[2 * nModulesMax - 1 + i];
        }

        for (int i = 0; i < (nModulesMax - 1); i++) {
            childOrientation[i] = chromosomeInt[3 * nModulesMax - 2 + i];
        }

        if (SimulationConfiguration.isDebug()) {
            String strType = "Module Type: ";
            String strConn = "Connections: ";
            String strDadface = "\nParent´s face: ";
            String strOrientation = "\nOrientation: ";
            for (int i = 0; i < nModulesMax; i++) {
                strType += moduleType[i] + " ";
                if (i != (nModulesMax - 1)) {
                    strConn += connections[i] + " ";
                    strDadface += dadFace[i] + " ";
                    strOrientation += childOrientation[i] + " ";
                }
            }
            System.out.println(strType);
            System.out.println(strConn);
            System.out.println(strDadface);
            System.out.println(strOrientation + "\n");
        }

        if ((chromosomeInt.length + 3) % 9 == 0) {

            // Control parameters of the amplitude
            for (int i = 0; i < (nModulesMax); i++) {
                this.amplitudeControl[i] = chromosomeDouble[4 * nModulesMax - 3 + i];
            }

            // Control parameters of the angular frequency
            for (int i = 0; i < (nModulesMax); i++) {
                this.angularFreqControl[i] = chromosomeDouble[5 * nModulesMax - 3 + i];
            }

            // Control parameters of phase
            for (int i = 0; i < (nModulesMax); i++) {
                this.phaseControl[i] = chromosomeInt[6 * nModulesMax - 3 + i];
            }

            // Control parameters of the amplitude modulator
            for (int i = 0; i < (nModulesMax); i++) {
                // this.amplitudeModulation[i] = chromosomeDouble[7 * nModulesMax - 3 + i];
            }

            // Control parameters of the frequency modulator
            for (int i = 0; i < (nModulesMax); i++) {
                // this.frequencyModulation[i] = chromosomeDouble[8 * nModulesMax - 3 + i];
            }

        } else {
            for (int i = 0; i < (nModulesMax); i++) {
                phaseControl[i] = chromosomeInt[4 * nModulesMax - 3 + i];
            }

            if (chromosomeInt.length > 5 * nModulesMax - 3) {
                for (int i = 0; i < (nModulesMax); i++) {
                    // frequencyModulation[i] = chromosomeDouble[5 * nModulesMax - 3 + i];
                }
            }

        }

        // Calculating the level of each module
        int[] levelModuleNumber = new int[nModulesMax];
        levelModuleNumber[0] = 1;
        int i = 0;

        nModules = 1;
        int[] level;
        level = new int[nModulesMax];
        for (int currentLevel = 0; currentLevel < nModulesMax; currentLevel++) {
            for (int j = 0; j < levelModuleNumber[currentLevel] && i < nModulesMax; j++) {
                level[i] = currentLevel;
                if (i < nModulesMax - 1) // To avoid the arry overflow
                {
                    levelModuleNumber[currentLevel + 1] += connections[i];
                    nModules += connections[i];
                }
                i++;
            }

        }
        if (nModules > nModulesMax) {
            nModules = nModulesMax;
        }

        // calculate the parent module
        int k = 0;
        for (int ii = 0; ii < nModules - 1; ii++) {
            for (int j = 0; j < connections[ii] && k < (nModules - 1); j++) {
                parentModule[1 + k++] = ii;
            }
        }

        // calculate connection features
        calculateConnectionFeatures(connections);
        calculateLimbFeatures(connections);

    }

    private void calculateConnectionFeatures(int[] connections) {

        int[] moduleConnections = new int[nModules];
        moduleConnections[0] = connections[0];
        // If there is more than one module, add the missing connection to the other
        // modules
        if (this.nModules > 1) {
            for (int i = 1; i < nModules - 1; i++) {
                moduleConnections[i] = connections[i] + 1;
            }
            moduleConnections[nModules - 1] = 1;
        }
        this.averageConnectionsPerModule = this.average(moduleConnections);
        this.dispersionConnectionsPerModule = this.standardDeviation(moduleConnections,
                this.averageConnectionsPerModule);
    }

    private void calculateLimbFeatures(int[] connections) {
        // TODO: Check
        // int[] moduleConnections = new int[nModules];
        int modulesAttachedInOneFace = 0;
        int modulesAttachedInTwoFaces = 0;
        if (this.nModules > 1) {
            for (int i = 1; i < nModules - 1; i++) {
                if (connections[i] == 0) {
                    modulesAttachedInOneFace++;
                }
                if (connections[i] == 1) {
                    modulesAttachedInTwoFaces++;
                }
            }
        } else {
            modulesAttachedInOneFace = 1;
        }
        // System.err.println("modulesAttachedInOneFace "+modulesAttachedInOneFace);
        // System.err.println("modulesAttachedInTwoFaces "+modulesAttachedInTwoFaces);

        this.limbDescriptor = (double) modulesAttachedInOneFace / nModules;
        this.limbLengthDescriptor = (double) modulesAttachedInTwoFaces / nModules;
    }

    private double average(int[] vector) {
        double average = 0;
        for (int i = 0; i < this.nModules; i++) {
            average += vector[i];
        }
        return average / this.nModules;
    }

    private double standardDeviation(int[] vector, double mean) {
        double deviation = 0;
        for (int i = 0; i < this.nModules; i++) {
            deviation += Math.pow(vector[i] - mean, 2);
        }
        return Math.sqrt(deviation) / this.nModules;
    }

    public double[] getAmplitudeControl() {
        return amplitudeControl;
    }

    public double[] getAngularFreqControl() {
        return angularFreqControl;
    }

    public int[] getPhaseControl() {
        return phaseControl;
    }

    public int[] getModuleType() {
        return moduleType;
    }

    public int[] getParentModule() {
        return parentModule;
    }

    public int[] getDadFace() {
        return dadFace;
    }

    public int[] getChildOrientation() {
        return childOrientation;
    }

    public Vector3D[] getModulePosition() {
        return modulePosition;
    }

    public Rotation[] getModuleRotation() {
        return moduleRotation;
    }

    public int getnModules() {
        return nModules;
    }

    public Point3d getMinPos() {
        return minPos;
    }

    public Point3d getMaxPos() {
        return maxPos;
    }

    public Point3d getDimensions() {
        return dimensions;
    }

    public Vector3D getCom() {
        return com;
    }

    public double getRobotMass() {
        return robotMass;
    }

    public double getAverageConnectionsPerModule() {
        return averageConnectionsPerModule;
    }

    public double getLimbDescriptor() {
        return limbDescriptor;
    }

    public double getLimbLengthDescriptor() {
        return limbLengthDescriptor;
    }

    public double getDispersionConnectionsPerModule() {
        return dispersionConnectionsPerModule;
    }

    public Vector3D getInertia() {
        return inertia;
    }

    public double[] getTypePercentage() {
        return typePercentage;
    }

    public double getCoverage() {
        return coverage;
    }

    public double getXYProportion() {
        return dimensions.x / dimensions.y;
    }

    public double getZProportion() {
        double baseLenght = dimensions.x * dimensions.x + dimensions.y * dimensions.y;
        baseLenght = Math.sqrt(baseLenght);
        return dimensions.z / baseLenght;
    }
}
