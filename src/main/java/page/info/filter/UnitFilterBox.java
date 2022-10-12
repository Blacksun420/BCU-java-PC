package page.info.filter;

import common.battle.data.MaskUnit;
import common.pack.FixIndexList;
import common.pack.Identifier;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.lang.MultiLangCont;
import common.util.lang.ProcLang;
import common.util.stage.Limit;
import common.util.unit.Form;
import common.util.unit.Trait;
import common.util.unit.Unit;
import main.MainBCU;
import page.JTG;
import page.MainLocale;
import page.Page;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import static utilpc.Interpret.*;

public abstract class UnitFilterBox extends Page {

	private static final long serialVersionUID = 1L;

	public static UnitFilterBox getNew(Page p, Limit lim, int price) {
		if (MainBCU.FILTER_TYPE == 0)
			return new UFBButton(p, lim, price);
		if (MainBCU.FILTER_TYPE == 1)
			return new UFBList(p, lim, price);
		return null;
	}

	public static UnitFilterBox getNew(Page p, String pack, List<String> parents) {
		if (MainBCU.FILTER_TYPE == 0)
			return new UFBButton(p, pack, parents);
		if (MainBCU.FILTER_TYPE == 1)
			return new UFBList(p, pack, parents);
		return null;
	}

	public String name = "";
	protected final List<String> parents;
	protected final String pack;
	protected final Limit lim;
	protected final int price;

	protected UnitFilterBox(Page p, Limit limit, int price) {
		super(p);
		lim = limit;
		this.price = price;

		pack = null;
		parents = null;
	}

	protected UnitFilterBox(Page p, String pack, List<String> parent) {
		super(p);
		lim = null;
		price = 0;

		this.pack = pack;
		this.parents = parent;
	}

	public abstract int[] getSizer();
}

class UFBButton extends UnitFilterBox {

	private static final long serialVersionUID = 1L;

	private final JTG[] orop = new JTG[3];
	private final JTG[] rare = new JTG[RARITY.length];
	private final JTG[] trait = new JTG[9];
	private final JTG[] abis = new JTG[SABIS.length];
	private final JTG[] proc = new JTG[UPROCIND.length];
	private final JTG[] atkt = new JTG[ATKCONF.length];

	protected UFBButton(Page p, Limit lim, int price) {
		super(p, lim, price);

		ini();
		confirm();
	}

	protected UFBButton(Page p, String pack, List<String> parents) {
		super(p, pack, parents);

		ini();
		confirm();
	}

	@Override
	public void callBack(Object o) {
		confirm();
	}

	@Override
	public int[] getSizer() {
		return new int[] { 2000, 400, 1, 400 };
	}

	@Override
	protected void resized(int x, int y) {
		JTG[][] btns = new JTG[][] { rare, trait, abis, proc, atkt };
		AttList.btnDealer(x, y, btns, orop, -1, 0, 1, -1, 2);
	}

	private final List<Trait> trlis = new ArrayList<>();

	private void confirm() {
		List<Form> ans = new ArrayList<>();
		int minDiff = 5;
		for(PackData p : UserProfile.getAllPacks()) {
			for (Unit u : p.units.getList())
				for (Form f : u.forms) {
					MaskUnit du = f.maxu();

					if (lim != null && lim.unusable(du, price))
						continue;

					List<Trait> ct = f.du.getTraits();
					int a = du.getAbi();
					boolean b0 = rare[u.rarity].isSelected();
					boolean b1 = !orop[0].isSelected();
					for (int i = 0; i < trait.length; i++)
						if (ct.size() > 0) {
							if (orop[0].isSelected())
								for (Trait diyt : ct) {
									b1 |= trlis.get(i).equals(diyt);
									if (b1)
										break;
								}
							else {
								b1 &= ct.contains(trlis.get(i));
								if (!b1)
									break;
							}
						} else b1 = false;
					boolean b2 = !orop[1].isSelected();
					for (int i = 0; i < abis.length; i++)
						if (abis[i].isSelected()) {
							boolean bind = ((a >> i) & 1) == 1;
							if (orop[1].isSelected())
								b2 |= bind;
							else
								b2 &= bind;
						}
					for (int i = 0; i < proc.length; i++)
						if (proc[i].isSelected())
							if (orop[1].isSelected())
								b2 |= du.getAllProc().getArr(UPROCIND[i]).exists();
							else
								b2 &= du.getAllProc().getArr(UPROCIND[i]).exists();
					boolean b3 = !orop[2].isSelected();
					for (int i = 0; i < atkt.length; i++)
						if (atkt[i].isSelected())
							if (orop[2].isSelected())
								b3 |= isType(du, i);
							else
								b3 &= isType(du, i);

					boolean b4;
					String fname = MultiLangCont.getStatic().FNAME.getCont(f);
					if (fname == null)
						fname = f.names.toString();
					int diff = UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase());
					minDiff = Math.min(minDiff, diff);
					b4 = diff == minDiff;

					boolean b5;

					if(pack == null)
						b5 = true;
					else {
						b5 = u.id.pack.equals(Identifier.DEF) || u.id.pack.equals(pack) || parents.contains(u.id.pack);
					}

					b0 = nonSele(rare) | b0;
					b1 = nonSele(trait) | b1;
					b2 = nonSele(abis) & nonSele(proc) | b2;
					b3 = nonSele(atkt) | b3;
					if (b0 & b1 & b2 & b3 & b4 & b5)
						ans.add(f);
				}
		}

