package mathax.installer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
        JSONObject json = JSONUtils.readJsonFromUrl(this.metaUrl);
        json.getJSONArray("client_versions").toList().forEach(element -> clientVersions.add(element.toString()));
        json.getJSONArray("game_versions").toList().forEach(element -> gameVersions.add(element.toString()));
    }

    public List<String> getClientVersions() {
        return this.clientVersions;
    }

    public List<String> getGameVersions() {
        return this.gameVersions;
    }
}
