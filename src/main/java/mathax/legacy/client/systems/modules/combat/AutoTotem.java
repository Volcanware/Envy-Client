package mathax.legacy.client.systems.modules.combat;

import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.player.EnhancedInvUtils;
import mathax.legacy.client.utils.player.FindItemResult;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.player.PlayerUtils;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.bus.EventPriority;
import mathax.legacy.client.settings.*;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem extends Module {
    static final boolean assertionsDisabled = !AutoTotem.class.desiredAssertionStatus();
    public int delayTake;
    public int totemCount;
    public int delayPlace;
    public int totemSlot;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines when to hold a totem, strict will always hold.")
        .defaultValue(Mode.EnhancedStrict)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The ticks between slot movements.")
        .defaultValue(0)
        .min(0)
        .sliderMax(10)
        .visible(() -> mode.get() == Mode.Strict || mode.get() == Mode.Smart)
        .build()
    );

    private final Setting<Integer> health = sgGeneral.add(new IntSetting.Builder()
        .name("health")
        .description("The health to hold a totem at.")
        .defaultValue(10)
        .min(0).max(36)
        .sliderMax(36)
        .visible(() -> mode.get() == Mode.Smart)
        .build()
    );

    private final Setting<Boolean> elytra = sgGeneral.add(new BoolSetting.Builder()
        .name("elytra")
        .description("Will always hold a totem when flying with elytra.")
        .defaultValue(true)
        .visible(() -> mode.get() == Mode.Smart)
        .build()
    );

    private final Setting<Boolean> fall = sgGeneral.add(new BoolSetting.Builder()
        .name("fall")
        .description("Will hold a totem when fall damage could kill you.")
        .defaultValue(true)
        .visible(() -> mode.get() == Mode.Smart)
        .build()
    );

    private final Setting<Boolean> explosion = sgGeneral.add(new BoolSetting.Builder()
        .name("explosion")
        .description("Will hold a totem when explosion damage could kill you.")
        .defaultValue(true)
        .visible(() -> mode.get() == Mode.Smart)
        .build()
    );

    private final Setting<Integer> takeDelay = sgGeneral.add(new IntSetting.Builder()
        .name("take-delay")
        .description("Delay between takes in ticks.")
        .defaultValue(0)
        .min(0)
        .sliderMax(20)
        .visible(() -> mode.get() == Mode.EnhancedStrict)
        .build()
    );

    private final Setting<Integer> placeDelay = sgGeneral.add(new IntSetting.Builder()
        .name("place-delay")
        .description("Delay between places in ticks.")
        .defaultValue(0)
        .min(0)
        .sliderMax(20)
        .visible(() -> mode.get() == Mode.EnhancedStrict)
        .build()
    );

    public boolean locked;
    private int totems, ticks;

    public AutoTotem() {
        super(Categories.Combat, Items.TOTEM_OF_UNDYING, "auto-totem", "Automatically equips a totem in your offhand.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTick(TickEvent.Pre event) {
        if (mode.get() == Mode.Smart || mode.get() == Mode.Strict) {
            FindItemResult result = InvUtils.find(Items.TOTEM_OF_UNDYING);
            totems = result.getCount();

            if (totems <= 0) locked = false;
            else if (ticks >= delay.get()) {
                boolean low = mc.player.getHealth() + mc.player.getAbsorptionAmount() - PlayerUtils.possibleHealthReductions(explosion.get(), fall.get()) <= health.get();
                boolean ely = elytra.get() && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && mc.player.isFallFlying();

                locked = mode.get() == Mode.Strict || (mode.get() == Mode.Smart && (low || ely));

                if (locked && mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                    InvUtils.move().from(result.getSlot()).toOffhand();
                }

                ticks = 0;
                return;
            }

            ticks++;
        }
        if (mode.get() == Mode.EnhancedStrict) {
            if (mc.player == null) {
                return;
            }
            FindTotem();
            if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
                return;
            }
            if (totemCount == 0 && InvUtils.find(Items.TOTEM_OF_UNDYING).getCount() > 0) {
                ++totemCount;
                if (mc.currentScreen instanceof InventoryScreen) {
                    return;
                }
                if (!(mc.currentScreen instanceof HandledScreen)) {
                    FullPlace();
                    return;
                }
            } else if (totemCount > 0 && (mc.currentScreen instanceof InventoryScreen || !(mc.currentScreen instanceof HandledScreen))) {
                FullPlace();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof EntityStatusS2CPacket p)) return;
        if (p.getStatus() != 35) return;

        Entity entity = p.getEntity(mc.world);
        if (entity == null || !(entity.equals(mc.player))) return;

        ticks = 0;
    }

    public boolean isLocked() {
        return isActive() && locked;
    }

    @Override
    public String getInfoString() {
        return String.valueOf(totems);
    }

    @Override
    public void onActivate() {
        delayTake = takeDelay.get();
        delayPlace = takeDelay.get();
    }

    private void FullPlace() {
        if (delayTake < takeDelay.get()) {
            ++delayTake;
            return;
        }
        EnhancedInvUtils.clickSlot(EnhancedInvUtils.invIndexToSlotId(totemSlot), 0, SlotActionType.PICKUP);
        if (delayPlace < placeDelay.get()) {
            ++delayPlace;
            return;
        }
        EnhancedInvUtils.clickSlot(EnhancedInvUtils.invIndexToSlotId(45), 0, SlotActionType.PICKUP);
        delayPlace = 0;
        delayTake = 0;
    }

    private void FindTotem() {
        totemCount = 0;
        totemSlot = -1;
        for (int i = 0; i < 44; ++i) {
            if (!assertionsDisabled && mc.player == null) {
                throw new AssertionError();
            }
            if (mc.player.getInventory().getStack(i).getItem() != Items.TOTEM_OF_UNDYING) continue;
            ++totemCount;
            totemSlot = i;
            if (-1 < 3) continue;
            return;
        }
    }

    public enum Mode {
        Strict,
        EnhancedStrict,
        Smart
    }
}
