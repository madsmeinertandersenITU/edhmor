package apriltag;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.visp.core.VpImageUChar;
import org.visp.io.VpImageIo;

import coppelia.CharWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.remoteApi;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import modules.evaluation.CoppeliaSimulator;
import org.opencv.videoio.Videoio;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoWriter;
import org.visp.core.VpCameraParameters;

public abstract class Capture {

    protected VpImageUChar I;
    protected BufferedImage bImage;
    protected Mat matrix;
    private Imgcodecs imageCodecs; //To save images
    protected VpCameraParameters camParam;

    public VpCameraParameters getCamParam() {
        return camParam;
    }

    public void setCamParam(VpCameraParameters camParam) {
        this.camParam = camParam;
    }

    public abstract boolean capture();

    public VpImageUChar getI() {
        return I;
    }

    public BufferedImage getbImage() {
        return bImage;
    }

    public Mat getMat() {
        return matrix;
    }
}
