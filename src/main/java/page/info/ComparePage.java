package page.info;

import common.pack.FixIndexList;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.unit.Enemy;
import common.util.unit.Form;
import common.util.unit.Trait;
import page.*;
import page.info.filter.EnemyFindPage;
import page.info.filter.TraitList;
import page.info.filter.UnitFindPage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ComparePage extends DefaultPage {

    private static final long serialVersionUID = 1L;

    private final JPanel cont = new JPanel();
    private final JScrollPane jsp = new JScrollPane(cont);
    private final ArrayList<CompareTable> tables = new ArrayList<>();
    private final JBTN addE = new JBTN(MainLocale.PAGE, "add");
    private final JBTN remE = new JBTN(MainLocale.PAGE, "rem");

    private final JL[] main = new JL[10]; // stats on both
    private final JL seco = new JL(MainLocale.INFO, "trait"); // stats after others
    private final JL[] unit = new JL[2]; // stats on unit
    private final JL enem = new JL(MainLocale.INFO, "drop"); // stats on enemy
    private final JL evol = new JL(MainLocale.INFO, "evolve"); // evolve slots

    private final JCB[] boxes = new JCB[main.length + unit.length + 4];

    private final TraitList trait = new TraitList(false);
    private final JScrollPane tlst = new JScrollPane(trait);

    private EnemyFindPage efp = null;
    private UnitFindPage ufp = null;
    private int s = -1;
    private boolean resize = true;

    public ComparePage(Page p) {
        super(p);

        ini();
        resized(true);
    }

    private void ini() {
        add(tlst);
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new JCB();
            boxes[i].setSelected(true);
            add(boxes[i]);
        }

        addStatLabels();

        main[0].setText(MainLocale.INFO, "HP");
        main[1].setText(MainLocale.INFO, "hb");
        main[2].setText(MainLocale.INFO, "range");
        main[3].setText(MainLocale.INFO, "atk");
        main[4].setText("dps");
        main[5].setText(MainLocale.INFO, "preaa");
        main[6].setText(MainLocale.INFO, "postaa");
        main[7].setText(MainLocale.INFO, "atkf");
        main[8].setText(MainLocale.INFO, "TBA");
        main[9].setText(MainLocale.INFO, "speed");

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

        unit[0].setText(MainLocale.INFO, "cdo");
        unit[1].setText(MainLocale.INFO, "price");
        boxes[main.length].setText(MainLocale.INFO, "cdo");
        boxes[1 + main.length].setText(MainLocale.INFO, "price");

        enem.setText(MainLocale.INFO, "drop");
        boxes[main.length + unit.length].setText(MainLocale.INFO, "drop");

        evol.setText(MainLocale.INFO, "evolve");
        boxes[main.length + unit.length + 1].setText(MainLocale.INFO, "evolve");

        seco.setText(MainLocale.INFO, "trait");
        boxes[main.length + unit.length + 2].setText(MainLocale.INFO, "trait");

        boxes[boxes.length - 1].setText("ability");

        for (int i = 0; i < 3; i++) {
            CompareTable comp = new CompareTable(this, i, boxes);
            tables.add(comp);
            cont.add(comp);
        }
        cont.setLayout(null);
        add(jsp);

        add(addE);
        add(remE);
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
        for (int i = 0; i < boxes.length; i++) {
            int FI = i;
            boxes[i].addActionListener(x -> {
                for (CompareTable tab : tables)
                    tab.seles[FI] = boxes[FI].isSelected();
                requireResize();
            });
        }

        addE.addActionListener(l -> {
            CompareTable comp = new CompareTable(this, tables.size(), boxes);
            tables.add(comp);
            cont.add(comp);
            remE.setEnabled(true);
        });
        remE.addActionListener(l -> {
            cont.remove(tables.size() - 1);
            tables.remove(tables.size() - 1);
            remE.setEnabled(tables.size() > 2);
        });
    }

    protected int getTableWidth() {
        return 1800 / Math.min(tables.size(), 4);
    }

    protected void getEnemy(int ind) {
        changePanel(efp = new EnemyFindPage(getThis(), false));
        s = ind;
    }
    protected void getUnit(int ind) {
        changePanel(ufp = new UnitFindPage(getThis(), false));
        s = ind;
    }

    private void reset() {
        s = -1;
        for (CompareTable ct : tables) {
            ct.setTraits(trait.getSelectedValuesList());
            ct.reset();
        }

        requireResize();
    }

    @Override
    protected void renew() {
        if (s == -1)
            return;
        if (efp != null && efp.getSelected() != null)
            tables.get(s).renewEnemy((Enemy)efp.getSelected());
        else if (ufp != null && ufp.getForm() != null)
            tables.get(s).renewUnit((Form) ufp.getForm());

        efp = null;
        ufp = null;
        reset();
    }

    private void addStatLabels() {
        for (int i = 0; i < main.length; i++) {
            main[i] = new JL("-");
            main[i].setHorizontalAlignment(SwingConstants.CENTER);
            add(main[i]);
        }
        for (int i = 0; i < unit.length; i++) {
            unit[i] = new JL("-");
            unit[i].setHorizontalAlignment(SwingConstants.CENTER);
            add(unit[i]);
        }

        enem.setHorizontalAlignment(SwingConstants.CENTER);
        add(enem);
        seco.setHorizontalAlignment(SwingConstants.CENTER);
        add(seco);
        evol.setHorizontalAlignment(SwingConstants.CENTER);
        add(evol);
    }

    @Override
    protected void resized(int x, int y) {
        super.resized(x, y);
        set(addE, x, y, 50, 150, 200, 50);
        set(remE, x, y, 50, 200, 200, 50);
        int width = 600;
        int posY = 250;

        for (JCB b : boxes) {
            set(b, x, y, 300 + (3 * width), posY, 200, 50);
            posY += 50;
        }
        posY = resizeArr(main, x, y, 250, 0);
        posY = resizeArr(unit, x, y, posY, main.length);
        posY += resizeJL(enem, x, y, posY, main.length + unit.length);
        posY += resizeJL(evol, x, y, posY, main.length + unit.length + 1) * 2;
        posY += resizeJL(seco, x, y, posY, main.length + unit.length + 2);

        int unselected = ((int) Arrays.stream(boxes).filter(b -> !b.isSelected()).count());
        if (!boxes[main.length + unit.length + 1].isSelected())
            unselected++;
        int height = 200 + unselected * 50;

        if (!boxes[boxes.length - 1].isSelected())
            height -= 50;

        int tw = getTableWidth();
        set(jsp, x, y, 250, 50, 1825, posY + height - 50);
        for (int i = 0; i < tables.size(); i++) {
            set(tables.get(i), x, y, i * tw, 0, tw, posY + height - 50);
            tables.get(i).resized(true);
        }
        cont.setPreferredSize(size(x, y, tables.size() * tw, posY + height - 50).toDimension());
        jsp.getHorizontalScrollBar().setUnitIncrement(size(x, y, 50));
        jsp.revalidate();

        set(tlst, x, y, 50, posY, 200, height);
        if (resize)
            tlst.revalidate();

        resize = false;
    }

    public final int resizeJL(final JL jl, final int x, final int y, final int posY, final int check) {
        if (!boxes[check].isSelected()) {
            set(jl, 0, 0, 0, 0, 0, 0);
            return 0;
        }
        set(jl, x, y, 50, posY, 200, 50); //250 start X pos for comparetable list
        return 50;
    }
    public final int resizeArr(final JL[] jls, final int x, final int y, int posY, final int checkbox) {
        for (int i = 0; i < jls.length; i++)
            posY += resizeJL(jls[i], x, y, posY, checkbox + i);
        return posY;
    }

    public void requireResize() {
        resize = true;
        for (CompareTable tab : tables)
            tab.requireResize();
    }
}