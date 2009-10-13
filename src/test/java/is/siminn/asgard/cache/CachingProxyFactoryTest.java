package is.siminn.asgard.cache;

import is.siminn.asgard.cache.support.CachableRepo;
import static org.mockito.Mockito.*;
import org.testng.annotations.Test;

public class CachingProxyFactoryTest {

    @Test
    public void should_clear_cache_store_before_calling_method_with_annotation_flushCache(){
        final CacheStoreFactory storeFactory = mock(CacheStoreFactory.class);
        final CacheStore store = mock(CacheStore.class);
        when(storeFactory.getNewInstance()).thenReturn(store);
        CachingProxyFactory proxyFactory = new CachingProxyFactory(storeFactory);
        final CachableRepo repo = proxyFactory.wrap(mock(CachableRepo.class), 100);
        repo.method_invalidates_cache();
        verify(store).clear();
    }
}
