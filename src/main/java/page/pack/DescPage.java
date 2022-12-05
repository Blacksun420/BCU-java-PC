package page.pack;

import common.CommonStatic;
import common.pack.Context;
import common.pack.PackData.UserPack;
import common.util.stage.StageMap;
import main.MainBCU;
import main.Opts;
import page.*;
import page.support.Importer;
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

    private final JL pname = new JL();
    private final JL pid = new JL();
    private final JL pauth = new JL();
    private final JL panim = new JL();
    private final JLabel picon = new JLabel();
    private final JLabel pbanner = new JLabel();
    private final JL psta = new JL();
    private final JL pver = new JL();
    private final JTF tver = new JTF();
    private final JL pbcuver = new JL();
    private final JL pdate = new JL();
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
        descDisplay.setText(pack.desc.desc);
        descDisplay.setEnabled(editable);
        add(pname);
        pname.setText(pack.toString());
        add(pid);
        pid.setText("ID: " + pack.getSID());
        add(pauth);
        pauth.setText(pack.desc.author == null ? "Unknown Creator" : "Made by " + pack.desc.author);
        if (!pack.editable) {
            add(panim);
            panim.setText((pack.desc.parentPassword == null ? "Parentable, " : "Unparentable, ") + "anims " +  (pack.desc.allowAnim ? "copyable" : "uncopyable"));
            add(pver);
            pver.setText("Pack Version " + pack.desc.version);
            add(pbcuver);
            pbcuver.setText("BCU Version: " + pack.desc.BCU_VERSION + (pack.desc.FORK_VERSION > 0 ? " " + pack.desc.FORK_VERSION + "f" : ""));
            add(pdate);
            pdate.setText(pack.desc.creationDate == null ? "Unknown Creation Date" : "Created at " + pack.desc.creationDate);
        } else {
            add(tver);
            tver.setText("Version " + pack.desc.version);
        }
        int stageTot = 0;
        for (StageMap smaps : pack.mc.maps)
            stageTot += smaps.list.size();
        add(psta);
        psta.setText(get(MainLocale.PAGE, "sttot") + " " + stageTot);

        if (pack.icon != null || pack.editable) {
            add(setIcn);
            add(picon);
            if (pack.icon != null)
                picon.setIcon(new ImageIcon(UtilPC.resizeImage((BufferedImage) pack.icon.getImg().bimg(), 128, 128)));
            else
                picon.setIcon(null);
        }
        if (pack.banner != null || pack.editable) {
            add(setBnr);
            add(pbanner);
            if (pack.banner != null)
                pbanner.setIcon(new ImageIcon(UtilPC.resizeImage((BufferedImage) pack.banner.getImg().bimg(), 1050, 550)));
            else
                pbanner.setIcon(null);
        }
        addListeners();
    }

    @Override
    protected void resized(int x, int y) {
        int h = pack.banner == null ? 50 : 650;
        set(descPane, x, y, 0, h, 900, 400);
        setComponentZOrder(descPane, 2);
        if (h == 50) {
            set(pbanner, x, y, 0, 0, 0, 0);
            set(picon, x, y, 500, 0, 200, 200);
            set(setIcn, x, y, 100, 0, 300, 50);
            set(setBnr, x, y, 680, 0, 300, 50);
        } else {
            set(pbanner, x, y, 0, 0, 1300, 650);
            set(picon, x, y, 0, 475, 182, 182);
            if (pack.icon == null)
                set(setIcn, x, y, 32, 475, 182, 182);
            set(setBnr, x, y, 680, 0, 0, 0);
        }
        setComponentZOrder(pbanner, 1);
        setComponentZOrder(picon, 0);
        set(pname, x, y, 900, h, 350, 50);
        set(pid, x, y, 900, h + 50, 350, 50);
        set(pauth, x, y, 900, h + 100, 350, 50);
        set(psta, x, y, 900, h + 200, 350, 50);
        if (!pack.editable) {
            set(panim, x, y, 900, h + 150, 350, 50);
            set(pver, x, y, 900, h + 250, 350, 50);
            set(pbcuver, x, y, 900, h + 300, 350, 50);
            set(pdate, x, y, 900, h + 350, 350, 50);
        } else {
            set(tver, x, y, 900, h + 150, 350, 50);
        }
    }

    private void addListeners() {
        descDisplay.setLnr(c -> pack.desc.desc = descDisplay.getText());

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
        BufferedImage bimg = new Importer(text).getImg();
        if (bimg == null)
            return;
        if (icon) {
            if (bimg.getWidth() != bimg.getHeight()) {
                getImage(true, "Icon must have the same width and height");
                return;
            }
            if (bimg.getWidth() != 128 || bimg.getHeight() != 128)
                bimg = resizeImage(bimg, 128, 128);

            if (pack.icon != null)
                pack.icon.setImg(MainBCU.builder.build(bimg));
            else
                pack.icon = MainBCU.builder.toVImg(bimg);
        } else {
            if (bimg.getWidth() != 1050 || bimg.getHeight() != 550)
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
        if (pack.icon != null)
            picon.setIcon(new ImageIcon(UtilPC.resizeImage((BufferedImage) pack.icon.getImg().bimg(), 128, 128)));
        if (pack.banner != null)
            pbanner.setIcon(new ImageIcon(UtilPC.resizeImage((BufferedImage) pack.banner.getImg().bimg(), 1050, 550)));
    }
}
