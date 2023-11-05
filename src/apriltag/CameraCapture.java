/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriltag;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import modules.util.PhysicalSetupConfiguration;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Size;
import static org.opencv.imgproc.Imgproc.resize;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.visp.core.VpCameraParameters;
import org.visp.core.VpImageUChar;

/**
 *
 * @author anfv
 */
public class CameraCapture extends Capture implements Runnable {

    private int cameraIndex = 0;
    private String cameraStreamURL = "";
    private ImageResolution resolution = ImageResolution.RES_640x480;
    VideoCapture videoCapture;
    private Thread thread;
    private boolean showImage = true;
    private JFrame jFrameOriginal, jFrameUndist;
    private Mat distorsion = null, intrinsic = null, optimized = null;
    private boolean undistort = false;

    private final Semaphore mutex = new Semaphore(1);

    public CameraCapture(String streamURL, VpCameraParameters camParam) {
        this.cameraIndex = 0;
        this.camParam = camParam;
        this.cameraStreamURL = streamURL;
        openCameraFromURL();
    }

    public CameraCapture(int cameraIndex, VpCameraParameters camParam) {
        this.cameraIndex = cameraIndex;
        this.camParam = camParam;
        openCamera(false);
    }

    public CameraCapture(int cameraIndex, VpCameraParameters camParam, ImageResolution resolution) {
        this.cameraIndex = cameraIndex;
        this.camParam = camParam;
        this.resolution = resolution;
        openCamera(false);
    }

    public CameraCapture(int cameraIndex, VpCameraParameters camParam, ImageResolution resolution, boolean fast) {
        this.cameraIndex = cameraIndex;
        this.camParam = camParam;
        this.resolution = resolution;
        openCamera(fast);
    }

    public CameraCapture(int cameraIndex, VpCameraParameters camParam, ImageResolution resolution, boolean fast, boolean showImage) {
        this.cameraIndex = cameraIndex;
        this.camParam = camParam;
        this.resolution = resolution;
        this.showImage = showImage;
        openCamera(fast);
    }

    public CameraCapture(CameraType type) {


        if (type == CameraType.ARENA_CEILING_CAMERA) {
            this.cameraIndex = PhysicalSetupConfiguration.getArenaCeilingCamIndex();
            this.resolution = PhysicalSetupConfiguration.getArenaCeilingCamResolution();
            this.camParam = PhysicalSetupConfiguration.getArenaCeilingOptimizedVispCamParam();
            this.distorsion = PhysicalSetupConfiguration.getArenaCeilingDistParam();
            this.intrinsic = PhysicalSetupConfiguration.getArenaCeilingIntrinsicCamParam();
            this.optimized = PhysicalSetupConfiguration.getArenaCeilingOptimizedCamParam();
            undistort = true;
        } else {
            System.err.println("camera type not implemented. Implement it or choose another camera type");
            System.exit(-1);
        }
        openCamera(false);
    }

    public void performUndistort(Mat distorsion, Mat intrinsic, Mat optimized) {
        this.distorsion = distorsion;
        this.intrinsic = intrinsic;
        this.optimized = optimized;
        undistort = true;
    }

    public VideoCapture getVideoCapture() {
        return videoCapture;
    }

