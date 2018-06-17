package roboy.io;

import roboy.dialog.action.Action;
import roboy.dialog.action.SpeechAction;

import java.net.*;
import java.util.List;

/**
 * Created by roboy on 7/27/17.
 */
public class UdpOutput implements OutputDevice {

    private DatagramSocket serverSocket;
    private InetAddress udpEndpointAddress;
    private int updEndpointPort;


    public UdpOutput(DatagramSocket ds, String address, int port) throws SocketException, UnknownHostException {
        serverSocket = ds;
        udpEndpointAddress = InetAddress.getByName(address);
        updEndpointPort = port;
    }

    @Override
    public void act(List<Action> actions) {
        for (Action a : actions) {
            if (a instanceof SpeechAction) {
                String text = ((SpeechAction) a).getText();
                byte[] out = text.toUpperCase().getBytes();

                try{
                    DatagramPacket sendPacket = new DatagramPacket(out, out.length, udpEndpointAddress, updEndpointPort);
                    serverSocket.send(sendPacket);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }
}
