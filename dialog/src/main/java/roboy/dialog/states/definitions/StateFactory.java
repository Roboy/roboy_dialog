package roboy.dialog.states.definitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 *   roboy.dialog...ToyGreetingsState"  --->  {Java object of class ToyGreetingsState}
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

    private final static Logger logger = LogManager.getLogger();


    /**
     * Create a Java State object based on the provided class name
     *
     * Full class name must be used: 'my.package.asdf.StateName' instead of 'StateName'.
     * The class must be a sub-class of State.
     *
     * This function doesn't throw Exceptions and will return null if something goes wrong.
     *
     * @param className  class name of the State object to be created
     * @param stateIdentifier state identifier/name (this is NOT the class name!)
     * @param parameters state parameters
     * @return a new instance of a State object of specified class OR null if something goes wrong
     */
    public static State createStateByClassName(String className, String stateIdentifier, StateParameters parameters) {

        if (className == null) {
            logger.warn("null was passed as className! This is not going to work");
            return null;
        }

        // get class by name
        Class<?> cls;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.warn("ClassNotFoundException for " + className + "!\n" +
                    "\tMake sure that you use full class name like " +
                    "'my.package.asdf.StateName' instead of simple name like 'StateName'!");
            return null;
        }

        // check if it is a subclass of State
        if ( ! State.class.isAssignableFrom(cls)) {
            logger.warn("Provided class (" + className + ") is not a subclass of State!");
            return null;
        }

        // get correct constructor (String id, StateParameters params)
        Constructor ctor;
        try {
            ctor = cls.getConstructor(String.class, StateParameters.class);
        } catch (NoSuchMethodException e) {
            logger.warn("NoSuchMethodException for " + className + "! Make sure that the class has a constructor " +
                    "that takes exactly two parameters: ClassName(String id, StateParameters params).");
            logger.warn("Exception message: " + e.getMessage());
            return null;
        }

        // create an object of that class
        Object stateObj;
        try {
            stateObj = ctor.newInstance(stateIdentifier, parameters);
        }catch (InstantiationException  e){
            logger.warn("[InstantiationException]Could not create an instance of " + className);
            return null;
        }
        catch (InvocationTargetException e){
            logger.warn("[InvocationTargetException]Could not create an instance of " + className);
            return null;
        }
        catch (IllegalAccessException e){
            logger.warn("[IllegalAccessException]Could not create an instance of " + className);
            return null;
        }
//        catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
//            logger.warn("Could not create an instance of " + className);
//            logger.warn("Exception message: " + e.getMessage());
//            return null;
//        }

        // additional check if the created instance is really a subclass of State
        // this should be always the case
        if ( ! (stateObj instanceof State)) {
            logger.warn("Created instance of class " + className + " is not a sub class of State! Weird!");
            return null;
        }

        return (State) stateObj;
    }


}
