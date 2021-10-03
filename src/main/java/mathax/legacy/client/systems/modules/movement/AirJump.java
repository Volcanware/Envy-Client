package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.events.mathax.KeyEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.render.Freecam;
import mathax.legacy.client.utils.misc.input.KeyAction;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.item.Items;

public class AirJump extends Module {
    private int level;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

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
    public void onActivate() {
        level = mc.player.getBlockPos().getY();
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        if (Modules.get().isActive(Freecam.class) || mc.currentScreen != null || mc.player.isOnGround()) return;

        if (event.action != KeyAction.Press) return;

        if (mc.options.keyJump.matchesKey(event.key, 0)) {
            level = mc.player.getBlockPos().getY();
            mc.player.jump();
        }
        else if (mc.options.keySneak.matchesKey(event.key, 0)) {
            level--;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (Modules.get().isActive(Freecam.class) || mc.player.isOnGround()) return;

        if (maintainLevel.get() && mc.player.getBlockPos().getY() == level && mc.options.keyJump.isPressed()) {
            mc.player.jump();
        }
    }
}
