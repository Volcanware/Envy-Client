package mathax.legacy.client.systems.modules.chat;

import mathax.legacy.client.events.entity.EntityAddedEvent;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.settings.StringSetting;
import mathax.legacy.client.systems.enemies.Enemies;
import mathax.legacy.client.systems.friends.Friends;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.utils.misc.Placeholders;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

public class MessageAura extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> message = sgGeneral.add(new StringSetting.Builder()
        .name("message")
        .description("The specified message sent to the player.")
        .defaultValue("Welcome to my render distance!")
        .build()
    );

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Will not send any messages to people which are your friends.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> ignoreEnemies = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-enemies")
        .description("Will not send any messages to people which are your enemies.")
        .defaultValue(false)
        .build()
    );

    public MessageAura() {
        super(Categories.Chat, Items.DISPENSER, "message-aura", "Sends a specified message to any player that enters render distance");
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (!(event.entity instanceof PlayerEntity) || event.entity.getUuid().equals(mc.player.getUuid())) return;

        if (!ignoreFriends.get() || (ignoreFriends.get() && !Friends.get().isFriend((PlayerEntity)event.entity))) {
            mc.player.sendChatMessage("/msg " + event.entity.getEntityName() + " " + Placeholders.apply(message.get()));
        }
        if (!ignoreEnemies.get() || (ignoreEnemies.get() && !Enemies.get().isEnemy((PlayerEntity)event.entity))) {
            mc.player.sendChatMessage("/msg " + event.entity.getEntityName() + " " + Placeholders.apply(message.get()));
        }
    }
}
