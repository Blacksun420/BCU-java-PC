package page.pack;

import common.pack.Identifier;
import common.util.Data;
import common.util.anim.AnimCI;
import common.util.anim.AnimD;
import common.util.pack.Soul;
import common.util.unit.*;
import kotlin.Pair;
import page.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class PackValidationPage extends DefaultPage {
    private static final long serialVersionUID = 1L;

    private final List<Pair<Object, List<String>>> data;

    private final JL animationTitle = new JL();

    private final JList<String> fileNames = new JList<>();
    private final JScrollPane fileScroll = new JScrollPane(fileNames);

    private final JList<Object> list = new JList<>();
    private final JScrollPane scroll = new JScrollPane(list);

    protected PackValidationPage(Page p, List<Pair<Object, List<String>>> data) {
        super(p);
        this.data = data;

        initialize();
    }

    @Override
    protected void resized(int x, int y) {
        super.resized(x, y);

        set(scroll, x, y, 50, 100, 300, 1100);
        set(animationTitle, x, y, 400, 100, 600, 50);
        set(fileScroll, x, y, 400, 150, 600, 1050);
    }

    private void initialize() {
        add(scroll);
        add(animationTitle);
        add(fileScroll);

        Vector<Object> containerData = new Vector<>();

        for (Pair<Object, List<String>> datum : data) {
            containerData.add(datum.getFirst());
        }

        list.setListData(containerData);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (!(component instanceof JLabel))
                    return component;

                JLabel label = (JLabel) component;

                if (value instanceof Form) {
                    String name = ((Form) value).names.toString();

                    if (name.isEmpty() && ((Form) value).unit != null) {
                        Identifier<AbUnit> id = ((Form) value).unit.id;

                        name = id.pack + " - " + Data.trio(id.id) + " - " + Data.trio(((Form) value).fid);
                    }

                    name += " [UNIT]";

                    label.setText(name);
                } else if (value instanceof Enemy) {
                    String name = ((Enemy) value).names.toString();

                    if (name.isEmpty()) {
                        Identifier<AbEnemy> id = ((Enemy) value).id;

                        name = id.pack + " - " + Data.trio(id.id);
                    }

                    name += " [ENEMY]";

                    label.setText(name);
                } else if (value instanceof Soul) {
                    String name = ((Soul) value).name;

                    if (name == null || name.isEmpty()) {
                        Identifier<Soul> id = ((Soul) value).getID();

                        name = id.pack + " - " + Data.trio(id.id);
                    }

                    name += " [SOUL]";

                    label.setText(name);
                }

                return label;
            }
        });

        list.addListSelectionListener(event -> setPair(findPair(list.getSelectedValue())));
    }

    private void setPair(Pair<Object, List<String>> pair) {
        if (pair == null) {
            fileNames.setListData(new Vector<>());
            animationTitle.setText("");

            fileScroll.setVisible(false);
            animationTitle.setVisible(false);
        } else {
            fileNames.setListData(pair.getSecond().toArray(new String[0]));

            String animationName;

            Object key = pair.getFirst();

            if (key instanceof Form) {
                AnimD<?, ?> animation = ((Form) key).anim;

                if (animation instanceof AnimCI) {
                    animationName = ((AnimCI) animation).id.getPath();
                } else {
                    animationName = "UNNAMED";
                }
            } else if (key instanceof Enemy) {
                AnimD<?, ?> animation = ((Enemy) key).anim;

                if (animation instanceof AnimCI) {
                    animationName = ((AnimCI) animation).id.getPath();
                } else {
                    animationName = "UNNAMED";
                }
            } else if (key instanceof Soul) {
                AnimD<?, ?> animation = ((Soul) key).anim;

                if (animation instanceof AnimCI) {
                    animationName = ((AnimCI) animation).id.getPath();
                } else {
                    animationName = "UNNAMED";
                }
            } else {
                animationName = "UNKNOWN TYPE";
            }

            animationTitle.setText(animationName);
        }

        fireDimensionChanged();
    }

    private Pair<Object, List<String>> findPair(Object key) {
        if (key == null)
            return null;

        for (Pair<Object, List<String>> datum : data) {
            if (datum.getFirst().equals(key))
                return datum;
        }

        return null;
    }
}
