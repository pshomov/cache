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
import is.siminn.asgard.cache.support.ObjectHolder;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Test
public class CacheWhenComplexParamsTest {
    private InMemCacheStore cache;

    public void should_compare_list_of_strings() throws NoSuchMethodException {
        final Method method = CachableRepo.class.getMethod("listOfStrings", List.class);
        List<String> cachedParams = new ArrayList<String>();
        cachedParams.add("fle");
        cache.store(method, new Object[] {cachedParams}, "f", 1000);

        List<String> actualResult = new ArrayList<String>();
        actualResult.add("fle");
        MethodResult cachedResult = new MethodResult();
        assertTrue(cache.hasCachedResult(method, new Object[]{actualResult}, cachedResult));
        String cachedValue = (String) cachedResult.getResult();
        assertEquals(cachedValue, "f");
    }

    public void should_compare_list_of_custom_objects() throws NoSuchMethodException {
        final Method method = CachableRepo.class.getMethod("listOfObjectHolders", List.class);
        List<ObjectHolder> cachedParams = new ArrayList<ObjectHolder>();
        cachedParams.add(new ObjectHolder(1));
        cache.store(method, new Object[] {cachedParams}, "f", 120000);

        List<ObjectHolder> actualResult = new ArrayList<ObjectHolder>();
        actualResult.add(new ObjectHolder(1));
        MethodResult cachedResult = new MethodResult();
        assertTrue(cache.hasCachedResult(method, new Object[]{actualResult}, cachedResult));
        String cachedValue = (String) cachedResult.getResult();
        assertEquals(cachedValue, "f");
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        cache = new InMemCacheStore(new TimeProvider());
    }

}
