package plugin.ui.main.handler;

import com.formdev.flatlaf.FlatLaf;
import plugin.ui.common.config.StaticConfig;
import plugin.ui.main.Theme;
import plugin.ui.main.Theme.ThemeType;
import plugin.ui.main.UITheme;
import plugin.ui.main.context.ThemeConfig;
import plugin.ui.main.context.UIContext;

import java.util.Map;
import java.util.function.Consumer;


public abstract class ThemeHandler {

    private static UITheme data;
    private static ThemeConfig config;

    protected static void removeFavorite(Theme theme) {
        boolean remove = config.favorites.remove(theme);
        if (!remove) {
            theme.name = getTranslation(theme.name);
            config.favorites.remove(theme);
        }
    }

    public static String getTranslation(String raw) {
        return config.translations.getProperty(raw, raw);
    }

    protected static Theme getCur() {
        return config.cur;
    }

    /**
     * below are methods about <h3>user config</h3>
     */
    protected static void setCur(Theme theme) {
        config.cur = theme;
    }

    public static void forEachFavorTheme(Consumer<Theme> action) {
        config.getFavorites(action);
    }

    protected static void addTranslation(String raw, String translation) {
        config.translations.setProperty(raw, translation);
    }

    public static String get(Theme theme) {
        return data.get(theme);
    }

    public static void init() {
        data = UIContext.getTheme();
        config = UIContext.getThemeConfig();
        FlatLaf.registerCustomDefaultsSource(StaticConfig.THEME_FLAT_REGISTER_SOURCE);
    }

    public static void addFavorite(Theme t) {
        config.favorites.add(t);
    }

    public static Map<String, String> getThemeGroup(ThemeType type) {
        return data.getGroup(type);
    }


    public static void removeSwingTheme() {
        // TODO: 2022/5/31  
    }
}
