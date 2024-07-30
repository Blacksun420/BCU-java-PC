package page.info;

import common.util.stage.MapColc;
import common.util.stage.Stage;
import common.util.stage.StageMap;
import page.JBTN;
import page.MainLocale;
import page.Page;

import java.util.List;
import java.util.Vector;

public class StageFilterPage extends StagePage {

	private static final long serialVersionUID = 1L;

	private final List<Stage> stages;
	private final Vector<StageMap> maps = new Vector<>();
	private final JBTN pinp = new JBTN(MainLocale.PAGE, "pinp");

	public StageFilterPage(Page p, List<Stage> ls) {
		super(p);
		stages = ls;
		jlst.setListData(ls.toArray(new Stage[0]));
		ini();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(strt, x, y, 400, 0, 400, 50);
		set(pinp, x, y, 400, 1200, 400, 50);
		if (jspmc.isVisible()) {
			set(jspsm, x, y, 0, 50, 400, 1150);
			set(jspmc, x, y, 400, 50, 400, 500);
			set(jspst, x, y, 400, 550, 400, 650);
		} else if (jspsm.isVisible()) {
			set(jspsm, x, y, 0, 50, 400, 1150);
			set(jspst, x, y, 400, 50, 400, 1150);
		} else
			set(jspst, x, y, 400, 50, 400, 1150);
	}

	private void addListeners() {

		pinp.setLnr(l -> changePanel(new StageViewPage(this, MapColc.values(), stage)));

		jlst.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			Stage s = jlst.getSelectedValue();
			if (s == null)
				return;
			setData(s, 0);
		});

		jlsm.addListSelectionListener(ls -> {
			if (ls.getValueIsAdjusting())
				return;
			setSMSelection();
		});

		jlmc.addListSelectionListener(ls -> {
			if (ls.getValueIsAdjusting())
				return;
			setMCSelection();
		});
	}

	private void setMCSelection() {
		List<StageMap> smaps = jlsm.getSelectedValuesList();
		List<MapColc> mpcs = jlmc.getSelectedValuesList();
		if (mpcs.isEmpty())
			jlsm.setListData(maps);
		else {
			Vector<StageMap> nmps = new Vector<>(maps);
			nmps.removeIf(sm -> !mpcs.contains(sm.getCont()));
			jlsm.setListData(nmps);
		}
		for (StageMap sm : smaps)
			jlsm.setSelectedValue(sm, true);
		setSMSelection();
	}

	private void setSMSelection() {
		List<Stage> sstg = jlst.getSelectedValuesList();
		List<StageMap> sms = jlsm.getSelectedValuesList();
		Vector<Stage> nsts = new Vector<>(stages);
		if (sms.isEmpty()) {
			List<MapColc> mpcs = jlmc.getSelectedValuesList();
			if (mpcs.isEmpty()) {
				jlst.setListData(stages.toArray(new Stage[0]));
				return;
			}
			Vector<StageMap> nmps = new Vector<>(maps);
			nmps.removeIf(sm -> !mpcs.contains(sm.getCont()));
			nsts.removeIf(st -> !nmps.contains(st.getCont()));
		} else
			nsts.removeIf(st -> !sms.contains(st.getCont()));
		jlst.setListData(nsts);
		for (Stage s : sstg)
			jlst.setSelectedValue(s, true);
	}

	@Override
	protected void setData(Stage st, int starId) {
		super.setData(st, starId);
		pinp.setEnabled(st != null);
	}

	private void ini() {
		add(pinp);
		Vector<MapColc> mpcs = new Vector<>();
		for (Stage st : stages)
			if (!maps.contains(st.getCont())) {
				maps.add(st.getCont());
				if (!mpcs.contains(st.getMC()))
					mpcs.add(st.getMC());
			}
		jlmc.setListData(mpcs);
		jspmc.setVisible(mpcs.size() > 1);
		jlsm.setListData(maps);
		jspsm.setVisible(maps.size() > 1);

		addListeners();
	}

}