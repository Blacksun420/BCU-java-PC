package page.info;

import common.CommonStatic;
import common.battle.BasisSet;
import common.battle.data.MaskAtk;
import common.battle.data.MaskUnit;
import common.battle.data.PCoin;
import common.pack.SortedPackSet;
import common.system.VImg;
import common.util.Data;
import common.util.unit.EForm;
import common.util.unit.Form;
import common.util.unit.Trait;
import main.MainBCU;
import page.*;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public class UnitInfoTable extends Page {

	private static final long serialVersionUID = 1L;

	private final JL[][] main = new JL[4][8];
	private final JL[][] special = new JL[1][8];
	private final JL[][] upgrade = new JL[3][2];
	private final JL[] atks;
	private JLabel[] proc;
	private final JTF jtf = new JTF();
	private JLabel pcoin;
	private final JTA descr = new JTA();
	private final JTA cfdesc = new JTA();
	private final JScrollPane desc = new JScrollPane(descr);

	private final JL atkind = new JL();
	private final JBTN prevatk = new JBTN(MainLocale.PAGE, "prev");
	private final JBTN nextatk = new JBTN(MainLocale.PAGE, "next");

	private final BasisSet b;
	private final Form f;
	private ArrayList<Integer> multi;
	private boolean displaySpecial;
	private int dispAtk = 0;

	protected UnitInfoTable(Page p, Form de, ArrayList<Integer> lvs) {
		super(p);
		b = BasisSet.current();

		f = de;
		multi = lvs;
		atks = new JL[6];
		ini();
	}

	protected UnitInfoTable(Page p, Form de, boolean sp) {
		super(p);
		b = BasisSet.current();

		f = de;
		displaySpecial = sp;
		multi = de.getPrefLvs();
		atks = new JL[6];

		ini();
	}

	private void resetAtk() {
		boolean pc = f.du.getPCoin() != null;
		atkind.setText(get(MainLocale.PAGE, "atk") + " " + dispAtk);
		MaskUnit du = pc ? f.du.getPCoin().improve(multi) : f.du;
		List<Interpret.ProcDisplay> ls = Interpret.getAbi(du, dispAtk);
		double mul = f.unit.lv.getMult(multi.get(0));
		ls.addAll(Interpret.getProc(du, false, new double[]{Math.round(du.getHp() * mul) * b.t().getDefMulti(), multi.get(0)}, dispAtk));
		if (pc)
			ls.add(new Interpret.ProcDisplay("",null));
		if (proc != null)
			for (JLabel p : proc)
				remove(p);
		proc = new JLabel[ls.size()];
		for (int i = 0; i < ls.size(); i++) {
			Interpret.ProcDisplay display = ls.get(i);
			add(proc[i] = new JLabel(display.toString()));
			proc[i].setBorder(BorderFactory.createEtchedBorder());
			proc[i].setIcon(display.getIcon());
		}
		if (pc) {
			pcoin = proc[ls.size() - 1];
			add(pcoin);
			pcoin.setText(UtilPC.lvText(f, multi)[1]);
		} else
			pcoin = null;
		MaskAtk[] atkData = f.du.getAtks(dispAtk);
		StringBuilder pre = new StringBuilder();
		StringBuilder use = new StringBuilder();
		for (MaskAtk atkDatum : atkData) {
			if (pre.length() > 0)
				pre.append(" / ");
			if (use.length() > 0)
				use.append(" / ");

			pre.append(MainBCU.convertTime(atkDatum.getPre()));
			use.append(atkDatum.getDire());
		}
		main[2][7].setText(MainBCU.convertTime(f.du.getItv(dispAtk)));
		main[3][1].setText("" + f.du.isRange(dispAtk));
		main[3][7].setText(MainBCU.convertTime(f.du.getPost(false, dispAtk)));
		atks[3].setText(pre.toString());
		atks[5].setText(use.toString());
		reset();
	}

	@Override
    public JButton getBackButton() {
		return null;
	}

	protected int getH() {
		int l = main.length + 1;
		if (displaySpecial)
			l += special.length;
		if (f.du.getAtkTypeCount() > 1)
			l++;
		if (f.hasEvolveCost())
			l += upgrade.length;
		return (l + (proc.length + 1) / 2) * 50 + (f.getExplanation().replace("\n","").length() > 0 ? 200 : 0);
	}

	protected void reset() {
		EForm ef = new EForm(f, multi);
		double mul = f.unit.lv.getMult(multi.get(0));
		double atk = b.t().getAtkMulti();
		double def = b.t().getDefMulti();

		int attack = (int) (Math.round(ef.du.allAtk(dispAtk) * mul) * atk);

		int hp = (int) (Math.round(ef.du.getHp() * mul) * def);

		PCoin pc = f.du.getPCoin();
		if (pc != null) {
			attack = (int) (attack * pc.getAtkMultiplication(multi));
			hp = (int) (hp * pc.getHPMultiplication(multi));
		}

		SortedPackSet<Trait> trs = ef.du.getTraits();
		String[] TraitBox = new String[trs.size()];
		for (int i = 0; i < trs.size(); i++) {
			Trait trait = ef.du.getTraits().get(i);
			if (trait.BCTrait)
				TraitBox[i] = Interpret.TRAIT[trait.id.id];
			else
				TraitBox[i] = trait.name;
		}
		main[1][3].setText(hp + " / " + ef.du.getHb());
		main[2][3].setText("" + (attack * 30 / ef.du.getItv(dispAtk)));
		main[2][5].setText("" + (int) (ef.du.getSpeed() * (1 + b.getInc(Data.C_SPE) * 0.01)));
		int respawn = b.t().getFinRes(ef.du.getRespawn());
		main[1][5].setText(MainBCU.convertTime(respawn));

		main[1][7].setText("" + ef.getPrice(1));
		main[0][4].setText(Interpret.getTrait(TraitBox, 0));
		MaskAtk[] atkData = ef.du.getAtks(dispAtk);
		StringBuilder satk = new StringBuilder();
		for (MaskAtk atkDatum : atkData) {
			if (satk.length() > 0)
				satk.append(" / ");

			int a = (int) (Math.round(atkDatum.getAtk() * mul) * b.t().getAtkMulti());

			if (pc != null) {
				a = (int) (a * pc.getAtkMultiplication(multi));
			}

			satk.append(a);
		}
		atks[1].setText(satk.toString());

		List<Interpret.ProcDisplay> ls = Interpret.getAbi(ef.du, dispAtk);
		ls.addAll(Interpret.getProc(ef.du, false, new double[]{mul, multi.get(0)}, dispAtk));
		for (JLabel l : proc) {
			if (l != pcoin)
				l.setText("");
			l.setIcon(null);
		}
		for (int i = 0; i < ls.size(); i++) {
			Interpret.ProcDisplay display = ls.get(i);
			proc[i].setText(display.toString());
			proc[i].setIcon(display.getIcon());
		}
		updateTooltips();
	}

	@Override
	protected void resized(int x, int y) {
		for (int i = 0; i < main.length; i++)
			for (int j = 0; j < main[i].length; j++)
				if ((i != 0 || j < 4) && (i != 1 || j > 1))
					set(main[i][j], x, y, 200 * j, 50 * i, 200, 50);
		set(jtf, x, y, 100, 50, 300, 50);
		set(main[1][0], x, y, 0, 50, 100, 50);
		set(main[0][4], x, y, 800, 0, 800, 50);
		int h = main.length * 50;
		if (displaySpecial) {
			for (JL[] jls : special) {
				for (int j = 0; j < jls.length; j++)
					set(jls[j], x, y, 200 * j, h, 200, 50);
				h += 50;
			}
		} else {
			for (JL[] jls : special) {
				for (int j = 0; j < jls.length; j++)
					set(jls[j], x, y, 200 * j, h, 0, 0);
			}
		}
		if (f.du.getAtkTypeCount() > 1) {
			set(prevatk, x, y, 0, h, 400, 50);
			set(atkind, x, y, 700, h, 300, 50);
			set(nextatk, x, y, 1200, h, 400, 50);
			h += 50;
		}
		set(atks[0], x, y, 0, h, 200, 50);
		set(atks[1], x, y, 200, h, 400, 50);
		set(atks[2], x, y, 600, h, 200, 50);
		set(atks[3], x, y, 800, h, 400, 50);
		set(atks[4], x, y, 1200, h, 200, 50);
		set(atks[5], x, y, 1400, h, 200, 50);
		h += 50;
		for (int i = 0; i < proc.length; i++)
			set(proc[i], x, y, i % 2 * 800, h + 50 * (i / 2), i % 2 == 0 && i + 1 == proc.length ? 1600 : 800, 50);
		h += proc.length * 25 + (proc.length % 2 == 1 ? 25 : 0);
		if (f.hasEvolveCost()) {
			set(cfdesc, x, y, 800, h, 800, 150);
			for (JL[] ug : upgrade) {
				for (int j = 0; j < ug.length; j++)
					set(ug[j], x, y, j * 400, h, 400, 50);
				h += 50;
			}
		}
		set(desc, x, y, 0, h, 1600, 200);
	}

	private void addListeners() {

		jtf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				multi = f.regulateLv(CommonStatic.parseIntsN(jtf.getText()), multi);
				String[] strs = UtilPC.lvText(f, multi);
				jtf.setText(strs[0]);
				if (pcoin != null)
					pcoin.setText(strs[1]);
				reset();
			}

		});

		prevatk.setLnr(sel -> {
			dispAtk--;
			prevatk.setEnabled(dispAtk != 0);
			nextatk.setEnabled(true);
			resetAtk();
		});

		nextatk.setLnr(sel -> {
			dispAtk++;
			prevatk.setEnabled(true);
			nextatk.setEnabled(dispAtk != f.du.getAtkTypeCount() - 1);
			resetAtk();
		});
	}

	private void ini() {
		for (int i = 0; i < main.length; i++)
			for (int j = 0; j < main[i].length; j++)
				if (i * j != 1 && (i != 0 || j < 5)) {
					add(main[i][j] = new JL());
					main[i][j].setBorder(BorderFactory.createEtchedBorder());
					if (i != 0 && j % 2 == 0 || i == 0 && j < 4)
						main[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				}
		for (int i = 0; i < atks.length; i++) {
			add(atks[i] = new JL());
			atks[i].setBorder(BorderFactory.createEtchedBorder());
			if (i % 2 == 0)
				atks[i].setHorizontalAlignment(SwingConstants.CENTER);
		}
		for (int i = 0; i < special.length; i++) {
			for (int j = 0; j < special[i].length; j++) {
				add(special[i][j] = new JL());
				special[i][j].setBorder(BorderFactory.createEtchedBorder());
				if (j % 2 == 0)
					special[i][j].setHorizontalAlignment(SwingConstants.CENTER);
			}
		}
		if (f.hasEvolveCost()) {
			for (int i = 0; i < upgrade.length; i++) {
				for (int j = 0; j < upgrade[i].length; j++) {
					add(upgrade[i][j] = new JL());
					upgrade[i][j].setBorder(BorderFactory.createEtchedBorder());
				}
			}
		}
		add(cfdesc);
		cfdesc.setBorder(BorderFactory.createEtchedBorder());
		add(jtf);
		jtf.setText(UtilPC.lvText(f, multi)[0]);
		if (pcoin != null)
			add(pcoin);

		main[0][0].setText("ID");
		main[0][1].setText(f.uid + "-" + f.fid);
		if (f.anim.getEdi() != null && f.anim.getEdi().getImg() != null)
			main[0][2].setIcon(UtilPC.getIcon(f.anim.getEdi()));
		main[0][3].setText(MainLocale.INFO, "trait");
		main[1][0].setText(Interpret.RARITY[f.unit.rarity]);
		main[1][2].setText(MainLocale.INFO, "hphb");
		main[1][4].setText(MainLocale.INFO, "cdo");
		main[1][6].setText(MainLocale.INFO, "price");
		main[2][0].setText(MainLocale.INFO, "range");
		main[2][1].setText("" + f.du.getRange());
		main[2][2].setText("dps");
		main[2][4].setText(MainLocale.INFO, "speed");
		main[2][6].setText(MainLocale.INFO, "atkf");

		main[3][0].setText(MainLocale.INFO, "isr");
		main[3][2].setText(MainLocale.INFO, "will");
		main[3][3].setText("" + (f.du.getWill() + 1));
		main[3][4].setText(MainLocale.INFO, "TBA");
		main[3][6].setText(MainLocale.INFO, "postaa");
		main[3][5].setText(MainBCU.convertTime(f.du.getTBA()));

		special[0][0].setText(MainLocale.INFO, "count");
		special[0][1].setText(f.du.getAtkLoop() < 0 ? get(MainLocale.UTIL, "inf") : f.du.getAtkLoop() + "");
		special[0][2].setText(MainLocale.INFO, "width");
		special[0][3].setText(f.du.getWidth() + "");
		special[0][4].setText(MainLocale.INFO, "minpos");
		special[0][5].setText(f.du.getLimit() + "");
		special[0][6].setText(MainLocale.INFO, "t7");
		int back = Math.min(f.du.getBack(), f.du.getFront());
		int front = Math.max(f.du.getBack(), f.du.getFront());
		if (back == front)
			special[0][7].setText(back + "");
		else
			special[0][7].setText(Math.min(back, front) + " ~ " + Math.max(back, front));

		if (f.hasEvolveCost()) {
			int[][] evo = f.unit.info.evo;
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
			xp.setText(f.unit.info.xp + " XP");
			String desc = f.unit.info.getCatfruitExplanation();
			if (desc != null)
				cfdesc.setText(desc.replace("<br>", "\n"));
		}

		atks[0].setText(1, "atk");
		atks[2].setText(1, "preaa");
		atks[4].setText(1, "dire");
		special[0][5].setToolTipText("<html>This unit will stay at least "
				+ f.du.getLimit()
				+ " units away from the max stage length<br>once it passes that threshold.");
		add(prevatk);
		add(atkind);
		add(nextatk);
		String fDesc = f.getExplanation();
		if (fDesc.replace("\n", "").length() > 0)
			add(desc);
		descr.setText(f.toString().replace((f.uid == null ? "NULL" : f.uid.id) + "-" + f.fid + " ", "") + "\n" + fDesc);
		descr.setEditable(false);
		prevatk.setEnabled(false);
		cfdesc.setEditable(false);
		resetAtk();
		addListeners();
		updateTooltips();
	}

	private void updateTooltips() {
		for (JLabel jl : proc) {
			String str = jl.getText();
			StringBuilder sb = new StringBuilder();
			FontMetrics fm = jl.getFontMetrics(jl.getFont());
			while (fm.stringWidth(str) >= 400) {
				int i = 1;
				String wrapped = str.substring(0, i);
				while (fm.stringWidth(wrapped) < 400)
					wrapped = str.substring(0, i++);

				int maximum; //JP proc texts don't count with space, this is here to prevent it from staying in while loop forever
				if (CommonStatic.getConfig().lang == 3)
					maximum = Math.max(wrapped.lastIndexOf("。"), wrapped.lastIndexOf("、"));
				else
					maximum = Math.max(Math.max(wrapped.lastIndexOf(" "), wrapped.lastIndexOf(".")), wrapped.lastIndexOf(","));

				if (maximum <= 0)
					maximum = Math.min(i, wrapped.length());

				wrapped = wrapped.substring(0, maximum);
				sb.append(wrapped).append("<br>");
				str = str.substring(wrapped.length());
			}
			sb.append(str);
			jl.setToolTipText("<html>" + sb + "</html>");
		}
	}

	public void setDisplaySpecial(boolean s) {
		displaySpecial = s;
	}
}
