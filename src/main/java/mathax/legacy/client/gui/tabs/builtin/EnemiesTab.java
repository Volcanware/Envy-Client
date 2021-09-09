package mathax.legacy.client.gui.tabs.builtin;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.tabs.Tab;
import mathax.legacy.client.gui.tabs.TabScreen;
import mathax.legacy.client.gui.tabs.WindowTabScreen;
import mathax.legacy.client.gui.widgets.containers.WHorizontalList;
import mathax.legacy.client.gui.widgets.containers.WSection;
import mathax.legacy.client.gui.widgets.containers.WTable;
import mathax.legacy.client.gui.widgets.input.WTextBox;
import mathax.legacy.client.gui.widgets.pressable.WMinus;
import mathax.legacy.client.gui.widgets.pressable.WPlus;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.ColorSetting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.settings.Settings;
import mathax.legacy.client.systems.enemies.Enemy;
import mathax.legacy.client.systems.enemies.Enemies;
import mathax.legacy.client.utils.render.color.SettingColor;
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

            Settings settings = new Settings();

            SettingGroup sgGeneral = settings.getDefaultGroup();

            sgGeneral.add(new ColorSetting.Builder()
                .name("color")
                .description("The color used to show enemies.")
                .defaultValue(new SettingColor(0, 255, 180))
                .onChanged(Enemies.get().color::set)
                .onModuleActivated(colorSetting -> colorSetting.set(Enemies.get().color))
                .build()
            );

            sgGeneral.add(new BoolSetting.Builder()
                .name("attack")
                .description("Whether to attack enemies.")
                .defaultValue(false)
                .onChanged(aBoolean -> Enemies.get().attack = aBoolean)
                .onModuleActivated(booleanSetting -> booleanSetting.set(Enemies.get().attack))
                .build()
            );

            settings.onActivated();
            add(theme.settings(settings)).expandX();

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
