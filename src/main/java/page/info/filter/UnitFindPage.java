package page.info.filter;

import common.CommonStatic;
import common.pack.PackData.UserPack;
import common.util.unit.AbForm;
import common.util.unit.AbUnit;
import page.JBTN;
import page.JTF;
import page.Page;
import page.SupPage;
import utilpc.Interpret;

import javax.swing.*;

public class UnitFindPage extends EntityFindPage<AbForm> implements SupPage<AbUnit> {

	private static final long serialVersionUID = 1L;

	private final JBTN fbtn = new JBTN("Any Form");
	private final JTF ffrm = new JTF("4");

	public UnitFindPage(Page p, boolean rand) {
		this(p, rand, null);
	}

	public UnitFindPage(Page p, boolean rand, UserPack pack) {
		super(p);

		elt = new UnitListTable(this);
		jsp = new JScrollPane(elt);
		efb = new UnitFilterBox(this, rand, pack);
		adv = new AdvProcFilterPage(this, true, efb.proc);
		ini();
	}

	public AbForm getForm() {
		if (elt.getSelectedRow() == -1 || elt.getSelectedRow() > elt.list.size() - 1)
			return null;
		return elt.list.get(elt.getSelectedRow());
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(seatf, x, y, 600, 0, 750, 50);
		if (efb == null)
			return;
		if (((UnitFilterBox)efb).frmf == -1) {
			set(fbtn, x, y, 1350, 0, 300, 50);
			set(ffrm, x, y, 0, 0, 0, 0);
		} else {
			set(fbtn, x, y, 1350, 0, 200, 50);
			set(ffrm, x, y, 1550, 0, 100, 50);
		}
	}

	@Override
	protected void ini() {
		super.ini();
		add(fbtn);
		add(ffrm);
	}

	@Override
	protected void addListeners() {
		super.addListeners();
		favs.addActionListener(e -> {
			int[] list = elt.getSelectedRows();
			for (int i : list) {
				AbForm sel = elt.list.get(i);
				if (!CommonStatic.getFaves().units.contains(sel))
					CommonStatic.getFaves().units.add(sel);
				else
					CommonStatic.getFaves().units.remove(sel);
				if (((UnitFilterBox)efb).rare.isSelectedIndex(Interpret.RARITY_TOT))
					efb.confirm();
			}
		});

		fbtn.setLnr(e -> {
			UnitFilterBox ufb = (UnitFilterBox)efb;
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
			((UnitFilterBox)efb).frmf = ind;
			ffrm.setText(String.valueOf(ind + 1));
			efb.callBack(null);
		});
	}

	@Override
	public AbUnit getSelected() {
		AbForm f = getForm();
		return f == null ? null : f.unit();
	}
}