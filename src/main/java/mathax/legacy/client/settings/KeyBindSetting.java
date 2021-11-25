package mathax.legacy.client.settings;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.eventbus.EventPriority;
import mathax.legacy.client.events.mathaxlegacy.KeyEvent;
import mathax.legacy.client.events.mathaxlegacy.MouseButtonEvent;
import mathax.legacy.client.gui.widgets.WKeyBind;
import mathax.legacy.client.utils.misc.KeyBind;
import mathax.legacy.client.utils.misc.input.KeyAction;
import net.minecraft.nbt.NbtCompound;

import java.util.function.Consumer;

public class KeyBindSetting extends Setting<KeyBind> {
    private final Runnable action;
    public WKeyBind widget;

    public KeyBindSetting(String name, String description, KeyBind defaultValue, Consumer<KeyBind> onChanged, Consumer<Setting<KeyBind>> onModuleActivated, IVisible visible, Runnable action) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.action = action;
        MatHaxLegacy.EVENT_BUS.subscribe(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onKeyBinding(KeyEvent event) {
        if (event.action == KeyAction.Release && widget != null && widget.onAction(true, event.key)) event.cancel();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onMouseButtonBinding(MouseButtonEvent event) {
        if (event.action == KeyAction.Release && widget != null && widget.onAction(false, event.button)) event.cancel();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Release && get().matches(true, event.key) && (module == null || module.isActive()) && action != null) action.run();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Release && get().matches(false, event.button) && (module == null || module.isActive()) && action != null) action.run();
    }

    @Override
    public void reset(boolean callbacks) {
        if (value == null) value = defaultValue.copy();
        else value.set(defaultValue);

        if (callbacks) onChanged();
    }

    @Override
    protected KeyBind parseImpl(String str) {
        try {
            return KeyBind.fromKey(Integer.parseInt(str.trim()));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @Override
    protected boolean isValueValid(KeyBind value) {
        return true;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = saveGeneral();

        tag.put("value", get().toTag());

        return tag;
    }

    @Override
    public KeyBind fromTag(NbtCompound tag) {
        get().fromTag(tag.getCompound("value"));

        return get();
    }

    public static class Builder extends SettingBuilder<Builder, KeyBind, KeyBindSetting> {
        private Runnable action;

        public Builder() {
            super(KeyBind.none());
        }

        public Builder action(Runnable action) {
            this.action = action;
            return this;
        }

        @Override
        public KeyBindSetting build() {
            return new KeyBindSetting(name, description, defaultValue, onChanged, onModuleActivated, visible, action);
        }
    }
}
