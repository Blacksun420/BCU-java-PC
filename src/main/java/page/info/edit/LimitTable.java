package page.info.edit;

import common.CommonStatic;
import common.pack.PackData;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.stage.CharaGroup;
import common.util.stage.Limit;
import page.*;
import page.pack.CharaGroupPage;
import page.pack.LvRestrictPage;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LimitTable extends Page {

	private static final long serialVersionUID = 1L;

	private static String[] limits, rarity;

	static {
		redefine();
	}

	protected static void redefine() {
		limits = Page.get(MainLocale.INFO, "ht1", 7);
		rarity = Page.get(MainLocale.UTIL, "r", 6);
	}

	private final JTF jcmin = new JTF();
	private final JTF jnum = new JTF();
	private final JTF jcmax = new JTF();
	private final JTF jcg = new JTF();
	private final JTF jlr = new JTF();
	private final JBTN cgb = new JBTN(MainLocale.INFO, "ht15");
	private final JBTN lrb = new JBTN(MainLocale.INFO, "ht16");
	private final JBTN one = new JBTN(MainLocale.INFO, "row0");
	private final JL rar = new JL(MainLocale.INFO, "ht10");
	private final JTG[] brars = new JTG[6];

	private final UserPack pac;
	private final Page par, main;

	private CharaGroupPage cgp;
	private LvRestrictPage lrp;

	private Limit lim;

	protected LimitTable(Page p0, Page p1, UserPack p) {
		super(null);
		main = p0;
		par = p1;
		pac = p;
		ini();
	}

	protected void abler(boolean b) {
		jcmin.setEnabled(b);
		jnum.setEnabled(b);
		jcmax.setEnabled(b);
		one.setEnabled(b);
		cgb.setEnabled(b);
		jcg.setEnabled(b);
		lrb.setEnabled(b);
		jlr.setEnabled(b);
		for (JTG jtb : brars)
			jtb.setEnabled(b);
	}

	@Override
    public JButton getBackButton() {
		return null;
	}

	@Override
	protected void renew() {
		if (cgp != null) {
			jcg.setText(cgp.cg + (cgp.cg != null && cgp.cg.type % 2 != 0 ? ": " + lim.fa : ""));
			lim.group = cgp.cg;
		}
		if (lrp != null) {
			jlr.setText("" + lrp.lr);
			lim.lvr = lrp.lr;
		}
		cgp = null;
		lrp = null;
	}

	@Override
	protected void resized(int x, int y) {
		int w = 1400 / 8;
		set(rar, x, y, 0, 0, w, 50);
		for (int i = 0; i < brars.length; i++)
			set(brars[i], x, y, w + w * i, 0, w, 50);
		set(cgb, x, y, w * 4, 50, w, 50);
		set(jcg, x, y, w * 5, 50, w, 50);
		set(lrb, x, y, w * 6, 50, w, 50);
		set(jlr, x, y, w * 7, 50, w, 50);
		set(jcmin, x, y, 0, 50, w, 50);
		set(jcmax, x, y, w, 50, w, 50);
		set(jnum, x, y, w * 2, 50, w, 50);
		set(one, x, y, w * 3, 50, w, 50);
	}

	protected void setLimit(Limit l) {
		lim = l;
		if (l == null) {
			for (int i = 0; i < brars.length; i++)
				brars[i].setSelected(false);
			jcmax.setText(limits[4] + ": ");
			jcmin.setText(limits[3] + ": ");
			jnum.setText(limits[1] + ": ");
			one.setText(MainLocale.getLoc(MainLocale.INFO, "row0"));
			jcg.setText("");
			jlr.setText("");
			abler(false);
			return;
		}
		abler(true);
		if (lim.rare > 0) {
			for (int i = 0; i < brars.length; i++)
				brars[i].setSelected(((lim.rare >> i) & 1) > 0);
		} else
			for (JTG brar : brars)
				brar.setSelected(true);
		jcmax.setText(limits[4] + ": " + lim.max);
		jcmin.setText(limits[3] + ": " + lim.min);
		jnum.setText(limits[1] + ": " + lim.num);
		one.setText(MainLocale.getLoc(MainLocale.INFO, "row" + lim.line));
		jcg.setText(lim.group + (lim.group != null && lim.group.type % 2 != 0 ? ": " + lim.fa : ""));
		jlr.setText("" + lim.lvr);
	}

	private void addListeners() {

		one.addActionListener(arg0 -> {
			lim.line = (lim.line + 1) % 3;
			one.setText(MainLocale.getLoc(MainLocale.INFO, "row" + lim.line));
		});

		for (int i = 0; i < brars.length; i++) {
			int I = i;
			brars[i].addActionListener(e -> {
				if (par.isAdj())
					return;
				lim.rare ^= 1 << I;
				par.callBack(lim);
			});
		}

		cgb.addActionListener(arg0 -> {
			cgp = new CharaGroupPage(main, pac, false);
			changePanel(cgp);
		});

		lrb.addActionListener(arg0 -> {
			lrp = new LvRestrictPage(main, pac, false);
			changePanel(lrp);
		});

		jcg.addActionListener(l -> {
			String[] strs = jcg.getText().split("/");
			if (strs.length < 2)
				return;
			PackData pk = UserProfile.getPack(strs[0]);
			if (pk != null) {
				int[] ints = CommonStatic.parseIntsN(strs[1]);
				CharaGroup cg = pk.groups.get(ints[0]);
				if (cg != null)
					lim.group = cg;
				if (ints.length >= 2)
					lim.fa = Math.max(0, Math.min(ints[1], Math.min(lim.group.fset.size(), 10)));
			} else
				lim.group = null;
		});
	}

	private void ini() {
		set(rar);
		add(cgb);
		add(lrb);
		add(one);
		set(jcmin);
		set(jcmax);
		set(jnum);
		set(jcg);
		set(jlr);
		for (int i = 0; i < brars.length; i++) {
			add(brars[i] = new JTG(rarity[i]));
			brars[i].setSelected(true);
		}
		addListeners();
	}

	private void input(JTF jtf, String str) {
		int val = CommonStatic.parseIntN(str);
		if (jtf == jcmax) {
			if (val < 0)
				return;
			lim.max = val;
		}
		if (jtf == jcmin) {
			if (val < 0)
				return;
			lim.min = val;
		}
		if (jtf == jnum) {
			if (val < 0 || val > 50)
				return;
			lim.num = val;
		}
	}

	private void set(JLabel jl) {
		jl.setHorizontalAlignment(SwingConstants.CENTER);
		jl.setBorder(BorderFactory.createEtchedBorder());
		add(jl);
	}

	private void set(JTF jtf) {
		add(jtf);

		jtf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				if (par.isAdj())
					return;
				input(jtf, jtf.getText());
				par.callBack(lim);
			}
		});

	}

}
