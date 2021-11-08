package mathax.legacy.client.utils;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.utils.network.HTTP;
import mathax.legacy.client.utils.render.prompts.OkPrompt;
import mathax.legacy.client.utils.render.prompts.YesNoPrompt;
import net.minecraft.util.Util;

import static mathax.legacy.client.utils.Version.*;

public class UpdateChecker {
    public static boolean didntCheckForLatestTitle = true;
    public static boolean didntCheckForLatest = true;

    public static String getLatest() {
        String latestVer = HTTP.get(MatHaxLegacy.API_URL + "Version/Legacy/1-17-1").sendString();

        if (latestVer == null) return null;
        else return latestVer.replace("\n", "");
    }

    public static CheckStatus checkLatest() {
        if (getDev() > 0) return CheckStatus.Running_Dev;

        String latestVersion = getLatest();

        if (latestVersion == null) return CheckStatus.Cant_Check;
        else {
            Version latestVer = new Version(latestVersion);
            Version currentVer = new Version(get());
            if (latestVer.isHigherThan(currentVer)) return CheckStatus.Newer_Found;
            else return CheckStatus.Latest;
        }
    }

    public static void checkForUpdate(boolean cant, boolean found, boolean latest, boolean dev, boolean cantDisable) {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Checking for latest version of MatHax Legacy!");
        switch (checkLatest()) {
            case Cant_Check -> {
                MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Could not check for latest version!");
                if (cant) cantPrompt(cantDisable);
            }
            case Newer_Found -> {
                MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "There is a new version of MatHax Legacy, v" + getLatest() + "! You are using v" + Version.getStylized() + "! You can download the newest version on " + MatHaxLegacy.URL + "Download!");
                if (found) foundPrompt(cantDisable);
            }
            case Latest -> {
                MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "You are using the latest version of MatHax Legacy, " + Version.getStylized() + "!");
                if (latest) latestPrompt(cantDisable);
            }
            case Running_Dev -> {
                MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Developer builds do not get update notifications about another developer build of the version they are a developer build of! You are running " + get() + "!");
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
            .message("A new version of MatHax Legacy has been released!")
            .message("\n")
            .message("Your version: %s", Version.getStylized())
            .message("Latest version: v%s", getLatest())
            .message("\n")
            .message("Do you want to update?")
            .message("Using old versions of MatHax Legacy is not recommended")
            .message("and could report in issues.")
            /*.onNo(() -> OkPrompt.create()
                .title("Are you sure?")
                .message("Using old versions of MatHax Legacy is not recommended")
                .message("and could report in issues.")
                .id("new-update-no")
                .show())*/
            .onYes(() -> Util.getOperatingSystem().open(MatHaxLegacy.URL + "Download"))
            .id(id)
            .show();
    }

    private static void latestPrompt(boolean cantDisable) {
        String id = "no-new-update";
        if (cantDisable) id += "-cant-disable";

        OkPrompt.create()
            .title("No New Update")
            .message("You are using the latest version of MatHax Legacy!")
            .message("\n")
            .message("Your version: %s", Version.getStylized())
            .message("Latest version: v%s", getLatest())
            .id(id)
            .show();
    }

    private static void devPrompt(boolean cantDisable) {
        String id = "running-dev";
        if (cantDisable) id += "-cant-disable";

        OkPrompt.create()
            .title("Running a Developer build")
            .message("Developer builds do not get update notifications")
            .message("about another developer build of the")
            .message("version they are a developer build of!")
            .message("\n")
            .message("Your version: %s", Version.getStylized())
            .message("Latest version: v%s", getLatest())
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
