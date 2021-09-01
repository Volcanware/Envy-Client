package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.TripleTextHudElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BiomeHud extends TripleTextHudElement {
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();

    public BiomeHud(HUD hud) {
        super(hud, "biome", "Displays the biome you are in.", false);
    }

    @Override
    protected String getLeft() {
        return "Biome: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return "Plains";

        blockPos.set(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        Identifier id = mc.world.getRegistryManager().get(Registry.BIOME_KEY).getId(mc.world.getBiome(blockPos));
        if (id == null) return "Unknown";

        return Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    @Override
    public String getEnd() {
        return "";
    }
}
