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
package manipulator.test;

import apriltag.CameraCapture;
import apriltag.CameraType;
import apriltag.Capture;
import apriltag.TagUtils;
import apriltag.CoppeliaSimCapture;
import java.util.logging.Level;
import java.util.logging.Logger;
import manipulator.ArenaCamera;
import manipulator.ArenaCameraCalibrated;
import manipulator.EmergeDimensions;
import manipulator.ManipulatorUR5;
import manipulator.ModuleAssembler;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimulator;
import modules.util.PhysicalSetupConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.opencv.core.Core;

/**
 * ManipulatorConnectorBayTest.java Created on 03/11/2022
 *
 * @author Andres Faiña <anfv  at itu.dk>
 *
 * This test attempts to grasp one Emerge module from the arena with the
 * manipulator, and move it to one port of the connection bay. After that, the
 * robot attempts to take the module to the arena from the connection bay.
 *
 * Requires: ArenaCamera, ur5 connection bay and one Emerge module.
 */
public class ManipulatorConnectorBayTest {

    static {
        System.loadLibrary("visp_java341");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static ArenaCamera arenaCam;
    private static ManipulatorUR5 ur5;
    private static CoppeliaSimulator coppeliaSim = null;

    private enum STATE {
        GRASPING_MODULE_FROM_ARENA,
        RELEASING_MODULE_AT_BAY,
        GRASPING_MODULE_FROM_BAY,
        RELEASING_MODULE_AT_ARENA
    };

    public static void main(String[] args) {

        boolean simulation = PhysicalSetupConfiguration.isSimulation();
        int tagToReach = 10;//Box:92
        
        
        Capture imageCapture;
        if (simulation) {
            coppeliaSim = new CoppeliaSimulator();
            coppeliaSim.connect2CoppeliaSim();
            imageCapture = new CoppeliaSimCapture(coppeliaSim, "Vision_sensor_arena");
            coppeliaSim.startSimulation();
        } else {
            imageCapture = new CameraCapture(CameraType.ARENA_CEILING_CAMERA);
        }

        //Create manipulator
        ur5 = new ManipulatorUR5();

        //Create the class of the camera that tracks the arena
        //ArenaCamera arenaCam = ArenaCameraCalibrated.calibratedCamera(imageCapture);
        arenaCam = ArenaCameraCalibrated.calibratedCameraRobotTag(imageCapture, ur5, 1);

        //Create module assembler
        ModuleAssembler moduleAssembler = new ModuleAssembler(ur5, arenaCam, coppeliaSim);

        //TODO: Move the emerge module to a random position in the arena
        //if (simulation) {
        //coppeliaSim.moveObjectTo("flatBaseID0", new Vector3D(0.55, 0, 0.1), null);
        //}
        //home robot
        ur5.home();
        sleep(1000);

        STATE state = STATE.GRASPING_MODULE_FROM_BAY;
        while (true) {

            switch (state) {
                case GRASPING_MODULE_FROM_ARENA:
                    System.out.println("Attempting ro graspEmergeModuleFromTag.");
                    if (moduleAssembler.graspEmergeModuleFromTag(tagToReach, 0.1, 0.007)) {
                        state = STATE.RELEASING_MODULE_AT_ARENA;
                    }
                    break;
                case RELEASING_MODULE_AT_BAY:
                    System.out.println("Attempting releaseModuleAtBay.");
                    if (moduleAssembler.releaseEmergeModuleAtBay(tagToReach, 1)) {
                        state = STATE.GRASPING_MODULE_FROM_BAY;
                    }
                    break;
                case GRASPING_MODULE_FROM_BAY:
                    System.out.println("Attempting graspEmergeModuleFromBay.");
                    if (moduleAssembler.graspEmergeModuleFromBay(tagToReach, 1, 0.1, 0.003)) {
                        state = STATE.RELEASING_MODULE_AT_ARENA;
                    }
                    break;
                case  RELEASING_MODULE_AT_ARENA:
                    System.out.println("Attempting releaseEmergeModuleAtArena.");
                    Vector3D pos = new Vector3D(-0.58, 0.0, 0);
                    if (moduleAssembler.releaseEmergeModuleAtArena(tagToReach, pos, 0.0)) {
                        state = STATE.GRASPING_MODULE_FROM_ARENA;
                    }
                    break;

            }
            sleep(5000);

        }

    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);

        } catch (InterruptedException ex) {
            Logger.getLogger(ManipulatorConnectorBayTest.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
