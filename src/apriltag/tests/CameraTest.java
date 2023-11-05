package apriltag.tests;

import apriltag.AprilTagDetector;
import apriltag.AprilTagFamily;
import apriltag.ImageConverter;
import apriltag.CameraCapture;
import apriltag.CameraType;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import modules.util.PhysicalSetupConfiguration;

import org.visp.core.VpCameraParameters;
import org.visp.core.VpHomogeneousMatrix;
import org.visp.core.VpImagePoint;
import org.visp.core.VpImageUChar;
import org.visp.core.VpPoint;
import org.visp.core.VpQuaternionVector;
import org.visp.core.VpTranslationVector;
import org.visp.detection.VpDetectorAprilTag;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.opencv.core.Core;

public class CameraTest {

    static {
        System.loadLibrary("visp_java341");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    private static VpImageUChar I;
    //private static VpImageRGBa I_RGB;

    private static VpDetectorAprilTag detector;
    //private static VpCameraParameters cam = new VpCameraParameters(615.1674805, 615.1675415, 312.1889954, 243.4373779);
    //private static Object[][] data = {{new Integer(-1), new Double(0.053)}};
    
    private static BufferedImage canvas;
    private static BufferedImage bImage;
    private static CameraCapture ImageCapture;
    private static AprilTagDetector aprilTagDetector;

    public static void main(String[] args) {
    	
    	
    	//Uncomment to get the example of apriltags in visp images
    	//ImageCapture.readImageFile("C:/visp-ws/visp-images/AprilTag/benchmark/640x480/tag36_11_640x480.png");
    	
    	//Uncomment to get a image from the webcam
    	ImageCapture = new CameraCapture(CameraType.ARENA_CEILING_CAMERA);
    	ImageCapture.capture();
       
    	
    	//VpImageIo.write(ImageCapture.getI(),"/home/fai/camera-calibration/focus.jpg");
        
        detectTags(ImageCapture.getI(),ImageCapture.getbImage());
        System.out.println("END");

    }
    
    

    public static void detectTags(VpImageUChar I, BufferedImage bImage) {
    	
    	aprilTagDetector = new AprilTagDetector(I, AprilTagFamily.TAG_36h11, ImageCapture.getCamParam());

        //detector = new VpDetectorAprilTag();
        //detector.setAprilTagFamily(0); //"TAG_36h11"
        
        JFrame f = createFrame(bImage, I);

        StringBuilder info = new StringBuilder();
        if (aprilTagDetector.isDetected()) {
            
            int[] tagsId = aprilTagDetector.getTagsId();
            double[] tagSize = aprilTagDetector.getTagSize();
            
            System.out.println(tagsId.length + " tags found. ");
            for (int i = 0; i < tagsId.length; i++) {
                System.out.println("Tag " + tagsId[i] + " found");
            }

            List<VpHomogeneousMatrix> cMo_vec = aprilTagDetector.getcMo_vec();
            List<VpQuaternionVector> quat_vec = aprilTagDetector.getQuat_vec();
            List<VpTranslationVector> trans_vec = aprilTagDetector.getTrans_vec();
            List<Vector3D> transVec3D = aprilTagDetector.getTagPoseTranslationList();
            List<Rotation> rotationVec = aprilTagDetector.getTagPoseRotationList();
            for (int i = 0; i < tagsId.length; i++) {
                
                //System.out.println("Translation " + trans_vec.get(i).toString());
            	System.out.println("Translation vec3d " + transVec3D.get(i).toArray()[0]+", "+transVec3D.get(i).toArray()[1]+", "+transVec3D.get(i).toArray()[2]);
                
            	double[] angles = getEulerAnglesEuclid(rotationVec.get(i)); 
				
				System.out.println("Tag " + i +" angles " + angles[0]*180/Math.PI+", "+angles[1]*180/Math.PI+", "+angles[2]*180/Math.PI);
            	
                //System.out.println("Quaternion w: " + quat_vec.get(i).w() + " x: " + quat_vec.get(i).x() + " y: " + quat_vec.get(i).y() + " z: " + quat_vec.get(i).z());
                System.out.println("Quaternion rotation "+ rotationVec.get(i).getQ0() + ", " + rotationVec.get(i).getQ1() + ", " + rotationVec.get(i).getQ2() + ", " + rotationVec.get(i).getQ3());
                
                displayFrame(canvas, cMo_vec.get(i), aprilTagDetector.getCamParam(), tagSize[i] / 2, 3);
            }
            
            if (tagsId.length > 1) {
                Rotation relModuleRotation = rotationVec.get(0).applyInverseTo(rotationVec.get(1));
                System.out.println("Quaternion rotation " + relModuleRotation.getQ0() + ", " + relModuleRotation.getQ1() + ", " + relModuleRotation.getQ2() + ", " + relModuleRotation.getQ3());

                Vector3D relModulePosition = rotationVec.get(0).applyInverseTo((transVec3D.get(1).subtract(transVec3D.get(0))));
                System.out.println("Translation vec3d " + relModulePosition.getX() + ", " + relModulePosition.getY() + ", " + relModulePosition.getZ());

                Vector3D moduleFaceNormal = relModuleRotation.applyTo(new Vector3D(0, 0, 0.1));
                System.out.println("Normal " + moduleFaceNormal.getX() + ", " + moduleFaceNormal.getY() + ", " + moduleFaceNormal.getZ());
            }
            
                      
            List<List<VpImagePoint>> tags_corners = aprilTagDetector.getTags_corners();
            for (List<VpImagePoint> corners : tags_corners) {
                displayLine(canvas, corners.get(0).get_i(), corners.get(0).get_j(),
                        corners.get(1).get_i(), corners.get(1).get_j(), Color.RED, 3);
                displayLine(canvas, corners.get(0).get_i(), corners.get(0).get_j(),
                        corners.get(3).get_i(), corners.get(3).get_j(), Color.GREEN, 3);
                displayLine(canvas, corners.get(1).get_i(), corners.get(1).get_j(),
                        corners.get(2).get_i(), corners.get(2).get_j(), Color.YELLOW, 3);
                displayLine(canvas, corners.get(2).get_i(), corners.get(2).get_j(),
                        corners.get(3).get_i(), corners.get(3).get_j(), Color.BLUE, 3);
            }

            for (int i = 0; i < tagsId.length; i++) {
                double[] centroid = computeCentroid(tags_corners.get(i));
                displayText(canvas, new String("Id: " + String.valueOf(tagsId[i])),
                        centroid[0] + 10, centroid[1] + 20, Color.RED, 3);

                info.append("Tag id: ");
                info.append(tagsId[i]);
                VpHomogeneousMatrix cMo = cMo_vec.get(i);
                info.append("\ncMo:\n" + cMo);
                info.append("\n");
                info.append("\n");
            }

            f.repaint();

        }

    }
    
    public static JFrame createFrame(BufferedImage bImage, VpImageUChar I) {
    	
    	canvas = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage tmp = ImageConverter.VpImageUChar2BufferedImage(I);
        if (tmp.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_RGB) {
            canvas = tmp;
        } else {
            Graphics2D g2d = canvas.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
        }
        JLabel picLabel = new JLabel(new ImageIcon(canvas));
        JPanel jPanel = new JPanel();
        jPanel.add(picLabel);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(new Dimension(bImage.getWidth(), bImage.getHeight()));
        f.add(jPanel);
        f.setVisible(true);
        
        return f;
    }
    
    
    


    public static double[] computeCentroid(List<VpImagePoint> corners) {
        double[] centroid = {0, 0};

        for (VpImagePoint pt : corners) {
            centroid[0] += pt.get_i();
            centroid[1] += pt.get_j();
        }
        if (!corners.isEmpty()) {
            centroid[0] /= corners.size();
            centroid[1] /= corners.size();
        }

        return centroid;
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

    public static void displayArrow(BufferedImage I, double i1, double j1, double i2, double j2, Color color, int w, int h, int thickness) {
        displayArrow(I, (int) i1, (int) j1, (int) i2, (int) j2, color, w, h, thickness);
    }

    public static void displayArrow(BufferedImage I, int i1, int j1, int i2, int j2, Color color, int w, int h, int thickness) {
        Graphics2D g = I.createGraphics();
        g.setStroke(new BasicStroke(thickness));
        g.setColor(color);

        double a = i2 - i1;
        double b = j2 - j1;
        double lg = Math.sqrt(a * a + b * b);

        if (Math.abs(a) <= Math.ulp(1.0)
                && Math.abs(b) <= Math.ulp(1.0)) {
        } else {
            a /= lg;
            b /= lg;

            double i3 = i2 - w * a;
            double j3 = j2 - w * b;

            double i4 = i3 - b * h;
            double j4 = j3 + a * h;

            double dist = Math.sqrt((i2 - i4) * (i2 - i4) + (j2 - j4) * (j2 - j4));
            if (lg > 2 * dist) {
                displayLine(I, i2, j2, i4, j4, color, thickness);
            }

            i4 = i3 + b * h;
            j4 = j3 - a * h;

            dist = Math.sqrt((i2 - i4) * (i2 - i4) + (j2 - j4) * (j2 - j4));
            if (lg > 2 * dist) {
                displayLine(I, i2, j2, i4, j4, color, thickness);
            }

            displayLine(I, i1, j1, i2, j2, color, thickness);
        }
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

        displayArrow(I, o.get_y() * cam.get_py() + cam.get_v0(), o.get_x() * cam.get_px() + cam.get_u0(),
                x.get_y() * cam.get_py() + cam.get_v0(), x.get_x() * cam.get_px() + cam.get_u0(),
                Color.RED, 4 * thickness, 2 * thickness, thickness);

        displayArrow(I, o.get_y() * cam.get_py() + cam.get_v0(), o.get_x() * cam.get_px() + cam.get_u0(),
                y.get_y() * cam.get_py() + cam.get_v0(), y.get_x() * cam.get_px() + cam.get_u0(),
                Color.GREEN, 4 * thickness, 2 * thickness, thickness);

        displayArrow(I, o.get_y() * cam.get_py() + cam.get_v0(), o.get_x() * cam.get_px() + cam.get_u0(),
                z.get_y() * cam.get_py() + cam.get_v0(), z.get_x() * cam.get_px() + cam.get_u0(),
                Color.BLUE, 4 * thickness, 2 * thickness, thickness);
        
    }

    public static void displayText(BufferedImage I, String text, double i, double j, Color color, int thickness) {
        displayText(I, text, (int) i, (int) j, color, thickness);
    }

    public static void displayText(BufferedImage I, String text, int i, int j, Color color, int thickness) {
        Graphics2D g = I.createGraphics();
        g.setStroke(new BasicStroke(thickness));
        g.setColor(color);
        g.drawString(text, j, i);
    }
    
    private static double[] getEulerAnglesEuclid(Rotation rotation) {
        double[] angles = new double[3];

        //Get quaternion components
        double w = -1*rotation.getQ0();
        double x = rotation.getQ1();
        double y = rotation.getQ2();
        double z = rotation.getQ3();

        angles[0] = 0;
        angles[1] = 0;
        angles[2] = 0;

        double test = w * y + x * z; //Test for a singularity when test gets near 0.5 or -0.5
        if (test > 0.499) { //Can be adjusted
            angles[0] = 2 * FastMath.atan2(x, w);
            angles[1] = Math.PI / 2;
            angles[2] = 0;
        } else if (test < -0.499) { //Can be adjusted
            angles[0] = 2 * FastMath.atan2(x, w);
            angles[1] = -Math.PI / 2;
            angles[2] = 0;
        } else {
            angles[0] = FastMath.atan2(2 * (w * x - y * z), 1 - 2 * ((x * x) + (y * y)));
            angles[1] = FastMath.asin(2 * (w * y + x * z));
            angles[2] = FastMath.atan2(2 * (w * z - x * y), 1 - 2 * ((y * y) + (z * z)));
        }
//        System.out.println("Quaternion: " + rotation.getQ0() + " " + rotation.getQ1() + " " + rotation.getQ2() + " "
//                + rotation.getQ3());
//        System.out.println("Test = " + test);
//        System.out.println("Euler Angles: " + angles[0]/Math.PI*180.0+ " "+ angles[1]/Math.PI*180.0 + " " + angles[2]/Math.PI*180.0);
        return angles;
    }


}
