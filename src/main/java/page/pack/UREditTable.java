package page.pack;

import common.CommonStatic;
import common.pack.Identifier;
import common.pack.PackData;
import common.pack.UserProfile;
import common.system.Node;
import common.util.unit.*;
import common.util.unit.rand.UREnt;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.info.UnitInfoPage;
import page.support.AbJTable;
import page.support.InTableTH;
import page.support.Reorderable;
import page.support.UnitTCR;
import utilpc.UtilPC;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventObject;

public class UREditTable extends AbJTable implements Reorderable {

    private static final long serialVersionUID = 1L;

    private static String[] title;

    static {
        redefine();
    }

    protected static void redefine() {
        title = Page.get(MainLocale.INFO, "ur", 3);
    }

    private UniRand rand;
    private final Page page;
    private final String pack;

    protected UREditTable(Page p, PackData.UserPack pack) {
        super(title);

        page = p;
        setTransferHandler(new InTableTH(this));
        setDefaultRenderer(Integer.class, new UnitTCR(new int[title.length], 0));
        this.pack = pack == null ? null : pack.desc.id;
    }

    @Override
    public boolean editCellAt(int r, int c, EventObject e) {
        boolean result = super.editCellAt(r, c, e);
        Component editor = getEditorComponent();
        if (!(editor instanceof JTextComponent))
            return result;
        JTextComponent jtf = ((JTextComponent) editor);
        if (e instanceof KeyEvent)
            jtf.selectAll();
        if (lnk[c] == 0 && jtf.getText().length() > 0) {
            Form frm = (Form) get(r, c);

            if(frm != null) {
                jtf.setText(frm.uid + "");
            } else {
                jtf.setText("NULL");
            }
        }
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
        if (rand == null)
            return 0;
        return rand.list.size();
    }

    @Override
    public synchronized Object getValueAt(int r, int c) {
        if (rand == null || r < 0 || c < 0 || r >= rand.list.size() || c > lnk.length)
            return null;
        return get(r, lnk[c]);
    }

    @Override
    public boolean isCellEditable(int r, int c) {
        return true;
    }

    @Override
    public synchronized void reorder(int ori, int fin) {
        if (fin > ori)
            fin--;
        if (fin == ori)
            return;
        rand.list.add(fin, rand.list.remove(ori));
    }

    @Override
    public synchronized void setValueAt(Object arg0, int r, int c) {
        if (rand == null)
            return;
        c = lnk[c];
        if (c > 0) {
            int[] is = CommonStatic.parseIntsN((String) arg0);
            if (is.length == 0)
                return;
            if (is.length == 1)
                set(r, c, is);
            else
                set(r, c, is);
        } else {
            int i = arg0 instanceof Integer ? (Integer) arg0 : CommonStatic.parseIntN((String) arg0);
            set(r, c, new int[]{i});
        }
    }

    protected synchronized int addLine(AbForm form) {
        if (rand == null || form == null)
            return -1;
        int ind = getSelectedRow() + 1;
        UREnt ur = new UREnt(form);
        rand.list.add(ind, ur);
        return rand.list.size() - 1;
    }

    protected synchronized void clicked(Point p) {
        if (rand == null)
            return;
        int c = getColumnModel().getColumnIndexAtX(p.x);
        c = lnk[c];
        if (c != 0)
            return;
        int r = p.y / getRowHeight();
        UREnt ur = rand.list.get(r);
        AbUnit u = Identifier.get(ur.ent.getID());
        if (u instanceof Unit) {
            java.util.List<Unit> uList = new ArrayList<>();
            for (int i = rand.list.size() - 1; i >= 0; i--) {
                UREnt es = rand.list.get(i);
                AbUnit su = Identifier.get(es.ent.getID());
                if (su instanceof Unit && !uList.contains(su)) {
                    uList.add((Unit) su);
                }
            }
            MainFrame.changePanel(new UnitInfoPage(page, Node.getList(uList,(Unit) u)));
        } else if (u instanceof UniRand && pack != null && !u.getID().pack.equals(pack))
            MainFrame.changePanel(new EREditPage(page, UserProfile.getUserPack(((UniRand) u).id.pack)));
    }

    protected synchronized int remLine() {
        if (rand == null)
            return -1;
        int ind = getSelectedRow();
        if (ind >= 0)
            rand.list.remove(ind);
        if (rand.list.size() > 0) {
            if (ind == 0)
                ind = 1;
            return ind - 1;
        }
        return -1;
    }

    protected synchronized void setData(UniRand st) {
        if (cellEditor != null)
            cellEditor.stopCellEditing();
        rand = st;
        clearSelection();
    }

    private Object get(int r, int c) {
        if (rand == null)
            return null;
        UREnt ur = rand.list.get(r);
        if (c == 0)
            return ur.ent;
        else if (c == 1)
            return ur.ent instanceof Form ? UtilPC.lvText((Form) ur.ent, ur.lv.getLvs())[0] : new String[]{"1"};
        else if (c == 2)
            return ur.share;
        return null;
    }

    private void set(int r, int c, int[] v) {
        if (rand == null)
            return;
        UREnt ur = rand.list.get(r);
        if (v[0] < 0)
            v[0] = 0;
        if (c == 1) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j : v)
                list.add(j);
            ur.lv.setLvs(list);
        } else if (c == 2)
            ur.share = v[0];
    }

}
