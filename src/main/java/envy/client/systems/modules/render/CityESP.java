package envy.client.systems.modules.render;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.render.Render3DEvent;
import envy.client.events.world.TickEvent;
import envy.client.renderer.ShapeMode;
import envy.client.settings.ColorSetting;
import envy.client.settings.EnumSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.entity.EntityUtils;
import envy.client.utils.entity.SortPriority;
import envy.client.utils.entity.TargetUtils;
import envy.client.utils.render.color.SettingColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class CityESP extends Module {
    private BlockPos target;

    private final SettingGroup sgRender = settings.createGroup("Render");

    // Render

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("Determines how the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the rendering.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the rendering.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b, 255))
        .build()
    );

    public CityESP() {
        super(Categories.Render, Items.RED_STAINED_GLASS, "city-esp", "Displays blocks that can be broken in order to city another player.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) { //i think this works
        PlayerEntity targetEntity = TargetUtils.getPlayerTarget(mc.interactionManager.getReachDistance() + 2, SortPriority.Lowest_Distance);

        if (TargetUtils.isBadTarget(targetEntity, mc.interactionManager.getReachDistance() + 2)) target = null;
        else target = EntityUtils.getCityBlock(targetEntity);
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (target == null) return;

        event.renderer.box(target, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
