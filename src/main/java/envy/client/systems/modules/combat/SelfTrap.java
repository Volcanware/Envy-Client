package envy.client.systems.modules.combat;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.render.Render3DEvent;
import envy.client.events.world.TickEvent;
import envy.client.renderer.ShapeMode;
import envy.client.settings.*;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.render.WallHack;
import envy.client.utils.player.FindItemResult;
import envy.client.utils.player.InvUtils;
import envy.client.utils.player.PlayerUtils;
import envy.client.utils.render.color.SettingColor;
import envy.client.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class SelfTrap extends Module {
    private final List<BlockPos> placePositions = new ArrayList<>();

    private boolean placed;

    private int delay;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General

    private final Setting<TopMode> topPlacement = sgGeneral.add(new EnumSetting.Builder<TopMode>()
        .name("top-mode")
        .description("Which positions to place on your top half.")
        .defaultValue(TopMode.Top)
        .build()
    );

    private final Setting<BottomMode> bottomPlacement = sgGeneral.add(new EnumSetting.Builder<BottomMode>()
        .name("bottom-mode")
        .description("Which positions to place on your bottom half.")
        .defaultValue(BottomMode.None)
        .build()
    );

    private final Setting<Integer> delaySetting = sgGeneral.add(new IntSetting.Builder()
        .name("place-delay")
        .description("How many ticks between block placements.")
        .defaultValue(1)
        .build()
    );

    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
        .name("center")
        .description("Centers you on the block you are standing on before placing.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> turnOff = sgGeneral.add(new BoolSetting.Builder()
        .name("turn-off")
        .description("Turns off after placing.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Sends rotation packets to the server when placing.")
        .defaultValue(true)
        .build()
    );

    // Render

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a block overlay where the obsidian will be placed.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("Determines how the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The color of the sides of the blocks being rendered.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(Envy.INSTANCE.MATHAX_COLOR.r, Envy.INSTANCE.MATHAX_COLOR.g, Envy.INSTANCE.MATHAX_COLOR.b))
        .build()
    );

    public SelfTrap(){
        super(Categories.Combat, Items.OBSIDIAN, "self-trap", "Places obsidian above your head.");
    }

    @Override
    public boolean onActivate() {
        if (Modules.get().isActive(WallHack.class)) {
            error("(highlight)Self Trap+(default) was enabled while enabling (highlight)Self Trap(default), disabling (highlight)Self Trap+(default)...");
            Modules.get().get(SelfTrapPlus.class).toggle();
        }

        if (!placePositions.isEmpty()) placePositions.clear();
        delay = 0;
        placed = false;

        if (center.get()) PlayerUtils.centerPlayer();
        return false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        FindItemResult obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);

        if (turnOff.get() && ((placed && placePositions.isEmpty()) || !obsidian.found())) {
            toggle();
            return;
        }

        if (!obsidian.found()) {
            placePositions.clear();
            return;
        }

        findPlacePos();

        if (delay >= delaySetting.get() && placePositions.size() > 0) {
            BlockPos blockPos = placePositions.get(placePositions.size() - 1);

            if (BlockUtils.place(blockPos, obsidian, rotate.get(), 50)) {
                placePositions.remove(blockPos);
                placed = true;
            }

            delay = 0;
        } else delay++;
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!render.get() || placePositions.isEmpty()) return;
        for (BlockPos pos : placePositions) event.renderer.box(pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    private void findPlacePos() {
        placePositions.clear();
        BlockPos pos = mc.player.getBlockPos();

        switch (topPlacement.get()) {
            case Full -> {
                add(pos.add(0, 2, 0));
                add(pos.add(1, 1, 0));
                add(pos.add(-1, 1, 0));
                add(pos.add(0, 1, 1));
                add(pos.add(0, 1, -1));
            }
            case Top -> add(pos.add(0, 2, 0));
            case Anti_Face_Place -> {
                add(pos.add(1, 1, 0));
                add(pos.add(-1, 1, 0));
                add(pos.add(0, 1, 1));
                add(pos.add(0, 1, -1));
            }
        }

        if (bottomPlacement.get() == BottomMode.Single) add(pos.add(0, -1, 0));
    }

    private void add(BlockPos blockPos) {
        if (!placePositions.contains(blockPos) && mc.world.getBlockState(blockPos).getMaterial().isReplaceable() && mc.world.canPlace(Blocks.OBSIDIAN.getDefaultState(), blockPos, ShapeContext.absent())) placePositions.add(blockPos);
    }

    public enum TopMode {
        Anti_Face_Place("Anti Face Place"),
        Full("Full"),
        Top("Top"),
        None("None");

        private final String title;

        TopMode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    public enum BottomMode {
        Single("Single"),
        None("None");

        private final String title;

        BottomMode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
