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
package apriltag;

import java.awt.Color;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.visp.core.VpCameraParameters;
import org.visp.core.VpHomogeneousMatrix;
import org.visp.core.VpImagePoint;
import org.visp.core.VpPoint;

/**
 * ImageUtils.java Created on 09/04/2021
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class ImageUtils {

    public static void paintTags(Mat frame, VpCameraParameters camParam) {
        double tagSize = 0.031;
        AprilTagDetector detector = new AprilTagDetector(ImageConverter.Mat2VpImageUChar(frame), AprilTagFamily.TAG_36h11, camParam, tagSize);
        if (detector.isDetected()) {

            List<VpHomogeneousMatrix> cMo_vec = detector.getcMo_vec();
            int[] tagsId = detector.getTagsId();
            for (int i = 0; i < tagsId.length; i++) {
                ImageUtils.displayFrame(frame, cMo_vec.get(i), detector.getCamParam(), tagSize / 2, 3);
            }
        }
    }

    public static Vector3D findBaseTag(Mat frame, VpCameraParameters camParam, boolean forceFinding) {
        return findTag(TagUtils.getBASE_TAG(), frame, camParam, forceFinding);
    }
    public static Vector3D findTag(int referenceTag, Mat frame, VpCameraParameters camParam, boolean forceFinding) {
        double tagSize = 0.086;
        int iter = 0;
        AprilTagDetector detector = new AprilTagDetector(ImageConverter.Mat2VpImageUChar(frame), AprilTagFamily.TAG_36h11, camParam, tagSize);
        boolean baseFound = detector.tagFound(referenceTag);
        while (!baseFound && iter < 5 && forceFinding) {
            Mat gray = new Mat(frame.size(), CvType.CV_8UC1);
            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_RGB2GRAY);
            Mat thres = new Mat(frame.size(), CvType.CV_8UC1);
            int c = -5;
            double kernelSize = 2;
            Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(2 * kernelSize + 1, 2 * kernelSize + 1),
                    new Point(kernelSize, kernelSize));
            Imgproc.erode(gray, gray, element);

            //Lets try to apply a filter to see if we can detect the tag
            Imgproc.adaptiveThreshold(gray, thres, 125,
                    Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                    Imgproc.THRESH_BINARY, 51, c + iter * 2);
            detector = new AprilTagDetector(ImageConverter.Mat2VpImageUChar(thres), AprilTagFamily.TAG_36h11, camParam, tagSize);
            baseFound = detector.tagFound(referenceTag);
            iter++;
        }

        if (baseFound) {
                int referenceTagPosition = detector.tagIndex(referenceTag);

                List<Vector3D> realTransVec = detector.getTagPoseTranslationList();
                List<Rotation> realRotationVec = detector.getTagPoseRotationList();
                List<VpHomogeneousMatrix> cMo = detector.getcMo_vec();

                // Get camera position from tag0 perspective
                Vector3D posBase = realTransVec.get(referenceTagPosition);
                Rotation rotBase = realRotationVec.get(referenceTagPosition);
                VpHomogeneousMatrix cMoBase = cMo.get(referenceTagPosition);

                //Draw corners
                List<VpImagePoint> corners = detector.getTags_corners().get(referenceTagPosition);
                Point pt1 = new Point(corners.get(0).get_j(), corners.get(0).get_i());
                Point pt2 = new Point(corners.get(1).get_j(), corners.get(1).get_i());
                Point pt3 = new Point(corners.get(2).get_j(), corners.get(2).get_i());
                Point pt4 = new Point(corners.get(3).get_j(), corners.get(3).get_i());
                Imgproc.line(frame, pt1, pt2, new Scalar(255, 0, 0), 3);
                Imgproc.line(frame, pt2, pt3, new Scalar(255, 0, 0), 3);
                Imgproc.line(frame, pt3, pt4, new Scalar(255, 0, 0), 3);
                Imgproc.line(frame, pt4, pt1, new Scalar(255, 0, 0), 3);

                //Draw arrow for heading
//                VpPoint o = new VpPoint(0.0, 0.0, 0.0);
//                VpPoint x = new VpPoint(0.086, 0.0, 0.0);
//
//                o.changeFrame(cMoBase);
//                o.projection();
//                x.changeFrame(cMoBase);
//                x.projection();
//
//                int thickness = 4;
//                displayArrow(frame, o.get_y() * camParam.get_py() + camParam.get_v0(), o.get_x() * camParam.get_px() + camParam.get_u0(),
//                        x.get_y() * camParam.get_py() + camParam.get_v0(), x.get_x() * camParam.get_px() + camParam.get_u0(), Color.RED,
//                        4 * thickness, 2 * thickness, thickness);

                displayFrame(frame, cMoBase, camParam, 0.08, 4);

                //Draw text
                String tagPos = "Pos: " + posBase.getX() + " " + posBase.getY() + " " + posBase.getZ();
                Imgproc.putText(frame, tagPos, new Point(50, 50), Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(0, 0, 0), 4);
                Vector3D rot_X = rotBase.applyTo(Vector3D.PLUS_I);
                double angle = Math.atan2(rot_X.getX(), rot_X.getY());
                String tagRot = String.format("Rot: %.2f %.2fi %.2fj %.2fz",rotBase.getQ0(), rotBase.getQ1(), rotBase.getQ2(), rotBase.getQ3());
                Imgproc.putText(frame, tagRot, new Point(50, 150), Imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(0, 0, 0), 4);
                return posBase;

        }
        //We could not detect the tag
        return null;
    }

    public static void displayArrow(Mat frame, double i1, double j1, double i2, double j2, Color color, int w, int h,
            int thickness) {
        ImageUtils.displayArrow(frame, (int) i1, (int) j1, (int) i2, (int) j2, color, w, h, thickness);
    }

    public static void displayArrow(Mat frame, int i1, int j1, int i2, int j2, Color color, int w, int h,
            int thickness) {

        double a = i2 - i1;
        double b = j2 - j1;
        double lg = Math.sqrt(a * a + b * b);

        if (Math.abs(a) <= Math.ulp(1.0) && Math.abs(b) <= Math.ulp(1.0)) {
        } else {
            a /= lg;
            b /= lg;

            double i3 = i2 - w * a;
            double j3 = j2 - w * b;

            double i4 = i3 - b * h;
            double j4 = j3 + a * h;

            double dist = Math.sqrt((i2 - i4) * (i2 - i4) + (j2 - j4) * (j2 - j4));
            if (lg > 2 * dist) {
                displayLine(frame, (int) i2, (int) j2, (int) i4, (int) j4, color, thickness);
            }

            i4 = i3 + b * h;
            j4 = j3 - a * h;

            dist = Math.sqrt((i2 - i4) * (i2 - i4) + (j2 - j4) * (j2 - j4));
            if (lg > 2 * dist) {
                displayLine(frame, (int) i2, (int) j2, (int) i4, (int) j4, color, thickness);
            }

            displayLine(frame, i1, j1, i2, j2, color, thickness);
        }
    }

    public static void displayLine(Mat frame, int i1, int j1, int i2, int j2, Color color,
            int thickness) {
        Point pt1 = new Point(j1, i1);
        Point pt2 = new Point(j2, i2);
        Imgproc.line(frame, pt1, pt2, new Scalar(color.getBlue(), color.getGreen(), color.getRed()), thickness);
    }

    public static void displayFrame(Mat I, VpHomogeneousMatrix mat, VpCameraParameters cam, double size, int thickness) {
        VpPoint o = new VpPoint(0.0, 0.0, 0.0);
        VpPoint x = new VpPoint(size, 0.0, 0.0);
        VpPoint y = new VpPoint(0.0, size*2, 0.0);
        VpPoint z = new VpPoint(0.0, 0.0, size);

        o.changeFrame(mat);
        o.projection();

        x.changeFrame(mat);
        x.projection();

        y.changeFrame(mat);
        y.projection();

        z.changeFrame(mat);
        z.projection();

        ImageUtils.displayArrow(I, o.get_y() * cam.get_py() + cam.get_v0(), o.get_x() * cam.get_px() + cam.get_u0(),
                x.get_y() * cam.get_py() + cam.get_v0(), x.get_x() * cam.get_px() + cam.get_u0(),
                Color.RED, 4 * thickness, 2 * thickness, thickness);

        ImageUtils.displayArrow(I, o.get_y() * cam.get_py() + cam.get_v0(), o.get_x() * cam.get_px() + cam.get_u0(),
                y.get_y() * cam.get_py() + cam.get_v0(), y.get_x() * cam.get_px() + cam.get_u0(),
                Color.GREEN, 4 * thickness, 2 * thickness, thickness);

//        ImageUtils.displayArrow(I, o.get_y() * cam.get_py() + cam.get_v0(), o.get_x() * cam.get_px() + cam.get_u0(),
//                z.get_y() * cam.get_py() + cam.get_v0(), z.get_x() * cam.get_px() + cam.get_u0(),
//                Color.BLUE, 4 * thickness, 2 * thickness, thickness);
    }

    public static void displayCrossHairCenterImage(Mat frame, double length, Color color, int thickness) {
        Size sizeImage = frame.size();
        double width = sizeImage.width;
        double height = sizeImage.height;

        Point pt1 = new Point(width/2, height/2 - length/2);
        Point pt2 = new Point(width/2, height/2 + length/2);
        Imgproc.line(frame, pt1, pt2, new Scalar(color.getBlue(), color.getGreen(), color.getRed()), thickness);
        pt1 = new Point(width/2 - length/2, height/2);
        pt2 = new Point(width/2 + length/2, height/2);
        Imgproc.line(frame, pt1, pt2, new Scalar(color.getBlue(), color.getGreen(), color.getRed()), thickness);
    }
    public static void displayCrossHairCamera(Mat frame, VpCameraParameters camParam, double length, Color color, int thickness) {



        double w0 = camParam.get_u0();
        double h0 = camParam.get_v0();
        Point pt1 = new Point(w0, h0 - length/2);
        Point pt2 = new Point(w0, h0 + length/2);
        Imgproc.line(frame, pt1, pt2, new Scalar(color.getBlue(), color.getGreen(), color.getRed()), thickness);
        pt1 = new Point(w0 - length/2, h0);
        pt2 = new Point(w0 + length/2, h0);
        Imgproc.line(frame, pt1, pt2, new Scalar(color.getBlue(), color.getGreen(), color.getRed()), thickness);
    }
}
