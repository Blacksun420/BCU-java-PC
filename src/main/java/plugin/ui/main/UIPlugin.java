package plugin.ui.main;

import main.MainBCU;
import main.Opts;
import page.LoadPage;
import page.MainFrame;
import page.Page;
import plugin.Plugin;
import plugin.ui.common.config.PageConfig;
import plugin.ui.common.util.ReflectUtils;
import plugin.ui.main.context.UIContext;
import plugin.ui.main.handler.ThemeHandler;
import plugin.ui.main.handler.UIHandler;
import plugin.ui.main.util.MenuBarHandler;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static javax.swing.SwingUtilities.updateComponentTreeUI;

public class UIPlugin extends UIHandler implements Plugin {

    public static final String PLUGIN_VERSION = "v0.6.0.0";
    public static UIPlugin P;

    public static UIPlugin getInstance() {
        return P == null ? new UIPlugin(MainFrame.F) : P;
    }

    public static UIPlugin getInstance(JFrame F) {
        return new UIPlugin(F);
    }

    public static void forEachFavorTheme(Consumer<Theme> doSth) {
        ThemeHandler.forEachFavorTheme(doSth);
    }

    public static Font getFont() {
        // return getInstance().setFrameFont();
        return P.getOptional("fontResize") ?
                P.getFrameFont(): UIManager.getFont("defaultFont");
    }

    @Override
    public void doBeforeFrameInit() {
        initHandler();
    }

    @Override
    public void doAfterFrameInit() {
        if (getTarget() == null) {
            setTarget(MainFrame.F);
        }
        setFrameUI();
        updateFrame();
    }

    @Override
    public void doAfterReadingLang() {
        reloadMenuBarCompName();
    }

    @Override
    public String getConfig(String key) {
        return getLoc(key);
    }

    @Override
    public void popInfo(String text, String title) {
        pop(text, title);
    }

    @Override
    public void checkUpdate() {
        UIContext.checkUpdate();
    }

    @Override
    public boolean confirm(String text) {
        return Opts.conf(text);
    }

    @Override
    public String input(String text) {
        return Opts.read(text);
    }

    protected UIPlugin(JFrame F) {
        super(F);
        P = this;
    }

    @Override
    public void writeData() {
        UIContext.writeData();
        if (getFrame() instanceof MainFrame && MainBCU.loaded) {
            io.BCUWriter.writeData();
        }
    }

    @Override
    public void updateFrame() {
        JFrame F = getFrame();
        if (F instanceof MainFrame) {
            Page temp = MainFrame.getPanel();
            while (temp != null) {
                updateComponentTreeUI(temp);
                temp = temp.getFront();
            }
            updateComponentTreeUI(F);
            updateComponentTreeUI(F.getGlassPane());
            ReflectUtils.invokeVoid(F, "Fresized");
        }

        updateComponentTreeUI(MenuBarHandler.getBar());

    }

    public static void popError(String text) {
        popError(text, "ERROR");
    }

    public static void popError(String text, String info) {
        int opt = JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(null, "<html><h3>" + text + "</h3></html>", info, opt);
    }


    public static void pop(String text, String title) {
        Opts.pop(text, title);
    }

    public static String getLoc(String key) {
        return PageConfig.getString(key);
    }

    @Override
    public String getConfig(String key, String defaultValue) {
        return PageConfig.getString(key, defaultValue);
    }

    @Override
    public void updatePage() {
        if (getFrame() instanceof MainFrame && MainFrame.getPanel() instanceof LoadPage)
            SwingUtilities.updateComponentTreeUI(MainFrame.getPanel());
    }


    public static void saveData(boolean pop) {
        P.writeData();
        if (pop) {
            pop("successfully saved data", "Info");
        }
    }

    public static JFrame getFrame() {
        return P.getTarget();
    }

    public static boolean conf(String text) {
        return Opts.conf(text);
    }

    public static void paintPage(Graphics g, JPanel page) {
        P.paintBG(g, page);
        P.paintGIF(g, page);
    }

    public static void execAnimated(Runnable task) {
        getInstance().runAnimated(task);
    }
}
