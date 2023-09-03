package mathax.client.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mathax.client.systems.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Coordinates extends Command {
    public Coordinates() {
        super("coordinates", "Copies your coordinates to the clipboard.", "coords", "position", "pos");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> CopyPos());
        builder.then(literal("copy").executes(context -> CopyPos()));
        builder.then(literal("share-in-chat").executes(context -> {
            info("Coordinates: " + getPos());
            return SINGLE_SUCCESS;
        }));
    }
    private String getPos() {
        BlockPos pos = mc.player.getBlockPos();
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }

    private int CopyPos() {
        mc.keyboard.setClipboard(getPos());
        info("Coordinates successfully copied to the clipboard");
        return SINGLE_SUCCESS;
    }
}
