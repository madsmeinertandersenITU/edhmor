package manipulator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;



public class Pose {
	
	private Position position;
	private Orientation orientation;
	
	public Pose(Position position, Orientation orientation) {
		this.position = position;
		this.orientation = orientation;
	}

	public Position getPosition() {
		return position;
	}
        
        public Vector3D getPositionVector() {
		return new Vector3D(position.getX(), position.getY(), position.getZ());
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}
	
        @Override
	public String toString(){
            return "[" + position.toString() + ", " + orientation.toString() + "]"; 
        }
	
	
}
