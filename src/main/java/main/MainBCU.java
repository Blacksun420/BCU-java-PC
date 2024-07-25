package main;

import common.CommonStatic;
import common.battle.BasisSet;
import common.io.Backup;
import common.io.PackLoader.ZipDesc.FileDesc;
import common.io.assets.Admin;
import common.io.assets.AssetLoader;
import common.pack.Context;
import common.pack.Context.ErrType;
import common.pack.Source;
import common.pack.Source.Workspace;
import common.pack.UserProfile;
import common.system.DateComparator;
import common.system.fake.ImageBuilder;
import common.system.files.VFile;
import common.util.AnimGroup;
import common.util.Data;
import common.util.stage.Replay;
import io.BCJSON;
import io.BCUReader;
import io.BCUWriter;
import jogl.GLBBB;
import jogl.util.GLIB;
import org.jetbrains.annotations.NotNull;
import page.LoadPage;
import page.MainFrame;
import page.MainPage;
import page.awt.AWTBBB;
import page.awt.BBBuilder;
import page.battle.BattleBox;
import plugin.Plugin;
import plugin.ui.main.UIPlugin;
import plugin.ui.main.util.MenuBarHandler;
import utilpc.UtilPC;
import utilpc.awt.FIBI;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class MainBCU {

	public static class AdminContext implements Context {

		@Override
		public boolean confirm(String str) {
			return Opts.conf(str);
		}

		@Override
		public boolean confirmDelete(File f) {
			return Opts.conf(f);
		}

		@Override
		public File getAssetFile(String string) {
			return new File(getBCUFolder(), "./assets/" + string);
		}

		@Override
		public File getAuxFile(String path) {
			return new File(getBCUFolder(), path);
		}

		@Override
		public InputStream getLangFile(String file) {
			File f = new File(getBCUFolder(),"./assets/lang/" + CommonStatic.getConfig().lang.code + "/" + file);
			if (f.exists())
				return Data.err(() -> new FileInputStream(f));

			String path = "common/util/lang/assets/" + file;
			return ClassLoader.getSystemResourceAsStream(path);
		}

		@Override
		public File getUserFile(String string) {
			return new File(getBCUFolder(), "./user/" + string);
		}

		@Override
		public File getWorkspaceFile(String relativePath) {
			return new File(getBCUFolder(), "./workspace/" + relativePath);
		}

		@Override
		public File getBackupFile(String string) {
			return new File(getBCUFolder(), "./backups/"+string);
		}

		@Override
		public String getAuthor() {
			return author;
		}

		@Override
		public void initProfile() {
			LoadPage.prog("reading assets");
			AssetLoader.load(LoadPage::prog);
			LoadPage.prog("reading BC data");
			UserProfile.getBCData().load(LoadPage::prog, LoadPage::prog);
			LoadPage.prog("reading backups");
			Backup.loadBackups();

			Backup restore = Backup.checkRestore();

			if(restore != null) {
				LoadPage.prog("restoring data");
				boolean result = CommonStatic.ctx.restore(restore, LoadPage::prog);

				if(!result)
					Opts.pop("Failed to restore data", "Restoration failed");
			}

			LoadPage.prog("reading local animations");
			Workspace.loadAnimations(null);
			LoadPage.prog("reading local animation group data");
			AnimGroup.readGroupData();
			LoadPage.prog("reading packs");
			UserProfile.loadPacks(true);
			LoadPage.prog("reading basis");
			BasisSet.read();
			LoadPage.prog("reading replays");
			Replay.read();
			LoadPage.prog("remove old files");
			CommonStatic.ctx.noticeErr(() -> {
				Context.delete(new File(getBCUFolder(), "./user/backup.zip"));
				Context.delete(new File(getBCUFolder(), "./user/basis.v"));
				Context.delete(new File(getBCUFolder(), "./user/data.ini"));
				Context.delete(new File(getBCUFolder(), "./assets/assets.zip"));
				Context.delete(new File(getBCUFolder(), "./assets/calendar/"));
			}, ErrType.WARN, "Failed to delete old files");
			LoadPage.prog("finished reading");
		}

		@Override
		public void noticeErr(Exception e, ErrType t, String str) {
			if (noNeedToShow(t)) {
				System.out.println(str);
				e.printStackTrace();
				return;
			}
			(t == ErrType.INFO ? System.out : System.err).println(str);
			e.printStackTrace(t == ErrType.INFO ? System.out : System.err);
			printErr(t, str);
		}

		@Override
		public boolean preload(FileDesc desc) {
			return Admin.preload(desc);
		}

		@Override
		public void printErr(ErrType t, String str) {
			if (noNeedToShow(t)) {
				System.out.println(str);
				return;
			}

			if (t != ErrType.INFO)
				Opts.errOnce(str, "ERROR", t == ErrType.FATAL);
		}

		@Override
		public void loadProg(double d, String str) {
			LoadPage.prog(d);
			LoadPage.packProg(str);
		}

		private boolean noNeedToShow(ErrType t) {
			return t == ErrType.DEBUG || !(t == ErrType.CORRUPT || t == ErrType.ERROR || t == ErrType.FATAL || t == ErrType.WARN || !MainBCU.WRITE);
		}

		@Override
		public boolean restore(Backup b, Consumer<Double> prog) {
			if(CommonStatic.getConfig().backupPath == null) {
				File packs = CommonStatic.ctx.getAuxFile("./packs");
				File workspace = CommonStatic.ctx.getWorkspaceFile("");
				File user = CommonStatic.ctx.getUserFile("");

				try {
					if(packs.exists())
						Context.delete(packs);

					if(workspace.exists())
						Context.delete(workspace);

					if(user.exists())
						Context.delete(user);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

				boolean result = CommonStatic.ctx.noticeErr(() -> b.backup.unzip(path -> new File(getBCUFolder(), "./"+path), prog), ErrType.ERROR, "Failed to restore files");

				DateComparator comparator = new DateComparator();

				Backup.backups.removeIf(ba -> comparator.compare(b, ba) < 1 && ba.safeDelete());

				return result;
			} else {
				VFile vf = b.backup.tree.find(CommonStatic.getConfig().backupPath);

				CommonStatic.getConfig().backupPath = null;

				if(vf == null)
					return false;

				try {
					extractData(vf, prog);
				} catch (Exception e) {
					return false;
				}

				return true;
			}
		}

		@NotNull
		@Override
		public File getBCUFolder() {
			if(!MainBCU.WRITE)
				return new File("./");

			try {
				File f = new File(MainBCU.class.getProtectionDomain().getCodeSource().getLocation().toURI());
				File parent = f.getParentFile();

				if(parent != null)
					return parent;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return new File("./");
		}

		@NotNull
		@Override
		public File newFile(String path) {
			return new File(getBCUFolder(), path);
		}

		private void extractData(VFile vf, Consumer<Double> prog) throws IOException {
			if(vf.getData() != null) {
				double max = vf.getData().size();
				double progress = 0;

				File f = newFile(vf.getPath());

				if(!f.exists() && !f.createNewFile())
					return;
				FileOutputStream fos = new FileOutputStream(f);
				InputStream ins = vf.getData().getStream();
				byte[] b = new byte[65536];
				int len;

				while((len = ins.read(b)) != -1) {
					fos.write(b, 0, len);
					progress += len;

					prog.accept(progress/max);
				}

				fos.flush();
				fos.close();
			} else {
				int num = getFileNumber(vf);
				int progress = 0;

				for(VFile v : vf.list()) {
					extractData(v, prog);

					progress += getFileNumber(v);

					prog.accept(progress * 1.0 / num);
				}
			}
		}

		private int getFileNumber(VFile vf) {
			if(vf.getData() != null)
				return 1;

			int result = 0;

			for(VFile v : vf.list()) {
				if(v.getData() != null)
					result++;
				else
					result += getFileNumber(v);
			}

			return result;
		}
	}

	public static final int ver = 60200;
	private static final DecimalFormat df = new DecimalFormat("#.##");
	public static int autoSaveTime = 0, searchTolerance = 4;
	public static final boolean WRITE = !new File("./.idea").exists();
	public static boolean preload = false, trueRun = true, loaded = false, USE_JOGL = false;
	public static boolean seconds = false, buttonSound = false, searchPerKey = false;
	public static String author = "";
	public static ImageBuilder<BufferedImage> builder;

	public static int[] dynamicTolerance = {0,0,0,0,1,1,1,2,2,2,3};
	public static boolean useDynamic = false;
	private static AutoSaveTimer ast;

	public static void restartAutoSaveTimer() {
		if (ast != null)
			ast.interrupt();
		ast = autoSaveTime > 0 ? new AutoSaveTimer() : null;
	}

	public static String getTime() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	public static String convertTime(int in) {
		if (seconds)
			return df.format(in / 30.0) + "s";
		else //for convenience, function is built to merely return the time if player prefers it like that
			return in + "f";
	}

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(MainBCU::noticeErr);
		trueRun = true;
		UserProfile.profile();
		CommonStatic.ctx = new AdminContext();
		CommonStatic.def = new UtilPC.PCItr();

		BCUWriter.logPrepare();
		BCUWriter.logSetup();
		BCUReader.readInfo();

		ImageBuilder.builder = builder = USE_JOGL ? new GLIB() : FIBI.builder;
		BBBuilder.def = USE_JOGL ? new GLBBB() : AWTBBB.INS;

		// get Plugin instance
		Plugin P = UIPlugin.getInstance();

		// UIPlugin has work to do before MainFrame init
		P.doBeforeFrameInit();
		new MainFrame(Data.revVer(MainBCU.ver)).initialize();
		// do after frame init
		P.doAfterFrameInit();

		// MainFrame should be invisible before calling this method
		MainFrame.F.setVisible(true);
		new Timer().start();

		// check Plugin update
		P.checkUpdate();
		BCJSON.check();
		CommonStatic.ctx.initProfile();

		BCUReader.getData$1();
		// Plugin may do sth
		P.doAfterReadingLang();
		BattleBox.StageNamePainter.read();
		loaded = true;
		JMenuItem menu = MenuBarHandler.getFileMenu("Save");
		if (menu != null)
			menu.setEnabled(true);
		afterLoading();
		// Plugin may do sth
		P.doAfterLoading();
	}

	private static void afterLoading() {
		// this MenuBarHandler is not page.MenuBarHandler but plugin.ui.main.util.MenuBarHandler
		BCUReader.readFaves();
		MenuBarHandler.enableSave();
		ast = autoSaveTime > 0 ? new AutoSaveTimer() : null;
		MainFrame.changePanel(new MainPage());
	}

	private static void noticeErr(Thread t, Throwable e) {
		BCUWriter.nothingburger = false;
		String msg = "ERROR: " + e + "/" + e.getMessage() + " in " + e.getStackTrace()[0].toString();
		Exception exc = (e instanceof Exception) ? (Exception) e : new Exception(msg, e);
		if (CommonStatic.ctx != null)
			CommonStatic.ctx.noticeErr(exc, ErrType.FATAL, msg);
		e.printStackTrace();
	}

	private static class AutoSaveTimer extends Thread {

		public AutoSaveTimer() {
			super();
			start();
		}

		@Override
		public void run() {
			try {
				Thread.sleep(MainBCU.autoSaveTime * 60000L);
				Source.Workspace.saveWorkspace(true);
				MainBCU.restartAutoSaveTimer();
			} catch (InterruptedException ignored) {
			}
		}
	}
}
