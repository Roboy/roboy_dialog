package roboy.ros;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private HashMap<RosSubscribers, Subscriber> subscriberMap;

    final Logger LOGGER = LogManager.getLogger();

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
            if (Config.NOROS && Config.MEMORY) {
                if (!c.address.contains("memory")) {
                    continue;
                }
            }
            try {
                clientMap.put(c, node.newServiceClient(c.address, c.type));
                LOGGER.info("{} client initialization SUCCESS!", c.toString());
            } catch (Exception e) {
                success = false;
                LOGGER.warn("{} client initialization FAILED!", c.toString());
            }
        }
        for(RosSubscribers s : RosSubscribers.values()) {
            try {
                subscriberMap.put(s, node.newSubscriber(s.address, s.type));
                LOGGER.info("{} subscriber initialization SUCCESS!", s.toString());
            } catch (Exception e) {
                success = false;
                LOGGER.warn("{} subscriber initialization FAILED!", s.toString());
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
        if(clientMap == null) {
            if(Config.SHUTDOWN_ON_SERVICE_FAILURE) {
                throw new RuntimeException("ROS clients have not been initialized! Stopping DM execution.");
            }
            LOGGER.warn("ROS clients have not been initialized! Is the ROS host running?");
            return true;
        }
        return !(clientMap.containsKey(c));
    }

    boolean notInitialized(RosSubscribers s) {
        if(subscriberMap == null) {
            if(Config.SHUTDOWN_ON_SERVICE_FAILURE) {
                throw new RuntimeException("ROS subscribers have not been initialized! Stopping DM execution.");
            }
            LOGGER.warn("ROS subscribers have not been initialized! Is the ROS host running?");
            return true;
        }
        return !(subscriberMap.containsKey(s));
    }

    /**
     *
     * Returns the ServiceClient matching the RosClients entry.
     * the return might need casting before further use.
     */
    ServiceClient getServiceClient(RosClients c) {
        return clientMap.get(c);
    }

    Subscriber getSubscriber(RosSubscribers s) {
        return subscriberMap.get(s);
    }
}