package page.anim;

import common.pack.PackData;
import common.pack.Source;
import common.pack.UserProfile;
import common.util.AnimGroup;
import common.util.anim.AnimCE;
import common.util.anim.AnimCI;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;

public class AnimGroupTree implements TreeExpansionListener {
    private final JTree animTree;
    private final HashMap<String, Boolean> groupExpanded = new HashMap<>();

    private DefaultMutableTreeNode nodes = new DefaultMutableTreeNode("Animation");
    private final Source.BasePath required;

    public AnimGroupTree(JTree animTree) {
        this(animTree, null);
    }

    public AnimGroupTree(JTree animTree, Source.BasePath basePath) {
        this.animTree = animTree;
        required = basePath;
    }

    public void renewNodes() {
        nodes.removeAllChildren();

        addPathNodes(required, true);
        if (required == null) {
            for (PackData.UserPack pack : UserProfile.getUserPacks())
                if (pack.editable) {
                    DefaultMutableTreeNode container = new DefaultMutableTreeNode(pack.getSID());
                    for (AnimCI anim : pack.source.getAnims(Source.BasePath.ANIM))
                        container.add(new DefaultMutableTreeNode(anim));
                    for (AnimCI anim : pack.source.getAnims(Source.BasePath.SOUL))
                        container.add(new DefaultMutableTreeNode(anim));
                    for (AnimCI anim : pack.source.getAnims(Source.BasePath.BGEffect))
                        container.add(new DefaultMutableTreeNode(anim));
                    if (container.getChildCount() > 0)
                        addNode(container);
                }
        }
        animTree.setModel(new DefaultTreeModel(nodes));
        animTree.setRowHeight(0);
    }

    public void addPathNodes(Source.BasePath path, boolean init) {
        for(String key : AnimGroup.workspaceGroup.groups.keySet()) {
            if(key.equals(""))
                continue;
            DefaultMutableTreeNode container = new DefaultMutableTreeNode(key);;
            if (!init) {
                for (int i = 0; i < nodes.getChildCount(); i++)
                    if (((DefaultMutableTreeNode)nodes.getChildAt(i)).getUserObject().equals(key)) {
                        container = (DefaultMutableTreeNode)nodes.getChildAt(i);
                        break;
                    }
            }
            ArrayList<AnimCE> anims = AnimGroup.workspaceGroup.groups.get(key);
            if(anims == null)
                continue;

            for(AnimCE anim : anims)
                if (path == null || anim.id.base.equals(path))
                    container.add(new DefaultMutableTreeNode(anim));
            if (init)
                nodes.add(container);
        }
        ArrayList<AnimCE> baseGroup = AnimGroup.workspaceGroup.groups.get("");
        if(baseGroup != null && !baseGroup.isEmpty())
            for(AnimCE anim : baseGroup) {
                if (path != null && !anim.id.base.equals(path))
                    continue;
                DefaultMutableTreeNode animNode = new DefaultMutableTreeNode(anim);

                nodes.add(animNode);
            }
    }

    public void addNode(DefaultMutableTreeNode node) {
        for (int i = 0; i < nodes.getChildCount(); i++)
            if (((DefaultMutableTreeNode)nodes.getChildAt(i)).getUserObject().equals(node.getUserObject()))
                return;
        nodes.add(node);
    }

    public void removeNode(String id) {
        for (int i = 0; i < nodes.getChildCount(); i++)
            if (((DefaultMutableTreeNode)nodes.getChildAt(i)).getUserObject().equals(id))
                nodes.remove(i);
    }

    public void applyNewNodes() {
        if(!(animTree.getModel().getRoot() instanceof DefaultMutableTreeNode))
            return;

        nodes = (DefaultMutableTreeNode) animTree.getModel().getRoot();

        handleAnimGroup(nodes, true);

        renewNodes();

        Enumeration<?> enumeration = nodes.children();

        while(enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

            if(node.getUserObject() instanceof String) {
                if(groupExpanded.containsKey((String) node.getUserObject()) && groupExpanded.get((String) node.getUserObject())) {
                    animTree.expandPath(new TreePath(node.getPath()));
                }
            }
        }
    }

