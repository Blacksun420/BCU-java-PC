package page.info.filter;

import common.util.unit.AbUnit;
import common.util.unit.Form;
import common.util.unit.Unit;
import page.Page;
import page.SupPage;

import javax.swing.*;
import java.util.List;

public class UnitFindPage extends EntityFindPage<Form> implements SupPage<AbUnit> {

	private static final long serialVersionUID = 1L;

	public UnitFindPage(Page p) {
		super(p);

		elt = new UnitListTable(this);
		jsp = new JScrollPane(elt);
		efb = new UnitFilterBox(this, null, 0);
		ini();
		resized();
	}

	public UnitFindPage(Page p, String pack, List<String> parents) {
		super(p);

		elt = new UnitListTable(this);
		jsp = new JScrollPane(elt);
		efb = new UnitFilterBox(this, pack, parents);
		ini();
		resized();
	}

	public Form getForm() {
		if (elt.getSelectedRow() == -1 || elt.getSelectedRow() > elt.list.size() - 1)
			return null;
		return elt.list.get(elt.getSelectedRow());
	}

	@Override
	public Unit getSelected() {
		Form f = getForm();
		return f == null ? null : f.unit;
	}
}
