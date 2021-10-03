package mathax.legacy.client.systems.modules.render;

import mathax.legacy.client.events.render.Render3DEvent;
import mathax.legacy.client.settings.ColorSetting;
import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.mixin.ClientPlayerInteractionManagerAccessor;
import mathax.legacy.client.mixin.WorldRendererAccessor;
import mathax.legacy.client.renderer.ShapeMode;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.utils.render.color.SettingColor;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

import java.util.Map;

public class BreakIndicators extends Module {
    private final Color cSides = new Color();
    private final Color cLines = new Color();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> startColor = sgGeneral.add(new ColorSetting.Builder()
        .name("start-color")
        .description("The color for the non-broken block.")
        .defaultValue(new SettingColor(25, 250, 25, 150))
        .build()
    );

    private final Setting<SettingColor> endColor = sgGeneral.add(new ColorSetting.Builder()
        .name("end-color")
        .description("The color for the fully-broken block.")
        .defaultValue(new SettingColor(255, 25, 25, 150))
        .build()
    );

    public BreakIndicators() {
        super(Categories.Render, Items.IRON_PICKAXE, "break-indicators", "Renders the progress of a block being broken.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        Map<Integer, BlockBreakingInfo> blocks = ((WorldRendererAccessor) mc.worldRenderer).getBlockBreakingInfos();

        float ownBreakingStage = ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getBreakingProgress();
        BlockPos ownBreakingPos = ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getCurrentBreakingBlockPos();

        blocks.values().forEach(info -> {
            BlockPos pos = info.getPos();
            int stage = info.getStage();

            BlockState state = mc.world.getBlockState(pos);
            VoxelShape shape = state.getOutlineShape(mc.world, pos);
            if (shape.isEmpty()) return;

            Box orig = shape.getBoundingBox();
            Box box = orig;

            double shrinkFactor = (9 - (stage + 1)) / 9d;
            if (ownBreakingPos != null && ownBreakingStage > 0 && ownBreakingPos.equals(pos)) {
                shrinkFactor = 1d - ownBreakingStage;
            }

            double progress = 1d - shrinkFactor;

            box = box.shrink(
                    box.getXLength() * shrinkFactor,
                    box.getYLength() * shrinkFactor,
                    box.getZLength() * shrinkFactor
            );

            double xShrink = (orig.getXLength() * shrinkFactor) / 2;
            double yShrink = (orig.getYLength() * shrinkFactor) / 2;
            double zShrink = (orig.getZLength() * shrinkFactor) / 2;

            double x1 = pos.getX() + box.minX + xShrink;
            double y1 = pos.getY() + box.minY + yShrink;
            double z1 = pos.getZ() + box.minZ + zShrink;
            double x2 = pos.getX() + box.maxX + xShrink;
            double y2 = pos.getY() + box.maxY + yShrink;
            double z2 = pos.getZ() + box.maxZ + zShrink;

            // Gradient
            Color c1Sides = startColor.get().copy().a(startColor.get().a / 2);
            Color c2Sides = endColor.get().copy().a(endColor.get().a / 2);

            cSides.set(
                    (int) Math.round(c1Sides.r + (c2Sides.r - c1Sides.r) * progress),
                    (int) Math.round(c1Sides.g + (c2Sides.g - c1Sides.g) * progress),
                    (int) Math.round(c1Sides.b + (c2Sides.b - c1Sides.b) * progress),
                    (int) Math.round(c1Sides.a + (c2Sides.a - c1Sides.a) * progress)
            );

            Color c1Lines = startColor.get();
            Color c2Lines = endColor.get();

            cLines.set(
                    (int) Math.round(c1Lines.r + (c2Lines.r - c1Lines.r) * progress),
                    (int) Math.round(c1Lines.g + (c2Lines.g - c1Lines.g) * progress),
                    (int) Math.round(c1Lines.b + (c2Lines.b - c1Lines.b) * progress),
                    (int) Math.round(c1Lines.a + (c2Lines.a - c1Lines.a) * progress)
            );

            event.renderer.box(x1, y1, z1, x2, y2, z2, cSides, cLines, shapeMode.get(), 0);
        });
    }
}
