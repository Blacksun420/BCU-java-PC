package page.battle;

import common.CommonStatic;
import common.battle.BasisLU;
import common.battle.BasisSet;
import common.pack.SortedPackSet;
import common.pack.UserProfile;
import common.util.stage.RandStage;
import common.util.stage.Stage;
import common.util.unit.Form;
import common.util.unit.UniRand;
import common.util.unit.Unit;
import page.JBTN;
import page.JTG;
import page.MainLocale;
import page.Page;
import page.basis.BasisPage;
import page.basis.LineUpBox;
import page.basis.LubCont;
import page.basis.ModifierList;
import page.info.StageTable;
import page.info.UnitInfoPage;
import page.pack.UREditPage;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Vector;

public class BattleSetupPage extends LubCont {

	private static final long serialVersionUID = 1L;

	private final JBTN strt = new JBTN(0, "start");
	private final JBTN tmax = new JBTN(0, "tomax");
	private final JTG rich = new JTG(0, "rich");
	private final JTG snip = new JTG(0, "sniper");
	private final JTG plus = new JTG(MainLocale.PAGE, "plusunlock");
	private final JTG testMode = new JTG(MainLocale.PAGE, "testMode");
	private final JComboBox<String> lvlim = new JComboBox<>();
	private final JList<String> jls = new JList<>();
	private final JScrollPane jsps = new JScrollPane(jls);
	private final JLabel jl = new JLabel();
	private final JLabel ulock = new JLabel();
	private final JBTN jlu = new JBTN(0, "line");
	private final LineUpBox lub = new LineUpBox(this);
	private final ModifierList mod = new ModifierList();
	private final JScrollPane jmod = new JScrollPane(mod);

	private final Stage st;
	private final StageTable sttb;
	private final JScrollPane jstt;

	private final int conf;

	public BattleSetupPage(Page p, Stage s, int confs) {
		super(p);
		sttb = new StageTable(this);
		jstt = new JScrollPane(sttb);
		st = s;
		conf = confs;
		ini();
	}

	@Override
	protected LineUpBox getLub() {
		return lub;
	}

	@Override
	public void callBack(Object obj) {
		BasisSet b = BasisSet.current();
		jl.setText(b + "-" + b.sele);
		if (st.lim != null) {
			boolean val = st.lim.valid(b.sele.lu);
			strt.setEnabled(val);
			if (!val) {
				if (st.lim.group != null && st.lim.group.type % 2 != 0) {
					SortedPackSet<Form> fSet = st.lim.getValid(b.sele.lu);
					if (fSet.size() - st.lim.fa != 0)
						if (st.lim.group.type == 3)
							strt.setToolTipText("Remove at least " + (fSet.size() - st.lim.fa) + " of these units from the lineup: " + fSet);
						else if (st.lim.group.type == 1) {
							SortedPackSet<Form> ffSet = new SortedPackSet<>(st.lim.group.fset);
							for (Form f : ffSet.inCommon(fSet))
								ffSet.remove(f);
							strt.setToolTipText((st.lim.fa - fSet.size()) + " more of these units is required in the lineup: " + ffSet);
						}
				}
				if (st.lim.lvr != null && !st.lim.lvr.isValid(b.sele.lu))
					strt.setToolTipText((strt.getToolTipText() == null ? "" : strt.getToolTipText() + ", and ") + " some units' Lv is above limits");
			} else
				strt.setToolTipText(null);
		}
	}

