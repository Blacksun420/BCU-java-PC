package page.battle;

import common.battle.StageBasis;
import common.util.unit.AbForm;
import common.util.unit.Form;
import page.MainLocale;
import page.Page;
import page.support.SortTable;
import page.support.UnitTCR;

public class TotalDamageTable extends SortTable<AbForm> {
    private static String[] title;

    static {
        redefine();
    }

    public static void redefine() {
        title = Page.get(MainLocale.INFO, "ut", 3);
    }

    private final StageBasis basis;

    protected TotalDamageTable(StageBasis basis) {
        super(title);

        this.basis = basis;

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
        } else {
            return Long.compare((long) get(e0, c), (long) get(e1, c));
        }
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
                return basis.totalDamageGiven[index[0]][index[1]];
            else
                return basis.totalDamageTaken[index[0]][index[1]];
        }
    }

    private int[] findForm(AbForm f) {
        for(int i = 0; i < basis.b.lu.fs.length; i++) {
            for(int j = 0; j < basis.b.lu.fs[i].length; j++) {
                AbForm target = basis.b.lu.fs[i][j];

                if(target == null)
                    continue;

                if(target.getID().equals(f.getID())) {
                    return new int[] {i, j};
                }
            }
        }

        return null;
    }
}
