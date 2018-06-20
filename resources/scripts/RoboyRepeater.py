#!/usr/bin/env python
import pdb
import sys
import rospy
from roboy_communication_cognition.srv import RecognizeSpeech, Talk

def stt_client():
	rospy.wait_for_service("/roboy/cognition/speech/recognition")
	rospy.wait_for_service("/roboy/cognition/speech/synthesis/talk")
	try:
		while True:	
			stt = rospy.ServiceProxy("/roboy/cognition/speech/recognition", RecognizeSpeech)
			tts = rospy.ServiceProxy("/roboy/cognition/speech/synthesis/talk", Talk)
			resp = stt()
			tts(resp.text)
	except rospy.ServiceException, e:
		print "Service call failed: %s"%e

if __name__ == "__main__":
	stt_client()