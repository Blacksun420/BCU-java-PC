package plugin.ui.main;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import plugin.ui.common.config.Config;
import plugin.ui.common.config.adapter.JsonMapAdaptHandler;
import plugin.ui.main.Theme.ThemeType;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

@JsonAdapter(UITheme.UIThemeAdapter.class)
public class UITheme {

    private Config<ThemeType> data = new Config<>();

    public UITheme() {
    }

    public void put(Theme theme, String clazz) {
        data.put(theme.type, theme.name, clazz);
    }

    public String get(Theme theme) {
        return get(theme.type, theme.name);
    }

    private String get(ThemeType type, String name) {
        return data.get(type, name);
    }

    public Map<String, String> getGroup(ThemeType type) {
        return data.getAll(type);
    }

    public Config<ThemeType> getAllThemes() {
        return data;
    }

    static class UIThemeAdapter extends TypeAdapter<UITheme> {
        @Override
        public void write(JsonWriter out, UITheme value) throws IOException {
            out.beginObject();
            for (Map.Entry<ThemeType, Map<String, Object>> entry : value.data.data.entrySet()) {
                ThemeType k = entry.getKey();
                Map<String, Object> v = entry.getValue();
                JsonMapAdaptHandler.encode(Object.class, v, out, k.name());
            }
            out.endObject();
        }

        @Override
        public UITheme read(JsonReader in) throws IOException {
            UITheme t = new UITheme();
            in.beginObject();

            while (in.hasNext()) {
                String type = in.nextName();
                Map<String, String> themeMap = new TreeMap<>(JsonMapAdaptHandler.decode(String.class, in));
                t.data.put(ThemeType.getInstance(type), themeMap);
            }

            in.endObject();
            return t;
        }
    }
}

