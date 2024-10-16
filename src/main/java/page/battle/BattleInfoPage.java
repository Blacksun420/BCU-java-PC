package page.battle;

import common.CommonStatic;
import common.battle.*;
import common.battle.entity.Entity;
import common.pack.SaveData;
import common.util.Data;
import common.util.stage.Replay;
import common.util.stage.Stage;
import common.util.unit.AbForm;
import common.util.unit.Form;
import io.BCMusic;
import main.MainBCU;
import main.Opts;
import page.*;
import page.awt.BBBuilder;
import page.battle.BattleBox.OuterBox;
import utilpc.Interpret;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;
import java.util.*;

public class BattleInfoPage extends KeyHandler implements OuterBox {

	private static final long serialVersionUID = 1L;

	public static boolean DEF_LARGE = false;

	public static BattleInfoPage current = null;

	public static void redefine() {
		ComingTable.redefine();
		TotalDamageTable.redefine();
		EnemyDamageTable.redefine();
	}

	private final JBTN paus = new JBTN(MainLocale.PAGE, "pause");
	private final JBTN next = new JBTN(MainLocale.PAGE, "nextf");
	private final JBTN rply = new JBTN();
	private final JBTN row = new JBTN();
	private final EntityTable ut = new EntityTable(-1, false);
	private final EntityTable ust = new EntityTable(-1, true);
	private final ComingTable ct = new ComingTable(this);
	private final EntityTable et = new EntityTable(1, false);
	private final EntityTable est = new EntityTable(1, true);
	private final TotalDamageTable utd;
	private final EnemyDamageTable etd;
	private final JScrollPane eup = new JScrollPane(ut);
	private final JScrollPane eusp = new JScrollPane(ust);
	private final JScrollPane eep = new JScrollPane(et);
	private final JScrollPane eesp = new JScrollPane(est);
	private final JScrollPane ctp = new JScrollPane(ct);
	private final JScrollPane utdsp;
	private final JScrollPane etdsp;
	private final JTG ustat = new JTG(MainLocale.INFO, "stat");
	private final JTG estat = new JTG(MainLocale.INFO, "stat");
	private final JLabel ebase = new JLabel();
	private final JLabel ubase = new JLabel();
	private final JLabel timer = new JLabel();
	private final JLabel ecount = new JLabel();
	private final JLabel ucount = new JLabel();
	private final JLabel stream = new JLabel();
	private final JLabel respawn = new JLabel();
	private final JTG jtb = new JTG(MainLocale.PAGE, "larges");
	private final JSlider jsl = new JSlider();
	private final BattleBox bb;
	private final BattleField basis;

	private boolean pause = false, changedBG = false;
	private Replay recd;
	private boolean backClicked = false;

	private byte spe = 0;
	private int upd = 0;
	private boolean musicChanged = false, exPopupShown = false;
	private float dataPopup = 75;
	private final SaveData packData;

	/**
	 * Creates a new Battle Page
	 * @param p The previous page
	 * @param rec Will be null if this battle is not began from a replay
	 * @param conf If the value is 0, lineup will be randomized, if it's 1, lineup won't be changed
	 */
	public BattleInfoPage(Page p, Replay rec, int conf) {
		super(p);
		recd = rec;
		basis = new SBRply(rec);
		if ((conf & 1) == 0)
			bb = BBBuilder.def.getDef(this, basis);
		else
			bb = BBBuilder.def.getRply(this, basis, rec.rl.id, (conf & 4) != 0);
		jtb.setSelected((conf & 2) != 0);
		jtb.setEnabled((conf & 1) == 0);
		ct.setData(basis.sb.st, rec.star);
		utd = new TotalDamageTable(basis);
		utdsp = new JScrollPane(utd);
		etd = new EnemyDamageTable(basis);
		etdsp = new JScrollPane(etd);

		jsl.setMaximum(((SBRply) basis).size());
		ini();
		rply.setText(0, recd.rl == null ? "save" : "start");
		packData = null;
	}

