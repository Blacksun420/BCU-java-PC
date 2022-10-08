package plugin.ui.main;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import plugin.ui.common.config.StaticConfig;
import plugin.ui.common.util.UIException;
import plugin.ui.main.handler.ThemeHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Theme {

    public String name;

    public ThemeType type;

    public Theme(String toString) {
        String[] split = toString.split(StaticConfig.SEPARATOR_THEME_NAME_TYPE);
        name = split[0];
        type = ThemeType.getInstance(split[1].trim());
    }

    public Theme(ThemeType type, String themeName) {
        this.name = themeName;
        this.type = type;
    }

    public static Theme getInstance(String toString) {
        return new Theme(toString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Theme theme = (Theme) o;
        return name.equals(theme.name) && type == theme.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return name + StaticConfig.SEPARATOR_THEME_NAME_TYPE + type.name();
    }

    public String getText() {
        return ThemeHandler.getTranslation(name) + StaticConfig.SEPARATOR_THEME_NAME_TYPE + type.name();
    }

    public boolean isDemanding() {
        return type.isFlat();
    }

    public void setTheme() {
        UIPlugin.getInstance().changeTheme(this, true);
    }

    public enum ThemeType {
        Light, Dark, Nimbus, Swing;

        public static ThemeType getInstance(String typeName) {
            final ThemeType[] type = new ThemeType[1];
            forEach((t) -> {
                if (t.sameTo(typeName)) {
                    type[0] = t;
                }
            });

            if (type[0] == null) {
                throw new UIException("unsupported ThemeType instance, typeName=" + typeName);
            } else {
                return type[0];
            }
        }

        public static void forEach(Consumer<ThemeType> consumer) {
            consumer.accept(Light);
            consumer.accept(Dark);
            consumer.accept(Nimbus);
            consumer.accept(Swing);
        }

        public boolean sameTo(Object o) {
            return this.name().equalsIgnoreCase(String.valueOf(o));
        }

        public boolean isFlat() {
            return this == Light || this == Dark;
        }
    }


    public static class ThemeMapAdapter extends TypeAdapter<Map<String, Theme>> {
        @Override
        public void write(JsonWriter out, Map<String, Theme> value) throws IOException {
            out.beginObject();
            for (Map.Entry<String, Theme> each : value.entrySet()) {
                out.name(each.getKey());
                out.value(each.getValue().toString());
            }
            out.endObject();
        }

        @Override
        public Map<String, Theme> read(JsonReader in) throws IOException {
            HashMap<String, Theme> map = new HashMap<>();
            in.beginObject();
            while (in.hasNext()) {
                String key = in.nextName();
                map.put(key, new Theme(in.nextString()));
            }
            in.endObject();
            return map;
        }

        private final static ThemeMapAdapter ADAPTER = new ThemeMapAdapter();
        public static ThemeMapAdapter getInstance(){
            return ADAPTER;
        }
    }
}
