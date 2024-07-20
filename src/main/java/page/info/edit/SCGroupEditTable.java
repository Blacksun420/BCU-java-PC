package page.info.edit;

import common.CommonStatic;
import common.pack.Identifier;
import common.util.Data;
import common.util.stage.SCDef;
import common.util.stage.SCGroup;
import common.util.unit.AbEnemy;
import page.MainLocale;
import page.support.AbJTable;
import page.support.EnemyTCR;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.Map.Entry;

class SCGroupEditTable extends AbJTable {

	private static final long serialVersionUID = 1L;

	private static String[] title;

	static {
		redefine();
	}

	protected static void redefine() {
		title = MainLocale.getLoc(MainLocale.INFO, "t1", "t8");
	}

	protected final SCDef scd;

	protected SCGroupEditTable(SCDef sc) {
		super(title);

		scd = sc;
		setDefaultRenderer(Integer.class, new EnemyTCR());
	}

	@Override
	public boolean editCellAt(int r, int c, EventObject e) {
		boolean result = super.editCellAt(r, c, e);
		Component editor = getEditorComponent();
		if (editor == null || !(editor instanceof JTextComponent))
			return result;
		JTextComponent jtf = ((JTextComponent) editor);
		if (e instanceof KeyEvent)
			jtf.selectAll();
		return result;
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return lnk[c] == 0 ? Integer.class : String.class;
	}

	@Override
	public int getColumnCount() {
		return title.length;
	}

	@Override
	public String getColumnName(int c) {
		return title[lnk[c]];
	}

	@Override
	public synchronized int getRowCount() {
		if (scd == null)
			return 0;
		return scd.smap.size();
	}

	@Override
	public synchronized Object getValueAt(int r, int c) {
		if (scd == null || r < 0 || c < 0 || r >= scd.smap.size() || c > lnk.length)
			return null;
		return get(r, lnk[c]);
	}

	@Override
	public boolean isCellEditable(int r, int c) {
		return lnk[c] != 0;
	}

	@Override
	public synchronized void setValueAt(Object arg0, int r, int c) {
		if (scd == null)
			return;
		c = lnk[c];
		if (c == 1) {
			int[] is = CommonStatic.parseIntsN((String) arg0);
			if (is.length == 1)
				set(r, is[0]);
		}
	}

	protected synchronized void addLine(AbEnemy enemy) {
		if (scd == null)
			return;
		int ind = getSelectedRow();

		if (enemy == null)
			return;
		Identifier<AbEnemy> eid = enemy.getID();
		if (scd.smap.containsKey(eid))
			return;
		scd.smap.put(eid, 0);
		ind++;
		if (ind < 0)
			clearSelection();
		else
			setRowSelectionInterval(ind, ind);
	}

	protected synchronized void remLine() {
		if (scd == null)
			return;
		int ind = getSelectedRow();
		if (ind == -1)
			return;
		scd.smap.remove(scd.getSMap()[ind].getKey());
		if (ind >= scd.smap.size())
			ind--;
		if (ind < 0)
			clearSelection();
		else
			setRowSelectionInterval(ind, ind);
	}

	private Object get(int r, int c) {
		Entry<Identifier<AbEnemy>, Integer>[] info = scd.getSMap();
		if (r >= info.length)
			return null;
		if (c == 0)
			return Identifier.get(info[r].getKey());
		else if (c == 1) {
			int g = info[r].getValue();
			SCGroup scg = scd.sub.get(g);
			return scg == null ? g != 0 ? Data.trio(g) + " - invalid" : "" : scg.toString();
		}
		return null;
	}

	private void set(int r, int v) {
		Entry<Identifier<AbEnemy>, Integer>[] info = scd.getSMap();
		if (r >= info.length)
			return;
		Entry<Identifier<AbEnemy>, Integer> data = info[r];
		if (v < 0)
			v = 0;
		scd.smap.put(data.getKey(), v);
	}
}