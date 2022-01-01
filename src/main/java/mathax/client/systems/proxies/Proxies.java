package mathax.client.systems.proxies;

import mathax.client.systems.Systems;
import mathax.client.utils.misc.NbtUtils;
import mathax.client.systems.System;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Proxies extends System<Proxies> implements Iterable<Proxy> {
    private List<Proxy> proxies = new ArrayList<>();

    public Proxies() {
        super("Proxies");
    }

    public static Proxies get() {
        return Systems.get(Proxies.class);
    }

    public boolean add(Proxy proxy) {
        for (Proxy p : proxies) {
            if (p.type == proxy.type && p.address.equals(proxy.address) && p.port == proxy.port) return false;
        }

        if (proxies.isEmpty()) proxy.enabled = true;

        proxies.add(proxy);
        save();

        return true;
    }

    public void remove(Proxy proxy) {
        if (proxies.remove(proxy)) {
            save();
        }
    }

    public Proxy getEnabled() {
        for (Proxy proxy : proxies) {
            if (proxy.enabled) return proxy;
        }

        return null;
    }

    public void setEnabled(Proxy proxy, boolean enabled) {
        for (Proxy p : proxies) {
            p.enabled = false;
        }

        proxy.enabled = enabled;
        save();
    }

    @NotNull
    @Override
    public Iterator<Proxy> iterator() {
        return proxies.iterator();
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.put("proxies", NbtUtils.listToTag(proxies));

        return tag;
    }

    @Override
    public Proxies fromTag(NbtCompound tag) {
        proxies = NbtUtils.listFromTag(tag.getList("proxies", 10), tag1 -> new Proxy().fromTag((NbtCompound) tag1));

        return this;
    }
}
