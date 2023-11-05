package manipulator;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

public class Orientation {
	
	public double w = 0.0;
	public double x = 0.0;
	public double y = 0.0;
	public double z = 0.0;
	
	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public Orientation(double w, double x, double y, double z) {
		super();
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

        @Override
	public String toString(){
            return "(" + w + " " + x + " " + y + " " + z + ")"; 
        }
        
        public static String rot2str(Rotation rot) {
        String quaternion = String.format("(%.3f, %.3f, %.3f, %.3f)", rot.getQ0(), rot.getQ1(), rot.getQ2(), rot.getQ3());
        double angle = 2*Math.acos(rot.getQ0());
        double div = Math.sin(angle/2);
        quaternion += String.format(" - %.3f degrees around (%.1f, %.3f, %.3f)", angle*180/Math.PI, rot.getQ1()/div, rot.getQ2()/div, rot.getQ3()/div); 
        return quaternion;
    }
	
}
