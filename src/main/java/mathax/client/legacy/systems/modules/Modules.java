package mathax.client.legacy.systems.modules;

import com.mojang.serialization.Lifecycle;
import mathax.client.legacy.MatHaxClientLegacy;
import mathax.client.legacy.events.game.GameJoinedEvent;
import mathax.client.legacy.events.game.GameLeftEvent;
import mathax.client.legacy.events.game.OpenScreenEvent;
import mathax.client.legacy.events.mathax.ActiveModulesChangedEvent;
import mathax.client.legacy.events.mathax.KeyEvent;
import mathax.client.legacy.events.mathax.ModuleBindChangedEvent;
import mathax.client.legacy.events.mathax.MouseButtonEvent;
import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.bus.EventPriority;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.System;
import mathax.client.legacy.systems.Systems;
import mathax.client.legacy.systems.config.Config;
import mathax.client.legacy.systems.modules.chat.*;
import mathax.client.legacy.systems.modules.combat.*;
import mathax.client.legacy.systems.modules.fun.*;
import mathax.client.legacy.systems.modules.misc.*;
import mathax.client.legacy.systems.modules.misc.swarm.Swarm;
import mathax.client.legacy.systems.modules.movement.*;
import mathax.client.legacy.systems.modules.movement.elytrafly.ElytraFly;
import mathax.client.legacy.systems.modules.movement.speed.Speed;
import mathax.client.legacy.systems.modules.player.*;
import mathax.client.legacy.systems.modules.render.*;
import mathax.client.legacy.systems.modules.render.hud.HUD;
import mathax.client.legacy.systems.modules.render.search.Search;
import mathax.client.legacy.systems.modules.world.*;
import mathax.client.legacy.systems.modules.world.Timer;
import mathax.client.legacy.utils.Utils;
import mathax.client.legacy.utils.misc.input.Input;
import mathax.client.legacy.utils.misc.input.KeyAction;
import mathax.client.legacy.utils.player.ChatUtils;
import mathax.client.legacy.utils.render.MatHaxToast;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static mathax.client.legacy.utils.Utils.mc;

public class Modules extends System<Modules> {
    public static final ModuleRegistry REGISTRY = new ModuleRegistry();

    private static final List<Category> CATEGORIES = new ArrayList<>();
    public static boolean REGISTERING_CATEGORIES;

    private final List<Module> modules = new ArrayList<>();
    private final Map<Class<? extends Module>, Module> moduleInstances = new HashMap<>();
    private final Map<Category, List<Module>> groups = new HashMap<>();

    private final List<Module> active = new ArrayList<>();
    private Module moduleToBind;

    public Modules() {
        super("Modules");
    }

    public static Modules get() {
        return Systems.get(Modules.class);
    }

    @Override
    public void init() {
        initCombat();
        initPlayer();
        initMovement();
        initRender();
        initWorld();
        initChat();
        initFun();
        initMisc();

        // This is here because some hud elements depend on modules to be initialised before them
        add(new HUD());
    }

    public void sortModules() {
        for (List<Module> modules : groups.values()) {
            modules.sort(Comparator.comparing(o -> o.title));
        }
        modules.sort(Comparator.comparing(o -> o.title));
    }

    public static void registerCategory(Category category) {
        if (!REGISTERING_CATEGORIES) throw new RuntimeException("Modules.registerCategory - Cannot register category outside of onRegisterCategories callback.");

        CATEGORIES.add(category);
    }

    public static Iterable<Category> loopCategories() {
        return CATEGORIES;
    }

