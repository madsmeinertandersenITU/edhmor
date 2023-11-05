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

import com.google.gson.Gson;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modules.evaluation.CoppeliaSimulator;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author anfv
 */
public class ManipulatorUR5 {

    final static Vector3D ROBOT_TRANSLATION = new Vector3D(0, 0, 0.0);

    private final Gson g = new Gson();
    private final Service robot_command;

    public static final Orientation FACING_DOWN = new Orientation(0.0, 0.0, -1.0, 0.0);
    public static final Orientation FACING_UP = new Orientation(1.0, 0.0, 0.0, 0.0);
    public static final Orientation FACING_LEFT = new Orientation(0.71, 0.71, 0.0, 0.0);
    //public static final Orientation FACING_RIGHT = new Orientation(0.71, -0.71, 0.0, 0.0);
    public static final Orientation FACING_FRONT = new Orientation(0.5, 0.5, 0.5, 0.5);

    public ManipulatorUR5() {
        Ros ros = new Ros("localhost");
        ros.connect();
        if (!ros.isConnected()) {
            System.err.println("We could not connect to ROS server, check that ROS is running");
            System.exit(-1);
        }
        robot_command = new Service(ros, "/robot_poseArray", "testing_ur5/robot_poseArray");
    }

    public ROSResponse move2Pose(Vector3D target) {
        return move2Pose(target, getPose().getPose().getOrientation());
    }
    
    public ROSResponse move2RelativePose(Vector3D relPos) {
        //move to a relative pose
        return move2Pose(getPose().getPose().getPositionVector().add(relPos));
    }

    public ROSResponse move2Pose(Vector3D target, Orientation orient) {
        System.out.println("Moving to pose...");
        Vector3D target_robot = target.subtract(ROBOT_TRANSLATION);
        Pose targetPose = new Pose(new Position(target_robot.getX(), target_robot.getY(), target_robot.getZ()), orient);

        ROSRequest request = new ROSRequest("cartesian", new Pose[]{targetPose});
        ROSResponse resp = runRequest(request);
        System.out.println("Moving to pose finished");
        return resp;
    }

    public ROSResponse move2Pose(Vector3D target, Rotation rot) {
        return move2Pose(target, rot2Orient(rot));
    }

    public ROSResponse move2PoseFromDistance(Vector3D target, Rotation rot, Rotation tagRot, double dist) {
        System.out.println("Moving to pose from distance...");
        Vector3D zDist = new Vector3D(0, 0, dist);
        Vector3D zDist_world = tagRot.applyInverseTo(zDist).add(target);
        List<Vector3D> targets = new ArrayList();
        System.out.println("First point : " + zDist_world);
        System.out.println("Second point : " + target);
        targets.add(zDist_world);
        targets.add(target);
        ROSResponse resp = move2Pose(targets, rot);
        System.out.println("Moving to pose from distance finished");
        return resp;
    }

    public ROSResponse move2PoseFromDistance(Vector3D target, Rotation rot, Rotation tagRot, double dist, double safetyZDist) {
        Vector3D safetyDist = new Vector3D(0, 0, safetyZDist);
        Vector3D zDist_world = tagRot.applyInverseTo(safetyDist).add(target);
        return this.move2PoseFromDistance(zDist_world, rot, tagRot, dist);
    }

    public ROSResponse arcMove(Vector3D axis, Vector3D point, double angle, CoppeliaSimulator coppeliaSim) {
        System.out.println("Arc movement...");
        int segments = 10;
        Pose currPose = getPose().getPose();
        Vector3D currPos = currPose.getPositionVector().add(ManipulatorUR5.ROBOT_TRANSLATION);
        Vector3D distPos = currPos.subtract(point);
        System.out.println("currPos " + currPos);
        Rotation currRot = orient2rot(currPose.getOrientation());
        List<Rotation> rots = new ArrayList();
        List<Vector3D> poses = new ArrayList();
        for (int i = 1; i <= segments; i++) {
            double newAngle = i / ((float) segments) * angle;
            Rotation rot = new Rotation(axis, newAngle);
            Rotation newRot = currRot.applyTo(rot);
            rots.add(newRot);
            Vector3D newPos = rot.applyInverseTo(distPos).add(point);
            //System.out.println("newAngle " + newAngle);
            System.out.println("newPos " + newPos);
            poses.add(newPos);
            move2Pose(newPos,newRot);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (coppeliaSim != null) {
                //we revert the orientation to show the right orientation in CoppeliaSim
                //not sure what the issue is, maybe the euler angles  
                coppeliaSim.moveObjectTo("test" + (i - 1), newPos, newRot.revert());
            }
        }
//        ROSResponse resp = move2Pose(poses, rots);
        System.out.println("Arc movement finished");
        return null;
    }

