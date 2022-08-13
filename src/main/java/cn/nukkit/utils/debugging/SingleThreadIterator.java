package cn.nukkit.utils.debugging;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public class SingleThreadIterator<E> implements Iterator<E> {

    protected final Iterator<E> iterator;
    protected final Thread thread;

    protected boolean read;
    protected Object parent;

    protected Throwable previousCall;
    protected Throwable currentCall;

    public SingleThreadIterator(Iterator<E> iterator) {
        this(iterator, null);
    }

    public SingleThreadIterator(Iterator<E> iterator, Thread thread) {
        Objects.requireNonNull(iterator, "iterator");
        this.iterator = iterator;
        this.thread = thread != null ? thread : Thread.currentThread();
    }

    public SingleThreadIterator<E> allowRead(boolean read) {
        this.read = read;
        return this;
    }

    public SingleThreadIterator<E> parent(Object parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public boolean hasNext() {
        if (!read) {
            checkThread();
        }
        return iterator.hasNext();
    }

    @Override
    public E next() {
        if (!read) {
            checkThread();
        }
        return iterator.next();
    }

    @Override
    public void remove() {
        checkThread();
        iterator.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        if (!read) {
            checkThread();
        }
        iterator.forEachRemaining(action);
    }

    protected void checkThread() {
        DebuggingHelper.checkCurrentThreadIs(thread);
        synchronized (this) {
            previousCall = currentCall;
            currentCall = new Throwable();
        }
    }
}
