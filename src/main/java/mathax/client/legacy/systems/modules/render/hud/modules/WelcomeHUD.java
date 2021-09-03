package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.settings.ColorSetting;
import mathax.client.legacy.settings.EnumSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.misc.NameProtect;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHUDElement;
import mathax.client.legacy.utils.render.color.SettingColor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WelcomeHUD extends TripleTextHUDElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<WelcomeHUD.Message> message = sgGeneral.add(new EnumSetting.Builder<WelcomeHUD.Message>()
        .name("message")
        .description("Determines what message style to use.")
        .defaultValue(WelcomeHUD.Message.Welcome)
        .build()
    );

    private final Setting<SettingColor> usernameColor = sgGeneral.add(new ColorSetting.Builder()
        .name("username-color")
        .description("Color of the username.")
        .defaultValue(new SettingColor(230, 75, 100, true))
        .build()
    );

    public WelcomeHUD(HUD hud) {
        super(hud, "welcome", "Displays a welcome message.", true);
        rightColor = usernameColor.get();
    }

    @Override
    protected String getLeft() {
        switch (message.get()) {
            case Welcome -> {
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") && !Modules.get().isActive(NameProtect.class)) return "Welcome to MatHax Client Legacy, Developer ";
                else return "Welcome to MatHax Client Legacy, ";
            }
            case Using -> {
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") && !Modules.get().isActive(NameProtect.class)) return "You are using MatHax Client Legacy, Developer";
                else return "You are using MatHax Client Legacy, ";
            }
            case Time -> {
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") && !Modules.get().isActive(NameProtect.class)) return getTime() + ", Developer";
                else return getTime() + ", ";
            }
            case RetardedTime -> {
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") && !Modules.get().isActive(NameProtect.class)) return getRetardedTime() + ", Developer";
                else return getTime() + ", ";
            }
            case Sussy -> {
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") && !Modules.get().isActive(NameProtect.class)) return getRetardedTime() + ", Developer";
                else return "You are a sussy baka, ";
            }
            default -> {
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") && !Modules.get().isActive(NameProtect.class)) return "Welcome to MatHax Client Legacy, Developer ";
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

    private String getTime() {
        final String date = new SimpleDateFormat("k").format(new Date());
        final int hour = Integer.valueOf(date);
        if (hour < 6) {
            return "Good Night";
        }
        if (hour < 12) {
            return "Good Morning";
        }
        if (hour < 17) {
            return "Good Afternoon";
        }
        if (hour < 20) {
            return "Good Evening";
        }
        return "Good Night";
    }

    private String getRetardedTime() {
        final String date = new SimpleDateFormat("k").format(new Date());
        final int hour = Integer.valueOf(date);
        if (hour < 6) {
            return "You really need get some sleep retard";
        }
        if (hour < 12) {
            return "Ur awake already? Pretty retarded";
        }
        if (hour < 17) {
            return "Go eat lunch retard";
        }
        if (hour < 20) {
            return "Time to sleep retard";
        }
        return "Time to sleep retard";
    }

    public enum Message {
        Welcome,
        Using,
        Time,
        RetardedTime,
        Sussy
    }
}
