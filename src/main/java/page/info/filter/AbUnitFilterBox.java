package page.info.filter;

import common.pack.PackData;
import common.pack.UserProfile;
import common.util.lang.MultiLangCont;
import common.util.lang.ProcLang;
import common.util.stage.Limit;
import common.util.unit.AbForm;
import common.util.unit.Form;
import common.util.unit.UniRand;
import common.util.unit.Unit;
import page.JTG;
import page.MainLocale;
import page.Page;
import utilpc.UtilPC;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utilpc.Interpret.*;

public class AbUnitFilterBox extends EntityFilterBox {

    private static final long serialVersionUID = 1L;

    protected final Limit lim;
    protected final int price;

    private final JList<String> rare = new JList<>(RARITY);
    private final AttList abis = new AttList(0, 0);
    private final JScrollPane jr = new JScrollPane(rare);
    private final JScrollPane jab = new JScrollPane(abis);
    private final JTG limbtn = new JTG(0, "usable");
    private final JTG inccus = new JTG(MainLocale.PAGE, "inccus"); //This button sucks, feel free to remove

    public AbUnitFilterBox(Page p, Limit limit, int price) {
        super(p);
        lim = limit;
        this.price = price;

        ini();
        confirm();
    }

    public AbUnitFilterBox(Page p, String pack, List<String> parent) {
        super(p, pack, parent);
        lim = null;
        price = 0;

        ini();
        confirm();
    }

    @Override
    public int[] getSizer() {
        return new int[] { 450, 1150, 0, 500 };
    }

    @Override
    protected void resized(int x, int y) {
        super.resized(x, y);

        set(limbtn, x, y, 0, 300, 200, 50);
        set(inccus, x, y, 0, 0, 200, 50);
        set(jr, x, y, 0, 50, 200, 250);
        set(jab, x, y, 250, 50, 200, 1100);
    }

    @Override
    protected void confirm() {
        List<AbForm> ans = new ArrayList<>();
        minDiff = 5;
        for(PackData p : UserProfile.getAllPacks()) {
            if(!inccus.isSelected() && !(p instanceof PackData.DefPack) || !validatePack(p)) {
                continue;
            }

            for (Unit u : p.units.getList()) {
                if (ans.contains(u))
                    continue;
                for (Form f : u.forms) {
                    String fname = MultiLangCont.getStatic().FNAME.getCont(f);
                    if (fname == null)
                        fname = f.names.toString();
                    int diff = UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase());
                    if (diff > minDiff)
                        continue;

                    minDiff = diff;
                    ans.add(u);
                }
            }
            for (UniRand u : p.randUnits.getList()) {
                if (ans.contains(u))
                    continue;
                String fname = u.name;
                int diff = UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase());
                if (diff > minDiff)
                    continue;

                minDiff = diff;
                ans.add(u);
            }
        }
        getFront().callBack(ans);
    }

    protected boolean validatePack(PackData p) {
        return p instanceof PackData.DefPack || pack == null || p.getSID().equals(pack) || parents.contains(p.getSID());
    }

    @Override
    protected void ini() {
        super.ini();

        Collections.addAll(va, SABIS);
        ProcLang proclang = ProcLang.get();
        for (int i = 0; i < UPROCIND.length; i++)
            va.add(proclang.get(UPROCIND[i]).abbr_name);
        abis.setListData(va);
        abis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        set(rare);
        set(abis);
        add(jr);
        add(jab);
        set(inccus);
        inccus.setSelected(true);

        if (lim != null) {
            add(limbtn);
            limbtn.addActionListener(l -> confirm());
        }
    }
}
