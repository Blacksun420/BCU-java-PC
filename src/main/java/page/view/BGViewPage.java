package page.view;

import common.pack.Identifier;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.pack.Background;
import page.*;
import page.awt.BBBuilder;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BGViewPage extends DefaultPage implements SupPage<Background> {

	private static final long serialVersionUID = 1L;

	private final JTG prev = new JTG(MainLocale.PAGE, "preview");
	private final JList<Background> jlst = new JList<>();
	private final JScrollPane jspst = new JScrollPane(jlst);
	private final JLabel jl = new JLabel();
	protected final ViewBox vb = BBBuilder.def.getViewBox();

	public BGViewPage(Page p) {
		super(p);
		List<Background> bgs = new ArrayList<>();
		for (PackData pac : UserProfile.getAllPacks())
			bgs.addAll(pac.bgs.getList());

		jlst.setListData(bgs.toArray(new Background[0]));
		ini();
	}

	public BGViewPage(Page p, String pac) {
		super(p);
		jlst.setListData(new Vector<>(UserProfile.getAll(pac, Background.class)));
		ini();
	}

	public BGViewPage(Page front, String pac, Identifier<Background> bg) {
		this(front, pac);
		Point p = getXY().toPoint();
		componentResized(p.x, p.y); //Width fucks up
		jlst.setSelectedValue(Identifier.get(bg), false);
	}

	@Override
	public Background getSelected() {
		return jlst.getSelectedValue();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(prev, x, y, 300, 0, 200, 50);
		set(jspst, x, y, 50, 100, 300, 1100);
		set((Canvas) vb, x, y, 400, 50, 1800, 1100);
		set(jl, x, y, 400, 50, 1800, 1100);
	}

	@Override
	public synchronized void onTimer(int t) {
		super.onTimer(t);
		Background s = jlst.getSelectedValue();
		if (s == null)
			return;
		if (prev.isSelected()) {
			vb.update(1);
			vb.paint();
		}
	}

	private void addListeners() {
		jlst.addListSelectionListener(arg0 -> {
			if (needResize)
				return;

			if (arg0.getValueIsAdjusting())
				return;
			Background s = jlst.getSelectedValue();
			if (s == null)
				return;
			jl.setIcon(UtilPC.getBg(s, jl.getWidth(), jl.getHeight()));
			vb.setBackground(s);
		});

		prev.addActionListener(arg0 -> {
			remove((Canvas) vb);
			remove(jl);
			if (prev.isSelected())
				add((Canvas) vb);
			else
				add(jl);
		});
	}

	private void ini() {
		add(prev);
		add(jspst);
		add(jl);
		addListeners();
	}
}