	@Override
	protected void renew() {
		BasisSet b = BasisSet.current();
		callBack(null);
		lub.setLU(b.sele.lu);

		mod.setBasis(BasisSet.current());
		mod.setComboList(BasisSet.current().sele.lu.coms);
		mod.setBanned(st.getCont().stageLimit != null ? st.getCont().stageLimit.bannedCatCombo : null);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jsps, x, y, 50, 100, 200, 200);
		set(jl, x, y, 50, 350, 200, 50);
		set(jlu, x, y, 50, 400, 200, 50);
		set(strt, x, y, 50, 500, 200, 50);
		set(rich, x, y, 300, 100, 200, 50);
		set(snip, x, y, 300, 200, 200, 50);
		set(tmax, x, y, 300, 500, 200, 50);
		set(lub, x, y, 550, 50, 600, 300);
		set(jmod, x, y, 550, 350, 600, 200);
		set(plus, x, y, 1200, 100, 200, 50);
		set(lvlim, x, y, 1200, 200, 200, 50);
		set(jstt, x, y, 50, 600, 1400, 650);
		set(testMode, x, y, 300, 400, 200, 50);
		set(ulock, x, y, 550, 550, 600, 50);
		sttb.setRowHeight(size(x, y, 50));
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		if (lub.unusable() == 2 && lub.getSelected() instanceof Form) {
			Stage sta = st.getCont().getCont().getSave(true).unlockedAt((Form)lub.getSelected());
			ulock.setText(sta == null ? "Forever Locked" : "Clear " + sta + " to unlock");
		} else
			ulock.setText("");
	}

	private void addListeners() {
		jls.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			if (jls.getSelectedIndex() == -1)
				jls.setSelectedIndex(0);
			lub.setLimit(st.getLim(jls.getSelectedIndex()), st.getCont().getCont().getSave(false), st.getCont().price);

			sttb.setData(st, jls.getSelectedIndex());
		});

		jlu.addActionListener(arg0 -> changePanel(new BasisPage(getThis(), st, st.getLim(conf == 1 ? jls.getSelectedIndex() : -1), testMode.isSelected())));

		strt.addActionListener(arg0 -> {
			int star = jls.getSelectedIndex();
			int cfg = 0;
			if (rich.isSelected())
				cfg |= 1;
			if (snip.isSelected())
				cfg |= 2;
			BasisLU b = BasisSet.current().sele;
			if (conf == 0) {
				b = RandStage.getLU(star);
				star = 0;
			}
			byte[] bans = new byte[10];
			for (byte i = 0; i < bans.length; i++)
				bans[i] = lub.unusable(i);

			changePanel(new BattleInfoPage(getThis(), st, star, b, cfg, bans));
		});

		tmax.addActionListener(arg0 -> {
			st.lim.lvr.validate(BasisSet.current().sele.lu);
			renew();
		});

		plus.setLnr(a -> CommonStatic.getConfig().plus = plus.isSelected());

		lvlim.addActionListener(a -> {
			CommonStatic.getConfig().levelLimit = lvlim.getSelectedIndex();

			plus.setEnabled(CommonStatic.getConfig().levelLimit != 0);
		});

		testMode.addActionListener(l -> lub.setTest(testMode.isSelected() ? st.getCont().getCont().getSave(true).getUnlockedsBeforeStage(st, true).keySet() : null));
	}

	private void ini() {
		add(jsps);
		add(jl);
		add(jlu);
		add(strt);
		add(rich);
		add(snip);
		add(tmax);
		add(lub);
		add(jmod);
		add(jstt);
		add(testMode);
		add(ulock);
		sttb.setData(st, 0);
		tmax.setEnabled(st.lim != null && st.lim.lvr != null);
		testMode.setEnabled(st.getCont().getCont().getSave(true) != null);
		if(st.isAkuStage()) {
			add(plus);
			add(lvlim);

			Vector<String> levLimitText = new Vector<>();

			levLimitText.add(get(MainLocale.PAGE, "levlimoff"));

			for(int i = 1; i < 51; i++) {
				levLimitText.add(Integer.toString(i));
			}

			lvlim.setModel(new DefaultComboBoxModel<>(levLimitText));

			plus.setToolTipText(MainLocale.getLoc(MainLocale.PAGE, "plusunlocktip"));
			lvlim.setToolTipText(MainLocale.getLoc(MainLocale.PAGE, "levellimit"));

			plus.setSelected(CommonStatic.getConfig().plus);
			lvlim.setSelectedIndex(CommonStatic.getConfig().levelLimit);
		}
		if (conf == 1) {
			String[] tit = new String[st.getCont().stars.length];
			String star = get(1, "star");
			for (int i = 0; i < st.getCont().stars.length; i++)
				tit[i] = (i + 1) + star + ": " + st.getCont().stars[i] + "%";
			jls.setListData(tit);
		} else if (conf == 0) {
			String[] tit = new String[5];
			String star = get(1, "attempt");
			for (int i = 0; i < 5; i++)
				tit[i] = star + (i + 1);
			jls.setListData(tit);
		}
		jls.setSelectedIndex(0);
		lub.setLimit(st.getLim(conf == 1 ? jls.getSelectedIndex() : -1), st.getCont().getCont().getSave(false), st.getCont().price);
		addListeners();
	}

}