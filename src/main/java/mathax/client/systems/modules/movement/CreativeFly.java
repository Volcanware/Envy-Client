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
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class CreativeFly extends Module {
	
	int x = 0;
	int z = 0;
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	private final Setting<Boolean> instafly = sgGeneral.add(new BoolSetting.Builder()
	        .name("fly on enable")
	        .description("makes you automatically start flying after enabling this module")
	        .defaultValue(true)
	        .build()
	    );
	
	private final Setting<Boolean> tickenabler = sgGeneral.add(new BoolSetting.Builder()
	        .name("update every tick")
	        .description("updates your flying ability every tick, enable only if flying doesnt work without this turned on")
	        .defaultValue(true)
	        .build()
	    );
	
    public CreativeFly() {
        super(Categories.Movement, Items.CHAINMAIL_BOOTS, "creative fly", "makes you can fly like in creative mode");
    }
    
    @Override
    public void onActivate() {
    	mc.player.getAbilities().allowFlying = true;
    	
    	if(instafly.get() == true)
    	{
    		mc.player.getAbilities().flying = true;
    	}
    }

    @Override
    public void onDeactivate() {
    	mc.player.getAbilities().allowFlying = false;
    }

    @EventHandler
    private void onTick(final TickEvent.Pre event) {
    	
    	if(tickenabler.get() == true)
    	{
    		mc.player.getAbilities().allowFlying = true;
    	}
    	
    }
    
    
}
