package mathax.legacy.installer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class InstallerMeta {
    private final String metaUrl;
    private final List<String> clientVersions = new ArrayList<>();
    private final List<String> gameVersions = new ArrayList<>();

    public InstallerMeta(String url) {
        this.metaUrl = url;
    }

    public void load() throws IOException, JSONException {
        JSONObject json = readJsonFromUrl(this.metaUrl);
        json.getJSONArray("client_versions").toList().forEach(element -> clientVersions.add(element.toString()));
        json.getJSONArray("game_versions").toList().forEach(element -> gameVersions.add(element.toString()));
    }

    public List<String> getClientVersions() {
        return this.clientVersions;
    }

    public List<String> getGameVersions() {
        return this.gameVersions;
    }

    public static String readAll(Reader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int codePoint;
        while ((codePoint = reader.read()) != -1) stringBuilder.append((char) codePoint);
        return stringBuilder.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8));
        return new JSONObject(readAll(bufferedReader));
    }
}
