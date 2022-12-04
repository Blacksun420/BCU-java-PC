package page.info;

import common.CommonStatic;
import common.battle.BasisLU;
import common.battle.BasisSet;
import common.battle.data.MaskAtk;
import common.battle.data.MaskEnemy;
import common.battle.data.MaskEntity;
import common.battle.data.MaskUnit;
import common.pack.FixIndexList;
import common.pack.PackData;
import common.pack.SortedPackSet;
import common.pack.UserProfile;
import common.util.Data;
import common.util.unit.EForm;
import common.util.unit.Form;
import common.util.unit.Level;
import common.util.unit.Trait;
import main.MainBCU;
import page.*;
import page.info.filter.EnemyFindPage;
import page.info.filter.TraitList;
import page.info.filter.UnitFindPage;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.*;

public class ComparePage extends Page {

    private static final long serialVersionUID = 1L;

    private final JL[] names = new JL[3];
    private final JTF[] level = new JTF[names.length];
    private final EntityAbilities[] abilities = new EntityAbilities[names.length];
    private final JScrollPane[] abilityPanes = new JScrollPane[names.length];

    private final JL[][] main = new JL[10][names.length + 1]; // stats on both
    private final JL[][] seco = new JL[1][names.length + 1]; // stats after others
    private final JL[][] unit = new JL[2][names.length + 1]; // stats on unit
    private final JL[][] enem = new JL[1][names.length + 1]; // stats on enemy

    private final JCB[] boxes = new JCB[main.length + unit.length + enem.length + seco.length + 1];

    private final JBTN back = new JBTN(0, "back");

    private final JBTN[][] sele = new JBTN[names.length][2];
    private final JBTN[][] swap = new JBTN[names.length][2];

    private final MaskEntity[] maskEntities = new MaskEntity[names.length];
    private final ArrayList<Integer>[] maskEntityLvl = new ArrayList[names.length];

    private final TraitList trait = new TraitList(false);
    private final JScrollPane tlst = new JScrollPane(trait);

    private EnemyFindPage efp = null;
    private UnitFindPage ufp = null;
    private int s = -1;

    private boolean resize = true;

    private final BasisLU b = BasisSet.current().sele;

    public ComparePage(Page p) {
        super(p);

        ini();
        resized();
    }

    private void ini() {
        add(back);
        add(tlst);

        for (int i = 0; i < sele.length; i++) {
            add(sele[i][0] = new JBTN(0, "veif"));
            add(sele[i][1] = new JBTN(0, "vuif"));
        }

        for (int i = 0; i < swap.length; i++) {
            JBTN left = new JBTN(0, "<");
            JBTN right = new JBTN(0, ">");
            left.setEnabled(false);
            right.setEnabled(false);
            add(swap[i][0] = left);
            add(swap[i][1] = right);
        }

        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new JCB();
            boxes[i].setSelected(true);
            add(boxes[i]);
        }

        for (int i = 0; i < names.length; i++) {
            add(names[i] = new JL("-"));
        }

        for (int i = 0; i < level.length; i++) {
            JTF jtf = new JTF("-");
            jtf.setEnabled(false);
            add(level[i] = jtf);
        }

        for (int i = 0; i < abilityPanes.length; i++) {
            JScrollPane p = new JScrollPane();
            p.setEnabled(false);
            add(abilityPanes[i] = p);
        }

        addStatLabels();

        main[0][0].setText(MainLocale.INFO, "HP");
        main[1][0].setText(MainLocale.INFO, "hb");
        main[2][0].setText(MainLocale.INFO, "range");
        main[3][0].setText(MainLocale.INFO, "atk");
        main[4][0].setText("dps");
        main[5][0].setText(MainLocale.INFO, "preaa");
        main[6][0].setText(MainLocale.INFO, "postaa");
        main[7][0].setText(MainLocale.INFO, "atkf");
        main[8][0].setText(MainLocale.INFO, "TBA");
        main[9][0].setText(MainLocale.INFO, "speed");

