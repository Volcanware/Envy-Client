package mathax.client.legacy.gui.screens;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WindowScreen;
import mathax.client.legacy.gui.widgets.containers.WHorizontalList;
import mathax.client.legacy.gui.widgets.containers.WTable;
import mathax.client.legacy.utils.misc.Version;
import mathax.client.legacy.systems.config.Config;
import net.minecraft.util.Util;

public class NewUpdateScreen extends WindowScreen {
    public NewUpdateScreen(GuiTheme theme, Version latestVer) {
        super(theme, "New Update");

        add(theme.label("A new version of MatHax Legacy has been released."));

        add(theme.horizontalSeparator()).expandX();

        WTable versionsT = add(theme.table()).widget();
        versionsT.add(theme.label("Your version:"));
        versionsT.add(theme.label("v" + Config.get().version.toString()));
        versionsT.row();
        versionsT.add(theme.label("Latest version:"));
        versionsT.add(theme.label("v" + latestVer.toString()));

        add(theme.horizontalSeparator()).expandX();

        WHorizontalList buttonsL = add(theme.horizontalList()).widget();
        buttonsL.add(theme.button("Download v" + latestVer)).expandX().widget().action = () -> Util.getOperatingSystem().open("https://mathaxclient.xyz");
        buttonsL.add(theme.button("OK")).expandX().widget().action = this::onClose;
    }
}
