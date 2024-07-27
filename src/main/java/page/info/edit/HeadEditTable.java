package page.info.edit;

import common.CommonStatic;
import common.pack.Identifier;
import common.pack.PackData;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.Data;
import common.util.pack.Background;
import common.util.stage.*;
import page.*;
import page.view.BGViewPage;
import page.view.CastleViewPage;
import page.view.MusicPage;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

class HeadEditTable extends Page {

	private static final long serialVersionUID = 1L;

	private final JL hea = new JL(MainLocale.INFO, "ht00");
	private final JL len = new JL(MainLocale.INFO, "ht01");
	private final JBTN mus = new JBTN(MainLocale.INFO, "mus");
	private final JL max = new JL(MainLocale.INFO, "ht02");
	private final JBTN bg = new JBTN(MainLocale.INFO, "ht04");
	private final JBTN cas = new JBTN(MainLocale.INFO, "ht05");
	private final JTF name = new JTF();
	private final JTF jhea = new JTF();
	private final JTF jlen = new JTF();
	private final JTF jbg = new JTF();
	private final JTF jcas = new JTF();
	private final JTF jm0 = new JTF();
	private final JTF jmh = new JTF();
	private final JTF jm1 = new JTF();
	private final JTF jbgh = new JTF();
	private final JTF jbg1 = new JTF();
	private final JTG con = new JTG(MainLocale.INFO, "ht03");
	private final JTF jmax = new JTF();
	private final JL res = new JL(MainLocale.INFO, "minspawn");
	private final JL ures = new JL(MainLocale.INFO, "uminspawn");
	private final JTF jres = new JTF();
	private final JTF jures = new JTF();
	private final JTG dojo = new JTG(MainLocale.PAGE, "dojo");
	private final JTG bbrr = new JTG(MainLocale.INFO, "bossBarrier");
	private final LimitTable lt;

	private Stage sta;
	private final UserPack pac;
	private BGViewPage bvp;
	private CastleViewPage cvp;
	private MusicPage mp;

	private int bgl = 0;
	private int musl = 0;

	protected HeadEditTable(Page p, UserPack pack) {
		super(p);
		pac = pack;
		lt = new LimitTable(p, this, pac);
		ini();
	}

	@Override
	public JButton getBackButton() {
		return null;
	}

	@Override
	public void callBack(Object o) {
		setData(sta);
	}

	@Override
	protected void renew() {
		lt.renew();
		if (bvp != null && bvp.getSelected() != null) {
			Identifier<Background> val = bvp.getSelected().id;
			if (val == null)
				return;
			if (bgl == 0) {
				jbg.setText(val.toString());
				sta.bg = val;
			} else {
				jbg1.setText(val.toString());
				sta.bg1 = val;
			}
		}
		if (cvp != null) {
			Identifier<CastleImg> val = cvp.getVal();
			if (val == null)
				return;
			jcas.setText(val.toString());
			sta.castle = val;
		}

		if (mp != null) {
			Identifier<Music> val = mp.getSelectedID();
			if (musl == 0) {
				jm0.setText(String.valueOf(val));
				sta.mus0 = val;
			} else {
				jm1.setText(String.valueOf(val));
				sta.mus1 = val;
			}
		}

		jres.setEnabled(sta != null);
		jures.setEnabled(sta != null);


		if (sta != null) {
			jres.setText(generateMinRespawn(sta.minSpawn, sta.maxSpawn));
			jures.setText(generateMinRespawn(sta.minUSpawn, sta.maxUSpawn));
		}

		bvp = null;
		cvp = null;
		mp = null;
	}

