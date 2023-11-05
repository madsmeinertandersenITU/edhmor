package manipulator;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Wrench {
    
    private Force force;
    private Torque torque;
    
    public Force getForce() {
        return force;
    }
    
    public Vector3D getForceVector() {
        return new Vector3D(force.getX(), force.getY(), force.getZ());
    }
    
    public Torque getTorque() {
        return torque;
    }
    
    public Vector3D getTorqueVector() {
        return new Vector3D(torque.getX(), torque.getY(), torque.getZ());
    }
    
}
