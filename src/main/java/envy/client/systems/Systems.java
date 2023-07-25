package envy.client.systems;

import envy.client.Envy;
import envy.client.eventbus.EventHandler;
import envy.client.events.game.GameLeftEvent;
import envy.client.systems.accounts.Accounts;
import envy.client.systems.commands.Commands;
import envy.client.systems.config.Config;
import envy.client.systems.enemies.Enemies;
import envy.client.systems.friends.Friends;
import envy.client.systems.hud.HUD;
import envy.client.systems.macros.Macros;
import envy.client.systems.modules.Modules;
import envy.client.systems.profiles.Profiles;
import envy.client.systems.proxies.Proxies;
import envy.client.systems.waypoints.Waypoints;
import envy.client.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Systems {
    private static final Map<Class<? extends System>, System<?>> systems = new HashMap<>();
    private static final List<Runnable> preLoadTasks = new ArrayList<>(1);

    public static void addPreLoadTask(Runnable task) {
        preLoadTasks.add(task);
    }

    public static void init() {
        System<?> config = add(new Config());
        config.init();
        config.load();

        add(new Modules());
        add(new Commands());
        add(new Friends());
        add(new Enemies());
        add(new Macros());
        add(new Accounts());
        add(new Waypoints());
        add(new Profiles());
        add(new Proxies());
        add(new HUD());

        Envy.EVENT_BUS.subscribe(Systems.class);
    }

    private static System<?> add(System<?> system) {
        systems.put(system.getClass(), system);
        Envy.EVENT_BUS.subscribe(system);
        system.init();

        return system;
    }

    // Game leave

    @EventHandler
    private static void onGameLeft(GameLeftEvent event) {
        save();
    }

    // Save

    public static void save(File folder) {
        long start = Utils.getCurrentTimeMillis();
        Envy.LOG.info("Systems are saving...");

        for (System<?> system : systems.values()) system.save(folder);

        Envy.LOG.info("Systems saved in {} milliseconds.", Utils.getCurrentTimeMillis() - start);
    }

    public static void save() {
        save(null);
    }

    // Load

    public static void load(File folder) {
        long start = Utils.getCurrentTimeMillis();
        Envy.LOG.info("Systems are loading...");

        for (Runnable task : preLoadTasks) task.run();

        for (System<?> system : systems.values()) system.load(folder);

        Envy.LOG.info("Systems loaded in {} milliseconds.", Utils.getCurrentTimeMillis() - start);
    }

    public static void load() {
        load(null);
    }

    public static <T extends System<?>> T get(Class<T> klass) {
        return (T) systems.get(klass);
    }
}
