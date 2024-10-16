package page.info.filter;

import common.CommonStatic;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.unit.Trait;
import utilpc.Interpret;
import utilpc.Theme;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

import static common.util.Data.*;

public class TraitList extends JList<Trait> {

    private static final long serialVersionUID = 1L;
    public final Vector<Trait> list = new Vector<>();
    public final boolean id;

    //editing is used to set whether the page using this is Trait Edit Page or not. May be used to add BC Traits to list at some point
    public TraitList(boolean editing) {
        id = editing;

        setSelectionBackground(Theme.DARK.NIMBUS_SELECT_BG);

        setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
                JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
                Trait trait = (Trait)o;
                if (trait.BCTrait()) {
                    jl.setText(Interpret.TRAIT[trait.id.id]);
                    jl.setIcon(UtilPC.getIcon(3, trait.id.id));
                } else {
                    if (!editing)
                        jl.setText(trait.name);
                    if (trait.icon != null)
                        jl.setIcon(UtilPC.getIcon(trait.icon));
                    else
                        jl.setIcon(UtilPC.getIcon(CommonStatic.getBCAssets().dummyTrait));
                }
                return jl;
            }
        });
    }

    public void setup(UserPack pack, boolean allTraits) {
        list.addAll(UserProfile.getBCData().traits.getList().subList(TRAIT_RED,allTraits ? TRAIT_TOT : TRAIT_EVA));
        if (pack == null)
            return;

        list.addAll(pack.traits.getList());
        for (String dep : pack.desc.dependency)
            list.addAll(UserProfile.getUserPack(dep).traits.getList());
        setListData();
    }

    public void setListData() {
        setListData(list);
    }
}