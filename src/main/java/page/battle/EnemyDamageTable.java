package page.battle;

import common.battle.BattleField;
import common.util.unit.Enemy;
import page.MainLocale;
import page.Page;
import page.support.EnemyTCR;
import page.support.SortTable;

public class EnemyDamageTable extends SortTable<Enemy> {
    private static String[] title;

    static {
        redefine();
    }

    public static void redefine() {
        title = Page.get(MainLocale.INFO, "ut", 4);
    }

    private final BattleField bf;

    protected EnemyDamageTable(BattleField bf) {
        super(title);

        this.bf = bf;
        setDefaultRenderer(Enemy.class, new EnemyTCR());
        sign = -1;
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (lnk[c] == 0)
            return Enemy.class;
        else
            return Object.class;
    }

    @Override
    protected int compare(Enemy e0, Enemy e1, int c) {
        if(c == 0)
            return e0.getID().compareTo(e1.getID());
        return Long.compare((long) get(e0, c), (long) get(e1, c));
    }

    @Override
    protected Object get(Enemy ene, int c) {
        if(c == 0)
            return ene;
        else
            return bf.sb.enemyStatistics.get(ene)[c - 1];
    }
}
