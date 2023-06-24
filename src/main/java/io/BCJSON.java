package io;

import common.CommonStatic;
import common.io.assets.AssetLoader;
import common.io.assets.UpdateCheck;
import common.io.assets.UpdateCheck.Downloader;
import common.io.assets.UpdateCheck.UpdateJson;
import common.pack.Context.ErrType;
import common.util.Data;
import main.Opts;
import page.LoadPage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BCJSON {

	public static final String[] PC_LANG_CODES = { "en", "jp", "kr", "zh", "fr", "it", "es", "de" };
	public static final String[] PC_LANG_FILES = { "util.properties", "page.properties", "info.properties", "docs.properties",
			"StageName.txt", "UnitName.txt", "UnitExplanation.txt", "EnemyName.txt", "EnemyExplanation.txt", "ComboName.txt", "RewardName.txt", "proc.json", "animation_type.json", "CatFruitExplanation.txt" };

	public static void check() {
		LoadPage.prog("checking update information");
		UpdateJson json = Data.ignore(UpdateCheck::checkUpdate);
		List<Downloader> assets = null, musics, libs = null, lang;
		try {
			libs = UpdateCheck.checkPCLibs(json);
			assets = UpdateCheck.checkAsset(json, "pc");
		} catch (Exception e) {
			Opts.pop(e.getMessage(), "FATAL ERROR");
			e.printStackTrace();
			CommonStatic.def.save(false, false, true);
		}

		int count = json != null ? json.music : getMusicTotal();
		if (CommonStatic.getConfig().updateOldMusic)
			musics = CommonStatic.ctx.noticeErr(UpdateCheck.checkMusic(count), ErrType.ERROR, "Failed to check for updates, try again later on a stable WI-FI connection");
		else
			musics = UpdateCheck.checkNewMusic(count);
		ArrayList<String> langList = new ArrayList<>();
		for (String pcLangCode : PC_LANG_CODES)
			for (String pcLangFile : PC_LANG_FILES)
				langList.add(pcLangCode + "/" + pcLangFile);

		lang = CommonStatic.ctx.noticeErr(UpdateCheck.checkLang(langList.toArray(new String[0])), ErrType.ERROR, "Failed to check for updates, try again later on a stable WI-FI connection");
		clearList(libs, true);
		clearList(assets, true);
		clearList(musics, false);
		clearList(lang, false);

		Downloader font = UpdateCheck.checkFont();
		if (font != null) {
			LoadPage.prog(font.desc);
			while (!CommonStatic.ctx.noticeErr(() -> font.run(LoadPage.lp::accept), ErrType.DEBUG, "failed to download"))
				if (!Opts.conf("failed to download fonts, retry?"))
					break;
		}

		while (!Data.err(AssetLoader::merge))
			if (!Opts.conf("failed to process assets, retry?"))
				CommonStatic.def.save(false, false, true);
	}

	private static int getMusicTotal() {
		File music = CommonStatic.ctx.getAssetFile("./music/");
		if (music.exists())
			return music.listFiles().length;
		return -1;
	}

	private static boolean clearList(List<Downloader> list, boolean quit) {
		boolean load = false;
		if (list != null)
			for (Downloader d : list) {
				LoadPage.prog(d.desc);
				boolean l;
				while (!(l = CommonStatic.ctx.noticeErr(() -> d.run(LoadPage.lp::accept), ErrType.DEBUG,
						"failed to download")))
					if (!Opts.conf("failed to download, retry?"))
						if (quit)
							CommonStatic.def.save(false, false, true);
						else
							break;
				load |= l;
			}
		return load;
	}
}
