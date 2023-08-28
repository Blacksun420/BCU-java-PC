package page.info.edit;

import common.CommonStatic;
import common.pack.Identifier;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.system.ENode;
import common.util.pack.Soul;
import common.util.stage.*;
import common.util.stage.info.CustomStageInfo;
import common.util.stage.info.StageInfo;
import common.util.unit.AbEnemy;
import common.util.unit.Enemy;
import common.util.unit.Form;
import common.util.unit.Level;
import main.Opts;
import page.*;
import page.info.EnemyInfoPage;
import page.info.StageViewPage;
import page.info.UnitInfoPage;
import page.info.filter.EnemyFindPage;
import page.info.filter.UnitFindPage;
import page.support.AnimLCR;
import page.support.UnitLCR;
import page.view.MusicPage;
import utilpc.Theme;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AdvStEditPage extends DefaultPage {

	private static final long serialVersionUID = 1L;
	private static class LineList extends JList<SCDef.Line> {
		private static final long serialVersionUID = 1L;

		public LineList() {
			setSelectionBackground(Theme.DARK.NIMBUS_SELECT_BG);

			setCellRenderer(new DefaultListCellRenderer() {
				private static final long serialVersionUID = 1L;
				@Override
				public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
					JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
					SCDef.Line li = (SCDef.Line)o;
					AbEnemy ee = Identifier.getOr(li.enemy, AbEnemy.class);
					if (ee.getIcon() != null) {
						jl.setIcon(UtilPC.getIcon(ee.getIcon()));
					}
					jl.setText(ee.toString());

					return jl;
				}
			});
		}
	}
	private final JTF sdef = new JTF();
	private final JTF smax = new JTF();
	private final JList<SCGroup> jls = new JList<>();
	private final JScrollPane jsps = new JScrollPane(jls);
	private final JList<AbEnemy> jle = new JList<>();
	private final JScrollPane jspe = new JScrollPane(jle);
	private final SCGroupEditTable sget;
	private final JScrollPane jspt;
	private final JL groups = new JL(MainLocale.UTIL, "groups");
	private final JBTN addg = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remg = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN addt = new JBTN(MainLocale.PAGE, "addl");
	private final JBTN remt = new JBTN(MainLocale.PAGE, "reml");

	private final JList<Stage> jex = new JList<>();
	private final JScrollPane jsex = new JScrollPane(jex);
	private final JL exSt = new JL(MainLocale.PAGE, "exsts");
	private final JBTN addex = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remex = new JBTN(MainLocale.PAGE, "rem");
	private final JL jlprob = new JL(MainLocale.INFO, "prob");
	private final JTF jprob = new JTF();
	private final JL jltprob = new JL(MainLocale.PAGE, "total");
	private final JTF jtprob = new JTF();
	private final JBTN equal = new JBTN(MainLocale.PAGE, "equalprob");
	private final JL jubas = new JL(MainLocale.PAGE, "ubase");
	private final JLabel lves = new JL();
	private final JTF ubaslv = new JTF();
	private final JL jurwd = new JL(MainLocale.PAGE, "urwd");
	private final JList<Form> jrwd = new JList<>();
	private final JScrollPane jsrwd = new JScrollPane(jrwd);
	private final JBTN addrw = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remrw = new JBTN(MainLocale.PAGE, "rem");

	private StageViewPage svp;
	private UnitFindPage ufp;
	private EnemyFindPage efp;
	private MusicPage musp;

	private final Stage st;
	private final SCDef data;

	private final LineList jlines = new LineList();
	private final JScrollPane jsines = new JScrollPane(jlines);
	private final JBTN revback = new JBTN(MainLocale.PAGE, "prev");
	private final JBTN revNext = new JBTN(MainLocale.PAGE, "next");
	private final JBTN remrev = new JBTN(MainLocale.PAGE, "rem");
	private final JL revEne = new JL();
	private final JL revBGM = new JL();
	private final JComboBox<Soul> revSoul = new JComboBox<>();
	private final JL revMults = new JL(MainLocale.INFO, "t2");
	private final JTF jtMults = new JTF();
	private final JBTN addrev = new JBTN(MainLocale.PAGE, "add");
	private final JBTN bossType = new JBTN(MainLocale.PAGE, "b0");
	private Revival rev;
	private boolean addEne = false, rewUni = false, summons;

	protected AdvStEditPage(Page p, Stage stage) {
		super(p);
		st = stage;
		data = st.data;
		sget = new SCGroupEditTable(data);
		jspt = new JScrollPane(sget);
		ini();
		resized(true);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(groups, x, y, 50, 100, 300, 50);
		set(jsps, x, y, 50, 150, 300, 950);
		set(addg, x, y, 50, 1100, 150, 50);
		set(remg, x, y, 200, 1100, 150, 50);
		set(smax, x, y, 50, 1150, 300, 50);

		short w = 350;
		if (summons) {
			set(jspe, x, y, w, 100, 300, 1050);
			set(sdef, x, y, w, 1150, 300, 50);
			w += 300;
			set(jspt, x, y, w, 150, 400, 1050);
			set(addt, x, y, w, 100, 200, 50);
			set(remt, x, y, w + 200, 100, 200, 50);
			w += 450;
			sget.setRowHeight(size(x, y, 50));
		} else
			w += 50;

		set(exSt, x, y, w, 100, 300, 50);
		set(jsex, x, y, w, 200, 300, 650);
		set(addex, x, y, w, 150, 150, 50);
		set(remex, x, y, w + 150, 150, 150, 50);
		set(jlprob, x, y, w, 850, 150, 50);
		set(jprob, x, y, w + 150, 850, 150, 50);
		set(jltprob, x, y, w, 900, 150, 50);
		set(jtprob, x, y, w + 150, 900, 150, 50);
		set(equal, x, y, w, 950, 300, 50);

		set(jubas, x, y, w, 1050, 300, 50);
		set(lves, x, y, w, 1100, 300, 50);
		set(ubaslv, x, y, w, 1150, 300, 50);
		w += 350;

		set(jsines, x, y, w, 100, 300, 800);
		set(addrev, x, y, w, 900, 150, 50);
		set(remrev, x, y, w + 150, 900, 150, 50);
		set(bossType, x, y, w, 950, 300, 50);
		set(revback, x, y, w, 1000, 150, 50);
		set(revNext, x, y, w + 150, 1000, 150, 50);
		set(revEne, x, y, w, 1050, 300, 50);
		set(revBGM, x, y, w, 1100, 150, 50);
		set(revSoul, x, y, w + 150, 1100, 150, 50);
		set(revMults, x, y, w, 1150, 100, 50);
		set(jtMults, x, y, w + 100, 1150, 200, 50);

		w += 350;
		set(jurwd, x, y, w, 100, 300, 50);
		set(addrw, x, y, w, 150, 150, 50);
		set(remrw, x, y, w + 150, 150, 150, 50);
		set(jsrwd, x, y, w, 200, 300, 1000);
	}

	private void addListeners$0() {
		jls.addListSelectionListener(arg0 -> {
			if (isAdj() || jls.getValueIsAdjusting())
				return;
			setSCG(jls.getSelectedValue());
		});

		addg.setLnr(e -> {
			int ind = data.sub.nextInd();
			SCGroup scg = new SCGroup(ind, st.max);
			data.sub.add(scg);
			setListG();
			setSCG(scg);
		});

		remg.setLnr(e -> {
			int ind = jls.getSelectedIndex();
			data.sub.remove(jls.getSelectedValue());
			if (ind > data.sub.size())
				ind--;
			setListG();
			jls.setSelectedIndex(ind);
		});

		smax.setLnr(e -> {
			int val = CommonStatic.parseIntN(smax.getText());
			SCGroup scg = jls.getSelectedValue();
			if (val > 0)
				scg.setMax(val, -1);
			setSCG(scg);
		});

		sdef.setLnr(e -> {
			int i = CommonStatic.parseIntN(sdef.getText());
			if (i >= 0)
				data.sdef = i;
			setListG();
		});

		jex.addListSelectionListener(e -> {
			if (isAdj() || jex.getValueIsAdjusting())
				return;
			if (st.info == null || jex.getSelectedIndex() == -1)
				jprob.setText("");
			else
				jprob.setText(((CustomStageInfo)st.info).chances.get(jex.getSelectedIndex()) + "%");
			jprob.setEnabled(!jprob.getText().equals(""));
		});

		addex.setLnr(e -> {
			if (svp == null) {
				ArrayList<MapColc> maps = new ArrayList<>();
				maps.add(st.getCont().getCont());

				UserPack pack = ((MapColc.PackMapColc)st.getCont().getCont()).pack;
				for (MapColc map : MapColc.values())
					if (map instanceof MapColc.DefMapColc || pack.desc.dependency.contains(((MapColc.PackMapColc)map).pack.desc.id))
						maps.add(map);

				svp = new StageViewPage(this, maps);
			}
			changePanel(svp);
		});

		remex.setLnr(e -> {
			int ind = jex.getSelectedIndex();
			((CustomStageInfo)st.info).remove(jex.getSelectedIndex());
			if (st.info != null) {
				jex.setListData(st.info.getExStages());
				if (ind >= st.info.getExChances().length)
					ind--;
				jex.setSelectedIndex(ind);
				setFollowups((CustomStageInfo) st.info);
			} else {
				jex.setListData(new Stage[0]);
				jex.clearSelection();
				setFollowups(null);
			}
		});

		jprob.setLnr(e -> {
			double d = CommonStatic.parseDoubleN(jprob.getText());
			if (d > 0) {
				CustomStageInfo csi = (CustomStageInfo)st.info;
				csi.chances.set(jex.getSelectedIndex(), (float) d);
				csi.checkChances();
				setFollowups(csi);
			}
		});

		jtprob.setLnr(e -> {
			int p = CommonStatic.parseIntN(jtprob.getText());
			CustomStageInfo csi = (CustomStageInfo) st.info;
			if (p > 0 && p <= 100)
				csi.setTotalChance((byte) p);

			if (jex.getSelectedIndex() != -1)
				jprob.setText(csi.chances.get(jex.getSelectedIndex()) + "%");
			jtprob.setText(csi.totalChance + "%");
		});

		jubas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getButton() == MouseEvent.BUTTON1) {
					rewUni = false;
					if (ufp == null)
						ufp = new UnitFindPage(getThis(), false, UserProfile.getUserPack(st.id.pack.substring(0, st.id.pack.indexOf('/'))));
					changePanel(ufp);
				} else if (st.info != null && ((CustomStageInfo)st.info).ubase != null)
					changePanel(new UnitInfoPage(getThis(), ((CustomStageInfo)st.info).ubase.unit, ((CustomStageInfo)st.info).lv));
			}
		});

		jrwd.addListSelectionListener(r -> remrw.setEnabled(st.info != null && jrwd.getSelectedIndex() != -1));

		addrw.addActionListener(r -> {
			rewUni = true;
			if (ufp == null)
				ufp = new UnitFindPage(getThis(), false, UserProfile.getUserPack(st.id.pack.substring(0, st.id.pack.indexOf('/'))));
			changePanel(ufp);
		});

		remrw.addActionListener(r -> {
			CustomStageInfo csi = (CustomStageInfo)st.info;
			for (Form f : jrwd.getSelectedValuesList())
				csi.rewards.remove(f);
			setRwd(csi);
		});

		ubaslv.addActionListener(l -> {
			if (st.info == null)
				return;
			CustomStageInfo csi = (CustomStageInfo)st.info;
			if (csi.ubase == null)
				return;
			csi.lv.setLvs(Level.lvList(csi.ubase.unit(), CommonStatic.parseIntsN(ubaslv.getText()), null));
			ubaslv.setText(UtilPC.lvText(csi.ubase, csi.lv)[0]);
		});

		equal.setLnr(e -> {
			((CustomStageInfo)st.info).equalizeChances();
			if (jex.getSelectedIndex() != -1)
				jprob.setText(((CustomStageInfo)st.info).chances.get(jex.getSelectedIndex()) + "%");
		});
	}

	private void addListeners$1() {
		addt.setLnr(e -> sget.addLine(jle.getSelectedValue()));

		remt.setLnr(e -> sget.remLine());

		jlines.addListSelectionListener(l -> {
			SCDef.Line li = jlines.getSelectedValue();
			if (li == null)
				setRevival(null);
			else
				setRevival(li.rev);
		});

		addrev.addActionListener(l -> {
			if (efp == null)
				efp = new EnemyFindPage(getThis(), true, UserProfile.getUserPack(st.id.pack.substring(0, st.id.pack.indexOf('/'))));
			changePanel(efp);
		});

		remrev.addActionListener(l -> {
			if (rev.par == null) {
				jlines.getSelectedValue().rev = null;
				setRevival(null);
			} else {
				rev.par.rev = null;
				setRevival(rev.par);
			}
		});

		bossType.addActionListener(l -> {
			rev.boss = (byte)((rev.boss + 1) % 3);
			bossType.setText(get(MainLocale.INFO, "b" + rev.boss));
		});

		revback.addActionListener(l -> setRevival(rev.par));
		revNext.addActionListener(l -> setRevival(rev.rev));

		revEne.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (rev == null && jlines.getSelectedValue() == null)
					return;

				if (e.getButton() == MouseEvent.BUTTON1) {
					addEne = true;
					if (efp == null)
						efp = new EnemyFindPage(getThis(), true, UserProfile.getUserPack(st.id.pack.substring(0, st.id.pack.indexOf('/'))));
					changePanel(efp);
				} else if (rev.enemy != null && rev.enemy.get() instanceof Enemy)
					changePanel(new EnemyInfoPage(getThis(), new ENode((Enemy)rev.enemy.get(), new int[]{rev.mhp, rev.matk})));
			}
		});

		revBGM.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (rev == null)
					return;

				if (musp == null) {
					if (rev.bgm == null)
						musp = new MusicPage(getThis(), st.id.pack.substring(0, st.id.pack.indexOf('/')));
					else
						musp = new MusicPage(getThis(), rev.bgm);
				}
				changePanel(musp);
			}
		});

		revSoul.addActionListener(l -> {
			if (rev == null)
				return;
			rev.soul = revSoul.getSelectedItem() != null ? ((Soul)revSoul.getSelectedItem()).getID() : null;
		});

		jtMults.addActionListener(l -> {
			int[] mags = CommonStatic.parseIntsN(jtMults.getText());
			if (mags.length >= 2) {
				rev.mhp = mags[0];
				rev.matk = mags[1];
			} else if (mags.length == 1)
				rev.mhp = rev.matk = mags[0];
			jtMults.setText(rev.mhp + "% / " + rev.matk + "%");
		});
	}

	private void ini() {
		AbEnemy[] aes = data.getSummon().toArray(new AbEnemy[0]);
		summons = aes.length > 0;
		add(groups);
		add(jsps);
		add(addg);
		add(remg);
		add(smax);

		if (summons) {
			add(sdef);
			add(jspe);
			add(jspt);
			add(addt);
			add(remt);
		}

		add(jsex);
		add(exSt);
		add(addex);
		add(remex);
		add(jlprob);
		add(jprob);
		add(jltprob);
		add(jtprob);
		add(equal);
		add(jubas);
		add(jurwd);
		add(addrw);
		add(remrw);
		add(jsrwd);
		jrwd.setCellRenderer(new UnitLCR());
		add(lves);
		add(ubaslv);
		jle.setCellRenderer(new AnimLCR());
		jle.setListData(aes);
		sdef.setText("default: " + data.sdef);

		add(jsines);
		add(revback);
		add(addrev);
		add(remrev);
		add(revback);
		add(revNext);
		add(revEne);
		add(revBGM);
		List<Soul> souls = UserProfile.getAll(st.id.pack.substring(0, st.id.pack.indexOf('/')), Soul.class);
		souls.add(0, null);
		revSoul.setModel(new DefaultComboBoxModel<>(souls.toArray(new Soul[0])));
		add(revSoul);
		add(bossType);
		add(revMults);
		add(jtMults);
		jlines.setListData(data.getSimple());

		if (st.info != null) {
			jex.setListData(st.info.getExStages());
			setFollowups((CustomStageInfo)st.info);
		} else
			setFollowups(null);
		setListG();
		addListeners$0();
		addListeners$1();
		setUBase(st.info);
		setRwd(st.info);
		setRevival(null);
	}

	private void setListG() {
		SCGroup scg = jls.getSelectedValue();
		List<SCGroup> l = data.sub.getList();
		change(0, n -> {
			jls.clearSelection();
			jls.setListData(l.toArray(new SCGroup[0]));
		});
		sdef.setText("default: " + data.sdef);
		if (scg != null && !l.contains(scg))
			scg = null;
		setSCG(scg);
	}

	private void setSCG(SCGroup scg) {
		if (jls.getSelectedValue() != scg)
			change(scg, s -> jls.setSelectedValue(s, true));
		remg.setEnabled(scg != null);
		smax.setEnabled(scg != null);
		smax.setText(scg != null ? "max: " + scg.getMax(0) : "");
	}

	private void setFollowups(CustomStageInfo si) {
		remex.setEnabled(si != null);
		equal.setEnabled(si != null);
		jex.setEnabled(si != null);
		if (si == null)
			jex.clearSelection();
		jprob.setEnabled(jex.getSelectedIndex() != -1);
		jprob.setText(jex.getSelectedIndex() != -1 ? si.chances.get(jex.getSelectedIndex()) + "%" : "");

		jtprob.setEnabled(si != null);
		jtprob.setText(si != null ? si.totalChance + "%" : "");
	}

	public void setUBase(StageInfo si) {
		if (si == null || ((CustomStageInfo)si).ubase == null) {
			ubaslv.setEnabled(false);
			ubaslv.setText("");
			lves.setText("");
			jubas.setIcon(null);
			jubas.setText(get(MainLocale.PAGE, "ubase"));
			return;
		}
		CustomStageInfo csi = (CustomStageInfo)si;
		ubaslv.setEnabled(true);
		String[] lvss = UtilPC.lvText(csi.ubase, csi.lv);
		lves.setText(lvss[1]);
		ubaslv.setText(lvss[0]);
		if (csi.ubase.getIcon() != null)
			jubas.setIcon(new ImageIcon((BufferedImage) csi.ubase.getIcon().getImg().bimg()));
		jubas.setText(csi.ubase.toString());
	}

	public void setRwd(StageInfo si) {
		int sel = Math.min(jrwd.getSelectedIndex(),jrwd.getModel().getSize() - 1);
		remrw.setEnabled(si != null && sel != -1);
		if (si == null)
			jrwd.setListData(new Form[0]);
		else
			jrwd.setListData(((CustomStageInfo)si).rewards.toArray(new Form[0]));
		jrwd.setSelectedIndex(sel);
	}

	public void setRevival(Revival r) {
		rev = r;
		revback.setEnabled(r != null && r.par != null);
		revNext.setEnabled(r != null && r.rev != null);
		bossType.setEnabled(r != null);
		addrev.setEnabled(r != null || jlines.getSelectedValue() != null);
		remrev.setEnabled(r != null);
		revEne.setEnabled(r != null);
		revBGM.setEnabled(r != null);
		revSoul.setEnabled(r != null);
		jtMults.setEnabled(r != null);

		if (r != null) {
			AbEnemy ene = r.enemy.get();
			revEne.setIcon(UtilPC.getIcon(ene.getIcon()));
			revEne.setText(ene.toString());

			revBGM.setText("BGM: " + r.bgm);
			revSoul.setSelectedItem(r.soul);

			jtMults.setText(r.mhp + "% / " + r.matk + "%");
			bossType.setText(get(MainLocale.INFO, "b" + rev.boss));
		} else {
			revEne.setIcon(null);
			revEne.setText("N/A");

			revBGM.setText("N/A");
			revSoul.setSelectedIndex(0);

			jtMults.setText("N/A");
			bossType.setText("N/A");
		}
	}

	@Override
	public void renew() {
		if (svp != null && svp.getSelectedStages().size() > 0) {
			if (st.info == null)
				st.info = new CustomStageInfo(st);
			CustomStageInfo csi = (CustomStageInfo)st.info;
			List<Stage> stages = svp.getSelectedStages();
			for (int i = 0; i < stages.size(); i++)
				if (csi.stages.contains(stages.get(i))) {
					Opts.pop("Already added EX stage", stages.get(i).toString() + " already exists in the EX stages list");
					stages.remove(i);
					i--;
				} else {
					csi.stages.add(stages.get(i));
					csi.chances.add(100f / csi.stages.size());
				}
			csi.checkChances();
			jex.setListData(st.info.getExStages());
			setFollowups(csi);
			svp = null;
		}
		if (musp != null) {
			rev.bgm = musp.getSelectedID();
			setRevival(rev);
			musp = null;
		}
		if (efp != null && efp.getSelected() != null) {
			if (addEne && rev != null) {
				AbEnemy ene = efp.getSelected();
				rev.enemy = ene.getID();
				revEne.setIcon(UtilPC.getIcon(ene.getIcon()));
				revEne.setText(ene.toString());
				addEne = false;
			} else if (rev == null) {
				SCDef.Line li = jlines.getSelectedValue();
				li.rev = new Revival(efp.getSelected().getID());
				setRevival(li.rev);
			} else {
				rev.rev = new Revival(rev, efp.getSelected().getID());
				setRevival(rev.rev);
			}
			efp = null;
		}
		if (ufp != null) {
			if (st.info == null)
				if (ufp.getForm() == null) {
					ufp = null;
					return;
				} else
					st.info = new CustomStageInfo(st);
			CustomStageInfo csi = (CustomStageInfo)st.info;

			if (rewUni) {
				Form fr = (Form)ufp.getForm();
				csi.rewards.removeIf(f -> f.unit.equals(fr.unit));
				csi.rewards.add(fr);
				csi.destroy(true);
				setRwd(csi);
			} else {
				if (csi.ubase != ufp.getForm() && (csi.ubase == null || Opts.conf("Replace base " + csi.ubase + " with " + ufp.getForm() + "?"))) {
					csi.ubase = (Form) ufp.getForm();
					if (csi.ubase != null)
						csi.lv = csi.ubase.unit().getPrefLvs();
					else {
						csi.lv = null;
						csi.destroy(true);
					}
					setUBase(csi);
				}
				ufp = null;
			}
		}
	}

}
