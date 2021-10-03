package mathax.legacy.client.systems.modules;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.settings.Settings;
import mathax.legacy.client.systems.config.Config;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.misc.ISerializable;
import mathax.legacy.client.utils.misc.Keybind;
import mathax.legacy.client.utils.player.ChatUtils;
import mathax.legacy.client.utils.render.ToastSystem;
import mathax.legacy.client.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public abstract class Module implements ISerializable<Module> {
    protected final MinecraftClient mc;

    public final Category category;
    public final Item icon;
    public final String name, title, description;
    public final Color color;

    public final Settings settings = new Settings();

    private boolean active;
    private boolean toggleMessage = true;
    private boolean toggleToast = true;
    private boolean visible = true;

    public boolean serialize = true;
    public boolean runInMainMenu = false;
    public boolean autoSubscribe = true;

    public final Keybind keybind = Keybind.none();
    public boolean toggleOnBindRelease = false;

    public Module(Category category, Item icon, String name, String description) {
        this.mc = MinecraftClient.getInstance();
        this.category = category;
        this.icon = icon;
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.description = description;
        this.color = new Color(Utils.random(0, 255), Utils.random(0, 255), Utils.random(0, 255));
    }

    public WWidget getWidget(GuiTheme theme) {
        return null;
    }

    public void onActivate() {}
    public void onDeactivate() {}

    public void toggle() {
        if (!active) {
            active = true;
            Modules.get().addActive(this);

            settings.onActivated();

            if (runInMainMenu || Utils.canUpdate()) {
                if (autoSubscribe) MatHaxLegacy.EVENT_BUS.subscribe(this);
                onActivate();
            }
        }
        else {
            if (runInMainMenu || Utils.canUpdate()) {
                if (autoSubscribe) MatHaxLegacy.EVENT_BUS.unsubscribe(this);
                onDeactivate();
            }

            active = false;
            Modules.get().removeActive(this);
        }
    }

    public void sendToggledMsg(String name, Module module) {
        if (!module.isMessageEnabled()) return;
        if (Config.get().chatCommandsInfo) ChatUtils.sendMsg(this.hashCode(), Formatting.GRAY, "Toggled (highlight)%s(default) %s(default).", title, getOnOff(module));
    }

    public void sendToggledToast(String name, Module module) {
        if (!module.isToastEnabled()) return;
        mc.getToastManager().add(new ToastSystem(module.icon, module.category.color, title, null, Formatting.GRAY + "Toggled " + getOnOff(module) + Formatting.GRAY + "."));
    }

    private String getOnOff(Module module) {
        return module.active ? Formatting.GREEN + "on" : Formatting.RED + "off";
    }

    public void info(Text message) {
        ChatUtils.sendMsg(title, message);
    }

    public void info(String message, Object... args) {
        ChatUtils.info(title, message, args);
    }

    public void warning(String message, Object... args) {
        ChatUtils.warning(title, message, args);
    }

    public void error(String message, Object... args) {
        ChatUtils.error(title, message, args);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void toggleMessage(boolean toggleMessage) {
        this.toggleMessage = toggleMessage;
    }

    public boolean isMessageEnabled() {
        return toggleMessage;
    }

    public void toggleToast(boolean toggleToast) {
        this.toggleToast = toggleToast;
    }

    public boolean isToastEnabled() {
        return toggleToast;
    }

    public boolean isActive() {
        return active;
    }

    public String getInfoString() {
        return null;
    }

    @Override
    public NbtCompound toTag() {
        if (!serialize) return null;
        NbtCompound tag = new NbtCompound();

        tag.putString("name", name);
        tag.put("keybind", keybind.toTag());
        tag.putBoolean("toggleOnKeyRelease", toggleOnBindRelease);
        tag.put("settings", settings.toTag());

        tag.putBoolean("toggleMessage", toggleMessage);
        tag.putBoolean("toggleToast", toggleToast);
        tag.putBoolean("active", active);
        tag.putBoolean("visible", visible);

        return tag;
    }

    @Override
    public Module fromTag(NbtCompound tag) {
        // General
        if (tag.contains("key")) keybind.set(true, tag.getInt("key"));
        else keybind.fromTag(tag.getCompound("keybind"));

        toggleOnBindRelease = tag.getBoolean("toggleOnKeyRelease");

        // Settings
        NbtElement settingsTag = tag.get("settings");
        if (settingsTag instanceof NbtCompound) settings.fromTag((NbtCompound) settingsTag);

        toggleMessage(tag.getBoolean("toggleMessage"));
        toggleToast(tag.getBoolean("toggleToast"));
        boolean active = tag.getBoolean("active");
        if (active != isActive()) toggle();
        setVisible(tag.getBoolean("visible"));

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return Objects.equals(name, module.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
