package apriltag.tests;

import apriltag.ImageConverter;
import apriltag.ImageResolution;
import apriltag.CameraCapture;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import modules.util.PhysicalSetupConfiguration;

import org.visp.core.VpImageUChar;
import org.visp.io.VpImageIo;
import org.opencv.core.Core;

public class TakeImagesForCalibration {

    static {
        System.loadLibrary("visp_java341");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

        final ImageResolution resolution = PhysicalSetupConfiguration.getArenaCeilingCamResolution();
        String dir = "/home/fai/camera-calibration/";
        int CAMERA_INDEX = PhysicalSetupConfiguration.getArenaCeilingCamIndex();

        dir += resolution.name() + "/";

        try {
            Path path = Paths.get(dir);
            Files.createDirectories(path);
        } catch (IOException ex) {
            Logger.getLogger(TakeImagesForCalibration.class.getName()).log(Level.SEVERE, null, ex);
        }

        File directoryForImages = new File(dir);
        if (directoryForImages.list().length > 2) {
            System.out.println("There are files in the folder. Delete them and try again...");
            System.exit(-1);
        }

        //Ucomment to get a image from the webcam
        CameraCapture imageCapture = new CameraCapture(CAMERA_INDEX, 
                PhysicalSetupConfiguration.getArenaCeilingIntrinsicVispCamParam(), 
                resolution);
        imageCapture.capture();
        JFrame frame = null;
        int i = 0;
        Scanner scanner = new Scanner(System.in);
        while (true) {

            System.out.println("Press enter to take picture n. " + i + " or (c)ancel to stop the program");

            String a = scanner.nextLine();
            if (a.contains("c")) {
                System.out.println("Exiting. Close the image to finish the program.");
                return;
            }

            if (frame != null) {
                //frame.setVisible(false);
                frame.dispose();
            }

            flushCamera(imageCapture);
            imageCapture.capture();
            if (frame != null) {
                //frame.setVisible(false);
                frame.dispose();
            }
            frame = createFrame(imageCapture.getbImage(), imageCapture.getI());

            System.out.println("Save frame? Press enter to acept and n(o) to discard it. " + i);

            a = scanner.nextLine();
            if (a.contains("n")) {
                continue;
            } else {
                VpImageIo.write(imageCapture.getI(), dir + "chessboard-" + String.format("%02d", i) + ".jpg");
                i++;
            }

        }
    }

    public static void showPicture(VpImageUChar I, BufferedImage bImage) {
        createFrame(bImage, I);
    }

    public static JFrame createFrame(BufferedImage bImage, VpImageUChar I) {
        BufferedImage canvas = new BufferedImage(bImage.getWidth(), bImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
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

    static void flushCamera(CameraCapture camera) {
        //double bufferSize = camera.getVideoCapture().get(Videoio.CAP_PROP_BUFFERSIZE);
        int framesToRead = 2;//(int) bufferSize + 1;
        for (int i = 0; i < framesToRead; i++) {
            camera.capture();
        }
    }

}
