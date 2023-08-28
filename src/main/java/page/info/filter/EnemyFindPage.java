package page.info.filter;

import common.CommonStatic;
import common.pack.PackData;
import common.util.unit.AbEnemy;
import page.Page;
import page.SupPage;
import utilpc.Interpret;

import javax.swing.*;

public class EnemyFindPage extends EntityFindPage<AbEnemy> implements SupPage<AbEnemy> {

	private static final long serialVersionUID = 1L;

	public EnemyFindPage(Page p, boolean rand) {
		this(p, rand, null);
	}

	public EnemyFindPage(Page p, boolean rand, PackData.UserPack pack) {
		super(p);

		elt = new EnemyListTable(this);
		jsp = new JScrollPane(elt);
		efb = new EnemyFilterBox(this, rand, pack);
		adv = new AdvProcFilterPage(this, false, efb.proc);
		ini();
		resized(true);
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		favs.addActionListener(e -> {
			int[] list = elt.getSelectedRows();
			for (int i : list) {
				AbEnemy sel = elt.list.get(i);
				if (!CommonStatic.getFaves().enemies.contains(sel))
					CommonStatic.getFaves().enemies.add(sel);
				else
					CommonStatic.getFaves().enemies.remove(sel);
			}
			if (efb.processOperator(0, ((EnemyFilterBox)efb).rare.isSelectedIndex(Interpret.ERARE.length - 1)))
				efb.confirm();
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
