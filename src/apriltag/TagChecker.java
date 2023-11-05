package apriltag;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.visp.core.VpCameraParameters;
import org.visp.core.VpHomogeneousMatrix;
import org.visp.core.VpImagePoint;
import org.visp.core.VpImageUChar;
import org.visp.core.VpPoint;

import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.remoteApi;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import modules.evaluation.CoppeliaSimulator;

public class TagChecker {

    private CoppeliaSimCapture coppelia;
    private CameraCapture camera;
    private int realCameraIndex = 0;
    private boolean stop = false;

    private List<Integer> realTagsFound;

    private AprilTagDetector cameraAprilTagDetector;
    private AprilTagDetector simAprilTagDetector;

    private VpCameraParameters realCamParam;

    private CoppeliaSimulator coppeliaSimulator;
    private remoteApi coppeliaSimApi;
    private int clientID = -1;

    private final static double positionThr = 0.1;
    private final static double rotationThr = Math.PI / 3.0; // Max Pi
    private final static double goodScore = 0.65; // Good score

    private static BufferedImage realCanvas;
    private static BufferedImage simCanvas;
    private JFrame realF;
    private JFrame simF;

    private JLabel realPicLabel;
    private JLabel simPicLabel;

    private int referenceTag = 0;

    private List<Integer> globalTagShowMask;
    private List<Integer> currentTagShowMask;
    private boolean spacePressed = false;

    private boolean noReferenceTag = false, noTags = false,
            noSameNumerTags = false, noSimTags = false;

    private boolean assembleCompleted = false;

    private final Font font = new Font("Helvetica", Font.PLAIN, 25);

    public TagChecker(int realCameraIndex, VpCameraParameters realCamParam, ImageResolution resolution, CoppeliaSimulator simulator) {
        this.realCameraIndex = realCameraIndex;
        this.realCamParam = realCamParam;
        setCoppeliaSimulator(simulator);

        camera = new CameraCapture(realCameraIndex, realCamParam, resolution);
        coppelia = new CoppeliaSimCapture(simulator);

        // TODO: change to a more appropiate position
        IntW cuboidHandle = new IntW(0);

        coppeliaSimApi.simxGetObjectHandle(clientID, "Cuboid", cuboidHandle, remoteApi.simx_opmode_blocking);

        coppeliaSimApi.simxSetModelProperty(clientID, cuboidHandle.getValue(), remoteApi.sim_modelproperty_not_dynamic,
                remoteApi.simx_opmode_blocking);

    }

    public TagChecker(int realCameraIndex, VpCameraParameters realCamParam, ImageResolution resolution, CoppeliaSimulator simulator, int referenceTag) {
        this(realCameraIndex, realCamParam, resolution, simulator);
        this.referenceTag = referenceTag;
    }

    public CoppeliaSimulator getCoppeliaSimulator() {
        return coppeliaSimulator;
    }

    public final void setCoppeliaSimulator(CoppeliaSimulator simulator) {
        this.coppeliaSimulator = simulator;
        this.coppeliaSimApi = simulator.getCoppeliaSimApi();
        this.clientID = simulator.getClientID();
    }

    public void connect2CoppeliaSim() {
        System.out.println("connecting to coppeliaSim...");
        coppeliaSimulator = new CoppeliaSimulator();
        coppeliaSimulator.connect2CoppeliaSim();

        coppeliaSimApi = coppeliaSimulator.getCoppeliaSimApi();
        clientID = coppeliaSimulator.getClientID();

    }

    public int getRealCameraIndex() {
        return realCameraIndex;
    }

    public void setRealCameraIndex(int realCameraIndex) {
        this.realCameraIndex = realCameraIndex;
    }

    private int findTagPositionInList(int[] list, int tag) {
        int position = -1;
        for (int i = 0; i < list.length; i++) {
            if (list[i] == tag) {
                position = i;
            }
        }
        return position;
    }

