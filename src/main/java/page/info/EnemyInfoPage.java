package page.info;

import common.pack.Identifier;
import common.system.ENode;
import page.DefaultPage;
import page.JBTN;
import page.JTG;
import page.Page;
import page.view.EnemyViewPage;

import javax.swing.*;

public class EnemyInfoPage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	private final JBTN anim = new JBTN(0, "anim");
	private final JBTN prev = new JBTN(0, "prev");
	private final JBTN next = new JBTN(0, "next");
	private final JBTN find = new JBTN(0, "stage");
	private final JTG extr = new JTG(0, "extra");
	private final JPanel cont = new JPanel();
	private final JScrollPane jsp = new JScrollPane(cont);
	private final EnemyInfoTable info;
	private final TreaTable trea;

	private final ENode e;

	public EnemyInfoPage(Page p, ENode de) {
		this(p, de, !de.val.id.pack.equals(Identifier.DEF));
	}

	public EnemyInfoPage(Page p, ENode de, boolean sp) {
		super(p);
		e = de;

		info = new EnemyInfoTable(this, de.val, de.mul, de.mula, sp);
		trea = new TreaTable(this);
		ini();
		extr.setSelected(sp);
	}

	@Override
	public void callBack(Object o) {
		info.reset();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(prev, x, y, 300, 0, 200, 50);
		set(anim, x, y, 600, 0, 200, 50);
		set(next, x, y, 900, 0, 200, 50);
		set(find, x, y, 1200, 0, 200, 50);
		set(extr, x, y, 1500, 0, 200, 50);
		set(jsp, x, y, 50, 100, 1650, 1150);
		set(trea, x, y, 1700, 100, 400, 1200);
		int ih = info.getH();
		cont.setPreferredSize(size(x, y, 1600, ih).toDimension());
		jsp.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
		set(info, x, y, 0, 0, 1600, ih);
	}

	@Override
	public synchronized void onTimer(int t) {
		super.onTimer(t);
		jsp.revalidate();
	}

	private void addListeners() {
		prev.addActionListener(arg0 -> changePanel(new EnemyInfoPage(getFront(), (ENode) e.prev, extr.isSelected())));

		anim.addActionListener(arg0 -> {
			if (getFront() instanceof EnemyViewPage)
				changePanel(getFront());
			else
				changePanel(new EnemyViewPage(getThis(), e.val));
		});

		next.addActionListener(arg0 -> changePanel(new EnemyInfoPage(getFront(), (ENode) e.next, extr.isSelected())));

		find.addActionListener(arg0 -> changePanel(new StageFilterPage(getThis(), e.val.findApp())));

		extr.addActionListener(arg0 -> {
			info.displaySpecial = extr.isSelected();
			info.fireDimensionChanged();
			fireDimensionChanged();
		});
	}

	private void ini() {
		assignSubPage(info);
		cont.add(info);
		cont.setLayout(null);
		add(jsp);
		add(trea);
		add(prev);
		add(anim);
		add(next);
		add(find);
		add(extr);
		prev.setEnabled(e.prev != null);
		next.setEnabled(e.next != null);
		addListeners();
	}

}
