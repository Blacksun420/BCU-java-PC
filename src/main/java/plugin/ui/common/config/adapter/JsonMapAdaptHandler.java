package plugin.ui.common.config.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import plugin.ui.common.util.ReflectUtils;
import plugin.ui.common.util.UIException;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class JsonMapAdaptHandler {
    private static final Map<Class, TypeAdapter> H = new HashMap<>();

    static {
        registerMapAdapter(Color.class, ColorMapAdapter.getInstance());
        registerMapAdapter(Object.class, DefaultMapAdapter.getInstance());
    }

    public static void registerMapAdapter(Class target, TypeAdapter adapter) {
        H.put(target, adapter);
    }

    public static <T> void encode(Class<T> clazz, Map<String, T> map, JsonWriter out) throws IOException {
        out.name(clazz.getName());

        TypeAdapter handler = H.get(clazz);
        if (handler != null) {
            handler.write(out, map);
        } else {
            H.get(Object.class).write(out, map);
        }
    }

    public static <T> void encode(Class<T> clazz, Map<String, T> map, JsonWriter out, String objName) throws IOException {
        out.name(objName);

        TypeAdapter handler = H.get(clazz);
        if (handler != null) {
            handler.write(out, map);
        } else {
            H.get(Object.class).write(out, map);
        }
    }

    public static <T> Map<String, T> decode(Class<T> clazz, JsonReader in) throws IOException {
        TypeAdapter handler = H.get(clazz);
        if (handler != null) {
            return (Map<String, T>) handler.read(in);
        } else {
            return (Map<String, T>) ((DefaultMapAdapter) H.get(Object.class)).setClass(clazz).read(in);
        }
    }

    private static class DefaultMapAdapter extends TypeAdapter<Map<String, Object>> {
        private final static DefaultMapAdapter ADAPTER = new DefaultMapAdapter();
        private Class<?> target;

        private DefaultMapAdapter() {
        }

        public static DefaultMapAdapter getInstance() {
            return ADAPTER;
        }

        public static DefaultMapAdapter getInstance(Class<?> clazz) {
            ADAPTER.setClass(clazz);
            return ADAPTER;
        }

        @Override
        public void write(JsonWriter out, Map<String, Object> map) throws IOException {
            out.beginObject();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                out.name(entry.getKey());
                out.value(entry.getValue().toString());
            }
            out.endObject();
        }

        @Override
        public Map<String, Object> read(JsonReader in) throws IOException {
            Map<String, Object> map = new HashMap<>();
            in.beginObject();
            while (in.hasNext()) {
                try {
                    map.put(in.nextName(), ReflectUtils.adaptValue(in.nextString(), target));
                } catch (ReflectiveOperationException e) {
                    throw new UIException(e);
                }
            }
            in.endObject();
            return map;
        }

        public DefaultMapAdapter setClass(Class<?> clazz) {
            target = clazz;
            return this;
        }
    }

}
