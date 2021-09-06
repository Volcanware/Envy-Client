package mathax.client.legacy.systems.config;

import mathax.client.legacy.gui.tabs.builtin.ConfigTab;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.systems.Systems;
import mathax.client.legacy.utils.misc.Version;
import mathax.client.legacy.utils.render.color.Color;
import mathax.client.legacy.utils.render.color.RainbowColors;
import mathax.client.legacy.systems.System;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.List;

public class Config extends System<Config> {
    public final Version version;

    public String font = ConfigTab.font.get();
    public boolean customFont = ConfigTab.customFont.get();
    public int rotationHoldTicks = ConfigTab.rotationHoldTicks.get();
    public boolean useTeamColor = ConfigTab.useTeamColor.get();

    public String prefix = ConfigTab.prefix.get();
    public boolean rainbowPrefix = ConfigTab.rainbowPrefix.get();
    public boolean openChatOnPrefix = ConfigTab.openChatOnPrefix.get();
    public boolean chatCommandsInfo = ConfigTab.chatCommandsInfo.get();
    public boolean deleteChatCommandsInfo = ConfigTab.deleteChatCommandsInfo.get();
    public boolean chatCommandsToast = ConfigTab.chatCommandsToast.get();
    public boolean playSoundToast = ConfigTab.playSoundToast.get();

    public List<String> dontShowAgainPrompts = new ArrayList<>();

    public Config() {
        super("Config");

        ModMetadata metadata = FabricLoader.getInstance().getModContainer("mathaxlegacy").get().getMetadata();

        String versionString = metadata.getVersion().getFriendlyString();
        if (versionString.contains("-")) versionString = versionString.split("-")[0];

        version = new Version(versionString);
    }

    public static Config get() {
        return Systems.get(Config.class);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putString("version", version.toString());

        tag.putString("font", font);
        tag.putBoolean("customFont", customFont);
        tag.putDouble("rainbowSpeed", RainbowColors.GLOBAL.getSpeed());
        tag.putInt("rotationHoldTicks", rotationHoldTicks);
        tag.putBoolean("useTeamColor", useTeamColor);
        tag.putBoolean("rainbowPrefix", rainbowPrefix);
        tag.putString("prefix", prefix);
        tag.putBoolean("openChatOnPrefix", openChatOnPrefix);
        tag.putBoolean("chatCommandsInfo", chatCommandsInfo);
        tag.putBoolean("deleteChatCommandsInfo", deleteChatCommandsInfo);
        tag.putBoolean("chatCommandsToast", chatCommandsToast);
        tag.putBoolean("playSoundToast", playSoundToast);

        tag.put("dontShowAgainPrompts", listToNbt(dontShowAgainPrompts));
        return tag;
    }

    @Override
    public Config fromTag(NbtCompound tag) {
        font = getString(tag, "font", ConfigTab.font);
        customFont = getBoolean(tag, "customFont", ConfigTab.customFont);
        RainbowColors.GLOBAL.setSpeed(tag.contains("rainbowSpeed") ? tag.getDouble("rainbowSpeed") : ConfigTab.rainbowSpeed.getDefaultValue() / 100);
        rotationHoldTicks = getInt(tag, "rotationHoldTicks", ConfigTab.rotationHoldTicks);
        useTeamColor = getBoolean(tag, "useTeamColor", ConfigTab.useTeamColor);

        prefix = getString(tag, "prefix", ConfigTab.prefix);
        rainbowPrefix = getBoolean(tag, "rainbowPrefix", ConfigTab.rainbowPrefix);
        openChatOnPrefix = getBoolean(tag, "openChatOnPrefix", ConfigTab.openChatOnPrefix);
        chatCommandsInfo = getBoolean(tag, "chatCommandsInfo", ConfigTab.chatCommandsInfo);
        deleteChatCommandsInfo = getBoolean(tag, "deleteChatCommandsInfo", ConfigTab.deleteChatCommandsInfo);
        chatCommandsToast = getBoolean(tag, "chatCommandsToast", ConfigTab.chatCommandsToast);
        playSoundToast = getBoolean(tag, "playSoundToast", ConfigTab.playSoundToast);

        dontShowAgainPrompts.clear();
        for (NbtElement item : tag.getList("dontShowAgainPrompts", NbtElement.STRING_TYPE)) {
            dontShowAgainPrompts.add(item.asString());
        }

        return this;
    }

    private boolean getBoolean(NbtCompound tag, String key, Setting<Boolean> setting) {
        return tag.contains(key) ? tag.getBoolean(key) : setting.get();
    }

    private String getString(NbtCompound tag, String key, Setting<String> setting) {
        return tag.contains(key) ? tag.getString(key) : setting.get();
    }

    private double getDouble(NbtCompound tag, String key, Setting<Double> setting) {
        return tag.contains(key) ? tag.getDouble(key) : setting.get();
    }

    private int getInt(NbtCompound tag, String key, Setting<Integer> setting) {
        return tag.contains(key) ? tag.getInt(key) : setting.get();
    }

    private NbtList listToNbt(List<String> lst) {
        NbtList nbt = new NbtList();
        for (String item: lst)
            nbt.add(NbtString.of(item));
        return nbt;
    }
}
