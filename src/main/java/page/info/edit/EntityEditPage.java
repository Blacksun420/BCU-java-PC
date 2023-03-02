package page.info.edit;

import common.CommonStatic;
import common.battle.Basis;
import common.battle.BasisSet;
import common.battle.data.AtkDataModel;
import common.battle.data.CustomEntity;
import common.pack.Identifier;
import common.pack.IndexContainer.Indexable;
import common.pack.PackData.UserPack;
import common.pack.Source;
import common.pack.UserProfile;
import common.util.Animable;
import common.util.anim.AnimCE;
import common.util.anim.AnimCI;
import common.util.anim.AnimU;
import common.util.anim.AnimU.UType;
import common.util.lang.Editors;
import common.util.pack.Background;
import common.util.pack.EffAnim;
import common.util.pack.Soul;
import common.util.stage.Music;
import common.util.unit.Enemy;
import common.util.unit.Form;
import common.util.unit.Unit;
import main.MainBCU;
import main.Opts;
import page.*;
import page.anim.DIYViewPage;
import page.info.edit.SwingEditor.EditCtrl;
import page.info.edit.SwingEditor.IdEditor;
import page.info.filter.EnemyFindPage;
import page.info.filter.UnitFindPage;
import page.support.ListJtfPolicy;
import page.support.ReorderList;
import page.support.ReorderListener;
import page.view.BGViewPage;
import page.view.EnemyViewPage;
import page.view.MusicPage;
import page.view.UnitViewPage;

import javax.swing.*;
import java.util.*;

import static common.util.Data.*;

