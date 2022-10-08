package plugin.ui.main.util;

import common.CommonStatic;
import common.io.WebFileIO;
import common.io.assets.UpdateCheck.Downloader;
import main.Opts;
import page.LoadPage;
import plugin.ui.main.UIPlugin;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.function.Consumer;


public class UIDownloader {

    public static void downloadLibs(List<Downloader> list) {
        DownloadProgressFrame frame = new DownloadProgressFrame(
                "BCU won't exit but download lib silently, " + "just do nothing and wait for a pop dialog patiently.</h3></html>",
                "downloading...");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        list.forEach((d) -> {
            while (true) {
                try {
                    System.out.println(d);
                    frame.text_below.setHtmlText(d.desc);
                    d.run((percent) -> {
                        frame.setProgress(percent);
                        System.out.println(percent);
                    });
                    break;
                } catch (Exception e) {
                    if (!UIPlugin.conf("failed to download, retry?"))
                        break;
                }
            }
        });
    }

    public static void downloadJar(Downloader d, boolean exit) {
        downloadJar(d, exit, LoadPage::prog, LoadPage.lp::accept);
    }

    public static void downloadJar(Downloader d, boolean exit, String text_above, String text_below) {
        DownloadProgressFrame frame = new DownloadProgressFrame(text_above, text_below);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                run = false;
            }
        });
        downloadJar(d, exit, frame.text_below::setHtmlText, frame::setProgress);
    }

    public static boolean run;

    private static void downloadJar(Downloader d, boolean exit, Consumer<String> desc, Consumer<Double> progress) {
        run = true;
        desc.accept(d.desc);

        boolean done = false;

        while (true) {
            if (!run)
                return;

            try {
                download(d, progress);
                done = true;
                break;
            } catch (Exception e) {
                e.printStackTrace();
                if (run && !UIPlugin.conf("failed to download, retry?")) {
                    break;
                }
            }
        }

        if (done && run) {
            Opts.pop("Finished downloading latest BCU, run this jar file from now on",
                    "Download finished");

            if (exit)
                CommonStatic.def.save(false, true);
        }
    }


    public static void download(Downloader d, Consumer<Double> progress) throws Exception {
        if (d.temp.exists() && !d.temp.delete()) {
            System.out.println("W/UpdateCheck::Downloader - Failed to delete " + d.temp.getAbsolutePath());
        }
        boolean success = false;
        for (String u : d.url) {
            WebFileIO.download(u, d.temp, progress, d.direct);
            success = true;
            break;
        }
        if (!success) {
            d.temp.delete();
            return;
        }
        if (!d.target.getParentFile().exists())
            d.target.getParentFile().mkdirs();
        if (d.target.exists())
            d.target.delete();
        d.temp.renameTo(d.target);
        if (d.post != null) {
            d.post.run();
        }
    }


}
