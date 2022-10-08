package plugin.ui.main.context;

import plugin.ui.main.Theme;

import java.util.HashSet;
import java.util.Properties;
import java.util.function.Consumer;

public class ThemeConfig {

    public Properties translations = new Properties();
    public Theme cur;
    public HashSet<Theme> favorites = new HashSet<>();

    public ThemeConfig() {
    }

    public void getFavorites(Consumer<Theme> action) {
        favorites.forEach(action);
    }

    public void updateFavorite(Theme theme) {
        favorites.add(theme);
    }

}
