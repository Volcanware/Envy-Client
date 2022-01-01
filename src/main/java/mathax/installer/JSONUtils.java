package mathax.installer;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JSONUtils {
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