		for (int i = 0; i < ans.size(); i++) {
			String fname = MultiLangCont.getStatic().FNAME.getCont(ans.get(i));
			if (fname == null)
				fname = ans.get(i).names.toString();
			if (UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase()) > minDiff) {
				ans.remove(i);
				i--;
			}
		}
		getFront().callBack(ans);
	}

	private void ini() {
		for (int i = 0; i < orop.length; i++)
			set(orop[i] = new JTG(0, "orop"));
		for (int i = 0; i < rare.length; i++)
			set(rare[i] = new JTG(RARITY[i]));
		for (int i = 0; i < trait.length; i++) {
			set(trait[i] = new JTG(TRAIT[i]));
			BufferedImage v = UtilPC.getIcon(3, i);
			if (v == null)
				continue;
			trait[i].setIcon(new ImageIcon(v));
		}
		FixIndexList.FixIndexMap<Trait> BCtraits = UserProfile.getBCData().traits;
		for (int i = 0 ; i < TRAIT_EVA ; i++)
			trlis.add(BCtraits.get(i));
		for (int i = 0; i < abis.length; i++) {
			set(abis[i] = new JTG(SABIS[i]));
			BufferedImage v = UtilPC.getIcon(0, i);
			if (v == null)
				continue;
			abis[i].setIcon(new ImageIcon(v));
		}
		for (int i = 0; i < proc.length; i++) {
			set(proc[i] = new JTG(ProcLang.get().get(UPROCIND[i]).abbr_name));
			BufferedImage v = UtilPC.getIcon(1, UPROCIND[i]);
			if (v == null)
				continue;
			proc[i].setIcon(new ImageIcon(v));
		}
		for (int i = 0; i < atkt.length; i++) {
			set(atkt[i] = new JTG(ATKCONF[i]));
			BufferedImage v = UtilPC.getIcon(2, i);
			if (v == null)
				continue;
			atkt[i].setIcon(new ImageIcon(v));
		}
	}

	private boolean nonSele(JTG[] jtbs) {
		int n = 0;
		for (JTG jtb : jtbs)
			if (jtb.isSelected())
				n++;
		return n == 0;
	}

	private void set(AbstractButton b) {
		add(b);
		b.addActionListener(arg0 -> confirm());
	}

}

class UFBList extends UnitFilterBox {

	private static final long serialVersionUID = 1L;

	private final JTG[] orop = new JTG[3];
	private final JList<String> rare = new JList<>(RARITY);
	private final Vector<String> va = new Vector<>();
	private final TraitList trait = new TraitList(false);
	private final AttList abis = new AttList(0, 0);
	private final AttList atkt = new AttList(2, 0);
	private final JScrollPane jr = new JScrollPane(rare);
	private final JScrollPane jt  = new JScrollPane(trait);
	private final JScrollPane jab = new JScrollPane(abis);
	private final JScrollPane jat = new JScrollPane(atkt);
	private final JTG limbtn = new JTG(0, "usable");
	private final JTG inccus = new JTG(MainLocale.PAGE, "inccus");

	protected UFBList(Page p, Limit lim, int price) {
		super(p, lim, price);

		ini();
		confirm();
	}

	protected UFBList(Page p, String pack, List<String> parents) {
		super(p, pack, parents);

		ini();
		confirm();
	}

	@Override
	public void callBack(Object o) {
		confirm();
	}

	@Override
	public int[] getSizer() {
		return new int[] { 450, 1150, 0, 500 };
	}

	@Override
	protected void resized(int x, int y) {
		set(orop[0], x, y, 0, 350, 200, 50);
		set(orop[1], x, y, 250, 0, 200, 50);
		set(orop[2], x, y, 0, 800, 200, 50);

		set(limbtn, x, y, 0, 300, 200, 50);
		set(inccus, x, y, 0, 0, 200, 50);
		set(jr, x, y, 0, 50, 200, 250);
		set(jt, x, y, 0, 400, 200, 350);
		set(jab, x, y, 250, 50, 200, 1100);
		set(jat, x, y, 0, 850, 200, 300);
	}

