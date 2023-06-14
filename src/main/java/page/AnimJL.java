package page;

import common.util.anim.EAnimU;

import javax.swing.*;

public class AnimJL extends Page {
    public final EAnimU anim;
    public final JLabel[] JLs;

    public AnimJL(Page p, EAnimU ea) {
        super(p);
        anim = ea;
        JLs = new JLabel[anim.anim().mamodel.n];
    }

    @Override
    protected void resized(int x, int y) {

    }

    @Override
    public JButton getBackButton() {
        return null;
    }
}
