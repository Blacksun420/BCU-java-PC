package plugin;

import plugin.ui.common.util.ReflectUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PluginHandler implements Plugin {
    private final static PluginHandler H = new PluginHandler();

    private final List<Plugin> pluginList = new ArrayList<>();

    private PluginHandler() {
        installDefaultPlugin();
    }

    public static PluginHandler getInstance() {
        return H;
    }

    private void installDefaultPlugin() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("plugin.txt");
        if (in == null) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            while (reader.ready()) {
                installPlugin((Plugin) ReflectUtils.getInstance(Class.forName(reader.readLine())));
            }
        } catch (Exception e) {
            System.out.println("error when install default plugin: " + e);
        }
    }

    public void installPlugin(Plugin plugin) {
        boolean b = pluginList.stream().map(Object::getClass).anyMatch(c -> c == plugin.getClass());

        if (!b) {
            pluginList.add(plugin);
        }
    }

    private void forEach(Consumer<Plugin> doSth, boolean sync) {
        if (sync)
            pluginList.forEach(doSth);
        else
            pluginList.forEach(p -> new Thread(() -> doSth.accept(p)).start());
    }

    private void forEach(Consumer<Plugin> doSth) {
        forEach(doSth, true);
    }

    @Override
    public void doBeforeFrameInit() {
        forEach(Plugin::doBeforeFrameInit);
    }

    @Override
    public void doAfterFrameInit() {
        forEach(Plugin::doAfterFrameInit);
    }

    @Override
    public void doAfterReadingLang() {
        forEach(Plugin::doAfterReadingLang);
    }

    @Override
    public void doAfterLoading() {
        forEach(Plugin::doAfterLoading);
    }

}