	@Override
	protected void resized(int x, int y) {
		int w = 1400 / 8;
		set(name, x, y, 0, 0, w * 2, 50);
		set(hea, x, y, 0, 50, w, 50);
		set(jhea, x, y, w, 50, w, 50);
		set(len, x, y, w * 2, 50, w, 50);
		set(jlen, x, y, w * 3, 50, w, 50);
		set(max, x, y, w * 4, 50, w, 50);
		set(jmax, x, y, w * 5, 50, w, 50);
		set(con, x, y, w * 6, 50, w, 50);
		set(dojo, x, y, w * 7, 50, w, 50);
		set(bg, x, y, 0, 100, w, 50);
		set(jbg, x, y, w, 100, w, 50); // line 2
		set(jbgh, x, y, w * 2, 100, w, 50);
		set(jbg1, x, y, w * 3, 100, w, 50);
		set(cas, x, y, w * 4, 100, w, 50);
		set(jcas, x, y, w * 5, 100, w, 50);
		set(bbrr, x, y, w * 6, 100, w, 50);
		set(mus, x, y, 0, 150, w, 50);
		set(jm0, x, y, w, 150, w, 50);
		set(jmh, x, y, w * 2, 150, w, 50);
		set(jm1, x, y, w * 3, 150, w, 50);
		set(res, x, y, w * 4, 150, w, 50);
		set(jres, x, y, w * 5, 150, w, 50);
		set(ures, x, y, w * 6, 150, w, 50);
		set(jures, x, y, w * 7, 150, w, 50);
		set(lt, x, y, 0, 200, 1400, 100);
		lt.componentResized(x, y);
	}

	protected void setData(Stage st) {
		sta = st;
		abler(st != null);
		if (st == null)
			return;
		change(true);
		name.setText(st.toString());
		if (st.trail) {
			hea.setText(get(MainLocale.INFO, "time"));
			jhea.setText(st.timeLimit + " secs");
		} else {
			hea.setText(get(MainLocale.INFO, "ht00"));
			jhea.setText(String.valueOf(st.health));
		}
		jlen.setText(String.valueOf(st.len));
		jbg.setText(String.valueOf(st.bg));
		jbgh.setText("<" + st.bgh + "% health:");
		jbg1.setText(String.valueOf(st.bg1));
		jcas.setText(String.valueOf(st.castle));
		jm0.setText(String.valueOf(st.mus0));
		jmh.setText("<" + st.mush + "% health:");
		jm1.setText(String.valueOf(st.mus1));
		jmax.setText(String.valueOf(st.max));
		con.setSelected(!st.non_con);
		dojo.setSelected(st.trail);
		bbrr.setSelected(st.bossGuard);
		barrierAbler();

		Limit lim = st.lim;
		lt.setLimit(lim);
		change(false);

		jres.setEnabled(true);
		jres.setText(generateMinRespawn(st.minSpawn, st.maxSpawn));
		jures.setEnabled(true);
		jures.setText(generateMinRespawn(sta.minUSpawn, sta.maxUSpawn));
	}

	void barrierAbler() {
		boolean bar = false;
		if (sta != null)
			for (SCDef.Line l : sta.data.datas)
				if (l.boss >= 1) {
					bar = true;
					break;
				}
		bbrr.setEnabled(bar);
		if (!bar && sta != null)
			sta.bossGuard = false;
	}

	private void abler(boolean b) {
		bg.setEnabled(b);
		cas.setEnabled(b);
		name.setEnabled(b);
		jhea.setEnabled(b);
		jlen.setEnabled(b);
		jbg.setEnabled(b);
		jbgh.setEnabled(b);
		jbg1.setEnabled(b);
		jcas.setEnabled(b);
		jmax.setEnabled(b);
		con.setEnabled(b);
		mus.setEnabled(b);
		jm0.setEnabled(b);
		jmh.setEnabled(b);
		jm1.setEnabled(b);
		dojo.setEnabled(b);
		lt.abler(b);
	}

	private void addListeners() {

		bg.addActionListener(arg0 -> {
			bvp = new BGViewPage(getFront(), pac.desc.id, sta.bg);
			changePanel(bvp);
		});

		cas.addActionListener(arg0 -> {
			cvp = new CastleViewPage(getFront(), CastleList.from(sta), sta.castle);
			changePanel(cvp);
		});

		mus.addActionListener(arg0 -> {
			mp = new MusicPage(getFront(), pac.desc.id);
			changePanel(mp);
		});

		con.addActionListener(arg0 -> {
			sta.non_con = !con.isSelected();
			setData(sta);
		});

		dojo.addActionListener(arg0 -> {
			sta.trail = dojo.isSelected();
			if (sta.trail) {
				sta.timeLimit = 60;
				hea.setText(get(MainLocale.INFO, "time"));
				jhea.setText(sta.timeLimit + " secs");
			} else {
				sta.timeLimit = 0;
				hea.setText(get(MainLocale.INFO, "ht00"));
				jhea.setText(String.valueOf(sta.health));
			}
		});

		bbrr.setLnr(a -> sta.bossGuard = bbrr.isSelected());
	}

