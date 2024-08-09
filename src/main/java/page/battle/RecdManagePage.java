package page.battle;

import common.CommonStatic;
import common.pack.Context;
import common.util.stage.MapColc;
import common.util.stage.Replay;
import main.Opts;
import page.JBTN;
import page.JL;
import page.JTF;
import page.Page;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Vector;

public class RecdManagePage extends AbRecdPage {

	private static final long serialVersionUID = 1L;

	private final JBTN dele = new JBTN(0, "rem");
	private final JTF rena = new JTF();
	private final JList<Replay> jlr = new JList<>();
	private final JScrollPane jspr = new JScrollPane(jlr);

	private final JL fil = new JL(0, "filter");
	private final JList<MapColc> jlm = new JList<>();
	private final JScrollPane jspm = new JScrollPane(jlm);

	public RecdManagePage(Page p) {
		super(p, true);
		preini();
		ini();
		setMCs();
	}

	@Override
	public Replay getSelection() {
		return jlr.getSelectedValue();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jspr, x, y, 50, 100, 500, 1100);
		set(rena, x, y, 600, 300, 300, 50);
		set(dele, x, y, 600, 400, 300, 50);
		set(fil, x, y, 600, 600, 1000, 50);
		set(jspm, x, y, 600, 650, 1000, 550);
	}

	@Override
	protected void setList() {
		change(true);
		Replay r = jlr.getSelectedValue();
		Vector<Replay> replays = new Vector<>(Replay.getMap().values());
		Vector<MapColc> cols = new Vector<>(jlm.getSelectedValuesList());
		if (!cols.isEmpty())
			replays.removeIf(re -> re.st.safeGet() == null || !cols.contains(re.st.get().getMC()));
		jlr.setListData(replays);
		jlr.setSelectedValue(r, true);
		setRecd(r);
		change(false);
	}

	private void setMCs() {
		Vector<Replay> replays = new Vector<>(Replay.getMap().values());
		Vector<MapColc> cmc = new Vector<>(MapColc.values());
		Vector<MapColc> cols = new Vector<>(jlm.getSelectedValuesList());
		cmc.removeIf(mc -> {
			for (Replay re : replays)
				if (re.st.safeGet() != null && re.st.get().getMC() == mc)
					return false;
			return true;
		});
		jlm.setListData(cmc.toArray(new MapColc[0]));
		for (MapColc mc : cols)
			jlm.setSelectedValue(mc, true);
	}

	@Override
	protected void setRecd(Replay r) {
		super.setRecd(r);
		dele.setEnabled(r != null);
		rena.setEditable(r != null);
		rena.setText(r == null || r.rl == null ? "" : r.rl.id);
	}

	private void addListeners() {

		jlr.addListSelectionListener(arg0 -> {
			if (isAdj() || jlr.getValueIsAdjusting())
				return;
			setRecd(jlr.getSelectedValue());
		});

		rena.setLnr(x -> {
			if (isAdj() || jlr.getValueIsAdjusting())
				return;
			Replay r = jlr.getSelectedValue();
			String n = rena.getText();
			if (r == null || r.rl.id.equals(n))
				return;
			String name = Context.validate(rena.getText().trim(),'-');
			if (!Replay.getMap().containsKey(name) || Opts.conf("A replay named " + name + " already exists. Do you wish to overwrite?"))
				r.rename(name);
			rena.setText(r.rl.id);
			jlr.revalidate();
			jlr.repaint();
		});

		dele.addActionListener(arg0 -> {
			if (isAdj() || jlr.getValueIsAdjusting())
				return;
			Replay r = jlr.getSelectedValue();

			if (!Opts.conf("Are you sure you want to delete " + r.rl.id + "?"))
				return;

			File f = CommonStatic.ctx.getWorkspaceFile(r.rl.getPath() + ".replay");
			if (f.exists() && f.delete()) {
				Replay.getMap().remove(r.rl.id);
				setMCs();
				setList();
				setRecd(null);
			}
		});

		jlm.addListSelectionListener(l -> setList());

	}

	private void ini() {
		add(jspr);
		add(dele);
		add(rena);
		add(fil);

		add(jspm);
		jlm.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
				JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
				if (o instanceof MapColc.PackMapColc)
					jl.setIcon(UtilPC.resizeIcon(((MapColc.PackMapColc)o).pack.icon, UtilPC.iconSize, UtilPC.iconSize));
				return jl;
			}
		});
		addListeners();
	}

}