    public static Category getCategoryByHash(int hash) {
        for (Category category : CATEGORIES) {
            if (category.hashCode() == hash) return category;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> klass) {
        return (T) moduleInstances.get(klass);
    }

    public Module get(String name) {
        for (Module module : moduleInstances.values()) {
            if (module.name.equalsIgnoreCase(name)) return module;
        }

        return null;
    }

    public boolean isActive(Class<? extends Module> klass) {
        Module module = get(klass);
        return module != null && module.isActive();
    }

    public List<Module> getGroup(Category category) {
        return groups.computeIfAbsent(category, category1 -> new ArrayList<>());
    }

    public Collection<Module> getAll() {
        return moduleInstances.values();
    }

    public List<Module> getList() {
        return modules;
    }

    public int getCount() {
        return moduleInstances.values().size();
    }

    public List<Module> getActive() {
        synchronized (active) {
            return active;
        }
    }

    public List<Pair<Module, Integer>> searchTitles(String text) {
        List<Pair<Module, Integer>> modules = new ArrayList<>();

        for (Module module : this.moduleInstances.values()) {
            int words = Utils.search(module.title, text);
            if (words > 0) modules.add(new Pair<>(module, words));
        }

        modules.sort(Comparator.comparingInt(value -> -value.getRight()));
        return modules;
    }

    public List<Pair<Module, Integer>> searchSettingTitles(String text) {
        List<Pair<Module, Integer>> modules = new ArrayList<>();

        for (Module module : this.moduleInstances.values()) {
            for (SettingGroup sg : module.settings) {
                for (Setting<?> setting : sg) {
                    int words = Utils.search(setting.title, text);
                    if (words > 0) {
                        modules.add(new Pair<>(module, words));
                        break;
                    }
                }
            }
        }

        modules.sort(Comparator.comparingInt(value -> -value.getRight()));
        return modules;
    }

    void addActive(Module module) {
        synchronized (active) {
            if (!active.contains(module)) {
                active.add(module);
                MatHaxClientLegacy.EVENT_BUS.post(ActiveModulesChangedEvent.get());
            }
        }
    }

    void removeActive(Module module) {
        synchronized (active) {
            if (active.remove(module)) {
                MatHaxClientLegacy.EVENT_BUS.post(ActiveModulesChangedEvent.get());
            }
        }
    }

    // Binding

    public void setModuleToBind(Module moduleToBind) {
        this.moduleToBind = moduleToBind;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onKeyBinding(KeyEvent event) {
        if (onBinding(true, event.key)) event.cancel();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onButtonBinding(MouseButtonEvent event) {
        if (onBinding(false, event.button)) event.cancel();
    }

    private boolean onBinding(boolean isKey, int value) {
        if (moduleToBind != null && moduleToBind.keybind.canBindTo(isKey, value)) {
            if (value != GLFW.GLFW_KEY_ESCAPE) {
                moduleToBind.keybind.set(isKey, value);
                ChatUtils.info("KeyBinds", "Module (highlight)%s (default)bound to (highlight)%s(default).", moduleToBind.title, moduleToBind.keybind);
                if (Config.get().chatCommandsToast && Config.get().moduleToggleToast) {
                    switch (moduleToBind.category.name) {
                        case "Combat" -> mc.getToastManager().add(new MatHaxToast(Items.END_CRYSTAL, Formatting.DARK_RED + "KeyBinds", Formatting.GRAY + "Module " + Formatting.WHITE + moduleToBind.name + Formatting.GRAY + " bound to " + Formatting.WHITE + moduleToBind.keybind + Formatting.GRAY + "."));
                        case "Player" -> mc.getToastManager().add(new MatHaxToast(Items.ARMOR_STAND, Formatting.DARK_RED + "KeyBinds", Formatting.GRAY + "Module " + Formatting.WHITE + moduleToBind.name + Formatting.GRAY + " bound to " + Formatting.WHITE + moduleToBind.keybind + Formatting.GRAY + "."));
                        case "Movement" -> mc.getToastManager().add(new MatHaxToast(Items.DIAMOND_BOOTS, Formatting.DARK_RED + "KeyBinds", Formatting.GRAY + "Module " + Formatting.WHITE + moduleToBind.name + Formatting.GRAY + " bound to " + Formatting.WHITE + moduleToBind.keybind + Formatting.GRAY + "."));
                        case "Render" -> mc.getToastManager().add(new MatHaxToast(Items.TINTED_GLASS, Formatting.DARK_RED + "KeyBinds", Formatting.GRAY + "Module " + Formatting.WHITE + moduleToBind.name + Formatting.GRAY + " bound to " + Formatting.WHITE + moduleToBind.keybind + Formatting.GRAY + "."));
                        case "World" -> mc.getToastManager().add(new MatHaxToast(Items.GRASS_BLOCK, Formatting.DARK_RED + "KeyBinds", Formatting.GRAY + "Module " + Formatting.WHITE + moduleToBind.name + Formatting.GRAY + " bound to " + Formatting.WHITE + moduleToBind.keybind + Formatting.GRAY + "."));
                        case "Chat" -> mc.getToastManager().add(new MatHaxToast(Items.BEACON, Formatting.DARK_RED + "KeyBinds", Formatting.GRAY + "Module " + Formatting.WHITE + moduleToBind.name + Formatting.GRAY + " bound to " + Formatting.WHITE + moduleToBind.keybind + Formatting.GRAY + "."));
                        case "Fun" -> mc.getToastManager().add(new MatHaxToast(Items.NOTE_BLOCK, Formatting.DARK_RED + "KeyBinds", Formatting.GRAY + "Module " + Formatting.WHITE + moduleToBind.name + Formatting.GRAY + " bound to " + Formatting.WHITE + moduleToBind.keybind + Formatting.GRAY + "."));
                        case "Misc" -> mc.getToastManager().add(new MatHaxToast(Items.NETHER_STAR, Formatting.DARK_RED + "KeyBinds", Formatting.GRAY + "Module " + Formatting.WHITE + moduleToBind.name + Formatting.GRAY + " bound to " + Formatting.WHITE + moduleToBind.keybind + Formatting.GRAY + "."));
                    }
                }
            }

            MatHaxClientLegacy.EVENT_BUS.post(ModuleBindChangedEvent.get(moduleToBind));
            moduleToBind = null;
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Repeat) return;
        onAction(true, event.key, event.action == KeyAction.Press);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Repeat) return;
        onAction(false, event.button, event.action == KeyAction.Press);
    }

    private void onAction(boolean isKey, int value, boolean isPress) {
        if (Utils.mc.currentScreen == null && !Input.isKeyPressed(GLFW.GLFW_KEY_F3)) {
            for (Module module : moduleInstances.values()) {
                if (module.keybind.matches(isKey, value) && (isPress || module.toggleOnBindRelease)) {
                    module.toggle();
                    module.sendToggledMsg(module.title, module);
                    module.sendToggledToast(module.title, module);
                }
            }
        }
    }

    // End of binding

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onOpenScreen(OpenScreenEvent event) {
        if (!Utils.canUpdate()) return;

        for (Module module : moduleInstances.values()) {
            if (module.toggleOnBindRelease && module.isActive()) {
                module.toggle();
                module.sendToggledMsg(module.title, module);
                module.sendToggledToast(module.title, module);
            }
        }
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        synchronized (active) {
            for (Module module : modules) {
                if (module.isActive()) {
                    MatHaxClientLegacy.EVENT_BUS.subscribe(module);
                    module.onActivate();
                }
            }
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        synchronized (active) {
            for (Module module : modules) {
                if (module.isActive()) {
                    MatHaxClientLegacy.EVENT_BUS.unsubscribe(module);
                    module.onDeactivate();
                }
            }
        }
    }

    public void disableAll() {
        synchronized (active) {
            for (Module module : modules) {
                if (module.isActive()) module.toggle(Utils.canUpdate());
            }
        }
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        NbtList modulesTag = new NbtList();
        for (Module module : getAll()) {
            NbtCompound moduleTag = module.toTag();
            if (moduleTag != null) modulesTag.add(moduleTag);
        }
        tag.put("modules", modulesTag);

        return tag;
    }

    @Override
    public Modules fromTag(NbtCompound tag) {
        disableAll();

        NbtList modulesTag = tag.getList("modules", 10);
        for (NbtElement moduleTagI : modulesTag) {
            NbtCompound moduleTag = (NbtCompound) moduleTagI;
            Module module = get(moduleTag.getString("name"));
            if (module != null) module.fromTag(moduleTag);
        }

        return this;
    }

    // INIT MODULES

    public void add(Module module) {
        // Check if the module's category is registered
        if (!CATEGORIES.contains(module.category)) {
            throw new RuntimeException("Modules.addModule - Module's category was not registered.");
        }

        // Remove the previous module with the same name
        AtomicReference<Module> removedModule = new AtomicReference<>();
        if (moduleInstances.values().removeIf(module1 -> {
            if (module1.name.equals(module.name)) {
                removedModule.set(module1);
                module1.settings.unregisterColorSettings();

                return true;
            }

            return false;
        })) {
            getGroup(removedModule.get().category).remove(removedModule.get());
        }

        // Add the module
        moduleInstances.put(module.getClass(), module);
        modules.add(module);
        getGroup(module.category).add(module);

        // Register color settings for the module
        module.settings.registerColorSettings(module);
    }

    private void initCombat() {
        add(new AimAssist());
        add(new AnchorAura());
        add(new AntiAnchor());
        add(new AntiAnvil());
        add(new AntiBed());
        add(new ArrowDodge());
        add(new AutoAnvil());
        add(new AutoArmor());
        add(new AutoCity());
        add(new AutoTotem());
        add(new AutoTrap());
        add(new AutoWeapon());
        add(new AutoWeb());
        add(new BedAura());
        //add(new BedAuraPlus());
        add(new BowAimbot());
        add(new BowSpam());
        add(new Burrow());
        add(new Criticals());
        add(new CrystalAura());
        //add(new CrystalAuraPlus());
        //add(new CEVBreaker());
        add(new Hitboxes());
        add(new HoleFiller());
        add(new InstaAutoCity());
        add(new KillAura());
        add(new Offhand());
        //add(new PistonAura());
        add(new Quiver());
        add(new SelfAnvil());
        add(new SelfTrap());
        add(new SelfWeb());
        add(new SmartSurround());
        add(new Surround());
    }

    private void initPlayer() {
        add(new AntiHunger());
        add(new AutoEat());
        add(new AutoFish());
        add(new AutoGap());
        add(new AutoMend());
        add(new AutoReplenish());
        add(new AutoTool());
        add(new ChestSwap());
        add(new EXPThrower());
        add(new FastUse());
        add(new GhostHand());
        add(new InstaMine());
        add(new LiquidInteract());
        add(new NoBreakDelay());
        add(new NoInteract());
        add(new NoMiningTrace());
        add(new NoRotate());
        add(new OffhandCrash());
        add(new PacketMine());
        add(new Portals());
        add(new PotionSaver());
        add(new PotionSpoof());
        add(new Reach());
        add(new Rotation());
        add(new SpeedMine());
    }

    private void initMovement() {
        add(new AirJump());
        add(new Anchor());
        add(new AntiAFK());
        add(new AntiLevitation());
        add(new AntiVoid());
        add(new AutoJump());
        add(new AutoWalk());
        add(new Blink());
        add(new BoatFly());
        add(new ClickTP());
        add(new ElytraBoost());
        add(new ElytraFly());
        add(new EntityControl());
        add(new EntitySpeed());
        add(new FastClimb());
        add(new Flight());
        add(new GUIMove());
        add(new HighJump());
        add(new Jesus());
        add(new NoFall());
        add(new NoSlow());
        //add(new PacketFly());
        add(new Parkour());
        //add(new Phase());
        add(new ReverseStep());
        add(new SafeWalk());
        add(new Scaffold());
        add(new Slippy());
        add(new Sneak());
        add(new Speed());
        add(new Spider());
        add(new Sprint());
        add(new Step());
        add(new TridentBoost());
        add(new Velocity());
    }

    private void initRender() {
        add(new BetterTooltips());
        add(new BlockSelection());
        add(new BossStack());
        add(new Breadcrumbs());
        add(new BreakIndicators());
        add(new CameraTweaks());
        add(new Chams());
        add(new CityESP());
        //add(new CustomCrosshair());
        add(new CustomFOV());
        add(new EntityOwner());
        add(new ESP());
        add(new FakePlayer());
        add(new Freecam());
        add(new FreeLook());
        add(new Fullbright());
        add(new HandView());
        add(new HoleESP());
        add(new ItemPhysics());
        add(new ItemHighlight());
        add(new LightOverlay());
        add(new LogoutSpots());
        add(new Nametags());
        add(new NoBob());
        add(new NoRender());
        add(new Search());
        add(new StorageESP());
        add(new TimeChanger());
        add(new Tracers());
        add(new Trail());
        add(new Trajectories());
        add(new UnfocusedCPU());
        add(new VoidESP());
        add(new WallHack());
        add(new WaypointsModule());
        add(new Xray());
        add(new Zoom());
        add(new Background());
    }

    private void initWorld() {
        add(new AirPlace());
        add(new Ambience());
        add(new AntiCactus());
        add(new AntiGhostBlock());
        add(new AutoBreed());
        add(new AutoBrewer());
        add(new AutoMount());
        add(new AutoNametag());
        add(new AutoShearer());
        add(new AutoSign());
        add(new AutoSmelter());
        add(new BuildHeight());
        add(new EChestFarmer());
        add(new EndermanLook());
        add(new Flamethrower());
        add(new HighwayBuilder());
        add(new InfinityMiner());
        add(new LiquidFiller());
        add(new MountBypass());
        add(new Nuker());
        add(new StashFinder());
        add(new SpawnProofer());
        add(new Timer());
        add(new VeinMiner());
    }

    private void initChat() {
        add(new Announcer());
        add(new AutoEZ());
        add(new BetterChat());
        add(new ChatBot());
        add(new MessageAura());
        add(new Spam());
    }

    private void initFun() {
        add(new BookBot());
        add(new Capes());
        add(new Notebot());
        //add(new PenisESP());
        add(new SpinBot());
    }

    private void initMisc() {
        add(new Swarm());
        add(new AntiPacketKick());
        add(new AutoClicker());
        add(new AutoLog());
        add(new AutoMountBypassDupe());
        add(new AutoReconnect());
        add(new AutoRespawn());
        add(new BetterTab());
        add(new MiddleClickExtra());
        add(new MiddleClickFriend());
        add(new NameProtect());
        add(new Notifier());
        add(new PacketCanceller());
        add(new SoundBlocker());
        add(new TPSSync());
        add(new VanillaSpoof());
        add(new InventoryTweaks());
    }

    public static class ModuleRegistry extends Registry<Module> {
        public ModuleRegistry() {
            super(RegistryKey.ofRegistry(new Identifier("mathaxlegacy", "modules")), Lifecycle.stable());
        }

        @Override
        public Identifier getId(Module entry) {
            return null;
        }

        @Override
        public Optional<RegistryKey<Module>> getKey(Module entry) {
            return Optional.empty();
        }

        @Override
        public int getRawId(Module entry) {
            return 0;
        }

        @Override
        public Module get(RegistryKey<Module> key) {
            return null;
        }

        @Override
        public Module get(Identifier id) {
            return null;
        }

        @Override
        protected Lifecycle getEntryLifecycle(Module object) {
            return null;
        }

        @Override
        public Lifecycle getLifecycle() {
            return null;
        }

        @Override
        public Set<Identifier> getIds() {
            return null;
        }

        @Override
        public Set<Map.Entry<RegistryKey<Module>, Module>> getEntries() {
            return null;
        }

        @Override
        public boolean containsId(Identifier id) {
            return false;
        }

        @Nullable
        @Override
        public Module get(int index) {
            return null;
        }

        @Override
        public Iterator<Module> iterator() {
            return new ModuleIterator();
        }

        @org.jetbrains.annotations.Nullable
        @Override
        public Module getRandom(Random random) {
            return null;
        }

        @Override
        public boolean contains(RegistryKey<Module> key) {
            return false;
        }

        private static class ModuleIterator implements Iterator<Module> {
            private final Iterator<Module> iterator = Modules.get().getAll().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Module next() {
                return iterator.next();
            }
        }
    }

}
