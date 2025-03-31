package page.battle;

import common.battle.BattleField;
import common.util.unit.Character;
import page.MainLocale;
import page.Page;
import page.support.CharaTCR;
import page.support.SortTable;

public class TotalDamageTable extends SortTable<Character> {
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
        setDefaultRenderer(Character.class, new CharaTCR(lnk, 0));
        sign = -1;
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (lnk[c] == 0)
            return Character.class;
        else
            return Object.class;
    }

    @Override
    protected int compare(Character c0, Character c1, int c) {
        if(c == 0)
            return c0.getID().compareTo(c1.getID());
        else if (c == 3)
            return Integer.compare((int) get(c0, c), (int) get(c1, c));
        return Long.compare((long) get(c0, c), (long) get(c1, c));
    }

    @Override
    protected Object get(Character ch, int c) {
        if(c == 0)
            return ch;
        else if (c == 3)
            return bf.sb.spawns.get(ch);
        return bf.sb.dmgStatistics.get(ch)[c - 1];
    }
}