	protected BattleInfoPage(BattleInfoPage p, SBRply rpl) {
		super(p);
		SBCtrl ctrl = rpl.transform(this);
		bb = BBBuilder.def.getCtrl(this, ctrl);
		pause = true;
		basis = ctrl;
		ct.setData(basis.sb.st, basis.sb.est.star);
		utd = new TotalDamageTable(basis);
		utdsp = new JScrollPane(utd);
		etd = new EnemyDamageTable(basis);
		etdsp = new JScrollPane(etd);
		jtb.setSelected(DEF_LARGE);

		ini();
		rply.setText(0, "rply");
		current = this;
		packData = null;
	}

	protected BattleInfoPage(Page p, Stage st, int star, BasisLU bl, int conf, byte saveMode) {
		super(p);
		long seed = new Random().nextLong();

		BasisLU lu = bl.copy();
		if (CommonStatic.getConfig().realLevel)
			lu.simulateBCLeveling();

		packData = st.getMC().getSave(false);
		SBCtrl sb = new SBCtrl(this, st, star, lu, conf, seed, saveMode);
		bb = BBBuilder.def.getCtrl(this, sb);
		basis = sb;
		ct.setData(basis.sb.st, basis.sb.est.star);
		jtb.setSelected(DEF_LARGE);
		utd = new TotalDamageTable(basis);
		utdsp = new JScrollPane(utd);
		etd = new EnemyDamageTable(basis);
		etdsp = new JScrollPane(etd);

		ini();
		rply.setText(0, "rply");
		current = this;
	}

	@Override
	public void callBack(Object o) {
		if (o instanceof String) {
			if (o.equals("music")) {
				if (BCMusic.play)
					renew();
				else
					BCMusic.stopAll();
			}
			return;
		}
		BCMusic.stopAll();
		if(o instanceof Stage) {
			changePanel(new BattleInfoPage(getFront(), (Stage) o, 0, basis.sb.b, 0, (byte)0));
		} else
			changePanel(getFront());
	}

	@Override
	public int getSpeed() {
		return spe;
	}

	@Override
	protected synchronized void keyTyped(KeyEvent e) {
		if (spe > -5 && e.getKeyChar() == ',') {
			spe--;
			bb.paint();
			bb.reset();
		} else if (spe < 8 && e.getKeyChar() == '.') {
			spe++;
			bb.paint();
			bb.reset();
		}
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		if (e.getSource() == bb)
			bb.click(e.getPoint(), e.getButton());
		else if (!jtb.isSelected())
			updateTables();
	}

	@Override
	protected void mouseDragged(MouseEvent e) {
		if (e.getSource() == bb) {
			bb.drag(e.getPoint());
			if (pause)
				bb.paint();
		}
	}

	@Override
	protected void mousePressed(MouseEvent e) {
		if (e.getSource() == bb)
			bb.press(e.getPoint());
	}

	@Override
	protected void mouseReleased(MouseEvent e) {
		if (e.getSource() == bb)
			bb.release();
	}

	@Override
	protected void mouseWheel(MouseEvent e) {
		if (e.getSource() == bb)
			bb.wheeled(e.getPoint(), ((MouseWheelEvent) e).getWheelRotation());
	}

	@Override
	protected void renew() {
		backClicked = false;

		if (basis.sb.mus != null) {
			if(BCMusic.BG != null)
				BCMusic.BG.stop();

			BCMusic.play(basis.sb.mus);
			return;
		}

		if (basis.sb.getEBHP() < basis.sb.st.mush)
			if(basis.sb.st.mush == 0 || basis.sb.st.mush == 100)
				BCMusic.play(basis.sb.st.mus1);
			else {
				if(BCMusic.BG != null)
					BCMusic.BG.stop();

				BCMusic.play(basis.sb.st.mus1);
			}
		else
			BCMusic.play(basis.sb.st.mus0);
	}

