package page.anim;

import common.CommonStatic;
import common.util.anim.AnimCE;
import common.util.anim.MaModel;
import page.MainLocale;
import page.Page;
import page.support.AnimTable;
import page.support.AnimTableTH;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.EventObject;

class MaModelEditTable extends AnimTable<int[]> {

	private static final long serialVersionUID = 1L;

	protected AnimCE anim;
	protected MaModel mm;
	private final Page page;

	protected MaModelEditTable(Page p) {
		super(Page.get(MainLocale.PAGE, "mampm", 11));

		selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setTransferHandler(new AnimTableTH<>(this, 1));
		page = p;
	}

	@Override
	public boolean editCellAt(int row, int column, EventObject e) {
		boolean result = super.editCellAt(row, column, e);
		final Component editor = getEditorComponent();
		if (!(editor instanceof JTextComponent))
			return result;
		JTextComponent jtc = (JTextComponent) editor;
		if (e instanceof KeyEvent)
			jtc.selectAll();
		return result;
	}

	@Override
	public Class<?> getColumnClass(int c) {
		if (lnk[c] == 10 || (lnk[c] >= 4 && lnk[c] <= 6))
			return String.class;
		return Integer.class;
	}

	@Override
	public int getRowCount() {
		if (mm == null)
			return 0;
		return mm.n;
	}

	@Override
	public int[][] getSelected() {
		int[] rows = getSelectedRows();
		int[][] ps = new int[rows.length][];
		for (int i = 0; i < rows.length; i++) {
			ps[i] = mm.parts[rows[i]].clone();
			for (int j = 0; j < i; j++)
				if (ps[i][0] == rows[j])
					ps[i][0] = -j - 10;
		}
		return ps;
	}

	@Override
	public Object getValueAt(int r, int c) {
		if (mm == null || r < 0 || c < 0 || r >= mm.n || c >= getColumnCount())
			return null;
		if (lnk[c] == 0)
			return r;
		if (lnk[c] == 1)
			return mm.parts[r][0];
		if (lnk[c] == 10)
			return mm.strs0[r];
		if (lnk[c] >= 4 && lnk[c] <= 6) {
			int par = c + c - 4;
			return "(" + mm.parts[r][lnk[par]] + ", " + mm.parts[r][lnk[par + 1]] + ")";
		}
		if (lnk[c] >= 7)
			return mm.parts[r][lnk[c] + 3];
		return mm.parts[r][lnk[c]];
	}

	@Override
	public boolean insert(int dst, int[][] data, int[] rows) {
		String[] names = new String[data.length];

		if(rows == null) {
			Arrays.fill(names, "copied");
		} else {
			for(int i = 0; i < names.length; i++) {
				if(i >= rows.length || rows[i] >= mm.strs0.length)
					names[i] = "copied";
				else {
					if(mm.strs0[rows[i]].endsWith("_copied")) {
						names[i] = mm.strs0[rows[i]]+"0";
					} else if(mm.strs0[rows[i]].matches("(.+)?_copied\\d?$")) {
						String[] split = mm.strs0[rows[i]].split("_copied");

						int value = CommonStatic.safeParseInt(split[1]) + 1;

						names[i] = mm.strs0[rows[i]].replaceAll("_copied\\d?$", "_copied"+value);
					} else {
						names[i] = mm.strs0[rows[i]]+"_copied";
					}
				}
			}
		}

		int[] inds = new int[mm.n];
		int[] move = new int[mm.n + data.length];
		int ind = 0;
		for (int i = 0; i < mm.n; i++) {
			if (i == dst)
				for (int j = 0; j < data.length; j++) {
					move[ind] = mm.n + j;
					ind++;
				}
			inds[i] = ind;
			move[ind] = i;
			ind++;
		}
		if (mm.n == dst)
			for (int j = 0; j < data.length; j++) {
				move[ind] = mm.n + j;
				ind++;
			}
		anim.reorderModel(inds);
		mm.parts = Arrays.copyOf(mm.parts, mm.n + data.length);
		mm.strs0 = Arrays.copyOf(mm.strs0, mm.n + data.length);
		for (int i = 0; i < data.length; i++) {
			mm.parts[mm.n + i] = data[i];
			int par = data[i][0];
			if (par <= -10)
				data[i][0] = dst - par - 10;
			mm.strs0[mm.n + i] = names[i];
		}
		mm.n = mm.n + data.length;
		mm.reorder(move);
		mm.check(anim);
		anim.unSave("mamodel paste");
		page.callBack(new int[] { dst, dst + data.length - 1 });
		return true;
	}

