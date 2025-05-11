package page.view;

import common.pack.Identifier;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.stage.Music;
import io.BCMusic;
import main.Opts;
import page.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MusicPage extends DefaultPage implements SupPage<Music> {

	private static final long serialVersionUID = 1L;

	private final JBTN strt = new JBTN(MainLocale.PAGE, "start");
	private final JBTN stop = new JBTN(MainLocale.PAGE, "stop");
	private final JBTN popout = new JBTN(MainLocale.PAGE, "popout");

	private final JList<Music> jlf = new JList<>();
	private final JScrollPane jsp = new JScrollPane(jlf);
	private final Collection<Music> mus;

	public MusicPage(Page p) {
		super(p);
		mus = null;
		List<Music> mus = new ArrayList<>();
		for (PackData pac : UserProfile.getAllPacks())
			mus.addAll(pac.musics.getList());

		jlf.setListData(mus.toArray(new Music[0]));
		ini();
	}

	public MusicPage(Page p, Collection<Music> mus) {
		super(p);
		this.mus = mus;
		jlf.setListData(mus.toArray(new Music[0]));
		ini();
	}

	public MusicPage(Page p, Identifier<Music> id) {
		this(p, id.pack);
		jlf.setSelectedValue(Identifier.get(id), true);
	}

	public MusicPage(Page p, String pack) {
		this(p, UserProfile.getAll(pack, Music.class));
	}

	public Identifier<Music> getSelectedID() {
		return jlf.getSelectedValue() == null ? null : jlf.getSelectedValue().getID();
	}

	@Override
	protected void exit() {
		BCMusic.stopAll();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jsp, x, y, 50, 100, 300, 800);
		set(strt, x, y, 400, 100, 200, 50);
		set(stop, x, y, 400, 200, 200, 50);
		set(popout, x, y, 400, 300, 200, 50);
	}

	private void addListeners() {
		getBackButton().addActionListener(arg0 -> BCMusic.clear());//There should be the default changePanel list so this is fien

		strt.addActionListener(arg0 -> {
			if (jlf.getSelectedValue() == null)
				return;
			BCMusic.setBG(jlf.getSelectedValue());
			stop.setEnabled(BCMusic.BG != null);
		});

		stop.setLnr(arg -> {
			BCMusic.BG.stop();
			BCMusic.clear();
			stop.setEnabled(false);
		});

		popout.setLnr(arg -> Opts.showMusicPopup(mus, jlf.getSelectedValue()));
	}

	private void ini() {
		add(strt);
		add(stop);
		add(popout);
		add(jsp);
		addListeners();
	}

	@Override
	public Music getSelected() {
		return jlf.getSelectedValue();
	}
}
