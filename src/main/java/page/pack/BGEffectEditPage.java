package page.pack;

import common.CommonStatic;
import common.pack.PackData;
import common.pack.PackData.UserPack;
import common.pack.Source;
import common.pack.UserProfile;
import common.util.anim.AnimCE;
import common.util.pack.Background;
import common.util.pack.bgeffect.BackgroundEffect;
import common.util.pack.bgeffect.CustomBGEffect;
import common.util.pack.bgeffect.MixedBGEffect;
import main.Opts;
import page.JBTN;
import page.JTF;
import page.MainLocale;
import page.Page;
import page.support.AnimLCR;
import utilpc.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.stream.Collectors;

class EffectList extends JList<BackgroundEffect> {

    private static final long serialVersionUID = 1L;

    public EffectList() {

        setSelectionBackground(Theme.DARK.NIMBUS_SELECT_BG);

        setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
                JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
                BackgroundEffect effect = (BackgroundEffect)o;

                jl.setText(effect.toString());
                if (effect instanceof CustomBGEffect) {
                    CustomBGEffect ceff = (CustomBGEffect)effect;
                    if (ceff.anim.getEdi() != null)
                        jl.setIcon(new ImageIcon((BufferedImage)ceff.anim.getEdi().getImg().bimg()));
                } else if (effect instanceof MixedBGEffect) {
                    MixedBGEffect meff = (MixedBGEffect)effect;
                    for (BackgroundEffect eff : meff.effects)
                        if (eff instanceof CustomBGEffect && ((CustomBGEffect) eff).anim.getEdi() != null)
                            jl.setIcon(new ImageIcon((BufferedImage)((CustomBGEffect) eff).anim.getEdi().getImg().bimg()));
                }
                return jl;
            }
        });
    }
}

public class BGEffectEditPage extends Page {

    private static final long serialVersionUID = 1L;

    private final EffectList jlbg = new EffectList();
    private final JScrollPane jspbg = new JScrollPane(jlbg);
    private final JBTN back = new JBTN(MainLocale.PAGE, "back");
    private final JBTN addbg = new JBTN(MainLocale.PAGE, "add");
    private final JBTN rembg = new JBTN(MainLocale.PAGE, "rem");
    private final JTF bgena = new JTF();


    private final JBTN changebg = new JBTN(MainLocale.PAGE, "change");
    private final JTF bgsp = new JTF();
    private final JList<AnimCE> jld = new JList<>(new Vector<>(AnimCE.map().values().stream().filter(a -> a.id.base.equals(Source.BasePath.BGEffect)).collect(Collectors.toList())));
    private final JScrollPane jspd = new JScrollPane(jld);

    private final EffectList jlme = new EffectList(); //addable ones
    private final JScrollPane jspme = new JScrollPane(jlme);
    private final EffectList jlbe = new EffectList(); //ones in mixedbge
    private final JScrollPane jspbe = new JScrollPane(jlbe);
    private final JBTN addme = new JBTN(MainLocale.PAGE, "add");
    private final JBTN remme = new JBTN(MainLocale.PAGE, "rem");

    private final UserPack pack;
    private boolean changing = false;
    private final boolean editable;
    private BackgroundEffect bge;

    public BGEffectEditPage(Page p, UserPack up) {
        super(p);
        pack = up;
        editable = up.editable;
        ini();
    }

    @Override
    public JButton getBackButton() {
        return back;
    }

