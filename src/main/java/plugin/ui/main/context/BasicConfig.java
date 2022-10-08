package plugin.ui.main.context;

import com.google.gson.annotations.JsonAdapter;
import plugin.ui.common.config.Config;
import plugin.ui.common.config.adapter.ConfigAdapter;

@SuppressWarnings("unchecked")
public class BasicConfig {

    @JsonAdapter(ConfigAdapter.class)

    public Config<Class<?>> config;

    public BasicConfig() {
    }

    public <T> T get(Class<T> ret, String key) {
        return config.get(ret, key);
    }

    public String getString(String key) {
        return get(String.class, key);
    }

    public Integer getInteger(String key) {
        return get(Integer.class, key);
    }

    public Boolean getBoolean(String key) {
        return get(Boolean.class, key);
    }

    public Double getDouble(String key) {
        return get(Double.class, key);
    }

    public Config<Class<?>> data() {
        return config;
    }

    public <T> void set(String key, T value) {
        set((Class<? super T>) value.getClass(), key, value);
    }

    public <T> void set(Class<T> clazz, String key, T value) {
        config.put(clazz, key, value);
    }

    public void clear() {
        // do nothing
    }


}
