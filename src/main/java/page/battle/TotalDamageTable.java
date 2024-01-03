package page.battle;

import common.battle.BattleField;
import common.util.unit.AbForm;
import common.util.unit.Form;
import page.MainLocale;
import page.Page;
import page.support.SortTable;
import page.support.UnitTCR;

public class TotalDamageTable extends SortTable<AbForm> {
    private static final long serialVersionUID = 1L;
    private static String[] title;

    static {
        redefine();
    }

    public static void redefine() {
        title = Page.get(MainLocale.INFO, "ut", 4);
    }

    private final BattleField bf;

    protected TotalDamageTable(BattleField bf) {
        super(title);

        this.bf = bf;
        setDefaultRenderer(Form.class, new UnitTCR(lnk, 0));
        sign = -1;
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (lnk[c] == 0)
            return Form.class;
        else
            return Object.class;
    }

    @Override
    protected int compare(AbForm e0, AbForm e1, int c) {
        if(c == 0) {
            if(e0 == null && e1 == null)
                return 0;
            if(e0 == null)
                return -1;
            if(e1 == null)
                return 1;

            return e0.getID().compareTo(e1.getID());
        } else if (c == 3)
            return Integer.compare((int) get(e0, 3), (int) get(e1, 3));
        return Long.compare((long) get(e0, c), (long) get(e1, c));
    }

    @Override
    protected Object get(AbForm form, int c) {
        if(c == 0)
            return form;
        else {
            int[] index = findForm(form);

            if(index == null)
                return 0L;

            if(c == 1)
                return bf.sb.totalDamageGiven[index[0]][index[1]];
            else if (c == 2)
                return bf.sb.totalDamageTaken[index[0]][index[1]];
            else if (index[1] != 5)
                return bf.sb.totalSpawned[index[0]][index[1]];
            return -1;
        }
    }

    private int[] findForm(AbForm f) {
        for(int i = 0; i < bf.sb.b.lu.fs.length; i++)
            for(int j = 0; j < bf.sb.b.lu.fs[i].length; j++) {
                AbForm target = bf.sb.b.lu.fs[i][j];

                if(target != null && target.getID().equals(f.getID()))
                    return new int[] {i, j};
            }
        if (bf.sb.est.getBase() != null && f.getID().equals(bf.sb.est.getBase().getID()))
            return new int[] {1, 5};
        return null;
    }
}