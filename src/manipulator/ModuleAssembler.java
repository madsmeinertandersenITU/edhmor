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
package manipulator;

import apriltag.TagUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import manipulator.test.ManipulatorConnectorBayTest;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimulator;
import modules.util.PhysicalSetupConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * ModuleAssembler created on Nov 4, 2022
 *
 * @author Andres Faiña <anfv at itu.dk>
 */
public class ModuleAssembler {

    private ArenaCamera arenaCamera = null;
    private ManipulatorUR5 ur5 = null;
    private CoppeliaSimulator coppeliaSim = null;
    private int maxAttempts = 3;

    public ModuleAssembler(ManipulatorUR5 robot, ArenaCamera arenaCam) {
        this.ur5 = robot;
        this.arenaCamera = arenaCam;

        if (arenaCamera == null) {
            System.err.println("arenaCamera is null in ModuleAssembler!");
            System.exit(-1);
        }
        if (robot == null) {
            System.err.println("manipulator is null in ModuleAssembler!");
            System.exit(-1);
        }
    }

    public ModuleAssembler(ManipulatorUR5 robot, ArenaCamera arenaCam, CoppeliaSimulator sim) {
        this(robot, arenaCam);
        this.coppeliaSim = sim;
        if (coppeliaSim == null && PhysicalSetupConfiguration.isSimulation()) {
            System.err.println("simulator is null in ModuleAssembler and we are simulating!");
            System.exit(-1);
        }
    }

    /**
     * Grasps a module on the arena. Requires that the end-effector is not
     * holding any modules.
     *
     * @param tagID the tagID of the module that will be used to grasp the
     * module
     * @param fromDistance the distance from the tag where the robot will follow
     * the normal direction of the tag
     * @param safetyDistance the distance to stop from the tag. In a perfect
     * system, it should be 0.
     * @return
     */
    public boolean graspEmergeModuleFromTag(int tagID, double fromDistance, double safetyDistance) {

        boolean simulation = PhysicalSetupConfiguration.isSimulation();
        int attempt = 0;
        while (++attempt < maxAttempts) {
            System.out.println("starting graspEmergeModuleFromTag. Attempt " + attempt);

            //Take a picture and see if there is the tag to grasp 
            if (arenaCamera.capture(tagID)) {
                //Try to move the robot to grasp the module
                boolean success = ur5.move2PoseFromDistance(arenaCamera.getTagTranslation_world(tagID),
                        arenaCamera.getUR5Rotation_world(tagID),
                        arenaCamera.getTagRotation_world(tagID), fromDistance, safetyDistance).isSuccess();

                if (!success) {
                    System.out.println("graspEmergeModuleFromTag: We could not move to the tag, homing.");
                    ur5.home();
                    continue;
                }

                if (simulation) {
                    //The module is attached to the end-effector
                    coppeliaSim.pauseSimulation();
                    coppeliaSim.recursiveConnectObjectTo(TagUtils.getCoppeliaObject(tagID), "UR5_connection");
                    coppeliaSim.startSimulation();
                }

                //move the robot to the home position
                ur5.home();

                //Check that the module is not on the arena
                if (!arenaCamera.capture(TagUtils.getAllFaceTagIDsFromSingleTagID(tagID))) {
                    //We have NOT found any of the tags that belog to this module.
                    return true;
                } else {
                    System.out.println("graspEmergeModuleFromTag: We have found some of the tags that belog to this module on the arena, grasping failed.");
                }

            } else {
                System.out.println("graspEmergeModuleFromTag: The tag for grasping the module was not found. We cannot grasp it, homing.");
                ur5.home();
                sleep(100); //Small delay to get different light confitions
            }
        }
        return false;
    }

