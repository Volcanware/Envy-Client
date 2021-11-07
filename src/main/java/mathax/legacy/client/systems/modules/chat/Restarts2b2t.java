package mathax.legacy.client.systems.modules.chat;

import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.config.Config;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.render.ToastSystem;
import mathax.legacy.client.utils.render.color.Color;
import net.minecraft.item.Items;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Restarts2b2t extends Module {
    private static final Color GOLD = new Color(215, 165, 35, 255);
    private static final int GOLD_INT = Color.fromRGBA(GOLD.r, GOLD.g, GOLD.b, GOLD.a);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines how to notify you.")
        .defaultValue(Mode.Both)
        .build()
    );

    public Restarts2b2t() {
        super(Categories.Chat, Items.COMMAND_BLOCK, "2b2t-restarts", "Notifies you when 2b2t is going to restart.");
    }

    @Override
    public void onActivate() {
        module.start();
    }

    @Override
    public void onDeactivate() {
        module.interrupt();
    }

    Thread module = new Thread(() -> {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                InputStream connection = new URL("http://crystalpvp.ru/restarts/fetch").openStream();
                String data = parseValue(new BufferedReader(new InputStreamReader(connection, StandardCharsets.UTF_8)).readLine());
                String time = parseValueTime(new BufferedReader(new InputStreamReader(connection, StandardCharsets.UTF_8)).readLine());
                if (!data.equalsIgnoreCase("None")) sendNotification(data, time);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    private String parseValue(String value) {
        String message;
        if (value.equalsIgnoreCase("m")) message = "(highlight)2b2t(default) is going to restart in (highlight)" + StringUtils.chop(value) + " minutes(default)!";
        else if (value.equalsIgnoreCase("s")) message = "(highlight)2b2t(default) is going to restart in (highlight)15 seconds(default)!";
        else if (value.equalsIgnoreCase("now")) message = "(highlight)2b2t(default) is restarting!";
        else message = "None";
        return message;
    }

    private String parseValueTime(String value) {
        String time = "ERROR";
        if (value.equalsIgnoreCase("m")) time = StringUtils.chop(value);
        return time;
    }

    private void sendNotification(String message, String time) {
        switch (mode.get()) {
            case Chat -> info(message);
            case Toast -> sendToast(message, time);
            case Both -> {
                info(message);
                sendToast(message, time);
            }
        }
    }

    private void sendToast(String message, String time) {
        if (message.contains("minutes")) mc.getToastManager().add(new ToastSystem(Items.COMMAND_BLOCK, GOLD_INT, "2b2t Restarts", null, "Restarting in " + time + " minutes", Config.get().toastDuration));
        else if (message.contains("seconds")) mc.getToastManager().add(new ToastSystem(Items.COMMAND_BLOCK, GOLD_INT, "2b2t Restarts", null, "Restarting in 15 seconds", Config.get().toastDuration));
        else if (message.contains("restarting")) mc.getToastManager().add(new ToastSystem(Items.COMMAND_BLOCK, GOLD_INT, "2b2t Restarts", null, "Restarting", Config.get().toastDuration));
    }

    public enum Mode {
        Chat,
        Toast,
        Both
    }
}
