package plugin.ui.common.config;

import com.google.gson.annotations.JsonAdapter;
import plugin.ui.common.config.adapter.ConfigAdapter;
import plugin.ui.common.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@SuppressWarnings({"unchecked", "rawtypes"})
@JsonAdapter(ConfigAdapter.class)
public class Config<K> {

    public Map<K, Map<String, Object>> data;

    public Config() {
        data = new HashMap<>();
    }

    public <T> void put(K k, String key, T value) {
        if (k.getClass() == Config.class) {
            combine((Config) k);
            return;
        }

        data.computeIfAbsent(k, key1 -> new HashMap<>());
        data.get(k).put(key, value);
    }

    public void combine(Config config) {
        if (config == this) {
            return;
        }

        if (keyClass() != config.keyClass()) {
            throw new UnsupportedOperationException("two config instances has different generic: " + keyClass() + " != " + config.keyClass());
        }

        try {
            for (Object key : config.keys()) {
                Map<String, Object> map = config.getAll(key);
                map.forEach((k, v) -> put((K) key, k, v));
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("incompatible two config instances");
        }

    }

    public <T> T get(K ret, String key) {
        if (!data.containsKey(ret)) {
            data.put(ret, new HashMap<>());
        }
        return (T) data.get(ret).get(key);
    }

    public <T> void put(K clazz, Map<String, T> decode) {
        data.put(clazz, (Map<String, Object>) decode);
    }

    public <T> T delete(K k, String key) {
        return (T) data.get(k).remove(key);
    }

    public <T> Map<String, T> delete(K clazz) {
        return (Map<String, T>) data.remove(clazz);
    }

    public <T> Map<String, T> getAll(K clazz) {
        return (Map<String, T>) data.get(clazz);
    }

    public <T> T get(K k, String key, T defaultValue) {
        T t = get(k, key);
        return t == null ? defaultValue : t;
    }

    public Set<K> keys() {
        return data.keySet();
    }

    public Class keyClass() {
        for (K key : keys()) {
            return key.getClass();
        }

        throw new UnsupportedOperationException("empty config");
    }

    public static Config<Class<?>> getInstance() {
        return new Config<>();
    }


    @Override
    public String toString() {
        return JsonUtils.G.toJson(this);
    }
}
