package manipulator;




public class ROSRequest {
	
	private String command;
	private Pose[] waypoints;
	
	
	public String getCommand() {
		return command;
	}


	public void setCommand(String command) {
		this.command = command;
	}


	public Pose[] getPoseArray() {
		return waypoints;
	}


	public void setPose(Pose[] waypoints) {
		this.waypoints = waypoints;
	}

	public ROSRequest() {
		this.command = "";
		this.waypoints = new Pose[]{};
		
	}
	
	public ROSRequest(String command, Pose[] waypoints) {
		super();
		this.command = command;
		this.waypoints = waypoints;
	}
	

}
