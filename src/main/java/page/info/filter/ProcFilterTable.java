package page.info.filter;

import common.CommonStatic;
import common.pack.Context;
import common.pack.Identifier;
import common.pack.IndexContainer;
import common.util.Data;
import common.util.lang.Editors;
import common.util.lang.Formatter;
import common.util.lang.ProcLang;
import main.MainBCU;
import page.*;
import page.info.edit.SwingEditor;
import page.support.ListJtfPolicy;
import utilpc.Interpret;

import javax.swing.*;
import java.awt.*;
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
                throw new Exception("unexpected class " + fc);
            } catch (Exception e) {
                CommonStatic.ctx.noticeErr(e, Context.ErrType.ERROR, "failed to generate editor");
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
                if (se.isInvisible())
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

    public void setData(Data.Proc ints) {
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
                se.add(this::add);
            }
        }
        setFocusTraversalPolicy(ljp);
        setFocusCycleRoot(true);
    }

    public boolean compare(Data.Proc proc) {
        for (int i = 0; i < inds.length; i++) {
            SwingEditor.SwingEG group = this.group[i];
            if (group.obj == null || !group.obj.exists())
                continue;
            Data.Proc.ProcItem itm = proc.getArr(inds[i]);
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
                    } else if (f.getType().equals(boolean.class)) {
                        if (!((BoolFilter)group.list[j]).btn.isSelected() && (boolean)pf0 != (boolean)pf1)
                            return false;
                    } else {
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
