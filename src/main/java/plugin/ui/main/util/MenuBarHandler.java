package plugin.ui.main.util;

import main.MainBCU;
import page.MainFrame;
import plugin.ui.common.config.StaticConfig;
import plugin.ui.main.Theme;
import plugin.ui.main.UIPlugin;
import plugin.ui.main.page.comp.Rectangle;
import plugin.ui.main.page.comp.UIMenuGroup;
import plugin.ui.main.page.comp.UIMenuItem;
import plugin.ui.main.util.api.UIFontMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static plugin.ui.main.handler.SwingComponentHandler.createSlider;


public class MenuBarHandler {
    private static final UIPlugin P = UIPlugin.P;

    private static final JMenuBar BAR = new JMenuBar();
    private static final JLabel themeTip = new JLabel();
    private static final Map<String, JMenuItem> fileMenuItemMap = new HashMap<>();

    public static void setTip(String text) {
        themeTip.setText(text);
    }

    public static JMenuBar getBar() {
        return BAR;
    }

    public static void initialize() {
        if (BAR.getMenuCount() != 0) {
            return;
        }

        addFileMenu();
        addThemeMenu();
        addFontMenu();
        addOptionMenu();
        addSettingMenu();
        BAR.add(themeTip);
    }

    private static void addFontMenu() {
        BAR.add(UIFontMenu.getFontMenu());
    }

