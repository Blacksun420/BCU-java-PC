package plugin.ui.main.page.comp;

import plugin.ui.main.Theme;
import plugin.ui.main.UIPlugin;

import javax.swing.*;

public class UIMenuItem extends JCheckBoxMenuItem {
    private final Theme theme;

    private UIMenuItem(Theme theme) {
        this.theme = theme;
        setText(theme.name);
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + theme.toString();
    }

    public Theme getTheme() {
        return theme;
    }

    public static UIMenuItem getInstance(Theme theme) {
        return getInstance(theme, true);
    }

    public static UIMenuItem getInstance(Theme theme, boolean enableFavor) {
        UIMenuItem item = new UIMenuItem(theme);
        // add Listener
        UIPlugin.getInstance().addThemeItemActionListener(item, enableFavor);
        return item;
    }

}
