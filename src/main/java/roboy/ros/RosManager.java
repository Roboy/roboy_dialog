package roboy.ros;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.topic.Subscriber;
import roboy.util.ConfigManager;

import java.util.HashMap;



/**
 * Stores all the Ros Service Clients and manages access to them.
 *
 * If SHUTDOWN_ON_ROS_FAILURE is set, throws a runtime exception
 * if any of the clients failed to initialize.
 */

class RosManager {

    private HashMap<RosServiceClients, ServiceClient> serviceMap;
    private HashMap<RosSubscribers, Subscriber> subscriberMap;
    final Logger LOGGER = LogManager.getLogger();

    /**
     * Initializes all ServiceClients for Ros.
     */
    boolean initialize(ConnectedNode node) {
        serviceMap = new HashMap<>();
        subscriberMap = new HashMap<>();
        boolean success = true;

        for(RosServiceClients client : RosServiceClients.values()) {
            //TODO: Remove these functions once we are sure that we do not need these Service Clients. Specifically: Remove the isAMemoryModule clients from ROS_ACTIVE_PKGS

            //END REMOVAL FROM TODO
            if (ConfigManager.ROS_ACTIVE_PKGS.contains(client.rosPackage)) {
                try {
                    serviceMap.put(client, node.newServiceClient(client.address, client.type));
                    LOGGER.info("{} client initialization SUCCESS!", client.toString());
                } catch (Exception e) {
                    success = false;
                    LOGGER.warn(e.getMessage());
                    LOGGER.warn("{} client initialization FAILED!", client.toString());
                }
            }
        }

        for(RosSubscribers subscriber : RosSubscribers.values()) {
            if (ConfigManager.ROS_ACTIVE_PKGS.contains(subscriber.rosPackage)) {
                try {
                    subscriberMap.put(subscriber, node.newSubscriber(subscriber.address, subscriber.type));
                    LOGGER.info("{} subscriber initialization SUCCESS!", subscriber.toString());
                }
                catch (Exception e) {
                    success = false;
                    LOGGER.warn("{} subscriber initialization FAILED!", subscriber.toString());
                }
            }
        }
        return success;
    }

    //Checks whether the client is one of the memory modules, that no longer needs to be init
    //(basically skip init of these services if we don't need memory active, they fail otherwise)
    private boolean isAMemoryModule(String client){
        return "CREATEMEMORY".equals(client) || "UPDATEMEMORY".equals(client) || "GETMEMORY".equals(client) || "DELETEMEMORY".equals(client) || "CYPHERMEMORY".equals(client) ;
    }

    /**
     * Should always be called before getService, such that if a client failed to initialize,
     * a fallback response can be created instead. Important if SHUTDOWN_ON_ROS_FAILURE is false.
     */
    boolean notInitialized(RosServiceClients c) {
        if(serviceMap == null) {
            LOGGER.error("ROS clients have not been initialized! Is the ROS host running?");
//            return true;
        }
        return !(serviceMap.containsKey(c));
    }

    boolean notInitialized(RosSubscribers s) {
        if(subscriberMap == null) {
            LOGGER.error("ROS subscribers have not been initialized! Is the ROS host running?");
            return true;
        }
        return !(subscriberMap.containsKey(s));
    }

    /**
     *
     * Returns the ServiceClient matching the RosServiceClients entry.
     * the return might need casting before further use.
     */
    ServiceClient getService(RosServiceClients c) {
        return serviceMap.get(c);
    }

    Subscriber getSubscriber(RosSubscribers s) {
        return subscriberMap.get(s);
    }
}