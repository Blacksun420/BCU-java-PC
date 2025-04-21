package page.info.filter;

import common.CommonStatic;
import common.pack.Context;
import common.pack.Identifier;
import common.pack.IndexContainer;
import common.pack.SortedPackSet;
import common.util.Data;
import common.util.Data.Proc;
import common.util.lang.Editors;
import common.util.lang.Formatter;
import common.util.lang.ProcLang;
import common.util.unit.Trait;
import main.MainBCU;
import page.*;
import page.info.edit.BlessPage;
import page.info.edit.SwingEditor;
import page.support.ListJtfPolicy;
import utilpc.Interpret;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public class ProcFilterTable extends Page {
    public static class IntFilter extends SwingEditor.IntEditor {

        private int filter = 0;
        private final JBTN btn = new JBTN(">=");

        public IntFilter(Editors.EditorGroup eg, Editors.EdiField field, String f, boolean edit) {
            super(eg, field, f, edit);
            btn.setLnr(l -> {
                cycle();
                if (par.callback != null)
                    par.callback.run();
            });
        }

        private void cycle() {
            filter = (filter + 1) % 5;
            btn.setText((filter % 3 == 0 ? ">" : filter == 1 ? "=" : "<") + (filter < 3 ? "=" : ""));
        }

        @Override
        public void setVisible(boolean res) {
            super.setVisible(res);
            btn.setVisible(res);
        }

        @Override
        public void resize(int x, int y, int x0, int y0, int w0, int h0) {
            set(label, x, y, x0, y0, 100, h0);
            set(input, x, y, x0 + 100, y0, w0 - 185, h0);
            set(btn, x, y, x0 + w0 - 85, y0, 85, h0);
        }

        @Override
        public void add(Consumer<JComponent> con) {
            super.add(con);
            con.accept(btn);
        }
    }

    public static class DoubleFilter extends SwingEditor.DoubleEditor {

        private int filter = 0;
        private final JBTN btn = new JBTN(">=");

        public DoubleFilter(Editors.EditorGroup eg, Editors.EdiField field, String f, boolean edit) {
            super(eg, field, f, edit);
            btn.setLnr(l -> {
                cycle();
                if (par.callback != null)
                    par.callback.run();
            });
        }

        private void cycle() {
            filter = (filter + 1) % 5;
            btn.setText((filter % 3 == 0 ? ">" : filter == 1 ? "=" : "<") + (filter < 3 ? "=" : ""));
        }

        @Override
        public void setVisible(boolean res) {
            super.setVisible(res);
            btn.setVisible(res);
        }

        @Override
        public void resize(int x, int y, int x0, int y0, int w0, int h0) {
            set(label, x, y, x0, y0, 100, h0);
            set(input, x, y, x0 + 100, y0, w0 - 185, h0);
            set(btn, x, y, x0 + w0 - 85, y0, 85, h0);
        }

        @Override
        public void add(Consumer<JComponent> con) {
            super.add(con);
            con.accept(btn);
        }
    }

    public static class BoolFilter extends SwingEditor.BoolEditor {
        private final JTG btn = new JTG("!");

        public BoolFilter(Editors.EditorGroup eg, Editors.EdiField field, String f, boolean edit) {
            super(eg, field, f, edit);
            btn.setLnr(l -> {
                if (par.callback != null)
                    par.callback.run();
            });
        }

        @Override
        public void setVisible(boolean res) {
            super.setVisible(res);
            btn.setVisible(res);
        }

        @Override
        public void resize(int x, int y, int x0, int y0, int w0, int h0) {
            int w1 = (int)(w0 * 0.8);
            set(input, x, y, x0, y0, w1, h0);
            set(btn, x, y, x0 + w1, y0, w0 - w1, h0);
        }

        @Override
        public void add(Consumer<JComponent> con) {
            super.add(con);
            con.accept(btn);
        }
    }

    public static class ProcFilter extends SwingEditor {

        public final JBTN btn;
        private final JTG rej = new JTG("!");
        private final FilterCtrl cont;
        private BlessPage edi;

        public ProcFilter(Editors.EditorGroup eg, Editors.EdiField field, String f, boolean edit, FilterCtrl ec) {
            super(eg, field, f, edit);
            cont = ec;
            btn = new JBTN(ProcLang.get().get(eg.proc).get(f));
            btn.setLnr(e -> MainFrame.changePanel(edi));
        }

        @Override
        public void setVisible(boolean res) {
            btn.setVisible(res);
            rej.setVisible(res);
        }

        @Override
        public boolean isInvisible() {
            return !btn.isVisible();
        }

        @Override
        public void resize(int x, int y, int x0, int y0, int w0, int h0) {
            Page.set(btn, x, y, x0, y0, w0, h0);
            set(btn, x, y, x0, y0, w0 - 85, h0);
            set(rej, x, y, x0 + w0 - 85, y0, 85, h0);
        }

        @Override
        public void setData() {
            field.setData(par.obj);
            if (edi != null)
                edi.setData(field.get() == null ? Data.Proc.blank() : (Data.Proc)field.get());
            else if (par.obj.exists())
                ini();
        }
        private void ini() {
            edi = new BlessPage(cont.table, cont.isEnemy);
            edi.exitter = l -> {
                if (par.callback != null)
                    par.callback.run();
            };
            edi.setData(field.get() == null ? Proc.blank() : (Proc)field.get());
        }

        @Override
        public void add(Consumer<JComponent> con) {
            con.accept(btn);
        }
    }

    public static class TraitFilter extends SwingEditor {

        private final TraitList traitList;
        private final JScrollPane tpane;
        public final JL label;
        boolean setting = true;

        public TraitFilter(Editors.EditorGroup eg, Editors.EdiField field, String f, boolean edit, FilterCtrl ec) {
            super(eg, field, f, edit);
            traitList = new TraitList(edit);
            tpane = new JScrollPane(traitList);
            label = new JL(ProcLang.get().get(eg.proc).get(f));
            traitList.setup(null, ec.isEnemy);
            traitList.addListSelectionListener(arg0 -> {
                if (setting)
                    return;
                if (par.callback != null)
                    par.callback.run();
            });
        }

        @Override
        public void setVisible(boolean res) {
            tpane.setVisible(res);
            label.setVisible(res);
        }

        @Override
        public boolean isInvisible() {
            return !tpane.isVisible();
        }

        @Override
        public void resize(int x, int y, int x0, int y0, int w0, int h0) {
            Page.set(label, x, y, x0, y0, w0, 50);
            Page.set(tpane, x, y, x0, y0 + 50, w0, h0 - 50);
        }

        @Override
        public int getH() {
            return 350;
        }

        @Override
        public void setData() {
            setting = true;
            field.setData(par.obj);
            SortedPackSet<Trait> lt = (SortedPackSet<Trait>)field.get();
            for (int k = 0; k < traitList.list.size(); k++)
                if (lt.contains(traitList.list.get(k)))
                    traitList.addSelectionInterval(k, k);
                else
                    traitList.removeSelectionInterval(k, k);
            setting = false;
        }

        @Override
        public void add(Consumer<JComponent> con) {
            con.accept(tpane);
            con.accept(label);
        }
    }

    /** For ProcID objects */
    public static class PIDFilter extends IntFilter {

        public PIDFilter(Editors.EditorGroup eg, Editors.EdiField field, String f, boolean edit) {
            super(eg, field, f, edit);
        }

        @Override
        public void setData() {
            field.setData(par.obj);
            if (field.obj == null)
                input.setText("");
            else
                input.setText(field.get().toString());
            input.setEnabled(edit && field.obj != null);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected void edit(FocusEvent fe) {
            ((Data.Proc.ProcID)field.get()).setData(CommonStatic.parseIntsN(input.getText()));
            update();
        }
    }

    public static class IDFilter<T extends IndexContainer.Indexable<?, T>> extends SwingEditor.IdEditor<T> {
        private final JTG btn = new JTG("!");

        public IDFilter(Editors.EditorGroup eg, Editors.EdiField field, String f, PageSup<T> sup, boolean edit) {
            super(eg, field, f, sup, edit);
            btn.setLnr(l -> {
                if (par.callback != null)
                    par.callback.run();
            });
        }

        @Override
        public void setVisible(boolean res) {
            super.setVisible(res);
            btn.setVisible(res);
        }

        @Override
        public void resize(int x, int y, int x0, int y0, int w0, int h0) {
            set(input, x, y, x0, y0, 100, h0);
            set(jl, x, y, x0 + 100, y0, w0 - 185, h0);
            set(btn, x, y, x0 + w0 - 85, y0, 85, h0);
        }

        @Override
        public void add(Consumer<JComponent> con) {
            super.add(con);
            con.accept(btn);
        }
    }
    public static class FilterCtrl implements Editors.EditorSupplier {
        private final boolean isEnemy;
        private final AdvProcFilterPage table;
        public FilterCtrl(boolean isEnemy, AdvProcFilterPage table) {
            this.isEnemy = isEnemy;
            this.table = table;
        }
        @Override
        public Editors.Editor getEditor(Editors.EditControl<?> ctrl, Editors.EditorGroup group, String f, boolean edit) {
            try {
                Editors.EdiField field = ctrl.getField(f);
                Class<?> fc = field.getType();
                if (fc == int.class)
                    return new IntFilter(group, field, f, edit);
                if (fc == float.class || fc == double.class)
                    return new DoubleFilter(group, field, f, edit);
                if (fc == boolean.class)
                    return new BoolFilter(group, field, f, edit);
                if (fc == Identifier.class) {
                    if (group.proc.equals("THEME")) {
                        if (f.equals("id"))
                            return new IDFilter<>(group, field, f, table::getBGSup, edit);
                        else
                            return new IDFilter<>(group, field, f, table::getMusicSup, edit);
                    } else if (group.proc.equals("SUMMON") || group.proc.equals("SPIRIT"))
                        return new IDFilter<>(group, field, f, table::getEntitySup, edit);
                }
                if (Enum.class.isAssignableFrom(fc))
                    return new SwingEditor.EnumEditor(group, field, f, edit);
                if (fc == Proc.class)
                    return new ProcFilter(group, field, f, edit, this);
                if (fc == SortedPackSet.class)
                    return new TraitFilter(group, field, f, edit, this);
                if (fc == Proc.ProcID.class)
                    return new PIDFilter(group, field, f, edit);
                throw new Exception("unexpected class " + fc);
            } catch (Exception e) {
                CommonStatic.ctx.noticeErr(e, Context.ErrType.ERROR, "failed to generate editor for " + group.obj + ":" + f);
            }
            return null;
        }
        @Override
        public void setEditorVisibility(Editors.Editor e, boolean b) {
            SwingEditor edi = (SwingEditor) e;
            edi.setVisible(b);
        }

        @Override
        public boolean isEnemy() {
            return isEnemy;
        }
    }

    private static final long serialVersionUID = 1L;

    protected final byte[] inds;
    protected final ListJtfPolicy ljp = new ListJtfPolicy();
    protected final SwingEditor.SwingEG[] group;
    private final boolean isUnit;
    protected static final int tabW = 350;
    public int height = 0;

    public ProcFilterTable(Page p, boolean unit) {
        super(p);
        isUnit = unit;
        inds = unit ? Interpret.UPROCIND : Interpret.EPROCIND;
        group = new SwingEditor.SwingEG[inds.length];
        ini();
    }

    @Override
    public Component add(Component comp) {
        Component ret = super.add(comp);
        if (comp instanceof JTF)
            ljp.add((JTF) comp);
        return ret;
    }

    @Override
    protected void resized(int x, int y) {
        int[] h = new int[]{0, 0, 0};
        for (int i = 0; i < group.length; i++) {
            int di = i % 3;
            int c = di * tabW;
            set(group[i].jlm, x, y, c, h[di], tabW, 50);
            h[di] += 50;
            for (int j = 0; j < group[i].list.length; j++) {
                SwingEditor se = (SwingEditor) group[i].list[j];
                if (se == null || se.isInvisible())
                    continue;
                se.resize(x, y, c, h[di], tabW, 50);
                h[di] += 50;
            }
        }
        for (int j : h)
            height = Math.max(height, j);
    }

    @Override
    public JButton getBackButton() {
        return null;
    }

    public void setData(Proc ints) {
        for (int i = 0; i < inds.length; i++)
            group[i].setData(ints.getArr(inds[i]));
    }

    private void ini() {
        Formatter.Context ctx = new Formatter.Context(!isUnit, MainBCU.seconds, new double[]{1.0, 1.0}, null);
        for (int i = 0; i < group.length; i++) {
            group[i] = new SwingEditor.SwingEG(inds[i], true, () -> getFront().callBack(null), ctx);
            add(group[i].jlm);
            for (int j = 0; j < group[i].list.length; j++) {
                SwingEditor se = (SwingEditor) group[i].list[j];
                if (se != null)
                    se.add(this::add);
            }
        }
        setFocusTraversalPolicy(ljp);
        setFocusCycleRoot(true);
    }

    public boolean compare(Proc proc) {
        for (int i = 0; i < inds.length; i++) {
            SwingEditor.SwingEG group = this.group[i];
            if (group.obj == null || !group.obj.exists())
                continue;
            Proc.ProcItem itm = proc.getArr(inds[i]);
            ProcLang.ItemLang item = ProcLang.get().get(inds[i]);
            String[] arr = item.list();

            for (int j = 0; j < arr.length; j++) {
                try {
                    Field f;
                    Object pf0, pf1;
                    if (arr[j].contains(".")) {
                        String[] strs = arr[j].split("\\.");
                        Field f0 = itm.getClass().getField(strs[0]);
                        f = f0.getType().getField(strs[1]);
                        pf0 = f.get(f0.get(itm));
                        pf1 = f.get(f0.get(group.obj));
                    } else {
                        f = itm.getClass().getField(arr[j]);
                        pf0 = f.get(itm);
                        pf1 = f.get(group.obj);
                    }
                    if (f.getType().equals(int.class)) {
                        if (group.list[j] == null)
                            continue;
                        int fil = ((IntFilter) group.list[j]).filter;
                        switch (fil) {
                            case 0:
                                if ((int)pf0 < (int)pf1)
                                    return false;
                                break;
                            case 1:
                                if ((int)pf0 != (int)pf1)
                                    return false;
                                break;
                            case 2:
                                if ((int)pf0 > (int)pf1)
                                    return false;
                                break;
                            case 3:
                                if ((int)pf0 <= (int)pf1)
                                    return false;
                                break;
                            default:
                                if ((int)pf0 >= (int)pf1)
                                    return false;
                        }
                    } else if (f.getType().equals(double.class)) {
                        if (group.list[j] == null)
                            continue;
                        int fil = ((DoubleFilter)group.list[j]).filter;
                        switch (fil) {
                            case 0:
                                if ((double)pf0 < (double)pf1)
                                    return false;
                                break;
                            case 1:
                                if ((double)pf0 != (double)pf1)
                                    return false;
                                break;
                            case 2:
                                if ((double)pf0 > (double)pf1)
                                    return false;
                                break;
                            case 3:
                                if ((double)pf0 <= (double)pf1)
                                    return false;
                                break;
                            default:
                                if ((double)pf0 >= (double)pf1)
                                    return false;
                        }
                    } else if (f.getType().equals(float.class)) {
                        if (group.list[j] == null)
                            continue;
                        int fil = ((DoubleFilter)group.list[j]).filter;
                        switch (fil) {
                            case 0:
                                if ((float)pf0 < (float)pf1)
                                    return false;
                                break;
                            case 1:
                                if ((float)pf0 != (float)pf1)
                                    return false;
                                break;
                            case 2:
                                if ((float)pf0 > (float)pf1)
                                    return false;
                                break;
                            case 3:
                                if ((float)pf0 <= (float)pf1)
                                    return false;
                                break;
                            default:
                                if ((float)pf0 >= (float)pf1)
                                    return false;
                        }
                    } else if (f.getType().equals(boolean.class)) {
                        if (group.list[j] == null)
                            continue;
                        if (!((BoolFilter) group.list[j]).btn.isSelected() && (boolean) pf0 != (boolean) pf1)
                            return false;
                    } else if (f.getType().equals(Proc.class)) {
                        return compare((Proc) pf0);//TODO
                    } else if (f.getType().equals(SortedPackSet.class)) {
                        //TODO
                    } else {
                        if (group.list[j] == null)
                            continue;
                        boolean ign = ((IDFilter<?>)group.list[j]).btn.isSelected();
                        if (ign)
                            continue;
                        if (pf0 == null || pf1 == null) {
                            if (pf0 != null || pf1 != null)
                                return false;
                        } else if (!pf0.equals(pf1)) //Identifier times hooray
                            return false;
                    }
                } catch (Exception e) {
                    CommonStatic.ctx.noticeErr(e, Context.ErrType.ERROR, "lmao");
                }
            }
        }
        return true;
    }
}
