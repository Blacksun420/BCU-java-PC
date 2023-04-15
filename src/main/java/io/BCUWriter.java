package io;

import com.google.gson.JsonObject;
import common.CommonStatic;
import common.battle.BasisSet;
import common.io.Backup;
import common.io.DataIO;
import common.io.OutStream;
import common.io.json.JsonEncoder;
import common.pack.Context;
import common.pack.Context.ErrType;
import common.pack.Source;
import common.util.AnimGroup;
import common.util.Data;
import main.MainBCU;
import main.Opts;
import main.Printer;
import page.MainFrame;
import page.battle.BattleInfoPage;
import page.support.Exporter;
import page.support.Importer;
import page.view.ViewBox;
import res.AnimatedGifEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@SuppressWarnings("UnusedReturnValue")
public class BCUWriter extends DataIO {

	private static File log, ph;
	private static WriteStream ps;
	private static final short MIN_LENGTH = 329;

	public static void logClose(boolean save, boolean genBackup) {
		if (save && MainBCU.loaded && MainBCU.trueRun)
			writeData(genBackup);

		if (ps.writed)
			ps.println("version: " + Data.revVer(MainBCU.ver));
		ps.close();
		ph.deleteOnExit();
		if (log.length() <= MIN_LENGTH)
			log.deleteOnExit();
	}

	private static void exit() {
		logClose(false, false);
		System.exit(0);
	}

	public static void logPrepare() {
		String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		log = new File(CommonStatic.ctx.getBCUFolder(), "./logs/" + str + ".log");
		ph = new File(CommonStatic.ctx.getBCUFolder(), "./logs/placeholder");

		if (!log.getParentFile().exists()) {
			boolean res = log.getParentFile().mkdirs();

			if(!res) {
				System.out.println("Can't create folder " + log.getParentFile().getAbsolutePath());
				return;
			}
		}

		try {
			if (!log.exists()) {
				boolean res = log.createNewFile();

				if(!res) {
					System.out.println("Can't create file " + log.getParentFile().getAbsolutePath());
					return;
				}
			}


			ps = new WriteStream(log);
		} catch (IOException e) {
			e.printStackTrace();
			Opts.pop(Opts.SECTY);
			exit();
		}
		try {
			if (ph.exists() && MainBCU.WRITE) {
				if (!Opts.conf("<html>" + "Another BCU is running in this folder or last BCU doesn't close properly. "
						+ "<br> Are you sure to run? It might damage your save.</html>")) {
					exit();
				}
			} else if(!ph.exists()) {
				boolean res = ph.createNewFile();

				if(!res) {
					Opts.ioErr("Can't create file "+ph.getAbsolutePath());
				}
			}
		} catch (IOException ignored) {
		}
	}

	public static void logSetup() {
		if (MainBCU.WRITE) {
			System.setErr(ps);
			System.setOut(ps);
		}
	}

