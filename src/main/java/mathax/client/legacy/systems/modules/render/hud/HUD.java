package mathax.client.legacy.systems.modules.render.hud;

import mathax.client.legacy.events.render.Render2DEvent;
import mathax.client.legacy.gui.GuiTheme;
import mathax.client.legacy.gui.screens.HudElementScreen;
import mathax.client.legacy.gui.tabs.builtin.HudTab;
import mathax.client.legacy.gui.widgets.WWidget;
import mathax.client.legacy.gui.widgets.containers.WHorizontalList;
import mathax.client.legacy.gui.widgets.pressable.WButton;
import mathax.client.legacy.settings.*;
import mathax.client.legacy.systems.modules.Modules;
import mathax.client.legacy.systems.modules.render.hud.modules.*;
import mathax.client.legacy.systems.modules.Categories;
import mathax.client.legacy.systems.modules.Module;
import mathax.client.legacy.utils.render.AlignmentX;
import mathax.client.legacy.utils.render.AlignmentY;
import mathax.client.legacy.utils.render.color.SettingColor;
import mathax.client.legacy.bus.EventHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

import java.util.ArrayList;
import java.util.List;

public class HUD extends Module {
    private static final HudRenderer RENDERER = new HudRenderer();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgEditor = settings.createGroup("Editor");
    private final SettingGroup sgVanilla = settings.createGroup("Vanilla HUD");

    // General

