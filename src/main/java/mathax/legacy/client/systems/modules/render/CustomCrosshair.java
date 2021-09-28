package mathax.legacy.client.systems.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.render.Render2DEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.misc.Timer;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.utils.render.color.SettingColor;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

/*/                                                                                                                      /*/
/*/ Used from Jex Client and highly modified by Matejko06                                                                /*/
/*/ https://github.com/DustinRepo/JexClient/blob/main/src/main/java/me/dustin/jex/feature/mod/impl/render/Crosshair.java /*/
/*/                                                                                                                      /*/

public class CustomCrosshair extends Module {
    private float x, y;
    private int spinAmount;
    private final Timer timer = new Timer();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgDot = settings.createGroup("Dot");
    private final SettingGroup sgOutline = settings.createGroup("Outline");
    private final SettingGroup sgSpin = settings.createGroup("Spin");
    private final SettingGroup sgAttackIndicator = settings.createGroup("Attack Indicator");

    // General

    private final Setting<Double> thickness = sgGeneral.add(new DoubleSetting.Builder()
        .name("thickness")
        .description("The thickness of the crosshair.")
        .defaultValue(3)
        .min(0)
        .sliderMin(0)
        .sliderMax(25)
        .build()
    );

    private final Setting<Double> size = sgGeneral.add(new DoubleSetting.Builder()
        .name("size")
        .description("The size of the crosshair.")
        .defaultValue(15)
        .min(0)
        .sliderMin(0)
        .sliderMax(50)
        .build()
    );

    private final Setting<Double> gap = sgGeneral.add(new DoubleSetting.Builder()
        .name("gap")
        .description("The gap in the crosshair.")
        .defaultValue(4)
        .min(0)
        .sliderMin(0)
        .sliderMax(30)
        .build()
    );

