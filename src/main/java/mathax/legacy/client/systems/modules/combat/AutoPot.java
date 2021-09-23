package mathax.legacy.client.systems.modules.combat;

import baritone.api.BaritoneAPI;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.events.entity.player.ItemUseCrosshairTargetEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.player.Rotations;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*/                                                                                                              /*/
/*/ Taken from Meteor Rejects                                                                                    /*/
/*/ https://github.com/AntiCope/meteor-rejects/blob/master/src/main/java/cloudburst/rejects/modules/AutoPot.java /*/
/*/                                                                                                              /*/

public class AutoPot extends Module {
    private final List<Class<? extends Module>> wasAura = new ArrayList<>();
    private static final Class<? extends Module>[] AURAS = new Class[] {
        KillAura.class,
        CrystalAura.class,
        AnchorAura.class,
        BedAura.class
    };
    private int slot, prevSlot;
    private boolean drinking, splashing;
    private boolean wasBaritone;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgAutomation = settings.createGroup("Automation");

    // General

    private final Setting<Boolean> healing = sgGeneral.add(new BoolSetting.Builder()
        .name("healing")
        .description("Enables healing potions.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> health = sgGeneral.add(new IntSetting.Builder()
        .name("health")
        .description("If health goes below this point, Healing Pot will trigger.")
        .defaultValue(15)
        .min(0)
        .sliderMax(20)
        .build()
    );

    private final Setting<Boolean> strength = sgGeneral.add(new BoolSetting.Builder()
        .name("strength")
        .description("Enables strength potions.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> useSplashPots = sgGeneral.add(new BoolSetting.Builder()
        .name("splash-pots")
        .description("Allow the use of splash pots")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> chatInfo = sgGeneral.add(new BoolSetting.Builder()
        .name("chat-info")
        .description("Sends information to chat.")
        .defaultValue(false)
        .build()
    );

    // Automations

    private final Setting<Boolean> pauseAuras = sgAutomation.add(new BoolSetting.Builder()
        .name("pause-auras")
        .description("Pauses all auras when eating.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> pauseBaritone = sgAutomation.add(new BoolSetting.Builder()
        .name("pause-baritone")
        .description("Pause baritone when eating.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> rotate = sgAutomation.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Forces you to rotate downwards when throwing bottles.")
        .defaultValue(true)
        .build()
    );

    public AutoPot() {
        super(Categories.Combat, Items.POTION, "auto-pot-drink", "Automatically Duses Potions");
    }

    @Override
    public void onDeactivate() {
        if (drinking) stopDrinking();
        if (splashing) stopSplashing();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (healing.get()) {
            if (ShouldDrinkHealth()) {

                //Heal Pot Slot
                int slot = HealingpotionSlot();

                //Slot Not Invalid
                if (slot != -1) {
                    startDrinking();
                } else if (HealingpotionSlot() == -1 && useSplashPots.get()) {
                    slot = HealingSplashpotionSlot();
                    if (slot != -1) {
                        startSplashing();
                    }
                }
            }

            if (drinking) {
                if (ShouldDrinkHealth()) {
                    if (isNotPotion(mc.player.getInventory().getStack(slot))) {
                        slot = HealingpotionSlot();
                        if (slot == -1) {
                            if (chatInfo.get()) info("Ran out of Pots while drinking...");
                            stopDrinking();
                            return;
                        }
                    } else changeSlot(slot);
                }
                drink();
                if (ShouldNotDrinkHealth()) {
                    if (chatInfo.get()) info("Health full!");
                    stopDrinking();
                    return;
                }
            }

            if (splashing) {
                if (ShouldDrinkHealth()) {
                    if (isNotSplashPotion(mc.player.getInventory().getStack(slot))) {
                        slot = HealingSplashpotionSlot();
                        if (slot == -1) {
                            if (chatInfo.get()) info("Ran out of Pots while splashing...");
                            stopSplashing();
                            return;
                        } else changeSlot(slot);
                    }
                    splash();
                    if (ShouldNotDrinkHealth()) {
                        if (chatInfo.get()) info("Health full!");
                        stopSplashing();
                        return;
                    }
                }
            }
        }

        if (strength.get()) {
            if (ShouldDrinkStrength()) {

                //Strength Pot Slot
                int slot = StrengthpotionSlot();

                //Slot Not Invalid
                if (slot != -1) {
                    startDrinking();
                }
                else if (StrengthpotionSlot() == -1 && useSplashPots.get()) {
                    slot = StrengthSplashpotionSlot();
                    if (slot != -1) {
                        startSplashing();
                    }
                }
            }

            if (drinking) {
                if (ShouldDrinkStrength()) {
                    if (isNotPotion(mc.player.getInventory().getStack(slot))) {
                        slot = StrengthpotionSlot();
                        if (slot == -1) {
                            stopDrinking();
                            if (chatInfo.get()) info("Out of Pots...");
                            return;
                        } else changeSlot(slot);
                    }
                    drink();
                } else {
                    stopDrinking();
                }
            }

            if (splashing) {
                if (ShouldDrinkStrength()) {
                    if (isNotSplashPotion(mc.player.getInventory().getStack(slot))) {
                        slot = StrengthSplashpotionSlot();
                        if (slot == -1) {
                            if (chatInfo.get()) info("Ran out of Pots while splashing...");
                            stopSplashing();
                            return;
                        } else changeSlot(slot);
                    }
                    splash();
                } else {
                    stopSplashing();
                }
            }
        }
    }

    @EventHandler
    private void onItemUseCrosshairTarget(ItemUseCrosshairTargetEvent event) {
        if (drinking) event.target = null;
    }

    private void setPressed(boolean pressed) {
        mc.options.keyUse.setPressed(pressed);
    }

    private void startDrinking() {
        prevSlot = mc.player.getInventory().selectedSlot;
        drink();

        // Pause auras
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

        // Pause baritone
        wasBaritone = false;
        if (pauseBaritone.get() && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            wasBaritone = true;
            BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("pause");
        }
    }

    private void startSplashing() {
        prevSlot = mc.player.getInventory().selectedSlot;
        if (rotate.get()){
            Rotations.rotate(mc.player.getYaw(), 90); splash();
        }
        splash();

        // Pause auras
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

        // Pause baritone
        wasBaritone = false;
        if (pauseBaritone.get() && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            wasBaritone = true;
            BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("pause");
        }
    }

    private void drink() {
        changeSlot(slot);
        setPressed(true);
        if (!mc.player.isUsingItem()) Utils.rightClick();

        drinking = true;
    }

    private void splash() {
        changeSlot(slot);
        setPressed(true);
        splashing = true;
    }

    private void stopDrinking() {
        changeSlot(prevSlot);
        setPressed(false);
        drinking = false;

        // Resume auras
        if (pauseAuras.get()) {
            for (Class<? extends Module> klass : AURAS) {
                Module module = Modules.get().get(klass);

                if (wasAura.contains(klass) && !module.isActive()) {
                    module.toggle();
                }
            }
        }

        // Resume baritone
        if (pauseBaritone.get() && wasBaritone) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("resume");
        }
    }

    private void stopSplashing() {
        changeSlot(prevSlot);
        setPressed(false);

        splashing = false;

        // Resume auras
        if (pauseAuras.get()) {
            for (Class<? extends Module> klass : AURAS) {
                Module module = Modules.get().get(klass);

                if (wasAura.contains(klass) && !module.isActive()) {
                    module.toggle();
                }
            }
        }

        // Resume baritone
        if (pauseBaritone.get() && wasBaritone) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("resume");
        }
    }

    private double truehealth() {
        assert mc.player != null;
        return mc.player.getHealth();
    }

    private void changeSlot(int slot) {
        mc.player.getInventory().selectedSlot = slot;
        this.slot = slot;
    }

    //Heal pot checks
    private int HealingpotionSlot() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {

            // Skip if item stack is empty
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() != Items.POTION) continue;
            if (stack.getItem() == Items.POTION) {
                List<StatusEffectInstance> effects = PotionUtil.getPotion(mc.player.getInventory().getStack(i)).getEffects();
                if (effects.size() > 0) {
                    StatusEffectInstance effect = effects.get(0);
                    if (effect.getTranslationKey().equals("effect.minecraft.instant_health")) {
                        slot = i;
                        break;
                    }
                }
            }
        }

        return slot;
    }

