package envy.client.systems.modules.world;

import envy.client.eventbus.EventHandler;
import envy.client.events.game.OpenScreenEvent;
import envy.client.mixin.SignEditScreenAccessor;
import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.settings.StringSetting;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.misc.Placeholders;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AutoSign extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<String> lineone = sgGeneral.add(new StringSetting.Builder()
        .name("line-one")
        .description("Text of the first line of the sign.")
        .defaultValue("%username%")
        .build()
    );

    private final Setting<String> linetwo = sgGeneral.add(new StringSetting.Builder()
        .name("line-two")
        .description("Text of the second line of the sign.")
        .defaultValue("was here!")
        .build()
    );

    private final Setting<String> linethree = sgGeneral.add(new StringSetting.Builder()
        .name("line-three")
        .description("Text of the third line of the sign.")
        .defaultValue("Envy on top!")
        .build()
    );

    private final Setting<String> linefour = sgGeneral.add(new StringSetting.Builder()
        .name("line-four")
        .description("Text of the fourth line of the sign.")
        .defaultValue("%date%")
        .build()
    );

    public final Setting<Boolean> euDate = sgGeneral.add(new BoolSetting.Builder()
        .name("EU-date")
        .description("Changes the date in %date% to Europian format.")
        .defaultValue(false)
        .build()
    );

    public AutoSign() {
        super(Categories.World, Items.OAK_SIGN, "auto-sign", "Automatically writes signs.");
    }

    private String getDate() {
        if (euDate.get()) {
            String date = new SimpleDateFormat("dd/MM/yy").format(Calendar.getInstance().getTime());
            return date + " EU";
        } else {
            String date = new SimpleDateFormat("MM/dd/yy").format(Calendar.getInstance().getTime());
            return date + " US";
        }
    }
    //You should write the entire bee movie on signs
    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (!(event.screen instanceof SignEditScreen)) return;

        SignBlockEntity sign = ((SignEditScreenAccessor) event.screen).getSign();

        String lineOne = Placeholders.apply(lineone.get().replace("%date%", getDate()));
        String lineTwo = Placeholders.apply(linetwo.get().replace("%date%", getDate()));
        String lineThree = Placeholders.apply(linethree.get().replace("%date%", getDate()));
        String lineFour = Placeholders.apply(linefour.get().replace("%date%", getDate()));

        mc.player.networkHandler.sendPacket(new UpdateSignC2SPacket(sign.getPos(), lineOne, lineTwo, lineThree, lineFour));

        event.cancel();
    }
}
