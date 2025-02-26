package page.info;

import common.util.stage.Revival;
import common.util.unit.AbEnemy;
import main.Opts;
import page.JBTN;
import page.JL;
import page.MainLocale;
import page.Page;
import utilpc.UtilPC;

import javax.swing.*;

public class RevivalPage extends Page {

    private final Revival rev;
    private final float mul;
    private final JL eData = new JL();
    private final JL rBGM = new JL();
    private final JL rSOL = new JL();
    private final JL rStt = new JL();
    private final JL rBos = new JL();
    private final JBTN nRev = new JBTN(MainLocale.PAGE, "next");


    public RevivalPage(Page p, Revival r, float starMult) {
        super(p);
        rev = r;
        mul = starMult;
        ini();
    }

    @Override
    protected void resized(int x, int y) {
        set(eData, x, y, 0, 0, 400, 50);
        set(rBos, x, y, 400, 0, 100, 50);
        if (rev.rev != null) {
            set(rBGM, x, y, 0, 50, 250, 50);
            set(rSOL, x, y, 250, 50, 250, 50);
            set(rStt, x, y, 0, 100, 300, 50);
            set(nRev, x, y, 300, 100, 200, 50);
        } else {
            set(rBGM, x, y, 0, 50, 500, 50);
            set(rSOL, x, y, 0, 100, 200, 50);
            set(rStt, x, y, 200, 100, 300, 50);
        }
    }

    @Override
    public JButton getBackButton() {
        return null;
    }

    @Override
    public int getHeight() {
        return 120;
    }

    private void ini() {
        AbEnemy e = rev.enemy.get();
        add(eData);
        eData.setText(e.toString());
        eData.setIcon(UtilPC.getIcon(e.getIcon()));
        add(rBGM);
        rBGM.setText("BGM: " + (rev.bgm == null ? "N/A" : rev.bgm.get().toString()));
        add(rSOL);
        rSOL.setText(get(MainLocale.PAGE, "soul") + ": " + (rev.soul == null ? "N/A" : rev.soul.get().toString()));
        add(rStt);
        rStt.setText(get(MainLocale.INFO, "t2") + ": " + (int)(rev.mhp * mul) + "% / " + (int)(rev.matk * mul) + "%");
        add(rBos);
        rBos.setText(MainLocale.INFO, "b" + rev.boss);

        if (rev.rev != null) {
            add(nRev);
            nRev.setLnr(x -> Opts.showRevivalData(this, rev.rev, mul));
        }
    }
}
