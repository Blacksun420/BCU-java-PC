package page.info.filter;

import page.JBTN;
import page.JTF;
import page.JTG;
import page.Page;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class EntityFindPage<R> extends Page {

    private static final long serialVersionUID = 1L;

    protected final JBTN back = new JBTN(0, "back");
    protected final JLabel source = new JLabel("All BC unit icons belong to Spica");
    protected final JTG show = new JTG(0, "showf");
    protected EntityListTable<R> elt;
    protected EntityFilterBox efb;
    protected JScrollPane jsp;
    protected final JTF seatf = new JTF();
    protected final JBTN seabt = new JBTN(0, "search");

    public EntityFindPage(Page p) {
        super(p);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void callBack(Object o) {
        elt.setList((List<R>) o);
        resized();
    }

    @Override
    protected void mouseClicked(MouseEvent e) {
        if (e.getSource() != elt)
            return;
        elt.clicked(e.getPoint());
    }

    public List<R> getList() {
        return elt.list;
    }

    public void r(int x,int y) {
        resized(x, y);
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
            int[] siz = efb.getSizer();
            set(efb, x, y, 50, 100, siz[0], siz[1]);
            int mx = 0, my = 0;
            if (siz[2] == 0)
                mx = siz[3];
            else
                my = siz[3];
            set(jsp, x, y, 50 + mx, 100 + my, 2200 - mx, 1150 - my);
        } else
            set(jsp, x, y, 50, 100, 2200, 1150);
        elt.setRowHeight(size(x, y, 50));
    }

    private void addListeners() {
        back.addActionListener(arg0 -> changePanel(getFront()));

        show.addActionListener(arg0 -> {
            if (show.isSelected())
                add(efb);
            else
                remove(efb);
        });

        seabt.setLnr((b) -> {
            if (efb != null) {
                efb.name = seatf.getText();
                efb.callBack(null);
            }
        });

        seatf.addActionListener(e -> {
            if (efb != null) {
                efb.name = seatf.getText();
                efb.callBack(null);
            }
        });
    }

    protected void ini() {
        add(back);
        add(show);
        add(efb);
        add(jsp);
        add(source);
        add(seatf);
        add(seabt);
        show.setSelected(true);
        addListeners();
    }
}
