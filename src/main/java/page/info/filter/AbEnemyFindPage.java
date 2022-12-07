package page.info.filter;

import common.pack.SortedPackSet;
import common.util.unit.AbEnemy;
import page.Page;
import page.SupPage;

import javax.swing.*;

public class AbEnemyFindPage extends EntityFindPage<AbEnemy> implements SupPage<AbEnemy> {

    private static final long serialVersionUID = 1L;

    public AbEnemyFindPage(Page p) {
        super(p);

        elt = new AbEnemyListTable(this);
        jsp = new JScrollPane(elt);
        efb = new AbEnemyFilterBox(this);
        ini();
        resized();
    }

    public AbEnemyFindPage(Page p, String pack, SortedPackSet<String> parents) {
        super(p);

        elt = new AbEnemyListTable(this);
        jsp = new JScrollPane(elt);
        efb = new AbEnemyFilterBox(this, pack, parents);
        ini();
        resized();
    }

    @Override
    public AbEnemy getSelected() {
        int sel = elt.getSelectedRow();
        if (sel < 0)
            return null;
        return elt.list.get(sel);
    }
}
