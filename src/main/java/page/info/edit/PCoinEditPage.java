package page.info.edit;


import common.battle.data.CustomUnit;
import common.battle.data.PCoin;
import common.util.Data;
import common.util.unit.Form;
import page.JBTN;
import page.Page;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PCoinEditPage extends Page {

    private static final long serialVersionUID = 1L;

    private final JBTN back = new JBTN(0, "back");
    private final JBTN addP = new JBTN(0, "add");
    private final JBTN remP = new JBTN(0, "rempc");
    private final boolean editable;
    private final CustomUnit uni;
    private final JPanel cont = new JPanel();
    private final JScrollPane jsp = new JScrollPane(cont);
    private final List<PCoinEditTable> pCoinEdits = new ArrayList<>();

    public PCoinEditPage(Page p, Form u, boolean edi) {
        super(p);
        uni = (CustomUnit) u.du;
        editable = edi;

        if (uni.pcoin != null)
            for (int i = 0; i < uni.pcoin.info.size(); i++)
                pCoinEdits.add(new PCoinEditTable(this, uni, i, editable));

        ini();
        resized();
    }

    @Override
    public JButton getBackButton() {
        return back;
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);
        set(addP, x, y, 400, 50, 300, 50);
        set(remP, x, y, 700, 50, 300, 50);
        set(jsp, x, y, 50, 100, 2050, 1100);
        for (int i = 0; i < pCoinEdits.size(); i++) {
            set(pCoinEdits.get(i), x, y, i * 400, 0, 400, 1050);
            pCoinEdits.get(i).resized();
        }
        cont.setPreferredSize(size(x, y, pCoinEdits.size() * 400, 1050).toDimension());
        jsp.getHorizontalScrollBar().setUnitIncrement(size(x, y, 50));
        jsp.revalidate();
    }

    private void addListeners() {
        back.addActionListener(arg0 -> changePanel(getFront()));

        addP.addActionListener(arg0 -> {
            if (uni.pcoin == null)
                uni.pcoin = new PCoin(uni);

            int slot = uni.pcoin.info.size();
            uni.pcoin.info.add(getCoinParams(slot + 1));

            uni.pcoin.max = new int[uni.pcoin.info.size()];
            for(int i = 0; i < uni.pcoin.info.size() -1; i++)
                uni.pcoin.max[i] = uni.pcoin.info.get(i)[1];

            pCoinEdits.add(new PCoinEditTable(this, uni, slot, editable));
            cont.add(pCoinEdits.get(slot));

            setCoinTypes();
        });

        //PCoin Structure:
        //[0] = ability identifier, [1] = max lv, [2,4,6,8] = min lv values, [3,5,7,9] = max lv values, [10]TextID, [11]LvID, [12]NameID, [13]Limit ([10~12] are useless)

        remP.addActionListener(arg0 -> {
            uni.pcoin = null;
            while (!pCoinEdits.isEmpty()) {
                cont.remove(pCoinEdits.size() - 1);
                pCoinEdits.remove(pCoinEdits.size() - 1);
            }

            setCoinTypes();
        });
    }

    private int[] getCoinParams(int slot) {
        for (int i = 0; i < pCoinEdits.size(); i++)
            if (uni.pcoin.info.get(i)[0] == slot) {
                slot = pCoinEdits.get(i).randomize();
                break;
            }
        int[] talent = Data.get_CORRES(slot);
        if (talent[0] == Data.PC_AB || talent[0] == Data.PC_TRAIT)
            return new int[]{slot, 1, 0}; //[0],[1],[13]
        else if (talent[0] == Data.PC_BASE)
            return new int[]{slot, 1, 2, 20, 0}; //[0],[1],[2],[3],[13]

        int params = uni.getProc().getArr(talent[1]).getDeclaredFields().length * 2;
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

        if (uni.pcoin.info.size() == 0)
            uni.pcoin = null;
        setCoinTypes();
    }

    private void ini() {
        add(back);
        add(addP);
        add(remP);
        for (PCoinEditTable pce : pCoinEdits)
            cont.add(pce);
        cont.setLayout(null);
        add(jsp);
        addListeners();
        setCoins();
    }

    private void setCoins() {
        if (uni.pcoin != null)
            uni.pcoin.update();
        for (PCoinEditTable pct : pCoinEdits)
            pct.setData();
        addP.setEnabled(editable && (uni.pcoin == null || uni.pcoin.info.size() < PCoinEditTable.PC_TOT));
        remP.setEnabled(editable && uni.pcoin != null);
    }
}