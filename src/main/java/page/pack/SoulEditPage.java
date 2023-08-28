package page.pack;

import common.pack.Identifier;
import common.pack.PackData.UserPack;
import common.pack.Source;
import common.pack.UserProfile;
import common.util.anim.AnimCE;
import common.util.anim.AnimU;
import common.util.pack.Soul;
import common.util.stage.Music;
import main.Opts;
import page.*;
import page.anim.ImgCutEditPage;
import page.support.AnimLCR;
import page.support.SoulLCR;
import page.view.AbViewPage;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.Vector;
import java.util.stream.Collectors;

public class SoulEditPage extends AbViewPage {

    private static final long serialVersionUID = 1L;

    private final JBTN adds = new JBTN(0, "add");
    private final JBTN rems = new JBTN(0, "rem");
    private final JBTN srea = new JBTN(0, "reassign");

    private final JComboBox<String> lbp = new JComboBox<>();
    private final JL lbs = new JL(0, "soul");
    private final JL lbd = new JL(0, "seleanim");

    private final JTF jtfs = new JTF();

    private final Vector<UserPack> vpack = new Vector<>(UserProfile.getUserPacks().stream().filter(p -> p.souls.size() > 0 || p.editable).collect(Collectors.toList()));
    private final PackEditPage.PackList jlp = new PackEditPage.PackList(vpack);
    private final JScrollPane jspp = new JScrollPane(jlp);
    private final JList<Soul> jls = new JList<>();
    private final JScrollPane jsps = new JScrollPane(jls);

    private final JList<AnimCE> jld = new JList<>(new Vector<>(AnimCE.map().values().stream().filter(a -> a.id.base.equals(Source.BasePath.SOUL)).collect(Collectors.toList())));
    private final JComboBox<Music> jcbm = new JComboBox<>();
    private final JScrollPane jspd = new JScrollPane(jld);

    private UserPack pac;
    private Soul soul;
    private boolean changing = false, unsorted = true;

    public SoulEditPage(Page p, UserPack pack) {
        super(p);
        vpack.sort(null);
        cx += 150;

        ini(pack != null && (pack.souls.size() > 0 || pack.editable) ? pack : null);
        resized(true);
    }

    @Override
    protected void renew() {
        setPack(pac);
    }

    @Override
    protected void updateChoice() {
        setSoul(soul);
    }

    @Override
    protected void resized(int x, int y) {
        super.resized(x, y);
        if (!larges.isSelected()) {
            cx = 50;

            set(lbp, x, y, cx, 100, 400, 50);
            set(jspp, x, y, cx, 150, 400, 600);

            cx += 450;

            set(lbs, x, y, cx, 100, 300, 50);
            set(jsps, x, y, cx, 150, 300, 600);
            set(srea, x, y, cx, 750, 300, 50);
            set(adds, x, y, cx, 800, 150, 50);
            set(rems, x, y, cx + 150, 800, 150, 50);
            set(jtfs, x, y, cx, 850, 300, 50);
            set(jcbm, x, y, cx, 900, 300, 50);

            cx += 300;

            set(lbd, x, y, cx, 100, 300, 50);
            set(jspd, x, y, cx, 150, 300, 600);

            cx += 350;
        } else {
            set(lbp, x, y, 50, 100, 400, 50);
            set(jspp, x, y, 50, 150, 400, 350);

            set(lbs, x, y, 50, 800, 400, 50);
            set(jsps, x, y, 50, 850, 400, 400);
            set(srea, 0, 0, 0, 0, 0, 0);
            set(adds, 0, 0, 0, 0, 0, 0);
            set(rems, 0, 0, 0, 0, 0, 0);
            set(jtfs, 0, 0, 0, 0, 0, 0);
            set(jcbm, 0, 0, 0, 0, 0, 0);
            set(lbd, 0, 0, 0, 0, 0, 0);
            set(jspd, 0, 0, 0, 0, 0, 0);
        }
    }

    private void addListeners() {
        jls.addListSelectionListener(x -> {
            if (changing || jls.getValueIsAdjusting())
                return;
            changing = true;
            setSoul(jls.getSelectedValue());
            changing = false;
        });

        jlp.addListSelectionListener(arg0 -> {
            if (changing || jlp.getValueIsAdjusting())
                return;
            changing = true;
            setPack(jlp.getSelectedValue());
            changing = false;
        });

        jld.addListSelectionListener(x -> {
            if (jld.getValueIsAdjusting())
                return;
            boolean editable = pac != null && pac.editable;
            boolean selected = jld.getSelectedValue() != null && jld.getSelectedValue().id.base.equals(Source.BasePath.SOUL);
            adds.setEnabled(editable && selected);
            rems.setEnabled(editable);
            srea.setEnabled(editable && selected);
        });

        copy.addActionListener(e -> {
            boolean change = false;
            UserPack pack = jlp.getSelectedValue();
            if (pack == null)
                return;

            for (Soul sl : jls.getSelectedValuesList()) {
                if (change || pack.editable || pack.desc.allowAnim) {
                    change = true;
                    copyAnim(sl.anim);
                } else {
                    String pass = Opts.read("Enter " + pack + "'s password:");
                    if (pass == null)
                        return;
                    else if (((Source.ZipSource) pack.source).zip.matchKey(pass)) {
                        change = true;
                        copyAnim(sl.anim);
                    } else {
                        Opts.pop("That's not the password", "Incorrect password");
                        return;
                    }
                }
            }

            if (change)
                changePanel(new ImgCutEditPage(getThis()));
        });
    }

