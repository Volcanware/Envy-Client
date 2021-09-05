package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.MatHaxClientLegacy;
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

import static mathax.client.legacy.utils.Utils.mc;

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
                if (Modules.get().isActive(NameProtect.class)) return "Welcome to MatHax Client Legacy, ";
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") || mc.getSession().getUuid().equals("7c73f844-73c3-3a7d-9978-004ba0a6436e")) return "Welcome to MatHax Client Legacy, Developer ";
                else return "Welcome to MatHax Client Legacy, ";
            }
            case Using -> {
                if (Modules.get().isActive(NameProtect.class)) return "You are using MatHax Client Legacy, ";
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") || mc.getSession().getUuid().equals("7c73f844-73c3-3a7d-9978-004ba0a6436e")) return "You are using MatHax Client Legacy, Developer ";
                else return "You are using MatHax Client Legacy, ";
            }
            case Time -> {
                if (Modules.get().isActive(NameProtect.class)) return getTime() + ", ";
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") || mc.getSession().getUuid().equals("7c73f844-73c3-3a7d-9978-004ba0a6436e")) return getTime() + ", Developer ";
                else return getTime() + ", ";
            }
            case RetardedTime -> {
                if (Modules.get().isActive(NameProtect.class)) return getRetardedTime() + ", ";
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") || mc.getSession().getUuid().equals("7c73f844-73c3-3a7d-9978-004ba0a6436e")) return getRetardedTime() + ", Developer ";
                else return getRetardedTime() + ", ";
            }
            case Sussy -> {
                if (Modules.get().isActive(NameProtect.class)) return "You are a sussy baka, ";
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") || mc.getSession().getUuid().equals("7c73f844-73c3-3a7d-9978-004ba0a6436e")) return getRetardedTime() + ", Developer ";
                else return "You are a sussy baka, ";
            }
            default -> {
                if (Modules.get().isActive(NameProtect.class)) return "Welcome to MatHax Client Legacy, ";
                if (mc.getSession().getUuid().equals("3e24ef27-e66d-45d2-bf4b-2c7ade68ff47") || mc.getSession().getUuid().equals("7c73f844-73c3-3a7d-9978-004ba0a6436e")) return "Welcome to MatHax Client Legacy, Developer ";
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
        final String hourDate = new SimpleDateFormat("k").format(new Date());
        final int hour = Integer.valueOf(hourDate);
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
        final String hourDate = new SimpleDateFormat("k").format(new Date());
        final int hour = Integer.valueOf(hourDate);
        if (hour < 3) {
            return "Why are you killing newfags at this hour retard";
        }
        if (hour < 6) {
            return "You really need get some sleep retard";
        }
        if (hour < 9) {
            return "Ur awake already? such a retard";
        }
        if (hour < 12) {
            return "retard moment";
        }
        if (hour < 14) {
            return "Go eat lunch retard";
        }
        if (hour < 17) {
            return "Retard playing minecraft";
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
