package page.awt;

import common.battle.BattleField;
import common.battle.SBCtrl;
import common.system.fake.FakeGraphics;
import main.Timer;
import page.battle.BBCtrl;
import page.battle.BattleBox;
import utilpc.awt.FG2D;

import java.awt.*;
import java.awt.image.BufferedImage;

class BattleBoxDef extends Canvas implements BattleBox {

	private static final long serialVersionUID = 1L;

	public final BBPainter bbp;

	protected BufferedImage prev;

	protected BattleBoxDef(OuterBox bip, BattleField bas, int p) {
		bbp = p == 0 ? new BBPainter(bip, bas, this) : new BBCtrl(bip, (SBCtrl) bas, this);
		setIgnoreRepaint(true);
	}

	@Override
	public BBPainter getPainter() {
		return bbp;
	}

	@Override
	public void paint() {
		paint(getGraphics());
	}

	@Override
	public void paint(Graphics g) {
		synchronized (bbp) {
			int w = getWidth();
			int h = getHeight();
			if (w * h == 0)
				return;
			if (bbp.pt < bbp.bf.sb.time) {
				bbp.pt = bbp.bf.sb.time;
			}

			prev = getImage();

			if (prev == null)
				return;
			g.drawImage(prev, 0, 0, null);
			g.setColor(Color.ORANGE);
			g.drawString("Time cost: " + Timer.inter + "%, " + bbp.pt, 20, 20);
			g.dispose();
		}
	}

	@Override
	public void reset() {
		prev = null;
	}

	@Override
	public void releaseData() {
		bbp.bf.sb.release();
	}

	protected BufferedImage getImage() {
		int w = getWidth();
		int h = getHeight();
		BufferedImage img = (BufferedImage) createImage(w, h);
		if (img == null)
			return null;
		Graphics2D gra = (Graphics2D) img.getGraphics();
		FakeGraphics g = new FG2D(gra);
		bbp.draw(g);
		gra.dispose();
		return img;
	}

}
