package page.info;

import common.CommonStatic;
import common.pack.Identifier;
import common.pack.PackData;
import common.pack.UserProfile;
import common.system.ENode;
import common.util.Data;
import common.util.stage.SCDef.Line;
import common.util.stage.SCGroup;
import common.util.stage.Stage;
import common.util.unit.EneRand;
import common.util.unit.Enemy;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.pack.EREditPage;
import page.support.AbJTable;
import page.support.EnemyTCR;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class StageTable extends AbJTable {

	private static final long serialVersionUID = 1L;

	private static String[] title;

	static {
		redefine();
	}

	public static void redefine() {
		title = Page.get(MainLocale.INFO, "t", 11);
	}

	protected Object[][] data;

	private final Page page;

	protected StageTable(Page p) {
		super(title);

		page = p;

		setDefaultRenderer(Enemy.class, new EnemyTCR());
	}

	@Override
	public Class<?> getColumnClass(int c) {
		if (lnk[c] == 1)
			return Enemy.class;
		else
			return Object.class;
	}

	@Override
	public int getColumnCount() {
		return title.length;
	}

	@Override
	public String getColumnName(int arg0) {
		return title[arg0];
	}

	@Override
	public int getRowCount() {
		if (data == null)
			return 0;
		return data.length;
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		if (lnk[columnAtPoint(e.getPoint())] == 2) {
			return "{hp, atk}";
		} else {
			return null;
		}
	}

	@Override
	public Object getValueAt(int r, int c) {
		if (data == null || r < 0 || c < 0 || r >= data.length || c >= data[r].length)
			return null;
		c = lnk[c];
		if (c == 2)
			return data[r][c] + "%";
		return data[r][c];
	}

	protected void clicked(Point p) {
		if (data == null)
			return;
		int c = getColumnModel().getColumnIndexAtX(p.x);

		c = lnk[c];

		int r = p.y / getRowHeight();

		if (r < 0 || r >= data.length || c != 1)
			return;

		if(data[r][c] instanceof Enemy) {
			Enemy e = (Enemy) data[r][c];

			if(e.anim == null)
				return;

			if (!(data[r][2] instanceof String))
				return;

			List<Enemy> eList = new ArrayList<>();
			List<int[]> muls = new ArrayList<>();
			for (Object[] datum : data) {
				if (!(datum[c] instanceof Enemy) || eList.contains(datum[c]))
					continue;
				eList.add((Enemy) datum[c]);

				final int[] b;
				if (datum[c] == e)
					b = CommonStatic.parseIntsN((String) data[r][2]);
				else
					b = CommonStatic.parseIntsN((String) datum[2]);

				if (b.length == 1)
					muls.add(new int[]{b[0], b[0]});
				else
					muls.add(new int[]{b[0], b[1]});
			}
			MainFrame.changePanel(new EnemyInfoPage(page, ENode.getListE(eList, e, muls)));
		} else if(data[r][c] instanceof EneRand) {
			EneRand e = (EneRand) data[r][c];

			PackData.UserPack pac = UserProfile.getUserPack(e.id.pack);

			if (pac != null) {
				MainFrame.changePanel(new EREditPage(page, pac));
			}
		}
	}

	protected void setData(Stage st) {
		Line[] info = st.data.getSimple();

		data = new Object[info.length][11];

		for (int i = 0; i < info.length; i++) {
			int ind = info.length - i - 1;
			data[ind][1] = Identifier.get(info[i].enemy);
			data[ind][0] = info[i].boss >= 1 ? MainLocale.getLoc(MainLocale.INFO,"b" + info[i].boss) : "";
			data[ind][2] = info[i].multiple == info[i].mult_atk ? info[i].multiple+"" : CommonStatic.toArrayFormat(info[i].multiple, info[i].mult_atk);
			data[ind][3] = info[i].number == 0 ? MainLocale.getLoc(MainLocale.UTIL, "inf") : info[i].number;

			if (info[i].castle_0 >= info[i].castle_1)
				data[ind][4] = info[i].castle_0 + "%";
			else
				data[ind][4] = info[i].castle_0 + "~" + info[i].castle_1 + "%";

			if (Math.abs(info[i].spawn_0) >= Math.abs(info[i].spawn_1))
				data[ind][5] = info[i].spawn_0;
			else
				data[ind][5] = info[i].spawn_0 + "~" + info[i].spawn_1;

			if (info[i].respawn_0 == info[i].respawn_1)
				data[ind][6] = info[i].respawn_0;
			else
				data[ind][6] = info[i].respawn_0 + "~" + info[i].respawn_1;

			data[ind][7] = info[i].layer_0 == info[i].layer_1 ? info[i].layer_0
					: info[i].layer_0 + "~" + info[i].layer_1;

			data[ind][8] = info[i].kill_count;

			int g = info[i].group;
			SCGroup scg = st.data.sub.get(g);

			data[ind][9] = info[i].doorchance == 0 ? "0%" : info[i].doorchance + "% - " +
					(info[i].doordis_0 == info[i].doordis_1 ? info[i].doordis_0 : info[i].doordis_0 + " ~ " + info[i].doordis_1);
			data[ind][10] = scg == null ? g != 0 ? Data.trio(g) + " - invalid" : "" : scg.toString();

		}
	}
}