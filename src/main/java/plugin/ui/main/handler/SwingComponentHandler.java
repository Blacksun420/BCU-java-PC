package plugin.ui.main.handler;

import com.formdev.flatlaf.util.StringUtils;
import org.intellij.lang.annotations.MagicConstant;
import plugin.ui.common.config.StaticConfig;
import plugin.ui.common.util.Analyser;
import plugin.ui.common.util.Fio.FileUtil;
import plugin.ui.common.util.UIException;
import plugin.ui.main.Theme;
import plugin.ui.main.page.comp.UIMenuItem;
import plugin.ui.main.util.ImageReader;
import plugin.ui.main.util.MenuBarHandler;
import plugin.ui.main.util.api.GifComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.event.ActionEvent.META_MASK;

public abstract class SwingComponentHandler {

    public final List<JComponent> OPAQUE_LIST = new ArrayList<>();
    public final Map<String, AbstractButton> LANG_COMP = new HashMap<>();

    private JFrame F;

    public SwingComponentHandler(JFrame frame) {
        F = frame;
    }

    public JFrame getTarget() {
        return F;
    }

    public void setTarget(JFrame frame) {
        F = frame;
    }

    public void addThemeToFavorMenu(Theme theme) {
        MenuBarHandler.getFavorMenu().addTheme(theme, false);
        ThemeHandler.addFavorite(theme);
        MenuBarHandler.updateSelectedList(theme);

        writeData();
    }

    public void removeThemeFromFavorMenu(Theme theme) {
        MenuBarHandler.getFavorMenu().remove(theme);
        ThemeHandler.removeFavorite(theme);
        MenuBarHandler.updateSelectedList(theme);

        writeData();
    }

    public void addCurrentThemeToFavorMenu() {
        addThemeToFavorMenu(getCur());
        popInfo(getConfig("add success"), "Tip");
    }

    public void addThemeItemActionListener(UIMenuItem item, boolean enableFavor) {
        item.addActionListener(e -> {
            Theme theme = item.getTheme();
            item.setSelected(false);
            if ((e.getModifiers() & META_MASK) != 0) {
                // right click
                if (enableFavor) {
                    addThemeToFavorMenu(theme);
                } else {
                    removeThemeFromFavorMenu(theme);
                }
            } else {
                // left click
                theme.setTheme();
            }
        });
    }

    public JMenuItem newFavorMenuTip() {
        JMenuItem tipMenu = getItem(StaticConfig.MENU_ITEM, "favorite-tip-menu");
        tipMenu.setAccelerator(KeyStroke.getKeyStroke('F', KeyEvent.CTRL_DOWN_MASK));
        tipMenu.addActionListener((e) -> addCurrentThemeToFavorMenu());
        return tipMenu;
    }

    public void installOpaqueHandler(JTextField input, JSlider slider) {
        OPAQUE_LIST.add(input);
        OPAQUE_LIST.add(slider);

        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        input.addActionListener((e) -> {
            int oldValue = getOpaque();
            int value = Analyser.parseInt(input.getText(), oldValue);
            if (value > StaticConfig.OPAQUE_MAX || value < StaticConfig.OPAQUE_MIN || value == oldValue) {
                input.setText(String.valueOf(oldValue));
            } else {
                updateOpaque(value);
            }
        });

        slider.addChangeListener(e -> updateOpaque(slider.getValue()));

    }

    public void installOpaqueHandler(JSlider opaSlider) {
        OPAQUE_LIST.add(opaSlider);
        opaSlider.setValue(getOpaque());
        opaSlider.setMajorTickSpacing(10);
        opaSlider.setMinorTickSpacing(5);
        opaSlider.setPaintTicks(true);
        opaSlider.setPaintLabels(true);
        opaSlider.addChangeListener(e -> updateOpaque(opaSlider.getValue()));
    }

    public void uninstallOpaqueHandler(JSlider opaSlider, JTextField opaInput) {
        OPAQUE_LIST.remove(opaSlider);
        OPAQUE_LIST.remove(opaInput);
    }

