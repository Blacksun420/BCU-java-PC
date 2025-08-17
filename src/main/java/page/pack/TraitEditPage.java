package page.pack;

import common.CommonStatic;
import common.pack.Context;
import common.pack.Source;
import common.util.unit.*;
import common.pack.PackData.UserPack;
import common.pack.FixIndexList.FixIndexMap;
import main.MainBCU;
import main.Opts;
import page.*;
import page.info.filter.TraitList;
import page.info.filter.UnitFindPage;
import page.support.AnimLCR;
import page.support.DropParser;
import page.support.Importer;
import page.support.ReorderList;
import utilpc.UtilPC;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TraitEditPage extends DefaultPage {

    private static final long serialVersionUID = 1L;

    private final TraitList jlct = new TraitList(true);
    private final JScrollPane jspct = new JScrollPane(jlct);

    private final JLabel jl = new JLabel();

    private final JBTN addct = new JBTN(MainLocale.PAGE, "add");
    private final JBTN remct = new JBTN(MainLocale.PAGE, "rem");
    private final JBTN adicn = new JBTN(MainLocale.PAGE, "icon");
    private final JBTN reicn = new JBTN(MainLocale.PAGE, "remicon");
    private final JBTN addu = new JBTN(MainLocale.PAGE, "add");
    private final JBTN remu = new JBTN(MainLocale.PAGE, "rem");
    private final JBTN vuif = new JBTN(MainLocale.PAGE, "vuif");
    private final JTG altrg = new JTG(MainLocale.PAGE, "traitfect");
    private final JL adv = new JL(MainLocale.PAGE, "advtrt");
    private final JTF ctrna = new JTF();

    private final ReorderList<AbForm> jlf = new ReorderList<>();
    private final JScrollPane jspf = new JScrollPane(jlf);
    private final ReorderList<Form> tlf = new ReorderList<>();
    private final JScrollPane tspf = new JScrollPane(tlf);

    private final UserPack pack;
    private final FixIndexMap<Trait> pct;
    private UnitFindPage ufp;

    private boolean changing = false;
    private final boolean editable;
    private Trait t;

    public TraitEditPage(Page p, UserPack pac) {
        super(p);
        pack = pac;
        pct = pac.traits;
        editable = pac.editable;
        ini();
    }

    @Override
    protected void renew() {
        if (ufp != null && ufp.getList() != null) {
            changing = true;
            ArrayList<AbForm> list = new ArrayList<>(ufp.getList());
            if (t != null) {
                list.removeAll(t.targetForms);
                for (Unit u : pack.units)
                    for (Form f : u.forms)
                        list.remove(f);
                list.removeIf(f -> ((Form) f).maxu().getTraits(false).isEmpty() || (t.targetType && Trait.targetTraited(((Form) f).maxu().getTraits(false))));
            } else
                list.clear();

            jlf.setListData(list.toArray(new AbForm[0]));
            jlf.clearSelection();
            if (!list.isEmpty())
                jlf.setSelectedIndex(0);
            else
                jlf.clearSelection();
            changing = false;
        }
    }

    @Override
    protected synchronized void resized(int x, int y) {
        super.resized(x, y);
        set(addct, x, y, 50, 1000, 150, 50);
        set(remct, x, y, 200, 1000, 150, 50);
        set(jspct, x, y, 50, 100, 300, 800);
        set(altrg, x, y, 50, 950, 300, 50);
        set(ctrna, x, y, 50, 900, 300, 50);
        set(adicn, x, y, 350, 100, 250, 50);
        set(jl, x, y, 350, 150, 250, 250);
        set(reicn, x, y, 350, 400, 250, 50);
        set(adv, x, y, 650, 50, 300, 50);
        set(jspf, x, y, 650, 100, 300, 800);
        set(vuif, x, y, 650, 900, 300, 50);
        set(addu, x, y, 650, 950, 150, 50);
        set(remu, x, y, 800, 950, 150, 50);
        set(tspf, x, y, 1000, 100, 300, 800);
    }

    private void addListeners$0() {
        adicn.addActionListener(arg0 -> getFile("Choose your file"));

        reicn.addActionListener(arg0 -> {
            File file = ((Source.Workspace) pack.source).getTraitIconFile(t.id);
            if (file.delete()) {
                t.icon = null;
                jl.setIcon(null);
                reicn.setEnabled(false);
                jlct.revalidate();
                jlct.repaint();
            }
        });

    }

    private void addListeners$CG() {
        addct.addActionListener(arg0 -> {
            changing = true;
            t = new Trait(pack.getNextID(Trait.class));
            pct.add(t);
            updateCTL();
            jlct.setSelectedValue(t, true);
            changing = false;
        });

        remct.addActionListener(arg0 -> {
            if (t == null)
                return;
            changing = true;
            List<Trait> list = pct.getList();
            int ind = list.indexOf(t) - 1;
            if (ind < 0 && list.size() > 1)
                ind = 0;
            File file = ((Source.Workspace) pack.source).getTraitIconFile(t.id);
            if (file.exists()) {
                if (!file.delete()) {
                    Opts.warnPop("Failed to delete file : " + file.getAbsolutePath(), "Delete Failed");
                }
            }
            list.remove(t);
            pct.remove(t);
            if (ind >= 0)
                t = list.get(ind);
            else
                t = null;
            updateCTL();
            changing = false;
        });

        altrg.addActionListener(arg0 -> {
            if (t == null)
                return;
            changing = true;
            t.targetType = !t.targetType;
            if (t.targetType)
                t.targetForms.removeIf(f -> Trait.targetTraited(f.maxu().getTraits(false)));
            updateCTL();
            changing = false;
        });

        jlct.addListSelectionListener(arg0 -> {
            if (changing || jlct.getValueIsAdjusting())
                return;
            changing = true;
            t = jlct.getSelectedValue();
            updateCT();
            changing = false;
        });

        ctrna.setLnr(x -> {
            String str = ctrna.getText();
            if (t.name.equals(str))
                return;
            if (str.isEmpty()) {
                ctrna.setText(t.name);
                return;
            }
            t.name = str;
            jlct.revalidate();
            jlct.repaint();
        });

        addu.addActionListener(arg0 -> {
            List<AbForm> formList = jlf.getSelectedValuesList();
            if (changing || jlct.getValueIsAdjusting())
                return;
            changing = true;
            t.targetForms.addAll(formList);
            updateCT();
            changing = false;
        });

        remu.addActionListener(arg0 -> {
            List<Form> formList = tlf.getSelectedValuesList();
            if (changing || jlct.getValueIsAdjusting())
                return;
            changing = true;
            formList.forEach(t.targetForms::remove);
            updateCT();
            changing = false;
        });

        jlf.addListSelectionListener(l -> addu.setEnabled(editable && jlf.getSelectedIndex() != -1));
        tlf.addListSelectionListener(l -> remu.setEnabled(editable && tlf.getSelectedIndex() != -1));

        vuif.addActionListener(arg0 -> {
            if (ufp == null)
                ufp = new UnitFindPage(getThis(), false, pack);
            changePanel(ufp);
        });

        jlf.addListSelectionListener(l -> {
            boolean b = editable && t != null && jlf.getSelectedIndex() != -1;
            addu.setEnabled(b);
            remu.setEnabled(b);
        });
    }

    private void updateCTL() {
        jlct.setListData(pct.toArray());
        jlct.setSelectedValue(t, true);
        updateCT();
    }

    private void updateCT() {
        altrg.setEnabled(t != null && editable);
        remct.setEnabled(t != null && !Trait.isUsed(t) && editable);
        ctrna.setEnabled(t != null && editable);
        adicn.setEnabled(t != null && editable);
        setIconImage(t);
        ctrna.setText("");
        if (t != null) {
            ctrna.setText(t.name);
            altrg.setSelected(t.targetType);
            tlf.setListData(t.targetForms.toArray(new Form[0]));
            jl.setIcon(t.icon != null ? UtilPC.resizeIcon(t.icon, 200, 200) : null);
        } else {
            tlf.clearSelection();
            tlf.setListData(new Form[0]);
        }
        renew();
        reicn.setEnabled(t != null && t.icon != null && editable);
        boolean b = editable && t != null && jlf.getSelectedIndex() != -1;
        addu.setEnabled(b);
        remu.setEnabled(b);
    }

    private void ini() {
        add(addct);
        add(remct);
        add(jspct);
        add(altrg);
        add(ctrna);
        add(adicn);
        add(jl);
        add(reicn);
        add(adv);
        add(addu);
        add(remu);
        add(vuif);
        add(jspf);
        add(tspf);
        addct.setEnabled(editable);
        jlf.setCellRenderer(new AnimLCR());
        tlf.setCellRenderer(new AnimLCR());
        jl.setIcon(null);
        jl.setVerticalAlignment(SwingConstants.CENTER);
        jl.setHorizontalAlignment(SwingConstants.CENTER);
        addListeners$0();
        addListeners$CG();
        updateCTL();

        jl.setDropTarget(new DropParser() {
            @Override
            public boolean process(File f) {
                if (t == null)
                    return false;
                return setTraitIcon(getImg());
            }
        });
    }

    private void getFile(String str) {
        BufferedImage bimg = new Importer(str, Importer.IMP_IMG).getImg();
        if (bimg == null)
            return;
        setTraitIcon(bimg);
    }

    private boolean setTraitIcon(BufferedImage bimg) {
        if (bimg.getWidth() != bimg.getHeight()) {
            getFile(get(MainLocale.PAGE, "sqrwrn"));
            return false;
        }
        bimg = UtilPC.resizeImage(bimg, 41, 41);

        if (t.icon != null)
            t.icon.setImg(MainBCU.builder.build(bimg));
        else
            t.icon = MainBCU.builder.toVImg(bimg);
        try {
            File file = ((Source.Workspace) pack.source).getTraitIconFile(t.id);
            Context.check(file);
            ImageIO.write(bimg, "PNG", file);
        } catch (IOException e) {
            CommonStatic.ctx.noticeErr(e, Context.ErrType.WARN, "failed to write file");
            getFile("Failed to save file");
            return false;
        }
        jlct.revalidate();
        jlct.repaint();
        updateCT();
        setIconImage(jlct.getSelectedValue());
        return true;
    }

    private void setIconImage(Trait slt) {
        if (t == null)
            return;
        if (jlct.getSelectedValue() != slt) {
            changing = true;
            jlct.setSelectedValue(slt, true);
            changing = false;
        }
    }
}