    private void changeSimCameraPosition() {
        if (cameraAprilTagDetector.isDetected()) {

            int[] realTagsId = cameraAprilTagDetector.getTagsId();

            int referenceTagPosition = findTagPositionInList(realTagsId, referenceTag);

            if (referenceTagPosition != -1) {

                List<Vector3D> realTransVec = cameraAprilTagDetector.getTagPoseTranslationList();
                List<Rotation> realRotationVec = cameraAprilTagDetector.getTagPoseRotationList();

                // Get camera position from tag0 perspective
                Vector3D origin = new Vector3D(0, 0, 0);

                Vector3D revertTranslation = origin.subtract(realTransVec.get(referenceTagPosition));

                // Inverse rotation of reverse translation
                Vector3D cameraPosition = realRotationVec.get(referenceTagPosition).applyTo(revertTranslation);

                // Calculate new camera rotation and translation
                // The vision sensor in coppeliaSim needs to be rotated 180 degrees around its own
                // Zaxis
                Rotation newCameraRotation = realRotationVec.get(referenceTagPosition)
                        .applyTo(new Rotation(new Vector3D(0, 0, 1), Math.PI));// tag0Rotation.applyTo(rotationVec.get(0).revert());
                Vector3D newCameraPosition = cameraPosition.add(new Vector3D(0.0, 0.0, 0.05));// (tag0Rotation.applyTo(rotationVec.get(0).applyInverseTo(origin.subtract(transVec3D.get(0))))).add(tag0Position);
                // Get new camera angles
                double[] newCameraAngles = getEulerAnglesEuclid(newCameraRotation);

                // Translate to coppeliaSim variables
                IntW coppeliaSimCameraHandle = new IntW(0);
                FloatWA coppeliaSimNewCameraPosition = new FloatWA(3);

                coppeliaSimNewCameraPosition.getArray()[0] = (float) newCameraPosition.getX();
                coppeliaSimNewCameraPosition.getArray()[1] = (float) newCameraPosition.getY();
                coppeliaSimNewCameraPosition.getArray()[2] = (float) newCameraPosition.getZ();

                FloatWA coppeliaSimNewCameraOrientation = new FloatWA(3);
                coppeliaSimNewCameraOrientation.getArray()[0] = (float) newCameraAngles[0];
                coppeliaSimNewCameraOrientation.getArray()[1] = (float) newCameraAngles[1];
                coppeliaSimNewCameraOrientation.getArray()[2] = (float) newCameraAngles[2];

                // Set new camera position and translation in coppeliaSim
                coppeliaSimApi.simxGetObjectHandle(clientID, "Vision_sensor", coppeliaSimCameraHandle,
                        remoteApi.simx_opmode_blocking);

                coppeliaSimApi.simxSetObjectPosition(clientID, coppeliaSimCameraHandle.getValue(), -1, coppeliaSimNewCameraPosition,
                        remoteApi.simx_opmode_blocking);
                coppeliaSimApi.simxSetObjectOrientation(clientID, coppeliaSimCameraHandle.getValue(), -1, coppeliaSimNewCameraOrientation,
                        remoteApi.simx_opmode_blocking);
                noReferenceTag = false;
            } else {
                //System.out.println("No reference tag found in image");
                noReferenceTag = true;
            }

            noTags = false;
        } else {
            //System.out.println("No Apriltags detected in the image from the camera.");
            noTags = true;
        }
    }

    public void positionCameraOnce() {

        // Capture camera image
        camera.capture();

        // Create detector and detect from camera
        cameraAprilTagDetector = new AprilTagDetector(camera.getI(), AprilTagFamily.TAG_36h11, realCamParam);

        changeSimCameraPosition();

    }

    public void positionCameraContinous() {

        // Capture camera image
        camera.capture();

        // Create detector and detect from camera
        cameraAprilTagDetector.setI(camera.getI());
        cameraAprilTagDetector.detect();

        changeSimCameraPosition();
    }

    private double[][] createMatchMatrix() {
        int[] realTagsId = cameraAprilTagDetector.getTagsId();
        double[][] matchMatrix;

        if (simAprilTagDetector.isDetected()) {

            // Get tag information from camera image
            int[] simTagsId = simAprilTagDetector.getTagsId();

            // Check tags
            this.noSameNumerTags = false;
            if (realTagsId.length > simTagsId.length || realTagsId.length < simTagsId.length) { // May be too
                // strict
                //System.out.println("Number of tags doesn't match");
                this.noSameNumerTags = true;
            }

            matchMatrix = new double[simTagsId.length][5];
            for (int i = 0; i < matchMatrix.length; i++) {
                matchMatrix[i][0] = simTagsId[i];
            }
            for (int i = 0; i < matchMatrix.length; i++) {
                for (int j = 0; j < realTagsId.length; j++) {
                    if (simTagsId[i] == realTagsId[j]) {
                        matchMatrix[i][1] = 1;
                        matchMatrix[i][2] = j;
                    }
                }
            }
            noSimTags = false;
            return matchMatrix;
        } else {
            noSimTags = true;
            //System.out.println("No tag detected in sim");
            return null;
        }
    }

    public double[][] checkTagPresenceOnce(boolean show) {

        if (cameraAprilTagDetector.isDetected()) {

            coppelia.captureCoppeliaSimImageOnce();

            simAprilTagDetector = new AprilTagDetector(coppelia.getI(), AprilTagFamily.TAG_36h11, coppelia.getCamParam());

            // show images if set
            if (show) {
                showImages();
            }

            double[][] matchMatrix = createMatchMatrix();

            return matchMatrix;
        } else {
            return null;
        }
    }

    public double[][] checkTagPresenceContinous(boolean show) {

        if (cameraAprilTagDetector.isDetected()) {
            double[][] matchMatrix = createMatchMatrix();
            return matchMatrix;
        } else {
            return null;
        }

    }

