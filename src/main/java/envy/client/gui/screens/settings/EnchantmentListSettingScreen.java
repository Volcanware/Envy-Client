package envy.client.gui.screens.settings;

import envy.client.gui.GuiTheme;
import envy.client.gui.widgets.WWidget;
import envy.client.settings.Setting;
import envy.client.utils.misc.Names;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class EnchantmentListSettingScreen extends LeftRightListSettingScreen<Enchantment> {
    public EnchantmentListSettingScreen(GuiTheme theme, Setting<List<Enchantment>> setting) {
        super(theme, "Select Enchantments", setting, setting.get(), Registry.ENCHANTMENT);
    }

    @Override
    protected WWidget getValueWidget(Enchantment value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(Enchantment value) {
        return Names.get(value);
    }
}
