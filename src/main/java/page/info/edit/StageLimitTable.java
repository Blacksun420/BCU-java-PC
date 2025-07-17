package page.info.edit;

import common.CommonStatic;
import common.pack.PackData.UserPack;
import common.util.stage.Limit;
import common.util.stage.StageMap;
import page.*;
import page.pack.PackSavePage;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

public class StageLimitTable extends Page {

    private static final long serialVersionUID = 1L;

    private final JTF[] star = new JTF[4];
    private final JL cost = new JL(MainLocale.INFO, "chcos");
    private final JTF cos = new JTF();

    private final JList<Limit> jll = new JList<>();
    private final JScrollPane jspl = new JScrollPane(jll);
    private final JBTN addl = new JBTN(0, "addlim");
    private final JBTN reml = new JBTN(0, "remlim");
    private final JTF lname = new JTF();
    private final LimitTable lt;
    private final JScrollPane jt;

    private final JBTN prog = new JBTN(MainLocale.PAGE, "csav");
    private final JTF jnam = new JTF();

    private final UserPack pac;

    private StageMap map;

    protected StageLimitTable(Page p, UserPack pack) {
        super(p);
        pac = pack;
        lt = new LimitTable(p, this, pack);
        jt = new JScrollPane(lt);
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
            set(jt, x, y, 0, 0, 0, 0);

            set(jspl, x, y, 0, 50, w * 2, 200);
            set(addl, x, y, 0, 250, w, 50);
            set(reml, x, y, w, 250, w, 50);
            set(lname, x, y, 0, 0, 0, 0);
        } else {
            set(jt, x, y, 0, 150, 1400, 150);
            lt.resized(x, y);
            lt.setPreferredSize(size(x, y, lt.getPWidth(), lt.getPHeight()).toDimension());
            jt.getHorizontalScrollBar().setUnitIncrement(25);
            jt.getVerticalScrollBar().setUnitIncrement(25);
            jt.revalidate();

            set(jspl, x, y, 0, 50, w * 2, 100);
            set(addl, x, y, w * 2, 100, w, 50);
            set(reml, x, y, w * 3, 100, w, 50);
            set(lname, x, y, w * 4, 50, w * 2, 50);
        }
        set(jnam, x, y, 0, 0, w * 2, 50);
        for (int i = 0; i < 4; i++)
            set(star[i], x, y, w * (2 + i), 0, w, 50);
        set(prog, x, y, w * 2, 50, w * 2, 50);

        set(cost, x, y, w * 6, 0, w, 50);
        set(cos, x, y, w * 7, 0, w, 50);
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
        add(jt);
        reg(lname);
        lname.setHintText(get(MainLocale.PAGE,"mampm10"));

        add(prog);
        assignSubPage(lt);
        addListeners();
    }

    public void setData(StageMap map) {
        if (map == null) {
            abler(false);
            setLimit(null);
            return;
        }
        this.map = map;
        setListL();
        jnam.setText(map.names.toString());
        cos.setText(String.valueOf(map.price + 1));
        String str = get(MainLocale.INFO, "star") + ": ";
        for (int i = 0; i < 4; i++)
            if (i < map.stars.length)
                star[i].setText(i + 1 + str + map.stars[i] + "%");
            else
                star[i].setText(i + 1 + str + "/");

        abler(true);
    }

    private void abler(boolean b) {
        jll.setEnabled(b);
        jnam.setEnabled(b);
        cos.setEnabled(b);
        for (JTF jtf : star)
            jtf.setEnabled(b);
        addl.setEnabled(b);
    }

    private void setLimit(Limit l) {
        reml.setEnabled(l != null);
        lname.setEnabled(l instanceof Limit.PackLimit);
        if (l instanceof Limit.PackLimit)
            lname.setText(((Limit.PackLimit)l).name);
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
                getFront().callBack(jll.getSelectedValue());
            }
        });
    }

    private void input(JTF jtf, String text) {
        if (jtf == jnam) {
            map.names.put(text);
            getFront().callBack(map);
        } else if (jtf == cos)
            map.price = Math.max(-2, CommonStatic.parseIntN(text) - 1);
        else if (jtf == lname)
            ((Limit.PackLimit)jll.getSelectedValue()).name = text;

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
        prog.setLnr(p -> changePanel(new PackSavePage(getFront(), pac, map)));

        addl.setLnr(e -> {
            map.lim.add(new Limit.PackLimit());
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

    public int getPWidth() {
        return (int) (1400 / 7.5);
    }

    public int getPHeight() {
        return 600;
    }
}
