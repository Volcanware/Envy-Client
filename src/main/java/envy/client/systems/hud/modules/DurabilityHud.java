package envy.client.systems.hud.modules;

import envy.client.systems.hud.DoubleTextHudElement;
import envy.client.systems.hud.HUD;

public class DurabilityHud extends DoubleTextHudElement {
    public DurabilityHud(HUD hud) {
        super(hud, "durability", "Displays durability of the item you are holding.", true);
    }

    @Override
    protected String getLeft() {
        return "Durability: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return "69";

        if (!mc.player.getMainHandStack().isEmpty() && mc.player.getMainHandStack().isDamageable()) return String.valueOf(mc.player.getMainHandStack().getMaxDamage() - mc.player.getMainHandStack().getDamage());

        return "Infinite";
    }
}
