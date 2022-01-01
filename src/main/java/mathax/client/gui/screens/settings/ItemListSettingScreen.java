package mathax.client.gui.screens.settings;

import mathax.client.gui.GuiTheme;
import mathax.client.gui.widgets.WWidget;
import mathax.client.settings.ItemListSetting;
import mathax.client.utils.misc.Names;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import java.util.function.Predicate;

public class ItemListSettingScreen extends LeftRightListSettingScreen<Item> {
    public ItemListSettingScreen(GuiTheme theme, ItemListSetting setting) {
        super(theme, "Select Items", setting, setting.get(), Registry.ITEM);
    }

    @Override
    protected boolean includeValue(Item value) {
        Predicate<Item> filter = ((ItemListSetting) setting).filter;
        if (filter != null && !filter.test(value)) return false;

        return value != Items.AIR;
    }

    @Override
    protected WWidget getValueWidget(Item value) {
        return theme.itemWithLabel(value.getDefaultStack());
    }

    @Override
    protected String getValueName(Item value) {
        return Names.get(value);
    }
}
