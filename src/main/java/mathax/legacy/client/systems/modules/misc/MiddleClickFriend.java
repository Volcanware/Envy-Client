package mathax.legacy.client.systems.modules.misc;

import mathax.legacy.client.events.mathax.MouseButtonEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.settings.StringSetting;
import mathax.legacy.client.systems.config.Config;
import mathax.legacy.client.systems.enemies.Enemies;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.friends.Friend;
import mathax.legacy.client.systems.friends.Friends;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.utils.misc.Placeholders;
import mathax.legacy.client.utils.misc.input.KeyAction;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.utils.player.ChatUtils;
import mathax.legacy.client.utils.render.MatHaxToast;
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
        super(Categories.Misc, Items.STONE_BUTTON, "middle-click-friend", "Adds or removes a player as a friend using middle click");
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Press && event.button == GLFW_MOUSE_BUTTON_MIDDLE && mc.currentScreen == null && mc.targetedEntity != null && mc.targetedEntity instanceof PlayerEntity) {
            if (mc.targetedEntity.getUuidAsString().equals(mc.getSession().getUuid())) return;
            if (!Friends.get().isFriend((PlayerEntity) mc.targetedEntity)) {
                if (Enemies.get().isEnemy((PlayerEntity) mc.targetedEntity)) {
                    ChatUtils.error("Friends", "Could not add to friends because this person is on your Enemy list.");
                    if (Config.get().chatCommandsToast) mc.getToastManager().add(new MatHaxToast(Items.EMERALD_BLOCK, Friends.get().color.getPacked(), "Friends " + Formatting.GRAY + "[" + Formatting.WHITE + mc.targetedEntity.getEntityName() + Formatting.GRAY + "]", Formatting.RED + "Person is enemy."));
                } else {
                    Friends.get().add(new Friend((PlayerEntity) mc.targetedEntity));
                    if (friendAddMessage.get()) {
                        mc.player.sendChatMessage("/msg " + mc.targetedEntity.getEntityName() + " " + Placeholders.apply(friendAddMessageText.toString()));
                    }
                    if (Config.get().chatCommandsInfo) ChatUtils.info("Friends", "Added (highlight)%s (default)to friends.", mc.targetedEntity.getEntityName());
                    if (Config.get().chatCommandsToast) mc.getToastManager().add(new MatHaxToast(Items.EMERALD_BLOCK, Friends.get().color.getPacked(), "Friends " + Formatting.GRAY + "[" + Formatting.WHITE + mc.targetedEntity.getEntityName() + Formatting.GRAY + "]", Formatting.GRAY + "Added to friends."));
                }
            } else {
                Friends.get().remove(Friends.get().get((PlayerEntity) mc.targetedEntity));
                if (friendRemoveMessage.get()) {
                    mc.player.sendChatMessage("/msg " + mc.targetedEntity.getEntityName() + " " + Placeholders.apply(friendRemoveMessageText.toString()));
                }
                if (Config.get().chatCommandsInfo) ChatUtils.info("Friends", "Removed (highlight)%s (default)from friends.", mc.targetedEntity.getEntityName());
                if (Config.get().chatCommandsToast) mc.getToastManager().add(new MatHaxToast(Items.EMERALD_BLOCK, Friends.get().color.getPacked(), "Friends " + Formatting.GRAY + "[" + Formatting.WHITE + mc.targetedEntity.getEntityName() + Formatting.GRAY + "]", Formatting.GRAY + "Removed from friends."));
            }
        }
    }
}