	@Override
	protected synchronized void resized(int x, int y) {
		super.resized(x, y);
		set(jtb, x, y, 2100, 0, 200, 50);
		if (jtb.isSelected()) {
			set(paus, x, y, 700, 0, 200, 50);
			set(rply, x, y, 900, 0, 200, 50);
			set(stream, x, y, 900, 0, 400, 50);
			set(next, x, y, 1100, 0, 200, 50);
			set(row, x, y, 1300, 0, 200, 50);
			set(ebase, x, y, 240, 0, 600, 50);
			set(timer, x, y, 1500, 0, 200, 50);
			set(ubase, x, y, 1740, 0, 360, 50);
			set((Canvas) bb, x, y, 190, 50, 1920, 1200);
			set(ctp, x, y, 0, 0, 0, 0);
			set(eep, x, y, 50, 100, 0, 0);
			set(eesp, x, y, 50, 100, 0, 0);
			set(eup, x, y, 50, 400, 0, 0);
			set(eusp, x, y, 50, 400, 0, 0);
			set(utdsp, x, y, 1650, 850, 0, 0);
			set(etdsp, x, y, 50, 850, 0, 0);
			set(ecount, x, y, 50, 50, 0, 0);
			set(estat, x, y, 650, 50, 0, 0);
			set(ucount, x, y, 50, 350, 0, 0);
			set(ustat, x, y, 2100, 50, 0, 0);
			set(respawn, x, y, 0, 0, 0, 0);
			set(jsl, x, y, 0, 0, 0, 0);
		} else {
			set(ctp, x, y, 675, 850, 950, 400);
			set(eep, x, y, 50, 100, 600, 700);
			set(eesp, x, y, 50, 100, 600, 700);
			set((Canvas) bb, x, y, 675, 200, 950, 600);
			set(row, x, y , 1425, 100, 200, 50);
			set(paus, x, y, 675, 100, 200, 50);
			set(rply, x, y, 900, 100, 200, 50);
			set(stream, x, y, 900, 100, 400, 50);
			set(next, x, y, 1200, 100, 200, 50);
			set(eup, x, y, 1650, 100, 600, 700);
			set(eusp, x, y, 1650, 100, 600, 700);
			set(utdsp, x, y, 1650, 850, 600, 400);
			set(etdsp, x, y, 50, 850, 600, 400);
			set(ebase, x, y, 675, 150, 400, 50);
			set(timer, x, y, 1100, 150, 200, 50);
			set(ubase, x, y, 1325, 150, 300, 50);
			set(ecount, x, y, 50, 50, 450, 50);
			set(estat, x, y, 500, 50, 150, 50);
			set(ucount, x, y, 1650, 50, 450, 50);
			set(ustat, x, y, 2100, 50, 150, 50);
			set(respawn, x, y, 50, 800, 600, 50);
			set(jsl, x, y, 650, 800, 950, 50);
			ct.setRowHeight(size(x, y, 50));
			et.setRowHeight(size(x, y, 50));
			est.setRowHeight(size(x, y, 50));
			ut.setRowHeight(size(x, y, 50));
			ust.setRowHeight(size(x, y, 50));
			utd.setRowHeight(size(x, y, 50));
			etd.setRowHeight(size(x, y, 50));
		}
	}

