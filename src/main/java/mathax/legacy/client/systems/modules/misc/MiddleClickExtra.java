package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.events.entity.player.FinishUsingItemEvent;
import mathax.legacy.client.events.entity.player.StoppedUsingItemEvent;
import mathax.legacy.client.events.mathax.MouseButtonEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.EnumSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.misc.input.KeyAction;
import mathax.legacy.client.utils.player.FindItemResult;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.eventbus.EventHandler;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickExtra extends Module {
    private boolean isUsing;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Which item to use when you middle click.")
        .defaultValue(Mode.Pearl)
        .build()
    );

    private final Setting<Boolean> notify = sgGeneral.add(new BoolSetting.Builder()
        .name("notify")
        .description("Notifies you when you do not have the specified item in your hotbar.")
        .defaultValue(true)
        .build()
    );

    public MiddleClickExtra() {
        super(Categories.Misc, Items.STONE_BUTTON, "middle-click-extra", "Lets you use items when you middle click.");
    }

    @Override
    public void onDeactivate() {
        stopIfUsing();
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action != KeyAction.Press || event.button != GLFW_MOUSE_BUTTON_MIDDLE) return;

        FindItemResult result = InvUtils.findInHotbar(mode.get().item);

        if (!result.found()) {
            if (notify.get()) warning("Unable to find specified item.");
            return;
        }

        InvUtils.swap(result.getSlot(), true);

        switch (mode.get().type) {
            case Immediate -> {
                mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                InvUtils.swapBack();
            }
            case LongerSingleClick -> mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
            case Longer -> {
                mc.options.keyUse.setPressed(true);
                isUsing = true;
            }
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (isUsing) {
            boolean pressed = true;

            if (mc.player.getMainHandStack().getItem() instanceof BowItem) {
                pressed = BowItem.getPullProgress(mc.player.getItemUseTime()) < 1;
            }

            mc.options.keyUse.setPressed(pressed);
        }
    }

    @EventHandler
    private void onFinishUsingItem(FinishUsingItemEvent event) {
        stopIfUsing();
    }

    @EventHandler
    private void onStoppedUsingItem(StoppedUsingItemEvent event) {
        stopIfUsing();
    }

    private void stopIfUsing() {
        if (isUsing) {
            mc.options.keyUse.setPressed(false);
            InvUtils.swapBack();
            isUsing = false;
        }
    }

    private enum Type {
        Immediate,
        LongerSingleClick,
        Longer
    }

    public enum Mode {
        Pearl(Items.ENDER_PEARL, Type.Immediate),
        Rocket(Items.FIREWORK_ROCKET, Type.Immediate),

        Rod(Items.FISHING_ROD, Type.LongerSingleClick),

        Bow(Items.BOW, Type.Longer),
        Gap(Items.GOLDEN_APPLE, Type.Longer),
        EGap(Items.ENCHANTED_GOLDEN_APPLE, Type.Longer),
        Chorus(Items.CHORUS_FRUIT, Type.Longer);

        private final Item item;
        private final Type type;

        Mode(Item item, Type type) {
            this.item = item;
            this.type = type;
        }
    }
}
