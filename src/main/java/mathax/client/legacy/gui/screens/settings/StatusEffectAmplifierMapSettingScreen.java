package mathax.client.legacy.gui.screens.settings;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WindowScreen;
import mathax.client.legacy.gui.widgets.containers.WTable;
import mathax.client.legacy.gui.widgets.input.WIntEdit;
import mathax.client.legacy.gui.widgets.input.WTextBox;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.utils.misc.Names;
import net.minecraft.entity.effect.StatusEffect;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatusEffectAmplifierMapSettingScreen extends WindowScreen {
    private final Setting<Object2IntMap<StatusEffect>> setting;

    private WTable table;

    private WTextBox filter;
    private String filterText = "";

    public StatusEffectAmplifierMapSettingScreen(GuiTheme theme, Setting<Object2IntMap<StatusEffect>> setting) {
        super(theme, "Modify Amplifiers");

        this.setting = setting;

        initWidgets();
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
        List<StatusEffect> statusEffects = new ArrayList<>(setting.get().keySet());
        statusEffects.sort(Comparator.comparing(Names::get));

        for (StatusEffect statusEffect : statusEffects) {
            String name = Names.get(statusEffect);
            if (!StringUtils.containsIgnoreCase(name, filterText)) continue;

            table.add(theme.label(name)).expandCellX();

            WIntEdit level = theme.intEdit(setting.get().getInt(statusEffect), 0, 0);
            level.hasSlider = false;
            level.action = () -> {
                setting.get().put(statusEffect, level.get());
                setting.changed();
            };

            table.add(level).minWidth(50);
            table.row();
        }
    }
}
