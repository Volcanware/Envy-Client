package envy.client.systems.hud.modules;

import envy.client.mixin.ClientPlayerInteractionManagerAccessor;
import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.hud.DoubleTextHudElement;
import envy.client.systems.hud.HUD;

public class BreakingBlockHud extends DoubleTextHudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> hide = sgGeneral.add(new BoolSetting.Builder()
        .name("hide")
        .description("Hide while not breaking any block.")
        .defaultValue(true)
        .build()
    );

    public BreakingBlockHud(HUD hud) {
        super(hud, "breaking-block", "Displays percentage of the block you are breaking.", true);
    }

    @Override
    protected String getLeft() {
        return "Breaking Block: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) {
            visible = true;
            return "0%";
        }

        float breakingProgress = ((ClientPlayerInteractionManagerAccessor) mc.interactionManager).getBreakingProgress();
        if (hide.get()) visible = breakingProgress > 0;
        return String.format("%.0f%%", breakingProgress * 100);
    }
}
