package mathax.legacy.client.settings;

import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.WidgetScreen;
import mathax.legacy.client.utils.misc.IChangeable;
import mathax.legacy.client.utils.misc.ICopyable;
import mathax.legacy.client.utils.misc.ISerializable;
import net.minecraft.block.Block;

public interface IBlockData<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> {
    WidgetScreen createScreen(GuiTheme theme, Block block, BlockDataSetting<T> setting);
}
