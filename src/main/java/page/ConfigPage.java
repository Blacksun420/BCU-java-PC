package page;

import common.CommonStatic;
import common.CommonStatic.Config;
import common.io.Backup;
import common.io.assets.AssetLoader;
import common.pack.UserProfile;
import common.util.Data;
import common.util.ImgCore;
import common.util.Res;
import common.util.lang.MultiLangCont;
import common.util.unit.Level;
import io.BCMusic;
import io.BCUReader;
import main.MainBCU;
import main.Opts;
import main.Timer;
import page.support.ColorPicker;
import page.support.ReorderList;
import page.support.ReorderListener;
import page.view.ViewBox;

import javax.swing.*;

import plugin.ui.main.util.BCUSettingMenu;

import java.awt.*;

import static utilpc.Interpret.RARITY;

public class ConfigPage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	private static Config cfg() {
		return CommonStatic.getConfig();
	}

	private final JBTN rlla = new JBTN(MainLocale.PAGE, "rllang");
	private final JBTN rlpk = new JBTN(MainLocale.PAGE, "rlpks");
	private final JCB prel = new JCB(MainLocale.PAGE, "preload");
	private final JCB whit = new JCB(MainLocale.PAGE, "white");
	private final JCB refe = new JCB(MainLocale.PAGE, "axis");
	private final JCB jogl = new JCB(MainLocale.PAGE, "JOGL");
	private final JBTN secs = new JBTN(MainLocale.PAGE, MainBCU.seconds ? "secs" : "frame");
	private final JCB musc = new JCB(MainLocale.PAGE, "musc");
	private final JCB jcsnd = new JCB(MainLocale.PAGE, "btnsnd");
	private final JCB jcraw = new JCB(MainLocale.PAGE, "rawdmg");
	private final JCB jceff = new JCB(MainLocale.PAGE, "bgeff");
	private final JCB jcdly = new JCB(MainLocale.PAGE, "btdly");
	private final JCB stdis = new JCB(MainLocale.PAGE, "stdis");
	private final JCB perfo = new JCB("60 FPS");
	private final JL preflv = new JL(MainLocale.PAGE, "preflv");
	private final JCB shake = new JCB(MainLocale.PAGE, "shake");
	private final JCB search = new JCB(MainLocale.PAGE, "searchkey");
	private final JL jtol = new JL(MainLocale.PAGE, "tolerance");
	private final JTF tole = new JTF(String.valueOf(MainBCU.searchTolerance));

	private final JL[] prfr = new JL[6];
	private final JTF[] jrfr = new JTF[6];

	private final JBTN[] left = new JBTN[4];
	private final JBTN[] right = new JBTN[4];
	private final JL[] name = new JL[4];
	private final JL[] vals = new JL[4];
	private final JL jlmin = new JL(MainLocale.PAGE, "opamin");
	private final JL jlmax = new JL(MainLocale.PAGE, "opamax");
	private final JL jlbg = new JL(MainLocale.PAGE, "BGvol");
	private final JL jlse = new JL(MainLocale.PAGE, "SEvol");
	private final JL jlui = new JL(MainLocale.PAGE, "UIvol");
	private final JL jlla = new JL(MainLocale.PAGE, "lang");
	private final JL jlfi = new JL(MainLocale.PAGE, "filter");
	private final JL jlti = new JL(MainLocale.PAGE, "meastime");
	private final JL jlly = new JL(MainLocale.PAGE, "layout");
	private final JL jlth = new JL(MainLocale.PAGE, "theme");
	private final JL jlga = new JL(MainLocale.PAGE, "gameplay");
	private final JL jlot = new JL(MainLocale.PAGE, "other");
	private final JL jlre = new JL(MainLocale.PAGE, "render");
	private final JL mbac = new JL(MainLocale.PAGE, "maxback");
	private final JCB jcbac = new JCB(MainLocale.PAGE, "jcbac");
	private final JCB jcmus = new JCB(MainLocale.PAGE, "updatemus");
	private final JSlider jsmin = new JSlider(0, 100);
	private final JSlider jsmax = new JSlider(0, 100);
	private final JSlider jsbg = new JSlider(0, 100);
	private final JSlider jsse = new JSlider(0, 100);
	private final JSlider jsui = new JSlider(0, 100);
	private final JSlider jsba = new JSlider(0, 50);
	private final ReorderList<CommonStatic.Lang.Locale> jls = new ReorderList<>(cfg().langs);
	private final JBTN row = new JBTN(MainLocale.PAGE, CommonStatic.getConfig().twoRow ? "tworow" : "onerow");
	private final JBTN vcol = new JBTN(MainLocale.PAGE, "viewcolor");
	private final JBTN vres = new JBTN(MainLocale.PAGE, "viewreset");
	private final JCB excont = new JCB(MainLocale.PAGE, "excont");
	private final JL autosave = new JL(MainLocale.PAGE, "autosave");
	private final JTF savetime = new JTF(MainBCU.autoSaveTime > 0 ? MainBCU.autoSaveTime + "min" : get(MainLocale.PAGE, "deactivated"));
	private final JTG dyna = new JTG(MainLocale.PAGE, "dynamic");
	private final JCB reallv = new JCB(MainLocale.PAGE, "reallv");
	private final JCB pkprog = new JCB(MainLocale.PAGE, "pkprog");
	private final JCB stat = new JCB(MainLocale.PAGE, "defstat");

	private final JL comv = new JL(Page.get(MainLocale.PAGE, "CORE Ver: ") + AssetLoader.CORE_VER);

	private final JScrollPane jsps = new JScrollPane(jls);

	private boolean changing = false;

	protected ConfigPage(Page p) {
		super(p);

		ini();
	}

	@Override
	protected void renew() {
		jlmin.setText(0, "opamin");
		jlmax.setText(0, "opamax");
		for (byte i = 0; i < 4; i++) {
			name[i].setText(0, ImgCore.NAME[i]);
			vals[i].setText(0, ImgCore.VAL[cfg().ints[i]]);
		}
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);

		set(jlre, x, y, 50, 100, 200, 50);
		set(jogl, x, y, 50, 150, 300, 50);
		set(whit, x, y, 50, 200, 300, 50);
		set(refe, x, y, 50, 250, 300, 50);
		set(prel, x, y, 50, 300, 300, 50);
		set(perfo, x, y, 50,  350, 300, 50);

		if (!MainBCU.USE_JOGL)
			for (byte i = 0; i < 4; i++) {
				set(name[i], x, y, 350, 100 + i * 75, 200, 50);
				set(left[i], x, y, 575, 100 + i * 75, 75, 50);
				set(vals[i], x, y, 650, 100 + i * 75, 200, 50);
				set(right[i], x, y, 850, 100 + i * 75, 75, 50);
			}

		set(jlmin, x, y, 50, 425, 300, 50);
		set(jsmin, x, y, 350, 425, 750, 75);
		set(jlmax, x, y, 50, 500, 300, 50);
		set(jsmax, x, y, 350, 500, 750, 75);
		set(jlbg, x, y, 50, 575, 300, 50);
		set(jsbg, x, y, 350, 575, 750, 75);
		set(jlse, x, y, 50, 650, 300, 50);
		set(jsse, x, y, 350, 650, 750, 75);
		set(jlui, x, y, 50, 725, 300, 50);
		set(jsui, x, y, 350, 725, 750, 75);
		set(mbac, x, y, 50, 800, 300, 50);
		set(jsba, x, y, 350, 800, 750, 75);

		set(jlga, x, y, 50, 900, 600, 50);
		set(musc, x, y, 50, 950, 300, 50);
		set(jceff, x, y, 50, 1000, 300, 50);
		set(jcdly, x, y, 50, 1050, 300, 50);
		set(stdis, x, y, 50, 1100, 300, 50);
		set(excont, x, y, 50, 1150, 300, 50);
		set(shake, x, y, 50, 1200, 300, 50);

		set(reallv, x, y, 350, 950, 300, 50);
		set(pkprog, x, y, 350, 1000, 300, 50);
		set(stat, x, y, 350, 1050, 300, 50);

		set(jlot, x, y, 650, 900, 300, 50);
		set(jcbac, x, y, 650, 950, 300, 50);
		set(jcmus, x, y, 650, 1000, 300, 50);
		set(jcsnd, x, y, 650, 1050, 300, 50);
		set(jcraw, x, y, 650, 1100, 300, 50);
		set(search, x, y, 650, 1150, 300, 50);

		set(jlfi, x, y, 1225, 100, 200, 50);
		set(jlti, x, y, 1225, 175, 200, 50);
		set(secs, x, y, 1425, 175, 200, 50);
		set(jlly, x, y, 1225, 250, 200, 50);
		set(row, x, y, 1425, 250, 200, 50);
		set(jlth, x, y, 1425, 100, 200, 50);
		set(rlla, x, y, 1225, 400, 400, 50);
		set(autosave, x, y, 1225, 475, 200, 50);
		set(savetime, x, y, 1425, 475, 200, 50);

		set(rlpk, x, y, 1225, 550, 400, 50);
		set(vcol, x, y, 1225, 625, 400, 50);
		set(vres, x, y, 1225, 700, 400, 50);
		set(jtol, x, y, 1225, 775, 200, 50);
		set(tole, x, y, 1425, 775, 200, 50);
		set(dyna, x, y, 1425, 850, 200, 50);

		set(jlla, x, y, 1750, 100, 300, 50);
		set(jsps, x, y, 1750, 150, 300, 300);

		set(preflv, x, y, 1675, 500, 450, 50);
		for (int i = 0; i < prfr.length; i++) {
			int yy = 550 + (i * 75);
			set(prfr[i], x, y, 1675, yy, 150, 50);
			set(jrfr[i], x, y, 1825, yy, 300, 50);
		}
	}

	@Override
	public void callBack(Object obj) {
		super.callBack(obj);

		if(obj instanceof ColorPicker) {
			int rgb = ((ColorPicker) obj).rgb[0];
			rgb = (rgb << 8) + ((ColorPicker) obj).rgb[1];
			rgb = (rgb << 8) + ((ColorPicker) obj).rgb[2];

			cfg().viewerColor = rgb;
		} else if (obj instanceof String) {
			prel.setSelected(MainBCU.preload);
			whit.setSelected(ViewBox.Conf.white);
			refe.setSelected(cfg().ref);
			musc.setSelected(BCMusic.play);
			excont.setSelected(cfg().exContinuation);
			jcsnd.setSelected(MainBCU.buttonSound);
			jogl.setSelected(MainBCU.USE_JOGL);
			jcbac.setSelected(cfg().maxBackup != -1);
			jcmus.setSelected(cfg().updateOldMusic);
			jcraw.setSelected(CommonStatic.getConfig().rawDamage);
			jceff.setSelected(cfg().drawBGEffect);
			jcdly.setSelected(cfg().buttonDelay);
			perfo.setSelected(cfg().fps60);
			stdis.setSelected(cfg().stageName);

			shake.setSelected(cfg().shake);
			reallv.setSelected(cfg().realLevel);
			pkprog.setSelected(cfg().prog);
			stat.setSelected(cfg().stat);
			search.setSelected(MainBCU.searchPerKey);
			dyna.setSelected(MainBCU.useDynamic);
		}
	}

	private void addListeners() {
		secs.addActionListener(arg0 -> {
			MainBCU.seconds = !MainBCU.seconds;
			secs.setText(0, MainBCU.seconds ? "secs" : "frame");
			MainBCU.getSettingMenu().syncCheckBox(BCUSettingMenu.TIME, MainBCU.seconds);
		});

		search.addActionListener(a -> MainBCU.searchPerKey = search.isSelected());

		tole.setLnr(c -> {
			MainBCU.searchTolerance = Math.max(0, CommonStatic.parseIntN(tole.getText()));
			tole.setText(String.valueOf(MainBCU.searchTolerance));
		});


		prel.addActionListener(arg0 -> MainBCU.preload = prel.isSelected());

		rlla.addActionListener(arg0 -> {
			MultiLangCont.getStatic().clear();
			BCUReader.readLang();
			Page.renewLoc(getThis());
		});

		whit.addActionListener(arg0 -> ViewBox.Conf.white = whit.isSelected());

		refe.addActionListener(arg0 -> {
			cfg().ref = refe.isSelected();
			MainBCU.getSettingMenu().syncCheckBox(BCUSettingMenu.AXIS, refe.isSelected());
		});

		jogl.addActionListener(arg0 -> {
			if (Opts.conf("This requires restart to apply. Do you want to restart?")) {
				MainBCU.USE_JOGL = jogl.isSelected();
				changePanel(new SavePage());
			} else
				jogl.setSelected(MainBCU.USE_JOGL);
		});

		stat.addActionListener(arg0 -> {
			if (Opts.conf("This requires restart to apply. Do you want to restart?")) {
				CommonStatic.getConfig().stat = stat.isSelected();
				changePanel(new SavePage());
			} else
				stat.setSelected(CommonStatic.getConfig().stat);
		});

		if (!MainBCU.USE_JOGL)
			for (byte i = 0; i < 4; i++) {
				byte I = i;

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

		jcbac.addActionListener(arg0 -> {
			if (CommonStatic.getConfig().maxBackup != -1) {
				if (Opts.conf(get(MainLocale.PAGE, "nobacwarn"))) {
					CommonStatic.getConfig().maxBackup = -1;
					jsba.setEnabled(false);
				} else {
					jcbac.setSelected(true);
				}
			} else {
				jsba.setEnabled(true);
				jsba.setValue(Math.max(1, Backup.backups.size()));
			}
		});

		jcmus.addActionListener(arg0 -> cfg().updateOldMusic = jcmus.isSelected());

		jsba.addChangeListener(arg0 -> {
			if(!jsba.getValueIsAdjusting()) {
				int back = Backup.backups.size();
				int pre = cfg().maxBackup;

				if(pre >= back && back > jsba.getValue() && jsba.getValue() > 0) {
					if(Opts.conf((back-jsba.getValue())+" "+get(MainLocale.PAGE, "backremwarn")))
						cfg().maxBackup = jsba.getValue();
					else
						jsba.setValue(cfg().maxBackup);
				} else if(jsba.getValue() == 0) {
					if(Opts.conf((get(MainLocale.PAGE, "backinfwarn"))))
						cfg().maxBackup = jsba.getValue();
					else
						jsba.setValue(cfg().maxBackup);
				} else
					cfg().maxBackup = jsba.getValue();
			}
		});

		jls.list = new ReorderListener<CommonStatic.Lang.Locale>() {
			@Override
			public void reordered(int ori, int fin) {
				change(false);
				changing = true;
				CommonStatic.Lang.Locale og = cfg().langs[ori];
				if (ori < fin)
					for (int i = ori; i < fin; i++)
						cfg().langs[i] = cfg().langs[i+1];
				else
					for (int i = ori; i > fin; i--)
						cfg().langs[i] = cfg().langs[i-1];
				cfg().langs[fin] = og;
				Res.langIcons();
				Page.renewLoc(getThis());
				MainBCU.getSettingMenu().updateLoc();
				for (int i = 0; i < prfr.length; i++)
					prfr[i].setText(RARITY[i]);
				changing = false;
			}
			@Override
			public void reordering() {
				change(true);
			}

		};

		musc.addActionListener(arg0 -> {
			BCMusic.play = musc.isSelected();
			jsbg.setEnabled(BCMusic.play);
			jsse.setEnabled(BCMusic.play);
			jcsnd.setEnabled(BCMusic.play);
			MainBCU.getSettingMenu().syncCheckBox(BCUSettingMenu.PLAYMUSIC, musc.isSelected());
		});

		row.addActionListener(a -> {
			cfg().twoRow = !cfg().twoRow;
			row.setText(MainLocale.PAGE, cfg().twoRow ? "tworow" : "onerow");
		});

		jcsnd.addActionListener(a -> {
			MainBCU.buttonSound = jcsnd.isSelected();
			if(MainBCU.buttonSound)
				BCMusic.doSound(11);
		});

		jcraw.addActionListener(a -> cfg().rawDamage = jcraw.isSelected());
		jcdly.addActionListener(a -> cfg().buttonDelay = !cfg().buttonDelay);
		stdis.addActionListener(a -> cfg().stageName = !cfg().stageName);

		perfo.addActionListener(a -> {
			cfg().fps60 = !cfg().fps60;
			Timer.fps = 1000 / (cfg().fps60 ? 60 : 30);
			MainBCU.getSettingMenu().syncCheckBox(BCUSettingMenu.FPS, cfg().fps60);
		});

		jceff.addActionListener(a -> cfg().drawBGEffect = !cfg().drawBGEffect);
		rlpk.addActionListener(l -> UserProfile.reloadExternalPacks());

		vcol.addActionListener(l -> {
			if(cfg().viewerColor != -1)
				Opts.showColorPicker("Pick Viewer BG Color", this, cfg().viewerColor);
			else
				Opts.showColorPicker("Pick Viewer BG Color", this);
		});

		vres.addActionListener(l -> cfg().viewerColor = -1);
		excont.addActionListener(l -> {
			cfg().exContinuation = excont.isSelected();
			MainBCU.getSettingMenu().syncCheckBox(BCUSettingMenu.EXCONT, excont.isSelected());
		});

		savetime.setLnr(c -> {
			MainBCU.autoSaveTime = Math.max(0, CommonStatic.parseIntN(savetime.getText()));
			savetime.setText(MainBCU.autoSaveTime > 0 ? MainBCU.autoSaveTime + "min" : get(MainLocale.PAGE, "deactivated"));
			MainBCU.restartAutoSaveTimer();
		});
		shake.addActionListener(c -> cfg().shake = shake.isSelected());
		reallv.addActionListener(c -> cfg().realLevel = reallv.isSelected());
		pkprog.addActionListener(c -> {
			cfg().prog = pkprog.isSelected();
			MainBCU.getSettingMenu().syncCheckBox(BCUSettingMenu.PROG, pkprog.isSelected());
		});
		dyna.setLnr(c -> {
			MainBCU.useDynamic = dyna.isSelected();
			tole.setEnabled(!dyna.isSelected());
		});

		for (int i = 0; i < jrfr.length; i++) {
			int I = i;
			jrfr[i].setLnr(e -> {
				String text = jrfr[I].getText().trim();
				Level l = CommonStatic.getPrefLvs().rare[I];
				if (!text.isEmpty()) {
					int[] v = CommonStatic.parseIntsN(text);
					if (v.length > 0) {
						l.setLevel(Math.max(1, v[0]));
						if (v.length > 1) {
							l.setPlusLevel(Math.max(0, v[1]));
							int[] nps = new int[Math.min(Data.PC_CORRES.length + Data.PC_CUSTOM.length, v.length - 2)];
							System.arraycopy(v, 2, nps, 0, nps.length);
							l.setTalents(nps);
						}
					}
				}
				jrfr[I].setText(Level.lvString(l));
			});
		}
	}

	private void ini() {
		add(search);
		add(jtol);
		add(tole);
		add(jogl);
		add(prel);
		add(refe);
		add(whit);
		add(jsps);
		set(jsmin);
		set(jsmax);
		add(jlmin);
		add(jlmax);
		add(jlla);
		add(jlfi);
		add(jlti);
		add(jlly);
		add(jlth);
		add(jlre);
		add(jlga);
		add(jlot);
		add(musc);
		add(rlla);
		add(jlbg);
		add(jlse);
		add(jlui);
		set(jsbg);
		set(jsse);
		set(jsui);
		add(row);
		add(secs);
		add(preflv);
		set(jsba);
		add(mbac);
		add(jcsnd);
		add(jcraw);
		add(jceff);
		add(jcdly);
		add(stdis);
		add(perfo);
		add(rlpk);
		add(vcol);
		add(vres);
		add(excont);
		add(autosave);
		add(savetime);
		add(jcbac);
		add(jcmus);
		add(shake);
		add(reallv);
		add(pkprog);
		add(stat);
		add(dyna);
		for (int i = 0; i < prfr.length; i++) {
			add(prfr[i] = new JL(RARITY[i]));
			add(jrfr[i] = new JTF(Level.lvString(CommonStatic.getPrefLvs().rare[i])));
		}
		jls.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
				JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
				jl.setText(((CommonStatic.Lang.Locale)o).name);
				return jl;
			}
		});
		jsmin.setValue(cfg().deadOpa);
		jsmax.setValue(cfg().fullOpa);
		jsbg.setValue(BCMusic.VOL_BG);
		jsse.setValue(BCMusic.VOL_SE);
		jsui.setValue(BCMusic.VOL_UI);
		for (byte i = 0; i < 4; i++) {
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
		callBack("init");
		jsbg.setEnabled(BCMusic.play);
		jsse.setEnabled(BCMusic.play);
		jcsnd.setEnabled(BCMusic.play);
		jcraw.setToolTipText(get(MainLocale.PAGE, "rawdmgtip"));
		if (cfg().maxBackup != -1)
			jsba.setValue(cfg().maxBackup);
		else
			jsba.setEnabled(false);
		reallv.setToolTipText(get(MainLocale.PAGE, "reallvtip"));
		pkprog.setToolTipText(get(MainLocale.PAGE, "pkprogtip"));
		stat.setToolTipText(get(MainLocale.PAGE, "defstattip"));
		tole.setText(String.valueOf(MainBCU.searchTolerance));
		comv.setBorder(null);
		addListeners();
	}

	private void set(JSlider sl) {
		add(sl);
		sl.setMajorTickSpacing(10);
		sl.setMinorTickSpacing(5);
		sl.setPaintTicks(true);
		sl.setPaintLabels(true);
	}
}
