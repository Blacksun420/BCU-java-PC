package page.pack;

import common.CommonStatic;
import common.battle.BasisSet;
import common.battle.data.AtkDataModel;
import common.battle.data.CustomEnemy;
import common.io.PackLoader;
import common.pack.Context;
import common.pack.Context.ErrType;
import common.pack.PackData.UserPack;
import common.pack.SortedPackSet;
import common.pack.Source;
import common.pack.Source.Workspace;
import common.pack.Source.ZipSource;
import common.pack.UserProfile;
import common.util.AnimGroup;
import common.util.Data;
import common.util.anim.AnimCE;
import common.util.anim.AnimCI;
import common.util.anim.AnimU;
import common.util.stage.MapColc;
import common.util.stage.Stage;
import common.util.stage.StageMap;
import common.util.stage.info.CustomStageInfo;
import common.util.unit.EneRand;
import common.util.unit.Enemy;
import common.util.unit.Trait;
import common.util.unit.UnitLevel;
import common.util.unit.rand.EREnt;
import main.MainBCU;
import main.Opts;
import page.*;
import page.anim.AnimGroupTree;
import page.info.StageViewPage;
import page.info.edit.EnemyEditPage;
import page.info.edit.StageEditPage;
import page.info.filter.EnemyFindPage;
import page.support.AnimLCR;
import page.support.AnimTreeRenderer;
import page.support.RLFIM;
import page.support.TreeNodeExpander;
import page.view.BGViewPage;
import page.view.CastleViewPage;
import page.view.EnemyViewPage;
import page.view.MusicPage;
import utilpc.Theme;
import utilpc.UtilPC;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

public class PackEditPage extends Page {

	private static final long serialVersionUID = 1L;

	static class PackList extends JList<UserPack> {

		private static final long serialVersionUID = 1L;

