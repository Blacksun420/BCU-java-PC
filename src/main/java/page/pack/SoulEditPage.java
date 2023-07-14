package page.pack;

import common.pack.Identifier;
import common.pack.PackData;
import common.pack.Source;
import common.pack.UserProfile;
import common.util.anim.AnimCE;
import common.util.pack.Soul;
import common.util.stage.Music;
import main.Opts;
import page.*;
import page.awt.BBBuilder;
import page.support.AnimLCR;
import page.support.SoulLCR;
import page.view.ViewBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Comparator;
import java.util.Vector;
import java.util.stream.Collectors;

public class SoulEditPage extends Page {

    private static final long serialVersionUID = 1L;

    private final JBTN back = new JBTN(0, "back");
    private final JBTN adds = new JBTN(0, "add");
    private final JBTN rems = new JBTN(0, "rem");
    private final JBTN srea = new JBTN(0, "reassign");

    private final JComboBox<String> lbp = new JComboBox<>();
    private final JL lbs = new JL(0, "soul");
    private final JL lbd = new JL(0, "seleanim");

    private final JTF jtfs = new JTF();

    private final Vector<PackData.UserPack> vpack = new Vector<>(UserProfile.getUserPacks().stream().filter(p -> p.souls.size() > 0 || p.editable).collect(Collectors.toList()));
    private final PackEditPage.PackList jlp = new PackEditPage.PackList(vpack);
    private final JScrollPane jspp = new JScrollPane(jlp);
    private final JList<Soul> jls = new JList<>();
    private final JScrollPane jsps = new JScrollPane(jls);

    private final JList<AnimCE> jld = new JList<>(new Vector<>(AnimCE.map().values().stream().filter(a -> a.id.base.equals(Source.BasePath.SOUL)).collect(Collectors.toList())));
    private final JComboBox<Music> jcbm = new JComboBox<>();
    private final JScrollPane jspd = new JScrollPane(jld);
    private final ViewBox vb = BBBuilder.def.getViewBox();

    private PackData.UserPack pac;
    private Soul soul;
    private boolean changing = false, unsorted = true;

    public SoulEditPage(Page p, PackData.UserPack pack) {
        super(p);
        vpack.sort(null);

        ini(pack != null && (pack.souls.size() > 0 || pack.editable) ? pack : null);
        resized();
    }

    @Override
    public JButton getBackButton() {
        return back;
    }

    @Override
    protected void renew() {
        setPack(pac);
    }

    @Override
    protected void mouseDragged(MouseEvent e) {
        if (e.getSource() == vb)
            vb.mouseDragged(e);
    }
    @Override
    protected void mousePressed(MouseEvent e) {
        if (e.getSource() == vb)
            vb.mousePressed(e);
    }
    @Override
    protected void mouseReleased(MouseEvent e) {
        if (e.getSource() == vb)
            vb.mouseReleased(e);
    }
    @Override
    protected void mouseWheel(MouseEvent e) {
        if (!(e.getSource() instanceof ViewBox))
            return;
        MouseWheelEvent mwe = (MouseWheelEvent) e;
        double d = mwe.getPreciseWheelRotation();
        vb.resize(Math.pow(0.95, d));
    }

    @Override
    public void timer(int f) {
        vb.update();
        vb.paint();
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);

        int w = 50, dw = 150;

        set(lbp, x, y, w, 100, 400, 50);
        set(jspp, x, y, w, 150, 400, 600);

        w += 450;

        set(lbs, x, y, w, 100, 300, 50);
        set(jsps, x, y, w, 150, 300, 600);
        set(srea, x, y, w, 750, 300, 50);
        set(adds, x, y, w, 800, 150, 50);
        set(rems, x, y, w + dw, 800, 150, 50);
        set(jtfs, x, y, w, 850, 300, 50);
        set(jcbm, x, y, w, 900, 300, 50);

        w += 300;

        set(lbd, x, y, w, 100, 300, 50);
        set(jspd, x, y, w, 150, 300, 600);

        w += 350;
        set((Canvas)vb, x, y, w, 150, 1000, 600);
    }

    private void addListeners() {
        back.setLnr(x -> changePanel(getFront()));

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
                    Vector<PackData.UserPack> vpack2 = new Vector<>(UserProfile.getUserPacks());
                    vpack.clear();
                    vpack.addAll(vpack2); //Dunno a more efficient way to unsort a list
                    break;
                case 1:
                    vpack.sort(null);
                    break;
                case 2:
                    vpack.sort(Comparator.comparing(PackData.UserPack::getSID));
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

    private void ini(PackData.UserPack pack) {
        add(back);

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

    private void setPack(PackData.UserPack pack) {
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
            vb.setEntity(s.getEAnim(s.anim.types()[0]));
        }

        boolean editable = s != null && pac.editable;
        rems.setEnabled(editable);
        srea.setEnabled(editable && jld.getSelectedValue() != null);
        jcbm.setEnabled(editable);
        jtfs.setEnabled(editable);

        changing = boo;
    }
}
