package mathax.legacy.client.systems.modules.render;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import mathax.legacy.client.events.render.Render3DEvent;
import mathax.legacy.client.renderer.ShapeMode;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.entity.EntityUtils;
import mathax.legacy.client.utils.player.PlayerUtils;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.utils.render.color.SettingColor;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;

public class ESP extends Module {
    private final Color lineColor = new Color();
    private final Color sideColor = new Color();

    private int count;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgColors = settings.createGroup("Colors");

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Rendering mode.")
        .defaultValue(Mode.Shader)
        .build()
    );

    public final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Lines)
        .build()
    );

    public final Setting<Integer> outlineWidth = sgGeneral.add(new IntSetting.Builder()
        .name("width")
        .description("The width of the shader outline.")
        .defaultValue(2)
        .min(1).max(10)
        .sliderMin(1).sliderMax(5)
        .visible(() -> mode.get() == Mode.Shader)
        .build()
    );

    public final Setting<Integer> fillOpacity = sgGeneral.add(new IntSetting.Builder()
        .name("fill-opacity")
        .description("The opacity of the shape fill.")
        .defaultValue(80)
        .min(0).max(255)
        .sliderMax(255)
        .build()
    );

    private final Setting<Double> fadeDistance = sgGeneral.add(new DoubleSetting.Builder()
        .name("fade-distance")
        .description("The distance from an entity where the color begins to fade.")
        .defaultValue(2)
        .min(0)
        .sliderMax(12)
        .build()
    );

    private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entites")
        .description("Select specific entities.")
        .defaultValue(Utils.asObject2BooleanOpenHashMap(EntityType.PLAYER))
        .build()
    );

    // Colors

    private final Setting<SettingColor> playersColor = sgColors.add(new ColorSetting.Builder()
        .name("players-color")
        .description("The other player's color.")
        .defaultValue(new SettingColor(255, 255, 255))
        .build()
    );


    public final Setting<SettingColor> selfColor = sgColors.add(new ColorSetting.Builder()
        .name("self-color")
        .description("The color of your ESP in Freecam.")
        .defaultValue(new SettingColor(0, 165, 255, 125))
        .build()
    );

    private final Setting<SettingColor> animalsColor = sgColors.add(new ColorSetting.Builder()
        .name("animals-color")
        .description("The animal's color.")
        .defaultValue(new SettingColor(25, 255, 25, 255))
        .build()
    );

    private final Setting<SettingColor> waterAnimalsColor = sgColors.add(new ColorSetting.Builder()
        .name("water-animals-color")
        .description("The water animal's color.")
        .defaultValue(new SettingColor(25, 25, 255, 255))
        .build()
    );

    private final Setting<SettingColor> monstersColor = sgColors.add(new ColorSetting.Builder()
        .name("monsters-color")
        .description("The monster's color.")
        .defaultValue(new SettingColor(255, 25, 25, 255))
        .build()
    );

    private final Setting<SettingColor> ambientColor = sgColors.add(new ColorSetting.Builder()
        .name("ambient-color")
        .description("The ambient's color.")
        .defaultValue(new SettingColor(25, 25, 25, 255))
        .build()
    );

    private final Setting<SettingColor> miscColor = sgColors.add(new ColorSetting.Builder()
        .name("misc-color")
        .description("The misc color.")
        .defaultValue(new SettingColor(175, 175, 175, 255))
        .build()
    );

    public ESP() {
        super(Categories.Render, Items.RED_STAINED_GLASS, "ESP", "Renders entities through walls.");
    }

    private void render(Render3DEvent event, Entity entity) {
        lineColor.set(getColor(entity));
        sideColor.set(lineColor).a(fillOpacity.get());

        double a = getFadeAlpha(entity);

        int prevLineA = lineColor.a;
        int prevSideA = sideColor.a;

        lineColor.a *= a;
        sideColor.a *= a;

        double x = (entity.getX() - entity.prevX) * event.tickDelta;
        double y = (entity.getY() - entity.prevY) * event.tickDelta;
        double z = (entity.getZ() - entity.prevZ) * event.tickDelta;

        Box box = entity.getBoundingBox();
        event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, sideColor, lineColor, shapeMode.get(), 0);

        lineColor.a = prevLineA;
        sideColor.a = prevSideA;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        count = 0;

        for (Entity entity : mc.world.getEntities()) {
            if ((!Modules.get().isActive(Freecam.class) && entity == mc.player) || !entities.get().getBoolean(entity.getType())) continue;
            if (!EntityUtils.isInRenderDistance(entity)) continue;

            if (mode.get() == Mode.Box) render(event, entity);
            count++;
        }
    }

    private double getFadeAlpha(Entity entity) {
        double dist = PlayerUtils.distanceTo(entity.getX() + entity.getWidth() / 2, entity.getY() + entity.getHeight() / 2, entity.getZ() + entity.getWidth() / 2);
        double fadeDist = fadeDistance.get().floatValue() * fadeDistance.get().floatValue();
        double alpha = 1;
        if (dist <= fadeDist) alpha = (float) (dist / fadeDist);
        if (alpha <= 0.075) alpha = 0;
        return alpha;
    }

    public Color getColor(Entity entity) {
        if (entity instanceof PlayerEntity) {
            if (entity.equals(MinecraftClient.getInstance().getCameraEntity())) {
                return PlayerUtils.getPlayerColor(((PlayerEntity) entity), selfColor.get());
            } else {
                return PlayerUtils.getPlayerColor(((PlayerEntity) entity), playersColor.get());
            }
        }

        return switch (entity.getType().getSpawnGroup()) {
            case CREATURE                                                  -> animalsColor.get();
            case WATER_AMBIENT, WATER_CREATURE, UNDERGROUND_WATER_CREATURE -> waterAnimalsColor.get();
            case MONSTER                                                   -> monstersColor.get();
            case AMBIENT                                                   -> ambientColor.get();
            default                                                        -> miscColor.get();
        };
    }

    // Outlines

    public boolean shouldDrawOutline(Entity entity) {
        return mode.get() == Mode.Shader && isActive() && getOutlineColor(entity) != null;
    }

    public Color getOutlineColor(Entity entity) {
        if (!entities.get().getBoolean(entity.getType())) return null;
        Color color = getColor(entity);
        double alpha = getFadeAlpha(entity);
        return lineColor.set(color).a((int) (alpha * 255));
    }

    @Override
    public String getInfoString() {
        return Integer.toString(count);
    }

    public enum Mode {
        Box,
        Shader
    }
}
