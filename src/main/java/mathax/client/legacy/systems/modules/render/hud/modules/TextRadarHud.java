package mathax.client.legacy.systems.modules.render.hud.modules;

import mathax.client.legacy.settings.*;
import mathax.client.legacy.systems.friends.Friends;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.hud.HudElement;
import mathax.client.legacy.systems.modules.render.hud.HudRenderer;
import mathax.client.legacy.utils.entity.EntityUtils;
import mathax.client.legacy.utils.player.PlayerUtils;
import mathax.client.legacy.utils.render.color.Color;
import mathax.client.legacy.utils.render.color.SettingColor;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TextRadarHud extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    public final Setting<SettingColor> playerNameColor = sgGeneral.add(new ColorSetting.Builder()
        .name("player-name-color")
        .description("Color of player names.")
        .defaultValue(new SettingColor(255, 0, 150))
        .build()
    );

    private final Setting<Integer> limit = sgGeneral.add(new IntSetting.Builder()
        .name("limit")
        .description("The max number of players to show.")
        .defaultValue(10)
        .min(1)
        .sliderMin(1).sliderMax(20)
        .build()
    );

    private final Setting<Boolean> health = sgGeneral.add(new BoolSetting.Builder()
        .name("health")
        .description("Shows the health of player next to their name.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> ping = sgGeneral.add(new BoolSetting.Builder()
        .name("ping")
        .description("Shows the ping of player next to their name.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> distance = sgGeneral.add(new BoolSetting.Builder()
        .name("distance")
        .description("Shows the distance to the player next to their name.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> friends = sgGeneral.add(new BoolSetting.Builder()
        .name("display-friends")
        .description("Whether to show friends or not.")
        .defaultValue(true)
        .build()
    );

    private final List<AbstractClientPlayerEntity> players = new ArrayList<>();

    public TextRadarHud(HUD hud) {
        super(hud, "player-info", "Displays players in your visual range.", false);
    }

    @Override
    public void update(HudRenderer renderer) {
        double width = renderer.textWidth("Players:");
        double height = renderer.textHeight();

        if (mc.world == null) {
            box.setSize(width, height);
            return;
        }

        for (PlayerEntity entity : getPlayers()) {
            if (entity.equals(mc.player)) continue;
            if (!friends.get() && Friends.get().isFriend(entity)) continue;

            String text = entity.getEntityName();
            if (health.get() || ping.get() || distance.get()) text += String.format("-");
            if (health.get()) text += String.format("%s", Math.round(entity.getHealth() + entity.getAbsorptionAmount()));
            if (ping.get()) text += String.format("[%sms]", Math.round(EntityUtils.getPing(entity)));
            if (distance.get()) text += String.format("(%sm)", Math.round(mc.getCameraEntity().distanceTo(entity)));

            width = Math.max(width, renderer.textWidth(text));
            height += renderer.textHeight() + 2;
        }

        box.setSize(width, height);
    }

    private final Color RED = new Color(255, 25, 25);
    private final Color AMBER = new Color(255, 105, 25);
    private final Color GREEN = new Color(25, 252, 25);
    private final Color BLUE = new Color(20, 170, 170);

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        renderer.text("Players:", x, y, hud.primaryColor.get());

        if (mc.world == null) return;

        for (PlayerEntity entity : getPlayers()) {
            if (entity.equals(mc.player)) continue;
            if (!friends.get() && Friends.get().isFriend(entity)) continue;

            x = box.getX();
            y += renderer.textHeight() + 2;

            String text = entity.getEntityName();
            Color color = PlayerUtils.getPlayerColor(entity, playerNameColor.get());

            renderer.text(text, x, y, color);

            if (health.get() || ping.get() || distance.get()) {
                x += renderer.textWidth(text + " ");

                text = String.format("-");
                color = hud.secondaryColor.get();

                renderer.text(text, x, y, color);
            }
            if (health.get()) {
                x += renderer.textWidth(text + " ");

                text = String.format("%s", Math.round(entity.getHealth() + entity.getAbsorptionAmount()));
                double healthPercentage = Math.round(entity.getHealth() + entity.getAbsorptionAmount()) / (entity.getMaxHealth() + entity.getAbsorptionAmount());
                if (healthPercentage <= 0.333) color = RED;
                else if (healthPercentage <= 0.666) color = AMBER;
                else color = GREEN;

                renderer.text(text, x, y, color);
            }
            if (ping.get()) {
                x += renderer.textWidth(text + " ");

                text = String.format("[%sms]", Math.round(EntityUtils.getPing(entity)));
                color = BLUE;

                renderer.text(text, x, y, color);
            }
            if (distance.get()) {
                x += renderer.textWidth(text + " ");

                text = String.format("(%sm)", Math.round(mc.getCameraEntity().distanceTo(entity)));
                color = hud.secondaryColor.get();

                renderer.text(text, x, y, color);
            }
        }
    }

    private List<AbstractClientPlayerEntity> getPlayers() {
        players.clear();
        players.addAll(mc.world.getPlayers());
        if (players.size() > limit.get()) players.subList(limit.get() - 1, players.size() - 1).clear();
        players.sort(Comparator.comparingDouble(e -> e.distanceTo(mc.getCameraEntity())));

        return players;
    }
}
