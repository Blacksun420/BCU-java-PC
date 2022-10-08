package plugin.ui.common.config;

import java.awt.*;
import java.awt.event.KeyEvent;

public interface StaticConfig {
    String UI_DIRECTORY = "plugin/ui/";
    String UI_CONFIG_FILE_NAME = "ui.json";
    String UI_JSON_PATH = UI_DIRECTORY + UI_CONFIG_FILE_NAME;
    String THEME_JSON_PATH = UI_DIRECTORY + "theme.json";

    String KEY_USER_SELECTED_IMAGE = "userSelected";
    String SRC_IMAGE_PREFIX = "src_";
    String SEPARATOR_THEME_NAME_TYPE = ",";
    String THEME_FLAT_REGISTER_SOURCE = "plugin/ui/flat_custom";

    String COLOR_CD_LINE_LOADING = "cd_loading";
    String COLOR_CD_LINE_BACKDROP = "cd_backdrop";
    String COLOR_CD_LOCK = "cd_lock";

    String KEY_ICON = "icon";
    String KEY_BG = "bg";

    Integer OPAQUE_MIN = 0;
    Integer OPAQUE_MAX = 100;
    String[] IMAGE_SUFFIX = new String[]{".png", ".jpg"};

    Integer FONT_MIN_SIZE = 14;
    Font DEFAULT_FONT = new Font("Microsoft YaHei UI", Font.PLAIN, 16);

    int MENU = 1;
    int MENU_ITEM = 2;
    int CHECK_BOX_MENU_ITEM = 3;
    int JBUTTON = 4;

    Integer DRAW_CASTLE = KeyEvent.VK_7;
    Integer DRAW_ENTITY = KeyEvent.VK_8;
    Integer DRAW_BTM = KeyEvent.VK_9;
    Integer DRAW_TOP = KeyEvent.VK_6;
}
