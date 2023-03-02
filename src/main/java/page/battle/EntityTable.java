package page.battle;

import common.CommonStatic;
import common.battle.data.MaskEnemy;
import common.battle.data.MaskUnit;
import common.battle.entity.Entity;
import common.pack.Identifier;
import common.util.unit.Enemy;
import common.util.unit.Form;
import page.MainLocale;
import page.support.EnemyTCR;
import page.support.SortTable;
import page.support.UnitTCR;

import java.text.DecimalFormat;

class EntityTable extends SortTable<Entity> {

	private static final long serialVersionUID = 1L;
	private static final DecimalFormat df = new DecimalFormat("#.##");

	private final boolean statistics;

	private final int dir;

	protected EntityTable(int dire, boolean statistics) {
		super(MainLocale.getLoc(MainLocale.INFO, statistics ? "us" : "u", statistics ? 5 : 3));

		dir = dire;
		this.statistics = statistics;

		if (dire == 1)
			setDefaultRenderer(Enemy.class, new EnemyTCR());
		else
			setDefaultRenderer(Form.class, new UnitTCR(lnk));

		sign = -1;
	}

	@Override
	public Class<?> getColumnClass(int c) {
		if (lnk[c] == 1)
			return dir == 1 ? Enemy.class : Form.class;
		else
			return Object.class;
	}

	@Override
	protected int compare(Entity e0, Entity e1, int c) {
		if(statistics) {
			if (c == 0 || c == 2)
				return Long.compare((long) get(e0, c), (long) get(e1, c));
			else if (c == 1) {
				Identifier<?> id0 = getID(e0);
				Identifier<?> id1 = getID(e1);
				if(id0 == null && id1 == null)
					return 0;
				if(id0 == null)
					return -1;
				if(id1 == null)
					return 1;

				return id0.compareTo(id1);
			} else if(c == 3)
				return Double.compare((double) get(e0, 3), (double) get(e1, 3));
			return Integer.compare((int) get(e0, c), (int) get(e1, c));
		} else {
			if (c == 0)
				return Long.compare(CommonStatic.parseLongN(get(e0,0).toString()),CommonStatic.parseLongN(get(e1,0).toString()));
			if (c == 1) {
				Identifier<?> id0 = getID(e0);
				Identifier<?> id1 = getID(e1);

				if(id0 == null && id1 == null)
					return 0;

				if(id0 == null)
					return -1;

				if(id1 == null)
					return 1;

				return id0.compareTo(id1);
			} if (c == 3)
				return Double.compare((double) get(e0, 3), (double) get(e1, 3));
			else
				return Long.compare((long) get(e0, c), (long) get(e1, c));
		}
	}

	@Override
	protected Object get(Entity t, int c) {
		if(statistics) {
			if(c == 0)
				return t.damageTaken;
			else if(c == 1)
				return t.data.getPack();
			else if(c == 2)
				return t.damageGiven;
			else if(c == 3)
				return t.livingTime == 0 ? 0.0 : CommonStatic.parseDoubleN(df.format(t.damageGiven * 30.0 / t.livingTime));
			return t.livingTime;
		} else {
			if (c == 0)
				return (t.hasBarrier() ? "[" : "") + t.health + (t.status.shield[0] > 0 ? " (+" + t.status.shield[0] + ")" : "") + (t.hasBarrier() ? "]" : "");
			else if (c == 1)
				return t.data.getPack();
			return (long) t.getAtk();
		}
	}

	private Identifier<?> getID(Entity e) {
		if (e.data instanceof MaskUnit)
			return ((MaskUnit) e.data).getPack().uid;
		if (e.data instanceof MaskEnemy)
			return ((MaskEnemy) e.data).getPack().id;

		return null;
	}

}