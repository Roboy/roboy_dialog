package roboy.util;

import roboy.io.*;
import roboy.ros.RosMainNode;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper class for IO related tasks.
 */
public class IO {

	private final static Logger logger = LogManager.getLogger();

	public static List<String> readLinesFromUtf8File(String path) {
	    try {
            Path p = Paths.get(path);
            return Files.readAllLines(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
	        logger.error("Error reading file " + path + ": " + e.getMessage());
        }
        return null;
	}

	public static MultiInputDevice getInputs(RosMainNode rosMainNode) throws SocketException{
		return getInputs(rosMainNode, null);
	}

	public static MultiInputDevice getInputs(RosMainNode rosMainNode, String uuid) throws SocketException{
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
				break;
			case "telegram":
				multiIn = new MultiInputDevice(new TelegramInput(uuid));
				break;
			default:
				multiIn = new MultiInputDevice(new CommandLineInput());
		}
		return multiIn;
	}

	public static MultiOutputDevice getOutputs(RosMainNode rosMainNode) throws SocketException, UnknownHostException{
		return getOutputs(rosMainNode, null);
	}

	public static MultiOutputDevice getOutputs(RosMainNode rosMainNode, String uuid) throws SocketException, UnknownHostException{
		MultiOutputDevice multiOut;
		List<OutputDevice> outputs = new ArrayList<>();
		for (String output: ConfigManager.OUTPUTS) {
			switch(output) {
				case "ibm":
					outputs.add(new IBMWatsonOutput());
					break;
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
				case "telegram":
					outputs.add(new TelegramOutput(uuid));
					break;
				default:
					outputs.add(new CommandLineOutput());
			}
		}

		if (outputs.size() < 1) {
			logger.warn("Seems like no outputs were defined in ConfigManager, using CommandLineOutput");
			return new MultiOutputDevice(new CommandLineOutput());
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