	public static boolean writeBytes(byte[] bs, String path) {
		File f = new File(path);
		FileOutputStream fos = null;
		try {
			Context.check(f);
			fos = new FileOutputStream(f);
			fos.write(bs);
			fos.close();
			return true;
		} catch (IOException e) {
			Printer.w(130, "IOE!!!");
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					Printer.w(131, "cannot close fos");
					e1.printStackTrace();
				}
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					Printer.w(139, "finally cannot close fos neither!");
					e1.printStackTrace();
				}
		}
		return false;
	}

	public static boolean writeBytes(OutStream os, String path) {
		os.terminate();
		byte[] md5 = os.MD5();
		File f = new File(path);
		boolean suc;
		if (!(suc = writeFile(os, f, md5))) {
			ps.println("failed to write file: " + f.getPath());
			if (Opts.writeErr0(f.getPath()))
				if (!(suc = writeFile(os, f, md5)))
					if (Opts.writeErr1(f.getPath()))
						new Exporter(os, Exporter.EXP_ERR);
		}
		return suc;
	}

	public static void writeData(boolean genBackup) {
		BasisSet.write();
		Source.Workspace.saveWorkspace(false);
		AnimGroup.writeAnimGroup();
		writeOptions();
		writeFaves();
		if (genBackup && CommonStatic.getConfig().maxBackup != -1)
			Backup.createBackup(null, new ArrayList<>(
					Arrays.asList(
							CommonStatic.ctx.getWorkspaceFile(""),
							CommonStatic.ctx.getUserFile(""),
							CommonStatic.ctx.getAuxFile("./packs")
					)
			));
	}

	public static void writeGIF(AnimatedGifEncoder age, String path) {
		if (path == null)
			path = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		File f = new File(CommonStatic.ctx.getBCUFolder(), "./img/" + path + ".gif");
		if (!f.getParentFile().exists()) {
			boolean res = f.getParentFile().mkdirs();

			if(!res) {
				Opts.ioErr("Can't create folder "+f.getParentFile().getAbsolutePath());
				return;
			}
		}

		File destination = new File(CommonStatic.ctx.getBCUFolder(), "./img/" + path + ".gif");

		age.start(destination.getAbsolutePath());
	}

	public static boolean writeImage(BufferedImage bimg, File f) {
		if (bimg == null)
			return false;
		boolean suc = Data.err(() -> Context.check(f));
		if (suc)
			try {
				suc = ImageIO.write(bimg, "PNG", f);
			} catch (IOException e) {
				e.printStackTrace();
				suc = false;
			}
		if (!suc) {
			ps.println("failed to write image: " + f.getPath());
			if (Opts.writeErr1(f.getPath()))
				new Exporter(bimg, Exporter.EXP_ERR);
		}
		return suc;
	}

	private static boolean writeFile(OutStream os, File f, byte[] md5) {
		boolean suc = Data.err(() -> Context.check(f));
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			os.flush(fos);
			fos.close();
		} catch (IOException e) {
			suc = false;
			e.printStackTrace();
			Printer.w(130, "IOE!!!");
			Opts.ioErr("failed to write file " + f);
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		try {
			byte[] cont = Files.readAllBytes(f.toPath());
			byte[] nmd = MessageDigest.getInstance("MD5").digest(cont);
			suc &= Arrays.equals(md5, nmd);
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		return suc;
	}

	private static void writeOptions() {
		File f = new File(CommonStatic.ctx.getBCUFolder(), "./user/config.json");
		Data.err(() -> Context.check(f));
		JsonObject jo = JsonEncoder.encode(CommonStatic.getConfig()).getAsJsonObject();
		Rectangle r = MainFrame.crect;
		jo.add("crect", JsonEncoder.encode(new int[] { r.x, r.y, r.width, r.height }));
		jo.addProperty("preload", MainBCU.preload);
		jo.addProperty("transparent", ViewBox.Conf.white);
		jo.addProperty("JOGL", MainBCU.USE_JOGL);
		jo.addProperty("seconds", MainBCU.seconds);
		jo.addProperty("prefLv", CommonStatic.getConfig().prefLevel);
		jo.addProperty("play_sound", BCMusic.play);
		jo.addProperty("volume_BG", BCMusic.VOL_BG);
		jo.addProperty("volume_SE", BCMusic.VOL_SE);
		jo.addProperty("volume_UI",BCMusic.VOL_UI);
		jo.addProperty("large_screen", BattleInfoPage.DEF_LARGE);
		jo.addProperty("author", MainBCU.author);
		jo.addProperty("rowlayout", CommonStatic.getConfig().twoRow);
		jo.addProperty("backup_file", CommonStatic.getConfig().backupFile == null ? "None" : CommonStatic.getConfig().backupFile);
		jo.addProperty("buttonSound", MainBCU.buttonSound);
		jo.addProperty("autosavetime", MainBCU.autoSaveTime);
		jo.addProperty("drawBGEffect", CommonStatic.getConfig().drawBGEffect);
		String[] exp = new String[Exporter.curs.length];
		for (int i = 0; i < exp.length; i++)
			exp[i] = Exporter.curs[i] == null ? null : Exporter.curs[i].toString();
		String[] imp = new String[Importer.curs.length];
		for (int i = 0; i < imp.length; i++)
			imp[i] = Importer.curs[i] == null ? null : Importer.curs[i].toString();
		jo.add("export_paths", JsonEncoder.encode(exp));
		jo.add("import_paths", JsonEncoder.encode(imp));
		try (java.io.Writer w = new OutputStreamWriter(Files.newOutputStream(f.toPath()), StandardCharsets.UTF_8)) {
			w.write(jo.toString());
		} catch (Exception e) {
			CommonStatic.ctx.noticeErr(e, ErrType.ERROR, "failed to write config");
		}
	}

	private static void writeFaves() {
		File f = new File(CommonStatic.ctx.getBCUFolder(), "./user/favorites.json");
		Data.err(() -> Context.check(f));
		JsonObject jo = JsonEncoder.encode(CommonStatic.getFaves()).getAsJsonObject();
		try (java.io.Writer w = new OutputStreamWriter(Files.newOutputStream(f.toPath()), StandardCharsets.UTF_8)) {
			w.write(jo.toString());
		} catch (Exception e) {
			CommonStatic.ctx.noticeErr(e, ErrType.ERROR, "failed to write favorites");
		}
	}
}

class WriteStream extends PrintStream {

	protected boolean writed = false;

	public WriteStream(File file) throws FileNotFoundException {
		super(file);
	}

	@Override
	public void println(Object str) {
		super.println(str);
		writed = true;
	}

	@Override
	public void println(String str) {
		super.println(str);
		writed = true;
	}

}
