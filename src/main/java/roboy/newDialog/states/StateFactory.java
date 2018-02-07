package roboy.newDialog.states;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is used to create State objects based on the class name (as a string).
 *
 * Use case:
 *   The personality file defines an implementation for each state.
 *   The implementation is a simple string that contains the class name.
 *
 * Example:
 *   Using some magic, this class would convert a string into a proper Java object:
 *   roboy.newDialog...ToyGreetingsState"  --->  {Java object of class ToyGreetingsState}
 *
 * Important note for the state implementation:
 *   The magic that is used here is called Java Reflection. It adds some small restriction
 *   to the implementation of the states: Every State sub-class must have a constructor
 *   that takes exactly two parameters. The first one is a String, the second is an object of
 *   type StateParameters. For example: ToyGreetingsState(String id, StateParameters params)
 *
 *   You can have other constructors as well.
 *
 */
public class StateFactory {

    private final static Logger logger = LoggerFactory.getLogger(StateFactory.class);


    public static State createStateByClassName(String className, String stateIdentifier, StateParameters parameters) {

        // get class by name
        Class<?> cls;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("ClassNotFoundException for " + className + "!");
            logger.error("Exception message:" + e.getMessage());
            return null;
        }

        // check if it is a subclass of State
        if ( ! State.class.isAssignableFrom(cls)) {
            logger.error("Provided class (" + className + ") is not a subclass of State!");
            return null;
        }

        // get correct constructor (String id, StateParameters params)
        Constructor ctor;
        try {
            ctor = cls.getConstructor(String.class, StateParameters.class);
        } catch (NoSuchMethodException e) {
            logger.error("NoSuchMethodException for " + className + "! Make sure that the class has a constructor " +
                    "that takes exactly two parameters: ClassName(String id, StateParameters params).");
            logger.error("Exception message:" + e.getMessage());
            return null;
        }

        // create an object of that class
        Object stateObj;
        try {
            stateObj = ctor.newInstance(stateIdentifier, parameters);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            logger.error("Could not create an instance of " + className);
            logger.error("Exception message:" + e.getMessage());
            return null;
        }

        // additional check if the created instance is really a subclass of State
        // this should be always the case
        if ( ! (stateObj instanceof State)) {
            logger.error("Created instance of class " + className + " is not a sub class of State! Weird!");
            return null;
        }

        return (State) stateObj;
    }


}
