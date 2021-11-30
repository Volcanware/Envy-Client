package mathax.legacy.client.systems.modules.player;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.misc.KeyBind;
import mathax.legacy.client.utils.player.FindItemResult;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.player.Rotations;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

public class EXPThrower extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("When the module should throw exp.")
        .defaultValue(Mode.Manual)
        .build()
    );

    private final Setting<KeyBind> manualBind = sgGeneral.add(new KeyBindSetting.Builder()
        .name("keybind")
        .description("The bind to press for exp to be thrown.")
        .visible(() -> mode.get() == Mode.Manual)
        .defaultValue(KeyBind.fromKey(GLFW.GLFW_KEY_GRAVE_ACCENT))
        .build()
    );

    private final Setting<Boolean> replenish = sgGeneral.add(new BoolSetting.Builder()
        .name("replenish")
        .description("Automatically replenishes exp into a selected hotbar slot.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> slot = sgGeneral.add(new IntSetting.Builder()
        .name("exp-slot")
        .description("The slot to replenish exp into.")
        .visible(replenish::get)
        .defaultValue(6)
        .range(1, 9)
        .sliderRange(1, 9)
        .build()
    );

    private final Setting<Integer> threshold = sgGeneral.add(new IntSetting.Builder()
        .name("threshold")
        .description("The minimum durability percentage for an item to be repaired.")
        .defaultValue(30)
        .range(1, 100)
        .sliderRange(1, 100)
        .build()
    );

    private final Setting<Boolean> armor = sgGeneral.add(new BoolSetting.Builder()
        .name("armor")
        .description("Repairs all repairable armor that you are wearing.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> hands = sgGeneral.add(new BoolSetting.Builder()
        .name("hands")
        .description("Repairs all repairable items in your hands.")
        .defaultValue(true)
        .build()
    );

    public EXPThrower() {
        super(Categories.Player, Items.EXPERIENCE_BOTTLE, "exp-thrower", "Automatically throws XP bottles in your hotbar.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mode.get() == Mode.Manual && !manualBind.get().isPressed()) return;

        boolean shouldThrow = false;

        if (armor.get()) {
            for (ItemStack itemStack : mc.player.getInventory().armor) {
                if (needsRepair(itemStack)) {
                    shouldThrow = true;
                    break;
                }
            }
        }

        if (hands.get() && !shouldThrow) {
            for (Hand hand : Hand.values()) {
                if (needsRepair(mc.player.getStackInHand(hand))) {
                    shouldThrow = true;
                    break;
                }
            }
        }

        if (!shouldThrow) return;

        FindItemResult exp = InvUtils.find(Items.EXPERIENCE_BOTTLE);

        if (exp.found()) {
            if (!exp.isHotbar() && !exp.isOffhand()) {
                if (!replenish.get()) return;
                InvUtils.move().from(exp.getSlot()).toHotbar(slot.get() - 1);
            }

            Rotations.rotate(-90, mc.player.getYaw(), () -> {
                if (exp.getHand() != null) mc.interactionManager.interactItem(mc.player, mc.world, exp.getHand());
                else {
                    InvUtils.swap(exp.getSlot(), true);
                    mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                    InvUtils.swapBack();
                }
            });
        }
    }

    private boolean needsRepair(ItemStack itemStack) {
        if (itemStack.isEmpty() || EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack) < 1) return false;
        return (itemStack.getMaxDamage() - itemStack.getDamage()) / (double) itemStack.getMaxDamage() * 100 <= threshold.get();
    }

    public enum Mode {
        Automatic,
        Manual
    }
}
