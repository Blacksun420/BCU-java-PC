package page.support;

import common.system.VImg;
import common.util.unit.AbForm;
import common.util.unit.Form;
import common.util.unit.Unit;
import utilpc.Theme;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;

public class UnitLCR extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
		AbForm form;
		if (o instanceof Unit)
			form = ((Unit)o).forms[0];
		else
			form = (AbForm)o;
		JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
		jl.setText(o.toString());
		jl.setIcon(null);
		jl.setHorizontalTextPosition(SwingConstants.RIGHT);

		if(form instanceof Form && ((Form)form).anim == null) {
			jl.setEnabled(false);
			jl.setText(o + " (Error - Corrupted)");
			return jl;
		}

		VImg v = form.getIcon();
		if (v == null)
			return jl;
		jl.setIcon(UtilPC.getIcon(v));
		if (s)
			jl.setBackground(Theme.DARK.NIMBUS_SELECT_BG);
		return jl;
	}
}
