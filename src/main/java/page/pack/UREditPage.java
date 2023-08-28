package page.pack;

import common.CommonStatic;
import common.battle.BasisSet;
import common.pack.Context;
import common.pack.PackData.UserPack;
import common.pack.Source;
import common.pack.UserProfile;
import common.util.unit.AbForm;
import common.util.unit.Form;
import common.util.unit.UniRand;
import common.util.unit.Unit;
import main.MainBCU;
import main.Opts;
import page.*;
import page.info.filter.UnitFindPage;
import page.support.AnimLCR;
import page.support.Importer;
import utilpc.UtilPC;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import static utilpc.UtilPC.resizeImage;

public class UREditPage extends DefaultPage {

    private static final long serialVersionUID = 1L;

    public static void redefine() {
        UREditTable.redefine();
    }

    public final BasisSet bas = BasisSet.current();
    private final JBTN veif = new JBTN(0, "veif");
    private final UREditTable jt;
    private final JScrollPane jspjt;
    private final JList<UniRand> jlst = new JList<>();
    private final JScrollPane jspst = new JScrollPane(jlst);
    private final JBTN adds = new JBTN(MainLocale.PAGE, "add");
    private final JBTN rems = new JBTN(MainLocale.PAGE, "rem");
    private final JBTN addl = new JBTN(MainLocale.PAGE, "addl");
    private final JBTN reml = new JBTN(MainLocale.PAGE, "reml");
    private final JBTN adicn = new JBTN(MainLocale.PAGE, "icon");
    private final JBTN reicn = new JBTN(MainLocale.PAGE, "remicon");
    private final JBTN aduni = new JBTN(MainLocale.PAGE, "icon");
    private final JBTN reuni = new JBTN(MainLocale.PAGE, "remicon");

    private final JL cost = new JL(MainLocale.INFO, "price");
    private final JTF jost = new JTF();
    private final JL cd = new JL(MainLocale.INFO, "cdo");
    private final JTF jd = new JTF();
    private final JLabel uni = new JLabel();
    private final JList<AbForm> jlu = new JList<>();
    private final JScrollPane jspe = new JScrollPane(jlu);
    private final JTF name = new JTF();
    private final JTG[] type = new JTG[3];

    private final UserPack pack;

    private UnitFindPage ufp;

    private UniRand rand;

    public UREditPage(Page p, UserPack pac) {
        super(p);
        pack = pac;
        List<Unit> uni = UserProfile.getBCData().units.getRawList();
        uni.addAll(pac.units.getList());
        ArrayList<Form> forms = new ArrayList<>();
        for (Unit unit : uni)
            Collections.addAll(forms, unit.forms);
        jlu.setListData(forms.toArray(new Form[0]));
        jt = new UREditTable(this, pac);
        jspjt = new JScrollPane(jt);
        ini();
        resized(true);
    }

    public UREditPage(Page p, UserPack pac, UniRand u) {
        this(p, pac);
        jlu.setSelectedValue(u, true);
    }

    @Override
    protected void mouseClicked(MouseEvent e) {
        int modifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if (e.getSource() == jt && (e.getModifiers() & modifier) == 0)
            jt.clicked(e.getPoint());
    }

    @Override
    protected void renew() {
        if (ufp != null && ufp.getList() != null)
            jlu.setListData(new Vector<>(ufp.getList()));
    }

    @Override
    protected synchronized void resized(int x, int y) {
        super.resized(x, y);

        set(jspst, x, y, 500, 150, 400, 800);
        set(adds, x, y, 500, 1000, 200, 50);
        set(rems, x, y, 700, 1000, 200, 50);
        set(name, x, y, 500, 1100, 400, 50);
        set(veif, x, y, 950, 100, 400, 50);
        set(jspe, x, y, 950, 150, 400, 1100);
        set(jspjt, x, y, 1400, 450, 850, 800);
        set(adicn, x, y, 200, 150, 200, 50);
        set(reicn, x, y, 200, 250, 200, 50);
        set(aduni, x, y, 200, 500, 200, 50);
        set(reuni, x, y, 200, 700, 200, 50);
        set(uni, x, y, 225, 550, 150, 150);

        set(jost, x, y, 150, 350, 100, 50);
        set(cost, x, y, 250, 350, 200, 50);
        set(jd, x, y, 150, 400, 100, 50);
        set(cd, x, y, 250, 400, 200, 50);

        for (int i = 0; i < 3; i++)
            set(type[i], x, y, 1550 + 250 * i, 250, 200, 50);
        set(addl, x, y, 1800, 350, 200, 50);
        set(reml, x, y, 2050, 350, 200, 50);
        jt.setRowHeight(size(x, y, 50));
        jlu.setFixedCellHeight(size(x, y, 50));
    }

