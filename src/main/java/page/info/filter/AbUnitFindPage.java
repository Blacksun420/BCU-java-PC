package page.info.filter;

import common.util.unit.AbForm;
import common.util.unit.AbUnit;
import page.*;

import javax.swing.*;
import java.util.List;

public class AbUnitFindPage extends EntityFindPage<AbForm> implements SupPage<AbUnit> {

    private static final long serialVersionUID = 1L;

    public AbUnitFindPage(Page p) {
        super(p);

        elt = new AbUnitListTable(this);
        jsp = new JScrollPane(elt);
        efb = new AbUnitFilterBox(this);
        ini();
        resized();
    }

    public AbUnitFindPage(Page p, String pack, List<String> parents) {
        super(p);

        elt = new AbUnitListTable(this);
        jsp = new JScrollPane(elt);
        efb = new AbUnitFilterBox(this, pack, parents);
        ini();
        resized();
    }

    public AbForm getForm() {
        if (elt.getSelectedRow() == -1 || elt.getSelectedRow() > elt.list.size() - 1)
            return null;
        return elt.list.get(elt.getSelectedRow());
    }

    @Override
    public AbUnit getSelected() {
        AbForm f = getForm();
        return f == null ? null : f.getID().get();
    }
}
