package page.info.filter;

import common.util.unit.AbForm;
import common.util.unit.Form;
import common.util.unit.UniRand;
import common.util.unit.Unit;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.lang.MultiLangCont;
import common.util.stage.Limit;
import page.Page;
import utilpc.UtilPC;

import java.util.ArrayList;
import java.util.List;

public class AbUnitFilterBox extends UnitFilterBox {

    private static final long serialVersionUID = 1L;

    public AbUnitFilterBox(Page p) {
        super(p, null, 0);
    }

    public AbUnitFilterBox(Page p, Limit lim, int pri) {
        super(p, lim, pri);
    }

    public AbUnitFilterBox(Page p, String pack, List<String> parent) {
        super(p, pack, parent);
    }

    @Override
    protected void confirm() {
        minDiff = 5;
        List<AbForm> ans = new ArrayList<>();
        for(PackData p : UserProfile.getAllPacks()) {
            if(!inccus.isSelected() && !(p instanceof PackData.DefPack) || !validatePack(p))
                continue;

            for (Unit u : p.units.getList())
                if (validateUnit(u))
                    for (Form f : u.forms)
                        if (validateForm(f))
                            ans.add(f);
        }

        for(PackData.UserPack p : UserProfile.getUserPacks())
            if(validatePack(p))
                for(UniRand rand : p.randUnits.getList()) {
                    int diff = UtilPC.damerauLevenshteinDistance(rand.name.toLowerCase(), name.toLowerCase());
                    if(diff <= minDiff) {
                        ans.add(rand);
                        minDiff = diff;
                    }
                }
        for (int i = 0; i < ans.size(); i++) {
            if (ans.get(i) instanceof Form) {
                Form f = (Form) ans.get(i);
                String fname = MultiLangCont.getStatic().FNAME.getCont(f);
                if (fname == null)
                    fname = f.names.toString();
                if (UtilPC.damerauLevenshteinDistance(fname.toLowerCase(), name.toLowerCase()) > minDiff) {
                    ans.remove(i);
                    i--;
                }
            } else if (UtilPC.damerauLevenshteinDistance(((UniRand) ans.get(i)).name.toLowerCase(), name.toLowerCase()) > minDiff) {
                ans.remove(i);
                i--;
            }
        }
        getFront().callBack(ans);
    }
}
