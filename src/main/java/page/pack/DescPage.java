package page.pack;

import common.CommonStatic;
import common.pack.Context;
import common.pack.PackData.UserPack;
import main.MainBCU;
import main.Opts;
import page.*;
import page.support.Importer;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static utilpc.UtilPC.resizeImage;

public class DescPage extends Page {

    private static final long serialVersionUID = 1L;

    private final JL pauth = new JL();
    private final JL panim = new JL();
    private final JLabel picon = new JLabel();
    private final JLabel pbanner = new JLabel();
    private final JL psta = new JL();
    private final JL pver = new JL();
    private final JTF tver = new JTF();
    private final JL pbcuver = new JL();
    private final JL pdate = new JL();
    private final JL pdatexp = new JL();
    private final JTA descDisplay = new JTA();
    private final JScrollPane descPane = new JScrollPane(descDisplay);

    private final JBTN setIcn = new JBTN(MainLocale.PAGE, "icon");
    private final JBTN setBnr = new JBTN(MainLocale.PAGE, "banner");
    private final UserPack pack;
    private final boolean editable;

    public DescPage(Page p, UserPack up) {
        super(p);
        pack = up;
        editable = up.editable;
        ini();
    }

    private void ini() {
        add(descPane);
        descDisplay.setText(pack.desc.info.toString());
        descDisplay.setEnabled(editable);
        add(pauth);
        pauth.setText(pack.desc.getAuthor().isEmpty() ? "No Author" : "By " + pack.desc.author);
        add(pdate);
        pdate.setText(pack.desc.creationDate == null ? get(MainLocale.PAGE, "ucdate") : get(MainLocale.PAGE, "cdate") + Interpret.translateDate(pack.desc.creationDate));
        if (!pack.editable) {
            add(panim);
            panim.setText((pack.desc.parentPassword == null ? "Parentable, " : "Unparentable, ") + "anims " +  (pack.desc.allowAnim ? "copyable" : "uncopyable"));
            add(pver);
            pver.setText("Version: " + pack.desc.version);
            add(pbcuver);
            pbcuver.setText("Core Ver: " + pack.desc.BCU_VERSION + (pack.desc.FORK_VERSION > 0 ? " " + pack.desc.FORK_VERSION + "f" : ""));
            add(pdatexp);
            pdatexp.setText(pack.desc.exportDate == null ? get(MainLocale.PAGE, "uedate") : get(MainLocale.PAGE, "edate") + Interpret.translateDate(pack.desc.exportDate));
        } else {
            add(tver);
            tver.setText("Version " + pack.desc.version);
        }
        add(psta);
        psta.setText(get(MainLocale.PAGE, "sttot") + " " + pack.mc.getStageCount());

        if (pack.editable) {
            add(setIcn);
            add(setBnr);
        }
        if (pack.icon != null || pack.editable) {
            add(picon);
            picon.setIcon(UtilPC.resizeIcon(pack.icon, 128, 128));
        }
        if (pack.banner != null || pack.editable) {
            add(pbanner);
            pbanner.setIcon(UtilPC.resizeIcon(pack.banner, 1050, 550));
        }
        addListeners();
    }

    @Override
    public JButton getBackButton() {
        return null;
    }

