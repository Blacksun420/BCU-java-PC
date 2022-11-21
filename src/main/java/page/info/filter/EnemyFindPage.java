package page.info.filter;

import common.util.unit.AbEnemy;
import common.util.unit.Enemy;
import page.Page;
import page.SupPage;

import javax.swing.*;

public class EnemyFindPage extends EntityFindPage<Enemy> implements SupPage<AbEnemy> {

	private static final long serialVersionUID = 1L;

	public EnemyFindPage(Page p) {
		super(p);

		elt = new EnemyListTable(this);
		jsp = new JScrollPane(elt);
		efb = new EnemyFilterBox(this);
	}

	@Override
	public Enemy getSelected() {
		int sel = elt.getSelectedRow();
		if (sel < 0)
			return null;
		return elt.list.get(sel);
	}
}
