package page.pack;

import common.CommonStatic;
import common.battle.BasisSet;
import common.pack.Identifier;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.unit.AbForm;
import common.util.unit.Combo;
import common.util.unit.Form;
import common.util.unit.Unit;
import page.*;
import page.basis.ComboListTable;
import page.info.filter.UnitFindPage;
import page.support.AnimLCR;
import page.support.ReorderList;
import page.support.UnitLCR;
import utilpc.Interpret;

import javax.swing.*;
import java.util.*;

public class ComboEditPage extends DefaultPage {

    private static final long serialVersionUID = 1L;

    private PackData.UserPack pac;
    private Unit uni;
    private Form frm;

    private final BasisSet b = BasisSet.current();

    private final Vector<PackData.UserPack> vpack = new Vector<>(UserProfile.getUserPacks());
    private final PackEditPage.PackList jlp = new PackEditPage.PackList(vpack);
    private final JScrollPane jspp = new JScrollPane(jlp);
    private final JList<Unit> jlu = new JList<>();
    private final JScrollPane jspu = new JScrollPane(jlu);
    private final ReorderList<Form> jlf = new ReorderList<>();
    private final JScrollPane jspf = new JScrollPane(jlf);
    private final ComboListTable jlc = new ComboListTable(this, b.sele.lu);
    private final JScrollPane jspc = new JScrollPane(jlc);
    private final JComboBox<String> ctypes = new JComboBox<>(Interpret.getComboFilter(0));
    private final JComboBox<String> clvls = new JComboBox<>(Interpret.comboLv);
    private final JTF comboname = new JTF();

    private final JBTN addf = new JBTN(0, "addf");
    private final JBTN addc = new JBTN(0, "addc");
    private final JBTN remcf = new JBTN(0, "remcf");
    private final JBTN remc = new JBTN(0, "remc");

    private final JComboBox<String> lbp = new JComboBox<>();
    private final JL lbu = new JL(0, "unit");
    private final JL lbf = new JL(0, "forms");

    private UnitFindPage ufp;
    private final JBTN vuif = new JBTN(0, "vuif");

    private boolean changing = false, unsorted = true;

    protected ComboEditPage(Page p, PackData.UserPack pack) {
        super(p);
        vpack.sort(null);

        pac = pack;

        ini();
    }

