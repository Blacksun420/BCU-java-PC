package page.battle;

import common.pack.Context;
import common.util.stage.Replay;
import io.BCMusic;
import main.Opts;
import page.DefaultPage;
import page.JBTN;
import page.JTF;
import page.Page;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RecdSavePage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	private final JBTN save = new JBTN(0, "save");
	private final JTF jtf = new JTF();

	private final Replay recd;
	private String name;

	protected RecdSavePage(Page p, Replay rec) {
		super(p);
		recd = rec;

		ini();
		resized(true);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jtf, x, y, 1000, 500, 300, 50);
		set(save, x, y, 1000, 600, 300, 50);
	}

	private void addListeners() {
		jtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String str = jtf.getText().trim();
				if (str.length() == 0)
					str = "new " + recd.st.get().toString() + " replay";
				str = Context.validate(str, '-');
				jtf.setText(name = str);
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
		addListeners();
		name = "new " + Context.validate(recd.st.get().toString(),'-') + " replay";
		jtf.setHintText(name);
	}

}
