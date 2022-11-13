package page.info.filter;

import common.battle.Basis;
import common.battle.BasisSet;
import common.system.ENode;
import common.util.unit.Enemy;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.info.EnemyInfoPage;
import page.support.EnemyTCR;
import page.support.SortTable;

import java.awt.*;

public class EnemyListTable extends SortTable<Enemy> {

	private static final long serialVersionUID = 1L;

	private static String[] tit;

	static {
		redefine();
	}

	public static void redefine() {
		tit = new String[] { "ID", "", Page.get(MainLocale.INFO, "HP"), Page.get(MainLocale.INFO, "hb"), Page.get(MainLocale.INFO, "atk"), Page.get(MainLocale.INFO, "range"), Page.get(MainLocale.INFO, "atkf"),
				Page.get(MainLocale.INFO, "speed"), Page.get(MainLocale.INFO, "drop"), Page.get(MainLocale.INFO, "preaa"), "dps", Page.get(MainLocale.INFO, "minpos"), Page.get(MainLocale.INFO, "will") };
	}

	private final Page page;

	private final Basis b = BasisSet.current();

	protected EnemyListTable(Page p) {
		super(tit);

		page = p;

		setDefaultRenderer(Enemy.class, new EnemyTCR());

	}

	@Override
	public Class<?> getColumnClass(int c) {
		c = lnk[c];
		if (c == 1)
			return Enemy.class;
		return String.class;
	}

	protected void clicked(Point p) {
		if (list == null)
			return;
		int c = getColumnModel().getColumnIndexAtX(p.x);
		c = lnk[c];
		int r = p.y / getRowHeight();
		if (r < 0 || r >= list.size() || c != 1)
			return;
		Enemy e = list.get(r);

		if(e.anim != null)
			MainFrame.changePanel(new EnemyInfoPage(page, ENode.getList(list, e)));
	}

	@Override
	protected int compare(Enemy e0, Enemy e1, int c) {
		if (c == 1)
			c--;
		if (c == 0)
			return e0.compareTo(e1);
		if (c == 8 || c == 11)
			return Double.compare((double) get(e0, c), (double) get(e1, c));
		int i0 = (int) get(e0, c);
		int i1 = (int) get(e1, c);
		return Integer.compare(i0, i1);
	}

	@Override
	protected Object get(Enemy e, int c) {
		if (c == 0)
			return e.id;
		else if (c == 1)
			return e;
		else if (c == 2)
			return e.de.getHp();
		else if (c == 3)
			return e.de.getHb();
		else if (c == 4)
			return e.de.allAtk(0);
		else if (c == 5)
			return e.de.getRange();
		else if (c == 6)
			return e.anim != null ? e.de.getItv(0) : "Corrupted";
		else if (c == 7)
			return e.de.getSpeed();
		else if (c == 8)
			return Math.floor(e.de.getDrop() * b.t().getDropMulti()) / 100;
		else if (c == 9)
			return e.de.getAtkModel(0, 0).getPre();
		else if (c == 10)
			return (int) ((long) e.de.allAtk(0) * 30 / e.de.getItv(0));
		else if (c == 11)
			return e.de.getLimit();
		else if (c == 12)
			return e.de.getWill() + 1;
		return null;
	}
}