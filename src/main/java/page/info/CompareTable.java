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
    protected MaskEntity maskEntities;
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
                Form oldf = (Form) maskEntities.getPack();
                int fid = oldf.fid;
                Form f = oldf.uid.get().getForms()[(FI == 1 ? fid + 1 : fid - 1) % oldf.unit.forms.length];

                int[] data = CommonStatic.parseIntsN(level.getText());
                maskEntityLvl = f.regulateLv(Level.lvList(f.unit, data, null), (Level) maskEntityLvl);

                String[] strs = UtilPC.lvText(f, (Level) maskEntityLvl);
                level.setText(strs[0]);

                maskEntities = f.du;
                reset();
            });
        }

        level.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                int[] data = CommonStatic.parseIntsN(level.getText().trim().replace("%", ""));

                if (maskEntities instanceof MaskEnemy) {
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
                    Form f = ((MaskUnit) maskEntities).getPack();
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
            int hp = maskEntities.getHp();
            int atk = 0;

            MaskAtk[] atkData = maskEntities.getAtks(0);
            StringBuilder atkString = new StringBuilder();
            StringBuilder preString = new StringBuilder();
            SortedPackSet<Trait> traits = new SortedPackSet<>(trs);

            if (maskEntities instanceof MaskEnemy) {
                MaskEnemy enemy = (MaskEnemy) maskEntities;
                if (!state)
                    maskEntityLvl = new Magnification(100, 100);

                LevelInterface multi = maskEntityLvl;
                if(!(multi instanceof Magnification))
                    return;
                double mul = (((Magnification) multi).hp * enemy.multi(b)) / 100.0;
                double mula = (((Magnification) multi).atk * enemy.multi(b)) / 100.0;

                abilityPanes.setViewportView(abilities = new EntityAbilities(getFront(), enemy, multi));
                int checkHealth = (Data.AB_GOOD | Data.AB_RESIST | Data.AB_RESISTS);
                int checkAttack = (Data.AB_GOOD | Data.AB_MASSIVE | Data.AB_MASSIVES);

                for (MaskAtk atkDatum : atkData) {
                    if (atkString.length() > 0) {
                        atkString.append(" / ");
                        preString.append(" / ");
                    }
                    int att = (int)Math.round(atkDatum.getAtk() * mula);
                    atkString.append(att);
                    preString.append(MainBCU.convertTime(atkDatum.getPre()));
                    traits.retainAll(enemy.getTraits());

                    if (traits.size() > 0 && (enemy.getAbi() & checkAttack) > 0) {
                        int effectiveDMG = att;
                        if ((enemy.getAbi() & Data.AB_MASSIVES) > 0)
                            effectiveDMG *= 5;
                        if ((enemy.getAbi() & Data.AB_MASSIVE) > 0)
                            effectiveDMG *= 3;
                        if ((enemy.getAbi() & Data.AB_GOOD) > 0)
                            effectiveDMG *= 1.5;

                        if (effectiveDMG > att)
                            atkString.append(" (").append(effectiveDMG).append(")");
                    }
                }
                hp *= mul;
                if (traits.size() > 0 && (enemy.getAbi() & checkHealth) > 0) {
                    int effectiveHP = hp;
                    if ((enemy.getAbi() & Data.AB_RESISTS) > 0)
                        effectiveHP *= 6;
                    if ((enemy.getAbi() & Data.AB_RESIST) > 0)
                        effectiveHP *= 4;
                    if ((enemy.getAbi() & Data.AB_GOOD) > 0)
                        effectiveHP *= 2;

                    if (effectiveHP > hp)
                        main[0].setText(hp + " (" + effectiveHP + ")");
                    else
                        main[0].setText(hp + "");
                } else
                    main[0].setText(hp + "");

                if (traits.size() > 0 && (enemy.getAbi() & checkAttack) > 0) {
                    int DPS = (int)(enemy.allAtk(0) * mula * 30.0 / enemy.getItv(0));
                    int effectiveDPS = DPS;
                    if ((enemy.getAbi() & Data.AB_MASSIVES) > 0)
                        effectiveDPS *= 5;
                    if ((enemy.getAbi() & Data.AB_MASSIVE) > 0)
                        effectiveDPS *= 3;
                    if ((enemy.getAbi() & Data.AB_GOOD) > 0)
                        effectiveDPS *= 1.5;
                    main[4].setText(DPS + (effectiveDPS > DPS ? " (" + effectiveDPS + ")" : ""));
                } else
                    main[4].setText((int) (enemy.allAtk(0) * mula * 30.0 / enemy.getItv(0)) + "");
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
            } else if (maskEntities instanceof MaskUnit) {
                Level multi = (Level) (state ? maskEntityLvl : (maskEntityLvl = ((MaskUnit) maskEntities).getPack().unit.getPrefLvs()));
                MaskUnit mu;
                if (((MaskUnit) maskEntities).getPCoin() != null) {
                    mu = ((MaskUnit) maskEntities).getPCoin().improve(multi.getTalents());
                    maskEntities = mu;
                } else
                    mu = (MaskUnit) maskEntities;
                Form f = mu.getPack();
                EForm ef = new EForm(f, multi);

                abilityPanes.setViewportView(abilities = new EntityAbilities(getFront(), mu, multi));
                double mul = f.unit.lv.getMult(multi.getTotalLv());
                double atkLv = b.t().getAtkMulti();
                double defLv = b.t().getDefMulti();

                traits.retainAll(mu.getTraits());
                List<Trait> trait = UserProfile.getBCData().traits.getList();
                SortedPackSet<Trait> spTraits = new SortedPackSet<>(UserProfile.getBCData().traits.getList()
                        .subList(Data.TRAIT_EVA, Data.TRAIT_BEAST + 1));
                spTraits.retainAll(traits);
                boolean overlap = traits.size() > 0;

                int checkHealth = (Data.AB_GOOD | Data.AB_RESIST | Data.AB_RESISTS);
                int checkAttack = (Data.AB_GOOD | Data.AB_MASSIVE | Data.AB_MASSIVES);
                hp = (int) (Math.round(hp * mul) * defLv);
                if (mu.getPCoin() != null)
                    hp = (int) (hp * mu.getPCoin().getStatMultiplication(Data.PC2_HP, multi.getTalents()));

                for (MaskAtk atkDatum : atkData) {
                    if (atkString.length() > 0) {
                        atkString.append(" / ");
                        preString.append(" / ");
                    }
                    int a = (int) (Math.round(atkDatum.getAtk() * mul) * atkLv);
                    if (mu.getPCoin() != null)
                        a = (int) (a * mu.getPCoin().getStatMultiplication(Data.PC2_ATK, multi.getTalents()));

                    atkString.append(a);
                    preString.append(MainBCU.convertTime(atkDatum.getPre()));
                    atk += a;

                    int effectiveDMG = a;
                    if (overlap && (mu.getAbi() & checkAttack) > 0) {
                        if ((mu.getAbi() & Data.AB_MASSIVES) > 0)
                            effectiveDMG *= b.t().getMASSIVESATK(traits);
                        if ((mu.getAbi() & Data.AB_MASSIVE) > 0)
                            effectiveDMG *= b.t().getMASSIVEATK(traits);
                        if ((mu.getAbi() & Data.AB_GOOD) > 0)
                            effectiveDMG *= b.t().getGOODATK(traits);
                    }
                    if (spTraits.contains(trait.get(Data.TRAIT_WITCH)) && (mu.getAbi() & Data.AB_WKILL) > 0)
                        effectiveDMG *= b.t().getWKAtk();
                    if (spTraits.contains(trait.get(Data.TRAIT_EVA)) && (mu.getAbi() & Data.AB_EKILL) > 0)
                        effectiveDMG *= b.t().getEKAtk();
                    if (spTraits.contains(trait.get(Data.TRAIT_BARON)) && (mu.getAbi() & Data.AB_BAKILL) > 0)
                        effectiveDMG *= 1.6;
                    if (spTraits.contains(trait.get(Data.TRAIT_BEAST)) && mu.getProc().BSTHUNT.type.active)
                        effectiveDMG *= 2.5;

                    if (effectiveDMG > a)
                        atkString.append(" (").append(effectiveDMG).append(")");
                }
                int respawn = b.t().getFinRes(mu.getRespawn());
                unit[0].setText(MainBCU.convertTime(respawn));

                double price = ef.getPrice(1);
                unit[1].setText(price + "");

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
                } else {
                    for (JL ev : evol) {
                        ev.setText("-");
                        ev.setIcon(null);
                        ev.setToolTipText(null);
                    }
                }
                enem.setText("-");

                int effectiveHP = hp;
                if (overlap && (mu.getAbi() & checkHealth) > 0) {
                    if ((mu.getAbi() & Data.AB_RESISTS) > 0)
                        effectiveHP /= b.t().getRESISTSDEF(traits);
                    if ((mu.getAbi() & Data.AB_RESIST) > 0)
                        effectiveHP /= b.t().getRESISTDEF(traits, traits, null, multi.clone());
                    if ((mu.getAbi() & Data.AB_GOOD) > 0)
                        effectiveHP /= b.t().getGOODDEF(traits, traits, null, multi.clone());
                }
                if (spTraits.contains(trait.get(Data.TRAIT_WITCH)) && (mu.getAbi() & Data.AB_WKILL) > 0)
                    effectiveHP /= b.t().getWKDef();
                if (spTraits.contains(trait.get(Data.TRAIT_EVA)) && (mu.getAbi() & Data.AB_EKILL) > 0)
                    effectiveHP /= b.t().getEKDef();
                if (spTraits.contains(trait.get(Data.TRAIT_BARON)) && (mu.getAbi() & Data.AB_BAKILL) > 0)
                    effectiveHP /= 0.7;
                if (spTraits.contains(trait.get(Data.TRAIT_BEAST)) && mu.getProc().BSTHUNT.type.active)
                    effectiveHP /= 0.6;
                if (effectiveHP > hp)
                    main[0].setText(hp + " (" + effectiveHP + ")");
                else
                    main[0].setText(hp + "");

                int effectiveDMG = atk;
                if (overlap && (mu.getAbi() & checkAttack) > 0) {
                    if ((mu.getAbi() & Data.AB_MASSIVES) > 0)
                        effectiveDMG *= b.t().getMASSIVESATK(traits);
                    if ((mu.getAbi() & Data.AB_MASSIVE) > 0)
                        effectiveDMG *= b.t().getMASSIVEATK(traits);
                    if ((mu.getAbi() & Data.AB_GOOD) > 0)
                        effectiveDMG *= b.t().getGOODATK(traits);
                }
                if (spTraits.contains(trait.get(Data.TRAIT_WITCH)) && (mu.getAbi() & Data.AB_WKILL) > 0)
                    effectiveDMG *= b.t().getWKAtk();
                if (spTraits.contains(trait.get(Data.TRAIT_EVA)) && (mu.getAbi() & Data.AB_EKILL) > 0)
                    effectiveDMG *= b.t().getEKAtk();
                if (spTraits.contains(trait.get(Data.TRAIT_BARON)) && (mu.getAbi() & Data.AB_BAKILL) > 0)
                    effectiveDMG *= 1.6;
                if (spTraits.contains(trait.get(Data.TRAIT_BEAST)) && mu.getProc().BSTHUNT.type.active)
                    effectiveDMG *= 2.5;

                if (effectiveDMG > atk)
                    main[4].setText((int) (atk * 30.0 / mu.getItv(0))
                            + " (" + (int) (effectiveDMG * 30.0 / mu.getItv(0)) + ")");
                else
                    main[4].setText((int) (atk * 30.0 / mu.getItv(0)) + "");

                for (JBTN btn : swap)
                    btn.setEnabled(true);
            }

            abilityPanes.setEnabled(true);
            level.setEnabled(true);

            names.setIcon(UtilPC.getIcon(maskEntities.getPack().anim.getEdi()));
            names.setText(maskEntities.getPack().toString());

            main[1].setText(maskEntities.getHb() + "");
            main[2].setText(maskEntities.getRange() + "");
            main[3].setText(atkString.toString());
            main[5].setText(preString.toString());
            main[6].setText(MainBCU.convertTime(maskEntities.getPost(false, 0)));
            main[7].setText(MainBCU.convertTime(maskEntities.getItv(0)));
            main[8].setText(MainBCU.convertTime(maskEntities.getTBA()));

            main[9].setText(maskEntities.getSpeed() + "");

            SortedPackSet<Trait> trs = maskEntities.getTraits();
            String[] TraitBox = new String[trs.size()];
            for (int t = 0; t < trs.size(); t++) {
                Trait trait = maskEntities.getTraits().get(t);
                if (trait.BCTrait())
                    TraitBox[t] = Interpret.TRAIT[trait.id.id];
                else
                    TraitBox[t] = trait.name;
            }

            seco.setText(Interpret.getTrait(TraitBox, maskEntities instanceof MaskEnemy ? ((MaskEnemy) maskEntities).getStar() : 0));

        requireResize();
    }

    protected void renewEnemy(Enemy ene) {
        maskEntities = ene.de;
        if (!(maskEntityLvl instanceof Magnification))
            maskEntityLvl = new Magnification(100, 100);
        level.setText(CommonStatic.toArrayFormat(((Magnification) maskEntityLvl).hp, ((Magnification) maskEntityLvl).atk) + "%");
    }

    protected void renewUnit(Form f) {
        maskEntities = f.du;
        if (!(maskEntityLvl instanceof Level)) {
            Level lvs = f.unit.getPrefLvs();
            maskEntityLvl = lvs.clone();
        } else {
            maskEntityLvl = f.regulateLv(Level.lvList(f.unit, CommonStatic.parseIntsN(level.getText()), null), (Level)maskEntityLvl);
        }
        level.setText(UtilPC.lvText(f, (Level) maskEntityLvl)[0]);
        reset();
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