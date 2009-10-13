package is.siminn.asgard.cache;

import java.lang.reflect.Method;

public interface CacheStore {
    void store(Method method, Object[] arguments, Object result, int timeToLive);
    boolean hasCachedResult(Method method, Object[] arguments, MethodResult resultArray);
    void clear();
}
