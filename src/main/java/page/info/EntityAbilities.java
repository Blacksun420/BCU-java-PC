package page.info;

import common.battle.BasisSet;
import common.battle.data.MaskEnemy;
import common.battle.data.MaskEntity;
import common.battle.data.MaskUnit;
import page.Page;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class EntityAbilities extends Page {

    private static final long serialVersionUID = 1L;

    private final MaskEntity me;
    private final ArrayList<Integer> lvl;
    private JLabel[] proc;
    private JLabel pcoin;

    public EntityAbilities(Page p, MaskEntity me, ArrayList<Integer> lv) {
        super(p);
        this.me = me;
        lvl = lv;

        ini();
        resized();
    }

    private void ini() {
        boolean isEnemy = me instanceof MaskEnemy;
        List<Interpret.ProcDisplay> ls = Interpret.getAbi(me, 0);
        ls.addAll(Interpret.getProc(me, isEnemy, lvl.stream().mapToDouble(x -> {
            if (isEnemy)
                return x * ((MaskEnemy) me).multi(BasisSet.current()) / 100;
            else
                return x;
        }).toArray(), 0));
        proc = new JLabel[ls.size()];
        for (int i = 0; i < ls.size(); i++) {
            Interpret.ProcDisplay disp = ls.get(i);
            add(proc[i] = new JLabel(disp.toString()));
            proc[i].setBorder(BorderFactory.createEtchedBorder());
            proc[i].setIcon(disp.getIcon());
            Interpret.setUnderline(proc[i]);
        }

        if (!isEnemy && ((MaskUnit) me).getPCoin() != null) {
            String[] strs = UtilPC.lvText(((MaskUnit) me).getPack(), lvl);
            add(pcoin = new JLabel(strs[1]));

            pcoin.setBorder(BorderFactory.createEtchedBorder());
        }
        for (JLabel jl : proc) add(jl);
    }

    @Override
    public JButton getBackButton() {
        return null;
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);

        int posY = 0;
        for (JLabel jLabel : proc) {
            set(jLabel, x, y, 0, posY, 1200, 50);
            posY += 50;
        }

        if (pcoin != null)
            set(pcoin, x, y, 0, posY, 750, 50);
    }

    public int getPWidth() {
        return (proc.length + (pcoin != null ? 1 : 0)) > 0 ? 1200 : 0;
    }

    public int getPHeight() {
        return (proc.length + (pcoin != null ? 1 : 0)) * 50;
    }
}
