package page.info.filter;

import common.CommonStatic;
import common.pack.PackData.UserPack;
import common.util.unit.AbForm;
import common.util.unit.AbUnit;
import page.Page;
import page.SupPage;
import utilpc.Interpret;

import javax.swing.*;

public class UnitFindPage extends EntityFindPage<AbForm> implements SupPage<AbUnit> {

	private static final long serialVersionUID = 1L;

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
		resized(true);
	}

	public AbForm getForm() {
		if (elt.getSelectedRow() == -1 || elt.getSelectedRow() > elt.list.size() - 1)
			return null;
		return elt.list.get(elt.getSelectedRow());
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
	}

	@Override
	public AbUnit getSelected() {
		AbForm f = getForm();
		return f == null ? null : f.unit();
	}
}
