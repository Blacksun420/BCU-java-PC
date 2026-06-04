package page.info.edit;

import common.battle.BasisSet;
import common.battle.data.CustomEntity;
import common.battle.data.CustomUnit;
import common.pack.Identifier;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.system.Node;
import common.util.unit.Form;
import common.util.unit.Unit;
import page.*;
import page.info.UnitInfoPage;
import page.info.filter.UnitEditBox;
import utilpc.Interpret;

public class FormEditPage extends EntityEditPage {

	private static final long serialVersionUID = 1L;

	private final JL llv = new JL(MainLocale.INFO, "Lv");
	private final JL ldr = new JL(MainLocale.INFO, "price");
	private final JL lrs = new JL(MainLocale.INFO, "cdo");
	private final JL llr = new JL(MainLocale.INFO, "t7");
	private final JL lic = new JL(MainLocale.INFO, "icd");
	private final JTF fdr = new JTF();
	private final JTF flv = new JTF();
	private final JTF frs = new JTF();
	private final JTF flr = new JTF();
	private final JTF fic = new JTF();
	private final JBTN vuni = new JBTN(MainLocale.PAGE, "vuni");
	private final JBTN stat = new JBTN(MainLocale.PAGE, "stat");
	private final JBTN impt = new JBTN(MainLocale.PAGE, "import");
	private final JBTN vene = new JBTN(MainLocale.PAGE, "enemy");
	private final JBTN pcoin = new JBTN(MainLocale.PAGE, "pcoin");
	private final JBTN pf = new JBTN("<");
	private final JBTN nf = new JBTN(">");
	private final UnitEditBox ueb;
	private final Form form;
	private final CustomUnit cu;
	private int lv;

	public FormEditPage(Page p, UserPack pac, Form f) {
		super(p, pac, (CustomEntity) f.du, false);
		form = f;
		cu = (CustomUnit) form.du;
		lv = f.unit.getPreferredLevel();
		ueb = new UnitEditBox(this, pac, cu);
		ini();
		setData((CustomUnit) f.du);
	}

	@Override
	protected double getAtk() {
		return bas.t().getAtkMulti();
	}

	@Override
	protected double getLvAtk() {
		return form.unit.lv.getMult(lv);
	}

	@Override
	protected double getDef() {
		double mul = form.unit.lv.getMult(lv);
		double def = bas.t().getDefMulti();
		return mul * def;
	}

	@Override
	public void callBack(Object o) {
		super.callBack(o);

		if(o instanceof int[])
			BasisSet.synchronizeOrb(form.unit);
	}

	@Override
	protected void getInput(JTF jtf, int[] v) {
		if (jtf == fdr)
			cu.price = (int) (v[0] / 1.5);
		else if (jtf == flv) {
			if (v[0] <= 0)
				v[0] = 1;
			lv = v[0];
		} else if (jtf == frs) {
			if (v[0] <= 60)
				v[0] = 60;
			cu.resp = bas.t().getRevRes(v[0]);
		} else if (jtf == fic) {
			if (v[0] < 0)
				v[0] = 0;
			cu.ini_resp = bas.t().getIniRes(v[0]);
		}
		if (jtf == flr) {
			try {
				if (v.length == 1) {
					int firstLayer = v[0];
					cu.back = cu.front = firstLayer;
				} else if (v.length >= 2) {
					int firstLayer = v[0];
					int secondLayer = v[1];
					if (firstLayer == secondLayer) {
						cu.back = cu.front = firstLayer;
					} else if (firstLayer < secondLayer) {
						cu.back = firstLayer;
						cu.front = secondLayer;
					} else {
						cu.front = firstLayer;
						cu.back = secondLayer;
					}
				}

				flr.setText(Interpret.layer(cu.back, cu.front));
			} catch (Exception ignored) { }
		}
		if (jtf == fli)
			cu.limit = v[0];
	}

	@Override
	protected void ini() {
		llr.setToolTipText("<html>set possible layers of which units can spawn on.</html>");

		set(ldr);
		set(llv);
		set(lrs);
		set(llr);

		set(flv);

		set(fdr);
		set(frs);
		super.ini();

		set(flr);
		add(lic);
		set(fic);

		add(ueb);

		add(vuni);
		add(stat);
		add(impt);
		add(vene);
		add(pcoin);
		if (form.fid > 0)
			add(pf);
		if (form.fid + 1 < form.unit.forms.length)
			add(nf);

		addLnrs();
		subListener(vene, impt, vuni, form.unit);
		assignSubPage(ueb);
	}

	private void addLnrs() {
		pf.setLnr(() -> new FormEditPage(getFront(), pack, form.unit.forms[form.fid - 1]));
		nf.setLnr(() -> new FormEditPage(getFront(), pack, form.unit.forms[form.fid + 1]));
		pcoin.setLnr(() -> new PCoinEditPage(getThis(),form, pack.editable));
		stat.setLnr(x -> {
			Unit u = (Unit) Identifier.get(cu.getPack().uid);
			Node<Unit> nu = Node.getList(UserProfile.getAll(cu.getPack().uid.pack, Unit.class), u);
			changePanel(new UnitInfoPage(this, nu));
		});
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(llv, x, y, 50, 50, 100, 50);
		set(flv, x, y, 150, 50, 200, 50);
		set(ldr, x, y, 50, 350, 100, 50);
		set(fdr, x, y, 150, 350, 200, 50);
		set(lrs, x, y, 1050, 50, 100, 50);
		set(frs, x, y, 1150, 50, 200, 50);
		set(llr, x, y, 1350, 50, 100, 50);
		set(flr, x, y, 1450, 50, 200, 50);
		set(lic, x, y, 1650, 50, 150, 50);
		set(fic, x, y, 1800, 50, 200, 50);
		set(ueb, x, y, 50, 650, 600, 500);
		if (pack.editable) {
			set(vuni, x, y, 1800, 1100, 200, 50);
			set(stat, x, y, 2000, 1100, 200, 50);
		} else {
			set(vuni, x, y, 650, 1000, 200, 50);
			set(stat, x, y, 850, 1000, 200, 50);
		}
		set(impt, x, y, 50, 1150, 200, 50);
		set(vene, x, y, 250, 1150, 200, 50);
		set(pcoin, x, y, 450, 1150, 200, 50);

		short nx = 350, w = 300;
		if (form.fid > 0)
			nx += 150;
		int ny = form.du.getAtkTypeCount() == 1 ? 50 : 0;
		if (form.fid + 1 < form.unit.forms.length) {
			if (nx == 500)
				w -= 150;
			set(nf, x, y, nx, ny, w, 50);
		}
		set(pf, x, y, 350, ny, w, 50);
	}

	@Override
	protected void setData(CustomEntity data) {
		super.setData(data);
		flv.setText(String.valueOf(lv));
		frs.setText(String.valueOf(bas.t().getFinRes(cu.getRespawn(), 0)));
		fdr.setText(String.valueOf((int) Math.round(cu.getPrice() * 1.5)));
		flr.setText(Interpret.layer(cu.back, cu.front));
		fli.setText(String.valueOf(cu.getLimit()));
		fli.setToolTipText("<html>This unit will always stay at least "
				+ cu.getLimit()
				+ " units away from the max stage length<br>once it passes that threshold.");
		fic.setText(String.valueOf(bas.t().getIniRes(cu.getFirstRespawn(), 0)));
		ueb.setData(cu.abi, data.traits);
		if (cu.getPCoin() != null) {
			cu.pcoin.verify();
			cu.pcoin.update();
		}
	}
}