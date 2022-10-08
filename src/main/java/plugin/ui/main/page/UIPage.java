package plugin.ui.main.page;

import page.Page;
import plugin.ui.common.config.StaticConfig;
import plugin.ui.main.UIPlugin;

import javax.swing.*;


public class UIPage extends EmptyPage {

    private final static UIPlugin P = UIPlugin.getInstance();

    private JSlider opaSlider;
    private JButton removeUI;
    private JButton removeSwingTheme;

    private JTextField opaInput;

    public UIPage(Page p) {
        super(p);
        init();
        addAndListen();
        resized();
    }

    @Override
    public void back() {
        super.back();
        P.uninstallOpaqueHandler(opaSlider, opaInput);
    }

    private void addAndListen() {
        add(removeUI);
        add(opaInput);
        add(opaSlider);
        add(removeSwingTheme);
        addListeners();
    }

    private void init() {
        removeUI = P.getItem(StaticConfig.JBUTTON, "removeUI");
        int alpha = P.getOpaque();
        opaInput = new JTextField(String.valueOf(alpha));
        opaSlider = new JSlider(StaticConfig.OPAQUE_MIN, StaticConfig.OPAQUE_MAX, alpha);
        removeSwingTheme = P.getItem(StaticConfig.JBUTTON, "removeSwingTheme");

    }

    @Override
    public void doResize(int x, int y) {
        set(removeSwingTheme, x, y, 0, 100, 200, 50);
        set(removeUI, x, y, 300, 0, 200, 50);
        set(opaInput, x, y, 600, 0, 100, 50);
        set(opaSlider, x, y, 700, 0, 600, 100);
    }

    public void addListeners() {
        P.installOpaqueHandler(opaInput, opaSlider);
        removeUI.addActionListener(e -> P.removeUI());
        removeSwingTheme.addActionListener(e -> P.removeSwingTheme());
    }



}
