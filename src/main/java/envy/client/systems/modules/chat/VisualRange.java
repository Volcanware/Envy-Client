package envy.client.systems.modules.chat;

import envy.client.eventbus.EventHandler;
import envy.client.events.entity.EntityAddedEvent;
import envy.client.events.entity.EntityRemovedEvent;
import envy.client.settings.*;
import envy.client.systems.enemies.Enemies;
import envy.client.systems.friends.Friends;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import envy.client.utils.entity.fakeplayer.FakePlayerEntity;
import envy.client.utils.misc.ChatUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VisualRange extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Event> eventMode = sgGeneral.add(new EnumSetting.Builder<Event>()
        .name("event")
        .description("Determines when to log the entities.")
        .defaultValue(Event.Both)
        .build()
    );

    private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Determines which entities to notify about.")
        .defaultValue(EntityType.PLAYER)
        .build()
    );

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> ignoreEnemies = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-enemies")
        .description("Ignores enemies.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> ignoreFakes = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-fake-players")
        .description("Ignores fake players.")
        .defaultValue(true)
        .build()
    );

    // TODO: Notify modes.

    public VisualRange() {
        super(Categories.Chat, Items.SPYGLASS, "visual-range", "Notifies you when an entity enters your render distance.");
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (event.entity.getUuid().equals(mc.player.getUuid()) || !entities.get().getBoolean(event.entity.getType()) || eventMode.get() == Event.Despawn) return;

        if (event.entity instanceof PlayerEntity) {
            if ((!ignoreFriends.get() || !Friends.get().isFriend(((PlayerEntity) event.entity))) && (!ignoreFakes.get() || !(event.entity instanceof FakePlayerEntity))) ChatUtils.sendMsg(event.entity.getId() + 100, Formatting.GRAY, "(highlight)%s(default) has entered your visual range!", event.entity.getEntityName());
        } else {
            String entityPos = event.entity.getPos().toString();
            MutableText text = Text.literal(event.entity.getType().getName().getString()).formatted(Formatting.WHITE);
            text.append(Text.literal(" has spawned at ").formatted(Formatting.GRAY));
            text.append(entityPos);
            text.append(Text.literal(".").formatted(Formatting.GRAY));
            info(text);
        }
    }

    @EventHandler
    private void onEntityRemoved(EntityRemovedEvent event) {
        if (event.entity.getUuid().equals(mc.player.getUuid()) || !entities.get().getBoolean(event.entity.getType()) || eventMode.get() == Event.Spawn) return;

        if (event.entity instanceof PlayerEntity player) {
            if ((!ignoreFriends.get() || !Friends.get().isFriend(player)) && (!ignoreEnemies.get() || !Enemies.get().isEnemy(player)) && (!ignoreFakes.get() || !(player instanceof FakePlayerEntity))) ChatUtils.sendMsg(player.getId() + 100, Formatting.GRAY, "(highlight)%s(default) has left your visual range!", player.getName().getString());
        } else {
            MutableText text = Text.literal(event.entity.getType().getName().getString()).formatted(Formatting.WHITE);
            text.append(Text.literal(" has despawned at ").formatted(Formatting.GRAY));
            text.append(ChatUtils.formatCoords(event.entity.getPos()));
            text.append(Text.literal(".").formatted(Formatting.GRAY));
            info(text);
        }
    }

    public enum Event {
        Spawn("Spawn"),
        Despawn("Despawn"),
        Both("Both");

        private final String title;

        Event(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
