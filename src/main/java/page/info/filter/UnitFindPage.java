package page.info.filter;

import common.CommonStatic;
import common.pack.PackData.UserPack;
import common.util.unit.AbForm;
import common.util.unit.AbUnit;
import page.Page;
import page.SupPage;

import javax.swing.*;

public class UnitFindPage extends EntityFindPage<AbForm> implements SupPage<AbUnit> {

	private static final long serialVersionUID = 1L;

	public UnitFindPage(Page p, boolean rand) {
		super(p);

		elt = new UnitListTable(this);
		jsp = new JScrollPane(elt);
		efb = new UnitFilterBox(this, rand, null, 0);
		ini();
		resized();
	}

	public UnitFindPage(Page p, boolean rand, UserPack pack) {
		super(p);

		elt = new UnitListTable(this);
		jsp = new JScrollPane(elt);
		efb = new UnitFilterBox(this, rand, pack);
		ini();
		resized();
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
				AbUnit sel = elt.list.get(i).unit();
				if (!CommonStatic.getConfig().favoriteUnits.contains(sel.getID()))
					CommonStatic.getConfig().favoriteUnits.add(sel.getID());
				else
					CommonStatic.getConfig().favoriteUnits.remove(sel.getID());
			}
		});
	}

	@Override
	public AbUnit getSelected() {
		AbForm f = getForm();
		return f == null ? null : f.unit();
	}
}
