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
package apriltag.tests;

import apriltag.Capture;
import apriltag.ImageConverter;
import apriltag.ImageResolution;
import apriltag.ImageUtils;
import apriltag.TagUtils;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import modules.evaluation.HardwareEvaluator;
import modules.util.PhysicalSetupConfiguration;
import modules.util.SimulationConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.visp.core.VpCameraParameters;

/**
 * AnalyzeVideo.java Created on 15/04/2021
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class AnalyzeVideo {

    static {
        System.loadLibrary("visp_java331");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
   

    private static Vector3D startPos, initialPos, finalPos;
    private static double startTime, finalTime, initialTime;
    private static VideoWriter processedWriter = null, rawWriter = null;

    public static void main(String[] args) {

        JFrame jframe = new JFrame("Title");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setVisible(true);

        //Iterate over all tye images
        String path = "C:/fai/documents/papers/2021_Frontiers_emerge/HPCResults/log29/2021_04_21T11_43_26.613Z/";
        File dir = new File(path);
        FilenameFilter jpgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg");
            }
        };

        System.out.println("Processing video...");

        int FPS_VIDEO = 10;

        File[] directoryListing = dir.listFiles(jpgFilter);
        if (directoryListing != null) {
            Arrays.sort(directoryListing);
            if (processedWriter == null) {
                Mat frameTmp = Imgcodecs.imread(directoryListing[0].getAbsolutePath());
                processedWriter = new VideoWriter(path + "/evaluationProcessed2.avi",
                        VideoWriter.fourcc('D', 'I', 'V', 'X'), FPS_VIDEO,
                        frameTmp.size());
                rawWriter = new VideoWriter(path + "/evaluation2.avi",
                        VideoWriter.fourcc('D', 'I', 'V', 'X'), FPS_VIDEO,
                        frameTmp.size());
            }

            for (File child : directoryListing) {
                Mat frame = processFrame(child.getAbsolutePath());

                jframe.setSize(new Dimension(frame.width() + 50, frame.height() + 50));
                ImageIcon image = new ImageIcon(ImageConverter.Mat2BufferedImage(frame));
                vidpanel.setIcon(image);
                vidpanel.repaint();
            }
        }

        //Calculate fitness and save data
        FileOutputStream resultsFile = null;
        try {
            resultsFile = new FileOutputStream(path + "/results2.txt", true);
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
            printResults.println("InitialPos: " + initialPos + ", time: " + initialTime);
            printResults.println("FinalPos: " + finalPos + ", time: " + finalTime);
            printResults.close();
            resultsFile.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HardwareEvaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
        processedWriter.release();
        rawWriter.release();
    }

    private static Mat processFrame(String imageFile) {
        double time = Integer.valueOf(imageFile.substring(imageFile.length() - 7 - 4, imageFile.length() - 4)) / 1000.0;
        Mat frame = Imgcodecs.imread(imageFile);
        rawWriter.write(frame);
        int referenceTag = TagUtils.getBASE_TAG();
        double tagSize = 0.086;

        Vector3D posBase = ImageUtils.findBaseTag(frame, PhysicalSetupConfiguration.getArenaCeilingIntrinsicVispCamParam(), true);
        processedWriter.write(frame);
        if (startPos == null && posBase != null) {
            startPos = posBase;
            startTime = time;
            System.out.println("Start Pos: " + posBase.getX() + " " + posBase.getY() + " " + posBase.getZ() + ", Time: " + time);
        }

        if (initialPos == null && posBase != null) {
            if (time >= SimulationConfiguration.getTimeIniFitness()) {
                initialPos = posBase;
                initialTime = time;
                System.out.println("Init Pos: " + posBase.getX() + " " + posBase.getY() + " " + posBase.getZ() + ", Time: " + time);
            }
        }
        double timeEndEval = SimulationConfiguration.getTimeEndFitness();
        if (timeEndEval < SimulationConfiguration.getTimeIniFitness()) {
            timeEndEval = SimulationConfiguration.getMaxSimulationTime();
        }

        if (finalPos == null && posBase != null) {
            if (time >= timeEndEval) {
                finalPos = posBase;
                finalTime = time;
                System.out.println("Final Pos: " + posBase.getX() + " " + posBase.getY() + " " + posBase.getZ() + ", Time: " + time);
            }
        }
        return frame;
    }

}
