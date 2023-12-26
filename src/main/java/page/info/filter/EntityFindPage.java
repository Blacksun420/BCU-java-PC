package page.info.filter;

import page.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseEvent;
import java.util.List;

public abstract class EntityFindPage<R> extends DefaultPage {

    private static final long serialVersionUID = 1L;

    protected final JTG show = new JTG(0, "showf");
    protected EntityListTable<R> elt;
    protected EntityFilterBox efb;
    protected JScrollPane jsp;
    protected AdvProcFilterPage adv;
    protected final JTF seatf = new JTF();
    protected final JBTN favs = new JBTN(MainLocale.PAGE, "addfav");
    private final JTG advs = new JTG(MainLocale.PAGE, "advance");

    public EntityFindPage(Page p) {
        super(p);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void callBack(Object o) {
        elt.setList((List<R>) o);
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
        super.resized(x, y);
        set(show, x, y, 250, 0, 150, 50);
        set(advs, x, y, 400, 0, 150, 50);
        set(seatf, x, y, 600, 0, 1050, 50);
        set(favs, x, y, 1700, 0, 550, 50);

        int[] coords = new int[]{50, 2200};

        if (show.isSelected()) {
            int[] siz = efb.getSizer();
            set(efb, x, y, 50, 100, siz[0], siz[1]);
            coords[0] += siz[3];
            coords[1] -= siz[3];
        }
        if (advs.isSelected()) {
            coords[show.isSelected() ? 0 : 1] -= 50;

            set(adv, x, y, coords[0], 100, ProcFilterTable.tabW * 3 + 25, 1150);
            coords[0] += ProcFilterTable.tabW * 3 + 75;
            coords[1] -= ProcFilterTable.tabW * 3 + 25;
        } else if (adv != null)
            set(adv, 0, 0, 0, 0, 0, 0);
        set(jsp, x, y, coords[0], 100, coords[1], 1150);
        elt.setRowHeight(size(x, y, 50));
    }

    protected void addListeners() {
        show.addActionListener(arg0 -> {
            if (show.isSelected())
                add(efb);
            else
                remove(efb);
        });

        advs.addActionListener(arg0 -> {
            if (advs.isSelected())
                add(adv);
            else
                remove(adv);
        });

        seatf.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                search(seatf.getText(), false);
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                search(seatf.getText(), false);
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                search(seatf.getText(), false);
            }
        });
    }

    public void search(String text, boolean setText) {
        if (setText)
            seatf.setText(text);
        else if (efb != null) {
            efb.name = text;
            efb.callBack(null);
        }
    }

    public String getName() {
        if (efb != null)
            return efb.name;
        return "";
    }

    protected void ini() {
        add(show);
        add(efb);
        add(jsp);
        add(seatf);
        seatf.setHintText(get(MainLocale.PAGE,"search"));
        add(favs);
        add(advs);
        assignSubPage(adv);
        show.setSelected(true);
        addListeners();
    }
}
