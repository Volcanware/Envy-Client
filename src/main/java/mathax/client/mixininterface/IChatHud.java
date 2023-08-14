package mathax.client.mixininterface;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface IChatHud {
    void add(Text message, int messageId, int timestamp, boolean refresh);

    void add(MutableText message, int id);
}
