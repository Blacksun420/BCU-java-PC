package page.info.edit;

import common.CommonStatic;
import common.pack.PackData;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.stage.CharaGroup;
import common.util.stage.Limit;
import common.util.stage.StageLimit;
import page.*;
import page.pack.CharaGroupPage;
import page.pack.LvRestrictPage;
import page.support.CrossList;
import utilpc.Interpret;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LimitTable extends Page {

	private static final long serialVersionUID = 1L;

	private static String[] limits, rarity, trar;

	static {
		redefine();
	}

	protected static void redefine() {
		limits = Page.get(MainLocale.INFO, "ht1", 7);
		rarity = Page.get(MainLocale.UTIL, "r", 6);
		trar = new String[] { "N", "EX", "R", "SR", "UR", "LR" };
	}

	private final JTF jcmin = new JTF();
	private final JTF jnum = new JTF();
	private final JTF jcmax = new JTF();
	private final JTF jcg = new JTF();
	private final JTF jlr = new JTF();
	private final JTF star = new JTF();
	private final JBTN cgb = new JBTN(MainLocale.INFO, "ht15");
	private final JBTN lrb = new JBTN(MainLocale.INFO, "ht16");
	private final JBTN one = new JBTN(MainLocale.INFO, "row0");
	private final JL rar = new JL(MainLocale.INFO, "ht10");
	private final JTG[] brars = new JTG[6];
	private final JL costo = new JL(MainLocale.INFO, "price");
	private final JTF[] bcost = new JTF[6];
	private final JL cdo = new JL(MainLocale.INFO, "cdo");
	private final JTF[] bcd = new JTF[6];
	private final JTG gcd = new JTG(MainLocale.INFO, "ht22");

	private final JL bank = new JL(MainLocale.INFO, "ht20");
	private final JL cres = new JL(MainLocale.INFO, "ht21");
	private final JTF jban = new JTF();
	private final JTF jcre = new JTF();

	private final CrossList<String> jlco = new CrossList<>(Interpret.getComboFilter(0));
	private final JScrollPane jsco = new JScrollPane(jlco);
	private final JBTN banc = new JBTN(MainLocale.PAGE, "ban0");

	private final JBTN ppage = new JBTN("<");
	private final JBTN npage = new JBTN(">");

	private final UserPack pac;
	private final Page par, main;

	private CharaGroupPage cgp;
	private LvRestrictPage lrp;

	private Limit lim;
	private int page = 0;

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
		star.setEnabled(b);
		for (JTG jtb : brars)
			jtb.setEnabled(b);
        for (JTF jtf : bcost)
			jtf.setEnabled(b);
        for (JTF jtf : bcd)
			jtf.setEnabled(b);
		gcd.setEnabled(b);
		jban.setEnabled(b);
		jcre.setEnabled(b);

		jlco.setEnabled(b);
		banc.setEnabled(b && jlco.getSelectedIndex() != -1);
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
		int w = page == 0 ? 1400 / 8 : 0;
		set(rar, x, y, 0, 0, w, 50);
		for (int i = 0; i < brars.length; i++)
			set(brars[i], x, y, w + w * i, 0, w, 50);
		set(star, x, y, w * 7, 0, w, 50);
		set(cgb, x, y, w * 4, 50, w, 50);
		set(jcg, x, y, w * 5, 50, w, 50);
		set(jcmin, x, y, 0, 50, w, 50);
		set(jcmax, x, y, w, 50, w, 50);
		set(jnum, x, y, w * 2, 50, w, 50);
		set(one, x, y, w * 3, 50, w, 50);
		if (page == 0) {
			set(ppage, x, y, w * 6, 50, w, 50);
			set(npage, x, y, w * 7, 50, w, 50);
		}
		w = page == 1 ? 1400 / 8 : 0;

		set(costo, x, y, 0, 0, w, 50);
		for (int i = 0; i < bcost.length; i++)
			set(bcost[i], x, y, w + w * i, 0, w, 50);
		set(cdo, x, y, 0, 50, w, 50);
		for (int i = 0; i < bcd.length; i++)
			set(bcd[i], x, y, w + w * i, 50, w, 50);
		if (page == 1) {
			set(ppage, x, y, w * 7, 0, w, 50);
			set(npage, x, y, w * 7, 50, w, 50);
		}
		w = page == 2 ? 1400 / 8 : 0;

		set(lrb, x, y, w * 4, 0, w, 50);
		set(jlr, x, y, w * 5, 0, w, 50);
		set(bank, x, y, 0, 0, w, 50);
		set(jban, x, y, w, 0, w, 50);
		set(cres, x, y, 0, 50, w, 50);
		set(jcre, x, y, w, 50, w, 50);
		set(jsco, x, y, w * 2, 0, w * 2, 100);
		set(banc, x, y, w * 4, 50, w, 50);
		set(gcd, x, y, w * 5, 50, w, 50);
		if (page == 2) {
			set(ppage, x, y, w * 6, 50, w, 50);
			set(npage, x, y, w * 7, 50, w, 50);
		}
	}

	protected void setLimit(Limit l) {
		lim = l;
		if (l == null) {
            for (JTG brar : brars)
				brar.setSelected(false);
			for (int i = 0; i < bcost.length; i++)
				bcost[i].setText(trar[i] + ":");
			for (int i = 0; i < bcd.length; i++)
				bcd[i].setText(trar[i] + ":");
			gcd.setSelected(false);
			jcmax.setText(limits[4] + ": ");
			jcmin.setText(limits[3] + ": ");
			jnum.setText(limits[1] + ": ");
			star.setText("");
			one.setText(MainLocale.getLoc(MainLocale.INFO, "row0"));
			jcg.setText("");
			jlr.setText("");
			jban.setText("");
			jcre.setText("");
			jlco.repaint();
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
		StageLimit stli = lim.stageLimit == null ? lim.stageLimit = new StageLimit() : lim.stageLimit;

        for (int i = 0; i < bcost.length; i++)
            bcost[i].setText(trar[i] + ": " + stli.costMultiplier[i] + "%");
        for (int i = 0; i < bcd.length; i++)
            bcd[i].setText(trar[i] + ": " + stli.cooldownMultiplier[i] + "%");
        gcd.setSelected(stli.coolStart);
        jban.setText(String.valueOf(stli.maxMoney));
        jcre.setText(String.valueOf(stli.globalCooldown));

        jcmax.setText(limits[4] + ": " + lim.max);
		jcmin.setText(limits[3] + ": " + lim.min);
		jnum.setText(limits[1] + ": " + lim.num);
		star.setText(l.toString());//l.star == -1 ? "all stars" : ((l.star + 1) + " star"));
		one.setText(MainLocale.getLoc(MainLocale.INFO, "row" + lim.line));
		jcg.setText(lim.group + (lim.group != null && lim.group.type % 2 != 0 ? ": " + lim.fa : ""));
		jlr.setText("" + lim.lvr);
		jlco.repaint();
	}

	private void addListeners() {

		one.addActionListener(arg0 -> {
			lim.line = (lim.line + 1) % 3;
			one.setText(MainLocale.getLoc(MainLocale.INFO, "row" + lim.line));
		});

		gcd.setLnr(e -> lim.stageLimit.coolStart = gcd.isSelected());

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

		jlco.addListSelectionListener(x -> {
			banc.setEnabled(jlco.getSelectedIndex() != -1);
			banc.setText(MainLocale.PAGE, "ban" + (!lim.stageLimit.bannedCatCombo.contains(jlco.getSelectedIndex()) ? "0" : "1"));
		});

		banc.setLnr(x -> {
			if (lim.stageLimit == null || jlco.getSelectedIndex() == -1)
				return;

			if (lim.stageLimit.bannedCatCombo.contains(jlco.getSelectedIndex())) {
				lim.stageLimit.bannedCatCombo.remove(jlco.getSelectedIndex());
				banc.setText(MainLocale.PAGE, "ban0");
			} else {
				lim.stageLimit.bannedCatCombo.add(jlco.getSelectedIndex());
				banc.setText(MainLocale.PAGE, "ban1");
			}
			jlco.repaint();
		});

		ppage.addActionListener(l -> {
			page--;
			ppage.setEnabled(page > 0);
			npage.setEnabled(true);
			main.fireDimensionChanged();
		});

		npage.addActionListener(l -> {
			page++;
			ppage.setEnabled(true);
			npage.setEnabled(page < 2);
			main.fireDimensionChanged();
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
		set(star);
		add(gcd);
		for (int i = 0; i < brars.length; i++) {
			add(brars[i] = new JTG(rarity[i]));
			brars[i].setSelected(true);
		}
		for (int i = 0; i < bcost.length; i++)
			set(bcost[i] = new JTF(trar[i] + ":"));
		for (int i = 0; i < bcd.length; i++)
			set(bcd[i] = new JTF(trar[i] + ":"));
		add(costo);
		add(cdo);
		add(bank);
		set(jban);
		add(cres);
		set(jcre);

		add(jsco);
		add(banc);

		add(ppage);
		ppage.setEnabled(false);
		add(npage);

		jlco.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlco.setCheck(i -> lim != null && lim.stageLimit.bannedCatCombo.contains(i));
		addListeners();
	}

	private void input(JTF jtf, String str) {
		int val = CommonStatic.parseIntN(str);
		if (jtf == jcmax) {
			if (val < 0)
				return;
			lim.max = val;
		} else if (jtf == jcmin) {
			if (val < 0)
				return;
			lim.min = val;
		} else if (jtf == jnum) {
			if (val < 0 || val > 50)
				return;
			lim.num = val;
		} else if (jtf == star) {
			if (isAdj())
				return;
			int[] is = CommonStatic.parseIntsN(star.getText());

			int bitmask = 0;
			for (int j : is)
				if (j >= 1 && j <= 4)
					bitmask |= 1 << (j-1);
			lim.star = bitmask;
		} else if (jtf == jban)
			lim.stageLimit.maxMoney = Math.max(CommonStatic.parseIntN(str), 0);
		else if (jtf == jcre)
			lim.stageLimit.globalCooldown = Math.max(CommonStatic.parseIntN(str), 0);
		for (int i = 0; i < bcost.length; i++) {
			if (jtf == bcost[i]) {
				lim.stageLimit.costMultiplier[i] = Math.max(0, CommonStatic.parseIntN(str));
				break;
			}
		}
		for (int i = 0; i < bcd.length; i++) {
			if (jtf == bcd[i]) {
				lim.stageLimit.cooldownMultiplier[i] = Math.max(0, CommonStatic.parseIntN(str));
				break;
			}
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