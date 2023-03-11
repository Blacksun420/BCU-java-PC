package page.info.filter;

import common.CommonStatic;
import common.pack.PackData;
import common.util.unit.AbEnemy;
import page.Page;
import page.SupPage;

import javax.swing.*;

public class EnemyFindPage extends EntityFindPage<AbEnemy> implements SupPage<AbEnemy> {

	private static final long serialVersionUID = 1L;

	public EnemyFindPage(Page p, boolean rand) {
		super(p);

		elt = new EnemyListTable(this);
		jsp = new JScrollPane(elt);
		efb = new EnemyFilterBox(this, rand);
		ini();
		resized();
	}

	public EnemyFindPage(Page p, boolean rand, PackData.UserPack pack) {
		super(p);

		elt = new EnemyListTable(this);
		jsp = new JScrollPane(elt);
		efb = new EnemyFilterBox(this, rand, pack);
		ini();
		resized();
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		favs.addActionListener(e -> {
			int[] list = elt.getSelectedRows();
			for (int i : list) {
				AbEnemy sel = elt.list.get(i);
				if (!CommonStatic.getConfig().favoriteEnemies.contains(sel.getID()))
					CommonStatic.getConfig().favoriteEnemies.add(sel.getID());
				else
					CommonStatic.getConfig().favoriteEnemies.remove(sel.getID());
			}
		});
	}

	@Override
	public AbEnemy getSelected() {
		int sel = elt.getSelectedRow();
		if (sel < 0)
			return null;
		return elt.list.get(sel);
	}
}
