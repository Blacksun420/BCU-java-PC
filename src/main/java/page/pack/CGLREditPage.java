package page.pack;

import common.CommonStatic;
import common.pack.FixIndexList.FixIndexMap;
import common.pack.Identifier;
import common.pack.PackData.UserPack;
import common.pack.SortedPackSet;
import common.util.Data;
import common.util.stage.CharaGroup;
import common.util.stage.LvRestrict;
import common.util.unit.AbForm;
import common.util.unit.Form;
import common.util.unit.Level;
import page.JBTN;
import page.JTF;
import page.MainLocale;
import page.Page;
import page.info.filter.UnitFindPage;
import page.support.AnimLCR;
import page.support.ReorderList;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import static utilpc.Interpret.RARITY;

public class CGLREditPage extends Page {

	private static final long serialVersionUID = 1L;
	private static final SortedPackSet<CharaGroup> copiedCGs = new SortedPackSet<>();
	private static final ArrayList<LvRestrict> copiedLRs = new ArrayList<>();

	private final JBTN back = new JBTN(MainLocale.PAGE, "back");
	private final JList<CharaGroup> jlcg = new JList<>();
	private final JList<CharaGroup> jlsb = new JList<>();
	private final JList<LvRestrict> jllr = new JList<>();
	private final ReorderList<Form> jlus = new ReorderList<>();
	private final ReorderList<AbForm> jlua = new ReorderList<>();
	private final JScrollPane jspcg = new JScrollPane(jlcg);
	private final JScrollPane jspsb = new JScrollPane(jlsb);
	private final JScrollPane jsplr = new JScrollPane(jllr);
	private final JScrollPane jspus = new JScrollPane(jlus);
	private final JScrollPane jspua = new JScrollPane(jlua);

	private final JBTN cgt = new JBTN(MainLocale.PAGE, "include");

	private final JBTN addcg = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remcg = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN addus = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remus = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN addlr = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remlr = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN addsb = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remsb = new JBTN(MainLocale.PAGE, "rem");

	private final JTF jtfsb = new JTF();
	private final JTF jtfal = new JTF();
	private final JTF[] jtfra = new JTF[Data.RARITY_TOT];
	private final JTF jtfna = new JTF();
	private final JTF jtflr = new JTF();

	private final JBTN vuif = new JBTN(0, "vuif");

	private final UserPack pack;
	private final FixIndexMap<CharaGroup> lcg;
	private final FixIndexMap<LvRestrict> llr;

	private boolean changing = false;
	private CharaGroup cg;
	private CharaGroup sb;
	private LvRestrict lr;
	private UnitFindPage ufp;

	private final JList<CharaGroup> jcpc = new JList<>();
	private final JScrollPane jscpc = new JScrollPane(jcpc);
	private final JList<LvRestrict> jclr = new JList<>();
	private final JScrollPane jsclr = new JScrollPane(jclr);
	private final JBTN cpc = new JBTN(MainLocale.PAGE, "copy");
	private final JBTN ppc = new JBTN(MainLocale.PAGE, "paste");
	private final JBTN rpc = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN clr = new JBTN(MainLocale.PAGE, "copy");
	private final JBTN plr = new JBTN(MainLocale.PAGE, "paste");
	private final JBTN rlr = new JBTN(MainLocale.PAGE, "rem");

	protected CGLREditPage(Page p, UserPack pac) {
		super(p);

		pack = pac;
		lcg = pack.groups;
		llr = pack.lvrs;
		ini();
		resized();
	}

	@Override
    public JButton getBackButton() {
		return back;
	}

