package page.battle;

import common.CommonStatic;
import common.battle.BasisLU;
import common.battle.BasisSet;
import common.pack.SortedPackSet;
import common.util.stage.Limit;
import common.util.stage.MapColc.PackMapColc;
import common.util.stage.RandStage;
import common.util.stage.Stage;
import common.util.unit.Form;
import page.JBTN;
import page.JTG;
import page.MainLocale;
import page.Page;
import page.basis.BasisPage;
import page.basis.LineUpBox;
import page.basis.LubCont;
import page.basis.ModifierList;
import page.info.StageTable;

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
	private final JTG testMode = new JTG(MainLocale.PAGE, "testmode");
	private final JTG tlu = new JTG(0, "testlu");
	private final JBTN rtlu = new JBTN(0, "remtlu");
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

	private final boolean rand;

	public BattleSetupPage(Page p, Stage s, boolean randa) {
		super(p);
		sttb = new StageTable(this);
		jstt = new JScrollPane(sttb);
		st = s;
		rand = randa;
		ini();
	}

	@Override
	protected LineUpBox getLub() {
		return lub;
	}

	@Override
	public void callBack(Object obj) {
		BasisLU bu = getLU();
		if (tlu.isSelected()) {
			jl.setText("Last Clear Lineup");
		} else if (st.preset == null) {
			BasisSet b = BasisSet.current();
			jl.setText(b + "-" + bu);
		} else
			jl.setText("Preset Lineup");

		Limit lim = st.getLim(jls.getSelectedIndex());
		rich.setEnabled(!lim.rich);
		snip.setEnabled(!lim.sniper);
        boolean val = lim.valid(bu.lu);
        strt.setEnabled(val);
        if (!val) {
            if (lim.group != null && lim.group.type % 2 != 0) {
                SortedPackSet<Form> fSet = lim.getValid(bu.lu);
                if (fSet.size() - lim.fa != 0)
                    if (lim.group.type == 3)
                        strt.setToolTipText("Remove at least " + (fSet.size() - lim.fa) + " of these units from the lineup: " + fSet);
                    else if (lim.group.type == 1) {
                        SortedPackSet<Form> ffSet = new SortedPackSet<>(lim.group.fset);
                        for (Form f : ffSet.inCommon(fSet))
                            ffSet.remove(f);
                        strt.setToolTipText((lim.fa - fSet.size()) + " more of these units is required in the lineup: " + ffSet);
                    }
            }
        }
        if (lim.lvr != null && !lim.lvr.isValid(bu.lu))
            strt.setToolTipText((strt.getToolTipText() == null ? "" : strt.getToolTipText() + ", and ") + "some units' Lv is above limits");
        else if (val)
            strt.setToolTipText(null);
        if (obj instanceof String)
			if (obj.equals("prog"))
				lub.setLimit(lim, st.getMC().getSave(false), st.getCont().price);
	}

	@Override
	protected void renew() {
		BasisLU b = getLU();
		callBack(null);
		lub.setLU(b.lu);

		mod.setBasis(b);
		mod.setBanned(lub.getLim().stageLimit != null ? lub.getLim().stageLimit.bannedCatCombo : null);
	}

	private BasisLU getLU() {
		if (st.preset != null)
			return st.preset.apply();
		return tlu.isSelected() ? st.lastClear : BasisSet.current().sele;
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
		set(tlu, x, y, 300, 700, 200, 50);
		set(rtlu, x, y, 550, 700, 200, 50);
		sttb.setRowHeight(size(x, y, 50));
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		if (lub.unusable() == 2 && lub.getSelected() instanceof Form) {
			Stage sta = st.getMC().getSave(true).unlockedAt((Form)lub.getSelected());
			ulock.setText(sta == null ? get(MainLocale.PAGE,"flocked") : get(MainLocale.PAGE,"cleartou").replace("_", sta.toString()));
		} else
			ulock.setText("");
	}

	private void addListeners() {
		jls.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			if (jls.getSelectedIndex() == -1)
				jls.setSelectedIndex(0);

			sttb.setData(st, jls.getSelectedIndex());
			lub.setLimit(st.getLim(jls.getSelectedIndex()), st.getMC().getSave(false), st.getCont().price);
			renew();
		});

		jlu.addActionListener(arg0 -> changePanel(new BasisPage(getThis(), st, !rand ? jls.getSelectedIndex() : -1, testMode.isSelected())));

		strt.addActionListener(arg0 -> {
			int star = jls.getSelectedIndex();
			int cfg = 0;
			if (rich.isSelected())
				cfg |= 1;
			if (snip.isSelected())
				cfg |= 2;
			BasisLU b = getLU();
			if (rand) {
				b = RandStage.getLU(star);
				star = 0;
			}
			byte saveMode = (byte)(testMode.isSelected() ? 2 : st.getMC().getSave(false) != null ? 1 : 0);
			changePanel(new BattleInfoPage(getThis(), st, star, b, cfg, saveMode));
		});

		tmax.addActionListener(arg0 -> {
			st.getLim(jls.getSelectedIndex()).lvr.validate(getLU().lu);
			renew();
		});

		plus.setLnr(a -> CommonStatic.getConfig().plus = plus.isSelected());

		lvlim.addActionListener(a -> {
			CommonStatic.getConfig().levelLimit = lvlim.getSelectedIndex();

			plus.setEnabled(CommonStatic.getConfig().levelLimit != 0);
		});

		testMode.addActionListener(l -> lub.setTest(testMode.isSelected() ? st.getMC().getSave(true).getUnlockedsBeforeStage(st, true).keySet() : null));
		tlu.addActionListener(l -> renew());
		rtlu.setLnr(l -> {
			tlu.setSelected(false);
			remove(tlu);
			remove(rtlu);
			st.lastClear = null;
			renew();
		});
	}

	private void ini() {
		add(jsps);
		add(jl);
		if (st.preset == null)
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
		tmax.setEnabled(st.getLim(jls.getSelectedIndex()).lvr != null);
		testMode.setEnabled(st.getMC().getSave(true) != null);
		if (st.lastClear != null) {
			add(tlu);
			if (st.getMC() instanceof PackMapColc && ((PackMapColc) st.getMC()).pack.editable)
				add(rtlu);
		}
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
        String[] tit;
        String star;
        if (!rand) {
            tit = new String[st.getCont().stars.length];
            star = get(1, "star");
			for (int i = 0; i < st.getCont().stars.length; i++)
				tit[i] = (i + 1) + star + ": " + st.getCont().stars[i] + "%";
        } else {
            tit = new String[5];
            star = get(1, "attempt");
			for (int i = 0; i < 5; i++)
				tit[i] = star + (i + 1);
        }
        jls.setListData(tit);
        jls.setSelectedIndex(0);
		lub.setLimit(st.getLim(!rand ? jls.getSelectedIndex() : -1), st.getMC().getSave(false), st.getCont().price);
		addListeners();
	}

}