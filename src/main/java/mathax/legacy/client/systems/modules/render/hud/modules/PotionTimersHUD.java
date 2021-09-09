package mathax.legacy.client.systems.modules.render.hud.modules;

import mathax.legacy.client.systems.modules.render.hud.HUD;
import mathax.legacy.client.systems.modules.render.hud.HUDElement;
import mathax.legacy.client.systems.modules.render.hud.HUDRenderer;
import mathax.legacy.client.utils.misc.Names;
import mathax.legacy.client.utils.render.color.Color;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;

public class PotionTimersHUD extends HUDElement {
    private final Color color = new Color();

    public PotionTimersHUD(HUD hud) {
        super(hud, "potion-timers", "Displays active potion effects with timers.", true);
    }

    @Override
    public void update(HUDRenderer renderer) {
        if (isInEditor()) {
            box.setSize(renderer.textWidth("Potion Timers 0:00"), renderer.textHeight());
            return;
        }

        double width = 0;
        double height = 0;

        int i = 0;
        for (StatusEffectInstance statusEffectInstance : mc.player.getStatusEffects()) {
            width = Math.max(width, renderer.textWidth(getString(statusEffectInstance)));
            height += renderer.textHeight();

            if (i > 0) height += 2;
            i++;
        }

        box.setSize(width, height);
    }

    @Override
    public void render(HUDRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (isInEditor()) {
            renderer.text("Potion Timers 0:00", x, y, color);
            return;
        }

        int i = 0;
        for (StatusEffectInstance statusEffectInstance : mc.player.getStatusEffects()) {
            StatusEffect statusEffect = statusEffectInstance.getEffectType();

            int c = statusEffect.getColor();
            color.r = Color.toRGBAR(c);
            color.g = Color.toRGBAG(c);
            color.b = Color.toRGBAB(c);

            String text = getString(statusEffectInstance);
            renderer.text(text, x + box.alignX(renderer.textWidth(text)), y, color);

            color.r = color.g = color.b = 255;
            y += renderer.textHeight();
            if (i > 0) y += 2;
            i++;
        }
    }

    private String getString(StatusEffectInstance statusEffectInstance) {
        return String.format("%s %d (%s)", Names.get(statusEffectInstance.getEffectType()), statusEffectInstance.getAmplifier() + 1, StatusEffectUtil.durationToString(statusEffectInstance, 1));
    }
}
