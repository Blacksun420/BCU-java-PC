package page;

import common.CommonStatic;
import common.CommonStatic.Config;
import common.io.Backup;
import common.pack.UserProfile;
import common.util.ImgCore;
import common.util.lang.MultiLangCont;
import io.BCMusic;
import io.BCUReader;
import main.MainBCU;
import main.Opts;
import page.support.ColorPicker;
import page.view.ViewBox;

import javax.swing.*;

public class ConfigPage extends Page {

	private static final long serialVersionUID = 1L;

	private static Config cfg() {
		return CommonStatic.getConfig();
	}

	private final JBTN back = new JBTN(MainLocale.PAGE, "back");
	private final JBTN rlla = new JBTN(MainLocale.PAGE, "rllang");
	private final JBTN rlpk = new JBTN(MainLocale.PAGE, "rlpks");
	private final JTG prel = new JTG(MainLocale.PAGE, "preload");
	private final JTG whit = new JTG(MainLocale.PAGE, "white");
	private final JTG refe = new JTG(MainLocale.PAGE, "axis");
	private final JTG jogl = new JTG(MainLocale.PAGE, "JOGL");
	private final JTG musc = new JTG(MainLocale.PAGE, "musc");
	private final JTG exla = new JTG(MainLocale.PAGE, "exlang");
	private final JTG extt = new JTG(MainLocale.PAGE, "extip");
	private final JTG secs = new JTG(MainLocale.PAGE, "secs");
	private final JTG btnsnd = new JTG(MainLocale.PAGE, "btnsnd");
	private final JTG bgeff = new JTG(MainLocale.PAGE, "bgeff");
	private final JTG btdly = new JTG(MainLocale.PAGE, "btdly");
	private final JTG stdis = new JTG(MainLocale.PAGE, "stdis");
	private final JL preflv = new JL(MainLocale.PAGE, "preflv");
	private final JTG shake = new JTG(MainLocale.PAGE, "shake");
	private final JTF prlvmd = new JTF();
	private final JBTN[] left = new JBTN[4];
	private final JBTN[] right = new JBTN[4];
	private final JL[] name = new JL[4];
	private final JL[] vals = new JL[4];
	private final JL jlmin = new JL(MainLocale.PAGE, "opamin");
	private final JL jlmax = new JL(MainLocale.PAGE, "opamax");
	private final JL jlbg = new JL(MainLocale.PAGE, "BGvol");
	private final JL jlse = new JL(MainLocale.PAGE, "SEvol");
	private final JL jlui = new JL(MainLocale.PAGE, "UIvol");
	private final JL mbac = new JL(MainLocale.PAGE, "maxback");
	private final JBTN nobac = new JBTN(MainLocale.PAGE, CommonStatic.getConfig().maxBackup != -1 ? "nobac" : "yesbac");
	private final JSlider jsmin = new JSlider(0, 100);
	private final JSlider jsmax = new JSlider(0, 100);
	private final JSlider jsbg = new JSlider(0, 100);
	private final JSlider jsse = new JSlider(0, 100);
	private final JSlider jsui = new JSlider(0, 100);
	private final JSlider jsba = new JSlider(0, 50);
	private final JList<String> jls = new JList<>(MainLocale.LOC_NAME);
	private final JBTN row = new JBTN(MainLocale.PAGE, CommonStatic.getConfig().twoRow ? "tworow" : "onerow");
	private final JBTN vcol = new JBTN(MainLocale.PAGE, "viewcolor");
	private final JBTN vres = new JBTN(MainLocale.PAGE, "viewreset");
	private final JTG exCont = new JTG(MainLocale.PAGE, "excont");
	private final JL autosave = new JL(MainLocale.PAGE, "autosave");
	private final JTF savetime = new JTF(MainBCU.autoSaveTime > 0 ? MainBCU.autoSaveTime + "min" : "deactivated");

	private final JScrollPane jsps = new JScrollPane(jls);

	private boolean changing = false;

	protected ConfigPage(Page p) {
		super(p);

		ini();
		resized();
	}

