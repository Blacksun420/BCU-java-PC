package page.support;

import common.pack.PackData;
import common.pack.UserProfile;
import common.util.stage.Music;
import io.BCMusic;
import io.BCPlayer;
import page.JBTN;
import page.MainLocale;
import page.Page;
import utilpc.PP;
import utilpc.Theme;
import utilpc.UtilPC;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MusicPopup extends Page {

    private static final long serialVersionUID = 1L;
    private static class PackList extends JList<PackData> {

        private static final long serialVersionUID = 1L;
        protected PackList() {
            super();
            ini();
        }

        private void ini() {
            setSelectionBackground(Theme.DARK.NIMBUS_SELECT_BG);
            setCellRenderer(new DefaultListCellRenderer() {
                private static final long serialVersionUID = 1L;

                @Override
                public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
                    JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
                    if (o instanceof PackData.UserPack)
                        jl.setIcon(UtilPC.resizeIcon(((PackData.UserPack)o).icon, UtilPC.iconSize, UtilPC.iconSize));
                    return jl;
                }
            });
        }
    }

    private final JList<Music> jlf = new JList<>();
    private final JScrollPane jsp = new JScrollPane(jlf);
    private final Collection<Music> musics;

    private final JBTN play = new JBTN(MainLocale.PAGE, "start");
    private final JBTN stop = new JBTN(MainLocale.PAGE, "stop");
    private final PackList packs = new PackList();
    private final JScrollPane jpack = new JScrollPane(packs);

    private ThreadPlayer BG = null;
    private PP previousDimension = new PP(0, 0);
    public static final int W = 300, H = 600;

    public MusicPopup(Collection<Music> mu, Music sele) {
        super(null);
        if (mu != null) {
            jlf.setListData((musics = mu).toArray(new Music[0]));
        } else {
            List<Music> mus = new ArrayList<>();
            List<PackData> pks = new ArrayList<>();
            for (PackData pac : UserProfile.getAllPacks()) {
                if (pac.musics.isEmpty())
                    continue;
                pks.add(pac);
                mus.addAll(pac.musics.getList());
            }
            packs.setListData(pks.toArray(new PackData[0]));
            jlf.setListData((musics = mus).toArray(new Music[0]));
        }
        jlf.setSelectedValue(sele, true);

        add(jsp);
        add(play);
        add(stop);
        add(jpack);

        play.setEnabled(sele != null);
        stop.setEnabled(false);
        setListeners();
    }

    private void setListeners() {
        jlf.addListSelectionListener(a -> play.setEnabled(jlf.getSelectedValue() != null));

        play.setLnr(b -> {
            setBG(jlf.getSelectedValue());
            stop.setEnabled(true);
        });

        stop.setLnr(b -> {
            close();
            stop.setEnabled(false);
        });

        packs.addListSelectionListener(a -> {
            List<PackData> packss = packs.getSelectedValuesList();
            if (packss.isEmpty()) {
                jlf.setListData(musics.toArray(new Music[0]));
                return;
            }
            Collection<Music> mus = new ArrayList<>();
            for (PackData pack : packss) {
                Collection<Music> muss = pack.musics.getList();
                muss.removeIf(m -> !musics.contains(m));
                mus.addAll(muss);
            }
            jlf.setListData(mus.toArray(new Music[0]));
        });
    }

    @Override
    protected void resized(int x, int y) {
        int packW = packs.getModel().getSize() >= 2 ? W / 2 : 0;
        put(jsp, x, y, 0, 0, 300 - packW, 500);
        put(jpack, x, y,packW, 0, packW, 500);
        put(play, x, y, 0, 500, W / 2, 100);
        put(stop, x, y, W / 2, 500, W / 2, 100);
    }

    public static void put(Component jc, int winx, int winy, int x, int y, int w, int h) {
        jc.setBounds(x * winx / W, y * winy / H, w * winx / W, h * winy / H);
    }

    @Override
    protected void resized() {
        PP dimension = new PP(getRootPane().getWidth(), getRootPane().getHeight());
        if (!dimension.equals(previousDimension)) {
            previousDimension = dimension;
            Point p = dimension.toPoint();
            componentResized(p.x, p.y);
        }
    }

    @Override
    public JButton getBackButton() {
        return null;
    }

    private synchronized void setBG(Music f) {
        if (BCMusic.VOL_BG == 0)
            return;
        try {
            AudioInputStream raw = AudioSystem.getAudioInputStream(f.data.getStream());
            AudioFormat rf = raw.getFormat();
            int ch = rf.getChannels();
            float rate = rf.getSampleRate();
            AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
            AudioInputStream stream = AudioSystem.getAudioInputStream(format, raw);
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip c = (Clip) AudioSystem.getLine(info);
            c.open(stream);
            raw.close();
            stream.close();

            c.loop(Clip.LOOP_CONTINUOUSLY);
            close();
            BG = new ThreadPlayer(c, -1, f.loop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (BG != null) {
            BG.end();
            BG = null;
        }
    }

    private static class ThreadPlayer extends BCPlayer {//Might make progbar in the future

        protected ThreadPlayer(Clip c, int ind, long loop) {
            super(c, ind, loop);
            setVolume(BCMusic.VOL_BG);
            start();
        }

        private void end() {
            release();
        }
    }
}
