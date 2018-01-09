package roboy.context;

/**
 * Interface for an enum describing all attributes belonging to the Context.
 * Defining the class and the return types enable retrieving values over generic methods with AttributeManager.
 */
public interface AttributeInterface {

    Class getClassType();

    Class getReturnType();

}
