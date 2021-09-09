package mathax.legacy.client.systems.modules.render;

import mathax.legacy.client.events.render.RenderBossBarEvent;
import mathax.legacy.client.settings.BoolSetting;
import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.WeakHashMap;

public class BossStack extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> stack = sgGeneral.add(new BoolSetting.Builder()
        .name("stack")
        .description("Stacks boss bars and adds a counter to the text.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> hideName = sgGeneral.add(new BoolSetting.Builder()
        .name("hide-name")
        .description("Hides the names of boss bars.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> spacing = sgGeneral.add(new DoubleSetting.Builder()
        .name("bar-spacing")
        .description("The spacing reduction between each boss bar.")
        .defaultValue(10)
        .min(0)
        .sliderMax(10)
        .build()
    );

    public static final WeakHashMap<ClientBossBar, Integer> barMap = new WeakHashMap<>();

    public BossStack() {
        super(Categories.Render, Items.NETHER_STAR, "boss-stack", "Stacks boss bars to make your HUD less cluttered.");
    }

    @EventHandler
    private void onFetchText(RenderBossBarEvent.BossText event) {
        if (hideName.get()) {
            event.name = Text.of("");
            return;
        } else if (barMap.isEmpty() || !stack.get()) return;
        ClientBossBar bar = event.bossBar;
        Integer integer = barMap.get(bar);
        barMap.remove(bar);
        if (integer != null && !hideName.get()) event.name = event.name.copy().append(" x" + integer);
    }

    @EventHandler
    private void onSpaceBars(RenderBossBarEvent.BossSpacing event) {
        event.spacing = spacing.get().intValue();
    }

    @EventHandler
    private void onGetBars(RenderBossBarEvent.BossIterator event) {
        if (stack.get()) {
            HashMap<String, ClientBossBar> chosenBarMap = new HashMap<>();
            event.iterator.forEachRemaining(bar -> {
                String name = bar.getName().asString();
                if (chosenBarMap.containsKey(name)) {
                    barMap.compute(chosenBarMap.get(name), (clientBossBar, integer) -> (integer == null) ? 2 : integer + 1);
                } else {
                    chosenBarMap.put(name, bar);
                }
            });
            event.iterator = chosenBarMap.values().iterator();
        }
    }
}
