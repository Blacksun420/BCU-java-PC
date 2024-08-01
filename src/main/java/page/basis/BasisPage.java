package page.basis;

import common.CommonStatic;
import common.battle.BasisLU;
import common.battle.BasisSet;
import common.battle.LineUp;
import common.pack.UserProfile;
import common.system.Node;
import common.util.pack.NyCastle;
import common.util.stage.Limit;
import common.util.stage.Stage;
import common.util.unit.*;
import page.*;
import page.info.TreaTable;
import page.support.ReorderList;
import page.support.ReorderListener;
import page.support.UnitLCR;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static common.battle.BasisSet.current;

public class BasisPage extends LubCont {

	private static final long serialVersionUID = 1L;

	private final JBTN unit = new JBTN(0, "vuif");
	private final JBTN setc = new JBTN(0, "set0");
	private final JBTN bsadd = new JBTN(0, "add");
	private final JBTN bsrem = new JBTN(0, "rem");
	private final JBTN bscop = new JBTN(0, "copy");
	private final JBTN badd = new JBTN(0, "add");
	private final JBTN brem = new JBTN(0, "rem");
	private final JBTN bcop = new JBTN(0, "copy");
	private final JBTN form = new JBTN(0, "form");
	private final JBTN reset = new JBTN(0, "renew");
	private final JTG combo = new JTG(0, "combo");
	private final JTF bsjtf = new JTF();
	private final JTF bjtf = new JTF();
	private final JTF lvjtf = new JTF();
	private final JTF cjtf = new JTF();
	private final JTF ujtf = new JTF();
	private final JBTN setpref = new JBTN(0, "setpref");
	private final JBTN lvorb = new JBTN(0, "orb");
	private final JLabel pcoin = new JLabel();
	private final Vector<BasisSet> vbs = new Vector<>(BasisSet.list());
	private final ReorderList<BasisSet> jlbs = new ReorderList<>(vbs);
	private final JScrollPane jspbs = new JScrollPane(jlbs);
	private final Vector<BasisLU> vb = new Vector<>();
	private final ReorderList<BasisLU> jlb = new ReorderList<>(vb, BasisLU.class, "lineup");
	private final JScrollPane jspb = new JScrollPane(jlb);
	private final JList<String> jlcs = new JList<>(Interpret.COMF);
	private final JScrollPane jspcs = new JScrollPane(jlcs);
	private final JList<String> jlcl = new JList<>();
	private final JScrollPane jspcl = new JScrollPane(jlcl);
	private final ComboListTable jlc = new ComboListTable(this, lu());
	private final JScrollPane jspc = new JScrollPane(jlc);
	private final ModifierList jlcn = new ModifierList();
	private final JScrollPane jspcn = new JScrollPane(jlcn);
	private final LineUpBox lub = new LineUpBox(this);
	private final JList<AbForm> ul = new JList<>();
	private final JScrollPane jspul = new JScrollPane(ul);
	private final NyCasBox ncb = new NyCasBox();
	private final JBTN[] jbcsR = new JBTN[3];
	private final JBTN[] jbcsL = new JBTN[3];
	private final JTG cost = new JTG(1, "price");

	private boolean changing = false, outside = false;

	private UnitFLUPage ufp;
	private final Stage st;

	private final TreaTable trea = new TreaTable(this);
	private final JScrollPane jspt = new JScrollPane(trea);
	private AbUnit cunit;

    private String comboName = "";

	public BasisPage(Page p) {
		super(p);
		st = null;

        ini();
	}

	public BasisPage(Page p, Stage st, Limit lim, boolean test) {
		super(p);
		this.st = st;
		lub.setLimit(lim, st.getMC().getSave(false), st.getCont().price);
		if (st.getCont().stageLimit != null)
			jlcn.setBanned(st.getCont().stageLimit.bannedCatCombo);
        if (test)
			lub.setTest(st.getMC().getSave(true).getUnlockedsBeforeStage(st, true).keySet());
		ini();
	}

