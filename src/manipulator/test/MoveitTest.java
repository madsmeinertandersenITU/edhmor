package manipulator.test;

import com.google.gson.Gson;


import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.std.Header;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;
import manipulator.ROSRequest;
import manipulator.ROSResponse;
import manipulator.Pose;
import manipulator.Position;
import manipulator.Orientation;


public class MoveitTest {
	
	static ROSRequest requestObject;
	static ROSResponse responseObject;
	static Gson g = new Gson();
	
	public static void main(String[] args) throws InterruptedException {
		Ros ros = new Ros("localhost");
		ros.connect();
		
		Service robot_command = new Service(ros, "/robot_poseArray", "testing_ur5/robot_poseArray");
		
		//ServiceRequest request = new ServiceRequest("{\"command\": \"pick_pose\"}");
		
		//requestObject = new ROSRequest("pose", new Pose(new Point(0.5, 0.0, 0.3), new Quaternion(0.0, 0.707, 0.0, 0.707)));
		Pose pose1 = new Pose(new Position(-0.3, -0.425559036224, 0.3), new Orientation(0.0, -0.707, -0.707, 0.0));
		Pose pose2 = new Pose(new Position(-0.4, -0.15, 0.3), new Orientation(0.0, -0.707, -0.707, 0.0));
		Pose pose3 = new Pose(new Position(-0.4, -0.15, 0.3), new Orientation(0.5, -0.5, -0.5, -0.5));
		requestObject = new ROSRequest("cartesian",new Pose[]{pose1,pose2,pose3});
		
		String requestJson = g.toJson(requestObject);
				
		System.out.println("Json output: "+ requestJson);
		
		//ServiceRequest request = new ServiceRequest("{\"command\": \"pose\",\"eefPose\": {\"position\": {\"x\": 0.5, \"y\": 0.15, \"z\": 0.3}, \"orientation\": {\"w\": 0.707, \"x\": 0.0, \"y\": 0.707, \"z\": 0.0} } }");
		ServiceRequest request = new ServiceRequest(requestJson);
		ServiceResponse response = robot_command.callServiceAndWait(request);
		//System.out.println(response.toString());
				
		responseObject = g.fromJson(response.toString(), ROSResponse.class);
				
		System.out.println("Response from server: message: " + responseObject.getMessage() 
                        + ", success: " + responseObject.isSuccess() 
                        + ", fraction: " + responseObject.getFraction() + "\n"
                        + "Pose: " + responseObject.getPose().getPosition().toString() + "\n"
                        + responseObject.getPose().getOrientation().toString());
		
		requestObject = new ROSRequest("pose",new Pose[]{pose1});
		requestJson = g.toJson(requestObject);
		System.out.println("Json output: "+ requestJson);
		request = new ServiceRequest(requestJson);
		response = robot_command.callServiceAndWait(request);
		
		responseObject = g.fromJson(response.toString(), ROSResponse.class);
		
		System.out.println("Response from server: message: " + responseObject.getMessage() 
                        + ", success: " + responseObject.isSuccess() 
                        + ", fraction: " + responseObject.getFraction() + "\n"
                        + "Pose: " + responseObject.getPose().getPosition().toString() + "\n"
                        + responseObject.getPose().getOrientation().toString());
				
		ros.disconnect();
		
	}
	
	
}
