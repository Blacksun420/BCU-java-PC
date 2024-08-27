package page.anim;

import common.CommonStatic;
import common.CommonStatic.BCAuxAssets;
import common.system.fake.FakeGraphics;
import common.system.fake.FakeImage;
import page.view.ViewBox;
import utilpc.PP;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static page.anim.IconBox.IBConf.*;

public interface IconBox extends ViewBox {

	class IBConf {
		public static final int[] line = new int[4];
		public static int mode = 0, type = 0;
		public static boolean glow = false;
	}

	class IBCtrl extends ViewBox.Controller {

		public int w = 1;
		public int h = 1;

		protected boolean drag;

		@Override
		public synchronized void mouseDragged(MouseEvent e) {
			if (!drag) {
				super.mouseDragged(e);
				return;
			}
			Point t = e.getPoint();
			int modifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			if ((e.getModifiers() & modifier) > 0) {
				line[2] += t.x - p.x;
				line[3] += t.y - p.y;

				if(line[2] < 1)
					line[2] = 1;

				if(line[3] < 1)
					line[3] = 1;

				if(line[0] + line[2] >= w) {
					line[2] = w - line[0];
				}

				if(line[1] + line[3] >= h) {
					line[3] = h - line[1];
				}
			} else {
				line[0] += t.x - p.x;
				line[1] += t.y - p.y;

				if(line[0] < 0)
					line[0] = 0;

				if(line[1] < 0)
					line[1] = 0;

				if(line[0] + line[2] >= w) {
					line[0] = w - line[2];
				}

				if(line[1] + line[3] >= h) {
					line[1] = h - line[3];
				}
			}

			p = t;
		}

		@Override
		public synchronized void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if (cont.isBlank() && !new PP(p).out(line, 1, -5))
				drag = true;
		}

		@Override
		public synchronized void mouseReleased(MouseEvent e) {
			drag = false;
			super.mouseReleased(e);
		}

		public void postdraw(FakeGraphics gra) {
			if (cont.isBlank()) {
				BCAuxAssets aux = CommonStatic.getBCAssets();

				int t = mode == 0 ? (type == 1 || type == 2) ? type : 0 : 4;

				FakeImage bimg = aux.ico[mode][t].getImg();

				int bw = bimg.getWidth();
				int bh = bimg.getHeight();

				float r = Math.min(1f * line[2] / bw, 1f * line[3] / bh);

				gra.setColor(FakeGraphics.BLACK);
				gra.drawRect(line[0] - 1, line[1] - 1, line[2] + 1, line[3] + 1);

				if (glow) {
					gra.setComposite(FakeGraphics.BLEND, 255, 3);
					bimg = aux.ico[0][6].getImg();
					gra.drawImage(bimg, line[0], line[1], (int) (bw * r), (int) (bh * r));
//					gra.setComposite(FakeGraphics.BLEND, 117, 3);
//					bimg = aux.ico[0][7].getImg();
//					gra.drawImage(bimg, line[0], line[1], (int) (bw * r), (int) (bh * r));
					gra.setComposite(FakeGraphics.DEF, 0, 0);
				}
				if (mode == 0 && type > 2)
					bimg = aux.ico[0][7].getImg();
				else
					bimg = aux.ico[mode][t].getImg();
				gra.drawImage(bimg, line[0], line[1], (int) (bw * r), (int) (bh * r));
			}
		}

		public void predraw(FakeGraphics gra) {
			if (cont.isBlank()) {
				BCAuxAssets aux = CommonStatic.getBCAssets();

				if (mode == 0 && type > 2 || mode == 1) {
					FakeImage bimg = aux.ico[mode][type].getImg();

					int bw = bimg.getWidth();
					int bh = bimg.getHeight();

					float r = Math.min(1f * line[2] / bw, 1f * line[3] / bh);

					gra.drawImage(bimg, line[0], line[1], bw * r, bh * r);
				}
			}
		}

		public void synchronizeDimension() {
			if(line[0] + line[2] >= w) {
				line[0] = w - line[2];

				if(line[0] < 0) {
					line[2] += line[0];
					line[0] = 0;

					if(line[2] < 1)
						line[2] = 1;
				}
			}

			if(line[1] + line[3] >= h) {
				line[1] = h - line[3];

				if(line[1] < 0) {
					line[3] += line[1];
					line[1] = 0;

					if(line[3] < 1)
						line[3] = 1;
				}
			}
		}
	}

	void changeType();

	BufferedImage getClip();

	@Override
	IBCtrl getCtrl();

	void setBlank(boolean selected);

	void updateControllerDimension(int w, int h);
}