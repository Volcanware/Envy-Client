package mathax.client.systems.modules.movement;

import mathax.client.settings.EnumSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.systems.modules.render.Freecam;
import net.minecraft.item.Items;

public class Sneak extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Which method to sneak.")
        .defaultValue(Mode.Vanilla)
        .build()
    );

    public Sneak() {
        super(Categories.Movement, Items.LEATHER_BOOTS, "sneak", "Makes you sneak automatically.");
    }

    public boolean doPacket() {
        return isActive() && !Modules.get().isActive(Freecam.class) && mode.get() == Mode.Packet;
    }

    public boolean doVanilla() {
        return isActive() && !Modules.get().isActive(Freecam.class) && mode.get() == Mode.Vanilla;
    }

    public enum Mode {
        Packet("Packet"),
        Vanilla("Vanilla");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
