package net.knsh.neoforged.bus.api;

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
}
