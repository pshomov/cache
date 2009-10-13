package is.siminn.asgard.cache;

public class InMemCacheStoreFactory implements CacheStoreFactory{
    private final TimeProvider timeProvider;

    public InMemCacheStoreFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public CacheStore getNewInstance() {
        return new InMemCacheStore(timeProvider);
    }
}