    /**
     * Releases a module that is on the end-effector of the manipulator at the
     * connection bay.
     *
     * @param tagID the tagID that the manipulator has grasped
     * @param bay the connector at the bay to release the module
     * @return true if the operation has been successful
     */
    public boolean releaseEmergeModuleAtBay(int tagID, int bay) {

        
        Rotation rotBay = ConnectionBay.getBayRotation();
        Vector3D posEndEffector = ConnectionBay.getEndEffectorPos(tagID, bay);
        //To grasp a connector, we need the rotation facing the tag
        Rotation ur5rotation;
        if(TagUtils.isTagOppositeFromMale(tagID)) {
            ur5rotation = new Rotation(new Vector3D(1, 0, 0), Math.PI/2);    
        } else {
            ur5rotation = new Rotation(new Vector3D(1, 0, 0), Math.PI);
        }

        boolean simulation = PhysicalSetupConfiguration.isSimulation();
        int attempt = 0;
        while (++attempt < maxAttempts) {
            ur5.home();
            
//            System.out.println("releaseEmergeModuleAtBay: Rotate module to face bay.");
//            //For face 1 we have to rotate the module 180 degrees around z
//            Pose currPose = ur5.getPose().getPose();
//            Rotation currRot = ManipulatorUR5.orient2rot(currPose.getOrientation());
              Rotation rot = new Rotation(new Vector3D(0,0,1), Math.PI/2 );
//            Rotation newRot = currRot.applyTo(rot);
//            ur5.move2Pose(currPose.getPositionVector(), newRot);
//            
//            sleep(1000);
            

            //We assume that the emerge module is on the end-effector.
            //Now, we go to place it in the connector bay
            boolean success = ur5.move2PoseFromDistance(posEndEffector,
                    rot.applyTo(ur5rotation),
                    rotBay, 0.2, 0.003).isSuccess();
            
            if (!success){
                continue;
            }
            
            //Calculate point of the edge
            Vector3D edgePoint = ConnectionBay.getDistanceToBreak(tagID, bay);
            Vector3D edgeAxis = ConnectionBay.getAxisToBreak(tagID);
           

            
           
            
            if (simulation) {
                //TODO: Attach the module to the connection bay
                //coppeliaSim.pauseSimulation();
                //coppeliaSim.recursiveConnectObjectTo(TagUtils.getCoppeliaObject(tagID), "UR5_connection");
                //or
                //coppeliaSim.connectObjectTo("tag" + tag ,null);
                //coppeliaSim.moveObjectTo("point1", edgePoint, null);
                //coppeliaSim.startSimulation();
            }
            
            //TODO: Check that movement is feasible, if not use other movements
            ur5.arcMove(edgeAxis, edgePoint, Math.PI * 0.25, coppeliaSim);
            
            //sleep(500);
            
            System.out.println("releaseEmergeModuleAtBay: Moving up.");
            success = ur5.move2Pose(ur5.getPose().getPose().getPositionVector().add(new Vector3D(0,0,0.05)))
                            .isSuccess();
            
            
            //Take a picture and see if there are tags found
            if (arenaCamera.capture(TagUtils.getAllFaceTagIDsFromSingleTagID(tagID))) {
                //We found the tag on the connection bay
                //TODO: Check that is on the connection bay and not in the arena
                ur5.home();
                return true;
            }
        }
        return false;
    }

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ex) {
			Logger.getLogger(ManipulatorConnectorBayTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Grasps a module that is on the connection bay and releases the module from
	 * it.
	 *
	 * @param tagID the tagID that the manipulator has grasped
	 * @param bay   the connector at the bay to release the module
	 * @param fromDistance the distance normal to the tag before approaching
	 * @param safetyDistance a safety distance from the tag to prevent collisions
	 * @return true if the operation has been successful
	 */
	public boolean graspEmergeModuleFromBay(int tagID, int bay, double fromDistance, double safetyDistance) {

		Rotation rotBay = ConnectionBay.getBayRotation();
		Vector3D posEndEffector = ConnectionBay.getEndEffectorPos(tagID, bay);
		boolean simulation = PhysicalSetupConfiguration.isSimulation();
		int attempt = 0;
		while (++attempt < maxAttempts) {
			System.out.println("starting graspEmergeModuleFromBay. Attempt " + attempt);
			ur5.home();

			// Take a picture and see if there is the tag to grasp
			if (arenaCamera.capture(tagID)) {
				// Try to move the robot to grasp the module
				System.out.println("graspEmergeModuleFromTag: Tag found moving to module bay.");
				Rotation rot = new Rotation(new Vector3D(0,0,1), -Math.PI/2 );
				boolean success = ur5
						.move2PoseFromDistance(posEndEffector, rot.applyTo(ManipulatorUR5.orient2rot(ManipulatorUR5.FACING_DOWN)),
								ManipulatorUR5.orient2rot(ManipulatorUR5.FACING_UP), fromDistance, safetyDistance)
						.isSuccess();

				if (!success) {
					System.out.println("graspEmergeModuleFromBay: We could not move to the tag, homing.");
					ur5.home();
					continue;
				}
				
				
				System.out.println("graspEmergeModuleFromTag: Rotating module out of bay.");
				//Calculate point of the edge
			            Vector3D edgePoint = ConnectionBay.getBayPosition(bay).add(new Vector3D(0,0,-EmergeDimensions.CONN2EDGE_DISTANCE));//(0,0,-0.0291)
			            Vector3D edgeAxis = ConnectionBay.getBayHorizontalBreakAxis();
			            
			            //TODO: Check that movement is feasible, if not use other movements
			            ur5.arcMove(edgeAxis, edgePoint, Math.PI * 0.08333, coppeliaSim);
			            
			        
				System.out.println("graspEmergeModuleFromTag: Moving module out of bay.");
				success = ur5.move2Pose(ur5.getPose().getPose().getPositionVector().add(new Vector3D(0,0.2,0)))
						.isSuccess();
				
				if (!success) {
					System.out.println("graspEmergeModuleFromBay: We could not separate the module from the bay, homing.");
					ur5.home();
					continue;
				}

			} else {
				System.out.println(
						"graspEmergeModuleFromBay: The tag for grasping the module was not found. We cannot grasp it, homing.");
				ur5.home();
				sleep(100); // Small delay to get different light confitions
			}

			// move the robot to the home position
			ur5.home();

			// Check that the module is not on the bay
			if (!arenaCamera.capture(TagUtils.getAllFaceTagIDsFromSingleTagID(tagID))) {
				// We have NOT found any of the tags that belog to this module.
				return true;
			} else {
				System.out.println(
						"graspEmergeModuleFromBay: We have found some of the tags that belong to this module on the bay, grasping failed.");
			}

		}
		return false;
	}
        
     /**
     * Releases a module that is on the end-effector of the manipulator at the
     * arena (not connected to any other modules).
     *
     * @param tagID the tagID that the manipulator has grasped
     * @return true if the operation has been successful
     */
    public boolean releaseEmergeModuleAtArena(int tagID, Vector3D pos, double angle) {

        
        //To grasp a connector, we need the rotation facing the tag
        Rotation ur5rotation;

        double zPos;
        if(TagUtils.isTagOppositeFromMale(tagID)) {
            ur5rotation = ManipulatorUR5.orient2rot(ManipulatorUR5.FACING_FRONT);
            zPos = EmergeDimensions.FEMALE2FEMALE_HEIGHT/2+PhysicalSetupConfiguration.getTableHeight();
        } else {
            ur5rotation = ManipulatorUR5.orient2rot(ManipulatorUR5.FACING_DOWN);
            zPos = EmergeDimensions.FEMALE2FEMALE_HEIGHT+PhysicalSetupConfiguration.getTableHeight();
        }
        
        System.out.println("zPos going down: "+zPos);
        Vector3D endEffectorPos = new Vector3D(pos.getX(), pos.getY(), zPos); 

        boolean simulation = PhysicalSetupConfiguration.isSimulation();
        int attempt = 0;
        while (++attempt < maxAttempts) {
            ur5.home();
            
            //TODO: Rotate end effector the angle
            
            //We assume that the emerge module is on the end-effector.
            //Now, we go to place it in the connector bay
            boolean success = ur5.move2PoseFromDistance(endEffectorPos,
                    ur5rotation,
                    ManipulatorUR5.orient2rot(ManipulatorUR5.FACING_UP), 
                    0.2, 0.003).isSuccess();
            
            if (!success){
                continue;
            }
            
            System.out.println("Moving down:");
            System.out.println("HEIGHT\tFORCE");
            double force = ur5.getForceSensors().getWrench().getForce().getZ();
            double filteredForce = force;
            
            while(Math.abs(filteredForce) < 25) {
                ur5.move2RelativePose(new Vector3D(0, 0, -0.0005));
                force = ur5.getForceSensors().getWrench().getForce().getZ();
                filteredForce = 0.8*filteredForce + 0.2*force;
                System.out.println(ur5.getPose().getPose().getPosition().getZ()+"\t"+force+"\t"+filteredForce);
            }
            
            sleep(2000);
            
            //Calculate point of the edge
            Rotation rot = new Rotation(new Vector3D(0, 0, 1), angle);
            Vector3D edgePoint = rot.applyTo(new Vector3D(EmergeDimensions.CONN2EDGE_DISTANCE, 0, -0.004)).add(ur5.getPose().getPose().getPositionVector()) ;
            Vector3D edgeAxis = rot.applyTo(new Vector3D(0, -1, 0));
           
            if (simulation) {
                //TODO: Attach the module to the connection bay
                //coppeliaSim.pauseSimulation();
                //coppeliaSim.recursiveConnectObjectTo(TagUtils.getCoppeliaObject(tagID), "UR5_connection");
                //or
                //coppeliaSim.connectObjectTo("tag" + tag ,null);
                //coppeliaSim.moveObjectTo("point1", edgePoint, null);
                //coppeliaSim.startSimulation();
            }
            
            //TODO: Check that movement is feasible, if not use other movements
            ur5.arcMove(edgeAxis, edgePoint, Math.PI/180 * 40, coppeliaSim);
            
            sleep(3000);
            force = ur5.getForceSensors().getWrench().getForce().getZ();
            filteredForce = force;
            
            while(Math.abs(filteredForce) < 25) {
                ur5.move2RelativePose(new Vector3D(0, 0, -0.0005));
                force = ur5.getForceSensors().getWrench().getForce().getZ();
                filteredForce = 0.8*filteredForce + 0.2*force;
                System.out.println(ur5.getPose().getPose().getPosition().getZ()+"\t"+force+"\t"+filteredForce);
            }
            
            
            sleep(3000);
            
            edgePoint = rot.applyTo(new Vector3D(0, EmergeDimensions.CONN2EDGE_DISTANCE, 0.0)).add(ur5.getPose().getPose().getPositionVector()) ;
            edgeAxis = rot.applyTo(new Vector3D(1, 0, 0));
            ur5.arcMove(edgeAxis, edgePoint, Math.PI/180 * 40, coppeliaSim);
            
            //move up a few mm up
            ur5.move2RelativePose(new Vector3D(0, 0, 0.05));
            
            ur5.home();
            
            //Take a picture and see if there are tags found
            if (arenaCamera.capture(TagUtils.getAllFaceTagIDsFromSingleTagID(tagID))) {
                //We found the tag on the connection bay
                //TODO: Check that is on the connection bay and not in the arena
                ur5.home();
                return true;
            }
        }
        return false;
    }
}
