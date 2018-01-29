package roboy.context;

/**
 * External interface for enums that define the updaters in context.
 */
public interface ContextUpdaterInterface extends ContextValueInterface {
    Class getTargetType();
}
