package mathax.legacy.client.systems.modules.chat;

import it.unimi.dsi.fastutil.chars.Char2CharArrayMap;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import mathax.legacy.client.Version;
import mathax.legacy.client.events.game.ReceiveMessageEvent;
import mathax.legacy.client.events.game.SendMessageEvent;
import mathax.legacy.client.mixin.ChatHudAccessor;
import mathax.legacy.client.mixininterface.IChatHud;
import mathax.legacy.client.systems.commands.Commands;
import mathax.legacy.client.systems.commands.commands.SayCommand;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.player.ChatUtils;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.settings.*;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.item.Items;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class BetterChat extends Module {
    private final Char2CharMap FULL_WIDTH = new Char2CharArrayMap();
    {
        String[] chars = "aábcčdďeéěfghchiíjklmnňoópqrřsštťuúůvwxyýzžAÁBCČDĎEÉĚFGHCHIÍJKLMNŇOÓPQRŘSŠTŤUÚŮVWXYÝZŽ0123456789|[]!?.,{}()\"'".split("");
        String[] fontchars = "ａáｂｃčｄďｅéěｆｇｈｃｈｉíｊｋｌｍｎňｏóｐｑｒřｓšｔťｕúůｖｗｘｙýｚžＡÁＢＣČＤĎＥÉĚＦＧＨＣＨＩÍＪＫＬＭＮŇＯÓＰＱＲŘＳŠＴŤＵÚŮＶＷＸＹÝＺŽ０１２３４５６７８９｜［］！？．，｛｝（）\"＇".split("");
        for (int i = 0; i < chars.length; i++) FULL_WIDTH.put(chars[i].charAt(0), fontchars[i].charAt(0));
    }
    private final Char2CharMap SMALL_CAPS = new Char2CharArrayMap();
    {
        String[] chars = "abcdefghchijklmnopqrstuvwxyzABCDEFGHCHIJKLMNOPQRSTUVWXYZ0123456789|[]!?.,{}()\"'".split("");
        String[] fontchars = "ᴀʙᴄᴅᴇꜰɢʜᴄʜɪᴊᴋʟᴍɴᴏᴘqʀꜱᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜᴄʜɪᴊᴋʟᴍɴᴏᴩQʀꜱᴛᴜᴠᴡxYᴢ0123456789｜[]!?.,{}()\"'".split("");
        for (int i = 0; i < chars.length; i++) SMALL_CAPS.put(chars[i].charAt(0), fontchars[i].charAt(0));
    }

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private final SimpleDateFormat timeFormatSeconds = new SimpleDateFormat("HH:mm:ss");

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgFilter = settings.createGroup("Filter");
    private final SettingGroup sgLongerChat = settings.createGroup("Longer Chat");
    private final SettingGroup sgSuffix = settings.createGroup("Suffix");

    public final Setting<Boolean> fancy = sgGeneral.add(new BoolSetting.Builder()
        .name("fancy-chat")
        .description("Makes your messages ＦＡＮＣＹ!")
        .defaultValue(false)
        .build()
    );

    public final Setting<FancyType> fancyType = sgGeneral.add(new EnumSetting.Builder<FancyType>()
        .name("fancy-type")
        .description("Determines what font or style to use in the your messages.")
        .defaultValue(FancyType.FullWidth)
        .visible(fancy::get)
        .build()
    );

    public final Setting<Boolean> annoy = sgGeneral.add(new BoolSetting.Builder()
        .name("annoy")
        .description("Makes your messages aNnOyInG.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> greenChat = sgGeneral.add(new BoolSetting.Builder()
        .name("green-chat")
        .description("Adds '>' to your chat messages.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> timestamps = sgGeneral.add(new BoolSetting.Builder()
        .name("timestamps")
        .description("Adds client side time stamps to the beginning of chat messages.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> timestampsSeconds = sgGeneral.add(new BoolSetting.Builder()
        .name("seconds")
        .description("Adds seconds to the timestamps.")
        .defaultValue(true)
        .visible(timestamps::get)
        .build()
    );

    private final Setting<Boolean> playerHeads = sgGeneral.add(new BoolSetting.Builder()
        .name("player-heads")
        .description("Displays player heads next to their messages.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> coordsProtection = sgGeneral.add(new BoolSetting.Builder()
        .name("coords-protection")
        .description("Prevents you from sending messages in chat that may contain coordinates.")
        .defaultValue(false)
        .build()
    );

    // Filter

    private final Setting<Boolean> antiSpam = sgFilter.add(new BoolSetting.Builder()
        .name("anti-spam")
        .description("Blocks duplicate messages from filling your chat.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> antiSpamDepth = sgFilter.add(new IntSetting.Builder()
        .name("depth")
        .description("How many messages to filter.")
        .defaultValue(20)
        .min(1)
        .sliderMin(1)
        .visible(antiSpam::get)
        .build()
    );

    private final Setting<Boolean> filterRegex = sgFilter.add(new BoolSetting.Builder()
        .name("filter-regex")
        .description("Filter out chat messages that match the regex filter.")
        .defaultValue(false)
        .build()
    );

    private final Setting<List<String>> regexFilters = sgFilter.add(new StringListSetting.Builder()
        .name("regex-filter")
        .description("Regex filter used for filtering chat messages.")
        .defaultValue(Collections.emptyList())
        .visible(filterRegex::get)
        .build()
    );


    // Longer chat

    private final Setting<Boolean> infiniteChatBox = sgLongerChat.add(new BoolSetting.Builder()
        .name("infinite-chat-box")
        .description("Lets you type infinitely long messages.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> longerChatHistory = sgLongerChat.add(new BoolSetting.Builder()
        .name("longer-chat-history")
        .description("Extends chat length.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> longerChatLines = sgLongerChat.add(new IntSetting.Builder()
        .name("extra-lines")
        .description("The amount of extra chat lines.")
        .defaultValue(1000)
        .min(100)
        .sliderMax(1000)
        .visible(longerChatHistory::get)
        .build()
    );

    // Suffix

    private final Setting<Boolean> suffix = sgSuffix.add(new BoolSetting.Builder()
        .name("suffix")
        .description("Adds MatHax suffix to your chat messages.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> suffixAppendVersion = sgSuffix.add(new BoolSetting.Builder()
        .name("append-version")
        .description("Adds MatHax version to the suffix.")
        .defaultValue(false)
        .build()
    );

    private final Setting<SuffixFont> suffixFont = sgSuffix.add(new EnumSetting.Builder<SuffixFont>()
        .name("suffix-font")
        .description("Determines what font to use in the suffix.")
        .defaultValue(SuffixFont.FullWidth)
        .build()
    );

    public BetterChat() {
        super(Categories.Chat, Items.DROPPER, "better-chat", "Improves your chat experience in various ways");
    }

    @EventHandler
    private void onMessageRecieve(ReceiveMessageEvent event) {
        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getVisibleMessages().removeIf((message) -> message.getId() == event.id && event.id != 0);
        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getMessages().removeIf((message) -> message.getId() == event.id && event.id != 0);

        Text message = event.message;

        if (filterRegex.get()) {
            for (int i = 0; i < regexFilters.get().size(); i++) {
                Pattern p;
                try {
                    p = Pattern.compile(regexFilters.get().get(i));
                }
                catch (PatternSyntaxException e) {
                    error("Removing Invalid regex: %s", regexFilters.get().get(i));
                    regexFilters.get().remove(i);
                    continue;
                }


                if (p.matcher(message.getString()).find()) {
                    event.cancel();
                    return;
                }
            }
        }

        if (timestamps.get()) {
            Matcher matcher = Pattern.compile("^(<[0-9]{2}:[0-9]{2}>\\s)").matcher(message.getString());
            if (matcher.matches()) message.getSiblings().subList(0, 8).clear();

            String time = "";
            if (timestampsSeconds.get()) {
                time = timeFormatSeconds.format(new Date());
            } else {
                time = timeFormat.format(new Date());
            }

            Text timestamp = new LiteralText("<" + time + "> ").formatted(Formatting.GRAY);

            message = new LiteralText("").append(timestamp).append(message);
        }

        if (playerHeads.get()) {
            message = new LiteralText("  ").append(message);
        }

        for (int i = 0; i < antiSpamDepth.get(); i++) {
            if (antiSpam.get()) {
                Text antiSpammed = appendAntiSpam(message, i);
                if (antiSpammed != null) {
                    message = antiSpammed;
                    ((ChatHudAccessor) mc.inGameHud.getChatHud()).getMessages().remove(i);
                    ((ChatHudAccessor) mc.inGameHud.getChatHud()).getVisibleMessages().remove(i);
                }
            }
        }

        event.cancel();
        ((IChatHud) mc.inGameHud.getChatHud()).add(message, event.id, mc.inGameHud.getTicks(), false);
    }

    private Text appendAntiSpam(Text text, int index) {
        List<ChatHudLine<OrderedText>> visibleMessages = ((ChatHudAccessor) mc.inGameHud.getChatHud()).getVisibleMessages();
        if (visibleMessages.isEmpty() || index < 0 || index > visibleMessages.size() - 1) return null;

        ChatHudLine<OrderedText> visibleMessage = visibleMessages.get(index);

        LiteralText parsed = new LiteralText("");

        visibleMessage.getText().accept((i, style, codePoint) -> {
            parsed.append(new LiteralText(new String(Character.toChars(codePoint))).setStyle(style));
            return true;
        });

        String oldMessage = parsed.getString();
        String newMessage = text.getString();

        if (oldMessage.equals(newMessage)) {
            return parsed.append(new LiteralText(" (2)").formatted(Formatting.GRAY));
        }
        else {
            Matcher matcher = Pattern.compile(".*(\\([0-9]+\\)$)").matcher(oldMessage);

            if (!matcher.matches()) return null;

            String group = matcher.group(matcher.groupCount());
            int number = Integer.parseInt(group.substring(1, group.length() - 1));

            String counter = " (" + number + ")";

            if (oldMessage.substring(0, oldMessage.length() - counter.length()).equals(newMessage)) {
                for (int i = 0; i < counter.length(); i++) parsed.getSiblings().remove(parsed.getSiblings().size() - 1);
                return parsed.append(new LiteralText( " (" + (number + 1) + ")").formatted(Formatting.GRAY));
            }
        }

        return null;
    }

    private boolean filterRegex(Pattern regex) {
        LiteralText parsed = new LiteralText("");

        ((ChatHudAccessor) mc.inGameHud.getChatHud()).getVisibleMessages().get(0).getText().accept((i, style, codePoint) -> {
            parsed.append(new LiteralText(new String(Character.toChars(codePoint))).setStyle(style));
            return true;
        });

        return regex.matcher(parsed.getString()).find();
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        String message = event.message;

        if (annoy.get()) message = applyAnnoy(message);

        if (fancy.get()) {
            if (fancyType.get() == FancyType.FullWidth) {
                message = applyFull(message);
            }
            if (fancyType.get() == FancyType.SmallCAPS) {
                message = applySmall(message);
            }
            if (fancyType.get() == FancyType.UwU) {
                message = applyUwU(message);
            }
            if (fancyType.get() == FancyType.Leet) {
                message = applyLeet(message);
            }
        }

        message = getGreenChat() + message + getSuffix();

        if (coordsProtection.get() && containsCoordinates(message)) {
            BaseText warningMessage = new LiteralText(Formatting.GRAY + "It looks like there are coordinates in your message! ");

            BaseText sendButton = getSendButton(message);
            warningMessage.append(sendButton);

            ChatUtils.sendMsg(warningMessage);

            event.cancel();
            return;
        }

        event.message = message;
    }

    // Annoy

    public String applyAnnoy(String message) {
        StringBuilder sb = new StringBuilder(message.length());
        boolean upperCase = true;
        for (int cp : message.codePoints().toArray()) {
            if (upperCase) sb.appendCodePoint(Character.toUpperCase(cp));
            else sb.appendCodePoint(Character.toLowerCase(cp));
            upperCase = !upperCase;
        }
        message = sb.toString();
        return message;
    }

    // FullWidth

    public String applyFull(String message) {
        StringBuilder sb = new StringBuilder();

        for (char ch : message.toCharArray()) {
            if (FULL_WIDTH.containsKey(ch)) sb.append(FULL_WIDTH.get(ch));
            else sb.append(ch);
        }

        return sb.toString();
    }

    // Small CAPS

    public String applySmall(String message) {
        StringBuilder sb = new StringBuilder();

        for (char ch : message.toCharArray()) {
            if (SMALL_CAPS.containsKey(ch)) sb.append(SMALL_CAPS.get(ch));
            else sb.append(ch);
        }

        return sb.toString();
    }

    // UWU

    public String applyUwU(String message) {
        String one = message.replace("ove", "uv");
        String two = one.replace("the", "da");
        String three = two.replace("is", "ish");
        String four = three.replace("r", "w");
        String five = four.replace("ve", "v");
        String six = five.replace("OVE", "UV");
        String seven = six.replace("THE", "DA");
        String eight = seven.replace("IS", "ISH");
        String nine = eight.replace("R", "W");
        String ten = nine.replace("L", "W");
        return ten.replace("l", "w");
    }

    // Leet

    public String applyLeet(String message) {
        String one = message.replace("a", "4");
        String two = one.replace("e", "3");
        String three = two.replace("g", "6");
        String four = three.replace("l" , "1");
        String five = four.replace("i" , "1");
        String six = five.replace("o", "0");
        String seven = six.replace("s", "$");
        String eight = seven.replace("A", "4");
        String nine = eight.replace("E", "3");
        String ten = nine.replace("G", "6");
        String eleven = ten.replace("L" , "1");
        String twelve = eleven.replace("I" , "1");
        String thirteen = twelve.replace("O", "0");
        String fourteen = thirteen.replace("S", "$");
        String fifteen = fourteen.replace("T", "7");
        return fifteen.replace("t", "7");
    }

    // GreenChat and Suffix

    public String getGreenChat() {
        return greenChat.get() ? "> " : "";
    }

    public String getSuffix() {
        if (suffix.get()) {
            if (suffixFont.get() == SuffixFont.FullWidth) {
                return applyFull(getSuffixString());
            } else if (suffixFont.get() == SuffixFont.SmallCAPS) {
                return applySmall(getSuffixString());
            } else {
                return getSuffixString();
            }
        } else {
            return "";
        }
    }

    private String getSuffixString() {
        if (suffixAppendVersion.get()) {
            return " | MatHax Legacy " + Version.getStylized();
        } else {
            return " | MatHax Legacy";
        }
    }

    // Coords Protection

    public boolean containsCoordinates(String message) {
        return message.matches(".*(?<x>-?\\d{3,}(?:\\.\\d*)?)(?:\\s+(?<y>\\d{1,3}(?:\\.\\d*)?))?\\s+(?<z>-?\\d{3,}(?:\\.\\d*)?).*");
    }

    public BaseText getSendButton(String message) {
        BaseText sendButton = new LiteralText("[SEND ANYWAY]");
        BaseText hintBaseText = new LiteralText("");

        BaseText hintMsg = new LiteralText("Send your message to the global chat even if there are coordinates:");
        hintMsg.setStyle(hintBaseText.getStyle().withFormatting(Formatting.GRAY));
        hintBaseText.append(hintMsg);

        hintBaseText.append(new LiteralText('\n' + message));

        sendButton.setStyle(sendButton.getStyle()
        .withFormatting(Formatting.DARK_RED)
        .withClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                Commands.get().get(SayCommand.class).toString(message)
            ))
        .withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                hintBaseText
            )));
        return sendButton;
    }

    // Longer chat

    public boolean isInfiniteChatBox() {
        return isActive() && infiniteChatBox.get();
    }

    public boolean isLongerChat() {
        return isActive() && longerChatHistory.get();
    }

    public boolean displayPlayerHeads() { return isActive() && playerHeads.get(); }

    public int getChatLength() {
        return longerChatLines.get();
    }

    public enum FancyType {
        FullWidth,
        SmallCAPS,
        UwU,
        Leet
    }

    public enum SuffixFont {
        Normal,
        FullWidth,
        SmallCAPS
    }
}
