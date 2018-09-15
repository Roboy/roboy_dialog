package roboy.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkUtils {
    private static final int TIMEOUT = 250;
    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean isInternetWorking(String addr){
        return isReachable(addr, TIMEOUT);
    }

    /**
     * Pings Cloudfare, Google and OpenDNS servers
     */
    public static boolean isInternetWorking(){
        boolean cloudFlare = isInternetWorking("1.1.1.1"), google = isInternetWorking("172.217.22.46"), openDNS = isInternetWorking("146.112.62.105");
        if(cloudFlare && google && openDNS){
            LOGGER.debug("Internet working");
            return true;
        }
        else{
            LOGGER.warn(String.format("Cloudflare %s, Google %s, OpenDNS %s", cloudFlare, google, openDNS));
            return false;
        }
    }
    //Shamelessly stolen from stackoverflow
    //Credits: https://stackoverflow.com/questions/9922543/why-does-inetaddress-isreachable-return-false-when-i-can-ping-the-ip-address
    private static boolean isReachable(String addr, int timeOutMillis) {
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(addr, 80), timeOutMillis);
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