    private double[][] updateMatrixWithPosition(double[][] matrix, boolean show) {
        if (matrix == null) {
            return matrix;
        }

        List<Vector3D> simTransVec = simAprilTagDetector.getTagPoseTranslationList();
        List<Rotation> simRotationVec = simAprilTagDetector.getTagPoseRotationList();

        List<Vector3D> realTransVec = cameraAprilTagDetector.getTagPoseTranslationList();
        List<Rotation> realRotationVec = cameraAprilTagDetector.getTagPoseRotationList();

        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][1] == 1) {
//                System.out.println("Sim tag" + (int) matrix[i][0] + " translation: x: " + simTransVec.get(i).getX()
//                        + " y: " + simTransVec.get(i).getY() + " z: " + simTransVec.get(i).getZ());
//                System.out.println("Real tag" + (int) matrix[i][0] + " translation: x: "
//                        + realTransVec.get((int) matrix[i][2]).getX() + " y: "
//                        + realTransVec.get((int) matrix[i][2]).getY() + " z: "
//                        + realTransVec.get((int) matrix[i][2]).getZ());

                double distance = simTransVec.get(i).distance(realTransVec.get((int) matrix[i][2]));
//                if(i==0){
//                    System.out.println("REAL TAG: " + realTransVec.get((int) matrix[i][2]).toString());
//                    System.out.println("SIM TAG: " + simTransVec.get(i).toString());
//                }
                // System.out.println("Distance: " + distance);

                double distScore = calculateDistScore(distance);
                // System.out.println("Score: " + distScore);

                matrix[i][3] = distScore;

                double[] anglesSim = getEulerAnglesEuclid(simRotationVec.get(i));
                double[] anglesReal = getEulerAnglesEuclid(realRotationVec.get((int) matrix[i][2]));

//                System.out.println("Sim tag" + (int) matrix[i][0] + " rotation: alpha: " + anglesSim[0] + " beta: "
//                        + anglesSim[1] + " gamma: " + anglesSim[2]);
//                System.out.println("Real tag" + (int) matrix[i][0] + " rotation: alpha: " + anglesReal[0] + " beta: "
//                        + anglesReal[1] + " gamma: " + anglesReal[2]);
                double rotDistance = Rotation.distance(simRotationVec.get(i), realRotationVec.get((int) matrix[i][2]));
                // System.out.println("Rot Distance: " + rotDistance);
                double rotScore = calculateRotScore(rotDistance);

                // System.out.println("Rot Score: " + rotScore);
                matrix[i][4] = rotScore;
            }
        }

        if (show) {
            System.out.println("Columns (Left to right):");
            System.out.println(
                    "Sim TagId | Found in real? | Position in real tags list | Position score | Rotation score");
            for (int i = 0; i < matrix.length; i++) {
                System.out.print("| ");
                for (int j = 0; j < matrix[i].length; j++) {
                    System.out.print(" " + String.format("%.2f", matrix[i][j]) + " |");
                }
                System.out.println();
            }
        }

        return matrix;

    }

    public double[][] checkTagPositionsOnce(boolean show) {

        if (cameraAprilTagDetector.isDetected()) {

            double[][] matchMatrix = checkTagPresenceOnce(show);

            matchMatrix = updateMatrixWithPosition(matchMatrix, show);
            noTags = false;
            return matchMatrix;
        } else {
            //System.out.println("No Apriltags detected in the image from the camera.");
            noTags = true;
            return null;
        }

    }

    public double[][] checkTagPositionsContinous(boolean show) {
        if (cameraAprilTagDetector.isDetected()) {
            double[][] matchMatrix = checkTagPresenceContinous(show);
            matchMatrix = updateMatrixWithPosition(matchMatrix, show);
            noTags = false;
            return matchMatrix;
        } else {
            noTags = true;
            //System.out.println("No Apriltags detected in the image from the camera.");
            return null;
        }
    }

    private double calculateRotScore(double rotDistance) {
        double score = 0;
        if (rotDistance >= rotationThr) {
            score = 0;
        } else {
            score = 1 - rotDistance / rotationThr;
        }
        return score;
    }

    private double calculateDistScore(double distance) {
        double score = 0;
        if (distance >= positionThr) {
            score = 0;
        } else {
            score = 1 - distance / positionThr;
        }

        return score;
    }

    private String positionScore2String(double score) {
        if (score == 0.0) {
            return "*";
        } else {
            return String.format("%.1f", (positionThr - score * positionThr) * 100);
        }
    }

    private String rotationScore2String(double score) {
        if (score == 0.0) {
            return "*";
        } else {
            int angle = (int) ((rotationThr - score * rotationThr) * 60);
            return String.format("%d", angle);
        }
    }

    private double[] getEulerAnglesEuclid(Rotation rotation) {
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

    private void updateRealCanvas(BufferedImage canvas, double[][] matrix) {

        realCanvas = canvas;

        //printShowMask(currentTagShowMask);
        realPicLabel.setIcon(new ImageIcon(realCanvas));
        StringBuilder realInfo = new StringBuilder();

        if (cameraAprilTagDetector.isDetected()) {

            int[] realTagsId = cameraAprilTagDetector.getTagsId();
            double[] realTagSize = cameraAprilTagDetector.getTagSize();
            List<VpHomogeneousMatrix> realCMo_vec = cameraAprilTagDetector.getcMo_vec();

            for (int i = 0; i < realTagsId.length; i++) {
                if (currentTagShowMask.contains(realTagsId[i])) {
                    displayFrame(realCanvas, realCMo_vec.get(i), cameraAprilTagDetector.getCamParam(),
                            realTagSize[i] / 2, 3);
                }
            }

            List<List<VpImagePoint>> realTags_corners = cameraAprilTagDetector.getTags_corners();

            int module = 0;
            for (List<VpImagePoint> corners : realTags_corners) {
                if (currentTagShowMask.contains(realTagsId[module])) {

                    if (matrix == null) {
                        displayLine(realCanvas, corners.get(0).get_i(), corners.get(0).get_j(), corners.get(1).get_i(),
                                corners.get(1).get_j(), Color.RED, 3);
                        displayLine(realCanvas, corners.get(0).get_i(), corners.get(0).get_j(), corners.get(3).get_i(),
                                corners.get(3).get_j(), Color.GREEN, 3);
                        displayLine(realCanvas, corners.get(1).get_i(), corners.get(1).get_j(), corners.get(2).get_i(),
                                corners.get(2).get_j(), Color.YELLOW, 3);
                        displayLine(realCanvas, corners.get(2).get_i(), corners.get(2).get_j(), corners.get(3).get_i(),
                                corners.get(3).get_j(), Color.BLUE, 3);
                    } else {
                        int position = findIfMatched(matrix, realTagsId[module]);
                        if (position == -1) {
                            displayLine(realCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                    corners.get(1).get_i(), corners.get(1).get_j(), Color.RED, 3);
                            displayLine(realCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                    corners.get(3).get_i(), corners.get(3).get_j(), Color.GREEN, 3);
                            displayLine(realCanvas, corners.get(1).get_i(), corners.get(1).get_j(),
                                    corners.get(2).get_i(), corners.get(2).get_j(), Color.YELLOW, 3);
                            displayLine(realCanvas, corners.get(2).get_i(), corners.get(2).get_j(),
                                    corners.get(3).get_i(), corners.get(3).get_j(), Color.BLUE, 3);
                        } else {
                            if (matrix[position][3] >= goodScore && matrix[position][4] >= goodScore) {
                                displayLine(realCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                        corners.get(1).get_i(), corners.get(1).get_j(), Color.GREEN, 5);
                                displayLine(realCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                        corners.get(3).get_i(), corners.get(3).get_j(), Color.GREEN, 5);
                                displayLine(realCanvas, corners.get(1).get_i(), corners.get(1).get_j(),
                                        corners.get(2).get_i(), corners.get(2).get_j(), Color.GREEN, 5);
                                displayLine(realCanvas, corners.get(2).get_i(), corners.get(2).get_j(),
                                        corners.get(3).get_i(), corners.get(3).get_j(), Color.GREEN, 5);
                            } else {
                                if (matrix[position][3] > 0 && matrix[position][4] > 0) {
                                    displayLine(realCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                            corners.get(1).get_i(), corners.get(1).get_j(), Color.YELLOW, 5);
                                    displayLine(realCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                            corners.get(3).get_i(), corners.get(3).get_j(), Color.YELLOW, 5);
                                    displayLine(realCanvas, corners.get(1).get_i(), corners.get(1).get_j(),
                                            corners.get(2).get_i(), corners.get(2).get_j(), Color.YELLOW, 5);
                                    displayLine(realCanvas, corners.get(2).get_i(), corners.get(2).get_j(),
                                            corners.get(3).get_i(), corners.get(3).get_j(), Color.YELLOW, 5);
                                } else {
                                    displayLine(realCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                            corners.get(1).get_i(), corners.get(1).get_j(), Color.RED, 5);
                                    displayLine(realCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                            corners.get(3).get_i(), corners.get(3).get_j(), Color.RED, 5);
                                    displayLine(realCanvas, corners.get(1).get_i(), corners.get(1).get_j(),
                                            corners.get(2).get_i(), corners.get(2).get_j(), Color.RED, 5);
                                    displayLine(realCanvas, corners.get(2).get_i(), corners.get(2).get_j(),
                                            corners.get(3).get_i(), corners.get(3).get_j(), Color.RED, 5);
                                }

                            }
                        }
                    }

                }

                module++;
            }

            for (int i = 0; i < realTagsId.length; i++) {

                if (currentTagShowMask.contains(realTagsId[i])) {
                    double[] centroid = computeCentroid(realTags_corners.get(i));
                    VpImagePoint corner = realTags_corners.get(i).get(0);
                    VpImagePoint oppositeCorner = realTags_corners.get(i).get(2);
                    double tagSize = Math.pow(corner.get_i() - oppositeCorner.get_i(), 2);
                    tagSize += Math.pow(corner.get_j() - oppositeCorner.get_j(), 2);
                    tagSize = Math.sqrt(tagSize);
                    FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
                    double textPosX = centroid[0] + (fm.getHeight() - fm.getDescent() - fm.getDescent()) / 2;
                    double textPosY = centroid[1] + tagSize / 2 + 5;
                    if (matrix == null) {
                        displayText(realCanvas, new String(String.valueOf(realTagsId[i])), textPosX,
                                textPosY, Color.RED, 3);
                    } else {
                        int position = findIfMatched(matrix, realTagsId[i]);
                        if (position == -1) {
                            displayText(realCanvas, new String(String.valueOf(realTagsId[i])), textPosX,
                                    textPosY, Color.RED, 3);
                        } else {
                            if (matrix[position][3] >= goodScore && matrix[position][4] >= goodScore) {
                                displayText(realCanvas,
                                        String.valueOf(realTagsId[i]) + " - "
                                        + positionScore2String(matrix[position][3]) + " - "
                                        + rotationScore2String(matrix[position][4]),
                                        textPosX, textPosY, Color.GREEN, 3);
                            } else {
                                if (matrix[position][3] > 0 && matrix[position][4] > 0) {
                                    displayText(realCanvas,
                                            String.valueOf(realTagsId[i]) + " - "
                                            + positionScore2String(matrix[position][3]) + " - "
                                            + rotationScore2String(matrix[position][4]),
                                            textPosX, textPosY, Color.YELLOW, 3);
                                } else {
                                    displayText(realCanvas,
                                            String.valueOf(realTagsId[i]) + " - "
                                            + positionScore2String(matrix[position][3]) + " - "
                                            + rotationScore2String(matrix[position][4]),
                                            textPosX, textPosY, Color.RED, 3);
                                }
                            }
                        }

                    }

                    realInfo.append("Tag id: ");
                    realInfo.append(realTagsId[i]);
                    VpHomogeneousMatrix cMo = realCMo_vec.get(i);
                    realInfo.append("\ncMo:\n" + cMo);
                    realInfo.append("\n");
                    realInfo.append("\n");

                }
            }
        }

        //Add tag missing info
        if (this.noReferenceTag) {
            displayText(realCanvas, "NO REF!", 20, 80, Color.RED, 5);
        }
        if (this.noTags) {
            displayText(realCanvas, "NO TAGS!", 20, 0, Color.RED, 5);
        }
        if (this.noSameNumerTags) {
            displayText(realCanvas, "MISS TAGS!", 45, 0, Color.RED, 5);
        }
        if (this.noSimTags) {
            displayText(realCanvas, "NO SIM TAGS!", 45, 80, Color.RED, 5);
        }
        if (this.assembleCompleted) {
            displayText(realCanvas, "DONE!", 100, 40, Color.RED, 5);
        }

    }

    private int findIfMatched(double[][] matrix, int realTagId) {
        int position = -1;
        for (int j = 0; j < matrix.length; j++) {
            if (matrix[j][0] == realTagId) {
                position = j;
            }
        }
        return position;
    }

    private void updateSimCanvas(BufferedImage canvas, double[][] matrix) {

        simCanvas = canvas;
        simPicLabel.setIcon(new ImageIcon(simCanvas));
        StringBuilder simInfo = new StringBuilder();

        if (simAprilTagDetector.isDetected()) {

            int[] simTagsId = simAprilTagDetector.getTagsId();
            double[] simTagSize = simAprilTagDetector.getTagSize();
            List<VpHomogeneousMatrix> simCMo_vec = simAprilTagDetector.getcMo_vec();

            for (int i = 0; i < simTagsId.length; i++) {
                if (currentTagShowMask.contains(simTagsId[i])) {
                    displayFrame(simCanvas, simCMo_vec.get(i), simAprilTagDetector.getCamParam(), simTagSize[i] / 2, 3);
                }
            }

            List<List<VpImagePoint>> simTags_corners = simAprilTagDetector.getTags_corners();

            int module = 0;
            for (List<VpImagePoint> corners : simTags_corners) {
                if (currentTagShowMask.contains(simTagsId[module])) {
                    if (matrix == null) {
                        displayLine(simCanvas, corners.get(0).get_i(), corners.get(0).get_j(), corners.get(1).get_i(),
                                corners.get(1).get_j(), Color.RED, 3);
                        displayLine(simCanvas, corners.get(0).get_i(), corners.get(0).get_j(), corners.get(3).get_i(),
                                corners.get(3).get_j(), Color.GREEN, 3);
                        displayLine(simCanvas, corners.get(1).get_i(), corners.get(1).get_j(), corners.get(2).get_i(),
                                corners.get(2).get_j(), Color.YELLOW, 3);
                        displayLine(simCanvas, corners.get(2).get_i(), corners.get(2).get_j(), corners.get(3).get_i(),
                                corners.get(3).get_j(), Color.BLUE, 3);
                    } else {
                        if (matrix[module][1] != 1) {
                            displayLine(simCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                    corners.get(1).get_i(), corners.get(1).get_j(), Color.RED, 3);
                            displayLine(simCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                    corners.get(3).get_i(), corners.get(3).get_j(), Color.GREEN, 3);
                            displayLine(simCanvas, corners.get(1).get_i(), corners.get(1).get_j(),
                                    corners.get(2).get_i(), corners.get(2).get_j(), Color.YELLOW, 3);
                            displayLine(simCanvas, corners.get(2).get_i(), corners.get(2).get_j(),
                                    corners.get(3).get_i(), corners.get(3).get_j(), Color.BLUE, 3);
                        } else {
                            if (matrix[module][3] >= goodScore && matrix[module][4] >= goodScore) {
                                displayLine(simCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                        corners.get(1).get_i(), corners.get(1).get_j(), Color.GREEN, 5);
                                displayLine(simCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                        corners.get(3).get_i(), corners.get(3).get_j(), Color.GREEN, 5);
                                displayLine(simCanvas, corners.get(1).get_i(), corners.get(1).get_j(),
                                        corners.get(2).get_i(), corners.get(2).get_j(), Color.GREEN, 5);
                                displayLine(simCanvas, corners.get(2).get_i(), corners.get(2).get_j(),
                                        corners.get(3).get_i(), corners.get(3).get_j(), Color.GREEN, 5);
                            } else {
                                if (matrix[module][3] > 0 && matrix[module][4] > 0) {
                                    displayLine(simCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                            corners.get(1).get_i(), corners.get(1).get_j(), Color.YELLOW, 5);
                                    displayLine(simCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                            corners.get(3).get_i(), corners.get(3).get_j(), Color.YELLOW, 5);
                                    displayLine(simCanvas, corners.get(1).get_i(), corners.get(1).get_j(),
                                            corners.get(2).get_i(), corners.get(2).get_j(), Color.YELLOW, 5);
                                    displayLine(simCanvas, corners.get(2).get_i(), corners.get(2).get_j(),
                                            corners.get(3).get_i(), corners.get(3).get_j(), Color.YELLOW, 5);
                                } else {
                                    displayLine(simCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                            corners.get(1).get_i(), corners.get(1).get_j(), Color.RED, 5);
                                    displayLine(simCanvas, corners.get(0).get_i(), corners.get(0).get_j(),
                                            corners.get(3).get_i(), corners.get(3).get_j(), Color.RED, 5);
                                    displayLine(simCanvas, corners.get(1).get_i(), corners.get(1).get_j(),
                                            corners.get(2).get_i(), corners.get(2).get_j(), Color.RED, 5);
                                    displayLine(simCanvas, corners.get(2).get_i(), corners.get(2).get_j(),
                                            corners.get(3).get_i(), corners.get(3).get_j(), Color.RED, 5);
                                }
                            }
                        }
                    }
                }

                module++;
            }

            for (int i = 0; i < simTagsId.length; i++) {
                if (currentTagShowMask.contains(simTagsId[i])) {
                    double[] centroid = computeCentroid(simTags_corners.get(i));

                    if (matrix == null) {
                        displayText(simCanvas, new String(String.valueOf(simTagsId[i])), centroid[0] + 10,
                                centroid[1] + 20, Color.RED, 3);
                    } else {
                        if (matrix[i][1] != 1) {
                            displayText(simCanvas, new String(String.valueOf(simTagsId[i])), centroid[0] + 10,
                                    centroid[1] + 20, Color.RED, 3);
                        } else {
                            if (matrix[i][3] >= goodScore && matrix[i][4] >= goodScore) {
                                displayText(simCanvas,
                                        String.valueOf(simTagsId[i]) + " - "
                                        + positionScore2String(matrix[i][3]) + " - "
                                        + rotationScore2String(matrix[i][4]),
                                        centroid[0] + 10, centroid[1] + 20, Color.GREEN, 3);
                            } else {
                                if (matrix[i][3] > 0 && matrix[i][4] > 0) {
                                    displayText(simCanvas,
                                            String.valueOf(simTagsId[i]) + " - "
                                            + positionScore2String(matrix[i][3]) + " - "
                                            + rotationScore2String(matrix[i][4]),
                                            centroid[0] + 10, centroid[1] + 20, Color.YELLOW, 3);
                                } else {
                                    displayText(simCanvas,
                                            "Id: " + String.valueOf(simTagsId[i]) + " - "
                                            + positionScore2String(matrix[i][3]) + " - "
                                            + rotationScore2String(matrix[i][4]),
                                            centroid[0] + 10, centroid[1] + 20, Color.RED, 3);
                                }
                            }
                        }

                    }

                    simInfo.append("Tag id: ");
                    simInfo.append(simTagsId[i]);
                    VpHomogeneousMatrix cMo = simCMo_vec.get(i);
                    simInfo.append("\ncMo:\n" + cMo);
                    simInfo.append("\n");
                    simInfo.append("\n");
                }
            }

        }

    }

    private double[][] initVideoWindows() {
        // Capture camera image
        camera.capture();
        // Create detector and detect from camera
        cameraAprilTagDetector = new AprilTagDetector(camera.getI(), AprilTagFamily.TAG_36h11, realCamParam);

        positionCameraContinous();

        coppelia.capture();
        simAprilTagDetector = new AprilTagDetector(coppelia.getI(), AprilTagFamily.TAG_36h11, coppelia.getCamParam());

        double[][] matchMatrix = checkTagPositionsContinous(false);

        realF = createRealFrame(camera.getbImage(), camera.getI());
        updateRealCanvas(realCanvas, matchMatrix);
        realF.repaint();

        simF = createSimFrame(coppelia.getbImage(), coppelia.getI());
        updateSimCanvas(simCanvas, matchMatrix);
        simF.repaint();

        return matchMatrix;
    }

    public void waitUntilPositioned(int nextTag) {
        double[][] matchMatrix;

        currentTagShowMask = new ArrayList<>();
        addTagsToTagShowMask(nextTag, currentTagShowMask);

        IntW pingTime = new IntW(0);

        coppeliaSimApi.simxStartSimulation(clientID, remoteApi.simx_opmode_oneshot_wait);
        coppeliaSimApi.simxGetPingTime(clientID, pingTime);

        while (!spacePressed) {
            positionCameraContinous();

            coppelia.capture(); // Capture and detect tag in simulation
            simAprilTagDetector.setI(coppelia.getI());
            simAprilTagDetector.detect();

            matchMatrix = checkTagPositionsContinous(false); // Create Tag position and rotation scores matrix (show
            // matrix in console?)

            BufferedImage newRealCanvas = prepareCanvas(camera.getbImage());

            updateRealCanvas(newRealCanvas, matchMatrix);

            BufferedImage newSimCanvas = prepareCanvas(coppelia.getbImage());

            updateSimCanvas(newSimCanvas, matchMatrix);

            realF.repaint();
            simF.repaint();

            if (!realF.isShowing()) {
                simF.dispose();
                stop = true;
                break;
            }

            if (!simF.isShowing()) {
                realF.dispose();
                stop = true;
                break;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        spacePressed = false;
        coppeliaSimApi.simxStopSimulation(clientID, remoteApi.simx_opmode_oneshot_wait);
        coppeliaSimApi.simxGetPingTime(clientID, pingTime);

    }

    public List<Integer> getNextModuleIDWithVideo(boolean firstTime) {
        double[][] matchMatrix;

        IntW pingTime = new IntW(0);

        coppeliaSimApi.simxStartSimulation(clientID, remoteApi.simx_opmode_oneshot_wait);
        coppeliaSimApi.simxGetPingTime(clientID, pingTime);

        boolean newTagFound = false;

        if (firstTime) {
            globalTagShowMask = new ArrayList<>();
            currentTagShowMask = globalTagShowMask;
            matchMatrix = initVideoWindows();

            globalTagShowMask.add(TagUtils.getBASE_TAG());
            currentTagShowMask = globalTagShowMask;
            newTagFound = true;
            if (realTagsFound == null) {
                realTagsFound = new ArrayList<Integer>();
                realTagsFound.add(referenceTag);
                newTagFound = false;
            }
        }

        List<Integer> newModuleIDs = new ArrayList<>();

        while (!newTagFound) {

            positionCameraContinous();

            newModuleIDs = checkIfNewModuleID();

            currentTagShowMask = globalTagShowMask;

            if (!newModuleIDs.isEmpty()) {
                newTagFound = true;
            }

            coppelia.capture(); // Capture and detect tag in simulation
            simAprilTagDetector.setI(coppelia.getI());
            simAprilTagDetector.detect();

            matchMatrix = checkTagPositionsContinous(false); // Create Tag position and rotation scores matrix (show
            // matrix in console?)

            BufferedImage newRealCanvas = prepareCanvas(camera.getbImage());

            updateRealCanvas(newRealCanvas, matchMatrix);

            BufferedImage newSimCanvas = prepareCanvas(coppelia.getbImage());

            updateSimCanvas(newSimCanvas, matchMatrix);

            realF.repaint();
            simF.repaint();

            if (!realF.isShowing()) {
                simF.dispose();
                stop = true;
                break;
            }

            if (!simF.isShowing()) {
                realF.dispose();
                stop = true;
                break;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        }

        if (stop) {
            // coppeliaSimApi.simxStopSimulation(clientID, remoteApi.simx_opmode_oneshot_wait);
            camera.closeCamera();
            // System.exit(0);
        }

        coppeliaSimApi.simxStopSimulation(clientID, remoteApi.simx_opmode_oneshot_wait);
        coppeliaSimApi.simxGetPingTime(clientID, pingTime);

        return newModuleIDs;
    }

    private List<Integer> checkIfNewModuleID() {

        List<Integer> newModuleIDs = new ArrayList<>();

        int[] realTagsId = cameraAprilTagDetector.getTagsId();
        if (realTagsId == null) {
            return newModuleIDs;
        }

        for (int i = 0; i < realTagsId.length; i++) {
            int moduleID;
            if (realTagsId[i] == referenceTag) {
                moduleID = referenceTag;
            } else {
                moduleID = TagUtils.Tag2DynamixelId(realTagsId[i]);
            }

            if (!realTagsFound.contains(moduleID)) {
                realTagsFound.add(moduleID);
                newModuleIDs.add(moduleID);
                addTagsToTagShowMask(moduleID, globalTagShowMask);
            }
        }

        if (!newModuleIDs.isEmpty()) {
            printRealTagsFound();
        }

        return newModuleIDs;
    }

    private void addTagsToTagShowMask(int moduleTag, List<Integer> mask) {

        int[] faceTags = TagUtils.getFaceTagIDs(moduleTag);
        for (int i = 0; i < faceTags.length; i++) {
            mask.add(faceTags[i]);
        }

    }

    private void printShowMask(List<Integer> list) {
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Integer integer = (Integer) iterator.next();
            System.out.print(integer.intValue() + ", ");
        }
        System.out.println();
    }

    public void printRealTagsFound() {
        System.out.print("Real Tags found until now: [ ");
        for (Iterator iterator = realTagsFound.iterator(); iterator.hasNext();) {
            Integer integer = (Integer) iterator.next();
            if (!iterator.hasNext()) {
                System.out.print(integer.intValue() + " ");
            } else {
                System.out.print(integer.intValue() + ", ");
            }
        }
        System.out.println("]");
    }

    public void showVideo() {
        coppeliaSimApi.simxStartSimulation(clientID, remoteApi.simx_opmode_oneshot_wait);

        double[][] matchMatrix = initVideoWindows();

        boolean xpressed = false;
        while (!xpressed) {

//            camera.captureCameraImage();
//            cameraAprilTagDetector.setI(camera.getI());
//            cameraAprilTagDetector.detect();
            positionCameraContinous();

            coppelia.capture();
            simAprilTagDetector.setI(coppelia.getI());
            simAprilTagDetector.detect();

            matchMatrix = checkTagPositionsContinous(false);

            BufferedImage newRealCanvas = prepareCanvas(camera.getbImage());

            updateRealCanvas(newRealCanvas, matchMatrix);

            BufferedImage newSimCanvas = prepareCanvas(coppelia.getbImage());

            updateSimCanvas(newSimCanvas, matchMatrix);

            realF.repaint();
            simF.repaint();

            if (!realF.isShowing()) {
                simF.dispose();
                xpressed = true;
            }

            if (!simF.isShowing()) {
                realF.dispose();
                xpressed = true;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        }
        coppeliaSimApi.simxStopSimulation(clientID, remoteApi.simx_opmode_oneshot_wait);
        camera.closeCamera();
    }

    private void showImages() {

        // TODO: METHOD FOR updating canvas
        realF = createRealFrame(camera.getbImage(), camera.getI());
        updateRealCanvas(realCanvas, null);
        realF.repaint();

        // This is done inside checkTagPresence so connection should be present
        simF = createSimFrame(coppelia.getbImage(), coppelia.getI());
        updateSimCanvas(simCanvas, null);
        simF.repaint();

    }

    public void displayFrame(BufferedImage I, VpHomogeneousMatrix mat, VpCameraParameters cam, double size,
            int thickness) {
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
                x.get_y() * cam.get_py() + cam.get_v0(), x.get_x() * cam.get_px() + cam.get_u0(), Color.RED,
                4 * thickness, 2 * thickness, thickness);

        displayArrow(I, o.get_y() * cam.get_py() + cam.get_v0(), o.get_x() * cam.get_px() + cam.get_u0(),
                y.get_y() * cam.get_py() + cam.get_v0(), y.get_x() * cam.get_px() + cam.get_u0(), Color.GREEN,
                4 * thickness, 2 * thickness, thickness);

        displayArrow(I, o.get_y() * cam.get_py() + cam.get_v0(), o.get_x() * cam.get_px() + cam.get_u0(),
                z.get_y() * cam.get_py() + cam.get_v0(), z.get_x() * cam.get_px() + cam.get_u0(), Color.BLUE,
                4 * thickness, 2 * thickness, thickness);
    }

    public void displayArrow(BufferedImage I, double i1, double j1, double i2, double j2, Color color, int w, int h,
            int thickness) {
        displayArrow(I, (int) i1, (int) j1, (int) i2, (int) j2, color, w, h, thickness);
    }

    public void displayArrow(BufferedImage I, int i1, int j1, int i2, int j2, Color color, int w, int h,
            int thickness) {
        Graphics2D g = I.createGraphics();
        g.setStroke(new BasicStroke(thickness));
        g.setColor(color);

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

    public void displayLine(BufferedImage I, double i1, double j1, double i2, double j2, Color color, int thickness) {
        displayLine(I, (int) i1, (int) j1, (int) i2, (int) j2, color, thickness);
    }

    public void displayLine(BufferedImage I, int i1, int j1, int i2, int j2, Color color, int thickness) {
        Graphics2D g = I.createGraphics();
        g.setStroke(new BasicStroke(thickness));
        g.setColor(color);
        g.drawLine(j1, i1, j2, i2);
    }

    public double[] computeCentroid(List<VpImagePoint> corners) {
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

    public void displayText(BufferedImage I, String text, double i, double j, Color color, int thickness) {
        displayText(I, text, (int) i, (int) j, color, thickness);
    }

    public void displayText(BufferedImage I, String text, int i, int j, Color color, int thickness) {
        Graphics2D g = I.createGraphics();
        g.setStroke(new BasicStroke(thickness));
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, j, i);
    }

    private BufferedImage prepareCanvas(BufferedImage bImage) {

        BufferedImage canvas = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage tmp = bImage;
        if (tmp.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_RGB) {
            canvas = tmp;
        } else {
            Graphics2D g2d = canvas.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
        }
        return canvas;
    }

    private JFrame createRealFrame(BufferedImage bImage, VpImageUChar I) {

//        realCanvas = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        BufferedImage tmp = Capture.toBufferedImage(I);
//        if (tmp.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_RGB) {
//            realCanvas = tmp;
//        } else {
//            Graphics2D g2d = realCanvas.createGraphics();
//            g2d.drawImage(tmp, 0, 0, null);
//            g2d.dispose();
//        }
        realCanvas = prepareCanvas(bImage);
        realPicLabel = new JLabel(new ImageIcon(realCanvas));
        realPicLabel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "pressingSpace");
        realPicLabel.getActionMap().put("pressingSpace", new SpaceAction());
        JPanel jPanel = new JPanel();
        jPanel.add(realPicLabel);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(new Dimension(bImage.getWidth() + 50, bImage.getHeight() + 50));
        f.add(jPanel);
        f.setVisible(true);

        return f;
    }

    private JFrame createSimFrame(BufferedImage bImage, VpImageUChar I) {

//        simCanvas = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        BufferedImage tmp = Capture.toBufferedImage(I);
//        if (tmp.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_RGB) {
//            simCanvas = tmp;
//        } else {
//            Graphics2D g2d = simCanvas.createGraphics();
//            g2d.drawImage(tmp, 0, 0, null);
//            g2d.dispose();
//        }
        simCanvas = prepareCanvas(bImage);
        simPicLabel = new JLabel(new ImageIcon(simCanvas));
        simPicLabel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "pressingSpace");
        simPicLabel.getActionMap().put("pressingSpace", new SpaceAction());
        JPanel jPanel = new JPanel();
        jPanel.add(simPicLabel);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(new Dimension(bImage.getWidth() + 50, bImage.getHeight() + 50));
        f.add(jPanel);
        f.setVisible(true);
        f.setLocation(1250, 0);
        return f;
    }

    public List<Integer> getRealModuleTagsList() {
        return realTagsFound;
    }

    public void setRealModuleTagsList(int[] array) {
        realTagsFound = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            realTagsFound.add(array[i]);
        }
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private class SpaceAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            spacePressed = true;
        }

    }

    public void setAssembleCompleted(boolean assembleCompleted) {
        this.assembleCompleted = assembleCompleted;
    }
}
