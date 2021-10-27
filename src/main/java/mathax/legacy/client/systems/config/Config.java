package mathax.legacy.client.systems.config;

import mathax.legacy.client.gui.tabs.builtin.ConfigTab;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.systems.Systems;
import mathax.legacy.client.utils.Version;
import mathax.legacy.client.systems.System;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.List;

public class Config extends System<Config> {
    // Visual
    public String font = ConfigTab.font.get();
    public boolean customFont = ConfigTab.customFont.get();
    public double rainbowSpeed = ConfigTab.rainbowSpeed.get();

    // Chat
    public String prefix = ConfigTab.prefix.get();
    public boolean prefixOpensConsole = ConfigTab.prefixOpensConsole.get();
    public boolean chatFeedback = ConfigTab.chatFeedback.get();
    public boolean deleteChatFeedback = ConfigTab.deleteChatFeedback.get();

    // Toasts
    public boolean toastFeedback = ConfigTab.toastFeedback.get();
    public int toastDuration = ConfigTab.toastDuration.get();
    public boolean toastSound = ConfigTab.toastSound.get();

    // Misc
    public int rotationHoldTicks = ConfigTab.rotationHoldTicks.get();
    public boolean useTeamColor = ConfigTab.useTeamColor.get();
    public List<String> dontShowAgainPrompts = new ArrayList<>();

    public Config() {
        super("Config");
    }

    public static Config get() {
        return Systems.get(Config.class);
    }

    // Serialisation

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putString("version", Version.get());

        tag.putString("font", font);
        tag.putBoolean("customFont", customFont);
        tag.putDouble("rainbowSpeed", ConfigTab.rainbowSpeed.get());

        tag.putString("prefix", prefix);
        tag.putBoolean("prefixOpensConsole", prefixOpensConsole);
        tag.putBoolean("chatFeedback", chatFeedback);
        tag.putBoolean("deleteChatFeedback", deleteChatFeedback);

        tag.putBoolean("toastFeedback", toastFeedback);
        tag.putInt("toastDuration", toastDuration);
        tag.putBoolean("toastSound", toastSound);

        tag.putInt("rotationHoldTicks", rotationHoldTicks);
        tag.putBoolean("useTeamColor", useTeamColor);
        tag.put("dontShowAgainPrompts", listToTag(dontShowAgainPrompts));

        return tag;
    }

    @Override
    public Config fromTag(NbtCompound tag) {
        font = getString(tag, "font", ConfigTab.font);
        customFont = getBoolean(tag, "customFont", ConfigTab.customFont);
        rainbowSpeed = getDouble(tag, "rainbowSpeed", ConfigTab.rainbowSpeed);

        prefix = getString(tag, "prefix", ConfigTab.prefix);
        prefixOpensConsole = getBoolean(tag, "prefixOpensConsole", ConfigTab.prefixOpensConsole);
        chatFeedback = getBoolean(tag, "chatFeedback", ConfigTab.chatFeedback);
        deleteChatFeedback = getBoolean(tag, "deleteChatFeedback", ConfigTab.deleteChatFeedback);

        toastFeedback = getBoolean(tag, "toastFeedback", ConfigTab.toastFeedback);
        toastDuration = getInt(tag, "toastDuration", ConfigTab.toastDuration);
        toastSound = getBoolean(tag, "toastSound", ConfigTab.toastSound);

        rotationHoldTicks = getInt(tag, "rotationHoldTicks", ConfigTab.rotationHoldTicks);
        useTeamColor = getBoolean(tag, "useTeamColor", ConfigTab.useTeamColor);
        dontShowAgainPrompts = listFromTag(tag, "dontShowAgainPrompts");

        return this;
    }


    // Utils

    private boolean getBoolean(NbtCompound tag, String key, Setting<Boolean> setting) {
        return tag.contains(key) ? tag.getBoolean(key) : setting.getDefaultValue();
    }

    private String getString(NbtCompound tag, String key, Setting<String> setting) {
        return tag.contains(key) ? tag.getString(key) : setting.getDefaultValue();
    }

    private double getDouble(NbtCompound tag, String key, Setting<Double> setting) {
        return tag.contains(key) ? tag.getDouble(key) : setting.getDefaultValue();
    }

    private int getInt(NbtCompound tag, String key, Setting<Integer> setting) {
        return tag.contains(key) ? tag.getInt(key) : setting.getDefaultValue();
    }

    private NbtList listToTag(List<String> list) {
        NbtList nbt = new NbtList();
        for (String item : list) nbt.add(NbtString.of(item));
        return nbt;
    }

    private List<String> listFromTag(NbtCompound tag, String key) {
        List<String> list = new ArrayList<>();
        for (NbtElement item : tag.getList(key, 8)) list.add(item.asString());
        return list;
    }
}
