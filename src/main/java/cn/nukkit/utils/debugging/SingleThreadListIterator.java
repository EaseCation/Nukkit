package cn.nukkit.utils.debugging;

import java.util.ListIterator;

public class SingleThreadListIterator<E> extends SingleThreadIterator<E> implements ListIterator<E> {

    protected final ListIterator<E> iterator;

    public SingleThreadListIterator(ListIterator<E> iterator) {
        this(iterator, null);
    }

    public SingleThreadListIterator(ListIterator<E> iterator, Thread thread) {
        super(iterator, thread);
        this.iterator = iterator;
    }

    @Override
    public SingleThreadListIterator<E> allowRead(boolean read) {
        super.allowRead(read);
        return this;
    }

    @Override
    public SingleThreadListIterator<E> parent(Object parent) {
        super.parent(parent);
        return this;
    }

    @Override
    public boolean hasPrevious() {
        if (!read) {
            checkThread();
        }
        return iterator.hasPrevious();
    }

    @Override
    public E previous() {
        if (!read) {
            checkThread();
        }
        return iterator.previous();
    }

    @Override
    public int nextIndex() {
        if (!read) {
            checkThread();
        }
        return iterator.nextIndex();
    }

    @Override
    public int previousIndex() {
        if (!read) {
            checkThread();
        }
        return iterator.previousIndex();
    }

    @Override
    public void set(E e) {
        checkThread();
        iterator.set(e);
    }

    @Override
    public void add(E e) {
        checkThread();
        iterator.add(e);
    }
}
