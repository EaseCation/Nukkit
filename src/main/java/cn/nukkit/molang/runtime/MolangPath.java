package cn.nukkit.molang.runtime;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 编译期解析的成员路径。缓存仅包含不可变布局与槽位，不持有某个运行时的父结构。
 */
public final class MolangPath {
    private final String name;
    private final int bindingIndex;
    private final String[] parts;
    private static final int MAX_CACHED_LAYOUTS = 4;
    private final AtomicReferenceArray<Slot[]> slots;

    public MolangPath(String name) {
        this(name, -1);
    }

    public MolangPath(String name, int bindingIndex) {
        this.bindingIndex = bindingIndex;
        this.name = Objects.requireNonNull(name, "name").toLowerCase(Locale.ROOT);
        parts = this.name.split("\\.", -1);
        for (String part : parts) {
            if (part.isEmpty()) {
                throw new IllegalArgumentException("Invalid Molang member path: " + name);
            }
        }
        slots = new AtomicReferenceArray<>(parts.length);
    }

    public String name() {
        return name;
    }

    public int bindingIndex() {
        return bindingIndex;
    }

    public int size() {
        return parts.length;
    }

    public String part(int index) {
        return parts[index];
    }

    @Nullable
    public Slot cached(int index, Object layout) {
        Slot[] cached = slots.get(index);
        if (cached != null) {
            for (Slot slot : cached) {
                if (slot.layout() == layout) {
                    return slot;
                }
            }
        }
        return null;
    }

    public void cache(int index, Object layout, int slot) {
        while (true) {
            Slot[] cached = slots.get(index);
            if (cached != null) {
                for (Slot entry : cached) {
                    if (entry.layout() == layout) {
                        return;
                    }
                }
                if (cached.length == MAX_CACHED_LAYOUTS) {
                    return;
                }
            }
            int size = cached != null ? cached.length : 0;
            Slot[] updated = cached != null ? Arrays.copyOf(cached, size + 1) : new Slot[1];
            updated[size] = new Slot(layout, slot);
            if (slots.compareAndSet(index, cached, updated)) {
                return;
            }
        }
    }

    /**
     * 一次原子发布布局和槽位，允许同一表达式在多个运行时之间并发使用。
     */
    public record Slot(Object layout, int index) {
    }
}
