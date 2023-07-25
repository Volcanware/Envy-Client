package envy.client.systems.hud.modules;

import envy.client.Envy;
import envy.client.settings.ColorSetting;
import envy.client.settings.EnumSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.hud.HUD;
import envy.client.systems.hud.TripleTextHudElement;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.misc.NameProtect;
import envy.client.utils.Utils;
import envy.client.utils.render.color.SettingColor;

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
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b, true))
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
                if (Modules.get().isActive(NameProtect.class)) return "You are using Envy Client, ";
                if (Utils.isDeveloper(mc.getSession().getUuid())) return "You are using Envy Client, Developer ";
                else return "You are using Envy Client, ";
            }
            case Time -> {
                if (Modules.get().isActive(NameProtect.class)) return getTime() + ", ";
                if (Utils.isDeveloper(mc.getSession().getUuid())) return getTime() + ", Developer ";
                else return getTime() + ", ";
            }
            case Retarded_Time -> {
                if (Modules.get().isActive(NameProtect.class)) return getRetardedTime() + ", ";
                if (Utils.isDeveloper(mc.getSession().getUuid())) return getRetardedTime() + ", Developer ";
                else return getRetardedTime() + ", ";
            }
            case Sussy -> {
                if (Modules.get().isActive(NameProtect.class)) return "You are a sussy baka, ";
                if (Utils.isDeveloper(mc.getSession().getUuid())) return "You are a sussy baka, Developer ";
                else return "You are a sussy baka, ";
            }
            default -> {
                if (Modules.get().isActive(NameProtect.class)) return "Welcome to Envy Client, ";
                if (Utils.isDeveloper(mc.getSession().getUuid())) return "Welcome to Envy Client, Developer ";
                else return "Welcome to Envy Client, ";
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
        final int hour = Integer.parseInt(hourDate);
        if (hour < 6) return "Good Night";
        if (hour < 12) return "Good Morning";
        if (hour < 17) return "Good Afternoon";
        if (hour < 20) return "Good Evening";
        return "Good Night";
    }

    private String getRetardedTime() {
        final String hourDate = new SimpleDateFormat("k").format(new Date());
        final int hour = Integer.parseInt(hourDate);
        if (hour < 3) return "Why are you killing newfags at this hour retard";
        if (hour < 6) return "You really need get some sleep retard";
        if (hour < 9) return "Ur awake already? such a retard";
        if (hour < 12) return "Retard moment";
        if (hour < 14) return "Go eat lunch retard";
        if (hour < 17) return "Retard playing minecraft";
        return "Time to sleep retard";
    }

    public enum Message {
        Welcome("Welcome"),
        Using("Using"),
        Time("Time"),
        Retarded_Time("Retarded Time"),
        Sussy("Sussy");

        private final String title;

        Message(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
