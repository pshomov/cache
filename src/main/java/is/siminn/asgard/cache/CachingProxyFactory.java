package is.siminn.asgard.cache;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

public class CachingProxyFactory {
    private static Log log = LogFactory.getLog(CachingProxyFactory.class);
    private final CacheStoreFactory cacheStoreFactory;

    public CachingProxyFactory(CacheStoreFactory cacheStoreFactory) {
        this.cacheStoreFactory = cacheStoreFactory;
    }

    public <T> T wrap(T repo, final int timeToLive) {
        ProxyFactory factory = new ProxyFactory(repo);
        factory.addAdvice(new org.aopalliance.intercept.MethodInterceptor() {
            CacheStore cache = cacheStoreFactory.getNewInstance();

            public Object invoke(MethodInvocation invocation) throws Throwable {
                final Object[] arguments = invocation.getArguments();
                final Method method = invocation.getMethod();
                if (flushesCache(method)) {
                    log.debug("clear cache for method "+method.getName());
                    cache.clear();
                }
                if (cacheEnabled(method)) {
                    MethodResult cachedResult = new MethodResult();
                    if (cache.hasCachedResult(method, arguments, cachedResult)) {
                        log.debug("cache hit for " + method.getName());
                        return cachedResult.getResult();
                    } else {
                        log.debug("cache miss for " + method.getName());
                        final Object result = invocation.proceed();
                        cache.store(method, arguments, result, timeToLive);
                        return result;
                    }
                }
                return invocation.proceed();
            }
        });
        return (T) factory.getProxy();
    }

    private boolean flushesCache(Method method) {
        final InvalidatesCache annotations = AnnotationUtils.findAnnotation(method, InvalidatesCache.class);
        return annotations != null;
    }

    private boolean cacheEnabled(Method method) {
        final Cached annotations = AnnotationUtils.findAnnotation(method, Cached.class);
        return annotations == null || annotations.enabled();
    }
}
