package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.ModuleListSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.movement.PacketFly;
import mathax.legacy.client.systems.modules.movement.Phase;
import mathax.legacy.client.systems.modules.movement.Step;
import mathax.legacy.client.systems.modules.movement.Tower;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class AntiDesync extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<List<Module>> modules = sgGeneral.add(new ModuleListSetting.Builder()
        .name("modules")
        .description("Determines which modules to ignore.")
        .defaultValue(PacketFly.class, Phase.class, Step.class, Tower.class)
        .build()
    );

    private ArrayList<Integer> teleportIDs;

    public AntiDesync() {
        super(Categories.Misc, Items.COMMAND_BLOCK, "anti-desync", "Stops you from desyncing with the server.");
    }

    @Override
    public void onActivate() {
        teleportIDs = new ArrayList<>();
    }

    @EventHandler
    private void onSentPacket(PacketEvent.Send event) {
        if (checkModules() && event.packet instanceof TeleportConfirmC2SPacket packet) teleportIDs.add(packet.getTeleportId());
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        if (!teleportIDs.isEmpty() && checkModules()) {
            mc.getNetworkHandler().sendPacket(new TeleportConfirmC2SPacket(teleportIDs.get(0)));
            teleportIDs.remove(0);
        }
    }

    private boolean checkModules() {
        List<Module> all = Modules.get().getList();

        for (Module module : modules.get()) {
            if (all.contains(module) && module.isActive()) return false;
        }

        return true;
    }
}