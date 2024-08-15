package page.basis;

import common.CommonStatic;
import common.battle.BasisLU;
import common.battle.BasisSet;
import common.battle.LineUp;
import common.battle.data.MaskUnit;
import common.pack.UserProfile;
import common.system.Node;
import common.util.Data;
import common.util.pack.NyCastle;
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
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static common.battle.BasisSet.current;

public class BasisPage extends LubCont {

	private static class OrbContainer {
		int i;
		int[] orb;

		public OrbContainer(int ind, int[] data) {
			i = ind;
			orb = data;
		}
		@Override
		public String toString() {
			if (orb.length != 0)
				return "Orb" + i + ": " + getTrait(orb[1]) + " " + getType(orb[0]) + " " + (orb[2] < gradeStrs.length ? gradeStrs[orb[2]] : "?");
			return "Orb" + i + ": None";
		}
	}
	private static final long serialVersionUID = 1L;

	private static final String[] gradeStrs = {"D","C","B","A","S"};
	private static String getTrait(int trait) {
		StringBuilder res = new StringBuilder();

		for (int i = 0; i < Interpret.TRAIT.length; i++)
			if (((trait >> i) & 1) > 0)
				res.append(Interpret.TRAIT[i]).append("/ ");

		if (res.toString().endsWith("/ "))
			res = new StringBuilder(res.substring(0, res.length() - 2));

		return res.toString();
	}
	private static String getType(int type) {
		if (type <= 4)
			return MainLocale.getLoc(MainLocale.UTIL, "ot"+type);
		return "Unknown Type " + type;
	}

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

	private final JList<OrbContainer> orbList = new JList<>();
	private final JScrollPane orbScroll = new JScrollPane(orbList);
	private final OrbBox orbb = new OrbBox();
	private final JComboBox<String> type = new JComboBox<>(), trait = new JComboBox<>(), grade = new JComboBox<>();
	private List<Byte> typeData = new ArrayList<>();
	private List<Integer> traitData = new ArrayList<>();
	private List<Byte> gradeData = new ArrayList<>();
	private final JBTN addo = new JBTN(0, "add");
	private final JBTN remo = new JBTN(0, "rem");

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

	public BasisPage(Page p, Stage st, int star, boolean test) {
		super(p);
		this.st = st;
		lub.setLimit(st.getLim(star), st.getMC().getSave(false), st.getCont().price);
		if (st.getLim(star).stageLimit != null)
			jlcn.setBanned(st.getLim(star).stageLimit.bannedCatCombo);
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
		if (trea.isFocusOwner() || lvjtf.isFocusOwner() || bjtf.isFocusOwner() || bsjtf.isFocusOwner() || cjtf.isFocusOwner() || ujtf.isFocusOwner())
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
		set(lvjtf, x, y, 500, 100, 600, 50);
		set(setpref, x, y, 1100, 100, 300, 50);
		set(form, x, y, 500, 450, 200, 50);
		set(reset, x, y, 700, 450, 200, 50);
		set(cost, x, y, 900, 450, 200, 50);

		set(lub, x, y, 500, 150, 600, 300);
		set(ncb, x, y, 1175, 150, 150, 300);
		for (int i = 0; i < jbcsL.length; i++)
			set(jbcsL[i], x, y, 1100, 150 + 100 * i, 75, 100);
		for (int i = 0; i < jbcsR.length; i++) // 1375 - 1170 = 205
			set(jbcsR[i], x, y, 1325, 150 + 100 * i, 75, 100);

		int cw = generateNames().length > 0 ? 50 : 0;
		set(type, x, y, 1100, 450, cw * 6, 50);
		set(trait, x, y, 1100, 500, cw * 3, 50);
		set(grade, x, y, 1250, 500, cw * 3, 50);
		if (cw != 0 && (lu().getLv(lub.sf).getOrbs() == null || lu().getLv(lub.sf).getOrbs().length < (((Form)lub.sf).orbs == null || ((Form)lub.sf).orbs.getSlots() >= 2 ? 1 : 2)))
			cw = 0;
		set(jspcn, x, y, 500, 500, 600 - (cw*5), 250);
		set(orbScroll, x, y, 850, 500, cw * 5, 250);
		cw = lub.sf instanceof Form && ((Form)lub.sf).orbs != null && ((Form)lub.sf).orbs.getSlots() == -1 ? 150 : 0;
		set(addo, x, y, 1100, 700, cw, 50);
		set(remo, x, y, 1250, 700, cw, 50);
		set(orbb, x, y, 1100, 550, 300, 200-(cw/3));

		jlc.setRowHeight(50);
		jlc.getColumnModel().getColumn(2).setPreferredWidth(size(x, y, 300));
		trea.resized(x, y);
		trea.setPreferredSize(size(x, y, trea.getPWidth(), trea.getPHeight()).toDimension());
		jspt.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
	}

