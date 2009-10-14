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
