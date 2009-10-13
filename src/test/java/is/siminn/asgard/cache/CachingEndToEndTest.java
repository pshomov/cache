package is.siminn.asgard.cache;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import is.siminn.asgard.cache.support.CachableRepo;

@Test
public class CachingEndToEndTest {
    private CachableRepo repo;
    private CachableRepo cached;

    public void should_propagate_to_repo_the_first_time() {
        when(repo.singleObject()).thenReturn("uff");
        String result = cached.singleObject();
        assertEquals(result, "uff");
    }

    public void should_cache_results() {
        cached.singleObject();
        cached.singleObject();
        verify(repo, times(1)).singleObject();
    }

    public void should_not_return_cache_results_when_using_different_primitive_param() {
        cached.singleObjectWithIntParam(1);
        cached.singleObjectWithIntParam(5);
        verify(repo, times(2)).singleObjectWithIntParam(anyInt());
    }

    public void should_cache_results_when_using_same_primitive_param() {
        cached.singleObjectWithIntParam(3);
        cached.singleObjectWithIntParam(3);
        verify(repo, times(1)).singleObjectWithIntParam(3);
    }

    public void should_return_cache_results_when_using_different_objects_but_same_value_as_param() {
        cached.singleObjectWithStringParam("a");
        cached.singleObjectWithStringParam("a");
        verify(repo, times(1)).singleObjectWithStringParam(anyString());
    }

    public void should_return_same_results_when_cache_kicks_in() {
        when(repo.singleObjectWithIntParam(3)).thenReturn("3");
        final String result1 = cached.singleObjectWithIntParam(3);
        final String result2 = cached.singleObjectWithIntParam(3);
        assertEquals(result1, "3");
        assertEquals(result2, "3");
    }

    public void should_return_cached_result_when_the_cache_match_is_not_the_first_hit_on_the_cached_args() {
        when(repo.singleObjectWithStringParam("3")).thenReturn("3");
        when(repo.singleObjectWithStringParam("4")).thenReturn("4");
        cached.singleObjectWithStringParam("3");
        cached.singleObjectWithStringParam("4");
        cached.singleObjectWithStringParam("4");
        verify(repo, times(1)).singleObjectWithStringParam("4");
    }

    public void should_not_cache_on_interfaces_that_are_not_marked_for_caching() {
        when(repo.never_cached()).thenReturn(3);
        final int result1 = cached.never_cached();
        final int result2 = cached.never_cached();
        assertEquals(result1, 3);
        assertEquals(result2, 3);
        verify(repo, times(2)).never_cached();
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        repo = mock(CachableRepo.class);
        cached = new CachingProxyFactory(new InMemCacheStoreFactory(new TimeProvider())).wrap(repo, 150);
    }
}
