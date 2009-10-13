package is.siminn.asgard.cache.support;

import is.siminn.asgard.cache.Cached;
import is.siminn.asgard.cache.InvalidatesCache;

import java.util.List;

public interface CachableRepo {
    String singleObject();
    String singleObjectWithIntParam(int p);
    @Cached(enabled = false)
    public int never_cached();

    @InvalidatesCache
    public int method_invalidates_cache();

    String singleObjectWithStringParam(String s);
    String listOfStrings(List<String> items);
    String listOfObjectHolders(List<ObjectHolder> items);
}
