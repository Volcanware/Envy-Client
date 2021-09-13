package mathax.legacy.client.systems.modules.render;

import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.events.render.Render3DEvent;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.gui.widgets.containers.WHorizontalList;
import mathax.legacy.client.gui.widgets.pressable.WButton;
import mathax.legacy.client.renderer.ShapeMode;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.render.color.Color;
import mathax.legacy.client.utils.render.color.SettingColor;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;

/*/                         /*/
/*/ Ported from BleachHack. /*/
/*/ https://bleachhack.org  /*/
/*/                         /*/

public class NewChunks extends Module {
    private Set<ChunkPos> newChunks = Collections.synchronizedSet(new HashSet<>());
    private Set<ChunkPos> oldChunks = Collections.synchronizedSet(new HashSet<>());
    private static final Direction[] searchDirs = new Direction[] { Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.UP };

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgNewChunks = settings.createGroup("New Chunks");
    private final SettingGroup sgOldChunks = settings.createGroup("Old Chunks");

    private final Setting<Boolean> remove = sgGeneral.add(new BoolSetting.Builder()
        .name("remove")
        .description("Removes the cached chunks when disabling the module and leaving.")
        .defaultValue(true)
        .build()
    );

    // New Chunks

    private final Setting<Boolean> newChunksToggle = sgNewChunks.add(new BoolSetting.Builder()
        .name("new-chunks")
        .description("Determines if new chunks are visible.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> newChunksFillColor = sgNewChunks.add(new ColorSetting.Builder()
        .name("fill-color")
        .description("Fill color of the chunks that are (most likely) completely new.")
        .defaultValue(new SettingColor(255, 0, 0, 0))
        .build()
    );

    private final Setting<SettingColor> newChunksLineColor = sgNewChunks.add(new ColorSetting.Builder()
        .name("line-color")
        .description("Line color of the chunks that are (most likely) completely new.")
        .defaultValue(new SettingColor(255, 0, 0))
        .build()
    );

    // Old Chunks

    private final Setting<Boolean> oldChunksToggle = sgOldChunks.add(new BoolSetting.Builder()
        .name("old-chunks")
        .description("Determines if old chunks are visible.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> oldChunksFillColor = sgOldChunks.add(new ColorSetting.Builder()
        .name("fill-color")
        .description("Fill color of the chunks that are old.")
        .defaultValue(new SettingColor(0, 255, 0, 0))
        .build()
    );

    private final Setting<SettingColor> oldChunksLineColor = sgOldChunks.add(new ColorSetting.Builder()
        .name("line-color")
        .description("Line color of the chunks that are old.")
        .defaultValue(new SettingColor(0, 255, 0))
        .build()
    );

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WHorizontalList w = theme.horizontalList();

        WButton remove = w.add(theme.button("Remove")).widget();
        remove.action = () -> {
            newChunks.clear();
            oldChunks.clear();
        };
        w.add(theme.label("Removes all cached chunks."));

        return w;
    }

    public NewChunks() {
        super(Categories.Render, Items.GRASS_BLOCK, "new-chunks", "Detects completely new chunks using certain traits of them");
    }

    @Override
    public void onDeactivate() {
        if (remove.get()) {
            newChunks.clear();
            oldChunks.clear();
        }
        super.onDeactivate();
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if ((newChunksLineColor.get().a > 3 || newChunksFillColor.get().a > 3) && newChunksToggle.get()) {
            synchronized (newChunks) {
                for (ChunkPos c : newChunks) {
                    if (mc.getCameraEntity().getBlockPos().isWithinDistance(c.getStartPos(), 1024)) {
                        drawBoxOutline(new Box(c.getStartPos(), c.getStartPos().add(16, 0, 16)), newChunksFillColor.get(), newChunksLineColor.get(), event);
                    }
                }
            }
        }

        if ((oldChunksLineColor.get().a > 3 || oldChunksFillColor.get().a > 3) && oldChunksToggle.get()){
            synchronized (oldChunks) {
                for (ChunkPos c : oldChunks) {
                    if (mc.getCameraEntity().getBlockPos().isWithinDistance(c.getStartPos(), 1024)) {
                        drawBoxOutline(new Box(c.getStartPos(), c.getStartPos().add(16, 0, 16)), oldChunksFillColor.get(), oldChunksLineColor.get(), event);
                    }
                }
            }
        }
    }

    @EventHandler
    private void onReadPacket(PacketEvent.Receive event) {
        if (event.packet instanceof ChunkDeltaUpdateS2CPacket) {
            ChunkDeltaUpdateS2CPacket packet = (ChunkDeltaUpdateS2CPacket) event.packet;

            packet.visitUpdates((pos, state) -> {
                if (!state.getFluidState().isEmpty() && !state.getFluidState().isStill()) {
                    ChunkPos chunkPos = new ChunkPos(pos);

                    for (Direction dir: searchDirs) {
                        if (mc.world.getBlockState(pos.offset(dir)).getFluidState().isStill() && !oldChunks.contains(chunkPos) && newChunksToggle.get()) {
                            newChunks.add(chunkPos);
                            return;
                        }
                    }
                }
            });
        }

        else if (event.packet instanceof BlockUpdateS2CPacket) {
            BlockUpdateS2CPacket packet = (BlockUpdateS2CPacket) event.packet;

            if (!packet.getState().getFluidState().isEmpty() && !packet.getState().getFluidState().isStill()) {
                ChunkPos chunkPos = new ChunkPos(packet.getPos());

                for (Direction dir: searchDirs) {
                    if (mc.world.getBlockState(packet.getPos().offset(dir)).getFluidState().isStill() && !oldChunks.contains(chunkPos) && newChunksToggle.get()) {
                        newChunks.add(chunkPos);
                        return;
                    }
                }
            }
        }

        else if (event.packet instanceof ChunkDataS2CPacket && mc.world != null) {
            ChunkDataS2CPacket packet = (ChunkDataS2CPacket) event.packet;

            ChunkPos pos = new ChunkPos(packet.getX(), packet.getZ());

            if (!newChunks.contains(pos) && mc.world.getChunkManager().getChunk(packet.getX(), packet.getZ()) == null) {
                WorldChunk chunk = new WorldChunk(mc.world, pos, null);
                chunk.loadFromPacket(null, packet.getReadBuffer(), new NbtCompound(), packet.getVerticalStripBitmask());

                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < mc.world.getHeight(); y++) {
                        for (int z = 0; z < 16; z++) {
                            FluidState fluid = chunk.getFluidState(x, y, z);

                            if (!fluid.isEmpty() && !fluid.isStill() && oldChunksToggle.get()) {
                                oldChunks.add(pos);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    private void drawBoxOutline(Box box, Color fillColor, Color lineColor, Render3DEvent event) {
        event.renderer.box(
            box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, fillColor, lineColor, ShapeMode.Lines, 0
        );
    }
}
