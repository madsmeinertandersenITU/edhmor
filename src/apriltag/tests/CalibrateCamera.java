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
package apriltag.tests;

import apriltag.CameraCapture;
import apriltag.ImageConverter;
import apriltag.ImageResolution;
import apriltag.ImageUtils;
import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import modules.util.PhysicalSetupConfiguration;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.resize;
import org.visp.core.VpCameraParameters;
import org.visp.io.VpImageIo;

/**
 * CalibrateCamera created on Sep 2, 2022
 *
 * @author Andres Faiña <anfv at itu.dk>
 */
public class CalibrateCamera {

    static {
        System.loadLibrary("visp_java341");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static JFrame jframe;
    private static JLabel vidpanel;

    public static void main(String[] args) {
        final ImageResolution resolution = PhysicalSetupConfiguration.getArenaCeilingCamResolution();
        String dir = "/home/fai/camera-calibration/";
        int CAMERA_INDEX = PhysicalSetupConfiguration.getArenaCeilingCamIndex();

        String dirImages = dir + resolution.name() + "/";

        // your directory
        File f = new File(dirImages);
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("chessboard") && name.endsWith("jpg");
            }
        });

        System.out.println("We found " + matchingFiles.length + " images. ");

        //Create the GUI
        jframe = new JFrame("Title");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setVisible(true);

        List<Mat> imagePoints = new ArrayList<>();
        List<Mat> objectPoints = new ArrayList<>();
        MatOfPoint3f obj = new MatOfPoint3f();
        int numRows = 9;
        int numColumns = 6;
        int numSquares = numRows * numColumns;
        double squareSize = 25.0;

        for (int j = 0; j < numSquares; j++) {
            MatOfPoint3f point = new MatOfPoint3f(new Point3((j / numRows) * squareSize, (j % numRows) * squareSize, 0.0f));
            obj.push_back(point);
            //System.out.println(point.dump());
        }

        Mat lastImage = new Mat();

        //Extract points for calibration
        for (File imageFile : matchingFiles) {
            Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
            // convert the frame in gray scale
            Mat grayImage = new Mat();
            Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
            showImage(grayImage);

            Size boardSize = new Size(numRows, numColumns);
            MatOfPoint2f imageCorners = new MatOfPoint2f();

            // look for the inner chessboard corners
            boolean found = Calib3d.findChessboardCorners(grayImage, boardSize, imageCorners,
                    Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE + Calib3d.CALIB_CB_FAST_CHECK);

            // all the required corners have been found...
            if (found) {
                System.out.println("Chessboard found in image " + imageFile.getName());
                // optimization
                TermCriteria term = new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1);
                Imgproc.cornerSubPix(grayImage, imageCorners, new Size(11, 11), new Size(-1, -1), term);
                // save the current frame for further elaborations
                grayImage.copyTo(lastImage);
                // show the chessboard inner corners on screen
                Calib3d.drawChessboardCorners(grayImage, boardSize, imageCorners, found);
                imagePoints.add(imageCorners);
                objectPoints.add(obj);
                showImage(grayImage);
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                Logger.getLogger(CalibrateCamera.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // init needed variables according to OpenCV docs
        List<Mat> rvecs = new ArrayList<>();
        List<Mat> tvecs = new ArrayList<>();
        Mat distCoeffs = new MatOfDouble();
        Mat intrinsic = new Mat(3, 3, CvType.CV_32FC1);
        intrinsic.put(0, 0, 1);
        intrinsic.put(1, 1, 1);
        // calibrate!
        System.out.println("Calibrating...");
        Calib3d.calibrateCamera(objectPoints, imagePoints, lastImage.size(), intrinsic, distCoeffs, rvecs, tvecs);

        //Store calibration matrix:
        Mat undistored = new Mat();
        Mat optimizedCamera = Calib3d.getOptimalNewCameraMatrix(intrinsic, distCoeffs, lastImage.size(), 0.2);
        Calib3d.undistort(lastImage, undistored, intrinsic, distCoeffs, optimizedCamera);
        System.out.println("distCoeffs: " + distCoeffs.dump());
        System.out.println("intrinsic: " + intrinsic.dump());
        System.out.println("optimizedCamera: " + optimizedCamera.dump());
        PhysicalSetupConfiguration.saveArenaCeilingCameraParameters(distCoeffs, intrinsic, optimizedCamera);
        System.out.println("Calibration finished...");

        
        

        System.out.println("Showing and saving undistorting images");

        //Create folder to save undistorted images
        String undistDirImages = dir + "undistort_" + resolution.name() + "/";
        try {
            Path path = Paths.get(undistDirImages);
            Files.createDirectories(path);
        } catch (IOException ex) {
            Logger.getLogger(TakeImagesForCalibration.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Show and save images
        for (File imageFile : matchingFiles) {
            Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
            // convert the frame in gray scale
            Mat grayImage = new Mat();
            Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

            undistored = new Mat();
            Calib3d.undistort(grayImage, undistored, intrinsic, distCoeffs, optimizedCamera);
            
            
            

            ImageUtils.paintTags(undistored, PhysicalSetupConfiguration.getArenaCeilingOptimizedVispCamParam());
            showImage(undistored);

            Imgcodecs.imwrite(undistDirImages + imageFile.getName(), undistored);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CalibrateCamera.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        double mean_error = 0;
        for (int i = 0; i < objectPoints.size(); i++) {

            MatOfPoint2f imagePoints2 = new MatOfPoint2f();
            //imgpoints2, _ = cv.projectPoints(objpoints[i], rvecs[i], tvecs[i], mtx, dist)
            Calib3d.projectPoints​(obj, rvecs.get(i), tvecs.get(i), intrinsic, (MatOfDouble) distCoeffs, imagePoints2);

            //error = cv.norm(imgpoints[i], imgpoints2, cv.NORM_L2) / len(imgpoints2)
            double error = Core.norm(imagePoints.get(i), imagePoints2, Core.NORM_L2) / imagePoints2.height();

            mean_error += error;

        }
        System.out.println("total error: " + mean_error / objectPoints.size());
    }

    private static void showImage(Mat matrix) {
        Mat resizeimage = new Mat();
        Size scaleSize = new Size(matrix.width() / 2, matrix.height() / 2);
        resize(matrix, resizeimage, scaleSize, 0, 0, Imgproc.INTER_AREA);

        jframe.setSize(new Dimension(resizeimage.width(), resizeimage.height()));
        ImageIcon imageToShow = new ImageIcon(ImageConverter.Mat2BufferedImage(resizeimage));
        vidpanel.setIcon(imageToShow);
        vidpanel.repaint();
    }

}
