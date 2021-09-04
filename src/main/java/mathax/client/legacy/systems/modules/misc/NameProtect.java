package mathax.client.legacy.systems.modules.misc;

import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.settings.StringSetting;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;

public class NameProtect extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("name")
        .description("Name to be replaced with.")
        .defaultValue("Fit")
        .build()
    );

    private String username = "If you see this, something is wrong.";

    public NameProtect() {
        super(Categories.Misc, "name-protect", "Hides your name client-side.");
    }

    @Override
    public void onActivate() {
        username = mc.getSession().getUsername();
    }

    public String replaceName(String string) {
        if (string != null && isActive()) {
            return string.replace(username, name.get());
        }

        return string;
    }

    public String getName(String original) {
        if (name.get().length() > 0 && isActive()) {
            return name.get();
        }

        return original;
    }
}
