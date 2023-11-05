// / *
// * EDHMOR - Evolutionary designer of heterogeneous modular robots
// * <https://bitbucket.org/afaina/edhmor>
// * Copyright (C) 2022 Andres Faiña <anfv at itu.dk> (ITU)
// *
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU General Public License
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
// */

package apriltag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.visp.core.VpCameraParameters;
import org.visp.core.VpHomogeneousMatrix;
import org.visp.core.VpImagePoint;
import org.visp.core.VpImageUChar;
import org.visp.detection.VpDetectorAprilTag;
import org.visp.core.VpQuaternionVector;
import org.visp.core.VpTranslationVector;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class AprilTagDetector {

    private List<VpHomogeneousMatrix> cMo_vec;
    private List<VpQuaternionVector> quat_vec;
    private List<VpTranslationVector> trans_vec;

    private int tagFamily = AprilTagFamily.TAG_36h11;
    private VpDetectorAprilTag detector;
    private VpImageUChar I;
    private VpCameraParameters camParam;
private VpCameraParameters realCamParam = new
/ VpCameraParameters(617.61502682912123, 618.3116453432466,
/ 301.58428853313404,
263.25723912627234, 0.070231314502182465, -0.068364835826872083);
private VpCameraParameters simCamParam = new
/ VpCameraParameters(442.41113895876384, 442.9774077525268,
/ 255.73451273768146,
254.01329695995091);

    List<List<VpImagePoint>> tags_corners;
    private boolean detected = false;
    private int[] tagsId;
    private double[] tagSize;
    private static final double DEFAULT_TAG_SIZE = 0.04;

    List<Rotation> rotation_vec;
    List<Vector3D> translation_vec;

public AprilTagDetector(VpImageUChar I, int tagFamiliy, VpCameraParameters
/ realCamParams, double tagSize) {
this.I = I;
this.tagFamily = tagFamiliy;
initTagSizes(tagSize);
this.detector = new VpDetectorAprilTag();
this.detector.setAprilTagFamily(this.tagFamily);
camParam = realCamParams;
detect();
}

public AprilTagDetector(VpImageUChar I, int tagFamily, VpCameraParameters
/ realCamParams) {
this(I, tagFamily, realCamParams, DEFAULT_TAG_SIZE);
}

    public final boolean detect() {
        if (this.detector.detect(this.I)) {
            tagsId = detector.getTagsId();
            tags_corners = detector.getTagsCorners();

            cMo_vec = new ArrayList<>();
            quat_vec = new ArrayList<>();
            trans_vec = new ArrayList<>();
            for (int i = 0; i < tagsId.length; i++) {
                VpHomogeneousMatrix cMo = new VpHomogeneousMatrix();
                VpQuaternionVector poseQuaternion = new VpQuaternionVector();
                VpTranslationVector poseTranslation = new VpTranslationVector();
                detector.getPose(i, tagSize[i], camParam, cMo);
                cMo_vec.add(cMo);
                cMo.extract(poseTranslation);
                trans_vec.add(poseTranslation);
                cMo.extract(poseQuaternion);
                quat_vec.add(poseQuaternion);
            }
            // System.out.println("Counts: " + cMo_vec.size() + " " + tagsId.length);
            fillTagPoseTranslationList();
            fillTagPoseRotationList();
            detected = true;
            return detected;
        } else {
            detected = false;
            return detected;
        }
    }

    public int[] getTagsId() {
        return tagsId;
    }

    public double[] getTagSize() {
        return tagSize;
    }

    public void setTagSize(double[] sizes) {
        tagSize = sizes;
    }

    private void initTagSizes(double size) {
        tagSize = new double[TagUtils.getMAX_TAG_ID()];
        for (int i = 0; i < tagSize.length; i++) {
            tagSize[i] = size;
        }
    }

    public List<List<VpImagePoint>> getTags_corners() {
        return tags_corners;
    }

    public List<VpQuaternionVector> getQuat_vec() {
        return quat_vec;
    }

    public List<VpTranslationVector> getTrans_vec() {
        return trans_vec;
    }

    public List<VpHomogeneousMatrix> getcMo_vec() {
        return cMo_vec;
    }

    public Rotation getTagPoseRotation(int tag) {
        int index = tagIndex(tag);
        if (index >= 0)
            return rotation_vec.get(index);
        return null;
    }

    public List<Rotation> getTagPoseRotationList() {
        return rotation_vec;
    }

private List<Rotation> fillTagPoseRotationList() {
rotation_vec = new ArrayList<>();
for (int i = 0; i < tagsId.length; i++) {
Rotation rotation = new Rotation(quat_vec.get(i).w(), quat_vec.get(i).x(),
/ quat_vec.get(i).y(),
quat_vec.get(i).z(), false);
rotation_vec.add(rotation);
}
return rotation_vec;
}

    public List<Vector3D> getTagPoseTranslationList() {
        return translation_vec;
    }

    public Vector3D getTagPoseTranslation(int tag) {
        int index = tagIndex(tag);
        if (index >= 0)
            return translation_vec.get(index);
        return null;
    }

private List<Vector3D> fillTagPoseTranslationList() {
translation_vec = new ArrayList<>();
for (int i = 0; i < tagsId.length; i++) {
String transString = this.trans_vec.get(i).toString();
String[] arrOfStr = transString.split("\n", 5);
Vector3D translation = new Vector3D(Double.parseDouble(arrOfStr[0]),
/ Double.parseDouble(arrOfStr[1]),
Double.parseDouble(arrOfStr[2]));
translation_vec.add(translation);
}
return translation_vec;
}

    public VpImageUChar getI() {
        return I;
    }

    public void setI(VpImageUChar i) {
        I = i;
    }

    public boolean isDetected() {
        return detected;
    }

    public int getTagFamily() {
        return tagFamily;
    }

    public void setTagFamily(int tagFamily) {
        this.tagFamily = tagFamily;
    }

    public VpCameraParameters getCamParam() {
        return camParam;
    }

    public boolean tagFound(int refTag) {
        int referenceTagPosition = tagIndex(refTag);
        return referenceTagPosition != -1;
    }

    public boolean tagFounds() {
        if (tagsId == null)
            return false;
        return tagsId.length > 0;
    }

    public int tagIndex(int refTag) {
        if (detected) {
            int referenceTagPosition = IntStream.range(0, tagsId.length)
                    .filter(idx -> tagsId[idx] == refTag)
                    .findFirst()
                    .orElse(-1);
            return referenceTagPosition;
        }
        return -1;
    }
}
