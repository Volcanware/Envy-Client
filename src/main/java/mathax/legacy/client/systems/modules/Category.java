package mathax.legacy.client.systems.modules;

import mathax.legacy.client.utils.Utils;
import net.minecraft.item.ItemStack;

public class Category {
    public final String name, title;
    public final ItemStack icon;
    public final int color;
    private final int nameHash;

    public Category(String name, ItemStack icon, int color) {
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.nameHash = name.hashCode();
        this.icon = icon;
        this.color = color;
    }

    public Category(String name, int color) {
        this(name, null, color);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return nameHash == category.nameHash;
    }

    @Override
    public int hashCode() {
        return nameHash;
    }
}
