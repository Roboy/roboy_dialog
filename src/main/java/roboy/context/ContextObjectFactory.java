package roboy.context;

import com.google.common.collect.ImmutableClassToInstanceMap;
import roboy.ros.RosMainNode;

import java.lang.reflect.InvocationTargetException;
import java.util.Observer;

public class ContextObjectFactory {

    /**
     * Used to initialize the Values and ValueHistories, returning a ClassToInstance map.
     * For each element in a ContextValueInterface enum, generates an instance of its classType.
     * Then returns the generated instances in a ClassToInstance map.
     */
    protected static <T extends ContextValueInterface> ImmutableClassToInstanceMap buildValueInstanceMap(T[] enumValueList) {
        ImmutableClassToInstanceMap.Builder valueMapBuilder = new ImmutableClassToInstanceMap.Builder<>();
        for(T v : enumValueList) {
            try {
                // Get the class which defines the Value/ValueHistory.
                Class c = v.getClassType();
                // Create an instance and add {(class) -> (instance)} to the map.
                valueMapBuilder = valueMapBuilder.put(c, c.getConstructor().newInstance());
            } catch (IllegalAccessException | NoSuchMethodException | InstantiationException |InvocationTargetException e) {
                // Just don't mess around when defining the classes and enums.
                e.printStackTrace();
            }
        }
        return valueMapBuilder.build();
    }

    /**
     * Used to initialize Updaters (external and internal), returning a ClassToInstance map.
     * For each element in a ContextUpdaterInterface enum:
     *  1. Seeks out the instance of its targetType (a Value or ValueHistory class).
     *  2. Generates an instance of the updater's classType, with a reference to the target.
     * Finally, returns the generated Updater instances in a ClassToInstance map.
     */
    protected static <T extends ContextUpdaterInterface> ImmutableClassToInstanceMap buildUpdaterInstanceMap(
        T[] enumValueList,
        ImmutableClassToInstanceMap<AbstractValue> values,
        ImmutableClassToInstanceMap<AbstractValueHistory> valueHistories,
        RosMainNode node) {
        ImmutableClassToInstanceMap.Builder updaterMapBuilder = new ImmutableClassToInstanceMap.Builder<>();
        // Go over all Updaters defined in the enum.
        for(T updater : enumValueList) {
            Class targetClass = updater.getTargetType();
            // Check the Value list in the Context for a target.
            AbstractValue targetInstance = values.get(targetClass);
            // If not there, check ValueHistories.
            if (targetInstance == null) {
                targetInstance = valueHistories.get(targetClass);
            }
            // Not found? Updater must have been defined wrongly.
            if (targetInstance == null) {
                throw new IllegalArgumentException("The target class "+ targetClass.getName() +" was not initialized!");
            }
            try {
                // Get the Updater class.
                Class updaterType = updater.getClassType();
                // Create an instance of the Updater class, with the target as its constructor parameter.
                // If it is a ROS-based Updater, add RosMainNode as second parameter.
                if(ROSTopicUpdater.class.isAssignableFrom(updaterType)) {
                    updaterMapBuilder.put(updaterType,
                            updaterType.getConstructor(targetClass, RosMainNode.class).newInstance(targetInstance, node));
                } else {
                    // Otherwise use the common constructor type.
                    updaterMapBuilder.put(updaterType, updaterType.getConstructor(targetClass).newInstance(targetInstance));
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                // Don't mess around defining Updaters (constructor access must be public, for example).
                // There are only two allowed constructors, (target, ROSMainNode) or (target).
                e.printStackTrace();
            }
        }
        return updaterMapBuilder.build();
    }

    protected static <T extends ContextObserverInterface> ImmutableClassToInstanceMap buildObserverInstanceMap(
            T[] enumValueList,
            ImmutableClassToInstanceMap<AbstractValue> values,
            ImmutableClassToInstanceMap<AbstractValueHistory> valueHistories) {
        ImmutableClassToInstanceMap.Builder observerMapBuilder = new ImmutableClassToInstanceMap.Builder<>();
        // Go over all Observers defined in the enum.
        for (T updater : enumValueList) {
            try {
                // Get the Observer class.
                Class updaterType = updater.getClassType();
                // Create an instance of the Observer class.
                Observer observer = (Observer) updaterType.getConstructor().newInstance();

                Class targetClass = updater.getTargetType();
                // Check the Value list in the Context for a target.
                AbstractValue targetInstance = values.get(targetClass);
                // If not there, check ValueHistories.
                if (targetInstance != null) {
                    ((ObservableValue) targetInstance).addObserver(observer);
                } else {
                    targetInstance = valueHistories.get(targetClass);
                    if (targetInstance != null) {
                        ((ObservableValueHistory) targetInstance).addObserver(observer);
                    } else {
                        // Not found? Observer must have been defined wrongly.
                        throw new IllegalArgumentException("The target class " + targetClass.getName() + " was not initialized!");
                    }
                }
                observerMapBuilder.put(updaterType, observer);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return observerMapBuilder.build();
    }

}
