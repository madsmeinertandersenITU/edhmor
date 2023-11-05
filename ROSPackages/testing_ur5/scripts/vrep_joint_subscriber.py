#!/usr/bin/env python

try:
    import sim
except:
    print ('--------------------------------------------------------------')
    print ('VREP_SUBSCRIBER: "sim.py" could not be imported.')
    print ('--------------------------------------------------------------')

import rospy
import os
from sensor_msgs.msg import JointState
from math import pi

clientID = -1
jointHandle = [-1,-1,-1,-1,-1,-1]
joint_offsets = [-pi/2,pi/2,0.0,pi/2,0.0,0.0]#Based on gazebo home pose values

def handle_shutdown():
	if clientID != -1:
		rospy.loginfo('VREP_SUBSCRIBER: Shutting down')
		#error = sim.simxStopSimulation(clientID, sim.simx_opmode_blocking)
        #if error != sim.simx_return_ok:
            #rospy.logwarn("VREP_SUBSCRIBER: Could not stop simulation, error code "+ str(error))
        sim.simxFinish(clientID)

def handle_message(data):
	#rospy.loginfo(rospy.get_caller_id() + "I heard %s", data.name[4])
	if clientID != -1:
		#sim.simxSetJointPosition(clientID,jointHandle,data.position[1],sim.simx_opmode_oneshot)
		sim.simxPauseCommunication(clientID,True)
        sim.simxSetJointTargetPosition(clientID,jointHandle[0],data.position[2]+joint_offsets[0],sim.simx_opmode_oneshot)
        sim.simxSetJointTargetPosition(clientID,jointHandle[1],data.position[1]+joint_offsets[1],sim.simx_opmode_oneshot)
        sim.simxSetJointTargetPosition(clientID,jointHandle[2],data.position[0]+joint_offsets[2],sim.simx_opmode_oneshot)
        sim.simxSetJointTargetPosition(clientID,jointHandle[3],data.position[3]+joint_offsets[3],sim.simx_opmode_oneshot)
        sim.simxSetJointTargetPosition(clientID,jointHandle[4],data.position[4]+joint_offsets[4],sim.simx_opmode_oneshot)
        sim.simxSetJointTargetPosition(clientID,jointHandle[5],data.position[5]+joint_offsets[5],sim.simx_opmode_oneshot)

        sim.simxPauseCommunication(clientID,False)


def vrep_joint_subscriber():

    rospy.init_node('vrep_joint_subscriber', anonymous=True)

    sim.simxFinish(-1) # just in case, close all opened connections

    global clientID
    rospy.loginfo('VREP_SUBSCRIBER: Connecting to CoppeliaSim...')
    IP = os.getenv('HOST_IP')
    clientID = sim.simxStart(IP,19996,True,True,5000,5) # Connect to CoppeliaSim
    rospy.loginfo('VREP_SUBSCRIBER: simxStart ended...')
    if clientID!=-1:
    	rospy.loginfo('VREP_SUBSCRIBER: Connected to remote API server')
    	global jointHandle
        for i in range(0,len(jointHandle)):
            error, jointHandle[i] = sim.simxGetObjectHandle(clientID,"UR5_joint"+str(i+1),sim.simx_opmode_blocking)
    	
        #sim.simxStartSimulation(clientID, sim.simx_opmode_oneshot)

        sim.simxPauseCommunication(clientID,True)
        for i in range(0,len(jointHandle)):
            sim.simxSetJointTargetPosition(clientID,jointHandle[i],joint_offsets[i],sim.simx_opmode_oneshot)
        sim.simxPauseCommunication(clientID,False)

        rospy.loginfo('VREP_SUBSCRIBER: Init coppeliasim complete')

        rospy.on_shutdown(handle_shutdown)
        rospy.Subscriber("joint_states", JointState, handle_message)

        rospy.spin()

    else:
        rospy.loginfo('VREP_SUBSCRIBER: Failed connecting to remote API server')
    	rospy.logerr('VREP_SUBSCRIBER: Failed connecting to remote API server, Shutting down!')

if __name__ == '__main__':
	vrep_joint_subscriber()