		protected PackList(Vector<UserPack> packs) {
			super(packs);
			setSelectionBackground(Theme.DARK.NIMBUS_SELECT_BG);
			setCellRenderer(new DefaultListCellRenderer() {

				private static final long serialVersionUID = 1L;

				@Override
				public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
					JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
					jl.setIcon(UtilPC.resizeIcon(((UserPack)o).icon, 40, 40));

					return jl;
				}
			});
		}
	}

	private final JBTN back = new JBTN(0, "back");
	private final Vector<UserPack> vpack = new Vector<>(UserProfile.getUserPacks());
	private final PackList jlp = new PackList(vpack);
	private final JScrollPane jspp = new JScrollPane(jlp);
	private final JList<Enemy> jle = new JList<>();
	private final JScrollPane jspe = new JScrollPane(jle);
	private final JTree jtd = new JTree();
	private final AnimGroupTree agt;
	private final JScrollPane jspd = new JScrollPane(jtd);
	private final RLFIM<StageMap> jls = new RLFIM<>(() -> this.changing = true, () -> changing = false, this::finishRemoving, this::setMap, StageMap::new);
	private final JScrollPane jsps = new JScrollPane(jls);
	private final JList<UserPack> jlr = new JList<>();
	private final JScrollPane jspr = new JScrollPane(jlr);
	private final JList<UserPack> jlt = new JList<>(vpack);
	private final JScrollPane jspt = new JScrollPane(jlt);

	private final JBTN addp = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remp = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN adde = new JBTN(MainLocale.PAGE, "add");
	private final JBTN reme = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN erea = new JBTN(MainLocale.PAGE, "reassign");
	private final JBTN adds = new JBTN(MainLocale.PAGE, "add");
	private final JBTN rems = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN addr = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remr = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN edit = new JBTN(MainLocale.PAGE, "edit");
	private final JBTN sdiy = new JBTN(MainLocale.PAGE, "sdiy");
	private final JBTN vene = new JBTN(MainLocale.PAGE, "vene");
	private final JBTN extr = new JBTN(MainLocale.PAGE, "extr");
	private final JBTN vcas = new JBTN(MainLocale.PAGE, "vcas");
	private final JBTN vbgr = new JBTN(MainLocale.PAGE, "vbgr");
	private final JBTN vrcg = new JBTN(MainLocale.PAGE, "recg");
	private final JBTN vrlr = new JBTN(MainLocale.PAGE, "relr");
	private final JBTN cunt = new JBTN(MainLocale.PAGE, "cunt");
	private final JBTN tdiy = new JBTN(MainLocale.PAGE, "ctrt");
	private final JBTN ener = new JBTN(MainLocale.PAGE, "ener");
	private final JBTN vmsc = new JBTN(MainLocale.PAGE, "vmsc");
	private final JBTN unpk = new JBTN(MainLocale.PAGE, "unpack");
	private final JBTN recd = new JBTN(MainLocale.PAGE, "replay");
	private final JBTN csol = new JBTN(MainLocale.PAGE, "csoul");
	private final JBTN cbge = new JBTN(MainLocale.PAGE, "cbge");
	private final JTG cmbo = new JTG(MainLocale.PAGE, "usecombo");
	private final JBTN cdesc = new JBTN(MainLocale.PAGE, "pinfo");

	private final JTF jtfp = new JTF();
	private final JTF jtfe = new JTF();
	private final JTF jtfs = new JTF();

	private final JComboBox<String> lbp = new JComboBox<>();
	private final JL lbe = new JL(0, "enemy");
	private final JL lbd = new JL(0, "seleanim");
	private final JL lbs = new JL(0, "stage");
	private final JL lbr = new JL(0, "parent");
	private final JL lbt = new JL(0, "selepar");
	private final JLabel pid = new JLabel();
	private final JLabel pauth = new JLabel();

	private UserPack pac;
	private Enemy ene;
	private EnemyFindPage efp;
	private StageMap sm;
	private boolean changing = false, unsorted = true;

	public PackEditPage(Page p) {
		super(p);
		AnimGroup.workspaceGroup.renewGroup();
		agt = new AnimGroupTree(jtd, Source.BasePath.ANIM);
		agt.renewNodes();

		ini();
	}

	@Override
    public JButton getBackButton() {
		return back;
	}

	@Override
	protected void renew() {
		if (efp != null && efp.getSelected() != null)
			addCE(((Enemy)efp.getSelected()).anim);
		efp = null;

		setPack(pac);
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		int w = 50, dw = 150;
		set(lbp, x, y, w, 100, 400, 50);
		set(jspp, x, y, w, 150, 400, 600);
		set(addp, x, y, w, 800, 200, 50);
		set(remp, x, y, w + 200, 800, 200, 50);
		set(jtfp, x, y, w, 850, 400, 50);
		set(extr, x, y, w, 950, 200, 50);
		set(unpk, x, y, w + 200, 950, 200, 50);
		set(cmbo, x, y, w, 1000, 400, 50);
		set(cdesc, x, y, w, 1050, 400, 50);
		set(pid, x, y, w, 1100, 400, 50);
		set(pauth, x, y, w, 1150, 400, 50);
		w += 450;
		set(lbe, x, y, w, 100, 300, 50);
		set(jspe, x, y, w, 150, 300, 600);
		set(erea, x, y, w, 750, 300, 50);
		set(adde, x, y, w, 800, 150, 50);
		set(reme, x, y, w + dw, 800, 150, 50);
		set(jtfe, x, y, w, 850, 300, 50);
		set(edit, x, y, w, 950, 300, 50);
		set(vene, x, y, w, 1050, 300, 50);
		set(ener, x, y, w, 1150, 300, 50);
		w += 300;
		set(lbd, x, y, w, 100, 300, 50);
		set(jspd, x, y, w, 150, 300, 600);
		w += 50;
		set(vbgr, x, y, w, 850, 250, 50);
		set(vcas, x, y, w, 950, 250, 50);
		set(vrcg, x, y, w, 1050, 250, 50);
		set(vrlr, x, y, w, 1150, 250, 50);
		w += 300;
		set(lbs, x, y, w, 100, 300, 50);
		set(jsps, x, y, w, 150, 300, 600);
		set(adds, x, y, w, 800, 150, 50);
		set(rems, x, y, w + dw, 800, 150, 50);
		set(jtfs, x, y, w, 850, 300, 50);
		set(sdiy, x, y, w, 950, 300, 50);
		set(cunt, x, y, w, 1050, 300, 50);
		set(vmsc, x, y, w, 1150, 300, 50);
		w += 350;
		set(lbr, x, y, w, 100, 350, 50);
		set(jspr, x, y, w, 150, 350, 600);
		set(addr, x, y, w, 800, 175, 50);
		set(remr, x, y, w + 175, 800, 175, 50);

		set(recd, x, y, w, 950, 300, 50);
		set(tdiy, x, y, w, 1050, 300, 50);
		set(csol, x, y, w, 1150, 300, 50);
		w += 350;
		set(cbge, x, y, w, 1150, 350, 50);
		set(lbt, x, y, w, 100, 350, 50);
		set(jspt, x, y, w, 150, 350, 600);
		SwingUtilities.invokeLater(() -> jtd.setUI(new TreeNodeExpander(jtd)));
	}

	private void addListeners() {

		back.setLnr(x -> changePanel(getFront()));

		recd.setLnr(x -> changePanel(new RecdPackPage(this, pac)));

		csol.setLnr(x -> changePanel(new SoulEditPage(this, pac)));

		cbge.setLnr(x -> changePanel(new BGEffectEditPage(this, pac)));

		vcas.addActionListener(arg0 -> {
			if (pac != null && pac.editable)
				changePanel(new CastleEditPage(getThis(), pac));
			else if (pac != null)
				changePanel(new CastleViewPage(getThis(), pac.castles));
		});

		vbgr.addActionListener(arg0 -> {
			if (pac != null && pac.editable)
				changePanel(new BGEditPage(getThis(), pac));
			else if (pac != null)
				changePanel(new BGViewPage(getThis(), pac.getSID()));
		});

		vrcg.addActionListener(arg0 -> {
			if (pac != null && pac.editable)
				changePanel(new CGLREditPage(getThis(), pac));
			else
				changePanel(new CharaGroupPage(getThis(), pac, true));
		});

		vrlr.addActionListener(arg0 -> {
			if (pac != null && pac.editable)
				changePanel(new CGLREditPage(getThis(), pac));
			else
				changePanel(new LvRestrictPage(getThis(), pac, true));
		});

		jtd.addTreeSelectionListener(arg0 -> {
			if (changing)
				return;
			changing = true;
			adde.setEnabled(pac != null && pac.editable);
			erea.setEnabled(adde.isEnabled() && jle.getSelectedValue() != null);
			changing = false;
		});
	}

	private void addListeners$1() {

		addp.addActionListener(arg0 -> {
			changing = true;
			String str = Workspace.validateWorkspace(Workspace.generatePackID());
			pac = Data.err(() -> UserProfile.initJsonPack(str));
			pac.desc.author = MainBCU.author;
			vpack.add(pac);
			jlp.setListData(vpack);
			jlt.setListData(vpack);
			jlp.setSelectedValue(pac, true);
			setPack(pac);
			changing = false;
		});

		remp.addActionListener(arg0 -> {
			if (!Opts.conf())
				return;
			changing = true;
			int ind = jlp.getSelectedIndex();
			UserProfile.unloadPack(pac);
			pac.delete();
			vpack.remove(pac);
			jlp.setListData(vpack);
			jlt.setListData(vpack);
			if (ind > 0)
				ind--;
			jlp.setSelectedIndex(ind);
			setPack(jlp.getSelectedValue());
			changing = false;
		});

		jlp.addListSelectionListener(arg0 -> {
			if (changing || jlp.getValueIsAdjusting())
				return;
			changing = true;
			setPack(jlp.getSelectedValue());
			changing = false;
		});

		jtfp.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				String str = jtfp.getText().trim();
				if (pac.desc.names.toString().equals(str))
					return;
				pac.desc.names.put(str);
				jtfp.setText(pac.desc.names.toString());
			}

		});

		extr.setLnr(x -> {
			if (pac.editable) {
				Object[] result = Opts.showTextCheck("Password", "Decide the password of pack : ", pac.desc.allowAnim);

				if (result == null)
					return;

				String password = (String) result[0];
				String parentPassword = (String) result[2];

				pac.desc.allowAnim = (boolean) result[1];

				CommonStatic.ctx.noticeErr(() -> ((Workspace) pac.source).export(pac, password, parentPassword, (d) -> {
				}), ErrType.WARN, "failed to export pack");
			}
		});

		unpk.setLnr(x -> {
			String pass = Opts.read("Enter the password : ");

			if(pass == null)
				return;

			if(((Source.ZipSource) pac.source).zip.matchKey(pass)) {
				Data.err(() -> ((ZipSource) pac.source).unzip(pass, (d) -> {
				}));
				unpk.setEnabled(false);
				extr.setEnabled(true);
			} else {
				Opts.pop("You typed incorrect password", "Incorrect password");
			}
		});

		cmbo.setLnr(x -> {
			pac.useCombos = cmbo.isSelected();
			for (BasisSet b : BasisSet.list()) {
				for (int i = 0; i < b.lb.size(); i++) {
					b.lb.get(i).lu.renew();
				}
			}
		});

	}

	private void addListeners$2() {

		jle.addListSelectionListener(e -> {
			if (changing || jle.getValueIsAdjusting())
				return;
			changing = true;
			setEnemy(jle.getSelectedValue());
			changing = false;
		});

		adde.addActionListener(arg0 -> {
			AnimCI anim = getSelectedAnim();
			ene = null;
			if (anim == null)
				changePanel(efp = new EnemyFindPage(this, false, pac));
			else
				addCE(anim);
		});

		reme.addActionListener(arg0 -> {
			if (!Opts.conf())
				return;
			changing = true;
			int ind = jle.getSelectedIndex();
			pac.enemies.remove(ene);
			jle.setListData(pac.enemies.toRawArray());
			if (ind >= 0)
				ind--;
			jle.setSelectedIndex(ind);
			setEnemy(jle.getSelectedValue());
			changing = false;
		});

		edit.setLnr(a -> {
			UserPack pack = UserProfile.getUserPack(ene.id.pack);

			if(pack == null)
				return;

			changePanel(new EnemyEditPage(getThis(), ene, pack));
		});

		erea.setLnr(a -> {
			if(jle.getSelectedValue() == null)
				return;

			AnimCI anim = getSelectedAnim();
			if (anim == null)
				changePanel(efp = new EnemyFindPage(this, false, pac));
			else
				addCE(anim);
		});

		jtfe.setLnr(e -> {
			ene.names.put(jtfe.getText().trim());
			jtfe.setText(ene.names.toString());
		});

		vene.setLnr(() -> new EnemyViewPage(getThis(), pac.getSID()));

		ener.setLnr(() -> new EREditPage(getThis(), pac));

		cdesc.setLnr(g -> Opts.showPackDescPage(getThis(), pac));
	}

	private void addListeners$3() {

		sdiy.addActionListener(arg0 -> {
			if (pac.editable)
				changePanel(new StageEditPage(getThis(), pac.mc, pac));
			else {
				List<MapColc> lmc = Arrays.asList(new MapColc[] { pac.mc });
				changePanel(new StageViewPage(getThis(), lmc));
			}
		});

		cunt.addActionListener(arg0 -> changePanel(new UnitManagePage(getThis(), pac)));

		tdiy.addActionListener(arg0 -> changePanel(new TraitEditPage(getThis(), pac)));

		vmsc.setLnr(() -> pac.editable ? new MusicEditPage(getThis(), pac)
				: new MusicPage(getThis(), pac.musics.getList()));

		jls.addListSelectionListener(arg0 -> {
			if (changing || jls.getValueIsAdjusting())
				return;
			changing = true;
			setMap(jls.getSelectedValue());
			changing = false;
		});

		adds.setLnr(jls::addItem);

		rems.setLnr(jls::deleteItem);

		jtfs.setLnr(x -> {
			if (sm != null) {
				sm.names.put(jtfs.getText().trim());
				jtfs.setText(sm.names.toString());
			}
		});

	}

	private void addListeners$4() {

		jlr.addListSelectionListener(arg0 -> {
			if (changing || arg0.getValueIsAdjusting())
				return;
			changing = true;
			setRely(jlr.getSelectedValue());
			changing = false;
		});

		jlt.addListSelectionListener(arg0 -> {
			if (changing || arg0.getValueIsAdjusting())
				return;
			checkAddr();
		});

		addr.addActionListener(arg0 -> {
			changing = true;
			UserPack rel = jlt.getSelectedValue();

			//Need to cache parented pack for situation when parent pack of parent pack has password
			ArrayList<String> passedParents = new ArrayList<>();

			if(rel.desc.parentPassword != null) {
				String pass = Opts.read("Enter the password for "+rel.getSID()+" : ");

				if(pass == null) {
					changing = false;
					return;
				}

				byte[] md5 = PackLoader.getMD5(pass.getBytes(StandardCharsets.UTF_8), 16);

				if(!Arrays.equals(rel.desc.parentPassword, md5)) {
					Opts.pop("You typed incorrect password", "Incorrect password");
					changing = false;
					return;
				}
			}

			pac.desc.dependency.add(rel.getSID());

			passedParents.add(rel.getSID());

			for (String id : rel.desc.dependency) {
				if (!pac.desc.dependency.contains(id)) {
					UserPack pack = UserProfile.getUserPack(id);

					if(pack == null) {
						Opts.pop("Can't get parent pack ["+id+".pack.bcuzip] from data, aborting parent pack adding", "Can't find parent pack");
						passedParents.forEach(pac.desc.dependency::remove);
						changing = false;
						return;
					}

					if(pack.editable) {
						pac.desc.dependency.add(id);
						passedParents.add(id);
					} else if(pack.desc.parentPassword != null) {
						String pass = Opts.read("Enter the password for "+id+" : ");

						if(pass == null) {
							changing = false;
							passedParents.forEach(pac.desc.dependency::remove);
							return;
						}

						byte[] md5 = PackLoader.getMD5(pass.getBytes(StandardCharsets.UTF_8), 16);

						if(!Arrays.equals(pack.desc.parentPassword, md5)) {
							Opts.pop("You typed incorrect password", "Incorrect password");
							passedParents.forEach(pac.desc.dependency::remove);
							changing = false;
							return;
						}

						pac.desc.dependency.add(id);
						passedParents.add(id);
					} else {
						pac.desc.dependency.add(id);
						passedParents.add(id);
					}
				}
			}
			updateJlr();
			checkAddr();
			jlr.setSelectedValue(rel, true);
			setRely(rel);
			changing = false;
		});

		remr.addActionListener(arg0 -> {
			changing = true;
			int ind = jlr.getSelectedIndex() - 1;
			UserPack rel = jlr.getSelectedValue();
			if (pac.relyOn(rel.getSID())) {
				StringBuilder sb = new StringBuilder();
				List<String> list = pac.foreignList(rel.getSID());
				for (String str : list)
					sb.append(str).append("\n");
				Opts.pop(sb.toString(), "list of dependency");
			} else {
				pac.desc.dependency.remove(rel.getSID());
				updateJlr();
				jlr.setSelectedIndex(ind);
				setRely(jlr.getSelectedValue());
			}
			changing = false;
		});

		lbp.addActionListener(j -> {
			int method = lbp.getSelectedIndex();
			switch (method) {
				case 0:
					if (unsorted)
						return;
					Vector<UserPack> vpack2 = new Vector<>(UserProfile.getUserPacks());
					vpack.clear();
					vpack.addAll(vpack2); //Dunno a more efficient way to unsort a list
					break;
				case 1:
					vpack.sort(null);
					break;
				case 2:
					vpack.sort(Comparator.comparing(UserPack::getSID));
					break;
				case 3:
					vpack.sort(Comparator.comparing(p -> p.desc.getAuthor()));
					break;
				case 4:
					vpack.sort(Comparator.comparing(p -> p.desc.BCU_VERSION));
					vpack.sort(Comparator.comparingInt(p -> p.desc.FORK_VERSION));
					break;
				case 5:
					vpack.sort(Comparator.comparingLong(p -> p.desc.getTimestamp("cdate")));
					break;
				case 6:
					vpack.sort(Comparator.comparingLong(p -> p.desc.getTimestamp("edate")));
					break;
				case 7:
					vpack.sort(Comparator.comparingInt(p -> p.enemies.size()));
					break;
				case 8:
					vpack.sort(Comparator.comparingInt(p -> p.units.size()));
					break;
				case 9:
					vpack.sort(Comparator.comparingInt(p -> p.mc.getStageCount()));
					break;
			}
			jlp.setListData(vpack);
			unsorted = method == 0;
			jlp.setSelectedValue(pac, true);
		});
	}

	private void checkAddr() {
		if (pac == null) {
			addr.setEnabled(false);
			return;
		}
		UserPack rel = jlt.getSelectedValue();
		boolean b = pac.editable;
		b &= rel != null && !pac.desc.dependency.contains(rel.getSID());
		b &= rel != pac;
		if (b)
			for (String id : rel.desc.dependency)
				if (id.equals(pac.getSID()))
					b = false;
		addr.setEnabled(b);
	}

	private void addCE(AnimU<? extends AnimU.ImageKeeper> anim) {
		if (anim == null)
			return;

		changing = true;
		if (ene == null) {
			CustomEnemy ce = new CustomEnemy(anim);
			ce.limit = CommonStatic.customEnemyMinPos(anim.mamodel);
			Enemy e = new Enemy(pac.getNextID(Enemy.class), anim, ce);
			pac.enemies.add(e);
			jle.setListData(pac.enemies.toRawArray());
			jle.setSelectedValue(e, true);
			setEnemy(e);
		} else if (ene.anim == null || (ene.anim != anim && Opts.conf(get(MainLocale.PAGE, "reasanim")))) {
			CustomEnemy ce = (CustomEnemy)ene.de;
			ce.share = Arrays.copyOf(ce.share, anim.anim.getAtkCount());
			if (ce.hits.size() < ce.share.length)
				for (int i = ce.hits.size(); i < ce.share.length; i++) {
					ce.hits.add(new AtkDataModel[1]);
					ce.hits.get(i)[0] = new AtkDataModel(ce);
					ce.share[i] = 1;
				}
			while (ce.hits.size() > ce.share.length)
				ce.hits.remove(ce.hits.size() - 1);
			ce.limit = CommonStatic.customEnemyMinPos(anim.mamodel);
			ene.anim = anim;
			edit.setEnabled(pac.editable);
			edit.setToolTipText(null);
		}
		changing = false;
	}

	private void ini() {
		add(back);
		add(jspp);
		add(jspe);
		add(jspd);
		add(addp);
		add(remp);
		add(jtfp);
		add(adde);
		add(reme);
		add(erea);
		add(jtfe);
		add(edit);
		add(sdiy);
		add(jsps);
		add(adds);
		add(rems);
		add(jtfs);
		add(extr);
		add(jspr);
		add(jspt);
		add(addr);
		add(remr);
		add(vene);
		add(lbp);
		add(lbe);
		add(lbd);
		add(lbs);
		add(lbr);
		add(lbt);
		add(cunt);
		add(tdiy);
		add(vcas);
		add(vrcg);
		add(vrlr);
		add(vbgr);
		add(ener);
		add(vmsc);
		add(unpk);
		add(recd);
		add(csol);
		add(cbge);
		add(cmbo);
		add(cdesc);

		cmbo.setToolTipText("Decide whether to apply or not this pack's custom CatCombos onto your lineups");

		add(pid);
		add(pauth);
		jle.setCellRenderer(new AnimLCR());
		jtd.setCellRenderer(new AnimTreeRenderer());
		SwingUtilities.invokeLater(() -> jtd.setUI(new TreeNodeExpander(jtd)));
		lbp.setModel(new DefaultComboBoxModel<>(get(MainLocale.PAGE, "psort", 10)));
		setPack(null);
		addListeners();
		addListeners$1();
		addListeners$2();
		addListeners$3();
		addListeners$4();
	}

	private void setEnemy(Enemy e) {
		ene = e;
		boolean b = e != null && pac.editable;
		edit.setEnabled(e != null && e.de instanceof CustomEnemy && e.anim != null);
		erea.setEnabled(b);

		if(e != null && e.anim == null) {
			edit.setToolTipText(get(MainLocale.PAGE, "corrrea"));
		} else {
			edit.setToolTipText(null);
		}

		jtfe.setEnabled(b);
		reme.setEnabled(b);
		if (b) {
			jtfe.setText(e.names.toString());
			boolean cont = false;

			for(EneRand rand : pac.randEnemies.getList()) {
				if(rand != null) {
					cont = cont || rand.contains(e.id, rand.id);
				}
			}

			reme.setEnabled(e.findApp(pac.mc).size() == 0 && !cont);
		}
	}

	private void finishRemoving(StageMap map) {
		for (Stage s : map.list)
			if (s.info != null)
				((CustomStageInfo)s.info).destroy();
		for (Stage s : map.list)
			for (CustomStageInfo si : ((MapColc.PackMapColc)map.getCont()).si)
				si.remove(s);
	}

	private void setMap(StageMap map) {
		sm = map;
		rems.setEnabled(sm != null && pac.editable);
		jtfs.setEnabled(sm != null && pac.editable);
		if (sm != null)
			jtfs.setText(sm.names.toString());
	}

	private void setPack(UserPack pack) {
		checkMapAnims(pack);

		pac = pack;
		boolean b = pac != null && pac.editable;
		SortedPackSet<String> deps = pac != null ? parentedList(pac) : null;
		remp.setEnabled(pac != null && deps.isEmpty());

		if(pac != null)
			if(!deps.isEmpty()) {
				remp.setToolTipText(Page.get(MainLocale.PAGE, "packused") + deps);
			} else {
				remp.setToolTipText(null);
			}

		jtfp.setEnabled(b);
		adde.setEnabled(b);
		adds.setEnabled(b);
		extr.setEnabled(pac != null);
		vcas.setEnabled(pac != null);
		vbgr.setEnabled(pac != null);
		vene.setEnabled(pac != null);
		vmsc.setEnabled(pac != null);
		recd.setEnabled(pac != null);
		ener.setEnabled(pac != null);
		cbge.setEnabled(pac != null);
		cdesc.setEnabled(pac != null);
		boolean canUnpack = pac != null && !pac.editable;
		boolean canExport = pac != null && pac.editable;
		unpk.setEnabled(canUnpack);
		extr.setEnabled(canExport);

		cmbo.setEnabled(pac != null && pac.combos.size() > 0);
		cmbo.setSelected(cmbo.isEnabled() && pac.useCombos);
		if (b)
			jtfp.setText(pack.desc.names.toString());
		if (pac == null) {
			jle.setListData(new Enemy[0]);
			jlr.setListData(new UserPack[0]);
		} else {
			jle.setListData(pac.enemies.toRawArray());
			jle.clearSelection();
			updateJlr();
			jlr.clearSelection();
		}
		checkAddr();
		boolean b0 = pac != null;
		sdiy.setEnabled(b0);
		tdiy.setEnabled(b0);
		if (b0) {
			jls.setListData(pac.mc, pac.mc.maps);
			jls.clearSelection();
		} else
			jls.setListData(null, null);
		setRely(null);
		setMap(null);
		setEnemy(null);
		pid.setVisible(pack != null);
		pauth.setVisible(pack != null);

		if(pack != null) {
			pid.setText("ID : "+pack.desc.id);
			if(pack.desc.getAuthor().isEmpty()) {
				pauth.setText("Author : (None)");
			} else {
				pauth.setText("Author : "+pack.desc.author);
			}
		}
	}

	private void checkMapAnims(UserPack pack) {
		if (Objects.equals(pack, pac))
			return;
		removeMappedAnims(pac);
		addMappedAnims(pack, false);
	}
	private void addMappedAnims(UserPack pack, boolean unedit) {
		if (pack != null && (pack.editable || (unedit && pack.desc.allowAnim))) {
			DefaultMutableTreeNode container = new DefaultMutableTreeNode(pack.getSID());
			for (AnimCI anim : pack.source.getAnims(Source.BasePath.ANIM))
				container.add(new DefaultMutableTreeNode(anim));
			if (container.getChildCount() > 0)
				agt.addNode(container);
			for (String s : pack.desc.dependency)
				addMappedAnims(UserProfile.getUserPack(s), true);
		}
	}
	private void removeMappedAnims(UserPack pack) {
		if (pack == null)
			return;
		agt.removeNode(pack.getSID());
		for (String s : pack.desc.dependency)
			removeMappedAnims(UserProfile.getUserPack(s));
	}

	private AnimCI getSelectedAnim() {
		TreePath path = jtd.getSelectionPath();

		if(path != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

			if (node.getUserObject() instanceof AnimCI)
				return (AnimCI) node.getUserObject();
		}
		return null;
	}

	private void setRely(UserPack rel) {
		if (pac == null || rel == null) {
			remr.setEnabled(false);
			return;
		}
		boolean re = pac.relyOn(rel.getSID());
		remr.setText(0, re ? "rema" : "rem");
		remr.setForeground(re ? Color.RED : Color.BLACK);
		remr.setEnabled(true);
	}

	private void updateJlr() {
		UserPack[] rel = new UserPack[pac.desc.dependency.size()];
		for (int i = 0; i < pac.desc.dependency.size(); i++)
			rel[i] = UserProfile.getUserPack(pac.desc.dependency.get(i));
		jlr.setListData(rel);
	}

	private SortedPackSet<String> parentedList(UserPack pack) {
		SortedPackSet<String> pars = new SortedPackSet<>();
		for(UserPack p : UserProfile.getUserPacks()) {
			if(p.getSID().equals(pack.getSID()))
				continue;
			if(p.desc.dependency.contains(pack.getSID()))
				pars.add(p.toString());
		}
		return pars;
	}

	public static void merge(UserPack src, UserPack dest) {
		for (Trait t : src.traits) {
			Trait nt = new Trait(dest.getNextID(Trait.class), t);
			if (t.icon != null)
				try {
					File file = ((Source.Workspace)dest.source).getTraitIconFile(nt.id);
					Context.check(file);

					BufferedImage bimg = (BufferedImage) t.icon.getImg().bimg();
					ImageIO.write(bimg, "PNG", file);
					t.icon = MainBCU.builder.toVImg(bimg);
				} catch (IOException e) {
					CommonStatic.ctx.noticeErr(e, Context.ErrType.WARN, "failed to merge icon for " + t);
				}
			dest.traits.add(nt);
		}
		for (Enemy e : src.enemies) {
			CustomEnemy ce;
			Enemy ne;
			if (e.anim instanceof AnimCE && ((AnimCE) e.anim).id.pack.equals(src.getSID())) {
				((Workspace)dest.source).addAnimation((AnimCE) e.anim);
				Source.ResourceLocation rl = new Source.ResourceLocation(dest.getSID(), e.anim.toString(), Source.BasePath.ANIM);
				Workspace.validate(rl);

				AnimCE copy = new AnimCE(rl, e.anim);
				ce = new CustomEnemy(copy);
				ne = new Enemy(dest.getNextID(Enemy.class), copy, ce);
			} else {
				ce = new CustomEnemy(e.anim);
				ne = new Enemy(dest.getNextID(Enemy.class), e.anim, ce);
			}
			ce.importData(e.de);
			for (int i = 0; i < ce.traits.size(); i++)
				if (ce.traits.get(i).id.pack.equals(src.getSID())) {
					Trait t = ce.traits.get(i);
					ce.traits.remove(i);
					ce.traits.add(dest.traits.get(dest.traits.size() - src.traits.size() + t.id.id));
					i--;
				}
			if (ce.getProc().SUMMON.id != null && ce.getProc().SUMMON.id.pack.equals(src.getSID()))
				ce.getProc().SUMMON.id.pack = dest.getSID();
			if (ce.getProc().THEME.id != null && ce.getProc().THEME.id.pack.equals(src.getSID()))
				ce.getProc().THEME.id.pack = dest.getSID();
			if (ce.getProc().THEME.mus != null && ce.getProc().THEME.mus.pack.equals(src.getSID()))
				ce.getProc().THEME.mus.pack = dest.getSID();
			for (AtkDataModel atk : ce.getAllAtkModels()) {
				if (atk.audio != null && atk.audio.pack.equals(src.getSID()))
					atk.audio.pack = dest.getSID();
				if (atk.audio1 != null && atk.audio1.pack.equals(src.getSID()))
					atk.audio1.pack = dest.getSID();
				for (int i = 0; i < atk.traits.size(); i++)
					if (atk.traits.get(i).id.pack.equals(src.getSID())) {
						Trait t = atk.traits.get(i);
						atk.traits.remove(i);
						atk.traits.add(dest.traits.get(dest.traits.size() - src.traits.size() + t.id.id));
						i--;
					}
			}
			if (ce.death != null && ce.death.pack.equals(src.getSID()))
				ce.death.pack = dest.getSID();
			dest.enemies.add(ne);
		}
		for (EneRand rand : src.randEnemies) {
			EneRand nr = new EneRand(dest.getNextID(EneRand.class));
			nr.name = rand.name;
			nr.type = rand.type;
			for (EREnt ere : rand.list) {
				EREnt ne = ere.copy();
				if (ere.ent != null && Enemy.class.isAssignableFrom(ere.ent.cls) && ere.ent.pack.equals(src.getSID()))
					ne.ent = dest.enemies.get(dest.enemies.size() - src.enemies.size() + ere.ent.id).getID();
				nr.list.add(ere);
			}
			if (rand.icon != null)
				try {
					File file = ((Source.Workspace)dest.source).getRandIconFile("enemyDisplayIcons", nr.id);
					Context.check(file);

					BufferedImage bimg = (BufferedImage) rand.icon.getImg().bimg();
					ImageIO.write(bimg, "PNG", file);
					rand.icon = MainBCU.builder.toVImg(bimg);
				} catch (IOException e) {
					CommonStatic.ctx.noticeErr(e, Context.ErrType.WARN, "failed to merge icon for " + rand);
				}
			dest.randEnemies.add(nr);
		}
		for (UnitLevel ul : src.unitLevels)
			dest.unitLevels.add(new UnitLevel(dest.getNextID(UnitLevel.class), ul));
		//TODO - Units, RandomUnit, Souls, BGs, BGEffects, CharaGroups, LvRestrictions, Musics, Combos, Stages
	}
}
