package page.info.filter;

import common.battle.data.MaskUnit;
import common.pack.PackData;
import common.pack.SortedPackSet;
import common.pack.UserProfile;
import common.util.lang.MultiLangCont;
import common.util.lang.ProcLang;
import common.util.stage.Limit;
import common.util.unit.*;
import common.util.unit.rand.UREnt;
import page.JTG;
import page.MainLocale;
import page.Page;
import utilpc.UtilPC;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utilpc.Interpret.*;

public class UnitFilterBox extends EntityFilterBox {

	private static final long serialVersionUID = 1L;

	protected final Limit lim;
	protected final int price;

	private final JList<String> rare = new JList<>(RARITY);
	private final AttList abis = new AttList(0, 0);
	private final JScrollPane jr = new JScrollPane(rare);
	private final JScrollPane jab = new JScrollPane(abis);
	private final JTG limbtn = new JTG(0, "usable");
	protected final JTG inccus = new JTG(MainLocale.PAGE, "inccus"); //This button sucks, feel free to remove
	private final boolean rand;

	public UnitFilterBox(Page p, boolean rand, Limit limit, int price) {
		super(p);
		lim = limit;
		this.price = price;
		this.rand = rand;

		ini();
		confirm();
	}

	public UnitFilterBox(Page p, boolean rand, PackData.UserPack pack) {
		super(p, pack);
		lim = null;
		price = 0;
		this.rand = rand;

		ini();
		confirm();
	}

	@Override
	public int[] getSizer() {
		return new int[] { 450, 1150, 0, 500 };
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);

		set(limbtn, x, y, 0, 300, 200, 50);
		set(inccus, x, y, 0, 0, 200, 50);
		set(jr, x, y, 0, 50, 200, 250);
		set(jab, x, y, 250, 50, 200, 1100);
	}

	@Override
	protected void confirm() {
		List<AbForm> ans = new ArrayList<>();
		minDiff = 5;
		for(PackData p : UserProfile.getAllPacks()) {
			if(!inccus.isSelected() && !(p instanceof PackData.DefPack) || !validatePack(p))
				continue;
			for (Unit u : p.units.getList())
				if (validateUnit(u))
					for (Form f : u.forms)
						if (validateForm(f))
							ans.add(f);
			if (rand)
				for(UniRand rand : p.randUnits.getList()) {
					int diff = UtilPC.damerauLevenshteinDistance(rand.name.toLowerCase(), name.toLowerCase());
					if(diff <= minDiff)
						for (UREnt en : rand.list)
							if (ans.contains(en.ent)) {
								ans.add(rand);
								minDiff = diff;
							}
				}
		}

		for (int i = 0; i < ans.size(); i++)
			if (ans.get(i) instanceof Form) {
				Form f = (Form) ans.get(i);
				String fname = MultiLangCont.getStatic().FNAME.getCont(f);
				if (fname == null)
					fname = f.names.toString();
				if (UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase()) > minDiff)
					ans.remove(i--);
			} else if (UtilPC.damerauLevenshteinDistance(((UniRand) ans.get(i)).name.toLowerCase(), name.toLowerCase()) > minDiff)
				ans.remove(i--);

		getFront().callBack(ans);
	}

	@Override
	protected void ini() {
		super.ini();

		Collections.addAll(va, SABIS);
		ProcLang proclang = ProcLang.get();
		for (int i = 0; i < UPROCIND.length; i++)
			va.add(proclang.get(UPROCIND[i]).abbr_name);
		abis.setListData(va);
		abis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		set(rare);
		set(abis);
		add(jr);
		add(jab);
		set(inccus);
		inccus.setSelected(true);

		if (lim != null) {
			add(limbtn);
			limbtn.addActionListener(l -> confirm());
		}
	}

	protected boolean validateUnit(Unit u) {
		return rare.getSelectedIndex() == -1 || rare.isSelectedIndex(u.rarity);
	}

	protected boolean validateForm(Form f) {
		String fname = MultiLangCont.getStatic().FNAME.getCont(f);
		if (fname == null)
			fname = f.names.toString();
		int diff = UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase());
		if (diff > minDiff)
			return false;

		MaskUnit du = f.maxu();
		int a = du.getAbi();

		if (limbtn.isSelected() && lim != null && lim.unusable(du, price))
			return false;

		boolean b0 = unchangeable(0);
		for (Trait t : trait.getSelectedValuesList()) {
			b0 = processOperator(0, checkTraitComp(du.getTraits(), t, f));
			if (b0 != unchangeable(0))
				break;
		}
		boolean b1 = unchangeable(1);
		int len = SABIS.length;
		for (int i : abis.getSelectedIndices()) {
			if (i < len)
				b1 = processOperator(1, ((a >> i) & 1) == 1);
			else
				b1 = processOperator(1, du.getAllProc().getArr(UPROCIND[i - len]).exists());
			if (b1 != unchangeable(1))
				break;
		}
		boolean b2 = unchangeable(2);
		for (int i : atkt.getSelectedIndices()) {
			b2 = processOperator(2, isType(du, i, 0));
			if (b2 != unchangeable(2))
				break;
		}

		b0 |= trait.getSelectedIndex() == -1;
		b1 |= abis.getSelectedIndex() == -1;
		b2 |= atkt.getSelectedIndex() == -1;
		if (!b0 | !b1 | !b2)
			return false; //Return early to not affect minDiff

		minDiff = diff;
		return true;
	}

	private boolean checkTraitComp(SortedPackSet<Trait> targets, Trait t, Form f) {
		if (targets.contains(t))
			return true;
		if (t.BCTrait)
			return false;
		if (t.others.contains(f))
			return true;
		if (!t.targetType)
			return false;
		List<Trait> temp = UserProfile.getBCData().traits.getList().subList(TRAIT_RED,TRAIT_WHITE);
		temp.remove(TRAIT_METAL);
		return targets.containsAll(temp);
	}
}
