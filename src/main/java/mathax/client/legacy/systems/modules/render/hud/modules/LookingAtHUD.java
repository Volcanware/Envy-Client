package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHUDElement;
import mathax.client.legacy.utils.misc.Names;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class LookingAtHUD extends TripleTextHUDElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> position = sgGeneral.add(new BoolSetting.Builder()
        .name("position")
        .description("Displays crosshair target's position.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> waterLogged = sgGeneral.add(new BoolSetting.Builder()
        .name("waterlogged-status")
        .description("Displays if a block is waterlogged or not")
        .defaultValue(true)
        .build()
    );

    public LookingAtHUD(HUD hud) {
        super(hud, "looking-at", "Displays what entity or block you are looking at.", true);
    }

    @Override
    protected String getLeft() {
        return "Looking at: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return position.get() ? "Obsidian [0, 0, 0]" : "Obsidian";

        if (mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();

            String result = Names.get(mc.world.getBlockState(pos).getBlock());

            if (position.get()) {
                result += String.format(" (%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
            }

            if (waterLogged.get() && mc.world.getFluidState(pos).isIn(FluidTags.WATER)) {
                result += " (water logged)";
            }
        }

        else if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) mc.crosshairTarget).getEntity();

            String result;
            if (target instanceof PlayerEntity) result = ((PlayerEntity) target).getGameProfile().getName();
            else result = target.getEntityName();

            if (position.get()) {
                result += String.format(" (%d, %d, %d)", target.getBlockX(), target.getBlockY(), target.getBlockZ());
            }

            if (waterLogged.get() && target.isTouchingWater()) {
                result += " (in water)";
            }

            return result;
        }

        return "Nothing";
    }

    @Override
    public String getEnd() {
        return "";
    }
}