public abstract class EntityEditPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JBTN back = new JBTN(0, "back");

	private final JL lhp = new JL(MainLocale.INFO, "HP");
	private final JL lhb = new JL(MainLocale.INFO, "hb");
	private final JL lsp = new JL(MainLocale.INFO, "speed");
	private final JL lra = new JL(MainLocale.INFO, "range");
	private final JL lwd = new JL(MainLocale.INFO, "width");
	private final JL ltb = new JL(MainLocale.INFO, "TBA");
	private final JL lbs = new JL(MainLocale.INFO, "tbase");
	private final JL ltp = new JL(MainLocale.INFO, "type");
	private final JL lct = new JL(MainLocale.INFO, "count");
	private final JL ldps = new JL(MainLocale.INFO,"DPS");
	private final JL lwp = new JL(MainLocale.INFO,"will");
	private final JL lli = new JL(MainLocale.INFO, "minpos");
	private final JL cdps = new JL();
	private final JTF fhp = new JTF();
	private final JTF fhb = new JTF();
	private final JTF fsp = new JTF();
	private final JTF fra = new JTF();
	private final JTF fwd = new JTF();
	private final JTF ftb = new JTF();
	private final JTF fbs = new JTF();
	private final JTF ftp = new JTF();
	private final JTF fct = new JTF();
	private final JTF fwp = new JTF();
	protected final JTF fli = new JTF();
	private final ReorderList<AtkDataModel> jli = new ReorderList<>();
	private final JScrollPane jspi = new JScrollPane(jli);
	private final JBTN add = new JBTN(MainLocale.PAGE, "add");
	private final JBTN rem = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN copy = new JBTN(MainLocale.PAGE, "copy");
	private final JBTN link = new JBTN(MainLocale.PAGE, "link");
	private final JTG comm = new JTG(MainLocale.INFO, "common");
	private final JTF atkn = new JTF();
	private final JL lpst = new JL(MainLocale.INFO, "postaa");
	private final JL vpst = new JL();
	private final JL litv = new JL(MainLocale.INFO, "atkf");
	private final JL vitv = new JL();
	private final JComboBox<AnimCI> jcba = new JComboBox<>();
	private final JComboBox<Soul> jcbs = new JComboBox<>();
	private final JComboBox<String> jlang = new JComboBox<>(MainLocale.LOC_NAME);
	private final JTG hbbo = new JTG(MainLocale.INFO, "kbbounce");
	private final JTG bobo = new JTG(MainLocale.INFO, "bossbounce");
	private final ListJtfPolicy ljp = new ListJtfPolicy();
	private final AtkEditTable aet;
	private final ProcTable.MainProcTable mpt;
	private final JScrollPane jspm;
	private final CustomEntity ce;
	protected final UserPack pack;
	private final JTA entDesc = new JTA();
	private final JScrollPane jsDesc = new JScrollPane(entDesc);

	private final ProcTable.AtkProcTable apt;
	private final JScrollPane jsp;

	private boolean changing = false;
	private EnemyFindPage efp;
	private UnitFindPage ufp;

	private SupPage<? extends Indexable<?, ?>> sup;
	private IdEditor<?> editor;

	protected final Basis bas = BasisSet.current();
	private final JComboBox<String> atkS = new JComboBox<>();
	private final JL jsh = new JL(MainLocale.INFO, "er2");
	private final JTF josh = new JTF();

	protected final ArrayList<AtkDataModel> extra = new ArrayList<>();

	public EntityEditPage(Page p, UserPack pac, CustomEntity e, boolean isEnemy) {
		super(p);
		Editors.setEditorSupplier(new EditCtrl(isEnemy, this));
		pack = pac;
		ce = e;
		aet = new AtkEditTable(this, pack);
		apt = new ProcTable.AtkProcTable(aet, pack.editable, !isEnemy);
		aet.setProcTable(apt);
		jsp = new JScrollPane(apt);
		mpt = new ProcTable.MainProcTable(this, pack.editable, !isEnemy);
		jspm = new JScrollPane(mpt);
		if (!pack.editable)
			jli.setDragEnabled(false);

		String[] atkSS = new String[e.getAtkTypeCount() + 1];
		for (int i = 0; i < atkSS.length - 1; i++)
			atkSS[i] = get(MainLocale.PAGE, "atk") + (i + 1);
		atkSS[atkSS.length - 1] = MainLocale.getLoc(MainLocale.PAGE, "spatk");
		atkS.setModel(new DefaultComboBoxModel<>(atkSS));
	}

	@Override
    public JButton getBackButton() {
		return back;
	}

	@Override
	public void callBack(Object o) {
		if (o instanceof int[]) {
			int[] vals = (int[]) o;
			if (vals.length == 2) {
				ce.abi = vals[0];
				ce.loop = (ce.abi & AB_GLASS) > 0 ? 1 : -1;
			}
		}
		setData(ce);
	}

	public SupPage<Music> getMusicSup(IdEditor<Music> edi) {
		editor = edi;
		SupPage<Music> ans = new MusicPage(this, pack.getSID());
		sup = ans;
		return ans;
	}

	public SupPage<Background> getBGSup(IdEditor<Background> edi) {
		editor = edi;
		SupPage<Background> ans = new BGViewPage(this, pack.getSID());
		sup = ans;
		return ans;
	}

	public SupPage<?> getEntitySup(IdEditor<?> edi) {
		editor = edi;

		SupPage<?> ans;
		if ((ce.getPack() instanceof Enemy && get(jli.getSelectedIndex()).dire != -1)
				|| (ce.getPack() instanceof Form && get(jli.getSelectedIndex()).dire == -1)) {
			ans = new EnemyFindPage(this, true, pack);
		} else {
			ans = new UnitFindPage(this, true, pack);
		}

		sup = ans;
		return ans;
	}

	protected double getAtk() {
		return 1;
	}

	protected double getLvAtk() {
		return 1;
	}

	protected double getDef() {
		return 1;
	}

	protected abstract void getInput(JTF jtf, int[] v);

	protected void ini() {
		set(lhp);
		set(lhb);
		set(lsp);
		set(lwd);
		set(lra);
		set(ltb);
		set(lbs);
		set(ltp);
		set(lct);
		set(fhp);
		set(fhb);
		set(fsp);
		set(fwd);
		set(fra);
		set(ftb);
		set(fbs);
		set(ftp);
		set(fct);
		set(lwp);
		set(fwp);
		set(ldps);
		set(cdps);
		set(lli);
		set(fli);
		ljp.end();
		add(jspi);
		add(atkS);
		if (ce.getAtkTypeCount() > 1) {
			add(jsh);
			add(josh);
		}
		add(aet);
		add(jspm);
		add(add);
		add(rem);
		add(copy);
		add(link);
		add(back);
		set(atkn);
		set(lpst);
		set(vpst);
		set(litv);
		set(vitv);
		add(comm);
		add(jcbs);
		add(jlang);
		jlang.setSelectedIndex(MainLocale.LOC_INDEX[CommonStatic.getConfig().lang]);
		add(jsDesc);
		add(hbbo);
		add(bobo);
		Vector<Soul> vec = new Vector<>();
		vec.add(null);
		vec.addAll(UserProfile.getAll(pack.getSID(), Soul.class));
		jcbs.setModel(new DefaultComboBoxModel<>(vec));
		if (pack.editable) {
			add(jcba);
			Vector<AnimCI> vda = new Vector<>();
			if (ce.getPack().anim instanceof AnimCE) {
				AnimCE ac = ((AnimCE) ce.getPack().anim);
				if (ac != null && !ac.inPool())
					vda.add(ac);
				vda.addAll(AnimCE.map().values());
				if (ac == null)
					ce.getPack().anim = vda.get(0);
			} else
				vda.addAll(AnimCE.map().values());
			getAnims(vda, pack);
			jcba.setModel(new DefaultComboBoxModel<>(vda));
			if (!(ce.getPack().anim instanceof AnimCE))
				jcba.setSelectedIndex(-1);
		}
		setFocusTraversalPolicy(ljp);
		setFocusCycleRoot(true);
		addListeners();
		atkn.setToolTipText("<html>use name \"revenge\" for attack during HB animation<br>"
				+ "use name \"resurrection\" for attack during death animation<br>"
				+ "use name \"counterattack\" for a more customizable counterattack (Needs Counter proc parameters still)<br>"
				+ "use name \"burrow\" for attack during burrow down animation<br>"
				+ "use name \"resurface\" for attack during burrow up animation<br>"
				+ "use name \"revive\" for attack during reviving</html>");
		ftp.setToolTipText(
				"<html>" + "+1 for normal attack<br>" + "+2 to attack kb<br>" + "+4 to attack underground<br>"
						+ "+8 to attack corpse<br>" + "+16 to attack soul<br>" + "+32 to attack ghost<br>" +
						"+64 to attack entities that can revive others<br>" + "+128 to attack enter animations</html>");
		fwp.setToolTipText(
				"<html>" + "The amount of slots this entity will take of the limit"
				+ " when spawned</html>");

		add.setEnabled(pack.editable);
		rem.setEnabled(pack.editable);
		copy.setEnabled(pack.editable);
		link.setEnabled(pack.editable);
		atkn.setEnabled(pack.editable);
		comm.setEnabled(pack.editable);
		jcbs.setEnabled(pack.editable);
		entDesc.setEnabled(pack.editable);
		hbbo.setEnabled(pack.editable);
		bobo.setEnabled(pack.editable);

		add(jsp);
	}

	private static void getAnims(Vector<AnimCI> vda, UserPack pac) {
		if (pac.editable || pac.desc.allowAnim)
			vda.addAll(pac.source.getAnims(Source.BasePath.ANIM));
		for (String s : pac.desc.dependency)
			getAnims(vda, UserProfile.getUserPack(s));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void renew() {
		if (efp != null && efp.getSelected() != null
				&& Opts.conf("do you want to overwrite stats? This operation cannot be undone")) {
			Enemy e = (Enemy) efp.getSelected();
			ce.importData(e.de);
			ce.traits.removeIf(t -> !(t.BCTrait || pack.desc.dependency.contains(t.id.pack) || pack.desc.id.equals(t.id.pack)));
			setData(ce);
		}
		if (ufp != null && ufp.getForm() != null
				&& Opts.conf("do you want to overwrite stats? This operation cannot be undone")) {
			Form f = (Form) ufp.getForm();
			ce.importData(f.du);
			ce.traits.removeIf(t -> !(t.BCTrait || pack.desc.dependency.contains(t.id.pack) || pack.desc.id.equals(t.id.pack)));
			setData(ce);
		}
		if (sup != null && editor != null && sup.getSelected() != null) {
			Identifier val = sup.getSelected().getID();
			editor.callback(val);
		}
		sup = null;
		editor = null;
		efp = null;
		ufp = null;
	}

	@Override
	protected void resized(int x, int y) {
		setSize(x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(lhp, x, y, 50, 100, 100, 50);
		set(fhp, x, y, 150, 100, 200, 50);
		set(lhb, x, y, 50, 150, 100, 50);
		set(fhb, x, y, 150, 150, 200, 50);
		set(lsp, x, y, 50, 200, 100, 50);
		set(fsp, x, y, 150, 200, 200, 50);
		set(lwp, x, y, 50, 250, 100, 50);
		set(fwp, x, y, 150, 250, 200, 50);
		set(lwd, x, y, 50, 300, 100, 50);
		set(fwd, x, y, 150, 300, 200, 50);
		set(jsh, x, y, 350, 50, 100, 50);
		set(josh, x, y, 450, 50, 200, 50);
		set(atkS, x, y, 650, 50, 400, 50);

		set(jspm, x, y, 1850, 100, 350, 900);
		mpt.componentResized(x, y);

		set(jspi, x, y, 350, 100, 300, 350);
		set(add, x, y, 350, 450, 150, 50);
		set(rem, x, y, 500, 450, 150, 50);
		set(copy, x, y, 350, 500, 150, 50);
		set(link, x, y, 500, 500, 150, 50);
		set(comm, x, y, 350, 550, 300, 50);
		set(atkn, x, y, 350, 600, 300, 50);

		set(lra, x, y, 50, 400, 100, 50);
		set(fra, x, y, 150, 400, 200, 50);
		set(ltb, x, y, 50, 450, 100, 50);
		set(ftb, x, y, 150, 450, 200, 50);
		set(lbs, x, y, 50, 500, 100, 50);
		set(fbs, x, y, 150, 500, 200, 50);
		set(ltp, x, y, 50, 550, 100, 50);
		set(ftp, x, y, 150, 550, 200, 50);
		set(lct, x, y, 50, 600, 100, 50);
		set(fct, x, y, 150, 600, 200, 50);

		set(aet, x, y, 650, 100, 400, 900);

		set(ldps, x, y, 650, 1050, 200, 50);
		set(cdps, x, y, 850, 1050, 200, 50);
		set(lpst, x, y, 650, 1100, 200, 50);
		set(vpst, x, y, 850, 1100, 200, 50);
		set(litv, x, y, 650, 1150, 200, 50);
		set(vitv, x, y, 850, 1150, 200, 50);
		set(jcba, x, y, 650, 1000, 400, 50);
		set(lli, x, y, 1800, 1000, 200, 50);
		set(fli, x, y, 2000, 1000, 200, 50);
		set(hbbo, x, y, 50, 1200, 200, 50);
		set(bobo, x, y, 250, 1200, 200, 50);

		set(jcbs, x, y, 1800, 1050, 400, 50);
		set(jsDesc, x, y, 1050, 1000, 750, 200);
		set(jlang, x, y, 1800, 1150, 400, 50);

		jsp.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
		jspm.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
		apt.setPreferredSize(size(x, y, 750, apt.height).toDimension());
		apt.resized(x, y);
		set(jsp, x, y, 1050, 100, 800, 900);
	}

	protected void set(JL jl) {
		jl.setHorizontalAlignment(SwingConstants.CENTER);
		add(jl);
	}

	protected void set(JTF jtf) {
		jtf.setEditable(pack.editable);
		add(jtf);
		ljp.add(jtf);

		jtf.setLnr(e -> {
			input(jtf, jtf.getText().trim());
			setData(ce);
		});

	}

	protected void setData(CustomEntity data) {
		changing = true;

		fhp.setText("" + (int) (ce.hp * getDef()));
		fhb.setText("" + ce.hb);
		fsp.setText("" + ce.speed);
		fra.setText("" + ce.range);
		fwd.setText("" + ce.width);
		ftb.setText("" + ce.tba);
		fbs.setText("" + ce.base);
		vitv.setText((isSp() ? ce.getItv(0) : ce.getItv(getSel())) + "");
		ftp.setText("" + ce.touch);
		fct.setText("" + ce.loop);
		fwp.setText("" + (ce.will + 1));
		if (!isSp())
			cdps.setText("" + (int) ((getLvAtk() * ce.allAtk(getSel())) * getAtk()  * 30 / ce.getItv(getSel())));
		else
			cdps.setText("-");

		String d = data.getPack().description.get(jlang.getSelectedIndex());
		entDesc.setText(d.isEmpty() ? "Description" : d);

		comm.setSelected(data.common);
		if (!comm.isSelected())
			ce.updateAllProc();

		mpt.setData(ce.rep.proc);

		int ind = jli.getSelectedIndex();
		josh.setEnabled(pack.editable && !isSp());
		if (isSp()) {
			extra.clear();
			for (AtkDataModel[] atks : ce.getSpAtks(true))
				Collections.addAll(extra, atks);
			jli.setListData(extra.toArray(new AtkDataModel[0]));

			if (ind >= extra.size())
				ind = extra.size() - 1;
		} else {
			AtkDataModel[] raw = (AtkDataModel[])ce.getAtks(getSel());
			jli.setListData(raw);
			if (ind >= raw.length)
				ind = raw.length - 1;
			josh.setText("" + ce.getShare(getSel()));
		}
		if (ind < 0)
			ind = 0;
		setA(ind);
		jli.setSelectedIndex(ind);
		Animable<AnimU<?>, UType> ene = ce.getPack();

		if (pack.editable)
			jcba.setSelectedItem(ene.anim);

		jcbs.setSelectedItem(Identifier.get(ce.death));
		hbbo.setSelected(ce.kbBounce);
		bobo.setSelected(ce.bossBounce);
		changing = false;
	}

	protected void subListener(JBTN e, JBTN u, JBTN a, Object o) {
		e.setLnr(x -> changePanel(efp = new EnemyFindPage(getThis(), false)));

		u.setLnr(x -> changePanel(ufp = new UnitFindPage(getThis(), false)));

		a.setLnr(x -> {
			if (pack.editable) {
				AnimCE anim = (AnimCE) jcba.getSelectedItem();

				if(anim != null)
					changePanel(new DIYViewPage(getThis(), anim));
			} else if (o instanceof Unit)
				changePanel(new UnitViewPage(getThis(), (Unit) o));
			else if (o instanceof Enemy)
				changePanel(new EnemyViewPage(getThis(), (Enemy) o));
		});

		e.setEnabled(pack.editable);
		u.setEnabled(pack.editable);
	}

	private void addListeners() {

		back.setLnr(e -> changePanel(getFront()));

		comm.setLnr(e -> {
			ce.common = comm.isSelected();
			setData(ce);
		});

		jli.addListSelectionListener(e -> {
			if (changing || jli.getValueIsAdjusting())
				return;
			changing = true;
			if (jli.getSelectedIndex() == -1)
				jli.setSelectedIndex(0);
			setA(jli.getSelectedIndex());
			changing = false;
		});

		jli.list = new ReorderListener<AtkDataModel>() {
			@Override
			public void reordered(int ori, int fin) {
				if (ori < ce.hits.get(getSel()).length) {
					if (fin >= ce.hits.get(getSel()).length)
						fin = ce.hits.get(getSel()).length - 1;
					List<AtkDataModel> l = new ArrayList<>();
					Collections.addAll(l, ce.hits.get(getSel()));
					l.add(fin, l.remove(ori));
					ce.hits.set(getSel(), l.toArray(new AtkDataModel[0]));
				}
				setData(ce);
				changing = false;
			}

			@Override
			public void reordering() {
				if (isSp())
					return;
				changing = true;
			}
		};

		atkS.addActionListener(l -> {
			if (changing || jli.getValueIsAdjusting())
				return;
			changing = true;
			boolean ignore = isSp();
			if (ignore) {
				for (AtkDataModel[] atks : ce.getSpAtks(true))
					if (atks.length != 0) {
						ignore = false;
						break;
					}
				if (ignore && !addSpecial(-1, new AtkDataModel(ce)))
					atkS.setSelectedIndex(ce.getAtkTypeCount() - 1);
			}
			if (jli.getSelectedIndex() >= getSelMask().length)
				jli.setSelectedIndex(0);
			setData(ce);
			changing = false;
		});

		add.setLnr(e -> {
			if (isSp()) {
				addSpecial(-1, new AtkDataModel(ce));
			} else {
				changing = true;
				int n = ce.hits.get(getSel()).length;
				int ind = jli.getSelectedIndex();
				if (ind >= n)
					ind = n - 1;
				AtkDataModel[] datas = new AtkDataModel[n + 1];
				if (ind + 1 >= 0)
					System.arraycopy(ce.hits.get(getSel()), 0, datas, 0, ind + 1);
				ind++;
				datas[ind] = new AtkDataModel(ce);
				if (n - ind >= 0)
					System.arraycopy(ce.hits.get(getSel()), ind, datas, ind + 1, n - ind);
				ce.hits.set(getSel(), datas);
				setData(ce);
				jli.setSelectedIndex(ind);
				setA(ind);
				changing = false;
			}
		});

		rem.setLnr(e -> remAtk(jli.getSelectedIndex()));

		copy.setLnr(e -> {
			if (isSp())
				return;
			changing = true;
			int n = ce.hits.get(getSel()).length;
			int ind = jli.getSelectedIndex();
			ce.hits.set(getSel(), Arrays.copyOf(ce.hits.get(getSel()), n + 1));
			ce.hits.get(getSel())[n] = ce.hits.get(getSel())[ind].clone();
			setData(ce);
			jli.setSelectedIndex(n);
			setA(n);
			changing = false;
		});

		link.setLnr(e -> {
			if (isSp())
				return;
			changing = true;
			int n = ce.hits.get(getSel()).length;
			int ind = jli.getSelectedIndex();
			ce.hits.set(getSel(), Arrays.copyOf(ce.hits.get(getSel()), n + 1));
			ce.hits.get(getSel())[n] = ce.hits.get(getSel())[ind];
			setData(ce);
			jli.setSelectedIndex(n);
			setA(n);
			changing = false;
		});

		jcba.addActionListener(arg0 -> {
			if (changing)
				return;
			ce.getPack().anim = (AnimCE) jcba.getSelectedItem();
			setData(ce);

		});

		jcbs.addActionListener(arg0 -> {
			if (changing)
				return;

			Soul s = (Soul) jcbs.getSelectedItem();

			ce.death = s == null ? null : s.getID();
			setData(ce);
		});

		jlang.addActionListener(act -> entDesc.setText(ce.getPack().description.get(MainLocale.LOC_INDEX[jlang.getSelectedIndex()])));

		entDesc.setLnr(j -> {
			String txt = entDesc.assignSplitText(-1);
			if (txt.equals("Description") || txt.isEmpty() || (MainLocale.LOC_INDEX[jlang.getSelectedIndex()] != CommonStatic.getConfig().lang && txt.equals(ce.getPack().description.toString())))
				ce.getPack().description.remove(MainLocale.LOC_INDEX[jlang.getSelectedIndex()]);
			else
				ce.getPack().description.put(MainLocale.LOC_INDEX[jlang.getSelectedIndex()], txt);
			setData(ce);
		});

		hbbo.addActionListener(arg0 -> {
			if (changing)
				return;

			ce.kbBounce = hbbo.isSelected();
		});

		bobo.addActionListener(arg0 -> {
			if (changing)
				return;

			ce.bossBounce = bobo.isSelected();
		});
	}

	private boolean addSpecial(int sel, AtkDataModel adm) {
		int selection = sel != -1 ? sel : Opts.selection("What kind of special Attack do you want to create?",
				"Select Attack Type",
				"Revenge", "Resurrection", "Counterattack", "Burrow", "Resurface", "Revival", "Entry");
		if (selection == -1)
			return false;
		changing = true;
		switch (selection) {
			case 0:
				ce.revs = Arrays.copyOf(ce.revs, ce.revs.length + 1);
				ce.revs[ce.revs.length - 1] = adm;
				ce.revs[ce.revs.length - 1].str = "revenge" + (ce.revs.length > 1 ? " " + ce.revs.length : "");
				break;
			case 1:
				ce.ress = Arrays.copyOf(ce.ress, ce.ress.length + 1);
				ce.ress[ce.ress.length - 1] = adm;
				ce.ress[ce.ress.length - 1].str = "resurrection" + (ce.ress.length > 1 ? " " + ce.ress.length : "");
				break;
			case 2:
				ce.cntr = new AtkDataModel(ce);
				ce.cntr.str = "counterattack";
				break;
			case 3:
				ce.burs = Arrays.copyOf(ce.burs, ce.burs.length + 1);
				ce.burs[ce.burs.length - 1] = adm;
				ce.burs[ce.burs.length - 1].str = "burrow" + (ce.burs.length > 1 ? " " + ce.burs.length : "");
				break;
			case 4:
				ce.resus = Arrays.copyOf(ce.resus, ce.resus.length + 1);
				ce.resus[ce.resus.length - 1] = adm;
				ce.resus[ce.resus.length - 1].str = "resurface" + (ce.resus.length > 1 ? " " + ce.resus.length : "");
				break;
			case 5:
				ce.revis = Arrays.copyOf(ce.revis, ce.revis.length + 1);
				ce.revis[ce.revis.length - 1] = adm;
				ce.revis[ce.revis.length - 1].str = "revive" + (ce.revis.length > 1 ? " " + ce.revis.length : "");
				break;
			case 6:
				ce.entrs = Arrays.copyOf(ce.entrs, ce.entrs.length + 1);
				ce.entrs[ce.entrs.length - 1] = adm;
				ce.entrs[ce.entrs.length - 1].str = "entrance" + (ce.entrs.length > 1 ? " " + ce.entrs.length : "");
		}
		return true;
	}

	private AtkDataModel get(int ind) {
		if (isSp()) {
			int real = 0;
			AtkDataModel[][] sps = ce.getSpAtks(true);
			while (ind >= sps[real].length)
				ind -= sps[real++].length;

			return sps[real][sps[real].length - 1 - ind];
		}
		return getSelMask()[ind];
	}

	private int getSel() {
		return Math.max(0, atkS.getSelectedIndex());
	}

	private boolean isSp() {
		return getSel() == ce.getAtkTypeCount();
	}

	private AtkDataModel[] getSelMask() {
		if (isSp()) {
			int num = jli.getSelectedIndex();
			if (num <= 0)
				return ce.getSpAtks(false)[0];

			int real = 0;
			AtkDataModel[][] sps = ce.getSpAtks(true);
			while (num >= sps[real].length) {
				real++;
				num -= sps[real].length;
			}
			return sps[real];
		}
		return ce.hits.get(getSel());
	}

	protected void input(JTF jtf, String text) {

		if (jtf == atkn) {
			AtkDataModel adm = aet.adm;

			if (adm == null || adm.str.equals(text))
				return;
			adm.checkAvail(text);

			switch (text) {
				case "revenge":
					remAtk(adm);
					addSpecial(0, adm);
					break;
				case "resurrection":
					remAtk(adm);
					addSpecial(1, adm);
					break;
				case "counterattack":
					remAtk(adm);
					ce.cntr = adm;
					break;
				case "burrow":
					remAtk(adm);
					addSpecial(3, adm);
					break;
				case "resurface":
					remAtk(adm);
					addSpecial(4, adm);
					break;
				case "revive":
					remAtk(adm);
					addSpecial(5, adm);
					break;
				case "entrance":
					remAtk(adm);
					addSpecial(6, adm);
					break;
			}
			return;
		}

		if (text.length() > 0) {
			int[] v = CommonStatic.parseIntsN(text);
			if (v.length > 0) {
				if (jtf == fhp) {
					v[0] /= getDef();
					if (v[0] <= 0)
						v[0] = 1;
					ce.hp = v[0];
				}
				if (jtf == fhb) {
					if (v[0] <= 0)
						v[0] = 1;
					ce.hb = v[0];
				}
				if (jtf == fsp) {
					ce.speed = Math.max(0, v[0]);
				}
				if (jtf == fra) {
					if (v[0] <= 0)
						v[0] = 1;
					ce.range = v[0];
				}
				if (jtf == fwd) {
					if (v[0] <= 0)
						v[0] = 1;
					ce.width = v[0];
				}
				if (jtf == ftb)
					ce.tba = v[0];
				if (jtf == fbs) {
					if (v[0] < 0)
						v[0] = 0;
					ce.base = v[0];
				}
				if (jtf == ftp) {
					if (v[0] < 1)
						v[0] = 1;
					ce.touch = v[0];
				}
				if (jtf == fct) {
					if (v[0] < -1)
						v[0] = -1;
					ce.loop = v[0];
				}
				if (jtf == fwp) {
					if (v[0] < 0)
						v[0] = 0;
					if (v[0] > 50)
						v[0] = 50;
					ce.will = v[0] - 1;
				}
				if (jtf == josh)
					ce.share[getSel()] = Math.max(1, v[0]);

				getInput(jtf, v);
			}
		}
		setData(ce);
	}

	private void remAtk(AtkDataModel adm) {
		for (int i = 0; i < getSelMask().length; i++)
			if (getSelMask()[i] == adm)
				remAtk(i);
	}

	private void remAtk(int ind) {
		changing = true;
		if (isSp()) {
			AtkDataModel rematk = extra.remove(ind);
			AtkDataModel[][] sps = ce.getSpAtks(false);
			if (rematk.equals(ce.cntr))
				ce.cntr = null;
			else
				for (int i = 0; i < sps.length; i++) {
					if (sps[i].length == 0)
						continue;
					if (rematk.str.contains(sps[i][0].str)) {
						for (int j = 0; j < sps[i].length; j++)
							if (sps[i][j] == rematk) {
								for (int k = j; k < sps[i].length - 1; k++) {
									sps[i][k] = sps[i][k + 1];
									sps[i][k].str = sps[i][k].str.substring(0, sps[i][k].str.length() - 1) + (k == 0 ? "" : " " + k);
									sps[i] = Arrays.copyOf(sps[i], sps[i].length - 1);
								}
								break;
							}
						break;
					}
				}
			boolean empty = true;
			for (AtkDataModel[] sp : sps)
				if (sp.length != 0) {
					empty = false;
					break;
				}
			if (empty)
				atkS.setSelectedIndex(ce.getAtkTypeCount() - 1);
		} else if (ce.hits.get(getSel()).length > 1) {
			int n = ce.hits.get(getSel()).length;
			AtkDataModel[] datas = new AtkDataModel[n - 1];
			if (ind >= 0)
				System.arraycopy(ce.hits.get(getSel()), 0, datas, 0, ind);
			if (n - (ind + 1) >= 0)
				System.arraycopy(ce.hits.get(getSel()), ind + 1, datas, ind + 1 - 1, n - (ind + 1));
			ce.hits.set(getSel(), datas);
		}
		setData(ce);
		ind--;
		if (ind < 0)
			ind = 0;
		jli.setSelectedIndex(ind);
		setA(ind);
		changing = false;
	}

	private void setA(int ind) {
		setA(get(ind));
		boolean b = pack.editable && ind < getSelMask().length;
		link.setEnabled(b);
		copy.setEnabled(b);
		atkn.setEnabled(b);
		rem.setEnabled(pack.editable && (isSp() || getSelMask().length > 1 || ind >= getSelMask().length));
	}
	private void setA(AtkDataModel adm) {
		atkn.setText(adm.str);
		aet.setData(adm, getAtk(), getLvAtk());
		if (isSp()) {
			if (adm.str.contains("revenge")) {
				lpst.setText(MainLocale.INFO, "Post-HB");
				vpst.setText(MainBCU.convertTime(KB_TIME[INT_HB] - ce.getPost(true, 0)));
			} else if (adm.str.contains("resurrection")) {
				lpst.setText(MainLocale.INFO, "Post-Death");
				Soul s = Identifier.get(ce.death);
				vpst.setText(s == null ? "-" : MainBCU.convertTime((s.anim.len(AnimU.SOUL[0]) - ce.getPost(true, 1))));
			} else if (adm.str.contains("counterattack")) {
				lpst.setText(MainLocale.INFO, "Post-Counter");
				vpst.setText("-");
			} else if (adm.str.contains("burrow")) {
				lpst.setText(MainLocale.INFO, "Post-Bury Atk");
				vpst.setText(MainBCU.convertTime(ce.getPack().anim.anims[4].len - ce.getPost(true, 2) + 1));
			} else if (adm.str.contains("revive")) {
				lpst.setText(MainLocale.INFO, "Post-Revival Atk");
				vpst.setText(MainBCU.convertTime(effas().A_ZOMBIE.getEAnim(EffAnim.ZombieEff.REVIVE).len() - ce.getPost(true, 3)));
			} else if (adm.str.contains("resurface")) {
				lpst.setText(MainLocale.INFO, "Post-Unburrow Atk");
				vpst.setText(MainBCU.convertTime(ce.getPack().anim.anims[6].len - ce.getPost(true, 4) + 1));
			} else {
				lpst.setText(MainLocale.INFO, "Post-Entrance Atk");
				vpst.setText(MainBCU.convertTime(ce.getPack().anim.anims[7].len - ce.getPost(true, 5) + 1));
			}
		} else {
			lpst.setText(MainLocale.INFO, "postaa");
			vpst.setText(MainBCU.convertTime(ce.getPost(false, getSel())));
		}
	}
}
