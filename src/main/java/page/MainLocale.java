package page;

import common.CommonStatic;
import common.CommonStatic.Lang;
import page.basis.ComboListTable;
import page.battle.BattleInfoPage;
import page.info.HeadTable;
import page.info.StageTable;
import page.info.edit.StageEditPage;
import page.info.filter.EnemyListTable;
import page.info.filter.UnitListTable;
import page.pack.EREditPage;
import page.pack.UREditPage;
import utilpc.Interpret;

import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.Map.Entry;

public strictfp class MainLocale {

	public static final int PAGE = 0;
	public static final int INFO = 1;
	public static final int UTIL = 2;
	public static final Map<String, MainLocale> NAMP = new TreeMap<>();
	public static final Map<String, TTT> TMAP = new TreeMap<>();
	public static final String[] LOC_NAME = { "English", "\u4E2D\u6587", "\uD55C\uAD6D\uC5B4", "\u65E5\u672C\u8A9E", "Français", "Italiano", "Español", "Deutsche" };
	public static final int[] LOC_INDEX = {0, 1, 2, 3, 6, 9, 8, 5};
	public static final String[] RENN = { "page", "info", "util", "docs" };
	private static final ResourceBundle[] RENS = new ResourceBundle[4];

	static {
		for (int i = 0; i < 4; i++)
			RENS[i] = ResourceBundle.getBundle(RENN[i], Locale.ROOT, new URLClassLoader(new URL[]{MainLocale.class.getClassLoader().getResource(RENN[i]+".properties")}));
	}

	public static TTT addTTT(String loc, String page, String text, String cont) {
		TTT ttt = TMAP.get(loc);
		if (ttt == null)
			TMAP.put(loc, ttt = new TTT());
		if (text.equals("*"))
			ttt.tttp.put(page, cont);
		else if (page.equals("*"))
			ttt.ttts.put(text, cont);
		else {
			if (!ttt.ttt.containsKey(page))
				ttt.ttt.put(page, new TreeMap<>());
			ttt.ttt.get(page).put(text, cont);
		}
		return ttt;
	}

	public static String getLoc(int loc, String key) {
		if (loc >= 0 && loc < 4) {
			String loci = RENN[loc] + "_";
			String locl = loci + langCode();
			if (NAMP.containsKey(locl) && NAMP.get(locl).contains(key)) {
				String str = NAMP.get(loci + langCode()).get(key);
				if (str.equals("(null)"))
					str = RENS[loc].getString(key);
				String[] strs = str.split("#");
				if (strs.length == 1)
					return str;
				for (int i = 1; i < strs.length; i += 2)
					strs[i] = getLoc(loc, strs[i]);
				StringBuilder ans = new StringBuilder();
				for (String s : strs) ans.append(s);
				return ans.toString();
			}
			try {
				return RENS[loc].getString(key);
			} catch (MissingResourceException e) {
				return key;
			}
		}
		return key;
	}

	public static String[] getLoc(int loc, String... strs) {
		String[] ans = new String[strs.length];
		for (int i = 0; i < ans.length; i++)
			ans[i] = getLoc(loc, strs[i]);
		return ans;
	}

	public static String[] getLoc(int loc, String pre, int max) {
		String[] ans = new String[max];
		for (int i = 0; i < ans.length; i++)
			ans[i] = getLoc(loc, pre + i);
		return ans;
	}

	protected static String getTTT(String page, String text) {
		String loc = langCode();
		String ans = null;
		if (TMAP.containsKey(loc))
			ans = TMAP.get(loc).getTTT(page, text);
		if (ans != null)
			return ans;
		loc = Lang.LOC_CODE[0];
		if (TMAP.containsKey(loc))
			return TMAP.get(loc).getTTT(page, text);
		return null;
	}

	protected static void redefine() {
		Interpret.redefine();
		EnemyListTable.redefine();
		UnitListTable.redefine();
		ComboListTable.redefine();
		StageTable.redefine();
		HeadTable.redefine();
		BattleInfoPage.redefine();
		StageEditPage.redefine();
		EREditPage.redefine();
		UREditPage.redefine();
	}

	protected static void setLoc(int i, String key, String value) {
		if (i < 0)
			return;
		String loc = RENN[i] + "_" + langCode();
		MainLocale ml = NAMP.get(loc);
		if (ml == null)
			NAMP.put(loc, ml = new MainLocale(loc));
		ml.res.put(key, value);
		ml.edited = true;
	}

	protected static void setTTT(String loc, String info, String str) {
		addTTT(langCode(), loc, info, str).edited = true;
	}

	private static String langCode() {
		return CommonStatic.Lang.LOC_CODE[CommonStatic.getConfig().lang];
	}

	public final Map<String, String> res = new TreeMap<>();

	public boolean edited;

	public MainLocale(String str) {
		NAMP.put(str, this);
	}

	public void write(PrintStream ps) {
		for (Entry<String, String> ent : res.entrySet())
			ps.println(ent.getKey() + "\t" + ent.getValue());
	}

	private boolean contains(String str) {
		return res.containsKey(str);
	}

	private String get(String str) {
		if (res.containsKey(str))
			return res.get(str);
		return "!" + str + "!";
	}

}

class TTT {

	public final Map<String, String> tttp = new TreeMap<>();
	public final Map<String, String> ttts = new TreeMap<>();
	public final Map<String, Map<String, String>> ttt = new TreeMap<>();

	public boolean edited;

	public void write(PrintStream ps) {
		if (ps == null)
			return;
		for (Entry<String, String> ent : tttp.entrySet())
			ps.println(ent.getKey() + "\t*\t" + ent.getValue());
		for (Entry<String, String> ent : ttts.entrySet())
			ps.println("*\t" + ent.getKey() + "\t" + ent.getValue());
		for (Entry<String, Map<String, String>> ent : ttt.entrySet())
			for (Entry<String, String> snt : ent.getValue().entrySet())
				ps.println(ent.getKey() + "\t" + snt.getKey() + "\t" + snt.getValue());
	}

	protected String getTTT(String page, String text) {
		if (ttt.get(page) != null) {
			String strs = ttt.get(page).get(text);
			if (strs != null)
				return strs;
		}
		String strt = ttts.get(text);
		if (strt != null)
			return strt;
		return tttp.get(page);
	}
}
