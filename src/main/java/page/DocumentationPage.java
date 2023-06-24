package page;

import javax.swing.*;

public class DocumentationPage extends Page {

    private static final long serialVersionUID = 1L;

    public static final int max = 2;
    public static final int[] maxes = new int[]{3, 0}; //Subsections, make sure length = maxes
    private final JBTN back = new JBTN(MainLocale.PAGE, "back");
    private final JList<String> secs = new JList<>();
    private final JScrollPane jsecs = new JScrollPane(secs);
    private final JList<String> subs = new JList<>();
    private final JScrollPane jsubs = new JScrollPane(subs);
    private final JTA subText = new JTA();
    private final JScrollPane jtext = new JScrollPane(subText);

    protected DocumentationPage(Page p) {
        super(p);
        ini();
        resized();
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);
        set(jsecs, x, y, 50, 100, 300, 1150);
        set(jsubs, x, y, 350, 100, 300, 1150);
        set(jtext, x, y, 650, 100, 1600, 1150);
    }

    @Override
    public JButton getBackButton() {
        return back;
    }

    private void ini() {
        add(back);
        add(jsecs);
        add(jsubs);
        add(jtext);

        secs.setListData(get(MainLocale.DOCS, "sec", max));
        subs.setEnabled(false);
        subText.setEditable(false);
        addListeners();
    }

    private void addListeners() {
        back.addActionListener(l -> changePanel(getFront()));

        secs.addListSelectionListener(l -> {
            if (secs.getSelectedIndex() == -1) {
                subs.setListData(new String[0]);
                subs.setEnabled(false);
                return;
            }
            subs.setEnabled(true);
            subs.setListData(get(MainLocale.DOCS, "sec" + secs.getSelectedIndex() + "sub", maxes[secs.getSelectedIndex()]));
        });

        subs.addListSelectionListener(l -> {
            if (subs.getSelectedIndex() == -1) {
                subText.setText("");
                return;
            }
            subText.setText(get(MainLocale.DOCS, "sec" + secs.getSelectedIndex() + "sub" + subs.getSelectedIndex() + "text"));
        });
    }
}
