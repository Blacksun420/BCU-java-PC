package page.info.filter;

import common.pack.PackData;
import common.pack.UserProfile;
import common.util.lang.MultiLangCont;
import common.util.unit.AbEnemy;
import common.util.unit.EneRand;
import common.util.unit.Enemy;
import page.Page;
import utilpc.UtilPC;

import java.util.ArrayList;
import java.util.List;

public class AbEnemyFilterBox extends EnemyFilterBox {

    private static final long serialVersionUID = 1L;

    public AbEnemyFilterBox(Page p) {
        super(p);
    }

    public AbEnemyFilterBox(Page p, String pack, List<String> parent) {
        super(p, pack, parent);
    }

    @Override
    protected void confirm() {
        minDiff = 5;
        List<AbEnemy> ans = new ArrayList<>();
        for(PackData p : UserProfile.getAllPacks())
            if (validatePack(p))
                for (Enemy e : p.enemies.getList())
                    if (validateEnemy(e))
                        ans.add(e);

        for(PackData.UserPack p : UserProfile.getUserPacks())
            if(pack == null || pack.equals(p.desc.id) || parents.contains(p.desc.id))
                for(EneRand rand : p.randEnemies.getList()) {
                    int diff = UtilPC.damerauLevenshteinDistance(rand.name.toLowerCase(), name.toLowerCase());
                    if(diff <= minDiff) {
                        ans.add(rand);
                        minDiff = diff;
                    }
                }
        for (int i = 0; i < ans.size(); i++) {
            if (ans.get(i) instanceof Enemy) {
                Enemy e = (Enemy) ans.get(i);
                String ename = MultiLangCont.getStatic().ENAME.getCont(e);
                if (ename == null)
                    ename = e.names.toString();
                if (UtilPC.damerauLevenshteinDistance(ename.toLowerCase(), name.toLowerCase()) > minDiff) {
                    ans.remove(i);
                    i--;
                }
            } else if (UtilPC.damerauLevenshteinDistance(((EneRand) ans.get(i)).name.toLowerCase(), name.toLowerCase()) > minDiff) {
                ans.remove(i);
                i--;
            }
        }
        getFront().callBack(ans);
    }
}
