package page.view;

import common.system.P;
import common.system.fake.FakeGraphics;
import common.util.anim.EAnimI;
import common.util.anim.EAnimU;
import common.util.pack.Background;
import common.util.pack.bgeffect.BackgroundEffect;
import page.JTG;
import page.RetFunc;
import page.awt.RecdThread;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Queue;

public interface ViewBox {
	double siz = 0.5875;
	class Conf {
		public static boolean white;
	}

	class Controller {
		public final P ori = new P(0, 0);
		public float siz = 0.5f;
		protected Point p = null;
		protected ViewBox cont;

		public synchronized void mouseDragged(MouseEvent e) {
			if (p == null)
				return;
			ori.x += p.x - e.getX();
			ori.y += p.y - e.getY();
			p = e.getPoint();
		}

		public synchronized void mousePressed(MouseEvent e) {
			p = e.getPoint();
		}

		public synchronized void mouseReleased(MouseEvent e) {
			p = null;
		}

		public void resize(float pow) {
			siz *= pow;
		}

		public void setCont(ViewBox vb) {
			cont = vb;
		}

		public void resetPos() {
			ori.x = 0;
			ori.y = 0;
		}
	}

	class EKeeper {
		public boolean blank;
		private EAnimI ent;
		private Background bg;
		private BackgroundEffect bgEffect;
		public boolean bgi;
		private double ms, y, midY, groundHeight;
		private int fw, midh;

		public EAnimI getEnt() {
			return ent;
		}
		public void setEntity(EAnimI ieAnim) {
			ent = ieAnim;
		}

		public void setBackground(Background bg, int w, int h) {
			if (bgEffect != null)
				bgEffect.release();
			bgEffect = null;

			this.bg = bg;
			if (bg != null) {
				double minSiz = getReulatedSiz(0, w, h);
				double maxSiz = getReulatedSiz(Double.MAX_VALUE, w, h);
				groundHeight = (h * 2 / 10.0) * (1 - minSiz/maxSiz);

				int gh = ((BufferedImage) bg.parts[Background.BG].bimg()).getHeight();
				double sh = bg.top ? ((BufferedImage) bg.parts[Background.TOP].bimg()).getHeight() : 1020 - groundHeight;
				if(sh < 0)
					sh = 0;
				double r = h / (gh + sh + 408);
				fw = (int) (768 * r);
				int i = 1;
				while (fw * i <= w)
					i++;
				fw *= i * 4;

				midY = groundHeight / minSiz;
				midh = h + (int) (groundHeight * (siz - maxSiz) / (maxSiz - minSiz));
				y = 1530 * siz - midh;
				ms = h / minSiz;

				if (bg.bgEffect != null)
					bgEffect = bg.bgEffect.get();
			}
			bgi = false;
		}

		public Background getBg() {
			return bg;
		}

		public void draw(FakeGraphics g, int w, int h) {
			if (bg != null) {
				bg.draw(g, new P(w, h), 0, midh, siz, (int) groundHeight);
				if (bgEffect != null)
					bgEffect.draw(g, (float) y, (float) siz, (float) midY);
				if(bg.overlay != null)
					g.gradRectAlpha(0, 0, w, h, 0, 0, bg.overlayAlpha, bg.overlay[1], 0, h, bg.overlayAlpha, bg.overlay[0]);
			}
		}

		public void update() {
			if (bgEffect != null) {
				if(!bgi) {
					bgEffect.initialize(fw, (float) ms, (float) midY, bg);
					bgi = true;
				}
				bgEffect.update(fw, (float) ms, (float) midY);
			}
			if (ent != null) {
				if (ent instanceof EAnimU) {
					EAnimU e = (EAnimU) ent;
					ent.update(e.type.rotate());
				} else
					ent.update(true);
			}
		}
	}

	class Loader implements RetFunc {

		public final RecdThread thr;
		public final boolean mp4;

		private JTG jtb;

		public Loader(Queue<BufferedImage> list, boolean mp4) {
			this.mp4 = mp4;
			thr = RecdThread.getIns(this, list, null, mp4 ? RecdThread.MP4 : RecdThread.GIF);
		}

		@Override
		public void callBack(Object o) {
			jtb.setEnabled(true);
		}

		public void finish(JTG btn) {
			jtb = btn;
			jtb.setEnabled(false);
		}

		public String getProg() {
			return "remain: " + thr.remain();
		}

		public void start() {
			thr.start();
		}

	}

	interface VBExporter {

		void end(JTG btn);

		BufferedImage getPrev();

		Loader start(boolean mp4);

	}

	Color c0 = new Color(70, 140, 160), c1 = new Color(85, 185, 205);

	default void end(JTG btn) {
		if (getExp() != null)
			getExp().end(btn);
	}

	Controller getCtrl();

	EAnimI getEnt();

	VBExporter getExp();

	default BufferedImage getPrev() {
		if (getExp() != null)
			return getExp().getPrev();
		return null;
	}

	boolean isBlank();

	default void mouseDragged(MouseEvent e) {
		getCtrl().mouseDragged(e);
	}

	default void mousePressed(MouseEvent e) {
		getCtrl().mousePressed(e);
	}

	default void mouseReleased(MouseEvent e) {
		getCtrl().mouseReleased(e);
	}

	void paint();

	default void resetPos() { getCtrl().resetPos(); }

	default void resize(float pow) {
		getCtrl().resize(pow);
	}

	void setEntity(EAnimI ieAnim);

	void setBackground(Background bg);

	static double getReulatedSiz(double size, int w, int h) {
		if (size * 510 > h)
			size = 1.0 * h / 510;
		if (size * 1530 < h)
			size = 1.0 * h / 1530;
		int maxW = (int) (w * 5 * 0.32 + 200 * 2);
		if (size * maxW < w)
			size = 1.0 * w / maxW;
		return size;
	}

	default Loader start(boolean mp4) {
		if (getExp() != null)
			return getExp().start(mp4);
		return null;
	}

	void update();
}