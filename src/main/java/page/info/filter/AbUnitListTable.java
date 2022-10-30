package page.info.filter;

import common.pack.UserProfile;
import common.system.Node;
import common.util.unit.AbForm;
import common.util.unit.Enemy;
import common.util.unit.Unit;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.info.UnitInfoPage;
import page.support.SortTable;
import page.support.UnitTCR;

import java.awt.*;

public class AbUnitListTable extends SortTable<AbForm> {

    private static final long serialVersionUID = 1L;

    private static String[] tit;

    static {
        redefine();
    }

    public static void redefine() {
        tit = new String[] { "ID", "name", Page.get(MainLocale.INFO, "pref") };
    }

    private final Page page;

    public AbUnitListTable(Page p) {
        super(tit);

        page = p;

        setDefaultRenderer(Unit.class, new UnitTCR(lnk));
    }

    public void clicked(Point p) {
        if (list == null)
            return;
        int c = getColumnModel().getColumnIndexAtX(p.x);
        c = lnk[c];
        int r = p.y / getRowHeight();
        if (r < 0 || r >= list.size() || c != 1)
            return;
        AbForm e = list.get(r);
        if (e instanceof Unit)
            MainFrame.changePanel(new UnitInfoPage(page, Node.getList(UserProfile.getAll(e.getID().pack, Unit.class), (Unit)e)));
    }

    @Override
    public Class<?> getColumnClass(int c) {
        c = lnk[c];
        if (c == 1)
            return Enemy.class;
        return String.class;
    }

    @Override
    protected int compare(AbForm e0, AbForm e1, int c) {
        if (c == 0) {
            return e0.getID().compareTo(e1.getID());
        }
        if (c == 1)
            return e0.toString().compareTo(e1.toString());
        int i0 = (int) get(e0, c);
        int i1 = (int) get(e1, c);
        return Integer.compare(i0, i1);
    }

    @Override
    protected Object get(AbForm e, int c) {
        if (e instanceof Unit) {
            Unit u = (Unit) e;
            if (c == 0)
                return e.getID();
            else if (c == 1)
                return e;
            else if (c == 2)
                return u.getPrefLv();
        } else {
            if(c == 0)
                return e.getID();
            else if(c == 1)
                return e;
            else if(c == 2)
                return "Random Unit";
        }
        return null;
    }
}
