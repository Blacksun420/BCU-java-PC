package page.info.edit;

import common.battle.data.CustomUnit;
import common.battle.data.PCoin;
import common.pack.PackData.UserPack;
import common.util.Data;
import common.util.unit.Form;
import page.DefaultPage;
import page.JBTN;
import page.Page;
import page.info.filter.TraitList;
import page.info.filter.UnitFindPage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PCoinEditPage extends DefaultPage {

    private static final long serialVersionUID = 1L;

    private final JBTN addP = new JBTN(0, "add");
    private final JBTN remP = new JBTN(0, "rempc");
    private final boolean editable;
    private final CustomUnit uni;
    private final JPanel cont = new JPanel();
    private final JScrollPane jsp = new JScrollPane(cont);
    private final List<PCoinEditTable> pCoinEdits = new ArrayList<>();
    private final TraitList nptr = new TraitList(false);
    private final JScrollPane ttr = new JScrollPane(nptr);

    PCoinEditTable.TalentTable tab;
    protected final UnitFindPage ufp;

    public PCoinEditPage(Page p, Form u, boolean edi) {
        super(p);
        uni = (CustomUnit) u.du;
        editable = edi;

        if (uni.pcoin != null)
            for (int i = 0; i < uni.pcoin.info.size(); i++)
                pCoinEdits.add(new PCoinEditTable(this, uni, i, editable));
        ufp = new UnitFindPage(this, ((UserPack)uni.getPack().getPack()));

        ini();
    }

    @Override
    protected void resized(int x, int y) {
        super.resized(x, y);
        set(addP, x, y, 400, 50, 300, 50);
        set(remP, x, y, 700, 50, 300, 50);
        set(jsp, x, y, 50, 100, 2000, 1120);
        set(ttr, x, y, 2075, 100, 200, 1120);
        for (int i = 0; i < pCoinEdits.size(); i++)
            set(pCoinEdits.get(i), x, y, i * getPTableWidth(), 0, getPTableWidth(), 1100);
        cont.setPreferredSize(size(x, y, pCoinEdits.size() * 400, 1050).toDimension());
        jsp.getHorizontalScrollBar().setUnitIncrement(size(x, y, 50));
        jsp.revalidate();
    }

    protected int getPTableWidth() {
        return Math.max(400, 2000 / pCoinEdits.size());
    }

    private void addListeners() {
        addP.addActionListener(arg0 -> {
            if (uni.pcoin == null)
                uni.pcoin = new PCoin(uni);

            int slot = uni.pcoin.info.size();
            uni.pcoin.info.add(getCoinParams(slot + 1));

            uni.pcoin.max = new int[uni.pcoin.info.size()];
            uni.pcoin.max[uni.pcoin.info.size() -1] = uni.pcoin.info.get(uni.pcoin.info.size() -1)[1];

            pCoinEdits.add(new PCoinEditTable(this, uni, slot, editable));
            cont.add(pCoinEdits.get(slot));
            assignSubPage(pCoinEdits.get(slot));

            setCoinTypes();
        });

        //PCoin Structure:
        //[0] = ability identifier, [1] = max lv, [2,4,6,8] = min lv values, [3,5,7,9] = max lv values, [10]TextID, [11]LvID, [12]NameID, [13]Limit ([10~12] are useless)

        remP.addActionListener(arg0 -> {
            uni.pcoin = null;
            while (!pCoinEdits.isEmpty()) {
                removeSubPage(pCoinEdits.size() - 1);
                cont.remove(pCoinEdits.size() - 1);
                pCoinEdits.remove(pCoinEdits.size() - 1);
            }
            setCoinTypes();
        });

        nptr.addListSelectionListener(arg0 -> {
            if (!nptr.getValueIsAdjusting()) {
                for (int i = 0; i < nptr.list.size(); i++)
                    if (nptr.isSelectedIndex(i)) {
                        uni.pcoin.trait.add(nptr.list.get(i));
                    } else
                        uni.pcoin.trait.remove(nptr.list.get(i));
            }
            setCoinTypes();
        });
    }

    @Override
    protected void renew() {
        if (tab == null)
            return;
        tab.renew(ufp.getSelected());
        tab = null;
    }

    private int[] getCoinParams(int slot) {
        for (int i = 0; i < pCoinEdits.size(); i++)
            if (uni.pcoin.info.get(i)[0] == slot) {
                slot = pCoinEdits.get(i).randomize();
                break;
            }
        int[] talent = Data.get_CORRES(slot);
        if (talent[0] == Data.PC_IMU || talent[0] == 5 || talent[0] == -1) {
            slot = pCoinEdits.get(0).randomize();
            talent = Data.get_CORRES(slot);
        }
        if (talent[0] == Data.PC_AB || talent[0] >= Data.PC_TRAIT)
            return new int[]{slot, 1, 0}; //[0],[1],[13]
        else if (talent[0] == Data.PC_BASE)
            return new int[]{slot, 1, 2, 20, 0}; //[0],[1],[2],[3],[13]

        int params = uni.getProc().getArr(talent[1]).getAllFields().length * 2;
        if (talent.length >= 3)
            params -= talent[2] * 2;
        int[] nps = new int[params + 3];
        nps[0] = slot;
        nps[1] = 10;
        return nps;
    }

    protected void setCoinTypes() {
        for (PCoinEditTable pcedi : pCoinEdits)
            pcedi.setCTypes(uni.pcoin != null && uni.pcoin.info.size() > pcedi.talent);
        setCoins();
    }

    //Changes the other talent indexes once a talent is removed from the list
    protected void removed(int talent) {
        cont.remove(talent);
        pCoinEdits.remove(talent);
        for (int i = talent; i < uni.pcoin.info.size(); i++)
            pCoinEdits.get(i).shiftDown();

        if (uni.pcoin.info.isEmpty())
            uni.pcoin = null;
        setCoinTypes();
    }

    private void ini() {
        add(addP);
        add(remP);
        add(ttr);
        nptr.setup((UserPack)uni.getPack().getPack(), false);
        nptr.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        for (PCoinEditTable pce : pCoinEdits) {
            cont.add(pce);
            assignSubPage(pce);
        }
        cont.setLayout(null);
        add(jsp);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        addListeners();
        setCoins();
    }

    private void setCoins() {
        if (uni.pcoin != null)
            uni.pcoin.update();
        for (PCoinEditTable pct : pCoinEdits)
            pct.setData();
        addP.setEnabled(editable && (uni.pcoin == null || uni.pcoin.info.size() < Data.PC_CORRES.length + Data.PC_CUSTOM.length));
        remP.setEnabled(editable && uni.pcoin != null);
        nptr.setEnabled(editable && uni.pcoin != null);
        fireDimensionChanged();
    }
}