    public final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .description("Scale of the HUD.")
        .defaultValue(1)
        .min(0.75)
        .sliderMin(0.75)
        .sliderMax(4)
        .build()
    );

    public final Setting<SettingColor> primaryColor = sgGeneral.add(new ColorSetting.Builder()
        .name("primary-color")
        .description("Primary color of text.")
        .defaultValue(new SettingColor(225, 75, 100))
        .build()
    );

    public final Setting<SettingColor> secondaryColor = sgGeneral.add(new ColorSetting.Builder()
        .name("secondary-color")
        .description("Secondary color of text.")
        .defaultValue(new SettingColor(175, 175, 175))
        .build()
    );

    // Editor

    public final Setting<Integer> snappingRange = sgEditor.add(new IntSetting.Builder()
        .name("snapping-range")
        .description("Snapping range in editor.")
        .defaultValue(6)
        .build()
    );

    // Improve vanilla HUD

    public final Setting<Boolean> mountHud = sgVanilla.add(new BoolSetting.Builder()
        .name("mount-hud")
        .description("Display xp bar and hunger when riding.")
        .defaultValue(true)
        .build()
    );

    public final List<HudElement> elements = new ArrayList<>();

    private final HudElementLayer mainInfo, moduleInfo, breakingLooking, coords, lag, modules, enemy, invPot, radar, crosshair, crosshair2, crosshair3, crosshair4;

    public final Runnable reset = () -> {
        align();
        elements.forEach(element -> {
            Modules.get().get(HUD.class).elements.get(0).toggle(false);                            // WATERMARK
            Modules.get().get(HUD.class).elements.get(1).toggle(false);                            // WELCOME
            Modules.get().get(HUD.class).elements.get(2).toggle(false);                            // FPS
            Modules.get().get(HUD.class).elements.get(3).toggle(false);                            // PING
            Modules.get().get(HUD.class).elements.get(4).toggle(false);                            // TPS
            Modules.get().get(HUD.class).elements.get(5).toggle(false);                            // SPEED
            Modules.get().get(HUD.class).elements.get(6).toggle(false);                            // SERVER
            Modules.get().get(HUD.class).elements.get(7).toggle(false);                            // SERVER BRAND
            Modules.get().get(HUD.class).elements.get(8).toggle(false);                            // DURABILITY
            //Modules.get().get(HUD.class).elements.get(9).toggle(false);                            // BIOME
            Modules.get().get(HUD.class).elements.get(10).toggle(false);                            // PLAYER MODEL
            Modules.get().get(HUD.class).elements.get(11).toggle(false);                            // MODULE INFO
            Modules.get().get(HUD.class).elements.get(12).toggle(false);                            // LOOKING AT
            Modules.get().get(HUD.class).elements.get(13).toggle(false);                            // BREAKING
            Modules.get().get(HUD.class).elements.get(14).toggle(false);                            // POSITION
            //Modules.get().get(HUD.class).elements.get(15).toggle(false);                            // ROTATION
            Modules.get().get(HUD.class).elements.get(16).toggle(false);                            // LAG NOTIFIER
            Modules.get().get(HUD.class).elements.get(17).toggle(false);                            // ACTIVE MODULES
            Modules.get().get(HUD.class).elements.get(18).toggle(false);                            // COMBAT HUD
            Modules.get().get(HUD.class).elements.get(19).toggle(false);                            // INVENTORY VIEWER
            Modules.get().get(HUD.class).elements.get(20).toggle(false);                            // CONTAINER VIEWER
            Modules.get().get(HUD.class).elements.get(21).toggle(false);                            // POTION
            Modules.get().get(HUD.class).elements.get(22).toggle(false);                            // DATE
            Modules.get().get(HUD.class).elements.get(23).toggle(false);                            // REAL TIME
            Modules.get().get(HUD.class).elements.get(24).toggle(false);                            // IN-GAME TIME
            Modules.get().get(HUD.class).elements.get(25).toggle(false);                            // TEXT RADAR
            Modules.get().get(HUD.class).elements.get(26).toggle(false);                            // TOTEM
            Modules.get().get(HUD.class).elements.get(27).toggle(false);                            // HOLE
            Modules.get().get(HUD.class).elements.get(28).toggle(false);                            // COMPASS
            Modules.get().get(HUD.class).elements.get(29).toggle(false);                            // ARMOR
            element.settings.forEach(group -> group.forEach(Setting::reset));
        });
    };

    public HudElement get(RegistryKey<HudElement> key) {
        return null;
    }

    public HudElement get(Identifier id) {
        return null;
    }

    public HUD() {
        super(Categories.Render, "HUD", "In game overlay.");

        // MAIN INFO
        mainInfo = new HudElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Top, 2, 2);
        // Modules
        mainInfo.add(new WatermarkHud(this));
        mainInfo.add(new WelcomeHud(this));
        mainInfo.add(new FpsHud(this));
        mainInfo.add(new PingHud(this));
        mainInfo.add(new TpsHud(this));
        mainInfo.add(new SpeedHud(this));
        mainInfo.add(new ServerHud(this));
        mainInfo.add(new ServerBrandHud(this));
        mainInfo.add(new DurabilityHud(this));
        mainInfo.add(new BiomeHud(this));
        mainInfo.add(new PlayerModelHud(this));

        // MODULE INFO
        moduleInfo = new HudElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Top, 100, 225);
        // Modules
        moduleInfo.add(new ModuleInfoHud(this));

        // LOOKING AT & BREAKING
        breakingLooking = new HudElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Bottom, -3, -215);
        // Modules
        breakingLooking.add(new LookingAtHud(this));
        breakingLooking.add(new BreakingBlockHud(this));

        // COORDS
        coords = new HudElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Bottom, 2, 2);
        // Modules
        coords.add(new PositionHud(this));
        coords.add(new RotationHud(this));

        // LAG
        lag = new HudElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Top, 0, 2);
        // Modules
        lag.add(new LagNotifierHud(this));

        // MODULES
        modules = new HudElementLayer(RENDERER, elements, AlignmentX.Right, AlignmentY.Bottom, 2, 2);
        // Modules
        modules.add(new ActiveModulesHud(this));

        // ENEMY
        enemy = new HudElementLayer(RENDERER, elements, AlignmentX.Right, AlignmentY.Center, 2, -100);
        // Modules
        enemy.add(new CombatHud(this));

        // INVPOT
        invPot = new HudElementLayer(RENDERER, elements, AlignmentX.Right, AlignmentY.Top, 2, 2);
        // Modules
        invPot.add(new InventoryViewerHud(this));
        invPot.add(new ContainerViewerHud(this));
        invPot.add(new PotionTimersHud(this));
        invPot.add(new DateHud(this));
        invPot.add(new RealTimeHud(this));
        invPot.add(new InGameTimeHud(this));

        // TEXT RADAR
        radar = new HudElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Center, 2, 100);
        // Modules
        radar.add(new TextRadarHud(this));

        // CROSSHAIR
        crosshair = new HudElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 0);
        // Modules
        crosshair.add(new TotemHud(this));

        // CROSSHAIR 2
        crosshair2 = new HudElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 0);
        // Modules
        crosshair2.add(new HoleHud(this));

        // CROSSHAIR 3
        crosshair3 = new HudElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 0);
        // Modules
        crosshair3.add(new CompassHud(this));

        // CROSSHAIR 4
        crosshair4 = new HudElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 65);
        // Modules
        crosshair4.add(new ArmorHud(this));

        align();
    }

    private void align() {
        RENDERER.begin(scale.get(), 0, true);

        mainInfo.align();
        moduleInfo.align();
        breakingLooking.align();
        coords.align();
        lag.align();
        modules.align();
        enemy.align();
        invPot.align();
        radar.align();
        crosshair.align();
        crosshair2.align();
        crosshair3.align();
        crosshair4.align();

        RENDERER.end();
    }

    @EventHandler
    public void onRender(Render2DEvent event) {
        if (mc.options.debugEnabled || mc.options.hudHidden) return;

        RENDERER.begin(scale.get(), event.tickDelta, false);

        for (HudElement element : elements) {
            if (element.active || HudTab.INSTANCE.isScreen(mc.currentScreen) || mc.currentScreen instanceof HudElementScreen) {
                element.update(RENDERER);
                element.render(RENDERER);
            }
        }

        RENDERER.end();
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WHorizontalList list = theme.horizontalList();

        WButton reset = list.add(theme.button("Reset")).widget();
        reset.action = this.reset;
        list.add(theme.label("Resets positions (do this after changing scale)."));

        /*WButton editor = list.add(theme.button("Editor")).widget();
        editor.action =
        list.add(theme.label("Opens the editor of HUD modules."));*/

        return list;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = super.toTag();

        NbtList modulesTag = new NbtList();
        for (HudElement module : elements) modulesTag.add(module.toTag());
        tag.put("modules", modulesTag);

        return tag;
    }

    @Override
    public Module fromTag(NbtCompound tag) {
        if (tag.contains("modules")) {
            NbtList modulesTag = tag.getList("modules", 10);

            for (NbtElement t : modulesTag) {
                NbtCompound moduleTag = (NbtCompound) t;

                HudElement module = getModule(moduleTag.getString("name"));
                if (module != null) module.fromTag(moduleTag);
            }
        }

        return super.fromTag(tag);
    }

    private HudElement getModule(String name) {
        for (HudElement module : elements) {
            if (module.name.equals(name)) return module;
        }

        return null;
    }

    public boolean mountHud() {
        return isActive() && mountHud.get();
    }
}
