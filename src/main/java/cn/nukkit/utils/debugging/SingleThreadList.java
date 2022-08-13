package cn.nukkit.utils.debugging;

import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Log4j2
public class SingleThreadList<E> implements List<E> {

    protected final List<E> list;
    protected final Thread thread;

    protected boolean read;
    protected Object parent;

    protected Throwable previousCall;
    protected Throwable currentCall;

    public SingleThreadList(List<E> list) {
        this(list, null);
    }

    public SingleThreadList(List<E> list, Thread thread) {
        Objects.requireNonNull(list, "list");
        this.list = list;
        this.thread = thread != null ? thread : Thread.currentThread();
    }

    public SingleThreadList<E> allowRead(boolean read) {
        this.read = read;
        return this;
    }

    public SingleThreadList<E> parent(Object parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public int size() {
        if (!read) {
            checkThread();
        }
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        if (!read) {
            checkThread();
        }
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (!read) {
            checkThread();
        }
        return list.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        if (!read) {
            checkThread();
        }
        return new SingleThreadIterator<>(list.iterator(), thread).parent(this);
    }

    @Override
    public Object[] toArray() {
        if (!read) {
            checkThread();
        }
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (!read) {
            checkThread();
        }
        return list.toArray(a);
    }

    @Override
    public boolean add(E e) {
        checkThread();
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        checkThread();
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (!read) {
            checkThread();
        }
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        checkThread();
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkThread();
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        checkThread();
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        checkThread();
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        checkThread();
        list.clear();
    }

    @Override
    public E get(int index) {
        if (!read) {
            checkThread();
        }
        return list.get(index);
    }

    @Override
    public E set(int index, E element) {
        checkThread();
        return list.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        checkThread();
        list.add(index, element);
    }

    @Override
    public E remove(int index) {
        checkThread();
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        if (!read) {
            checkThread();
        }
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!read) {
            checkThread();
        }
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        if (!read) {
            checkThread();
        }
        return new SingleThreadListIterator<>(list.listIterator(), thread).parent(this);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        if (!read) {
            checkThread();
        }
        return new SingleThreadListIterator<>(list.listIterator(index), thread).parent(this);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (!read) {
            checkThread();
        }
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        checkThread();
        list.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        checkThread();
        list.sort(c);
    }

    @Override
    public Spliterator<E> spliterator() {
        if (!read) {
            checkThread();
        }
        return list.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        checkThread();
        return list.removeIf(filter);
    }

    @Override
    public Stream<E> stream() {
        if (!read) {
            checkThread();
        }
        return list.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        log.warn("Attempt to call Collection::parallelStream", new Throwable());
        return list.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        if (!read) {
            checkThread();
        }
        list.forEach(action);
    }

    protected void checkThread() {
        DebuggingHelper.checkCurrentThreadIs(thread);
        synchronized (this) {
            previousCall = currentCall;
            currentCall = new Throwable();
        }
    }
}