    public void reloadMenuBarCompName() {
        LANG_COMP.forEach((k, item) -> item.setText(getConfig(k)));
    }

    /**
     * [0] = JMenu
     * [1] = JMenuItem
     * [2] = JCheckBoxMenuItem
     */
    @SuppressWarnings("unchecked")
    public <T> T getItem(@MagicConstant(intValues = {StaticConfig.MENU, StaticConfig.MENU_ITEM, StaticConfig.CHECK_BOX_MENU_ITEM, StaticConfig.JBUTTON})
                         int code, String key) {
        AbstractButton target;
        switch (code) {
            case StaticConfig.MENU:
                target = new JMenu();
                break;
            case StaticConfig.MENU_ITEM:
                target = new JMenuItem();
                break;
            case StaticConfig.CHECK_BOX_MENU_ITEM:
                target = new JCheckBoxMenuItem();
                target.setSelected(getOptional(key));
                break;
            case StaticConfig.JBUTTON:
                target = new JButton();
                break;
            default:
                throw new UIException("unsupported code: " + code + ",key=" + key);
        }

        target.setText(getConfig(key));
        String toolTipText = getConfig("tip-" + key, null);
        if (!StringUtils.isEmpty(toolTipText))
            target.setToolTipText(toolTipText);
        LANG_COMP.put(key, target);
        return (T) target;
    }

    public void putDefaultFont(Font newFont) {
        UIManager.put("defaultFont", newFont);
    }

    void setLookAndFeel(String clazz) {
        try {
            UIManager.setLookAndFeel(clazz);
        } catch (Exception e) {
            popInfo("failed to change theme: " + e, "unexpected error");
            throw new UIException(e);
        }
        updateFrame();
    }

    private void updateOpaque0(int opaque) {
        for (JComponent jComponent : OPAQUE_LIST) {
            if (jComponent instanceof JTextField) {
                ((JTextField) jComponent).setText(String.valueOf(opaque));
            } else if (jComponent instanceof JSlider) {
                ((JSlider) jComponent).setValue(opaque);
            }
        }

        ImageReader.setAlpha(opaque);
        ImageReader.reTransparency(StaticConfig.KEY_BG);
        ImageReader.reTransparency(StaticConfig.KEY_USER_SELECTED_IMAGE);
        /*
         * call updatePage() here is just to update LoadPage,
         * when BCU is loading (current page is LoadPage) and user try to change opaque.
         * if call updateFrame() here instead may cause NPE,
         * because a component can't update itself when it performs action.
         */
        updatePage();
    }

    public BufferedImage getImage(String filename, String key) {
        return getImage(filename, key, getOpaque());
    }

    public BufferedImage getImage(String filename, String key, int alpha) {
        if (filename.contains(".")) {
            File file = new File(StaticConfig.UI_DIRECTORY + filename);
            if (file.exists()) {
                return ImageReader.getImage(file, key, alpha);
            }
        }

        filename = filename.split("\\.")[0];
        for (String suffix : StaticConfig.IMAGE_SUFFIX) {
            File file = new File(StaticConfig.UI_DIRECTORY + filename + suffix);
            if (file.exists()) {
                return ImageReader.getImage(file, key, alpha);
            }
        }

        return null;
    }

    public void paintBG(Graphics g, JPanel page) {
        UIPainter.paintBG(g, page);
    }

    public void paintGIF(Graphics g, JPanel page) {
        UIPainter.paintGIF(g, page, GifComponent.gif);
    }

    public void setMenuBar() {
        F.setJMenuBar(MenuBarHandler.getBar());
    }

    public void setIcon(String icon, String key) {
        F.setIconImage(getImage(icon, key, 100));
    }

    public int getFontSize() {
        JFrame F = getTarget();

        if (F == null) {
            return StaticConfig.FONT_MIN_SIZE;
        } else {
            int w = F.getRootPane().getWidth();
            int h = F.getRootPane().getHeight();
            int size = Math.min(24 * w / 2300, 24 * h / 1300);
            return Math.max(size, StaticConfig.FONT_MIN_SIZE);
        }
    }

