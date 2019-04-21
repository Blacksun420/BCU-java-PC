package util.stage;

import util.basis.StageBasis;
import util.entity.EEnemy;
import util.unit.AbEnemy;
import util.unit.EnemyStore;
import static util.stage.SCDef.*;

public class EStage {

	public final Stage s;
	public final Limit lim;
	public final int[] num, rem;
	public final double mul;

	private StageBasis b;

	public EStage(Stage st, int star) {
		s = st;
		st.validate();
		int[][] datas = s.data.getSimple();
		rem = new int[datas.length];
		num = new int[datas.length];
		for (int i = 0; i < rem.length; i++)
			num[i] = datas[i][N];
		lim = st.getLim(star);
		mul = st.map.stars[star] * 0.01;
	}

	/** add n new enemies to StageBasis */
	public EEnemy allow() {
		for (int i = 0; i < rem.length; i++) {
			int[] data = s.data.getSimple(i);
			if (inHealth(data[C0], data[C1]) &&s.data.allow(b,data[G])&& rem[i] == 0 && num[i] != -1) {
				rem[i] = data[R0] + (int) (b.r.nextDouble() * (data[R1] - data[R0]));
				if (num[i] > 0) {
					num[i]--;
					if (num[i] == 0)
						num[i] = -1;
				}
				if (data[8] == 1)
					b.shock = true;
				double multi = (data[M] == 0 ? 100 : data[M]) * mul * 0.01;
				AbEnemy e = EnemyStore.getAbEnemy(data[0], false);
				EEnemy ee = e.getEntity(b, data, multi, data[L0], data[L1], data[B]);
				ee.group = data[G];
				return ee;
			}
		}
		return null;
	}

	public void assign(StageBasis sb) {
		b = sb;
		int[][] datas = s.data.getSimple();
		for (int i = 0; i < rem.length; i++) {
			rem[i] = datas[i][S0];
			if (datas[i][S0] < datas[i][S1])
				rem[i] += (int) ((datas[i][S1] - datas[i][S0]) * b.r.nextDouble());
		}
	}

	/** get the Entity representing enemy base, return null if none */
	public EEnemy base(StageBasis sb) {
		int ind = num.length - 1;
		int[] data = s.data.getSimple(ind);
		if (ind >= 0 && data[C0] == 0) {
			num[ind] = -1;
			double multi = data[M] * mul * 0.01;
			AbEnemy e = EnemyStore.getAbEnemy(data[E], false);
			return e.getEntity(sb, this, multi, data[L0], data[L1], -1);
		}
		return null;
	}

	/** return true if there is still boss in the base */
	public boolean hasBoss() {
		for (int i = 0; i < rem.length; i++) {
			int[] data = s.data.getSimple(i);
			if (data[B] == 1 && num[i] > 0)
				return true;
		}
		return false;
	}

	public void update() {
		for (int i = 0; i < rem.length; i++) {
			int[] data = s.data.getSimple(i);
			if (inHealth(data[C0], data[C1]) && rem[i] < 0)
				rem[i] *= -1;
			if (rem[i] > 0)
				rem[i]--;
		}
	}

	private boolean inHealth(int c0, int c1) {
		double d = !s.trail ? b.getEBHP() * 100 : b.ebase.maxH - b.ebase.health;
		return c0 >= c1 ? (s.trail ? d >= c0 : d <= c0) : (d > c0 && d <= c1);
	}

}
