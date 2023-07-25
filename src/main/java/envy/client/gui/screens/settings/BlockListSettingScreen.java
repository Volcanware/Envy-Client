package envy.client.gui.screens.settings;

import envy.client.gui.GuiTheme;
import envy.client.gui.widgets.WWidget;
import envy.client.mixin.IdentifierAccessor;
import envy.client.settings.BlockListSetting;
import envy.client.settings.Setting;
import envy.client.utils.misc.Names;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.function.Predicate;

public class BlockListSettingScreen extends LeftRightListSettingScreen<Block> {
    private static final Identifier ID = new Identifier("minecraft", "");

    public BlockListSettingScreen(GuiTheme theme, Setting<List<Block>> setting) {
        super(theme, "Select Blocks", setting, setting.get(), Registry.BLOCK);
    }

    @Override
    protected boolean includeValue(Block value) {
        Predicate<Block> filter = ((BlockListSetting) setting).filter;

        if (filter == null) return value != Blocks.AIR;
        return filter.test(value);
    }

    @Override
    protected WWidget getValueWidget(Block value) {
        return theme.itemWithLabel(value.asItem().getDefaultStack(), getValueName(value));
    }

    @Override
    protected String getValueName(Block value) {
        return Names.get(value);
    }

    @Override
    protected boolean skipValue(Block value) {
        return Registry.BLOCK.getId(value).getPath().endsWith("_wall_banner");
    }

    @Override
    protected Block getAdditionalValue(Block value) {
        String path = Registry.BLOCK.getId(value).getPath();
        if (!path.endsWith("_banner")) return null;

        ((IdentifierAccessor) ID).setPath(path.substring(0, path.length() - 6) + "wall_banner");
        return Registry.BLOCK.get(ID);
    }
}
