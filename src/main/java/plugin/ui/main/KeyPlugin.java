package plugin.ui.main;

import plugin.Plugin;
import plugin.ui.common.config.StaticConfig;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeyPlugin extends KeyAdapter implements Plugin {
    public static KeyPlugin P = new KeyPlugin();

    public Boolean drawAll = true;
    public Map<Integer, Boolean> keyEventMap = new HashMap<>();

    public KeyPlugin() {
        keyEventMap.put(StaticConfig.DRAW_TOP, true);
        keyEventMap.put(StaticConfig.DRAW_BTM, true);
        keyEventMap.put(StaticConfig.DRAW_ENTITY, true);
        keyEventMap.put(StaticConfig.DRAW_CASTLE, true);
    }

    public boolean canDraw(Integer code) {
        return keyEventMap.get(code);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_0) {
            Set<Map.Entry<Integer, Boolean>> entries = keyEventMap.entrySet();
            for (Map.Entry<Integer, Boolean> entry : entries) {
                entry.setValue(drawAll);
            }
            drawAll = !drawAll;
        } else {
            Boolean value = keyEventMap.get(e.getKeyCode());
            if (value != null) {
                keyEventMap.put(e.getKeyCode(), !value);
            }
        }
    }

}
