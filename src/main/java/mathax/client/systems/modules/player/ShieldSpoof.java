package mathax.client.systems.modules.player;

import mathax.client.systems.modules.Category;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.mathax.MouseButtonEvent;
import mathax.client.events.world.TickEvent;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.EnumSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.Modules;
import mathax.client.utils.misc.input.KeyAction;
import mathax.client.utils.player.FindItemResult;
import mathax.client.utils.player.InvUtils;
import mathax.client.utils.player.PlayerUtils;
import net.minecraft.client.Mouse;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.item.*;

import static mathax.client.MatHax.mc;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
public class ShieldSpoof extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public ShieldSpoof(Category category, Item icon, String name, String description, boolean runInMainMenu) {
        super(category, icon, name, description, runInMainMenu);
    }

    public ShieldSpoof() {
        super(Categories.Player, Items.SHIELD, "ShieldSpoof", "Allows you to use a shield without having one in your hotbar.", false);
    }

    public MouseButtonEvent get(int button, KeyAction action) {
        if (mc.mouse.wasRightButtonClicked()) {
            mc.player.getInventory().offHand.set(0, new ItemStack(Items.SHIELD));
        }
        if (mc.player.getOffHandStack().isEmpty() && mc.player.getMainHandStack().getItem() instanceof ShieldItem){
            Animation.class.cast(mc.player.isBlocking());
        }
        return null;
    }
}
