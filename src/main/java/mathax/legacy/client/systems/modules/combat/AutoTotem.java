package mathax.legacy.client.systems.modules.combat;

import mathax.legacy.client.events.game.GameLeftEvent;
import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.mixininterface.IExplosion;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.player.FindItemResult;
import mathax.legacy.client.utils.player.InvUtils;
import mathax.legacy.client.utils.player.PlayerUtils;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.bus.EventPriority;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.explosion.Explosion;

import java.util.concurrent.atomic.AtomicBoolean;

/*/                                                                                                                               /*/
/*/ Enhanced mode by l1tecorejz                                                                                                   /*/
/*/ https://github.com/l1tecorejz/Perfect-Auto-Totem/blob/main/src/main/java/meteordevelopment/addons/L1tE/modules/AutoTotem.java /*/
/*/                                                                                                                               /*/

public class AutoTotem extends Module {
    private final AtomicBoolean should_wait_next_tick = new AtomicBoolean(false);
    private static final double damage = (float)((int)((1 + 1) / 2.0D * 7.0D * 12.0D + 1.0D));
    private static final Explosion iexplosion = new Explosion(null, null, 0, 0, 0, 6.0F, false, Explosion.DestructionType.DESTROY);
    private boolean should_override_totem, should_click_blank;
    private int selected_slot = 0;
    public boolean locked;
    private int totems, ticks;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Determines when to hold a totem, strict will always hold.")
        .defaultValue(Mode.Enhanced)
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

