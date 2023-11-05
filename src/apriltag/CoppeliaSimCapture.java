/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriltag;

import coppelia.CharWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;
import java.awt.image.BufferedImage;
import modules.evaluation.CoppeliaSimulator;
import org.opencv.core.Mat;
import org.visp.core.VpCameraParameters;
import org.visp.core.VpImageUChar;

/**
 *
 * @author anfv
 */
public final class CoppeliaSimCapture extends Capture {

    private CoppeliaSimulator coppeliaSimulator;
    private IntW sensorHandle;
    final private static VpCameraParameters COPPELIASIM_CAM_PARAM_512x512 = new VpCameraParameters(443.40499181748, 443.40499181748, 256,
            256);//Parameters calculated from coppelia sim vision sensor parameters
    final private static VpCameraParameters COPPELIASIM_CAM_PARAM_1024x1024 = new VpCameraParameters(443.40499181748 * 2, 443.40499181748 * 2, 256 * 2,
            256 * 2);//Parameters calculated from coppelia sim vision sensor parameters
    private int resX, resY;

    public CoppeliaSimCapture(CoppeliaSimulator simulator) {
        this(simulator, "Vision_sensor");
    }

    public CoppeliaSimCapture(String sensorName) {
        this(null, sensorName);
    }

    public CoppeliaSimCapture(CoppeliaSimulator simulator, String sensorName) {
        if (simulator == null) {
            connect2CoppeliaSim();
        } else {
            setCoppeliaSimulator(simulator);
            if (simulator.getClientID() == -1) {
                simulator.connect2CoppeliaSim();
            }
        }

        if (this.coppeliaSimulator.getClientID() == -1) {
            System.out.println("Connect to CoppeliaSim");
        } else {
            sensorHandle = new IntW(0);
            int err = this.coppeliaSimulator.getCoppeliaSimApi().simxGetObjectHandle(this.coppeliaSimulator.getClientID(), sensorName, sensorHandle,
                    remoteApi.simx_opmode_blocking);
            if (err != remoteApi.simx_error_noerror) {
                System.err.println("Vision sensor not found, name: " + sensorName);
            }
        }
        //Get resolution of the image
        capture();
        switch (resX) {
            case 512:
                camParam = COPPELIASIM_CAM_PARAM_512x512;
                break;
            case 1024:
                camParam = COPPELIASIM_CAM_PARAM_1024x1024;
                break;
            default:
                System.err.println("The paramenters for the camera have not been defined for this resolution: " + resX + "x"+ resY);
                System.exit(-1);
        }
    }

    public CoppeliaSimulator getCoppeliaSimulator() {
        return coppeliaSimulator;
    }

    public void setCoppeliaSimulator(CoppeliaSimulator simulator) {
        this.coppeliaSimulator = simulator;
    }

    public void connect2CoppeliaSim() {
        System.out.println("connecting to coppeliaSim...");
        coppeliaSimulator = new CoppeliaSimulator();
        coppeliaSimulator.connect2CoppeliaSim();
    }

    public void captureCoppeliaSimImageOnce() {
        if (coppeliaSimulator.getClientID() == -1) {
            System.out.println("Connect to CoppeliaSim to capture image");
        } else {

            coppeliaSimulator.getCoppeliaSimApi().simxStartSimulation(coppeliaSimulator.getClientID(), remoteApi.simx_opmode_oneshot_wait);

            capture();

            coppeliaSimulator.getCoppeliaSimApi().simxStopSimulation(coppeliaSimulator.getClientID(), remoteApi.simx_opmode_oneshot_wait);
        }

    }

    @Override
    public boolean capture() {

        CharWA image = new CharWA(0);
        IntWA resolution = new IntWA(0);

        int err = coppeliaSimulator.getCoppeliaSimApi().simxGetVisionSensorImage(coppeliaSimulator.getClientID(), sensorHandle.getValue(), resolution, image, 1,
                remoteApi.simx_opmode_streaming);

        while (coppeliaSimulator.getCoppeliaSimApi().simxGetConnectionId(coppeliaSimulator.getClientID()) != -1) {
            err = coppeliaSimulator.getCoppeliaSimApi().simxGetVisionSensorImage(coppeliaSimulator.getClientID(), sensorHandle.getValue(), resolution, image, 1,
                    remoteApi.simx_opmode_buffer);
            if (err == remoteApi.simx_error_noerror) {
                //System.out.println("Resolution: " + resolution.getLength() + " " + resolution.getArray()[0] + " "
                //        + resolution.getArray()[1]);
                break;
            }

        }
        if (coppeliaSimulator.getCoppeliaSimApi().simxGetConnectionId(coppeliaSimulator.getClientID()) == -1) {
            return false;
        }

        // err = coppeliaSimulator.getCoppeliaSimApi().simxGetVisionSensorImage(clientID, sensorHandle.getValue(),
        // resolution, image, 1,
        // remoteApi.simx_opmode_discontinue);
        //System.out.println(image.getLength());
        resX = resolution.getArray()[0];
        resY = resolution.getArray()[1];
        int res = resX;
        if (resX != resY) {
            System.err.println("The image of the coppeliaSim vision sensors need to be square. "
                    + "Currenr resolution is " + resX + "x" + resY);
            System.exit(-1);
        }
        byte[][] imageMatrix = new byte[res][res];
        for (int i = 0; i < res; i++) {
            for (int j = 0; j < res; j++) {
                imageMatrix[i][j] = (byte) image.getArray()[(res - i - 1) * res + j];// Flip image vertically
            }
        }

        byte[] imageData = new byte[image.getLength()];

        for (int i = 0; i < res; i++) {
            for (int j = 0; j < res; j++) {
                imageData[i * res + j] = imageMatrix[i][j];
            }
        }

//			for (int i = 0; i < imageData.length; i++) {
//				imageData[i] = (byte) image.getArray()[i];
//			}
        I = new VpImageUChar(imageData, res, res, true);

        return true;
    }

    @Override
    public BufferedImage getbImage() {
        bImage = ImageConverter.VpImageUChar2BufferedImage(I);
        return bImage;
    }

    @Override
    public Mat getMat() {
        matrix = ImageConverter.BufferedImage2Mat(getbImage());
        return matrix;
    }

}
