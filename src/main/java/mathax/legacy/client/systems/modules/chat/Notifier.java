package mathax.legacy.client.systems.modules.chat;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mathax.legacy.client.events.entity.EntityAddedEvent;
import mathax.legacy.client.events.entity.EntityRemovedEvent;
import mathax.legacy.client.events.game.GameJoinedEvent;
import mathax.legacy.client.events.packets.PacketEvent;
import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.enemies.Enemies;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.friends.Friends;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.entity.fakeplayer.FakePlayerEntity;
import mathax.legacy.client.utils.player.ChatUtils;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static mathax.legacy.client.utils.player.ChatUtils.formatCoords;

public class Notifier extends Module {
    private static String entityName;
    private static String entityPos;

    private final Map<PlayerEntity, Integer> entityArmorArraylist = new HashMap<>();
    private final Object2IntMap<UUID> totemPopMap = new Object2IntOpenHashMap<>();
    private final Object2IntMap<UUID> chatIdMap = new Object2IntOpenHashMap<>();

    private final Random random = new Random();

    private final SettingGroup sgTotemPops = settings.createGroup("Totem Pops");
    private final SettingGroup sgVisualRange = settings.createGroup("Visual Range");
    private final SettingGroup sgArmor = settings.createGroup("Armor");
    private final SettingGroup sgArmorMessages = settings.createGroup("Armor Messages");

    // Totem Pops

