package page.basis;

import common.pack.UserProfile;
import common.util.unit.Form;
import common.util.unit.UniRand;
import common.util.unit.Unit;
import page.DefaultPage;
import page.Page;
import page.info.UnitInfoPage;
import page.pack.UREditPage;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class LubCont extends DefaultPage {

	private static final long serialVersionUID = 1L;

	protected LubCont(Page p) {
		super(p);
	}

	protected abstract LineUpBox getLub();

	@Override
	protected void keyTyped(KeyEvent e) {
		if (Character.isDigit(e.getKeyChar())) {
			int i = Integer.parseInt(String.valueOf(e.getKeyChar()));
			if (i == 0)
				i = 9;
			else
				i--;
			getLub().setPos(i);
		} else if (e.getKeyChar() == 'd') {
			getLub().setPos(10);
		}
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		if (e.getSource() == getLub())
			if (e.getButton() == MouseEvent.BUTTON1)
				getLub().click(e.getPoint());
			else if (getLub().sf instanceof Form)
				changePanel(new UnitInfoPage(this, (Unit)getLub().sf.unit(), getLub().lu.getLv(getLub().sf)));
			else if (getLub().sf != null)
				changePanel(new UREditPage(this, UserProfile.getUserPack(getLub().sf.getID().pack), (UniRand)getLub().sf));
	}

	@Override
	protected void mouseDragged(MouseEvent e) {
		if (e.getSource() == getLub())
			getLub().drag(e.getPoint());
	}

	@Override
	protected void mousePressed(MouseEvent e) {
		if (e.getSource() == getLub())
			getLub().press(e.getPoint());
	}

	@Override
	protected void mouseReleased(MouseEvent e) {
		if (e.getSource() == getLub())
			getLub().release();
	}

	@Override
    public synchronized void onTimer(int t) {
		super.onTimer(t);
		getLub().paint(getLub().getGraphics());
	}

}
