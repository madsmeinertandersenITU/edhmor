package math.util;

import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

public class QuaternionMath {

    public static double dotProduct(Rotation r1, Rotation r2) {
        return r1.getQ0() * r2.getQ0() + r1.getQ1() * r2.getQ1() + r1.getQ2() * r2.getQ2() + r1.getQ3() * r2.getQ3();
    }

    public static boolean areClose(Rotation r1, Rotation r2) {
        double dot = dotProduct(r1, r2);
        System.out.println("dotProduct: " + dot);

        return dot > (0.0);

    }

    public static Rotation inverseSign(Rotation r) {
        return new Rotation(-r.getQ0(), -r.getQ1(), -r.getQ2(), -r.getQ3(), true);
    }

    public static Rotation average(Rotation r1, Rotation r2) {
        double w = 0.0;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        if (!areClose(r1, r2)) {
            r2 = inverseSign(r2);
        }

        w = (r1.getQ0() + r2.getQ0()) / 2;
        x = (r1.getQ1() + r2.getQ1()) / 2;
        y = (r1.getQ2() + r2.getQ2()) / 2;
        z = (r1.getQ3() + r2.getQ3()) / 2;

        return new Rotation(w, x, y, z, true);

    }

    public static Rotation average(List<Rotation> rots) {

        if (rots.isEmpty()) {
            return null;
        }

        Rotation firstRot = rots.get(0);
        double w = 0.0;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        for (int i = 0; i < rots.size(); i++) {
            Rotation r2 = rots.get(i);
            if (!areClose(firstRot, r2)) {
                System.out.println("Invert sign of quaternion, rot" + i);
                r2 = inverseSign(r2);
            }
            w+=r2.getQ0();
            x+=r2.getQ1();
            y+=r2.getQ2();
            z+=r2.getQ3();
        }
        return new Rotation(w/rots.size(), x/rots.size(), y/rots.size(), z/rots.size(), true);
        
    }

}
