package roboy.ros;

import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.topic.Subscriber;
import roboy.dialog.Config;

import java.util.HashMap;

/**
 * Stores all the Ros Service Clients and manages access to them.
 *
 * If SHUTDOWN_ON_ROS_FAILURE is set, throws a runtime exception
 * if any of the clients failed to initialize.
 */

class RosManager {
    private HashMap<RosClients, ServiceClient> clientMap;
    private HashMap<RosClients, Subscriber> subscriberMap;

    /**
     * Initializes all ServiceClients for Ros.
     */
    boolean initialize(ConnectedNode node) {
        clientMap = new HashMap<>();
        subscriberMap = new HashMap<>();
        boolean success = true;
        // Iterate through the RosClients enum, mapping a client for each.
        for(RosClients c : RosClients.values()) {
            // Do not initialize non-memory services if NOROS, but Memory!
            if(Config.NOROS && Config.MEMORY) {
                if (!c.address.contains("memory")) {
                    continue;
                }
            }
            try {
                if(c.isService) {
                    clientMap.put(c, node.newServiceClient(c.address, c.type));
                } else {
                    subscriberMap.put(c, node.newSubscriber(c.address, c.type));
                }
                System.out.println(c.toString() + " initialization SUCCESS!");
                } catch (Exception e) {
                    success = false;
                    System.out.println(c.toString() + " initialization FAILED, could not reach ROS service!");
                }
        }
        if(Config.SHUTDOWN_ON_SERVICE_FAILURE && !success) {
            throw new RuntimeException("DialogSystem shutdown caused by ROS service initialization failure.");
        }
        return success;
    }

    /**
     * Should always be called before getServiceClient, such that if a client failed to initialize,
     * a fallback response can be created instead. Important if SHUTDOWN_ON_ROS_FAILURE is false.
     */
    boolean notInitialized(RosClients c) {
        if(clientMap == null && subscriberMap == null) {
            if(Config.SHUTDOWN_ON_SERVICE_FAILURE) {
                throw new RuntimeException("ROS clients have not been initialized! Stopping DM execution.");
            }
            System.out.println("ROS clients have not been initialized! Is the ROS host running?");
            return true;
        }
        return !(clientMap.containsKey(c) || subscriberMap.containsKey(c));
    }

    /**
     *
     * Returns the ServiceClient matching the RosClients entry.
     * the return might need casting before further use.
     */
    ServiceClient getServiceClient(RosClients c) {
        return clientMap.get(c);
    }

    Subscriber getSubscriber(RosClients c) {
        return subscriberMap.get(c);
    }
}