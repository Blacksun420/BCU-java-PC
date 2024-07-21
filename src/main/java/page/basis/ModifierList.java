package page.basis;

import common.CommonStatic;
import common.battle.BasisLU;
import common.battle.BasisSet;
import common.pack.Identifier;
import common.pack.SortedPackSet;
import common.util.unit.Combo;
import page.MainLocale;
import utilpc.Interpret;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModifierList extends JList<Object> {
    private BasisSet lineup;
    private SortedPackSet<Combo> combos;
    private Set<Integer> banned;

    private static final long serialVersionUID = 1L;

    static {
        ComboListTable.redefine();
    }

    public ModifierList() {
        super();
        setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
                JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
                if (o instanceof Combo) {
                    Combo c = (Combo) o;
                    if (banned != null && banned.contains(c.type) || (!c.id.pack.equals(Identifier.DEF) && !CommonStatic.getConfig().packCombos.getOrDefault(c.id.pack, false))) {
                        jl.setText("<html><strike>" + Interpret.comboLv[c.lv] + " Combo: " + Interpret.comboInfo(c, lineup) + "</strike></html>");
                        jl.setForeground(getSelectedIndex() == ind ? Color.WHITE : Color.GRAY);
                    } else
                        jl.setText(Interpret.comboLv[c.lv] + " Combo: " + Interpret.comboInfo(c, lineup));
                } else
                    jl.setText(o.toString());
                return jl;
            }
        });

        reset();
    }

    protected void reset() {
        List<Object> list = new ArrayList<>();

        if (lineup != null) {
            BasisLU lu = lineup.sele;
            int[] lvls = lu.nyc;
            if (lvls[1] > 0 && lu.t().deco[lvls[1] - 1] > 0)
                list.add("Lv. " + lu.t().deco[lvls[1] - 1] + " "
                        + MainLocale.getLoc(MainLocale.UTIL, "t" + (lvls[1] + 43)) + ": "
                        + Interpret.deco(lvls[1] - 1, lineup));
            if (lvls[2] > 0 && lu.t().base[lvls[2] - 1] > 0)
                list.add("Lv. " + lu.t().base[lvls[2] - 1] + " "
                        + MainLocale.getLoc(MainLocale.UTIL, "t" + (lvls[2] + 36)) + ": "
                        + Interpret.base(lvls[2] - 1, lineup));
        }

        if (combos != null)
            list.addAll(combos);

        setListData(list.toArray(new Object[0]));
    }

    public void setComboList(SortedPackSet<Combo> lf) {
        combos = lf;
        reset();
    }

    public void setBanned(Set<Integer> lb) {
        banned = lb;
        reset();
    }

    public void setBasis(BasisSet b) {
        lineup = b;
        reset();
    }
}