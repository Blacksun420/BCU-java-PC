package page.pack;

import common.CommonStatic;
import common.pack.PackData.UserPack;
import common.pack.Source.Workspace;
import common.system.VImg;
import common.util.Data;
import common.util.stage.CastleImg;
import common.util.stage.CastleList;
import main.MainBCU;
import page.*;
import page.support.Exporter;
import page.support.Importer;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class CastleEditPage extends DefaultPage {

	private static final long serialVersionUID = 1L;

	private final JList<CastleImg> jlst = new JList<>();
	private final JScrollPane jspst = new JScrollPane(jlst);
	private final JL jl = new JL();
	private final JTF spwn = new JTF();

	private final JL sc = new JL(MainLocale.PAGE, "scale");
	private final JTF sca = new JTF();

	private final JL cn = new JL("Center");
	private final JTF cen = new JTF();

	private final JBTN addc = new JBTN(MainLocale.PAGE, "add");
	private final JBTN remc = new JBTN(MainLocale.PAGE, "rem");
	private final JBTN impc = new JBTN(MainLocale.PAGE, "import");
	private final JBTN expc = new JBTN(MainLocale.PAGE, "export");
	private final JL jspwn = new JL(MainLocale.PAGE, "bspwn");

	private final UserPack pack;
	private final CastleList cas;

	private boolean changing = false;

	public CastleEditPage(Page p, UserPack ac) {
		super(p);
		pack = ac;
		cas = ac.castles;

		ini();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jspst, x, y, 50, 100, 300, 1000);
		set(addc, x, y, 400, 100, 200, 50);
		set(impc, x, y, 400, 200, 200, 50);
		set(expc, x, y, 400, 300, 200, 50);
		set(remc, x, y, 400, 400, 200, 50);
		set(jspwn, x, y, 400, 550, 200, 50);
		set(spwn, x, y, 400, 600, 200, 50);
		set(sc, x, y, 400, 700, 200, 50);
		set(sca, x, y, 400, 750, 200, 50);
		set(cn, x, y, 400, 900, 200, 50);
		set(cen, x, y, 400, 950, 200, 50);
		set(jl, x, y, 800, 50, 1000, 1000);

	}

	private void addListeners() {
		jlst.addListSelectionListener(arg0 -> {
			if (changing || arg0.getValueIsAdjusting())
				return;
			CastleImg img = jlst.getSelectedValue();
			ImageIcon ic = null;
			if (img != null) {
				VImg s = img.img;
				if (s != null)
					ic = UtilPC.resizeIcon(s, s.getImg().getWidth() * img.scale / 1000, s.getImg().getHeight() * img.scale / 1000);
				spwn.setEnabled(true);
				spwn.setText(String.valueOf(img.boss_spawn));
				sca.setEnabled(true);
				sca.setText(String.valueOf(img.scale));
				cen.setEnabled(true);
				cen.setText(String.valueOf(img.center));
			} else {
				spwn.setEnabled(false);
				sca.setEnabled(false);
				cen.setEnabled(false);
			}
			jl.setIcon(ic);
		});

		spwn.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (jlst.isSelectionEmpty())
					return;
				changing = true;
				double firstDouble = CommonStatic.parseDoubleN(spwn.getText());
				int formatDouble = (int) (Interpret.formatDouble(firstDouble, 2) * 100);
				double result = (25 * Math.floor(formatDouble / 25.0)) / 100;

				jlst.getSelectedValue().boss_spawn = result;
				spwn.setText(String.valueOf(result));
				changing = false;
			}
		});

		sca.setLnr(c -> {
			if (jlst.isSelectionEmpty())
				return;
			changing = true;
			jlst.getSelectedValue().scale = CommonStatic.parseIntN(sca.getText());
			changing = false;
		});

		cen.setLnr(c -> {
			if (jlst.isSelectionEmpty())
				return;
			changing = true;
			CastleImg img = jlst.getSelectedValue();
			int res = Math.max(0, CommonStatic.parseIntN(cen.getText()));
			res = Math.min(res, img.img.getImg().getWidth());
			img.center = res;
			changing = false;
		});

		addc.addActionListener(arg0 -> getFile("Choose your file", null));

		impc.addActionListener(arg0 -> {
			CastleImg img = jlst.getSelectedValue();
			if (img != null)
				getFile("Choose your file", img);
		});

		expc.addActionListener(arg0 -> {
			CastleImg img = jlst.getSelectedValue();
			if (img != null) {
				VImg s = img.img;
				if (s != null)
					new Exporter((BufferedImage) s.getImg().bimg(), Exporter.EXP_IMG);
			}
		});

		remc.addActionListener(arg0 -> {
			CastleImg img = jlst.getSelectedValue();
			if (img != null) {
				cas.remove(img);
				((Workspace) pack.source).getCasFile(img.getID()).delete();
				changing = true;
				setList();
				changing = false;
			}
		});

	}

	private void getFile(String str, CastleImg vimg) {
		changing = true;
		BufferedImage bimg = new Importer(str, Importer.IMP_IMG).getImg();
		if (bimg == null)
			return;

		if (vimg == null) {
			CastleImg castle = new CastleImg(cas.getNextID(CastleImg.class), MainBCU.builder.toVImg(bimg));
			castle.boss_spawn = 828.5;
			cas.add(vimg = castle);
		} else {
			vimg.img.setImg(MainBCU.builder.build(bimg));
		}

		try {
			OutputStream os = ((Workspace) pack.source).writeFile("castles/" + Data.trio(vimg.id.id) + ".png");
			ImageIO.write(bimg, "PNG", os);
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			getFile("Failed to save file", vimg);
			return;
		}

		setList();
		changing = false;
	}

	private void ini() {
		add(jspst);
		add(jl);
		add(addc);
		add(remc);
		add(impc);
		add(expc);
		add(jspwn);
		add(spwn);
		add(sc);
		add(sca);
		add(cn);
		add(cen);
		spwn.setEnabled(false);
		sca.setEnabled(false);
		cen.setEnabled(false);
		setList();
		addListeners();

	}

	private void setList() {
		int ind = jlst.getSelectedIndex();
		jlst.setListData(cas.toArray());
		if (ind < 0)
			ind = 0;
		if (ind >= cas.size())
			ind = cas.size() - 1;
		jlst.setSelectedIndex(ind);
		CastleImg img = jlst.getSelectedValue();
		if (img != null) {
			jl.setIcon(UtilPC.getIcon(img.img));
			spwn.setEnabled(true);
			spwn.setText(String.valueOf(img.boss_spawn));
			sca.setEnabled(true);
			sca.setText(String.valueOf(img.scale));
			cen.setEnabled(true);
			cen.setText(String.valueOf(img.center));
		} else {
			jl.setIcon(null);
			spwn.setEnabled(false);
			sca.setEnabled(false);
			cen.setEnabled(false);
			spwn.setText("Boss Spawn: ");
		}

	}

}
