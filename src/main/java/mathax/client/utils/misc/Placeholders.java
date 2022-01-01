package mathax.client.utils.misc;

import mathax.client.utils.Version;
import mathax.client.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mathax.client.MatHax.mc;

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
        switch (placeholder) {
            case "%version%":
                return Version.getStylized();
            case "%mc_version%":
                return Version.getMinecraft();
            case "%player%":
            case "%username%":
                return mc.getSession().getUsername();
            case "%server%":
                return Utils.getWorldName();
            case "%health%":
                return String.valueOf(Utils.getPlayerHealth());
            default:
                return "";
        }
    }
}
