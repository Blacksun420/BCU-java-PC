package plugin.ui.main.util;

import common.CommonStatic;
import io.BCMusic;
import main.MainBCU;
import main.Timer;
import page.MainFrame;
import page.MainLocale;
import plugin.ui.main.UIPlugin;

import javax.swing.*;
import java.util.ArrayList;

public class BCUSettingMenu extends JMenu {

    public static final int PROG = 0, FPS = 1, AXIS = 2, EXCONT = 3, TIME = 4, PLAYMUSIC = 5;
    private final ArrayList<JMenuItem> comps = new ArrayList<>(5);

    public BCUSettingMenu() {
        setText(UIPlugin.P.getConfig("game-setting"));
        JCheckBoxMenuItem prog = buildCheckBox(CommonStatic.getConfig().prog);
        prog.addActionListener(l -> {
            CommonStatic.getConfig().prog = prog.isSelected();
            MainFrame.callback("prog");
        });

        JCheckBoxMenuItem fps60 = buildCheckBox(CommonStatic.getConfig().fps60);
        fps60.setText("60 FPS");
        fps60.addActionListener(l -> {
            CommonStatic.getConfig().fps60 = fps60.isSelected();
            Timer.fps = 1000 / (CommonStatic.getConfig().fps60 ? 60 : 30);
            MainFrame.callback("fps");
        });

        JCheckBoxMenuItem axis = buildCheckBox(CommonStatic.getConfig().ref);
        axis.addActionListener(l -> {
            CommonStatic.getConfig().ref = axis.isSelected();
            MainFrame.callback("axis");
        });

        JCheckBoxMenuItem excont = buildCheckBox(CommonStatic.getConfig().exContinuation);
        excont.addActionListener(l -> {
            CommonStatic.getConfig().exContinuation = excont.isSelected();
            MainFrame.callback("excont");
        });

        JCheckBoxMenuItem time = buildCheckBox(MainBCU.seconds);
        time.addActionListener(l -> {
            MainBCU.seconds = time.isSelected();
            MainFrame.callback("time");
        });

        JCheckBoxMenuItem mus = buildCheckBox(BCMusic.play);
        mus.addActionListener(l -> {
            BCMusic.play = mus.isSelected();
            MainFrame.callback("music");
        });
        //add
        updateLoc();
        add(prog);
        add(fps60);
        add(axis);
        add(excont);
        add(time);
        add(mus);
    }

    private JCheckBoxMenuItem buildCheckBox(boolean sele) {
        JCheckBoxMenuItem jcb = new JCheckBoxMenuItem();
        jcb.setSelected(sele);
        comps.add(jcb);
        return jcb;
    }

    public void updateLoc() {
        comps.get(PROG).setText(MainLocale.getLoc(MainLocale.PAGE, "pkprog"));
        comps.get(AXIS).setText(MainLocale.getLoc(MainLocale.PAGE, "axis"));
        comps.get(EXCONT).setText(MainLocale.getLoc(MainLocale.PAGE, "excont"));
        comps.get(TIME).setText(MainLocale.getLoc(MainLocale.PAGE, "meastime") + ": " + MainLocale.getLoc(MainLocale.PAGE, "secs"));
        comps.get(PLAYMUSIC).setText(MainLocale.getLoc(MainLocale.PAGE, "musc"));
    }

    public void syncCheckBox(int ind, boolean sele) {
        JCheckBoxMenuItem jcb = (JCheckBoxMenuItem)comps.get(ind);
        jcb.setSelected(sele);
    }
}
