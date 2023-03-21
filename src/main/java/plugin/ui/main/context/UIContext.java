package plugin.ui.main.context;

import com.google.gson.JsonElement;
import common.CommonStatic;
import common.io.WebFileIO;
import common.io.assets.AssetLoader;
import common.io.assets.UpdateCheck;
import main.MainBCU;
import main.Opts;
import page.LoadPage;
import plugin.ui.common.config.StaticConfig;
import plugin.ui.common.util.JsonUtils;
import plugin.ui.common.util.UIException;
import plugin.ui.main.UIPlugin;
import plugin.ui.main.UITheme;
import plugin.ui.main.util.UIDownloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class UIContext {
    public static final Integer SUPPORTED_LEAST_PROFILE_VERSION = 22;
    private static UserProfile profile;
    private static UITheme theme;

    public static void init() {
        checkEnvironment();
        profile = getOrDefault(StaticConfig.UI_JSON_PATH, UserProfile.class);
        theme = getOrDefault(StaticConfig.THEME_JSON_PATH, UITheme.class);
        checkProfileVersion();
    }

    private static void checkProfileVersion() {
        Integer localVer = profile.basic.getInteger("version");
        if (localVer == null || SUPPORTED_LEAST_PROFILE_VERSION > localVer) {
            UIPlugin.pop("Deprecated profile [version=" + localVer + "], the old version ui.json will be overwritten.", "Info");
            profile = UserProfile.getDefault();
            toFile(profile, StaticConfig.UI_JSON_PATH);
        }
    }

    public static void checkEnvironment() {
        UIChecker.checkEnvironment();
    }

    private static <T> T getOrDefault(String path, Class<T> clazz) {
        T target = null;
        File file = new File(path);
        if (file.exists())
            // from locale
            try {
                target = JsonUtils.fromFile(file, clazz);
            } catch (Exception e) {
                UIPlugin.popError("Invalid file: " + new File(path).getAbsolutePath() + "\n<html><h2>Advice: delete it.\n<html><h3>Reason:" + e);
                e.printStackTrace();
            }
        if (target == null)
            // from classpath
            try {
                target = JsonUtils.fromClasspath(path, clazz);
                // if (write)
                    // toFile(target, path);
            } catch (Exception e) {
                throw new UIException("Failed to init " + path + ": " + e);
            }

        return target;
    }

    public static void writeData() {
        toFile(profile, StaticConfig.UI_JSON_PATH);
        toFile(theme, StaticConfig.THEME_JSON_PATH);
    }

    private static void toFile(Object o, String path) {
        try {
            JsonUtils.toFile(o, path);
        } catch (IOException e) {
            e.printStackTrace();
            UIPlugin.popError("failed to write " + path);
        }
    }

    public static ThemeConfig getThemeConfig() {
        return profile.theme;
    }

    public static BasicConfig getBasicConfig() {
        return profile.basic;
    }

    public static UITheme getTheme() {
        return theme;
    }

    public static void askUpdate() {
        UIChecker.askUpdate();
    }

    public static void checkUpdate() {
        UIChecker.checkUpdate();
    }

    public abstract static class UIChecker {
        private static final String LIB_URL = "https://repo1.maven.org/maven2/com/formdev/";
        private static final String LIB_DIRECTORY = "./BCU_lib/";
        private static final String JAR_CHECK_URL = "https://raw.githubusercontent.com/Blacksun420/bcu-assets/master/jar/check.json";
        private static final String[] UILibs = {
                "flatlaf-intellij-themes-2.3.jar", "flatlaf-2.3.jar"
        };

        public static void checkEnvironment() {
            // check ui dir
            File file = new File(StaticConfig.UI_DIRECTORY);
            if (!file.exists()) {
                System.out.println(file.getAbsolutePath());
                boolean mkdir = file.mkdirs();
                if (!mkdir) {
                    throw new UIException("failed to create ui dir.");
                }
            }

            // check ui lib
            checkLib();
        }

        public static String getURL(String lib) {
            int index = lib.lastIndexOf("-");
            String version = lib.substring(index + 1, lib.length() - ".jar".length());
            String artifacts = lib.substring(0, index);
            return LIB_URL + artifacts + "/" + version + "/" + lib;
        }

        public static List<UpdateCheck.Downloader> getMissingLib() {
            List<UpdateCheck.Downloader> downloaderList = new ArrayList<>();
            for (String lib : UILibs) {
                File libFile = new File(LIB_DIRECTORY + lib);
                if (!libFile.exists()) {
                    String url = getURL(lib);
                    downloaderList.add(new UpdateCheck.Downloader(libFile, new File("./BCU_lib/.jar.temp"),
                            "download UI library: " + lib, false, url)
                    );
                }
            }
            return downloaderList;
        }

        private static void checkLib() {
            // get missing lib
            List<UpdateCheck.Downloader> missingLib = getMissingLib();

            if (missingLib.size() == 0) {
                return;
            }

            // inquiry
            if (Opts.conf("BCU needs to download necessary UI lib before access, do you accept?")) {
                // result
                UIDownloader.downloadLibs(missingLib);
                Opts.pop("Download UI library successfully, please restart BCU", "success");
            }

            CommonStatic.def.save(false, false, true);
        }

        public static void checkUpdate() {
            LoadPage.prog("checking UI update information");
            // get update json
            UpdateJson uj = getUpdateJson();
            // inquiry
            if (uj != null && uj.getVer() > MainBCU.ver && uj.forkver > AssetLoader.FORK_VER) {
                String popText = "New BCU file update found: " + uj.getArtifact() +
                        ", do you want to update jar file?\n" + uj.getDescription();
                // result
                if (Opts.conf(popText))
                    UIDownloader.downloadJar(getDownloader(uj), true);
            }
        }

        public static void askUpdate() {
            // get update json
            UpdateJson uj = getUpdateJson();
            if (uj == null)
                return;
            // inquiry
            System.out.println(uj.getVer());
            if (uj.getVer() > MainBCU.ver) {
                String popText = "New BCU Jar file update found: " + uj.getArtifact()
                        + ", do you want to update?" + " Its' " + (uj.forkver > AssetLoader.FORK_VER ? "necessary.\n" : "unnecessary.\n")
                        + uj.getDescription();
                // result
                if (Opts.conf(popText)) {
                    UpdateCheck.Downloader d = getDownloader(uj);
                    UIDownloader.downloadJar(d, false, "url: " + uj.getURL(), d.desc);
                }
            } else
                Opts.pop("Your BCU is the latest version.\n" + uj.getDescription(), "RESULT");
        }

        private static UpdateJson getUpdateJson() {
            try {
                JsonElement json = WebFileIO.read(JAR_CHECK_URL);
                if (json != null)
                    return JsonUtils.get("latest", json.getAsJsonObject(), UpdateJson.class);
            } catch (Exception ignored) {
                UIPlugin.popError("Failed to check update, try again later on a stable WI-FI connection");
            }
            return null;
        }

        private static UpdateCheck.Downloader getDownloader(UpdateJson updateJson) {
            String filename = updateJson.getArtifact();
            File target = new File("./" + filename);
            return new UpdateCheck.Downloader(target,
                    new File("./temp.temp"),
                    "Downloading " + filename + "...",
                    true, updateJson.getURL());
        }

        public static class UpdateJson {

            public String ver;
            public Byte forkver;
            public String info;

            public int getVer() {
                int[] digs = CommonStatic.parseIntsN(ver);
                int tott = 0;
                for (int i = 0; i < Math.min(4, digs.length); i++)
                    tott += (digs[i] * 1000000) / Math.pow(100, i);
                return tott;
            }

            public String getArtifact() {
                return "BCU-" + ver.replace(".", "-") + ".jar";
            }
            public String getURL() {
                return "https://github.com/Blacksun420/bcu-assets/raw/master/jar/" + getArtifact();
            }

            public String getDescription() {
                return this.ver + " info: \n" + this.info;
            }

            @Override
            public String toString() {
                return JsonUtils.G.toJson(this);
            }
        }

    }
}
