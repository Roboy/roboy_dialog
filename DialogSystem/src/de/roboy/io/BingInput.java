package de.roboy.io;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

public class BingInput implements InputDevice 
{
	
	private Service BingSTT;
	
	public BingInput() throws InterruptedException
	{
		Ros ros = new Ros("localhost");
	    ros.connect();
	    BingSTT = new Service(ros, "bing_stt", "TextSpoken");
	}

	
	@Override
	public String listen() throws InterruptedException 
	{
		
		ServiceRequest request = new ServiceRequest("");
	    ServiceResponse response = BingSTT.callServiceAndWait(request);
	    String input = response.toString();
	    
	    System.out.println(input);							
		return input;
	}
	
}
