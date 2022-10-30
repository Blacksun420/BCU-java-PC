package page.pack;

import common.pack.PackData.UserPack;
import common.util.unit.Form;
import common.util.unit.UniRand;
import common.util.unit.Unit;
import main.Opts;
import page.JBTN;
import page.JTF;
import page.JTG;
import page.Page;
import page.info.filter.UnitFindPage;
import page.support.AnimLCR;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class UREditPage extends Page {

    private static final long serialVersionUID = 1L;

    public static void redefine() {
        EREditTable.redefine();
    }

    private final JBTN back = new JBTN(0, "back");
    private final JBTN veif = new JBTN(0, "veif");
    private final UREditTable jt;
    private final JScrollPane jspjt;
    private final JList<UniRand> jlst = new JList<>();
    private final JScrollPane jspst = new JScrollPane(jlst);
    private final JBTN adds = new JBTN(0, "add");
    private final JBTN rems = new JBTN(0, "rem");
    private final JBTN addl = new JBTN(0, "addl");
    private final JBTN reml = new JBTN(0, "reml");
    private final JList<Form> jlu = new JList<>();
    private final JScrollPane jspe = new JScrollPane(jlu);
    private final JTF name = new JTF();
    private final JTG[] type = new JTG[3];

    private final UserPack pack;

    private UnitFindPage ufp;

    private UniRand rand;

    public UREditPage(Page p, UserPack pac) {
        super(p);
        pack = pac;
        List<Unit> uni = pac.units.getList();
        ArrayList<Form> forms = new ArrayList<>();
        for (int i = 0; i < uni.size(); i++)
            Collections.addAll(forms, uni.get(i).forms);
        jlu.setListData(forms.toArray(new Form[0]));
        jt = new UREditTable(this);
        jspjt = new JScrollPane(jt);
        ini();
        resized();
    }

    public UREditPage(Page page, UserPack pac, UniRand e) {
        this(page, pac);
        jlu.setSelectedValue(e, true);
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
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);

        set(jspst, x, y, 500, 150, 400, 800);
        set(adds, x, y, 500, 1000, 200, 50);
        set(rems, x, y, 700, 1000, 200, 50);
        set(name, x, y, 500, 1100, 400, 50);
        set(veif, x, y, 950, 100, 400, 50);
        set(jspe, x, y, 950, 150, 400, 1100);
        set(jspjt, x, y, 1400, 450, 850, 800);

        for (int i = 0; i < 3; i++)
            set(type[i], x, y, 1550 + 250 * i, 250, 200, 50);
        set(addl, x, y, 1800, 350, 200, 50);
        set(reml, x, y, 2050, 350, 200, 50);
        jt.setRowHeight(size(x, y, 50));
        jlu.setFixedCellHeight(size(x, y, 50));
    }

    private void addListeners() {

        back.addActionListener(arg0 -> changePanel(getFront()));

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
                ufp = new UnitFindPage(getThis(), pack.desc.id, pack.desc.dependency);
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

        for (int i = 0; i < 3; i++) {
            int I = i;
            type[i].addActionListener(arg0 -> {
                if (isAdj() || rand == null)
                    return;
                rand.type = I;
                setUR(rand);
            });
        }

    }

    private void ini() {
        add(back);
        add(veif);
        add(adds);
        add(rems);
        add(jspjt);
        add(jspst);
        add(addl);
        add(reml);
        add(jspe);
        add(name);
        for (int i = 0; i < 3; i++)
            add(type[i] = new JTG(1, "ert" + i));
        setES();
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
            jt.setEnabled(b);
            for (JTG btn : type)
                btn.setEnabled(b);
            rand = st;
            jt.setData(st);
            name.setText(st == null ? "" : rand.name);
            int t = st == null ? -1 : st.type;
            for (int i = 0; i < 3; i++)
                type[i].setSelected(i == t);
            jspjt.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        });
        resized();
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

}
