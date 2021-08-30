package mathax.client.legacy.systems.modules.render.hud;

import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.gui.screens.HudElementScreen;
import mathax.client.legacy.gui.tabs.builtin.HudTab;
import mathax.client.legacy.settings.Settings;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.misc.ISerializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

public abstract class HudElement implements ISerializable<HudElement> {
    public final String name, title;
    public final String description;

    public boolean active;

    protected final HUD hud;

    public final Settings settings = new Settings();
    public final BoundingBox box = new BoundingBox();

    protected final MinecraftClient mc;

    public HudElement(HUD hud, String name, String description, boolean nothing) {
        this.hud = hud;
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.description = description;
        this.mc = MinecraftClient.getInstance();
    }

    public HudElement(HUD hud, String name, String description) {
        this.hud = hud;
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.description = description;
        this.mc = MinecraftClient.getInstance();
    }

    public void toggle(boolean onToggle) {
        if (!active) {
            active = true;

            if (onToggle) {
                MatHaxClientLegacy.EVENT_BUS.subscribe(this);
            }
        }
        else {
            if (onToggle) {
                MatHaxClientLegacy.EVENT_BUS.unsubscribe(this);
            }
            active = false;
        }
    }

    public void toggle() {
        toggle(true);
    }

    public abstract void update(HudRenderer renderer);

    public abstract void render(HudRenderer renderer);

    protected boolean isInEditor() {
        return HudTab.INSTANCE.isScreen(mc.currentScreen) || mc.currentScreen instanceof HudElementScreen || !Utils.canUpdate();
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putString("name", name);
        tag.putBoolean("active", active);
        tag.put("settings", settings.toTag());
        tag.put("box", box.toTag());

        return tag;
    }

    @Override
    public HudElement fromTag(NbtCompound tag) {
        active = tag.contains("active");
        if (tag.contains("settings")) settings.fromTag(tag.getCompound("settings"));
        box.fromTag(tag.getCompound("box"));

        return this;
    }
}
