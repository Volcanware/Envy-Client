package mathax.client.systems.modules.render;

import mathax.client.MatHax;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.render.Render3DEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.misc.Pool;
import mathax.client.utils.render.color.SettingColor;
import net.minecraft.item.Items;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayDeque;
import java.util.Queue;

public class Breadcrumbs extends Module {
    private final Pool<Section> sectionPool = new Pool<>(Section::new);
    private final Queue<Section> sections = new ArrayDeque<>();

    private Section section;

    private DimensionType lastDimension;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("The color of the breadcrumbs trail.")
        .defaultValue(new SettingColor(MatHax.INSTANCE.MATHAX_COLOR.r, MatHax.INSTANCE.MATHAX_COLOR.g, MatHax.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    private final Setting<Integer> maxSections = sgGeneral.add(new IntSetting.Builder()
        .name("max-sections")
        .description("The maximum number of sections.")
        .defaultValue(1000)
        .min(1)
        .sliderRange(1, 5000)
        .build()
    );

    private final Setting<Double> sectionLength = sgGeneral.add(new DoubleSetting.Builder()
        .name("section-length")
        .description("The section length in blocks.")
        .defaultValue(0.5)
        .min(0)
        .sliderRange(0, 1)
        .build()
    );

    public Breadcrumbs() {
        super(Categories.Render, Items.BREAD, "breadcrumbs", "Displays a trail behind where you have walked.");
    }

    @Override
    public void onActivate() {
        section = sectionPool.get();
        section.set1();

        lastDimension = mc.world.getDimension();
    }

    @Override
    public void onDeactivate() {
        for (Section section : sections) sectionPool.free(section);
        sections.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (lastDimension != mc.world.getDimension()) {
            for (Section sec : sections) sectionPool.free(sec);
            sections.clear();
        }

        if (isFarEnough(section.x1, section.y1, section.z1)) {
            section.set2();

            if (sections.size() >= maxSections.get()) {
                Section section = sections.poll();
                if (section != null) sectionPool.free(section);
            }

            sections.add(section);
            section = sectionPool.get();
            section.set1();
        }

        lastDimension = mc.world.getDimension();
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        int iLast = -1;

        for (Section section : sections) {
            if (iLast == -1) {
                iLast = event.renderer.lines.vec3(section.x1, section.y1, section.z1).color(color.get()).next();
            }

            int i = event.renderer.lines.vec3(section.x2, section.y2, section.z2).color(color.get()).next();
            event.renderer.lines.line(iLast, i);
            iLast = i;
        }
    }

    private boolean isFarEnough(double x, double y, double z) {
        return Math.abs(mc.player.getX() - x) >= sectionLength.get() || Math.abs(mc.player.getY() - y) >= sectionLength.get() || Math.abs(mc.player.getZ() - z) >= sectionLength.get();
    }

    private class Section {
        public float x1, y1, z1;
        public float x2, y2, z2;

        public void set1() {
            x1 = (float) mc.player.getX();
            y1 = (float) mc.player.getY();
            z1 = (float) mc.player.getZ();
        }

        public void set2() {
            x2 = (float) mc.player.getX();
            y2 = (float) mc.player.getY();
            z2 = (float) mc.player.getZ();
        }

        public void render(Render3DEvent event) {
            event.renderer.line(x1, y1, z1, x2, y2, z2, color.get());
        }
    }
}