	private void ini() {
		set(hea);
		set(len);
		set(max);
		add(bg);
		add(cas);
		add(con);
		add(dojo);
		add(mus);
		set(jhea);
		set(jlen);
		set(jbg);
		set(jbgh);
		set(jbg1);
		set(jcas);
		set(jmax);
		set(name);
		set(jm0);
		set(jmh);
		set(jm1);
		add(lt);
		set(res);
		set(jres);
		set(ures);
		set(jures);
		con.setSelected(true);
		add(bbrr);
		bbrr.setEnabled(false);

		addListeners();
		abler(false);
	}

	private void input(JTF jtf, String str) {
		if (sta == null)
			return;
		if (jtf == name) {
			str = str.trim();
			if (str.length() > 0)
				sta.names.put(str);
			if (getFront() != null)
				getFront().callBack(null);
			return;
		}
		int val = CommonStatic.parseIntN(str);
		if (jtf == jhea) {
			if (val <= 0)
				return;
			if (!sta.trail)
				sta.health = val;
			else
				sta.timeLimit = val;
		}
		if (jtf == jlen) {
			if (val > 8000)
				val = 8000;
			if (val < 2000)
				val = 2000;
			sta.len = val;
		}
		if (jtf == jmax) {
			if (val <= 0)
				return;
			sta.max = val;
		}

		if (jtf == jbgh)
			sta.bgh = val;

		if (jtf == jmh)
			sta.mush = val;

		if (jtf == jres) {
			try {
				int[] vals = CommonStatic.parseIntsN(jtf.getText());

				if (vals.length == 1) {
					if (vals[0] <= 0)
						return;

					sta.minSpawn = sta.maxSpawn = vals[0];
				} else if (vals.length >= 2) {
					if (vals[0] <= 0 || vals[1] <= 0)
						return;

					if (vals[0] == vals[1]) {
						sta.minSpawn = sta.maxSpawn = vals[0];
					} else if (vals[0] < vals[1]) {
						sta.minSpawn = vals[0];
						sta.maxSpawn = vals[1];
					}
				}

				jres.setText(generateMinRespawn(sta.minSpawn, sta.maxSpawn));
			} catch (Exception ignored) {
			}
		}

		if (jtf == jures) {
			try {
				int[] vals = CommonStatic.parseIntsN(jtf.getText());
				if (vals.length == 1) {
					if (vals[0] <= 0)
						return;
					sta.minUSpawn = sta.maxUSpawn = vals[0];
				} else if (vals.length >= 2) {
					if (vals[0] <= 0 || vals[1] <= 0)
						return;
					if (vals[0] == vals[1])
						sta.minUSpawn = sta.maxUSpawn = vals[0];
					else {
						sta.minUSpawn = Math.min(vals[0], vals[1]);
						sta.maxUSpawn = Math.max(vals[0], vals[1]);
					}
				}
				jures.setText(generateMinRespawn(sta.minUSpawn, sta.maxUSpawn));
			} catch (Exception ignored) {
			}
		}

		if (jtf == jbg || jtf == jbg1) {
			boolean mainbg = jtf == jbg;
			String[] result = CommonStatic.getPackContentID(str);
			if (result[0].isEmpty())
				return;
			if (result[1].isEmpty()) {
				if (!CommonStatic.isInteger(result[0]))
					return;
				Background b = UserProfile.getBCData().bgs.get(CommonStatic.safeParseInt(result[0]));
				if (b == null)
					return;
				if (mainbg) {
					jbg.setText(b.toString());
					sta.bg = b.getID();
				} else {
					jbg1.setText(b.toString());
					sta.bg1 = b.getID();
				}
				return;
			}
			String p = result[0];
			String i = result[1];
			if (CommonStatic.isInteger(p))
				p = Data.hex(CommonStatic.parseIntN(p));
			PackData pack = PackData.getPack(p);
			if (pack == null)
				return;
			Background bg = pack.bgs.get(CommonStatic.safeParseInt(i));
			if (bg == null)
				return;

			if (mainbg) {
				jbg.setText(bg.toString());
				sta.bg = bg.getID();
			} else {
				jbg1.setText(bg.toString());
				sta.bg1 = bg.getID();
			}
		}

		if (jtf == jcas) {
			String[] result = CommonStatic.getPackContentID(str);
			if (result[0].isEmpty()) {
				jm0.setText("null");
				sta.mus0 = null;
				return;
			}
			if (result[1].isEmpty()) {
				if (!CommonStatic.isInteger(result[0]))
					return;
				CastleImg c = CastleList.getList("000000").get(CommonStatic.safeParseInt(result[0]));
				if (c == null)
					return;
				jcas.setText(c.toString());
				sta.castle = c.getID();
				return;
			}
			String p = result[0];
			String i = result[1];
			if (CommonStatic.isInteger(p))
				p = Data.hex(CommonStatic.safeParseInt(p));
			CastleList cl = CastleList.getList(p);
			if (cl == null)
				return;
			CastleImg castle = cl.get(CommonStatic.safeParseInt(i));
			if (castle == null)
				return;

			jcas.setText(castle.toString());
			sta.castle = castle.getID();
		}

		if (jtf == jm0 || jtf == jm1) {
			boolean mainmus = jtf == jm0;
			String[] result = CommonStatic.getPackContentID(str);
			if (result[0].isEmpty()) {
				if (mainmus) {
					jm0.setText("null");
					sta.mus0 = null;
				} else {
					jm1.setText("null");
					sta.mus1 = null;
				}
				return;
			}
			if (result[1].isEmpty()) {
				if (!CommonStatic.isInteger(result[0]))
					return;
				Music m = UserProfile.getBCData().musics.get(CommonStatic.safeParseInt(result[0]));
				if (m == null)
					return;
				if (mainmus) {
					jm0.setText(m.toString());
					sta.mus0 = m.getID();
				} else {
					jm1.setText(m.toString());
					sta.mus1 = m.getID();
				}
				return;
			}
			String p = result[0];
			String i = result[1];
			if (CommonStatic.isInteger(p))
				p = Data.hex(CommonStatic.safeParseInt(p));
			PackData pack = PackData.getPack(p);
			if (pack == null) {
				if (mainmus) {
					jm0.setText("null");
					sta.mus0 = null;
				} else {
					jm1.setText("null");
					sta.mus1 = null;
				}
				return;
			}
			Music music = pack.musics.get(CommonStatic.safeParseInt(i));
			if (music == null)
				return;

			if (mainmus) {
				jm0.setText(str);
				sta.mus0 = music.getID();
			} else {
				jm1.setText(str);
				sta.mus1 = music.getID();
			}
		}
	}

	private void set(JLabel jl) {
		jl.setHorizontalAlignment(SwingConstants.CENTER);
		jl.setBorder(BorderFactory.createEtchedBorder());
		add(jl);
	}

	private void set(JTF jtf) {
		add(jtf);

		jtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent fe) {
				if (isAdj())
					return;
				if (jtf == jbg)
					bgl = 0;
				if (jtf == jbg1)
					bgl = 1;
				if (jtf == jm0)
					musl = 0;
				if (jtf == jm1)
					musl = 1;
			}

			@Override
			public void focusLost(FocusEvent fe) {
				if (isAdj())
					return;
				input(jtf, jtf.getText());
				setData(sta);
			}
		});

	}

	private String generateMinRespawn(int min, int max) {
		if (min == max) {
			return min + "f";
		} else {
			return min + "f ~ " + max + "f";
		}
	}
}