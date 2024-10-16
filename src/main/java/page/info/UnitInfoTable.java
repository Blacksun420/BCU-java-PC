package page.info;

import common.CommonStatic;
import common.battle.data.MaskAtk;
import common.battle.data.PCoin;
import common.pack.Identifier;
import common.pack.UserProfile;
import common.system.ENode;
import common.system.Node;
import common.system.VImg;
import common.util.Data;
import common.util.unit.*;
import main.MainBCU;
import page.*;
import page.pack.EREditPage;
import page.pack.UREditPage;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class UnitInfoTable extends CharacterInfoTable {

	private static final long serialVersionUID = 1L;

	private final JL[][] upgrade = new JL[3][2];
	private JLabel pcoin;
	private final HTMLTextField cfdesc = new HTMLTextField();

	private final Form f;
	private Level multi;

	protected UnitInfoTable(Page p, Form de, Level lvs, boolean sp) {
		super(p, de, 10, sp);

		f = de;
		multi = lvs;
		ini();
	}

	protected UnitInfoTable(Page p, Form de, boolean sp) {
		this(p, de, de.unit.getPrefLvs(), sp);
	}

	@Override
	protected void resetAtk() {
		super.resetAtk();
		reset();
	}

	@Override
	protected int getH() {
		int l = super.getH();
		if (f.getEvoCost() != -1)
			return l + (upgrade.length * 50);
		return l;
	}

	@Override
	protected void panelClicked(Data.Proc.ProcItem item) {
		if (item instanceof Data.Proc.SUMMON) {
			Data.Proc.SUMMON su = (Data.Proc.SUMMON)item;
			if (((Data.Proc.SUMMON)item).id == null || AbUnit.class.isAssignableFrom(((Data.Proc.SUMMON)item).id.cls))
				if (su.id != null && su.id.cls == UniRand.class)
					changePanel(new UREditPage(getFront(), UserProfile.getUserPack(su.id.pack), (UniRand)su.id.get()));
				else
					changePanel(new UnitInfoPage(getFront(), new Node<>(Identifier.getOr(su.id, Unit.class))));
			else if (su.id.cls == EneRand.class)
				changePanel(new EREditPage(getFront(), UserProfile.getUserPack(su.id.pack), (EneRand)su.id.get()));
			else
				changePanel(new EnemyInfoPage(getFront(), new ENode((Enemy)su.id.get(), su.type.fix_buff ? new int[]{su.mult, su.mult}
						: new int[]{(int)(su.mult / 100.0 * ((multi.getTotalLv() - 1) * 20)), (int)(su.mult / 100.0 * ((multi.getTotalLv() - 1) * 20))})));
		}
	}

	protected void reset() {
		EForm ef = new EForm(f, multi);
		double mul = f.unit.lv.getMult(multi.getTotalLv());
		double atk = b.t().getAtkMulti();
		double def = b.t().getDefMulti();

		int attack = (int) (Math.round(ef.du.allAtk(dispAtk) * mul) * atk);

		int hp = (int) (Math.round(ef.du.getHp() * mul) * def);

		PCoin pc = f.du.getPCoin();

		if (pc != null) {
			attack = (int) (attack * pc.getStatMultiplication(Data.PC2_ATK, multi.getTalents()));
			hp = (int) (hp * pc.getStatMultiplication(Data.PC2_HP, multi.getTalents()));
		}

		main[0][3].setText(hp + " / " + ef.du.getHb());
		main[1][1].setText(String.valueOf(ef.du.getRange()));
		main[1][3].setText(String.valueOf((attack * 30 / ef.du.getItv(dispAtk))));
		main[1][5].setText(String.valueOf((int) (ef.du.getSpeed() * (1 + b.getInc(Data.C_SPE) * 0.01))));
		main[1][7].setText(MainBCU.convertTime(ef.du.getItv(dispAtk)));
		main[2][1].setText(MainBCU.convertTime(ef.du.getTBA()));

		int respawn = b.t().getFinRes(ef.du.getRespawn(), b.getInc(Data.C_RESP));
		main[0][5].setText(MainBCU.convertTime(respawn));

		main[0][7].setText("" + ef.getPrice(1));
		String[] TraitBox = Interpret.getTrait(ef.du.getTraits());
		inis[2].setText(Interpret.getTrait(TraitBox, 0));
		MaskAtk[] atkData = ef.du.getAtks(dispAtk);
		for (int i = 0; i < atks.length; i++) {
			int a = (int) (Math.round((i < atkData.length ? atkData[i] : atkList.get(i - atkData.length)).getAtk() * mul) * b.t().getAtkMulti());
			if (pc != null)
				a = (int) (a * pc.getStatMultiplication(Data.PC2_ATK, multi.getTalents()));
			atks[i][1].setText(a + "");
		}

		List<Interpret.ProcDisplay> ls = Interpret.getAbi(ef.du, dispAtk);
		ls.addAll(Interpret.getProc(ef.du, false, new double[]{mul, multi.getTotalLv()}, dispAtk));
		if (proc != null)
			for (JLabel p : proc)
				remove(p);
		proc = new JLabel[ls.size() + (pc != null ? 1 : 0)];
		for (int i = 0; i < ls.size(); i++) {
			Interpret.ProcDisplay display = ls.get(i);
			add(proc[i] = new JLabel(display.toString()));
			proc[i].setIcon(UtilPC.getScaledIcon(display.icon, UtilPC.iconSize, UtilPC.iconSize));
			proc[i].setBorder(BorderFactory.createEtchedBorder());
		}
		if (pc != null) {
			add(pcoin = proc[ls.size()] = new JLabel(UtilPC.lvText(f, multi)[1]));
			pcoin.setBorder(BorderFactory.createEtchedBorder());
		} else
			pcoin = null;
		updateTooltips();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		if (f.getEvoCost() != -1) {
			int h = 1 + main.length + atks.length + ((proc.length + (proc.length % 2 == 1 ? 1 : 0)) / 2);
			if (displaySpecial)
				h += 1 + (isBC ? 0 : atks.length);
			if (multiAtk)
				h++;
			h *= 50;
			set(cfdesc, x, y, 800, h, 800, 150);
			for (JL[] ug : upgrade) {
				for (int j = 0; j < ug.length; j++)
					set(ug[j], x, y, j * 400, h, 400, 50);
				h += 50;
			}
			set(desc, x, y, 0, h, 1600, 200);
		}
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		jtf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				multi = f.regulateLv(Level.lvList(f.unit, CommonStatic.parseIntsN(jtf.getText()), null), multi);
				String[] strs = UtilPC.lvText(f, multi);
				jtf.setText(strs[0]);
				if (pcoin != null)
					pcoin.setText(strs[1]);
				reset();
			}

		});
	}

	@Override
	protected void ini() {
		super.ini();
		inis[0].setText(f.getID().pack + "/" + f);
		if (f.getEvoCost() != -1) {
			for (int i = 0; i < upgrade.length; i++)
				for (int j = 0; j < upgrade[i].length; j++) {
					add(upgrade[i][j] = new JL());
					upgrade[i][j].setBorder(BorderFactory.createEtchedBorder());
				}
			add(cfdesc);
			cfdesc.setBorder(BorderFactory.createEtchedBorder());
		}
		add(jtf);
		jtf.setText(UtilPC.lvText(f, multi)[0]);
		if (pcoin != null)
			add(pcoin);

		if (!isBC && UserProfile.getUserPack(f.uid.pack).save != null) {
			String st = String.valueOf(UserProfile.getUserPack(f.uid.pack).save.unlockedAt(f));
			if (!st.equals("null"))
				inis[0].setToolTipText(get(MainLocale.PAGE, "uclearinf").replace("_", st));
		}

		main[0][0].setText(Interpret.RARITY[f.unit.rarity]);
		main[0][2].setText(MainLocale.INFO, "hphb");
		main[0][4].setText(MainLocale.INFO, "cdo");
		main[0][6].setText(MainLocale.INFO, "price");

		main[2][0].setText(MainLocale.INFO, "TBA");
		main[2][2].setText(MainLocale.INFO, "postaa");
		main[2][5].setText(f.du.getLimit() + "");
		main[2][5].setToolTipText("<html>This unit will stay at least "
				+ f.du.getLimit()
				+ " units away from the max stage length<br>once it passes that threshold.");

		special[8].setText(MainLocale.INFO, "t7");
		int back = Math.min(f.du.getBack(), f.du.getFront());
		int front = Math.max(f.du.getBack(), f.du.getFront());
		if (back == front)
			special[9].setText(back + "");
		else
			special[9].setText(Math.min(back, front) + " ~ " + Math.max(back, front));

		if (f.getEvoCost() != -1) {
			for (int i = 0; i < upgrade.length; i++)
				for (int j = 0; j < upgrade[i].length; j++) {
					add(upgrade[i][j] = new JL());
					upgrade[i][j].setBorder(BorderFactory.createEtchedBorder());
				}
			add(cfdesc);
			cfdesc.setBorder(BorderFactory.createEtchedBorder());
			int[][] evo = f.getEvoMaterials();
			int count = 0;
			for (int i = 0; i < evo.length; i++) {
				int id = evo[i][0];
				JL up = upgrade[i / 2][i % 2];
				if (id == 0)
					break;
				VImg img = CommonStatic.getBCAssets().gatyaitem.get(id);
				up.setIcon(img != null ? UtilPC.getScaledIcon(img, 50, 50) : null);
				up.setText(evo[i][1] + " " + get(MainLocale.UTIL, "cf" + id));
				count++;
			}
			JL xp = upgrade[count / 2][count % 2];
			xp.setIcon(UtilPC.getScaledIcon(CommonStatic.getBCAssets().XP, 50, 30));
			xp.setText(f.getEvoCost() + " XP");
			String desc = f.hasZeroForm() ? f.unit.info.getUltraFormEvolveExplanation() : f.unit.info.getCatfruitExplanation();
			if (desc != null)
				cfdesc.setText(desc.replace("<br>", "\n"));
			cfdesc.setEditable(false);
		}
		String fDesc = f.getExplanation();
		if (!fDesc.replace("\n", "").isEmpty())
			add(desc);
		descr.setText("<h2>" + f.toString().replace((f.uid == null ? "NULL" : f.uid.id) + "-" + f.fid + " ", "") + "</h2><hr>" + fDesc);
		descr.setBorder(BorderFactory.createEtchedBorder());
		resetAtk();
		addListeners();
	}
}