    private static void addFileMenu() {
        JMenu menu = P.getItem(StaticConfig.MENU, "File");
        BAR.add(menu);

        JMenuItem save = P.getItem(StaticConfig.MENU_ITEM, "Save All");
        save.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK));
        save.setEnabled(MainBCU.loaded);
        save.addActionListener(e -> UIPlugin.saveData(true));
        menu.add(save);

        fileMenuItemMap.put(save.getText(), save);
    }

    public static JMenuItem getFileMenu(String key) {
        JMenuItem item = fileMenuItemMap.get(key);
        if (item == null) {
            System.out.println("MenuItem is null: " + key);
        }
        return item;
    }

    /**
     * <pre>
     * Theme
     * |
     * |---> favorite-group
     * |      |---> (Tip Item)
     * |      |---> theme 0
     * |      |---> theme 1
     * |
     * |---> flat
     * |    | ---> light-group
     * |    |        |---> theme 0
     * |    |        |---> theme 1
     * |    |        |---> theme 2
     * |    |
     * |    | ---> dark-group
     * |    |        |---> theme 0
     * |    |        |---> theme 1
     * |    |        |---> theme 2
     * |
     * |
     * |---> nimbus-group
     * |       |---> nimbus light
     * |       |---> nimbus dark
     * |
     * |---> default-group
     * |       |--->
     * |       |--->
     * |
     * |
     * </pre>
     */
    private static void addThemeMenu() {
        JMenu theme = P.getItem(StaticConfig.MENU, "Theme");

        // create favorite group
        UIMenuGroup favor = UIMenuGroup.createGroup("Favorites");
        favor.add(P.newFavorMenuTip());
        UIPlugin.forEachFavorTheme(t -> favor.addTheme(t, true));
        theme.add(favor);

        // add separator
        theme.addSeparator();

        // create FlatLaf group
        JMenu flat = P.getItem(StaticConfig.MENU, "Flat");

        // create a group of each ThemeType
        P.forEachTheme((type -> {
            UIMenuGroup group = UIMenuGroup.createGroup(type);

            if (type.isFlat()) {
                flat.add(group);
            } else {
                theme.add(group);
            }

            return group::addTheme;
        }));

        // BCU Origin Theme
        UIMenuGroup nimbus = UIMenuGroup.getGroup(Theme.ThemeType.Nimbus);
        if (nimbus != null && nimbus.themeSize() != 0) {
            JMenuItem light = P.getItem(StaticConfig.MENU_ITEM, "nimbus-light");
            JMenuItem dark = P.getItem(StaticConfig.MENU_ITEM, "nimbus-dark");
            nimbus.add(light);
            nimbus.add(dark);

            light.addActionListener(e -> {
                utilpc.Theme.LIGHT.setTheme();
                MenuBarHandler.setTip("( Now Theme: " + light.getText() + ")");
                P.putDefaultFont(new Font(MainFrame.fontType, MainFrame.fontStyle, MainFrame.fontSize));
                P.updateFrame();
            });

            dark.addActionListener(e -> {
                utilpc.Theme.DARK.setTheme();
                MenuBarHandler.setTip("( Now Theme: " + dark.getText() + ")");
                P.putDefaultFont(new Font(MainFrame.fontType, MainFrame.fontStyle, MainFrame.fontSize));
                P.updateFrame();
            });
        }

        adjustFlatMenuLocation(theme, flat, removeEmptyGroup(theme));

        getBar().add(theme);
    }

    private static void adjustFlatMenuLocation(JMenu themeMenu, JMenu flat, List<String> removed) {
        boolean defaultThemesWereMoved = true;
        String[] arr = {"Nimbus", "SwingDefault"};
        for (String s : arr) {
            if (!removed.contains(s)) {
                defaultThemesWereMoved = false;
                break;
            }
        }

        if (defaultThemesWereMoved && flat.getMenuComponentCount() != 0) {
            Component[] menu = flat.getMenuComponents();
            for (Component comp : menu) {
                if (comp instanceof UIMenuGroup) {
                    themeMenu.add(comp);
                }
            }

            themeMenu.remove(flat);
            P.removeItem(flat);
        } else {
            themeMenu.add(flat, 2);
        }
    }

    private static List<String> removeEmptyGroup(JMenu m) {
        List<String> removed = new LinkedList<>();
        UIMenuGroup temp;
        for (Component component : m.getMenuComponents()) {
            if (component instanceof UIMenuGroup && (temp = (UIMenuGroup) component).themeSize() == 0 && !"Favorites".equals(temp.getText())) {
                m.remove(temp);
                UIMenuGroup.GROUP_MAP.remove(temp.getText(), temp);
                removed.add(temp.getText());
            }
        }
        return removed;
    }

    /**
     * <pre>
     * Option
     * |
     * |---> animated
     * |---> Font Resize
     * |---> BG Resize
     * |---> GIF Resize
     * |
     * </pre>
     */
    private static void addOptionMenu() {
        JMenu option = P.getItem(StaticConfig.MENU, "Option");

        // animated
        JCheckBoxMenuItem animated = P.getItem(StaticConfig.CHECK_BOX_MENU_ITEM, "animatedChangeUI");
        animated.addActionListener((e -> P.reverseAnimated()));

        // resize
        JCheckBoxMenuItem fontResize = P.getItem(StaticConfig.CHECK_BOX_MENU_ITEM, "fontResize");
        JCheckBoxMenuItem bgResize = P.getItem(StaticConfig.CHECK_BOX_MENU_ITEM, "bgResize");
        JCheckBoxMenuItem gifResize = P.getItem(StaticConfig.CHECK_BOX_MENU_ITEM, "gifResize");
        fontResize.addActionListener(e -> P.setFontResizable(fontResize.isSelected()));
        bgResize.addActionListener((e) -> P.setImageResizable(bgResize.isSelected()));
        gifResize.addActionListener((e) -> P.setGifResizable(gifResize.isSelected()));

        // add
        option.add(animated);
        option.add(fontResize);
        option.add(bgResize);
        option.add(gifResize);

        getBar().add(option);
    }

    private static void addSettingMenu() {
        JMenu setting = P.getItem(StaticConfig.MENU, "setting");

        JMenu UI = P.getItem(StaticConfig.MENU, "UI");

        /**/
        JMenuItem remove = P.getItem(StaticConfig.MENU_ITEM, "removeUI");
        remove.addActionListener(e -> P.removeUI());
        UI.add(remove);


        JMenu importMenu = P.getItem(StaticConfig.MENU, "importBTN");
        JMenuItem icon = P.getItem(StaticConfig.MENU_ITEM, "importBTN-icon");
        icon.addActionListener((e -> P.selectFileAsIcon()));
        JMenuItem bg = P.getItem(StaticConfig.MENU_ITEM, "importBTN-bg");
        bg.addActionListener((e -> P.selectFileAsBG()));
        importMenu.add(icon);
        importMenu.add(bg);


        /**/
        JMenu image_opaque = P.getItem(StaticConfig.MENU, "image-opaque");
        JSlider opaSlider = createSlider();
        P.installOpaqueHandler(opaSlider);
        image_opaque.add(opaSlider);

        /**/
        JMenuItem translation = P.getItem(StaticConfig.MENU_ITEM, "theme-translation");
        translation.addActionListener(e -> P.inputTranslation());
        translation.setAccelerator(KeyStroke.getKeyStroke('F', KeyEvent.ALT_DOWN_MASK));

        /**/
        JMenuItem export = P.getItem(StaticConfig.MENU_ITEM, "export-config");
        export.addActionListener((e -> {
            P.writeData();
            P.showLocation();
        }));
        export.setAccelerator(KeyStroke.getKeyStroke('E', KeyEvent.CTRL_DOWN_MASK));

        /**/
        JMenuItem checkUpdate = P.getItem(StaticConfig.MENU_ITEM, "check-update");
        checkUpdate.addActionListener((e -> P.askUpdate()));
        checkUpdate.setAccelerator(KeyStroke.getKeyStroke('U', KeyEvent.CTRL_DOWN_MASK));

        /**/
        UI.add(importMenu);
        UI.add(image_opaque);
        UI.add(translation);
        UI.add(export);
        UI.add(checkUpdate);

        /*----------------------*/
        JMenu battle_scene = P.getItem(StaticConfig.MENU, "battle-scene");

        /**/
        JMenu radio_w = P.getItem(StaticConfig.MENU, "radio-w");
        JMenu radio_h = P.getItem(StaticConfig.MENU, "radio-h");
        JSlider jsw = createSlider();
        JSlider jsh = createSlider();
        JMenuItem tip = P.getItem(StaticConfig.MENU_ITEM, "battle-scene-tip-menu");

        battle_scene.add(tip);
        battle_scene.add(radio_w);
        battle_scene.add(radio_h);
        radio_w.add(jsw);
        radio_h.add(jsh);

        jsw.setValue((int) (Rectangle.ratioX / Rectangle.UNIT_X));
        jsw.addChangeListener((e -> P.setRatioX(jsw.getValue())));
        jsh.setValue((int) (Rectangle.ratioY / Rectangle.UNIT_Y));
        jsh.addChangeListener((e -> P.setRatioY(jsh.getValue())));


        setting.add(UI);
        setting.add(battle_scene);
        getBar().add(setting);
    }

    public static void enableSave() {
        JMenuItem save_all = getFileMenu("Save All");
        if (save_all != null) save_all.setEnabled(true);
    }

    public static void updateSelectedList(Theme theme) {
        getThemeItem(P.getCur()).forEach(uiMenuItem -> uiMenuItem.setSelected(false));

        if (theme != null) getThemeItem(theme).forEach(uiMenuItem -> uiMenuItem.setSelected(true));
    }

    public static UIMenuGroup getFavorMenu() {
        return UIMenuGroup.getGroup("Favorites");
    }

    public static List<UIMenuItem> getThemeItem(Theme theme) {
        List<UIMenuItem> items = new LinkedList<>();
        // for each group, if a group has this theme, add its item .
        UIMenuGroup.GROUP_MAP.values().forEach(group -> {
            UIMenuItem item = group.getItem(theme);
            if (item != null) {
                items.add(item);
            }
        });
        return items;
    }
}
