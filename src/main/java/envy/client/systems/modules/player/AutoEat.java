package envy.client.systems.modules.player;

import baritone.api.BaritoneAPI;
import envy.client.eventbus.EventHandler;
import envy.client.eventbus.EventPriority;
import envy.client.events.entity.player.ItemUseCrosshairTargetEvent;
import envy.client.events.world.TickEvent;
import envy.client.settings.*;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.systems.modules.Modules;
import envy.client.systems.modules.combat.AnchorAura;
import envy.client.systems.modules.combat.BedAura;
import envy.client.systems.modules.combat.CrystalAura;
import envy.client.systems.modules.combat.KillAura;
import envy.client.utils.Utils;
import envy.client.utils.player.InvUtils;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class AutoEat extends Module {
    public boolean eating;
    private int slot, prevSlot;

    private final List<Class<? extends Module>> wasAura = new ArrayList<>();
    private boolean wasBaritone;

    private static final Class<? extends Module>[] AURAS = new Class[] { KillAura.class, CrystalAura.class, AnchorAura.class, BedAura.class };

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgHunger = settings.createGroup("Hunger");

    // General

    private final Setting<List<Item>> blacklist = sgGeneral.add(new ItemListSetting.Builder()
        .name("blacklist")
        .description("Which items to not eat.")
        .defaultValue(
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.GOLDEN_APPLE,
            Items.CHORUS_FRUIT,
            Items.POISONOUS_POTATO,
            Items.PUFFERFISH,
            Items.CHICKEN,
            Items.ROTTEN_FLESH,
            Items.SPIDER_EYE,
            Items.SUSPICIOUS_STEW
        )
        .filter(Item::isFood)
        .build()
    );

    private final Setting<Boolean> pauseAuras = sgGeneral.add(new BoolSetting.Builder()
        .name("pause-auras")
        .description("Pauses all auras when eating.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> pauseBaritone = sgGeneral.add(new BoolSetting.Builder()
        .name("pause-baritone")
        .description("Pause baritone when eating.")
        .defaultValue(true)
        .build()
    );

    // Hunger

    private final Setting<Integer> hungerThreshold = sgHunger.add(new IntSetting.Builder()
        .name("hunger-threshold")
        .description("The level of hunger you eat at.")
        .defaultValue(16)
        .range(1, 19)
        .sliderRange(1, 19)
        .build()
    );

    private final Setting<Boolean> avoidOvereating = sgHunger.add(new BoolSetting.Builder()
        .name("avoid-overeating")
        .description("Avoid eating foods that would bring your hunger over full.")
        .defaultValue(false)
        .build()
    );

    public AutoEat() {
        super(Categories.Player, Items.APPLE, "auto-eat", "Automatically eats food.");
    }

    @Override
    public void onDeactivate() {
        if (eating) stopEating();
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onTick(TickEvent.Pre event) {
        if (Modules.get().get(AutoGap.class).isEating()) return;

        if (eating) {
            if (shouldEat()) {
                if (!mc.player.getInventory().getStack(slot).isFood()) {
                    int slot = findSlot();

                    if (slot == -1) {
                        stopEating();
                        return;
                    } else changeSlot(slot);
                }

                eat();
            } else stopEating();
        } else {
            if (shouldEat()) {
                slot = findSlot();

                if (slot != -1) startEating();
            }
        }
    }

    @EventHandler
    private void onItemUseCrosshairTarget(ItemUseCrosshairTargetEvent event) {
        if (eating) event.target = null;
    }

    private void startEating() {
        prevSlot = mc.player.getInventory().selectedSlot;
        eat();

        wasAura.clear();
        if (pauseAuras.get()) {
            for (Class<? extends Module> klass : AURAS) {
                Module module = Modules.get().get(klass);

                if (module.isActive()) {
                    wasAura.add(klass);
                    module.toggle();
                }
            }
        }

        wasBaritone = false;
        if (pauseBaritone.get() && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            wasBaritone = true;
            BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("pause");
        }
    }

    private void eat() {
        changeSlot(slot);
        setPressed(true);
        if (!mc.player.isUsingItem()) Utils.rightClick();

        eating = true;
    }

    private void stopEating() {
        changeSlot(prevSlot);
        setPressed(false);

        eating = false;

        if (pauseAuras.get()) {
            for (Class<? extends Module> klass : AURAS) {
                Module module = Modules.get().get(klass);

                if (wasAura.contains(klass) && !module.isActive()) module.toggle();
            }
        }

        if (pauseBaritone.get() && wasBaritone) BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("resume");
    }

    private void setPressed(boolean pressed) {
        mc.options.useKey.setPressed(pressed);
    }

    private void changeSlot(int slot) {
        InvUtils.swap(slot, false);
        this.slot = slot;
    }

    private boolean shouldEat() {
        return mc.player.getHungerManager().getFoodLevel() <= hungerThreshold.get();
    }

    private int findSlot() {
        int slot = -1;
        int bestHunger = -1;
        int foodLevel = mc.player.getHungerManager().getFoodLevel();

        for (int i = 0; i < 9; i++) {
            Item item = mc.player.getInventory().getStack(i).getItem();
            if (!item.isFood()) continue;
            if (blacklist.get().contains(item)) continue;
            if (avoidOvereating.get() && item.getFoodComponent().getHunger() + foodLevel > 20) continue;

            int hunger = item.getFoodComponent().getHunger();
            if (hunger > bestHunger) {
                slot = i;
                bestHunger = hunger;
            }
        }

        return slot;
    }
}
