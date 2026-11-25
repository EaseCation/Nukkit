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
    private static final int MAX_CACHED_LAYOUTS = 4;

    static final Slot SATURATED = new Slot(new Object(), -1);

    private final String name;
    private final int bindingIndex;
    private final String[] parts;
    private final AtomicReferenceArray<Slot[]> slots;
    // Slot 的布局和槽号均为 final。其他线程尚未看到此引用时，仍可通过 slots 解析路径。
    @Nullable
    private Slot direct;

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

    int directSlot(Object layout) {
        Slot hint = direct;
        return hint != null && hint.layout() == layout ? hint.index() : -1;
    }

    @Nullable
    Slot nestedLeafHint() {
        return direct;
    }

    @Nullable
    public Slot cached(int index, Object layout) {
        return find(slots.get(index), layout);
    }

    @Nullable
    Slot lookup(int index, Object layout) {
        Slot[] cached = slots.get(index);
        return cached != null && cached.length > MAX_CACHED_LAYOUTS ? SATURATED : find(cached, layout);
    }

    @Nullable
    private static Slot find(@Nullable Slot[] cached, Object layout) {
        if (cached != null) {
            for (Slot slot : cached) {
                if (slot.layout() == layout && slot != SATURATED) {
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
                if (cached.length > MAX_CACHED_LAYOUTS || find(cached, layout) != null) {
                    return;
                }
                if (cached.length == MAX_CACHED_LAYOUTS) {
                    // 此步已见到第五种布局。保留前四项供 cached 使用，后续通用读写直接查询自身布局。
                    Slot[] saturated = Arrays.copyOf(cached, MAX_CACHED_LAYOUTS + 1);
                    saturated[MAX_CACHED_LAYOUTS] = SATURATED;
                    if (slots.compareAndSet(index, cached, saturated) && parts.length == 2 && index == 1) {
                        direct = SATURATED;
                    }
                    return;
                }
            }
            int size = cached != null ? cached.length : 0;
            Slot[] updated = cached != null ? Arrays.copyOf(cached, size + 1) : new Slot[1];
            updated[size] = new Slot(layout, slot);
            if (slots.compareAndSet(index, cached, updated)) {
                if (parts.length <= 2 && index == parts.length - 1 && slot >= 0 && direct == null) {
                    // 只有首个成功插入非缺失槽的线程发布这个布局和槽号。
                    for (int i = 0; i < size; i++) {
                        if (updated[i].index() >= 0) {
                            return;
                        }
                    }
                    // 双段路径的叶槽取反后始终为负，整路径入口遇到同一布局时仍会遍历根和叶。
                    direct = parts.length == 1 ? updated[size] : new Slot(layout, ~slot);
                    // 首次命中与第五种布局可能在不同线程中发布。
                    if (parts.length == 2 && slots.get(index).length > MAX_CACHED_LAYOUTS) {
                        direct = SATURATED;
                    }
                }
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
