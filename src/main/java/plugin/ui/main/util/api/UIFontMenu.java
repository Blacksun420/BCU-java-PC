package plugin.ui.main.util.api;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import plugin.ui.common.util.Analyser;
import plugin.ui.main.UIPlugin;

import javax.swing.*;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class UIFontMenu extends JMenu {
    private static UIFontMenu fontMenu;
    private static final UIPlugin P = UIPlugin.P;


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
        // customFontMenuItem.addActionListener(System.out::println);
        add(customFontMenuItem);
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
        Font currentFont = UIManager.getFont("Label.font");
        String currentFamily = currentFont.getFamily();
        String currentSize = Integer.toString(currentFont.getSize());

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
            item.setSelected(family.equals(currentFamily));
            item.addActionListener(this::fontFamilyChanged);
            add(item);

            familiesGroup.add(item);
        }

        // add font sizes
        addSeparator();
        ArrayList<String> sizes = new ArrayList<>(Arrays.asList(
                "10", "11", "12", "14", "16", "18", "20", "24", "28"));
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
        String fontSizeStr = e.getActionCommand();

        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) Integer.parseInt(fontSizeStr));
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
        for (Component item : getMenuComponents()) {
            if (item instanceof JCheckBoxMenuItem && Analyser.isInteger(((JCheckBoxMenuItem) item).getText())) {
                item.setEnabled(enable);
            }
        }
    }
}
