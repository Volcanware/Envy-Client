package mathax.client.utils.misc;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Icons;
import net.minecraft.resource.DefaultResourcePackBuilder;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import static mathax.client.MatHax.mc;

public class Icon {
    public static boolean iconChanged = false;

    public static void setIcon() {
        try {
            mc.getWindow().setIcon(
                new DefaultResourcePackBuilder()
                    .withRoot(new File("/assets/mathax/").toPath())
                    .build(),
                Icons.RELEASE
            );
            iconChanged = true;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public static void setMinecraft() {
        try {
            mc.getWindow().setIcon(
                MinecraftClient.getInstance().getDefaultResourcePack(),
                Icons.RELEASE
            );
            iconChanged = false;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

