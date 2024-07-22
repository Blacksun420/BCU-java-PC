package page.info.filter;

import common.CommonStatic;
import common.battle.data.MaskUnit;
import common.pack.*;
import common.util.lang.MultiLangCont;
import common.util.lang.ProcLang;
import common.util.stage.Limit;
import common.util.unit.*;
import common.util.unit.rand.UREnt;
import main.MainBCU;
import page.JTG;
import page.MainLocale;
import page.Page;
import page.basis.UnitFLUPage;
import utilpc.UtilPC;

import javax.swing.*;
import java.util.*;

import static utilpc.Interpret.*;

public class UnitFilterBox extends EntityFilterBox {

	private static final long serialVersionUID = 1L;

	protected final Limit lim;
	protected final int price;

	protected final JList<String> rare = new JList<>(Page.get(MainLocale.UTIL, "r", 7));
	private final AttList abis = new AttList(0, 0);
	private final JScrollPane jr = new JScrollPane(rare);
	private final JScrollPane jab = new JScrollPane(abis);
	private final JTG limbtn = new JTG(0, "usable");
	private final boolean hideSpirits;
	private final SaveData sdat;
	private final Set<AbForm> unlk;
	public byte frmf = -1;
	public boolean rf = false;

	public UnitFilterBox(Page p, boolean rand, Limit limit, int price, SaveData sdat, Set<AbForm> ulk) {
		super(p, sdat == null ? null : sdat.pack, rand && sdat == null);
		lim = limit;
		this.price = price;
		this.sdat = sdat;
		unlk = ulk;
		hideSpirits = true;

		ini();
		confirm();
	}

	public UnitFilterBox(Page p, boolean rand, PackData.UserPack pack) {
		super(p, pack, rand);
		lim = null;
		price = 1;
		sdat = null;
		unlk = null;
		hideSpirits = false;

		ini();
		confirm();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);

		set(limbtn, x, y, 0, 300, 200, 50);
		if (multipacks) {
			set(pks, x, y, 0, 0, 200, 50);
			set(jr, x, y, 0, 50, 200, 250);
		} else {
			set(pks, x, y, 0, 0, 0, 0);
			set(jr, x, y, 0, 0, 200, 300);
		}
		set(jab, x, y, 250, 50, 200, 1100);
	}

	@Override
	protected void confirm() {
		List<AbForm> ans = new ArrayList<>();
		minDiff = MainBCU.searchTolerance;
		for(PackData p : UserProfile.getAllPacks()) {
			if(pks.getSelectedItem() != null && !(p.equals(pks.getSelectedItem())) || !validatePack(p))
				continue;
			for (Unit u : p.units.getList())
				if (validateUnit(u))
                    if (frmf == -1) {
                        for (Form f : u.forms)
                            if (validateForm(f))
                                ans.add(f);
                    } else {
						if (rf && u.forms.length <= frmf)
							continue;
                        Form f = u.forms[Math.min(u.forms.length - 1, frmf)];
                        if (validateForm(f))
                            ans.add(f);
                    }
			if (rand)
				for(UniRand rand : p.randUnits.getList()) {
					int diff = UtilPC.damerauLevenshteinDistance(rand.name.toLowerCase(), name.toLowerCase());
					if(diff <= minDiff)
						for (UREnt en : rand.list)
							if (ans.contains(en.ent)) {
								ans.add(rand);
								minDiff = diff;
								break;
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
		for (int j : UPROCIND)
			va.add(proclang.get(j).abbr_name);
		abis.setListData(va);
		abis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		set(rare);
		set(abis);
		add(jr);
		add(jab);
		Vector<PackData> pkv = new Vector<>(UserProfile.getAllPacks().size() + 1);
		pkv.add(null);
		if (pack == null) {
			for (PackData p : UserProfile.getAllPacks())
				if (p.units.size() > 0 || (rand && p.randUnits.size() > 0))
					pkv.add(p);
		} else {
			pkv.add(UserProfile.getBCData());
			if (pack.units.size() > 0 || (rand && pack.randUnits.size() > 0))
				pkv.add(pack);
			for (String s : pack.desc.dependency) {
				PackData p = UserProfile.getUserPack(s);
				if (p.units.size() > 0 || (rand && p.randUnits.size() > 0))
					pkv.add(UserProfile.getUserPack(s));
			}
		}
		multipacks = pkv.size() > 2;
		pks.setModel(new DefaultComboBoxModel<>(pkv));

		if (lim != null) {
			add(limbtn);
			limbtn.addActionListener(l -> confirm());
		}
		postIni();
	}

	protected boolean validateUnit(Unit u) {
		return (rare.getSelectedIndex() == -1 || rare.isSelectedIndex(u.rarity) || (rare.getSelectedIndices().length == 1 && rare.isSelectedIndex(rare.getModel().getSize() - 1)))
				&& (sdat == null || pack.defULK.containsKey(u) || sdat.ulkUni.containsKey(u));
	}

	protected boolean validateForm(Form f) {
		if (rare.isSelectedIndex(rare.getModel().getSize() - 1) && !CommonStatic.getFaves().units.contains(f))
			return false;
		if ((sdat != null && sdat.locked(f)) || (hideSpirits && f.anim.getAtkCount() == 0) || (unlk != null && !unlk.contains(f)))
			return false;

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
			b1 = processOperator(1, i < len ? ((a >> i) & 1) == 1 : du.getAllProc().getArr(UPROCIND[i - len]).exists());
			if (b1 != unchangeable(1))
				break;
		}
		if (b1 == unchangeable(1)) {
			AdvProcFilterPage adv = getFront() instanceof UnitFindPage ? ((UnitFindPage)getFront()).adv : ((UnitFLUPage)getFront()).adv;
			if (adv != null && !adv.isBlank) {
				b1 = processOperator(1, adv.compare(du.getAllProc()));
				if (b1 != unchangeable(1) && !b1)
					return false;
			}
		}

		boolean b2 = unchangeable(2);
		for (int i : atkt.getSelectedIndices()) {
			b2 = processOperator(2, isType(du, i));
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
		if (t.BCTrait())
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