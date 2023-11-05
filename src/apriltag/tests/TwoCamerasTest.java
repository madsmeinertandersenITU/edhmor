package apriltag.tests;

import apriltag.AprilTagDetector;
import apriltag.AprilTagFamily;
import apriltag.CameraCapture;
import apriltag.Capture;
import apriltag.ImageConverter;
import apriltag.CoppeliaSimCapture;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.opencv.core.Core;
import org.visp.core.VpHomogeneousMatrix;
import org.visp.core.VpImagePoint;
import org.visp.core.VpImageUChar;

import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.remoteApi;
import modules.evaluation.CoppeliaSimulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.lang.Math;
import modules.util.PhysicalSetupConfiguration;

public class TwoCamerasTest {

    static {
        System.loadLibrary("visp_java331");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static CoppeliaSimulator coppeliaSimulator;
    private static remoteApi coppeliaSimApi;
    private static int clientID;

    private static CoppeliaSimCapture coppelia;
    private static CameraCapture camera;

    private static AprilTagDetector cameraAprilTagDetector;
    private static AprilTagDetector simAprilTagDetector;

    private static BufferedImage canvas;
    private static BufferedImage simcanvas;

    public static void main(String[] args) {

        coppeliaSimulator = new CoppeliaSimulator();
        coppeliaSimulator.connect2CoppeliaSim();
        
        camera = new CameraCapture(0, PhysicalSetupConfiguration.getArenaCeilingIntrinsicVispCamParam());
        coppelia = new CoppeliaSimCapture(coppeliaSimulator);

        coppeliaSimApi = coppeliaSimulator.getCoppeliaSimApi();
        clientID = coppeliaSimulator.getClientID();

        if (clientID == -1) {
            System.out.println("Connect to CoppeliaSim to capture image");
        } else {
            // Capture camera image
            camera.capture();

            // Create detector and detect from camera
            cameraAprilTagDetector = new AprilTagDetector(camera.getI(), AprilTagFamily.TAG_36h11, camera.getCamParam());

            if (cameraAprilTagDetector.isDetected()) {

                // Get tag information from camera image
                int[] tagsId = cameraAprilTagDetector.getTagsId();
                double[] tagSize = cameraAprilTagDetector.getTagSize();

                System.out.println(tagsId.length + " tags found. ");
                for (int i = 0; i < tagsId.length; i++) {
                    System.out.println("Tag " + tagsId[i] + " found");
                }

                List<VpHomogeneousMatrix> cMo_vec = cameraAprilTagDetector.getcMo_vec();
                List<Vector3D> transVec3D = cameraAprilTagDetector.getTagPoseTranslationList();
                List<Rotation> rotationVec = cameraAprilTagDetector.getTagPoseRotationList();

                // Get tag0 rotation and translation info
                double[] angles = getEulerAnglesEuclid(rotationVec.get(0));

                System.out.println("Tag 0 angles " + angles[0] * 180 / Math.PI + ", " + angles[1] * 180 / Math.PI + ", "
                        + angles[2] * 180 / Math.PI);

                // Get camera position from tag0 perspective
                Vector3D origin = new Vector3D(0, 0, 0);
                // Vector3D x = new Vector3D(0,-0.2,-2);

                System.out.println("Translation camera-object " + transVec3D.get(0).toArray()[0] + ", "
                        + transVec3D.get(0).toArray()[1] + ", " + transVec3D.get(0).toArray()[2]);
                // Subtract translation from origin
                Vector3D revertTranslation = origin.subtract(transVec3D.get(0));
                System.out.println("Translation object-camera " + revertTranslation.toArray()[0] + ", "
                        + revertTranslation.toArray()[1] + ", " + revertTranslation.toArray()[2]);
                // Inverse rotation of reverse translation
                Vector3D cameraPosition = rotationVec.get(0).applyTo(revertTranslation);
                // Print to check consistency
                System.out.println("Translation object-camera " + cameraPosition.toArray()[0] + ", "
                        + cameraPosition.toArray()[1] + ", " + cameraPosition.toArray()[2]);

                double[] angles1 = getEulerAnglesEuclid(rotationVec.get(0).revert());

                System.out.println("Camera angles " + angles1[0] * 180 / Math.PI + ", " + angles1[1] * 180 / Math.PI
                        + ", " + angles1[2] * 180 / Math.PI);
                // System.out.println("Quaternion rotation "+
                // rotationVec.get(0).revert().getQ0() + ", " +
                // rotationVec.get(0).revert().getQ1() + ", " +
                // rotationVec.get(0).revert().getQ2() + ", " +
                // rotationVec.get(0).revert().getQ3());
                // System.out.println("Quaternion rotation "+ rotationVec.get(0).getQ0() + ", "
                // + rotationVec.get(0).getQ1() + ", " + rotationVec.get(0).getQ2() + ", " +
                // rotationVec.get(0).getQ3());

                // Get position and rotation of tag0 from coppelia
                FloatWA coppeliaSimTag0Position = new FloatWA(3);
                FloatWA coppeliaSimTag0Orientation = new FloatWA(4);
                IntW coppeliaSimTag0Handle = new IntW(0);

                coppeliaSimApi.simxGetObjectHandle(clientID, "Dummy", coppeliaSimTag0Handle, remoteApi.simx_opmode_blocking);

                coppeliaSimApi.simxGetObjectPosition(clientID, coppeliaSimTag0Handle.getValue(), -1, coppeliaSimTag0Position,
                        remoteApi.simx_opmode_blocking);

                coppeliaSimApi.simxGetObjectOrientation(clientID, coppeliaSimTag0Handle.getValue(), -1, coppeliaSimTag0Orientation,
                        remoteApi.simx_opmode_blocking);
                // Convert to Vector3D and Rotation
                Vector3D tag0Position = new Vector3D(coppeliaSimTag0Position.getArray()[0], coppeliaSimTag0Position.getArray()[1],
                        coppeliaSimTag0Position.getArray()[2]);
                Rotation tag0Rotation = new Rotation(RotationOrder.XYZ, coppeliaSimTag0Orientation.getArray()[0],
                        coppeliaSimTag0Orientation.getArray()[1], coppeliaSimTag0Orientation.getArray()[2]);

                double[] angles2 = getEulerAnglesEuclid(tag0Rotation);
                // Check translation and rotation
                System.out.println("Translation Tag 0 " + coppeliaSimTag0Position.getArray()[0] + ", "
                        + coppeliaSimTag0Position.getArray()[1] + ", " + coppeliaSimTag0Position.getArray()[2]);
                System.out.println("Tag 0 angles " + angles2[0] * 180 / Math.PI + ", " + angles2[1] * 180 / Math.PI
                        + ", " + angles2[2] * 180 / Math.PI);
                System.out.println("Tag 0 angles " + coppeliaSimTag0Orientation.getArray()[0] * 180 / Math.PI + " "
                        + coppeliaSimTag0Orientation.getArray()[1] * 180 / Math.PI + " "
                        + coppeliaSimTag0Orientation.getArray()[2] * 180 / Math.PI);

                // Calculate new camera rotation and translation
                // The vision sensor in CoppeliaSim needs to be rotated 180 degrees around its own
                // Zaxis
                Rotation newCameraRotation = rotationVec.get(0).applyTo(new Rotation(new Vector3D(0, 0, 1), Math.PI));// tag0Rotation.applyTo(rotationVec.get(0).revert());
                Vector3D newCameraPosition = cameraPosition;// (tag0Rotation.applyTo(rotationVec.get(0).applyInverseTo(origin.subtract(transVec3D.get(0))))).add(tag0Position);
                // Get new camera angles
                double[] newCameraAngles = getEulerAnglesEuclid(newCameraRotation);

                // Translate to CoppeliaSim variables
                IntW coppeliaSimCameraHandle = new IntW(0);
                FloatWA coppeliaSimNewCameraPosition = new FloatWA(3);

                coppeliaSimNewCameraPosition.getArray()[0] = (float) newCameraPosition.getX();
                coppeliaSimNewCameraPosition.getArray()[1] = (float) newCameraPosition.getY();
                coppeliaSimNewCameraPosition.getArray()[2] = (float) newCameraPosition.getZ();

                FloatWA coppeliaSimNewCameraOrientation = new FloatWA(3);
                coppeliaSimNewCameraOrientation.getArray()[0] = (float) newCameraAngles[0];
                coppeliaSimNewCameraOrientation.getArray()[1] = (float) newCameraAngles[1];
                coppeliaSimNewCameraOrientation.getArray()[2] = (float) newCameraAngles[2];

                // Set new camera position and translation in CoppeliaSim
                coppeliaSimApi.simxGetObjectHandle(clientID, "Vision_sensor", coppeliaSimCameraHandle,
                        remoteApi.simx_opmode_blocking);

                coppeliaSimApi.simxSetObjectPosition(clientID, coppeliaSimCameraHandle.getValue(), -1, coppeliaSimNewCameraPosition,
                        remoteApi.simx_opmode_blocking);
                coppeliaSimApi.simxSetObjectOrientation(clientID, coppeliaSimCameraHandle.getValue(), -1, coppeliaSimNewCameraOrientation,
                        remoteApi.simx_opmode_blocking);
                // coppeliaSimApi.simxGetObjectQuaternion(clientID, coppeliaSimCameraHandle.getValue(), -1,
                // coppeliaSimCameraQuaternion,
                // remoteApi.simx_opmode_blocking);

                JFrame f = createFrame(camera.getbImage(), camera.getI());
                StringBuilder info = new StringBuilder();

                for (int i = 0; i < tagsId.length; i++) {

                    CoppeliaSimTest.displayFrame(canvas, cMo_vec.get(i), cameraAprilTagDetector.getCamParam(), tagSize[i] / 2,
                            3);
                }

                List<List<VpImagePoint>> tags_corners = cameraAprilTagDetector.getTags_corners();
                for (List<VpImagePoint> corners : tags_corners) {
                    CoppeliaSimTest.displayLine(canvas, corners.get(0).get_i(), corners.get(0).get_j(), corners.get(1).get_i(),
                            corners.get(1).get_j(), Color.RED, 3);
                    CoppeliaSimTest.displayLine(canvas, corners.get(0).get_i(), corners.get(0).get_j(), corners.get(3).get_i(),
                            corners.get(3).get_j(), Color.GREEN, 3);
                    CoppeliaSimTest.displayLine(canvas, corners.get(1).get_i(), corners.get(1).get_j(), corners.get(2).get_i(),
                            corners.get(2).get_j(), Color.YELLOW, 3);
                    CoppeliaSimTest.displayLine(canvas, corners.get(2).get_i(), corners.get(2).get_j(), corners.get(3).get_i(),
                            corners.get(3).get_j(), Color.BLUE, 3);
                }

                for (int i = 0; i < tagsId.length; i++) {
                    double[] centroid = CoppeliaSimTest.computeCentroid(tags_corners.get(i));
                    CoppeliaSimTest.displayText(canvas, new String("Id: " + String.valueOf(tagsId[i])), centroid[0] + 10,
                            centroid[1] + 20, Color.RED, 3);

                    info.append("Tag id: ");
                    info.append(tagsId[i]);
                    VpHomogeneousMatrix cMo = cMo_vec.get(i);
                    info.append("\ncMo:\n" + cMo);
                    info.append("\n");
                    info.append("\n");
                }

                f.repaint();

                coppelia.setCoppeliaSimulator(coppeliaSimulator);
                coppelia.capture();

                simAprilTagDetector = new AprilTagDetector(coppelia.getI(), AprilTagFamily.TAG_36h11, coppelia.getCamParam());

                // Get tag information from camera image
                int[] simTagsId = simAprilTagDetector.getTagsId();
                double[] simTagSize = simAprilTagDetector.getTagSize();

                System.out.println(simTagsId.length + " tags found. ");
                for (int i = 0; i < simTagsId.length; i++) {
                    System.out.println("Tag " + simTagsId[i] + " found");
                }

                List<VpHomogeneousMatrix> simcMo_vec = simAprilTagDetector.getcMo_vec();
                // List<Vector3D> simtransVec3D =
                // cameraAprilTagDetector.getTagPoseTranslationList();
                // List<Rotation> simrotationVec =
                // cameraAprilTagDetector.getTagPoseRotationList();

                JFrame simf = createFrameSim(coppelia.getbImage(), coppelia.getI());
                StringBuilder siminfo = new StringBuilder();

                for (int i = 0; i < simTagsId.length; i++) {

                    CoppeliaSimTest.displayFrame(simcanvas, simcMo_vec.get(i), simAprilTagDetector.getCamParam(),
                            simTagSize[i] / 2, 3);
                }

                List<List<VpImagePoint>> simtags_corners = simAprilTagDetector.getTags_corners();
                for (List<VpImagePoint> corners : simtags_corners) {
                    CoppeliaSimTest.displayLine(simcanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                            corners.get(1).get_i(), corners.get(1).get_j(), Color.RED, 3);
                    CoppeliaSimTest.displayLine(simcanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                            corners.get(3).get_i(), corners.get(3).get_j(), Color.GREEN, 3);
                    CoppeliaSimTest.displayLine(simcanvas, corners.get(1).get_i(), corners.get(1).get_j(),
                            corners.get(2).get_i(), corners.get(2).get_j(), Color.YELLOW, 3);
                    CoppeliaSimTest.displayLine(simcanvas, corners.get(2).get_i(), corners.get(2).get_j(),
                            corners.get(3).get_i(), corners.get(3).get_j(), Color.BLUE, 3);
                }

                for (int i = 0; i < simTagsId.length; i++) {
                    double[] simcentroid = CoppeliaSimTest.computeCentroid(simtags_corners.get(i));
                    CoppeliaSimTest.displayText(simcanvas, new String("Id: " + String.valueOf(simTagsId[i])),
                            simcentroid[0] + 10, simcentroid[1] + 20, Color.RED, 3);

                    siminfo.append("Tag id: ");
                    siminfo.append(simTagsId[i]);
                    VpHomogeneousMatrix cMo = simcMo_vec.get(i);
                    siminfo.append("\ncMo:\n" + cMo);
                    siminfo.append("\n");
                    siminfo.append("\n");
                }

                simf.repaint();

                // Check tags
                if (tagsId.length > simTagsId.length || tagsId.length < simTagsId.length) { // May be too strict
                    System.out.println("Number of tags found in camera does not match number of tags in simulator");
                } else {
                    boolean match = false;
                    for (int i = 0; i < tagsId.length; i++) {
                        boolean tagMatch = false;
                        for (int j = 0; j < simTagSize.length; j++) {
                            if (tagsId[i] == simTagsId[j]) {
                                tagMatch = true;
                            }
                        }
                        match = tagMatch;
                    }
                    if (match) {
                        System.out.println("Tags match!!");
                    } else {
                        System.out.println("Tags don't match");
                    }
                }

            } else {
                System.out.println("No Apriltags detected in the imagre from the camera.");
            }
        }

        coppeliaSimApi.simxFinish(-1);

    }

    private static double[] getEulerAnglesEuclid(Rotation rotation) {
        double[] angles = new double[3];

        // Get quaternion components
        double w = -1 * rotation.getQ0();
        double x = rotation.getQ1();
        double y = rotation.getQ2();
        double z = rotation.getQ3();

        angles[0] = 0;
        angles[1] = 0;
        angles[2] = 0;

        double test = w * y + x * z; // Test for a singularity when test gets near 0.5 or -0.5
        if (test > 0.499) { // Can be adjusted
            angles[0] = 2 * FastMath.atan2(x, w);
            angles[1] = Math.PI / 2;
            angles[2] = 0;
        } else if (test < -0.499) { // Can be adjusted
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

    public static JFrame createFrameSim(BufferedImage bImage, VpImageUChar I) {

        simcanvas = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage tmp = ImageConverter.VpImageUChar2BufferedImage(I);
        if (tmp.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_RGB) {
            canvas = tmp;
        } else {
            Graphics2D g2d = simcanvas.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
        }
        JLabel picLabel = new JLabel(new ImageIcon(simcanvas));
        JPanel jPanel = new JPanel();
        jPanel.add(picLabel);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(new Dimension(bImage.getWidth(), bImage.getHeight()));
        f.add(jPanel);
        f.setVisible(true);
        f.setLocation(600, 0);

        return f;
    }
}
