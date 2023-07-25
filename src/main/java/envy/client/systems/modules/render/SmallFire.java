package envy.client.systems.modules.render;

import envy.client.settings.DoubleSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3f;

public class SmallFire extends Module {
    private final SettingGroup sgScale = settings.createGroup("Scale");
    private final SettingGroup sgPosition = settings.createGroup("Position");

    // Scale

    private final Setting<Double> scaleX = sgScale.add(new DoubleSetting.Builder()
        .name("scale-x")
        .description("Zoom for fire on screen.")
        .defaultValue(0.0)
        .sliderRange(0.05, 1.0)
        .build()
    );

    private final Setting<Double> scaleY = sgScale.add(new DoubleSetting.Builder()
        .name("scale-y")
        .description("Zoom for fire on screen.")
        .defaultValue(0.2)
        .sliderRange(0.05, 1.0)
        .build()
    );

    private final Setting<Double> scaleZ = sgScale.add(new DoubleSetting.Builder()
        .name("Scale Z")
        .description("Zoom for fire on screen.")
        .defaultValue(0.0)
        .sliderRange(0.05, 1.0)
        .build()
    );

    // Position

    private final Setting<Double> positionX = sgPosition.add(new DoubleSetting.Builder()
        .name("position-x")
        .description("Offset for fire on screen.")
        .defaultValue(0.0)
        .sliderRange(-10.0, 10.0)
        .build()
    );

    private final Setting<Double> positionY = sgPosition.add(new DoubleSetting.Builder()
        .name("position-y")
        .description("Offset for fire on screen.")
        .defaultValue(-1.0)
        .sliderRange(-10.0, 10.0)
        .build()
    );

    private final Setting<Double> positionZ = sgPosition.add(new DoubleSetting.Builder()
        .name("Position Z")
        .description("Offset for fire on screen.")
        .defaultValue(0.0)
        .sliderRange(-10.0, 10.0)
        .build()
    );

    public SmallFire() {
        super(Categories.Render, Items.SPYGLASS, "small-fire", "Smalls fire on screen.");
    }

    public Vec3f getFireScale() { //your fired
        return new Vec3f(scaleX.get().floatValue(), scaleY.get().floatValue(), scaleZ.get().floatValue());
    }

    public Vec3f getFirePosition() {
        return new Vec3f(scaleX.get().floatValue(), scaleY.get().floatValue(), scaleZ.get().floatValue());
    }
}
