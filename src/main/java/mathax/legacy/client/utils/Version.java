package mathax.legacy.client.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

public class Version {
    private final String string;
    private final int[] numbers;

    public Version(String string) {
        this.string = string;
        this.numbers = new int[3];

        String[] split = string.split("\\.");
        if (split.length != 3) throw new IllegalArgumentException("[MatHax Legacy] Version string needs to have 3 numbers.");

        for (int i = 0; i < 3; i++) {
            try {
                numbers[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("[MatHax Legacy] Failed to parse version string.");
            }
        }
    }

    public boolean isHigherThan(Version version) {
        for (int i = 0; i < 3; i++) {
            if (numbers[i] > version.numbers[i]) return true;
            if (numbers[i] < version.numbers[i]) return false;
        }

        return false;
    }

    public static String get() {
        return FabricLoader.getInstance().getModContainer("mathaxlegacy").get().getMetadata().getVersion().getFriendlyString();
    }

    public static Integer getDev() {
        return 3;
    }

    public static String getDevString() {
        if (getDev() == 0) return "";
        else return "Dev-" + getDev();
    }

    public static String getStylized() {
        if (getDev() == 0) return "v" + get();
        else return "v" + get() + " " + getDevString();
    }

    public static String getMinecraft(){
        return SharedConstants.getGameVersion().getName();
    }

    public static Integer getMinecraftProtocol(){
        return SharedConstants.getGameVersion().getProtocolVersion();
    }

    @Override
    public String toString() {
        return string;
    }
}
