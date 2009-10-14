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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CachingProxyFactoryTest {
    private final CacheStoreFactory storeFactory = mock(CacheStoreFactory.class);
    private CacheStore store;
    private CachableRepo repo;

    @BeforeMethod
    public void run_before_tests() {
        store = mock(CacheStore.class);
        when(storeFactory.getNewInstance()).thenReturn(store);
        CachingProxyFactory proxyFactory = new CachingProxyFactory(storeFactory);
        repo = proxyFactory.wrap(mock(CachableRepo.class), 100);
    }

    @Test
    public void should_clear_cache_store_before_calling_method_with_annotation_flushCache() {
        repo.method_invalidates_cache();
        verify(store).clear();
    }

    @Test
    public void should_clear_cache_store_when_calling_explicit_clearCache_method() {
        ((CacheManagement) repo).clearCache();
        verify(store).clear();
    }
}