    private void openCameraFromURL() {

        videoCapture = new VideoCapture(cameraStreamURL);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Capture.class.getName()).log(Level.SEVERE, null, ex);
        }

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

        System.out.println("Starting Thread");
        thread = new Thread(this);
        thread.start();
    }

    private void openCamera(boolean fast) {

        if (!fast) {
            videoCapture = new VideoCapture(cameraIndex);
        } else {
            videoCapture = new VideoCapture(cameraIndex, Videoio.CAP_DSHOW);
            videoCapture.set(Videoio.CAP_PROP_FOURCC, VideoWriter.fourcc('M', 'J', 'P', 'G'));
            videoCapture.set(Videoio.CAP_PROP_FPS, 60);
        }

        if (resolution == ImageResolution.RES_1280x720) {
            videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, 1280);
            videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 720);
        }
        if (resolution == ImageResolution.RES_1920x1080) {
            videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, 1920);
            videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 1080);
        }

        //capture.set(Videoio.CAP_PROP_EXPOSURE,-3);
        videoCapture.set(Videoio.CAP_PROP_AUTO_EXPOSURE, 0.25);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Capture.class.getName()).log(Level.SEVERE, null, ex);
        }

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

        System.out.println("Starting Thread");
        thread = new Thread(this);
        thread.start();
    }

    public boolean fastCaptureCameraImage() {
        if (videoCapture.isOpened()) {
            // If there is next video frame
            if (videoCapture.read(matrix)) {
                return true;
            }
        }
        return false;
    }

    private void captureBackground() {
        if (videoCapture.isOpened()) {
            try {
                // Read but discard the frame
                Mat tmpMatrix = new Mat();
                mutex.acquire();
                videoCapture.read(tmpMatrix);
                mutex.release();

                //Resize the image
                Mat resizedimage = new Mat();
                Size scaleSize = new Size(tmpMatrix.width() / 2, tmpMatrix.height() / 2);
                resize(tmpMatrix, resizedimage, scaleSize, 0, 0, Imgproc.INTER_AREA);

                //Find tags and show image
                ImageUtils.paintTags(resizedimage, camParam);
                ImageIcon image = new ImageIcon(ImageConverter.Mat2BufferedImage(resizedimage));
                ((JLabel) jFrameOriginal.getContentPane()).setIcon(image);
                ((JLabel) jFrameOriginal.getContentPane()).repaint();

                if (undistort) {
                    //We undistort each image before procesing them
                    Mat matrixUndistorted = new Mat();
                    Calib3d.undistort(tmpMatrix, matrixUndistorted, intrinsic, distorsion, optimized);

                    //Resize the image
                    Mat undistResizedimage = new Mat();
                    resize(matrixUndistorted, undistResizedimage, scaleSize, 0, 0, Imgproc.INTER_AREA);

                    //Find tags and show image
                    ImageUtils.paintTags(undistResizedimage, camParam);
                    ImageIcon imageUndist = new ImageIcon(ImageConverter.Mat2BufferedImage(undistResizedimage));
                    ((JLabel) jFrameUndist.getContentPane()).setIcon(imageUndist);
                    ((JLabel) jFrameUndist.getContentPane()).repaint();
                }

                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(CameraCapture.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean capture() {
        try {
            mutex.acquire();
            // Reading the next video frame from the camera
            matrix = new Mat();
            // videoCapture.read(matrix);

            if (videoCapture.isOpened()) {
                // If there is next video frame
                if (videoCapture.read(matrix)) {

                    if (undistort) {
                        //We undistort each image before procesing them
                        Mat matrixDst = new Mat();
                        Calib3d.undistort(matrix, matrixDst, intrinsic, distorsion, optimized);

                        //ONLY FOR DEBUGGING
                        //String file ="/home/fai/sample.jpg";
                        //Imgcodecs.imwrite(file, matrix);
                        //file ="/home/fai/sampleUndist.jpg";
                        //Imgcodecs.imwrite(file, matrixDst);

                        matrix = matrixDst;

                    }

                    //System.out.println("Camera frame taken");
                    int length = (int) (matrix.total() * matrix.elemSize());
                    //System.out.println("Length mat1: " + length + " Size mat1: " + matrix.size());
                    // Create the black and white image
                    Mat mat1 = new Mat(matrix.size(), CvType.CV_8UC1);
                    Imgproc.cvtColor(matrix, mat1, Imgproc.COLOR_RGB2GRAY);

//                Mat matud = new Mat(matrix.size(), CvType.CV_8UC1);
//
//                Mat camMatrix = new Mat( 3, 3, CvType.CV_32FC1);
//                int row = 0, col = 0;
//                camMatrix.put(row ,col, camParam.get_px(), 0, camParam.get_u0(), 0, camParam.get_py(), camParam.get_v0(), 0, 0, 1 );
//                row = 0;
//                col = 0;
//                Mat camDMatrix = new Mat( 1, 4, CvType.CV_32FC1);
//                camDMatrix.put(row ,col, camParam.get_kud() ,0 , 0, 0);
//System.out.println("camMatrix" + camMatrix.dump());
//System.out.println("dist Matrix" + camDMatrix.dump());
//                Calib3d.undistort(mat1, matud,camMatrix , camDMatrix);
//              // Creating buffer from the matrix
//              length = (int) (matud.total() * matud.elemSize());
//              //System.out.println("Length mat1: " + length + " size: " + mat1.size());
//              byte buffer[] = new byte[length];
//              matud.get(0, 0, buffer);
//
//              bImage = new BufferedImage(matud.width(), matud.height(), BufferedImage.TYPE_BYTE_GRAY);
//              bImage.getRaster().setDataElements(0, 0, matud.width(), matud.height(), buffer);
//
//              // Create VpImageUChar
//              I = new VpImageUChar(buffer, matud.height(), matud.width(), true);
// Creating buffer from the matrix
                    length = (int) (mat1.total() * mat1.elemSize());
//System.out.println("Length mat1: " + length + " size: " + mat1.size());
                    byte buffer[] = new byte[length];
                    mat1.get(0, 0, buffer);

                    bImage = new BufferedImage(mat1.width(), mat1.height(), BufferedImage.TYPE_BYTE_GRAY);
                    bImage.getRaster().setDataElements(0, 0, mat1.width(), mat1.height(), buffer);

// Create VpImageUChar
                    I = new VpImageUChar(buffer, mat1.height(), mat1.width(), true);
                    mutex.release();
                    return true;
                }
            } else {
                System.out.println("Camera is not open");
            }
            mutex.release();

        } catch (InterruptedException ex) {
            Logger.getLogger(CameraCapture.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void saveImage(String path) {
        if (matrix != null && videoCapture.isOpened()) {
            Imgcodecs.imwrite(path, matrix);
        }
    }

    public void closeCamera() {
        videoCapture.release();
    }

    @Override
    public void run() {
        while (true) {
            captureBackground();
        }
    }

}
