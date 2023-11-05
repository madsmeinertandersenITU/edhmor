/*
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2022 Andres Faiña <anfv at itu.dk> (ITU)
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
import apriltag.ImageResolution;
import apriltag.CoppeliaSimCapture;
import java.util.logging.Level;
import java.util.logging.Logger;
import manipulator.ArenaCamera;
import manipulator.ArenaCameraCalibrated;
import manipulator.ManipulatorUR5;
import manipulator.Orientation;
import modules.evaluation.CoppeliaSimulator;
import modules.util.PhysicalSetupConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.opencv.core.Core;
import org.visp.core.VpCameraParameters;

/**
 *
 * @author anfv
 */
public class ManipulatorReachTagTest {

    static {
        System.loadLibrary("visp_java341");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        boolean simulation = PhysicalSetupConfiguration.isSimulation();
        int tagToReach = 92;//Box:92

        Capture imageCapture;
        if (simulation) {
            CoppeliaSimulator coppeliaSim = new CoppeliaSimulator();
            coppeliaSim.connect2CoppeliaSim();
            imageCapture = new CoppeliaSimCapture(coppeliaSim, "Vision_sensor_arena");
            coppeliaSim.startSimulation();
        } else {
            imageCapture = new CameraCapture(CameraType.ARENA_CEILING_CAMERA);
        }

        //Create manipulator
        ManipulatorUR5 ur5 = new ManipulatorUR5();

        //Create the class of the camera that tracks the arena
        //ArenaCamera arenaCam = ArenaCameraCalibrated.calibratedCamera(imageCapture);
        ArenaCamera arenaCam = ArenaCameraCalibrated.calibratedCameraRobotTag(imageCapture,ur5, 5);

        //Start simulator and home robot
        ur5.home();
        sleep(1000);

        while (true) {
            //Take a picture and see if there are tags found
            arenaCam.capture();
            if (arenaCam.tagFound(tagToReach)) {

                System.out.println("Tag " + tagToReach + " found");

                ur5.getPose();

                //Move to each tag (10cm above the tag)
                //ur5.move2Pose(new Vector3D(-0.55, 0, 0.3), ManipulatorUR5.FACING_DOWN);
  
                //Calculate translation of the tag 
                Vector3D tagPose_world = arenaCam.getTagTranslation_world(tagToReach);
                System.out.println("tagPose_robot: " + tagPose_world.toString());

                //Calculate orientation of the tag
                Rotation rotTag_world = arenaCam.getTagRotation_world(tagToReach);
                Rotation rotUr5tag_world = arenaCam.getUR5Rotation_world(tagToReach);
//                System.out.println("tag_world: " + rot2str(tag_world));
                System.out.println("ur5tag_world: " + Orientation.rot2str(rotUr5tag_world));

                if (!ur5.move2PoseFromDistance(tagPose_world, rotUr5tag_world, rotTag_world, 0.1,0.007).isSuccess()) {
                    //ur5.move2Pose(tagPose_world.add(new Vector3D(0,0,0.4)));
                    //ur5.move2Pose(tagPose_world, ur5tag_world);
                }
                sleep(3000);

                ur5.home();
                sleep(3000);

            } else {
                System.out.println("No AprilTags detected");
            }
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(ManipulatorReachTagTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
