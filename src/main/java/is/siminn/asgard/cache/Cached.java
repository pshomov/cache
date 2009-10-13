package is.siminn.asgard.cache;

@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME) @java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE}) public @interface Cached {
    boolean enabled() default true;
}
