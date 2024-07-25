package page.basis;

import common.CommonStatic;
import common.battle.BasisSet;
import common.pack.SaveData;
import common.util.stage.Limit;
import common.util.unit.AbForm;
import page.*;
import page.info.filter.AdvProcFilterPage;
import page.info.filter.UnitFilterBox;
import page.info.filter.UnitListTable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Set;

public class UnitFLUPage extends LubCont {

	private static final long serialVersionUID = 1L;

	private final LineUpBox lub = new LineUpBox(this);
	private final JTG show = new JTG(0, "showf");
	private final UnitListTable ult = new UnitListTable(this);
	private final JScrollPane jsp = new JScrollPane(ult);
	public final UnitFilterBox ufb;
	public final AdvProcFilterPage adv;
	private final JTF seatf = new JTF();
	private final JTG advs = new JTG(MainLocale.PAGE, "advance");

	private final JBTN fbtn = new JBTN("Any Form");
	private final JTF ffrm = new JTF("4");

	public UnitFLUPage(Page p, SaveData sdat, Limit lim, int price, Set<AbForm> ulk) {
		super(p);

		ult.cost = price;
		lub.setLimit(lim, sdat, price);
		if (ulk != null)
			lub.setTest(ulk);
		ufb = new UnitFilterBox(this, true, lim, price, sdat, ulk);
		adv = new AdvProcFilterPage(this, true, ufb.proc);
		ini();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void callBack(Object o) {
		if (o instanceof List)
			ult.setList((List<AbForm>) o);
	}

	public List<AbForm> getList() {
		return ult.list;
	}

	@Override
	protected LineUpBox getLub() {
		return lub;
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		if (e.getSource() == ult)
			ult.clicked(e.getPoint());
		super.mouseClicked(e);
	}

	@Override
	protected void keyTyped(KeyEvent e) {
		if (!seatf.isFocusOwner() && !ffrm.isFocusOwner()) {
			super.keyTyped(e);
			e.consume();
		}
	}

	@Override
	protected void renew() {
		lub.setLU(BasisSet.current().sele.lu);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(show, x, y, 250, 0, 150, 50);
		set(advs, x, y, 400, 0, 150, 50);
		set(seatf, x, y, 600, 0, 750, 50);

		int[] coords = new int[]{50, 2200, 1150};
		if (show.isSelected()) {
			int[] siz = ufb.getSizer();
			set(ufb, x, y, 50, 100, siz[0], siz[1]);
			coords[0] += siz[3];
			coords[1] -= siz[3];
			coords[2] -= 350;
		} else
			coords[1] = 1550;
		if (advs.isSelected()) {
			coords[show.isSelected() ? 0 : 1] -= 50;

			set(adv, x, y, coords[0], 100, 350 * 3 + 25, 1150);
			coords[0] += 350 * 3 + 75;
			coords[1] -= 350 * 3 + 25;
		} else if (adv != null)
			set(adv, 0, 0, 0, 0, 0, 0);
		set(jsp, x, y, coords[0], 100, coords[1], coords[2]);
		set(lub, x, y, 1650, 950, 600, 300);
		ult.setRowHeight(size(x, y, 50));

		if (ufb == null)
			return;
		if (ufb.frmf == -1) {
			set(fbtn, x, y, 1350, 0, 300, 50);
			set(ffrm, x, y, 0, 0, 0, 0);
		} else {
			set(fbtn, x, y, 1350, 0, 200, 50);
			set(ffrm, x, y, 1550, 0, 100, 50);
		}
	}

	private void addListeners() {
		show.addActionListener(arg0 -> {
			if (show.isSelected())
				add(ufb);
			else
				remove(ufb);
		});

		advs.addActionListener(arg0 -> {
			if (advs.isSelected())
				add(adv);
			else
				remove(adv);
		});

		ListSelectionModel lsm = ult.getSelectionModel();
		ult.setRowSelectionAllowed(false);

		lsm.addListSelectionListener(e -> {
			if (lsm.getValueIsAdjusting())
				return;
			int ind = lsm.getAnchorSelectionIndex();
			if (ind < 0)
				return;
			AbForm f = ult.list.get(ind);
			lub.select(f);
			lsm.clearSelection();
		});

		seatf.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				search(seatf.getText());
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				search(seatf.getText());
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				search(seatf.getText());
			}
		});

		fbtn.setLnr(e -> {
			if (ufb.frmf == -1) {
				ufb.frmf = (byte) (CommonStatic.parseIntN(ffrm.getText()) - 1);
				fbtn.setText("Max Form #");
			} else {
				ufb.frmf = ufb.rf ? -1 : ufb.frmf;
				ufb.rf = !ufb.rf;
				fbtn.setText(ufb.frmf == -1 ? "Any Form" : "Form #");
			}
			ufb.callBack(null);
			fireDimensionChanged();
		});

		ffrm.setLnr(e -> {
			byte ind = (byte) (Math.max(Math.min(128, CommonStatic.parseIntN(ffrm.getText())), 1) - 1);
			ufb.frmf = ind;
			ffrm.setText(String.valueOf(ind + 1));
			ufb.callBack(null);
		});
	}

	protected void search(String text) {
		if (ufb != null) {
			ufb.name = text;
			ufb.callBack(null);
		}
	}

	private void ini() {
		add(show);
		add(ufb);
		add(jsp);
		add(lub);
		add(seatf);
		add(fbtn);
		add(ffrm);
		seatf.setHintText(get(MainLocale.PAGE,"search"));
		add(advs);
		assignSubPage(adv);
		show.setSelected(true);
		addListeners();
	}

}