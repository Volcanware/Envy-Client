package envy.client.gui.themes.mathax.widgets.pressable;

import envy.client.gui.themes.mathax.MatHaxWidget;
import envy.client.gui.widgets.pressable.WFavorite;
import envy.client.utils.render.color.Color;

public class WMatHaxFavorite extends WFavorite implements MatHaxWidget {
    public WMatHaxFavorite(boolean checked) {
        super(checked);
    }

    @Override
    protected Color getColor() {
        return theme().favoriteColor.get();
    }
}