	@Override
	public void callBack(Object o) {
		if (o == null)
			changeLU();
		else if (o instanceof Unit) {
			Unit unit = (Unit) o;
			if (cunit != null && unit.compareTo(cunit) == 0)
				return;
			combo.setSelected(true);
			lub.select(unit.forms[unit.forms.length - 1]);
		} else if (o instanceof Form) {
			Unit unit = ((Form) o).unit;
			setLvs((Form) o);
			if (!combo.isSelected())
				return;
			if (cunit != null && unit.compareTo(cunit) == 0)
				return;
			cunit = unit;
			outside = true;
			changing = true;
			setCL(jlcs.getSelectedIndex());
			changing = false;
		} else if (o instanceof UniRand) {
			UniRand ur = (UniRand)o;
			setLvs(ur);
			if (cunit != null && ur.compareTo(cunit) == 0)
				return;
			changing = true;
			cunit = ur;
			outside = true;
			lub.select(ur);
			changing = false;
		}
	}

	@Override
	protected LineUpBox getLub() {
		return lub;
	}

	@Override
	protected void keyTyped(KeyEvent e) {
		if (trea.isFocusOwner())
			return;
		if (lvjtf.isFocusOwner())
			return;
		if (bjtf.isFocusOwner())
			return;
		if (bsjtf.isFocusOwner())
			return;
		if (cjtf.isFocusOwner())
			return;
		if (ujtf.isFocusOwner())
			return;
		super.keyTyped(e);
		e.consume();
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		if (e.getSource() == jlc)
			jlc.clicked(e.getPoint());
		super.mouseClicked(e);
	}

	@Override
	protected void renew() {
		if (ufp != null) {
			List<AbForm> lf = ufp.getList();
			if (lf != null)
				ul.setListData(Node.deRep(lf).toArray(new AbForm[0]));
		}
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);

		set(jspbs, x, y, 50, 100, 200, 500);
		set(bsadd, x, y, 50, 600, 200, 50);
		set(bsrem, x, y, 50, 650, 200, 50);
		set(bscop, x, y, 50, 700, 200, 50);
		set(bsjtf, x, y, 50, 750, 200, 50);

		set(jspb, x, y, 275, 100, 200, 500);
		set(badd, x, y, 275, 600, 200, 50);
		set(brem, x, y, 275, 650, 200, 50);
		set(bcop, x, y, 275, 700, 200, 50);
		set(bjtf, x, y, 275, 750, 200, 50);

		set(unit, x, y, 1450, 100, 325, 50);
		set(ujtf, x, y, 1450, 750, 325, 50);
		set(jspul, x, y, 1450, 150, 325, 600);
		set(jspt, x, y, 1775, 100, 425, 700);

		set(jspc, x, y, 50, 800, 1550, 450);
		set(jspcs, x, y, 1600, 800, 300, 450);
		set(jspcl, x, y, 1900, 800, 300, 450);
		set(cjtf, x, y, 500, 750, 600, 50);
		set(setc, x, y, 1100, 750, 150, 50);
		set(combo, x, y, 1250, 750, 150, 50);

		set(pcoin, x, y, 500, 50, 1200, 50);
		set(lvjtf, x, y, 500, 100, 500, 50);
		set(setpref, x, y, 1000, 100, 200, 50);
		set(lvorb, x, y, 1200, 100, 200, 50);
		set(form, x, y, 500, 450, 200, 50);
		set(reset, x, y, 700, 450, 200, 50);
		set(cost, x, y, 900, 450, 200, 50);

		set(jspcn, x, y, 500, 500, 600, 250);

		set(lub, x, y, 500, 150, 600, 300);
		set(ncb, x, y, 1175, 150, 150, 300);
		for (int i = 0; i < jbcsL.length; i++)
			set(jbcsL[i], x, y, 1100, 150 + 100 * i, 75, 100);
		for (int i = 0; i < jbcsR.length; i++) // 1375 - 1170 = 205
			set(jbcsR[i], x, y, 1325, 150 + 100 * i, 75, 100);

