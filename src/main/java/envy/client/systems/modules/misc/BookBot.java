package envy.client.systems.modules.misc;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.world.TickEvent;
import envy.client.gui.GuiTheme;
import envy.client.gui.widgets.WLabel;
import envy.client.gui.widgets.WWidget;
import envy.client.gui.widgets.containers.WHorizontalList;
import envy.client.gui.widgets.pressable.WButton;
import envy.client.mixin.TextHandlerAccessor;
import envy.client.settings.*;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.player.FindItemResult;
import envy.client.utils.player.InvUtils;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.PrimitiveIterator;
import java.util.Random;

public class BookBot extends Module {
    private File file = new File(Envy.VERSION_FOLDER, "bookbot.txt");

    private final PointerBuffer filters;

    private int delayTimer, bookCount;

    private Random random;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("What kind of text to write.")
        .defaultValue(Mode.Random)
        .build()
    );

    private final Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("name")
        .description("The name you want to give your books.")
        .defaultValue("Envy on top!")
        .build()
    );

    private final Setting<Integer> pages = sgGeneral.add(new IntSetting.Builder()
        .name("pages")
        .description("The number of pages to write per book.")
        .defaultValue(50)
        .range(1, 100)
        .sliderRange(1, 100)
        .visible(() -> mode.get() != Mode.File)
        .build()
    );

    private final Setting<Boolean> onlyAscii = sgGeneral.add(new BoolSetting.Builder()
        .name("ascii-only")
        .description("Only uses the characters in the ASCII charset.")
        .defaultValue(false)
        .visible(() -> mode.get() == Mode.Random)
        .build()
    );

    private final Setting<Boolean> count = sgGeneral.add(new BoolSetting.Builder()
        .name("append-count")
        .description("Whether to append the number of the book to the title.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The amount of delay between writing books.")
        .defaultValue(20)
        .min(1)
        .sliderRange(1, 200)
        .build()
    );

    // Buttons

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WHorizontalList list = theme.horizontalList();

        WButton selectFile = list.add(theme.button("Select File")).widget();

        WLabel fileName = list.add(theme.label((file != null && file.exists()) ? file.getName() : "No file selected.")).widget();

        selectFile.action = () -> {
            String path = TinyFileDialogs.tinyfd_openFileDialog("Select File", new File(Envy.VERSION_FOLDER, "bookbot.txt").getAbsolutePath(), filters, null, false);

            if (path != null) {
                file = new File(path);
                fileName.set(file.getName());
            }
        };

        return list;
    }

    public BookBot() {
        super(Categories.Misc, Items.WRITABLE_BOOK, "book-bot", "Automatically writes in books.");

        if (!file.exists()) file = null;

        filters = BufferUtils.createPointerBuffer(1);

        ByteBuffer txtFilter = MemoryUtil.memASCII("*.txt");

        filters.put(txtFilter);
        filters.rewind();
    }

    @Override
    public boolean onActivate() {
        if ((file == null || !file.exists()) && mode.get() == Mode.File) {
            info("No file selected, please select a file in the GUI.");
            toggle();
            return false;
        }

        random = new Random();
        delayTimer = delay.get();
        bookCount = 0;
        return false;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        FindItemResult writableBook = InvUtils.find(Items.WRITABLE_BOOK);

        // Check if there is a book to write
        if (!writableBook.found()) {
            toggle();
            return;
        }

        // Move the book into hand
        if (!writableBook.isMainHand()) {
            InvUtils.move().from(writableBook.slot()).toHotbar(mc.player.getInventory().selectedSlot);
            return;
        }

        // If somehow it failed, just dont do anything until it tries again
        FindItemResult finalBook = InvUtils.findInHotbar(Items.WRITABLE_BOOK);
        if (!finalBook.isMainHand()) return;

        // Check delay
        if (delayTimer > 0) {
            delayTimer--;
            return;
        }

        // Reset delay
        delayTimer = delay.get();

        // Write book

        if (mode.get() == Mode.Random) {
            int origin = onlyAscii.get() ? 0x21 : 0x0800;
            int bound = onlyAscii.get() ? 0x7E : 0x10FFFF;

            writeBook(
                // Generate a random load of ints to use as random characters
                random.ints(origin, bound)
                    .filter(i -> !Character.isWhitespace(i) && i != '\r' && i != '\n')
                    .iterator()
            );
        } else if (mode.get() == Mode.File) {
            // Ignore if somehow the file got deleted
            if ((file == null || !file.exists()) && mode.get() == Mode.File) {
                info("No file selected, please select a file in the GUI.");
                toggle();
                return;
            }

            // Handle the file being empty
            if (file.length() == 0) {
                MutableText message = Text.literal("");
                message.append(Text.literal("The bookbot file is empty! ").formatted(Formatting.RED));
                message.append(Text.literal("Click here to edit it.")
                    .setStyle(Style.EMPTY
                        .withFormatting(Formatting.UNDERLINE, Formatting.RED)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()))
                    )
                );
                info(message);
                toggle();
                return;
            }

            // Read each line of the file and construct a string with the needed line breaks
            try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                StringBuilder file = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    file.append(line).append('\n');
                }

                reader.close();

                // Write the file string to a book
                writeBook(file.toString().chars().iterator());
            } catch (IOException ignored) {
                error("Failed to read the file.");
            }
        }
    }

    private void writeBook(PrimitiveIterator.OfInt chars) {
        ArrayList<String> pages = new ArrayList<>();

        for (int pageI = 0; pageI < (mode.get() == Mode.File ? 100 : this.pages.get()); pageI++) {
            // Check if the stream is empty before creating a new page
            if (!chars.hasNext()) break;

            StringBuilder page = new StringBuilder();

            for (int lineI = 0; lineI < 13; lineI++) {
                // Check if the stream is empty before creating a new line
                if (!chars.hasNext()) break;

                double lineWidth = 0;
                StringBuilder line = new StringBuilder();

                while (true) {
                    // Check if the stream is empty
                    if (!chars.hasNext()) break;

                    // Get the next character
                    int nextChar = chars.nextInt();

                    // Ignore newline chars when writing lines, should already be organised
                    if (nextChar == '\r' || nextChar == '\n') break;

                    // Append it to the line
                    double charWidth = ((TextHandlerAccessor) mc.textRenderer.getTextHandler()).getWidthRetriever().getWidth(nextChar, Style.EMPTY);
                    line.appendCodePoint(nextChar);
                    lineWidth += charWidth;

                    // Make sure the character will fit on the line
                    if (lineWidth + charWidth > 114) break;
                }

                // Append the line to the page
                page.append(line).append('\n');
            }

            // Append page to the page list
            pages.add(page.toString());
        }

        // Get the title with count
        String title = name.get();
        if (count.get() && bookCount != 0) title += " #" + bookCount;

        // Write data to book
        mc.player.getMainHandStack().setSubNbt("title", NbtString.of(title));
        mc.player.getMainHandStack().setSubNbt("author", NbtString.of(mc.player.getGameProfile().getName()));

        // Write pages NBT
        NbtList pageNbt = new NbtList();
        pages.stream().map(NbtString::of).forEach(pageNbt::add);
        if (!pages.isEmpty()) mc.player.getMainHandStack().setSubNbt("pages", pageNbt);

        // Send book update to server
        mc.player.networkHandler.sendPacket(new BookUpdateC2SPacket(mc.player.getInventory().selectedSlot, pages, Optional.of(title)));

        bookCount++;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = super.toTag();

        if (file != null && file.exists()) tag.putString("file", file.getAbsolutePath());

        return tag;
    }

    @Override
    public Module fromTag(NbtCompound tag) {
        if (tag.contains("file")) file = new File(tag.getString("file"));

        return super.fromTag(tag);
    }

    public enum Mode {
        File("File"),
        Random("Random");

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
