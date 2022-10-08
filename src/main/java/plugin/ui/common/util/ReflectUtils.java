package plugin.ui.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings({"unchecked"})
public abstract class ReflectUtils {

    private static final Map<Object, Method> METHOD_CACHE_MAP = new HashMap<>();

    public static <T, K> void invokeField(Object o, Map<String, K> valueMap, BiConsumer<T, K> doSth) throws IllegalAccessException {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field each : fields) {
            each.setAccessible(true);
            String key = each.getName();
            K value = valueMap.get(key);
            if (value != null) {
                doSth.accept((T) each.get(o), value);
            }
        }
    }

    public static void invokeVoid(Object o, String methodName) {
        Method method;

        try {
            method = METHOD_CACHE_MAP.get(o);
            if (method == null) {
                method = o.getClass().getDeclaredMethod(methodName);
                method.setAccessible(true);
                METHOD_CACHE_MAP.put(methodName, method);
            }
            method.invoke(o);

        } catch (Exception e) {
            throw new UIException(e);
        }

    }

    public static <T> T adaptValue(Object value, Class<T> clazz) throws ReflectiveOperationException {
        if (clazz == String.class) {
            return (T) value.toString();
        } else if (clazz.getName().indexOf("java.lang") == 0) {
            Method method = clazz.getMethod("parse" + fixParse(clazz.getSimpleName()), String.class);
            return (T) method.invoke(null, value.toString());
        }

        throw new UIException("unsupported clazz:" + clazz.getName());
    }

    private static String fixParse(String clazzName) {
        if ("Integer".equals(clazzName)) {
            return "Int";
        }
        return clazzName;
    }

    public static <T> T getInstance(Class<T> clazz) throws Exception {
        try {
            return (T) clazz.getDeclaredMethod("getInstance").invoke(null);
        } catch (Exception ignored) {
        }

        return clazz.newInstance();
    }
}
