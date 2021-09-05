package mathax.client.legacy.systems.modules.misc;

import mathax.client.legacy.events.mathax.MouseButtonEvent;
import mathax.client.legacy.settings.BoolSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.settings.StringSetting;
import mathax.client.legacy.systems.config.Config;
import mathax.client.legacy.systems.enemies.Enemies;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.systems.friends.Friend;
import mathax.client.legacy.systems.friends.Friends;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.utils.placeholders.Placeholders;
import mathax.client.legacy.utils.misc.input.KeyAction;
import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.utils.player.ChatUtils;
import mathax.client.legacy.utils.render.MatHaxToast;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickFriend extends Module {
    private final SettingGroup sgAdd = settings.createGroup("Add");
    private final SettingGroup sgRemove = settings.createGroup("Remove");

    private final Setting<Boolean> friendAddMessage = sgAdd.add(new BoolSetting.Builder()
        .name("friend-add-message")
        .description("Sends a message to the player when you add them as a friend.")
        .defaultValue(false)
        .build()
    );

    private final Setting<String> friendAddMessageText = sgAdd.add(new StringSetting.Builder()
        .name("friend-add-message-text")
        .description("The message sent to the player after friending him.")
        .defaultValue("I just friended you on MatHax Legacy!")
        .visible(friendAddMessage::get)
        .build()
    );

    private final Setting<Boolean> friendRemoveMessage = sgRemove.add(new BoolSetting.Builder()
        .name("friend-remove-message")
        .description("Sends a message to the player when you add them as a friend.")
        .defaultValue(false)
        .build()
    );

    private final Setting<String> friendRemoveMessageText = sgRemove.add(new StringSetting.Builder()
        .name("friend-remove-message-text")
        .description("The message sent to the player after unfriending him.")
        .defaultValue("I just unfriended you on MatHax Legacy!")
        .visible(friendRemoveMessage::get)
        .build()
    );

    public MiddleClickFriend() {
        super(Categories.Misc, "middle-click-friend", "Adds or removes a player as a friend using middle click.");
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Press && event.button == GLFW_MOUSE_BUTTON_MIDDLE && mc.currentScreen == null && mc.targetedEntity != null && mc.targetedEntity instanceof PlayerEntity) {
            if (!Friends.get().isFriend((PlayerEntity) mc.targetedEntity)) {
                if (Enemies.get().isEnemy((PlayerEntity) mc.targetedEntity)) {
                    ChatUtils.error("Friends", "Could not add to friends because this person is on your Enemy list.");
                    if (Config.get().chatCommandsToast) mc.getToastManager().add(new MatHaxToast(Items.EMERALD, Formatting.DARK_RED + "Friends", Formatting.RED + "Could not add to friends because this person is on your Enemy list."));
                } else {
                    Friends.get().add(new Friend((PlayerEntity) mc.targetedEntity));
                    if (friendAddMessage.get()) {
                        mc.player.sendChatMessage("/msg " + mc.targetedEntity.getEntityName() + " " + Placeholders.apply(friendAddMessageText.toString()));
                    }
                }
            } else {
                Friends.get().remove(Friends.get().get((PlayerEntity) mc.targetedEntity));
                if (friendRemoveMessage.get()) {
                    mc.player.sendChatMessage("/msg " + mc.targetedEntity.getEntityName() + " " + Placeholders.apply(friendRemoveMessageText.toString()));
                }
            }
        }
    }
}
