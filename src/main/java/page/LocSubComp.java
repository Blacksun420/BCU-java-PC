package page;

import common.util.lang.LocaleCenter.Binder;
import utilpc.PP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LocSubComp {

	static class LocBinder implements Binder {

		private final int loc;
		private final String info;
		private final LocSubComp par;

		LocBinder(LocSubComp par, int loc, String info) {
			this.par = par;
			this.loc = loc;
			this.info = info;
		}

		@Override
		public String getNameID() {
			return loc < 0 ? null : MainLocale.RENN[loc] + "_" + info;
		}

		@Override
		public String getNameValue() {
			return MainLocale.getLoc(loc, info);
		}

		@Override
		public String getTooltipID() {
			if (par.page == null)
				return null;
			return par.page.getClass().getSimpleName() + "_" + info;
		}

		@Override
		public String getToolTipValue() {
			if (par.page == null)
				return null;
			return MainLocale.getTTT(par.page.getClass().getSimpleName(), info);
		}

		@Override
		public Binder refresh() {
			return this;
		}

		@Override
		public void setNameValue(String str) {
			MainLocale.setLoc(loc, info, str);
		}

		@Override
		public void setToolTipValue(String str) {
			if (par.page == null)
				return;
			MainLocale.setTTT(par.page.getClass().getSimpleName(), info, str);
		}

	}

	protected final LocComp lc;
	protected Binder binder;
	protected Page page;

	public LocSubComp(LocComp comp) {
		lc = comp;
	}

	public void update() {
		if (binder == null)
			return;
		lc.setText(binder.getNameValue());
		if (binder.getToolTipValue() != null)
			lc.setToolTipText(binder.getToolTipValue());
	}

	protected void added(Page p) {
		page = p;
		update();
	}

	protected void init(Binder b) {
		binder = b;
		update();
	}

	protected void init(int i, String str) {
		init(new LocBinder(this, i, str));
	}

	protected void reLoc() {
		binder = binder.refresh();
		update();
	}

}