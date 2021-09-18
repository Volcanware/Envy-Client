package mathax.legacy.client.utils.entity.blink;

import java.util.ArrayList;
import java.util.List;

public class BlinkPlayerCloneManager {
    private static final List<BlinkPlayerCloneEntity> clones = new ArrayList<>();

    public static void add() {
        BlinkPlayerCloneEntity clone = new BlinkPlayerCloneEntity();
        clones.add(clone);
    }

    public static void clear() {
        if (clones.isEmpty()) return;
        clones.forEach(BlinkPlayerCloneEntity::despawn);
        clones.clear();
    }

    public static List<BlinkPlayerCloneEntity> getClones() {
        return clones;
    }

    public static int size() {
        return clones.size();
    }
}
