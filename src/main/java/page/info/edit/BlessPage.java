package page.info.edit;

import common.pack.Identifier;
import common.pack.IndexContainer;
import common.pack.PackData.UserPack;
import common.util.Data.Proc;
import common.util.lang.Editors;
import common.util.pack.Background;
import common.util.stage.Music;
import common.util.unit.AbUnit;
import main.Opts;
import page.DefaultPage;
import page.SupPage;
import page.info.filter.AdvProcFilterPage;
import page.info.filter.EnemyFindPage;
import page.info.filter.UnitFindPage;
import page.support.EntSupInt;
import page.view.BGViewPage;
import page.view.MusicPage;

import javax.swing.*;
import java.util.function.Consumer;

public class BlessPage extends DefaultPage implements EntSupInt {

    private final UserPack pack;
    private final ProcTable.MainProcTable mpt;
    private final JScrollPane jspm;
    private final ProcTable.AtkProcTable apt;
    private final JScrollPane jsm;
    private Proc p;
    private final boolean isEnemy;
    public Consumer<Proc> exitter;

    private SwingEditor.IdEditor<?> editor;
    private SupPage<? extends IndexContainer.Indexable<?, ?>> sup;

    public BlessPage(EntityEditPage pg, boolean enemy) {
        super(pg);
        pack = pg.pack;
        mpt = new ProcTable.MainProcTable(this, pack.editable, !(isEnemy = enemy));
        jspm = new JScrollPane(mpt);
        apt = new ProcTable.AtkProcTable(this, pack.editable, !enemy);
        jsm = new JScrollPane(apt);
        ini();
    }

    public BlessPage(AdvProcFilterPage pg, boolean enemy) {
        super(pg);
        pack = null;
        mpt = new ProcTable.MainProcTable(this, true, !(isEnemy = enemy));
        jspm = new JScrollPane(mpt);
        apt = new ProcTable.AtkProcTable(this, true, !enemy);
        jsm = new JScrollPane(apt);
        ini();
    }

    private void ini() {
        assignSubPage(mpt, apt);
        add(jspm);
        add(jsm);
    }
    public void setData(Proc proc) {
        p = proc;
        mpt.setData(proc);
        apt.setData(proc);
    }

    @Override
    public void callBack(Object o) {
        fireDimensionChanged();
    }

    @Override
    public void exit() {
        exitter.accept(p.isBlank() ? null : p);
        Editors.def = !(getFront() instanceof BlessPage);
    }

    @Override
    public void resized(int x, int y) {
        super.resized(x, y);
        set(jsm, x, y, 1000, 100, 800, 900);
        set(jspm, x, y, 1850, 100, 350, 900);
        mpt.componentResized(x, y);
        apt.componentResized(x, y);

        jspm.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
        jsm.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
    }

    @Override
    public SupPage<Music> getMusicSup(SwingEditor.IdEditor<Music> edi) {
        editor = edi;
        SupPage<Music> ans = new MusicPage(this, pack.getSID());
        sup = ans;
        return ans;
    }
    @Override
    public SupPage<Background> getBGSup(SwingEditor.IdEditor<Background> edi) {
        editor = edi;
        SupPage<Background> ans = new BGViewPage(this, pack.getSID());
        sup = ans;
        return ans;
    }
    @Override
    public SupPage<?> getEntitySup(SwingEditor.IdEditor<?> edi) {
        editor = edi;
        SupPage<?> ans;
        if (isEnemy) {
            ans = new EnemyFindPage(this, true, pack);
        } else
            ans = new UnitFindPage(this, true, pack);
        sup = ans;
        return ans;
    }
    @Override
    public SupPage<AbUnit> getUnitSup(SwingEditor.IdEditor<?> edi) {
        editor = edi;
        SupPage<AbUnit> ans = new UnitFindPage(this, true, pack);
        sup = ans;
        return ans;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected void renew() {
        if (sup != null && editor != null && ((sup.getSelected() == null && editor.field.get() != null) || (sup.getSelected() != null && !sup.getSelected().getID().equals(editor.field.get())))
                && (editor.field.get() == null || (Opts.conf("Replace " + ((Identifier<?>)editor.field.get()).get() + " with " + sup.getSelected() + "?")))) {
            Identifier val = sup.getSelected() == null ? null : sup.getSelected().getID();
            editor.callback(val);
        }
        sup = null;
        editor = null;
    }
}
