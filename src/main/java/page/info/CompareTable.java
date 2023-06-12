package page.info;

import common.CommonStatic;
import common.battle.BasisLU;
import common.battle.BasisSet;
import common.battle.data.MaskAtk;
import common.battle.data.MaskEnemy;
import common.battle.data.MaskEntity;
import common.battle.data.MaskUnit;
import common.pack.SortedPackSet;
import common.pack.UserProfile;
import common.system.VImg;
import common.util.Data;
import common.util.unit.*;
import common.util.unit.Character;
import main.MainBCU;
import page.*;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class CompareTable extends Page {
    private static final long serialVersionUID = 1L;

    private final JL names = new JL("-");
    private final JTF level = new JTF("-");
    private EntityAbilities abilities;
    private final JScrollPane abilityPanes = new JScrollPane();

    private final JL[] main = new JL[10]; // stats on both
    private final JL seco = new JL("-"); // stats after others
    private final JL[] unit = new JL[2]; // stats on unit
    private final JL enem = new JL("-"); // stats on enemy
    private final JL[] evol = new JL[6]; // evolve slots

    private final JBTN[] sele = new JBTN[2];
    private final JBTN[] swap = new JBTN[2];

    private final ComparePage par;
    private Character maskEntities;
    private LevelInterface maskEntityLvl;
    private boolean resize = true;

    protected final boolean[] seles = new boolean[main.length + unit.length + 4];
    private SortedPackSet<Trait> trs = new SortedPackSet<>();
    private final int ind;

    private final BasisLU b = BasisSet.current().sele;

    public CompareTable(ComparePage p, int ind, int sellen) {
        super(p);
        par = p;
        this.ind = ind;
        for (int i = 0; i < sellen; i++)
            seles[i] = true;

        ini();
    }

    @Override
    public JButton getBackButton() {
        return null;
    }

    private void ini() {
        add(sele[0] = new JBTN(0, "veif"));
        add(sele[1] = new JBTN(0, "vuif"));

        JBTN left = new JBTN(0, "<");
        left.setEnabled(false);
        add(swap[0] = left);
        JBTN right = new JBTN(0, ">");
        right.setEnabled(false);
        add(swap[1] = right);

        add(names);
        add(level);
        level.setEnabled(false);
        add(abilityPanes);
        abilityPanes.setEnabled(false);

        addStatLabels();
        addListeners();
    }

    public void setTraits(List<Trait> traits) {
        trs = new SortedPackSet<>(traits);
    }

    private void addListeners() {
        sele[0].addActionListener(x -> par.getEnemy(ind));
        sele[1].addActionListener(x -> par.getUnit(ind));

        for (byte i = 0; i < 2 ; i++) {
            byte FI = i;
            swap[i].addActionListener(x -> {
                Form oldf = (Form) maskEntities;
                int fid = oldf.fid;
                Form f = oldf.uid.get().getForms()[(FI == 1 ? fid + 1 : fid - 1) % oldf.unit.forms.length];

                int[] data = CommonStatic.parseIntsN(level.getText());
                maskEntityLvl = f.regulateLv(Level.lvList(f.unit, data, null), (Level) maskEntityLvl);

                String[] strs = UtilPC.lvText(f, (Level) maskEntityLvl);
                level.setText(strs[0]);

                maskEntities = f;
                reset();
            });
        }

        level.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                int[] data = CommonStatic.parseIntsN(level.getText().trim().replace("%", ""));

                if (maskEntities instanceof Enemy) {
                    if (data.length == 1) {
                        if (data[0] > 0)
                            ((Magnification) maskEntityLvl).hp = ((Magnification) maskEntityLvl).atk = data[0];
                    } else if (data.length >= 2) {
                        if (data[0] > 0)
                            ((Magnification) maskEntityLvl).hp = data[0];
                        if (data[1] > 0)
                            ((Magnification) maskEntityLvl).atk = data[1];
                    }
                    level.setText(CommonStatic.toArrayFormat(((Magnification) maskEntityLvl).hp, ((Magnification) maskEntityLvl).atk) + "%");
                } else {
                    Form f = (Form) maskEntities;
                    maskEntityLvl = f.regulateLv(Level.lvList(f.unit, data, null), (Level) maskEntityLvl);
                    String[] strs = UtilPC.lvText(f, (Level) maskEntityLvl);
                    level.setText(strs[0]);
                }
                reset();
            }
        });
    }

    protected void reset() {
        if (maskEntities == null) {
            abilityPanes.setViewportView(null);
            abilityPanes.setEnabled(false);

            names.setIcon(null);
            names.setText("-");

            level.setEnabled(false);
            level.setText("-");

            for (JBTN btn : swap)
                btn.setEnabled(false);
            for (JL jls : main)
                jls.setText("-");
            for (JL jls : unit)
                jls.setText("-");
            enem.setText("-");
            seco.setText("-");

            for (JL ev : evol) {
                ev.setText("-");
                ev.setIcon(null);
                ev.setToolTipText(null);
            }
            return;
        }
        boolean state = level.isEnabled();
        MaskEntity me = maskEntities.getMask();
        boolean isEnemy = maskEntities instanceof Enemy;
        int hp = me.getHp();
        int atk = 0, eatk = 0;
        double mul, mula = 1;

        MaskAtk[] atkData = maskEntities.getMask().getAtks(0);
        StringBuilder atkString = new StringBuilder();
        StringBuilder preString = new StringBuilder();
        List<Trait> DefTraits = UserProfile.getBCData().traits.getList();
        SortedPackSet<Trait> traits = new SortedPackSet<>(trs), spTraits = new SortedPackSet<>();
        traits.retainAll(me.getTraits());

        if (isEnemy) {
            MaskEnemy enemy = (MaskEnemy)me;
            if (!state)
                maskEntityLvl = new Magnification(100, 100);
            hp *= (((Magnification) maskEntityLvl).hp * enemy.multi(b)) / 100.0;
            mul = (((Magnification) maskEntityLvl).atk * enemy.multi(b)) / 100.0;
            enem.setText(Math.floor(enemy.getDrop() * b.t().getDropMulti()) / 100 + "");
            for (JL jls : unit)
                jls.setText("-");
            for (JL jls : evol) {
                jls.setText("-");
                jls.setIcon(null);
                jls.setToolTipText(null);
            }
            for (JBTN btn : swap)
                btn.setEnabled(false);
        } else {
            Level multi = (Level)(state ? maskEntityLvl : (maskEntityLvl = ((MaskUnit) me).getPack().unit.getPrefLvs()));
            MaskUnit mu = (((MaskUnit) me).getPCoin() != null) ? ((MaskUnit) me).getPCoin().improve(multi.getTalents()) : (MaskUnit) me;
            me = mu;
            Form f = (Form)maskEntities;

            abilityPanes.setViewportView(abilities = new EntityAbilities(getFront(), mu, multi));
            mul = f.unit.lv.getMult(multi.getTotalLv());
            mula = b.t().getAtkMulti();
            hp = (int)(Math.round(hp * mul) * b.t().getDefMulti());
            if (mu.getPCoin() != null)
                hp = (int) (hp * mu.getPCoin().getStatMultiplication(Data.PC2_HP, multi.getTalents()));
            spTraits = new SortedPackSet<>(UserProfile.getBCData().traits.getList().subList(Data.TRAIT_EVA, Data.TRAIT_BEAST + 1));
            spTraits.retainAll(traits);

            EForm ef = new EForm(f, multi);
            unit[0].setText(MainBCU.convertTime(b.t().getFinRes(mu.getRespawn())));
            unit[1].setText(ef.getPrice(1) + "");
            if (f.hasEvolveCost()) {
                int[][] evo = f.unit.info.evo;
                int count = 0;
                for (int j = 0; j < evo.length; j++) {
                    int id = evo[j][0];
                    if (id == 0)
                        break;
                    JL up = evol[j];
                    VImg img = CommonStatic.getBCAssets().gatyaitem.get(id);
                    up.setIcon(img != null ? UtilPC.getScaledIcon(img, 50, 50) : null);
                    up.setText(evo[j][1] + " " + get(MainLocale.UTIL, "cf" + id + "s"));
                    up.setToolTipText(evo[j][1] + " " + get(MainLocale.UTIL, "cf" + id));
                    count++;
                }
                JL xp = evol[count];
                xp.setIcon(UtilPC.getScaledIcon(CommonStatic.getBCAssets().XP, 50, 30));
                xp.setText(f.unit.info.xp + "");
                xp.setToolTipText(f.unit.info.xp + " XP");
                for (int j = count + 1; j < 6; j++) {
                    evol[j].setText("-");
                    evol[j].setIcon(null);
                    evol[j].setToolTipText(null);
                }
            } else
                for (JL ev : evol) {
                    ev.setText("-");
                    ev.setIcon(null);
                    ev.setToolTipText(null);
                }
            enem.setText("-");
        }
        abilityPanes.setViewportView(abilities = new EntityAbilities(getFront(), me, maskEntityLvl));
        int checkHealth = (Data.AB_GOOD | Data.AB_RESIST | Data.AB_RESISTS);
        int checkAttack = (Data.AB_GOOD | Data.AB_MASSIVE | Data.AB_MASSIVES);

        for (MaskAtk atkDatum : atkData) {
            if (atkString.length() > 0) {
                atkString.append(" / ");
                preString.append(" / ");
            }
            int att = (int)(Math.round(atkDatum.getAtk() * mul) * mula);
            if (!isEnemy && ((MaskUnit)me).getPCoin() != null)
                att = (int) (att * ((MaskUnit)me).getPCoin().getStatMultiplication(Data.PC2_ATK, ((Level)maskEntityLvl).getTalents()));
            atkString.append(att);
            preString.append(MainBCU.convertTime(atkDatum.getPre()));

            int effectiveDMG = att;
            if (traits.size() > 0 && (me.getAbi() & checkAttack) > 0) {
                if ((me.getAbi() & Data.AB_MASSIVES) > 0)
                    effectiveDMG *= isEnemy ? 5 : b.t().getMASSIVESATK(traits);
                if ((me.getAbi() & Data.AB_MASSIVE) > 0)
                    effectiveDMG *= isEnemy ? 3 : b.t().getMASSIVEATK(traits);
                if ((me.getAbi() & Data.AB_GOOD) > 0)
                    effectiveDMG *= isEnemy ? 1.5 : b.t().getGOODATK(traits);
            }
            if (spTraits.contains(DefTraits.get(Data.TRAIT_WITCH)) && (me.getAbi() & Data.AB_WKILL) > 0)
                effectiveDMG *= b.t().getWKAtk();
            if (spTraits.contains(DefTraits.get(Data.TRAIT_EVA)) && (me.getAbi() & Data.AB_EKILL) > 0)
                effectiveDMG *= b.t().getEKAtk();
            if (spTraits.contains(DefTraits.get(Data.TRAIT_BARON)) && (me.getAbi() & Data.AB_BAKILL) > 0)
                effectiveDMG *= 1.6;
            if (spTraits.contains(DefTraits.get(Data.TRAIT_BEAST)) && me.getProc().BSTHUNT.type.active)
                effectiveDMG *= 2.5;
            if (effectiveDMG > att)
                atkString.append(" (").append(effectiveDMG).append(")");
            atk += att;
            eatk += effectiveDMG;
        }
        int effectiveHP = hp;
        if (traits.size() > 0 && (me.getAbi() & checkHealth) > 0) {
            if ((me.getAbi() & Data.AB_RESISTS) > 0)
                effectiveHP /= isEnemy ? 1f/6 : b.t().getRESISTSDEF(traits);
            if ((me.getAbi() & Data.AB_RESIST) > 0)
                effectiveHP /= isEnemy ? 0.25f : b.t().getRESISTDEF(traits, traits, null, (Level)maskEntityLvl);
            if ((me.getAbi() & Data.AB_GOOD) > 0)
                effectiveHP /= isEnemy ? 0.5f : b.t().getGOODDEF(traits, traits, null, (Level)maskEntityLvl);
        }
        if (spTraits.contains(DefTraits.get(Data.TRAIT_WITCH)) && (me.getAbi() & Data.AB_WKILL) > 0)
            effectiveHP /= b.t().getWKDef();
        if (spTraits.contains(DefTraits.get(Data.TRAIT_EVA)) && (me.getAbi() & Data.AB_EKILL) > 0)
            effectiveHP /= b.t().getEKDef();
        if (spTraits.contains(DefTraits.get(Data.TRAIT_BARON)) && (me.getAbi() & Data.AB_BAKILL) > 0)
            effectiveHP /= 0.7;
        if (spTraits.contains(DefTraits.get(Data.TRAIT_BEAST)) && me.getProc().BSTHUNT.type.active)
            effectiveHP /= 0.6;

        if (effectiveHP > hp)
            main[0].setText(hp + " (" + effectiveHP + ")");
        else
            main[0].setText(hp + "");

        int DPS = (int)(atk * 30.0 / me.getItv(0));
        int effectiveDPS = (int)(eatk * 30.0 / me.getItv(0));
        main[4].setText(DPS + (effectiveDPS > DPS ? " (" + effectiveDPS + ")" : ""));

        abilityPanes.setEnabled(true);
        level.setEnabled(true);
        names.setIcon(UtilPC.getIcon(maskEntities.getIcon()));
        names.setText(maskEntities.toString());

        main[1].setText(me.getHb() + "");
        main[2].setText(me.getRange() + "");
        main[3].setText(atkString.toString());
        main[5].setText(preString.toString());
        main[6].setText(MainBCU.convertTime(me.getPost(false, 0)));
        main[7].setText(MainBCU.convertTime(me.getItv(0)));
        main[8].setText(MainBCU.convertTime(me.getTBA()));
        main[9].setText(me.getSpeed() + "");

        SortedPackSet<Trait> trs = me.getTraits();
        String[] TraitBox = new String[trs.size()];
        for (int t = 0; t < trs.size(); t++) {
            Trait trait = trs.get(t);
            if (trait.BCTrait())
                TraitBox[t] = Interpret.TRAIT[trait.id.id];
            else
                TraitBox[t] = trait.name;
        }
        seco.setText(Interpret.getTrait(TraitBox, maskEntities instanceof MaskEnemy ? ((MaskEnemy) maskEntities).getStar() : 0));

        requireResize();
    }

    protected void renewEnemy(Enemy ene) {
        maskEntities = ene;
        if (!(maskEntityLvl instanceof Magnification))
            maskEntityLvl = new Magnification(100, 100);
        level.setText(CommonStatic.toArrayFormat(((Magnification) maskEntityLvl).hp, ((Magnification) maskEntityLvl).atk) + "%");
    }

    protected void renewUnit(Form f) {
        maskEntities = f;
        if (!(maskEntityLvl instanceof Level)) {
            Level lvs = f.unit.getPrefLvs();
            maskEntityLvl = lvs.clone();
        } else
            maskEntityLvl = f.regulateLv(Level.lvList(f.unit, CommonStatic.parseIntsN(level.getText()), null), (Level)maskEntityLvl);
        level.setText(UtilPC.lvText(f, (Level) maskEntityLvl)[0]);
    }

    private void addStatLabels() {
        for (int i = 0; i < main.length; i++) {
            main[i] = new JL("-");
            Interpret.setUnderline(main[i]);
            add(main[i]);
        }
        for (int i = 0; i < unit.length; i++) {
            unit[i] = new JL("-");
            Interpret.setUnderline(unit[i]);
            add(unit[i]);
        }
        Interpret.setUnderline(enem);
        add(enem);
        Interpret.setUnderline(seco);
        add(seco);
        for (int i = 0; i < evol.length; i++) {
            evol[i] = new JL("-");
            Interpret.setUnderline(evol[i]);
            add(evol[i]);
        }
    }

    @Override
    protected void resized(int x, int y) {
        int width = par.getTableWidth();
        int p = width / 3;
        set(sele[0], x, y, p, 0, 200, 50);
        set(sele[1], x, y, p, 50, 200, 50);
        set(swap[0], x, y, p - 100, 50, 100, 50);
        set(swap[1], x, y, p + 200, 50, 100, 50);
        set(names, x, y, 0, 150, width, 50);
        set(level, x, y, 0, 100, width, 50);
        int posY = 200;

        for (int i = 0; i < main.length; i++) {
            if (!seles[i]) {
                set(main[i], x, y, 0, 0, 0, 0);
                continue;
            }
            set(main[i], x, y, 0, posY, width, 50);
            posY += 50;
        }
        for (int i = 0; i < unit.length; i++) {
            if (!seles[i + main.length]) {
                set(unit[i], x, y, 0, 0, 0, 0);
                continue;
            }
            set(unit[i], x, y, 0, posY, width, 50);
            posY += 50;
        }
        if (!seles[main.length + unit.length])
            set(enem, x, y, 0, 0, 0, 0);
        else {
            set(enem, x, y, 0, posY, width, 50);
            posY += 50;
        }
        for (int i = 0; i < evol.length; i++) {
            if (!seles[main.length + unit.length + 1]) {
                set(evol[i], x, y, 0, 0, 0, 0);
                continue;
            }
            JL jl = evol[i];
            int localPosX = (i % 3 * p) + (i / 6 * width);
            set(jl, x, y, localPosX, posY, width / 3, 50);
            if ((i + 1) % 3 == 0)
                posY += 50;
        }
        if (!seles[main.length + unit.length + 2])
            set(seco, x, y, 0, 0, 0, 0);
        else {
            set(seco, x, y, 0, posY, width, 50);
            posY += 50;
        }

        int unselected = !seles[main.length + unit.length + 1] ? 1 : 0;
        for (boolean s : seles)
            if (!s)
                unselected++;
        int height = 200 + unselected * 50;

        if (!seles[seles.length - 1])
            set(abilityPanes, x, y, 0, 0, 0, 0);
        else {
            if (resize && abilities != null) {
                abilities.setPreferredSize(size(x, y, abilities.getPWidth(), abilities.getPHeight()).toDimension());
                abilityPanes.getHorizontalScrollBar().setUnitIncrement(size(x, y, 20));
                abilityPanes.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
                abilityPanes.revalidate();
            }
            set(abilityPanes, x, y, 0, posY, width, height);
        }

        resize = false;
    }

    public void requireResize() {
        resize = true;
    }
}