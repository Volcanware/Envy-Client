package mathax.legacy.client.systems.modules.crash;

import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import net.minecraft.item.Items;

/*/-------------------------------------------------------------------------------------------------------------------------/*/
/*/ Used from Meteor Crash Addon by Wide Cat                                                                                /*/
/*/ https://github.com/Wide-Cat/meteor-crash-addon/blob/main/src/main/java/widecat/meteorcrashaddon/modules/LoginCrash.java /*/
/*/-------------------------------------------------------------------------------------------------------------------------/*/

public class LoginCrash extends Module {
    public LoginCrash() {
        super(Categories.Crash, Items.COMMAND_BLOCK, "login-crash", "Tries to crash the server on login using null packets.");
    }
}
