package page.info;

import common.pack.SaveData;
import common.util.stage.MapColc;
import common.util.stage.Stage;
import common.util.stage.StageMap;
import main.Opts;
import page.DefaultPage;
import page.JBTN;
import page.Page;
import page.battle.BattleSetupPage;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.LinkedList;

public class StagePage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	protected final JBTN strt = new JBTN(0, "start");
	private final StageTable jt = new StageTable(this);
	private final JScrollPane jspjt = new JScrollPane(jt);
	private final HeadTable info = new HeadTable(this);
	private final JScrollPane jspinfo = new JScrollPane(info);
	private final JBTN pres = new JBTN(0, "preset");

	protected Stage stage;
	private final JBTN binf = new JBTN(0, "info");
	private int star;

	protected final JList<MapColc> jlmc = new JList<>();
	protected final JScrollPane jspmc = new JScrollPane(jlmc);
	protected final JList<StageMap> jlsm = new JList<>();
	protected final JScrollPane jspsm = new JScrollPane(jlsm);
	protected final JList<Stage> jlst = new JList<>();
	protected final JScrollPane jspst = new JScrollPane(jlst);

	public StagePage(Page p) {
		super(p);
		ini();
	}

	public Stage getStage() {
		return stage;
	}

	@Override
	public void callBack(Object v) {
		if (v instanceof Integer)
			setData(stage, (int) v);
		else
			setData(stage, 0);
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		if (e.getSource() == jt)
			jt.clicked(e.getPoint());
		if (e.getSource() == info)
			info.clicked(e.getPoint());
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jspinfo, x, y, 800, 50, 1400, 350);
		set(jspjt, x, y, 800, 400, 1400, 800);
		set(pres, x, y, 1400, 0, 200, 50);
		jt.setRowHeight(size(x, y, 50));
		info.setRowHeight(size(x, y, 50));
		set(binf, x, y, 1600, 0, 200, 50);
	}

	protected void setData(Stage st, int starId) {
		stage = st;
		strt.setEnabled(st != null);
		pres.setEnabled(st != null && st.preset != null);
		if(st != null) {
			jt.setData(st, Math.min(starId, st.getCont().stars.length - 1));
			info.setData(st);
		}
		star = starId;
		binf.setEnabled(st != null && getInfo().length() > 6);
		jspjt.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
	}

	private void addListeners() {
		strt.addActionListener(arg0 -> {
			if (stage == null)
				return;
			changePanel(new BattleSetupPage(getThis(), stage, false));
		});

		binf.setLnr(x -> Opts.pop(getInfo(), stage + " info"));
	}

	private String getInfo() {
		StringBuilder str = new StringBuilder();
		if (stage.info != null)
			str = new StringBuilder(Interpret.infoHTML(stage.info, star));
		else if (stage.getLim(star).stageLimit != null)
			str = new StringBuilder(Interpret.stageLimHTML(stage.getLim(star).stageLimit));

		if (stage.id() == stage.getCont().list.size() - 1) {
			if (str.length() == 0)
				str.append("<html>");
			LinkedList<StageMap> newUnlocks = stage.getCont().getUnlockableMaps();
			if (!newUnlocks.isEmpty()) {
				str.append("<table><tr><th>Chapters that require clearing this chapter to be unlocked:</th></tr> ");
				for (StageMap newUnlock : newUnlocks)
					str.append("<tr><td>").append(newUnlock).append("</td></tr>");
			}
		} else if (stage.id() == 0 && !stage.getCont().unlockReq.isEmpty()) {
			if (str.length() == 0)
				str.append("<html>");
			str.append("<table><tr><th>Unlock Chapter Clear requirements:</th></tr> ");
			for (StageMap newUnlock : stage.getCont().unlockReq)
				str.append("<tr><td>").append(newUnlock).append("</td></tr>");
		}
		return str.toString();

		pres.setLnr(x -> {
			if (stage != null && stage.preset != null)
				Opts.pop(Interpret.readBattlePreset(stage.preset), "preset lineup");
		});
	}

	private void ini() {
		add(jspmc);
		add(jspsm);
		add(jspst);
		add(jspjt);
		add(jspinfo);
		add(strt);
		add(binf);
		binf.setEnabled(false);

		jlsm.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
				JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
				StageMap sm = (StageMap)o;
				SaveData sv = sm.getCont().getSave(false);
				if (sv == null || !sv.nearUnlock(sm)) {
					jl.setToolTipText(null);
					if (sv != null && sv.clear(sm))
						jl.setText("<html><strong>" + sm + "</strong></html>");
					return jl;
				}
				jl.setText("<html><strike>" + sm + "</strike></html>");
				StringBuilder sbl = new StringBuilder("<html><table><tr><th>Requires clearing:</th></tr>");
				for (StageMap lsm : sv.requirements(sm))
					sbl.append("<tr><td>").append(lsm).append("</td></tr>");
				sbl.append("</html>");
				jl.setToolTipText(sbl.toString());
				jl.setEnabled(false);

				return jl;
			}
		});
		jlmc.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
				JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
				if (o instanceof MapColc.PackMapColc)
					jl.setIcon(UtilPC.resizeIcon(((MapColc.PackMapColc)o).pack.icon, UtilPC.iconSize, UtilPC.iconSize));
				return jl;
			}
		});
		strt.setEnabled(false);
		add(pres);
		pres.setEnabled(false);
		info.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				info.hover(e.getPoint());
			}
		});
		addListeners();
	}

}