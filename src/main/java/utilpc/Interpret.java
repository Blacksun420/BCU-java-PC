package utilpc;

import common.CommonStatic;
import common.battle.BasisLU;
import common.battle.BasisSet;
import common.battle.Treasure;
import common.battle.data.*;
import common.pack.SortedPackSet;
import common.pack.UserProfile;
import common.system.P;
import common.util.Data;
import common.util.Data.Proc.ProcItem;
import common.util.lang.Formatter;
import common.util.lang.ProcLang;
import common.util.stage.MapColc;
import common.util.stage.MapColc.DefMapColc;
import common.util.unit.Combo;
import common.util.unit.Enemy;
import common.util.unit.Trait;
import main.MainBCU;
import page.MainLocale;
import page.Page;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class Interpret extends Data {

	/**
	 * enemy types
	 */
	public static String[] ERARE;

	/**
	 * unit rarities
	 */
	public static String[] RARITY;

	/**
	 * enemy traits
	 */
	public static String[] TRAIT;

	/**
	 * star names
	 */
	public static String[] STAR;

	/**
	 * ability name
	 */
	public static String[] ABIS;

	/**
	 * enemy ability name
	 */
	public static String[] EABI;

	public static String[] SABIS;
	public static String[] TREA;
	public static String[] ATKCONF;
	public static String[] COMF;
	public static String[] COMN;
	public static String[] TCTX;
	public static String[] PCTX;
	public static String[] CCTX;

	/**
	 * treasure orderer
	 */
	public static final int[] TIND = { 0, 1, 18, 19, 20, 21, 22, 23, 2, 3, 4, 5, 24, 25, 26, 27, 28, 6, 7, 8, 9, 10, 11,
			12, 13, 14, 15, 16, 17, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50 };

	/**
	 * treasure grouper
	 */
	public static final int[][] TCOLP = { { 0, 8 }, { 8, 6 }, { 14, 3 }, { 17, 4 }, { 21, 3 }, { 29, 22 } };

	/**
	 * treasure max
	 */
	private static final int[] TMAX = { 30, 30, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 300, 600, 1500, 100,
			100, 100, 30, 30, 30, 30, 30, 10, 300, 300, 600, 600, 600, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0 };

	/**
	 * combo string component
	 */
	private static final String[][] CDP = { { "", "+", "-" }, { "_", "_%", "_f", "Lv._" } };

	/**
	 * combo string formatter
	 */
	private static final byte[][] CDC = { { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 3 }, { 1, 0 }, { 1, 1 }, { 2, 2 },
			{ 1, 1 }, { 1, 1 }, { 1, 1 }, { 2, 2 }, { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 },
			{ 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 }, { 1, 1 } };

	//Filters abilities and procs that are available for enemies. Also gives better organization to the UI
	public static final byte[] EABIIND = { ABI_ONLY, ABI_SNIPERI, ABI_TIMEI, ABI_GHOST, ABI_GLASS, ABI_THEMEI };
	public static final byte[] EPROCIND = { Data.P_DMGINC, Data.P_DEFINC, Data.P_KB, Data.P_STOP, Data.P_SLOW, Data.P_WEAK, Data.P_LETHARGY, Data.P_BOUNTY, Data.P_CRIT, Data.P_WAVE,
			Data.P_WORKERLV, Data.P_CDSETTER, Data.P_MINIWAVE, Data.P_VOLC, Data.P_MINIVOLC, Data.P_DEMONVOLC, Data.P_BARRIER, Data.P_DEMONSHIELD, Data.P_BREAK, Data.P_SHIELDBREAK,
			Data.P_WARP, Data.P_CURSE, Data.P_SEAL, Data.P_SATK, Data.P_POIATK, Data.P_ATKBASE, Data.P_SUMMON, Data.P_MOVEWAVE, Data.P_SNIPER, Data.P_BOSS, Data.P_TIME, Data.P_THEME,
			Data.P_POISON, Data.P_ARMOR, Data.P_SPEED, Data.P_RAGE, Data.P_HYPNO, Data.P_STRONG, Data.P_LETHAL, Data.P_BURROW, Data.P_REVIVE, Data.P_COUNTER, Data.P_IMUATK, Data.P_DMGCUT,
			Data.P_DMGCAP, Data.P_REMOTESHIELD, Data.P_IMUKB, Data.P_IMUSTOP, Data.P_IMUSLOW, Data.P_IMUWAVE, Data.P_IMUVOLC, Data.P_IMUWEAK, Data.P_IMULETHARGY, Data.P_IMUWARP, Data.P_IMUCURSE,
			Data.P_IMUSEAL, Data.P_IMUMOVING, Data.P_IMUPOI, Data.P_IMUPOIATK, Data.P_CRITI, Data.P_IMUARMOR, Data.P_IMUSPEED, Data.P_IMUSUMMON, Data.P_IMURAGE, Data.P_IMUHYPNO,
			Data.P_IMUCANNON, Data.P_DEATHSURGE, Data.P_WEAKAURA, Data.P_STRONGAURA, Data.P_AI};
	//Filters abilities and procs that are available for units. Also gives better organization to the UI
	public static final byte[] UPROCIND = { Data.P_BSTHUNT, Data.P_DMGINC, Data.P_DEFINC, Data.P_KB, Data.P_STOP, Data.P_SLOW, Data.P_WEAK, Data.P_LETHARGY, Data.P_BOUNTY, Data.P_CRIT,
			Data.P_WAVE, Data.P_WORKERLV, Data.P_CDSETTER, Data.P_MINIWAVE, Data.P_VOLC, Data.P_MINIVOLC, Data.P_DEMONVOLC, Data.P_BARRIER, Data.P_DEMONSHIELD, Data.P_BREAK, Data.P_SHIELDBREAK,
			Data.P_WARP, Data.P_CURSE, Data.P_SEAL, Data.P_SATK, Data.P_POIATK, Data.P_ATKBASE, Data.P_SUMMON, Data.P_MOVEWAVE, Data.P_SNIPER, Data.P_BOSS, Data.P_TIME, Data.P_THEME,
			Data.P_POISON, Data.P_ARMOR, Data.P_SPEED, Data.P_RAGE, Data.P_HYPNO, Data.P_STRONG, Data.P_LETHAL, Data.P_BURROW, Data.P_REVIVE, Data.P_CRITI, Data.P_COUNTER, Data.P_IMUATK,
			Data.P_DMGCUT, Data.P_DMGCAP, Data.P_REMOTESHIELD, Data.P_IMUKB, Data.P_IMUSTOP, Data.P_IMUSLOW, Data.P_IMUWAVE, Data.P_IMUVOLC, Data.P_IMUWEAK, Data.P_IMULETHARGY,
			Data.P_IMUWARP, Data.P_IMUCURSE, Data.P_IMUSEAL, Data.P_IMUMOVING, Data.P_IMUPOI, Data.P_IMUPOIATK, Data.P_IMUARMOR, Data.P_IMUSPEED, Data.P_IMUSUMMON, Data.P_IMURAGE, Data.P_IMUHYPNO,
			Data.P_DEATHSURGE, Data.P_WEAKAURA, Data.P_STRONGAURA, Data.P_AI};

	private static final DecimalFormat df;

	static {
		redefine();

		NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
		df = (DecimalFormat) nf;
	}

	public static boolean allRangeSame(MaskEntity me, int ind) {
		if (me instanceof CustomEntity) {
			List<Integer> near = new ArrayList<>();
			List<Integer> far = new ArrayList<>();

			for (AtkDataModel atk : ((CustomEntity) me).hits.get(ind)) {
				near.add(atk.getShortPoint());
				far.add(atk.getLongPoint());
			}

			if (near.isEmpty())
				return true;

			for (int n : near)
				if (n != near.get(0))
					return false;
			for (int f : far)
				if (f != far.get(0))
					return false;
		} else {
			for (int i = 1; i < me.getAtkCount(ind); i++)
				if (me.getAtkModel(ind, i).getShortPoint() != me.getAtkModel(ind, 0).getShortPoint() || me.getAtkModel(ind, i).getLongPoint() != me.getAtkModel(ind, 0).getLongPoint())
					return false;
		}
		return true;
	}

	public static void loadCannonMax() {
		for (int i = 1; i <= Treasure.curveData.size(); i++)
			TMAX[29 + i] = Treasure.curveData.get(i).max;
		for (int i = 1; i <= Treasure.baseData.size(); i++)
			TMAX[36 + i] = Treasure.baseData.get(i).max;
		for (int i = 1; i <= Treasure.decorationData.size(); i++)
			TMAX[43 + i] = Treasure.decorationData.get(i).max;
	}

	public static String comboInfo(Combo c, BasisSet b) {
		return combo(c.type, CommonStatic.getBCAssets().values[c.type][c.lv], b);
	}

	public static class ProcDisplay {
		private final String text;
		public final ImageIcon icon;
		public final ProcItem item;

		public ProcDisplay(String desc, ImageIcon img) {
			this(desc, img, null);
		}

		public ProcDisplay(String desc, ImageIcon img, ProcItem proc) {
			text = desc;
			icon = img;
			item = proc;
		}

		@Override
		public String toString() { return text; }
	}

	public static List<ProcDisplay> getAbi(MaskEntity me, int atkind) {
		int tb = me.touchBase();
		final MaskAtk ma;

		if (me.getAtkCount(atkind) == 1) {
			ma = me.getAtkModel(atkind,0);
		} else {
			ma = me.getRepAtk();
		}

		int lds;
		int ldr;

		if (allRangeSame(me, atkind)) {
			lds = me.getAtkModel(atkind, 0).getShortPoint();
			ldr = me.getAtkModel(atkind, 0).getLongPoint() - me.getAtkModel(atkind, 0).getShortPoint();
		} else {
			lds = ma.getShortPoint();
			ldr = ma.getLongPoint() - ma.getShortPoint();
		}


		List<ProcDisplay> l = new ArrayList<>();
		if (!allRangeSame(me, atkind)) {
			LinkedHashMap<String, List<Integer>> LDInts = new LinkedHashMap<>();
			MaskAtk[] atks = me.getAtks(atkind);
			List<ImageIcon> ics = new ArrayList<>();

			for (int i = 0; i < atks.length ; i++ ) {
				int rs = atks[i].getShortPoint();
				int rl = atks[i].getLongPoint();
				if (rs == 0 && rl == 0)
					continue;
				String LDData = Page.get(MainLocale.UTIL, "ld0") + ": " + tb + ", " + Page.get(MainLocale.UTIL, "ld1")
						+ ": " + rs + "~" + rl + ", " + Page.get(MainLocale.UTIL, "ld2") + ": " + Math.abs(rl - rs);
				if (LDInts.containsKey(LDData)) {
					List<Integer> li = LDInts.get(LDData);
					li.add(i + 1);
				} else {
					List<Integer> li = new ArrayList<>();
					li.add(i + 1);

					LDInts.put(LDData, li);
				}
				if (atks[i].isOmni())
					ics.add(UtilPC.getIcon(2, ATK_OMNI));
				else
					ics.add(UtilPC.getIcon(2, ATK_LD));
			}

			int i = 0;
			for (String key : LDInts.keySet()) {
				List<Integer> inds = LDInts.get(key);
				if (inds == null) {
					l.add(new ProcDisplay(key, ics.get(i++)));
				} else {
					if (inds.size() == me.getAtkCount(atkind)) {
						l.add(new ProcDisplay(key, ics.get(i++)));
					} else {
						l.add(new ProcDisplay(key + " " + getAtkNumbers(inds), ics.get(i++)));
					}
				}
			}
		} else if (lds != 0 || ldr != 0) {
			int p0 = Math.min(lds, lds + ldr);
			int p1 = Math.max(lds, lds + ldr);
			int r = Math.abs(ldr);
			ImageIcon bi;
			if (me.isOmni()) {
				bi = UtilPC.getIcon(2, ATK_OMNI);
			} else {
				bi = UtilPC.getIcon(2, ATK_LD);
			}
			l.add(new ProcDisplay(Page.get(MainLocale.UTIL, "ld0") + ": " + tb + ", " + Page.get(MainLocale.UTIL, "ld1") + ": " + p0 + "~" + p1 + ", "
					+ Page.get(MainLocale.UTIL, "ld2") + ": " + r, bi));
		}

		AtkDataModel[][] satks = me.getSpAtks(true);
		for (int z = 0; z < satks.length; z++)
			for (int j  = 0; j < satks[z].length; j++) {
				AtkDataModel rev = satks[z][j];
				if (rev != null) {
					int revs = rev.getShortPoint();
					int revl = rev.getLongPoint();
					if (revs != 0 || revl != 0) {
						ImageIcon bi;
						if (rev.isOmni())
							bi = (UtilPC.getIcon(2, ATK_OMNI));
						else
							bi = (UtilPC.getIcon(2, ATK_LD));
						l.add(new ProcDisplay(Page.get(MainLocale.UTIL, "ld1") + ": " + revs + "~" + revl +
								", " + Page.get(MainLocale.UTIL, "ld2") + ": " + Math.abs(revl - revs) +
								" [" + Page.get(MainLocale.UTIL, "aa" + (z + (me.getCounter() == null && z >= 2 ? 7 : 6))) + " " + j + "]", bi));
					}
				}
		}
		for (int i = 0; i < ABIS.length; i++)
			if (((me.getAbi() >> i) & 1) > 0)
				l.add(new ProcDisplay(ABIS[i], UtilPC.getIcon(0, i)));
		return l;
	}

	public static String[] getComboFilter(int n) {
		int[] res = CommonStatic.getBCAssets().filter[n];
		String[] strs = new String[res.length];
		for (int i = 0; i < res.length; i++)
			strs[i] = COMN[res[i]];
		return strs;
	}

	public static int getComp(int ind, Treasure t) {
		int ans = -2;
		for (int i = 0; i < TCOLP[ind][1]; i++) {
			int temp = getValue(TIND[i + TCOLP[ind][0]], t);
			if (ans == -2)
				ans = temp;
			else if (ans != temp)
				return -1;
		}
		return ans;
	}

	public static List<ProcDisplay> getProc(MaskEntity du, boolean isEnemy, double[] magnification, int atkind) {
		Formatter.Context ctx = new Formatter.Context(isEnemy, MainBCU.seconds, magnification, du.getTraits());

		ArrayList<ProcDisplay> l = new ArrayList<>();

		if(du.isCommon()) {
			Proc proc = du.getRepAtk().getProc();

			for(int i = 0; i < Data.PROC_TOT; i++) {
				ProcItem item = proc.getArr(i);

				if(!item.exists())
					continue;
				String format = ProcLang.get().get(i).format;
				String formatted = Formatter.format(format, item, ctx);

				l.add(new ProcDisplay(formatted, UtilPC.getIcon(item,1, i), item));
			}
		} else {
			if (du instanceof DefaultData) {
				MaskAtk[] atkData = du.getAtks(0);

				List<Integer> atks = new ArrayList<>(3);
				for (int i = 0; i < atkData.length; i++)
					if (atkData[i].canProc())
						atks.add(i + 1);

				Proc proc = du.getProc();
				String nums = getAtkNumbers(atks);
				for(int i = 0; i < Data.PROC_TOT; i++) {
					ProcItem item = proc.getArr(i);

					if (!item.exists())
						continue;
					boolean p = false;
					if (proc.sharable(i))
						p = true;
					else
						for (int pr : BCShareable)
							if (pr == i) {
								p = true;
								break;
							}
					String format = ProcLang.get().get(i).format;
					String formatted = Formatter.format(format, item, ctx);
					if (!p)
						formatted += " " + nums;

					l.add(new ProcDisplay(formatted, UtilPC.getIcon(item,1, i), item));
				}
			} else {
				LinkedHashMap<String, List<Integer>> atkMap = new LinkedHashMap<>();
				ArrayList<ImageIcon> procIcons = new ArrayList<>();
				ArrayList<ProcItem> procItems = new ArrayList<>();

				MaskAtk ma = du.getRepAtk();

				for (int i = 0; i < Data.PROC_TOT; i++) {
					ProcItem item = ma.getProc().getArr(i);

					if (!item.exists() || !ma.getProc().sharable(i))
						continue;

					String format = ProcLang.get().get(i).format;
					String formatted = Formatter.format(format, item, ctx);
					l.add(new ProcDisplay(formatted, UtilPC.getIcon(item, 1, i), item));
				}

				for (int i = 0; i < du.getAtkCount(atkind); i++) {
					ma = du.getAtkModel(atkind, i);

					for (int j = 0; j < Data.PROC_TOT; j++) {
						ProcItem item = ma.getProc().getArr(j);

						if (!item.exists() || ma.getProc().sharable(j))
							continue;

						String format = ProcLang.get().get(j).format;
						String formatted = Formatter.format(format, item, ctx);

						if (atkMap.containsKey(formatted)) {
							List<Integer> inds = atkMap.get(formatted);

							inds.add(i + 1);
						} else {
							List<Integer> inds = new ArrayList<>();

							inds.add(i + 1);

							atkMap.put(formatted, inds);
							procIcons.add(UtilPC.getIcon(1, j));
							procItems.add(item);
						}
					}
				}

				int i = 0;
				for (String key : atkMap.keySet()) {
					List<Integer> inds = atkMap.get(key);

					if (inds == null || inds.size() == du.getAtkCount(atkind))
						l.add(new ProcDisplay(key, procIcons.get(i), procItems.get(i++)));
					else
						l.add(new ProcDisplay(key + " " + getAtkNumbers(inds), procIcons.get(i), procItems.get(i++)));
				}
			}
		}

		if (!du.isCommon()) {
			AtkDataModel[][] sps = du.getSpAtks(true);
			for (int i = 0; i < sps.length; i++)
				for (int j = 0; j < sps[i].length; j++) {
					AtkDataModel rev = sps[i][j];
					if (rev != null)
						for (int k = 0; k < Data.PROC_TOT; k++) {
							ProcItem item = rev.getProc().getArr(k);
							if (!item.exists() || rev.getProc().sharable(k))
								continue;

							String format = ProcLang.get().get(k).format;
							String formatted = Formatter.format(format, item, ctx);
							l.add(new ProcDisplay(formatted + " [" + Page.get(MainLocale.UTIL, "aa" + ((du.getCounter() == null && i >= 2 ? 7 : 6) + i)) + " #" + j + "]", UtilPC.getIcon(1, k), item));
						}
				}
		}

		return l;
	}

	public static String[] getTrait(SortedPackSet<Trait> trs) {
		String[] TraitBox = new String[trs.size()];
		for (int i = 0; i < TraitBox.length; i++) {
			Trait trait = trs.get(i);
			if (trait.BCTrait())
				TraitBox[i] = Interpret.TRAIT[trait.id.id];
			else
				TraitBox[i] = trait.name;
		}
		return TraitBox;
	}

	public static String getTrait(String[] cTraits, int star) {
		StringBuilder ans = new StringBuilder();
		for (String cTrait : cTraits) ans.append(cTrait).append(", ");
		if (star > 0)
			ans.append(STAR[star]);

		String res = ans.toString();
		if(res.endsWith(", "))
			res = res.substring(0, res.length() - 2);

		return res;
	}

	public static int getValue(int ind, Treasure t) {
		switch (ind) {
			case 0:
				return t.tech[LV_RES];
			case 1:
				return t.tech[LV_ACC];
			case 2:
				return t.trea[T_ATK];
			case 3:
				return t.trea[T_DEF];
			case 4:
				return t.trea[T_RES];
			case 5:
				return t.trea[T_ACC];
			case 6:
				return t.fruit[T_RED];
			case 7:
				return t.fruit[T_FLOAT];
			case 8:
				return t.fruit[T_BLACK];
			case 9:
				return t.fruit[T_ANGEL];
			case 10:
				return t.fruit[T_METAL];
			case 11:
				return t.fruit[T_ZOMBIE];
			case 12:
				return t.fruit[T_ALIEN];
			case 13:
				return t.alien;
			case 14:
				return t.star;
			case 15:
				return t.gods[0];
			case 16:
				return t.gods[1];
			case 17:
				return t.gods[2];
			case 18:
				return t.tech[LV_BASE];
			case 19:
				return t.tech[LV_WORK];
			case 20:
				return t.tech[LV_WALT];
			case 21:
				return t.tech[LV_RECH];
			case 22:
				return t.tech[LV_CATK];
			case 23:
				return t.tech[LV_CRG];
			case 24:
				return t.trea[T_WORK];
			case 25:
				return t.trea[T_WALT];
			case 26:
				return t.trea[T_RECH];
			case 27:
				return t.trea[T_CATK];
			case 28:
				return t.trea[T_BASE];
			case 29:
				return t.bslv[BASE_H];
			case 30:
				return t.bslv[BASE_SLOW];
			case 31:
				return t.bslv[BASE_WALL];
			case 32:
				return t.bslv[BASE_STOP];
			case 33:
				return t.bslv[BASE_WATER];
			case 34:
				return t.bslv[BASE_GROUND];
			case 35:
				return t.bslv[BASE_BARRIER];
			case 36:
				return t.bslv[BASE_CURSE];
			case 37:
				return t.base[DECO_BASE_SLOW - 1];
			case 38:
				return t.base[DECO_BASE_WALL - 1];
			case 39:
				return t.base[DECO_BASE_STOP - 1];
			case 40:
				return t.base[DECO_BASE_WATER - 1];
			case 41:
				return t.base[DECO_BASE_GROUND - 1];
			case 42:
				return t.base[DECO_BASE_BARRIER - 1];
			case 43:
				return t.base[DECO_BASE_CURSE - 1];
			case 44:
				return t.deco[DECO_BASE_SLOW - 1];
			case 45:
				return t.deco[DECO_BASE_WALL - 1];
			case 46:
				return t.deco[DECO_BASE_STOP - 1];
			case 47:
				return t.deco[DECO_BASE_WATER - 1];
			case 48:
				return t.deco[DECO_BASE_GROUND - 1];
			case 49:
				return t.deco[DECO_BASE_BARRIER - 1];
			case 50:
				return t.deco[DECO_BASE_CURSE - 1];
			default:
				return -1;
		}
	}

	public static boolean isER(Enemy e, int t) {
		if (t == 0)
			return e.getExplanation().replace("\n","").length() > 0; //e.inDic;
		else if (t == 1)
			return e.de.getStar() == 1;
		else if (t == 4)
			return CommonStatic.getFaves().enemies.contains(e);

		if (e.de instanceof DataEnemy) {
			Map<MapColc.DefMapColc, Integer> lis = e.findMap();
			final int recurring = lis.getOrDefault(DefMapColc.getMap("N"), 0) + lis.getOrDefault(DefMapColc.getMap("A"), 0)
					+ lis.getOrDefault(DefMapColc.getMap("Q"), 0) + lis.getOrDefault(DefMapColc.getMap("ND"), 0);
			if (t == 3)
				return recurring > 3;
			return recurring == 0 && (lis.containsKey(DefMapColc.getMap("C")) || lis.containsKey(DefMapColc.getMap("R")) || lis.containsKey(DefMapColc.getMap("CH"))
					|| lis.containsKey(DefMapColc.getMap("CA")));
		} else if (t == 3)
			return e.findApp(UserProfile.getUserPack(e.id.pack).mc).size() > 3;
		return false;
	}

	public static boolean isType(MaskEntity de, int type) {
		int ind = de.firstAtk();
		MaskAtk[] atks = de.getAtks(ind);
		if (type == 0)
			return !de.isRange(ind);
		else if (type == 1)
			return de.isRange(ind);
		else if (type == 2)
			return de.isLD();
		else if (type == 3)
			return atks.length > 1;
		else if (type == 4)
			return de.isOmni();
		else if (type == 5)
			return de.getTBA() + (de.getAnimLen(ind) - de.getPost(false, ind)) < de.getPost(false, ind);
		else if (type >= 6 && type <= 11) {
			if (type == 8)
				return de.getCounter() != null;
			if (type < 8 || de.getCounter() != null)
				return de.getSpAtks(true, type - 6).length != 0;
			return de.getSpAtks(true, type - 7).length != 0;
		}
		return false;
	}

	public static void redefine() {
		ERARE = Page.get(MainLocale.UTIL, "er", 5);
		RARITY = Page.get(MainLocale.UTIL, "r", 6);
		TRAIT = Page.get(MainLocale.UTIL, "c", TRAIT_TOT);
		STAR = Page.get(MainLocale.UTIL, "s", 5);
		ABIS = Page.get(MainLocale.UTIL, "a", ABI_TOT);
		SABIS = Page.get(MainLocale.UTIL, "sa", ABI_TOT);
		ATKCONF = Page.get(MainLocale.UTIL, "aa", ATK_TOT);
		TREA = Page.get(MainLocale.UTIL, "t", TIND.length);
		COMF = Page.get(MainLocale.UTIL, "na", 6);
		COMN = Page.get(MainLocale.UTIL, "nb", 25);
		TCTX = Page.get(MainLocale.UTIL, "tc", 6);
		PCTX = Page.get(MainLocale.UTIL, "aq", PC_CORRES.length);
		CCTX = Page.get(MainLocale.UTIL, "cq", PC_CUSTOM.length);
		EABI = new String[EABIIND.length];
		for (int i = 0; i < EABI.length; i++)
			EABI[i] = SABIS[EABIIND[i]];
	}

	public static void setComp(int ind, int v, BasisSet b) {
		for (int i = 0; i < TCOLP[ind][1]; i++)
			setValue(TIND[i + TCOLP[ind][0]], v, b);
	}

	public static void setValue(int ind, int v, BasisSet b) {
		setVal(ind, v, b.t());
		for (BasisLU bl : b.lb)
			setVal(ind, v, bl.t());
	}

	private static String combo(int t, int val, BasisSet b) {
		byte[] con = CDC[t];
		if (t == C_RESP) {
			double research = (b.t().tech[LV_RES] - 1) * 6 + b.t().trea[T_RES] * 0.3;
			return COMN[t] + " " + CDP[0][con[0]] + CDP[1][con[1]].replaceAll("_", "" + research * val / 100);
		} else {
			return COMN[t] + " " + CDP[0][con[0]] + CDP[1][con[1]].replaceAll("_", "" + val);
		}
	}

	private static void setVal(int ind, int v, Treasure t) {
		if (v < 0)
			v = 0;
		v = Math.min(v, TMAX[ind]);
		switch (ind) {
			case 0:
				t.tech[LV_RES] = Math.max(v, 1);
				break;
			case 1:
				t.tech[LV_ACC] = Math.max(v, 1);
				break;
			case 2:
				t.trea[T_ATK] = v;
				break;
			case 3:
				t.trea[T_DEF] = v;
				break;
			case 4:
				t.trea[T_RES] = v;
				break;
			case 5:
				t.trea[T_ACC] = v;
				break;
			case 6:
				t.fruit[T_RED] = v;
				break;
			case 7:
				t.fruit[T_FLOAT] = v;
				break;
			case 8:
				t.fruit[T_BLACK] = v;
				break;
			case 9:
				t.fruit[T_ANGEL] = v;
				break;
			case 10:
				t.fruit[T_METAL] = v;
				break;
			case 11:
				t.fruit[T_ZOMBIE] = v;
				break;
			case 12:
				t.fruit[T_ALIEN] = v;
				break;
			case 13:
				t.alien = v;
				break;
			case 14:
				t.star = v;
				break;
			case 15:
				t.gods[0] = v;
				break;
			case 16:
				t.gods[1] = v;
				break;
			case 17:
				t.gods[2] = v;
				break;
			case 18:
				t.tech[LV_BASE] = Math.max(v, 1);
				break;
			case 19:
				t.tech[LV_WORK] = Math.max(v, 1);
				break;
			case 20:
				t.tech[LV_WALT] = Math.max(v, 1);
				break;
			case 21:
				t.tech[LV_RECH] = Math.max(v, 1);
				break;
			case 22:
				t.tech[LV_CATK] = Math.max(v, 1);
				break;
			case 23:
				t.tech[LV_CRG] = Math.max(v, 1);
				break;
			case 24:
				t.trea[T_WORK] = v;
				break;
			case 25:
				t.trea[T_WALT] = v;
				break;
			case 26:
				t.trea[T_RECH] = v;
				break;
			case 27:
				t.trea[T_CATK] = v;
				break;
			case 28:
				t.trea[T_BASE] = v;
				break;
			case 29:
				t.bslv[BASE_H] = v;
				break;
			case 30:
				t.bslv[BASE_SLOW] = v;
				break;
			case 31:
				t.bslv[BASE_WALL] = v;
				break;
			case 32:
				t.bslv[BASE_STOP] = v;
				break;
			case 33:
				t.bslv[BASE_WATER] = v;
				break;
			case 34:
				t.bslv[BASE_GROUND] = v;
				break;
			case 35:
				t.bslv[BASE_BARRIER] = v;
				break;
			case 36:
				t.bslv[BASE_CURSE] = v;
				break;
			case 37:
				t.base[DECO_BASE_SLOW - 1] = v;
				break;
			case 38:
				t.base[DECO_BASE_WALL - 1] = v;
				break;
			case 39:
				t.base[DECO_BASE_STOP - 1] = v;
				break;
			case 40:
				t.base[DECO_BASE_WATER - 1] = v;
				break;
			case 41:
				t.base[DECO_BASE_GROUND - 1] = v;
				break;
			case 42:
				t.base[DECO_BASE_BARRIER - 1] = v;
				break;
			case 43:
				t.base[DECO_BASE_CURSE - 1] = v;
				break;
			case 44:
				t.deco[DECO_BASE_SLOW - 1] = v;
				break;
			case 45:
				t.deco[DECO_BASE_WALL - 1] = v;
				break;
			case 46:
				t.deco[DECO_BASE_STOP - 1] = v;
				break;
			case 47:
				t.deco[DECO_BASE_WATER - 1] = v;
				break;
			case 48:
				t.deco[DECO_BASE_GROUND - 1] = v;
				break;
			case 49:
				t.deco[DECO_BASE_BARRIER - 1] = v;
				break;
			case 50:
				t.deco[DECO_BASE_CURSE - 1] = v;
				break;
		}
	}

	private static String getAtkNumbers(List<Integer> inds) {
		StringBuilder builder = new StringBuilder("[");
		String suffix;
		switch (CommonStatic.getConfig().lang) {
			case 1:
				builder.append("第 ");
				suffix = " 次攻擊]";
				break;
			case 2:
				suffix = " 번째 공격]";
				break;
			case 3:
				suffix = " 回目の攻撃]";
				break;
			default:
				for (int i = 0; i < inds.size(); i++) {
					builder.append(getNumberExtension(inds.get(i)));

					if (i < inds.size() - 1)
						builder.append(", ");
				}
				return builder.append(" Attack]").toString();
		}
		for(int i = 0; i < inds.size(); i++) {
			builder.append(inds.get(i));
			if(i < inds.size() -1)
				builder.append(", ");
		}
		return builder.append(suffix).toString();
	}

	public static String translateDate(String date) {
		StringBuilder ans = new StringBuilder();
		int[] times = CommonStatic.parseIntsN(date);
		//English, also used for placeholder
		if (CommonStatic.getConfig().lang == 3) { //Japanese
			ans.append(times[0]).append('月').append(times[1]).append('日').append(times[2]).append("年、")
					.append(times[3] >= 12 ? "午後" : "午前").append((times[3] - 1) % 12 + 1).append('時');
		} else {
			String[] ms = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
			ans.append(ms[times[0] - 1]).append(' ').append(getNumberExtension(times[1])).append(",").append(times[2]).append(" at ")
					.append((times[3] - 1) % 12 + 1).append(times[3] >= 12 ? "PM" : "AM");
		}
		return ans.toString();
	}

	public static String getExtension(int i) {
		switch (CommonStatic.getConfig().lang) {
			case 1:
				return ("第 " + i);
			case 2:
				return (i + " 번째");
			case 3:
				return (i + " 回目");
			default:
				return getNumberExtension(i);
		}
	}

	private static String getNumberExtension(int i) {
		if(i != 11 && i % 10 == 1) {
			return i + "st";
		} else if(i != 12 && i % 10 == 2) {
			return i + "nd";
		} else if(i != 13 && i % 10 == 3) {
			return i + "rd";
		} else {
			return i + "th";
		}
	}

	public static double formatDouble(double number, int decimalPlaces) {
		String format = "#." + new String(new char[decimalPlaces]).replace("\0", "#");
		df.applyPattern(format);
		return Double.parseDouble(df.format(number));
	}

	public static void setUnderline(JLabel label) {
		label.addMouseListener(new MouseAdapter() {
			String text;

			@Override
			public void mouseClicked(MouseEvent e) {
				JLabel j = (JLabel) e.getComponent();

				if (text == null || !j.getText().equals("<html><u>" + text + "</u></html>")) {
					text = j.getText();
					j.setText("<html><u>" + j.getText() + "</u></html>");
				} else {
					j.setText(text);
				}
			}
		});

		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	public static Point getPoint(Point p, double x, double y, double size) {
		return new Point((int) ((p.x + x) / size), (int) ((p.y + y) / size));
	}

	public static Point getPoint(Point p, P pp, double size) {
		return new Point((int) ((p.x + pp.x) / size), (int) ((p.y + pp.y) / size));
	}
}