		jlc.setRowHeight(50);
		jlc.getColumnModel().getColumn(2).setPreferredWidth(size(x, y, 300));
		trea.resized(x, y);
		trea.setPreferredSize(size(x, y, trea.getPWidth(), trea.getPHeight()).toDimension());
		jspt.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
	}

	@Override
	public synchronized void onTimer(int t) {
		super.onTimer(t);
		ncb.paint(ncb.getGraphics());
		jspt.revalidate();
	}

	private void addListeners$0() {
		unit.addActionListener(e -> changePanel(ufp));

		ujtf.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				ufp.search(ujtf.getText());
				renew();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				ufp.search(ujtf.getText());
				renew();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				ufp.search(ujtf.getText());
				renew();
			}
		});

		ul.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				changing = true;
				if (e.getButton() == MouseEvent.BUTTON3 && lub.sf instanceof Form) {
					int row = ul.locationToIndex(e.getPoint());
					ul.setSelectedIndex(row);
					lub.select(ul.getSelectedValue());
					if (((Form) lub.sf).du.getPCoin() != null) {
						lub.setLv(new Level(((Form)lub.sf).unit.getPreferredLevel(), ((Form)lub.sf).unit.getPreferredPlusLevel(), new int[0]));
						setLvs(lub.sf);
					}
				} else {
					if (lub.sf != null)
						setLvs(lub.sf);
					lub.select(ul.getSelectedValue());
				}
				changing = false;
			}
		});

		form.addActionListener(arg0 -> {
			lub.adjForm();
			changeLU();
		});

		lvjtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (lub.sf != null) {
					int[] lv = CommonStatic.parseIntsN(lvjtf.getText());

					lub.setLv(Level.lvList(lub.sf.unit(), lv, null));

					setLvs(lub.sf);
				}
			}
		});

		setpref.setLnr(x -> {
			if (lub.sf instanceof Form) {
				Level lv = lu().getLv(lub.sf);
				if (CommonStatic.getPrefLvs().uni.containsKey(lub.sf.getID()) && (CommonStatic.getPrefLvs().uni.get(lub.sf.getID()).equals(lv)
						|| CommonStatic.getPrefLvs().equalsDef(((Form)lub.sf), lv))) {
					CommonStatic.getPrefLvs().uni.remove(lub.sf.getID());
				} else
					CommonStatic.getPrefLvs().uni.put(lub.sf.getID(), lv);
				setLvs(lub.sf);
			}
		});

		lvorb.setLnr(x -> {
			if (lub.sf != null)
				changePanel(new LevelEditPage(this, lu().getLv(lub.sf), (Form) lub.sf));
		});

		for (int i = 0; i < 3; i++) {
			int I = i;

			jbcsR[i].addActionListener(e -> {
				current().sele.nyc[I]++;
				current().sele.nyc[I] %= NyCastle.TOT;
				jlcn.reset();
			});

			jbcsL[i].addActionListener(e -> {
				if (current().sele.nyc[I] == 0)
					current().sele.nyc[I] = NyCastle.TOT - 1;
				else
					current().sele.nyc[I] = (current().sele.nyc[I] - 1) % NyCastle.TOT;
				jlcn.reset();
			});
		}

	}

	private void addListeners$1() {

		jlbs.addListSelectionListener(e -> {
			if (jlb.getValueIsAdjusting() || changing)
				return;
			changing = true;
			if (jlbs.getSelectedValue() == null)
				jlbs.setSelectedValue(BasisSet.current(), true);
			else
				setBS(jlbs.getSelectedValue());
			changing = false;
		});

		jlb.addListSelectionListener(e -> {
			if (jlb.getValueIsAdjusting() || changing)
				return;
			changing = true;
			if (jlb.getSelectedValue() == null)
				jlb.setSelectedValue(BasisSet.current().sele, true);
			else
				setB(jlb.getSelectedValue());
			changing = false;
		});

		jlbs.list = new ReorderListener<BasisSet>() {

			@Override
			public void reordered(int ori, int fin) {
				changing = false;
				List<BasisSet> l = BasisSet.list();
				BasisSet b = l.remove(ori);
				l.add(fin, b);
			}

			@Override
			public void reordering() {
				changing = true;
			}

		};

		jlb.list = new ReorderListener<BasisLU>() {

			@Override
			public boolean add(BasisLU blu) {
				BasisSet.current().lb.add(blu);
				return true;
			}

			@Override
			public void reordered(int ori, int fin) {
				changing = false;
				List<BasisLU> l = BasisSet.current().lb;
				BasisLU b = l.remove(ori);
				l.add(fin, b);
			}

			@Override
			public void reordering() {
				changing = true;
			}

		};

		bsadd.addActionListener(arg0 -> {
			changing = true;
			BasisSet b = new BasisSet();
			vbs.clear();
			vbs.addAll(BasisSet.list());
			jlbs.setListData(vbs);
			jlbs.setSelectedValue(b, true);
			setBS(b);
			changing = false;
		});

		bsrem.addActionListener(arg0 -> {
			changing = true;
			BasisSet.list().remove(current());
			vbs.clear();
			vbs.addAll(BasisSet.list());
			jlbs.setListData(vbs);
			BasisSet b = BasisSet.list().get(BasisSet.list().size() - 1);
			jlbs.setSelectedValue(b, true);
			setBS(b);
			changing = false;
		});

		bscop.addActionListener(arg0 -> {
			changing = true;
			BasisSet b = new BasisSet(current());
			vbs.clear();
			vbs.addAll(BasisSet.list());
			jlbs.setListData(vbs);
			jlbs.setSelectedValue(b, true);
			setBS(b);
			changing = false;
		});

		badd.addActionListener(arg0 -> {
			changing = true;
			BasisLU b = current().add(jlb.getSelectedIndex() + 1);
			vb.clear();
			vb.addAll(current().lb);
			jlb.setListData(vb);
			jlb.setSelectedValue(b, true);
			setB(b);
			changing = false;
		});

		brem.addActionListener(arg0 -> {
			changing = true;
			BasisLU b = current().remove();
			vb.clear();
			vb.addAll(current().lb);
			jlb.setListData(vb);
			jlb.setSelectedValue(b, true);
			setB(b);
			changing = false;
		});

		bcop.addActionListener(arg0 -> {
			changing = true;
			BasisLU b = current().copyCurrent();
			vb.clear();
			vb.addAll(current().lb);
			jlb.setListData(vb);
			jlb.setSelectedValue(b, true);
			setB(b);
			changing = false;
		});

		bsjtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String str = bsjtf.getText().trim();
				if (str.length() > 0)
					BasisSet.current().name = str;
				bsjtf.setText(BasisSet.current().name);
				jlbs.repaint();
			}
		});

		bjtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String str = bjtf.getText().trim();
				if (str.length() > 0)
					BasisSet.current().sele.name = str;
				bjtf.setText(BasisSet.current().sele.name);
				jlb.repaint();
			}
		});

	}

	private void addListeners$2() {
		jlcs.addListSelectionListener(e -> {
			if (changing || e.getValueIsAdjusting())
				return;
			changing = true;
			if (jlcs.getSelectedValue() == null)
				jlcs.setSelectedIndex(0);
			setCS(jlcs.getSelectedIndex());
			changing = false;
		});

		jlcl.addListSelectionListener(e -> {
			if (changing || e.getValueIsAdjusting())
				return;
			changing = true;
			setCL(jlcs.getSelectedIndex());
			changing = false;
		});

		jlcn.addListSelectionListener(e -> {
			if (changing || e.getValueIsAdjusting())
				return;
			changing = true;
			setCN();
			changing = false;
		});

		jlc.getSelectionModel().addListSelectionListener(arg0 -> {
			if (changing || arg0.getValueIsAdjusting())
				return;
			changing = true;
			setC();
			changing = false;
		});

		setc.addActionListener(arg0 -> {
			lu().set(jlc.list.get(jlc.getSelectedRow()).forms);
			changeLU();
		});

		reset.addActionListener(x -> lub.resetBackup());

		cjtf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				comboName = cjtf.getText();
				changing = true;
				setCL(jlcs.getSelectedIndex());
				changing = false;
			}
		});

		combo.addActionListener(x -> {
			if (combo.isSelected() && lub.sf instanceof Form) {
				Unit unit = (Unit)lub.sf.unit();
				setLvs(unit.forms[unit.forms.length - 1]);
				cunit = unit;
				outside = true;
			} else {
				cunit = null;
			}
			changing = true;
			setCL(jlcs.getSelectedIndex());
			changing = false;
		});

		cost.addActionListener(x -> lub.swap = !cost.isSelected());
	}

	private void changeLU() {
		jlcn.setComboList(lu().coms);
		jlc.setLU(lu());
		setCN();
		updateSetC();
		lub.updateLU();
		setLvs(lub.sf);
		trea.callBack(null);
	}

	private void ini() {
		add(jspbs);
		add(jspb);
		add(jspt);
		add(bsadd);
		add(bsrem);
		add(bscop);
		add(badd);
		add(brem);
		add(bcop);
		add(lub);
		add(unit);
		add(jspcs);
		add(jspcl);
		add(jspc);
		add(jspcn);
		add(lub);
		add(jspul);
		add(setc);
		add(bsjtf);
		add(bjtf);
		add(form);
		add(lvjtf);
		add(pcoin);
		add(lvorb);
		add(setpref);
		add(ncb);
		add(reset);
		add(cjtf);
		add(ujtf);
		add(combo);
		add(cost);
		for (int i = 0; i < 3; i++)
			add(jbcsR[i] = new JBTN(0, ">"));
		for (int i = 0; i < 3; i++)
			add(jbcsL[i] = new JBTN(0, "<"));
		ul.setCellRenderer(new UnitLCR());
		int m0 = ListSelectionModel.SINGLE_SELECTION;
		int m1 = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		jlbs.setSelectedValue(current(), true);
		jlcs.setSelectedIndex(0);
		jlbs.setSelectionMode(m0);
		jlb.setSelectionMode(m0);
		jlcs.setSelectionMode(m0);
		jlcl.setSelectionMode(m1);
		jlcn.setSelectionMode(m0);
		ul.setSelectionMode(m0);
		jlc.getSelectionModel().setSelectionMode(m0);
		setCS(0);
		setBS(current());
		lub.setLU(lu());
		bsjtf.setText(BasisSet.current().name);
		bjtf.setText(BasisSet.current().sele.name);
		changeLU();
		addListeners$0();
		addListeners$1();
		addListeners$2();
		lvorb.setEnabled(lub.sf != null);
		setpref.setEnabled(lub.sf != null);
		cost.setSelected(true);
		ufp = new UnitFLUPage(getThis(), st == null ? null : st.getMC().getSave(false), lub.lim, lub.price,
				lub.isTest() && st != null ? st.getMC().getSave(true).getUnlockedsBeforeStage(st, true).keySet() : null);
		assignSubPage(trea);
	}

	private LineUp lu() {
		return BasisSet.current().sele.lu;
	}

	private void setB(BasisLU b) {
		current().sele = b;
		lub.setLU(b.lu);
		brem.setEnabled(current().lb.size() > 1);
		bjtf.setText(b.name);
		ncb.set(b.nyc);
		changeLU();
		callBack(lub.sf);
		trea.callBack(null);
	}

	private void setBS(BasisSet bs) {
		BasisSet.setCurrent(bs);
		vb.clear();
		vb.addAll(bs.lb);
		jlb.setListData(vb);
		BasisLU b = bs.sele;
		jlb.setSelectedValue(b, true);
		bsjtf.setText(bs.name);
		bsrem.setEnabled(bs != BasisSet.def());
		setB(b);
		jlcn.setBasis(bs);
		trea.callBack(null);
	}

	private void setC() {
		if (outside) {
			jlcs.setSelectedIndex(0);
			jlcl.setListData(Interpret.getComboFilter(0));
		}
		updateSetC();
	}

	private void setCL(int cs) {
		int[] cls = jlcl.getSelectedIndices();
		List<Combo> lc = new ArrayList<>();
		List<Combo> comboList = UserProfile.getBCData().combos.getList();
		UserProfile.getUserPacks().forEach(p -> comboList.addAll(p.combos.getList()));
		if (cls.length == 0) {
			for (int i = 0; i < CommonStatic.getBCAssets().filter[cs].length; i++) {
				int finalI = i;
				for (Combo c : comboList.stream()
						.filter(c -> c.type == CommonStatic.getBCAssets().filter[cs][finalI])
						.collect(Collectors.toList())) {
					String name = c.getName();
					if (name != null && name.toLowerCase().contains(comboName.toLowerCase()))
						lc.add(c);
				}
			}
		} else {
			for (int val : cls)
				for (Combo c : comboList.stream()
						.filter(c -> c.type == CommonStatic.getBCAssets().filter[cs][val])
						.collect(Collectors.toList())) {
					String name = c.getName();
					if (name != null && name.toLowerCase().contains(comboName.toLowerCase()))
						lc.add(c);
				}
		}
		if (cunit instanceof Unit) {
			List<Combo> combos = ((Unit)cunit).allCombo();
			lc = lc.stream().filter(combos::contains).collect(Collectors.toList());
		}
		jlc.setList(lc);
		jlc.getSelectionModel().setSelectionInterval(0, 0);
		outside = false;
		setC();
	}

	private void setCN() {
		Object v = jlcn.getSelectedValue();
		lub.select(v instanceof Combo ? (Combo) v : null);
	}

	private void setCS(int cs) {
		jlcl.setListData(Interpret.getComboFilter(cs));
		setCL(cs);
	}

	private void setLvs(AbForm f) {
		if (f instanceof Form) {
			lvorb.setEnabled(((Form) f).orbs != null);
			Level lv = lu().getLv(f);
			String[] strs = UtilPC.lvText(f, lv);
			lvjtf.setText(strs[0]);
			pcoin.setText(strs[1]);

			setpref.setEnabled(CommonStatic.getPrefLvs().uni.containsKey(f.getID()) || !CommonStatic.getPrefLvs().equalsDef((Form)f, lv));
			if (CommonStatic.getPrefLvs().uni.containsKey(f.getID()) && (CommonStatic.getPrefLvs().uni.get(f.getID()).equals(lv)
					|| CommonStatic.getPrefLvs().equalsDef((Form)f, lv))) {
				setpref.setText(MainLocale.PAGE, "rempref");
				setpref.setForeground(Color.RED);
			} else {
				setpref.setText(MainLocale.PAGE, "setpref");
				setpref.setForeground(Color.BLACK);
			}
		} else {
			setpref.setEnabled(false);
			lvorb.setEnabled(false);
			pcoin.setText("");
			if (f == null) {
				lvjtf.setText("");
			} else
				lvjtf.setText(UtilPC.lvText(f, lu().getLv(f))[0]);
		}
	}

	private void updateSetC() {
		Combo com = !jlc.list.isEmpty() && jlc.getSelectedRow() != -1 ? jlc.list.get(jlc.getSelectedRow()) : null;
		setc.setEnabled(com != null && !lu().contains(com));
		boolean b = false;
		if (com != null)
			b = lu().willRem(com);
		setc.setForeground(b ? Color.RED : Color.BLACK);
		setc.setText(0, "set" + (b ? "1" : "0"));
	}
}