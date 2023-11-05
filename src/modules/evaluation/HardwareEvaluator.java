/* 
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2021 REAL (ITU)
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

import apriltag.AprilTagDetector;
import apriltag.AprilTagFamily;
import apriltag.CameraCapture;
import apriltag.Capture;
import apriltag.ImageConverter;
import apriltag.ImageUtils;
import apriltag.TagUtils;
import dynamixel.DynamixelController;
import dynamixel.DynamixelSinusoidalController;
import java.io.File;
import java.io.FilenameFilter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import modules.util.SimulationConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.visp.core.VpCameraParameters;
import org.visp.core.VpImagePoint;
import apriltag.ImageResolution;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * HardwareEvaluator.java Created on 07/04/2021
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class HardwareEvaluator implements Runnable {

    private DynamixelSinusoidalController controller;
    private CameraCapture camera;
    private boolean emergencyStop = false;
    private String defaultPath;
    private String path;
    private boolean video = false;
    private VideoWriter rawWriter;
    private VideoWriter processedWriter;
    private VpCameraParameters camParam;
    private Vector3D startPos = null, finalPos = null, initialPos = null;
    private double startTime, finalTime, initialTime;
    private final static double EXTRA_RECORDING_TIME = 5000; //In ms
    private final int FPS_VIDEO = 10;
    private final int FPS_FAST_VIDEO = 2;

    public HardwareEvaluator(DynamixelSinusoidalController controller) {
        this.controller = controller;
    }

    public HardwareEvaluator(DynamixelSinusoidalController controller, int cameraIndex, VpCameraParameters camParam, ImageResolution resolution, String path) {
        this(controller);
        camera = new CameraCapture(cameraIndex, camParam, resolution, true);
        camera.capture();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
        camera.capture();
        this.defaultPath = path;
        video = true;
        this.camParam = camParam;
    }

    public void evaluate() {
        System.out.println("Starting the evaluation...");
        double time = 0;
        double maxTime = SimulationConfiguration.getMaxSimulationTime() * 1000;
        System.out.println("Evaluation Time: " + SimulationConfiguration.getMaxSimulationTime());
        if (video) {

            if (!defaultPath.endsWith("/")) {
                defaultPath = defaultPath.concat("/");
            }
            path = defaultPath + Instant.now() // Capture the current moment in UTC.
                    .truncatedTo(ChronoUnit.MILLIS) // Lop off the fractional second, as superfluous to our purpose.
                    .toString() // Generate a `String` with text representing the value of the moment in our `Instant` using standard ISO 8601 format: 2016-10-02T19:04:16Z
                    .replace("-", "_") // Shorten the text to the “Basic” version of the ISO 8601 standard format that minimizes the use of delimiters. First we drop the hyphens from the date portion
                    .replace(":", "_");              // Returns 20161002T190416Z afte we drop the colons from the time portion. ;
            File file = new File(path);
            boolean ok = file.mkdir();
            if (!ok) {
                video = false;
            } else {
//                rawWriter = new VideoWriter(path + "/evaluation.avi",
//                        VideoWriter.fourcc('D', 'I', 'V', 'X'), FPS_VIDEO,
//                        new Size(camera.getVideoCapture().get(Videoio.CAP_PROP_FRAME_WIDTH),
//                                camera.getVideoCapture().get(Videoio.CAP_PROP_FRAME_HEIGHT)));
            }

        }
        long timeInit = System.currentTimeMillis();
        int iter = 0;
        while (time < (maxTime + EXTRA_RECORDING_TIME)) {
            if (controller.getEmergencyStop()) {
                break;
            }
            if (video) {
                camera.fastCaptureCameraImage();
                camera.saveImage(path + "/" + String.format("%07d", (int) time) + ".jpg");
                //record the frame
                //rawWriter.write(camera.getMat());
            }
            if (time < maxTime) {
                controller.updateJoints(time / 1000.0);
            }

            iter++;
            time = System.currentTimeMillis() - timeInit;
            while (time < iter * 100) {
                time = System.currentTimeMillis() - timeInit;
            }
        }
        //rawWriter.release();
        processVideo();
        System.out.println("Evaluation ended at time " + time / 1000.0);
    }

    public void setEmergencyStop(boolean emergencyStop) {
        this.emergencyStop = emergencyStop;
    }

    @Override
    public void run() {
        evaluate();
    }

    private void processVideo() {
        System.out.println("Processing video...");
        processedWriter = new VideoWriter(path + "/evaluationProcessed.avi",
                VideoWriter.fourcc('D', 'I', 'V', 'X'), FPS_FAST_VIDEO,
                new Size(camera.getVideoCapture().get(Videoio.CAP_PROP_FRAME_WIDTH),
                        camera.getVideoCapture().get(Videoio.CAP_PROP_FRAME_HEIGHT)));

        //Iterate over all tye images
        File dir = new File(path);
        FilenameFilter jpgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg");
            }
        };
        File[] directoryListing = dir.listFiles(jpgFilter);
        if (directoryListing != null) {
            Arrays.sort(directoryListing);
            int i = 0;
            for (File child : directoryListing) {
                if (i % ((int) (FPS_VIDEO / FPS_FAST_VIDEO)) == 0) {
                    processFrame(child.getAbsolutePath());
                }
                i++;
            }
        }

        //Calculate fitness and save data
        FileOutputStream resultsFile = null;
        try {
            resultsFile = new FileOutputStream(path +"/results.txt", true);
            PrintStream printResults = new PrintStream(resultsFile);
            
            if (initialPos == null || finalPos == null) {
                System.err.println("Initial or final pose not yet defined at the end of the hardware  evaluation!");
            } else {
                double fitness = Math.pow(finalPos.getX() - initialPos.getX(), 2) + Math.pow(finalPos.getY() - initialPos.getY(), 2);
                fitness = Math.sqrt(fitness);
                printResults.println("Fitness: " + fitness);
                System.out.println("Fitness: " + fitness);
            }
            printResults.println("StartPos: " + startPos + ", time: " + startTime);
            printResults.println("InitialPos: " + initialPos  + ", time: " + initialTime);
            printResults.println("FinalPos: " + finalPos + ", time: " + finalTime);
            printResults.close();
            resultsFile.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }

        processedWriter.release();
        System.out.println("Video porcessed!");
    }

    private void processFrame(String imageFile) {
        double time = Integer.valueOf(imageFile.substring(imageFile.length() - 7 - 4, imageFile.length() - 4)) / 1000.0;
        Mat frame = Imgcodecs.imread(imageFile);
        int referenceTag = TagUtils.getBASE_TAG();
        double tagSize = 0.086;
        AprilTagDetector detector = new AprilTagDetector(ImageConverter.Mat2VpImageUChar(frame), AprilTagFamily.TAG_36h11, camParam, tagSize);
        if (detector.isDetected()) {

            int[] realTagsId = detector.getTagsId();

            int referenceTagPosition = IntStream.range(0, realTagsId.length)
                    .filter(idx -> realTagsId[idx] == referenceTag)
                    .findFirst()
                    .orElse(-1);
            if (referenceTagPosition != -1) {

                List<Vector3D> realTransVec = detector.getTagPoseTranslationList();
                List<Rotation> realRotationVec = detector.getTagPoseRotationList();

                // Get camera position from tag0 perspective
                Vector3D posBase = realTransVec.get(referenceTagPosition);
                Rotation rotBase = realRotationVec.get(referenceTagPosition);
                List<VpImagePoint> corners = detector.getTags_corners().get(referenceTagPosition);
                Point pt1 = new Point(corners.get(0).get_j(), corners.get(0).get_i());
                Point pt2 = new Point(corners.get(1).get_j(), corners.get(1).get_i());
                Point pt3 = new Point(corners.get(2).get_j(), corners.get(2).get_i());
                Point pt4 = new Point(corners.get(3).get_j(), corners.get(3).get_i());
                Imgproc.line(frame, pt1, pt2, new Scalar(255, 0, 0), 3);
                Imgproc.line(frame, pt2, pt3, new Scalar(255, 0, 0), 3);
                Imgproc.line(frame, pt3, pt4, new Scalar(255, 0, 0), 3);
                Imgproc.line(frame, pt4, pt1, new Scalar(255, 0, 0), 3);

                if (startPos == null) {
                    startPos = posBase;
                    startTime = time;
                    System.out.println("Start Pos: " + posBase.getX() + " " + posBase.getY() + " " + posBase.getZ());
                }

                if (initialPos == null) {
                    if (time >= SimulationConfiguration.getTimeIniFitness()) {
                        initialPos = posBase;
                        initialTime = time;
                        System.out.println("Init Pos: " + posBase.getX() + " " + posBase.getY() + " " + posBase.getZ());
                    }
                }
                double timeEndEval = SimulationConfiguration.getTimeEndFitness();
                if (timeEndEval < SimulationConfiguration.getTimeIniFitness()) {
                    timeEndEval = SimulationConfiguration.getMaxSimulationTime();
                }

                if (finalPos == null) {
                    if (time >= timeEndEval) {
                        finalPos = posBase;
                        finalTime = time;
                        System.out.println("Final Pos: " + posBase.getX() + " " + posBase.getY() + " " + posBase.getZ());
                    }
                }

            }
        }
        processedWriter.write(frame);
    }

}
