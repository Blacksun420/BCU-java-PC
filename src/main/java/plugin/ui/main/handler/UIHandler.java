package plugin.ui.main.handler;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.StringUtils;
import plugin.ui.common.config.StaticConfig;
import plugin.ui.common.util.Fio.FileUtil;
import plugin.ui.main.Theme;
import plugin.ui.main.Theme.ThemeType;
import plugin.ui.main.context.BasicConfig;
import plugin.ui.main.context.UIContext;
import plugin.ui.main.page.comp.Rectangle;
import plugin.ui.main.util.MenuBarHandler;
import plugin.ui.main.util.api.FlatAnimatedLafChange;
import plugin.ui.main.util.api.GifComponent;
import plugin.ui.main.util.api.UIFontMenu;

import java.awt.*;
import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class UIHandler extends SwingComponentHandler {
    private BasicConfig config;
    private boolean init = false;

    public UIHandler(javax.swing.JFrame frame) {
        super(frame);
    }

    /**
     * <pre>initHandler():
     * 1. init UIContext
     * 2. init UIPainter
     * 3. init ThemeHandler
     * 4. init MenuBarHandler
     * 5. set default theme
     * 6. do others
     * </pre>
     */
    protected void initHandler() {
        init = false;

        // init config
        UIContext.init();
        config = UIContext.getBasicConfig();

        // init Painter
        UIPainter.init();

        // init Theme
        ThemeHandler.init();

        // init MenuBar
        MenuBarHandler.initialize();

        // init theme
        changeTheme(ThemeHandler.getCur(), false);

        // init others
        initOthers();

        init = true;
    }

    private void initOthers() {
        UIFontMenu.getFontMenu().setFontItemsEnabled();
    }

    public void setFrameUI() {
        Font font = readUIFont();
        if (font != null) {
            putDefaultFont(font);
        }

        conditionalOnAvailable(config.getString("bgFile"), StaticConfig.KEY_BG, this::setBG);
        conditionalOnAvailable(config.getString("iconFile"), StaticConfig.KEY_ICON, this::setIcon);
        conditionalOnAvailable(config.getString("gifFile"), this::setGIF);
        MenuBarHandler.enableSave();
        setMenuBar();
    }

    private Font readUIFont() {
        File file = new File(StaticConfig.UI_DIRECTORY + config.getString("fontFile"));
        Font UIFont = null;
        try {
            if (file.exists() && file.isFile()) {
                UIFont = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(Font.PLAIN, getFontSize());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UIFont;
    }

    private void setGIF(String gifFile) {
        GifComponent.gif = new GifComponent(new File(gifFile), config.getInteger("gifDelay"));
        GifComponent.gif.resize = config.getBoolean("resizeGIF");
    }

    private void conditionalOnAvailable(String test, String attr, BiConsumer<String, String> todo) {
        if (!StringUtils.isEmpty(test)) {
            todo.accept(test, attr);
        }
    }

    private void conditionalOnAvailable(String test, Consumer<String> todo) {
        if (test != null && !test.isEmpty()) {
            todo.accept(test);
        }
    }

    @Override
    public void changeTheme(Theme theme, boolean writeFile) {
        final String key = "warningChangeTheme";
        if (theme.isDemanding() && config.getBoolean(key)) {
            if (!confirm(getConfig("tip-warning-when-change-theme"))) {
                return;
            }
        }

        changeTheme(theme, ThemeHandler.get(theme), writeFile);
        MenuBarHandler.updateSelectedList(theme);
        UIFontMenu.getFontMenu().updateFontMenuItems();
        config.set(key, !theme.isDemanding());
    }

    private void changeTheme(Theme theme, String clazz, boolean writeFile) {
        if (StringUtils.isEmpty(clazz)) {
            popErr("invalid theme: [" + theme + "] in ui.json file");
            return;
        }

        if (isAnimated())
            invokeAnimated(() -> changeTheme0(theme, clazz, writeFile));
        else
            changeTheme0(theme, clazz, writeFile);
    }

    private void changeTheme0(Theme theme, String clazz, boolean writeFile) {
        /*1*/
        setThemeTip(theme);

        /*2*/
        runAnimated(() -> setLookAndFeel(clazz));

        /*3*/
        updateFrame();

        /*4*/
        ThemeHandler.setCur(theme);

        /*5*/
        if (writeFile) {
            writeData();
        }
    }

    public void askUpdate() {
        new Thread(UIContext::askUpdate).start();
    }

    public void forEachTheme(Function<ThemeType, Consumer<Theme>> doSth) {
        ThemeType.forEach((
                type -> {
                    Consumer<Theme> themeConsumer = doSth.apply(type);
                    ThemeHandler.getThemeGroup(type).forEach((name, clazz) -> themeConsumer.accept(new Theme(type, name)));
                }));
    }

    public void reverseAnimated() {
        config.set("animatedChangeUI", !config.getBoolean("animatedChangeUI"));
        writeData();
    }

    @Override
    public File selectFileAsBG() {
        File file = super.selectFileAsBG();

        if (file != null && confirm(getConfig("set-default-image"))) {
            String name = file.getName();
            FileUtil.copyFile(file, new File(StaticConfig.UI_DIRECTORY + name));
            config.set("bgFile", name);
            writeData();
        }
        return file;
    }

    @Override
    public File selectFileAsIcon() {
        File file = super.selectFileAsIcon();

        if (file != null && confirm(getConfig("set-default-icon"))) {
            String name = file.getName();
            FileUtil.copyFile(file, new File(StaticConfig.UI_DIRECTORY + name));
            config.set("iconFile", name);
            writeData();
        }
        return file;
    }

    public void inputTranslation() {
        while (true) {
            String read = input("Now theme name: " + getCur() + "\nInput your translation: ");
            if (read == null) {
                return;
            }

            if (read.isEmpty()) {
                if (isOk(getConfig("input-nothing"))) {
                    return;
                } else {
                    continue;
                }
            }

            if (read.length() > 20) {
                if (!isOk(getConfig("translation-too-long"))) {
                    continue;
                }
            }

            addTranslation(read);
            break;
        }
    }

    private void addTranslation(String read) {
        Theme cur = getCur();
        ThemeHandler.addTranslation(cur.name, read);
        setThemeTip(cur);
        MenuBarHandler.getThemeItem(cur).forEach(item -> item.setText(read));
        writeData();
    }

    @Override
    public void setFileAsGIF(File file) {
        if (file == null) {
            return;
        }

        GifComponent.gif = new GifComponent(file, config.getInteger("gifDelay"));

        if (confirm(getConfig("set-default-gif"))) {
            String name = file.getName();
            FileUtil.copyFile(file, new File(StaticConfig.UI_DIRECTORY + name));
            config.set("gifDelay", name);
            writeData();
        }
    }

    @Override
    protected void clear() {
        config.clear();
    }

    private boolean isAnimated() {
        return init && getOptional("animatedChangeUI");
    }

    @Override
    public int getOpaque() {
        return config.getInteger("opaque");
    }

    @Override
    public void setOpaque(Integer opaque) {
        config.set("opaque", opaque);
    }

    public double getRatioX() {
        return config.getDouble("padding_radio_width");
    }

    public void setRatioX(int value) {
        Rectangle.setRatioX(value);
        config.set("padding_radio_width", Rectangle.ratioX);
    }

    public double getRatioY() {
        return config.getDouble("padding_radio_height");
    }

    public void setRatioY(int value) {
        Rectangle.setRatioY(value);
        config.set("padding_radio_height", Rectangle.ratioX);
    }

    public int getPointX() {
        return config.getInteger("BG-Point-X");
    }

    public int getPointY() {
        return config.getInteger("BG-Point-Y");
    }

    private void setResizable(String key, Boolean resize) {
        config.set(key, resize);
        updateFrame();
    }

    @Override
    public boolean getOptional(String key) {
        return config.getBoolean(key);
    }


    public boolean isFontResizable() {
        return getOptional("fontResize");
    }


    public void setImageResizable(boolean resize) {
        setResizable("bgResize", resize);
        UIPainter.resize = resize;
    }

    public void setGifResizable(boolean resize) {
        setResizable("gifResize", resize);
        GifComponent.gif.resize = resize;
    }

    public void setFontResizable(boolean resize) {
        setResizable("fontResize", resize);
        if (resize) {
            UIFontMenu.getFontMenu().updateFontMenuItems();
        }
        UIFontMenu.getFontMenu().setFontSizeItemEnable(!resize);

    }

    public void popErr(String msg) {
        popInfo(msg, "ERROR");
    }

    @Override
    public Color getColor(String key) {
        return config.get(Color.class, key);
    }

    public void removeSwingTheme() {
        ThemeHandler.removeSwingTheme();
    }

    public void runAnimated(Runnable task) {
        if (isAnimated()) {
            FlatAnimatedLafChange.showSnapshot();
            task.run();
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        } else {
            task.run();
        }
    }

    public abstract String input(String s);


}
