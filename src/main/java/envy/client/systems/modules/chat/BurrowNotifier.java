package envy.client.systems.modules.chat;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.EnumSetting;
import envy.client.settings.IntSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.config.Config;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.misc.NotificationMode;
import envy.client.utils.player.PlayerUtils;
import envy.client.utils.render.ToastSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class BurrowNotifier extends Module {
    public static List<PlayerEntity> burrowedPlayers = new ArrayList<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<NotificationMode> mode = sgGeneral.add(new EnumSetting.Builder<NotificationMode>()
        .name("mode")
        .description("Determines how to notify you when someone burrows.")
        .defaultValue(NotificationMode.Chat)
        .build()
    );

    private final Setting<NotificationType> notificationType = sgGeneral.add(new EnumSetting.Builder<NotificationType>()
        .name("type")
        .description("Determines when to notify you.")
        .defaultValue(NotificationType.Both)
        .build()
    );

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range")
        .description("How far away from you to check for burrowed players.")
        .defaultValue(3)
        .min(0)
        .sliderMax(15)
        .build()
    );

    public BurrowNotifier() {
        super(Categories.Chat, Items.OBSIDIAN, "burrow-notifier", "Notifies you when a player burrows in your render distance.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (isBurrowValid(player)) {
                burrowedPlayers.add(player);

                if (notificationType.get() == NotificationType.Not_Burrowed) sendBurrowedNotification(player.getGameProfile().getName());
            }

            if (burrowedPlayers.contains(player) && !PlayerUtils.isBurrowed(player, true)) {
                burrowedPlayers.remove(player);

                if (notificationType.get() == NotificationType.Burrowed) sendNotBurrowedNotification(player.getGameProfile().getName());
            }
        }
    }

    private boolean isBurrowValid(PlayerEntity p) {
        if (p == mc.player) return false;
        return mc.player.distanceTo(p) <= range.get() && !burrowedPlayers.contains(p) && PlayerUtils.isBurrowed(p, true) && !PlayerUtils.isPlayerMoving(p);
    }

    private void sendBurrowedNotification(String playerName) {
        switch (mode.get()) {
            case Chat -> warning("(highlight)%s(default) is burrowed!", playerName);
            case Toast -> mc.getToastManager().add(new ToastSystem(Items.BEDROCK, Envy.INSTANCE.MATHAX_COLOR_INT, "Burrow Notifier", null, playerName + " is burrowed!", Config.get().toastDuration.get()));
            case Both -> {
                warning("(highlight)%s(default) is burrowed!", playerName);
                mc.getToastManager().add(new ToastSystem(Items.BEDROCK, Envy.INSTANCE.MATHAX_COLOR_INT, "Burrow Notifier", null, playerName + " is burrowed!", Config.get().toastDuration.get()));
            }
        }
    }

    private void sendNotBurrowedNotification(String playerName) {
        switch (mode.get()) {
            case Chat -> warning("(highlight)%s(default) is no longer burrowed!", playerName);
            case Toast -> mc.getToastManager().add(new ToastSystem(Items.BEDROCK, Envy.INSTANCE.MATHAX_COLOR_INT, "Burrow Notifier", null, playerName + " is no longer burrowed!", Config.get().toastDuration.get()));
            case Both -> {
                warning("(highlight)%s(default) is no longer burrowed!", playerName);
                mc.getToastManager().add(new ToastSystem(Items.BEDROCK, Envy.INSTANCE.MATHAX_COLOR_INT, "Burrow Notifier", null, playerName + " is no longer burrowed!", Config.get().toastDuration.get()));
            }
        }
    }

    public enum NotificationType {
        Burrowed("Burrowed"),
        Not_Burrowed("Not Burrowed"),
        Both("Both");

        private final String title;

        NotificationType(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
