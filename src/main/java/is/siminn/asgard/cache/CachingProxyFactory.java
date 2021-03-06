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
        factory.addInterface(CacheManagement.class);
        factory.addAdvice(new org.aopalliance.intercept.MethodInterceptor() {
            CacheStore cache = cacheStoreFactory.getNewInstance();

            public Object invoke(MethodInvocation invocation) throws Throwable {
                final Object[] arguments = invocation.getArguments();
                final Method method = invocation.getMethod();
                if (CacheManagement.CLEARCACHE_METHOD.equals(method.getName())) {
                    log.debug("clearing cache implicitly via " + method.getName());
                    cache.clear();
                    // its a virtual method and with void return type, so its ok
                    return null;
                }                                
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
