package mathax.client.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Playerhandler {
    public static Map<String, Identifier> capes = new HashMap<>();

    public interface ReturnCapeTexture {
        void response(Identifier id);
    }

    public static void loadPlayerCape(GameProfile player, ReturnCapeTexture response) {
        Util.getMainWorkerExecutor().execute(() -> {
            try {
                String uuid = player.getId().toString();
                NativeImageBackedTexture nIBT = getCapeFromURL(String.format("https://raw.githubusercontent.com/Volcanware/Envy-Client/Now-Fixed/EnvyCape.png"));
                Identifier capeTexture = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("of-capes-" + uuid, nIBT);
                capes.put(uuid, capeTexture);
                response.response(capeTexture);
            } catch (Exception ignored) {
            }
        });
    }

    public static NativeImageBackedTexture getCapeFromURL(String capeStringURL) {
        try {
            URL capeURL = new URL(capeStringURL);
            return getCapeFromStream(capeURL.openStream());
        } catch (IOException e) {
            return null;
        }
    }

    public static NativeImageBackedTexture getCapeFromStream(InputStream image) {
        NativeImage cape = null;
        try {
            cape = NativeImage.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cape != null) {
            NativeImageBackedTexture nIBT = new NativeImageBackedTexture(parseCape(cape));
            return nIBT;
        }
        return null;
    }

    public static NativeImage parseCape(NativeImage image) {
        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        for (int imageSrcHeight = image.getHeight(); imageWidth < imageSrcWidth
            || imageHeight < imageSrcHeight; imageHeight *= 2) {
            imageWidth *= 2;
        }

        NativeImage imgNew = new NativeImage(imageWidth, imageHeight, true);
        for (int x = 0; x < imageSrcWidth; x++) {
            for (int y = 0; y < srcHeight; y++) {
                imgNew.setColor(x, y, image.getColor(x, y));
            }
        }
        image.close();
        return imgNew;
    }
}