	@Override
	protected void renew() {
		jlmin.setText(0, "opamin");
		jlmax.setText(0, "opamax");
		for (int i = 0; i < 4; i++) {
			name[i].setText(0, ImgCore.NAME[i]);
			vals[i].setText(0, ImgCore.VAL[cfg().ints[i]]);
		}
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(jogl, x, y, 50, 100, 200, 50);
		set(prel, x, y, 50, 200, 200, 50);
		set(whit, x, y, 50, 300, 200, 50);
		set(refe, x, y, 50, 400, 200, 50);
		for (int i = 0; i < 4; i++) {
			set(name[i], x, y, 300, 100 + i * 100, 200, 50);
			set(left[i], x, y, 550, 100 + i * 100, 100, 50);
			set(vals[i], x, y, 650, 100 + i * 100, 200, 50);
			set(right[i], x, y, 850, 100 + i * 100, 100, 50);
		}
		set(jsps, x, y, 1100, 100, 200, 400);
		set(jlmin, x, y, 50, 500, 400, 50);
		set(jsmin, x, y, 50, 550, 1000, 100);
		set(jlmax, x, y, 50, 650, 400, 50);
		set(jsmax, x, y, 50, 700, 1000, 100);
		set(jlbg, x, y, 50, 800, 400, 50);
		set(jsbg, x, y, 50, 850, 1000, 100);
		set(jlse, x, y, 50, 950, 400, 50);
		set(jsse, x, y, 50, 1000, 1000, 100);
		set(jlui, x, y, 50, 1100, 400, 50);
		set(jsui, x, y, 50, 1150, 1000, 100);
		set(musc, x, y, 1350, 550, 200, 50);
		set(exla, x, y, 1100, 625, 450, 50);
		set(extt, x, y, 1100, 700, 450, 50);
		set(rlla, x, y, 1100, 775, 450, 50);
		set(row, x, y, 1100, 925, 450, 50);
		set(secs, x, y, 1100, 1000, 450, 50);
		set(preflv, x, y, 1600, 550, 200, 50);
		set(prlvmd, x, y, 1800, 550, 250, 50);
		set(mbac, x, y, 1100, 1100, 400, 50);
		set(nobac, x, y, 1525, 1100, 250, 50);
		set(jsba, x, y, 1100, 1150, 1000, 100);
		set(btnsnd, x, y, 1600, 625, 200, 50);
		set(bgeff, x, y, 1850, 625, 200, 50);
		set(btdly, x, y, 1600, 700, 200, 50);
		set(stdis, x, y, 1850, 700, 200, 50);
		set(rlpk, x, y, 1600, 775, 450, 50);
		set(vcol, x, y, 1600, 850, 200, 50);
		set(vres, x, y, 1850, 850, 200, 50);
		set(exCont, x, y, 1600, 925, 450, 50);
		set(autosave, x, y, 1600, 1000, 200, 50);
		set(savetime, x, y, 1800, 1000, 250, 50);
		set(shake, x, y, 1600, 475, 450, 50);
	}

	@Override
	public void callBack(Object obj) {
		super.callBack(obj);

		if(obj instanceof ColorPicker) {
			int rgb = ((ColorPicker) obj).rgb[0];
			rgb = (rgb << 8) + ((ColorPicker) obj).rgb[1];
			rgb = (rgb << 8) + ((ColorPicker) obj).rgb[2];

			cfg().viewerColor = rgb;
		}
	}

