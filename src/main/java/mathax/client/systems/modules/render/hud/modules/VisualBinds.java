package mathax.client.systems.modules.render.hud.modules;

import mathax.client.settings.EnumSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.utils.Utils;
import mathax.client.systems.modules.render.hud.HUD;
import mathax.client.systems.modules.render.hud.HudElement;
import mathax.client.systems.modules.render.hud.HudRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VisualBinds extends HudElement {
    private final List<Module> boundModules = new ArrayList<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Separator> separator = sgGeneral.add(new EnumSetting.Builder<Separator>()
        .name("sort-mode")
        .description("Determines what symbol to put between the module and the keybind.")
        .defaultValue(Separator.Arrow)
        .build()
    );

    public VisualBinds(HUD hud) {
        super(hud, "visual-binds", "Display keybound modules and their bind.", false);
    }

    @Override
    public void update(HudRenderer renderer) {
        updateBinds();

        double width = 0;
        double height = 0;

        if (Modules.get() == null) {
            String t = "KeyBinds";
            width = Math.max(width, renderer.textWidth(t));
            height += renderer.textHeight();
            box.setSize(width, height);
            return;
        }

        int i = 0;

        if (boundModules.isEmpty()) {
            String t = "You have no keybound modules";
            width = Math.max(width, renderer.textWidth(t));
            height += renderer.textHeight();
        } else {
            for (Module boundModule : boundModules) {
                String length = boundModule.title + getSeparator() + Utils.getKeyName(boundModule.keybind.getValue());
                width = Math.max(width, renderer.textWidth(length));
                height += renderer.textHeight();
                if (i > 0) height += 2;
                i++;
            }
        }

        box.setSize(width, height);
    }

    @Override
    public void render(HudRenderer renderer) {
        updateBinds();

        double x = box.getX();
        double y = box.getY();

        if (Modules.get() == null) {
            renderer.text("KeyBinds", x, y, hud.color);
            return;
        }

        int i = 0;

        if (boundModules.isEmpty()) {
            String t = "You have no keybound modules";
            renderer.text(t, x + box.alignX(renderer.textWidth(t)), y, hud.primaryColor.get());
        } else {
            for (Module boundModule: boundModules) {
                String separator = getSeparator();
                String length = boundModule.title + separator + Utils.getKeyName(boundModule.keybind.getValue());

                renderer.text(boundModule.title, x + box.alignX(renderer.textWidth(length)), y, hud.primaryColor.get());
                renderer.text(separator, x + renderer.textWidth(boundModule.title) + box.alignX(renderer.textWidth(length)), y, hud.secondaryColor.get());
                renderer.text(Utils.getKeyName(boundModule.keybind.getValue()), x + renderer.textWidth(boundModule.title) + renderer.textWidth(separator) + box.alignX(renderer.textWidth(length)), y, hud.primaryColor.get());

                y += renderer.textHeight();
                if (i > 0) y += 2;
                i++;
            }
        }
    }

    private void updateBinds() {
        boundModules.clear();
        boundModules.addAll(Modules.get().getAll().stream().filter(module -> module.keybind.isSet()).collect(Collectors.toList()));
    }

    private String getSeparator() {
        return switch (separator.get()) {
            case Arrow -> " -> ";
            case Colon -> ": ";
        };
    }

    public enum Separator {
        Arrow,
        Colon
    }
}
