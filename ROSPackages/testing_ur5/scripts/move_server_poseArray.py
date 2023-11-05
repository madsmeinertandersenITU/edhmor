#!/usr/bin/env python

from __future__ import print_function


import sys
import copy
import rospy
import math
import moveit_commander
import moveit_msgs.msg
import geometry_msgs.msg
from geometry_msgs.msg import WrenchStamped
from math import pi
from moveit_commander.conversions import pose_to_list

from testing_ur5.srv import robot_poseArray,robot_poseArrayResponse
import rospy


def all_close(goal, actual, tolerance):
  """
  Convenience method for testing if a list of values are within a tolerance of their counterparts in another list
  @param: goal       A list of floats, a Pose or a PoseStamped
  @param: actual     A list of floats, a Pose or a PoseStamped
  @param: tolerance  A float
  @returns: bool
  """
  all_equal = True
  if type(goal) is list:
    #for index in range(len(goal)):
    for index in range(3): #TODO implement quaternion checking
      #print(abs(actual[index] - goal[index]))
      if abs(actual[index] - goal[index]) > tolerance:
        return False

  elif type(goal) is geometry_msgs.msg.PoseStamped:
    return all_close(goal.pose, actual.pose, tolerance)

  elif type(goal) is geometry_msgs.msg.Pose:
    return all_close(pose_to_list(goal), pose_to_list(actual), tolerance)

  return True
