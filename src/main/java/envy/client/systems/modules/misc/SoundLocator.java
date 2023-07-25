package envy.client.systems.modules.misc;

import envy.client.eventbus.EventHandler;
import envy.client.events.world.PlaySoundEvent;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.settings.SoundEventListSetting;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.misc.ChatUtils;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class SoundLocator extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<List<SoundEvent>> sounds = sgGeneral.add(new SoundEventListSetting.Builder()
        .name("sounds")
        .description("Sounds to find.")
        .build()
    );

    public SoundLocator() {
        super(Categories.Misc, Items.NOTE_BLOCK, "sound-locator", "Prints locations of sound events.");
    }

    @EventHandler
    private void onPlaySound(PlaySoundEvent event) {
        for (SoundEvent sound : sounds.get()) {
            if (sound.getId().equals(event.sound.getId())) {
                printSound(event.sound);
                break;
            }
        }
    }

    private void printSound(SoundInstance sound) {
        WeightedSoundSet soundSet = mc.getSoundManager().get(sound.getId());
        MutableText text = soundSet.getSubtitle().copy();
        text.append(String.format("%s at ", Formatting.GRAY));
        Vec3d pos = new Vec3d(sound.getX(), sound.getY(), sound.getZ());
        text.append(ChatUtils.formatCoords(pos));
        text.append(String.format("%s.", Formatting.GRAY));
        info(text);
    }
}
