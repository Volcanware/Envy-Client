package mathax.legacy.client.systems.modules.world;

import mathax.legacy.client.events.game.OpenScreenEvent;
import mathax.legacy.client.mixin.SignEditScreenAccessor;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.settings.StringSetting;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.utils.placeholders.Placeholders;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AutoSign extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

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
