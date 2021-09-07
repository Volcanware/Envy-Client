package installer.mathax.client.legacy.version;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Version {
    public static String get() throws IOException, URISyntaxException {
        File jsonPath;
        jsonPath = new File(Version.class.getResource("/fabric.mod.json").toURI());
        String content = Files.readString(jsonPath.toPath(), StandardCharsets.US_ASCII);
        Object obj = new JsonParser().parse(content);
        JsonObject jo = (JsonObject) obj;
        return jo.get("version").toString().replace("\"", "");
    }

    public static String getDevStylized() {
        Integer dev = mathax.client.legacy.Version.getDev();
        if (dev == 0) {
            return "";
        } else {
            return " Dev-" + dev;
        }
    }

    public static String getMinecraft() throws IOException, URISyntaxException {
        File jsonPath;
        jsonPath = new File(Version.class.getResource("/fabric.mod.json").toURI());
        String content = Files.readString(jsonPath.toPath(), StandardCharsets.US_ASCII);
        Object obj = new JsonParser().parse(content);
        JsonObject jo = (JsonObject) obj;
        String string = jo.get("depends").toString().replace("\"", "");
        String string2 = string.replace("{java:>=16,minecraft:>=", "");
        return string2.replace("}", "");
    }
}