    public final Setting<Integer> health = sgGeneral.add(new IntSetting.Builder()
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

    public final Setting<Boolean> explosion = sgGeneral.add(new BoolSetting.Builder()
        .name("explosion")
        .description("Will hold a totem when explosion damage could kill you.")
        .defaultValue(true)
        .visible(() -> mode.get() == Mode.Smart)
        .build()
    );

    public final Setting<Boolean> fall = sgGeneral.add(new BoolSetting.Builder()
        .name("fall")
        .description("Will hold a totem when fall damage could kill you.")
        .defaultValue(true)
        .visible(() -> mode.get() == Mode.Smart)
        .build()
    );

    public final Setting<Versions> version = sgGeneral.add(new EnumSetting.Builder<Versions>()
        .name("minecraft-server-version")
        .defaultValue(Versions.mc_1_17)
        .visible(() -> mode.get() == Mode.Enhanced)
        .build()
    );

    private final Setting<Boolean> close_screen = sgGeneral.add(new BoolSetting.Builder()
        .name("close-screen")
        .description("Closes any screen handler while putting totem in offhand.")
        .defaultValue(false)
        .visible(() -> mode.get() == Mode.Enhanced)
        .build()
    );

    public AutoTotem() {
        super(Categories.Combat, Items.TOTEM_OF_UNDYING, "auto-totem", "Automatically equips a totem in your offhand.");
    }

    @Override
    public void onActivate() {
        should_override_totem = true;
        selected_slot = mc.player.getInventory().selectedSlot;
    }

    @EventHandler
    private void onDisconnect(GameLeftEvent event) {
        switch (mode.get()) {
            case Strict, Smart: return;
            case Enhanced:
                int totem_id = getTotemId();
                if (totem_id == -1) return;

                if (version.get() == Versions.mc_1_12) {
                    return;
                }

                InvUtils.swap(totem_id, selected_slot);

                totem_id = getTotemId();
                if (totem_id == -1) return;

                InvUtils.swap(totem_id, 40);
        }
    }

    @EventHandler
    private void onPacketSent(PacketEvent.Sent event) {
        switch (mode.get()) {
            case Strict, Smart: return;
            case Enhanced:
                if (event.packet instanceof ClickSlotC2SPacket) {
                    should_wait_next_tick.set(true);
                    return;
                }

                if (event.packet instanceof UpdateSelectedSlotC2SPacket packet) {
                    selected_slot = packet.getSelectedSlot();
                }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTick(TickEvent.Pre event) {
        switch (mode.get()) {
            case Strict, Smart:
            FindItemResult result = InvUtils.find(Items.TOTEM_OF_UNDYING);
            totems = result.getCount();

            if (totems <= 0) locked = false;
            else if (ticks >= delay.get()) {
                boolean low = mc.player.getHealth() + mc.player.getAbsorptionAmount() - PlayerUtils.possibleHealthReductions(explosion.get(), fall.get()) <= health.get();
                boolean elytras = elytra.get() && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && mc.player.isFallFlying();

                locked = mode.get() == Mode.Strict || (mode.get() == Mode.Smart && (low || elytras));

                if (locked && mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
                    InvUtils.move().from(result.getSlot()).toOffhand();
                }

                ticks = 0;
                return;
            }

            ticks++;
            case Enhanced:
                if (mc.player.currentScreenHandler instanceof CreativeInventoryScreen.CreativeScreenHandler) return;

                if (should_wait_next_tick.getAndSet(false)) return;

                ItemStack
                    offhand_stack = mc.player.getInventory().getStack(40),
                    cursor_stack = mc.player.currentScreenHandler.getCursorStack();

                final boolean
                    is_holding_totem = cursor_stack.getItem() == Items.TOTEM_OF_UNDYING,
                    is_totem_in_offhand = offhand_stack.getItem() == Items.TOTEM_OF_UNDYING;
                boolean can_click_offhand = mc.player.currentScreenHandler instanceof PlayerScreenHandler;

                if (is_totem_in_offhand && !shouldOverrideTotem()) {
                    if (!(mc.currentScreen instanceof HandledScreen) &&
                        (should_click_blank || (version.get() != Versions.mc_1_12 && is_holding_totem))) {
                        should_click_blank = false;

                        for (Slot slot : mc.player.currentScreenHandler.slots) {
                            if (!slot.getStack().isEmpty()) continue;
                            InvUtils.clickId(slot.id);
                            return;
                        }
                    }

                    return;
                }

                final int totem_id = getTotemId();
                if (totem_id == -1 && !is_holding_totem) return;

                if (!can_click_offhand && close_screen.get() && mc.player.getInventory().count(Items.TOTEM_OF_UNDYING) < 1) {
                    mc.player.closeHandledScreen();
                    can_click_offhand = true;
                }

                if (is_holding_totem && can_click_offhand) {
                    InvUtils.clickId(45);
                    return;
                }

                if (version.get() == Versions.mc_1_12 && !can_click_offhand) {
                    ItemStack mainhand_stack = mc.player.getInventory().getStack(selected_slot);
                    if (mainhand_stack.getItem() == Items.TOTEM_OF_UNDYING) {
                        mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket
                            (PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                        return;
                    }

                    if (is_holding_totem) {
                        InvUtils.clickId(InvUtils.getFirstHotbarSlotId() + selected_slot);
                        return;
                    }
                }

                if (totem_id == -1) {
                    if (is_holding_totem) {
                        for (Slot slot : mc.player.currentScreenHandler.slots) {
                            if (!slot.getStack().isEmpty()) continue;
                            InvUtils.clickId(slot.id);
                            return;
                        }

                        InvUtils.clickId(InvUtils.getFirstHotbarSlotId() + selected_slot);
                    }
                    return;
                }

                if (version.get() == Versions.mc_1_12) {
                    InvUtils.clickId(totem_id);
                    should_click_blank = true;
                    return;
                }

                InvUtils.swap(totem_id, 40);

                should_override_totem = !is_totem_in_offhand;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onReceivePacket(PacketEvent.Receive event) {
        switch (mode.get()) {
            case Strict, Smart:
                if (!(event.packet instanceof EntityStatusS2CPacket p)) return;
                if (p.getStatus() != 35) return;

                Entity entity = p.getEntity(mc.world);
                if (entity == null || !(entity.equals(mc.player))) return;

                ticks = 0;
            case Enhanced:
                if (event.packet instanceof EntityStatusS2CPacket packet) {
                    if (mc.player.currentScreenHandler instanceof PlayerScreenHandler) return;
                    if (packet.getStatus() != 35 || packet.getEntity(mc.world) != mc.player) return;

                    if (mc.player.getMainHandStack().getItem() != Items.TOTEM_OF_UNDYING && // inaccurate
                        mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING)
                        mc.player.getOffHandStack().decrement(1);
                }
                else if (event.packet instanceof UpdateSelectedSlotS2CPacket packet) {
                    selected_slot = packet.getSlot();
                }
                else if (event.packet instanceof OpenScreenS2CPacket || event.packet instanceof CloseScreenS2CPacket) {
                    should_override_totem = true;
                }
        }
    }

    public boolean isLocked() {
        return isActive() && locked;
    }

    private int getTotemId() {
        final int hotbar_start = InvUtils.getFirstHotbarSlotId();
        for (int i = hotbar_start; i < hotbar_start + 9; ++i) {
            if (mc.player.currentScreenHandler.getSlot(i).getStack().getItem() != Items.TOTEM_OF_UNDYING) continue;
            return i;
        }

        for (int i = 0; i < hotbar_start; ++i) {
            if (mc.player.currentScreenHandler.getSlot(i).getStack().getItem() != Items.TOTEM_OF_UNDYING) continue;
            return i;
        }

        return -1;
    }

    private boolean shouldOverrideTotem() {
        return should_override_totem && (version.get() == Versions.mc_1_16 ||
            (!(mc.player.currentScreenHandler instanceof PlayerScreenHandler) &&
                version.get() == Versions.mc_1_17));
    }

    private boolean smartCheck() {
        if (mc.player.isFallFlying()) return false;
        if (getLatency() >= 125) return false;

        float health = getHealth();
        if (health < 10.0F) return false;

        if (mc.player.fallDistance > 3.f && health - mc.player.fallDistance * 0.5 <= 2.0F) return false;

        double resistance_coefficient = 1.d;
        if (mc.player.hasStatusEffect(StatusEffects.RESISTANCE)) {
            resistance_coefficient -= (mc.player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 0.2;
            if (resistance_coefficient <= 0.d) return true;
        }

        double damage2 = damage;

        switch (mc.world.getDifficulty()) {
            case EASY -> damage2 = damage2 * 0.5d + 1.0d;
            case HARD -> damage2 *= 1.5d;
        }

        damage2 *= resistance_coefficient;

        EntityAttributeInstance attribute_instance =
            mc.player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);

        float f = 2.0F + (float) attribute_instance.getValue() / 4.0F;
        float g = (float) MathHelper.clamp((float) mc.player.getArmor() - damage2 / f,
            (float) mc.player.getArmor() * 0.2F, 20.0F);
        damage2 *= 1 - g / 25.0F;

        // Reduce by enchants
        ((IExplosion) iexplosion).set(mc.player.getPos(), 6.0F, false);

        int protLevel =
            EnchantmentHelper.getProtectionAmount(mc.player.getArmorItems(), DamageSource.explosion(iexplosion));
        if (protLevel > 20) protLevel = 20;

        damage2 *= 1 - (protLevel / 25.0);

        return health - damage2 > 2.0F;
    }

    private float getHealth() {
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    private long getLatency() {
        PlayerListEntry playerListEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getUuid());
        return playerListEntry != null ? playerListEntry.getLatency() : 0L;
    }

    @Override
    public String getInfoString() {
        return String.valueOf(totems);
    }

    public enum Mode {
        Strict,
        Enhanced,
        Smart
    }

    public enum Versions {
        mc_1_12,
        mc_1_16,
        mc_1_17
    }
}
