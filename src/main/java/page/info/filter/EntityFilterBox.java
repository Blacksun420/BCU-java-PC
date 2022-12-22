package page.info.filter;

import common.pack.FixIndexList;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.unit.Trait;
import page.JBTN;
import page.MainLocale;
import page.Page;

import javax.swing.*;
import java.util.Vector;

import static utilpc.Interpret.ATKCONF;
import static utilpc.Interpret.TRAIT_EVA;

public abstract class EntityFilterBox extends Page {

    private static final long serialVersionUID = 1L;

    public String name = ""; //Keeps all data for all filter pages. Consider rare and abis are the only difference between unit and enemy filters, as well as confirm function
    protected static int minDiff = 5;

    protected final PackData.UserPack pack;

    protected final JBTN[] orop = new JBTN[4];
    protected final byte[] ops = new byte[4];
    protected final Vector<String> va = new Vector<>();
    protected final TraitList trait = new TraitList(false);
    protected final AttList atkt = new AttList(2, 0);
    protected final JScrollPane jt = new JScrollPane(trait);
    protected final JScrollPane jat = new JScrollPane(atkt);

    protected EntityFilterBox(Page p) {
        super(p);
        pack = null;
    }

    protected EntityFilterBox(Page p, PackData.UserPack pack) {
        super(p);
        this.pack = pack;
    }

    @Override
    public JButton getBackButton() {
        return null;
    }

    @Override
    public void callBack(Object o) {
        confirm();
    }

    protected abstract int[] getSizer();
    @Override
    protected void resized(int x, int y) {
        set(orop[0], x, y, 0, 350, 200, 50);
        set(orop[1], x, y, 250, 0, 200, 50);
        set(orop[2], x, y, 0, 800, 200, 50);

        set(jt, x, y, 0, 400, 200, 350);
        set(jat, x, y, 0, 850, 200, 300);
    }

    protected abstract void confirm();

    protected boolean validatePack(PackData p) {
        return p instanceof PackData.DefPack || pack == null || p == pack || pack.desc.dependency.contains(p.getSID());
    }

    protected void ini() {
        opBtnListeners();
        FixIndexList.FixIndexMap<Trait> BCtraits = UserProfile.getBCData().traits;
        for (int i = 0 ; i < (this instanceof UnitFilterBox ? TRAIT_EVA : BCtraits.size() - 1) ; i++)
            trait.list.add(BCtraits.get(i));
        if (pack == null)
            for (PackData.UserPack pacc : UserProfile.getUserPacks())
                pacc.traits.forEach(t -> trait.list.add(t));
        else {
            trait.list.addAll(pack.traits.getList());
            for (String s : pack.desc.dependency)
                UserProfile.getUserPack(s).traits.forEach(t -> trait.list.add(t));
        }
        trait.setListData();

        atkt.setListData(ATKCONF);
        int m = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
        trait.setSelectionMode(m);
        atkt.setSelectionMode(m);
        set(trait);
        set(atkt);
        add(jt);
        add(jat);
    }

    private void opBtnListeners() {
        for (int i = 0; i < orop.length; i++) {
            int finalI = i;
            orop[i] = new JBTN(MainLocale.PAGE, "ops0");
            add(orop[i]);
            orop[i].addActionListener(arg0 -> changeOperator(finalI));
        }
    }

    protected void set(AbstractButton b) {
        add(b);
        b.addActionListener(arg0 -> confirm());
    }

    protected void set(JList<?> jl) {
        jl.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        jl.addListSelectionListener(arg0 -> confirm());
    }

    private void changeOperator(int i) {
        ops[i] = (byte) ((ops[i] + 1) % 4);
        orop[i].setText(get(MainLocale.PAGE, "ops" + ops[i]));
        confirm();
    }

    protected boolean unchangeable(int ind) {
        return ops[ind] % 2 == 0;
    }

    protected boolean processOperator(int ind, boolean res) {
        if (ops[ind] >= 2)
            res = !res;
        return res;
    }
}
