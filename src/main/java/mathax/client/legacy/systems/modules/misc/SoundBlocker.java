package mathax.client.legacy.systems.modules.misc;

import mathax.client.legacy.events.world.PlaySoundEvent;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.settings.SoundEventListSetting;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.bus.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class SoundBlocker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<SoundEvent>> sounds = sgGeneral.add(new SoundEventListSetting.Builder()
        .name("sounds")
        .description("Sounds to block.")
        .defaultValue(new ArrayList<>(0))
        .build()
    );

    public SoundBlocker() {
        super(Categories.Misc, Items.COMMAND_BLOCK, "sound-blocker", "Cancels out selected sounds.");
    }

    @EventHandler
    private void onPlaySound(PlaySoundEvent event) {
        for (SoundEvent sound : sounds.get()) {
            if (sound.getId().equals(event.sound.getId())) {
                event.cancel();
                break;
            }
        }
    }
}
