package page.support;

import common.system.VImg;
import common.util.unit.AbCharacter;
import utilpc.UtilPC;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CharaTCR extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private final int[] lnk;
    private final int manualIndex;

    public CharaTCR(int[] ints) {
        this(ints, 1);
    }

    public CharaTCR(int[] ints, int manualIndex) {
        lnk = ints;
        this.manualIndex = manualIndex;
    }

    @Override
    public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
        Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
        if (lnk != null)
            c = lnk[c];
        if (c != manualIndex || (v != null && !(v instanceof AbCharacter)))
            return comp;
        JLabel jl = (JLabel) comp;
        AbCharacter e = (AbCharacter) v;
        jl.setHorizontalTextPosition(SwingConstants.RIGHT);
        jl.setIcon(null);
        if (e == null) {
            jl.setText("");
            return jl;
        }
        jl.setText(e.toString());
        VImg vimg = e.getIcon();
        if (vimg == null)
            return jl;
        jl.setIcon(UtilPC.getIcon(vimg));
        return jl;
    }
}
