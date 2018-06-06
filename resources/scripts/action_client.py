#! /usr/bin/env python


# Initializes a rospy node so that the SimpleActionClient can
# publish and subscribe over ROS.

import rospy
import sys
from std_msgs.msg import String, Bool
import actionlib
from roboy_communication_control.msg import PerformMovementAction, PerformMovementGoal

import time

body_part = sys.argv[1]
traj_name = sys.argv[2]

pub = rospy.Publisher('/roboy/control/EnablePlayback', Bool, queue_size=1)
rospy.init_node('roboy_dialog_actions_client')

msg = Bool()
msg.data = True
pub.publish(msg)

actionClient = actionlib.SimpleActionClient(
    body_part+'_movement_server',
    PerformMovementAction)
print("Waiting for 'movement_server'...")
actionClient.wait_for_server()
print("done")
name = body_part + "_" + traj_name
print(name)
movementAction = PerformMovementGoal(action=name)
actionClient.send_goal_and_wait(movementAction)
actionClient.wait_for_result()
actionClient.get_result()
rospy.spin()