package plugin.ui.main.context;

import plugin.ui.common.config.StaticConfig;
import plugin.ui.common.util.JsonUtils;


public class UserProfile {
    public BasicConfig basic;

    public ThemeConfig theme;

    public static UserProfile getDefault() {
        return JsonUtils.fromClasspath(StaticConfig.UI_JSON_PATH, UserProfile.class);
    }
}