    private final Setting<SettingColor> crosshairColor = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("The color of the crosshair.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    // Dot

    private final Setting<Boolean> dot = sgDot.add(new BoolSetting.Builder()
        .name("dot")
        .description("Renders a dot in the middle of the crosshair.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> dotSize = sgDot.add(new DoubleSetting.Builder()
        .name("dot-size")
        .description("The size of the dot.")
        .defaultValue(1)
        .min(0)
        .sliderMin(0)
        .sliderMax(15)
        .visible(dot::get)
        .build()
    );

    // Outline

    private final Setting<Double> outline = sgOutline.add(new DoubleSetting.Builder()
        .name("enabled")
        .description("The outline of the crosshair.")
        .defaultValue(2)
        .min(0)
        .sliderMin(0)
        .sliderMax(10)
        .build()
    );

    private final Setting<SettingColor> outlineColor = sgOutline.add(new ColorSetting.Builder()
        .name("outline-color")
        .description("The color of the crosshair outline.")
        .defaultValue(new SettingColor(0, 0, 0))
        .build()
    );

    // Spin

    private final Setting<Boolean> spin = sgSpin.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Makes the crosshair spin.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> spinSpeed = sgSpin.add(new IntSetting.Builder()
        .name("speed")
        .description("The spin speed.")
        .defaultValue(2)
        .min(1)
        .sliderMin(1)
        .sliderMax(25)
        .build()
    );

    // Attack Indicator

    private final Setting<SettingColor> indicatorColor = sgAttackIndicator.add(new ColorSetting.Builder()
        .name("color")
        .description("The color of attack indicator.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    private final Setting<Double> indicatorOutline = sgOutline.add(new DoubleSetting.Builder()
        .name("outline")
        .description("The outline of the indicator.")
        .defaultValue(2)
        .min(0)
        .sliderMin(0)
        .sliderMax(10)
        .build()
    );

    private final Setting<SettingColor> indicatorOutlineColor = sgAttackIndicator.add(new ColorSetting.Builder()
        .name("outline-color")
        .description("The color of the attack indicator outline.")
        .defaultValue(new SettingColor(0, 0, 0))
        .build()
    );


    public CustomCrosshair() {
        super(Categories.Render, Items.COMPASS, "custom-crosshair", "Renders a customizable crosshair instead of the Minecraft one");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        x = mc.getWindow().getWidth() / 2;
        y = mc.getWindow().getHeight() / 2;
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        MatrixStack matrixStack = event.matrixStack;

        // Spin
        if (spin.get()) {
            matrixStack.push();
            matrixStack.translate(x, y, 0);
            matrixStack.multiply(new Quaternion(new Vec3f(0F, 0F, 1F), spinAmount, true));
            matrixStack.translate(-x, -y, 0);
        }

        // Dot
        if (dot.get()) fillAndBorder(matrixStack, x + dotSize.get().floatValue(), y + dotSize.get().floatValue(), x - dotSize.get().floatValue(), y - dotSize.get().floatValue(), Color.fromRGBA(outlineColor.get().r, outlineColor.get().g, outlineColor.get().b, outlineColor.get().a), Color.fromRGBA(crosshairColor.get().r, crosshairColor.get().g, crosshairColor.get().b, crosshairColor.get().a), outline.get().floatValue());

        // Lines
        fillAndBorder(matrixStack, x - gap.get().floatValue() - size.get().floatValue() - thickness.get().floatValue(), y - thickness.get().floatValue(), x - gap.get().floatValue() - thickness.get().floatValue(), y + thickness.get().floatValue(), Color.fromRGBA(outlineColor.get().r, outlineColor.get().g, outlineColor.get().b, outlineColor.get().a), Color.fromRGBA(crosshairColor.get().r, crosshairColor.get().g, crosshairColor.get().b, crosshairColor.get().a), outline.get().floatValue());
        fillAndBorder(matrixStack, x + gap.get().floatValue() + thickness.get().floatValue(), y - thickness.get().floatValue(), x + gap.get().floatValue() + size.get().floatValue() + thickness.get().floatValue(), y + thickness.get().floatValue(), Color.fromRGBA(outlineColor.get().r, outlineColor.get().g, outlineColor.get().b, outlineColor.get().a), Color.fromRGBA(crosshairColor.get().r, crosshairColor.get().g, crosshairColor.get().b, crosshairColor.get().a), outline.get().floatValue());
        fillAndBorder(matrixStack, x - thickness.get().floatValue(), y - gap.get().floatValue() - size.get().floatValue() - thickness.get().floatValue(), x + thickness.get().floatValue(), y - gap.get().floatValue() - thickness.get().floatValue(), Color.fromRGBA(outlineColor.get().r, outlineColor.get().g, outlineColor.get().b, outlineColor.get().a), Color.fromRGBA(crosshairColor.get().r, crosshairColor.get().g, crosshairColor.get().b, crosshairColor.get().a), outline.get().floatValue());
        fillAndBorder(matrixStack, x - thickness.get().floatValue(), y + gap.get().floatValue() + thickness.get().floatValue(), x + thickness.get().floatValue(), y + gap.get().floatValue() + size.get().floatValue() + thickness.get().floatValue(), Color.fromRGBA(outlineColor.get().r, outlineColor.get().g, outlineColor.get().b, outlineColor.get().a), Color.fromRGBA(crosshairColor.get().r, crosshairColor.get().g, crosshairColor.get().b, crosshairColor.get().a), outline.get().floatValue());

        if (spin.get()) {
            matrixStack.pop();
        }

        // Attack Indicator
        if (mc.options.attackIndicator.equals(AttackIndicator.CROSSHAIR) && mc.player.getAttackCooldownProgress(0) < 1) {
            float width = 30;
            if (mc.player.getAttackCooldownProgress(0) > 0) fillAndBorder(matrixStack, x - 15, y + gap.get().floatValue() + size.get().floatValue() + thickness.get().floatValue() + 10, x - 15 + (width * mc.player.getAttackCooldownProgress(0)), y + gap.get().floatValue() + size.get().floatValue() + thickness.get().floatValue() + 14, Color.fromRGBA(indicatorOutlineColor.get().r, indicatorOutlineColor.get().g, indicatorOutlineColor.get().b, indicatorOutlineColor.get().a), Color.fromRGBA(indicatorColor.get().r, indicatorColor.get().g, indicatorColor.get().b, indicatorColor.get().a), indicatorOutline.get().floatValue());
            fillAndBorder(matrixStack, x - 15, y + gap.get().floatValue() + size.get().floatValue() + thickness.get().floatValue() + 10, x + 15, y + gap.get().floatValue() + size.get().floatValue() + thickness.get().floatValue() + 14, Color.fromRGBA(indicatorOutlineColor.get().r, indicatorOutlineColor.get().g, indicatorOutlineColor.get().b, indicatorOutlineColor.get().a), Color.fromRGBA(indicatorColor.get().r, indicatorColor.get().g, indicatorColor.get().b, indicatorColor.get().a), indicatorOutline.get().floatValue());
        }

        if (!timer.passedMs(20 / spinSpeed.get())) return;

        timer.reset();
        spinAmount += spinSpeed.get();

        if (spinAmount > 360)
            spinAmount -= 360;
    }

    // Rendering

    public void fillAndBorder(MatrixStack matrixStack, float left, float top, float right, float bottom, int bcolor, int icolor, float f) {
        fill(matrixStack, left + f, top + f, right - f, bottom - f, icolor);
        fill(matrixStack, left, top, left + f, bottom, bcolor);
        fill(matrixStack, left + f, top, right, top + f, bcolor);
        fill(matrixStack, left + f, bottom - f, right, bottom, bcolor);
        fill(matrixStack, right - f, top + f, right, bottom - f, bcolor);
    }

    public void fill(MatrixStack matrixStack, float x1, float y1, float x2, float y2, int color) {
        Matrix4f matrix = matrixStack.peek().getModel();
        float j;
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }

        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }

        float f = (color >> 24 & 255) / 255.0F;
        float g = (color >> 16 & 255) / 255.0F;
        float h = (color >> 8 & 255) / 255.0F;
        float k = (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x1, y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, x2, y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