#Class for moving the UR5 robot with the moveit commander, with default planner
class MoveUR5(object):

  def __init__(self):
    super(MoveUR5, self).__init__()

    ## First initialize `moveit_commander`_ and a `rospy`_ node:
    moveit_commander.roscpp_initialize(sys.argv)
    #rospy.init_node('moveTestUR5', anonymous=True)
    rospy.init_node('robot_pose_server')

    ## Instantiate a `RobotCommander`_ object. Provides information such as the robot's
    ## kinematic model and the robot's current joint states
    robot = moveit_commander.RobotCommander()

    ## Instantiate a `PlanningSceneInterface`_ object.  This provides a remote interface
    ## for getting, setting, and updating the robot's internal understanding of the
    ## surrounding world:
    scene = moveit_commander.PlanningSceneInterface()

    ## Instantiate a `MoveGroupCommander`_ object.  This object is an interface
    ## to a planning group (group of joints).
    ## This interface can be used to plan and execute motions:
    group_name = "manipulator" #From the UR5 urdf definition
    move_group = moveit_commander.MoveGroupCommander(group_name)

    ## Create a `DisplayTrajectory`_ ROS publisher which is used to display
    ## trajectories in Rviz:
    #display_trajectory_publisher = rospy.Publisher('/move_group/display_planned_path',
                                                   #moveit_msgs.msg.DisplayTrajectory,
                                                   #queue_size=20)
    ## Getting Basic Information
    ## ^^^^^^^^^^^^^^^^^^^^^^^^^
    # We can get the name of the reference frame for this robot:
    planning_frame = move_group.get_planning_frame()
    print("MOVE_SERVER: ============ Planning frame: %s" % (planning_frame))

    # We can also print the name of the end-effector link for this group:
    eef_link = move_group.get_end_effector_link()
    print("MOVE_SERVER: ============ End effector link: %s" % (eef_link))

    # We can get a list of all the groups in the robot:
    group_names = robot.get_group_names()
    print ("MOVE_SERVER: ============ Available Planning Groups:", robot.get_group_names())

    # Setting the planner timeout time in seconds:
    timeout = 20
    move_group.set_planning_time(timeout)
    print ("MOVE_SERVER: ============ Planning time timeout:", timeout)

    # Sometimes for debugging it is useful to print the entire state of the
    # robot:
    print ("MOVE_SERVER: ============ Printing robot state")
    print(robot.get_current_state())
    print ("")
    ## END_SUB_TUTORIAL

    self.robot = robot
    self.scene = scene
    self.move_group = move_group
    self.box_name = ""
    #self.display_trajectory_publisher = display_trajectory_publisher
    self.planning_frame = planning_frame
    self.eef_link = eef_link
    self.group_names = group_names
  
  #Return the current pose of the eef
  def get_pose(self):
    return self.move_group.get_current_pose().pose

  #Return the current pose of the eef
  def get_force_sensors(self):
    sensor_msg = rospy.wait_for_message('/wrench',WrenchStamped,timeout=5)
    return sensor_msg.wrench

  def plan_cartesian_path(self,  posearray):
    ##
    ## Cartesian Paths
    ## ^^^^^^^^^^^^^^^
    ## You can plan a Cartesian path directly by specifying a list of waypoints
    ## for the end-effector to go through. If executing  interactively in a
    ## Python shell, set scale = 1.0.
    ##
    waypoints = posearray

    # We want the Cartesian path to be interpolated at a resolution of 1 cm
    # which is why we will specify 0.01 as the eef_step in Cartesian
    # translation. "The jump_threshold parameter limits the the maximum 
    # distance or jump in joint positions between two consecutive trajectory points.
    # When this parameter is set, the planner returns a partial solution if consecutive 
    #joint positions exceed the threshold, so the robot will no longer move jerkily.
    # https://thomasweng.com/moveit_cartesian_jump_threshold/
    # ditance between consecutive joint positions <= jump_threshold * mean joint position distance
    (plan, fraction) = self.move_group.compute_cartesian_path(
                                       waypoints,   # waypoints to follow
                                       0.01,        # eef_step
                                       5.0)         # jump_threshold Test for our specific case

    # return the planned trajectory and the fraction of the original trajectory that was followed
    return plan, fraction


  def execute_plan(self, plan):

    ## Executing a Plan
    ## ^^^^^^^^^^^^^^^^
    ## Use execute if you would like the robot to follow
    ## the plan that has already been computed:
    success = self.move_group.execute(plan, wait=True)

    ## **Note:** The robot's current joint state must be within some tolerance of the
    ## first waypoint in the `RobotTrajectory`_ or ``execute()`` will fail
    # In our case this is 0,01m in cartesian space but joint space deviation can also affect
    return success

  def go_to_home(self):

    ## Planning to a Joint Goal
    ## ^^^^^^^^^^^^^^^^^^^^^^^^
    ## Move the robot to an appropiate joint_space configuration so that the planner can 
    ## work with not many failed attempts
    # We can get the joint values from the group and adjust some of the values:
    joint_goal = self.move_group.get_current_joint_values()
    
    #Joint values for the hardware robot original home position
    #joint_goal[0] = 0
    #joint_goal[1] = -pi/2
    #joint_goal[2] = 0
    #joint_goal[3] = -pi/2
    #joint_goal[4] = 0
    #joint_goal[5] = 0
    #joint_goal[6] = 0

    joint_goal[0] = 0#pi/2 0
    joint_goal[1] = -1.9049# -1.7947
    joint_goal[2] = 1.9520 #1.2356
    joint_goal[3] = -1.6088#pi -1.033
    joint_goal[4] = -pi/2#-pi/2 -1.5710
    joint_goal[5] = 0# 0.0051

    # The go command can be called with joint values, poses, or without any
    # parameters if you have already set the pose or joint target for the group
    self.move_group.go(joint_goal, wait=True)

    # Calling ``stop()`` ensures that there is no residual movement
    self.move_group.stop()

    # Check if joint values are within tolerance with the goal position:
    current_joints = self.move_group.get_current_joint_values()
    return all_close(joint_goal, current_joints, 0.01)


  def go_to_calibrationHome(self):

    ## Planning to a Joint Goal
    ## ^^^^^^^^^^^^^^^^^^^^^^^^
    ## Move the robot to an appropiate joint_space configuration so that the planner can 
    ## work with not many failed attempts
    # We can get the joint values from the group and adjust some of the values:
    joint_goal = self.move_group.get_current_joint_values()
    
    #Joint values for the hardware robot original home position
    #joint_goal[0] = 0
    #joint_goal[1] = -pi/2
    #joint_goal[2] = 0
    #joint_goal[3] = -pi/2
    #joint_goal[4] = 0
    #joint_goal[5] = 0
    #joint_goal[6] = 0

    joint_goal[0] = 0#pi/2 0
    joint_goal[1] = -1.9049# -1.7947
    joint_goal[2] = 1.9520 #1.2356
    joint_goal[3] = -1.6088#pi -1.033
    joint_goal[4] = pi/2#-pi/2 -1.5710
    joint_goal[5] = 0# 0.0051

    # The go command can be called with joint values, poses, or without any
    # parameters if you have already set the pose or joint target for the group
    self.move_group.go(joint_goal, wait=True)

    # Calling ``stop()`` ensures that there is no residual movement
    self.move_group.stop()

    # Check if joint values are within tolerance with the goal position:
    current_joints = self.move_group.get_current_joint_values()
    return all_close(joint_goal, current_joints, 0.01)


  def go_to_pose(self, pose, attempts):

    ## Planning to a Pose Goal
    ## ^^^^^^^^^^^^^^^^^^^^^^^
    ## We can plan a motion for this group to a desired pose for the
    ## end-effector:
    
    # Specify pose for the end effector
    #pose_goal = geometry_msgs.msg.Pose()
    #pose_goal.orientation.w = 1.0
    #pose_goal.position.x = 0.4 #meters
    #pose_goal.position.y = 0.0
    #pose_goal.position.z = 0.4
    pose_goal = pose


    #mag = math.sqrt(pose_goal.position.x*pose_goal.position.x+pose_goal.position.y*pose_goal.position.y+pose_goal.position.z*pose_goal.position.z)
    #print ("Point in ", mag)

    self.move_group.set_pose_target(pose_goal)
    #move_group.set_named_target("home")
    success = False
    ## Repeat in case of failure
    for i in range(attempts):
    
        ## Now, we call the planner to compute the plan and execute it.
        plan = self.move_group.go(wait=True)
        
        #success,plan,time,error = self.move_group.plan()
        #success = self.move_group.plan.execute(plan, wait=True)
        # Calling `stop()` ensures that there is no residual movement
        self.move_group.stop()
        # It is always good to clear your targets after planning with poses.
        # Note: there is no equivalent function for clear_joint_value_targets()
        self.move_group.clear_pose_targets()

        # Check if joint values are within tolerance with the goal position:
        current_pose = self.move_group.get_current_pose().pose
        if (all_close(pose_goal, current_pose, 0.01)):
            success = True
            print("MOVE_SERVER: Successful after "+ str(i+1) +" attempts")
            break
    return success

  def go_down_to_touch(self,force_threshold, step_size, window_filter, alpha_filter, attempts):

    ## Move down to touch the ground
    ## ^^^^^^^^^^^^^^^^^^^^^^^^
    ## Move the robot down by a step_size until a force_threshold is reached 
    ## A moving average force filter is used with past data alpha weight
    ## and a specified window
  
    # Check the values of the force sensors and initialize the force filter:
    wrench = self.get_force_sensors()
    force = wrench.force.z
    filtered_force = force

    while(abs(filtered_force) < force_threshold):
      # Move the robot down by step_size:
      pose = self.get_pose()
      pose.position.z = pose.position.z-step_size
      if (not self.go_to_pose(pose,attempts)):
        return False
      
      # Check the values of the force sensors window_filter times for every step:
      for i in range(window_filter):
        wrench = self.get_force_sensors()
        force = wrench.force.z
        filtered_force = filtered_force*alpha_filter+force*(1-alpha_filter)
    
      print("MOVE_SERVER: Going down, force filtered: "+str(filtered_force))

    return True
      

     
    
  
  def add_floor(self, timeout=4):
    
    # Add a box to act as a floor plane for the robot not to collide in reality and simulation
    self.box_name
    self.scene
    self.move_group


 
    ## Create a box with neutral orientation below the robot base:
    box_pose = geometry_msgs.msg.PoseStamped()
    box_pose.header.frame_id = self.move_group.get_planning_frame()
    box_pose.pose.position.x = 0.0
    box_pose.pose.position.y = 0.0
    box_pose.pose.position.z = -0.21 # Measured from the real robot to the plane
    self.box_name = "floor"
    self.scene.add_box(self.box_name, box_pose, size=(2, 2, 0.07)) #Arbitrary values to cover all the robot workspace

    return self.wait_for_state_update(box_is_known=True, timeout=timeout)

  def wait_for_state_update(self, box_is_known=False, box_is_attached=False, timeout=4):


    ## Ensuring Collision Updates Are Receieved
    ## ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    ## If the Python node dies before publishing a collision object update message, the message
    ## could get lost and the box will not appear. To ensure that the updates are
    ## made, we wait until we see the changes reflected in the
    ## ``get_attached_objects()`` and ``get_known_object_names()`` lists.
    ## We call this function after adding,
    ## removing, attaching or detaching an object in the planning scene. We then wait
    ## until the updates have been made or ``timeout`` seconds have passed
    start = rospy.get_time()
    seconds = rospy.get_time()
    while (seconds - start < timeout) and not rospy.is_shutdown():

      # Test if the box is in the scene.
      # Note that attaching the box will remove it from known_objects
      is_known = self.box_name in self.scene.get_known_object_names()

      # Test if we are in the expected state
      if (box_is_known == is_known):
        return True

      # Sleep so that we give other threads time on the processor
      rospy.sleep(0.1)
      seconds = rospy.get_time()

    # If we exited the while loop without returning then we timed out
    return False