    private int HealingSplashpotionSlot() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {

            // Skip if item stack is empty
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() != Items.SPLASH_POTION) continue;
            if (stack.getItem() == Items.SPLASH_POTION) {
                List<StatusEffectInstance> effects = PotionUtil.getPotion(mc.player.getInventory().getStack(i)).getEffects();
                if (effects.size() > 0) {
                    StatusEffectInstance effect = effects.get(0);
                    if (effect.getTranslationKey().equals("effect.minecraft.instant_health")) {
                        slot = i;
                        break;
                    }
                }
            }
        }

        return slot;
    }

    //Strength Pot Checks
    private int StrengthSplashpotionSlot () {
        int slot = -1;
        for (int i = 0; i < 9; i++) {

            // Skip if item stack is empty
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() != Items.SPLASH_POTION) continue;
            if (stack.getItem() == Items.SPLASH_POTION) {
                List<StatusEffectInstance> effects = PotionUtil.getPotion(mc.player.getInventory().getStack(i)).getEffects();
                if (effects.size() > 0) {
                    StatusEffectInstance effect = effects.get(0);
                    if (effect.getTranslationKey().equals("effect.minecraft.strength")) {
                        slot = i;
                        break;
                    }
                }

            }
        }

        return slot;
    }

    private int StrengthpotionSlot () {
        int slot = -1;
        for (int i = 0; i < 9; i++) {

            // Skip if item stack is empty
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() != Items.POTION) continue;
            if (stack.getItem() == Items.POTION) {
                List<StatusEffectInstance> effects = PotionUtil.getPotion(mc.player.getInventory().getStack(i)).getEffects();
                if (effects.size() > 0) {
                    StatusEffectInstance effect = effects.get(0);
                    if (effect.getTranslationKey().equals("effect.minecraft.strength")) {
                        slot = i;
                        break;
                    }
                }

            }
        }

        return slot;
    }

    private boolean isNotPotion(ItemStack stack) {
        Item item = stack.getItem();
        return item != Items.POTION;
    }

    private boolean isNotSplashPotion(ItemStack stack) {
        Item item = stack.getItem();
        return item != Items.SPLASH_POTION;
    }

    private boolean ShouldDrinkHealth(){
        if (truehealth() < health.get()) return true;
        return false;
    }

    private boolean ShouldNotDrinkHealth(){
        if (truehealth() >= health.get()) return true;
        return false;
    }

    private boolean ShouldDrinkStrength(){
        Map<StatusEffect, StatusEffectInstance> effects = mc.player.getActiveStatusEffects();
        return !effects.containsKey(StatusEffects.STRENGTH);
    }
}