	@Override
	protected void renew() {
		if (ufp != null && ufp.getList() != null) {
			changing = true;
			List<AbForm> list = new ArrayList<>(ufp.getList());
			if (cg != null)
				for (Form f : cg.fset)
					list.remove(f);
			jlua.setListData(list.toArray(new AbForm[0]));
			jlua.clearSelection();
			if (list.size() > 0)
				jlua.setSelectedIndex(0);
			changing = false;
		}
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(jspcg, x, y, 50, 100, 300, 800);
		set(addcg, x, y, 50, 950, 150, 50);
		set(remcg, x, y, 200, 950, 150, 50);
		set(cgt, x, y, 400, 100, 300, 50);
		set(jspus, x, y, 400, 200, 300, 700);
		set(addus, x, y, 400, 950, 150, 50);
		set(remus, x, y, 550, 950, 150, 50);
		set(vuif, x, y, 750, 100, 300, 50);
		set(jspua, x, y, 750, 200, 300, 700);
		set(jsplr, x, y, 1100, 100, 300, 800);
		set(addlr, x, y, 1100, 950, 150, 50);
		set(remlr, x, y, 1250, 950, 150, 50);
		set(jspsb, x, y, 1450, 100, 300, 800);
		set(addsb, x, y, 1450, 950, 150, 50);
		set(remsb, x, y, 1600, 950, 150, 50);
		set(jtfal, x, y, 1800, 100, 400, 50);
		set(jtfsb, x, y, 1800, 550, 400, 50);
		set(jtfna, x, y, 50, 900, 300, 50);
		set(jtflr, x, y, 1100, 900, 300, 50);
		for (int i = 0; i < jtfra.length; i++)
			set(jtfra[i], x, y, 1800, 200 + 50 * i, 400, 50);

		set(jscpc, x, y, 50, 1050, 300, 150);
		set(cpc, x, y, 50, 1000, 150, 50);
		set(ppc, x, y, 200, 1000, 150, 50);
		set(rpc, x, y, 50, 1200, 300, 50);

		set(jsclr, x, y, 1100, 1050, 300, 150);
		set(clr, x, y, 1100, 1000, 150, 50);
		set(plr, x, y, 1250, 1000, 150, 50);
		set(rlr, x, y, 1100, 1200, 300, 50);
	}

	private void addListeners() {

		back.addActionListener(arg0 -> changePanel(getFront()));

		vuif.addActionListener(arg0 -> {
			if (ufp == null)
				ufp = new UnitFindPage(getThis(), false, pack);
			changePanel(ufp);
		});

	}

	private void addListeners$CG() {

		addcg.addActionListener(arg0 -> {
			changing = true;
			cg = new CharaGroup(pack.getNextID(CharaGroup.class));
			lcg.add(cg);
			updateCGL();
			jlcg.setSelectedValue(cg, true);
			changing = false;
		});

		remcg.addActionListener(arg0 -> {
			if (cg == null)
				return;
			changing = true;
			List<CharaGroup> list = lcg.getList();
			int ind = list.indexOf(cg) - 1;
			if (ind < 0 && list.size() > 1)
				ind = 0;
			list.remove(cg);
			lcg.remove(cg);
			if (ind >= 0)
				cg = list.get(ind);
			else
				cg = null;
			updateCGL();
			changing = false;
		});

		jlcg.addListSelectionListener(arg0 -> {
			if (changing || jlcg.getValueIsAdjusting())
				return;
			changing = true;
			cg = jlcg.getSelectedValue();
			updateCG();
			changing = false;
		});

		addus.addActionListener(arg0 -> {
			List<AbForm> u = jlua.getSelectedValuesList();
			if (cg == null || u.size() == 0)
				return;
			changing = true;
			cg.fset.addAll(u);
			updateCG();
			jlus.setSelectedValue(u.get(0), true);
			changing = false;
		});

		remus.addActionListener(arg0 -> {
			Form u = jlus.getSelectedValue();
			if (cg == null || u == null)
				return;
			changing = true;
			List<Form> list = new ArrayList<>(cg.fset);
			int ind = list.indexOf(u) - 1;
			if (ind < 0 && list.size() > 1)
				ind = 0;
			cg.fset.remove(u);
			updateCG();
			jlus.setSelectedIndex(ind);
			changing = false;
		});

		cgt.addActionListener(arg0 -> {
			if (cg == null)
				return;
			cg.type = (cg.type + 1) % 4;
			cgt.setText(0, cg.type == 0 ? "include" : cg.type == 1 ? "at least" : cg.type == 2 ? "exclude" : "at most");
		});

		jtfna.setLnr(x -> {
			String str = jtfna.getText();
			if (cg.name.equals(str))
				return;
			cg.name = str;
		});

		jcpc.addListSelectionListener(arg0 -> {
			if (changing || jlsb.getValueIsAdjusting())
				return;
			changing = true;
			updateCGC(false);
			changing = false;
		});

		cpc.addActionListener(c -> {
			copiedCGs.add(cg);
			updateCGC(true);
		});

		ppc.addActionListener(c -> {
			changing = true;
			CharaGroup pcg = new CharaGroup(pack.getNextID(CharaGroup.class), jcpc.getSelectedValue());
			pcg.fset.removeIf(f -> !(f.getID().pack.equals(Identifier.DEF) || f.getID().pack.equals(pack.getSID()) || pack.desc.dependency.contains(f.getID().pack)));

			lcg.add(pcg);
			updateCGL();
			jlcg.setSelectedValue(pcg, true);
			changing = false;
		});

		rpc.addActionListener(arg0 -> {
			copiedCGs.remove(jcpc.getSelectedValue());
			updateCGC(true);
		});
	}

