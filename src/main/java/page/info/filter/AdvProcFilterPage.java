package page.info.filter;

import common.util.Data;
import common.util.lang.Editors;
import common.util.pack.Background;
import common.util.stage.Music;
import page.Page;
import page.SupPage;
import page.info.edit.SwingEditor;
import page.support.EntSupInt;
import page.view.BGViewPage;
import page.view.MusicPage;

import javax.swing.*;

public class AdvProcFilterPage extends Page implements EntSupInt {
    private static final long serialVersionUID = 1L;

    private final ProcFilterTable pft;
    private final JScrollPane jat;

    private final Data.Proc proc;
    private final boolean isUnit;

    public AdvProcFilterPage(Page p, boolean isUnit, Data.Proc proc) {
        super(p);
        this.isUnit = isUnit;
        Editors.setEditorSupplier(new ProcFilterTable.FilterCtrl(!isUnit, this));
        pft = new ProcFilterTable(this, isUnit);
        jat = new JScrollPane(pft);
        this.proc = proc;
        ini();
    }

    @Override
    public void callBack(Object obj) {
        ((EntityFindPage<?>)getFront()).efb.callBack(obj);
        fireDimensionChanged();
    }

    @Override
    protected void resized(int x, int y) {
        set(jat, x, y, 0, 0, ProcFilterTable.tabW * 3 + 25, 1150);

        jat.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
        pft.setPreferredSize(size(x, y, ProcFilterTable.tabW * 3, pft.height).toDimension());
        pft.componentResized(x, y);
    }

    private void ini() {
        add(jat);
        pft.setData(proc);
    }

    @Override
    public JButton getBackButton() {
        return null;
    }

    @Override
    public SupPage<Music> getMusicSup(SwingEditor.IdEditor<Music> edi) {
        return new MusicPage(this);
    }

    @Override
    public SupPage<Background> getBGSup(SwingEditor.IdEditor<Background> edi) {
        return new BGViewPage(this);
    }

    @Override
    public SupPage<?> getEntitySup(SwingEditor.IdEditor<?> edi) {
        if (isUnit)
            return new UnitFindPage(this, true);
        return new EnemyFindPage(this, true);
    }

    public boolean compare(Data.Proc proc) {
        return pft.compare(proc);
    }
}
