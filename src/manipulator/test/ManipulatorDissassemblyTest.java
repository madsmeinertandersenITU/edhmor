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

import apriltag.AprilTagDetector;
import apriltag.Capture;
import apriltag.TagUtils;
import apriltag.CoppeliaSimCapture;
import static apriltag.tests.CoppeliaSimTest.computeCentroid;
import static apriltag.tests.CoppeliaSimTest.displayFrame;
import static apriltag.tests.CoppeliaSimTest.displayLine;
import static apriltag.tests.CoppeliaSimTest.displayText;
import coppelia.remoteApi;
import java.awt.Color;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import manipulator.ArenaCamera;
import manipulator.ManipulatorUR5;
import manipulator.Orientation;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.evaluation.CoppeliaSimCreateRobotWId;
import modules.evaluation.CoppeliaSimulator;
import modules.util.ChromoConversion;
import modules.util.SimulationConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.opencv.core.Core;
import org.visp.core.VpHomogeneousMatrix;
import org.visp.core.VpImagePoint;
import org.visp.core.VpImageUChar;
import org.visp.core.VpQuaternionVector;
import org.visp.core.VpTranslationVector;

/**
 * ManipulatorDissassemblyTest.java 
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class ManipulatorDissassemblyTest {

    static {
        System.loadLibrary("visp_java341");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    

    public static void main(String[] args) {
        
        CoppeliaSimulator coppeliaSim = new CoppeliaSimulator();
        coppeliaSim.connect2CoppeliaSim();
        
        //Create manipulator
        ManipulatorUR5 ur5 = new ManipulatorUR5();
        
        
        double[] chromosomeDouble = ChromoConversion.str2double("[(0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, "
               + "                                                1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
               + "                                                0.0, 7.0, 2.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
               + "                                                1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
               + "                                                0.5455963591611365, 0.11371233636543576, 0.036290954196614544, 0.3903656017030154, 0.00679176572264828, 0.08146566559447732, 0.07535369694622207, 0.7730905441161025, 0.2500507281219915, 0.40313639025411774, 0.003973608347822122, 0.7781750911928096, 0.3899374167547881, 0.9600134640670561, 0.7250404836902793, 0.8445273695804674, 0.6467894800807368, 0.16030850687424925, 0.2018541954218076, 0.49567921698965856, 0.9019485291134028, 0.20829306079896515, 0.06612457688359719, 0.49610490207718816, 0.7488436794002526, 0.9645264481423355, 0.5163520317251132, 0.2457172378705993, 0.41377103759169165, 0.12842989434914487, 0.35238585296706193, 0.22133798867604992, 344.4679226211596, 309.281008589332, 266.8072439685561, 134.9338321211479, 12.159645687248428, 57.23409787318109, 232.8997112761175, 90.64154969188141, 121.74772800789312, 252.27177708222214, 66.17009812495084, 11.886793305793866, 11.792446546844415, 205.59952213863193, 79.29145620359417, 30.7604219708471, 0.4378857762257965, -0.4539603364820609, -0.018494123142167918, 0.49687190619165233, -0.43881005244237103, 0.39648466172584684, -0.304664078161781, -0.4413269361659312, 0.2201318672469349, -0.39377771322649235, -0.3754566640525431, -0.3969155009689903, 0.017185949030072756, -0.21401444995093033, 0.1949855550960401, -0.3794066650961495, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0] - 1.7976931348623157E308");
   	 //Set the correct module set to employ
        String moduleSet = "Emerge18AndFlatBaseModules";
        SimulationConfiguration.setModuleSet(moduleSet);
        ModuleSetFactory.reloadModuleSet();

        CoppeliaSimCreateRobot robot = new CoppeliaSimCreateRobotWId(coppeliaSim.getCoppeliaSimApi(), coppeliaSim.getClientID(), chromosomeDouble, "ur5Arena", false, false, 0);
        robot.createRobot();
                
        

        //Create the class of the camera that tracks the arena
        CoppeliaSimCapture coppeliaSimCapture = new CoppeliaSimCapture(coppeliaSim, "Vision_sensor_arena");
        ArenaCamera arenaCam = new ArenaCamera(coppeliaSimCapture);
        
        //Move the robot to the arena
        coppeliaSim.moveObjectTo("flatBaseID0", new Vector3D(0.55, 0, 0.1), null);
        
        //Start simulator and home robot
        ur5.home();
        sleep(1000);
        coppeliaSim.startSimulation();
        
        
        
        //Take a picture and see if there are tags found
        if (arenaCam.capture()) {

            int[] tagsId = arenaCam.getTagsId();

            System.out.println(tagsId.length + " tags found. ");
            for (int i = 0; i < tagsId.length; i++) {
                System.out.println("Tag " + tagsId[i] + " found");
            }
            
            ur5.getPose();

            for (int i = 0; i < tagsId.length; i++) {
                int tag = tagsId[i];
                System.out.println("Tag " + tag + ":");
                
                if(tag==0)
                    continue;
            
                ur5.move2Pose(new Vector3D(0.55, 0, 0.3),ManipulatorUR5.FACING_DOWN);
                
                //Calculate translation of the tag 
                Vector3D tagPose_camera = arenaCam.getTagTranslation_camera(tag);
                Vector3D tagPose_world = arenaCam.getTagTranslation_world(tag);
                
//                System.out.println("tagPose_camera: " + tagPose_camera.toString());
                System.out.println("tagPose_robot: " + tagPose_world.toString());
                
                //Calculate orientation of the tag
                Rotation rotTag_camera = arenaCam.getTagRotation_camera(tag);;
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
                
                //Calculate point of the edge

                Vector3D edgePoint = TagUtils.getDistanceToConnectionToBreak(tag);
                Vector3D edgeAxis = TagUtils.getAxisToConnectionToBreak(tag);
                Vector3D edgePoint_world = arenaCam.tagCS2worldCS(edgePoint, tag);
                Vector3D edgeAxis_world = arenaCam.getTagRotation_world(tag).applyInverseTo(edgeAxis);
                
                
                coppeliaSim.moveObjectTo("point1", edgePoint_world, null);
                System.out.println("edgePoint_world: " + edgePoint_world);
                System.out.println("edgeAxis_world: " + edgeAxis_world);
                
                coppeliaSim.pauseSimulation();
                coppeliaSim.recursiveConnectObjectTo(TagUtils.getCoppeliaObject(tag) ,"UR5_connection");
                coppeliaSim.startSimulation();
                
                //ur5.move2Pose(tagPose_world.add(new Vector3D(0,0.2,0.01)), rotUr5tag_world);
                //sleep(3000);
                //ur5.move2Pose(tagPose_world, rotUr5tag_world);
                sleep(3000);
                        
                ur5.arcMove(edgeAxis_world, edgePoint_world, Math.PI*0.5, coppeliaSim);
                //coppeliaSim.connectObjectTo("tag" + tag ,null);
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
            Logger.getLogger(ManipulatorDissassemblyTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
