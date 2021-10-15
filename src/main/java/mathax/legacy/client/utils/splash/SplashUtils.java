package mathax.legacy.client.utils.splash;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.*;

public class SplashUtils extends ResourceTexture {

    protected final Identifier location;

    public SplashUtils(Identifier location) {
        super(location);
        this.location = location;
    }

    protected TextureData loadTextureData(ResourceManager resourceManager) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        DefaultResourcePack defaultResourcePack = minecraftClient.getResourcePackProvider().getPack();

        try {
            InputStream inputStream = defaultResourcePack.open(ResourceType.CLIENT_RESOURCES, location);
            TextureData textureData;

            try {
                textureData = new TextureData(new TextureResourceMetadata(true, true), NativeImage.read(inputStream));
            } catch (Throwable throwable) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (inputStream != null) inputStream.close();
            return textureData;
        } catch (IOException ioException) {
            return new TextureData(ioException);
        }
    }
}
