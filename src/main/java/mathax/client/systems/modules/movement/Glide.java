package mathax.client.systems.modules.movement;

import mathax.client.eventbus.EventHandler;
import mathax.client.events.world.TickEvent;
import mathax.client.mixininterface.IVec3d;
import mathax.client.settings.BoolSetting;
import mathax.client.settings.DoubleSetting;
import mathax.client.settings.EnumSetting;
import mathax.client.settings.Setting;
import mathax.client.settings.SettingGroup;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.movement.Flight.Mode;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class Glide extends Module {
	
	int x = 0;
	int z = 0;
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
	        .name("mode")
	        .description("The mode for Glide.")
	        .defaultValue(Mode.Normal)
	        .build()
	    );
	
	private final Setting<Double> fallspeed = sgGeneral.add(new DoubleSetting.Builder()
	        .name("falling speed")
	        .description("How fast to fall?")
	        .defaultValue(0.01)
	        .min(0.0000001)
	        .sliderRange(0.0000001, 0.6)
	        .build()
	    );
	
	private final Setting<Boolean> defstrafe = sgGeneral.add(new BoolSetting.Builder()
	        .name("default strafe speed")
	        .description("Does not modifies you strafing speed. Can bypass some servers")
	        .defaultValue(true)
	        .build()
	    );

	
	private final Setting<Double> strafespeed = sgGeneral.add(new DoubleSetting.Builder()
	        .name("strafing speed")
	        .description("How fast to strafe?")
	        .defaultValue(0.2)
	        .min(0.001)
	        .sliderRange(0.050, 1.0)
	        .build()
	    );
	
    public Glide() {
        super(Categories.Movement, Items.FEATHER, "glide", "makes you glide slowly when falling");
    }

    @EventHandler
    private void onTick(final TickEvent.Post event) {
    	
    	if(defstrafe.get() == false)
        {
        	mc.player.airStrafingSpeed = strafespeed.get().floatValue();
        }
    	
        if(mode.get() == Mode.Vertical)
        {
        	if(mc.player.isOnGround() || mc.player.isTouchingWater() || mc.player.isInLava()
        			|| mc.player.isClimbing())
        			return;
        	
        	x = (int) mc.player.getVelocity().x;
        	z = (int) mc.player.getVelocity().z;
        	
            mc.player.getAbilities().flying = false; //made flying buggy when in creative mode
            
            mc.player.setVelocity(x, fallspeed.get() * -1, z);
            
        }
        else if(mode.get() == Mode.Normal)
        {
        	if(mc.player.isOnGround() || mc.player.isTouchingWater() || mc.player.isInLava()
        			|| mc.player.isClimbing())
        			return;
        	
        	Vec3d vel = mc.player.getVelocity();
        	
        	mc.player.setVelocity(vel.x, Math.max(vel.y, -fallspeed.get()), vel.z); //applies velocity to the player
            
        }
        
        //todo list: FastFall module
    }
    
    public enum Mode {
        Normal("Normal"),
        Vertical("Vertical");

        private final String title;

        Mode(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
