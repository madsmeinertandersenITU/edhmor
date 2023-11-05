#!/usr/bin/env python


import rospy
from geometry_msgs.msg import WrenchStamped


def fake_force_sensors_publisher():
    pub = rospy.Publisher('wrench', WrenchStamped, queue_size=10)
    rospy.init_node('fake_force_sensors_publisher', anonymous=True)
    rate = rospy.Rate(10) # 10hz
    while not rospy.is_shutdown():
        msg = WrenchStamped()
        #rospy.loginfo(msg)
        pub.publish(msg)
        rate.sleep()


if __name__ == '__main__':
    try:
        fake_force_sensors_publisher()
    except rospy.ROSInterruptException:
        pass
