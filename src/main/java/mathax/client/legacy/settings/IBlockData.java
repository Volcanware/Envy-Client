package mathax.client.legacy.settings;

import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.WidgetScreen;
import mathax.client.legacy.utils.misc.IChangeable;
import mathax.client.legacy.utils.misc.ICopyable;
import mathax.client.legacy.utils.misc.ISerializable;
import net.minecraft.block.Block;

public interface IBlockData<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> {
    WidgetScreen createScreen(GuiTheme theme, Block block, BlockDataSetting<T> setting);
}
