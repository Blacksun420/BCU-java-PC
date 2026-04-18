package page.basis;

import common.CommonStatic;
import common.battle.data.Orb;
import common.system.fake.FakeGraphics;
import common.util.Data;
import utilpc.awt.FG2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class OrbBox extends Canvas {
	private static final long serialVersionUID = 1L;

	private int[] orbs = new int[0];

	public OrbBox() {
		setIgnoreRepaint(true);
	}
	public OrbBox(int[] data) {
		this();
		orbs = data;
	}

	public void changeOrb(int[] orbs) {
		this.orbs = orbs;
	}

	@Override
	public synchronized void paint(Graphics g) {
		if (orbs.length == 0) {
			g.clearRect(0, 0, getWidth(), getHeight());
			return;
		}

		float a = Math.min(getWidth(), getHeight());
		float w = getWidth(), h = getHeight();

		BufferedImage img = (BufferedImage) createImage((int) a, (int) a);
		FG2D f = new FG2D(img.getGraphics());
		paintOrb(f, orbs, 0, 0, a, false);

		g.drawImage(img, (int) ((w - a) / 2), (int) ((h - a) / 2), null);
		g.dispose();
	}

	public static void paintOrb(FakeGraphics f, int[] orbs, float x, float y, float a, boolean small) {
		byte i = (byte)(small ? 1 : 0);
		f.drawImage(CommonStatic.getBCAssets().TRAITS[i][Orb.reverse(orbs[Data.ORB_TRAIT])], x, y, a, a);
		f.setComposite(FakeGraphics.TRANS, 204, 0);
		f.drawImage(orbs[Data.ORB_TYPE] < CommonStatic.getBCAssets().TYPES[i].length ? CommonStatic.getBCAssets().TYPES[i][orbs[Data.ORB_TYPE]] : CommonStatic.getBCAssets().TYPES[1-i][orbs[Data.ORB_TYPE]], x, y, a, a);
		f.setComposite(FakeGraphics.DEF, 0, 0);
		f.drawImage(CommonStatic.getBCAssets().GRADES[orbs[Data.ORB_GRADE]], x, y, a, a);
	}
}
