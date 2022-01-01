package mathax.client.utils;

import mathax.client.MatHax;
import mathax.client.utils.network.HTTP;
import mathax.client.utils.render.prompts.OkPrompt;
import mathax.client.utils.render.prompts.YesNoPrompt;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class Version {
    private final String string;
    private final int[] numbers;

    public Version(String string) {
        this.string = string;
        this.numbers = new int[3];

        String[] split = string.split("\\.");
        if (split.length != 3) throw new IllegalArgumentException("[MatHax] Version string needs to have 3 numbers.");

        for (int i = 0; i < 3; i++) {
            try {
                numbers[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("[MatHax] Failed to parse version string.");
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

    @Override
    public String toString() {
        return string;
    }

    public static Version get() {
        return new Version(FabricLoader.getInstance().getModContainer("mathax").get().getMetadata().getVersion().getFriendlyString());
    }

    public static Integer getDev() {
        return 0;
    }

    public static String getDevString() {
        if (getDev() < 1) return "";
        else return "Dev-" + getDev();
    }

    public static String getStylized() {
        if (getDev() < 1) return "v" + get();
        else return "v" + get() + " " + getDevString();
    }

    public static String getMinecraft(){
        return SharedConstants.getGameVersion().getName();
    }

    public static Integer getMinecraftProtocol(){
        return SharedConstants.getGameVersion().getProtocolVersion();
    }

    public static class UpdateChecker {
        public static boolean checkForLatestTitle = true;
        public static boolean checkForLatest = true;

        public static Version getLatest() {
            InputStream api = HTTP.get(MatHax.API_URL + "Version/metadata.json").sendInputStream();
            if (api == null) return null;

            String latestVer = null;
            String key = getMinecraft().replace(".", "-");
            JSONObject json = new JSONObject(new JSONTokener(api));
            if (json.has(key)) latestVer = json.getString(key);
            if (latestVer == null) return null;
            return new Version(latestVer);
        }

        public static CheckStatus checkLatest() {
            if (getDev() > 0) return CheckStatus.Running_Dev;

            Version latestVersion = getLatest();
            if (latestVersion == null) return CheckStatus.Cant_Check;
            else {
                Version currentVer = get();
                if (latestVersion.isHigherThan(currentVer)) return CheckStatus.Newer_Found;
                else return CheckStatus.Latest;
            }
        }

        public static void checkForUpdate(boolean cant, boolean found, boolean latest, boolean dev, boolean cantDisable) {
            MatHax.LOG.info(MatHax.logPrefix + "Checking for latest version of MatHax!");
            switch (checkLatest()) {
                case Cant_Check -> {
                    MatHax.LOG.info(MatHax.logPrefix + "Could not check for latest version!");
                    if (cant) cantPrompt(cantDisable);
                }
                case Newer_Found -> {
                    MatHax.LOG.info(MatHax.logPrefix + "There is a new version of MatHax, v" + getLatest() + "! You are using " + Version.getStylized() + "! You can download the newest version on " + MatHax.URL + "Download!");
                    if (found) foundPrompt(cantDisable);
                }
                case Latest -> {
                    MatHax.LOG.info(MatHax.logPrefix + "You are using the latest version of MatHax, " + Version.getStylized() + "!");
                    if (latest) latestPrompt(cantDisable);
                }
                case Running_Dev -> {
                    MatHax.LOG.info(MatHax.logPrefix + "Developer builds do not get update notifications about another developer build of the version they are a developer build of! You are running " + getStylized() + "!");
                    if (dev) devPrompt(cantDisable);
                }
            }
        }

        // TODO: PROMPTS GLITCHING OUT AND APPEARING AGAIN [ISSUE IN METEOR TOO]

        private static void cantPrompt(boolean cantDisable) {
            String id = "cant-check";
            if (cantDisable) id += "-cant-disable";

            OkPrompt.create()
                .title("Update Check Failed")
                .message("Could not get latest MatHax version from the API!")
                .message("\n")
                .message("Your version: %s", Version.getStylized())
                .id(id)
                .show();
        }

        private static void foundPrompt(boolean cantDisable) {
            String id = "new-update";
            if (cantDisable) id += "-cant-disable";

            YesNoPrompt.create()
                .title("New Update")
                .message("A new version of MatHax has been released!")
                .message("\n")
                .message("Your version: %s", Version.getStylized())
                .message("Latest version: v%s", getLatest())
                .message("\n")
                .message("Do you want to update?")
                .message("Using old versions of MatHax is not recommended")
                .message("and could report in issues.")
                //.onNo(() -> foundNoPrompt(cantDisable))
                .onYes(() -> Util.getOperatingSystem().open(MatHax.URL + "Download"))
                .id(id)
                .show();
        }

        /*private static void foundNoPrompt(boolean cantDisable) {
            String id = "new-update-no";
            if (cantDisable) id += "-cant-disable";

            OkPrompt.create()
                .title("Are you sure?")
                .message("Using old versions of MatHax is not recommended")
                .message("and could report in issues.")
                .id(id)
                .show();
        }*/

        private static void latestPrompt(boolean cantDisable) {
            String id = "no-new-update";
            if (cantDisable) id += "-cant-disable";

            OkPrompt.create()
                .title("No New Update")
                .message("You are using the latest version of MatHax!")
                .message("\n")
                .message("Your version: %s", Version.getStylized())
                .message("Latest version: v%s", getLatest())
                .id(id)
                .show();
        }

        private static void devPrompt(boolean cantDisable) {
            String id = "running-dev";
            if (cantDisable) id += "-cant-disable";

            Version latest = getLatest();

            OkPrompt.create()
                .title("Running a Developer build")
                .message("Developer builds do not get update notifications")
                .message("about another developer build of the")
                .message("version they are a developer build of!")
                .message("\n")
                .message("Your version: %s", Version.getStylized())
                .message(latest == null ? "Couldn't get latest version from the API." : "Latest version: v%s", latest)
                .id(id)
                .show();
        }

        public enum CheckStatus {
            Cant_Check,
            Newer_Found,
            Latest,
            Running_Dev
        }
    }
}
