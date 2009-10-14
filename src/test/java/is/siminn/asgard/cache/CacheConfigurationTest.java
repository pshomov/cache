/*

Created by Petar Shomov <petar@sprettur.is> and contributors

Copyright (c) 2009 Síminn hf (http://www.siminn.is). All rights
reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Inital version of this file contributed by Síminn hf. (http://www.siminn.is)

*/
package is.siminn.asgard.cache;

import is.siminn.asgard.cache.support.CachableRepo;
import static org.mockito.Mockito.*;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public class CacheConfigurationTest {
    @Test
    public void should_use_the_provided_cacheStoreFactory_when_creating_new_cache_instances(){
        final CacheStoreFactory cacheStoreFactory = mock(CacheStoreFactory.class);
        when(cacheStoreFactory.getNewInstance()).thenReturn(mock(CacheStore.class));

        CachingProxyFactory proxyFactory = new CachingProxyFactory(cacheStoreFactory);
        proxyFactory.wrap(mock(CachableRepo.class), 150);
        verify(cacheStoreFactory).getNewInstance();
    }

    @Test
    public void should_use_the_provided_timeToLive_when_storing_items_in_the_cache_store() throws NoSuchMethodException {
        final CacheStoreFactory cacheStoreFactory = mock(CacheStoreFactory.class);
        final CacheStore store = mock(CacheStore.class);
        when(cacheStoreFactory.getNewInstance()).thenReturn(store);
        when(store.hasCachedResult((Method)anyObject(), (Object[]) anyObject(), isA(MethodResult.class))).thenReturn(false);

        CachingProxyFactory proxyFactory = new CachingProxyFactory(cacheStoreFactory);
        final CachableRepo repo = mock(CachableRepo.class);
        final CachableRepo wrappedRepo = proxyFactory.wrap(repo, 666);
        wrappedRepo.singleObject();
        verify(store).store(CachableRepo.class.getMethod("singleObject"), new Object[]{}, null, 666 );
    }
}
