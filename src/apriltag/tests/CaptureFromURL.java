package apriltag.tests;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.visp.core.VpImageUChar;

import apriltag.CameraCapture;
import apriltag.Capture;
import apriltag.ImageConverter;
import apriltag.ImageResolution;
import apriltag.ImageUtils;

public class CaptureFromURL extends Capture{
    
    private static boolean showImage = true;
    private static int cameraIndex = 0;
    private static ImageResolution resolution = ImageResolution.RES_640x480;
    static VideoCapture videoCapture;
    private static Thread thread;
    private static JFrame jFrameOriginal, jFrameUndist;
    private static Mat distorsion = null, intrinsic = null, optimized = null;
    private static boolean undistort = false;
    
    private static final Semaphore mutex = new Semaphore(1);
    
    
    static{ 
        System.loadLibrary("visp_java341");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
        }

    
private void openCamera() {
        
        
            videoCapture = new VideoCapture("http://192.168.1.120:81/stream");
        
        
        if (showImage) {
            jFrameOriginal = new JFrame("OriginalArenaCamera");
            jFrameUndist = new JFrame("UndistArenaCamera");
            jFrameOriginal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrameUndist.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel vidpanelOriginal = new JLabel();
            JLabel vidpanelUndist = new JLabel();
            jFrameOriginal.setContentPane(vidpanelOriginal);
            jFrameUndist.setContentPane(vidpanelUndist);
            jFrameOriginal.setVisible(true);
            jFrameUndist.setVisible(true);
            capture();
            jFrameOriginal.setSize(new Dimension(matrix.width() / 2 + 50, matrix.height() / 2 + 50));
            jFrameUndist.setSize(new Dimension(matrix.width() / 2 + 50, matrix.height() / 2 + 50));
        }
        
        
    }

@Override
public boolean capture() {
   
 
        matrix = new Mat();
        // videoCapture.read(matrix);

        if (videoCapture.isOpened()) {
            // If there is next video frame
            if (videoCapture.read(matrix)) {
                return true;
            }
              
        } else {
            System.out.println("Camera is not open");
        }


    return false;
}

public static void main(String[] args) {
    CaptureFromURL cap = new CaptureFromURL();
    cap.openCamera();
    while(true) {
    cap.capture();
  //Find tags and show image
    ImageUtils.paintTags(cap.matrix, cap.camParam);
    ImageIcon imageUndist = new ImageIcon(ImageConverter.Mat2BufferedImage(cap.matrix));
    ((JLabel) jFrameUndist.getContentPane()).setIcon(imageUndist);
    ((JLabel) jFrameUndist.getContentPane()).repaint();
    }
}
}


