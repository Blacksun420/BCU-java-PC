package page.basis;

import common.CommonStatic;
import common.battle.LineUp;
import common.pack.SaveData;
import common.system.P;
import common.system.SymCoord;
import common.system.VImg;
import common.system.fake.FakeGraphics;
import common.system.fake.FakeImage;
import common.util.Res;
import common.util.stage.Limit;
import common.util.unit.*;
import page.Page;
import utilpc.PP;
import utilpc.awt.FG2D;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class LineUpBox extends Canvas {

	private static final long serialVersionUID = 1L;

	private AbForm[] backup = new AbForm[5];
	private final Page page;
	protected LineUp lu;
	private int pt = 0;
	private boolean time = false;
	private Combo sc;
	private PP relative, mouse;
	protected boolean swap = false;

	protected Limit lim;
	protected int price = 1;
	protected SaveData sdat;
	private Set<AbForm> testL = null;

	protected AbForm sf;

	public LineUpBox(Page p) {
		page = p;
		setIgnoreRepaint(true);
	}

	@Override
	public void paint(Graphics g) {
		VImg[] slot = CommonStatic.getBCAssets().slot;
		Image bimg = createImage(600, 300);
		if (bimg == null)
			return;
		FakeGraphics gra = new FG2D(bimg.getGraphics());
		for (byte i = 0; i < 3; i++)
			for (int j = 0; j < 5; j++) {
				AbForm f = getForm(i, j);
				VImg img;
				if (f == null)
					img = slot[0];
				else
					img = f.getDeployIcon();
				if (sf == null || sf != f || relative == null)
					gra.drawImage(img.getImg(), 120 * j, 100 * i);
				if (f == null)
					continue;
				if (!time && sc != null)
					for (Form fc : sc.forms)
						if (f.unit() == fc.unit && f.getFid() >= fc.fid)
							gra.drawImage(slot[2].getImg(), 120 * j, 100 * i);
				if (sf != null && f.unit() == sf.unit() && relative == null)
					if(time)
						gra.drawImage(slot[1].getImg(), 120 * j, 100 * i);
					else
						gra.drawImage(slot[2].getImg(), 120 * j, 100 * i);
				if (sf == null || sf != f || relative == null) {
					IForm ef = i != 2 ? lu.efs[i][j] : IForm.newIns(f, lu.getLv(f));
					byte unuse = unusable(f, ef, i);
					if (unuse != 0) {
						gra.colRect(120 * j, 100 * i, img.getImg().getWidth(), img.getImg().getHeight(), 255 / unuse, 0, 0, 100 * unuse);
						Res.getCost(-1, false,
							new SymCoord(gra, 1, 120 * j, 100 * i + img.getImg().getHeight(), 2));
					} else if (swap) {
						Res.getCost((int) ef.getPrice(price), true,
							new SymCoord(gra, 0.8f, 120 * j, 100 * i + (img.getImg().getHeight() / 3.5f), 2));
						Res.getLv(lu.getLv(f).getLv() + lu.getLv(f).getPlusLv(),
							new SymCoord(gra, 1, 120 * j, 100 * i + img.getImg().getHeight(), 2));
					} else {
						Res.getCost((int) ef.getPrice(price), true,
							new SymCoord(gra, 1, 120 * j, 100 * i + img.getImg().getHeight(), 2));
						Res.getLv(lu.getLv(f).getLv() + lu.getLv(f).getPlusLv(),
							new SymCoord(gra, 0.8f, 120 * j, 100 * i + (img.getImg().getHeight() / 3.5f), 2));
					}
					if (lu.getLv(f).getOrbs() != null) {
						int[][] orbs = lu.getLv(f).getOrbs();
						for (int k = orbs.length - 1; k >= 0; k--)
							OrbBox.paintOrb(gra, orbs[k], (120 * j) + 90 - (k * Math.min(24, 72f / orbs.length)), 100 * i, 24);
					}
				}
			}
		if (relative != null && sf != null) {
			Point p = relative.sf(mouse).toPoint();
			FakeImage uni = sf.getDeployIcon().getImg();
			gra.drawImage(uni, p.x, p.y);
			IForm ef = IForm.newIns(sf, lu.getLv(sf));
			byte unuse = unusable(sf, ef, (byte)(getPos(sf)/5));
			if (unuse != 0) {
				gra.colRect(p.x, p.y, uni.getWidth(), uni.getHeight(), 255 / unuse, 0, 0, 100 * unuse);
				Res.getCost(-1, true, new SymCoord(gra, 1, p.x, p.y + uni.getHeight(), 2));
			} else if (swap) {
				Res.getCost((int) ef.getPrice(price), true,
					new SymCoord(gra, 0.8f, p.x, p.y + (uni.getHeight() / 3.5f), 2));
				Res.getLv(lu.getLv(sf).getLv() + lu.getLv(sf).getPlusLv(),
					new SymCoord(gra, 1, p.x, p.y + uni.getHeight(), 2));
			} else {
				Res.getCost((int) ef.getPrice(price), true,
					new SymCoord(gra, 1, p.x, p.y + uni.getHeight(), 2));
				Res.getLv(lu.getLv(sf).getLv() + lu.getLv(sf).getPlusLv(),
					new SymCoord(gra, 0.8f, p.x, p.y + (uni.getHeight() / 3.5f), 2));
			}
			if (lu.getLv(sf).getOrbs() != null) {
				int[][] orbs = lu.getLv(sf).getOrbs();
				for (int k = orbs.length - 1; k >= 0; k--)
					OrbBox.paintOrb(gra, orbs[k], p.x + 90 - (k * Math.min(24, 72f / orbs.length)), p.y, 24);
			}
		}
		g.drawImage(bimg, 0, 0, getWidth(), getHeight(), null);
		pt++;
		if (pt == 5)
			time = !time;
		pt %= 5;
	}

	public AbForm getSelected() {
		return sf;
	}
	public byte unusable() {
		if (sf == null)
			return 1;
		return unusable((byte)getPos(sf));
	}
	public byte unusable(byte slot) {
		AbForm f = slot >= 10 ? backup[slot - 10] : lu.fs[slot / 5][slot % 5];
		if (f == null)
			return 1;
		IForm ef = slot >= 10 ? IForm.newIns(f, lu.getLv(f)) : lu.efs[slot / 5][slot % 5];
		return unusable(f, ef, (byte)(slot/5));
	}
	public byte unusable(AbForm f, IForm ef, byte row) {
		if ((isTest() && !testL.contains(f)) || (sdat != null && sdat.locked(f)))
			return 2;
		return (byte)(lim != null && ef instanceof EForm && lim.unusable(((EForm)ef).du, price, row) ? 1 : 0);
	}

	public void setLU(LineUp l) {
		lu = l;
		backup = new AbForm[5];
	}

	public void setLimit(Limit l, SaveData data, int price) {
		lim = l;
		this.price = price;
		sdat = data;
		paint(getGraphics());
	}

	public Limit getLim() {
		return lim;
	}

	public void setTest(Set<AbForm> units) {
		testL = units;
		paint(getGraphics());
	}

	public boolean isTest() {
		return testL != null;
	}

	protected void adjForm() {
		if (!(sf instanceof Form) || getPos(sf) == -1)
			return;
		int i = getPos(sf);
		if (getForm(i).unit() == sf.unit()) {
			Form[] ufs = sf.unit().getForms();
			sf = ufs[(getForm(i).getFid() + 1) % ufs.length];
			setForm(i, sf);
			lu.renew();
			page.callBack(null);
		}

	}

	protected void click(Point p) {
		p = getPos(p);
		select(getForm(p.y, p.x));
	}

	protected void drag(Point p) {
		if (relative == null || sf == null)
			return;
		mouse = new PP(p).divide(getScale());
		int ori = getPos(sf);
		Point pf = getPos(mouse);
		int fin = pf.x + pf.y * 5;
		if (ori != fin)
			jump(ori, fin);
	}

	protected void press(Point p) {
		click(p);
		PP ul = new PP(getPos(p)).times(new P(120, 100));
		relative = ul.sf(mouse = new PP(p).divide(getScale()));

	}

	protected void release() {
		relative = null;
		mouse = null;
	}

	protected void select(Combo c) {
		sc = c;
		time = false;
		paint(getGraphics());
	}

	protected void select(AbForm f) {
		sf = f;
		if (f == null)
			return;
		if (getPos(f) == -1) {
			boolean b = false;
			for (int i = 0; i < 5; i++)
				if (backup[i] == null) {
					backup[i] = f;
					b = true;
					break;
				}
			if (!b)
				backup[4] = f;
		}
		time = true;
		paint(getGraphics());
		page.callBack(f);
	}

	protected void setLv(Level lv) {
		if (sf == null)
			return;
		if (sf instanceof Form) {
			Form f = (Form)sf;
			lu.setLv(f.unit, f.regulateLv(lv, lu.getLv(f)));
		} else
			lu.setLv((UniRand) sf, lv);
	}

	protected void setPos(int pos) {
		if (sf == null || getPos(sf) == -1)
			return;
		int p = getPos(sf);
		jump(p, pos);
	}

	protected void updateLU() {
		Set<AbUnit> su = new TreeSet<>();
		for (int i = 0; i < 10; i++)
			if (getForm(i) != null)
				su.add(getForm(i).unit());
		for (int i = 0; i < 5; i++)
			if (backup[i] != null && su.contains(backup[i].unit()))
				backup[i] = null;
	}

	protected void resetBackup() {
		Arrays.fill(backup, null);
	}

	private AbForm getForm(int pos) {
		return pos < 10 ? lu.fs[pos / 5][pos % 5] : backup[pos % 5];
	}

	private AbForm getForm(int i, int j) {
		return i < 2 ? lu.fs[i][j] : backup[j];
	}

	private int getPos(AbForm f) {
		if (f == null)
			return -1;
		for (int i = 0; i < 15; i++)
			if (getForm(i) != null && getForm(i).unit() == f.unit())
				return i;
		return -1;
	}

	private Point getPos(Point p) {
		PP siz = new PP(getSize());
		PP ans = new PP(p).times(new P(5, 3)).divide(siz);
		ans.limit(new PP(4, 2));
		return ans.toPoint();
	}

	private Point getPos(PP p) {
		PP ans = p.copy().times(new P(5, 3)).divide(new P(600, 300));
		ans.limit(new P(4, 2));
		return ans.toPoint();
	}

	private P getScale() {
		return new PP(getSize()).divide(new P(600, 300));
	}

	private void jump(int ior, int ifi) {
		AbForm f = getForm(ior);
		if (ior > ifi)
			for (int i = ifi; i <= ior; i++)
				f = setForm(i, f);
		else {
			if (ifi > 9) {
				f = setForm(ifi, f);
				if (ior > 9)
					setForm(ior, f);
				else
					for (int i = 0; i < 5; i++)
						if (backup[i] == null) {
							setForm(10 + i, f);
							break;
						}
				ifi = 9;
				f = null;
			}
			for (int i = ifi; i >= ior; i--)
				f = setForm(i, f);
		}
		lu.arrange();
		lu.renew();
		page.callBack(null);
	}

	private AbForm setForm(int pos, AbForm f) {
		AbForm ans = getForm(pos);
		if (pos < 10)
			lu.fs[pos / 5][pos % 5] = f;
		else
			backup[pos % 5] = f;
		return ans;
	}

}