package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.TripleTextHUDElement;

public class DurabilityHUD extends TripleTextHUDElement {
    public DurabilityHUD(HUD hud) {
        super(hud, "durability", "Displays durability of the item you are holding", true);
    }

    @Override
    protected String getLeft() {
        return "Durability: ";
    }

    @Override
    protected String getRight() {
        if (isInEditor()) return "69";

        if (!mc.player.getMainHandStack().isEmpty() && mc.player.getMainHandStack().isDamageable()) {
            return String.valueOf(mc.player.getMainHandStack().getMaxDamage() - mc.player.getMainHandStack().getDamage());
        }

        return "Infinite";
    }

    @Override
    public String getEnd() {
        return "";
    }
}
