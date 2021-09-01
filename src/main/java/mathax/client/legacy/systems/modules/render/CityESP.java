package mathax.client.legacy.systems.modules.render;

import mathax.client.legacy.events.render.Render3DEvent;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.settings.ColorSetting;
import mathax.client.legacy.settings.EnumSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.renderer.ShapeMode;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.utils.entity.EntityUtils;
import mathax.client.legacy.utils.entity.SortPriority;
import mathax.client.legacy.utils.entity.TargetUtils;
import mathax.client.legacy.utils.render.color.SettingColor;
import mathax.client.legacy.bus.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class CityESP extends Module {
    private final SettingGroup sgRender = settings.createGroup("Render");

    // Render

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the rendering.")
        .defaultValue(new SettingColor(230, 75, 100, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the rendering.")
        .defaultValue(new SettingColor(230, 75, 100, 255))
        .build()
    );

    private BlockPos target;

    public CityESP() {
        super(Categories.Render, "city-ESP", "Displays blocks that can be broken in order to city another player.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        PlayerEntity targetEntity = TargetUtils.getPlayerTarget(mc.interactionManager.getReachDistance() + 2, SortPriority.LowestDistance);

        if (TargetUtils.isBadTarget(targetEntity, mc.interactionManager.getReachDistance() + 2)) {
            target = null;
        } else {
            target = EntityUtils.getCityBlock(targetEntity);
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (target == null) return;

        event.renderer.box(target, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
