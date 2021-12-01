package mathax.legacy.client.systems.modules.render.hud;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.render.Render2DEvent;
import mathax.legacy.client.gui.GuiTheme;
import mathax.legacy.client.gui.GuiThemes;
import mathax.legacy.client.gui.renderer.GuiRenderer;
import mathax.legacy.client.gui.screens.HudElementScreen;
import mathax.legacy.client.gui.tabs.Tab;
import mathax.legacy.client.gui.tabs.Tabs;
import mathax.legacy.client.gui.tabs.builtin.HudTab;
import mathax.legacy.client.gui.widgets.WWidget;
import mathax.legacy.client.gui.widgets.containers.WTable;
import mathax.legacy.client.gui.widgets.pressable.WButton;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.utils.render.AlignmentX;
import mathax.legacy.client.utils.render.AlignmentY;
import mathax.legacy.client.utils.render.color.SettingColor;
import mathax.legacy.client.eventbus.EventHandler;
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

    public final List<HUDElement> elements = new ArrayList<>();
    private final HUDElementLayer mainInfo, moduleInfo, breakingLooking, coords, lag, modules, invPot, time, radar, itemsArmor, crosshair, crosshair2;

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
        super(Categories.Render, Items.GLASS, "hud", "In game overlay.");

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
        mainInfo.add(new MusicHUD(this));

        // MODULE INFO
        moduleInfo = new HUDElementLayer(RENDERER, elements, AlignmentX.Left, AlignmentY.Top, 100, 200);
        // Modules
        moduleInfo.add(new ModuleInfoHUD(this));

        // LOOKING AT & BREAKING
        breakingLooking = new HUDElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 215);
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
        lag.add(new BigRatHUD(this));

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
        radar.add(new CombatInfoHUD(this));
        radar.add(new TextRadarHUD(this));

        // ITEMS & ARMOR
        itemsArmor = new HUDElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, -150, -50);
        // Modules
        itemsArmor.add(new TotemHUD(this));
        itemsArmor.add(new CrystalHUD(this));
        itemsArmor.add(new EGapHUD(this));
        itemsArmor.add(new XPBottleHUD(this));
        itemsArmor.add(new ObsidianHUD(this));
        itemsArmor.add(new BedHUD(this));
        itemsArmor.add(new ArmorHUD(this));

        // CROSSHAIR
        crosshair = new HUDElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 0);
        // Modules
        crosshair.add(new HoleHUD(this));

        // CROSSHAIR 2
        crosshair2 = new HUDElementLayer(RENDERER, elements, AlignmentX.Center, AlignmentY.Center, 0, 0);
        // Modules
        crosshair2.add(new CompassHUD(this));

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
        itemsArmor.align();
        crosshair.align();
        crosshair2.align();

        RENDERER.end();
    }

    @EventHandler
    public void onRender(Render2DEvent event) {
        if (mc.options.debugEnabled || mc.options.hudHidden) return;

        RENDERER.begin(scale.get(), event.frameTime, false);

        for (HUDElement element : elements) {
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
