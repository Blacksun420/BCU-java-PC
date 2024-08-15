package page.info;

import common.CommonStatic;
import common.battle.BasisSet;
import common.battle.data.AtkDataModel;
import common.battle.data.MaskAtk;
import common.pack.Identifier;
import common.pack.SortedPackSet;
import common.util.Data;
import common.util.unit.Character;
import common.util.unit.Trait;
import main.MainBCU;
import page.*;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class CharacterInfoTable extends Page {

    private static final long serialVersionUID = 1L;

    protected final JL[] inis = new JL[3];
    protected final JL[][] main = new JL[3][8];
    protected final JL[] special;
    protected JL[][] atks;
    protected JLabel[] proc;
    protected final JTF jtf = new JTF();
    protected final HTMLTextField descr = new HTMLTextField();
    protected final JScrollPane desc = new JScrollPane(descr);

    private final JL atkind = new JL();
    private final JBTN prevatk = new JBTN(MainLocale.PAGE, "prev");
    private final JBTN nextatk = new JBTN(MainLocale.PAGE, "next");

    protected final BasisSet b = BasisSet.current();
    private final Character c;
    protected boolean displaySpecial;
    protected int dispAtk;
    protected final boolean multiAtk, isBC;
    protected final ArrayList<AtkDataModel> atkList = new ArrayList<>();

    protected CharacterInfoTable(Page p, Character ch, int sptot) {
        super(p);
        special = new JL[sptot];

        c = ch;
        for (AtkDataModel[] atks : ch.getMask().getSpAtks(true))
            atkList.addAll(Arrays.asList(atks));
        dispAtk = ch.getMask().firstAtk();
        multiAtk = getNext() != -1;
        isBC = c.getID().pack.equals(Identifier.DEF);
        displaySpecial = !isBC;
    }

    protected CharacterInfoTable(Page p, Character ch, int sptot, boolean sp) {
        this(p, ch, sptot);
        displaySpecial = sp;
    }

    protected void ini() {
        for (int j = 0; j < inis.length; j++) {
            add(inis[j] = new JL());
            inis[j].setBorder(BorderFactory.createEtchedBorder());
            if (j % 2 == 1)
                inis[j].setHorizontalAlignment(SwingConstants.CENTER);
        }
        if (c.anim.getEdi() != null && c.anim.getEdi().getImg() != null)
            inis[0].setIcon(UtilPC.getIcon(c.anim.getEdi()));
        inis[1].setText(MainLocale.INFO, "trait");

        main[2] = new JL[6];
        for (int i = 0; i < main.length; i++)
            for (int j = 0; j < main[i].length; j++)
                if (i + j != 1 || j == 0) {
                    add(main[i][j] = new JL());
                    main[i][j].setBorder(BorderFactory.createEtchedBorder());
                    if (j % 2 == 0)
                        main[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                }
        main[1][0].setText(MainLocale.INFO, "range");
        main[1][2].setText(MainLocale.INFO, "us3");
        main[1][4].setText(MainLocale.INFO, "speed");
        main[1][6].setText(MainLocale.INFO, "atkf");
        main[2][0].setText(MainLocale.INFO, "TBA");
        main[2][2].setText(MainLocale.INFO, "postaa");
        main[2][4].setText(MainLocale.INFO, "minpos");

        for (int i = 0; i < special.length; i++) {
            add(special[i] = new JL());
            special[i].setBorder(BorderFactory.createEtchedBorder());
            if (i % 2 == 0)
                special[i].setHorizontalAlignment(SwingConstants.CENTER);
        }
        special[0].setText(MainLocale.INFO, "will");
        special[1].setText((c.getMask().getWill() + 1) + "");
        special[2].setText(MainLocale.INFO, "count");
        special[3].setText(c.getMask().getAtkLoop() < 0 ? get(MainLocale.UTIL, "inf") : c.getMask().getAtkLoop() + "");
        special[4].setText(MainLocale.INFO, "width");
        special[5].setText(c.getMask().getWidth() + "");
        special[6].setText(MainLocale.INFO, "type");
        special[7].setText(c.getMask().getTouch() + "");

        add(jtf);
        if (multiAtk) {
            add(prevatk);
            prevatk.setEnabled(false);
            add(atkind);
            atkind.setHorizontalAlignment(SwingConstants.CENTER);
            add(nextatk);
        }
    }

    protected void addListeners() {
        prevatk.setLnr(sel -> {
            dispAtk = getPrev();
            prevatk.setEnabled(getPrev() != -1);
            nextatk.setEnabled(true);
            resetAtk();
        });
        nextatk.setLnr(sel -> {
            dispAtk = getNext();
            prevatk.setEnabled(true);
            nextatk.setEnabled(getNext() != -1);
            resetAtk();
        });
    }

    private int getPrev() {
        for (int i = dispAtk - 1; i >= 0; i--)
            if (c.getMask().getShare(i) > 0)
                return i;
        return -1;
    }
    private int getNext() {
        for (int i = dispAtk + 1; i < c.getMask().getAtkTypeCount(); i++)
            if (c.getMask().getShare(i) > 0)
                return i;
        return -1;
    }

    protected abstract void reset();

    /**
     * Make sure to also call reset() after finishing with overriden resetAtk(). Not called now as proc doesn't get initialized
     */
    protected void resetAtk() {
        atkind.setText(get(MainLocale.PAGE, "atk") + " " + (dispAtk + 1) + " (" + get(MainLocale.INFO, "er2") + ": " + c.getMask().getShare(dispAtk) + ")");
        if (atks != null)
            for (JL[] atk : atks)
                for (JL jl : atk)
                    remove(jl);
        MaskAtk[] atkData = c.getMask().getAtks(dispAtk);
        atks = new JL[atkData.length + atkList.size()][isBC ? 4 : 14];
        for (int i = 0; i < atks.length; i++)
            for (int j = 0; j < atks[i].length; j++) {
                add(atks[i][j] = new JL());
                atks[i][j].setBorder(BorderFactory.createEtchedBorder());
                if (j == 0) {
                    atks[i][0].setHorizontalAlignment(SwingConstants.LEFT);
                    atks[i][0].setHorizontalTextPosition(SwingConstants.RIGHT);
                } else if (j % 2 == 0)
                    atks[i][j].setHorizontalAlignment(SwingConstants.CENTER);
            }
        if (proc != null)
            for (JLabel p : proc)
                remove(p);
        main[2][3].setText(MainBCU.convertTime(c.getMask().getPost(false, dispAtk)));

        for (int i = 0; i < atks.length; i++) {
            MaskAtk matk = i < atkData.length ? atkData[i] : atkList.get(i - atkData.length);
            atks[i][0].setText(matk.getName().toLowerCase().startsWith("combo") ? " [combo]" : get(MainLocale.INFO, "atk") + (i < atkData.length ? "" : " [" + matk.getName() + "]"));
            atks[i][0].setIcon(UtilPC.getIcon(2, matk.isRange() ? Data.ATK_AREA : Data.ATK_SINGLE));
            atks[i][2].setText(MainLocale.INFO, "preaa");
            atks[i][3].setText(MainBCU.convertTime(matk.getPre()));
            if (!isBC) {
                atks[i][4].setText(MainLocale.INFO, "dire");
                atks[i][5].setText(matk.getDire() == 1 ? get(MainLocale.PAGE, "unit") : matk.getDire() == -1 ? get(MainLocale.PAGE, "enemy") : "N/A");
                atks[i][6].setText(MainLocale.INFO, "type");
                atks[i][7].setText(String.valueOf(matk.getTarget()));
                atks[i][8].setText(MainLocale.INFO, "count");
                atks[i][9].setText(matk.loopCount() < 0 ? get(MainLocale.UTIL, "inf") : matk.loopCount() + "");

                atks[i][10].setText(MainLocale.INFO, "ability");
                StringBuilder abis = new StringBuilder();
                for (int j = 0; j < Interpret.SABIS.length; j++)
                    if (((1 << j) & matk.getAltAbi()) > 0)
                        abis.append(Interpret.SABIS[j]);
                String strabis = abis.toString();
                if(strabis.endsWith(", "))
                    strabis = strabis.substring(0, strabis.length() - 2);
                atks[i][11].setText(strabis);
                SortedPackSet<Trait> atrs = matk.getATKTraits();
                if (!atrs.isEmpty()) {
                    atks[i][12].setText(MainLocale.INFO, "trait");
                    String[] Atraits = Interpret.getTrait(atrs);
                    atks[i][13].setText(Arrays.toString(Atraits));
                    atks[i][12].setToolTipText(atks[i][12].getText());
                }
            }
        }
        for (int i = 0; i < atkList.size(); i++) {
            int ind = i + atkData.length;
            atks[ind][0].setText(get(MainLocale.INFO, "atk") + " [" + atkList.get(i).str + "]");
            atks[ind][2].setText(MainLocale.INFO, "preaa");
            atks[ind][3].setText(MainBCU.convertTime(atkList.get(i).pre));
            if (!isBC) {
                atks[ind][4].setText(MainLocale.INFO, "dire");
                atks[ind][5].setText(atkList.get(i).dire == 1 ? get(MainLocale.PAGE, "unit") : atkList.get(i).dire == -1 ? get(MainLocale.PAGE, "enemy") : "N/A");
                atks[ind][0].setIcon(UtilPC.getIcon(2, atkList.get(i).range ? Data.ATK_AREA : Data.ATK_SINGLE));
            }
        }
    }

    @Override
    protected void resized(int x, int y) {
        set(inis[0], x, y, 0, 0, 400, 50);
        set(inis[1], x, y, 400, 0, 200, 50);
        set(inis[2], x, y, 600, 0, 1000, 50);

        for (int i = 0; i < main.length; i++) {
            int aw = 1600 / main[i].length;
            for (int j = 0; j < main[i].length; j++)
                if (i + j != 1 || j == 0)
                    set(main[i][j], x, y, aw * j, 50 + 50 * i, aw, 50);
        }
        set(jtf, x, y, 200, 50, 200, 50);
        int h = 50 + main.length * 50;

        if (displaySpecial) {
            int w = 1600 / special.length;
            for (int i = 0; i < special.length; i++)
                set(special[i], x, y, w * i, h, w, 50);
            h += 50;
        } else
            for (JL jl : special)
                set(jl, x, y, 0, 0, 0, 0);

        if (multiAtk) {
            set(prevatk, x, y, 0, h, 400, 50);
            set(atkind, x, y, 600, h, 400, 50);
            set(nextatk, x, y, 1200, h, 400, 50);
            h += 50;
        }
        for (int i = 0; i < atks.length; i++) {
            int tw = 0;
            for (int j = 0; j < atks[i].length; j++) {
                int w = isBC || (displaySpecial && Math.abs(j - 12) == 1) ? 400 : displaySpecial || j < 8 ? 200 : 0;
                set(atks[i][j], x, y, tw, h + 50 * i, w, 50);
                tw += w;
                if (j == 7)
                    if (displaySpecial) {
                        h += 50;
                        tw = 0;
                    }
            }
        }
        h += atks.length * 50;
        for (int i = 0; i < proc.length; i++)
            set(proc[i], x, y, i % 2 * 800, h + 50 * (i / 2), i % 2 == 0 && i + 1 == proc.length ? 1600 : 800, 50);
        h += proc.length * 25 + (proc.length % 2 == 1 ? 25 : 0);
        set(desc, x, y, 0, h, 1600, 200);
    }

    protected int getH() {
        int l = 1 + main.length + atks.length;
        if (displaySpecial)
            l += 1 + (isBC ? 0 : atks.length);
        if (multiAtk)
            l++;
        return (l + (proc.length + (proc.length % 2 == 1 ? 1 : 0)) / 2) * 50 + (!c.getExplanation().replace("\n", "").isEmpty() ? 200 : 0);
    }

    protected void updateTooltips() {
        for (JLabel jl : proc) {
            String str = jl.getText();
            StringBuilder sb = new StringBuilder();
            FontMetrics fm = jl.getFontMetrics(jl.getFont());
            while (fm.stringWidth(str) >= 400) {
                int i = 1;
                String wrapped = str.substring(0, i);
                while (fm.stringWidth(wrapped) < 400)
                    wrapped = str.substring(0, i++);

                int maximum; //JP proc texts don't count with space, this is here to prevent it from staying in while loop forever
                if (CommonStatic.getConfig().langs[0] == CommonStatic.Lang.Locale.JP)
                    maximum = Math.max(wrapped.lastIndexOf("。"), wrapped.lastIndexOf("、"));
                else
                    maximum = Math.max(Math.max(wrapped.lastIndexOf(" "), wrapped.lastIndexOf(".")), wrapped.lastIndexOf(","));

                if (maximum <= 0)
                    maximum = Math.min(i, wrapped.length());

                wrapped = wrapped.substring(0, maximum);
                sb.append(wrapped).append("<br>");
                str = str.substring(wrapped.length());
            }
            sb.append(str);
            jl.setToolTipText("<html>" + sb + "</html>");
        }
    }

    protected abstract void panelClicked(Data.Proc.ProcItem item);

    @Override
    public JButton getBackButton() {
        return null;
    }
}
