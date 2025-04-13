package plugin.ui.main.util.api;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import common.CommonStatic;
import main.Opts;
import page.support.Importer;
import plugin.ui.common.config.StaticConfig;
import plugin.ui.common.util.Analyser;
import plugin.ui.main.UIPlugin;
import plugin.ui.main.context.BasicConfig;
import plugin.ui.main.context.UIContext;

import javax.swing.*;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class UIFontMenu extends JMenu {

    private static final long serialVersionUID = 1L;

    private static UIFontMenu fontMenu;
    private static final UIPlugin P = UIPlugin.P;
    private static final BasicConfig cfg = UIContext.getBasicConfig();


    private UIFontMenu() {
        super("fontMenu");
        fontMenu = this;
        initFontMenu();
        updateFontMenuItems();
        setFontSizeItemEnable(P.isFontResizable());
    }

    int initialFontMenuItemCount = -1;

    public static UIFontMenu getFontMenu() {
        return fontMenu == null ? new UIFontMenu() : fontMenu;
    }

    private void initFontMenu() {

        setText("Font");
        JMenuItem restoreFontMenuItem = new JMenuItem();
        JMenuItem incrFontMenuItem = new JMenuItem();
        JMenuItem decrFontMenuItem = new JMenuItem();
        JMenuItem customFontMenuItem = new JMenuItem();


        //---- restoreFontMenuItem ----
        restoreFontMenuItem.setText("Restore Font");
        restoreFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        restoreFontMenuItem.addActionListener(e -> restoreFont());
        add(restoreFontMenuItem);

        //---- incrFontMenuItem ----
        incrFontMenuItem.setText("Increase Font Size");
        incrFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        incrFontMenuItem.addActionListener(e -> incrFont());
        add(incrFontMenuItem);

        //---- decrFontMenuItem ----
        decrFontMenuItem.setText("Decrease Font Size");
        decrFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        decrFontMenuItem.addActionListener(e -> decrFont());
        add(decrFontMenuItem);

        //---- useCustomFontMenuItem ----
        customFontMenuItem.setText("Use Custom Font");
        customFontMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        customFontMenuItem.addActionListener(e -> customFont());
        add(customFontMenuItem);

        if (cfg.getString("fontFamily") == null && cfg.getString("fontFile") == null)
            cfg.set("fontFamily", UIManager.getFont("Label.font").getFamily());
        if (cfg.getInteger("fontSize") == null)
            cfg.set("fontSize", 16);
        UIPlugin.execAnimated(() -> {
            Font newFont = StyleContext.getDefaultStyleContext().getFont(cfg.getString("fontFamily"), Font.PLAIN, cfg.getInteger("fontSize"));
            // StyleContext.getFont() may return a UIResource, which would cause loosing user scale factor on Windows
            newFont = FlatUIUtils.nonUIResource(newFont);
            P.putDefaultFont(newFont);
            update();
        });
    }

    private void restoreFont() {
        UIManager.put("defaultFont", null);
        updateFontMenuItems();
        update();
    }

    private void incrFont() {
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) (font.getSize() + 1));
        UIManager.put("defaultFont", newFont);

        updateFontMenuItems();
        update();
    }

    private void decrFont() {
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) Math.max(font.getSize() - 1, 10));
        UIManager.put("defaultFont", newFont);

        updateFontMenuItems();

        update();
    }

    private void customFont() {
        File font = new Importer("Add custom font", Importer.IMP_FONT).get();
        if (font == null)
            return;
        try {
            File dest = CommonStatic.ctx.newFile(StaticConfig.UI_DIRECTORY + font.getName());
            if (!dest.exists())
                if (!dest.createNewFile())
                    throw new Exception("Failed creating destination file: " + dest.getPath());
            if (!font.getAbsolutePath().equals(dest.getAbsolutePath())) {
                //The check is in case the player is using a file from the UI_DIRECTORY
                FileInputStream is = new FileInputStream(font);
                FileOutputStream os = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0)
                    os.write(buffer, 0, length);
                is.close();
                os.close();
            }

            P.putDefaultFont(Font.createFont(Font.TRUETYPE_FONT, dest).deriveFont(Font.PLAIN, P.getFontSize()));
            cfg.set("fontFile", dest.getName());
        } catch (Exception e) {
            Opts.pop("Couldn't create file for Custom Font\n\n" + Arrays.toString(e.getStackTrace()), "Font Error");
            e.printStackTrace();
        }
    }

    public void updateFontMenuItems() {

        if (initialFontMenuItemCount < 0)
            initialFontMenuItemCount = getItemCount();
        else {
            // remove old font items
            for (int i = getItemCount() - 1; i >= initialFontMenuItemCount; i--)
                remove(i);
        }

        // get available font family names
        String[] availableFontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames().clone();
        // sort is essential for binary search
        Arrays.sort(availableFontFamilyNames);

        // get current font
        Font currentFont = UIManager.getFont("defaultFont");
        String currentFamily = currentFont.getFamily();
        boolean def = cfg.getString("fontFile").isEmpty();

        String currentSize = Integer.toString(cfg.getInteger("fontSize"));

        // add font families
        addSeparator();
        ArrayList<String> families = new ArrayList<>(Arrays.asList(
                "Arial", "Cantarell", "Comic Sans MS", "Courier New", "DejaVu Sans",
                "Dialog", "Liberation Sans", "Microsoft YaHei UI", "Monospaced", "Noto Sans", "Roboto",
                "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana"));
        if (!families.contains(currentFamily))
            families.add(currentFamily);
        families.sort(String.CASE_INSENSITIVE_ORDER);
        ButtonGroup familiesGroup = new ButtonGroup();
        for (String family : families) {
            if (!isFontFamilyAvailable(availableFontFamilyNames, family)) {
                // System.out.println("not available: " + family);
                continue;
            }
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(family);
            item.setSelected(def && family.equals(currentFamily));
            item.addActionListener(this::fontFamilyChanged);
            add(item);

            familiesGroup.add(item);
        }

        // add font sizes
        addSeparator();
        ArrayList<String> sizes = new ArrayList<>(Arrays.asList(
                "08","09","10", "11", "12", "14", "16", "18", "20", "24", "28", "32", "36"));
        if (!sizes.contains(currentSize))
            sizes.add(currentSize);
        sizes.sort(String.CASE_INSENSITIVE_ORDER);

        ButtonGroup sizesGroup = new ButtonGroup();
        for (String size : sizes) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(size);
            item.setSelected(size.equals(currentSize));
            item.addActionListener(this::fontSizeChanged);
            add(item);

            sizesGroup.add(item);
        }

        // enabled/disable items
        setFontItemsEnabled();
    }

    private boolean isFontFamilyAvailable(String[] availableFontFamilyNames, String family) {
        return Arrays.binarySearch(availableFontFamilyNames, family) >= 0;
    }

    private void fontFamilyChanged(ActionEvent e) {
        String fontFamily = e.getActionCommand();
        cfg.set("fontFamily", fontFamily);
        cfg.set(String.class, "fontFile", "");

        UIPlugin.execAnimated(() -> {
            Font font = UIManager.getFont("defaultFont");
            Font newFont = StyleContext.getDefaultStyleContext().getFont(fontFamily, font.getStyle(), font.getSize());
            // StyleContext.getFont() may return a UIResource, which would cause loosing user scale factor on Windows
            newFont = FlatUIUtils.nonUIResource(newFont);
            P.putDefaultFont(newFont);
            update();
        });
    }

    private void fontSizeChanged(ActionEvent e) {
        int fontSize = Integer.parseInt(e.getActionCommand());
        cfg.set("fontSize", fontSize);

        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) fontSize);
        UIManager.put("defaultFont", newFont);

        update();
    }

    public void setFontItemsEnabled() {
        boolean enable = UIManager.getLookAndFeel() instanceof FlatLaf;
        for (Component item : getMenuComponents())
            item.setEnabled(enable);
    }

    private void update() {
        P.updateFrame();
    }

    public void setFontSizeItemEnable(boolean enable) {
        for (Component item : getMenuComponents())
            if (item instanceof JCheckBoxMenuItem && Analyser.isInteger(((JCheckBoxMenuItem) item).getText()))
                item.setEnabled(enable);
    }
}