	private void addListeners() {
		back.addActionListener(arg0 -> changePanel(getFront()));

		secs.addActionListener(arg0 -> MainBCU.seconds = secs.isSelected());

		prel.addActionListener(arg0 -> MainBCU.preload = prel.isSelected());

		exla.addActionListener(arg0 -> {
			MainLocale.exLang = exla.isSelected();
			Page.renewLoc(getThis());
		});

		extt.addActionListener(arg0 -> {
			MainLocale.exTTT = extt.isSelected();
			Page.renewLoc(getThis());
		});

		rlla.addActionListener(arg0 -> {
			MultiLangCont.getStatic().clear();
			BCUReader.readLang();
			Page.renewLoc(getThis());
		});

		whit.addActionListener(arg0 -> ViewBox.Conf.white = whit.isSelected());

		refe.addActionListener(arg0 -> cfg().ref = refe.isSelected());

		jogl.addActionListener(arg0 -> {
			if (Opts.conf("This requires restart to apply. Do you want to restart?")) {
				MainBCU.USE_JOGL = jogl.isSelected();
				changePanel(new SavePage());
			} else
				jogl.setSelected(MainBCU.USE_JOGL);
		});

		for (int i = 0; i < 4; i++) {
			int I = i;

			left[i].addActionListener(arg0 -> {
				cfg().ints[I]--;
				vals[I].setText(0, ImgCore.VAL[cfg().ints[I]]);
				left[I].setEnabled(cfg().ints[I] > 0);
				right[I].setEnabled(cfg().ints[I] < 2);
			});

			right[i].addActionListener(arg0 -> {
				cfg().ints[I]++;
				vals[I].setText(0, ImgCore.VAL[cfg().ints[I]]);
				left[I].setEnabled(cfg().ints[I] > 0);
				right[I].setEnabled(cfg().ints[I] < 2);
			});

		}

		jsmin.addChangeListener(arg0 -> cfg().deadOpa = jsmin.getValue());

		jsmax.addChangeListener(arg0 -> cfg().fullOpa = jsmax.getValue());

		jsbg.addChangeListener(arg0 -> BCMusic.setBGVol(jsbg.getValue()));

		jsse.addChangeListener(arg0 -> BCMusic.setSEVol(jsse.getValue()));

		jsui.addChangeListener(arg0 -> BCMusic.setUIVol(jsui.getValue()));

		nobac.addActionListener(arg0 -> {
			if (CommonStatic.getConfig().maxBackup != -1) {
				if (Opts.conf(get(MainLocale.PAGE, "nobacwarn"))) {
					nobac.setText(get(MainLocale.PAGE, "yesbac"));
					CommonStatic.getConfig().maxBackup = -1;
					jsba.setEnabled(false);
				}
			} else {
				nobac.setText(get(MainLocale.PAGE, "nobac"));
				jsba.setEnabled(true);
				jsba.setValue(Backup.backups.size());
			}
		});

		jsba.addChangeListener(arg0 -> {
			if(!jsba.getValueIsAdjusting()) {
				int back = Backup.backups.size();
				int pre = CommonStatic.getConfig().maxBackup;

				if(pre >= back && back > jsba.getValue() && jsba.getValue() > 0) {
					if(Opts.conf((back-jsba.getValue())+" "+get(MainLocale.PAGE, "backremwarn"))) {
						CommonStatic.getConfig().maxBackup = jsba.getValue();
					} else {
						jsba.setValue(CommonStatic.getConfig().maxBackup);
					}
				} else if(jsba.getValue() == 0) {
					if(Opts.conf((get(MainLocale.PAGE, "backinfwarn")))) {
						CommonStatic.getConfig().maxBackup = jsba.getValue();
					} else {
						jsba.setValue(CommonStatic.getConfig().maxBackup);
					}
				} else {
					CommonStatic.getConfig().maxBackup = jsba.getValue();
				}
			}
		});

		jls.addListSelectionListener(arg0 -> {
			if (changing)
				return;
			changing = true;
			if (jls.getSelectedIndex() == -1) {
				jls.setSelectedIndex(localeIndexOf(cfg().lang));
			}
			cfg().lang = MainLocale.LOC_INDEX[jls.getSelectedIndex()];
			Page.renewLoc(getThis());
			changing = false;
		});

		musc.addActionListener(arg0 -> BCMusic.play = musc.isSelected());

		row.addActionListener(a -> {
			CommonStatic.getConfig().twoRow = !CommonStatic.getConfig().twoRow;
			row.setText(MainLocale.PAGE, CommonStatic.getConfig().twoRow ? "tworow" : "onerow");
		});

		btnsnd.addActionListener(a -> {
			MainBCU.buttonSound = !MainBCU.buttonSound;
			if(MainBCU.buttonSound)
				BCMusic.clickSound();
		});

		btdly.addActionListener(a -> CommonStatic.getConfig().buttonDelay = !CommonStatic.getConfig().buttonDelay);

		stdis.addActionListener(a -> CommonStatic.getConfig().stageName = !CommonStatic.getConfig().stageName);

		bgeff.addActionListener(a -> CommonStatic.getConfig().drawBGEffect = !CommonStatic.getConfig().drawBGEffect);

		rlpk.addActionListener(l -> UserProfile.reloadExternalPacks());

		vcol.addActionListener(l -> {
			if(CommonStatic.getConfig().viewerColor != -1)
				Opts.showColorPicker("Color pick pick", this, CommonStatic.getConfig().viewerColor);
			else
				Opts.showColorPicker("Color pick pick", this);
		});

		vres.addActionListener(l -> CommonStatic.getConfig().viewerColor = -1);

		exCont.addActionListener(l -> CommonStatic.getConfig().exContinuation = exCont.isSelected());

		savetime.setLnr(c -> {
			int time = CommonStatic.parseIntN(savetime.getText());
			boolean eq = time != -1 && time != MainBCU.autoSaveTime;

			savetime.setText(time > 0 ? time + "min" : "deactivated");
			if (eq) {
				MainBCU.autoSaveTime = time;
				MainBCU.restartAutoSaveTimer();
			}
		});

		shake.addActionListener(c -> CommonStatic.getConfig().shake = shake.isSelected());
	}

