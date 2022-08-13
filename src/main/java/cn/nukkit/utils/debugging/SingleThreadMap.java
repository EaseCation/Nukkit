package cn.nukkit.utils.debugging;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SingleThreadMap<K, V> implements Map<K, V> {

    protected final Map<K, V> map;
    protected final Thread thread;

    protected boolean read;
    protected Object parent;

    protected Throwable previousCall;
    protected Throwable currentCall;

    public SingleThreadMap(Map<K, V> map) {
        this(map, null);
    }

    public SingleThreadMap(Map<K, V> map, Thread thread) {
        Objects.requireNonNull(map, "map");
        this.map = map;
        this.thread = thread != null ? thread : Thread.currentThread();
    }

    public SingleThreadMap<K, V> allowRead(boolean read) {
        this.read = read;
        return this;
    }

    public SingleThreadMap<K, V> parent(Object parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public int size() {
        if (!read) {
            checkThread();
        }
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        if (!read) {
            checkThread();
        }
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (!read) {
            checkThread();
        }
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (!read) {
            checkThread();
        }
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        if (!read) {
            checkThread();
        }
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        checkThread();
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        checkThread();
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        checkThread();
        map.putAll(m);
    }

    @Override
    public void clear() {
        checkThread();
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        if (!read) {
            checkThread();
        }
        return new SingleThreadSet<>(map.keySet(), thread).parent(this);
    }

    @Override
    public Collection<V> values() {
        if (!read) {
            checkThread();
        }
        return new SingleThreadCollection<>(map.values(), thread).parent(this);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        if (!read) {
            checkThread();
        }
        return new SingleThreadSet<>(map.entrySet(), thread).parent(this);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if (!read) {
            checkThread();
        }
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        checkThread();
        return map.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        checkThread();
        return map.remove(key, value);
    }

    @Override
    public V replace(K key, V value) {
        checkThread();
        return map.replace(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        checkThread();
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        checkThread();
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        checkThread();
        return map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        checkThread();
        return map.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        checkThread();
        return map.merge(key, value, remappingFunction);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        if (!read) {
            checkThread();
        }
        map.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        checkThread();
        map.replaceAll(function);
    }

    protected void checkThread() {
        DebuggingHelper.checkCurrentThreadIs(thread);
        synchronized (this) {
            previousCall = currentCall;
            currentCall = new Throwable();
        }
    }
}
