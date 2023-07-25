package envy.client.systems.modules.player;

import envy.client.eventbus.EventHandler;
import envy.client.events.game.GameJoinedEvent;
import envy.client.events.game.OpenScreenEvent;
import envy.client.events.world.TickEvent;
import envy.client.settings.BoolSetting;
import envy.client.settings.Setting;
import envy.client.settings.SettingGroup;
import envy.client.systems.modules.Categories;
import envy.client.systems.modules.Module;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.item.Items;

/*/--------------------------------------------------------------------------------------------------------------/*/
/*/ Used from Meteor Rejects                                                                                     /*/
/*/ https://github.com/AntiCope/meteor-rejects/blob/master/src/main/java/anticope/rejects/modules/GhostMode.java /*/
/*/--------------------------------------------------------------------------------------------------------------/*/

public class Ghost extends Module {
    private boolean active = false;

	private int x = 0;
	private int z = 0;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // General

    private final Setting<Boolean> fullFood = sgGeneral.add(new BoolSetting.Builder()
    	.name("full-food")
    	.description("Sets the food level client side to max so u can sprint.")
    	.defaultValue(true)
    	.build()
    );

    public Ghost() {
    	super(Categories.Player, Items.SKELETON_SKULL, "ghost", "Allows you to move after death.");
    }

    @Override
    public void onDeactivate() {
        active = false;
        warning("You are no longer in ghost mode.");
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        active = false;
    }

    @EventHandler
    private void onTick(final TickEvent.Pre event) {
    	if (!active) return;
        if (mc.player.getHealth() < 1f) mc.player.setHealth(20f);
        if (fullFood.get() && mc.player.getHungerManager().getFoodLevel() < 20) mc.player.getHungerManager().setFoodLevel(20);
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (event.screen instanceof DeathScreen) {
            event.cancel();

            if (!active) {
                active = true;
                info("You are now in ghost mode.");
            }
        }
    }
}
