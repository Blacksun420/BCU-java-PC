package page.pack;

import common.CommonStatic;
import common.battle.BasisLU;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.unit.AbForm;
import main.MainBCU;
import main.Opts;
import main.Timer;
import page.*;
import plugin.ui.main.util.DownloadProgressFrame;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Vector;

public class PackLoadPage extends DefaultPage {

    private static boolean first = true;
    private final PackEditPage.PackList jlp = new PackEditPage.PackList(new Vector<>(UserProfile.getUserPacks()));
    private final JScrollPane jspp = new JScrollPane(jlp);
    private final JTG skld = new JTG(MainLocale.PAGE, "startskip");

    private final PackEditPage.PackList llp = new PackEditPage.PackList(new Vector<>(UserProfile.profile().skipped.values()));
    private final JScrollPane lspp = new JScrollPane(llp);
    private final JBTN pkld = new JBTN(MainLocale.PAGE, "loadpack");

    private final JL pid = new JL();
    private final JL parents = new JL();

    private UserPack pac;
    private boolean changing = false;

    protected PackLoadPage(Page p, UserPack pack) {
        super(p);
        pac = pack;
        ini();
    }

    protected void resized(int x, int y) {
        super.resized(x, y);
        int w = 50;
        set(jspp, x, y, w, 150, 400, 600);
        set(skld, x, y, w, 800, 400, 50);
        w += 450;
        set(lspp, x, y, w, 150, 400, 600);
        set(pkld, x, y, w, 800, 400, 50);
        w += 450;
        set(pid, x, y, w, 150, 600, 50);
        set(parents, x, y, w, 200, 600, 50);
    }

    private void ini() {
        if (first) {
            for (UserPack p : UserProfile.profile().skipped.values())
                p.icon = p.source.readImage("icon");
            first = false;
        }
        add(jspp);
        add(skld);
        add(lspp);
        add(pkld);
        add(pid);
        add(parents);
        setPack(pac);

        pkld.setEnabled(false);
        setListeners();
    }

    private void setListeners() {
        jlp.addListSelectionListener(arg0 -> {
            if (changing || jlp.getValueIsAdjusting())
                return;
            changing = true;
            setPack(jlp.getSelectedValue());
            changing = false;
        });

        llp.addListSelectionListener(arg -> {
            if (changing)
                return;
            changing = true;
            UserPack lod = llp.getSelectedValue();
            pkld.setEnabled(lod != null);
            if (lod != null) {
                pid.setText("ID: " + lod.desc.id);
                parents.setText(get(MainLocale.PAGE, "parent") + ": " + lod.preGetDependencies());
            } else {
                parents.setText("");
            }
            changing = false;
        });

        skld.setLnr(x -> {
            if (changing)
                return;
            changing = true;
            if (!skld.isSelected()) {
                CommonStatic.getConfig().skipLoad.remove(pac.desc.id);
            } else {
                LinkedList<BasisLU> ls = new LinkedList<>();
                for (BasisLU b : BasisLU.allLus())
                    for (AbForm[] afs : b.lu.fs) {
                        boolean bl = false;
                        for (AbForm af : afs)
                            if (af != null && af.getID().pack.equals(pac.desc.id)) {
                                bl = ls.add(b);
                                break;
                            } else if (af == null)
                                break;
                        if (bl)
                            break;
                    }
                if (ls.isEmpty()) {
                    LinkedList<UserPack> children = new LinkedList<>();
                    for (UserPack p : UserProfile.getUserPacks())
                        if (!CommonStatic.getConfig().skipLoad.contains(p.desc.id) && p.desc.dependency.contains(pac.desc.id))
                            children.add(p);


                    if (children.isEmpty())
                        CommonStatic.getConfig().skipLoad.add(pac.desc.id);
                    else {
                        StringBuilder b = new StringBuilder("<html><table><tr><th>The following packs require this one:</th></tr>");
                        for (UserPack p : children)
                            b.append("<tr><td>").append(p).append("</td></tr>");
                        if (Opts.conf(b.append("Add all of them?</html>").toString())) {
                            CommonStatic.getConfig().skipLoad.add(pac.desc.id);
                            for (UserPack p : children)
                                CommonStatic.getConfig().skipLoad.add(p.desc.id);
                        } else
                            skld.setSelected(false);
                    }
                } else {
                    skld.setSelected(false);
                    StringBuilder b = new StringBuilder("<html><table><tr><th>Can't skip because it'd ruin the following lineups:</th></tr>");
                    for (BasisLU obj : ls)
                        b.append("<tr><td>").append(obj.name).append("</td></tr>");
                    Opts.pop(b.append("</html>").toString(),"Units used in LineUp");
                }
            }
            changing = false;
        });

        pkld.setLnr(a -> new Thread(new Runnable() {
            public int inter = 0;
            @SuppressWarnings("BusyWait")
            @Override
            public void run() {
                LinkedList<UserPack> ps = new LinkedList<>();
                UserPack p = llp.getSelectedValue();
                ps.add(p);
                for (String s : p.preGetDependencies())
                    if (UserProfile.profile().skipped.containsKey(s))
                        ps.add(UserProfile.profile().skipped.get(s));

                getBackButton().setEnabled(false);
                fireDimensionChanged();

                DownloadProgressFrame frame = new DownloadProgressFrame("Reading Packs",
                        "Packs: " + ps, "Reading " + p);
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

                ((MainBCU.AdminContext)CommonStatic.ctx).loadProg = (pair -> {
                    frame.setProgress(pair.getFirst());
                    frame.text_below.setHtmlText(pair.getSecond());
                });

                while (changing) {
                    long m = System.currentTimeMillis();
                    try {
                        int delay = (int) (System.currentTimeMillis() - m);
                        inter = (inter * 9 + 100 * delay / Timer.fps) / 10;
                        int sle = delay >= Timer.fps ? 1 : Timer.fps - delay;
                        Thread.sleep(sle);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                UserProfile.loadPacks(ps);
                getBackButton().setEnabled(true);

                llp.clearSelection();
                jlp.setListData(new Vector<>(UserProfile.getUserPacks()));
                llp.setListData(new Vector<>(UserProfile.profile().skipped.values()));
                frame.dispose();
            }
        }).start());
    }

    private void setPack(UserPack pack) {
        pac = pack;
        skld.setEnabled(pac != null);
        skld.setSelected(pac != null && CommonStatic.getConfig().skipLoad.contains(pac.getSID()));
    }
}
