package envy.client.settings;

import envy.client.gui.GuiTheme;
import envy.client.gui.WidgetScreen;
import envy.client.utils.misc.IChangeable;
import envy.client.utils.misc.ICopyable;
import envy.client.utils.misc.ISerializable;
import net.minecraft.block.Block;

public interface IBlockData<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> {
    WidgetScreen createScreen(GuiTheme theme, Block block, BlockDataSetting<T> setting);
}
