package page.anim;

import common.CommonStatic;
import common.pack.PackData.UserPack;
import common.pack.Source;
import common.pack.Source.ResourceLocation;
import common.pack.Source.Workspace;
import common.pack.UserProfile;
import common.util.AnimGroup;
import common.util.anim.AnimCE;
import common.util.anim.ImgCut;
import common.util.anim.MaAnim;
import common.util.anim.Part;
import common.util.unit.Enemy;
import main.MainBCU;
import main.Opts;
import page.*;
import page.support.*;
import utilpc.Algorithm;
import utilpc.Algorithm.SRResult;
import utilpc.UtilPC;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ImgCutEditPage extends DefaultPage implements AbEditPage {

	private static final long serialVersionUID = 1L;

	private final JTF name = new JTF();
	private final JTF resz = new JTF("resize to: _%");
	private final JBTN add = new JBTN(0, "add");
	private final JBTN rem = new JBTN(0, "rem");
	private final JBTN copy = new JBTN(0, "copy");
	private final JBTN addl = new JBTN(0, "addl");
	private final JBTN reml = new JBTN(0, "reml");
	private final JBTN relo = new JBTN(0, "relo");
	private final JBTN impt = new JBTN(0, "import");
	private final JBTN expt = new JBTN(0, "export");
	private final JBTN ico = new JBTN(0, "icondi");
	private final JBTN loca = new JBTN(0, "localize");
	private final JBTN merg = new JBTN(0, "merge");
	private final JBTN spri = new JBTN(0, "sprite");
	private final JBTN white = new JBTN(0, "whiteBG");
	private final JLabel edi = new JLabel();
	private final JLabel uni = new JLabel();
	private final JLabel prev = new JLabel();
	private final JTree jta = new JTree();
	private final AnimGroupTree agt;
	private final JScrollPane jspu = new JScrollPane(jta);
	private final ImgCutEditTable icet = new ImgCutEditTable();
	private final JScrollPane jspic = new JScrollPane(icet);
	private final SpriteBox sb = new SpriteBox(this);
	private final EditHead aep;

	private SpriteEditPage sep;

	private boolean changing = false;

	public ImgCutEditPage(Page p) {
		super(p);
		aep = new EditHead(this, 1);
		agt = new AnimGroupTree(jta);
		preIni();
	}

	public ImgCutEditPage(Page p, EditHead bar) {
		super(p);
		aep = bar;
		agt = new AnimGroupTree(jta);
		preIni();
	}

	public ImgCutEditPage(Page p, AnimCE anim) {
		this(p);
		setSelection(anim);
	}

	@Override
	public void callBack(Object o) {
		changing = true;
		if (o instanceof SpriteBox && sb.sele >= 0) {
			icet.getSelectionModel().setSelectionInterval(sb.sele, sb.sele);
			int h = icet.getRowHeight();
			icet.scrollRectToVisible(new Rectangle(0, h * sb.sele, 1, h));
		} else
			icet.clearSelection();
		setB();
		changing = false;
	}

	@Override
	public void setSelection(AnimCE ac) {
		changing = true;
		DefaultMutableTreeNode selectedNode = agt.findAnimNode(ac, null);

		if(selectedNode == null) {
			changing = false;
			return;
		}
		agt.expandCurrentAnimNode(selectedNode);
		TreePath path = new TreePath(selectedNode.getPath());
		jta.setSelectionPath(path);
		jta.scrollPathToVisible(path);

		setA(ac);
		changing = false;
	}

	@Override
	protected void renew() {
		if (sep != null && Opts.conf("Do you want to save edited sprite?")) {
			icet.anim.setNum(MainBCU.builder.build(sep.getEdit()));
			icet.anim.saveImg();
			icet.anim.reloImg();
		}
		sep = null;
	}

	@Override
	public synchronized void onTimer(int t) {
		super.onTimer(t);
		sb.paint(sb.getGraphics());
		SwingUtilities.invokeLater(() -> jta.setUI(new TreeNodeExpander(jta)));
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(aep, x, y, 800, 0, 1750, 50);
		set(relo, x, y, 250, 0, 200, 50);
		set(jspu, x, y, 50, 100, 300, 450);
		set(name, x, y, 400, 100, 200, 50);
		set(copy, x, y, 650, 100, 200, 50);
		set(add, x, y, 400, 150, 200, 50);
		set(rem, x, y, 650, 150, 200, 50);
		set(impt, x, y, 400, 200, 200, 50);
		set(expt, x, y, 650, 200, 200, 50);
		set(resz, x, y, 400, 250, 200, 50);
		set(loca, x, y, 650, 250, 200, 50);
		set(merg, x, y, 400, 300, 200, 50);
		set(spri, x, y, 650, 300, 200, 50);
		set(addl, x, y, 400, 350, 200, 50);
		set(reml, x, y, 650, 350, 200, 50);
		set(ico, x, y, 400, 400, 200, 50);
		set(white, x, y, 650, 400, 200, 50);
		set(edi, x, y, 400, 450, 150, 150);
		set(uni, x, y, 550, 450, 150, 150);
		set(prev, x, y, 700, 450, 150, 150);
		set(jspic, x, y, 50, 600, 800, 650);
		set(sb, x, y, 900, 100, 1400, 1150);

		aep.componentResized(x, y);
		icet.setRowHeight(size(x, y, 50));
	}

	private void selectAnimNode(AnimCE ac) {
		DefaultMutableTreeNode selectedNode = agt.findAnimNode(ac, null);

		if(selectedNode != null) {
			agt.expandCurrentAnimNode(selectedNode);
			jta.setSelectionPath(new TreePath(selectedNode.getPath()));
		}
	}

	private void addListeners$0() {
		add.addActionListener(arg0 -> {
			BufferedImage bimg = new Importer("Add your sprite", Importer.IMP_IMG).getImg();
			addAnimation(bimg);
		});

		impt.addActionListener(arg0 -> {
			BufferedImage bimg = new Importer("Update your sprite", Importer.IMP_IMG).getImg();
			if (bimg != null) {
				AnimCE ac = icet.anim;
				ac.setNum(MainBCU.builder.build(bimg));
				ac.saveImg();
				ac.reloImg();
			}
		});

		expt.addActionListener(arg0 -> new Exporter((BufferedImage) icet.anim.getNum().bimg(), Exporter.EXP_IMG));

		jta.addTreeSelectionListener(arg0 -> {
			if (changing)
				return;
			changing = true;
			TreePath path = jta.getSelectionPath();

			if(path != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

				if(node.getUserObject() instanceof AnimCE) {
					setA((AnimCE) node.getUserObject());
				}
			}

			changing = false;

		});

		name.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				changing = true;
				String str = CommonStatic.verifyFileName(name.getText().trim());
				if (str.isEmpty() || icet.anim == null || icet.anim.id.id.equals(str)) {
					if (icet.anim != null)
						name.setText(icet.anim.id.id);
					changing = false;
					return;
				}
				AnimCE rep = AnimCE.map().get(str);
				if (rep != null) {
					icet.anim.renameTo(str);
					for (UserPack pack : UserProfile.getUserPacks())
						for (Enemy e : pack.enemies.getList())
							if (e.anim == rep)
								e.anim = icet.anim;
					agt.renewNodes();
					selectAnimNode(icet.anim);
					setA(icet.anim);
				} else {
					str = AnimCE.getAvailable(str, icet.anim.id.base);
					icet.anim.renameTo(str);
					name.setText(str);
				}
				changing = false;
			}
		});

		copy.addActionListener(arg0 -> {
			changing = true;
			ResourceLocation rl = new ResourceLocation(ResourceLocation.LOCAL, icet.anim.id.id, icet.anim.id.base);
			Workspace.validate(rl);
			AnimCE ac = new AnimCE(rl, icet.anim);
			ac.setEdi(icet.anim.getEdi());
			ac.setUni(icet.anim.getUni());
			ac.setPreview(icet.anim.getPreviewIcon());
			agt.renewNodes();
			selectAnimNode(ac);
			setA(ac);
			changing = false;
		});

		rem.setLnr(x -> {
			if (!Opts.conf())
				return;
			changing = true;
			AnimCE ac = icet.anim;
			ac.delete();
			agt.renewNodes();

			DefaultMutableTreeNode leftNode = agt.selectVeryFirstBaseNodeOr();

			if(leftNode != null) {
				agt.expandCurrentAnimNode(leftNode);
				jta.setSelectionPath(new TreePath(leftNode.getPath()));
			}

			setA(leftNode == null ? null : (AnimCE) leftNode.getUserObject());

			changing = false;
		}

		);

		loca.setLnr(x -> {
			if (!Opts.conf())
				return;
			changing = true;
			AnimCE ac = icet.anim;
			ac.localize();
			agt.renewNodes();

			DefaultMutableTreeNode leftNode = agt.selectVeryFirstBaseNodeOr();

			if(leftNode != null) {
				agt.expandCurrentAnimNode(leftNode);
				jta.setSelectionPath(new TreePath(leftNode.getPath()));
			}

			setA(leftNode == null ? null : (AnimCE) leftNode.getUserObject());

			changing = false;
		}

		);

		relo.addActionListener(arg0 -> {
			if (icet.anim == null)
				return;
			icet.anim.reloImg();
			icet.anim.ICedited();
		});

		ico.addActionListener(arg0 -> {
			BufferedImage bimg = new Importer("select icon image", Importer.IMP_IMG).getImg();
			int selection = Opts.selection("What icon is this for?",
					"Select Icon Type",
					"Display icon",
					"Deploy icon",
					"Preview icon");
			setIcon(bimg, selection);
		});

		white.setLnr(e -> {
			if(!sb.isAnimValid())
				return;

			white.setText(MainLocale.getLoc(MainLocale.PAGE, sb.white ? "blackBG" : "whiteBG"));

			sb.white = !sb.white;
		});
	}

	private void addListeners$1() {

		ListSelectionModel lsm = icet.getSelectionModel();

		lsm.addListSelectionListener(arg0 -> {
			if (changing || lsm.getValueIsAdjusting())
				return;
			changing = true;
			setB();
			changing = false;
		});

		addl.addActionListener(arg0 -> {
			changing = true;

			ImgCut ic = icet.anim.imgcut;

			int[][] data = ic.cuts;
			String[] name = ic.strs;

			ic.cuts = new int[++ic.n][];
			ic.strs = new String[ic.n];

			for (int i = 0; i < data.length; i++) {
				ic.cuts[i] = data[i];
				ic.strs[i] = name[i];
			}

			int ind = icet.getSelectedRow();

			if (ind >= 0)
				ic.cuts[ic.n - 1] = ic.cuts[ind].clone();
			else
				ic.cuts[ic.n - 1] = new int[] { 0, 0, 1, 1 };

			ic.strs[ic.n - 1] = "";

			icet.anim.unSave("imgcut add line");
			lsm.setSelectionInterval(ic.n - 1, ic.n - 1);

			int h = icet.getRowHeight();

			icet.scrollRectToVisible(new Rectangle(0, h * (ic.n - 1), 1, h));

			setB();

			changing = false;
		});

		reml.addActionListener(arg0 -> {
			changing = true;

			ImgCut ic = icet.anim.imgcut;

			int ind = sb.sele;
			int[][] data = ic.cuts;

			String[] name = ic.strs;

			ic.cuts = new int[--ic.n][];
			ic.strs = new String[ic.n];

			for (int i = 0; i < ind; i++) {
				ic.cuts[i] = data[i];
				ic.strs[i] = name[i];
			}

			for (int i = ind + 1; i < data.length; i++) {
				ic.cuts[i - 1] = data[i];
				ic.strs[i - 1] = name[i];
			}

			for (int[] ints : icet.anim.mamodel.parts)
				if (ints[2] > ind)
					ints[2]--;

			for (MaAnim ma : icet.anim.anims)
				for (Part part : ma.parts)
					if (part.ints[1] == 2)
						for (int[] ints : part.moves)
							if (ints[1] > ind)
								ints[1]--;

			icet.anim.ICedited();
			icet.anim.unSave("imgcut remove line");

			if (ind >= ic.n)
				ind--;

			lsm.setSelectionInterval(ind, ind);

			setB();

			changing = false;
		});

		resz.setLnr(x -> {
			double d = CommonStatic.parseIntN(resz.getText()) * 0.01;
			if (d > 0 && Opts.conf("do you want to resize sprite to " + d + "%?")) {
				icet.anim.resize(d);
				icet.anim.ICedited();
				icet.anim.unSave("resized");
			}
			resz.setText("resize to: _%");
		});

		merg.addActionListener(e -> {
			changing = true;

			ResourceLocation rl = new ResourceLocation(ResourceLocation.LOCAL, "merged", icet.anim.id.base);
			Workspace.validate(rl);

			TreePath[] paths = jta.getSelectionPaths();

			if(paths == null)
				return;

			ArrayList<AnimCE> anims = new ArrayList<>();

			//validation
			for(TreePath path : paths) {
				if(!(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
					return;

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

				if(!(node.getUserObject() instanceof AnimCE))
					return;

				anims.add((AnimCE) node.getUserObject());
			}

			AnimCE[] list = anims.toArray(new AnimCE[0]);
			int[][] rect = new int[list.length][2];
			for (int i = 0; i < list.length; i++) {
				rect[i][0] = list[i].getNum().getWidth();
				rect[i][1] = list[i].getNum().getHeight();
			}
			SRResult ans = Algorithm.stackRect(rect);
			AnimCE cen = list[ans.center];
			AnimCE ac = new AnimCE(rl, cen);
			BufferedImage bimg = new BufferedImage(ans.w, ans.h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bimg.getGraphics();
			for (int i = 0; i < list.length; i++) {
				BufferedImage b = (BufferedImage) list[i].getNum().bimg();
				int x = ans.pos[i][0];
				int y = ans.pos[i][1];
				g.drawImage(b, x, y, null);
				if (i != ans.center)
					ac.merge(list[i], x, y);
			}
			ac.setNum(MainBCU.builder.build(bimg));
			ac.saveImg();
			ac.reloImg();
			ac.unSave("merge");
			agt.renewNodes();
			selectAnimNode(ac);
			setA(ac);
			changing = false;
		});

		spri.setLnr(x -> changePanel(sep = new SpriteEditPage(this, (BufferedImage) icet.anim.getNum().bimg())));
	}

	private void preIni() {
		AnimGroup.workspaceGroup.renewGroup();
		agt.renewNodes();

		ini();
	}

	@Override
	protected void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		aep.hotkey(e);
	}

	private void ini() {
		add(aep);
		add(resz);
		add(relo);
		add(jspu);
		add(jspic);
		add(add);
		add(rem);
		add(copy);
		add(addl);
		add(reml);
		add(name);
		add(sb);
		add(impt);
		add(expt);
		add(loca);
		add(ico);
		add(merg);
		add(spri);
		add(white);
		add(uni);
		uni.setVerticalAlignment(SwingConstants.CENTER);
		uni.setHorizontalAlignment(SwingConstants.CENTER);
		add(edi);
		edi.setVerticalAlignment(SwingConstants.CENTER);
		edi.setHorizontalAlignment(SwingConstants.CENTER);
		add(prev);
		prev.setVerticalAlignment(SwingConstants.CENTER);
		prev.setHorizontalAlignment(SwingConstants.CENTER);
		jta.setCellRenderer(new AnimTreeRenderer());
		SwingUtilities.invokeLater(() -> jta.setUI(new TreeNodeExpander(jta)));
		setA(null);
		addListeners$0();
		addListeners$1();
		setDrops();
	}

	private void setDrops() {
		setDropTarget(new DropParser() {
			@Override
			public boolean process(File f) {
				return addAnimation(getImg());
			}
		});
		edi.setDropTarget(new DropParser() {
			@Override
			public boolean process(File f) {
				if (icet.anim == null)
					return addAnimation(getImg());
				return setIcon(getImg(), 0);
			}
		});
		uni.setDropTarget(new DropParser() {
			@Override
			public boolean process(File f) {
				if (icet.anim == null)
					return addAnimation(getImg());
				return setIcon(getImg(), 1);
			}
		});
		prev.setDropTarget(new DropParser() {
			@Override
			public boolean process(File f) {
				if (icet.anim == null)
					return addAnimation(getImg());
				return setIcon(getImg(), 2);
			}
		});
	}

	private boolean addAnimation(BufferedImage bimg) {
		if (bimg == null)
			return false;
		int selection = Opts.selection("What kind of animation do you want to create?",
				"Select Animation Type",
				"Unit/Enemy",
				"Soul",
				"Background Effect");
		if (selection == -1)
			return false;
		changing = true;
		ResourceLocation rl;
		if (selection == 2)
			rl = new ResourceLocation(ResourceLocation.LOCAL, "new bgeffect anim", Source.BasePath.BGEffect);
		else if (selection == 1)
			rl = new ResourceLocation(ResourceLocation.LOCAL, "new soul anim", Source.BasePath.SOUL);
		else
			rl = new ResourceLocation(ResourceLocation.LOCAL, "new anim", Source.BasePath.ANIM);
		Workspace.validate(rl);
		Source.warn = false;
		AnimCE ac = new AnimCE(rl);
		Source.warn = true;
		ac.setNum(MainBCU.builder.build(bimg));
		ac.saveImg();
		ac.createNew();
		AnimCE.map().put(rl.id, ac);
		AnimGroup.workspaceGroup.renewGroup();
		agt.renewNodes();
		selectAnimNode(ac);
		setA(ac);
		changing = false;
		return true;
	}

	private boolean setIcon(BufferedImage bimg, int selection) {
		if (selection == 0) {
			icet.anim.setEdi(MainBCU.builder.toVImg(bimg));
			icet.anim.saveIcon();
			edi.setIcon(icet.anim.getEdi() == null ? null : UtilPC.getIcon(icet.anim.getEdi()));
		} else if (selection == 1) {
			icet.anim.setUni(MainBCU.builder.toVImg(bimg == null ? (BufferedImage)CommonStatic.getBCAssets().slot[0].getImg().bimg() : bimg));
			icet.anim.saveUni();
			//If it's null, it will get the default
			uni.setIcon(UtilPC.getIcon(icet.anim.getUni()));
		} else if (selection == 2) {
			if (bimg.getWidth() != bimg.getHeight())
				return false;
			bimg = UtilPC.resizeImage(bimg, 72, 72);
			icet.anim.setPreview(MainBCU.builder.toVImg(bimg));
			icet.anim.savePreview();
			prev.setIcon(UtilPC.getIcon(icet.anim.getPreviewIcon() == null ? null : icet.anim.getPreviewIcon()));
		}
		return selection >= 0 && selection <= 2;
	}

	private void setA(AnimCE anim) {
		boolean boo = changing;
		if(anim != null) {
			anim.check();
		}
		changing = true;
		aep.setAnim(anim);
		addl.setEnabled(anim != null);
		resz.setEnabled(anim != null);
		relo.setEnabled(anim != null);
		white.setEnabled(anim != null);
		icet.setCut(anim);
		sb.setAnim(anim);
		if (sb.sele == -1)
			icet.clearSelection();
		sb.setEnabled(anim != null);
		name.setEnabled(anim != null);
		name.setText(anim == null ? "" : anim.id.id);
		boolean del = anim != null && anim.deletable();
		rem.setEnabled(anim != null && del);
		loca.setEnabled(anim != null && !del && anim.inPool());
		copy.setEnabled(anim != null);
		impt.setEnabled(anim != null);
		expt.setEnabled(anim != null);
		spri.setEnabled(anim != null);
		ico.setEnabled(anim != null);

		boolean mergeEnabled = true;

		TreePath[] paths = jta.getSelectionPaths();

		if(paths == null)
			mergeEnabled = false;
		else {
			for(TreePath path : paths) {
				if(!(path.getLastPathComponent() instanceof DefaultMutableTreeNode)) {
					mergeEnabled = false;
					break;
				}

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

				if(!(node.getUserObject() instanceof AnimCE)) {
					mergeEnabled = false;
					break;
				}
			}
		}

		merg.setEnabled(mergeEnabled);
		if (anim != null) {
			uni.setIcon(UtilPC.getIcon(anim.getUni()));
			edi.setIcon(anim.getEdi() == null ? null : UtilPC.getIcon(anim.getEdi()));
			prev.setIcon(anim.getPreviewIcon() == null ? null : UtilPC.getIcon(anim.getPreviewIcon()));
		}
		setB();
		changing = boo;
	}

	private void setB() {
		sb.sele = icet.getSelectedRow();

		reml.setEnabled(sb.sele != -1);
		if (sb.sele >= 0) {
			for (int[] ints : icet.anim.mamodel.parts)
				if (ints[2] == sb.sele)
					reml.setEnabled(false);
			for (MaAnim ma : icet.anim.anims)
				for (Part part : ma.parts)
					if (part.ints[1] == 2)
						for (int[] ints : part.moves)
							if (ints[1] == sb.sele)
								reml.setEnabled(false);
		}
	}
}