	@Override
	public synchronized void onTimer(int t) {
		super.onTimer(t);
		orbb.paint(orbb.getGraphics());
		ncb.paint(ncb.getGraphics());
		jspt.revalidate();
	}

	private void addListeners$0() {
		unit.setLnr(() -> ufp);

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
					changing = true;
					int[] lv = CommonStatic.parseIntsN(lvjtf.getText());
					lub.setLv(Level.lvList(lub.sf.unit(), lv, null));
					for (int[] orb : getOrbs(-1))
						initializeDrops(orb, false);
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
					CommonStatic.getPrefLvs().uni.put(lub.sf.getID(), lv.clone());
				setLvs(lub.sf);
			}
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
				if (!str.isEmpty())
					BasisSet.current().name = str;
				bsjtf.setText(BasisSet.current().name);
				jlbs.repaint();
			}
		});

		bjtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String str = bjtf.getText().trim();
				if (!str.isEmpty())
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

		addo.setLnr(x -> {
			if (changing)
				return;
			int[][] orbs = getOrbs(-1);
			orbs = Arrays.copyOf(orbs, orbs.length + 1);
			orbs[orbs.length - 1] = new int[]{0,CommonStatic.getBCAssets().DATA.get((byte)0),0};
			setLvOrb((Form)lub.sf, orbs);
		});
		remo.setLnr(x -> {
			if (changing)
				return;
			setLvOrb((Form)lub.sf, getOrbs(orbList.getSelectedIndex()));
		});
		orbList.addListSelectionListener(e -> {
			if (changing || e.getValueIsAdjusting())
				return;
			setOrb((Form)lub.sf);
		});
		type.addActionListener(arg0 -> {
			if (!changing && !orbList.isSelectionEmpty()) {
				int[] data = orbList.getSelectedValue().orb;
				Form f = (Form)lub.sf;
				if (f.orbs != null && f.orbs.getSlots() != -1) {
					if (type.getSelectedIndex() == 0) {
						setLvOrb((Form)lub.sf, getOrbs(orbList.getSelectedIndex()));
						return;
					} else {
						if (data.length == 0) {
							int[][] orbs = getOrbs(-1);
							orbs = Arrays.copyOf(orbs, orbs.length + 1);
							orbs[orbs.length - 1] = data = new int[]{0, 0, 0};
							setLvOrb((Form)lub.sf, orbs);
						}
						data[0] = typeData.get(type.getSelectedIndex() - 1);
					}
				} else
					data[0] = typeData.get(type.getSelectedIndex());
				changeOrb(f, data, true);
			}
		});
		trait.addActionListener(arg0 -> {
			if (!changing && !orbList.isSelectionEmpty()) {
				int[] data = orbList.getSelectedValue().orb;
				data[1] = traitData.get(trait.getSelectedIndex());
				changeOrb((Form)lub.sf, data, true);
			}
		});
		grade.addActionListener(arg0 -> {
			if (!changing && !orbList.isSelectionEmpty()) {
				int[] data = orbList.getSelectedValue().orb;
				data[2] = gradeData.get(grade.getSelectedIndex());
				changeOrb((Form)lub.sf, data, true);
			}
		});
	}
	private boolean valid() {
		return !orbList.isSelectionEmpty() && orbList.getSelectedValue().orb.length == Data.ORB_TOT;
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
		add(setpref);
		add(ncb);
		add(reset);
		add(cjtf);
		add(ujtf);
		add(combo);
		add(cost);

		add(orbScroll);
		orbList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(orbb);
		add(trait);
		add(type);
		add(grade);
		add(addo);
		add(remo);

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
		setpref.setEnabled(lub.sf != null);
		cost.setSelected(true);
		ujtf.setHintText(get(MainLocale.PAGE, "search"));
		cjtf.setHintText(get(MainLocale.PAGE, "search"));
		ufp = new UnitFLUPage(getThis(), st == null ? null : st.getMC().getSave(false), lub.lim, lub.price,
				lub.isTest() && st != null ? st.getMC().getSave(true).getUnlockedsBeforeStage(st, true).keySet() : null);
		assignSubPage(trea);
	}

	private void setOrb(Form f) {
		changing = true;
		if (f == null) {
			orbList.setListData(new OrbContainer[0]);
			orbList.setSelectedIndex(-1);
		} else {
			OrbContainer[] names = generateNames();
			int s = Math.max(0, Math.min(orbList.getSelectedIndex(), names.length - 1));
			orbList.setListData(names);
			orbList.setSelectedIndex(s);
			type.setEnabled(!orbList.isSelectionEmpty());
			if (!orbList.isSelectionEmpty()) {
				initializeDrops(orbList.getSelectedValue().orb, true);
				orbb.changeOrb(orbList.getSelectedValue().orb);
			} else {
				initializeDrops(new int[0], true);
				orbb.changeOrb(new int[0]);
			}
		}
		fireDimensionChanged();
		remo.setEnabled(valid());
		changing = false;
	}
	private int[][] getOrbs(int substr) {
		if (!(lub.sf instanceof Form) || lu().getLv(lub.sf).getOrbs() == null)
			return new int[0][];
		int[][] orbs = lu().getLv(lub.sf).getOrbs();
		if (substr == -1)
			return orbs;
		int[][] valid = new int[orbs.length - 1][];
        for (int i = 0; i < valid.length; i++)
			valid[i] = orbs[i + (i < substr ? 0 : 1)];
		return valid;
	}
	private OrbContainer[] generateNames() {
		int[][] orbs = getOrbs(-1);
		int min = lub.sf instanceof Form && ((Form)lub.sf).orbs != null && ((Form)lub.sf).orbs.getSlots() != -1 ? ((Form)lub.sf).orbs.getSlots() : 0;
		OrbContainer[] res = new OrbContainer[Math.max(min, orbs.length)];
		for (int i = 0; i < orbs.length; i++)
			res[i] = new OrbContainer(i, orbs[i]);
		for (int i = orbs.length; i < min; i++)
			res[i] = new OrbContainer(i, new int[0]);
		return res;
	}
	private void initializeDrops(int[] data, boolean setLists) {
		CommonStatic.BCAuxAssets aux = CommonStatic.getBCAssets();

		if (!(lub.sf instanceof Form) || lub.sf.unit() == null || ((Form)lub.sf).orbs == null)
			return;
		Form f = (Form)lub.sf;
		Level lv = lu().getLv(f);
		ArrayList<String> typeText = new ArrayList<>();
		boolean str = false;
		boolean mas = false;
		boolean res = false;

		if(f.orbs.getSlots() == -1) {
			for(Form form : f.unit.forms) {
				MaskUnit mu = form.du.getPCoin() != null ? form.du.getPCoin().improve(lv.getTalents()) : form.du;
				int atk = (int) mu.getProc().DMGINC.mult;
				int def = (int) mu.getProc().DEFINC.mult;
				str |= (atk > 100 && atk < 300) || (def > 100 && def < 400);
				mas |= atk >= 300 && atk < 500;
				res |= def >= 400 && def < 600;
			}
		} else {
			MaskUnit mu = f.du.getPCoin() != null ? f.du.getPCoin().improve(lv.getTalents()) : f.du;
			int atk = (int) mu.getProc().DMGINC.mult;
			int def = (int) mu.getProc().DEFINC.mult;
			str = (atk > 100 && atk < 300) || (def > 100 && def < 400);
			mas = atk >= 300 && atk < 500;
			res = def >= 400 && def < 600;
		}
		if (f.orbs.getSlots() != -1)
			typeText.add("None");

		typeData = new ArrayList<>();
		typeText.add(MainLocale.getLoc(MainLocale.UTIL, "ot0"));
		typeData.add(Data.ORB_ATK);
		typeText.add(MainLocale.getLoc(MainLocale.UTIL, "ot1"));
		typeData.add(Data.ORB_RES);

		if(str) {
			typeText.add(MainLocale.getLoc(MainLocale.UTIL, "ot2"));
			typeData.add(Data.ORB_STRONG);
		}
		if(mas) {
			typeText.add(MainLocale.getLoc(MainLocale.UTIL, "ot3"));
			typeData.add(Data.ORB_MASSIVE);
		}
		if(res) {
			typeText.add(MainLocale.getLoc(MainLocale.UTIL, "ot4"));
			typeData.add(Data.ORB_RESISTANT);
		}
		if (data.length == 0) {
			if (!setLists)
				return;
			type.setModel(new DefaultComboBoxModel<>(typeText.toArray(new String[0])));
			type.setEnabled(f.orbs.getSlots() != -1);
			type.setSelectedIndex(0);
			trait.setEnabled(false);
			grade.setEnabled(false);
			if (!orbList.isSelectionEmpty()) {
				int index = orbList.getSelectedIndex();
				orbList.setListData(generateNames());
				orbList.setSelectedIndex(index);
			}
			return;
		}
		trait.setEnabled(true);
		grade.setEnabled(true);

		String[] traits;
		String[] grades;
		byte otype = (byte)data[Data.ORB_TYPE];
		if (!typeData.contains(otype))
			data[Data.ORB_TYPE] = otype = Data.ORB_ATK;

		if (aux.ORB.containsKey(otype)) {
			if(otype == Data.ORB_STRONG || otype == Data.ORB_MASSIVE || otype == Data.ORB_RESISTANT) {
				List<Integer> allTraits = new ArrayList<>(aux.ORB.get(otype).keySet());
				traitData = new ArrayList<>();
				List<Trait> traitList = new ArrayList<>();

				if(f.orbs.getSlots() == -1) {
					for(Form form : f.unit.forms) {
						MaskUnit mu = form.du.getPCoin() != null ? form.du.getPCoin().improve(lv.getTalents()) : form.du;
						for(Trait t : mu.getTraits())
							if(t.BCTrait() && !traitList.contains(t))
								traitList.add(t);
					}
				} else {
					MaskUnit mu = f.du.getPCoin() != null ? f.du.getPCoin().improve(lv.getTalents()) : f.du;
					for(Trait t : mu.getTraits())
						if(t.BCTrait() && !traitList.contains(t))
							traitList.add(t);
				}
				for (Trait t : traitList)
					if (allTraits.contains(1 << t.id.id))
						traitData.add(1 << t.id.id);

				if(traitData.isEmpty())
					traitData = allTraits;
				else
					traitData.sort(Integer::compareTo);
			} else
				traitData = new ArrayList<>(aux.ORB.get(otype).keySet());

			traits = new String[traitData.size()];
			for (int i = 0; i < traits.length; i++)
				traits[i] = getTrait(traitData.get(i));

			if (!traitData.contains(data[Data.ORB_TRAIT]))
				data[Data.ORB_TRAIT] = traitData.get(0);
			gradeData = aux.ORB.get(otype).get(data[Data.ORB_TRAIT]);
			grades = new String[gradeData.size()];

			for (int i = 0; i < grades.length; i++)
				grades[i] = gradeData.get(i) < gradeStrs.length ? gradeStrs[gradeData.get(i)] : "?"; //getGrade(gradeData.get(i)); "Unknown Grade " + grade
		} else
			return;

		byte ograde = (byte)data[Data.ORB_GRADE];
		if (!gradeData.contains(ograde))
			data[Data.ORB_GRADE] = gradeData.get(Data.ORB_GRADE);

		if (valid())
			changeOrb(f, data, false);

		if (setLists) {
			type.setModel(new DefaultComboBoxModel<>(typeText.toArray(new String[0])));
			trait.setModel(new DefaultComboBoxModel<>(traits));
			grade.setModel(new DefaultComboBoxModel<>(grades));

			if (f.orbs.getSlots() != -1)
				type.setSelectedIndex(typeData.indexOf(otype) + 1);
			else
				type.setSelectedIndex(typeData.indexOf(otype));

			trait.setSelectedIndex(traitData.indexOf(data[Data.ORB_TRAIT]));
			grade.setSelectedIndex(gradeData.indexOf(ograde));

			if (valid()) {
				int index = orbList.getSelectedIndex();
				orbList.setListData(generateNames());
				orbList.setSelectedIndex(index);
			}
		}
	}

	private void changeOrb(Form f, int[] orb, boolean setp) {
		int[][] orbs = getOrbs(-1);
		orbs[orbList.getSelectedIndex()] = orb;
		if (setp)
			setLvOrb(f, orbs);
		else
			lu().setOrb(f.unit, lu().getLv(f), orbs);
	}
	private void setLvOrb(Form f, int[][] orbs) {
		lu().setOrb(f.unit, lu().getLv(f), orbs);
		setOrb(f); //callBack(null);
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
			Level lv = lu().getLv(f);
			String[] strs = UtilPC.lvText(f, lv);
			lvjtf.setText(strs[0]);
			pcoin.setText(strs[1]);
			setOrb((Form)f);

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
			setOrb(null);
			setpref.setEnabled(false);
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