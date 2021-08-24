package mathax.client.legacy.systems.modules.world;

import mathax.client.legacy.events.game.OpenScreenEvent;
import mathax.client.legacy.mixin.SignEditScreenAccessor;
import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.settings.StringSetting;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.utils.misc.placeholders.Placeholders;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AutoSign extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public AutoSign() {
        super(Categories.World, "auto-sign", "Automatically writes signs.");
    }

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
        .defaultValue("MatHax on top!")
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
        .description("Changes the date to Europian format.")
        .defaultValue(false)
        .build()
    );

    private String getDate() {
        if (euDate.get()) {
            String date = new SimpleDateFormat("dd/MM/yy").format(Calendar.getInstance().getTime());
            return date + " EU";
        } else {
            String date = new SimpleDateFormat("MM/dd/yy").format(Calendar.getInstance().getTime());
            return date + " US";
        }
    }

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
