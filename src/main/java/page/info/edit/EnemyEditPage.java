package page.info.edit;

import common.CommonStatic;
import common.battle.data.CustomEnemy;
import common.battle.data.CustomEntity;
import common.pack.PackData;
import common.pack.UserProfile;
import common.system.ENode;
import common.util.unit.AbEnemy;
import common.util.unit.Enemy;
import org.jcodec.common.tools.MathUtil;
import page.*;
import page.info.EnemyInfoPage;
import page.info.filter.EnemyEditBox;
import utilpc.Interpret;

import java.util.ArrayList;
import java.util.List;

public class EnemyEditPage extends EntityEditPage {

	private static final long serialVersionUID = 1L;

	private final JL ldr = new JL(MainLocale.INFO, "drop");
	private final JTF fdr = new JTF();
	private final JTF fsr = new JTF();
	private final JBTN vene = new JBTN(MainLocale.PAGE, "vene");
	private final JBTN stat = new JBTN(MainLocale.PAGE, "stat");
	private final JBTN impt = new JBTN(MainLocale.PAGE, "import");
	private final JBTN vuni = new JBTN(MainLocale.PAGE, "unit");
	private final EnemyEditBox eeb;
	private final Enemy ene;
	private final CustomEnemy ce;

	public EnemyEditPage(Page p, Enemy e, PackData.UserPack pack) {
		super(p, pack, (CustomEntity) e.de, true);
		ene = e;
		ce = (CustomEnemy) ene.de;
		eeb = new EnemyEditBox(this, pack, ce);
		ini();
		setData((CustomEnemy) e.de);
	}

	@Override
	protected void getInput(JTF jtf, int[] v) {
		if (jtf == fli) {
			float firstDouble = CommonStatic.parseFloatN(fli.getText());
			int formatDouble = (int) (Interpret.formatDouble(firstDouble, 1) * 10);
			float result = (25 * (float) Math.floor(formatDouble / 25f)) / 10;

			ce.limit = result;
			fli.setText(String.valueOf(result));
		}
		if (jtf == fdr) {
			ce.drop = Math.round(v[0] / bas.t().getDropMulti(false));
		}
		if (jtf == fsr) {
			v[0] = MathUtil.clip(v[0], 0, 4);
			ce.star = v[0];
		}
	}

	@Override
	protected void ini() {
		set(ldr);
		set(fdr);
		set(fsr);
		super.ini();
		add(eeb);
		add(vene);
		add(stat);
		add(impt);
		add(vuni);

		List<Enemy> m = UserProfile.getAll(ene.id.pack, Enemy.class);
		ArrayList<AbEnemy> l = new ArrayList<>(m);
		stat.setLnr(x -> changePanel(new EnemyInfoPage(this, ENode.getListE(l, ene))));
		subListener(impt, vuni, vene, ene);
		assignSubPage(eeb);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(ldr, x, y, 50, 350, 100, 50);
		set(fdr, x, y, 150, 350, 200, 50);
		set(eeb, x, y, 50, 650, 600, 500);
		set(fsr, x, y, 50, 1150, 200, 50);

		if (pack.editable) {
			set(impt, x, y, 250, 1150, 200, 50);
			set(vuni, x, y, 450, 1150, 200, 50);
			set(vene, x, y, 1800, 1100, 200, 50);
			set(stat, x, y, 2000, 1100, 200, 50);
		} else {
			set(vene, x, y, 650, 1000, 200, 50);
			set(stat, x, y, 850, 1000, 200, 50);
		}
	}

	@Override
	protected void setData(CustomEntity data) {
		super.setData(data);
		fsr.setText("star: " + ce.star);
		fdr.setText(String.valueOf(Math.floor(ce.getDrop() * bas.t().getDropMulti(false)) / 100));
		fli.setText(String.valueOf(ce.getLimit()));
		fli.setToolTipText("<html>"
				+ "This enemy will stay at least "
				+ (ce.getLimit() - 100)
				+ " units from position 0. If the enemy is a boss,<br>the value will be added by the boss spawn point determined by the stage castle."
				+ "</html>");
		eeb.setData(ce.abi, data.traits);
	}

}