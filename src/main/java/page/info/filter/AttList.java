package page.info.filter;

import page.JTG;
import page.Page;
import utilpc.Theme;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static utilpc.Interpret.*;

public class AttList extends JList<String> {

	private static final long serialVersionUID = 1L;

	protected AttList(int type, int para) {
		setSelectionBackground(Theme.DARK.NIMBUS_SELECT_BG);

		setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
				JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
				BufferedImage v;
				if (type == -1) {
					v = ind < para ? UtilPC.getIcon(0, EABIIND[ind]) : UtilPC.getIcon(1, EPROCIND[ind - para]);
				} else if (type == 0) {
					v = ind < SABIS.length ? UtilPC.getIcon(0, ind) : UtilPC.getIcon(1, UPROCIND[ind - SABIS.length]);
				} else
					v = UtilPC.getIcon(type, ind);
				if (v == null)
					return jl;
				jl.setIcon(new ImageIcon(v));
				return jl;
			}
		});
	}
}