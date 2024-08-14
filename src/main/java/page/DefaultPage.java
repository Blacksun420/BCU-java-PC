package page;

import javax.swing.*;

public class DefaultPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JBTN back = new JBTN(0, "back");

	protected DefaultPage(Page p) {
		super(p);

		ini();
	}

	@Override
	public JButton getBackButton() {
		return back;
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
	}

	private void addListeners() {
		back.setLnr(this::getFront);
	}

	private void ini() {
		add(back);
		addListeners();
	}

}
