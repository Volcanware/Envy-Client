package mathax.client.systems.modules.chat;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.IntSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.player.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class BurrowNotifier extends Module {
    public static List<PlayerEntity> burrowedPlayers = new ArrayList<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range")
        .description("How far away from you to check for burrowed players.")
        .defaultValue(3)
        .min(0)
        .sliderMax(15)
        .build()
    );

    // TODO: Notify modes

    public BurrowNotifier() {
        super(Categories.Chat, Items.OBSIDIAN, "burrow-notifier", "Notifies you when a player burrows in your render distance.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (isBurrowValid(player)) {
                burrowedPlayers.add(player);
                warning("(highlight)%s(default) is burrowed!", player.getEntityName());
            }

            if (burrowedPlayers.contains(player) && !PlayerUtils.isBurrowed(player, true)) {
                burrowedPlayers.remove(player);
                warning("(highlight)%s(default) is no longer burrowed.", player.getEntityName());
            }
        }
    }

    private boolean isBurrowValid(PlayerEntity p) {
        if (p == mc.player) return false;
        return mc.player.distanceTo(p) <= range.get() && !burrowedPlayers.contains(p) && PlayerUtils.isBurrowed(p, true) && !PlayerUtils.isPlayerMoving(p);
    }
}
