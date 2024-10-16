package page.info;

import common.util.stage.MapColc;
import common.util.stage.RandStage;
import common.util.stage.Stage;
import common.util.stage.StageMap;
import page.JBTN;
import page.Page;
import page.battle.BattleSetupPage;
import page.battle.StRecdPage;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class StageViewPage extends StagePage {

	private static final long serialVersionUID = 1L;

	private final JBTN cpsm = new JBTN(0, "cpsm");
	private final JBTN cpst = new JBTN(0, "cpst");
	private final JBTN dgen = new JBTN(0, "dungeon");
	private final JBTN recd = new JBTN(0, "replay");
	private final JBTN search = new JBTN(0, "search");

	public StageViewPage(Page p, Collection<MapColc> collection) {
		super(p);
		collection.removeIf(mc -> mc.getStageCount() == 0);
		jlmc.setListData(new Vector<>(collection));

		ini();
	}

	public StageViewPage(Page p, Collection<MapColc> col, Stage st) {
		this(p, col);
		if (st == null)
			return;
		jlmc.setSelectedValue(st.getMC(), true);
		jlsm.setSelectedValue(st.getCont(), true);
		jlst.setSelectedValue(st, true);
	}

	@Override
	public void callBack(Object v) {
		super.callBack(v);
		if (v instanceof String && v.equals("prog"))
			renew();
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
		set(search, x, y, 200, 0, 200, 50);
	}

	@Override
	protected void setData(Stage st, int starId) {
		super.setData(st, starId);
		cpst.setEnabled(st != null);
		recd.setEnabled(st != null);
	}

	private void addListeners() {

		recd.setLnr(x -> changePanel(new StRecdPage(this, stage, false)));

		jlmc.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			List<MapColc> mcs = jlmc.getSelectedValuesList();
			if (mcs.isEmpty())
				return;
			Vector<StageMap> sms = new Vector<>();
			for (MapColc mc : mcs)
				if (mc.getSave(false) != null) {
					for (StageMap sm : mc.maps)
						if (mc.getSave(true).unlocked(sm) || mc.getSave(true).nearUnlock(sm))
							sms.add(sm);
				} else
					sms.addAll(mc.maps.getList());
			jlsm.setListData(sms);
			jlsm.setSelectedIndex(0);
		});

		jlsm.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			List<StageMap> sms = jlsm.getSelectedValuesList();
			cpsm.setEnabled(false);
			if (sms.isEmpty())
				return;
			cpsm.setEnabled(true);
			Vector<Stage> sts = new Vector<>();

			for (StageMap sm : sms)
				if (sm.getCont().getSave(false) != null) {
					Integer stInds = sm.getCont().getSave(true).cSt.get(sm);
					if (stInds == null) {
						if (!sm.list.isEmpty() && sm.unlockReq.isEmpty())
							sts.add(sm.list.get(0));
					} else if (stInds >= sm.list.size() - 1)
						sts.addAll(sm.list.getList());
					else
						for (int i = 0; i <= stInds; i++)
							sts.add(sm.list.get(i));
				} else
					sts.addAll(sm.list.getList());
			jlst.setListData(sts);
			jlst.setSelectedIndex(0);
		});

		jlst.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			Stage s = jlst.getSelectedValue();
			cpst.setEnabled(false);
			if (s == null)
				return;
			setData(s, 0);
		});

		cpsm.addActionListener(arg0 -> {
			List<StageMap> sms = jlsm.getSelectedValuesList();
			if (sms.isEmpty())
				return;
			MapColc mc = Stage.CLIPMC;
			for (StageMap sm : sms) {
				StageMap copy = sm.copy(mc);
				mc.maps.add(copy);
			}
		});

		cpst.addActionListener(arg0 -> {
			List<Stage> stages = jlst.getSelectedValuesList();
			if (stages.isEmpty())
				return;
			for (Stage stage : stages)
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
		add(recd);
		add(cpsm);
		add(cpst);
		add(dgen);
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
		if (mc == null || mc.getSave(false) == null)
			return;

		Vector<StageMap> sms = new Vector<>();
		for (StageMap sm : mc.maps)
			if (mc.getSave(true).unlocked(sm) || mc.getSave(true).nearUnlock(sm))
				sms.add(sm);
		jlsm.setListData(sms);

		StageMap sm = jlsm.getSelectedValue();
		if (sm == null) {
			if (jlst.getSelectedValue() != null)
				sm = jlst.getSelectedValue().getCont();
			else
				return;
		}
		Integer stInds = mc.getSave(true).cSt.get(sm);
		if (stInds == null) {
			if (!sm.list.isEmpty() && sm.unlockReq.isEmpty())
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