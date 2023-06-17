package page.pack;

import common.CommonStatic;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.stage.StageMap;
import common.util.unit.AbUnit;
import common.util.unit.Unit;
import page.*;
import page.support.ReorderList;
import page.support.UnitLCR;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class PackSavePage extends Page {

    private final JBTN back = new JBTN(MainLocale.PAGE, "back");
    private final JL packJL = new JL(MainLocale.PAGE, "pkstm");
    private final JList<StageMap> packMaps = new JList<>();
    private final JScrollPane jkMaps = new JScrollPane(packMaps);
    private final JL potJL = new JL(MainLocale.PAGE, "adreq");
    private final JList<StageMap> potMaps = new JList<>();
    private final JScrollPane jotMap = new JScrollPane(potMaps);
    private final JL reqJL = new JL(MainLocale.PAGE, "cureq");
    private final JList<StageMap> reqMaps = new JList<>();
    private final JScrollPane jrqMap = new JScrollPane(reqMaps);
    private final JBTN addreq = new JBTN(MainLocale.PAGE, "add");
    private final JBTN remreq = new JBTN(MainLocale.PAGE, "rem");

    private final JL lockUnit = new JL(MainLocale.PAGE, "ulk");
    private final ReorderList<AbUnit> locUnits = new ReorderList<>();
    private final JScrollPane jcU = new JScrollPane(locUnits);
    private final JL unlockUnit = new JL(MainLocale.PAGE, "plku");
    private final ReorderList<AbUnit> ulkUnits = new ReorderList<>();
    private final JScrollPane jlU = new JScrollPane(ulkUnits);
    private final JBTN addulk = new JBTN(MainLocale.PAGE, "add");
    private final JTF maxFrm = new JTF();
    private final JBTN remulk = new JBTN(MainLocale.PAGE, "rem");

    private final UserPack pk;
    private StageMap curMap;
    private boolean changing = false;

    public PackSavePage(Page p, UserPack pack) {
        super(p);
        pk = pack;

        ini();
        resized();
    }

    private void addListeners() {
        back.addActionListener(l -> changePanel(getFront()));
        packMaps.addListSelectionListener(l -> setMap(packMaps.getSelectedValue(), true));
        potMaps.addListSelectionListener(l -> addreq.setEnabled(pk.editable && curMap != null && potMaps.getSelectedIndex() != -1));
        reqMaps.addListSelectionListener(l -> remreq.setEnabled(pk.editable && curMap != null && reqMaps.getSelectedIndex() != -1));

        addreq.setLnr(e -> {
            if (changing)
                return;
            changing = true;
            StageMap map = packMaps.getSelectedValue();

            map.unlockReq.addAll(potMaps.getSelectedValuesList());
            setMap(map, false);
            changing = false;
        });
        remreq.setLnr(e -> {
            if (changing)
                return;
            changing = true;
            StageMap map = packMaps.getSelectedValue();

            for (StageMap selMap : reqMaps.getSelectedValuesList())
                map.unlockReq.remove(selMap);
            setMap(map, false);
            changing = false;
        });

        locUnits.addListSelectionListener(l -> addulk.setEnabled(pk.editable && locUnits.getSelectedIndex() != -1));
        ulkUnits.addListSelectionListener(l -> {
            remulk.setEnabled(pk.editable && ulkUnits.getSelectedIndex() != -1);
            setMJTF();
        });
        maxFrm.addActionListener(l -> {
            if (changing)
                return;
            int f = CommonStatic.parseIntN(maxFrm.getText());
            if (f <= 0) {
                setMJTF();
                return;
            }
            changing = true;
            AbUnit u = ulkUnits.getSelectedValue();
            pk.defULK.replace(u, Math.min(f - 1, u.getForms().length - 1));
            setMJTF();
            changing = false;
        });
        addulk.setLnr(e -> {
            if (changing)
                return;
            changing = true;
            List<AbUnit> units = locUnits.getSelectedValuesList();
            for (AbUnit u : units)
                pk.defULK.put(u, u.getForms().length - 1);
            setUnits();
            changing = false;
        });
        remulk.setLnr(e -> {
            if (changing)
                return;
            changing = true;
            List<AbUnit> units = ulkUnits.getSelectedValuesList();
            for (AbUnit u : units)
                pk.defULK.remove(u);
            setUnits();
            changing = false;
        });
    }

    private void setMap(StageMap smap, boolean nmod) {
        if (nmod && curMap == smap)
            return;
        curMap = smap;
        if (smap != null) {
            List<StageMap> maps = pk.mc.maps.getList();
            LinkedList<StageMap> rm = new LinkedList<>(), pm = new LinkedList<>();
            for (StageMap m : maps) {
                if (m == smap)
                    continue;
                (smap.unlockReq.contains(m) ? rm : pm).add(m);
            }
            potMaps.setListData(pm.toArray(new StageMap[0]));
            potMaps.setSelectedIndex(0);
            reqMaps.setListData(rm.toArray(new StageMap[0]));
            reqMaps.setSelectedIndex(0);
        } else {
            potMaps.setListData(new StageMap[0]);
            reqMaps.setListData(new StageMap[0]);
        }
        addreq.setEnabled(pk.editable && smap != null && potMaps.getSelectedIndex() != -1);
        remreq.setEnabled(pk.editable && smap != null && reqMaps.getSelectedIndex() != -1);
    }

    private void setMJTF() {
        maxFrm.setEnabled(pk.editable && ulkUnits.getSelectedIndex() != -1);
        if (ulkUnits.getSelectedIndex() != -1)
            maxFrm.setText("Max Form: " + (pk.defULK.get(ulkUnits.getSelectedValue()) + 1));
        else
            maxFrm.setText("");
    }

    private void setUnits() {
        LinkedList<Unit> lu = new LinkedList<>(), uu = new LinkedList<>();
        for (Unit u : UserProfile.getBCData().units)
            (pk.defULK.containsKey(u) ? uu : lu).add(u);
        for (Unit u : pk.units)
            (pk.defULK.containsKey(u) ? uu : lu).add(u);

        locUnits.setListData(lu.toArray(new AbUnit[0]));
        ulkUnits.setListData(uu.toArray(new AbUnit[0]));
        addulk.setEnabled(pk.editable && locUnits.getSelectedIndex() != -1);
        remulk.setEnabled(pk.editable && ulkUnits.getSelectedIndex() != -1);
        setMJTF();
    }

    private void ini() {
        add(back);
        add(packJL);
        add(jkMaps);
        add(potJL);
        add(jotMap);
        add(addreq);
        add(reqJL);
        add(jrqMap);
        add(remreq);

        add(lockUnit);
        add(jcU);
        locUnits.setCellRenderer(new UnitLCR());
        add(addulk);
        add(unlockUnit);
        add(jlU);
        ulkUnits.setCellRenderer(new UnitLCR());
        add(maxFrm);
        add(remulk);

        List<StageMap> maps = pk.mc.maps.getList();
        packMaps.setListData(maps.toArray(new StageMap[0]));

        addListeners();
        setMap(null, false);
        setUnits();
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);
        set(packJL, x, y, 50, 100, 300, 50);
        set(jkMaps, x, y, 50, 150, 300, 950);

        set(potJL, x, y, 350, 100, 300, 50);
        set(jotMap, x, y, 350, 150, 300, 400);
        set(addreq, x, y, 350, 550, 300, 50);

        set(reqJL, x, y, 350, 600, 300, 50);
        set(jrqMap, x, y, 350, 650, 300, 400);
        set(remreq, x, y, 350, 1050, 300, 50);

        set(lockUnit, x, y, 700, 100, 300, 50);
        set(jcU, x, y, 700, 150, 300, 900);
        set(addulk, x, y, 700, 1050, 300, 50);

        set(unlockUnit, x, y, 1000, 100, 300, 50);
        set(jlU, x, y, 1000, 150, 300, 850);
        set(maxFrm, x, y, 1000, 1000, 300, 50);
        set(remulk, x, y, 1000, 1050, 300, 50);
    }

    @Override
    public JButton getBackButton() {
        return back;
    }
}
