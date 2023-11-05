package manipulator.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Core;
import org.visp.core.VpCameraParameters;

import apriltag.CameraCapture;
import apriltag.CameraType;
import apriltag.Capture;
import apriltag.CoppeliaSimCapture;
import apriltag.ImageResolution;
import apriltag.tests.TakeImagesForCalibration;
import manipulator.ArenaCamera;
import manipulator.ArenaCameraCalibrated;
import manipulator.ManipulatorUR5;
import manipulator.Orientation;
import modules.evaluation.CoppeliaSimulator;
import modules.util.PhysicalSetupConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class ManipulatorMapTagError {

	static {
		System.loadLibrary("visp_java341");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private static final double[] xLimits = { -0.5, -0.72 };
	private static final double[] yLimits = { -0.25, 0.25 };//NOt finding the tags at 0.3 0.25
	private static final double zHeight = 0.2;
	private static final int divisions = 8;

	public static void main(String[] args) {
		boolean simulation = PhysicalSetupConfiguration.isSimulation();
		CameraType CAMERA_TYPE = CameraType.ARENA_CEILING_CAMERA;
		String dir = "/home/fai/camera-calibration/errorMap/";
		
		VpCameraParameters vpCamParam = PhysicalSetupConfiguration.getArenaCeilingIntrinsicVispCamParam();
		int tag = 92;

		Capture imageCapture;
		if (simulation) {
			CoppeliaSimulator coppeliaSim = new CoppeliaSimulator();
			coppeliaSim.connect2CoppeliaSim();
			imageCapture = new CoppeliaSimCapture(coppeliaSim, "Vision_sensor_arena");
			coppeliaSim.startSimulation();
		} else {
			imageCapture = new CameraCapture(CAMERA_TYPE);
		}

		// Create manipulator
		ManipulatorUR5 ur5 = new ManipulatorUR5();

		// Create the class of the camera that tracks the arena
		ArenaCamera arenaCam = ArenaCameraCalibrated.calibratedCameraRobotTag(imageCapture,ur5, 5);

		// Start simulator and home robot
		ur5.home();
		ur5.calibrationHome();
		sleep(1000);

//        ur5.move2Pose(ur5.getPose().getPose().getPositionVector(),new Orientation(1.0, 0.0, 0.0, 0.0));
//        sleep(2000);
//		ur5.move2Pose(new Vector3D(xLimits[0], yLimits[0], zHeight), ManipulatorUR5.FACING_UP);
//		sleep(2000);
//		ur5.move2Pose(new Vector3D(xLimits[1], yLimits[0], zHeight), ManipulatorUR5.FACING_UP);
//		sleep(2000);
//		ur5.move2Pose(new Vector3D(xLimits[1], yLimits[1], zHeight), ManipulatorUR5.FACING_UP);
//		sleep(2000);
//		ur5.move2Pose(new Vector3D(xLimits[0], yLimits[1], zHeight), ManipulatorUR5.FACING_UP);
//		sleep(2000);

		double xStep = Math.abs(xLimits[1] - xLimits[0]) / divisions;
		double yStep = Math.abs(yLimits[1] - yLimits[0]) / divisions;
		double xPos = xLimits[0];
		double yPos = yLimits[0];
		boolean comingBack = false;
		List<Vector3D> referencePoints = new ArrayList<Vector3D>();
		List<Vector3D> errorAverage = new ArrayList<Vector3D>();
		List<Vector3D> errorStd = new ArrayList<Vector3D>();

		for (int i = 0; i < divisions + 1; i++) {
			for (int j = 0; j < divisions + 1; j++) {
				ur5.move2Pose(new Vector3D(xPos, yPos, zHeight), ManipulatorUR5.FACING_UP);
				sleep(1000);
				System.out.println("x: " + xPos + " y: " + yPos);
				referencePoints.add(new Vector3D(xPos, yPos, zHeight));
				List<Vector3D> errors = new ArrayList<Vector3D>();
				for (int k = 0; k < 10; k++) {
					arenaCam.capture();
					if (arenaCam.tagFound(tag)) {
						System.out.println("Tag " + tag + " found");
						// Calculate translation of the tag
						Vector3D tagPose_world = arenaCam.getTagTranslation_world(tag);
						System.out.println("Position: " + xPos + ", " + yPos + ", " + zHeight);
						System.out.println("Tag Pos: " + tagPose_world.toString());
						Vector3D err = new Vector3D(xPos, yPos, zHeight).subtract(tagPose_world);
						errors.add(err);
						System.out.println("Error: " + err.toString());
					} else {
						errors.add(new Vector3D(xPos, yPos, zHeight).subtract(new Vector3D(1000, 1000, 1000)));
					}
                    

				}
				Vector3D average = averageErrors(errors);
				errorAverage.add(average);
				Vector3D stdeviation = calculateDeviation(errors,average);
				errorStd.add(stdeviation);

				if (j != divisions) {

					if (!comingBack) {
						yPos += yStep;
					} else {
						yPos -= yStep;
					}
				}
			}
			xPos -= xStep;
			comingBack = !comingBack;
		}

		try {
			File csvFile = new File(dir+"Height"+zHeight +"Div"+divisions+ ".csv");
			FileWriter fileWriter = new FileWriter(csvFile);
			
			for (int i = 0; i < errorAverage.size();i++) {
			    StringBuilder line = new StringBuilder();   
			    line.append(referencePoints.get(i).getX()+","+referencePoints.get(i).getY()+","+referencePoints.get(i).getZ()+","
			    		    +errorAverage.get(i).getX()+","+errorAverage.get(i).getY()+","+errorAverage.get(i).getZ()+","
			    		    +errorStd.get(i).getX()+","+errorStd.get(i).getY()+","+errorStd.get(i).getZ());
			    line.append("\n");
			    fileWriter.write(line.toString());
			    
			    System.out.println("Point:" + referencePoints.get(i).toString());
				System.out.println("Error Average:" + errorAverage.get(i).toString());
				System.out.println("Error std:" + errorStd.get(i).toString());
				System.out.println();
			}
			fileWriter.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		ur5.calibrationHome();
		ur5.home();
		sleep(3000);

	}

	private static Vector3D calculateDeviation(List<Vector3D> errors, Vector3D average) {
		ArrayList<Vector3D> errorToAverage = new ArrayList<Vector3D>();	
		for (Vector3D errorVector : errors) {
			errorToAverage.add(average.subtract(errorVector));
		}
		
		Vector3D variance = new Vector3D(0.0, 0.0, 0.0);
		for (Vector3D errorVector : errorToAverage) {
			variance = variance.add(new Vector3D(errorVector.getX()*errorVector.getX(),errorVector.getY()*errorVector.getY(),errorVector.getZ()*errorVector.getZ()));
		}
		variance = variance.scalarMultiply(1/(double)errorToAverage.size());
		
		return new Vector3D(Math.sqrt(variance.getX()),Math.sqrt(variance.getY()),Math.sqrt(variance.getZ()));
	}

	private static Vector3D averageErrors(List<Vector3D> errors) {
		Vector3D average = new Vector3D(0.0, 0.0, 0.0);
		for (Vector3D errorVector : errors) {
			average = average.add(errorVector);
		}
		average = average.scalarMultiply(1/(double)errors.size());
		return average;
	}


	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ex) {
			Logger.getLogger(ManipulatorReachTagTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
