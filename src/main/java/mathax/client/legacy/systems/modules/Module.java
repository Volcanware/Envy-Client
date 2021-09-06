package mathax.client.legacy.systems.modules;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.widgets.WWidget;
import mathax.client.legacy.settings.Settings;
import mathax.client.legacy.systems.config.Config;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.misc.ISerializable;
import mathax.client.legacy.utils.misc.Keybind;
import mathax.client.legacy.utils.player.ChatUtils;
import mathax.client.legacy.utils.render.MatHaxToast;
import mathax.client.legacy.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

public abstract class Module implements ISerializable<Module> {
    protected final MinecraftClient mc;

    public final Category category;
    public final Item icon;
    public final String name;
    public final String title;
    public final String description;
    public final Color color;

    public final Settings settings = new Settings();

    private boolean active;
    private boolean toggleMessage = true;
    private boolean toggleToast = true;
    private boolean visible = true;

    public boolean serialize = true;

    public final Keybind keybind = Keybind.none();
    public boolean toggleOnBindRelease = false;

    public Module(Category category, Item icon, String name, String description) {
        this.mc = MinecraftClient.getInstance();
        this.category = category;
        this.icon = icon;
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.description = description;
        this.color = Color.fromHsv(Utils.random(0.0, 360.0), 0.35, 1);
    }

    public WWidget getWidget(GuiTheme theme) {
        return null;
    }

    public void onActivate() {}
    public void onDeactivate() {}

    public void toggle(boolean onToggle) {
        if (!active) {
            active = true;
            Modules.get().addActive(this);

            settings.onActivated();

            if (onToggle) {
                MatHaxClientLegacy.EVENT_BUS.subscribe(this);
                onActivate();
            }
        }
        else {
            if (onToggle) {
                MatHaxClientLegacy.EVENT_BUS.unsubscribe(this);
                onDeactivate();
            }

            active = false;
            Modules.get().removeActive(this);
        }
    }

    public void toggle() {
        toggle(true);
    }

    public void sendToggledMsg(String title, Module module) {
        if (!module.isMessageEnabled()) return;
        if (Config.get().chatCommandsInfo) ChatUtils.sendMsg(this.hashCode(), Formatting.GRAY, "Toggled (highlight)%s(default) %s(default).", title, getOnOff(module));
    }

    public void sendToggledToast(String title, Module module) {
        if (!module.isToastEnabled()) return;
        mc.getToastManager().add(new MatHaxToast(module.icon, module.category.color, title, Formatting.GRAY + "Toggled " + getOnOff(module) + Formatting.GRAY + "."));
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

        toggleToast(tag.getBoolean("toggleToast"));
        boolean active = tag.getBoolean("active");
        if (active != isActive()) toggle(Utils.canUpdate());
        setVisible(tag.getBoolean("visible"));

        return this;
    }
    public void toClipboard() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NbtIo.writeCompressed(toTag(), byteArrayOutputStream);
            mc.keyboard.setClipboard(Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fromClipboard() {
        try {
            byte[] data = Base64.getDecoder().decode(mc.keyboard.getClipboard());
            ByteArrayInputStream bis = new ByteArrayInputStream(data);

            NbtCompound pasted = NbtIo.readCompressed(new DataInputStream(bis));
            NbtCompound current = this.toTag();

            for (String key : current.getKeys()) if (!pasted.getKeys().contains(key)) return;
            if (!pasted.getString("name").equals(current.getString("name"))) return;

            this.fromTag(pasted);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
