package plugin.ui.common.config.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ColorMapAdapter extends TypeAdapter<Map<String, Color>> {

    static ColorMapAdapter cma;

    public ColorMapAdapter() {
        cma = this;
    }

    public static ColorMapAdapter getInstance() {
        return cma == null ? new ColorMapAdapter() : cma;
    }

    @Override
    public void write(JsonWriter out, Map<String, Color> value) throws IOException {
        out.beginObject();
        for (Map.Entry<String, Color> each : value.entrySet()) {
            out.name(each.getKey());
            out.beginArray();
            out.value(each.getValue().getRed());
            out.value(each.getValue().getGreen());
            out.value(each.getValue().getBlue());
            out.endArray();
        }
        out.endObject();
    }

    @Override
    public Map<String, Color> read(JsonReader in) throws IOException {
        HashMap<String, Color> map = new HashMap<>();
        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName();
            in.beginArray();
            map.put(key, new Color(in.nextInt(), in.nextInt(), in.nextInt()));
            in.endArray();
        }
        in.endObject();
        return map;
    }
}
