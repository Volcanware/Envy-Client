package mathax.client.systems.modules.render.hud;

import mathax.client.MatHax;
import mathax.client.eventbus.EventHandler;
import mathax.client.events.render.Render2DEvent;
import mathax.client.gui.GuiTheme;
import mathax.client.gui.GuiThemes;
import mathax.client.gui.renderer.GuiRenderer;
import mathax.client.gui.screens.HudElementScreen;
import mathax.client.gui.tabs.Tab;
import mathax.client.gui.tabs.Tabs;
import mathax.client.gui.tabs.builtin.HudTab;
import mathax.client.gui.widgets.WWidget;
import mathax.client.gui.widgets.containers.WTable;
import mathax.client.gui.widgets.pressable.WButton;
import mathax.client.settings.*;
import mathax.client.systems.modules.Categories;
import mathax.client.systems.modules.Module;
import mathax.client.systems.modules.render.hud.modules.*;
import mathax.client.utils.render.AlignmentX;
import mathax.client.utils.render.AlignmentY;
import mathax.client.utils.render.color.SettingColor;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

import java.util.*;

public class HUD extends Module {
    private static final HudRenderer RENDERER = new HudRenderer();

    public final List<HudElement> elements = new ArrayList<>();
    private final HudElementLayer mainInfo, moduleInfo, breakingLooking, coords, lag, modules, invPot, binds, radar, itemsArmor, crosshair, crosshair2;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgEditor = settings.createGroup("Editor");

    // General

    public final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .description("Scale of the HUD.")
        .defaultValue(1)
        .min(0.75)
        .sliderMin(0.75)
        .sliderMax(5)
        .build()
    );

    public final Setting<SettingColor> primaryColor = sgGeneral.add(new ColorSetting.Builder()
        .name("primary-color")
        .description("Primary color of text.")
        .defaultValue(new SettingColor(MatHax.INSTANCE.MATHAX_COLOR.r, MatHax.INSTANCE.MATHAX_COLOR.g, MatHax.INSTANCE.MATHAX_COLOR.b))
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

    // Buttons

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WTable table = theme.table();

        WButton openEditor = table.add(theme.button(GuiRenderer.EDIT)).widget();
        openEditor.action = this::openHudEditor;
        table.add(theme.label("Opens HUD editor."));
        table.row();

        WButton reset = table.add(theme.button(GuiRenderer.RESET)).widget();
        reset.action = this.reset;
        table.add(theme.label("Resets positions (do this after changing scale)."));
        table.row();

        return table;
    }

    public HUD() {
        super(Categories.Render, Items.GLASS, "hud", "In game overlay.");

        // MAIN INFO
        mainInfo = new HudElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Top, 2, 2);
        // Modules
        mainInfo.add(new WatermarkHud(this));
        mainInfo.add(new WelcomeHud(this));
        mainInfo.add(new FPSHud(this));
        mainInfo.add(new PingHud(this));
        mainInfo.add(new TPSHud(this));
        mainInfo.add(new SpeedHud(this));
        mainInfo.add(new ServerHud(this));
        mainInfo.add(new ServerBrandHud(this));
        mainInfo.add(new DurabilityHud(this));
        mainInfo.add(new BiomeHud(this));
        mainInfo.add(new PlayerModelHud(this));
        mainInfo.add(new DateHud(this));
        mainInfo.add(new RealTimeHud(this));
        mainInfo.add(new GameTimeHud(this));
        mainInfo.add(new MusicHud(this));

        // MODULE INFO
        moduleInfo = new HudElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Top, 100, 200);
        // Modules
        moduleInfo.add(new ModuleInfoHud(this));

        // LOOKING AT & BREAKING
        breakingLooking = new HudElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 215);
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
        lag.add(new BigRatHud(this));

        // MODULES
        modules = new HudElementLayer(RENDERER, elements, AlignmentX.Right, AlignmentY.Bottom, 2, 2);
        // Modules
        modules.add(new ActiveModulesHud(this));

        // INVPOT
        invPot = new HudElementLayer(RENDERER, elements, AlignmentX.Right, AlignmentY.Top, 2, 2);
        // Modules
        invPot.add(new InventoryViewerHud(this));
        invPot.add(new ContainerViewerHud(this));
        invPot.add(new PotionTimersHud(this));

        // BINDS
        binds = new HudElementLayer(RENDERER, elements, AlignmentX.Right, AlignmentY.Center, 2, 5);
        // Modules
        binds.add(new VisualBinds(this));

        // TEXT RADAR
        radar = new HudElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Center, 2, 100);
        // Modules
        radar.add(new CombatInfoHud(this));
        radar.add(new TextRadarHud(this));

        // ITEMS & ARMOR
        itemsArmor = new HudElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, -150, -50);
        // Modules
        itemsArmor.add(new TotemHud(this));
        itemsArmor.add(new CrystalHud(this));
        itemsArmor.add(new EGapHud(this));
        itemsArmor.add(new XPBottleHud(this));
        itemsArmor.add(new ObsidianHud(this));
        itemsArmor.add(new BedHud(this));
        itemsArmor.add(new ArmorHud(this));

        // CROSSHAIR
        crosshair = new HudElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 0);
        // Modules
        crosshair.add(new HoleHud(this));

        // CROSSHAIR 2
        crosshair2 = new HudElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 0);
        // Modules
        crosshair2.add(new CompassHud(this));

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
        binds.align();
        radar.align();
        itemsArmor.align();
        crosshair.align();
        crosshair2.align();

        RENDERER.end();
    }

    public final Runnable reset = () -> {
        align();
        elements.forEach(element -> {
            element.active = element.defaultActive;
            element.settings.forEach(group -> group.forEach(Setting::reset));
        });
    };

    public HudElement get(RegistryKey<HudElement> key) {
        return null;
    }

    public HudElement get(Identifier id) {
        return null;
    }

    @EventHandler
    public void onRender(Render2DEvent event) {
        if (mc.options.debugEnabled || mc.options.hudHidden) return;

        RENDERER.begin(scale.get(), event.frameTime, false);

        for (HudElement element : elements) {
            if (element.active || HudTab.INSTANCE.isScreen(mc.currentScreen) || mc.currentScreen instanceof HudElementScreen) {
                element.update(RENDERER);
                element.render(RENDERER);
            }
        }

        RENDERER.end();
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

    private void openHudEditor() {
        HudTab hudTab = null;
        for (Tab tab : Tabs.get()) {
            if (tab instanceof HudTab) {
                hudTab = (HudTab) tab;
                break;
            }
        }

        if (hudTab == null) return;
        hudTab.openScreen(GuiThemes.get());
    }
}
