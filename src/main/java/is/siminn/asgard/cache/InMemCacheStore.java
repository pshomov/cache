package is.siminn.asgard.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemCacheStore implements CacheStore {
    Map<Method, Map<Object[], CachedResultStore>> cache = new HashMap<Method, Map<Object[], CachedResultStore>>();
    TimeProvider timeProvider;

    public InMemCacheStore(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public synchronized void store(Method method, Object[] arguments, Object result, int timeToLive) {
        Map<Object[], CachedResultStore> argumentsResultMap = cache.get(method);

        if (argumentsResultMap == null) {
            argumentsResultMap = new HashMap<Object[], CachedResultStore>();
            cache.put(method, argumentsResultMap);
        }
        final Object[] argsCachedIndex = cachedArgumentKeyForArguments(method, arguments);
        final long now = timeProvider.getTimeInMS();
        if (argsCachedIndex != null) {
            final CachedResultStore cachedResultStore = argumentsResultMap.get(argsCachedIndex);
            cachedResultStore.setTimeToLive(now + timeToLive);
            cachedResultStore.setValue(result);
        } else {
            argumentsResultMap.put(arguments, new CachedResultStore(now + timeToLive, result));
        }
    }

    public synchronized boolean hasCachedResult(Method method, Object[] arguments, MethodResult resultArray) {
        Map<Object[], CachedResultStore> argumentsResultMap = cache.get(method);

        Object[] argumentsKey = cachedArgumentKeyForArguments(method, arguments);
        if (argumentsKey != null && argumentsResultMap.get(argumentsKey).getTimeToLive() >= timeProvider.getTimeInMS()) {
            resultArray.setResult(argumentsResultMap.get(argumentsKey).getCachedResult());
            return true;
        }
        return false;
    }

    public synchronized void clear() {
        cache.clear();
    }

    /**
     * finds an existing entry in the cache for the arguments passed and returns the key for that entry, or null if nothing was found
     */
    private Object[] cachedArgumentKeyForArguments(Method method, Object[] arguments) {
        Map<Object[], CachedResultStore> argumentsResultMap = cache.get(method);
        if (argumentsResultMap == null) return null;

        boolean entryMatches;
        Object[] result = null;
        for (Object[] cachedArguments : argumentsResultMap.keySet()) {
            entryMatches = true;
            for (int i = 0; i < cachedArguments.length; i++) {
                final Object cachedPositionalArgument = cachedArguments[i];
                final Object actualPositionalArgument = arguments[i];
                if ((!argumentsAreEqual(cachedPositionalArgument, actualPositionalArgument))) {
                    log.trace("parameter mismatch at position " + i + " for " + method.getName() + ", cached value was: " + cachedPositionalArgument + "; actual value was: " + actualPositionalArgument);
                    entryMatches = false;
                    break;
                }
            }
            if (entryMatches) {
                result = cachedArguments;
                break;
            }
        }
        return result;
    }

    private boolean argumentsAreEqual(Object cachedArg, Object actualArgument) {

        if (oneObjectIsNullAndTheOtherNot(cachedArg, actualArgument)) {
            return false;
        }

        boolean areEqual;
        if (cachedArg == null && actualArgument == null) return true;

        if (cachedArg != null && cachedArg instanceof Comparable) {
            return (cachedArg != null) && ((Comparable) cachedArg).compareTo(actualArgument) == 0;
        }

        areEqual = cachedArg != null && cachedArg.equals(actualArgument);

        if (!areEqual) areEqual = reflectCompare(cachedArg, actualArgument);
        return areEqual;
    }

    private boolean doesObjectImplementAnInterface(Class projectionResultClass, Class<?> intf) {
        boolean projectionResultClassIsCollection = false;
        for (Class interfaze : projectionResultClass.getInterfaces()) {
            if (interfaze == intf) {
                projectionResultClassIsCollection = true;
                break;
            }
        }
        return projectionResultClassIsCollection;
    }

    private boolean reflectCompare(Object cachedArg, Object actualArgument) {
        boolean result;

        if (doesObjectImplementAnInterface(cachedArg.getClass(), List.class) && doesObjectImplementAnInterface(actualArgument.getClass(), List.class)) {
            result = compareLists(cachedArg, actualArgument);
        } else {
            result = compareObjects(cachedArg, actualArgument);
        }

        return result;
    }

    private boolean compareObjects(Object cachedArg, Object actualArgument) {
        final Field[] fields = cachedArg.getClass().getDeclaredFields();
        for (Field field : fields) {
            final Object cachedArgFieldValue = getFieldValue(cachedArg, field);
            final Object actualArgFieldValue = getFieldValue(actualArgument, field);
            if (!argumentsAreEqual(cachedArgFieldValue, actualArgFieldValue)) return false;
        }
        return true;
    }

    private Object getFieldValue(Object instance, Field field) {
        field.setAccessible(true);
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean compareLists(Object cachedArg, Object actualArgument) {
        List cachedArgAsList = (List) cachedArg;
        List actualArgsAsList = (List) actualArgument;
        if (cachedArgAsList.size() == actualArgsAsList.size()) {
            for (int i = 0; i < cachedArgAsList.size(); i++) {
                if (!argumentsAreEqual(cachedArgAsList.get(i), actualArgsAsList.get(i))) return false;
            }
            return true;
        }
        return false;
    }

    private boolean oneObjectIsNullAndTheOtherNot(Object cachedArg, Object actualArgument) {
        return (cachedArg == null && actualArgument != null) || (cachedArg != null && actualArgument == null);
    }

    private Log log = LogFactory.getLog(InMemCacheStore.class);
}
