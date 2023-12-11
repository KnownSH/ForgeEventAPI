package net.neoforged.bus.api;

public class ForgeEvent {
    public enum Result
    {
        DENY,
        DEFAULT,
        ALLOW
    }

    boolean isCanceled = false;
    private Result result = Result.DEFAULT;


    /**
     * Returns the value set as the result of this event
     */
    public final Result getResult()
    {
        return result;
    }

    /**
     * Sets the result value for this event, not all events can have a result set, and any attempt to
     * set a result for an event that isn't expecting it will result in a IllegalArgumentException.
     *
     * The functionality of setting the result is defined on a per-event basis.
     *
     * @param value The new result
     */
    public void setResult(Result value) {
        result = value;
    }
}
