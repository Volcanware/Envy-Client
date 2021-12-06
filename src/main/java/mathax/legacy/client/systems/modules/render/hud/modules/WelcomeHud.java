package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.settings.ColorSetting;
import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.misc.NameProtect;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.TripleTextHudElement;
import mathax.legacy.client.utils.render.color.SettingColor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WelcomeHud extends TripleTextHudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Message> message = sgGeneral.add(new EnumSetting.Builder<Message>()
        .name("message")
        .description("Determines what message style to use.")
        .defaultValue(WelcomeHud.Message.Welcome)
        .build()
    );

    private final Setting<SettingColor> usernameColor = sgGeneral.add(new ColorSetting.Builder()
        .name("username-color")
        .description("Color of the username.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b, true))
        .build()
    );

    public WelcomeHud(HUD hud) {
        super(hud, "welcome", "Displays a welcome message.", true);
        rightColor = usernameColor.get();
    }

    @Override
    protected String getLeft() {
        switch (message.get()) {
            case Using -> {
                if (Modules.get().isActive(NameProtect.class)) return "You are using MatHax Legacy, ";
                if (MatHaxLegacy.isDeveloper(mc.getSession().getUuid())) return "You are using MatHax Legacy, Developer ";
                else return "You are using MatHax Legacy, ";
            }
            case Time -> {
                if (Modules.get().isActive(NameProtect.class)) return getTime() + ", ";
                if (MatHaxLegacy.isDeveloper(mc.getSession().getUuid())) return getTime() + ", Developer ";
                else return getTime() + ", ";
            }
            case Retarded_Time -> {
                if (Modules.get().isActive(NameProtect.class)) return getRetardedTime() + ", ";
                if (MatHaxLegacy.isDeveloper(mc.getSession().getUuid())) return getRetardedTime() + ", Developer ";
                else return getRetardedTime() + ", ";
            }
            case Sussy -> {
                if (Modules.get().isActive(NameProtect.class)) return "You are a sussy baka, ";
                if (MatHaxLegacy.isDeveloper(mc.getSession().getUuid())) return "You are a sussy baka, Developer ";
                else return "You are a sussy baka, ";
            }
            default -> {
                if (Modules.get().isActive(NameProtect.class)) return "Welcome to MatHax Legacy, ";
                if (MatHaxLegacy.isDeveloper(mc.getSession().getUuid())) return "Welcome to MatHax Legacy, Developer ";
                else return "Welcome to MatHax Legacy, ";
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
        if (hour < 6) return "Good Night";
        if (hour < 12) return "Good Morning";
        if (hour < 17) return "Good Afternoon";
        if (hour < 20) return "Good Evening";
        return "Good Night";
    }

    private String getRetardedTime() {
        final String hourDate = new SimpleDateFormat("k").format(new Date());
        final int hour = Integer.valueOf(hourDate);
        if (hour < 3) return "Why are you killing newfags at this hour retard";
        if (hour < 6) return "You really need get some sleep retard";
        if (hour < 9) return "Ur awake already? such a retard";
        if (hour < 12) return "Retard moment";
        if (hour < 14) return "Go eat lunch retard";
        if (hour < 17) return "Retard playing minecraft";
        return "Time to sleep retard";
    }

    public enum Message {
        Welcome,
        Using,
        Time,
        Retarded_Time,
        Sussy;

        @Override
        public String toString() {
            return super.toString().replace("_", " ");
        }
    }
}
