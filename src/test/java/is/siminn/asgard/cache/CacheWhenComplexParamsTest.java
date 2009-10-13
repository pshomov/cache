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