	private void addListeners$LR() {

		addlr.addActionListener(arg0 -> {
			changing = true;
			lr = new LvRestrict(pack.getNextID(LvRestrict.class));
			llr.add(lr);
			updateLRL();
			jllr.setSelectedValue(lr, true);
			changing = false;
		});

		remlr.addActionListener(arg0 -> {
			if (lr == null)
				return;
			changing = true;
			List<LvRestrict> list = llr.getList();
			int ind = list.indexOf(lr) - 1;
			if (ind < 0 && list.size() > 1)
				ind = 0;
			list.remove(lr);
			llr.remove(lr);
			if (ind >= 0)
				lr = list.get(ind);
			else
				lr = null;
			updateLRL();
			changing = false;
		});

		jllr.addListSelectionListener(arg0 -> {
			if (changing || jllr.getValueIsAdjusting())
				return;
			changing = true;
			lr = jllr.getSelectedValue();
			updateLR();
			changing = false;
		});

		addsb.addActionListener(arg0 -> {
			changing = true;
			lr.cgl.put(cg, LvRestrict.MAX.clone());
			sb = cg;
			updateLR();
			changing = false;
		});

		remsb.addActionListener(arg0 -> {
			if (sb == null)
				return;
			changing = true;
			int ind = jlsb.getSelectedIndex();
			lr.cgl.remove(sb);
			updateLR();
			if (lr.cgl.size() >= ind)
				ind = lr.cgl.size() - 1;
			jlsb.setSelectedIndex(ind);
			sb = jlsb.getSelectedValue();
			updateSB();
			changing = false;
		});

		jlsb.addListSelectionListener(arg0 -> {
			if (changing || jlsb.getValueIsAdjusting())
				return;
			changing = true;
			sb = jlsb.getSelectedValue();
			updateSB();
			changing = false;
		});

		jtflr.setLnr(x -> {
			String str = jtflr.getText();
			if (lr.name.equals(str))
				return;
			lr.name = str;
		});

		jclr.addListSelectionListener(arg0 -> {
			if (changing || jlsb.getValueIsAdjusting())
				return;
			changing = true;
			updateLRC(false);
			changing = false;
		});

		clr.addActionListener(c -> {
			copiedLRs.add(lr);
			updateLRC(true);
		});

		plr.addActionListener(c -> {
			changing = true;
			lr = new LvRestrict(pack.getNextID(LvRestrict.class), jclr.getSelectedValue());
			lr.cgl.keySet().removeIf(cg -> !(cg.getID().pack.equals(Identifier.DEF) || cg.getID().pack.equals(pack.getSID()) || pack.desc.dependency.contains(cg.getID().pack)));

			llr.add(lr);
			updateLRL();
			jllr.setSelectedValue(lr, true);
			changing = false;
		});

		rlr.addActionListener(arg0 -> {
			copiedLRs.remove(jclr.getSelectedValue());
			updateLRC(true);
		});
	}

	private void ini() {
		add(back);
		add(jspcg);
		add(addcg);
		add(remcg);
		add(jspus);
		add(addus);
		add(remus);
		add(jsplr);
		add(addlr);
		add(remlr);
		add(jspsb);
		add(addsb);
		add(remsb);
		add(vuif);
		add(jspua);
		add(cgt);
		set(jtfsb);
		set(jtfal);
		set(jtfna);
		set(jtflr);
		for (int i = 0; i < jtfra.length; i++)
			set(jtfra[i] = new JTF());
		add(jscpc);
		add(cpc);
		add(ppc);
		add(rpc);

		add(jsclr);
		add(clr);
		add(plr);
		add(rlr);
		jlus.setCellRenderer(new AnimLCR());
		jlua.setCellRenderer(new AnimLCR());
		updateCGL();
		updateCGC(true);
		updateLRL();
		updateLRC(true);
		addListeners();
		addListeners$CG();
		addListeners$LR();
	}

	private void put(Level tar, int[] val) {
		if (val.length == 1) {
			tar.setLevel(Math.max(1, Math.min(val[0], 200 - tar.getPlusLv())));
			tar.setTalents(new int[0]);
		} else {
			tar.setLevel(Math.max(1, Math.min(val[0], 200 - val[1])));
			tar.setPlusLevel(Math.max(0, Math.min(val[1], 200 - tar.getLv())));

			int[] nps = new int[Math.min(Data.PC_CORRES.length + Data.PC_CUSTOM.length, val.length - 2)];
			System.arraycopy(val, 2, nps, 0, nps.length);
			tar.setTalents(nps);
		}
	}

