package page.info.filter;

import common.battle.data.MaskUnit;
import common.battle.entity.Entity;
import common.pack.Identifier;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.lang.MultiLangCont;
import common.util.lang.ProcLang;
import common.util.stage.Limit;
import common.util.unit.Form;
import common.util.unit.Trait;
import common.util.unit.Unit;
import page.JTG;
import page.MainLocale;
import page.Page;
import utilpc.UtilPC;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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
	private final JTG inccus = new JTG(MainLocale.PAGE, "inccus"); //This button sucks, feel free to remove

	public UnitFilterBox(Page p, Limit limit, int price) {
		super(p);
		lim = limit;
		this.price = price;

		ini();
		confirm();
	}

	public UnitFilterBox(Page p, String pack, List<String> parent) {
		super(p, pack, parent);
		lim = null;
		price = 0;

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
		List<Form> ans = new ArrayList<>();
		minDiff = 5;
		for(PackData p : UserProfile.getAllPacks()) {
			if(!inccus.isSelected() && !(p instanceof PackData.DefPack)) {
				continue;
			}

			for (Unit u : p.units.getList())
				for (Form f : u.forms) {
					String fname = MultiLangCont.getStatic().FNAME.getCont(f);
					if (fname == null)
						fname = f.names.toString();
					int diff = UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase());
					if (diff > minDiff)
						continue;

					MaskUnit du = f.maxu();
					int a = du.getAbi();

					if (limbtn.isSelected() && lim != null && lim.unusable(du, price))
						continue;

					boolean b0 = rare.isSelectedIndex(u.rarity);
					boolean b1 = unchangeable(0);
					for (Trait t : trait.getSelectedValuesList()) {
						b1 = processOperator(0, checkTraitComp(du.getTraits(), t, f));
						if (b1 != unchangeable(0))
							break;
					}
					boolean b2 = unchangeable(1);
					int len = SABIS.length;
					for (int i : abis.getSelectedIndices()) {
						if (i < len)
							b2 = processOperator(1, ((a >> i) & 1) == 1);
						else
							b2 = processOperator(1, du.getAllProc().getArr(UPROCIND[i - len]).exists());
						if (b2 != unchangeable(1))
							break;
					}
					boolean b3 = unchangeable(2);
					for (int i : atkt.getSelectedIndices()) {
						b3 = processOperator(2, isType(du, i));
						if (b1 != unchangeable(2))
							break;
					}

					b0 |= rare.getSelectedIndex() == -1;
					b1 |= trait.getSelectedIndex() == -1;
					b2 |= abis.getSelectedIndex() == -1;
					b3 |= atkt.getSelectedIndex() == -1;
					if (!b0 | !b1 | !b2 | !b3)
						continue; //Return early to not affect minDiff

					minDiff = diff;
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

	private boolean checkTraitComp(ArrayList<Trait> targets, Trait t, Form f) {
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
