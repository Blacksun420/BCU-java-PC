package page.anim;

import common.system.VImg;
import common.util.AnimGroup;
import common.util.anim.AnimCE;
import main.MainBCU;
import main.Opts;
import page.JBTN;
import page.JTG;
import page.MainLocale;
import page.Page;
import page.awt.BBBuilder;
import page.support.AnimTreeRenderer;
import page.support.AnimTreeTransfer;
import page.support.TreeNodeExpander;
import page.view.AbViewPage;
import utilpc.UtilPC;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class DIYViewPage extends AbViewPage implements AbEditPage {

	private static final long serialVersionUID = 1L;

	private static final String[] icos = new String[] { "default", "starred", "soul", "EF", "TF", "ZF", "uni_f", "uni_c", "uni_s", "uni_u" };

	private final JTree jlt = new JTree();
	private final JScrollPane jspu = new JScrollPane(jlt);
	private final JBTN group = new JBTN(MainLocale.PAGE, "addgroup");
	private final JBTN remgroup = new JBTN(MainLocale.PAGE, "remgroup");
	private final EditHead aep;
	private final AnimGroupTree agt;

	private final JTG ics = new JTG(0, "icon");
	private final JBTN icc = new JBTN(0, "confirm");
	private final JComboBox<String> jcb = new JComboBox<>(icos);
	private final JLabel uni = new JLabel();
	private final IconBox ib;

	public DIYViewPage(Page p) {
		super(p, BBBuilder.def.getIconBox());
		ib = (IconBox) vb;
		aep = new EditHead(this, 0);
		agt = new AnimGroupTree(jlt);
		preIni();
		ini();
	}

	public DIYViewPage(Page p, AnimCE ac) {
		this(p);
		setSelection(ac);
	}

	public DIYViewPage(Page p, EditHead bar) {
		super(p, BBBuilder.def.getIconBox());
		ib = (IconBox) vb;
		aep = bar;
		agt = new AnimGroupTree(jlt);
		ini();
	}

	@Override
	public void setSelection(AnimCE ac) {
		DefaultMutableTreeNode selectedNode = agt.findAnimNode(ac, null);
		if(selectedNode == null)
			return;

		agt.expandCurrentAnimNode(selectedNode);
		TreePath path = new TreePath(selectedNode.getPath());
		jlt.setSelectionPath(path);
		jlt.scrollPathToVisible(path);
		remgroup.setEnabled(false);
	}

	@Override
	protected void enabler(boolean b) {
		super.enabler(b);
		ics.setEnabled(ics.isSelected() || b && pause);
		icc.setEnabled(ics.isSelected());
		jlt.setEnabled(b);
	}

	@Override
	protected void keyPressed(KeyEvent ke) {
		super.keyPressed(ke);

		if(ke.getSource() == jlt)
			if(ke.getKeyCode() == KeyEvent.VK_DELETE)
				if(remgroup.isEnabled()) {
					TreePath[] paths = jlt.getSelectionPaths();
					if(paths == null)
						return;
					if(Opts.conf(get(MainLocale.PAGE, "remgroupconf"))) {
						ArrayList<String> groups = new ArrayList<>();
						for(TreePath path : paths) {
							if(path == null || !(path.getLastPathComponent() instanceof DefaultMutableTreeNode) || !(((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof String))
								return;
							if(((DefaultMutableTreeNode) path.getLastPathComponent()).isRoot())
								return;

							String groupName = (String) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
							if(groupName.isEmpty())
								return;
							groups.add(groupName);
						}
						for(String groupName : groups)
							agt.removeGroup(groupName);
						remgroup.setEnabled(false);
					}
				}
	}

	private void preIni() {
		AnimGroup.workspaceGroup.renewGroup();
		agt.renewNodes();
	}

	@Override
	protected void renew() {
		TreePath path = jlt.getSelectionPath();
		preIni();
		if (path != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject() instanceof AnimCE) {
			setSelection((AnimCE)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject());
			return;
		}
		DefaultMutableTreeNode firstNode = agt.getVeryFirstAnimNode();
		if (firstNode != null) {
			agt.expandCurrentAnimNode(firstNode);
			path = new TreePath(firstNode);
			jlt.setSelectionPath(path);
			jlt.scrollPathToVisible(path);
		}
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(camres, x ,y, 400, 0, 200, 50);
		set(copy, x, y, 200, 0, 200, 50);
		set(larges, x, y , 600, 0, 200, 50);
		set(aep, x, y, 800, 0, 1500, 50);
		if (!larges.isSelected()) {
			set(jspu, x, y, 50, 100, 300, 1050);
			set(ics, x, y, 1000, 1050, 200, 50);
			set(uni, x, y, 750, 500, 200, 200);
			set(jcb, x, y, 750, 750, 200, 50);
			set(icc, x, y, 1000, 1150, 200, 50);
			set(group, x, y, 50, 1150, 300, 50);
			set(remgroup, x, y, 50, 1200, 300, 50);
		} else {
			set(uni, x, y, 200, 800, 200, 150);
			set(jcb, x, y, 150, 950, 200, 50);
			set(ics, x, y, 50, 1200, 200, 50);
			set(icc, x, y, 250, 1200, 200, 50);
			set(jspu, x, y, 50, 1000, 400, 200);
			set(group, x, y, 0, 0, 0, 0);
			set(remgroup, x, y, 0, 0, 0, 0);
		}
	}

	@Override
	public synchronized void onTimer(int t) {
		super.onTimer(t);
		SwingUtilities.invokeLater(() -> jlt.setUI(new TreeNodeExpander(jlt)));
		ib.updateControllerDimension(((Canvas) vb).getWidth(), ((Canvas) vb).getHeight());
	}

	@Override
	protected void updateChoice() {
		TreePath path = jlt.getSelectionPath();
		if(path == null)
			return;
		Object o = path.getLastPathComponent();
		if(!(o instanceof DefaultMutableTreeNode))
			return;

		if(((DefaultMutableTreeNode) o).getUserObject() instanceof AnimCE) {
			AnimCE e = (AnimCE)((DefaultMutableTreeNode) o).getUserObject();
			aep.setAnim(e);
			uni.setIcon(e == null ? null : UtilPC.getIcon(e.getUni()));
			if (e == null)
				return;
			setAnim(e);
		}
	}

	private void addListeners() {
		defCopyListener();

		jlt.addTreeSelectionListener(t -> {
			TreePath[] paths = jlt.getSelectionPaths();

			if(paths == null)
				return;
			boolean canEnabled = true;
			for(TreePath path : paths) {
				if(!(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
					return;

				if(((DefaultMutableTreeNode) path.getLastPathComponent()).isRoot() || !(((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof String)) {
					canEnabled = false;
					break;
				}
			}
			remgroup.setEnabled(canEnabled);
			updateChoice();
		});

		remgroup.setLnr(a -> {
			TreePath[] paths = jlt.getSelectionPaths();
			if(paths == null)
				return;

			if(Opts.conf(get(MainLocale.PAGE, "remgroupconf"))) {
				ArrayList<String> groups = new ArrayList<>();

				for(TreePath path : paths) {
					if(path == null || !(path.getLastPathComponent() instanceof DefaultMutableTreeNode) || !(((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof String)
							|| ((DefaultMutableTreeNode) path.getLastPathComponent()).isRoot())
						return;

					String groupName = (String) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					if(groupName.isEmpty())
						return;
					groups.add(groupName);
				}
				for(String groupName : groups)
					agt.removeGroup(groupName);

				remgroup.setEnabled(false);
			}
		});

		ics.addActionListener(arg0 -> {
			enabler(!ics.isSelected());
			ib.setBlank(ics.isSelected());
		});

		jcb.addActionListener(arg0 -> {
			int t = jcb.getSelectedIndex();
			IconBox.IBConf.mode = t / 6; // [ Def, Str, Sou,
			IconBox.IBConf.type = t % 6; // [ 0,   1,   2,  3, 4, 5, 6, 7]
			IconBox.IBConf.glow = t >= 3 && t != 6;
			ib.changeType();
		});

		icc.setLnr(x -> {
			VImg clip = MainBCU.builder.toVImg(ib.getClip());
			if (IconBox.IBConf.mode == 0
					&& Opts.conf("are you sure to replace display icon? This action cannot be undone")) {
				AnimCE ac = aep.anim;
				ac.setEdi(clip);
				ac.saveIcon();
				jlt.repaint();
			}
			if (IconBox.IBConf.mode == 1
					&& Opts.conf("are you sure to replace battle icon? This action cannot be undone")) {
				AnimCE ac = aep.anim;
				ac.setUni(MainBCU.builder.toVImg(ib.getClip()));
				ac.saveUni();
				uni.setIcon(UtilPC.getIcon(ac.getUni()));
			}
		});

		group.setLnr(a -> {
			String groupName = Opts.read(get(MainLocale.PAGE, "addgrouppop"));

			if(groupName == null)
				return;

			groupName = AnimGroup.workspaceGroup.validateGroupName(groupName);

			AnimGroup.workspaceGroup.groups.put(groupName, new ArrayList<>());

			renew();
		});
	}

	private void ini() {
		preini();
		add(aep);
		add(jspu);
		add(ics);
		add(icc);
		add(jcb);
		add(uni);
		add(group);
		add(remgroup);
		jcb.setSelectedIndex(IconBox.IBConf.type);
		ics.setEnabled(false);
		icc.setEnabled(false);
		jlt.setCellRenderer(new AnimTreeRenderer());
		SwingUtilities.invokeLater(() -> jlt.setUI(new TreeNodeExpander(jlt)));
		jlt.setTransferHandler(new AnimTreeTransfer(agt));
		jlt.setDragEnabled(true);
		jlt.addTreeExpansionListener(agt);
		jlt.setDropMode(DropMode.ON_OR_INSERT);
		remgroup.setEnabled(false);
		addListeners();
	}

}