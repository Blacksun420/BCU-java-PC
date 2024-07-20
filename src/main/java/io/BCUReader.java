package io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.CommonStatic;
import common.CommonStatic.Config;
import common.io.DataIO;
import common.io.json.JsonDecoder;
import common.pack.Context.ErrType;
import common.pack.UserProfile;
import common.util.lang.MultiLangCont;
import common.util.stage.MapColc;
import common.util.stage.MapColc.DefMapColc;
import common.util.stage.Stage;
import common.util.stage.StageMap;
import common.util.unit.Combo;
import common.util.unit.Enemy;
import common.util.unit.Unit;
import main.MainBCU;
import main.Opts;
import main.Timer;
import page.LoadPage;
import page.MainFrame;
import page.MainLocale;
import page.battle.BattleInfoPage;
import page.support.Exporter;
import page.support.Importer;
import page.view.ViewBox;
import utilpc.Interpret;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BCUReader extends DataIO {

	public static void getData$1() {
		readLang();
		Interpret.loadCannonMax();
		BCMusic.preload();
	}

	public static void readInfo() {
		File fd = new File(CommonStatic.ctx.getBCUFolder(), "./user/hashes.json");
		if (fd.exists()) {
			try (Reader r = new InputStreamReader(Files.newInputStream(fd.toPath()), StandardCharsets.UTF_8)) {
				JsonElement je = JsonParser.parseReader(r);
				r.close();
				CommonStatic.LocalMaps cfg = CommonStatic.getDataMaps();
				JsonDecoder.inject(je, CommonStatic.LocalMaps.class, cfg);
			} catch (Exception e) {
				CommonStatic.ctx.noticeErr(e, ErrType.WARN, "failed to read config");
			}
		}

		File f = new File(CommonStatic.ctx.getBCUFolder(), "./user/config.json");
		if (f.exists()) {
			try (Reader r = new InputStreamReader(Files.newInputStream(f.toPath()), StandardCharsets.UTF_8)) {
				JsonElement je = JsonParser.parseReader(r);
				r.close();
				Config cfg = CommonStatic.getConfig();
				JsonDecoder.inject(je, Config.class, cfg);
				JsonObject jo = je.getAsJsonObject();
				int[] rect = JsonDecoder.decode(jo.get("crect"), int[].class);
				MainFrame.crect = new Rectangle(rect[0], rect[1], rect[2], rect[3]);
				MainBCU.preload = jo.get("preload").getAsBoolean();
				ViewBox.Conf.white = jo.get("transparent").getAsBoolean();
				MainBCU.USE_JOGL = jo.get("JOGL").getAsBoolean();
				if(jo.has("seconds"))
					MainBCU.seconds = jo.get("seconds").getAsBoolean();
				if(jo.has("prefLV"))
					CommonStatic.getConfig().prefLevel = jo.get("prefLv").getAsInt();
				if(jo.has("buttonSound"))
					MainBCU.buttonSound = jo.get("buttonSound").getAsBoolean();
				if (jo.has("drawBGEffect"))
					CommonStatic.getConfig().drawBGEffect = jo.get("drawBGEffect").getAsBoolean();
				BCMusic.play = jo.get("play_sound").getAsBoolean();
				BCMusic.VOL_BG = jo.get("volume_BG").getAsInt();
				BCMusic.VOL_SE = jo.get("volume_SE").getAsInt();
				if(jo.has("volume_UI"))
					BCMusic.VOL_UI = jo.get("volume_UI").getAsInt();
				BattleInfoPage.DEF_LARGE = jo.get("large_screen").getAsBoolean();
				if(jo.has("author"))
					MainBCU.author = jo.get("author").getAsString();
				if(jo.has("rowlayout"))
					CommonStatic.getConfig().twoRow = jo.get("rowlayout").getAsBoolean();
				if(jo.has("backup_file")) {
					String value = jo.get("backup_file").getAsString();
					CommonStatic.getConfig().backupFile = value.equals("None") ? null : value;
				}
				if (jo.has("autosavetime"))
					MainBCU.autoSaveTime = jo.get("autosavetime").getAsInt();
				if (jo.has("searchtype"))
					MainBCU.searchPerKey = jo.get("searchtype").getAsBoolean();
				if (jo.has("tolerance"))
					MainBCU.searchTolerance = jo.get("tolerance").getAsInt();
				if (jo.has("usedynamic"))
					MainBCU.useDynamic = jo.get("usedynamic").getAsBoolean();
				String[] exp = JsonDecoder.decode(jo.get("export_paths"), String[].class);
				String[] imp = JsonDecoder.decode(jo.get("import_paths"), String[].class);
				for (int i = 0; i < Math.min(Exporter.curs.length, exp.length); i++)
					Exporter.curs[i] = exp[i] == null ? null : new File(exp[i]);
				for (int i = 0; i < Math.min(Importer.curs.length, imp.length); i++)
					Importer.curs[i] = imp[i] == null ? null : new File(imp[i]);
				if (jo.has("fps60"))
					CommonStatic.getConfig().fps60 = jo.get("fps60").getAsBoolean();
				if (CommonStatic.getConfig().fps60)
					Timer.fps = 1000 / 60;
				if (jo.has("stat"))
					CommonStatic.getConfig().stat = jo.get("stat").getAsBoolean();
			} catch (Exception e) {
				CommonStatic.ctx.noticeErr(e, ErrType.WARN, "failed to read config");
			}
		}
	}

	public static void readFaves() {
		File f = new File(CommonStatic.ctx.getBCUFolder(), "./user/favorites.json");
		if (f.exists())
			try (Reader r = new InputStreamReader(Files.newInputStream(f.toPath()), StandardCharsets.UTF_8)) {
				JsonElement je = JsonParser.parseReader(r);
				r.close();
				CommonStatic.Faves cfg = CommonStatic.getFaves();
				JsonDecoder.inject(je, CommonStatic.Faves.class, cfg);

				CommonStatic.getFaves().enemies.removeIf(Objects::isNull);
				CommonStatic.getFaves().units.removeIf(Objects::isNull);
			} catch (Exception e) {
				CommonStatic.ctx.noticeErr(e, ErrType.WARN, "failed to read favorites");
			}
	}

	public static void readLang() {
		LoadPage.prog("reading language information");
		File f = new File(CommonStatic.ctx.getBCUFolder(), "./assets/lang/");
		if (!f.exists())
			return;

		File[] fis = f.listFiles();

		if(fis != null) {
			for (File fi : fis) {
				String ni = fi.getName();
				if (!fi.isDirectory())
					continue;
				if (ni.length() != 2)
					continue;
				CommonStatic.Lang.Locale locale = null;
				for (CommonStatic.Lang.Locale l : CommonStatic.Lang.Locale.values()) {
					if (l.code.equals(ni)) {
						locale = l;
						break;
					}
				}
				if (locale == null) {
					System.out.println("unregistered locale: " + ni);
					continue;
				}

				File[] fls = fi.listFiles();

				if(fls != null) {
					for (File fl : fls)
						try {
							String nl = fl.getName();

							if (nl.equals("RewardName.txt")) {
								Queue<String> qs = readLines(fl);
								if (qs != null)
									for (String line : qs) {
										String[] str = line.trim().split("\t");

										if(str.length >= 2) {
											String[] ids = str[0].split("\\|");

											for (String id : ids) {
												if (CommonStatic.isInteger(id)) {
													MultiLangCont.getStatic().RWNAME.put(locale, Integer.parseInt(id), str[1]);
												} else if (id.startsWith("S")) {
													String realID = id.replace("S", "");

													if (CommonStatic.isInteger(realID))
														MultiLangCont.getStatic().RWSTNAME.put(locale, Integer.parseInt(realID), str[1]);
												} else if (id.startsWith("I")) {
													String realID = id.replace("I", "");

													if (CommonStatic.isInteger(realID))
														MultiLangCont.getStatic().RWSVNAME.put(locale, Integer.parseInt(realID), str[1]);
												}
											}
										}
									}
							}
							if (nl.equals("ComboName.txt")) {
								Queue<String> qs = readLines(fl);
								if (qs != null) {
									for (String line : qs) {
										String[] str = line.trim().split("\t");
										List<Combo> combo = Arrays.stream(UserProfile.getBCData().combos.toArray())
												.filter(c -> c.name.equals(str[0]))
												.collect(Collectors.toList());
										if (combo.size() > 0)
											MultiLangCont.getStatic().COMNAME.put(locale, combo.get(0), str[1]);
									}
								}
								continue;
							}
							if (nl.equals("tutorial.txt")) {
								Queue<String> qs = readLines(fl);
								if(qs != null)
									for (String line : qs) {
										String[] strs = line.trim().split("\t");
										if (strs.length != 3)
											continue;
										MainLocale.addTTT(ni, strs[0].trim(), strs[1].trim(), strs[2].trim());
									}
								continue;
							}
							if (nl.equals("StageName.txt")) {
								Queue<String> qs = readLines(fl);
								if (qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t");
										if (strs.length == 1)
											continue;
										String idstr = strs[0].trim();
										String name = strs[strs.length - 1].trim();
										if (idstr.length() == 0 || name.length() == 0)
											continue;
										String[] ids = idstr.split("-");
										int id0 = CommonStatic.parseIntN(ids[0]);

										StageMap stm = DefMapColc.getMap(id0 * 1000);

										if(stm != null) {
											MapColc mc = stm.getCont();
											if (mc == null)
												continue;
											if (ids.length == 1) {
												MultiLangCont.getStatic().MCNAME.put(locale, mc, name);
												continue;
											}
											int id1 = CommonStatic.parseIntN(ids[1]);
											if (id1 >= mc.maps.size() || id1 < 0)
												continue;
											StageMap sm = mc.maps.get(id1);
											if (sm == null)
												continue;
											if (ids.length == 2) {
												MultiLangCont.getStatic().SMNAME.put(locale, sm, name);
												continue;
											}
											int id2 = CommonStatic.parseIntN(ids[2]);
											if (id2 >= sm.list.size() || id2 < 0)
												continue;
											Stage st = sm.list.get(id2);
											MultiLangCont.getStatic().STNAME.put(locale, st, name);
										}
									}
								continue;
							}
							if (nl.equals("UnitName.txt")) {
								Queue<String> qs = readLines(fl);
								if(qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t");
										Unit u = UserProfile.getBCData().units.get(CommonStatic.parseIntN(strs[0]));
										if (u == null)
											continue;
										for (int i = 0; i < Math.min(u.forms.length, strs.length - 1); i++)
											MultiLangCont.getStatic().FNAME.put(locale, u.forms[i], strs[i + 1].trim());
									}
								continue;
							}
							if (nl.equals("EnemyName.txt")) {
								Queue<String> qs = readLines(fl);
								if(qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t");
										Enemy e = UserProfile.getBCData().enemies.get(CommonStatic.parseIntN(strs[0]));
										if (e == null || strs.length < 2)
											continue;
										MultiLangCont.getStatic().ENAME.put(locale, e, strs[1].trim());
									}
								continue;
							}
							if (nl.equals("UnitExplanation.txt")) {
								Queue<String> qs = readLines(fl);
								if(qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t");
										Unit u = UserProfile.getBCData().units.get(CommonStatic.parseIntN(strs[0]));
										if (u == null)
											continue;
										for (int i = 0; i < Math.min(u.forms.length, strs.length - 1); i++)
											MultiLangCont.getStatic().FEXP.put(locale, u.forms[i], strs);
									}
								continue;
							}
							if (nl.equals("CatFruitExplanation.txt")) {
								Queue<String> qs = readLines(fl);
								if (qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t", 3);
										if (strs.length < 2 || strs[1].equals("<br><br>"))
											continue;
										Unit u = UserProfile.getBCData().units.get(CommonStatic.parseIntN(strs[0]));
										if (u != null) {
											MultiLangCont.getStatic().CFEXP.put(locale, u.info, strs[1]);
											if (strs.length == 3 && !strs[2].equals("<br><br>"))
												MultiLangCont.getStatic().UFEXP.put(locale, u.info, strs[2]);
										}
									}
							}
							if (nl.equals("EnemyExplanation.txt")) {
								Queue<String> qs = readLines(fl);
								if(qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t");
										Enemy e = UserProfile.getBCData().enemies.get(CommonStatic.parseIntN(strs[0]));
										if (e == null || strs.length < 2)
											continue;
										MultiLangCont.getStatic().EEXP.put(locale, e, strs);
									}
								continue;
							}
							if (!nl.endsWith(".properties"))
								continue;
							MainLocale ml = new MainLocale(nl.split("\\.")[0] + "_" + ni);
							Queue<String> qs = readLines(fl);
							if(qs != null)
								for (String line : qs) {
									String[] strs = line.split("[=\t]", 2);
									if (strs.length < 2)
										continue;
									ml.res.put(strs[0], strs[1]);
								}
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			}
		}
	}

	public static Queue<String> readLines(File file) {
		try {
			return new ArrayDeque<>(Files.readAllLines(file.toPath()));
		} catch (IOException e) {
			Opts.ioErr("failed to read file " + file);
			e.printStackTrace();
			return null;
		}
	}
}