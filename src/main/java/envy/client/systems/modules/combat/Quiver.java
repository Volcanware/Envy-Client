package envy.client.systems.modules.combat;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.settings.StatusEffectListSetting;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.player.FindItemResult;
import envy.client.utils.player.InvUtils;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.potion.PotionUtil;

import java.util.ArrayList;
import java.util.List;

public class Quiver extends Module {
    private final List<Integer> arrowSlots = new ArrayList<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<List<StatusEffect>> effects = sgGeneral.add(new StatusEffectListSetting.Builder()
        .name("effects")
        .description("Which effects to shoot you with.")
        .defaultValue(StatusEffects.STRENGTH)
        .build()
    );

    private final Setting<Boolean> checkEffects = sgGeneral.add(new BoolSetting.Builder()
        .name("check-existing-effects")
        .description("Won't shoot you with effects you already have.")
        .defaultValue(true)
        .build()
    );

    public Quiver() {
        super(Categories.Combat, Items.BOW, "quiver", "Shoots arrows at yourself.");
    }

    @Override
    public boolean onActivate() {

        FindItemResult bow = InvUtils.findInHotbar(Items.BOW);

        if (!bow.isHotbar()) {
            error("No bow found, disabling...");
            toggle();
        }

        mc.options.useKey.setPressed(false);
        mc.interactionManager.stopUsingItem(mc.player);

        InvUtils.swap(bow.slot(), true);

        arrowSlots.clear();

        List<StatusEffect> usedEffects = new ArrayList<>();

        for (int i = mc.player.getInventory().size(); i > 0; i--) {
            if (i == mc.player.getInventory().selectedSlot) continue;

            ItemStack item = mc.player.getInventory().getStack(i);

            if (item.getItem() != Items.TIPPED_ARROW)  continue;

            List<StatusEffectInstance> effects = PotionUtil.getPotionEffects(item);

            if (effects.isEmpty()) continue;

            StatusEffect effect = effects.get(0).getEffectType();

            if (this.effects.get().contains(effect)
                && !usedEffects.contains(effect)
                && (!hasEffect(effect) || !checkEffects.get())) {
                usedEffects.add(effect);
                arrowSlots.add(i);
            }
        }
        return false;
    }

    private boolean hasEffect(StatusEffect effect) {
        for (StatusEffectInstance statusEffect : mc.player.getStatusEffects()) {
            if (statusEffect.getEffectType() == effect) return true;
        }

        return false;
    }

    @Override
    public void onDeactivate() {
        InvUtils.swapBack();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (arrowSlots.isEmpty() || !InvUtils.findInHotbar(Items.BOW).isMainHand()) {
            toggle();
            return;
        }

        boolean charging = mc.options.useKey.isPressed();

        if (!charging) {
            InvUtils.move().from(arrowSlots.get(0)).to(9);
            mc.options.useKey.setPressed(true);
        } else {
            if (BowItem.getPullProgress(mc.player.getItemUseTime()) >= 0.12) {
                int targetSlot = arrowSlots.get(0);
                arrowSlots.remove(0);

                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(mc.player.getYaw(), -90, mc.player.isOnGround()));
                mc.options.useKey.setPressed(false);
                mc.interactionManager.stopUsingItem(mc.player);
                if (targetSlot != 9) InvUtils.move().from(9).to(targetSlot);
            }
        }
    }
}
