package mathax.legacy.client.systems.macros;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.events.mathax.KeyEvent;
import mathax.legacy.client.events.mathax.MouseButtonEvent;
import mathax.legacy.client.systems.System;
import mathax.legacy.client.systems.Systems;
import mathax.legacy.client.utils.misc.NbtUtils;
import mathax.legacy.client.utils.misc.input.KeyAction;
import mathax.legacy.client.eventbus.EventHandler;
import mathax.legacy.client.eventbus.EventPriority;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Macros extends System<Macros> implements Iterable<Macro> {
    private List<Macro> macros = new ArrayList<>();

    public Macros() {
        super("Macros");
    }

    public static Macros get() {
        return Systems.get(Macros.class);
    }

    public void add(Macro macro) {
        macros.add(macro);
        MatHaxLegacy.EVENT_BUS.subscribe(macro);
        save();
    }

    public List<Macro> getAll() {
        return macros;
    }

    public void remove(Macro macro) {
        if (macros.remove(macro)) {
            MatHaxLegacy.EVENT_BUS.unsubscribe(macro);
            save();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onKey(KeyEvent event) {
        if (event.action == KeyAction.Release) return;

        for (Macro macro : macros) {
            if (macro.onAction(true, event.key)) return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Release) return;

        for (Macro macro : macros) {
            if (macro.onAction(false, event.button)) return;
        }
    }

    @Override
    public Iterator<Macro> iterator() {
        return macros.iterator();
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.put("macros", NbtUtils.listToTag(macros));
        return tag;
    }

    @Override
    public Macros fromTag(NbtCompound tag) {
        for (Macro macro : macros) MatHaxLegacy.EVENT_BUS.unsubscribe(macro);

        macros = NbtUtils.listFromTag(tag.getList("macros", 10), tag1 -> new Macro().fromTag((NbtCompound) tag1));

        for (Macro macro : macros) MatHaxLegacy.EVENT_BUS.subscribe(macro);
        return this;
    }
}
