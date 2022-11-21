package page.info.filter;

import common.util.unit.AbEnemy;
import page.Page;
import page.SupPage;

import javax.swing.*;
import java.util.List;

public class AbEnemyFindPage extends EntityFindPage<AbEnemy> implements SupPage<AbEnemy> {

    private static final long serialVersionUID = 1L;

    public AbEnemyFindPage(Page p) {
        super(p);

        elt = new AbEnemyListTable(this);
        jsp = new JScrollPane(elt);
        efb = new AbEnemyFilterBox(this);
    }

    public AbEnemyFindPage(Page p, String pack, List<String> parents) {
        super(p);

        elt = new AbEnemyListTable(this);
        jsp = new JScrollPane(elt);
        efb = new AbEnemyFilterBox(this, pack, parents);
    }

    @Override
    public AbEnemy getSelected() {
        int sel = elt.getSelectedRow();
        if (sel < 0)
            return null;
        return elt.list.get(sel);
    }
}