    public ROSResponse move2Pose(List<Vector3D> targets, Rotation rot) {
        return move2Pose(targets, Collections.nCopies(targets.size(), rot));
    }

    public ROSResponse move2Pose(List<Vector3D> targets, List<Rotation> rots) {
        return move2PoseList(targets, rot2Orient(rots));
    }

    public ROSResponse move2PoseList(List<Vector3D> target, List<Orientation> orient) {
        System.out.println("Moving to poses...");
        if (target.size() != orient.size()) {
            System.err.println("The number of positions and roations do not match!");
            System.exit(-1);
        }
        Pose[] poses = new Pose[target.size()];
        for (int i = 0; i < target.size(); i++) {
            Vector3D target_robot = target.get(i).subtract(ROBOT_TRANSLATION);
            poses[i] = new Pose(new Position(target_robot.getX(), target_robot.getY(), target_robot.getZ()), orient.get(i));
        }

        ROSRequest request = new ROSRequest("cartesian", poses);
        ROSResponse resp = runRequest(request);
        System.out.println("Moving to poses finished");
        return resp;
    }

    public ROSResponse home() {
        System.out.println("Homming...");
        ROSRequest request = new ROSRequest("home", new Pose[]{});
        ROSResponse resp = runRequest(request);
        System.out.println("Homming finished");
        return resp;
    }

    public ROSResponse calibrationHome() {
        System.out.println("Homming to calibration position...");
        ROSRequest request = new ROSRequest("calibrationHome", new Pose[]{});
        ROSResponse resp = runRequest(request);
        System.out.println("Homming to calibration position finished");
        return resp;
    }

    public ROSResponse getPose() {
        System.out.println("Getting pose...");
        ROSRequest request = new ROSRequest("get_pose", new Pose[]{});
        ROSResponse resp = runRequest(request);
        System.out.println("Getting pose finished: Pose " + resp.getPose());
        return resp;
    }
    
    public ROSResponse getForceSensors() {
        System.out.println("Getting force sensors...");
        ROSRequest request = new ROSRequest("get_force_sensors", new Pose[]{});
        ROSResponse resp = runRequest(request);
        System.out.println("Getting force sensors finished: Sensors " + resp.getWrench());
        return resp;
    }
    
    public ROSResponse moveDownToTouch() {
        System.out.println("Moving down until touching something...");
        ROSRequest request = new ROSRequest("down_to_touch", new Pose[]{});
        ROSResponse resp = runRequest(request);
        System.out.println("Moving down until touching finished");
        return resp;
    }

    private ROSResponse runRequest(ROSRequest requestObject) {
        String requestJson = g.toJson(requestObject);
        ServiceRequest request = new ServiceRequest(requestJson);
        ServiceResponse response = robot_command.callServiceAndWait(request);
        ROSResponse responseObject = g.fromJson(response.toString(), ROSResponse.class);
        String str = "Response from server: message: " + responseObject.getMessage() + ", success: " + responseObject.isSuccess() + ", fraction: " + responseObject.getFraction();
        if (responseObject.isSuccess()) {
            //System.out.println(str);
        } else {
            System.err.println(str);
        }
        return responseObject;
    }

    public static Orientation rot2Orient(Rotation rot) {
        return new Orientation(rot.getQ0(), rot.getQ1(), rot.getQ2(), rot.getQ3());
    }

    public static Rotation orient2rot(Orientation or) {
        return new Rotation(or.getW(), or.getX(), or.getY(), or.getZ(), false);
    }

    private List<Orientation> rot2Orient(List<Rotation> rots) {
        List<Orientation> orients = new ArrayList();
        for (Rotation rot : rots) {
            orients.add(rot2Orient(rot));
        }
        return orients;
    }
}
