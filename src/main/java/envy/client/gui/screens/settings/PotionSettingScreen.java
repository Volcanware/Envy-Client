package envy.client.gui.screens.settings;

import envy.client.gui.GuiTheme;
import envy.client.gui.WindowScreen;
import envy.client.gui.widgets.containers.WTable;
import envy.client.gui.widgets.pressable.WButton;
import envy.client.settings.PotionSetting;
import envy.client.utils.misc.MyPotion;

public class PotionSettingScreen extends WindowScreen {
    private final PotionSetting setting;

    public PotionSettingScreen(GuiTheme theme, PotionSetting setting) {
        super(theme, "Select Potion");

        this.setting = setting;
    }

    @Override
    public void initWidgets() {
        WTable table = add(theme.table()).expandX().widget();

        for (MyPotion potion : MyPotion.values()) {
            table.add(theme.itemWithLabel(potion.potion, potion.potion.getName().getString()));

            WButton select = table.add(theme.button("Select")).widget();
            select.action = () -> {
                setting.set(potion);
                close();
            };

            table.row();
        }
    }
}
