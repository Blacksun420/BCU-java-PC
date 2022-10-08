package plugin.ui.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public abstract class JsonUtils {

    public static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public static void toFile(Object src, String path) throws IOException {
        String json = G.toJson(src);
        Writer w = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8);
        w.write(json);
        w.close();
    }

    public static <T> T fromClasspath(String resourcePath, Class<T> classOfT) {
        try {
            return fromFile(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath), classOfT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromClasspath(String resourcePath, Type type) {
        try (Reader r = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath), StandardCharsets.UTF_8)) {
            return G.fromJson(JsonParser.parseReader(r).getAsJsonObject(), type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static <T> T fromFile(InputStream in, Class<T> classOfT) throws IOException {
        Reader r = new InputStreamReader(in, StandardCharsets.UTF_8);
        T t = G.fromJson(JsonParser.parseReader(r).getAsJsonObject(), classOfT);
        r.close();
        return t;
    }

    public static <T> T fromFile(File file, Class<T> classOfT) throws IOException {
        Reader r = new FileReader(file);
        T t = G.fromJson(JsonParser.parseReader(r).getAsJsonObject(), classOfT);
        r.close();
        return t;
    }

    public static <T> T get(String s, JsonObject json, Class<T> clazz) {
        String[] split = s.split("/");
        for (String each : split) {
            json = json.getAsJsonObject(each);
        }

        return G.fromJson(json, clazz);
    }
}
