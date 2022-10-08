package plugin.ui.common.config.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import plugin.ui.common.config.Config;
import plugin.ui.common.util.UIException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigAdapter extends TypeAdapter<Config<Class<?>>> {

    static ConfigAdapter adapter;


    public ConfigAdapter() {
        adapter = this;
    }

    public static ConfigAdapter getInstance() {
        return adapter == null ? new ConfigAdapter() : adapter;
    }

    @Override
    public void write(JsonWriter out, Config<Class<?>> value) throws IOException {
        out.beginObject();
        for (Map.Entry<Class<?>, Map<String, Object>> each : value.data.entrySet()) {
            JsonMapAdaptHandler.encode(each.getKey(), (Map) each.getValue(), out);
        }
        out.endObject();
    }

    @Override
    public Config read(JsonReader in) throws IOException {
        Config map = new Config();
        in.beginObject();
        while (in.hasNext()) {
            Class clazz;
            try {
                clazz = Class.forName(in.nextName());
            } catch (ClassNotFoundException e) {
                throw new UIException(e);
            }

            try {
                Map<String, Object> decode = JsonMapAdaptHandler.decode(clazz, in);
                map.put(clazz, decode);
            } catch (UIException e) {
                throw e;
            } catch (Exception e) {
                throw new UIException(e);
            }
        }
        in.endObject();
        return map;
    }

    public static class ConfigMapAdapter extends TypeAdapter<Map<String, Config>> {

        static ConfigMapAdapter adapter;

        private ConfigMapAdapter() {
            adapter = this;
        }

        public static ConfigMapAdapter getInstance() {
            return adapter == null ? new ConfigMapAdapter() : adapter;
        }

        @Override
        public void write(JsonWriter out, Map<String, Config> value) throws IOException {
            out.beginObject();
            for (Map.Entry<String, Config> entry : value.entrySet()) {
                out.name(entry.getKey());
                ConfigAdapter.getInstance().write(out, entry.getValue());
            }
            out.endObject();
        }

        @Override
        public Map<String, Config> read(JsonReader in) throws IOException {
            HashMap<String, Config> map = new HashMap<>();
            in.beginObject();
            while (in.hasNext()) {
                map.put(in.nextName(), ConfigAdapter.getInstance().read(in));
            }
            in.endObject();
            return map;
        }
    }
}


