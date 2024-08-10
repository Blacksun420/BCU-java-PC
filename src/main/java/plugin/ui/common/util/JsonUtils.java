package plugin.ui.common.util;

import com.google.gson.*;
import common.CommonStatic;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public abstract class JsonUtils {

    public static final Gson G = new GsonBuilder().setPrettyPrinting().create();

    public static void toFile(Object src, String path) throws IOException {
        String json = G.toJson(src);
        File wf = CommonStatic.ctx.newFile(path);
        Writer w = new OutputStreamWriter(new FileOutputStream(wf), StandardCharsets.UTF_8);
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
        for (String each : split)
            json = json.getAsJsonObject(each);

        return G.fromJson(json, clazz);
    }

    public static <T> T[] getArr(String s, JsonObject json, Class<T> clazz) {
        String[] split = s.split("/");
        JsonArray jr = null;
        for (String each : split)
            jr = json.getAsJsonArray(each);
        if (jr == null)
            return null;
        T[] arr = (T[])Array.newInstance(clazz, jr.size());
        for (int i = 0; i < arr.length; i++)
            arr[i] = G.fromJson(jr.get(i), clazz);
        return arr;
    }
}
