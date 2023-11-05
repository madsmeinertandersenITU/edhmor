/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manipulator;

import apriltag.AprilTagDetector;
import apriltag.AprilTagFamily;
import apriltag.CameraCapture;
import apriltag.Capture;
import apriltag.ImageUtils;
import apriltag.CoppeliaSimCapture;
import modules.util.PhysicalSetupConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.opencv.core.Mat;
import org.visp.core.VpImageUChar;
import org.opencv.highgui.HighGui;

/**
 *
 * @author anfv
 */
public class ArenaCamera {

    final static Vector3D DEFAULT_TRANSLATION = new Vector3D(-0.60, 0.1, 1.000);
    final static Rotation DEFAULT_ROTATION = new Rotation(new Vector3D(1, 0, 0), Math.PI);
    private Vector3D cameraArenaTranslation = DEFAULT_TRANSLATION;
    private Rotation cameraArenaRotation = DEFAULT_ROTATION;
    private Capture capture;
    private static AprilTagDetector aprilTagDetector;

    public ArenaCamera(Vector3D trans, Rotation rot, Capture cap) {
        this.cameraArenaTranslation = trans;
        this.cameraArenaRotation = rot;
        this.capture = cap;
    }

    public ArenaCamera(Capture cap) {
        this(DEFAULT_TRANSLATION, DEFAULT_ROTATION, cap);
    }
    
    public boolean capture() {
    	//FIXME: The real tags are 0.032m (black tag, white edge is not part of tne tag)
        // but in simulation the tags are 0.04m (black tag, white edge is not part of tne tag)
        // In the simulator the texture is 0.05m as includes the white edge!
    	boolean sim = capture instanceof CoppeliaSimCapture;
    	double tagSize = sim? 0.04: 0.032;
    	return this.capture(tagSize);   	
    }
    
    public boolean capture(double tagSize) {
        capture.capture();
        
//        Mat frame = capture.getMat();
//        ImageUtils.paintTags(frame, capture.getCamParam());
//        HighGui.imshow("Image", frame);
//        HighGui.waitKey();

        
        
        aprilTagDetector = new AprilTagDetector(capture.getI(), AprilTagFamily.TAG_36h11, capture.getCamParam(), tagSize);
        return aprilTagDetector.detect();
    }

    public boolean capture(int tag) {
        if (capture()) {
            return aprilTagDetector.tagFound(tag);
        }
        return false;
    }
    
    //Returns true if any of the tags of the array has been found
    public boolean capture(int[] tag) {
        if (capture()) {
            for (int i = 0; i < tag.length; i++) {
                if(aprilTagDetector.tagFound(tag[i]))
                    return true;
            }
            return false;
        }
        return false;
    }
    
    public int[] getTagsId(){
        return aprilTagDetector.getTagsId();
    }
    
    public Vector3D getTagTranslation_camera(int tag) {
        return aprilTagDetector.getTagPoseTranslation(tag);
    }

    public Vector3D getTagTranslation_world(int tag) {
        if (aprilTagDetector.tagFound(tag)) {
            Vector3D trans_camera = aprilTagDetector.getTagPoseTranslation(tag);
            //Vector3D trans_camera_hack = new Vector3D(-1*trans_camera.getX(),-1*trans_camera.getY(),trans_camera.getZ());
            Vector3D trans_world = cameraArenaRotation.applyTo(trans_camera).add(cameraArenaTranslation);
            return trans_world;
        }
        return null;
    }

    public Rotation getTagRotation_camera(int tag) {
        return aprilTagDetector.getTagPoseRotation(tag);
    }

    public Rotation getTagRotation_world(int tag) {
        if (aprilTagDetector.tagFound(tag)) {
            Rotation rotation_world = aprilTagDetector.getTagPoseRotation(tag).applyTo(cameraArenaRotation.revert());
            return rotation_world;
        }
        return null;
    }

    public Rotation getUR5Rotation_world(int tag) {
        if (aprilTagDetector.tagFound(tag)) {
            //To grasp a connector, we need the rotation facing the tag
            //Thus, we rotote 180 degrees around the Y axis the rotation of the tag.
            Rotation tag2ur5 = new Rotation(new Vector3D(0, 1, 0), Math.PI);
            Rotation ur5rotation_camera = tag2ur5.applyTo(aprilTagDetector.getTagPoseRotation(tag));
            Rotation ur5rotation_world = ur5rotation_camera.applyTo(cameraArenaRotation.revert());
            return ur5rotation_world;
        }
        return null;
    }
    
    public Vector3D tagCS2worldCS(Vector3D pos, int tag){
        if (aprilTagDetector.tagFound(tag)) {
            Rotation rotTag_world = getTagRotation_world(tag);
            return rotTag_world.applyInverseTo(pos).add(getTagTranslation_world(tag));
        }
        return null;
    }
    
    public boolean tagFound(int tag) {
        return aprilTagDetector.tagFound(tag);
    }
    
     public boolean tagFounds() {
        return aprilTagDetector.tagFounds();
    }
    
}
