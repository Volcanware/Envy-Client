package mathax.client.legacy.utils.player;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.utils.render.color.RainbowColor;
import mathax.client.legacy.utils.render.color.RainbowColors;
import mathax.client.legacy.mixin.ChatHudAccessor;
import mathax.client.legacy.systems.config.Config;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import static mathax.client.legacy.utils.Utils.mc;

public class ChatUtils {
    private static final RainbowColor RAINBOW = new RainbowColor();

    // Default
    public static void info(String message, Object... args) {
        sendMsg(Formatting.GRAY, message, args);
    }

    public static void info(String prefix, String message, Object... args) {
        sendMsg(0, prefix, Formatting.DARK_RED, Formatting.GRAY, message, args);
    }

    // Warning
    public static void warning(String message, Object... args) {
        sendMsg(Formatting.YELLOW, message, args);
    }

    public static void warning(String prefix, String message, Object... args) {
        sendMsg(0, prefix, Formatting.DARK_RED, Formatting.YELLOW, message, args);
    }

    // Error
    public static void error(String message, Object... args) {
        sendMsg(Formatting.RED, message, args);
    }

    public static void error(String prefix, String message, Object... args) {
        sendMsg(0, prefix, Formatting.DARK_RED, Formatting.RED, message, args);
    }

    // Misc
    public static void sendMsg(Text message) {
        sendMsg(null, message);
    }

    public static void sendMsg(String prefix, Text message) {
        sendMsg(0, prefix, Formatting.DARK_RED, message);
    }

    public static void sendMsg(Formatting color, String message, Object... args) {
        sendMsg(0, null, null, color, message, args);
    }

    public static void sendMsg(int id, Formatting color, String message, Object... args) {
        sendMsg(id, null, null, color, message, args);
    }

    public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, Formatting messageColor, String messageContent, Object... args) {
        sendMsg(id, prefixTitle, prefixColor, formatMsg(messageContent, messageColor, args), messageColor);
    }

    public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, String messageContent, Formatting messageColor) {
        BaseText message = new LiteralText(messageContent);
        message.setStyle(message.getStyle().withFormatting(messageColor));
        sendMsg(id, prefixTitle, prefixColor, message);
    }

    public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, Text msg) {
        if (mc.world == null) return;

        BaseText message = new LiteralText("");
        message.append(getMatHaxPrefix());
        if (prefixTitle != null) message.append(getCustomPrefix(prefixTitle, prefixColor));
        message.append(msg);

        if (!Config.get().deleteChatCommandsInfo) id = 0;

        ((ChatHudAccessor) mc.inGameHud.getChatHud()).add(message, id);
    }

    private static BaseText getCustomPrefix(String prefixTitle, Formatting prefixColor) {
        BaseText prefix = new LiteralText("");
        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.GRAY));

        prefix.append("[");

        BaseText moduleTitle = new LiteralText(prefixTitle);
        moduleTitle.setStyle(moduleTitle.getStyle().withFormatting(prefixColor));
        prefix.append(moduleTitle);

        prefix.append("] ");

        return prefix;
    }

    private static BaseText getMatHaxPrefix() {
        BaseText mathax = new LiteralText("");
        BaseText prefix = new LiteralText("");

        RAINBOW.setSpeed(RainbowColors.GLOBAL.getSpeed());

        if (Config.get().rainbowPrefix) {
            mathax.append(new LiteralText("M").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("a").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("t").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("H").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("a").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("x").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText(" ").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("L").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("e").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("g").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("a").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("c").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
            mathax.append(new LiteralText("y").setStyle(mathax.getStyle().withColor(new TextColor(RAINBOW.getNext().getPacked()))));
        } else {
            mathax = new LiteralText("MatHax Legacy");
            mathax.setStyle(mathax.getStyle().withColor(MatHaxClientLegacy.INSTANCE.MATHAX_COLOR.getPacked()));
        }

        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.GRAY));
        prefix.append("[");
        prefix.append(mathax);
        prefix.append("] ");

        return prefix;
    }

    private static String formatMsg(String format, Formatting defaultColor, Object... args) {
        String msg = String.format(format, args);
        msg = msg.replaceAll("\\(default\\)", defaultColor.toString());
        msg = msg.replaceAll("\\(highlight\\)", Formatting.WHITE.toString());
        msg = msg.replaceAll("\\(underline\\)", Formatting.UNDERLINE.toString());

        return msg;
    }

    public static BaseText formatCoords(Vec3d pos) {
        String coordsString = String.format("(highlight)(underline)%.0f, %.0f, %.0f(default)", pos.x, pos.y, pos.z);
        coordsString = formatMsg(coordsString, Formatting.GRAY);
        BaseText coordsText = new LiteralText(coordsString);
        coordsText.setStyle(coordsText.getStyle()
                .withFormatting(Formatting.BOLD)
                .withClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        String.format("%sb goto %d %d %d", Config.get().prefix, (int) pos.x, (int) pos.y, (int) pos.z)
                ))
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new LiteralText("Set as Baritone goal")
                ))
        );
        return coordsText;
    }
}
