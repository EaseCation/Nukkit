package cn.nukkit.item.randomitem;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;

import java.util.Objects;

/**
 * Created by Snake1999 on 2016/1/15.
 * Package cn.nukkit.item.randomitem in project nukkit.
 */
public final class RandomItem {
    private static final Object2FloatMap<Selector> selectors = new Object2FloatOpenHashMap<>();

    public static final Selector ROOT = new Selector(null);

    public static Selector putSelector(Selector selector) {
        return putSelector(selector, 1);
    }

    public static Selector putSelector(Selector selector, float chance) {
        if (selector.getParent() == null) selector.setParent(ROOT);
        selectors.put(selector, chance);
        return selector;
    }

    static Object selectFrom(Selector selector) {
        Objects.requireNonNull(selector);
        Object2FloatMap<Selector> child = new Object2FloatOpenHashMap<>();
        selectors.forEach((s, f) -> {
            if (s.getParent() == selector) child.put(s, f);
        });
        if (child.isEmpty()) return selector.select();
        return selectFrom(Selector.selectRandom(child));
    }

}
