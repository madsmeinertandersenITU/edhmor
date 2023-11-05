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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.visp.core.VpImageUChar;

/**
 * ImageConverter.java Created on 12/09/2021
 *
 * @author Andres Faiña <anfv  at itu.dk>
 */
public class ImageConverter {

    public static VpImageUChar Mat2VpImageUChar(Mat matrix) {
        int length = (int) (matrix.total() * matrix.elemSize());
        //System.out.println("Length mat1: " + length + " Size mat1: " + matrix.size());
        // Create the black and white image
        Mat mat1;
        if (matrix.type() != CvType.CV_8UC1) {
            mat1 = new Mat(matrix.size(), CvType.CV_8UC1);
            Imgproc.cvtColor(matrix, mat1, Imgproc.COLOR_RGB2GRAY);
        } else {
            mat1 = matrix;
        }

        // Creating buffer from the matrix
        length = (int) (mat1.total() * mat1.elemSize());
        //System.out.println("Length mat1: " + length + " size: " + mat1.size());
        byte buffer[] = new byte[length];
        mat1.get(0, 0, buffer);

        BufferedImage bImage = new BufferedImage(mat1.width(), mat1.height(), BufferedImage.TYPE_BYTE_GRAY);
        bImage.getRaster().setDataElements(0, 0, mat1.width(), mat1.height(), buffer);

        // Create VpImageUChar
        return new VpImageUChar(buffer, mat1.height(), mat1.width(), true);
    }

    public static BufferedImage Mat2BufferedImage(Mat m) {
        //Method converts a Mat to a Buffered Image
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }

    public static BufferedImage VpImageUChar2BufferedImage(VpImageUChar image) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        byte[] b = image.getPixels(); // get all the pixels
        BufferedImage I = new BufferedImage(image.cols(), image.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) I.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return I;
    }

    public static Mat BufferedImage2Mat(BufferedImage in) {
        Mat out;
        byte[] data;
        int r, g, b;

        if (in.getType() == BufferedImage.TYPE_INT_RGB) {
            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
            for (int i = 0; i < dataBuff.length; i++) {
                data[i * 3] = (byte) ((dataBuff[i] >> 0) & 0xFF);
                data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
                data[i * 3 + 2] = (byte) ((dataBuff[i] >> 16) & 0xFF);
            }
        } else {
            out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC1);
            data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
            for (int i = 0; i < dataBuff.length; i++) {
                r = (byte) ((dataBuff[i] >> 0) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i] >> 16) & 0xFF);
                data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b));
            }
        }
        out.put(0, 0, data);
        return out;
    }
}
