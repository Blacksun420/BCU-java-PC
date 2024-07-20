package page.info.edit;

import common.CommonStatic;
import common.pack.PackData;
import common.util.stage.Stage;
import common.util.stage.StageLimit;
import page.*;
import page.support.CrossList;
import utilpc.Interpret;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class StageLimitTable extends Page {

    private static final long serialVersionUID = 1L;

    private static String[] climits;

    static {
        redefine();
    }

    protected static void redefine() {
        climits = Page.get(MainLocale.INFO, "ht2", 2);
    }

    private final JL bank = new JL(MainLocale.INFO, "ht20");
    private final JL cres = new JL(MainLocale.INFO, "ht21");
    private final JTF jban = new JTF();
    private final JTF jcre = new JTF();

    private final CrossList<String> jlco = new CrossList<>(Interpret.getComboFilter(0));
    private final JScrollPane jsco = new JScrollPane(jlco);
    private final JBTN banc = new JBTN(MainLocale.PAGE, "ban0");

    private final PackData.UserPack pac;

    private StageLimit stli;

    protected StageLimitTable(Page p, PackData.UserPack pack) {
        super(p);
        pac = pack;
        ini();
    }

    @Override
    protected void resized(int x, int y) {
        int w = 1400 / 8;

        set(bank, x, y, 0, 0, w, 50);
        set(jban, x, y, w, 0, w, 50);
        set(cres, x, y, w * 2, 0, w, 50);
        set(jcre, x, y, w * 3, 0, w, 50);
        set(jsco, x, y, w * 5, 0, w * 2, 250);
        set(banc, x, y, (int) (w * 5.5), 250, w, 50);
    }

    private void ini() {
        add(bank);
        reg(jban);
        add(cres);
        reg(jcre);
        add(jsco);
        add(banc);

        jlco.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlco.setCheck(i -> stli != null && stli.bannedCatCombo.contains(i));
        jlco.addListSelectionListener(x -> {
            banc.setEnabled(jlco.getSelectedIndex() != -1);
            banc.setText(MainLocale.PAGE, "ban" + (!stli.bannedCatCombo.contains(jlco.getSelectedIndex()) ? "0" : "1"));
        });

        banc.setLnr(x -> {
            if (stli == null || jlco.getSelectedIndex() == -1)
                return;

            if (stli.bannedCatCombo.contains(jlco.getSelectedIndex())) {
                stli.bannedCatCombo.remove(jlco.getSelectedIndex());
                banc.setText(MainLocale.PAGE, "ban0");
            } else {
                stli.bannedCatCombo.add(jlco.getSelectedIndex());
                banc.setText(MainLocale.PAGE, "ban1");
            }

            jlco.repaint();
        });

        addListeners();
    }

    public void setData(Stage st) {
        if (st == null) {
            abler(false);
            return;
        }
        stli = st.getCont().stageLimit == null ? st.getCont().stageLimit = new StageLimit() : st.getCont().stageLimit;
        jban.setText(stli.maxMoney + "");
        jcre.setText(stli.globalCooldown + "");
        jlco.repaint();
        abler(true);
    }

    private void abler(boolean b) {
        jban.setEnabled(b);
        jcre.setEnabled(b);
        jlco.setEnabled(b);
        banc.setEnabled(b && jlco.getSelectedIndex() != -1);
    }

    private void reg(JTF jtf) { // using "reg" for "register" because "set" is already used for ui
        add(jtf);

        jtf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (getFront().isAdj())
                    return;
                input(jtf, jtf.getText());
                getFront().callBack(stli);
            }
        });
    }

    private void input(JTF jtf, String text) {
        if (jtf == jban) {
            stli.maxMoney = Math.max(CommonStatic.parseIntN(text), 0);
        } else if (jtf == jcre)
            stli.globalCooldown = Math.max(CommonStatic.parseIntN(text), 0);
    }

    private void addListeners() {
    }

    @Override
    protected JButton getBackButton() {
        return null;
    }
}
