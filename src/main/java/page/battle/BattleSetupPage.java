package page.battle;

import common.CommonStatic;
import common.battle.BasisLU;
import common.battle.BasisSet;
import common.pack.SortedPackSet;
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

import javax.swing.*;
import java.util.Vector;

public class BattleSetupPage extends LubCont {

	private static final long serialVersionUID = 1L;

	private final JBTN back = new JBTN(0, "back");
	private final JBTN strt = new JBTN(0, "start");
	private final JBTN tmax = new JBTN(0, "tomax");
	private final JTG rich = new JTG(0, "rich");
	private final JTG snip = new JTG(0, "sniper");
	private final JTG plus = new JTG(MainLocale.PAGE, "plusunlock");
	private final JComboBox<String> lvlim = new JComboBox<>();
	private final JList<String> jls = new JList<>();
	private final JScrollPane jsps = new JScrollPane(jls);
	private final JLabel jl = new JLabel();
	private final JBTN jlu = new JBTN(0, "line");
	private final LineUpBox lub = new LineUpBox(this);

	private final Stage st;

	private final int conf;

	public BattleSetupPage(Page p, Stage s, int confs) {
		super(p);
		st = s;
		conf = confs;
		ini();
		resized(true);
	}

	@Override
    public JButton getBackButton() {
		return back;
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
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(jsps, x, y, 50, 100, 200, 200);
		set(jl, x, y, 50, 350, 200, 50);
		set(jlu, x, y, 50, 400, 200, 50);
		set(strt, x, y, 50, 500, 200, 50);
		set(rich, x, y, 300, 100, 200, 50);
		set(snip, x, y, 300, 200, 200, 50);
		set(tmax, x, y, 300, 500, 200, 50);
		set(lub, x, y, 550, 50, 600, 300);
		set(plus, x, y, 1200, 100, 200, 50);
		set(lvlim, x, y, 1200, 200, 200, 50);
	}

	private void addListeners() {
		back.addActionListener(arg0 -> changePanel(getFront()));

		jls.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			if (jls.getSelectedIndex() == -1)
				jls.setSelectedIndex(0);
			lub.setLimit(st.getLim(jls.getSelectedIndex()), st.getCont().getCont().getSave(false), st.getCont().price);
		});

		jlu.addActionListener(arg0 -> changePanel(new BasisPage(getThis(), st, st.getLim(conf == 1 ? jls.getSelectedIndex() : -1))));

		strt.addActionListener(arg0 -> {
			int star = jls.getSelectedIndex();
			int[] ints = new int[1];
			if (rich.isSelected())
				ints[0] |= 1;
			if (snip.isSelected())
				ints[0] |= 2;
			BasisLU b = BasisSet.current().sele;
			if (conf == 0) {
				b = RandStage.getLU(star);
				star = 0;
			}
			changePanel(new BattleInfoPage(getThis(), st, star, b, ints));
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
	}

	private void ini() {
		add(back);
		add(jsps);
		add(jl);
		add(jlu);
		add(strt);
		add(rich);
		add(snip);
		add(tmax);
		add(lub);
		tmax.setEnabled(st.lim != null && st.lim.lvr != null);
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
