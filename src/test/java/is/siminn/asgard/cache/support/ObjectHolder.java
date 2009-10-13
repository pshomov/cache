package is.siminn.asgard.cache.support;

public class ObjectHolder {
    Integer value;

    public ObjectHolder(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public int hashCode() {
        return value.hashCode();
    }
}
