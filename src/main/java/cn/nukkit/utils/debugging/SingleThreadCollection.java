package cn.nukkit.utils.debugging;

import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Log4j2
public class SingleThreadCollection<E> implements Collection<E> {

    protected final Collection<E> collection;
    protected final Thread thread;

    protected boolean read;
    protected Object parent;

    protected Throwable previousCall;
    protected Throwable currentCall;

    public SingleThreadCollection(Collection<E> collection) {
        this(collection, null);
    }

    public SingleThreadCollection(Collection<E> collection, Thread thread) {
        Objects.requireNonNull(collection, "collection");
        this.collection = collection;
        this.thread = thread != null ? thread : Thread.currentThread();
    }

    public SingleThreadCollection<E> allowRead(boolean read) {
        this.read = read;
        return this;
    }

    public SingleThreadCollection<E> parent(Object parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public int size() {
        if (!read) {
            checkThread();
        }
        return collection.size();
    }

    @Override
    public boolean isEmpty() {
        if (!read) {
            checkThread();
        }
        return collection.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (!read) {
            checkThread();
        }
        return collection.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        if (!read) {
            checkThread();
        }
        return new SingleThreadIterator<>(collection.iterator(), thread).parent(this);
    }

    @Override
    public Object[] toArray() {
        if (!read) {
            checkThread();
        }
        return collection.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (!read) {
            checkThread();
        }
        return collection.toArray(a);
    }

    @Override
    public boolean add(E e) {
        checkThread();
        return collection.add(e);
    }

    @Override
    public boolean remove(Object o) {
        checkThread();
        return collection.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (!read) {
            checkThread();
        }
        return collection.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        checkThread();
        return collection.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        checkThread();
        return collection.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        checkThread();
        return collection.retainAll(c);
    }

    @Override
    public void clear() {
        checkThread();
        collection.clear();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        checkThread();
        return collection.removeIf(filter);
    }

    @Override
    public Spliterator<E> spliterator() {
        if (!read) {
            checkThread();
        }
        return collection.spliterator();
    }

    @Override
    public Stream<E> stream() {
        if (!read) {
            checkThread();
        }
        return collection.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        log.warn("Attempt to call Collection::parallelStream", new Throwable());
        return collection.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        if (!read) {
            checkThread();
        }
        collection.forEach(action);
    }

    protected void checkThread() {
        DebuggingHelper.checkCurrentThreadIs(thread);
        synchronized (this) {
            previousCall = currentCall;
            currentCall = new Throwable();
        }
    }
}
