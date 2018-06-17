package roboy.io;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by roboy on 7/27/17.
 */
public class UdpInput implements InputDevice {

    private DatagramSocket serverSocket;

    public UdpInput(DatagramSocket ds) throws SocketException {
        serverSocket = ds;
    }

    @Override
    public Input listen()
    {
        byte[] in = new byte[1024];

        DatagramPacket receivedPacket = new DatagramPacket(in, in.length);
        try {
            serverSocket.receive(receivedPacket);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        String text = new String(receivedPacket.getData());
        return new Input(text);
    }

}