	private void confirm() {
		List<Form> ans = new ArrayList<>();
		int minDiff = 5;
		for(PackData p : UserProfile.getAllPacks()) {
			if(!inccus.isSelected() && !(p instanceof PackData.DefPack)) {
				continue;
			}

			for (Unit u : p.units.getList())
				for (Form f : u.forms) {
					MaskUnit du = f.maxu();
					int a = du.getAbi();

					if (limbtn.isSelected() && lim != null && lim.unusable(du, price))
						continue;

					List<Trait> traits = du.getTraits();
					boolean b0 = rare.isSelectedIndex(u.rarity);
					boolean b1 = !orop[0].isSelected();
					for (int i : trait.getSelectedIndices())
						if (traits.size() > 0) {
							if (orop[0].isSelected())
								for (Trait tr : traits) {
									b1 |= trait.list.get(i).equals(tr) || trait.list.get(i).others.contains(f);
									if (b1)
										break;
								}
							else {
								b1 &= traits.contains(trait.list.get(i)) || trait.list.get(i).others.contains(f);
								if (!b1)
									break;
							}
						} else b1 = false;
					boolean b2 = !orop[1].isSelected();
					int len = SABIS.length;
					for (int i : abis.getSelectedIndices())
						if (i < len) {
							boolean bind = ((a >> i) & 1) == 1;
							if (orop[1].isSelected())
								b2 |= bind;
							else
								b2 &= bind;
						} else if (orop[1].isSelected())
							b2 |= du.getAllProc().getArr(UPROCIND[i - len]).exists();
						else
							b2 &= du.getAllProc().getArr(UPROCIND[i - len]).exists();
					boolean b3 = !orop[2].isSelected();
					for (int i : atkt.getSelectedIndices())
						if (orop[2].isSelected())
							b3 |= isType(du, i);
						else
							b3 &= isType(du, i);

					boolean b4;
					String fname = MultiLangCont.getStatic().FNAME.getCont(f);
					if (fname == null)
						fname = f.names.toString();
					int diff = UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase());
					minDiff = Math.min(minDiff, diff);
					b4 = diff == minDiff;

					boolean b5;

					if(pack == null)
						b5 = true;
					else {
						b5 = u.id.pack.equals(Identifier.DEF) || u.id.pack.equals(pack) || parents.contains(u.id.pack);
					}

					b0 = rare.getSelectedIndex() == -1 | b0;
					b1 = trait.getSelectedIndex() == -1 | b1;
					b2 = abis.getSelectedIndex() == -1 | b2;
					b3 = atkt.getSelectedIndex() == -1 | b3;
					if (b0 & b1 & b2 & b3 & b4 & b5)
						ans.add(f);
				}
		}

		for (int i = 0; i < ans.size(); i++) {
			String fname = MultiLangCont.getStatic().FNAME.getCont(ans.get(i));
			if (fname == null)
				fname = ans.get(i).names.toString();
			if (UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase()) > minDiff) {
				ans.remove(i);
				i--;
			}
		}
		getFront().callBack(ans);
	}

	private void ini() {
		for (int i = 0; i < orop.length; i++)
			set(orop[i] = new JTG(get(0, "orop")));
		FixIndexList.FixIndexMap<Trait> BCtraits = UserProfile.getBCData().traits;
		for (int i = 0 ; i < TRAIT_EVA ; i++)
			trait.list.add(BCtraits.get(i));
		for (PackData.UserPack pacc : UserProfile.getUserPacks())
			for (Trait ctra : pacc.traits)
				if (pack == null || ctra.id.pack.equals(pack) || parents.contains(ctra.id.pack))
					trait.list.add(ctra);

		trait.setListData();
		Collections.addAll(va, SABIS);
		for (int i = 0; i < UPROCIND.length; i++)
			va.add(ProcLang.get().get(UPROCIND[i]).abbr_name);
		abis.setListData(va);
		atkt.setListData(ATKCONF);
		set(rare);
		set(trait);
		set(abis);
		set(atkt);
		add(jr);
		add(jt);
		add(jab);
		add(jat);
		set(inccus);

		inccus.setSelected(true);

		if (lim != null) {
			add(limbtn);
			limbtn.addActionListener(l -> confirm());
		}
	}

	private void set(AbstractButton b) {
		add(b);
		b.addActionListener(arg0 -> confirm());
	}

	private void set(JList<?> jl) {
		int m = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		jl.setSelectionMode(m);

		jl.addListSelectionListener(arg0 -> confirm());
	}
}