    @Override
    protected void renew() {
        if (ufp != null && ufp.getList() != null) {
            changing = true;
            List<Unit> list = new ArrayList<>();
            for (AbForm f : ufp.getList())
                if (!list.contains((Unit) f.unit()))
                    list.add((Unit) f.unit());
            jlu.setListData(list.toArray(new Unit[0]));
            jlu.clearSelection();
            if (list.size() > 0) {
                changing = false;
                jlu.setSelectedIndex(0);
            }
            ufp = null;
        } else if (pac == null) {
            jlu.setListData(new Unit[0]);
            jlc.clearSelection();
            jlc.setList(new ArrayList<>());
        } else {
            jlf.allowDrag(pac.editable);
            List<Unit> unis = new ArrayList<>();
            for (PackData p : UserProfile.getAllPacks())
                for (Unit u : p.units.getList())
                    if (u.id.pack.equals(Identifier.DEF) || u.id.pack.equals(pac.getSID()) || pac.desc.dependency.contains(u.id.pack))
                        unis.add(u);
            jlu.setListData(unis.toArray(new Unit[0]));
            jlu.clearSelection();
            if (unis.size() > 0)
                jlu.setSelectedIndex(0);
            jlc.setList(pac.combos.getList());
        }

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

    private void ini() {
        add(jspp);
        add(jspu);
        add(vuif);
        add(jspf);

        add(addf);
        add(remc);

        add(lbp);
        add(lbu);
        add(lbf);

        add(addc);
        add(remcf);
        add(ctypes);
        add(clvls);
        add(jspc);
        add(comboname);

        jlu.setCellRenderer(new UnitLCR());
        jlf.setCellRenderer(new AnimLCR());
        jlc.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lbp.setModel(new DefaultComboBoxModel<>(get(MainLocale.PAGE, "psort", 10)));
        ctypes.setEnabled(false);
        clvls.setEnabled(false);

        setPack(pac);

        addListeners$0();
        addListeners$1();
    }

    private void addListeners$0() {
        jlp.addListSelectionListener(arg0 -> {
            if (changing || jlp.getValueIsAdjusting())
                return;
            changing = true;
            jlc.getSelectionModel().setSelectionInterval(0, 0);
            setPack(jlp.getSelectedValue());
            updateC();
            changing = false;
        });

        vuif.addActionListener(arg0 -> {
            if (ufp == null)
                ufp = new UnitFindPage(getThis(), false, pac);
            changePanel(ufp);
        });

        jlu.addListSelectionListener(e -> {
            if (changing || jlu.getValueIsAdjusting())
                return;
            changing = true;
            setUnit(jlu.getSelectedValue());
            changing = false;
        });

        jlf.addListSelectionListener(e -> {
            if (changing || jlf.getValueIsAdjusting())
                return;
            changing = true;
            setForm(jlf.getSelectedValue());
            changing = false;
        });

        addf.addActionListener(x -> {
            if (changing || jlf.getValueIsAdjusting())
                return;
            changing = true;
            Combo combo = jlc.list.get(jlc.getSelectedRow());
            combo.addForm(frm);
            updateC();
            changing = false;
        });

        remcf.addActionListener(x -> {
            if (changing || jlf.getValueIsAdjusting())
                return;
            changing = true;
            Combo combo = jlc.list.get(jlc.getSelectedRow());
            combo.removeForm(jlc.getSelectedColumn() - 4 >= combo.forms.length || jlc.getSelectedColumn() - 4 < 0 ? combo.forms.length - 1 : jlc.getSelectedColumn() - 4);
            updateC();
            changing = false;
        });
    }

    private void addListeners$1() {
        jlc.getSelectionModel().addListSelectionListener(x -> {
            if (changing || x.getValueIsAdjusting())
                return;
            changing = true;
            updateC();
            changing = false;
        });

        addc.addActionListener(x -> {
            if (changing || jlf.getValueIsAdjusting())
                return;
            changing = true;
            Identifier<Combo> id = pac.getNextID(Combo.class);
            Combo combo = new Combo(id, frm);
            pac.combos.add(combo);
            if (!CommonStatic.getConfig().packCombos.containsKey(pac.getSID()))
                CommonStatic.getConfig().packCombos.put(pac.getSID(), true);

            jlc.setList(pac.combos.getRawList());
            jlc.getSelectionModel().setSelectionInterval(0, pac.combos.indexOf(combo));
            updateC();
            changing = false;
        });

        remc.addActionListener(x -> {
            if (changing || jlf.getValueIsAdjusting())
                return;
            changing = true;
            int sel = jlc.getSelectedRow();
            Combo combo = jlc.list.get(sel);
            if (sel > 0)
                sel--;
            jlc.setRowSelectionInterval(sel, sel);
            pac.combos.remove(combo);
            if (pac.combos.size() == 0)
                CommonStatic.getConfig().packCombos.remove(pac.getSID());

            jlc.setList(pac.combos.getRawList());
            updateC();
            changing = false;
        });

        ctypes.addActionListener(x -> {
            if (changing || jlf.getValueIsAdjusting())
                return;
            changing = true;
            Combo combo = jlc.list.get(jlc.getSelectedRow());
            combo.setType(ctypes.getSelectedIndex());
            changing = false;
        });

        clvls.addActionListener(x -> {
            if (changing || jlf.getValueIsAdjusting())
                return;
            changing = true;
            Combo combo = jlc.list.get(jlc.getSelectedRow());
            combo.setLv(clvls.getSelectedIndex());
            changing = false;
        });

        comboname.setLnr(x -> {
            String str = comboname.getText();
            Combo combo = jlc.list.get(jlc.getSelectedRow());
            if (combo.name.equals(str))
                return;
            if (str.equals("")) {
                comboname.setText(combo.name);
                return;
            }
            combo.name = str;
        });

    }

    @Override
    protected void resized(int x, int y) {
        super.resized(x, y);
        set(lbp, x, y, 50, 100, 400, 50);
        set(jspp, x, y, 50, 150, 400, 600);

        set(lbu, x, y, 500, 100, 300, 50);
        set(jspu, x, y, 500, 150, 300, 600);
        set(vuif, x, y, 500, 750, 300, 50);

        set(lbf, x, y, 800, 100, 300, 50);
        set(jspf, x, y, 800, 150, 300, 600);

        set(addf, x, y, 1550, 800, 225, 50);
        set(remcf, x, y, 1775, 800, 225, 50);
        set(comboname, x, y, 1550, 1000, 450, 50);
        set(jspc, x, y, 50, 800, 1450, 450);
        set(addc, x, y, 1550, 850, 450, 50);
        set(remc, x, y, 1550, 900, 300, 50);
        set(ctypes, x, y, 1550, 950, 450, 50);
        set(clvls, x, y, 1850, 900, 150, 50);

        jlc.setRowHeight(50);
        jlc.getColumnModel().getColumn(2).setPreferredWidth(size(x, y, 300));
    }


    private void setPack(PackData.UserPack pack) {
        pac = pack;
        boolean pre = changing;
        changing = true;
        if (jlp.getSelectedValue() != pack)
            jlp.setSelectedValue(pac, true);
        changing = pre;
        renew();
        if (pac == null || !pac.units.contains(uni))
            uni = null;
        setUnit(uni);
    }

    private void setUnit(Unit unit) {
        uni = unit;
        boolean pre = changing;
        changing = true;
        if (jlu.getSelectedValue() != uni)
            jlu.setSelectedValue(uni, true);
        if (unit == null)
            jlf.setListData(new Form[0]);
        else
            jlf.setListData(unit.forms);
        changing = pre;
        if (frm != null && frm.unit != unit)
            frm = null;
        setForm(uni != null ? uni.forms[0] : null);
    }

    private void setForm(Form f) {
        frm = f;
        if (jlf.getSelectedValue() != frm) {
            boolean boo = changing;
            changing = true;
            jlf.setSelectedValue(frm, true);
            changing = boo;
        }
        updateC();
    }

    private void updateC() {
        boolean editable = frm != null && pac.editable;
        addc.setEnabled(editable);
        vuif.setEnabled(pac != null && pac.editable);
        boolean size = pac != null && jlc.getSelectedRow() != -1 && jlc.list.size() > 0;
        boolean dsize = size && pac.editable;
        boolean esize = editable && dsize;
        if (size) {
            Combo c = jlc.list.get(jlc.getSelectedRow());
            ctypes.setSelectedIndex(c.type);
            clvls.setSelectedIndex(c.lv);
            comboname.setText(c.name);
        } else
            comboname.setText("");
        comboname.setEnabled(dsize);
        ctypes.setEnabled(dsize);
        clvls.setEnabled(dsize);
        remc.setEnabled(dsize);
        remcf.setEnabled(esize && jlc.list.get(jlc.getSelectedRow()).forms.length > 1);
        boolean check = esize && Arrays.stream(jlc.list.get(jlc.getSelectedRow()).forms).noneMatch(fr -> fr.unit == frm.unit) && jlc.list.get(jlc.getSelectedRow()).forms.length < 5;
        addf.setEnabled(check);
    }
}