	private void ini() {
		add(back);
		add(jogl);
		add(prel);
		add(refe);
		add(whit);
		add(jsps);
		set(jsmin);
		set(jsmax);
		add(jlmin);
		add(jlmax);
		add(musc);
		add(rlla);
		add(exla);
		add(extt);
		add(jlbg);
		add(jlse);
		add(jlui);
		set(jsbg);
		set(jsse);
		set(jsui);
		add(row);
		add(secs);
		add(preflv);
		add(prlvmd);
		set(prlvmd);
		set(jsba);
		add(mbac);
		add(btnsnd);
		add(bgeff);
		add(btdly);
		add(stdis);
		add(rlpk);
		add(vcol);
		add(vres);
		add(exCont);
		add(autosave);
		add(savetime);
		add(nobac);
		add(shake);
		exCont.setSelected(CommonStatic.getConfig().exContinuation);
		prlvmd.setText("" + CommonStatic.getConfig().prefLevel);
		jls.setSelectedIndex(localeIndexOf(cfg().lang));
		jsmin.setValue(cfg().deadOpa);
		jsmax.setValue(cfg().fullOpa);
		jsbg.setValue(BCMusic.VOL_BG);
		jsse.setValue(BCMusic.VOL_SE);
		jsui.setValue(BCMusic.VOL_UI);
		for (int i = 0; i < 4; i++) {
			left[i] = new JBTN("<");
			right[i] = new JBTN(">");
			name[i] = new JL(0, ImgCore.NAME[i]);
			vals[i] = new JL(0, ImgCore.VAL[cfg().ints[i]]);
			add(left[i]);
			add(right[i]);
			add(name[i]);
			add(vals[i]);
			name[i].setHorizontalAlignment(SwingConstants.CENTER);
			vals[i].setHorizontalAlignment(SwingConstants.CENTER);
			name[i].setBorder(BorderFactory.createEtchedBorder());
			vals[i].setBorder(BorderFactory.createEtchedBorder());
			left[i].setEnabled(cfg().ints[i] > 0);
			right[i].setEnabled(cfg().ints[i] < 2);
		}
		exla.setSelected(MainLocale.exLang);
		extt.setSelected(MainLocale.exTTT);
		secs.setSelected(MainBCU.seconds);
		prel.setSelected(MainBCU.preload);
		whit.setSelected(ViewBox.Conf.white);
		refe.setSelected(cfg().ref);
		musc.setSelected(BCMusic.play);
		jogl.setSelected(MainBCU.USE_JOGL);
		btnsnd.setSelected(MainBCU.buttonSound);
		if (CommonStatic.getConfig().maxBackup != -1)
			jsba.setValue(CommonStatic.getConfig().maxBackup);
		else
			jsba.setEnabled(false);
		bgeff.setSelected(CommonStatic.getConfig().drawBGEffect);
		btdly.setSelected(CommonStatic.getConfig().buttonDelay);
		stdis.setSelected(CommonStatic.getConfig().stageName);

		shake.setSelected(CommonStatic.getConfig().shake);
		addListeners();
	}

	protected void set(JTF jtf) {
		jtf.setLnr(e -> {
			String text = jtf.getText().trim();
			if (text.length() > 0) {
				int[] v = CommonStatic.parseIntsN(text);
				CommonStatic.getConfig().prefLevel = Math.max(1, v[0]);
				jtf.setText("" + CommonStatic.getConfig().prefLevel);
			}
		});
	}

	private void set(JSlider sl) {
		add(sl);
		sl.setMajorTickSpacing(10);
		sl.setMinorTickSpacing(5);
		sl.setPaintTicks(true);
		sl.setPaintLabels(true);
	}

	private int localeIndexOf(int elem) {
		for(int i = 0; i < MainLocale.LOC_INDEX.length; i++) {
			if(MainLocale.LOC_INDEX[i] == elem)
				return i;
		}

		return -1;
	}
}
