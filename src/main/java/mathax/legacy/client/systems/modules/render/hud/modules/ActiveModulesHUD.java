package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.renderer.Renderer2D;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.HUDElement;
import mathax.legacy.client.systems.modules.render.hud.HUDRenderer;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.utils.render.color.SettingColor;
import mathax.legacy.client.settings.*;

import java.util.ArrayList;
import java.util.List;

public class ActiveModulesHUD extends HUDElement {
    private final List<Module> modules = new ArrayList<>();

    private final Color rainbow = new Color(255, 255, 255);
    private double rainbowHue1, rainbowHue2;

    private double prevX, prevY;
    private double prevTextLength;
    private Color prevColor = new Color();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Sort> sort = sgGeneral.add(new EnumSetting.Builder<Sort>()
        .name("sort")
        .description("How to sort active modules.")
        .defaultValue(Sort.Biggest)
        .build()
    );

    private final Setting<Boolean> activeInfo = sgGeneral.add(new BoolSetting.Builder()
        .name("additional-info")
        .description("Shows additional info from the module next to the name in the active modules list.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ColorMode> colorMode = sgGeneral.add(new EnumSetting.Builder<ColorMode>()
        .name("color-mode")
        .description("What color to use for active modules.")
        .defaultValue(ColorMode.Category)
        .build()
    );

    private final Setting<SettingColor> flatColor = sgGeneral.add(new ColorSetting.Builder()
        .name("flat-color")
        .description("Color for flat color mode.")
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b))
        .visible(() -> colorMode.get() == ColorMode.Flat)
        .build()
    );

    private final Setting<Boolean> outlines = sgGeneral.add(new BoolSetting.Builder()
        .name("outlines")
        .description("Whether or not to render outlines")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> outlineWidth = sgGeneral.add(new IntSetting.Builder()
        .name("outline-width")
        .description("Outline width")
        .defaultValue(4)
        .min(1)
        .sliderMin(1).sliderMax(10)
        .visible(outlines::get)
        .build()
    );

    private final Setting<Double> rainbowSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("rainbow-speed")
        .description("Rainbow speed of rainbow color mode.")
        .defaultValue(0.0035)
        .sliderMax(0.1)
        .decimalPlaces(4)
        .visible(() -> colorMode.get() == ColorMode.Rainbow)
        .build()
    );

    private final Setting<Double> rainbowSpread = sgGeneral.add(new DoubleSetting.Builder()
        .name("rainbow-spread")
        .description("Rainbow spread of rainbow color mode.")
        .defaultValue(0.025)
        .sliderMax(0.05)
        .decimalPlaces(4)
        .visible(() -> colorMode.get() == ColorMode.Rainbow)
        .build()
    );

    public ActiveModulesHUD(HUD hud) {
        super(hud, "active-modules", "Displays your active modules.", true);
    }

    @Override
    public void update(HUDRenderer renderer) {
        if (Modules.get() == null) {
            box.setSize(renderer.textWidth("Active Modules"), renderer.textHeight());
            return;
        }

        modules.clear();

        for (Module module : Modules.get().getActive()) {
            if (module.isVisible()) modules.add(module);
        }

        modules.sort((o1, o2) -> {
            double _1 = getModuleWidth(renderer, o1);
            double _2 = getModuleWidth(renderer, o2);

            if (sort.get() == Sort.Smallest) {
                double temp = _1;
                _1 = _2;
                _2 = temp;
            }

            int a = Double.compare(_1, _2);
            if (a == 0) return 0;
            return a < 0 ? 1 : -1;
        });

        double width = 0;
        double height = 0;

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);

            width = Math.max(width, getModuleWidth(renderer, module));
            height += renderer.textHeight();
            if (i > 0) height += 2;
        }

        box.setSize(width, height);
    }

    @Override
    public void render(HUDRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (Modules.get() == null) {
            renderer.text("Active Modules", x, y, hud.color);
            return;
        }

        rainbowHue1 += rainbowSpeed.get() * renderer.delta;
        if (rainbowHue1 > 1) rainbowHue1 -= 1;
        else if (rainbowHue1 < -1) rainbowHue1 += 1;

        rainbowHue2 = rainbowHue1;

        prevX = x;
        prevY = y;
        Renderer2D.COLOR.begin();
        for (int i = 0; i < modules.size(); i++) {
            renderModule(renderer, modules, i, x + box.alignX(getModuleWidth(renderer, modules.get(i))), y);

            prevX = x + box.alignX(getModuleWidth(renderer, modules.get(i)));
            prevY = y;

            y += 2 + renderer.textHeight();
        }
        Renderer2D.COLOR.render(null);
    }

    private void renderModule(HUDRenderer renderer, List<Module> modules, int index, double x, double y) {
        Module module = modules.get(index);
        Color color = flatColor.get();

        ColorMode colorMode = this.colorMode.get();
        if (colorMode == ColorMode.Random) color = module.color;
        else if (colorMode == ColorMode.Rainbow) {
            rainbowHue2 += rainbowSpread.get();
            int c = java.awt.Color.HSBtoRGB((float) rainbowHue2, 1, 1);

            rainbow.r = Color.toRGBAR(c);
            rainbow.g = Color.toRGBAG(c);
            rainbow.b = Color.toRGBAB(c);

            color = rainbow;
        }

        else if (colorMode == ColorMode.Category) color = new Color(module.category.color);

        renderer.text(module.title, x, y, color);

        double textLength = renderer.textWidth(module.title);

        if (activeInfo.get()) {
            String info = module.getInfoString();
            if (info != null) {
                renderer.text(info, x + renderer.textWidth(module.title) + renderer.textWidth(" "), y, hud.secondaryColor.get());
                textLength += renderer.textWidth(" ") + renderer.textWidth(info);
            }
        }

        if (outlines.get()) {
            if (index == 0) {
                Renderer2D.COLOR.quad(x - 2 - outlineWidth.get(), y - 2, outlineWidth.get(), renderer.textHeight() + 4, prevColor, prevColor, color, color); // Left quad
                Renderer2D.COLOR.quad(x + textLength + 2, y - 2, outlineWidth.get(), renderer.textHeight() + 4, prevColor, prevColor, color, color); // Right quad

                Renderer2D.COLOR.quad(x - 2 - outlineWidth.get(), y - 2 - outlineWidth.get(), textLength + 4 + (outlineWidth.get() * 2), outlineWidth.get(), prevColor, prevColor, color, color); // Top quad

            } else if (index == modules.size() - 1) {
                Renderer2D.COLOR.quad(x - 2 - outlineWidth.get(), y, outlineWidth.get(), renderer.textHeight() + 2 + outlineWidth.get(), prevColor, prevColor, color, color); // Left quad
                Renderer2D.COLOR.quad(x + textLength + 2, y, outlineWidth.get(), renderer.textHeight() + 2 + outlineWidth.get(), prevColor, prevColor, color, color); // Right quad

                Renderer2D.COLOR.quad(x - 2 - outlineWidth.get(), y + renderer.textHeight() + 2, textLength + 4 + (outlineWidth.get() * 2), outlineWidth.get(), prevColor, prevColor, color, color); // Bottom quad
            }

            if (index > 0) {
                if (index < modules.size() - 1) {

                    Renderer2D.COLOR.quad(x - 2 - outlineWidth.get(), y, outlineWidth.get(), renderer.textHeight() + 2, prevColor, prevColor, color, color); // Left quad
                    Renderer2D.COLOR.quad(x + textLength + 2, y, outlineWidth.get(), renderer.textHeight() + 2, prevColor, prevColor, color, color); // Right quad

                }

                Renderer2D.COLOR.quad(Math.min(prevX, x) - 2 - outlineWidth.get(), Math.max(prevX, x) == x ? y : y - outlineWidth.get(),
                    (Math.max(prevX, x) - 2) - (Math.min(prevX, x) - 2 - outlineWidth.get()), outlineWidth.get(),
                    prevColor, prevColor, color, color); // Left inbetween quad

                Renderer2D.COLOR.quad(Math.min(prevX + prevTextLength, x + textLength) + 2, Math.min(prevX + prevTextLength, x + textLength) == x + textLength ? y : y - outlineWidth.get(),
                    (Math.max(prevX + prevTextLength, x + textLength) + 2 + outlineWidth.get()) - (Math.min(prevX + prevTextLength, x + textLength) + 2), outlineWidth.get(),
                    prevColor, prevColor, color, color); // Right inbetween quad
            }
        }

        prevTextLength = textLength;
        prevColor = color;
    }

    private double getModuleWidth(HUDRenderer renderer, Module module) {
        double width = renderer.textWidth(module.title);

        if (activeInfo.get()) {
            String info = module.getInfoString();
            if (info != null) width += renderer.textWidth(" ") + renderer.textWidth(info);
        }

        return width;
    }

    public enum Sort {
        Biggest,
        Smallest
    }

    public enum ColorMode {
        Category,
        Flat,
        Random,
        Rainbow
    }
}