    private void addListeners() {
        back.setLnr(x -> changePanel(getFront()));

        jlbg.addListSelectionListener(x -> {
            if (changing || jlbg.getValueIsAdjusting())
                return;
            changing = true;
            setBGE(jlbg.getSelectedValue());
            changing = false;
        });

        addbg.setLnr(c -> {
            int selection = Opts.selection("What kind of BGEffect do you want to create?",
                    "Select BGEffect Type", "Custom Background Effect", "Mixed Background Effect");
            if (selection != -1) {
                if (selection == 0 && jld.getSelectedIndex() == -1) {
                    Opts.pop("Make sure to select an animation first", "Failed to add Custom BGEffect");
                    return;
                }
                changing = true;
                BackgroundEffect bgeff = selection == 1 ? new MixedBGEffect(pack.getNextID(BackgroundEffect.class), jlme.getSelectedValuesList())
                        : new CustomBGEffect(pack.getNextID(BackgroundEffect.class), jld.getSelectedValue());
                pack.bgEffects.add(bgeff);
                jlbg.setListData(pack.bgEffects.toArray());
                jlbg.setSelectedValue(bgeff, true);
                setBGE(bgeff);
                changing = false;
            }
        });

        rembg.setLnr(c -> {
            if (!Opts.conf())
                return;

            changing = true;
            int ind = jlbg.getSelectedIndex();
            pack.bgEffects.remove(bge);
            jlbg.setListData(pack.bgEffects.toRawArray());
            if (ind >= 0)
                ind--;
            jlbg.setSelectedIndex(ind);
            setBGE(jlbg.getSelectedValue());
            changing = false;
        });

        bgena.setLnr(c -> {
            if (bge instanceof MixedBGEffect)
                ((MixedBGEffect) bge).name = bgena.getText();
            else
                ((CustomBGEffect) bge).name = bgena.getText();
        });


        jlme.addListSelectionListener(x -> addme.setEnabled(editable && jlme.getSelectedIndex() != -1));

        jlbe.addListSelectionListener(x -> remme.setEnabled(editable && jlbe.getSelectedIndex() != -1));

        addme.setLnr(c -> {
            changing = true;
            int ind = jlme.getSelectedIndex();
            ((MixedBGEffect)bge).effects.add(jlme.getSelectedValue());
            setMBELists((MixedBGEffect) bge, ind - 1, jlbe.getSelectedIndex());
            changing = false;
        });

        remme.setLnr(c -> {
            changing = true;
            int ind = jlbe.getSelectedIndex();
            ((MixedBGEffect)bge).effects.remove(jlbe.getSelectedValue());
            setMBELists((MixedBGEffect) bge, jlme.getSelectedIndex(), ind - 1);
            changing = false;
        });


        jld.addListSelectionListener(x -> changebg.setEnabled(editable && jld.getSelectedIndex() != -1));

        changebg.setLnr(c -> ((CustomBGEffect)bge).anim = jld.getSelectedValue());

        bgsp.setLnr(c -> {
            int a = CommonStatic.parseIntN(bgsp.getText());
            if (changing || a < 0)
                return;
            changing = true;
            ((CustomBGEffect)bge).spacer = a;
            bgsp.setText("Draw Spacer: " + ((CustomBGEffect)bge).spacer);
            changing = false;
        });
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);
        set(addbg, x, y, 50, 1050, 150, 50);
        set(rembg, x, y, 200, 1050, 150, 50);
        set(jspbg, x, y, 50, 100, 300, 800);
        set(bgena, x, y, 50, 1000, 300, 50);
        if (bge instanceof MixedBGEffect) {
            set(jspbe, x, y, 450, 100, 300, 800);
            set(remme, x, y, 450, 900, 300, 50);
            set(jspme, x, y, 850, 100, 300, 800);
            set(addme, x, y, 850, 900, 300, 50);
        } else if (bge != null) {
            //Set some other params like name, animation changer, and spacer
            set(jspd, x, y, 450, 100, 300, 800);
            set(changebg, x, y, 450, 900, 300, 50);
            set(bgsp, x, y, 450, 1000, 300, 50);
        } else {
            set(jspd, x, y, 450, 100, 300, 800);
            set(jspme, x, y, 850, 100, 300, 800);
        }
    }

    private void ini() {
        add(back);
        add(addbg);
        add(rembg);
        add(jspbg);
        add(bgena);
        jld.setCellRenderer(new AnimLCR());
        jlbg.setListData(pack.bgEffects.toArray());
        setBGE(null);
        addListeners();
    }

    private void setBGE(BackgroundEffect be) {
        boolean b = editable && be != null;
        String[] rely = b ? inUse(be) : null;
        rembg.setEnabled(b && rely[0].length() + rely[1].length() == 0);
        if (rely != null && rely[0].length() + rely[1].length() > 0)
            rembg.setToolTipText("Cannot remove this effect cause it's being used by these BGs: " + rely[0] + " and the following Effects: " + rely[1]);

        addbg.setEnabled(editable);
        bgena.setEnabled(b);
        if (be == null) {
            remove(changebg);
            remove(bgsp);
            remove(addme);
            remove(remme);
            remove(jspbe);

            add(jspd);
            add(jspme);
            Vector<BackgroundEffect> bges = new Vector<>(UserProfile.getBCData().bgEffects.getList());
            bges.addAll(pack.bgEffects.getList());
            jlme.setListData(bges);
            jlme.setSelectedIndex(0);
        } else if (be instanceof MixedBGEffect) {
            remove(jspd);
            remove(changebg);
            remove(bgsp);

            add(addme);
            add(remme);
            addme.setEnabled(editable && jlme.getSelectedIndex() != -1);
            remme.setEnabled(editable && jlbe.getSelectedIndex() != -1);
            add(jspme);
            add(jspbe);
            bgena.setText(be.getName());
            setMBELists((MixedBGEffect) be, jlme.getSelectedIndex(), jlbe.getSelectedIndex());
        } else {
            remove(jspme);
            remove(jspbe);
            remove(addme);
            remove(remme);

            add(jspd);
            add(changebg);
            add(bgsp);
            bgena.setText(be.getName());
            bgsp.setText("Draw Spacer: " + ((CustomBGEffect)be).spacer);
            bgsp.setEnabled(editable);
            changebg.setEnabled(editable && jld.getSelectedIndex() != -1);
        }

        bge = be;
    }

    private void setMBELists(MixedBGEffect be, int sel0, int sel1) {
        Vector<BackgroundEffect> bges = new Vector<>(UserProfile.getBCData().bgEffects.getList());
        bges.addAll(pack.bgEffects.getList());
        for (String s : pack.desc.dependency)
            bges.addAll(UserProfile.getPack(s).bgEffects.getList());
        bges.removeIf(ef -> ef == be || be.effects.contains(ef));
        jlme.setListData(bges);
        jlme.setSelectedIndex(sel0);

        jlbe.setListData(be.effects.toArray(new BackgroundEffect[0]));
        jlbe.setSelectedIndex(sel1);
    }

    private String[] inUse(BackgroundEffect be) {
        StringBuilder bgss = new StringBuilder();
        for (Background b : pack.bgs)
            if (b.bgEffect != null && b.bgEffect.get() == be)
                bgss.append(b);
        StringBuilder bges = new StringBuilder();
        for (BackgroundEffect bgeff : pack.bgEffects)
            if (bgeff instanceof MixedBGEffect && ((MixedBGEffect)bgeff).effects.contains(be))
                bges.append(bgeff);
        for (String s : pack.desc.dependency) {
            PackData p = UserProfile.getPack(s);
            for (Background b : p.bgs)
                if (b.bgEffect != null && b.bgEffect.get() == be)
                    bgss.append(b);
            for (BackgroundEffect bgeff : p.bgEffects)
                if (bgeff instanceof MixedBGEffect && ((MixedBGEffect)bgeff).effects.contains(be))
                    bges.append(bgeff);
        }
        return new String[]{bgss.toString(), bges.toString()};
    }
}