        boxes[0].setText(MainLocale.INFO, "HP");
        boxes[1].setText(MainLocale.INFO, "hb");
        boxes[2].setText(MainLocale.INFO, "range");
        boxes[3].setText(MainLocale.INFO, "atk");
        boxes[4].setText("dps");
        boxes[5].setText(MainLocale.INFO, "preaa");
        boxes[6].setText(MainLocale.INFO, "postaa");
        boxes[7].setText(MainLocale.INFO, "atkf");
        boxes[8].setText(MainLocale.INFO, "TBA");
        boxes[9].setText(MainLocale.INFO, "speed");

        unit[0][0].setText(MainLocale.INFO, "cdo");
        unit[1][0].setText(MainLocale.INFO, "price");

        boxes[main.length].setText(MainLocale.INFO, "cdo");
        boxes[1 + main.length].setText(MainLocale.INFO, "price");

        enem[0][0].setText(MainLocale.INFO, "drop");

        boxes[main.length + unit.length].setText(MainLocale.INFO, "drop");

        seco[0][0].setText(MainLocale.INFO, "trait");

        boxes[main.length + unit.length + enem.length].setText(MainLocale.INFO, "trait");

        boxes[boxes.length - 1].setText("ability");

        addListeners();
        setTraits();
    }

    private void setTraits() {
        FixIndexList.FixIndexMap<Trait> BCtraits = UserProfile.getBCData().traits;
        for (int i = 0 ; i < BCtraits.size() - 1 ; i++)
            trait.list.add(BCtraits.get(i));

        Collection<PackData.UserPack> pacs = UserProfile.getUserPacks();
        for (PackData.UserPack pack : pacs)
            for (Trait t : pack.traits)
                trait.list.add(t);

        trait.setListData();
        trait.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        trait.addListSelectionListener(x -> reset());
    }

    private void addListeners() {
        back.addActionListener(x -> changePanel(getFront()));

        for (int i = 0; i < sele.length; i++) {
            int finalI = i;
            sele[i][0].addActionListener(x -> {
                changePanel(efp = new EnemyFindPage(getThis()));
                s = finalI;
            });
            sele[i][1].addActionListener(x -> {
                changePanel(ufp = new UnitFindPage(getThis()));
                s = finalI;
            });
        }

        for (int i = 0; i < swap.length; i++) {
            int finalI = i;
            swap[i][0].addActionListener(x -> {
                if (!(maskEntities[finalI] instanceof MaskUnit))
                    return;

                Form oldf = (Form) maskEntities[finalI].getPack();
                Form[] forms = oldf.unit.forms;
                int fid = oldf.fid;
                Form f = (fid - 1) < 0 ? forms[forms.length - 1] : forms[fid - 1];

                int[] data = CommonStatic.parseIntsN(level[finalI].getText());
                maskEntityLvl[finalI] = f.regulateLv(data, maskEntityLvl[finalI]);
                String[] strs = UtilPC.lvText(f, maskEntityLvl[finalI]);
                level[finalI].setText(strs[0]);

                maskEntities[finalI] = f.du;
                reset();
            });
            swap[i][1].addActionListener(x -> {
                if (!(maskEntities[finalI] instanceof MaskUnit))
                    return;

                Form oldf = (Form) maskEntities[finalI].getPack();
                int fid = oldf.fid;
                Form f = oldf.uid.get().getForms()[(fid + 1) % oldf.unit.forms.length];

                int[] data = CommonStatic.parseIntsN(level[finalI].getText());
                maskEntityLvl[finalI] = f.regulateLv(data, maskEntityLvl[finalI]);
                String[] strs = UtilPC.lvText(f, maskEntityLvl[finalI]);
                level[finalI].setText(strs[0]);

                maskEntities[finalI] = f.du;
                reset();
            });
        }

        for (int i = 0; i < level.length; i++) {
            JTF jtf = level[i];
            int finalI = i;
            jtf.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    int[] data = CommonStatic.parseIntsN(jtf.getText().trim().replace("%", ""));

                    if (maskEntities[finalI] instanceof MaskEnemy) {
                        if (data.length == 1) {
                            if (data[0] != -1) {
                                maskEntityLvl[finalI].set(0, data[0]);
                                maskEntityLvl[finalI].set(1, data[0]);
                            }

                            jtf.setText(CommonStatic.toArrayFormat(data[0], data[0]) + "%");
                        } else if (data.length == 2) {
                            if (data[0] != -1)
                                maskEntityLvl[finalI].set(0, data[0]);
                            if (data[1] != -1)
                                maskEntityLvl[finalI].set(1, data[1]);

                            jtf.setText(CommonStatic.toArrayFormat(data[0], data[1]) + "%");
                        } else {
                            jtf.setText(CommonStatic.toArrayFormat(maskEntityLvl[finalI].get(0), maskEntityLvl[finalI].get(1)) + "%");
                        }
                    } else {
                        Form f = ((MaskUnit) maskEntities[finalI]).getPack();
                        maskEntityLvl[finalI] = f.regulateLv(data, maskEntityLvl[finalI]);
                        String[] strs = UtilPC.lvText(f, maskEntityLvl[finalI]);
                        jtf.setText(strs[0]);
                    }


                    reset();
                }
            });
        }

        for (JCB b : boxes)
            b.addActionListener(x -> requireResize());
    }

    private void reset() {
        s = -1;

        for (int i = 0; i < maskEntities.length; i++) {
            MaskEntity m = maskEntities[i];
            int index = i + 1;
            if (m == null) {
                abilityPanes[i].setViewportView(null);
                abilityPanes[i].setEnabled(false);
                names[i].setIcon(null);
                names[i].setText("-");
                level[i].setEnabled(false);
                level[i].setText("-");
                for (JBTN btn : swap[i])
                    btn.setEnabled(false);
                for (JL[] jls : main)
                    jls[index].setText("-");
                for (JL[] jls : unit)
                    jls[index].setText("-");
                for (JL[] jls : enem)
                    jls[index].setText("-");
                for (JL[] jls : seco)
                    jls[index].setText("-");
                continue;
            }

            boolean state = level[i].isEnabled();

            int hp = m.getHp();
            int atk = 0;
            MaskAtk[] atkData = m.getAtks(0);
            StringBuilder atkString = new StringBuilder();
            StringBuilder preString = new StringBuilder();

            if (m instanceof MaskEnemy) {
                MaskEnemy enemy = (MaskEnemy) m;

                if (!state) {
                    maskEntityLvl[i] = new ArrayList<>();
                    maskEntityLvl[i].add(100);
                    maskEntityLvl[i].add(100);
                }
                ArrayList<Integer> multi = maskEntityLvl[i];

                double mul = (multi.get(0) * enemy.multi(b)) / 100.0;
                double mula = (multi.get(1) * enemy.multi(b)) / 100.0;

                abilityPanes[i].setViewportView(abilities[i] = new EntityAbilities(getFront(), m, multi));

                for (MaskAtk atkDatum : atkData) {
                    if (atkString.length() > 0) {
                        atkString.append(" / ");
                        preString.append(" / ");
                    }

                    atkString.append(Math.round(atkDatum.getAtk() * mula));
                    preString.append(MainBCU.convertTime(atkDatum.getPre()));
                }

                main[0][index].setText((int) (hp * mul) + "");
                main[4][index].setText((int) (m.allAtk(0) * mula * 30.0 / m.getItv(0)) + "");

                enem[0][index].setText(Math.floor(enemy.getDrop() * b.t().getDropMulti()) / 100 + "");

                for (JL[] jls : unit)
                    jls[index].setText("-");
                for (JBTN btn : swap[i])
                    btn.setEnabled(false);
            } else if (m instanceof MaskUnit) {
                ArrayList<Integer> multi = state
                        ? maskEntityLvl[i]
                        : (maskEntityLvl[i] = ((MaskUnit) m).getPack().getPrefLvs());
                MaskUnit mu;
                if (((MaskUnit) m).getPCoin() != null) {
                    mu = ((MaskUnit) m).getPCoin().improve(multi);
                    m = mu;
                } else
                    mu = (MaskUnit) m;

                Form f = mu.getPack();
                EForm ef = new EForm(f, multi);

                abilityPanes[i].setViewportView(abilities[i] = new EntityAbilities(getFront(), mu, multi));

                double mul = f.unit.lv.getMult(multi.get(0));
                double atkLv = b.t().getAtkMulti();
                double defLv = b.t().getDefMulti();

                SortedPackSet<Trait> traits = new SortedPackSet<>(trait.getSelectedValuesList());
                SortedPackSet<Trait> spTraits = new SortedPackSet<>(UserProfile.getBCData().traits.getList()
                        .subList(Data.TRAIT_EVA, Data.TRAIT_BEAST + 1));
                spTraits.retainAll(traits);
                traits.retainAll(mu.getTraits());
                boolean overlap = traits.size() > 0;

                int checkHealth = (Data.AB_GOOD | Data.AB_RESIST | Data.AB_RESISTS);
                int checkAttack = (Data.AB_GOOD | Data.AB_MASSIVE | Data.AB_MASSIVES);

                hp = (int) (Math.round(hp * mul) * defLv);
                if (mu.getPCoin() != null)
                    hp = (int) (hp * mu.getPCoin().getHPMultiplication(multi));

                for (MaskAtk atkDatum : atkData) {
                    if (atkString.length() > 0) {
                        atkString.append(" / ");
                        preString.append(" / ");
                    }

                    int a = (int) (Math.round(atkDatum.getAtk() * mul) * atkLv);
                    if (mu.getPCoin() != null)
                        a = (int) (a * mu.getPCoin().getAtkMultiplication(multi));

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
                    if (spTraits.contains(trait.list.get(Data.TRAIT_WITCH)) && (mu.getAbi() & Data.AB_WKILL) > 0)
                        effectiveDMG *= b.t().getWKAtk();
                    if (spTraits.contains(trait.list.get(Data.TRAIT_EVA)) && (mu.getAbi() & Data.AB_EKILL) > 0)
                        effectiveDMG *= b.t().getEKAtk();
                    if (spTraits.contains(trait.list.get(Data.TRAIT_BARON)) && (mu.getAbi() & Data.AB_BAKILL) > 0)
                        effectiveDMG *= 1.6;
                    if (spTraits.contains(trait.list.get(Data.TRAIT_BEAST)) && mu.getProc().BSTHUNT.type.active)
                        effectiveDMG *= 2.5;

                    if (effectiveDMG > a)
                        atkString.append(" (").append(effectiveDMG).append(")");
                }

                int respawn = b.t().getFinRes(mu.getRespawn());
                unit[0][index].setText(MainBCU.convertTime(respawn));

                double price = ef.getPrice(1);
                unit[1][index].setText(price + "");

                for (JL[] jls : enem)
                    jls[index].setText("-");

                int effectiveHP = hp;
                if (overlap && (mu.getAbi() & checkHealth) > 0) {
                    if ((mu.getAbi() & Data.AB_RESISTS) > 0)
                        effectiveHP /= b.t().getRESISTSDEF(traits);
                    if ((mu.getAbi() & Data.AB_RESIST) > 0)
                        effectiveHP /= b.t().getRESISTDEF(traits, traits, null, new Level(multi));
                    if ((mu.getAbi() & Data.AB_GOOD) > 0)
                        effectiveHP /= b.t().getGOODDEF(traits, traits, null, new Level(multi));
                }
                if (spTraits.contains(trait.list.get(Data.TRAIT_WITCH)) && (mu.getAbi() & Data.AB_WKILL) > 0)
                    effectiveHP /= b.t().getWKDef();
                if (spTraits.contains(trait.list.get(Data.TRAIT_EVA)) && (mu.getAbi() & Data.AB_EKILL) > 0)
                    effectiveHP /= b.t().getEKDef();
                if (spTraits.contains(trait.list.get(Data.TRAIT_BARON)) && (mu.getAbi() & Data.AB_BAKILL) > 0)
                    effectiveHP /= 0.7;
                if (spTraits.contains(trait.list.get(Data.TRAIT_BEAST)) && mu.getProc().BSTHUNT.type.active)
                    effectiveHP /= 0.6;
                if (effectiveHP > hp)
                    main[0][index].setText(hp + " (" + effectiveHP + ")");
                else
                    main[0][index].setText(hp + "");

                int effectiveDMG = atk;
                if (overlap && (mu.getAbi() & checkAttack) > 0) {
                    if ((mu.getAbi() & Data.AB_MASSIVES) > 0)
                        effectiveDMG *= b.t().getMASSIVESATK(traits);
                    if ((mu.getAbi() & Data.AB_MASSIVE) > 0)
                        effectiveDMG *= b.t().getMASSIVEATK(traits);
                    if ((mu.getAbi() & Data.AB_GOOD) > 0)
                        effectiveDMG *= b.t().getGOODATK(traits);
                }
                if (spTraits.contains(trait.list.get(Data.TRAIT_WITCH)) && (mu.getAbi() & Data.AB_WKILL) > 0)
                    effectiveDMG *= b.t().getWKAtk();
                if (spTraits.contains(trait.list.get(Data.TRAIT_EVA)) && (mu.getAbi() & Data.AB_EKILL) > 0)
                    effectiveDMG *= b.t().getEKAtk();
                if (spTraits.contains(trait.list.get(Data.TRAIT_BARON)) && (mu.getAbi() & Data.AB_BAKILL) > 0)
                    effectiveDMG *= 1.6;
                if (spTraits.contains(trait.list.get(Data.TRAIT_BEAST)) && mu.getProc().BSTHUNT.type.active)
                    effectiveDMG *= 2.5;

                if (effectiveDMG > atk)
                    main[4][index].setText((int) (atk * 30.0 / m.getItv(0))
                            + " (" + (int) (effectiveDMG * 30.0 / m.getItv(0)) + ")");
                else
                    main[4][index].setText((int) (atk * 30.0 / m.getItv(0)) + "");

                for (JBTN btn : swap[i])
                    btn.setEnabled(true);
            }

            abilityPanes[i].setEnabled(true);
            level[i].setEnabled(true);

            names[i].setIcon(UtilPC.getIcon(m.getPack().anim.getEdi()));
            names[i].setText(m.getPack().toString());

            main[1][index].setText(m.getHb() + "");
            main[2][index].setText(m.getRange() + "");
            main[3][index].setText(atkString.toString());
            main[5][index].setText(preString.toString());
            main[6][index].setText(MainBCU.convertTime(m.getPost(false, 0)));
            main[7][index].setText(MainBCU.convertTime(m.getItv(0)));
            main[8][index].setText(MainBCU.convertTime(m.getTBA()));

            main[9][index].setText(m.getSpeed() + "");

            SortedPackSet<Trait> trs = m.getTraits();
            String[] TraitBox = new String[trs.size()];
            for (int t = 0; t < trs.size(); t++) {
                Trait trait = m.getTraits().get(t);
                if (trait.BCTrait)
                    TraitBox[t] = Interpret.TRAIT[trait.id.id];
                else
                    TraitBox[t] = trait.name;
            }

            seco[0][index].setText(Interpret.getTrait(TraitBox, m instanceof MaskEnemy ? ((MaskEnemy) m).getStar() : 0));
        }

        requireResize();
    }

    @Override
    protected void renew() {
        if (s == -1)
            return;

        MaskEntity ent = null;
        maskEntityLvl[s] = new ArrayList<>();
        maskEntityLvl[s].add(0);

        if (efp != null && efp.getSelected() != null)
            ent = efp.getSelected().de;
        else if (ufp != null && ufp.getForm() != null)
            ent = ufp.getForm().du;

        if (ent instanceof MaskEnemy) {
            int[] data = maskEntities[s] instanceof MaskEnemy
                    ? CommonStatic.parseIntsN(level[s].getText().trim().replace("%", ""))
                    : new int[]{100, 100};
            if (data.length == 1) {
                if (data[0] != -1) {
                    maskEntityLvl[s].set(0, data[0]);
                    maskEntityLvl[s].add(data[0]);
                }

                level[s].setText(CommonStatic.toArrayFormat(data[0], data[0]) + "%");
            } else if (data.length == 2) {
                if (data[0] != -1)
                    maskEntityLvl[s].set(0, data[0]);
                if (data[1] != -1)
                    maskEntityLvl[s].add(data[1]);

                level[s].setText(CommonStatic.toArrayFormat(data[0], data[1]) + "%");
            } else {
                level[s].setText(CommonStatic.toArrayFormat(maskEntityLvl[s].get(0), maskEntityLvl[s].get(1)) + "%");
            }
        } else if (ent != null) {
            Form f = ((MaskUnit) ent).getPack();
            int[] data;

            ArrayList<Integer> lvs = f.getPrefLvs();
            if (maskEntities[s] instanceof MaskUnit) {
                data = CommonStatic.parseIntsN(level[s].getText());
            } else {
                data = new int[lvs.size()];

                for (int i = 0; i < data.length; i++)
                    data[i] = lvs.get(i);
            }
            while (maskEntityLvl[s].size() < data.length)
                maskEntityLvl[s].add(data[maskEntityLvl[s].size()]);
            while (maskEntityLvl[s].size() < lvs.size())
                maskEntityLvl[s].add(lvs.get(maskEntityLvl[s].size()));

            maskEntityLvl[s] = f.regulateLv(data, maskEntityLvl[s]);
            String[] strs = UtilPC.lvText(f, maskEntityLvl[s]);
            level[s].setText(strs[0]);
        }

        maskEntities[s] = ent;
        efp = null;
        ufp = null;
        reset();
    }

    private void addStatLabels() {
        for (int i = 0; i < main.length; i++) {
            for (int j = 0; j < main[i].length; j++) {
                main[i][j] = new JL("-");
                if (j == 0)
                    main[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                else
                    Interpret.setUnderline(main[i][j]);

                add(main[i][j]);
            }
        }

        for (int i = 0; i < unit.length; i++) {
            for (int j = 0; j < unit[i].length; j++) {
                unit[i][j] = new JL("-");
                if (j == 0)
                    unit[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                else
                    Interpret.setUnderline(unit[i][j]);

                add(unit[i][j]);
            }
        }

        for (int i = 0; i < enem.length; i++) {
            for (int j = 0; j < enem[i].length; j++) {
                enem[i][j] = new JL("-");
                if (j == 0)
                    enem[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                else
                    Interpret.setUnderline(enem[i][j]);

                add(enem[i][j]);
            }
        }

        for (int i = 0; i < seco.length; i++) {
            for (int j = 0; j < seco[i].length; j++) {
                seco[i][j] = new JL("-");
                if (j == 0)
                    seco[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                else
                    Interpret.setUnderline(seco[i][j]);

                add(seco[i][j]);
            }
        }
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);

        set(back, x, y, 0, 0, 200, 50);

        int width = 600;

        for (int i = 0; i < sele.length; i++) {
            int p = (width * 3 / 4) + width * i;
            set(sele[i][0], x, y, p, 50, 200, 50);
            set(sele[i][1], x, y, p, 100, 200, 50);
        }

        for (int i = 0; i < swap.length; i++) {
            int p = (width * 3 / 4) + width * i;
            set(swap[i][0], x, y, p - 100, 100, 100, 50);
            set(swap[i][1], x, y, p + 200, 100, 100, 50);
        }

        int posY = 250;

        for (JCB b : boxes) {
            set(b, x, y, 275 + ((main[0].length - 1) * width), posY, 200, 50);
            posY += 50;
        }

        for (int i = 0; i < names.length; i++)
            set(names[i], x, y, 250 + (i * width), 200, width, 50);
        for (int i = 0; i < level.length; i++)
            set(level[i], x, y, 250 + (i * width), 150, width, 50);

        posY = 250;

        for (int i = 0; i < main.length; i++) { // 9
            JL[] d = main[i];
            if (!boxes[i].isSelected()) {
                for (JL ex : d)
                    set(ex, x, y, 0, 0, 0, 0);
                continue;
            }

            int posX = 50;
            for (int j = 0; j < d.length; j++) {
                set(d[j], x, y, posX, posY, j == 0 ? 200 : width, 50);
                if (j == 0)
                    posX += 200;
                else
                    posX += width;
            }
            posY += 50;
        }

        for (int i = 0; i < unit.length; i++) {
            JL[] d = unit[i];
            if (!boxes[i + main.length].isSelected()) {
                for (JL ex : d)
                    set(ex, x, y, 0, 0, 0, 0);
                continue;
            }
            int posX = 50;
            for (int j = 0; j < d.length; j++) {
                set(d[j], x, y, posX, posY, j == 0 ? 200 : width, 50);
                if (j == 0)
                    posX += 200;
                else
                    posX += width;
            }
            posY += 50;
        }

        for (int i = 0; i < enem.length; i++) {
            JL[] d = enem[i];
            if (!boxes[i + main.length + unit.length].isSelected()) {
                for (JL ex : d)
                    set(ex, x, y, 0, 0, 0, 0);
                continue;
            }
            int posX = 50;
            for (int j = 0; j < d.length; j++) {
                set(d[j], x, y, posX, posY, j == 0 ? 200 : width, 50);
                if (j == 0)
                    posX += 200;
                else
                    posX += width;
            }
            posY += 50;
        }

        for (int i = 0; i < seco.length; i++) {
            JL[] d = seco[i];
            if (!boxes[i + main.length + unit.length + enem.length].isSelected()) {
                for (JL ex : d)
                    set(ex, x, y, 0, 0, 0, 0);
                continue;
            }
            int posX = 50;
            for (int j = 0; j < d.length; j++) {
                set(d[j], x, y, posX, posY, j == 0 ? 200 : width, 50);
                if (j == 0)
                    posX += 200;
                else
                    posX += width;
            }
            posY += 50;
        }

        int posX = 250;
        int unselected = ((int) Arrays.stream(boxes).filter(b -> !b.isSelected()).count());
        int height = 200 + unselected * 50;

        for (int i = 0; i < abilityPanes.length; i++) {
            JScrollPane pane = abilityPanes[i];
            if (!boxes[boxes.length - 1].isSelected()) {
                set(pane, x, y, 0, 0, 0, 0);
                continue;
            }

            if (resize) {
                EntityAbilities e = abilities[i];
                if (e != null) {
                    e.setPreferredSize(size(x, y, e.getPWidth(), e.getPHeight()).toDimension());
                    pane.getHorizontalScrollBar().setUnitIncrement(size(x, y, 20));
                    pane.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
                    pane.revalidate();
                }
            }

            set(pane, x, y, posX + i * 600, posY, width, height);
        }

        set(tlst, x, y, 50, posY, 200, height);
        if (resize)
            tlst.revalidate();

        resize = false;
    }

    public void requireResize() {
        resize = true;
    }
}
