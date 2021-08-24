package mathax.client.legacy.systems.modules.render;

import mathax.client.legacy.events.world.ChunkOcclusionEvent;
import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.settings.*;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.utils.player.ChatUtils;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class WallHack extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Integer> opacity = sgGeneral.add(new IntSetting.Builder()
        .name("opacity")
        .description("The opacity for rendered blocks.")
        .defaultValue(100)
        .min(1)
        .max(255)
        .sliderMax(255)
        .onChanged(onChanged -> {
            if(this.isActive()) {
                mc.worldRenderer.reload();
            }
        })
        .build()
    );

    public final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("What blocks should be targeted for Wall Hack.")
        .defaultValue(new ArrayList<>())
        .onChanged(onChanged -> {
            if(this.isActive()) {
                mc.worldRenderer.reload();
            }
        })
        .build()
    );

    public final Setting<Boolean> occludeChunks = sgGeneral.add(new BoolSetting.Builder()
        .name("occlude-chunks")
        .description("Whether caves should occlude underground (may look wonky when on).")
        .defaultValue(false)
        .build()
    );

    public WallHack() {
        super(Categories.Render, "wall-hack", "Makes blocks translucent.");
    }

    @Override
    public void onActivate() {
        if (Modules.get().isActive(Xray.class)) {
            ChatUtils.error("Xray", "Xray was enabled while enabling Wallhack, disabling Xray...");
            Modules.get().get(Xray.class).toggle();
        }
        mc.worldRenderer.reload();
    }

    @Override
    public void onDeactivate() {
        mc.worldRenderer.reload();
    }

    @EventHandler
    private void onChunkOcclusion(ChunkOcclusionEvent event) {
        if(!occludeChunks.get()) {
            event.cancel();
        }
    }
}
