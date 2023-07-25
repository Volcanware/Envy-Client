package envy.client.systems.modules.chat;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.settings.DoubleSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.player.ArmorUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArmorNotifier extends Module {
    private boolean alertedHelmet;
    private boolean alertedChestplate;
    private boolean alertedLeggings;
    private boolean alertedBoots;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Double> threshold = sgGeneral.add(new DoubleSetting.Builder()
        .name("durability")
        .description("How low an armor piece needs to be to alert you.")
        .defaultValue(15)
        .range(1, 100)
        .sliderRange(1, 100)
        .build()
    );

    // TODO: Notify modes & other players

    public ArmorNotifier() {
        super(Categories.Chat, Items.DIAMOND_CHESTPLATE, "armor-notifier", "Notifies you when your armor is low.");
    }

    @Override
    public boolean onActivate() {
        alertedHelmet = false;
        alertedChestplate = false;
        alertedLeggings = false;
        alertedBoots = false;
        return false;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Iterable<ItemStack> armorPieces = mc.player.getArmorItems();
        for (ItemStack armorPiece : armorPieces){
            if (ArmorUtils.checkThreshold(armorPiece, threshold.get())) {
                if (ArmorUtils.isHelmet(armorPiece) && !alertedHelmet) {
                    warning("Your (highlight)helmet(default) has low durability!");
                    alertedHelmet = true;
                }

                if (ArmorUtils.isChestplate(armorPiece) && !alertedChestplate) {
                    warning("Your (highlight)chestplate(default) has low durability!");
                    alertedChestplate = true;
                }

                if (ArmorUtils.areLeggings(armorPiece) && !alertedLeggings) {
                    warning("Your (highlight)leggings(default) have low durability!");
                    alertedLeggings = true;
                }

                if (ArmorUtils.areBoots(armorPiece) && !alertedBoots) {
                    warning("Your (highlight)boots(default) have low durability!");
                    alertedBoots = true;
                }
            }

            if (!ArmorUtils.checkThreshold(armorPiece, threshold.get())) {
                if (ArmorUtils.isHelmet(armorPiece) && alertedHelmet) alertedHelmet = false;
                if (ArmorUtils.isChestplate(armorPiece) && alertedChestplate) alertedChestplate = false;
                if (ArmorUtils.areLeggings(armorPiece) && alertedLeggings) alertedLeggings = false;
                if (ArmorUtils.areBoots(armorPiece) && alertedBoots) alertedBoots = false;
            }
        }
    }
}
