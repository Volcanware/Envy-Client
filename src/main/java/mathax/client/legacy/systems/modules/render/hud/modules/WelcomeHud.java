package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.settings.ColorSetting;
import mathax.client.legacy.settings.EnumSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.chat.AutoEZ;
import mathax.client.legacy.systems.modules.misc.NameProtect;
import mathax.client.legacy.systems.modules.player.ChestSwap;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.render.color.SettingColor;

public class WelcomeHud extends TripleTextHudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<WelcomeHud.Message> message = sgGeneral.add(new EnumSetting.Builder<WelcomeHud.Message>()
        .name("message")
        .description("Determines what message style to use.")
        .defaultValue(WelcomeHud.Message.Welcome)
        .build()
    );

    private final Setting<SettingColor> usernameColor = sgGeneral.add(new ColorSetting.Builder()
        .name("username-color")
        .description("Color of the username.")
        .defaultValue(new SettingColor(230, 75, 100, true))
        .build()
    );

    public WelcomeHud(HUD hud) {
        super(hud, "welcome", "Displays a welcome message.");
        rightColor = usernameColor.get();
    }

    @Override
    protected String getLeft() {
        switch (message.get()) {
            case Welcome -> {
                if (Modules.get().isActive(NameProtect.class)) return "Welcome to MatHax Client Legacy, ";
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47")) return "Welcome to MatHax Client Legacy, Developer ";
                else return "Welcome to MatHax Client Legacy, ";
            }
            case Using -> {
                if (Modules.get().isActive(NameProtect.class)) return "You are using MatHax Client Legacy, ";
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47")) return "You are using MatHax Client Legacy, Developer";
                else return "You are using MatHax Client Legacy, ";
            }
            default -> {
                if (Modules.get().isActive(NameProtect.class)) return "Welcome to MatHax Client Legacy, ";
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47")) return "Welcome to MatHax Client Legacy, Developer ";
                else return "Welcome to MatHax Client Legacy, ";
            }
        }
    }

    @Override
    protected String getRight() {
        return Modules.get().get(NameProtect.class).getName(mc.getSession().getUsername());
    }

    @Override
    public String getEnd() {
        return "!";
    }

    public enum Message {
        Welcome,
        Using
    }
}
