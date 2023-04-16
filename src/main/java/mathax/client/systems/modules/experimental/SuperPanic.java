package mathax.client.systems.modules.experimental;

import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import net.minecraft.item.Items;

import java.awt.*;
import java.awt.event.KeyEvent;

public class SuperPanic extends Module{
    public SuperPanic(){
        super(Categories.Experimental, Items.ACACIA_BOAT,"SuperPanic","automatically crashes you game(doesnt work)");
    }
    private void Crasher() throws AWTException {
        Robot r = new Robot();
        r.keyPress(KeyEvent.VK_ALT);
        r.keyPress(KeyEvent.VK_F4);
        r.keyRelease(KeyEvent.VK_ALT);
        r.keyRelease(KeyEvent.VK_F4);
    }
}
