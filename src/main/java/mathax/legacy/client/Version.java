package mathax.legacy.client;

import mathax.legacy.client.utils.network.HTTP;
import mathax.legacy.client.utils.network.MatHaxExecutor;
import mathax.legacy.client.utils.render.prompts.OkPrompt;
import mathax.legacy.client.utils.render.prompts.YesNoPrompt;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;

public class Version {
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

    public boolean isHigherThan(Version version) {
        for (int i = 0; i < 3; i++) {
            if (numbers[i] > version.numbers[i]) return true;
            if (numbers[i] < version.numbers[i]) return false;
        }

        return false;
    }

    public static Integer getDev() {
        return 1;
    }

    public static String getDevString() {
        Integer dev = getDev();
        if (dev == 0) {
            return "";
        } else {
            return "Dev-" + dev;
        }
    }

    public static String getStylized() {
        Integer dev = getDev();
        if (dev == 0) {
            return "v" + get();
        } else {
            return "v" + get() + " " + getDevString();
        }
    }

    public static String getMinecraft(){
        return SharedConstants.getGameVersion().getName();
    }

    public static Integer checkLatest() {
        String apiLatestVer = HTTP.get(MatHaxLegacy.API_URL + "Version/Legacy/1-17-1").sendString();
        if (apiLatestVer == null) {
            return 0;
        } else {
            Version latestVer = new Version(apiLatestVer.replace("\n", ""));
            Version currentVer = new Version(Version.get());
            if (latestVer.isHigherThan(currentVer)) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    public static String getLatest() {
        String latestVer = HTTP.get(MatHaxLegacy.API_URL + "Version/Legacy/1-17-1").sendString();
        if (latestVer == null) {
            return "NULL";
        } else {
            return latestVer.replace("\n", "");
        }
    }

    // TODO: PROMPT GLITCHING OUT AND APPEARING AGAIN ON OKPROMPT [ISSUE IN METEOR TOO]
    public static void checkForUpdate(boolean dontDisable) {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Checking for latest version of MatHax Legacy!");
        switch (Version.checkLatest()) {
            case 0:
                MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Could not check for latest version!");
                return;
            case 1:
                MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "There is a new version of MatHax Legacy, v" + Version.getLatest() + "! You are using v" + Version.getStylized() + "! You can download the newest version on " + MatHaxLegacy.URL + "Download!");
                MatHaxExecutor.execute(() -> {
                    String id = "new-update";
                    if (dontDisable) {
                        id += "-dont-disable";
                    }

                    YesNoPrompt.create()
                        .title("New Update")
                        .message("A new version of MatHax Legacy has been released!")
                        .message("\n")
                        .message("Your version: %s", Version.getStylized())
                        .message("Latest version: v%s", Version.getLatest())
                        .message("\n")
                        .message("Do you want to update?")
                        .message("Using old versions of MatHax Legacy is not recommended and could report in issues.")
                        /*.onNo(() -> OkPrompt.create()
                            .title("Are you sure?")
                            .message("Using old versions of MatHax Legacy is not recommended")
                            .message("and could report in issues.")
                            .id("new-update-no")
                            .show())*/
                        .onYes(() -> {
                            Util.getOperatingSystem().open(MatHaxLegacy.URL + "Download");
                        })
                        .id(id)
                        .show();
                });
            case 2:
                if (getDev() == 0) {
                    MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "You are using the latest version of MatHax Legacy, " + Version.getStylized() + "!");
                } else {
                    MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "You are using the latest version of MatHax Legacy, " + Version.getStylized() + "! [Developer builds do not get update notifications on the full version or another developer build of the version they are a developer build of!]");
                }
        }
    }

    @Override
    public String toString() {
        return string;
    }
}