#Class end




#Start of main program

# Create robot commander object from the class previously created 
robot_commander = MoveUR5()


#Handle incoming messages in the server
def handle_incoming(req):
  #Prepare default response fields
  success = False
  fraction = 0.0
  current_pose = robot_commander.get_pose()
  force_sensors = robot_commander.get_force_sensors()
  
  
  print("MOVE_SERVER: Receiving [%s]"%(req.command))
  
  if req.command == "home": #Go to the home position
        success = robot_commander.go_to_home()
        current_pose = robot_commander.get_pose()
        force_sensors = robot_commander.get_force_sensors()
        return robot_poseArrayResponse(success,fraction,"Home action attempted",current_pose,force_sensors)

  if req.command == "calibrationHome": #Go to the calibration position
        success = robot_commander.go_to_calibrationHome()
        current_pose = robot_commander.get_pose()
        force_sensors = robot_commander.get_force_sensors()
        return robot_poseArrayResponse(success,fraction,"Calibration Home action attempted",current_pose,force_sensors)
    
  elif req.command == "pose": #Go to a pose of the eef
  	success = robot_commander.go_to_pose(req.waypoints[0],10)
        current_pose = robot_commander.get_pose()
        force_sensors = robot_commander.get_force_sensors()
        return robot_poseArrayResponse(success,fraction,"Attempted to get to pose",current_pose,force_sensors)

  elif req.command == "down_to_touch": #Go down with the eef until touching the ground
  	success = robot_commander.go_down_to_touch(25, 0.0005, 3, 0.8, 10)
        current_pose = robot_commander.get_pose()
        force_sensors = robot_commander.get_force_sensors()
        return robot_poseArrayResponse(success,fraction,"Attempted to get to pose",current_pose,force_sensors)
    
  elif req.command == "cartesian": # Plan and execute a cartesian path, return a failed attempt if a full plan could not be computed
    cartesian_plan, fraction = robot_commander.plan_cartesian_path(req.waypoints)
    if fraction < 1.0:
      return robot_poseArrayResponse(False,fraction,"Cartesian path could not be planned",current_pose,force_sensors)
    else:
      success = robot_commander.execute_plan(cartesian_plan)
      current_pose = robot_commander.get_pose()
      force_sensors = robot_commander.get_force_sensors()
      return robot_poseArrayResponse(success,fraction,"Attempted to execute cartesian path",current_pose,force_sensors)
      
  elif req.command == "get_pose":
    success = True
    current_pose = robot_commander.get_pose()
    return robot_poseArrayResponse(success,fraction,"Returning current pose",current_pose,force_sensors)

  elif req.command == "get_force_sensors":
    success = True
    force_sensors = robot_commander.get_force_sensors()
    return robot_poseArrayResponse(success,fraction,"Returning force sensor values",current_pose,force_sensors)
    
  else: #Command not recognized
    return robot_poseArrayResponse(success,fraction,"Command not recognized",current_pose,force_sensors)
    


def robot_pose_server():
  #rospy.init_node('robot_command_server') #Node is initiated when creating the robot commander object
  s = rospy.Service('robot_poseArray', robot_poseArray, handle_incoming) #Initialize the server
  
  #print("Adding floor colission box")
  #robot_commander.add_floor()

  print("MOVE_SERVER: Moving robot to home")
  robot_commander.go_to_home()
  
  
  print("MOVE_SERVER: Ready to receive")
  rospy.spin() #Idle while waiting for requests to arrive

if __name__ == "__main__":
  robot_pose_server()
