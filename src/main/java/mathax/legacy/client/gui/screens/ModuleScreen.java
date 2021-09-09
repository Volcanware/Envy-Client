package mathax.legacy.client.gui.screens;

import mathax.legacy.client.events.mathax.ModuleBindChangedEvent;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.WindowScreen;
import mathax.legacy.client.gui.utils.Cell;
import mathax.legacy.client.gui.widgets.WKeybind;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.gui.widgets.containers.WContainer;
import mathax.legacy.client.gui.widgets.containers.WHorizontalList;
import mathax.legacy.client.gui.widgets.containers.WSection;
import mathax.legacy.client.gui.widgets.pressable.WCheckbox;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.client.MinecraftClient;

import static org.lwjgl.glfw.GLFW.*;

public class ModuleScreen extends WindowScreen {
    public final Module module;

    private WKeybind keybind;

    public ModuleScreen(GuiTheme theme, Module module) {
        super(theme, module.title);

        this.module = module;
    }

    @Override
    public void initWidgets() {
        // Description
        add(theme.label(module.description, Utils.getWindowWidth() / 2.0));

        // Settings
        if (module.settings.groups.size() > 0) {
            add(theme.settings(module.settings)).expandX();
        }

        // Custom widget
        WWidget widget = module.getWidget(theme);

        if (widget != null) {
            add(theme.horizontalSeparator()).expandX();
            Cell<WWidget> cell = add(widget);
            if (widget instanceof WContainer) cell.expandX();
        }

        // Bind
        WSection section = add(theme.section("Bind", true)).expandX().widget();
        keybind = section.add(theme.keybind(module.keybind)).expandX().widget();
        keybind.actionOnSet = () -> Modules.get().setModuleToBind(module);

        // Toggle on bind release
        WHorizontalList tobr = section.add(theme.horizontalList()).widget();

        tobr.add(theme.label("Toggle on bind release: "));
        WCheckbox tobrC = tobr.add(theme.checkbox(module.toggleOnBindRelease)).widget();
        tobrC.action = () -> module.toggleOnBindRelease = tobrC.checked;

        // Module
        add(theme.section("Module", true)).expandX().widget();

        // Bottom
        WHorizontalList bottom = add(theme.horizontalList()).expandX().widget();

        //   Active
        bottom.add(theme.label("Active: "));
        WCheckbox active = bottom.add(theme.checkbox(module.isActive())).expandCellX().widget();
        active.action = () -> {
            if (module.isActive() != active.checked) module.toggle(Utils.canUpdate());
        };

        // Bottom 2
        WHorizontalList bottom2 = add(theme.horizontalList()).expandX().widget();

        // Messages
        bottom2.add(theme.label("Toggle message: "));
        WCheckbox messageToggle = bottom2.add(theme.checkbox(module.isMessageEnabled())).widget();
        messageToggle.action = () -> {
            if (module.isMessageEnabled() != messageToggle.checked) module.toggleMessage(messageToggle.checked);
        };

        // Bottom 3
        WHorizontalList bottom3 = add(theme.horizontalList()).expandX().widget();

        // Toasts
        bottom3.add(theme.label("Toggle toast: "));
        WCheckbox toastToggle = bottom3.add(theme.checkbox(module.isToastEnabled())).widget();
        toastToggle.action = () -> {
            if (module.isToastEnabled() != toastToggle.checked) module.toggleToast(toastToggle.checked);
        };

        // Bottom 4
        WHorizontalList bottom4 = add(theme.horizontalList()).expandX().widget();

        //   Visible
        bottom4.add(theme.label("Visible: "));
        WCheckbox visible = bottom4.add(theme.checkbox(module.isVisible())).widget();
        visible.action = () -> {
            if (module.isVisible() != visible.checked) module.setVisible(visible.checked);
        };
    }

    @Override
    public void tick() {
        super.tick();

        module.settings.tick(window, theme);
    }

    @EventHandler
    private void onModuleBindChanged(ModuleBindChangedEvent event) {
        keybind.reset();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;

        boolean control = MinecraftClient.IS_SYSTEM_MAC ? modifiers == GLFW_MOD_SUPER : modifiers == GLFW_MOD_CONTROL;

        if (control && keyCode == GLFW_KEY_C) {
            module.toClipboard();
            return true;
        }
        else if (control && keyCode == GLFW_KEY_V) {
            module.fromClipboard();
            reload();
            return true;
        }

        return false;
    }
}