    public void handleAnimGroup(DefaultMutableTreeNode root, boolean initial) {
        if(initial) {
            AnimGroup.workspaceGroup.groups.clear();
            AnimGroup.workspaceGroup.groups.put("", new ArrayList<>());
        }

        Enumeration<?> enumeration = root.children();

        while(enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

            if(node.getUserObject() instanceof String) {
                if(node.getChildCount() > 0)
                    handleAnimGroup(node, false);
                else
                    AnimGroup.workspaceGroup.groups.put((String) node.getUserObject(), new ArrayList<>());
            } else if(node.getUserObject() instanceof AnimCE) {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

                if(!parent.isRoot()) {
                    if(parent.getUserObject() instanceof String) {
                        String id = (String) parent.getUserObject();

                        ArrayList<AnimCE> anim = AnimGroup.workspaceGroup.groups.get(id);

                        if(anim == null)
                            anim = new ArrayList<>();

                        AnimCE a = (AnimCE) node.getUserObject();

                        if(!anim.contains(a)) {
                            a.group = id;

                            anim.add(a);

                            AnimGroup.workspaceGroup.groups.put(id, anim);
                        }
                    } else {
                        throw new IllegalStateException("Parent node must be String : "+parent.getUserObject().getClass().getName());
                    }
                } else {
                    ArrayList<AnimCE> anim = AnimGroup.workspaceGroup.groups.get("");

                    AnimCE a = (AnimCE) node.getUserObject();

                    if(!anim.contains(a)) {
                        a.group = "";

                        anim.add(a);

                        AnimGroup.workspaceGroup.groups.put("", anim);
                    }
                }
            }
        }

        for(ArrayList<AnimCE> anims : AnimGroup.workspaceGroup.groups.values()) {
            anims.sort(Comparator.comparing(a -> a.id.id));
        }
    }

    public DefaultMutableTreeNode findAnimNode(AnimCE anim, DefaultMutableTreeNode nodes) {
        if(anim == null)
            return null;

        DefaultMutableTreeNode node;

        if(nodes == null)
            nodes = this.nodes;

        Enumeration<?> e = nodes.children();

        while(e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();

            if(node.getUserObject() instanceof String) {
                DefaultMutableTreeNode trial = findAnimNode(anim, node);

                if(trial != null)
                    return trial;
            } else if(node.getUserObject() instanceof AnimCE && node.getUserObject() == anim)
                return node;
        }

        return null;
    }

    public void removeGroup(String groupName) {
        if(groupName.equals(""))
            return;

        groupExpanded.remove(groupName);

        ArrayList<AnimCE> anims = AnimGroup.workspaceGroup.groups.get(groupName);

        if(anims == null)
            return;

        ArrayList<AnimCE> base = AnimGroup.workspaceGroup.groups.get("");

        for(AnimCE anim : anims) {
            anim.group = "";

            base.add(anim);
        }

        base.sort(Comparator.comparing(a -> a.id.id));

        AnimGroup.workspaceGroup.groups.remove(groupName);
        AnimGroup.workspaceGroup.groups.put("", base);

        renewNodes();
    }

    public DefaultMutableTreeNode getVeryFirstAnimNode() {
        DefaultMutableTreeNode node;

        Enumeration<?> e = nodes.breadthFirstEnumeration();

        while(e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();

            if(node.getUserObject() instanceof AnimCE)
                return node;
        }

        return null;
    }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

        if(node.getUserObject() instanceof String) {
            groupExpanded.put((String) node.getUserObject(), true);
        }
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

        if(node.getUserObject() instanceof String) {
            groupExpanded.put((String) node.getUserObject(), false);
        }
    }

    public DefaultMutableTreeNode selectVeryFirstBaseNodeOr() {
        Enumeration<?> enumeration = nodes.children();

        while(enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

            if(node.getUserObject() instanceof AnimCE) {
                return node;
            }
        }

        //no base anim node

        enumeration = nodes.children();

        while(enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();

            if(node.getUserObject() instanceof String && node.getChildCount() > 0) {

                return (DefaultMutableTreeNode) node.getChildAt(0);
            }
        }

        return null;
    }

    public void expandCurrentAnimNode(DefaultMutableTreeNode node) {
        if(!(node.getParent() instanceof DefaultMutableTreeNode))
            return;

        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

        if(parent.isRoot())
            return;

        if(!animTree.isExpanded(new TreePath(node.getPath()))) {
            animTree.expandPath(new TreePath(node.getPath()));
        }
    }
}
