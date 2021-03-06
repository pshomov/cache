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
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Test
public class CacheExpirationTest {
    private InMemCacheStore cache;
    private Object[] params;

    public void should_return_cached_data_that_is_not_expired() throws NoSuchMethodException {
        Method method = CachableRepo.class.getMethod("singleObject");
        Object result = new Object();
        cache.store(method, params, result, 50000);
        MethodResult resultArray = new MethodResult();
        boolean cachedResultPresent = cache.hasCachedResult(method, params.clone(), resultArray);
        assertTrue(cachedResultPresent);
        assertSame(resultArray.getResult(), result);
    }

    public void should_not_return_cached_data_that_is_expired() throws NoSuchMethodException {
        Method method = CachableRepo.class.getMethod("singleObject");
        Object result = new Object();
        cache.store(method, params, result, -1);
        MethodResult resultArray = new MethodResult();
        boolean cachedResultPresent = cache.hasCachedResult(method, params.clone(), resultArray);
        assertFalse(cachedResultPresent);
        assertNull(resultArray.getResult());
    }

    public void should_replace_existing_cached_info_and_timeToLive_for_method_with_specific_params_when_updaing_the_method_result() throws NoSuchMethodException {
        cache.store(CachableRepo.class.getMethod("listOfStrings", List.class), new Object[]{Arrays.asList("1", "2")}, "first", 2000);
        cache.store(CachableRepo.class.getMethod("listOfStrings", List.class), new Object[]{Arrays.asList("1", "2")}, "second", 100);
        MethodResult result = new MethodResult();
        cache.hasCachedResult(CachableRepo.class.getMethod("listOfStrings", List.class), new Object[]{Arrays.asList("1", "2")}, result);
        assertEquals(result.getResult(), "second");
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        cache = new InMemCacheStore(new TimeProvider());
        params = new Object[]{1, 2};
    }
}
