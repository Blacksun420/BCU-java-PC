package page.info;

import common.CommonStatic;
import common.battle.data.MaskAtk;
import common.pack.Identifier;
import common.pack.UserProfile;
import common.system.ENode;
import common.system.Node;
import common.util.Data;
import common.util.unit.*;
import main.MainBCU;
import page.MainLocale;
import page.pack.EREditPage;
import page.pack.UREditPage;
import utilpc.Interpret;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EnemyInfoTable extends CharacterInfoTable {

	private static final long serialVersionUID = 1L;

	private final Enemy e;
	private int multi, mulatk;

	protected EnemyInfoTable(EnemyInfoPage p, Enemy de, int mul, int mula, boolean sp) {
		super(p, de, 8, sp);
		e = de;
		multi = mul;
		mulatk = mula;
		ini();
	}

	@Override
	protected void resetAtk() {
		super.resetAtk();
		List<Interpret.ProcDisplay> ls = Interpret.getAbi(e.de, dispAtk);
		ls.addAll(Interpret.getProc(e.de, true, new double[]{multi  * e.de.multi(b) / 100, mulatk  * e.de.multi(b) / 100}, dispAtk));
		proc = new JLabel[ls.size()];
		for (int i = 0; i < ls.size(); i++) {
			Interpret.ProcDisplay disp = ls.get(i);
			add(proc[i] = new JLabel(disp.toString()));
			JLabel pi = proc[i];
			pi.setBorder(BorderFactory.createEtchedBorder());
			pi.setIcon(disp.icon);
			pi.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					if (e.getSource() == pi)
						panelClicked(disp.item);
				}
			});
		}
		int itv = e.de.getItv(dispAtk);
		main[1][7].setText(MainBCU.convertTime(itv));
		reset();
	}

	@Override
	protected void reset() {
		double mul = multi * e.de.multi(b) / 100;
		double mula = mulatk * e.de.multi(b) / 100;
		main[0][3].setText("" + (int) (e.de.getHp() * mul));
		main[0][7].setText("" + Math.floor(e.de.getDrop() * b.t().getDropMulti(false)) / 100);
		main[1][3].setText("" + (int) (e.de.allAtk(dispAtk) * mula * 30 / e.de.getItv(dispAtk)));
		MaskAtk[] atkData = e.de.getAtks(dispAtk);
		for (int i = 0; i < atks.length; i++)
			atks[i][1].setText("" + Math.round((i < atkData.length ? atkData[i] : atkList.get(i - atkData.length)).getAtk() * mula));

		List<Interpret.ProcDisplay> ls = Interpret.getAbi(e.de, dispAtk);
		ls.addAll(Interpret.getProc(e.de, true, new double[]{mul, mula}, dispAtk));
		for (int i = 0; i < ls.size(); i++)
			proc[i].setText(ls.get(i).toString());
		updateTooltips();
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		jtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int[] data = CommonStatic.parseIntsN(jtf.getText().trim());

				if (data.length >= 1) {
					if (data[0] > 0)
						multi = mulatk = data[0];
					if (data.length > 1 && data[1] > 0)
						mulatk = data[1];
					jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");
				} else
					jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");
				reset();
			}

		});
	}

	@Override
	protected void ini() {
		super.ini();
		String[] TraitBox = Interpret.getTrait(e.de.getTraits());
		add(jtf);
		jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");

		inis[0].setText(e.id + e.toString().replace(Data.trio(e.getID().id), ""));
		inis[2].setText(Interpret.getTrait(TraitBox, e.de.getStar()));
		main[0][0].setText(MainLocale.INFO, "mult");
		main[0][2].setText(MainLocale.INFO, "HP");
		main[0][4].setText(MainLocale.INFO, "hb");
		main[0][5].setText("" + e.de.getHb());
		main[0][6].setText(MainLocale.INFO, "drop");
		main[1][1].setText("" + e.de.getRange());
		main[1][5].setText("" + e.de.getSpeed());

		main[2][1].setText(MainBCU.convertTime(e.de.getTBA()));
		main[2][5].setText(e.de.getLimit() + "");
		if (e.de.getLimit() >= 100)
			main[2][5].setToolTipText("<html>"
					+ "This enemy, if it's a boss, will always stay at least "
					+ (e.de.getLimit() - 100)
					+ " units from the base<br>once it passes that threshold."
					+ "</html>");
		else
			main[2][5].setToolTipText("<html>"
					+ "This enemy, if it's a boss, will always stay at least "
					+ (100 - e.de.getLimit())
					+ " units inside the base<br>once it passes that threshold."
					+ "</html>");
		String eDesc = e.getExplanation();
		if (eDesc.replace("\n", "").length() > 0)
			add(desc);
		descr.setText(e.toString().replace(Data.trio(e.id.id) + " - ", "") + (e.de.getStar() > 2 ? " (Cool Dude)" : "") + "\n" + eDesc);
		descr.setEditable(false);
		resetAtk();
		addListeners();
	}

	@Override
	protected void panelClicked(Data.Proc.ProcItem item) {
		if (item instanceof Data.Proc.SUMMON && (((Data.Proc.SUMMON)item).id == null || Enemy.class.isAssignableFrom(((Data.Proc.SUMMON)item).id.cls))) {
			Data.Proc.SUMMON su = (Data.Proc.SUMMON)item;
			if (((Data.Proc.SUMMON)item).id == null || AbEnemy.class.isAssignableFrom(((Data.Proc.SUMMON)item).id.cls))
				if (su.id.cls == EneRand.class)
					changePanel(new EREditPage(getFront(), UserProfile.getUserPack(su.id.pack), (EneRand)su.id.get()));
				else
					changePanel(new EnemyInfoPage(getFront(), new ENode(Identifier.getOr(su.id, Enemy.class), su.type.fix_buff ? new int[]{su.mult, su.mult} : new int[]{(int)((su.mult / 100.0) * multi), (int)((su.mult / 100.0) * mulatk)})));
			else if (su.id.cls == UniRand.class)
				changePanel(new UREditPage(getFront(), UserProfile.getUserPack(su.id.pack), (UniRand)su.id.get()));
			else
				changePanel(new UnitInfoPage(getFront(), new Node<>(Identifier.getOr(su.id, Unit.class))));
		}
	}
}