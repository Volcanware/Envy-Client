package mathax.client.systems.modules.client;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.widgets.WWidget;
import mathax.client.gui.widgets.containers.WHorizontalList;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.EnumSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.misc.PacketPitch;
import mathax.client.utils.network.Capes;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.item.Items;

public class CapesModule extends Module {

    public CapesModule() {
        super(Categories.Client, Items.CAKE, "capes", "Gives Players Envy Capes");
    }

    public String capeurl = "1.1.1.1";
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Decide from packet or client sided rotation.")
        .defaultValue(Mode.Envy)
        .build()
    );


    @EventHandler
    public boolean onActivate() {

        if (mode.get() == Mode.Envy) {
            capeurl = "https://raw.githubusercontent.com/Volcanware/Envy-Client/Now-Fixed/EnvyCape.png";
        }
        if (mode.get() == Mode.Optifine) {
            capeurl = "http://s.optifine.net/capes/%s.png";
        }
        if (mode.get() == Mode.Cosmetica) {
            capeurl = "23.95.137.176";
        }

        return false;
    }




    public enum Mode {
        Envy("Envy"),

        Optifine("Optifine"),
        Cosmetica("Cosmetica");
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
