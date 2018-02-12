package roboy.context;

/**
 * External interface for enums that define the updaters in context.
 */
public interface ContextUpdaterInterface {
    Class getClassType();
    Class getTargetType();
    Class getTargetValueType();
}
