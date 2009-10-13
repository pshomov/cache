package is.siminn.asgard.cache;

import is.siminn.asgard.cache.support.CachableRepo;
import org.testng.annotations.Test;
import static org.testng.Assert.assertFalse;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

public class CacheStoreCommonTest {
    @Test
    public void should_expire_data_after_timeToLive_period() throws NoSuchMethodException {
        TimeProvider timeProvider = mock(TimeProvider.class);
        CacheStoreFactory storeFactory = new InMemCacheStoreFactory(timeProvider);
        final CacheStore cacheStore = storeFactory.getNewInstance();
        final Method method = CachableRepo.class.getMethod("singleObject");
        when(timeProvider.getTimeInMS()).thenReturn(100l);
        cacheStore.store(method, new Object[]{}, null, 10);
        when(timeProvider.getTimeInMS()).thenReturn(111l);
        assertFalse(cacheStore.hasCachedResult(method, new Object[]{}, new MethodResult()));
    }
}
