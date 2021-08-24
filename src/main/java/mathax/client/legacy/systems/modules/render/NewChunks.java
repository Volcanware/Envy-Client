package mathax.client.legacy.systems.modules.render;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.events.packets.PacketEvent;
import mathax.client.legacy.events.render.Render3DEvent;
import mathax.client.legacy.renderer.Renderer3D;
import mathax.client.legacy.renderer.ShapeMode;
import mathax.client.legacy.settings.*;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.utils.render.color.Color;
import mathax.client.legacy.utils.render.color.SettingColor;
import mathax.client.legacy.utils.world.Dir;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mathax.client.legacy.systems.modules.Module;

public class NewChunks extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgNewChunks = settings.createGroup("New Chunks");
    private final SettingGroup sgOldChunks = settings.createGroup("Old Chunks");

    private final Setting<Boolean> remove = sgGeneral.add(new BoolSetting.Builder()
        .name("remove")
        .description("Removes gathered chunks when disabling the module/leaving.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> newChunksToggle = sgNewChunks.add(new BoolSetting.Builder()
        .name("new-chunks")
        .description("Gathers completely new chunks.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> newColor = sgNewChunks.add(new ColorSetting.Builder()
        .name("new-color")
        .description("The color of new chunks")
        .defaultValue(new SettingColor(225, 0, 0, 100))
        .build()
    );

    private final Setting<Boolean> oldChunksToggle = sgOldChunks.add(new BoolSetting.Builder()
        .name("old-chunks")
        .description("Gathers old chunks.")
        .defaultValue(false)
        .build()
    );

    private final Setting<SettingColor> oldColor = sgOldChunks.add(new ColorSetting.Builder()
        .name("old-color")
        .description("The color of old chunks")
        .defaultValue(new SettingColor(0, 255, 0, 100))
        .build()
    );

    private Set<ChunkPos> newChunks = Collections.synchronizedSet(new HashSet<>());
    private Set<ChunkPos> oldChunks = Collections.synchronizedSet(new HashSet<>());

    private ChunkPos newChunksPos;
    private ChunkPos oldChunksPos;

    @Override
    public void onDeactivate() {
        if (remove.get()) {
            newChunks.clear();
            oldChunks.clear();
        }

        super.onDeactivate();
    }

    public NewChunks() {
        super(Categories.Render, "new-chunks", "Detects completely new chunks.");
    }

    @EventHandler
    public void onReadPacket(PacketEvent event) {
        Direction[] searchDirs = new Direction[]{Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.UP};

        if (newChunksToggle.get()) {
            if (event.packet instanceof ChunkDeltaUpdateS2CPacket) {
                ChunkDeltaUpdateS2CPacket packet = (ChunkDeltaUpdateS2CPacket) event.packet;

                packet.visitUpdates((pos, state) -> {
                    if (!state.getFluidState().isEmpty() && !state.getFluidState().isStill()) {
                        ChunkPos chunkPos = new ChunkPos(pos);

                        for (Direction dir : searchDirs) {
                            if (mc.world.getBlockState(pos.offset(dir)).getFluidState().isStill() && !oldChunks.contains(chunkPos)) {
                                newChunks.add(chunkPos);
                                newChunksPos = chunkPos;
                                return;
                            }
                        }
                    }
                });
            } else if (event.packet instanceof BlockUpdateS2CPacket) {
                BlockUpdateS2CPacket packet = (BlockUpdateS2CPacket) event.packet;

                if (!packet.getState().getFluidState().isEmpty() && !packet.getState().getFluidState().isStill()) {
                    ChunkPos chunkPos = new ChunkPos(packet.getPos());

                    for (Direction dir : searchDirs) {
                        if (mc.world.getBlockState(packet.getPos().offset(dir)).getFluidState().isStill() && !oldChunks.contains(chunkPos)) {
                            newChunks.add(chunkPos);
                            newChunksPos = chunkPos;
                            return;
                        }
                    }
                }
            }
        } if (oldChunksToggle.get()) {
        if (event.packet instanceof ChunkDataS2CPacket && mc.world != null) {
                ChunkDataS2CPacket packet = (ChunkDataS2CPacket) event.packet;

                ChunkPos pos = new ChunkPos(packet.getX(), packet.getZ());

                if (!newChunks.contains(pos) && mc.world.getChunkManager().getChunk(packet.getX(), packet.getZ()) == null) {
                    WorldChunk chunk = new WorldChunk(mc.world, pos, null);
                    chunk.loadFromPacket(null, packet.getReadBuffer(), new NbtCompound(), packet.getVerticalStripBitmask());

                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < mc.world.getHeight(); y++) {
                            for (int z = 0; z < 16; z++) {
                                FluidState fluid = chunk.getFluidState(x, y, z);

                                if (!fluid.isEmpty() && !fluid.isStill()) {
                                    oldChunks.add(pos);
                                    oldChunksPos = pos;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //TODO: Render :))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))

}
