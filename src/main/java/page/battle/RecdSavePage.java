package page.battle;

import common.pack.Context;
import common.util.stage.Replay;
import io.BCMusic;
import main.Opts;
import page.*;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RecdSavePage extends DefaultPage {

	private static final long serialVersionUID = 1L;
	public static String defaultReplayFormat = "new ${s} replay";//p = pack, m = map, s = stage; finishing any with i uses index instead of name

	private final JBTN save = new JBTN(0, "save");
	private final JTF jtf = new JTF();
	private final JL def = new JL("Format");
	private final JTF jdf = new JTF();

	private final Replay recd;
	private String name;

	protected RecdSavePage(Page p, Replay rec) {
		super(p);
		recd = rec;

		ini();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jtf, x, y, 900, 500, 500, 50);
		set(def, x, y, 900, 0, 500, 50);
		set(jdf, x, y, 900, 50, 500, 50);
		set(save, x, y, 1000, 600, 300, 50);
	}

	private void addListeners() {
		jtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String str = jtf.getText().trim();
				if (str.isEmpty())
					str = getDefaultName();
				str = Context.validate(str, '-');
				jtf.setText(name = str);
			}
		});

		jdf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String str = jdf.getText().trim();
				if (str.isEmpty())
					str = "new ${s} replay";
				str = Context.validate(str, '-');
				jdf.setText(defaultReplayFormat = str);
			}
		});

		save.addActionListener(arg0 -> {
			if (Replay.getMap().containsKey(name) && !Opts.conf("A replay named " + name + " already exists. Do you wish to overwrite?"))
				return;

			recd.rename(name);
			recd.write();
			Replay.getMap().put(recd.rl.id, recd);

			if (BCMusic.music != null)
				BCMusic.stopAll();

			changePanel(new RecdManagePage(getRootPage()));
		});

	}

	private void ini() {
		add(jtf);
		add(save);
		add(jdf);
		jdf.setText(defaultReplayFormat);
		add(def);
		addListeners();
		jtf.setHintText(name = getDefaultName());
	}

	private String getDefaultName() {
		String def = defaultReplayFormat;
		int i = def.indexOf('$');
		while (i != -1) {
			i++;
			Object obj = null;
			if (def.charAt(i) == '{' && def.indexOf('}') != -1 && def.indexOf('}') <= i+3) {
				boolean id = def.charAt(i+2) == 'i';
				switch (def.charAt(i+1)) {
					case 's':
						obj = id ? recd.st.get().id() : recd.st.get();
						break;
					case 'm':
						obj = id ? recd.st.get().getCont().id.id : recd.st.get().getCont();
						break;
					case 'p':
						obj = id ? recd.st.get().getCont().id.pack : recd.st.get().getMC();
				}
			}
			if (obj != null)
				def = def.replace(def.substring(i-1,def.indexOf('}')+1),Context.validate(obj.toString(),'-'));
			i = def.indexOf('$',i);
		}
		return def;
	}
}
