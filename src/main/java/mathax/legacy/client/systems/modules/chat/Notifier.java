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
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.systems.friends.Friends;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.utils.entity.fakeplayer.FakePlayerEntity;
import mathax.legacy.client.utils.player.ArmorUtils;
import mathax.legacy.client.utils.player.ChatUtils;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static mathax.legacy.client.utils.player.ChatUtils.formatCoords;

/*/                                                                                                              /*/
/*/ Burrow & Armor taken from Orion Meteor Addon and modified by Matejko06                                       /*/
/*/ https://github.com/GhostTypes/orion/blob/main/src/main/java/me/ghosttypes/orion/modules/main/AnchorAura.java /*/
/*/                                                                                                              /*/

public class Notifier extends Module {
    private static String entityName;
    private static String entityPos;

    private boolean alertedHelm;
    private boolean alertedChest;
    private boolean alertedLegs;
    private boolean alertedBoots;

    private int burrowMsgWait;

    public static List<PlayerEntity> burrowedPlayers = new ArrayList<>();

    private final Object2IntMap<UUID> totemPopMap = new Object2IntOpenHashMap<>();
    private final Object2IntMap<UUID> chatIdMap = new Object2IntOpenHashMap<>();

    private final Random random = new Random();

    private final SettingGroup sgTotemPops = settings.createGroup("Totem Pops");
    private final SettingGroup sgVisualRange = settings.createGroup("Visual Range");
    private final SettingGroup sgBurrow = settings.createGroup("Burrow");
    private final SettingGroup sgArmor = settings.createGroup("Armor");

    // Totem Pops

    private final Setting<Boolean> totemPops = sgTotemPops.add(new BoolSetting.Builder()
        .name("enabled")
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
        .name("enabled")
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

    // Burrow

    private final Setting<Boolean> burrow = sgBurrow.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Notifies you when someone burrows in your render distance.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> range = sgBurrow.add(new IntSetting.Builder()
        .name("range")
        .description("How far away from you to check for burrowed players.")
        .defaultValue(3)
        .min(0)
        .sliderMax(15)
        .build()
    );

    // Burrow

    private final Setting<Boolean> armor = sgArmor.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Notifies you when your armor is low.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> threshold = sgArmor.add(new DoubleSetting.Builder()
        .name("durability")
        .description("How low an armor piece needs to be to alert you.")
        .defaultValue(10)
        .min(1)
        .sliderMin(1)
        .sliderMax(100)
        .max(100)
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

    // Totem Pops & Burrow

    @Override
    public void onActivate() {
        totemPopMap.clear();
        chatIdMap.clear();
        alertedHelm = false;
        alertedChest = false;
        alertedLegs = false;
        alertedBoots = false;
        burrowMsgWait = 0;
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
        if (armor.get()) {
            Iterable<ItemStack> armorPieces = mc.player.getArmorItems();
            for (ItemStack armorPiece : armorPieces){
                if (ArmorUtils.checkThreshold(armorPiece, threshold.get())) {
                    if (ArmorUtils.isHelmet(armorPiece) && !alertedHelm) {
                        warning("Your helmet has low durability!");
                        alertedHelm = true;
                    }
                    if (ArmorUtils.isChestplate(armorPiece) && !alertedChest) {
                        warning("Your chestplate has low durability!");
                        alertedChest = true;
                    }
                    if (ArmorUtils.isLegs(armorPiece) && !alertedLegs) {
                        warning("Your leggings habe low durability!");
                        alertedLegs = true;
                    }
                    if (ArmorUtils.isBoots(armorPiece) && !alertedBoots) {
                        warning("Your boots have low durability!");
                        alertedBoots = true;
                    }
                }
                if (!ArmorUtils.checkThreshold(armorPiece, threshold.get())) {
                    if (ArmorUtils.isHelmet(armorPiece) && alertedHelm) alertedHelm = false;
                    if (ArmorUtils.isChestplate(armorPiece) && alertedChest) alertedChest = false;
                    if (ArmorUtils.isLegs(armorPiece) && alertedLegs) alertedLegs = false;
                    if (ArmorUtils.isBoots(armorPiece) && alertedBoots) alertedBoots = false;
                }
            }
        }

        if (burrow.get()) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (isBurrowValid(player)) {
                    burrowedPlayers.add(player);
                    warning("(highlight)" + player.getEntityName() + " (default)is burrowed!");
                }

                if (burrowedPlayers.contains(player) && !PlayerUtils.isBurrowed(player, true)) {
                    burrowedPlayers.remove(player);
                    warning("(highlight)" + player.getEntityName() + " (default)is no longer burrowed.");
                }
            }
        }

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
    }

    private boolean isBurrowValid(PlayerEntity p) {
        if (p == mc.player) return false;
        return mc.player.distanceTo(p) <= range.get() && !burrowedPlayers.contains(p) && PlayerUtils.isBurrowed(p, true) && !PlayerUtils.isPlayerMoving(p);
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