    private final Setting<Boolean> totemPops = sgTotemPops.add(new BoolSetting.Builder()
        .name("totem-pops")
        .description("Notifies you when a player pops a totem.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> totemsIgnoreOwn = sgTotemPops.add(new BoolSetting.Builder()
        .name("ignore-own")
        .description("Notifies you of your own totem pops.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> totemsIgnoreFriends = sgTotemPops.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends totem pops.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> totemsIgnoreOthers = sgTotemPops.add(new BoolSetting.Builder()
        .name("ignore-others")
        .description("Ignores other players totem pops.")
        .defaultValue(false)
        .build()
    );

    // Visual Range

    private final Setting<Boolean> visualRange = sgVisualRange.add(new BoolSetting.Builder()
        .name("visual-range")
        .description("Notifies you when an entity enters your render distance.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Event> event = sgVisualRange.add(new EnumSetting.Builder<Event>()
        .name("event")
        .description("When to log the entities.")
        .defaultValue(Event.Both)
        .build()
    );

    private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgVisualRange.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Which entities to notify about.")
        .defaultValue(Utils.asObject2BooleanOpenHashMap(EntityType.PLAYER))
        .build()
    );

    private final Setting<Boolean> visualRangeIgnoreFriends = sgVisualRange.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> visualRangeIgnoreFakes = sgVisualRange.add(new BoolSetting.Builder()
        .name("ignore-fake-players")
        .description("Ignores fake players.")
        .defaultValue(true)
        .build()
    );

    // Armor

    private final Setting<Boolean> armor = sgArmor.add(new BoolSetting.Builder()
        .name("armor")
        .description("Notifies you & others when they have low armor durability.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> armorThreshhold = sgArmor.add(new IntSetting.Builder()
        .name("percentage")
        .description("At which percentage to notify.")
        .defaultValue(20)
        .min(1)
        .max(100)
        .sliderMin(1)
        .sliderMax(100)
        .build()
    );

    private final Setting<Boolean> armorIgnoreSelf = sgArmor.add(new BoolSetting.Builder()
        .name("ignore-self")
        .description("Ignores you.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> armorIgnoreFriends = sgArmor.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> armorIgnoreEnemies = sgArmor.add(new BoolSetting.Builder()
        .name("ignore-enemies")
        .description("Ignores enemies.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> armorIgnoreOthers = sgArmor.add(new BoolSetting.Builder()
        .name("ignore-others")
        .description("Ignores everyone else.")
        .defaultValue(true)
        .build()
    );

    // Armor Messages

    private final Setting<Boolean> armorMessagesFriends = sgArmorMessages.add(new BoolSetting.Builder()
        .name("private-friends")
        .description("Shows a message just for you about friends armor durability being low.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> armorPMsFriends = sgArmorMessages.add(new BoolSetting.Builder()
        .name("PM-friends")
        .description("PM friends armor durability being low.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> armorMessagesEnemies = sgArmorMessages.add(new BoolSetting.Builder()
        .name("private-enemies")
        .description("Shows a message just for you about enemies armor durability being low.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> armorPMsEnemies = sgArmorMessages.add(new BoolSetting.Builder()
        .name("PM-friends")
        .description("PM enemies armor durability being low.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> armorMessagesOthers = sgArmorMessages.add(new BoolSetting.Builder()
        .name("private-others")
        .description("Shows a message just for you about others armor durability being low.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> armorPMsOthers = sgArmorMessages.add(new BoolSetting.Builder()
        .name("PM-others")
        .description("PM others armor durability being low.")
        .defaultValue(true)
        .build()
    );

    public Notifier() {
        super(Categories.Chat, Items.PAPER, "notifier", "Notifies you or others of different events.");
    }

    // Visual Range

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (event.entity.getUuid().equals(mc.player.getUuid()) || !entities.get().getBoolean(event.entity.getType()) || !visualRange.get() || this.event.get() == Event.Despawn) return;

        if (event.entity instanceof PlayerEntity) {
            if ((!visualRangeIgnoreFriends.get() || !Friends.get().isFriend(((PlayerEntity) event.entity))) && (!visualRangeIgnoreFakes.get() || !(event.entity instanceof FakePlayerEntity))) {
                entityName = event.entity.getEntityName();
                ChatUtils.sendMsg(event.entity.getId() + 100, Formatting.GRAY, "(highlight)%s(default) has entered your visual range!", entityName);
            }
        }
        else {
            entityName = event.entity.getType().getName().getString();
            entityPos = event.entity.getPos().toString();
            MutableText text = new LiteralText(entityName).formatted(Formatting.WHITE);
            text.append(new LiteralText(" has spawned at ").formatted(Formatting.GRAY));
            text.append(entityPos);
            text.append(new LiteralText(".").formatted(Formatting.GRAY));
            info(text);
        }
    }

    @EventHandler
    private void onEntityRemoved(EntityRemovedEvent event) {
        if (event.entity.getUuid().equals(mc.player.getUuid()) || !entities.get().getBoolean(event.entity.getType()) || !visualRange.get() || this.event.get() == Event.Spawn) return;

        if (event.entity instanceof PlayerEntity) {
            if ((!visualRangeIgnoreFriends.get() || !Friends.get().isFriend(((PlayerEntity) event.entity))) && (!visualRangeIgnoreFakes.get() || !(event.entity instanceof FakePlayerEntity))) {
                ChatUtils.sendMsg(event.entity.getId() + 100, Formatting.GRAY, "(highlight)%s(default) has left your visual range!", event.entity.getEntityName());
            }
        } else {
            MutableText text = new LiteralText(event.entity.getType().getName().getString()).formatted(Formatting.WHITE);
            text.append(new LiteralText(" has despawned at ").formatted(Formatting.GRAY));
            text.append(formatCoords(event.entity.getPos()));
            text.append(new LiteralText(".").formatted(Formatting.GRAY));
            info(text);
        }
    }

    // Totem Pops

    @Override
    public void onActivate() {
        totemPopMap.clear();
        chatIdMap.clear();
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        totemPopMap.clear();
        chatIdMap.clear();
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!totemPops.get()) return;
        if (!(event.packet instanceof EntityStatusS2CPacket)) return;

        EntityStatusS2CPacket p = (EntityStatusS2CPacket) event.packet;
        if (p.getStatus() != 35) return;

        Entity entity = p.getEntity(mc.world);

        if (!(entity instanceof PlayerEntity)) return;

        if ((entity.equals(mc.player) && totemsIgnoreOwn.get())
            || (Friends.get().isFriend(((PlayerEntity) entity)) && totemsIgnoreOthers.get())
            || (!Friends.get().isFriend(((PlayerEntity) entity)) && totemsIgnoreFriends.get())
        ) return;

        synchronized (totemPopMap) {
            int pops = totemPopMap.getOrDefault(entity.getUuid(), 0);
            totemPopMap.put(entity.getUuid(), ++pops);

            ChatUtils.sendMsg(getChatId(entity), Formatting.GRAY, "(highlight)%s (default)popped (highlight)%d (default)%s.", entity.getEntityName(), pops, pops == 1 ? "totem" : "totems");
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (totemPops.get()) {
            synchronized (totemPopMap) {
                for (PlayerEntity player : mc.world.getPlayers()) {
                    if (!totemPopMap.containsKey(player.getUuid())) continue;

                    if (player.deathTime > 0 || player.getHealth() <= 0) {
                        int pops = totemPopMap.removeInt(player.getUuid());

                        ChatUtils.sendMsg(getChatId(player), Formatting.GRAY, "(highlight)%s (default)died after popping (highlight)%d (default)%s.", player.getEntityName(), pops, pops == 1 ? "totem" : "totems");
                        chatIdMap.removeInt(player.getUuid());
                    }
                }
            }
        }

        // Armor

        if (armor.get()) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player instanceof FakePlayerEntity) return;
                if (player.isDead()) return;
                for (ItemStack stack : player.getInventory().armor) {
                    if (stack == ItemStack.EMPTY) continue;
                    int percent = Math.round(((stack.getMaxDamage() - stack.getDamage()) * 100f) / (float) stack.getMaxDamage());
                    if (percent <= armorThreshhold.get() && !entityArmorArraylist.containsKey(player)) {
                        if (player == mc.player && !armorIgnoreSelf.get()) {
                            info("Your (highlight)%s(default) %s low durability!", getArmorPieceName(stack), getArmorPieceHasHave(stack));
                        } else if (Friends.get().isFriend(player) && !armorIgnoreFriends.get()) {
                            if (armorMessagesFriends.get())
                                info("(highlight)%s(default)'s (highlight)%s(default) %s low durability!", player.getEntityName(), getArmorPieceName(stack), getArmorPieceHasHave(stack));
                            if (armorPMsFriends.get())
                                mc.player.sendChatMessage("/msg " + player.getEntityName() + " Your " + getArmorPieceName(stack) + " " + getArmorPieceHasHave(stack) + " low durability!");
                        } else if (Enemies.get().isEnemy(player) && !armorIgnoreEnemies.get()) {
                            if (armorMessagesEnemies.get())
                                info("(highlight)%s(default)'s " + getArmorPieceName(stack) + " %s low durability!", player.getEntityName(), getArmorPieceName(stack), getArmorPieceHasHave(stack));
                            if (armorPMsEnemies.get())
                                mc.player.sendChatMessage("/msg " + player.getEntityName() + " Your " + getArmorPieceName(stack) + " " + getArmorPieceHasHave(stack) + " low durability!");
                        } else if (armorIgnoreOthers.get()) {
                            if (armorMessagesOthers.get())
                                info("(highlight)%s(default)'s (highlight)%s(default) %s low durability!", player.getEntityName(), getArmorPieceName(stack), getArmorPieceHasHave(stack));
                            if (armorPMsOthers.get())
                                mc.player.sendChatMessage("/msg " + player.getEntityName() + " Your " + getArmorPieceName(stack) + " " + getArmorPieceHasHave(stack) + " low durability!");
                        }
                        entityArmorArraylist.put(player, player.getInventory().armor.indexOf(stack));
                    }
                    if (!entityArmorArraylist.containsKey(player) || entityArmorArraylist.get(player).intValue() != player.getInventory().armor.indexOf(stack) || percent <= armorThreshhold.get())
                        continue;
                    entityArmorArraylist.remove(player);
                }
                if (!entityArmorArraylist.containsKey(player) || player.getInventory().armor.get(entityArmorArraylist.get(player).intValue()) != ItemStack.EMPTY)
                    continue;
                entityArmorArraylist.remove(player);
            }
        }
    }

    private String getArmorPieceName(ItemStack stack) {
        if (stack.getItem() == Items.DIAMOND_HELMET || stack.getItem() == Items.GOLDEN_HELMET || stack.getItem() == Items.IRON_HELMET || stack.getItem() == Items.CHAINMAIL_HELMET || stack.getItem() == Items.LEATHER_HELMET) {
            return "helmet";
        }
        if (stack.getItem() == Items.DIAMOND_CHESTPLATE || stack.getItem() == Items.GOLDEN_CHESTPLATE || stack.getItem() == Items.IRON_CHESTPLATE || stack.getItem() == Items.CHAINMAIL_CHESTPLATE || stack.getItem() == Items.LEATHER_CHESTPLATE) {
            return "chestplate";
        }
        if (stack.getItem() == Items.DIAMOND_LEGGINGS || stack.getItem() == Items.GOLDEN_LEGGINGS || stack.getItem() == Items.IRON_LEGGINGS || stack.getItem() == Items.CHAINMAIL_LEGGINGS || stack.getItem() == Items.LEATHER_LEGGINGS) {
            return "leggings";
        }
        return "boots";
    }

    private String getArmorPieceHasHave(ItemStack stack) {
        if (stack.getItem() == Items.DIAMOND_HELMET || stack.getItem() == Items.GOLDEN_HELMET || stack.getItem() == Items.IRON_HELMET || stack.getItem() == Items.CHAINMAIL_HELMET || stack.getItem() == Items.LEATHER_HELMET) {
            return "has";
        }
        if (stack.getItem() == Items.DIAMOND_CHESTPLATE || stack.getItem() == Items.GOLDEN_CHESTPLATE || stack.getItem() == Items.IRON_CHESTPLATE || stack.getItem() == Items.CHAINMAIL_CHESTPLATE || stack.getItem() == Items.LEATHER_CHESTPLATE) {
            return "has";
        }
        if (stack.getItem() == Items.DIAMOND_LEGGINGS || stack.getItem() == Items.GOLDEN_LEGGINGS || stack.getItem() == Items.IRON_LEGGINGS || stack.getItem() == Items.CHAINMAIL_LEGGINGS || stack.getItem() == Items.LEATHER_LEGGINGS) {
            return "have";
        }
        return "have";
    }

    private int getChatId(Entity entity) {
        return chatIdMap.computeIntIfAbsent(entity.getUuid(), value -> random.nextInt());
    }

    public enum Event {
        Spawn,
        Despawn,
        Both
    }
}
