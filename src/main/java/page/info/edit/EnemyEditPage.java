package page.info.edit;

import common.CommonStatic;
import common.battle.data.CustomEnemy;
import common.battle.data.CustomEntity;
import common.pack.PackData;
import common.pack.UserProfile;
import common.system.ENode;
import common.util.unit.Enemy;
import org.jcodec.common.tools.MathUtil;
import page.*;
import page.info.EnemyInfoPage;
import page.info.filter.EnemyEditBox;
import utilpc.Interpret;

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
		super(p, e.id.pack, (CustomEntity) e.de, pack.editable, true);
		ene = e;
		ce = (CustomEnemy) ene.de;
		eeb = new EnemyEditBox(this, pack, ce);
		ini();
		setData((CustomEnemy) e.de);
		resized();
	}

	@Override
	protected void getInput(JTF jtf, int[] v) {
		if (jtf == fli) {
			double firstDouble = CommonStatic.parseDoubleN(fli.getText());
			int formatDouble = (int) (Interpret.formatDouble(firstDouble, 1) * 10);
			double result = (25 * Math.floor(formatDouble / 25.0)) / 10;

			ce.limit = result;
			fli.setText(result + "");
		}
		if (jtf == fdr) {
			ce.drop = (int) Math.round(v[0] / bas.t().getDropMulti());
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

		stat.setLnr(x -> changePanel(new EnemyInfoPage(this, ENode.getList(UserProfile.getAll(ene.id.pack, Enemy.class), ene))));
		subListener(impt, vuni, vene, ene);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(ldr, x, y, 50, 350, 100, 50);
		set(fdr, x, y, 150, 350, 200, 50);
		set(eeb, x, y, 50, 650, 600, 500);
		set(fsr, x, y, 50, 1150, 200, 50);

		if (editable) {
			set(impt, x, y, 250, 1150, 200, 50);
			set(vuni, x, y, 450, 1150, 200, 50);
			set(vene, x, y, 1800, 1100, 200, 50);
			set(stat, x, y, 2000, 1100, 200, 50);
		} else {
			set(vene, x, y, 650, 1000, 200, 50);
			set(stat, x, y, 850, 1000, 200, 50);
		}
		eeb.resized();

	}

	@Override
	protected void setData(CustomEntity data) {
		super.setData(data);
		fsr.setText("star: " + ce.star);
		fdr.setText("" + Math.floor(ce.getDrop() * bas.t().getDropMulti()) / 100);
		fli.setText(ce.getLimit() + "");
		fli.setToolTipText("<html>"
				+ "This enemy will stay at least "
				+ (ce.getLimit() - 100)
				+ " units from position 0. If the enemy is a boss,<br>the value will be added by the boss spawn point determined by the stage castle."
				+ "</html>");
		eeb.setData(ce.abi, data.traits);
	}

}
