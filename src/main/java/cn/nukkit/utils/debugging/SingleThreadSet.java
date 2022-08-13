package cn.nukkit.utils.debugging;

import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Log4j2
public class SingleThreadSet<E> implements Set<E> {

    protected final Set<E> set;
    protected final Thread thread;

    protected boolean read;
    protected Object parent;

    protected Throwable previousCall;
    protected Throwable currentCall;

    public SingleThreadSet(Set<E> set) {
        this(set, null);
    }

    public SingleThreadSet(Set<E> set, Thread thread) {
        Objects.requireNonNull(set, "set");
        this.set = set;
        this.thread = thread != null ? thread : Thread.currentThread();
    }

    public SingleThreadSet<E> allowRead(boolean read) {
        this.read = read;
        return this;
    }

    public SingleThreadSet<E> parent(Object parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public int size() {
        if (!read) {
            checkThread();
        }
        return set.size();
    }

    @Override
    public boolean isEmpty() {
        if (!read) {
            checkThread();
        }
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (!read) {
            checkThread();
        }
        return set.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        if (!read) {
            checkThread();
        }
        return new SingleThreadIterator<>(set.iterator(), thread).parent(this);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        if (!read) {
            checkThread();
        }
        set.forEach(action);
    }

    @Override
    public Object[] toArray() {
        if (!read) {
            checkThread();
        }
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (!read) {
            checkThread();
        }
        return set.toArray(a);
    }

    @Override
    public boolean add(E e) {
        checkThread();
        return set.add(e);
    }

    @Override
    public boolean remove(Object o) {
        checkThread();
        return set.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (!read) {
            checkThread();
        }
        return set.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        checkThread();
        return set.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        checkThread();
        return set.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        checkThread();
        return set.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        checkThread();
        return set.removeIf(filter);
    }

    @Override
    public void clear() {
        checkThread();
        set.clear();
    }

    @Override
    public Spliterator<E> spliterator() {
        if (!read) {
            checkThread();
        }
        return set.spliterator();
    }

    @Override
    public Stream<E> stream() {
        if (!read) {
            checkThread();
        }
        return set.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        log.warn("Attempt to call Collection::parallelStream", new Throwable());
        return set.parallelStream();
    }

    protected void checkThread() {
        DebuggingHelper.checkCurrentThreadIs(thread);
        synchronized (this) {
            previousCall = currentCall;
            currentCall = new Throwable();
        }
    }
}
