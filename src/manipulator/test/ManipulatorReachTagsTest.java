/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manipulator.test;

import apriltag.CameraCapture;
import apriltag.CameraType;
import apriltag.Capture;
import apriltag.ImageResolution;
import apriltag.TagUtils;
import apriltag.CoppeliaSimCapture;
import java.util.logging.Level;
import java.util.logging.Logger;
import manipulator.ArenaCamera;
import manipulator.ArenaCameraCalibrated;
import manipulator.ManipulatorUR5;
import manipulator.Orientation;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.evaluation.CoppeliaSimCreateRobotWId;
import modules.evaluation.CoppeliaSimulator;
import modules.util.ChromoConversion;
import modules.util.PhysicalSetupConfiguration;
import modules.util.SimulationConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.opencv.core.Core;
import org.visp.core.VpCameraParameters;

/**
 *
 * @author anfv
 */
public class ManipulatorReachTagsTest {

    static {
        System.loadLibrary("visp_java341");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    

    public static void main(String[] args) {
        
        boolean simulation = PhysicalSetupConfiguration.isSimulation();
        int CAMERA_INDEX = PhysicalSetupConfiguration.getArenaCeilingCamIndex();
        

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
        ArenaCamera arenaCam = ArenaCameraCalibrated.calibratedCamera(imageCapture);
        
       
        
        //Start simulator and home robot
        ur5.home();
        sleep(1000);
        
        
        //Take a picture and see if there are tags found
        if (arenaCam.capture()) {

            int[] tagsId = arenaCam.getTagsId();

            System.out.println(tagsId.length + " tags found. ");
            for (int i = 0; i < tagsId.length; i++) {
                System.out.println("Tag " + tagsId[i] + " found");
            }
            
            ur5.getPose();

            for (int i = 0; i < tagsId.length; i++) {
                //Move to each tag (10cm above the tag)
                
                int tag = tagsId[i];
                System.out.println("Tag " + tag + ":");
                
                if(tag==0)
                    continue;
            
                ur5.move2Pose(new Vector3D(-0.55, 0, 0.3), ManipulatorUR5.FACING_DOWN);
                
                //Calculate translation of the tag 
                Vector3D tagPose_camera = arenaCam.getTagTranslation_camera(tag);
                Vector3D tagPose_world = arenaCam.getTagTranslation_world(tag);
                
//                System.out.println("tagPose_camera: " + tagPose_camera.toString());
                System.out.println("tagPose_robot: " + tagPose_world.toString());
                
                //Calculate orientation of the tag
                Rotation rotTag_camera = arenaCam.getTagRotation_camera(tag);
                Rotation rotTag_world = arenaCam.getTagRotation_world(tag);
                Rotation rotUr5tag_world = arenaCam.getUR5Rotation_world(tag);
//                System.out.println("tag_camera: " + rot2str(tag_camera));
//                System.out.println("tag_world: " + rot2str(tag_world));
                System.out.println("ur5tag_world: " + Orientation.rot2str(rotUr5tag_world));
                
               
                if (!ur5.move2PoseFromDistance(tagPose_world, rotUr5tag_world, rotTag_world, 0.1).isSuccess()){
                    //ur5.move2Pose(tagPose_world.add(new Vector3D(0,0,0.4)));
                    //ur5.move2Pose(tagPose_world, ur5tag_world);
                }
                sleep(3000);
                
                
                
                ur5.home();

            }
        } else {
            System.out.println("No AprilTags detected");
        }
        //coppeliaSim.stopSimulation();
        System.out.println("End!");

    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Logger.getLogger(ManipulatorReachTagsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
