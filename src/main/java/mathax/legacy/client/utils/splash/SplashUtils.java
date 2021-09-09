package mathax.legacy.client.utils.splash;

import mathax.legacy.client.MatHaxLegacy;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.*;

public class SplashUtils extends ResourceTexture {

    public SplashUtils(Identifier location) {
        super(location);
    }

    protected TextureData loadTextureData(ResourceManager resourceManager) {
        try {
            InputStream input = new FileInputStream(MatHaxLegacy.MCCONFIG_FOLDER + location.toString().replace("mathaxlegacy:splash/splash.png","/Splash/Logo/Splash_MatHax.png"));
            TextureData texture;

            try {
                texture = new TextureData(new TextureResourceMetadata(true, true), NativeImage.read(input));
            } finally {
                input.close();
            }

            return texture;
        } catch (IOException var18) {
            return new TextureData(var18);
        }
    }

    private static final File LOGO_FOLDER = new File(MatHaxLegacy.MCCONFIG_FOLDER, "Splash/Logo");

    public static void reset() {
        File[] files = LOGO_FOLDER.exists() ? LOGO_FOLDER.listFiles() : new File[0];
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".png") || file.getName().endsWith(".PNG")) {
                    file.delete();
                }
            }
        }
    }

    public static void init() {
        File[] files = LOGO_FOLDER.exists() ? LOGO_FOLDER.listFiles() : new File[0];
        File splashFile = null;
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".png") || file.getName().endsWith(".PNG")) {
                    splashFile = file;
                    break;
                }
            }
        }

        if (splashFile == null) {
            try {

                splashFile = new File(LOGO_FOLDER, "Splash_MatHax.png");
                splashFile.getParentFile().mkdirs();

                InputStream in = MatHaxLegacy.class.getResourceAsStream("/assets/mathaxlegacy/textures/splash/splash.png");
                OutputStream out = new FileOutputStream(splashFile);

                byte[] bytes = new byte[255];
                int read;
                while ((read = in.read(bytes)) > 0) out.write(bytes, 0, read);

                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
