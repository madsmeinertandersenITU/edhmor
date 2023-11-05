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

import static apriltag.ImageUtils.displayArrow;
import static apriltag.ImageUtils.displayFrame;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.visp.core.VpCameraParameters;
import org.visp.core.VpHomogeneousMatrix;
import org.visp.core.VpImageUChar;
import org.visp.core.VpPoint;
import org.visp.core.VpQuaternionVector;
import org.visp.core.VpTranslationVector;

/**
 * BufferedImageUtils.java Created on 12/09/2021
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class BufferedImageUtils {

    public static Vector3D paintTags(VpImageUChar I, BufferedImage frame, VpCameraParameters camParam) {
        double tagSize = 0.031;
        AprilTagDetector detector = new AprilTagDetector(I, AprilTagFamily.TAG_36h11, camParam, tagSize);
        if (detector.isDetected()) {
            List<VpHomogeneousMatrix> cMo_vec = detector.getcMo_vec();
            List<VpQuaternionVector> quat_vec = detector.getQuat_vec();
            List<VpTranslationVector> trans_vec = detector.getTrans_vec();
            //List<Vector3D> transVec3D = detector.getTagPoseTranslationList();
            //List<Rotation> rotationVec = detector.getTagPoseRotationList();
            int[] tagsId = detector.getTagsId();
            for (int i = 0; i < tagsId.length; i++) {

                //System.out.println("Translation " + trans_vec.get(i).toString());
//                System.out.println("Translation vec3d " + transVec3D.get(i).toArray()[0] + ", " + transVec3D.get(i).toArray()[1] + ", " + transVec3D.get(i).toArray()[2]);

                //double[] angles = getEulerAnglesEuclid(rotationVec.get(i));
                //System.out.println("Tag " + i +" angles " + angles[0]*180/Math.PI+", "+angles[1]*180/Math.PI+", "+angles[2]*180/Math.PI);
                //System.out.println("Quaternion w: " + quat_vec.get(i).w() + " x: " + quat_vec.get(i).x() + " y: " + quat_vec.get(i).y() + " z: " + quat_vec.get(i).z());
//                System.out.println("Quaternion rotation " + rotationVec.get(i).getQ0() + ", " + rotationVec.get(i).getQ1() + ", " + rotationVec.get(i).getQ2() + ", " + rotationVec.get(i).getQ3());

                displayFrame(frame, cMo_vec.get(i), detector.getCamParam(), tagSize / 2, 3);
            }
        }
        return null;
    }

    public static void displayArrow(BufferedImage I, double i1, double j1, double i2, double j2, Color color, int w, int h, int thickness) {
        displayArrow(I, (int) i1, (int) j1, (int) i2, (int) j2, color, w, h,thickness);
    }

    public static void displayArrow(BufferedImage I, int i1, int j1, int i2, int j2, Color color, int w, int h, int thickness) {
        Graphics2D g = I.createGraphics();
        g.setStroke(new BasicStroke(thickness));
        g.setColor(color);

        double a = i2 - i1;
        double b = j2 - j1;
        double lg = Math.sqrt(a*a + b*b);

        if (Math.abs(a) <= Math.ulp(1.0) &&
            Math.abs(b) <= Math.ulp(1.0)) {
        } else {
          a /= lg;
          b /= lg;

          double i3 = i2 - w*a;
          double j3 = j2 - w*b;

          double i4 = i3 - b*h;
          double j4 = j3 + a*h;

          double dist = Math.sqrt((i2 - i4)*(i2 - i4) + (j2 - j4)*(j2 - j4));
          if (lg > 2 * dist) {
            displayLine(I, i2, j2, i4, j4, color, thickness);
          }

          i4 = i3 + b*h;
          j4 = j3 - a*h;

          dist = Math.sqrt((i2 - i4)*(i2 - i4) + (j2 - j4)*(j2 - j4));
          if (lg > 2 * dist) {
              displayLine(I, i2, j2, i4, j4, color, thickness);
          }

          displayLine(I, i1, j1, i2, j2, color, thickness);
        }
    }
    public static void displayLine(BufferedImage I, double i1, double j1, double i2, double j2, Color color, int thickness) {
        displayLine(I, (int) i1, (int) j1, (int) i2, (int) j2, color, thickness);
    }

    public static void displayLine(BufferedImage I, int i1, int j1, int i2, int j2, Color color, int thickness) {
        Graphics2D g = I.createGraphics();
        g.setStroke(new BasicStroke(thickness));
        g.setColor(color);
        g.drawLine(j1, i1, j2, i2);
    }

    public static void displayFrame(BufferedImage I, VpHomogeneousMatrix mat, VpCameraParameters cam, double size, int thickness) {
        VpPoint o = new VpPoint(0.0, 0.0, 0.0);
        VpPoint x = new VpPoint(size, 0.0, 0.0);
        VpPoint y = new VpPoint(0.0, size, 0.0);
        VpPoint z = new VpPoint(0.0, 0.0, size);

        o.changeFrame(mat);
        o.projection();

        x.changeFrame(mat);
        x.projection();

        y.changeFrame(mat);
        y.projection();

        z.changeFrame(mat);
        z.projection();

        displayArrow(I, o.get_y()*cam.get_py() + cam.get_v0(), o.get_x()*cam.get_px() + cam.get_u0(),
                 x.get_y()*cam.get_py() + cam.get_v0(), x.get_x()*cam.get_px() + cam.get_u0(),
                 Color.RED, 4 * thickness, 2 * thickness, thickness);

        displayArrow(I, o.get_y()*cam.get_py() + cam.get_v0(), o.get_x()*cam.get_px() + cam.get_u0(),
                 y.get_y()*cam.get_py() + cam.get_v0(), y.get_x()*cam.get_px() + cam.get_u0(),
                 Color.GREEN, 4 * thickness, 2 * thickness, thickness);

        displayArrow(I, o.get_y()*cam.get_py() + cam.get_v0(), o.get_x()*cam.get_px() + cam.get_u0(),
                 z.get_y()*cam.get_py() + cam.get_v0(), z.get_x()*cam.get_px() + cam.get_u0(),
                 Color.BLUE, 4 * thickness, 2 * thickness, thickness);
    }

}
