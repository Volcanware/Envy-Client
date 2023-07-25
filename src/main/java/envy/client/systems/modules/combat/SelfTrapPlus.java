package envy.client.systems.modules.combat;

import envy.client.eventbus.EventHandler;
import envy.client.events.packets.PacketEvent;
import envy.client.events.world.TickEvent;
import envy.client.settings.BoolSetting;
import envy.client.settings.EnumSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.render.WallHack;
import envy.client.utils.player.FindItemResult;
import envy.client.utils.player.InvUtils;
import envy.client.utils.world.BlockUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.util.math.BlockPos;

/*/------------------------------------------------------------------------------------------------------------------/*/
/*/ Used from Karasic Meteor Addon and edited a bit by Matejko06                                                     /*/
/*/ https://github.com/Kiriyaga7615/karasic/blob/main/src/main/java/bedtrap/kiriyaga/karasic/modules/selftrapik.java /*/
/*/------------------------------------------------------------------------------------------------------------------/*/

public class SelfTrapPlus extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> placement = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines where to place blocks.")
        .defaultValue(Mode.Top)
        .build()
    );

    private final Setting<Boolean> selfToggle = sgGeneral.add(new BoolSetting.Builder()
        .name("self-toggle")
        .description("Turns off after placing all blocks.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> onlyHole = sgGeneral.add(new BoolSetting.Builder()
        .name("only-hole")
        .description("Toggles when not in a hole.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> tpToggle = sgGeneral.add(new BoolSetting.Builder()
        .name("tp-toggle")
        .description("Toggles after teleporting.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Sends rotation packets to the server when placing.")
        .defaultValue(false)
        .build()
    );

    public SelfTrapPlus(){
        super(Categories.Combat, Items.OBSIDIAN, "self-trap+", "Places obsidian above your head.");
    }

    @Override
    public boolean onActivate() {
        if (Modules.get().isActive(WallHack.class)) {
            error("(highlight)Self Trap(default) was enabled while enabling (highlight)Self Trap+(default), disabling (highlight)Self Trap(default)...");
            Modules.get().get(SelfTrap.class).toggle();
        }
        return false;
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof TeleportConfirmC2SPacket && tpToggle.get()) toggle();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        FindItemResult obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);

        if (!obsidian.found()) {
            error("No obsidian in hotbar, disabling...");
            toggle();
            return;
        }

        if (onlyHole.get() && !check(mc.player)) return;

        BlockPos pos = mc.player.getBlockPos();
        switch (placement.get()) {
            case Full -> {
                place(pos.add(0, 2, 0));
                place(pos.add(1, 1, 0));
                place(pos.add(-1, 1, 0));
                place(pos.add(0, 1, 1));
                place(pos.add(0, 1, -1));
            }
            case Full_Plus -> {
                place(pos.add(0, 2, 0));
                place(pos.add(0, 3, 0));
                place(pos.add(1, 1, 0));
                place(pos.add(-1, 1, 0));
                place(pos.add(0, 1, 1));
                place(pos.add(0, 1, -1));
            }
            case Full_Plus_Plus -> {
                place(pos.add(0, 3, 0));
                place(pos.add(1, 2, 0));
                place(pos.add(-1, 2, 0));
                place(pos.add(0, 2, 1));
                place(pos.add(0, 2, -1));
                place(pos.add(1, 1, 0));
                place(pos.add(-1, 1, 0));
                place(pos.add(0, 1, 1));
                place(pos.add(0, 1, -1));
            }
            case Top -> place(pos.add(0, 2, 0));
        }

        if (selfToggle.get()) toggle();
    }

    private boolean check(LivingEntity target) {
        assert mc.world != null;
        return !mc.world.getBlockState(target.getBlockPos().add(1, 0, 0)).isAir() && !mc.world.getBlockState(target.getBlockPos().add(-1, 0, 0)).isAir() && !mc.world.getBlockState(target.getBlockPos().add(0, 0, 1)).isAir() && !mc.world.getBlockState(target.getBlockPos().add(0, 0, -1)).isAir();
    }

    private void place(BlockPos pos){
        FindItemResult obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);
        BlockUtils.place(pos, obsidian, rotate.get(), 50);
    }

    public enum Mode {
        Full("Full"),
        Full_Plus("Full+"),
        Full_Plus_Plus("Full++"),
        Top("Top");

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
