package page.info.filter;

import common.battle.Basis;
import common.battle.BasisSet;
import common.battle.data.MaskUnit;
import common.pack.UserProfile;
import common.system.Node;
import common.util.unit.*;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.info.UnitInfoPage;
import page.pack.UREditPage;
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
        tit = new String[] { "ID", "name", Page.get(MainLocale.INFO, "pref"), "HP", Page.get(MainLocale.INFO,"hb"), "atk", Page.get(MainLocale.INFO, "range"),
                Page.get(MainLocale.INFO, "speed"), "dps", Page.get(MainLocale.INFO, "preaa"), "CD", Page.get(MainLocale.INFO, "price"), Page.get(MainLocale.INFO, "atkf"), Page.get(MainLocale.INFO, "will") };
    }

    private final Page page;

    public AbUnitListTable(Page p) {
        super(tit);

        page = p;

        setDefaultRenderer(Enemy.class, new UnitTCR(lnk));

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
        if (e instanceof Form) {
            Node<Unit> n = Node.getList(UserProfile.getAll((((Form) e).unit.id.pack), Unit.class), ((Form) e).unit);
            MainFrame.changePanel(new UnitInfoPage(page, n));
        } else if (e instanceof UniRand)
            MainFrame.changePanel(new UREditPage(page, UserProfile.getUserPack(e.getID().pack)));
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
            int val = e0.getID().compareTo(e1.getID());
            return val != 0 ? val : Integer.compare(e0.getFid(), e1.getFid());
        }
        if (c == 1)
            return e0.toString().compareTo(e1.toString());
        int i0 = (int) get(e0, c);
        int i1 = (int) get(e1, c);
        return Integer.compare(i0, i1);
    }

    @Override
    protected Object get(AbForm e, int c) {
        Basis b = BasisSet.current();
        if (e instanceof Form) {
            Form f = (Form) e;
            MaskUnit du = f.maxu();
            double mul = f.unit.lv.getMult(f.unit.getPrefLv());
            double atk = b.t().getAtkMulti();
            double def = b.t().getDefMulti();
            int itv = f.anim != null ? du.getItv(0) : -1;
            if (c == 0)
                return f.uid + "-" + f.fid;
            else if (c == 1)
                return f;
            else if (c == 2)
                return f.unit.getPrefLv();
            else if (c == 3)
                return (int) (du.getHp() * mul * def);
            else if (c == 4)
                return du.getHb();
            else if (c == 5)
                return (int) (Math.round(du.allAtk(0) * mul) * atk);
            else if (c == 6)
                return du.getRange();
            else if (c == 7)
                return du.getSpeed();
            else if (c == 8)
                return itv == -1 ? "Corrupted" : (int) (du.allAtk(0) * mul * atk * 30 / itv);
            else if (c == 9)
                return du.getAtkModel(0, 0).getPre();
            else if (c == 10)
                return b.t().getFinRes(du.getRespawn());
            else if (c == 11)
                return e.getDefaultPrice(1);
            else if (c == 12)
                return du.getItv(0);
            else if (c == 13)
                return du.getWill() + 1;
        } else if (e instanceof UniRand) {
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
