package mathax.client.legacy.gui.screens;

import mathax.client.legacy.events.mathax.ModuleBindChangedEvent;
import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WindowScreen;
import mathax.client.legacy.gui.utils.Cell;
import mathax.client.legacy.gui.widgets.WKeybind;
import mathax.client.legacy.gui.widgets.WWidget;
import mathax.client.legacy.gui.widgets.containers.WContainer;
import mathax.client.legacy.gui.widgets.containers.WHorizontalList;
import mathax.client.legacy.gui.widgets.containers.WSection;
import mathax.client.legacy.gui.widgets.pressable.WCheckbox;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.bus.EventHandler;
import net.minecraft.client.MinecraftClient;

import static mathax.client.legacy.utils.Utils.getWindowWidth;
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
        add(theme.label(module.description, getWindowWidth() / 2.0));

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

        //WHorizontalList bottom2 = add(theme.horizontalList()).expandX().widget();

        // Messages
        bottom.add(theme.label("Toggle message: "));
        WCheckbox messageToggle = bottom.add(theme.checkbox(module.isMessageEnabled())).widget();
        messageToggle.action = () -> {
            if (module.isMessageEnabled() != messageToggle.checked) module.toggleMessage(messageToggle.checked);
        };

        WHorizontalList bottom2 = add(theme.horizontalList()).expandX().widget();

        // Toasts
        bottom2.add(theme.label("Toggle toast: "));
        WCheckbox toastToggle = bottom2.add(theme.checkbox(module.isToastEnabled())).widget();
        toastToggle.action = () -> {
            if (module.isToastEnabled() != toastToggle.checked) module.toggleToast(toastToggle.checked);
        };

        //WHorizontalList bottom4 = add(theme.horizontalList()).expandX().widget();

        //   Visible
        bottom2.add(theme.label("Visible: "));
        WCheckbox visible = bottom2.add(theme.checkbox(module.isVisible())).widget();
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
