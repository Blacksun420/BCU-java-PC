package page.info;

import common.util.stage.MapColc;
import common.util.stage.RandStage;
import common.util.stage.Stage;
import common.util.stage.StageMap;
import main.Opts;
import page.JBTN;
import page.Page;
import page.battle.BattleSetupPage;
import page.battle.StRecdPage;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class StageViewPage extends StagePage {

	private static final long serialVersionUID = 1L;

	private final JList<MapColc> jlmc = new JList<>();
	private final JScrollPane jspmc = new JScrollPane(jlmc);
	private final JList<StageMap> jlsm = new JList<>();
	private final JScrollPane jspsm = new JScrollPane(jlsm);
	private final JList<Stage> jlst = new JList<>();
	private final JScrollPane jspst = new JScrollPane(jlst);
	private final JBTN cpsm = new JBTN(0, "cpsm");
	private final JBTN cpst = new JBTN(0, "cpst");
	private final JBTN dgen = new JBTN(0, "dungeon");
	private final JBTN recd = new JBTN(0, "replay");
	private final JBTN info = new JBTN(0, "info");
	private final JBTN search = new JBTN(0, "search");
	public boolean nonSt;

	public StageViewPage(Page p, Collection<MapColc> collection) {
		super(p);
		jlmc.setListData(new Vector<>(collection));

		ini();
		resized();
	}

	public StageViewPage(Page p, Collection<MapColc> col, Stage st) {
		this(p, col);
		if (st == null)
			return;
		jlmc.setSelectedValue(st.getCont().getCont(), true);
		jlsm.setSelectedValue(st.getCont(), true);
		jlst.setSelectedValue(st, true);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jspsm, x, y, 0, 50, 400, 1150);
		set(jspmc, x, y, 400, 50, 400, 500);
		set(jspst, x, y, 400, 550, 400, 650);
		set(cpsm, x, y, 50, 1200, 300, 50);
		set(cpst, x, y, 450, 1200, 300, 50);
		set(dgen, x, y, 600, 0, 200, 50);
		set(strt, x, y, 400, 0, 200, 50);
		set(recd, x, y, 1850, 0, 200, 50);
		set(info, x, y, 1600, 0, 200, 50);
		set(search, x, y, 200, 0, 200, 50);
	}

	@Override
	protected void setData(Stage st) {
		super.setData(st);
		info.setEnabled(st != null && st.info != null);
		cpst.setEnabled(st != null);
		recd.setEnabled(st != null);
	}

	private void addListeners() {

		info.setLnr(x -> Opts.pop(stage.info.getHTML(), "stage info"));

		recd.setLnr(x -> changePanel(new StRecdPage(this, stage, false)));

		jlmc.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			MapColc mc = jlmc.getSelectedValue();
			if (mc == null)
				return;

			if (!nonSt && mc.getSave() != null) {
				Vector<StageMap> sms = new Vector<>();
				for (StageMap sm : mc.maps)
					if (sm.unlockReq.isEmpty() || mc.getSave().cSt.containsKey(sm.id))
						sms.add(sm);
				jlsm.setListData(sms);
			} else
				jlsm.setListData(mc.maps.toArray());
			jlsm.setSelectedIndex(0);
		});

		jlsm.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			StageMap sm = jlsm.getSelectedValue();
			cpsm.setEnabled(false);
			if (sm == null)
				return;
			cpsm.setEnabled(true);

			if (!nonSt && sm.getCont().getSave() != null) {
				Integer stInds = sm.getCont().getSave().cSt.get(sm.id);
				if (stInds == null) {
					if (sm.list.size() > 0 && sm.unlockReq.isEmpty())
						jlst.setListData(new Stage[]{sm.list.get(0)});
					else
						jlst.setListData(sm.list.toArray());
				} else if (stInds >= sm.list.size() - 1)
					jlst.setListData(sm.list.toArray());
				else {
					Vector<Stage> sts = new Vector<>(stInds + 1);
					for (int i = 0; i <= stInds; i++)
						sts.add(sm.list.get(i));
					jlst.setListData(sts);
				}
			} else
				jlst.setListData(sm.list.toArray());
			jlst.setSelectedIndex(0);
		});

		jlst.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			Stage s = jlst.getSelectedValue();
			cpst.setEnabled(false);
			if (s == null)
				return;
			setData(s);
		});

		cpsm.addActionListener(arg0 -> {
			StageMap sm = jlsm.getSelectedValue();
			if (sm == null)
				return;
			MapColc mc = Stage.CLIPMC;
			StageMap copy = sm.copy(mc);
			mc.maps.add(copy);
		});

		cpst.addActionListener(arg0 -> {
			Stage stage = jlst.getSelectedValue();
			if (stage == null)
				return;
			Stage.CLIPSM.add(stage.copy(Stage.CLIPSM));
		});

		dgen.setLnr(x -> {
			StageMap sm = jlsm.getSelectedValue();
			if (sm == null)
				changePanel(new StageRandPage(getThis(), jlmc.getSelectedValue()));
			else {
				Stage s = RandStage.getStage(sm);
				changePanel(new BattleSetupPage(getThis(), s, 0));
			}
		});

		search.setLnr(x -> changePanel(new StageSearchPage(getThis())));

	}

	private void ini() {
		add(jspmc);
		add(jspsm);
		add(jspst);
		add(recd);
		add(cpsm);
		add(cpst);
		add(dgen);
		add(info);
		add(search);
		cpsm.setEnabled(false);
		cpst.setEnabled(false);
		recd.setEnabled(false);
		addListeners();
	}

	public List<Stage> getSelectedStages() {
		return jlst.getSelectedValuesList();
	}

	@Override
	public void renew() {
		MapColc mc = jlmc.getSelectedValue();
		if (mc == null || mc.getSave() == null)
			return;

		Vector<StageMap> sms = new Vector<>();
		for (StageMap sm : mc.maps)
			if (sm.unlockReq.isEmpty() || mc.getSave().cSt.containsKey(sm.id))
				sms.add(sm);
		jlsm.setListData(sms);

		StageMap sm = jlsm.getSelectedValue();
		if (sm == null) {
			if (jlst.getSelectedValue() != null)
				sm = jlst.getSelectedValue().getCont();
			else
				return;
		}
		Integer stInds = mc.getSave().cSt.get(sm.id);
		if (stInds == null) {
			if (sm.list.size() > 0 && sm.unlockReq.isEmpty())
				jlst.setListData(new Stage[]{sm.list.get(0)});
			else
				jlst.setListData(sm.list.toArray());
		} else if (stInds >= sm.list.size() - 1)
			jlst.setListData(sm.list.toArray());
		else {
			Vector<Stage> sts = new Vector<>(stInds + 1);
			for (int i = 0; i <= stInds; i++)
				sts.add(sm.list.get(i));
			jlst.setListData(sts);
		}
	}
}
