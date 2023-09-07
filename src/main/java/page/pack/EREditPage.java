package page.pack;

import common.CommonStatic;
import common.pack.Context;
import common.pack.PackData.UserPack;
import common.pack.Source;
import common.pack.UserProfile;
import common.util.unit.AbEnemy;
import common.util.unit.EneRand;
import main.MainBCU;
import main.Opts;
import page.*;
import page.info.filter.EnemyFindPage;
import page.support.AnimLCR;
import page.support.Importer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import static utilpc.UtilPC.resizeImage;

public class EREditPage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	public static void redefine() {
		EREditTable.redefine();
	}

	private final JBTN veif = new JBTN(0, "veif");
	private final EREditTable jt;
	private final JScrollPane jspjt;
	private final JList<EneRand> jlst = new JList<>();
	private final JScrollPane jspst = new JScrollPane(jlst);
	private final JBTN adds = new JBTN(MainLocale.PAGE, "add");
	private final JBTN rems = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN addl = new JBTN(MainLocale.PAGE, "addl");
	private final JBTN reml = new JBTN(MainLocale.PAGE, "reml");
	private final JList<AbEnemy> jle = new JList<>();
	private final JScrollPane jspe = new JScrollPane(jle);
	private final JTF name = new JTF();
	private final JTG[] type = new JTG[3];
	private final JBTN adicn = new JBTN(MainLocale.PAGE, "icon");
	private final JBTN reicn = new JBTN(MainLocale.PAGE, "remicon");

	private final UserPack pack;

	private EnemyFindPage efp;

	private EneRand rand;

	public EREditPage(Page p, UserPack pac) {
		super(p);
		pack = pac;
		jle.setListData(UserProfile.getAll(pack.desc.id, AbEnemy.class).toArray(new AbEnemy[0]));
		jt = new EREditTable(this, pac);
		jspjt = new JScrollPane(jt);
		ini();
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
		super.resized(x, y);
		set(jspst, x, y, 500, 150, 400, 800);
		set(adds, x, y, 500, 1000, 200, 50);
		set(rems, x, y, 700, 1000, 200, 50);
		set(name, x, y, 500, 1100, 400, 50);
		set(veif, x, y, 950, 100, 400, 50);
		set(jspe, x, y, 950, 150, 400, 1100);
		set(jspjt, x, y, 1400, 450, 850, 800);
		set(adicn, x, y, 200, 150, 200, 50);
		set(reicn, x, y, 200, 250, 200, 50);

		for (int i = 0; i < 3; i++)
			set(type[i], x, y, 1550 + 250 * i, 250, 200, 50);
		set(addl, x, y, 1800, 350, 200, 50);
		set(reml, x, y, 2050, 350, 200, 50);
		jt.setRowHeight(size(x, y, 50));
		jle.setFixedCellHeight(size(x, y, 50));
	}

	private void addListeners() {
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
				efp = new EnemyFindPage(getThis(), true, pack);
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
			if (rand.icon != null) {
				File file = ((Source.Workspace) pack.source).getRandIconFile("enemyDisplayIcons", rand.id);
				file.delete();
			}
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

		adicn.addActionListener(arg0 -> getFile("Choose your file"));

		reicn.addActionListener(arg0 -> {
			File file = ((Source.Workspace) pack.source).getRandIconFile("enemyDisplayIcons", rand.id);
			if (file.delete()) {
				rand.icon = null;
				reicn.setEnabled(false);
			}
		});

		name.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				if (rand == null)
					return;
				rand.name = name.getText().trim();
				setER(rand);
				jlst.revalidate();
				jlst.repaint();
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
		add(veif);
		add(adds);
		add(rems);
		add(jspjt);
		add(jspst);
		add(addl);
		add(reml);
		add(jspe);
		add(name);
		add(adicn);
		add(reicn);
		for (int i = 0; i < 3; i++)
			add(type[i] = new JTG(1, "ert" + i));
		setES();
		jlst.setCellRenderer(new AnimLCR());
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
			adicn.setEnabled(b);
			reicn.setEnabled(b && st.icon != null);
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

	private void getFile(String str) {
		BufferedImage bimg = new Importer(str, Importer.IMP_IMG).getImg();
		if (bimg == null)
			return;
		bimg = resizeImage(bimg, 85, 32);

		if (rand.icon != null)
			rand.icon.setImg(MainBCU.builder.build(bimg));
		else
			rand.icon = MainBCU.builder.toVImg(bimg);
		try {
			File file = ((Source.Workspace) pack.source).getRandIconFile("enemyDisplayIcons", rand.id);
			Context.check(file);
			ImageIO.write(bimg, "PNG", file);
		} catch (IOException e) {
			CommonStatic.ctx.noticeErr(e, Context.ErrType.WARN, "failed to write file");
			getFile("Failed to save file");
			return;
		}
		setIconImage(jlst.getSelectedValue());
		setES();
	}

	private void setIconImage(EneRand slt) {
		if (rand == null)
			return;
		if (jlst.getSelectedValue() != slt) {
			jlst.setSelectedValue(slt, true);
		}
	}
}
