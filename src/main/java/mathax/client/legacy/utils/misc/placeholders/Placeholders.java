package mathax.client.legacy.utils.misc.placeholders;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.gui.screens.*;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.network.Http;
import mathax.client.legacy.utils.player.ChatUtils;
import mathax.client.legacy.utils.player.PlayerUtils;
import mathax.client.legacy.utils.world.TickRate;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.*;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mathax.client.legacy.utils.Utils.mc;

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
            case "%version%":      return MatHaxClientLegacy.clientVersionWithV;
            case "%mc_version%":   return SharedConstants.getGameVersion().getName();
            case "%player%":
            case "%username%":     return mc.getSession().getUsername();
            case "%server%":       return Utils.getWorldName();
            default:               return "";
        }
    }
}
