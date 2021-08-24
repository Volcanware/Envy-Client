package mathax.client.legacy.systems.enemies;

import mathax.client.legacy.systems.System;
import mathax.client.legacy.systems.Systems;
import mathax.client.legacy.utils.misc.NbtUtils;
import mathax.client.legacy.utils.render.color.RainbowColors;
import mathax.client.legacy.utils.render.color.SettingColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Enemies extends System<Enemies> implements Iterable<Enemy> {
    private List<Enemy> Enemies = new ArrayList<>();

    public final SettingColor color = new SettingColor(255, 0, 0);
    public boolean attack = false;

    public Enemies() {
        super("Enemies");
    }

    public static Enemies get() {
        return Systems.get(Enemies.class);
    }

    @Override
    public void init() {
        RainbowColors.add(color);
    }

    //TODO: Remove from friends on add.
    public boolean add(Enemy enemy) {
        if (enemy.name.isEmpty()) return false;

        if (!Enemies.contains(enemy)) {
            Enemies.add(enemy);
            save();

            return true;
        }

        return false;
    }

    public boolean remove(Enemy enemy) {
        if (Enemies.remove(enemy)) {
            save();
            return true;
        }

        return false;
    }

    public Enemy get(String name) {
        for (Enemy Enemy : Enemies) {
            if (Enemy.name.equals(name)) {
                return Enemy;
            }
        }

        return null;
    }

    public Enemy get(PlayerEntity player) {
        return get(player.getEntityName());
    }

    public boolean isEnemy(PlayerEntity player) {
        return get(player) != null;
    }

    public boolean shouldAttack(PlayerEntity player) {
        return !isEnemy(player) || attack;
    }

    public int count() {
        return Enemies.size();
    }

    @Override
    public @NotNull Iterator<Enemy> iterator() {
        return Enemies.iterator();
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        NbtList EnemiesTag = new NbtList();

        for (Enemy Enemy : Enemies) EnemiesTag.add(Enemy.toTag());
        tag.put("Enemies", EnemiesTag);
        tag.put("color", color.toTag());
        tag.putBoolean("attack", attack);

        return tag;
    }

    @Override
    public Enemies fromTag(NbtCompound tag) {
        Enemies = NbtUtils.listFromTag(tag.getList("Enemies", 10), tag1 -> new Enemy((NbtCompound) tag1));
        if (tag.contains("color")) color.fromTag(tag.getCompound("color"));
        attack = tag.contains("attack") && tag.getBoolean("attack");
        return this;
    }
}
