package page.info.edit;

import common.CommonStatic;
import common.pack.PackData.UserPack;
import common.util.stage.Limit;
import common.util.stage.StageLimit;
import common.util.stage.StageMap;
import page.*;
import page.pack.PackSavePage;
import page.support.CrossList;
import utilpc.Interpret;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

public class StageLimitTable extends Page {

    private static final long serialVersionUID = 1L;

    private static String[] climits;

    static {
        redefine();
    }

    protected static void redefine() {
        climits = Page.get(MainLocale.INFO, "ht2", 2);
    }

    private final JTF[] star = new JTF[4];
    private final JL cost = new JL(MainLocale.INFO, "chcos");
    private final JTF cos = new JTF();

    private final JList<Limit> jll = new JList<>();
    private final JScrollPane jspl = new JScrollPane(jll);
    private final JBTN addl = new JBTN(0, "addlim");
    private final JBTN reml = new JBTN(0, "remlim");
    private final LimitTable lt;

    private final JL bank = new JL(MainLocale.INFO, "ht20");
    private final JL cres = new JL(MainLocale.INFO, "ht21");
    private final JTF jban = new JTF();
    private final JTF jcre = new JTF();
    private final JBTN prog = new JBTN(MainLocale.PAGE, "csav");
    private final JTF jnam = new JTF();

    private final CrossList<String> jlco = new CrossList<>(Interpret.getComboFilter(0));
    private final JScrollPane jsco = new JScrollPane(jlco);
    private final JBTN banc = new JBTN(MainLocale.PAGE, "ban0");

    private final UserPack pac;

    private StageLimit stli;
    private StageMap map;

    protected StageLimitTable(Page p, UserPack pack) {
        super(p);
        pac = pack;
        lt = new LimitTable(p, this, pack);
        ini();
    }

    @Override
    protected void renew() {
        lt.renew();
    }

    @Override
    protected void resized(int x, int y) {
        int w = 1400 / 8;
        if (jll.isSelectionEmpty()) {
            set(lt, x, y, 0, 0, 0, 0);

            set(jspl, x, y, w * 2, 50, w * 2, 200);
            set(addl, x, y, w * 2, 250, w, 50);
            set(reml, x, y, w * 3, 250, w, 50);

            set(jsco, x, y, w * 6, 0, w * 2, 250);
            set(banc, x, y, w * 6, 250, w * 2, 50);
            set(prog, x, y, 0, 200, w * 2, 50);
        } else {
            set(lt, x, y, 0, 200, 1400, 100);

            set(jspl, x, y, w * 2, 50, w * 2, 100);
            set(addl, x, y, w * 2, 150, w, 50);
            set(reml, x, y, w * 3, 150, w, 50);

            set(jsco, x, y, w * 6, 0, w * 2, 150);
            set(banc, x, y, w * 6, 150, w * 2, 50);
            set(prog, x, y, w * 4, 150, w * 2, 50);
        }
        set(jnam, x, y, 0, 0, w * 2, 50);
        for (int i = 0; i < 4; i++)
            set(star[i], x, y, w * (2 + i), 0, w, 50);

        set(bank, x, y, 0, 50, w, 50);
        set(cres, x, y, 0, 100, w, 50);
        set(cost, x, y, 0, 150, w, 50);
        set(jban, x, y, w, 50, w, 50);
        set(jcre, x, y, w, 100, w, 50);
        set(cos, x, y, w, 150, w, 50);
    }

    @Override
    public void callBack(Object o) {
        if (o instanceof Limit)
            setLimit((Limit)o);
    }

    private void ini() {
        reg(jnam);
        add(cost);
        reg(cos);
        for (int i = 0; i < 4; i++)
            reg(star[i] = new JTF());

        add(jspl);
        add(addl);
        add(reml);
        add(lt);

        add(bank);
        reg(jban);
        add(cres);
        add(prog);
        reg(jcre);
        add(jsco);
        add(banc);

        jlco.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlco.setCheck(i -> stli != null && stli.bannedCatCombo.contains(i));
        addListeners();
    }

    public void setData(StageMap map) {
        if (map == null) {
            abler(false);
            setLimit(null);
            return;
        }
        this.map = map;
        stli = map.stageLimit == null ? map.stageLimit = new StageLimit() : map.stageLimit;
        setListL();
        jnam.setText(map.names.toString());
        cos.setText(String.valueOf(map.price + 1));
        String str = get(MainLocale.INFO, "star") + ": ";
        for (int i = 0; i < 4; i++)
            if (i < map.stars.length)
                star[i].setText(i + 1 + str + map.stars[i] + "%");
            else
                star[i].setText(i + 1 + str + "/");

        jban.setText(stli.maxMoney + "");
        jcre.setText(stli.globalCooldown + "");
        jlco.repaint();
        abler(true);
    }

    private void abler(boolean b) {
        jll.setEnabled(b);
        jnam.setEnabled(b);
        cos.setEnabled(b);
        for (JTF jtf : star)
            jtf.setEnabled(b);
        jban.setEnabled(b);
        jcre.setEnabled(b);
        jlco.setEnabled(b);
        banc.setEnabled(b && jlco.getSelectedIndex() != -1);
        addl.setEnabled(b);
    }

    private void setLimit(Limit l) {
        reml.setEnabled(l != null);
        lt.setLimit(l);
        getFront().fireDimensionChanged();
    }

    private void setListL() {
        Limit l = jll.getSelectedValue();
        change(map.lim.toArray(new Limit[0]), jll::setListData);
        if (!map.lim.contains(l))
            l = null;
        setLimit(l);
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
        if (jtf == jnam) {
            map.names.put(text);
            getFront().callBack(map);
        } else if (jtf == cos)
            map.price = Math.max(0 , Math.min(CommonStatic.parseIntN(text) - 1, 9));
        else if (jtf == jban)
            stli.maxMoney = Math.max(CommonStatic.parseIntN(text), 0);
        else if (jtf == jcre)
            stli.globalCooldown = Math.max(CommonStatic.parseIntN(text), 0);

        for (int i = 0; i < 4; i++)
            if (jtf == star[i]) {
                String[] strs = text.split(" ");
                int[] vals = CommonStatic.parseIntsN(strs[strs.length - 1]);
                int val = vals.length == 0 ? -1 : vals[vals.length - 1];
                int[] sr = map.stars;
                if (i == 0 && val <= 0)
                    val = 100;
                if (i < sr.length)
                    if (val > 0)
                        sr[i] = val;
                    else
                        map.stars = Arrays.copyOf(sr, i);
                else if (val > 0) {
                    int[] ans = new int[i + 1];
                    for (int j = 0; j < i; j++)
                        if (j < sr.length)
                            ans[j] = sr[j];
                        else
                            ans[j] = sr[sr.length - 1];
                    ans[i] = val;
                    map.stars = ans;
                }
                break;
            }
        setData(map);
    }

    private void addListeners() {
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

        prog.setLnr(p -> changePanel(new PackSavePage(getFront(), pac, map)));

        addl.setLnr(e -> {
            map.lim.add(new Limit());
            setListL();
        });

        reml.setLnr(e -> {
            map.lim.remove(jll.getSelectedValue());
            setListL();
        });

        jll.addListSelectionListener(e -> {
            if (isAdj() || jll.getValueIsAdjusting())
                return;
            setLimit(jll.getSelectedValue());
        });
    }

    @Override
    public JButton getBackButton() {
        return null;
    }
}
