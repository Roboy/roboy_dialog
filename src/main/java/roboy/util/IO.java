package roboy.util;

import roboy.io.*;
import roboy.ros.RosMainNode;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper class for IO related tasks.
 */
public class IO {
	
	public static String readFile(String file){
		try{
			return new String(Files.readAllBytes(Paths.get(file)));
		} catch(IOException e){
			return "";
		}
	}
	
	public static String readFile(File file){
		try{
			return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		} catch(IOException e){
			return "";
		}
	}
	
	public static List<String> readLines(String file){
		try{
			return Files.readAllLines(Paths.get(file));
		} catch(IOException e){
			return new ArrayList<>();
		}
	}
	
	public static List<String> readLines(File file){
		try{
			return Files.readAllLines(Paths.get(file.getAbsolutePath()));
		} catch(IOException e){
			return new ArrayList<>();
		}
	}

	public static MultiInputDevice getInputs(RosMainNode rosMainNode) throws SocketException{
		MultiInputDevice multiIn;
		switch (ConfigManager.INPUT) {
			case "cmdline":
				multiIn = new MultiInputDevice(new CommandLineInput());
				break;
			case "bing":
				multiIn = new MultiInputDevice(new BingInput(rosMainNode));
				break;
			case "udp":
				multiIn = new MultiInputDevice(new UdpInput(ConfigManager.DATAGRAM_SOCKET));
			default:
				multiIn = new MultiInputDevice(new CommandLineInput());
		}
		return multiIn;
	}

	public static MultiOutputDevice getOutputs(RosMainNode rosMainNode) throws SocketException, UnknownHostException{
		MultiOutputDevice multiOut;
		List<OutputDevice> outputs = new ArrayList<>();
		for (String output: ConfigManager.OUTPUTS) {
			switch(output) {
				case "cerevoice":
					outputs.add(new CerevoiceOutput(rosMainNode));
					break;
				case "cmdline":
					outputs.add(new CommandLineOutput());
					break;
				case "emotions":
					outputs.add(new EmotionOutput(rosMainNode));
					break;
				case "freetts":
					outputs.add(new FreeTTSOutput());
					break;
				case "udp":
					outputs.add(new UdpOutput(ConfigManager.DATAGRAM_SOCKET,
							ConfigManager.UDP_HOST_ADDRESS,
							ConfigManager.UDP_OUT_SOCKET));
					break;
				default:
					outputs.add(new CommandLineOutput());
			}
		}
		multiOut = new MultiOutputDevice(outputs.get(0));
		if (outputs.size()>1) {
			for (int i=1; i<outputs.size(); i++)
			{
				multiOut.add(outputs.get(i));
			}
		}

		return multiOut;

	}

}