	@Override
	public boolean isCellEditable(int r, int c) {
		return lnk[c] != 0;
	}

	@Override
	public boolean reorder(int dst, int[] ori) {
		int[] inds = new int[mm.n];
		int[] move = new int[mm.n];
		int[] orid = new int[mm.n];
		for (int val : ori)
			orid[val] = -1;
		int ind = 0, fin = 0;
		for (int i = 0; i <= mm.n; i++) {
			if (i == dst) {
				fin = ind;
				for (int k : ori) {
					move[ind] = k;
					inds[k] = ind;
					ind++;
				}
			}
			if (i != mm.n && orid[i] != -1) {
				move[ind] = i;
				inds[i] = ind;
				ind++;
			}
		}

		anim.reorderModel(inds);
		mm.reorder(move);
		anim.unSave("mamodel reorder");
		page.callBack(new int[] { fin, fin + ori.length - 1 });
		return true;
	}

	@Override
	public synchronized void setValueAt(Object val, int r, int c) {
		if (mm == null || r >= mm.n)
			return;
		c = lnk[c];
		if (c == 10)
			mm.strs0[r] = ((String) val).trim();
		else if (c >= 4 && c <= 6) {
			int[] ints = CommonStatic.parseIntsN((String)val);
			if (ints.length == 0)
				return;
			int par = c + c - 4;
			mm.parts[r][par] = ints[0];
			if (ints.length >= 2)
				mm.parts[r][par + 1] = ints[1];
		} else {
			int v = (int) val;
			if (c == 1 && (v < -1 || r == 0))
				v = -1;
			else if (c == 2)
				if (v < -1)
					v = -1;
				else if (v >= anim.imgcut.n)
					v = anim.imgcut.n - 1;
			if (c == 1)
				c--;
			else if (c >= 7)
				c += 3;
			mm.parts[r][c] = v;
			if (c == 0)
				mm.check(anim);
		}
		anim.unSave("mamodel edit");
		page.callBack(null);
	}

	protected void setMaModel(AnimCE au) {
		if (cellEditor != null)
			cellEditor.stopCellEditing();
		anim = au;
		mm = au == null ? null : au.mamodel;
		revalidate();
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int x = 0, f = 0;
		while (lnk[f] != 10)
			x += getColumnModel().getColumn(lnk[f++]).getWidth();
		for (int i = 0; i < mm.strs0.length; i++) {
			if (!mm.strs0[i].isEmpty() || anim.imgcut.strs[mm.parts[i][2]].isEmpty())
				continue;
			int leadRow = getSelectionModel().getLeadSelectionIndex();
			int leadCol = getColumnModel().getSelectionModel().getLeadSelectionIndex();
			if (leadRow == i && leadCol == lnk[f])
				continue;
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Insets ins = getInsets();
			FontMetrics fm = g.getFontMetrics();
			int m = 0xfefefefe;
			int c2 = ((getBackground().getRGB() & m) >>> 1) + ((getForeground().getRGB() & m) >>> 1);
			g.setColor(new Color(c2, true));
			g.drawString(anim.imgcut.strs[mm.parts[i][2]], x + ins.left,i * getRowHeight() + getRowHeight() / 2 + fm.getAscent() / 2 - 2);
		}
	}
}