package is.siminn.asgard.cache;

public class CachedResultStore {
    private long timeToLive;
    private Object cachedResult;

    public CachedResultStore(long timeToLive, Object cachedResult) {
        this.timeToLive = timeToLive;
        this.cachedResult = cachedResult;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void setValue(Object cachedResult) {
        this.cachedResult = cachedResult;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public Object getCachedResult() {
        return cachedResult;
    }
}
