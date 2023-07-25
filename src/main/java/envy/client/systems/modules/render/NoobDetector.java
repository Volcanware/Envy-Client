package envy.client.systems.modules.render;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.render.Render3DEvent;
import envy.client.events.world.TickEvent;
import envy.client.renderer.ShapeMode;
import envy.client.settings.*;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.combat.CrystalAura;
import envy.client.utils.player.PlayerUtils;
import envy.client.utils.render.color.SettingColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

public class NoobDetector extends Module {
    private boolean isTargetANoob = false;

    private PlayerEntity target;

    private int count;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General

    private final Setting<Double> damageThreshold = sgGeneral.add(new DoubleSetting.Builder()
        .name("damage-threshold")
        .description("The threshold for Crystal Aura damage before Noob Detector begins rendering.")
        .defaultValue(6.0)
        .min(0.0)
        .sliderRange(0.0, 40.0)
        .build()
    );

    private final Setting<Boolean> chat = sgGeneral.add(new BoolSetting.Builder()
        .name("chat")
        .description("Notifies you in chat when the target is a noob.")
        .defaultValue(false)
        .build()
    );

    // Render

    private final Setting<Boolean> render = sgGeneral.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a box around the targets feet if it is a noob.")
        .defaultValue(false)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("Determines how the shapes are rendered.")
        .defaultValue(ShapeMode.Lines)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    public NoobDetector() {
        super(Categories.Render, Items.IRON_BOOTS, "noob-detector", "Checks if the Crystal Aura target is not burrowed, and isn't surrounded.");
    }

    @Override
    public boolean onActivate() {
        isTargetANoob = false;
        target = null;
        return false;
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (this.count >= 20) {
            for (Entity entity : mc.world.getEntities()) {
                if (entity.isAlive() && entity.distanceTo(mc.player) <= Modules.get().get(CrystalAura.class).targetRange.get() && entity != mc.player && PlayerUtils.isNoob(target)) {
                    isTargetANoob = true;
                    if (chat.get()) info("Your target is a noob!");
                    count = 0;
                    //new south wales
                }
            }
        }
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (render.get() && isTargetANoob) event.renderer.box(target.getBlockPos(), sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
