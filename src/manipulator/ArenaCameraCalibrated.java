package manipulator;

import apriltag.CameraCapture;
import apriltag.CameraType;
import apriltag.Capture;
import apriltag.ImageResolution;
import manipulator.test.ManipulatorReachTagTest;
import apriltag.CoppeliaSimCapture;
import org.opencv.core.Core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import math.util.QuaternionMath;
import modules.evaluation.CoppeliaSimulator;
import modules.util.PhysicalSetupConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.visp.core.VpCameraParameters;

public class ArenaCameraCalibrated {

	static {
		System.loadLibrary("visp_java341");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
// Exterior tags
//    private static final int[] referenceTags = new int[]{97, 98, 99, 100}; 
//    private static final Vector3D[] referenceTagsTranslations_robot = new Vector3D[]{
//        new Vector3D(-0.3825, -0.416, -0.0195), //	97    
//        new Vector3D(-0.8775, -0.416, -0.0195), //	98
//        new Vector3D(-0.3825, 0.554, -0.0195), //	99
//        new Vector3D(-0.8775, 0.554, -0.0195)};     //	100	
//    private static final double tagSize = 0.032;

//// A3 paper 32 mm tag size
//  private static final int[] referenceTags = new int[]{93, 94, 95, 96}; 
//  private static final Vector3D[] referenceTagsTranslations_robot = new Vector3D[]{
//      new Vector3D(-0.8035, 0.1875, -0.0195), //	93    
//      new Vector3D(-0.4215, 0.1875, -0.0195), //	94
//      new Vector3D(-0.8035, -0.0715, -0.0195), //	95
//      new Vector3D(-0.4215, -0.0715, -0.0195)};     //	96
//  private static final double tagSize = 0.032; 

	// A3 paper 64 mm tag size
//  private static final int[] referenceTags = new int[]{93, 94, 95, 96}; 
//  private static final Vector3D[] referenceTagsTranslations_robot = new Vector3D[]{
//      new Vector3D(-0.7825, 0.1665, -0.0195), //	93    
//      new Vector3D(-0.4425, 0.1665, -0.0195), //	94
//      new Vector3D(-0.7825, -0.0505, -0.0195), //	95
//      new Vector3D(-0.4425, -0.0505, -0.0195)};     //	96
//  private static final double tagSize = 0.064; 

////Robot tag
	private static final int[] referenceTags = new int[] { 92 };
	private static final Vector3D[] referenceTagsTranslations_robot = new Vector3D[] { new Vector3D(-0.58, -0.05, 0.2),
			new Vector3D(-0.48, -0.25, 0.2), new Vector3D(-0.48, 0.18, 0.2), new Vector3D(-0.69, 0.18, 0.2),
			new Vector3D(-0.69, -0.25, 0.2) }; // 92

	private static final double tagSize = 0.032;

	public static void main(String[] args) {

		boolean simulation = false;

		Capture imageCapture;

		if (simulation) {
			CoppeliaSimulator sim = new CoppeliaSimulator();
			sim.connect2CoppeliaSim();
			imageCapture = new CoppeliaSimCapture(sim, "Vision_sensor_arena");
		} else {
			imageCapture = new CameraCapture(CameraType.ARENA_CEILING_CAMERA);
		}

		calibratedCamera(imageCapture);

	}

	public static ArenaCamera calibratedCameraRobotTag(Capture imageCapture, ManipulatorUR5 ur5, int numberOfPoints) {
		ur5.calibrationHome();
		sleep(1000);
		
		

		ur5.move2Pose(new Vector3D(referenceTagsTranslations_robot[0].getX(), referenceTagsTranslations_robot[0].getY(),
				referenceTagsTranslations_robot[0].getZ()), ManipulatorUR5.FACING_UP);
		sleep(1000);
		ArenaCamera camera = new ArenaCamera(imageCapture);
		if (!camera.capture(tagSize)) {
			System.err.println(
					"ArenaCameraCalibrated: No reference tags found. Stopping as we cannot calibrate the camera with tags on the table!");
			System.exit(-1);
		}

		// We calculate the rotation of the camera by using the rotation of all the tags
		System.out.println("Check the rotation of the robot tag 3 times: ");

		List<Rotation> rots_camera = new ArrayList<>();
		for (int i = 0; i < numberOfPoints; i++) {
			int tag = referenceTags[0];
			ur5.move2Pose(new Vector3D(referenceTagsTranslations_robot[i].getX(),
			referenceTagsTranslations_robot[i].getY(),
			referenceTagsTranslations_robot[i].getZ()), ManipulatorUR5.FACING_UP);
			sleep(1000);
			if (!camera.capture(tagSize)) {
				System.err.println(
						"ArenaCameraCalibrated: No reference tags found. Stopping as we cannot calibrate the camera with tags on the table!");
				System.exit(-1);
			}
			if (camera.tagFound(tag)) {
				rots_camera.add(camera.getTagRotation_camera(tag));
				System.out
						.println("Tag " + tag + " rotation: " + Orientation.rot2str(camera.getTagRotation_camera(tag)));
			} else {
				System.out.println("Tag " + tag + "not found");
			}
		}
		Rotation avr_rot = QuaternionMath.average(rots_camera);// camera.getTagRotation_camera(referenceTags[0]);
		System.out.println("Average Rotation: " + Orientation.rot2str(avr_rot));

		// Create the new arena camera tracker with the new orientation
		camera = new ArenaCamera(new Vector3D(0.0, 0.0, 0.0), avr_rot.revert(), imageCapture);
		camera.capture(tagSize);

		Vector3D cam_trans = new Vector3D(0.0, 0.0, 0.0);
		int tagsFound = 0;
		for (int index = 0; index < numberOfPoints; index++) {
			int tag = referenceTags[0];
			ur5.move2Pose(new Vector3D(referenceTagsTranslations_robot[index].getX(),
			referenceTagsTranslations_robot[index].getY(),
			referenceTagsTranslations_robot[index].getZ()), ManipulatorUR5.FACING_UP);
			sleep(1000);
			if (!camera.capture(tagSize)) {
				System.err.println(
						"ArenaCameraCalibrated: No reference tags found. Stopping as we cannot calibrate the camera with tags on the table!");
				System.exit(-1);
			}
			if (camera.tagFound(tag)) {
				tagsFound++;
				Vector3D traslation = camera.getTagTranslation_world(tag);
				System.out.println("Tag 0 position world = " + traslation);

				cam_trans = cam_trans.add(traslation.negate().add(referenceTagsTranslations_robot[index]));

			} else {
				System.out.println("Tag " + tag + " not found.");
			}
		}
		if (tagsFound > 0) {
			// Create the new arena camera tracker with the new translation
			cam_trans = new Vector3D(cam_trans.getX() / tagsFound, cam_trans.getY() / tagsFound,
					cam_trans.getZ() / tagsFound);
			camera = new ArenaCamera(cam_trans, avr_rot.revert(), imageCapture);
			camera.capture(tagSize);
		} else {
			System.out.println("Tags not found. Aborting");
			System.exit(-1);
		}
		System.out.println("Calibration finished!");
		System.out.println("Testing the tags\n");

		// printReferenceTagsPositions(camera);
		// printReferenceTagsPositionErrors(camera);
		ur5.calibrationHome();
		ur5.home();

		return camera;

	}


	public static ArenaCamera calibratedCamera(Capture imageCapture) {
		ArenaCamera camera = new ArenaCamera(imageCapture);
		if (!camera.capture(tagSize)) {
			System.err.println(
					"ArenaCameraCalibrated: No reference tags found. Stopping as we cannot calibrate the camera with tags on the table!");
			System.exit(-1);
		}

		// We calculate the rotation of the camera by using the rotation of all the tags
		System.out.println("Check that all the tags have the same rotation: ");

		List<Rotation> rots_camera = new ArrayList<>();
		for (int i = 0; i < referenceTags.length; i++) {
			int tag = referenceTags[i];
			if (camera.tagFound(tag)) {
				rots_camera.add(camera.getTagRotation_camera(tag));
				System.out
						.println("Tag " + tag + " rotation: " + Orientation.rot2str(camera.getTagRotation_camera(tag)));
			} else {
				System.out.println("Tag " + tag + "not found");
			}
		}
		Rotation avr_rot = QuaternionMath.average(rots_camera);// camera.getTagRotation_camera(referenceTags[0]);
		System.out.println("Average Rotation: " + Orientation.rot2str(avr_rot));

		// Create the new arena camera tracker with the new orientation
		camera = new ArenaCamera(new Vector3D(0.0, 0.0, 0.0), avr_rot.revert(), imageCapture);
		camera.capture(tagSize);

		Vector3D cam_trans = new Vector3D(0.0, 0.0, 0.0);
		int tagsFound = 0;
		for (int index = 0; index < referenceTags.length; index++) {
			int tag = referenceTags[index];
			if (camera.tagFound(tag)) {
				tagsFound++;
				Vector3D traslation = camera.getTagTranslation_world(tag);
				System.out.println("Tag 0 position world = " + traslation);

				cam_trans = cam_trans.add(traslation.negate().add(referenceTagsTranslations_robot[index]));

			} else {
				System.out.println("Tag " + tag + " not found.");
			}
		}
		if (tagsFound > 1) {
			// Create the new arena camera tracker with the new translation
			cam_trans = new Vector3D(cam_trans.getX() / tagsFound, cam_trans.getY() / tagsFound,
					cam_trans.getZ() / tagsFound);
			camera = new ArenaCamera(cam_trans, avr_rot.revert(), imageCapture);
			camera.capture(tagSize);
		} else {
			System.out.println("Tags not found. Aborting");
			System.exit(-1);
		}
		System.out.println("Calibration finished!");
		System.out.println("Testing the tags\n");

		printReferenceTagsPositions(camera);
		printReferenceTagsPositionErrors(camera);

		return camera;

	}

	public static void printReferenceTagsPositions(ArenaCamera camera) {
		for (int i = 0; i < referenceTags.length; i++) {
			if (camera.tagFound(referenceTags[i])) {
				System.out.println("Tag " + referenceTags[i] + " position_world = "
						+ camera.getTagTranslation_world(referenceTags[i]));
			}
		}

	}

	public static void printReferenceTagsPositionErrors(ArenaCamera camera) {
		Vector3D errorVector;
		for (int i = 0; i < referenceTags.length; i++) {
			if (camera.tagFound(referenceTags[i])) {
				errorVector = referenceTagsTranslations_robot[i]
						.subtract(camera.getTagTranslation_world(referenceTags[i]));
				System.out.println(
						"Tag " + referenceTags[i] + " position error world = " + errorVector.scalarMultiply(1000));
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
