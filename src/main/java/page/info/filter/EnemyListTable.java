package page.info.filter;

import common.battle.Basis;
import common.battle.BasisSet;
import common.pack.UserProfile;
import common.system.ENode;
import common.util.unit.AbEnemy;
import common.util.unit.EneRand;
import common.util.unit.Enemy;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.info.EnemyInfoPage;
import page.pack.EREditPage;
import page.support.EnemyTCR;

import java.awt.*;

public class EnemyListTable extends EntityListTable<AbEnemy> {

	private static final long serialVersionUID = 1L;

	private static String[] tit;

	static {
		redefine();
	}

	public static void redefine() {
		tit = new String[] { "ID", "", Page.get(MainLocale.INFO, "HP"), Page.get(MainLocale.INFO, "hb"), Page.get(MainLocale.INFO, "atk"), Page.get(MainLocale.INFO, "range"), Page.get(MainLocale.INFO, "atkf"),
				Page.get(MainLocale.INFO, "speed"), Page.get(MainLocale.INFO, "drop"), Page.get(MainLocale.INFO, "preaa"), "dps", Page.get(MainLocale.INFO, "minpos"), Page.get(MainLocale.INFO, "will"), "hp/dps" };
	}

	private final Basis b = BasisSet.current();

	protected EnemyListTable(Page p) {
		super(p, tit);
		setDefaultRenderer(Enemy.class, new EnemyTCR());
	}

	@Override
	public Class<?> getColumnClass(int c) {
		c = lnk[c];
		if (c == 1)
			return Enemy.class;
		return String.class;
	}

	@Override
	public void clicked(Point p) {
		if (list == null)
			return;
		int c = getColumnModel().getColumnIndexAtX(p.x);
		c = lnk[c];
		int r = p.y / getRowHeight();
		if (r < 0 || r >= list.size() || c != 1)
			return;
		AbEnemy e = list.get(r);

		if(e instanceof Enemy)
			MainFrame.changePanel(new EnemyInfoPage(page, ENode.getListE(list, e)));
		else if(e instanceof EneRand)
			MainFrame.changePanel(new EREditPage(page, UserProfile.getUserPack(e.getID().pack)));
	}

	@Override
	protected int compare(AbEnemy e0, AbEnemy e1, int c) {
		if (c == 0)
			return e0.compareTo(e1);
		if (c == 1)
			return e0.toString().compareTo(e1.toString());
		if (e0 instanceof EneRand || e1 instanceof EneRand)
			return e0 instanceof EneRand ? e1 instanceof EneRand ? compare(e0, e1, 0) : -1 : 1;

		if (c == 8 || c == 11 || c == 13)
			return Double.compare((double) get(e0, c), (double) get(e1, c));
		int i0 = (int) get(e0, c);
		int i1 = (int) get(e1, c);
		return Integer.compare(i0, i1);
	}

	@Override
	protected Object get(AbEnemy abe, int c) {
		if (abe instanceof Enemy) {
			Enemy e = (Enemy) abe;

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
			else if (c == 13) {
				return e.de.allAtk(0) == 0 ? Double.MAX_VALUE : Math.round(1.0 * e.de.getHp() / (e.de.allAtk(0) * 30.0 / e.de.getItv(0)) * 1000000.0)/1000000.0;
			}
		} else {
			if (c == 0)
				return abe.getID();
			else if (c == 1)
				return abe;
			else if (c == 2)
				return "Random Enemy";
		}
		return null;
	}
}