    private void copyAnim(AnimU<?> eau) {
        Source.ResourceLocation rl = new Source.ResourceLocation(Source.ResourceLocation.LOCAL, "new anim", Source.BasePath.ANIM);
        Source.Workspace.validate(rl);
        new AnimCE(rl, eau);
    }

    private void addListeners$1() {
        srea.addActionListener(x -> {
            if (jls.getSelectedValue() == null || jld.getSelectedValue() == null)
                return;

            Soul s = jls.getSelectedValue();
            if (s.anim == null || (jld.getSelectedValue() != s.anim && Opts.conf(get(MainLocale.PAGE, "reasanim")))) {
                changing = true;

                s.anim = jld.getSelectedValue();
                changing = false;
            }
        });

        adds.addActionListener(x -> {
            changing = true;
            Soul s = new Soul(pac.getNextID(Soul.class), jld.getSelectedValue());
            pac.souls.add(s);
            jls.setListData(pac.souls.toRawArray());
            jls.setSelectedValue(s, true);
            setSoul(s);
            changing = false;
        });

        rems.addActionListener(x -> {
            if (!Opts.conf())
                return;

            changing = true;
            int ind = jls.getSelectedIndex();
            pac.souls.remove(soul);
            jls.setListData(pac.souls.toRawArray());
            if (ind >= 0)
                ind--;
            jls.setSelectedIndex(ind);
            setSoul(jls.getSelectedValue());
            changing = false;
        });
    }

    private void addListeners$2() {
        jtfs.setLnr(x -> soul.name = jtfs.getText().trim());

        jcbm.addActionListener(x -> {
            if (changing || soul == null)
                return;

            changing = true;

            Music m = (Music) jcbm.getSelectedItem();
            soul.audio = m != null ? m.getID() : null;

            changing = false;
        });

        lbp.addActionListener(j -> {
            int method = lbp.getSelectedIndex();
            switch (method) {
                case 0:
                    if (unsorted)
                        return;
                    Vector<UserPack> vpack2 = new Vector<>(UserProfile.getUserPacks());
                    vpack.clear();
                    vpack.addAll(vpack2); //Dunno a more efficient way to unsort a list
                    break;
                case 1:
                    vpack.sort(null);
                    break;
                case 2:
                    vpack.sort(Comparator.comparing(UserPack::getSID));
                    break;
                case 3:
                    vpack.sort(Comparator.comparing(p -> p.desc.getAuthor()));
                    break;
                case 4:
                    vpack.sort(Comparator.comparing(p -> p.desc.BCU_VERSION));
                    vpack.sort(Comparator.comparingInt(p -> p.desc.FORK_VERSION));
                    break;
                case 5:
                    vpack.sort(Comparator.comparingLong(p -> p.desc.getTimestamp("cdate")));
                    break;
                case 6:
                    vpack.sort(Comparator.comparingLong(p -> p.desc.getTimestamp("edate")));
                    break;
                case 7:
                    vpack.sort(Comparator.comparingInt(p -> p.enemies.size()));
                    break;
                case 8:
                    vpack.sort(Comparator.comparingInt(p -> p.units.size()));
                    break;
                case 9:
                    vpack.sort(Comparator.comparingInt(p -> p.mc.getStageCount()));
                    break;
            }
            jlp.setListData(vpack);
            unsorted = method == 0;
            jlp.setSelectedValue(pac, true);
        });
    }

    private void ini(UserPack pack) {
        preini();
        remove(jspt);
        add(lbp);
        add(jspp);

        add(lbs);
        add(jsps);
        add(lbd);
        add(jspd);

        add(srea);
        add(adds);
        add(rems);

        add(jtfs);
        add(jcbm);

        add((Canvas)vb);
        jls.setCellRenderer(new SoulLCR());
        jld.setCellRenderer(new AnimLCR());
        lbp.setModel(new DefaultComboBoxModel<>(get(MainLocale.PAGE, "psort", 10)));

        setPack(pack);
        addListeners();
        addListeners$1();
        addListeners$2();
    }

    private void setPack(UserPack pack) {
        pac = pack;
        boolean boo = changing;
        boolean exists = pac != null;
        changing = true;
        if (jlp.getSelectedValue() != pack)
            jlp.setSelectedValue(pac, true);

        if (exists) {
            jls.setListData(pac.souls.toRawArray());

            Vector<Music> vs = new Vector<>();
            vs.add(null);
            vs.addAll(UserProfile.getAll(pac.getSID(), Music.class));
            jcbm.setModel(new DefaultComboBoxModel<>(vs));
        } else {
            jls.setListData(new Soul[0]);
            jcbm.removeAllItems();
        }

        boolean editable = exists && pac.editable;
        boolean selected = jld.getSelectedValue() != null;
        adds.setEnabled(editable && selected);
        rems.setEnabled(editable && soul != null);
        srea.setEnabled(editable && soul != null);
        jcbm.setEnabled(editable && soul != null);

        if (!exists || !pac.souls.contains(soul))
            soul = null;
        setSoul(soul);

        changing = boo;
    }

    private void setSoul(Soul s) {
        soul = s;
        boolean boo = changing;
        changing = true;
        if (jls.getSelectedValue() != s)
            jls.setSelectedValue(s, true);

        if (s != null) {
            jtfs.setText(soul.name);
            jcbm.setSelectedItem(Identifier.get(s.audio));
            setAnim(s.anim);
        }

        boolean editable = s != null && pac.editable;
        rems.setEnabled(editable);
        srea.setEnabled(editable && jld.getSelectedValue() != null);
        jcbm.setEnabled(editable);
        jtfs.setEnabled(editable);

        changing = boo;
    }
}
