package page.anim;

import common.CommonStatic;
import common.util.anim.AnimCE;
import common.util.anim.AnimU.UType;
import common.util.anim.EPart;
import common.util.anim.MaAnim;
import common.util.anim.Part;
import main.Opts;
import page.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvAnimEditPage extends Page implements TreeCont {

	private static final long serialVersionUID = 1L;

	private static final double res = 0.95;

	private final JBTN back = new JBTN(0, "back");
	private final JTree jlm = new JTree();
	private final JScrollPane jspm = new JScrollPane(jlm);
	private final JList<String> jlv = new JList<>(Page.get(MainLocale.PAGE, "maepm", 17));
	private final JScrollPane jspv = new JScrollPane(jlv);
	private final MaAnimEditTable maet = new MaAnimEditTable(this);
	private final JScrollPane jspma = new JScrollPane(maet);
	private final PartEditTable mpet = new PartEditTable(this);
	private final JScrollPane jspmp = new JScrollPane(mpet);
	private final JTG jtb = new JTG(MainLocale.PAGE, "pause");
	private final JBTN nex = new JBTN(MainLocale.PAGE, "nextf");
	private final JSlider jtl = new JSlider();
	private final AnimBox ab = AnimBox.getInstance();
	private final JBTN addp = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remp = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN addl = new JBTN(MainLocale.PAGE, "addl");
	private final JBTN reml = new JBTN(MainLocale.PAGE, "reml");
	private final JBTN revt = new JBTN(MainLocale.PAGE, "revt"); //Reverts a specific animation
	private final JBTN polish = new JBTN(MainLocale.PAGE, "polish"); //Polshes maanims
	private final JL addfs = new JL(MainLocale.PAGE, "addfs");
	private final JTF jstfs = new JTF();
	private final JL trimfs = new JL(MainLocale.PAGE, "trim");
	private final JTF jtrim = new JTF();
	private final JL inft = new JL();
	private final JL inff = new JL();
	private final JL infv = new JL();
	private final JL infm = new JL();
	private final AnimCE ac;
	private final UType animID;
	private final MMTree mmt;
	private Point p = null;
	private boolean pause, death;

	public AdvAnimEditPage(Page p, AnimCE anim, UType id) {
		super(p);
		ac = anim;
		animID = id;
		mmt = new MMTree(this, ac, jlm);
		ini();
		resized();
	}

	@Override
    public JButton getBackButton() {
		return back;
	}

	@Override
	public void callBack(Object o) {
		if (o instanceof int[])
			change((int[]) o, rs -> {
				if (rs[0] == 0) {
					maet.setRowSelectionInterval(rs[1], rs[2]);
					setC(rs[1]);
				} else {
					mpet.setRowSelectionInterval(rs[1], rs[2]);
					setD(rs[1]);
				}
			});
		int time = ab.getEntity() == null ? 0 : ab.getEntity().ind();
		ab.setEntity(ac.getEAnim(animID));
		ab.getEntity().setTime(time);
	}

	@Override
	public void collapse() {
		selectTree(false);
	}

	@Override
	public void expand() {
		selectTree(false);
	}

	public void selectTree(boolean bv) {
		if (isAdj())
			return;
		boolean exp = jlm.isExpanded(jlm.getSelectionPath());
		Object o = jlm.getLastSelectedPathComponent();
		if (o == null)
			return;
		String str = o.toString();
		int ind = CommonStatic.parseIntN(str.split(" - ")[0]);

		List<Integer> ses = new ArrayList<>();
		for (int i = 0; i < maet.ma.n; i++) {
			Part p = maet.ma.parts[i];
			if (p.ints[0] == ind && (!bv || jlv.isSelectedIndex(p.ints[1])))
				ses.add(i);
		}
		if (!exp)
			mmt.nav(ind, xnd -> {
				for (int i = 0; i < maet.ma.n; i++) {
					Part p = maet.ma.parts[i];
					if (p.ints[0] == xnd && (!bv || jlv.isSelectedIndex(p.ints[1])))
						ses.add(i);
				}
				return true;
			});
		mmt.setAdjusting(true);
		setCs(ses);
		mmt.setAdjusting(false);
		ab.setSele(ind);
	}

	@Override
	protected void mouseDragged(MouseEvent e) {
		if (p == null)
			return;
		ab.ori.x += p.x - e.getX();
		ab.ori.y += p.y - e.getY();
		p = e.getPoint();
	}

	@Override
	protected void mousePressed(MouseEvent e) {
		if (!(e.getSource() instanceof AnimBox))
			return;
		p = e.getPoint();
	}

	@Override
	protected void mouseReleased(MouseEvent e) {
		p = null;
	}

	@Override
	protected void mouseWheel(MouseEvent e) {
		if (!(e.getSource() instanceof AnimBox))
			return;
		MouseWheelEvent mwe = (MouseWheelEvent) e;
		double d = mwe.getPreciseWheelRotation();
		ab.setSiz(ab.getSiz() * Math.pow(res, d));
	}

	@Override
	protected synchronized void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);

		set(addp, x, y, 300, 750, 200, 50);
		set(remp, x, y, 300, 800, 200, 50);
		set(jtb, x, y, 300, 650, 200, 50);
		set(nex, x, y, 300, 700, 200, 50);
		set(jspv, x, y, 300, 850, 200, 450);
		set(jspma, x, y, 500, 650, 900, 650);
		set(jspmp, x, y, 1400, 650, 900, 650);
		set(jspm, x, y, 0, 50, 300, 1250);
		set((Canvas) ab, x, y, 300, 50, 700, 500);
		set(addl, x, y, 2100, 550, 200, 50);
		set(reml, x, y, 2100, 600, 200, 50);
		set(jtl, x, y, 300, 550, 700, 100);
		set(inft, x, y, 1400, 550, 250, 50);
		set(inff, x, y, 1650, 550, 250, 50);
		set(infv, x, y, 1400, 600, 250, 50);
		set(infm, x, y, 1650, 600, 250, 50);

		set(revt, x, y, 1000, 50, 200, 50);
		set(polish, x, y, 1200, 50, 200, 50);
		set(addfs, x, y, 1000, 100, 200, 50);
		set(jstfs, x, y, 1200, 100, 200, 50);
		set(trimfs, x, y, 1000, 150, 200, 50);
		set(jtrim, x, y, 1200, 150, 200, 50);

		maet.setRowHeight(size(x, y, 50));
		mpet.setRowHeight(size(x, y, 50));
		ab.draw();
	}

	@Override
    public void timer(int t) {
		if (!pause)
			eupdate();
		if (ab.getEntity() != null && mpet.part != null) {
			Part p = mpet.part;
			EPart ep = ab.getEntity().ent[p.ints[0]];
			inft.setText("frame: " + ab.getEntity().ind());
			inff.setText("part frame: " + (p.frame - p.off));
			infv.setText("actual value: " + ep.getVal(p.ints[1]));
			infm.setText("part value: " + p.vd);
		} else {
			inft.setText("");
			inff.setText("");
			infv.setText("");
			infm.setText("");
		}
		resized();
	}

	private void addListeners$0() {

		back.setLnr(x -> changePanel(getFront()));

		jlm.addTreeSelectionListener(arg0 -> selectTree(false));

		jlv.addListSelectionListener(arg0 -> selectTree(true));

		jtb.setLnr(arg0 -> {
			pause = jtb.isSelected();
			jtl.setEnabled(pause && ab.getEntity() != null);
		});

		nex.setLnr(e -> eupdate());

		jtl.addChangeListener(arg0 -> {
			if (isAdj() || !pause)
				return;
			ab.getEntity().setTime(jtl.getValue());
		});

		final ListSelectionModel lsm = maet.getSelectionModel();
		lsm.addListSelectionListener(e -> {
			if (isAdj() || lsm.getValueIsAdjusting())
				return;
			int[] inds = maet.getSelectedRows();
			List<Integer> l = new ArrayList<>();
			for (int i : inds)
				l.add(i);
			setCs(l);
		});

		addp.addActionListener(arg0 -> {
			change(true);
			int ind = maet.getSelectedRow() + 1;
			MaAnim ma = maet.ma;
			Part[] data = ma.parts;
			ma.parts = new Part[++ma.n];
			if (ind >= 0)
				System.arraycopy(data, 0, ma.parts, 0, ind);
			if (data.length - ind >= 0)
				System.arraycopy(data, ind, ma.parts, ind + 1, data.length - ind);
			Part np = new Part();
			np.validate();
			ma.parts[ind] = np;
			ma.validate();
			maet.anim.unSave("maanim add part");
			callBack(null);
			resized();
			lsm.setSelectionInterval(ind, ind);
			setC(ind);
			int h = mpet.getRowHeight();
			mpet.scrollRectToVisible(new Rectangle(0, h * ind, 1, h));
			change(false);
		});

		remp.addActionListener(arg0 -> {
			change(true);
			MaAnim ma = maet.ma;
			int[] rows = maet.getSelectedRows();
			Part[] data = ma.parts;
			for (int row : rows)
				data[row] = null;
			ma.n -= rows.length;
			ma.parts = new Part[ma.n];
			int ind = 0;
			for (Part datum : data)
				if (datum != null)
					ma.parts[ind++] = datum;
			ind = rows[rows.length - 1];
			ma.validate();
			maet.anim.unSave("maanim remove part");
			callBack(null);
			if (ind >= ma.n)
				ind = ma.n - 1;
			lsm.setSelectionInterval(ind, ind);
			setC(ind);
			change(false);
		});

		final ListSelectionModel lsp = mpet.getSelectionModel();
		lsp.addListSelectionListener(e -> {
			if (isAdj() || lsp.getValueIsAdjusting())
				return;
			setD(lsp.getLeadSelectionIndex());
		});

		addl.addActionListener(arg0 -> {
			Part p = mpet.part;
			int[][] data = p.moves;
			p.moves = new int[++p.n][];
			System.arraycopy(data, 0, p.moves, 0, data.length);
			p.moves[p.n - 1] = new int[4];
			p.validate();
			maet.ma.validate();
			callBack(null);
			maet.anim.unSave("maanim add line");
			resized();
			change(p.n - 1, i -> lsp.setSelectionInterval(i, i));
			setD(p.n - 1);
			int h = mpet.getRowHeight();
			mpet.scrollRectToVisible(new Rectangle(0, h * (p.n - 1), 1, h));
		});

		reml.addActionListener(arg0 -> {
			int[] inds = mpet.getSelectedRows();
			if (inds.length == 0)
				return;
			Part p = mpet.part;
			List<int[]> l = new ArrayList<>();
			int j = 0;
			for (int i = 0; i < p.n; i++)
				if (j >= inds.length || i != inds[j])
					l.add(p.moves[i]);
				else
					j++;
			p.moves = l.toArray(new int[0][]);
			p.n = l.size();
			p.validate();
			maet.ma.validate();
			callBack(null);
			maet.anim.unSave("maanim remove line");
			int ind = inds[0];
			if (ind >= p.n)
				ind--;
			change(ind, i -> lsp.setSelectionInterval(i, i));
			setD(ind);
		});
	}

	private void addListeners$1() {

		revt.setLnr(x -> {
			MaAnim anim = ac.getMaAnim(animID);
			if (anim == null)
				return;
			if (Arrays.stream(anim.parts).anyMatch(p -> p.ints[0] == 0 && p.ints[1] == 9)) {
				Arrays.stream(anim.parts).filter(p -> p.ints[0] == 0 && p.ints[1] == 9).forEach(p -> Arrays.stream(p.moves)
						.forEach(ints -> ints[1] *= -1));
			} else {
				Part[] data = anim.parts;
				anim.parts = new Part[++anim.n];
				System.arraycopy(data, 0, anim.parts, 0, data.length);
				Part newScalePart = new Part();
				newScalePart.validate();
				newScalePart.ints[1] = 9;
				newScalePart.ints[2] = 1;
				newScalePart.moves = new int[++newScalePart.n][];
				newScalePart.moves[0] = new int[] { 0, -1000, 0, 0 };
				newScalePart.validate();
				anim.parts[anim.n - 1] = newScalePart;
				anim.validate();
			}
			for (int i = 0; i < ac.mamodel.parts.length; i++) {
				int[] parts = ac.mamodel.parts[i];
				int partID = i;
				int angle = parts[10];
				if (Arrays.stream(anim.parts).anyMatch(p -> p.ints[0] == partID && p.ints[1] == 11)) {
					Arrays.stream(anim.parts)
							.filter(p -> p.ints[0] == partID && p.ints[1] == 11)
							.forEach(p -> Arrays.stream(p.moves).forEach(ints -> {
								ints[1] += angle * 2;
								ints[1] *= -1;
							}));
				} else if (angle != 0) {
					Part[] data = anim.parts; // copy parts
					anim.parts = new Part[++anim.n]; // add slot for new part
					System.arraycopy(data, 0, anim.parts, 0, data.length); // preserve parts
					Part newPart = new Part(); // create part
					newPart.validate(); // validate
					newPart.ints[0] = partID;
					newPart.ints[1] = 11; // set type to angle
					newPart.ints[2] = 1; // Loop once
					newPart.moves = new int[++newPart.n][]; // add slot for new move
					newPart.moves[0] = new int[] { 0, angle * -2, 0, 0 }; // set angle to negative ma_model angle
					newPart.validate(); // validate part
					anim.parts[anim.n - 1] = newPart; // set part to anim parts
				}
				anim.validate(); // validate animation before next ma_model part check
			}
			maet.anim.unSave("maanim revert");
			callBack(null);
		});

		polish.addActionListener(l -> {
			MaAnim anim = ac.getMaAnim(animID);
			if (anim == null)
				return;
			change(true);
			Part[] data = anim.parts;
			for (int i = 0; i < data.length; i++) {
				if (data[i].moves.length == 0) {
					data[i] = null;
					anim.n--;
					continue;
				}
				int[][] movs = data[i].moves;
				int pre = 0;
				while (pre < movs.length && unecessarymove(data[i], pre) && ((pre == movs.length - 1 && data[i].ints[2] == 1) || movs[pre][2] == 1 || movs[pre][1] == movs[pre + 1][1] || movs[pre][0] == movs[pre + 1][0] - 1)) {
					movs[pre] = null;
					data[i].n--;
					pre++;
				}
				for (int j = pre + 1; j < movs.length; j++) {
					if (movs[pre][1] == movs[j][1] && ((j == movs.length - 1 && data[i].ints[2] == 1) || movs[j + 1][1] == movs[j][1] || (movs[pre][2] == 1 && movs[j][2] == 1))) {
						movs[j] = null;
						data[i].n--;
					} else if (movs[pre][1] == movs[j][1] && movs[j][0] == movs[j + 1][0] - 1) {
						movs[pre][2] = 1;
						movs[j] = null;
						data[i].n--;
					} else
						pre = j;
				}
				if (data[i].n < movs.length) {
					data[i].moves = new int[data[i].n][];
					pre = 0;
					for (int[] mov : movs)
						if (mov != null)
							data[i].moves[pre++] = mov;
				}
				if (data[i].n == 1) {
					if (unecessarymove(data[i], 0)) {
						data[i] = null;
						anim.n--;
					}
				}
			}
			anim.parts = new Part[anim.n];
			int ind = 0;
			for (Part datum : data)
				if (datum != null)
					anim.parts[ind++] = datum;
			anim.validate();
			maet.anim.unSave("polish anim");
			callBack(null);
			change(false);
		});

		jstfs.setLnr(theJ -> {
			MaAnim anim = ac.getMaAnim(animID);
			if (anim == null || death || isAdj())
				return;
			death = true;
			int add = CommonStatic.parseIntN(jstfs.getText());
			int[] rows = maet.getSelectedRows();
			if (rows.length == 0) {
				death = false;
				Opts.pop("You must select at least 1 part to add", "Nothing selected");
				return;
			}
			if (add != 0 && Opts.conf((add > 0 ? "Add " : "Substract ") + Math.abs(add) + "f startup time for the selected parts?")) {
				change(true);
				for (int row : rows)
					for (int i = 0; i < anim.parts[row].n; i++)
						anim.parts[row].moves[i][0] += add;
				anim.validate();
				setJTLs();
				change(false);
			}
			death = false;
		});

		jtrim.setLnr(theJ -> {
			MaAnim anim = ac.getMaAnim(animID);
			if (anim == null || isAdj())
				return;
			int trim = CommonStatic.parseIntN(jtrim.getText());
			int[] rows = maet.getSelectedRows();
			death = true;
			if (rows.length == 0) {
				death = false;
				Opts.pop("You must select at least 1 part to trim", "Nothing selected");
				return;
			}
			if (Opts.conf("Trim every keyframe timed " + (trim > 0 ? "after " : "before ") + trim + "f for the selected parts?")) {
				change(true);
				for (int row : rows) {
					if (trim > 0) {
						for (int i = anim.parts[row].n - 1; i >= 0; i--)
							if (anim.parts[row].moves[i][0] <= trim || (i == 0 && anim.parts[row].moves[0][0] > Math.abs(trim))) {
								anim.parts[row].n = i + 1;
								break;
							}
						if (anim.parts[row].n < anim.parts[row].moves.length) //To prevent pointless processing, only copy if there was actually trimming
							anim.parts[row].moves = Arrays.copyOf(anim.parts[row].moves, anim.parts[row].n);
					} else {
						int v = -1;
						for (int i = 0; i < anim.parts[row].n; i++)
							if (anim.parts[row].moves[i][0] >= Math.abs(trim) || (i == anim.parts[row].n - 1 && anim.parts[row].moves[i][0] < Math.abs(trim))) {
								v = i;
								break;
							}
						if (v > 0) {
							int[][] moovs = new int[anim.parts[row].moves.length - v][];
							System.arraycopy(anim.parts[row].moves, v, moovs, 0, anim.parts[row].moves.length - v);
							anim.parts[row].moves = moovs;
							anim.parts[row].n = moovs.length;
						}
					}
					anim.parts[row].validate();
				}
				anim.validate();
				setJTLs();
				change(false);
			}
			death = false;
		});
	}

	private boolean unecessarymove(Part data, int part) {
		return (data.ints[1] <= 3 && data.moves[part][1] == ac.mamodel.parts[data.ints[0]][data.ints[1]]) ||
				(data.moves[part][1] == 0 && ((data.ints[1] >= 4 && data.ints[1] <= 7) || data.ints[1] == 11 || data.ints[1] >= 13)) ||
				(data.moves[part][1] == 1000 && ((data.ints[1] >= 8 && data.ints[1] <= 10) || data.ints[1] == 12 || data.ints[1] >= 50));
	}

	private void eupdate() {
		ab.update();
		if (ab.getEntity() != null)
			change(0, x -> jtl.setValue(ab.getEntity().ind()));
	}

	private void ini() {
		add(back);
		add(jspm);
		add(jspv);
		add(jspma);
		add(jspmp);
		add(addp);
		add(remp);
		add(addl);
		add(reml);
		add(jtb);
		add(jtl);
		add(nex);
		add((Canvas) ab);
		add(inft);
		add(inff);
		add(infv);
		add(infm);
		add(revt);
		add(polish);
		add(addfs);
		add(jstfs);
		add(trimfs);
		add(jtrim);
		setA();

		addListeners$0();
		addListeners$1();
	}

	private void setA() {
		mmt.renew();
		int row = maet.getSelectedRow();
		maet.setAnim(ac, ac.getMaAnim(animID));
		ab.setEntity(ac.getEAnim(animID));
		if (row >= maet.getRowCount()) {
			maet.clearSelection();
			row = -1;
		}
		setC(row);
		setJTLs();
	}

	private void setJTLs() {
		jtl.setPaintTicks(true);
		jtl.setPaintLabels(true);
		jtl.setMinimum(0);
		jtl.setMaximum(ab.getEntity().len());
		jtl.setLabelTable(null);
		if (ab.getEntity().len() <= 50) {
			jtl.setMajorTickSpacing(5);
			jtl.setMinorTickSpacing(1);
		} else if (ab.getEntity().len() <= 200) {
			jtl.setMajorTickSpacing(10);
			jtl.setMinorTickSpacing(2);
		} else if (ab.getEntity().len() <= 1000) {
			jtl.setMajorTickSpacing(50);
			jtl.setMinorTickSpacing(10);
		} else if (ab.getEntity().len() <= 5000) {
			jtl.setMajorTickSpacing(250);
			jtl.setMinorTickSpacing(50);
		} else {
			jtl.setMajorTickSpacing(1000);
			jtl.setMinorTickSpacing(200);
		}
	}

	private void setC(int ind) {
		Part p = ind < 0 || ind >= maet.ma.parts.length ? null : maet.ma.parts[ind];

		remp.setEnabled(ind >= 0);
		addl.setEnabled(ind >= 0);
		ab.setSele(p == null ? -1 : p.ints[0]);
		change(true);
		mpet.setAnim(maet.anim, maet.ma, p);
		mpet.clearSelection();
		if (ind >= 0) {
			if(p != null) {
				int par = p.ints[0];
				mmt.select(par);
				jlv.setSelectedIndex(mpet.part.ints[1]);
				if (maet.getSelectedRow() != ind) {
					maet.setRowSelectionInterval(ind, ind);
					maet.scrollRectToVisible(maet.getCellRect(ind, 0, true));
				}
				ab.setSele(par);
			}
		} else
			maet.clearSelection();
		change(false);
		setD(-1);
	}

	private void setCs(Iterable<Integer> is) {
		change(true);
		boolean setted = false;
		maet.clearSelection();
		for (int i : is)
			if (i >= 0) {
				if (!setted) {
					setC(i);
					setted = true;
				}
				maet.addRowSelectionInterval(i, i);
				int v = maet.ma.parts[i].ints[1];
				jlv.addSelectionInterval(v, v);
			}
		if (!setted)
			setC(-1);
		change(false);
	}

	private void setD(int ind) {
		reml.setEnabled(ind >= 0);
	}

}