    @Override
    protected void resized(int x, int y) {
        int h = pack.banner == null ? pack.editable ? 50 : 0 : 720;
        set(descPane, x, y, 0, h, 900, 300);
        setComponentZOrder(descPane, 2);
        if (h != 720) {
            set(pbanner, x, y, 0, 0, 0, 0);
            set(picon, x, y, 740, h, 182, 182);
            setComponentZOrder(picon, 0);
            set(setIcn, x, y, 100, 0, 300, 50);
            set(setBnr, x, y, 680, 0, 300, 50);
        } else {
            set(pbanner, x, y, 0, 0, 1300, 720);
            setComponentZOrder(pbanner, 1);
            if (pack.icon == null) {
                set(setIcn, x, y, 0, 545, 182, 182);
                set(picon, x, y, 0, 545, 0, 0);
                setComponentZOrder(setIcn, 0);
            } else {
                set(picon, x, y, 0, 545, 182, 182);
                set(setIcn, x, y, 0, 545, 0, 0);
                setComponentZOrder(picon, 0);
            }
            set(setBnr, x, y, 680, 0, 0, 0);
        }
        set(pauth, x, y, 900, h, 350, 50);
        set(psta, x, y, 900, h + 100, 350, 50);
        if (!pack.editable) {
            set(panim, x, y, 900, h + 50, 350, 50);
            set(pver, x, y, 900, h + 150, 150, 50);
            set(pbcuver, x, y, 1050, h + 150, 200, 50);
            set(pdate, x, y, 900, h + 200, 350, 50);
            set(pdatexp, x, y, 900, h + 250, 350, 50);
        } else {
            set(tver, x, y, 900, h + 50, 350, 50);
            set(pdate, x, y, 900, h + 150, 350, 50);
        }
    }

    private void addListeners() {
        descDisplay.setLnr(c -> pack.desc.info.put(descDisplay.getText()));

        picon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getButton());
                super.mouseClicked(e);
                if (editable)
                    if (e.getButton() == MouseEvent.BUTTON1)
                        getImage(true, "Choose an icon for your pack");
                    else if (Opts.conf()) {
                        File file = CommonStatic.ctx.getWorkspaceFile(pack.getSID() + "/icon.png");
                        if (file.delete()) {
                            pack.icon = null;
                            picon.setIcon(null);
                        }
                    }
            }
        });
        pbanner.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (editable)
                    if (e.getButton() == MouseEvent.BUTTON1)
                        getImage(false, "Choose a banner for your pack (Recommended Res: 1050x550)");
                    else if (Opts.conf()) {
                        File file = CommonStatic.ctx.getWorkspaceFile(pack.getSID() + "/banner.png");
                        if (file.delete()) {
                            pack.icon = null;
                            pbanner.setIcon(null);
                        }
                    }
            }
        });

        setIcn.setLnr(c -> getImage(true, "Choose an icon for your pack"));
        setBnr.setLnr(c -> getImage(false, "Choose a banner for your pack"));

        tver.setLnr(c -> {
            double n = CommonStatic.parseDoubleN(tver.getText());
            if (n > 0)
                pack.desc.version = n;
            tver.setText("Version " + pack.desc.version);
        });
    }

    private void getImage(boolean icon, String text) {
        BufferedImage bimg = new Importer(text, Importer.IMP_IMG).getImg();
        if (bimg == null)
            return;
        if (icon) {
            if (bimg.getWidth() != bimg.getHeight()) {
                getImage(true, "Icon must have the same width and height");
                return;
            }
            bimg = resizeImage(bimg, 128, 128);
            if (pack.icon != null)
                pack.icon.setImg(MainBCU.builder.build(bimg));
            else
                pack.icon = MainBCU.builder.toVImg(bimg);
        } else {
            bimg = resizeImage(bimg, 1050, 550);
            if (pack.banner != null)
                pack.banner.setImg(MainBCU.builder.build(bimg));
            else
                pack.banner = MainBCU.builder.toVImg(bimg);
        }
        try {
            File file = CommonStatic.ctx.getWorkspaceFile(pack.getSID() + "/" + (icon ? "icon" : "banner") + ".png");
            Context.check(file);
            ImageIO.write(bimg, "PNG", file);
        } catch (IOException e) {
            CommonStatic.ctx.noticeErr(e, Context.ErrType.WARN, "failed to write file");
            getImage(icon, "Failed to save file");
            return;
        }
        updateIconDisplays();
    }

    private void updateIconDisplays() {
        picon.setIcon(UtilPC.resizeIcon(pack.icon, 128, 128));
        pbanner.setIcon(UtilPC.resizeIcon(pack.banner, 1050, 550));
    }
}
