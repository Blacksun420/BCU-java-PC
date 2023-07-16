package page.view;

import common.pack.PackData;
import common.pack.Source;
import common.pack.UserProfile;
import common.system.ENode;
import common.util.anim.AnimCE;
import common.util.anim.AnimU;
import common.util.unit.AbEnemy;
import common.util.unit.Enemy;
import main.Opts;
import page.JBTN;
import page.Page;
import page.anim.ImgCutEditPage;
import page.info.EnemyInfoPage;
import page.support.AnimLCR;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class EnemyViewPage extends AbViewPage {

	private static final long serialVersionUID = 1L;

	private final JList<Enemy> jlu = new JList<>();
	private final JScrollPane jspu = new JScrollPane(jlu);
	private final JBTN stat = new JBTN(0, "stat");

	public EnemyViewPage(Page p, Enemy e) {
		this(p, e.getID().pack);
		jlu.setSelectedValue(e, true);

	}

	public EnemyViewPage(Page p, String pac) {
		super(p);
		PackData pack = UserProfile.getPack(pac);
		if(pack != null)
			jlu.setListData(new Vector<>(pack.enemies.getList()));
		ini();
		resized();
	}

	public EnemyViewPage(Page p) {
		super(p);
		Vector<Enemy> v = new Vector<>();

		for(PackData pack : UserProfile.getAllPacks()) {
			v.addAll(pack.enemies.getList());
		}

		jlu.setListData(v);

		ini();
		resized();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		if (!larges.isSelected()) {
			set(jspu, x, y, 50, 100, 300, 1100);
			set(stat, x, y, 400, 1000, 300, 50);
		} else {
			set(jspu, x, y, 50, 800, 300, 400);
			set(stat, x, y, 150, 1200, 200, 50);
		}
		jlu.setFixedCellHeight(size(x, y, 50));
	}

	@Override
	protected void updateChoice() {
		Enemy u = jlu.getSelectedValue();
		if (u == null)
			return;
		setAnim(u.anim);
	}

	private void addListeners() {

		jlu.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			updateChoice();
		});

		stat.addActionListener(e -> {
			Enemy ene = jlu.getSelectedValue();
			if (ene == null)
				return;

			ListModel<Enemy> enes = jlu.getModel();
			List<AbEnemy> lis = new ArrayList<>();
			for (int i = 0;i < enes.getSize(); i++)
				lis.add(enes.getElementAt(i));
			ENode n = ENode.getListE(lis, ene);

			changePanel(new EnemyInfoPage(getThis(), n));
		});

		copy.addActionListener(e -> {
			boolean change = false;
			List<Enemy> list = jlu.getSelectedValuesList();
			List<PackData> pk = new ArrayList<>();
			List<PackData> pkFail = new ArrayList<>();
			for (Enemy ene : list) {
				PackData pack = ene.getCont();
				if (pack != null && !pkFail.contains(pack))
					if (pack instanceof PackData.DefPack || pk.contains(pack) || ((PackData.UserPack) pack).editable || ((PackData.UserPack) pack).desc.allowAnim) {
						change = true;
						copyAnim(ene.anim);
					} else {
						String pass = Opts.read("Enter the pack's password:");
						if (pass == null)
							pkFail.add(pack);
						else if (((Source.ZipSource) ((PackData.UserPack) pack).source).zip.matchKey(pass)) {
							change = true;
							copyAnim(ene.anim);
							pk.add(pack);
						} else {
							Opts.pop("That's not the password", "Incorrect password");
							pkFail.add(pack);
						}
					}
				}

			if (change)
				changePanel(new ImgCutEditPage(getThis()));
		});
	}

	private void ini() {
		preini();
		add(jspu);
		add(stat);
		jlu.setCellRenderer(new AnimLCR());

		addListeners();
	}

	private void copyAnim(AnimU<?> eau) {
		Source.ResourceLocation rl = new Source.ResourceLocation(Source.ResourceLocation.LOCAL, "new anim", Source.BasePath.ANIM);
		Source.Workspace.validate(rl);
		new AnimCE(rl, eau);
	}
}
