package page.info.filter;

import common.battle.Basis;
import common.battle.BasisSet;
import common.battle.data.MaskUnit;
import common.pack.UserProfile;
import common.system.Node;
import common.util.Data;
import common.util.unit.*;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.info.UnitInfoPage;
import page.pack.UREditPage;
import page.support.UnitTCR;

import java.awt.*;

public class UnitListTable extends EntityListTable<AbForm> {

	private static final long serialVersionUID = 1L;

	private static String[] tit;
	public int cost = 1;

	static {
		redefine();
	}

	public static void redefine() {
		tit = new String[] { "ID", "name", Page.get(MainLocale.INFO, "pref"), Page.get(MainLocale.INFO,"HP"), Page.get(MainLocale.INFO,"hb"), Page.get(MainLocale.INFO,"atk"), Page.get(MainLocale.INFO, "range"),
				Page.get(MainLocale.INFO, "speed"), "dps", Page.get(MainLocale.INFO, "preaa"), Page.get(MainLocale.INFO, "cdo"), Page.get(MainLocale.INFO, "price"), Page.get(MainLocale.INFO, "atkf"), Page.get(MainLocale.INFO, "will") };
	}

	public UnitListTable(Page p) {
		super(p, tit);
		setDefaultRenderer(Enemy.class, new UnitTCR(lnk));
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
		AbForm e = list.get(r);
		if (e instanceof Form) {
			if (((Form)e).anim == null)
				return;
			Node<Unit> n = Node.getList(UserProfile.getAll((((Form) e).unit.id.pack), Unit.class), ((Form) e).unit);
			MainFrame.changePanel(new UnitInfoPage(page, n));
		} else if (e instanceof UniRand)
			MainFrame.changePanel(new UREditPage(page, UserProfile.getUserPack(e.getID().pack), (UniRand)e));
	}

	@Override
	public Class<?> getColumnClass(int c) {
		c = lnk[c];
		if (c == 1)
			return Enemy.class;
		return String.class;
	}

	@Override
	protected int compare(AbForm e0, AbForm e1, int c) {
		if (c == 0) {
			int val = e0.getID().compareTo(e1.getID());
			return val != 0 ? val : Integer.compare(e0.getFid(), e1.getFid());
		}
		if (c == 1)
			return e0.toString().compareTo(e1.toString());

		if (c != 10 && c != 11 && (e0 instanceof UniRand || e1 instanceof UniRand))
			return e0 instanceof UniRand ? e1 instanceof UniRand ? compare(e0, e1, 0) : -1 : 1;

		int i0 = (int) get(e0, c);
		int i1 = (int) get(e1, c);
		return Integer.compare(i0, i1);
	}

	@Override
	protected Object get(AbForm f, int c) {
		Basis b = BasisSet.current();
		if (f instanceof Form) {
			Form e = (Form) f;
			MaskUnit du = e.maxu();
			double mul = e.unit.lv.getMult(e.unit.getPreferredLevel() + e.unit.getPreferredPlusLevel());
			double atk = b.t().getAtkMulti() * (e.du.getPCoin() != null ? e.du.getPCoin().getStatMultiplication(Data.PC2_ATK, e.du.getPCoin().max) : 1);
			double def = b.t().getDefMulti() * (e.du.getPCoin() != null ? e.du.getPCoin().getStatMultiplication(Data.PC2_HP, e.du.getPCoin().max) : 1);
			int itv = e.anim != null ? du.getItv(0) : -1;
			if (c == 0)
				return e.uid + "-" + e.fid;
			else if (c == 1)
				return e;
			else if (c == 2)
				return e.unit.getPreferredLevel() + e.unit.getPreferredPlusLevel();
			else if (c == 3)
				return (int) (du.getHp() * mul * def);
			else if (c == 4)
				return du.getHb();
			else if (c == 5)
				return (int) (Math.round(du.allAtk(du.firstAtk()) * mul) * atk);
			else if (c == 6)
				return du.getRange();
			else if (c == 7)
				return du.getSpeed();
			else if (c == 8)
				return itv == -1 ? "Corrupted" : (int) (du.allAtk(du.firstAtk()) * mul * atk * 30 / itv);
			else if (c == 9)
				return du.getAtkModel(du.firstAtk(), 0).getPre();
			else if (c == 10)
				return b.t().getFinRes(du.getRespawn());
			else if (c == 11)
				return (int) (du.getPrice() * (1 + cost * 0.5));
			else if (c == 12)
				return itv;
			else if (c == 13)
				return du.getWill() + 1;
		} else {
			if(c == 0)
				return f.getID();
			else if(c == 1)
				return f;
			else if(c == 2)
				return "Random Unit";
			else if (c == 10)
				return b.t().getFinRes(((UniRand)f).cooldown);
			else if (c == 11)
				return (int) (((UniRand)f).price * (1 + cost * 0.5));
		}
		return null;
	}
}