	@Override
	public synchronized void onTimer(int t) {
		super.onTimer(t);
		StageBasis sb = basis.sb;
		if (!pause) {
			upd++;
			if (spe < 0)
				if (upd % (1 - spe) != 0)
					return;
			basis.update();
			updateKey();
			if (spe > 0)
				for (int i = 0; i < Math.pow(2, spe) - 1; i++)
					basis.update();
			if (!jtb.isSelected())
				updateTables();
			updateTablesL();
			BCMusic.flush(spe < 3 && sb.ebase.health > 0 && sb.ubase.health > 0);
		}

		if (sb.getEBHP() < sb.st.bgh && sb.st.bg1 != null) {
			if (!changedBG) {
				changedBG = true;
				sb.changeBG(sb.st.bg1);
			}
		} else if (changedBG) {
			changedBG = false;
			sb.changeBG(sb.st.bg);
		}
		if (sb.ebase.health <= 0 || sb.ubase.health <= 0) {
			BCMusic.EndTheme(sb.ebase.health <= 0);

			if (sb.ebase.health <= 0) {
				if (packData != null && dataPopup >= 0)
					claimReward();
				else if (!exPopupShown && CommonStatic.getConfig().exContinuation && sb.st.info != null && (sb.st.info.getExStages() != null && sb.st.info.getExStages().length != 0)) {
					exPopupShown = true;
					Opts.showExStageSelection("EX stages found", "You can select one of these EX stages and continue the battle", sb.st, this);
					return;
				}
			}
		} else if (sb.mus != null) {
			if (BCMusic.music != sb.mus) {
				BCMusic.play(sb.mus);
				musicChanged = sb.getEBHP() > sb.st.mush;
			}
		} else {
			if (sb.getEBHP() <= sb.st.mush && BCMusic.music != sb.st.mus1)
				if(sb.st.mush == 0 || sb.st.mush == 100)
					BCMusic.play(sb.st.mus1);
				else {
					if(!musicChanged && !backClicked) {
						if(BCMusic.BG != null)
							BCMusic.BG.stop();
						new Thread(() -> {
							try {
								Thread.sleep(Data.MUSIC_DELAY);

								if(backClicked)
									return;

								BCMusic.play(sb.st.mus1);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}).start();

						musicChanged = true;
					}
				}
			else if (BCMusic.music != sb.st.mus0 && sb.getEBHP() > sb.st.mush) {
				if(musicChanged && !backClicked) {
					if(BCMusic.BG != null)
						BCMusic.BG.stop();
					new Thread(() -> {
						try {
							Thread.sleep(Data.MUSIC_DELAY);

							if(backClicked)
								return;

							BCMusic.play(sb.st.mus0);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}).start();

					musicChanged = false;
				}
			}
		}
		if (bb instanceof BBRecd) {
			BBRecd bbr = (BBRecd) bb;
			stream.setText("frame left: " + bbr.info());
		}
		if(bb.getPainter().dragging)
			bb.getPainter().dragFrame++;
	}

	@SuppressWarnings("unchecked")
	public void claimReward() {
		if (dataPopup == 0) {
			Collection<?>[] clearStuff = packData.validClear(basis.sb.st);
			if (clearStuff != null && !clearStuff[0].isEmpty()) {
				String rewardText = get(MainLocale.PAGE, "rewardText");
				StringBuilder unlockeds = new StringBuilder();
				while (!clearStuff[0].isEmpty()) {
					Form rf = ((ArrayDeque<Form>)clearStuff[0]).pop();
					if (!((ArrayDeque<Boolean>)clearStuff[1]).pop())
						unlockeds.append(rf.unit.toString());
					else {
						String rwForm = get(MainLocale.PAGE, "formType");
						if (rf.fid == rf.unit.forms.length - 1)
							rwForm = rwForm.replaceFirst("_", get(MainLocale.PAGE, "final"));
						else
							rwForm = rwForm.replaceFirst("_", Interpret.getExtension(rf.fid + 1));
						rwForm = rwForm.replace("{UNITNAME}", rf.unit.toString());
						unlockeds.append(rwForm);
						if (clearStuff[0].size() > 1)
							unlockeds.append(", ");
					}
				}
				rewardText = rewardText.replace("_", unlockeds.toString());
				BCMusic.doSound(29); //Fanfare SE
				Opts.pop(rewardText, get(MainLocale.PAGE, "grats"));
			}
			if (clearStuff != null && !clearStuff[2].isEmpty()) {
				String rewardText = get(MainLocale.PAGE, "rewardText").replace("_", Arrays.toString(clearStuff[2].toArray()));
				BCMusic.doSound(29); //Fanfare SE
				Opts.pop(rewardText, get(MainLocale.PAGE, "grats"));
			}
		}
		dataPopup -= CommonStatic.fltFpsDiv(1f);
	}

	private void updateTables() {
		StageBasis sb = basis.sb;
		ct.update(sb.est);
		List<Entity> le = new ArrayList<>(sb.st.max / 2);
		List<Entity> lu = new ArrayList<>(sb.max_num / 2);

		for (Entity e : sb.le)
			(e.dire == 1 ? le : lu).add(e);

		(estat.isSelected() ? est : et).setList(le);
		(ustat.isSelected() ? ust : ut).setList(lu);
		utd.sort();
		etd.setList(new ArrayList<>(sb.enemyStatistics.keySet()));

		if (basis instanceof SBRply)
			change((SBRply) basis, b -> jsl.setValue(b.prog()));
		ecount.setText(sb.entityCount(1) + "/" + sb.st.max);
		ucount.setText(sb.entityCount(-1) + "/" + sb.max_num);
		respawn.setText("respawn timer: " + MainBCU.convertTime(sb.respawnTime));
	}
	private void updateTablesL() {
		long h = basis.sb.ebase.health;
		long mh = basis.sb.ebase.maxH;
		ebase.setText("HP: " + h + "/" + mh + ", " + 10000 * h / mh / 100.0 + "%");
		h = basis.sb.ubase.health;
		mh = basis.sb.ubase.maxH;
		ubase.setText("HP: " + h + "/" + mh + ", " + 10000 * h / mh / 100.0 + "%");
		timer.setText(basis.sb.time + "f");
		bb.paint();
	}

	private void addListeners() {
		jtb.setLnr(x -> {
			remove((Canvas) bb);
			add((Canvas) bb);
			DEF_LARGE = jtb.isSelected();
			if (!DEF_LARGE)
				updateTables();
			updateTablesL();
			fireDimensionChanged();
		});

		JBTN back = (JBTN)getBackButton();
		back.removeActionListener(back.getActionListeners()[0]);
		back.setLnr(x -> {
			backClicked = true;
			BCMusic.stopAll();
			if (bb instanceof BBRecd) {
				BBRecd bbr = (BBRecd) bb;
				if (Opts.conf("Do you want to save this video?")) {
					bbr.end();
					return;
				} else
					bbr.quit();
				bb.releaseData();
			}
			if (basis.sb.ebase.health <= 0 && packData != null && dataPopup > 0) {
				dataPopup = 0;
				claimReward();
			}
			changePanel(getFront());
		});

		rply.setLnr(x -> {
			backClicked = true;
			if (basis instanceof SBCtrl) {
				Replay r = ((SBCtrl) basis).getData();
				changePanel(new BattleInfoPage(getThis(), r.rl == null ? r : r.clone(), 0));
			} if (basis instanceof SBRply)
				if (recd.rl == null)
					changePanel(new RecdSavePage(getThis(), recd));
				else
					changePanel(new BattleInfoPage(this, (SBRply) basis));
		});

		paus.addActionListener(arg0 -> {
			pause = !pause;
			jsl.setEnabled(pause);
		});

		next.addActionListener(arg0 -> {
			pause = false;
			timer(0);
			if (CommonStatic.getConfig().fps60)
				timer(0);
			pause = true;
		});

		row.addActionListener(a -> {
			CommonStatic.getConfig().twoRow = !CommonStatic.getConfig().twoRow;
			row.setText(get(MainLocale.PAGE, CommonStatic.getConfig().twoRow ? "tworow" : "onerow"));
			bb.paint();
		});

		jsl.addChangeListener(e -> {
			if (jsl.getValueIsAdjusting() || isAdj() || !(basis instanceof SBRply))
				return;
			((SBRply) basis).restoreTo(jsl.getValue());
			ct.setData(basis.sb.st, basis.sb.est.star);
			updateTables();
			updateTablesL();
			BCMusic.flush(false);
			bb.reset();
		});

		estat.addActionListener(a -> {
			eep.setVisible(!estat.isSelected());
			eesp.setVisible(estat.isSelected());
		});

		ustat.addActionListener(a -> {
			eup.setVisible(!ustat.isSelected());
			eusp.setVisible(ustat.isSelected());
		});
	}

	private void ini() {
		add(eup);
		add(eusp);
		add(eep);
		add(eesp);
		add(ctp);
		add(utdsp);
		add(etdsp);
		add((Canvas) bb);
		add(paus);
		add(next);
		add(ebase);
		add(ubase);
		add(timer);
		add(ecount);
		add(estat);
		add(ucount);
		add(ustat);
		add(respawn);
		add(jtb);
		add(row);
		row.setText(get(MainLocale.PAGE, CommonStatic.getConfig().twoRow ? "tworow" : "onerow"));
		estat.setSelected(false);
		eep.setVisible(!estat.isSelected());
		eesp.setVisible(estat.isSelected());
		ustat.setSelected(false);
		eup.setVisible(!ustat.isSelected());
		eusp.setVisible(ustat.isSelected());
		if (bb instanceof BBRecd)
			add(stream);
		else {
			add(rply);
			if (recd != null && basis instanceof SBRply) {
				add(jsl);
				jsl.setEnabled(pause);
			}
		}
		List<AbForm> lf = new ArrayList<>(basis.sb.ubase instanceof Entity ? 11 : 5);
		for (AbForm[] fs : basis.sb.b.lu.fs)
			for(AbForm f : fs)
				if(f != null)
					lf.add(f);
		if (basis.sb.est.getBase() != null)
			lf.add(basis.sb.est.getBase());
		utd.setList(lf);

		addListeners();
	}

}