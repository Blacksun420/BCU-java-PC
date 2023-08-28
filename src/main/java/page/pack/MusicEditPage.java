package page.pack;

import common.CommonStatic;
import common.pack.Context;
import common.pack.PackData.UserPack;
import common.system.files.FDFile;
import common.util.Data;
import common.util.stage.Music;
import io.BCMusic;
import main.Opts;
import page.*;
import page.support.Importer;

import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MusicEditPage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	private final JList<Music> jlst = new JList<>();
	private final JScrollPane jspst = new JScrollPane(jlst);

	private final JBTN add = new JBTN(MainLocale.PAGE, "add");
	private final JBTN rem = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN relo = new JBTN(MainLocale.PAGE, "read list");
	private final JBTN play = new JBTN(MainLocale.PAGE, "start");
	private final JBTN stop = new JBTN(MainLocale.PAGE, "stop");
	private final JBTN show = new JBTN(MainLocale.PAGE, "show");
	private final JL jlp = new JL(MainLocale.INFO, "loop");
	private final JTF jtp = new JTF();
	private final JTF jtn = new JTF();

	private final UserPack pack;
	private Music sele;

	public MusicEditPage(Page p, UserPack ac) {
		super(p);
		pack = ac;
		ini();
		resized(true);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jspst, x, y, 50, 100, 400, 1000);
		set(relo, x, y, 500, 100, 200, 50);
		set(show, x, y, 500, 200, 200, 50);
		set(add, x, y, 500, 350, 200, 50);
		set(rem, x, y, 500, 450, 200, 50);
		set(play, x, y, 750, 100, 200, 50);
		set(stop, x, y, 750, 200, 200, 50);
		set(jlp, x, y, 500, 600, 200, 50);
		set(jtp, x, y, 500, 650, 200, 50);
		set(jtn, x, y, 50, 1100, 400, 50);
	}

	private void addListeners() {
		getBackButton().addActionListener(arg0 -> { //changepanel should be managed I think
			if (BCMusic.BG != null && BCMusic.BG.isPlaying()) {
				BCMusic.BG.stop();
				BCMusic.clear();
			}
		});

		relo.addActionListener(arg0 -> {
			for (Music m : pack.musics)
				BCMusic.CACHE_CUSTOM.remove(m.getID());
			pack.loadMusics();
			setList();
		});

		add.addActionListener(arg0 -> {
			Music m = getFile("Choose Music");
			if (m != null) {
				pack.musics.set(m.id.id, m);
				setList();
			}
		});

		rem.addActionListener(l -> {
			try {
				Context.delete(new File(CommonStatic.ctx.getBCUFolder(), "/workspace/" + pack.desc.id + "/musics/" + sele.data));
				pack.musics.remove(sele);
				setList();
			} catch (Exception e) {
				CommonStatic.ctx.noticeErr(e, Context.ErrType.ERROR, "Failed to delete " + sele.data);
			}
		});

		show.addActionListener(arg0 -> {
			File f = new File(CommonStatic.ctx.getBCUFolder(), "./workspace/" + pack.desc.id + "/musics/");
			if(!f.exists())
				if(!f.mkdirs()) {
					Opts.pop("Couldn't create folder "+f.getAbsolutePath(), "IO Error");
					return;
				}
			try {
				Desktop.getDesktop().open(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		play.addActionListener(arg0 -> {
			BCMusic.setBG(sele);
			BCMusic.BG.setLineListener(event -> {
				if (event.getType() == LineEvent.Type.STOP)
					stop.setEnabled(false);
			});
			stop.setEnabled(true);
		});

		stop.addActionListener(arg -> {
			BCMusic.BG.stop();
			BCMusic.clear();
			stop.setEnabled(false);
		});

		jlst.addListSelectionListener(arg0 -> {
			if (isAdj() || arg0.getValueIsAdjusting())
				return;
			sele = jlst.getSelectedValue();
			toggleButtons();
		});

		jtp.setLnr(x -> {
			if (sele.data == null)
				return;
			long tim = Math.min(toMilli(jtp.getText()), toMilli(getMusTime()) - 1);
			if (tim != -1)
				sele.loop = tim;
			jtp.setText(convertTime(sele.loop));
		});

		jtn.setLnr(x -> sele.name = jtn.getText());
	}

	private void ini() {
		add(jspst);
		add(show);
		add(relo);
		add(play);
		add(stop);
		add(jlp);
		add(jtp);
		add(jtn);
		add(add);
		add(rem);
		setList();
		addListeners();
	}

	private void toggleButtons() {
		play.setEnabled(sele != null && BCMusic.play && BCMusic.VOL_BG > 0);
		stop.setEnabled(BCMusic.BG != null);
		jtp.setEnabled(sele != null);
		jtp.setText(sele != null ? convertTime(sele.loop) : "-");
		jtp.setToolTipText(getMusTime());
		jtn.setText(sele != null ? sele.name : "-");
		rem.setEnabled(sele != null);
	}

	private void setList() {
		change(this, p -> {
			int ind = jlst.getSelectedIndex();
			Music[] arr = pack.musics.toArray();
			jlst.setListData(arr);
			if (ind < 0)
				ind = 0;
			if (ind >= arr.length)
				ind = arr.length - 1;
			jlst.setSelectedIndex(ind);
			if (ind >= 0)
				sele = arr[ind];
			toggleButtons();
		});
	}

	private String getMusTime() {
		if (sele == null || sele.data == null)
			return "Music not found";
		try {
			long duration = CommonStatic.def.getMusicLength(sele);

			if (duration == -1) {
				return "Invalid Format";
			} else if (duration == -2) {
				return "Unsupported Format";
			} else if (duration == -3) {
				return "Can't get duration";
			} else if (duration >= 0) {
				return convertTime(duration);
			} else {
				return "Unknown error";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static String convertTime(long milli) {
		long min = milli / 60 / 1000;
		double time = milli - (double) min * 60000;
		time /= 1000;
		NumberFormat nf = NumberFormat.getInstance(Locale.US);

		DecimalFormat df = (DecimalFormat) nf;

		df.applyPattern("#.###");
		double s = Double.parseDouble(df.format(time));
		if (s >= 60) {
			s -= 60;
			min += 1;
		}
		if (s < 10) {
			return min + ":" + "0" + df.format(s);
		} else {
			return min + ":" + df.format(s);
		}
	}

	private static long toMilli(String time) {
		try {
			long[] times = CommonStatic.parseLongsN(time);

			for (long t : times) {
				if (t < 0) {
					return -1;
				}
			}

			if (times.length == 1) {
				return times[0] * 1000;
			} else if (times.length == 2) {
				return (times[0] * 60 + times[1]) * 1000;
			} else if (times.length == 3) {
				if (times[2] < 1000) {
					return (times[0] * 60 + times[1]) * 1000 + getMili(times[2]);
				} else {
					String decimal = Long.toString(times[2]).substring(0, 3);
					return (times[0] * 60 + times[1]) * 1000 + Integer.parseInt(decimal);
				}
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	private static long getMili(long milis) {
		if (milis == 0 || milis >= 100)
			return milis;
		else if (milis >= 10)
			return milis * 10;
		else
			return milis * 100;
	}

	private Music getFile(String str) {
		File mus = new Importer(str, Importer.IMP_MUS).get();
		if (mus == null)
			return null;
		if (!Music.isMusic(mus.getName()))
			return getFile("Wrong file format. Must be ogg");

		int id = CommonStatic.parseIntN(mus.getName().substring(0, 3));
		if (id == -1)
			id = pack.musics.nextInd();

		Music musc = pack.musics.getRaw(id);
		if (musc != null && !Opts.conf("Music file with id " + id + " already exists in the pack. Replace?"))
			return null;
		try {
			File nmf = CommonStatic.ctx.getWorkspaceFile("./" + pack.getSID() + "/musics/" + Data.trio(id) + mus.getName().substring(mus.getName().length() - 4));
			Context.check(nmf);
			Files.copy(mus.toPath(), nmf.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return new Music(pack.getID(Music.class, id), new FDFile(nmf), musc);
		} catch (Exception e) {
			CommonStatic.ctx.noticeErr(e, Context.ErrType.ERROR, "Failed to copy music file to pack");
		}
		return null;
	}
}
