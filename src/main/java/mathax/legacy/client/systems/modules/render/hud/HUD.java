package mathax.legacy.client.systems.modules.render.hud;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.render.Render2DEvent;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.screens.HudElementScreen;
import mathax.legacy.client.gui.tabs.builtin.HudTab;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.gui.widgets.containers.WHorizontalList;
import mathax.legacy.client.gui.widgets.pressable.WButton;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.render.AlignmentX;
import mathax.legacy.client.utils.render.AlignmentY;
import mathax.legacy.client.utils.render.color.SettingColor;
import mathax.legacy.client.bus.EventHandler;
import mathax.legacy.client.settings.*;
import mathax.legacy.client.systems.modules.render.hud.modules.*;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

import java.util.*;

public class HUD extends Module {
    private static final HUDRenderer RENDERER = new HUDRenderer();

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
        .defaultValue(new SettingColor(MatHaxLegacy.INSTANCE.MATHAX_COLOR.r, MatHaxLegacy.INSTANCE.MATHAX_COLOR.g, MatHaxLegacy.INSTANCE.MATHAX_COLOR.b))
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

    public final List<HUDElement> elements = new ArrayList<>();
    private final HUDElementLayer mainInfo, moduleInfo, breakingLooking, coords, lag, modules, invPot, time, radar, crosshair, crosshair2, crosshair3, crosshair4;

    public final Runnable reset = () -> {
        align();
        elements.forEach(element -> {
            element.active = element.defaultActive;
            element.settings.forEach(group -> group.forEach(Setting::reset));
        });
    };

    public HUDElement get(RegistryKey<HUDElement> key) {
        return null;
    }

    public HUDElement get(Identifier id) {
        return null;
    }

    public HUD() {
        super(Categories.Render, Items.GLASS, "HUD", "In game overlay.");

        // MAIN INFO
        mainInfo = new HUDElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Top, 2, 2);
        // Modules
        mainInfo.add(new WatermarkHUD(this));
        mainInfo.add(new WelcomeHUD(this));
        mainInfo.add(new FPSHUD(this));
        mainInfo.add(new PingHUD(this));
        mainInfo.add(new TPSHUD(this));
        mainInfo.add(new SpeedHUD(this));
        mainInfo.add(new ServerHUD(this));
        mainInfo.add(new ServerBrandHUD(this));
        mainInfo.add(new DurabilityHUD(this));
        mainInfo.add(new BiomeHUD(this));
        mainInfo.add(new PlayerModelHUD(this));
        mainInfo.add(new CombatInfoHUD(this));

        // MODULE INFO
        moduleInfo = new HUDElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Top, 100, 215);
        // Modules
        moduleInfo.add(new ModuleInfoHUD(this));

        // LOOKING AT & BREAKING
        breakingLooking = new HUDElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, -215);
        // Modules
        breakingLooking.add(new LookingAtHUD(this));
        breakingLooking.add(new BreakingBlockHUD(this));

        // COORDS
        coords = new HUDElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Bottom, 2, 2);
        // Modules
        coords.add(new PositionHUD(this));
        coords.add(new RotationHUD(this));

        // LAG
        lag = new HUDElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Top, 0, 2);
        // Modules
        lag.add(new LagNotifierHUD(this));

        // MODULES
        modules = new HUDElementLayer(RENDERER, elements, AlignmentX.Right, AlignmentY.Bottom, 2, 2);
        // Modules
        modules.add(new ActiveModulesHUD(this));

        // INVPOT
        invPot = new HUDElementLayer(RENDERER, elements, AlignmentX.Right, AlignmentY.Top, 2, 2);
        // Modules
        invPot.add(new InventoryViewerHUD(this));
        invPot.add(new ContainerViewerHUD(this));
        invPot.add(new PotionTimersHUD(this));

        // TIME
        time = new HUDElementLayer(RENDERER, elements, AlignmentX.Right, AlignmentY.Center, 2, 5);
        // Modules
        time.add(new InGameTimeHUD(this));
        time.add(new RealTimeHUD(this));
        time.add(new DateHUD(this));

        // TEXT RADAR
        radar = new HUDElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Center, 2, 100);
        // Modules
        radar.add(new TextRadarHUD(this));

        // CROSSHAIR
        crosshair = new HUDElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 0);
        // Modules
        crosshair.add(new TotemHUD(this));
        crosshair.add(new CrystalHUD(this));
        crosshair.add(new EGapHUD(this));
        crosshair.add(new XPBottleHUD(this));
        crosshair.add(new BedHUD(this));

        // CROSSHAIR 2
        crosshair2 = new HUDElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 0);
        // Modules
        crosshair2.add(new HoleHUD(this));

        // CROSSHAIR 3
        crosshair3 = new HUDElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 0);
        // Modules
        crosshair3.add(new CompassHUD(this));

        // CROSSHAIR 4
        crosshair4 = new HUDElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 65);
        // Modules
        crosshair4.add(new ArmorHUD(this));

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
        invPot.align();
        time.align();
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

        for (HUDElement element : elements) {
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
        for (HUDElement module : elements) modulesTag.add(module.toTag());
        tag.put("modules", modulesTag);

        return tag;
    }

    @Override
    public Module fromTag(NbtCompound tag) {
        if (tag.contains("modules")) {
            NbtList modulesTag = tag.getList("modules", 10);

            for (NbtElement t : modulesTag) {
                NbtCompound moduleTag = (NbtCompound) t;

                HUDElement module = getModule(moduleTag.getString("name"));
                if (module != null) module.fromTag(moduleTag);
            }
        }

        return super.fromTag(tag);
    }

    private HUDElement getModule(String name) {
        for (HUDElement module : elements) {
            if (module.name.equals(name)) return module;
        }

        return null;
    }

    public boolean mountHud() {
        return isActive() && mountHud.get();
    }
}
