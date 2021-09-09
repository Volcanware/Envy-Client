package mathax.legacy.client;

import mathax.legacy.client.utils.network.HTTP;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

public class Version {
    public static boolean checkedForLatestTitleText = false;
    public static boolean checkedForLatestTitle = false;
    public static boolean checkedForLatest = false;
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

    public static String get() {
        return FabricLoader.getInstance().getModContainer("mathaxlegacy").get().getMetadata().getVersion().getFriendlyString();
    }

    public static Integer getDev() {
        return 4;
    }

    public static String getDevBuild() {
        Integer dev = getDev();
        if (dev == 0) {
            return "";
        } else {
            return "Dev-" + dev;
        }
    }

    public static String getStylized() {
        return "v" + get() + " " + getDevBuild();
    }

    public static String getMinecraft(){
        return SharedConstants.getGameVersion().getName();
    }

    public boolean isHigherThan(Version version) {
        for (int i = 0; i < 3; i++) {
            if (numbers[i] > version.numbers[i]) return true;
            if (numbers[i] < version.numbers[i]) return false;
        }

        return false;
    }

    public static Integer checkLatest() {
        String apiLatestVer = HTTP.get(MatHaxLegacy.API_URL + "Version/Legacy/1-17-1").sendString().replace("\n", "");
        if (apiLatestVer == null) {
            return 0;
        } else {
            Version latestVer = new Version(apiLatestVer);
            Version currentVer = new Version(Version.get());
            if (latestVer.isHigherThan(currentVer)) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    public static String getLatest() {
        String latestVer = HTTP.get(MatHaxLegacy.API_URL + "Version/Legacy/1-17-1").sendString().replace("\n", "");
        if (latestVer == null) {
            return "NULL";
        } else {
            return latestVer;
        }
    }

    @Override
    public String toString() {
        return string;
    }
}
