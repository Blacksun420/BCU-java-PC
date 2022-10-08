package plugin.ui.common.config;

import com.google.common.reflect.TypeToken;
import plugin.ui.common.util.JsonUtils;

public class PageConfig{
    public Config<Class<?>> config;
    private static final PageConfig instance = new PageConfig();

    private PageConfig(){
        config = JsonUtils.fromClasspath("plugin/ui/page.json", new TypeToken<Config<Class<?>>>(){}.getType());
    }

    public static PageConfig getInstance() {
        return instance;
    }

    public static String getString(String key){
        return getString(key, key);
    }

    public static String getString(String key, String defaultValue){
        return instance.config.get(String.class, key, defaultValue);
    }
}