	private void set(JTF jtf) {
		add(jtf);

		jtf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				int[] inp = CommonStatic.parseIntsN(jtf.getText());
				for (int i = 0; i < inp.length; i++)
					if (inp[i] < 0)
						inp[i] = 0;
				if (jtf == jtfal)
					put(lr.def, inp);
				if (jtf == jtfsb)
					put(lr.cgl.get(sb), inp);
				for (int i = 0; i < jtfra.length; i++)
					if (jtf == jtfra[i])
						put(lr.rs[i], inp);
				updateSB();
			}

		});

	}

	private void set(JTF jtf, String str, Level lvs) {
		if (lvs != null)
			str += Level.lvString(lvs);
		jtf.setText(str);
	}

	private void updateCG() {
		jlus.setEnabled(cg != null);
		addus.setEnabled(cg != null);
		remus.setEnabled(cg != null);
		remcg.setEnabled(cg != null && !cg.used());
		cgt.setEnabled(cg != null);
		jtfna.setEnabled(cg != null);
		cgt.setText("");
		jtfna.setText("");
		addsb.setEnabled(lr != null && cg != null && !lr.cgl.containsKey(cg));
		cpc.setEnabled(cg != null && !copiedCGs.contains(cg));

		if (cg == null)
			jlus.setListData(new Form[0]);
		else {
			jlus.setListData(cg.fset.toArray(new Form[0]));
			cgt.setText(0, cg.type == 0 ? "include" : cg.type == 1 ? "at least" : cg.type == 2 ? "exclude" : "at most");
			jtfna.setText(cg.name);
		}
	}

	private void updateCGC(boolean ul) {
		if (ul)
			jcpc.setListData(copiedCGs.toArray(new CharaGroup[0]));
		CharaGroup cg = jcpc.getSelectedValue();
		cpc.setEnabled(this.cg != null && !copiedCGs.contains(this.cg));
		ppc.setEnabled(cg != null);
		rpc.setEnabled(cg != null);
	}

	private void updateCGL() {
		jlcg.setListData(lcg.toArray());
		jlcg.setSelectedValue(cg, true);
		updateCG();
	}

	private void updateLR() {
		remlr.setEnabled(lr != null && !lr.used());
		jlsb.setEnabled(lr != null);
		addsb.setEnabled(lr != null && cg != null && !lr.cgl.containsKey(cg));
		jtflr.setEnabled(lr != null);
		clr.setEnabled(lr != null && !copiedLRs.contains(lr));
		jtflr.setText("");
		if (lr == null)
			jlsb.setListData(new CharaGroup[0]);
		else {
			jlsb.setListData(lr.cgl.keySet().toArray(new CharaGroup[0]));
			jtflr.setText(lr.name);
		}
		if (lr == null || sb == null || !lr.cgl.containsKey(sb))
			sb = null;
		jlsb.setSelectedValue(sb, true);
		jtfal.setEnabled(lr != null);
		for (JTF jtf : jtfra)
			jtf.setEnabled(lr != null);
		updateSB();
	}

	private void updateLRC(boolean ul) {
		if (ul)
			jclr.setListData(copiedLRs.toArray(new LvRestrict[0]));
		LvRestrict lr = jclr.getSelectedValue();
		clr.setEnabled(this.lr != null && !copiedLRs.contains(this.lr));
		plr.setEnabled(lr != null);
		rlr.setEnabled(lr != null);
	}

	private void updateLRL() {
		jllr.setListData(llr.toArray());
		jllr.setSelectedValue(lr, true);
		updateLR();
	}

	private void updateSB() {
		jtfsb.setEnabled(sb != null);

		if (lr != null) {
			set(jtfal, "all: ", lr.def);
			for (int i = 0; i < jtfra.length; i++)
				set(jtfra[i], RARITY[i] + ": ", lr.rs[i]);
		} else {
			set(jtfal, "all: ", null);
			for (int i = 0; i < jtfra.length; i++)
				set(jtfra[i], RARITY[i] + ": ", null);
		}

		if (lr == null || sb == null)
			set(jtfsb, "group: ", null);
		else
			set(jtfsb, "group: ", lr.cgl.get(sb));
		remsb.setEnabled(sb != null);
	}

}