    private void addListeners() {
        addl.addActionListener(arg0 -> {
            int ind = jt.addLine(jlu.getSelectedValue());
            setUR(rand);
            if (ind < 0)
                jt.clearSelection();
            else
                jt.addRowSelectionInterval(ind, ind);
        });

        reml.addActionListener(arg0 -> {
            int ind = jt.remLine();
            setUR(rand);
            if (ind < 0)
                jt.clearSelection();
            else
                jt.addRowSelectionInterval(ind, ind);
        });

        veif.addActionListener(arg0 -> {
            if (ufp == null)
                ufp = new UnitFindPage(getThis(), true, pack);
            changePanel(ufp);
        });

        jlst.addListSelectionListener(arg0 -> {
            if (isAdj() || arg0.getValueIsAdjusting())
                return;
            setUR(jlst.getSelectedValue());
        });

        adds.addActionListener(arg0 -> {
            rand = new UniRand(pack.getNextID(UniRand.class));
            pack.randUnits.add(rand);
            change(null, p -> {
                jlst.setListData(pack.randUnits.toArray());
                jlst.setSelectedValue(rand, true);
                setUR(rand);
            });

        });

        rems.addActionListener(arg0 -> {
            if (!Opts.conf())
                return;
            int ind = jlst.getSelectedIndex() - 1;
            if (ind < 0)
                ind = -1;
            if (rand.icon != null) {
                File file = ((Source.Workspace) pack.source).getRandIconFile("unitDisplayIcons", rand.id);
                file.delete();
            }
            if (rand.deployIcon != null) {
                File file = ((Source.Workspace) pack.source).getRandIconFile("unitDeployIcons", rand.id);
                file.delete();
            }
            pack.randUnits.remove(rand);
            change(ind, IND -> {
                List<UniRand> l = pack.randUnits.getList();
                jlst.setListData(l.toArray(new UniRand[0]));

                if (IND < l.size())
                    jlst.setSelectedIndex(IND);
                else
                    jlst.setSelectedIndex(l.size() - 1);
                setUR(jlst.getSelectedValue());
            });
        });

        name.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent fe) {
                if (rand == null)
                    return;
                rand.name = name.getText().trim();
                setUR(rand);
            }

        });

        jost.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent fe) {
                if (rand == null)
                    return;
                rand.price = (int) (CommonStatic.parseIntN(jost.getText().trim()) / 1.5);
                setUR(rand);
            }

        });

        jd.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent fe) {
                if (rand == null)
                    return;
                rand.cooldown = bas.t().getRevRes(CommonStatic.parseIntN(jd.getText().trim()));
                setUR(rand);
            }

        });

        for (byte i = 0; i < 3; i++) {
            byte I = i;
            type[i].addActionListener(arg0 -> {
                if (isAdj() || rand == null)
                    return;
                rand.type = I;
                setUR(rand);
            });
        }

        adicn.addActionListener(arg0 -> getFile("Choose your file (recommended size: 85x32)", false));

        reicn.addActionListener(arg0 -> {
            File file = ((Source.Workspace) pack.source).getRandIconFile("unitDisplayIcons", rand.id);
            if (file.delete()) {
                rand.icon = null;
                reicn.setEnabled(false);
            }
        });

        aduni.addActionListener(arg0 -> getFile("Choose your file (recommended size: 110x85)", true));

        reuni.addActionListener(arg0 -> {
            File file = ((Source.Workspace) pack.source).getRandIconFile("unitDeployIcons", rand.id);
            if (file.delete()) {
                rand.icon = null;
                reuni.setEnabled(false);
            }
        });
    }

    private void ini() {
        add(veif);
        add(adds);
        add(rems);
        add(jspjt);
        add(jspst);
        add(addl);
        add(reml);
        add(jspe);
        add(name);
        add(adicn);
        add(reicn);
        add(aduni);
        add(reuni);
        add(uni);
        add(cost);
        add(jost);
        add(cd);
        add(jd);
        for (int i = 0; i < 3; i++)
            add(type[i] = new JTG(1, "ert" + i));
        setES();
        jlst.setCellRenderer(new AnimLCR());
        jlu.setCellRenderer(new AnimLCR());
        addListeners();

    }

    private void setUR(UniRand er) {
        change(er, st -> {
            boolean b = st != null && pack.editable;
            rems.setEnabled(b);
            addl.setEnabled(b);
            reml.setEnabled(b);
            name.setEnabled(b);
            adicn.setEnabled(b);
            reicn.setEnabled(b && st.icon != null);
            aduni.setEnabled(b);
            reuni.setEnabled(b && st.deployIcon != null);
            jt.setEnabled(b);
            for (JTG btn : type)
                btn.setEnabled(b);
            rand = st;
            jt.setData(st);
            name.setText(st == null ? "" : rand.name);
            uni.setIcon(st == null ? null : UtilPC.getIcon(st.getDeployIcon()));
            int t = st == null ? -1 : st.type;
            for (int i = 0; i < 3; i++)
                type[i].setSelected(i == t);
            if (t != -1) {
                jost.setText("" + st.price * 1.5);
                jd.setText("" + bas.t().getFinRes(st.cooldown));
            }
            jspjt.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        });
        resized(true);
    }

    private void setES() {
        if (pack == null) {
            jlst.setListData(new UniRand[0]);
            setUR(null);
            adds.setEnabled(false);
            return;
        }
        adds.setEnabled(pack.editable);
        List<UniRand> l = pack.randUnits.getList();
        jlst.setListData(l.toArray(new UniRand[0]));
        if (l.size() == 0) {
            jlst.clearSelection();
            setUR(null);
            return;
        }
        jlst.setSelectedIndex(0);
        setUR(pack.randUnits.getList().get(0));
    }

    private void getFile(String str, boolean uni) {
        BufferedImage bimg = new Importer(str, Importer.IMP_IMG).getImg();
        if (bimg == null)
            return;
        if (uni) {
            bimg = resizeImage(bimg, 110, 85);

            if (rand.deployIcon != null)
                rand.deployIcon.setImg(MainBCU.builder.build(bimg));
            else
                rand.deployIcon = MainBCU.builder.toVImg(bimg);
            try {
                File file = ((Source.Workspace) pack.source).getRandIconFile("unitDeployIcons", rand.id);
                Context.check(file);
                ImageIO.write(bimg, "PNG", file);
            } catch (IOException e) {
                CommonStatic.ctx.noticeErr(e, Context.ErrType.WARN, "failed to write file");
                getFile("Failed to save file", true);
                return;
            }
        } else {
            bimg = resizeImage(bimg, 85, 32);

            if (rand.icon != null)
                rand.icon.setImg(MainBCU.builder.build(bimg));
            else
                rand.icon = MainBCU.builder.toVImg(bimg);
            try {
                File file = ((Source.Workspace) pack.source).getRandIconFile("unitDisplayIcons", rand.id);
                Context.check(file);
                ImageIO.write(bimg, "PNG", file);
            } catch (IOException e) {
                CommonStatic.ctx.noticeErr(e, Context.ErrType.WARN, "failed to write file");
                getFile("Failed to save file", true);
                return;
            }
        }
        setIconImage(jlst.getSelectedValue());
        setES();
    }

    private void setIconImage(UniRand slt) {
        if (rand == null)
            return;
        if (jlst.getSelectedValue() != slt) {
            jlst.setSelectedValue(slt, true);
        }
    }
}
