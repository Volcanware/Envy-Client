package mathax.client.legacy.gui.tabs.builtin;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.tabs.Tab;
import mathax.client.legacy.gui.tabs.TabScreen;
import mathax.client.legacy.gui.tabs.WindowTabScreen;
import mathax.client.legacy.gui.widgets.containers.WHorizontalList;
import mathax.client.legacy.gui.widgets.containers.WSection;
import mathax.client.legacy.gui.widgets.containers.WTable;
import mathax.client.legacy.gui.widgets.input.WTextBox;
import mathax.client.legacy.gui.widgets.pressable.WMinus;
import mathax.client.legacy.gui.widgets.pressable.WPlus;
import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.ColorSetting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.settings.Settings;
import mathax.client.legacy.systems.enemies.Enemy;
import mathax.client.legacy.systems.enemies.Enemies;
import mathax.client.legacy.utils.render.color.SettingColor;
import net.minecraft.client.gui.screen.Screen;

public class EnemiesTab extends Tab {
    public EnemiesTab() {
        super("Enemies");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new EnemiesScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof EnemiesScreen;
    }

    public static class EnemiesScreen extends WindowTabScreen {
        public EnemiesScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);

            Settings s = new Settings();

            SettingGroup sgDefault = s.getDefaultGroup();

            sgDefault.add(new ColorSetting.Builder()
                    .name("color")
                    .description("The color used to show enemies.")
                    .defaultValue(new SettingColor(0, 255, 180))
                    .onChanged(Enemies.get().color::set)
                    .onModuleActivated(colorSetting -> colorSetting.set(Enemies.get().color))
                    .build()
            );

            sgDefault.add(new BoolSetting.Builder()
                    .name("attack")
                    .description("Whether to attack enemies.")
                    .defaultValue(false)
                    .onChanged(aBoolean -> Enemies.get().attack = aBoolean)
                    .onModuleActivated(booleanSetting -> booleanSetting.set(Enemies.get().attack))
                    .build()
            );

            s.onActivated();
            add(theme.settings(s)).expandX();

            // Enemies
            WSection enemies = add(theme.section("Enemies")).expandX().widget();
            WTable table = enemies.add(theme.table()).expandX().widget();

            fillTable(table);

            // New
            WHorizontalList list = enemies.add(theme.horizontalList()).expandX().widget();

            WTextBox nameW = list.add(theme.textBox("")).minWidth(400).expandX().widget();
            nameW.setFocused(true);

            WPlus add = list.add(theme.plus()).widget();
            add.action = () -> {
                String name = nameW.get().trim();

                if (Enemies.get().add(new Enemy(name))) {
                    nameW.set("");

                    table.clear();
                    fillTable(table);
                }
            };

            enterAction = add.action;
        }

        private void fillTable(WTable table) {
            for (Enemy enemy : Enemies.get()) {
                table.add(theme.label(enemy.name));

                WMinus remove = table.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    Enemies.get().remove(enemy);

                    table.clear();
                    fillTable(table);
                };

                table.row();
            }
        }
    }
}
