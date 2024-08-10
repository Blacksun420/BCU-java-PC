package page.info.edit;

import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.stage.MapColc;
import common.util.stage.Stage;
import common.util.stage.StageMap;
import common.util.stage.info.CustomStageInfo;
import common.util.unit.AbEnemy;
import common.util.unit.Enemy;
import main.MainBCU;
import page.*;
import page.battle.BattleSetupPage;
import page.battle.StRecdPage;
import page.info.filter.EnemyFindPage;
import page.support.AnimLCR;
import page.support.RLFIM;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class StageEditPage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	public static void redefine() {
		StageEditTable.redefine();
		LimitTable.redefine();
		SCGroupEditTable.redefine();
	}

	private final JBTN strt = new JBTN(0, "start");
	private final JBTN veif = new JBTN(0, "veif");
	private final JBTN cpsm = new JBTN(0, "cpsm");
	private final JBTN cpst = new JBTN(0, "cpst");
	private final JBTN ptsm = new JBTN(0, "ptsm");
	private final JBTN ptst = new JBTN(0, "ptst");
	private final JBTN rmsm = new JBTN(0, "rmsm");
	private final JBTN rmst = new JBTN(0, "rmst");
	private final JBTN recd = new JBTN(0, "replay");
	private final JTF enam = new JTF();
	private final StageEditTable jt;
	private final JScrollPane jspjt;
	private final RLFIM<StageMap> jlsm = new RLFIM<>(() -> this.changing = true, () -> changing = false,
			this::finishRemoving, this::setAA, StageMap::new);
	private final JScrollPane jspsm = new JScrollPane(jlsm);
	private final RLFIM<Stage> jlst = new RLFIM<>(() -> this.changing = true, () -> changing = false,
			this::finishRemoving, this::setAB, Stage::new);
	private final JScrollPane jspst = new JScrollPane(jlst);
	private final JList<StageMap> lpsm = new JList<>(Stage.CLIPMC.maps.toArray());
	private final JScrollPane jlpsm = new JScrollPane(lpsm);
	private final JList<Stage> lpst = new JList<>();
	private final JScrollPane jlpst = new JScrollPane(lpst);
	private final JBTN adds = new JBTN(0, "add");
	private final JBTN rems = new JBTN(0, "rem");
	private final JBTN addl = new JBTN(0, "addl");
	private final JBTN reml = new JBTN(0, "reml");
	private final JBTN advs = new JBTN(0, "advance");
	private final JList<AbEnemy> jle = new JList<>();
	private final JScrollPane jspe = new JScrollPane(jle);

	final HeadEditTable hinf;
	final StageLimitTable sinf;
	private final JTG data = new JTG(MainLocale.PAGE, "head1");

	private final MapColc mc;
	private final UserPack pack;
	private final EnemyFindPage efp;

	private boolean changing = false;
	private Stage stage;

	public StageEditPage(Page p, MapColc map, UserPack pac) {
		super(p);
		mc = map;
		pack = pac;
		jt = new StageEditTable(this, pac);
		jspjt = new JScrollPane(jt);
		hinf = new HeadEditTable(this, pac);
		sinf = new StageLimitTable(this, pac);
		jlsm.setListData(mc, mc.maps);
		jle.setListData(UserProfile.getAll(pack.getSID(), Enemy.class).toArray(new Enemy[0]));
		efp = new EnemyFindPage(getThis(), true, pac);
		ini();
	}

	@Override
	public void callBack(Object newParam) {
		super.callBack(newParam);
		jlst.revalidate();
		jlst.repaint();

		setData(stage);
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		if (e.getSource() == jt && !e.isShiftDown())
			jt.clicked(e);
	}

	@Override
	protected void renew() {
		hinf.renew();
		sinf.renew();
		jt.updateAbEnemy();
		if (efp != null)
			enam.setText(efp.getSearch());
		renewEList();
	}

	public void renewEList() {
		Vector<AbEnemy> v = new Vector<>();
		if (efp.getList() != null)
			v.addAll(efp.getList());
		jle.setListData(v);
		fireDimensionChanged();
	}

	@Override
	protected synchronized void resized(int x, int y) {
		super.resized(x, y);
		if (data.isSelected()) {
			set(sinf, x, y, 900, 50, 1400, 300);
			set(hinf, x, y, 900, 50, 0, 0);
		} else {
			set(sinf, x, y, 900, 50, 0, 0);
			set(hinf, x, y, 900, 50, 1400, 300);
		}
		int mw = (int)(1400 / 8 * 1.5f);
		set(addl, x, y, 900, 400, mw, 50);
		set(reml, x, y, 900 + mw, 400, mw, 50);
		set(recd, x, y, 2300 - mw * 2, 400, mw, 50);
		set(advs, x, y, 2300 - mw, 400, mw, 50);
		set(jspjt, x, y, 900, 450, 1400, 850);

		set(data, x, y, 900, 0, 1400, 50);
		set(jspsm, x, y, 0, 50, 300, 800);
		set(cpsm, x, y, 0, 850, 300, 50);
		set(ptsm, x, y, 0, 900, 300, 50);
		set(rmsm, x, y, 0, 950, 300, 50);
		set(jlpsm, x, y, 0, 1000, 300, 300);

		set(strt, x, y, 300, 0, 300, 50);
		set(adds, x, y, 300, 50, 150, 50);
		set(rems, x, y, 450, 50, 150, 50);
		set(jspst, x, y, 300, 100, 300, 750);
		set(cpst, x, y, 300, 850, 300, 50);
		set(ptst, x, y, 300, 900, 300, 50);
		set(rmst, x, y, 300, 950, 300, 50);
		set(jlpst, x, y, 300, 1000, 300, 300);

		set(veif, x, y, 600, 0, 300, 50);
		set(enam, x, y, 600, 50, 300, 50);
		set(jspe, x, y, 600, 100, 300, 1200);
		jt.setRowHeight(size(x, y, 50));
	}

	private void addListeners$0() {
		strt.setLnr(x -> changePanel(new BattleSetupPage(getThis(), stage, 1)));

		advs.setLnr(x -> changePanel(new AdvStEditPage(getThis(), stage)));

		recd.setLnr(x -> changePanel(new StRecdPage(getThis(), stage, true)));

		addl.addActionListener(arg0 -> {
			int ind = jt.addLine(jle.getSelectedValue());
			setData(stage);
			if (ind < 0)
				jt.clearSelection();
			else
				jt.addRowSelectionInterval(ind, ind);
		});

		reml.addActionListener(arg0 -> {
			int ind = jt.remLine();
			setData(stage);
			if (ind < 0)
				jt.clearSelection();
			else
				jt.addRowSelectionInterval(ind, ind);
		});

		veif.setLnr(x -> changePanel(efp));

		if (MainBCU.searchPerKey) {
			enam.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					efp.search(enam.getText(), true);
					renewEList();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					efp.search(enam.getText(), true);
					renewEList();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					efp.search(enam.getText(), true);
					renewEList();
				}
			});
		} else
			enam.setLnr(e -> {
				efp.search(enam.getText(), true);
				renewEList();
			});
	}

	private void addListeners$1() {

		jlsm.addListSelectionListener(arg0 -> {
			if (changing || arg0.getValueIsAdjusting())
				return;
			changing = true;
			setAA(jlsm.getSelectedValue());
			changing = false;
		});

		jlst.addListSelectionListener(arg0 -> {
			if (changing || arg0.getValueIsAdjusting())
				return;
			changing = true;
			setAB(jlst.getSelectedValue());
			changing = false;
		});

		lpsm.addListSelectionListener(arg0 -> {
			if (changing || arg0.getValueIsAdjusting())
				return;
			changing = true;
			setBA(lpsm.getSelectedValue());
			changing = false;
		});

		lpst.addListSelectionListener(arg0 -> {
			if (changing || arg0.getValueIsAdjusting())
				return;
			changing = true;
			setBB(lpst.getSelectedValue());
			changing = false;
		});

	}

	private void addListeners$2() {

		cpsm.addActionListener(arg0 -> {
			StageMap sm = jlsm.getSelectedValue();
			MapColc col = Stage.CLIPMC;
			StageMap copy = sm.copy(col);
			col.maps.add(copy);
			changing = true;
			lpsm.setListData(col.maps.toArray());
			lpsm.setSelectedValue(copy, true);
			setBA(copy);
			changing = false;
		});

		cpst.addActionListener(arg0 -> {
			Stage copy = stage.copy(Stage.CLIPSM);
			Stage.CLIPSM.add(copy);
			changing = true;
			lpst.setListData(Stage.CLIPSM.list.toArray());
			lpst.setSelectedValue(copy, true);
			lpsm.setSelectedIndex(0);
			setBB(copy);
			changing = false;
		});

		ptsm.addActionListener(arg0 -> {
			StageMap sm = lpsm.getSelectedValue();
			StageMap ni = sm.copy(mc);
			mc.maps.add(ni);
			changing = true;
			jlsm.setListData(mc, mc.maps);
			jlsm.setSelectedValue(ni, true);
			setBA(ni);
			changing = false;
		});

		ptst.addActionListener(arg0 -> {
			StageMap sm = jlsm.getSelectedValue();
			stage = lpst.getSelectedValue().copy(sm);
			sm.add(stage);
			changing = true;
			jlst.setListData(sm, sm.list);
			jlst.setSelectedValue(stage, true);
			setBB(stage);
			changing = false;
		});

		rmsm.addActionListener(arg0 -> {
			int ind = lpsm.getSelectedIndex();
			MapColc col = Stage.CLIPMC;
			col.maps.remove(lpsm.getSelectedValue());
			changing = true;
			lpsm.setListData(col.maps.toArray());
			lpsm.setSelectedIndex(ind - 1);
			setBA(lpsm.getSelectedValue());
			changing = false;
		});

		rmst.addActionListener(arg0 -> {
			StageMap sm = lpsm.getSelectedValue();
			Stage st = lpst.getSelectedValue();
			int ind = lpst.getSelectedIndex();
			sm.list.remove(st);
			changing = true;
			lpst.setListData(sm.list.toArray());
			lpst.setSelectedIndex(ind - 1);
			setBB(lpst.getSelectedValue());
			changing = false;
		});

		adds.setLnr(jlst::addItem);

		rems.setLnr(jlst::deleteItem);

		data.setLnr(x -> {
			data.setText(MainLocale.PAGE, "head" + (data.isSelected() ? "0" : "1"));
			needResize = true;
		});
	}

	private void checkPtsm() {
		StageMap sm = lpsm.getSelectedValue();
		if (sm == null) {
			ptsm.setEnabled(false);
			return;
		}
		Set<String> set = new TreeSet<>();
		for (Stage st : sm.list)
			set.addAll(st.isSuitable(pack));
		ptsm.setEnabled(set.size() == 0);
		if (set.size() > 0)
			ptsm.setToolTipText("requires: " + set);

	}

	private void checkPtst() {
		Stage st = lpst.getSelectedValue();
		StageMap sm = jlsm.getSelectedValue();
		if (st == null || sm == null)
			ptst.setEnabled(false);
		else {
			Set<String> set = st.isSuitable(pack);
			ptst.setEnabled(set.isEmpty());
			if (!set.isEmpty())
				ptst.setToolTipText("requires: " + set);
		}
		rmst.setEnabled(st != null);
	}

	private void ini() {
		add(veif);
		add(enam);
		add(adds);
		add(rems);
		add(jspjt);
		add(hinf);
		add(sinf);
		add(strt);
		add(jspsm);
		add(jspst);
		add(addl);
		add(reml);
		add(jspe);
		add(cpsm);
		add(cpst);
		add(ptsm);
		add(ptst);
		add(rmsm);
		add(rmst);
		add(jlpsm);
		add(jlpst);
		add(recd);
		add(advs);
		add(data);
		setAA(null);
		setBA(null);
		jle.setCellRenderer(new AnimLCR());
		jt.ini();
		enam.setHintText(get(MainLocale.PAGE,"search"));
		addListeners$0();
		addListeners$1();
		addListeners$2();

	}

	private void setAA(StageMap sm) {
		sinf.setData(sm);
		if (sm == null) {
			jlst.setListData(null, null);
			setAB(null);
			cpsm.setEnabled(false);
			ptst.setEnabled(false);
			adds.setEnabled(false);
			return;
		}
		jlst.setListData(sm, sm.list);
		if (sm.list.size() == 0) {
			jlst.clearSelection();
			cpsm.setEnabled(false);
			adds.setEnabled(true);
			checkPtst();
			setAB(null);
			return;
		}
		jlst.setSelectedIndex(0);
		cpsm.setEnabled(true);
		adds.setEnabled(true);
		checkPtst();
		setAB(sm.list.getList().get(0));
	}

	private void finishRemoving(Object obj) {
		if (obj instanceof StageMap) {
			StageMap stm = (StageMap)obj;
			stm.getCont().getSave(true).cSt.remove(stm);
			for (Stage s : stm.list)
				if (s.info != null)
					((CustomStageInfo)s.info).destroy(false);
			for (Stage s : stm.list)
				for (CustomStageInfo si : ((MapColc.PackMapColc)mc).si)
					si.remove(s);
		} else {
			Stage st = (Stage)obj;
			MapColc.PackMapColc pmc = (MapColc.PackMapColc)mc;
			if (st.getCont().list.size() == 1)
				pmc.getSave(true).cSt.remove(st.getCont());
			if (pmc.getSave(true).cSt.containsKey(st.getCont()))
				pmc.getSave(true).cSt.put(st.getCont(), pmc.getSave(true).cSt.get(st.getCont()) - 1);

			if (st.info != null)
				((CustomStageInfo)st.info).destroy(false);
			for (CustomStageInfo si : pmc.si)
				si.remove(st);
		}
	}

	private void setAB(Stage st) {
		if (st == null) {
			setData(lpst.getSelectedValue());
			cpst.setEnabled(false);
			rems.setEnabled(false);
			return;
		}
		cpst.setEnabled(true);
		rems.setEnabled(true);
		lpst.clearSelection();
		checkPtst();
		setData(st);
	}

	private void setBA(StageMap sm) {
		if (sm == null) {
			lpst.setListData(new Stage[0]);
			ptsm.setEnabled(false);
			rmsm.setEnabled(false);
			setBB(null);
			return;
		}
		lpst.setListData(sm.list.toArray());
		rmsm.setEnabled(sm != Stage.CLIPSM);
		if (sm.list.size() == 0) {
			lpst.clearSelection();
			ptsm.setEnabled(false);
			setBB(null);
			return;
		}
		lpst.setSelectedIndex(0);
		setBB(sm.list.getList().get(0));
		checkPtsm();

	}

	private void setBB(Stage st) {
		if (st == null) {
			setData(jlst.getSelectedValue());
			ptst.setEnabled(false);
			rmst.setEnabled(false);
			return;
		}
		cpst.setEnabled(false);
		checkPtst();
		jlst.clearSelection();
		setData(st);
	}

	private void setData(Stage st) {
		stage = st;
		hinf.setData(st);
		jt.setData(st);
		strt.setEnabled(st != null);
		recd.setEnabled(st != null);
		advs.setEnabled(st != null);
		jspjt.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
	}
}