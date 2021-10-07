package mathax.legacy.client.systems;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.systems.accounts.Accounts;
import mathax.legacy.client.systems.commands.Commands;
import mathax.legacy.client.systems.enemies.Enemies;
import mathax.legacy.client.systems.macros.Macros;
import mathax.legacy.client.systems.profiles.Profiles;
import mathax.legacy.client.systems.seeds.Seeds;
import mathax.legacy.client.systems.waypoints.Waypoints;
import mathax.legacy.client.systems.config.Config;
import mathax.legacy.client.systems.friends.Friends;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.proxies.Proxies;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Systems {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends System>, System<?>> systems = new HashMap<>();

    private static final List<Runnable> preLoadTasks = new ArrayList<>(1);
    private static System<?> config;

    public static void init() {
        config = add(new Config());
        config.load();
        config.init();

        add(new Modules());
        add(new Commands());
        add(new Friends());
        add(new Enemies());
        add(new Macros());
        add(new Accounts());
        add(new Waypoints());
        add(new Profiles());
        add(new Proxies());
        add(new Seeds());

        for (System<?> system : systems.values()) {
            if (system != config) system.init();
        }
    }

    private static System<?> add(System<?> system) {
        systems.put(system.getClass(), system);
        MatHaxLegacy.EVENT_BUS.subscribe(system);

        return system;
    }

    public static void save(File folder) {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Systems are saving...");
        long start = java.lang.System.currentTimeMillis();

        for (System<?> system : systems.values()) system.save(folder);

        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Systems saved in %time% milliseconds.".replace("%time%", String.valueOf(java.lang.System.currentTimeMillis() - start)));
    }

    public static void save() {
        save(null);
    }

    public static void addPreLoadTask(Runnable task) {
        preLoadTasks.add(task);
    }

    public static void load(File folder) {
        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Systems are loading...");
        long start = java.lang.System.currentTimeMillis();

        for (Runnable task : preLoadTasks) task.run();

        for (System<?> system : systems.values()) {
            if (system != config) system.load(folder);
        }

        MatHaxLegacy.LOG.info(MatHaxLegacy.logPrefix + "Systems loaded in %time% milliseconds.".replace("%time%", String.valueOf(java.lang.System.currentTimeMillis() - start)));
    }

    public static void load() {
        load(null);
    }

    @SuppressWarnings("unchecked")
    public static <T extends System<?>> T get(Class<T> klass) {
        return (T) systems.get(klass);
    }
}
