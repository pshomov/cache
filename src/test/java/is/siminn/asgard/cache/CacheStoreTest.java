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

@Test
public class CacheStoreTest {
    private InMemCacheStore cache;
    private Object[] params;

    public void should_store_method_name_params_and_result() throws NoSuchMethodException {
        Object[] params = new Object[]{1, 2};
        Method method = CachableRepo.class.getMethod("singleObject");
        Object result = new Object();
        cache.store(method, params, result, 2000);
    }

    public void should_not_return_cached_data_for_uncached_methods() throws NoSuchMethodException {
        Method method = CachableRepo.class.getMethod("singleObject");
        MethodResult resultArray = new MethodResult();
        boolean cachedResultPresent = cache.hasCachedResult(method, params.clone(), resultArray);
        assertFalse(cachedResultPresent);
        assertNull(resultArray.getResult());
    }

    public void should_store_results_when_the_parameter_is_null() throws NoSuchMethodException {
        Method method = CachableRepo.class.getMethod("singleObjectWithStringParam", java.lang.String.class);
        Object[] resultArray = new Object[]{"f"};
        Object[] params = new Object[]{null};
        cache.store(method, params, resultArray, 1);
    }

    public void should_return_cached_results_when_the_parameter_is_null() throws NoSuchMethodException {
        Method method = CachableRepo.class.getMethod("singleObjectWithStringParam", java.lang.String.class);
        Object[] storeResultArray = new Object[]{"f"};
        Object[] params = new Object[]{null};
        cache.store(method, params, storeResultArray, 1);
        cache.hasCachedResult(method, params, new MethodResult());
    }

    public void should_not_return_cached_results_when_the_cached_parameter_is_null_and_the_argument_is_not_null() throws NoSuchMethodException {
        Method method = CachableRepo.class.getMethod("singleObjectWithStringParam", java.lang.String.class);
        Object[] storeResultArray = new Object[]{"f"};
        Object[] params1 = new Object[]{null};
        Object[] params2 = new Object[]{"d"};
        cache.store(method, params1, storeResultArray, 1);
        assertFalse(cache.hasCachedResult(method, params2, new MethodResult()));
    }

    public void should_not_return_cached_results_when_the_cached_parameter_is_not_null_and_the_argument_is_null() throws NoSuchMethodException {
        Method method = CachableRepo.class.getMethod("singleObjectWithStringParam", java.lang.String.class);
        Object[] cacheResult = new Object[]{"f"};
        final NonComparableObject comparableObject = new NonComparableObject();
        comparableObject.setOne(new NonComparableObject());
        Object[] cachedParameters = new Object[]{comparableObject};
        Object[] arguments = new Object[]{new NonComparableObject()};
        cache.store(method, cachedParameters, cacheResult, 1);
        assertFalse(cache.hasCachedResult(method, arguments, new MethodResult()));
    }

    public void should_cache_multiple_calls_and_their_results() throws NoSuchMethodException {
        Method method = CachableRepo.class.getMethod("singleObjectWithStringParam", java.lang.String.class);
        Object[] storeResultArray1 = new Object[]{"f"};
        Object[] storeResultArray2 = new Object[]{"fa"};
        Object[] params1 = new Object[]{"a"};
        Object[] params2 = new Object[]{"d"};
        Object[] params3 = new Object[]{"a"};
        Object[] params4 = new Object[]{"d"};
        cache.store(method, params1, storeResultArray1, 10000);
        cache.store(method, params2, storeResultArray2, 10000);
        assertTrue(cache.hasCachedResult(method, params3, new MethodResult()));
        assertTrue(cache.hasCachedResult(method, params4, new MethodResult()));
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        cache = new InMemCacheStore(new TimeProvider());
        params = new Object[]{1, 2};
    }

    private class NonComparableObject {
        private NonComparableObject one;

        public void setOne(NonComparableObject one) {
            this.one = one;
        }
    }
}
