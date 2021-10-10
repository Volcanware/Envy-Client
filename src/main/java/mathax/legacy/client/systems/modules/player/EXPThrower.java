package mathax.legacy.client.systems.modules.player;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.player.FindItemResult;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.player.Rotations;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class EXPThrower extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> lookDown = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Forces you to rotate downwards when throwing bottles.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> autoToggle = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-toggle")
        .description("Toggles off when your armor is repaired.")
        .defaultValue(true)
        .build()
    );

    public EXPThrower() {
        super(Categories.Player, Items.EXPERIENCE_BOTTLE, "exp-thrower", "Automatically throws XP bottles in your hotbar.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (autoToggle.get()) {
            boolean shouldThrow = false;

            for (ItemStack itemStack : mc.player.getInventory().armor) {
                // If empty
                if (itemStack.isEmpty()) continue;

                // If no mending
                if (EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack) < 1) continue;

                // If damaged
                if (itemStack.isDamaged()) {
                    shouldThrow = true;
                    break;
                }
            }

            if (!shouldThrow) {
                toggle();
                return;
            }
        }

        FindItemResult exp = InvUtils.findInHotbar(Items.EXPERIENCE_BOTTLE);

        if (exp.found()) {
            if (lookDown.get()) Rotations.rotate(mc.player.getYaw(), 90, () -> throwExp(exp));
            else throwExp(exp);
        }
    }

    private void throwExp(FindItemResult exp) {
        if (exp.isOffhand()) mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
        else {
            InvUtils.swap(exp.getSlot(), true);
            mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            InvUtils.swapBack();
        }
    }
}