    public void setThemeTip(Theme theme) {
        MenuBarHandler.setTip(" ( Now Theme: " + theme.getText() + " )");
    }

    public Font getFrameFont() {
        Font oldFont = UIManager.getFont("defaultFont");
        Font newFont = oldFont == null ? StaticConfig.DEFAULT_FONT : oldFont.deriveFont((float) getFontSize());
        putDefaultFont(newFont);
        updateFrame();
        return newFont;
    }

    boolean isOk(String msg) {
        int opt = JOptionPane.YES_NO_OPTION;
        int val = JOptionPane.showConfirmDialog(null, msg, getConfig("confirm"), opt);
        return val == JOptionPane.YES_OPTION || val == JOptionPane.CLOSED_OPTION;
    }

    protected void invokeAnimated(Runnable run) {
        EventQueue.invokeLater(run);
    }

    public void updateOpaque(int opaque) {
        if (getOpaque() == opaque) {
            return;
        }
        setOpaque(opaque);
        updateOpaque0(opaque);
    }

    protected abstract void setOpaque(Integer opaque);

    public void removeItem(JMenuItem item) {
        LANG_COMP.remove(item.getText());
    }

    public void showLocation() {
        if (confirm(getConfig("export-success"))) {
            try {
                Desktop.getDesktop().open(new File(StaticConfig.UI_DIRECTORY));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected File selectFileAsIcon() {
        File file;
        while (true) {
            file = FileUtil.getFile("plugin/ui/", ".png/jpg file", "png", "jpg");

            if (file == null) {
                return null;
            }

            // try read
            ImageReader.readImage(file, StaticConfig.KEY_ICON, 100);
            // file may be unsupported
            BufferedImage image = ImageReader.getImage(StaticConfig.KEY_ICON);
            if (image != null) {
                F.setIconImage(image);
                return file;
            }

            if (!confirm(file.getName() + getConfig("file-not-supported"))) {
                return null;
            }
        }

    }

    public File selectFileAsBG() {
        File file = FileUtil.getFile("plugin/ui/", ".png/jpg file", "png", "jpg", "gif");

        if (file == null || file.getName().endsWith(".gif")) {
            setFileAsGIF(file);
            return null;
        }

        setBG(file);
        updateFrame();
        return file;
    }

    protected abstract void setFileAsGIF(File file);


    public void setBG(File file) {
        ImageReader.readImage(file, StaticConfig.KEY_USER_SELECTED_IMAGE);
        UIPainter.setKey(StaticConfig.KEY_USER_SELECTED_IMAGE);
    }


    public void setBG(String filename, String key) {
        getImage(filename, key);
        UIPainter.setKey(key);
    }

    public void removeUI() {
        // remove bg
        UIPainter.setKey(null);
        // remove gif
        GifComponent.gif = null;
        // remove icon
        F.setIconImage(null);
        // clear cache
        ImageReader.clearImage();
        // api for subclass
        clear();
        // update frame
        updateFrame();
    }

    public static JSlider createSlider() {
        JSlider s = new JSlider(0, 100);
        s.setMinorTickSpacing(5);
        s.setMajorTickSpacing(20);
        s.setPaintTicks(true);
        s.setPaintLabels(true);
        return s;
    }

    public Theme getCur() {
        return ThemeHandler.getCur();
    }

    protected abstract void clear();

    public abstract boolean confirm(String text);

    public abstract void updateFrame();

    public abstract int getOpaque();

    public abstract void changeTheme(Theme theme, boolean writeData);

    public abstract void popInfo(String add_success, String tip);

    public abstract String getConfig(String key);

    public abstract String getConfig(String key, String defaultValue);

    public abstract Color getColor(String key);

    protected abstract void updatePage();

    public abstract void writeData();

    protected abstract boolean getOptional(String key);

}
