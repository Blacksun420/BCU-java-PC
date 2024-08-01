package page.view;

import common.pack.PackData;
import common.pack.Source;
import common.pack.UserProfile;
import common.system.Node;
import common.util.anim.AnimCE;
import common.util.anim.AnimD;
import common.util.anim.EAnimI;
import common.util.anim.EAnimU;
import common.util.unit.Form;
import common.util.unit.Unit;
import main.Opts;
import page.JBTN;
import page.Page;
import page.anim.ImgCutEditPage;
import page.info.UnitInfoPage;
import page.support.UnitLCR;

import javax.swing.*;
import java.util.Vector;

public class UnitViewPage extends AbViewPage {

	private static final long serialVersionUID = 2010L;

	private final JList<Unit> jlu = new JList<>();
	private final JScrollPane jspu = new JScrollPane(jlu);
	private final JList<Form> jlf = new JList<>();
	private final JScrollPane jspf = new JScrollPane(jlf);
	private final JBTN stat = new JBTN(0, "stat");

	private final String pac;

	public UnitViewPage(Page p, String pack) {
		super(p);
		pac = pack;

		PackData pac = UserProfile.getPack(pack);

		if(pac != null)
			jlu.setListData(new Vector<>(pac.units.getList()));

		ini();
	}

	public UnitViewPage(Page p) {
		super(p);
		pac = null;
		Vector<Unit> v = new Vector<>();

		for(PackData pack : UserProfile.getAllPacks()) {
			v.addAll(pack.units.getList());
		}

		jlu.setListData(v);

		ini();
	}

	public UnitViewPage(Page p, Unit u) {
		this(p, u.getID().pack);
		jlu.setSelectedValue(u, true);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		if (!larges.isSelected()) {
			set(jspu, x, y, 50, 100, 300, 1100);
			set(jspf, x, y, 400, 100, 300, 400);
			set(stat, x, y, 400, 1000, 300, 50);
		} else {
			set(jspf, x, y, 100, 800, 300, 400);
			set(stat, x, y, 100, 1200, 300, 50);
			set(jspu, x, y, 0, 0, 0, 0);
		}
	}

	@Override
	protected void updateChoice() {
		Form f = jlf.getSelectedValue();
		if (f == null)
			return;
		setAnim(f.anim);
		((EAnimU)vb.getEnt()).setDir(f.rev);
	}

	private void addListeners() {

		jlu.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			Unit u = jlu.getSelectedValue();
			if (u == null)
				return;
			int ind = jlf.getSelectedIndex();
			if (ind == -1)
				ind = 0;
			jlf.setListData(u.forms);
			jlf.setSelectedIndex(ind < u.forms.length ? ind : 0);
		});

		jlf.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			updateChoice();
		});

		stat.addActionListener(e -> {
			Unit u = jlu.getSelectedValue();
			if (u == null)
				return;
			Node<Unit> n;

			if(pac == null) {
				n = Node.getList(UserProfile.getAll(u.id.pack, Unit.class), u);
			} else {
				n = Node.getList(UserProfile.getAll(pac, Unit.class), u);
			}

			changePanel(new UnitInfoPage(getThis(), n));
		});

		copy.addActionListener(e -> {
			Unit uni = jlu.getSelectedValue();
			if(uni != null) {
				PackData pack = uni.getCont();

				if(pack != null)
					if(pack instanceof PackData.DefPack)
						copyAnim();
					else if(pack instanceof PackData.UserPack) {
						if(((PackData.UserPack) pack).editable || ((PackData.UserPack) pack).desc.allowAnim)
							copyAnim();
						else {
							String pass = Opts.read("Enter the password : ");
							if(pass == null)
								return;

							if(((Source.ZipSource) ((PackData.UserPack) pack).source).zip.matchKey(pass)) {
								copyAnim();
							} else
								Opts.pop("You typed incorrect password", "Incorrect password");
						}
					}
			}
		});
	}

	private void ini() {
		preini();
		add(jspu);
		add(jspf);
		add(stat);
		jlu.setCellRenderer(new UnitLCR());
		jlf.setCellRenderer(new UnitLCR());
		addListeners();

	}

	private void copyAnim() {
		EAnimI ei = vb.getEnt();
		if (ei == null || !(ei.anim() instanceof AnimD))
			return;
		AnimD<?, ?> eau = (AnimD<?, ?>) ei.anim();
		Source.ResourceLocation rl = new Source.ResourceLocation(Source.ResourceLocation.LOCAL, eau.toString(), Source.BasePath.ANIM);
		Source.Workspace.validate(rl);
		new AnimCE(rl, eau);
		changePanel(new ImgCutEditPage(getThis()));
	}
}