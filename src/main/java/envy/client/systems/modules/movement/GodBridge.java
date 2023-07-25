package envy.client.systems.modules.movement;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public class GodBridge extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    final Direction[] allowedSides = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

    public GodBridge() {
        super(Categories.Movement, Items.RED_WOOL, "GodBridge", "Puts you in the right position to god bridge.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        mc.player.setYaw(mc.player.getMovementDirection().asRotation());
        if(mc.player.getPitch() > 83 || mc.player.getPitch() < 81) {
            mc.player.setPitch(82.5f);
        }
        HitResult hr = mc.crosshairTarget;
        if (hr.getType() == HitResult.Type.BLOCK && hr instanceof BlockHitResult result) {
            if(Arrays.stream(allowedSides).anyMatch(direction -> direction == result.getSide()) && mc.player.getMainHandStack().getItem() instanceof BlockItem){
                mc.options.useKey.setPressed(true);
            }
        }
    }
}
