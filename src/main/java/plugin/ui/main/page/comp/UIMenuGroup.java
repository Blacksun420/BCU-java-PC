package plugin.ui.main.page.comp;

import plugin.ui.main.Theme;
import plugin.ui.main.Theme.ThemeType;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class UIMenuGroup extends JMenu {

    public static final Map<String, UIMenuGroup> GROUP_MAP = new HashMap<>();

    public static UIMenuGroup getGroup(ThemeType type) {
        return GROUP_MAP.get(type.name());
    }

    public static UIMenuGroup getGroup(String key) {
        return GROUP_MAP.get(key);
    }

    private final Map<Theme, UIMenuItem> ITEM_MAP = new HashMap<>();

    public UIMenuGroup(String text) {
        setText(text);
        GROUP_MAP.put(text, this);
    }

    public static UIMenuGroup createGroup(ThemeType type) {
        return createGroup(type.name());
    }

    public static UIMenuGroup createGroup(String text) {
        return new UIMenuGroup(text);
    }

    public UIMenuItem getItem(Theme theme) {
        return ITEM_MAP.get(theme);
    }

    public void remove(Theme theme) {
        UIMenuItem item = ITEM_MAP.get(theme);
        remove(item);
        ITEM_MAP.remove(theme);
        SwingUtilities.updateComponentTreeUI(this);
    }


    public void addTheme(Theme theme, boolean enableFavor) {
        if (getItem(theme) == null) {
            UIMenuItem item = UIMenuItem.getInstance(theme, enableFavor);
            // add to favorMenu
            ITEM_MAP.put(theme, item);
            add(item);
        }
    }

    public void addTheme(Theme theme) {
        addTheme(theme, true);
    }

    @Override
    public String toString() {
        return getText();
    }

    public int themeSize() {
        return ITEM_MAP.size();
    }
}
