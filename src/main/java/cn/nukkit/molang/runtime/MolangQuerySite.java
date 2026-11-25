package cn.nukkit.molang.runtime;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * 通用表达式的查询调用点。只记住名称布局和槽号，处理器始终从本次求值的宿主取得。
 */
public final class MolangQuerySite {
    private final String name;
    @Nullable
    private volatile Layout firstLayout;
    private int firstSlot;

    public MolangQuerySite(String name) {
        this.name = Objects.requireNonNull(name, "name").toLowerCase(Locale.ROOT);
    }

    String name() {
        return name;
    }

    int slot(Layout layout) {
        Layout initial = firstLayout;
        return initial == layout ? firstSlot : anotherLayout(initial, layout);
    }

    private int anotherLayout(@Nullable Layout initial, Layout layout) {
        if (initial != null && layout.matches(firstSlot, name)) {
            return firstSlot;
        }
        int slot = layout.index(name);
        if (slot >= 0 && initial == null) {
            publish(layout, slot);
        }
        return slot;
    }

    private synchronized void publish(Layout layout, int slot) {
        if (firstLayout == null) {
            firstSlot = slot;
            // 编译产物可以跨线程共享；先保存槽号，再发布与之对应的不可变布局。
            firstLayout = layout;
        }
    }

    @Nullable
    @SuppressWarnings("unused")
    public static Object queryValue0(MolangRuntime runtime, MolangQuerySite site) {
        return runtime instanceof MolangRuntimeBuilder.MolangRuntimeImpl owner ? owner.resolve(site).queryValue0() : runtime.queryValue0(site.name);
    }

    @Nullable
    @SuppressWarnings("unused")
    public static Object queryValue1(MolangRuntime runtime, MolangQuerySite site, @Nullable Object argument) {
        return runtime instanceof MolangRuntimeBuilder.MolangRuntimeImpl owner ? owner.resolve(site).queryValue1(argument) : runtime.queryValue1(site.name, argument);
    }

    @SuppressWarnings("unused")
    public static float queryNumber0(MolangRuntime runtime, MolangQuerySite site) {
        return runtime instanceof MolangRuntimeBuilder.MolangRuntimeImpl owner ? owner.resolve(site).queryNumber0() : runtime.queryNumber0(site.name);
    }

    static final class Layout {
        private static final Layout EMPTY = new Layout(Set.of());

        private final String[] names;
        private final Object2IntMap<String> slots;

        static Layout of(Set<String> registered) {
            return registered.isEmpty() ? EMPTY : new Layout(registered);
        }

        private Layout(Set<String> registered) {
            names = registered.toArray(String[]::new);
            Arrays.sort(names);
            slots = new Object2IntOpenHashMap<>(names.length);
            slots.defaultReturnValue(-1);
            for (int index = 0; index < names.length; index++) {
                slots.put(names[index], index);
            }
        }

        int index(String name) {
            return slots.getInt(name);
        }

        int size() {
            return names.length;
        }

        boolean matches(int slot, String name) {
            return slot < names.length && name.equals(names[slot]);
        }
    }
}
