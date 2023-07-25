package envy.client.utils.misc;

import envy.client.Envy;
import envy.client.utils.Utils;
import envy.client.utils.Version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholders {
    private static final Pattern pattern = Pattern.compile("(%version%|%mc_version%|%player%|%username%|%server%)");

    public static String apply(String string) {
        Matcher matcher = pattern.matcher(string);
        StringBuffer sb = new StringBuffer(string.length());

        while (matcher.find()) {
            matcher.appendReplacement(sb, getReplacement(matcher.group(1)));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private static String getReplacement(String placeholder) {
        return switch (placeholder) {
            case "%version%" -> Version.getStylized();
            case "%mc_version%" -> Version.getMinecraft();
            case "%player%", "%username%" -> Envy.mc.getSession().getUsername();
            case "%server%" -> Utils.getWorldName();
            case "%health%" -> String.valueOf(Utils.getPlayerHealth());
            default -> "";
        };
    }
}
