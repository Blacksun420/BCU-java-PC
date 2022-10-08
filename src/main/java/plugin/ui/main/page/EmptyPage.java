package plugin.ui.main.page;

import page.JBTN;
import page.Page;

public class EmptyPage extends Page {

    protected final JBTN back = new JBTN(0, "back");

    public EmptyPage(Page p) {
        super(p);
        add(back); back.addActionListener(e -> back());
    }

    public void back() {
        changePanel(getFront());
    }

    @Override
    protected final void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);
        doResize(x, y);
    }

    void doResize(int x, int y){}

}
