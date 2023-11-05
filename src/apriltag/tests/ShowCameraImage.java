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

import apriltag.AprilTagDetector;
import apriltag.BufferedImageUtils;
import apriltag.CameraCapture;
import apriltag.Capture;
import apriltag.ImageConverter;
import apriltag.ImageResolution;
import apriltag.ImageUtils;
import apriltag.TagUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import modules.util.PhysicalSetupConfiguration;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import static org.opencv.highgui.HighGui.imshow;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.visp.core.VpCameraParameters;
import org.visp.core.VpImagePoint;
import org.visp.core.VpImageUChar;

/**
 * ShowCameraImage.java Created on 11/04/2021
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class ShowCameraImage {

    static {
        System.loadLibrary("visp_java341");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public static void main(String[] args) {
        System.out.println("Starting ShowCameraImage...");
        ImageResolution res = PhysicalSetupConfiguration.getArenaCeilingCamResolution();
        CameraCapture camera = new CameraCapture(
                PhysicalSetupConfiguration.getArenaCeilingCamIndex(), 
                PhysicalSetupConfiguration.getArenaCeilingIntrinsicVispCamParam(), 
                res, false);
        camera.capture();
        Mat frame = camera.getMat();
        System.out.println("Real resolution: " + frame.width() + "x" + frame.height());

        JFrame jframe = new JFrame("Title");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setVisible(true);

        jframe.setSize(new Dimension(frame.width() + 50, frame.height() + 50));
        

        while (true) {
            if (camera.capture()) {
                
                frame = camera.getMat();
                
                //With Buffered Image (in gray scale)
//                VpImageUChar frameI = camera.getI();
//                BufferedImage frameBufImg = ImageConverter.VpImageUChar2BufferedImage(frameI);
//                BufferedImageUtils.paintTags(frameI, frameBufImg, camParam);
//                ImageIcon image = new ImageIcon(frameBufImg);
                
                //With Mat (in color)
                int refTag = 99;
                ImageUtils.findTag(refTag, frame, camera.getCamParam(), false);
                ImageUtils.displayCrossHairCamera(frame, camera.getCamParam(), 1000, Color.green, 1);
                ImageIcon image = new ImageIcon(ImageConverter.Mat2BufferedImage(frame));
                
                vidpanel.setIcon(image);
                vidpanel.repaint();
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(ShowCameraImage.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }
    }
}
