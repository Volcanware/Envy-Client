package envy.client.gui.themes.meteor.widgets.pressable;

import envy.client.gui.themes.meteor.MeteorWidget;
import envy.client.gui.widgets.pressable.WFavorite;
import envy.client.utils.render.color.Color;

public class WMeteorFavorite extends WFavorite implements MeteorWidget {
    public WMeteorFavorite(boolean checked) {
        super(checked);
    }

    @Override
    protected Color getColor() {
        return theme().favoriteColor.get();
    }
}
