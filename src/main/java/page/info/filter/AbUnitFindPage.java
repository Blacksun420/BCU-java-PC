package page.info.filter;

import common.util.unit.AbForm;
import common.util.unit.AbUnit;
import page.*;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class AbUnitFindPage extends Page implements SupPage<AbUnit> {

    private static final long serialVersionUID = 1L;

    private final JBTN back = new JBTN(0, "back");
    private final JLabel source = new JLabel("Source of unit icon: DB");
    private final JTG show = new JTG(0, "showf");
    private final AbUnitListTable ult = new AbUnitListTable(this);
    private final AbUnitFilterBox ufb;
    private final JScrollPane jsp = new JScrollPane(ult);
    private final JTF seatf = new JTF();
    private final JBTN seabt = new JBTN(0, "search");

    public AbUnitFindPage(Page p) {
        super(p);

        ufb = new AbUnitFilterBox(this);
        ini();
        resized();
    }

    public AbUnitFindPage(Page p, String pack, List<String> parents) {
        super(p);

        ufb = new AbUnitFilterBox(this, pack, parents);
        ini();
        resized();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void callBack(Object o) {
        ult.setList((List<AbForm>) o);
        resized();
    }

    public AbForm getForm() {
        if (ult.getSelectedRow() == -1 || ult.getSelectedRow() > ult.list.size() - 1)
            return null;
        return ult.list.get(ult.getSelectedRow());
    }
    public List<AbForm> getList() {
        return ult.list;
    }

    @Override
    public AbUnit getSelected() {
        AbForm f = getForm();
        return f == null ? null : f.getID().get();
    }

    @Override
    protected void mouseClicked(MouseEvent e) {
        if (e.getSource() != ult)
            return;
        ult.clicked(e.getPoint());
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);
        set(source, x, y, 0, 50, 600, 50);
        set(show, x, y, 250, 0, 200, 50);
        set(seatf, x, y, 550, 0, 1000, 50);
        set(seabt, x, y, 1600, 0, 200, 50);
        if (show.isSelected()) {
            int[] siz = ufb.getSizer();
            set(ufb, x, y, 50, 100, siz[0], siz[1]);
            int mx = 0, my = 0;
            if (siz[2] == 0)
                mx = siz[3];
            else
                my = siz[3];
            set(jsp, x, y, 50 + mx, 100 + my, 2200 - mx, 1150 - my);
        } else
            set(jsp, x, y, 50, 100, 2200, 1150);
        ult.setRowHeight(size(x, y, 50));
    }

    private void addListeners() {
        back.addActionListener(arg0 -> changePanel(getFront()));

        show.addActionListener(arg0 -> {
            if (show.isSelected())
                add(ufb);
            else
                remove(ufb);
        });

        seabt.setLnr((b) -> {
            if (ufb != null) {
                ufb.name = seatf.getText();

                ufb.callBack(null);
            }
        });

        seatf.addActionListener(e -> {
            if (ufb != null) {
                ufb.name = seatf.getText();

                ufb.callBack(null);
            }
        });
    }

    private void ini() {
        add(back);
        add(show);
        add(ufb);
        add(jsp);
        add(source);
        add(seatf);
        add(seabt);
        show.setSelected(true);
        addListeners();
    }
}
