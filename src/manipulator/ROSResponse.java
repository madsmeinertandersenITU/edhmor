package manipulator;

public class ROSResponse {

    private String message = "";
    private boolean success = false;
    private float fraction = 0.0f;
    private Pose current_pose;
    private Wrench force_sensors;

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public float getFraction() {
        return fraction;
    }

    public Pose getPose() {
        return current_pose;
    }
    
    public Wrench getWrench() {
        return force_sensors;
    }
}
