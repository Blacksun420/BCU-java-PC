package page.pack;

import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.unit.AbEnemy;
import common.util.unit.EneRand;
import main.Opts;
import page.JBTN;
import page.JTF;
import page.JTG;
import page.Page;
import page.info.filter.AbEnemyFindPage;
import page.support.AnimLCR;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class EREditPage extends Page {

	private static final long serialVersionUID = 1L;

	public static void redefine() {
		EREditTable.redefine();
	}

	private final JBTN back = new JBTN(0, "back");
	private final JBTN veif = new JBTN(0, "veif");
	private final EREditTable jt;
	private final JScrollPane jspjt;
	private final JList<EneRand> jlst = new JList<>();
	private final JScrollPane jspst = new JScrollPane(jlst);
	private final JBTN adds = new JBTN(0, "add");
	private final JBTN rems = new JBTN(0, "rem");
	private final JBTN addl = new JBTN(0, "addl");
	private final JBTN reml = new JBTN(0, "reml");
	private final JList<AbEnemy> jle = new JList<>();
	private final JScrollPane jspe = new JScrollPane(jle);
	private final JTF name = new JTF();
	private final JTG[] type = new JTG[3];

	private final UserPack pack;

	private AbEnemyFindPage efp;

	private EneRand rand;

	public EREditPage(Page p, UserPack pac) {
		super(p);
		pack = pac;
		jle.setListData(UserProfile.getAll(pack.desc.id, AbEnemy.class).toArray(new AbEnemy[0]));
		jt = new EREditTable(this, pac);
		jspjt = new JScrollPane(jt);
		ini();
		resized();
	}

	public EREditPage(Page page, UserPack pac, EneRand e) {
		this(page, pac);
		jle.setSelectedValue(e, true);
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		int modifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		if (e.getSource() == jt && (e.getModifiers() & modifier) == 0)
			jt.clicked(e.getPoint());
	}

	@Override
	protected void renew() {
		if (efp != null && efp.getList() != null)
			jle.setListData(new Vector<>(efp.getList()));
	}

	@Override
	protected synchronized void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);

		set(jspst, x, y, 500, 150, 400, 800);
		set(adds, x, y, 500, 1000, 200, 50);
		set(rems, x, y, 700, 1000, 200, 50);
		set(name, x, y, 500, 1100, 400, 50);
		set(veif, x, y, 950, 100, 400, 50);
		set(jspe, x, y, 950, 150, 400, 1100);
		set(jspjt, x, y, 1400, 450, 850, 800);

		for (int i = 0; i < 3; i++)
			set(type[i], x, y, 1550 + 250 * i, 250, 200, 50);
		set(addl, x, y, 1800, 350, 200, 50);
		set(reml, x, y, 2050, 350, 200, 50);
		jt.setRowHeight(size(x, y, 50));
		jle.setFixedCellHeight(size(x, y, 50));
	}

	private void addListeners() {

		back.addActionListener(arg0 -> changePanel(getFront()));

		addl.addActionListener(arg0 -> {
			int ind = jt.addLine(jle.getSelectedValue());
			setER(rand);
			if (ind < 0)
				jt.clearSelection();
			else
				jt.addRowSelectionInterval(ind, ind);
		});

		reml.addActionListener(arg0 -> {
			int ind = jt.remLine();
			setER(rand);
			if (ind < 0)
				jt.clearSelection();
			else
				jt.addRowSelectionInterval(ind, ind);
		});

		veif.addActionListener(arg0 -> {
			if (efp == null)
				efp = new AbEnemyFindPage(getThis(), pack.desc.id, pack.desc.dependency);
			changePanel(efp);
		});

		jlst.addListSelectionListener(arg0 -> {
			if (isAdj() || arg0.getValueIsAdjusting())
				return;
			setER(jlst.getSelectedValue());
		});

		adds.addActionListener(arg0 -> {
			rand = new EneRand(pack.getNextID(EneRand.class));
			pack.randEnemies.add(rand);
			change(null, p -> {
				jlst.setListData(pack.randEnemies.toArray());
				jlst.setSelectedValue(rand, true);
				setER(rand);
			});

		});

		rems.addActionListener(arg0 -> {
			if (!Opts.conf())
				return;
			int ind = jlst.getSelectedIndex() - 1;
			if (ind < 0)
				ind = -1;
			pack.randEnemies.remove(rand);
			change(ind, IND -> {
				List<EneRand> l = pack.randEnemies.getList();
				jlst.setListData(l.toArray(new EneRand[0]));

				if (IND < l.size())
					jlst.setSelectedIndex(IND);
				else
					jlst.setSelectedIndex(l.size() - 1);
				setER(jlst.getSelectedValue());
			});
		});

		name.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				if (rand == null)
					return;
				rand.name = name.getText().trim();
				setER(rand);
			}

		});

		for (int i = 0; i < 3; i++) {
			int I = i;
			type[i].addActionListener(arg0 -> {
				if (isAdj() || rand == null)
					return;
				rand.type = I;
				setER(rand);
			});
		}

	}

	private void ini() {
		add(back);
		add(veif);
		add(adds);
		add(rems);
		add(jspjt);
		add(jspst);
		add(addl);
		add(reml);
		add(jspe);
		add(name);
		for (int i = 0; i < 3; i++)
			add(type[i] = new JTG(1, "ert" + i));
		setES();
		jle.setCellRenderer(new AnimLCR());
		addListeners();

	}

	private void setER(EneRand er) {
		change(er, st -> {
			boolean b = st != null && pack.editable;
			rems.setEnabled(b);
			addl.setEnabled(b);
			reml.setEnabled(b);
			name.setEnabled(b);
			jt.setEnabled(b);
			for (JTG btn : type)
				btn.setEnabled(b);
			rand = st;
			jt.setData(st);
			name.setText(st == null ? "" : rand.name);
			int t = st == null ? -1 : st.type;
			for (int i = 0; i < 3; i++)
				type[i].setSelected(i == t);
			jspjt.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
		});
		resized();
	}

	private void setES() {
		if (pack == null) {
			jlst.setListData(new EneRand[0]);
			setER(null);
			adds.setEnabled(false);
			return;
		}
		adds.setEnabled(pack.editable);
		List<EneRand> l = pack.randEnemies.getList();
		jlst.setListData(l.toArray(new EneRand[0]));
		if (l.size() == 0) {
			jlst.clearSelection();
			setER(null);
			return;
		}
		jlst.setSelectedIndex(0);
		setER(pack.randEnemies.getList().get(0));
	}

}
