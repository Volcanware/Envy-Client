package envy.client.gui.screens.settings;

import envy.client.gui.GuiTheme;
import envy.client.gui.WindowScreen;
import envy.client.gui.widgets.WItemWithLabel;
import envy.client.gui.widgets.containers.WTable;
import envy.client.gui.widgets.input.WTextBox;
import envy.client.gui.widgets.pressable.WButton;
import envy.client.settings.BlockSetting;
import envy.client.utils.misc.Names;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

public class BlockSettingScreen extends WindowScreen {
    private final BlockSetting setting;

    private WTable table;

    private WTextBox filter;
    private String filterText = "";

    public BlockSettingScreen(GuiTheme theme, BlockSetting setting) {
        super(theme, "Select Block");

        this.setting = setting;
    }

    @Override
    public void initWidgets() {
        filter = add(theme.textBox("")).minWidth(400).expandX().widget();
        filter.setFocused(true);
        filter.action = () -> {
            filterText = filter.get().trim();

            table.clear();
            initTable();
        };

        table = add(theme.table()).expandX().widget();

        initTable();
    }

    private void initTable() {
        for (Block block : Registry.BLOCK) {
            if (setting.filter != null && !setting.filter.test(block)) continue;
            if (skipValue(block)) continue;

            WItemWithLabel item = theme.itemWithLabel(block.asItem().getDefaultStack(), Names.get(block));
            if (!filterText.isEmpty() && !StringUtils.containsIgnoreCase(item.getLabelText(), filterText)) continue;
            table.add(item);

            WButton select = table.add(theme.button("Select")).expandCellX().right().widget();
            select.action = () -> {
                setting.set(block);
                close();
            };

            table.row();
        }
    }

    protected boolean skipValue(Block value) {
        return value == Blocks.AIR || Registry.BLOCK.getId(value).getPath().endsWith("_wall_banner");
    }
}
