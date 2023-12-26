package page.view;

import common.pack.Identifier;
import common.util.stage.CastleImg;
import common.util.stage.CastleList;
import page.*;
import utilpc.UtilPC;

import javax.swing.*;
import java.util.Collection;
import java.util.Vector;

public class CastleViewPage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	private final JList<CastleList> jlsm = new JList<>();
	private final JScrollPane jspsm = new JScrollPane(jlsm);
	private final JList<CastleImg> jlst = new JList<>();
	private final JScrollPane jspst = new JScrollPane(jlst);
	private final JLabel jl = new JLabel();
	private final JL jbs = new JL(MainLocale.PAGE, "bspwn");
	private final JL bs = new JL();

	public CastleViewPage(Page p) {
		this(p, CastleList.map().values());
	}

	public CastleViewPage(Page p, CastleList sele) {
		this(p);
		jlsm.setSelectedValue(sele, true);
	}

	public CastleViewPage(Page p, Collection<CastleList> list) {
		super(p);
		Vector<CastleList> vec = new Vector<>(list);
		jlsm.setListData(vec);
		ini();
	}

	public CastleViewPage(Page p, Collection<CastleList> defcas, Identifier<CastleImg> id) {
		this(p, defcas);
		if (id != null) {
			CastleList abcas = (CastleList) id.getCont();
			jlsm.setSelectedValue(abcas, true);
			jlst.setSelectedValue(id.get(), true);
		}
	}

	public Identifier<CastleImg> getVal() {
		CastleImg img = jlst.getSelectedValue();
		return img == null ? null : img.getID();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jspsm, x, y, 50, 100, 300, 1100);
		set(jspst, x, y, 400, 550, 300, 650);
		set(jl, x, y, 800, 50, 1000, 1000);
		set(jbs, x, y, 400, 500, 200, 50);
		set(bs, x, y, 600, 500, 100, 50);
	}

	private void addListeners() {
		jlsm.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			CastleList sm = jlsm.getSelectedValue();
			if (sm == null)
				return;
			jlst.setListData(new Vector<>(sm.getList()));
			jlst.setSelectedIndex(0);
		});

		jlst.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			CastleImg s = jlst.getSelectedValue();
			if (s == null)
				jl.setIcon(null);
			else {
				jl.setIcon(UtilPC.getIcon(s.img));
				bs.setText(String.valueOf(s.boss_spawn));
			}
		});

	}

	private void ini() {
		add(jspsm);
		add(jspst);
		add(jl);
		add(jbs);
		add(bs);
		addListeners();
	}

}
