package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.mathax.KeyEvent;
import envy.client.events.world.TickEvent;
import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.render.Freecam;
import envy.client.utils.misc.input.KeyAction;
import net.minecraft.item.Items;

public class AirJump extends Module {
    private int level;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> maintainLevel = sgGeneral.add(new BoolSetting.Builder()
        .name("maintain-level")
        .description("Maintains your current Y level when holding the jump key.")
        .defaultValue(false)
        .build()
    );

    public AirJump() {
        super(Categories.Movement, Items.BARRIER, "air-jump", "Lets you jump in the air.");
    }

    @Override
    public boolean onActivate() {
        level = mc.player.getBlockPos().getY();
        return false;
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (Modules.get().isActive(Freecam.class) || mc.currentScreen != null || mc.player.isOnGround()) return;

        if (event.action != KeyAction.Press) return;

        if (mc.options.jumpKey.matchesKey(event.key, 0)) {
            level = mc.player.getBlockPos().getY();
            mc.player.jump();
        } else if (mc.options.sneakKey.matchesKey(event.key, 0)) level--;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (Modules.get().isActive(Freecam.class) || mc.player.isOnGround()) return;

        if (maintainLevel.get() && mc.player.getBlockPos().getY() == level && mc.options.jumpKey.isPressed()) mc.player.jump();
    }
}
