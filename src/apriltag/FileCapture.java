/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apriltag;

import java.awt.image.BufferedImage;
import java.io.File;
import org.opencv.core.Mat;
import org.visp.core.VpImageUChar;
import org.visp.io.VpImageIo;

/**
 *
 * @author anfv
 */
public class FileCapture extends Capture {

    private String filePath;

    public FileCapture(String path) {
        filePath = path;
    }

    public boolean capture(String fileName) {
        I = new VpImageUChar();
        VpImageIo.read(I, fileName);
        bImage = ImageConverter.VpImageUChar2BufferedImage(I);
        return true;
    }

    public boolean capture() {
        I = new VpImageUChar();
        File file = new File(this.filePath);
        VpImageIo.read(I, file.getAbsolutePath());
        return true;
    }

    @Override
    public Mat getMat() {
        matrix = ImageConverter.BufferedImage2Mat(getbImage());
        return matrix;
    }

    @Override
    public BufferedImage getbImage() {
        //TODO: use a direct transformation VpImageUChar2Mat
        bImage = ImageConverter.VpImageUChar2BufferedImage(I);
        return bImage;
    }

}
