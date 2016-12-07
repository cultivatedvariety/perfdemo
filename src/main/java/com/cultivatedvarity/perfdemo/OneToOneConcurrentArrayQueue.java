package com.cultivatedvarity.perfdemo;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by robch on 07/12/2016.
 */
public class OneToOneConcurrentArrayQueue<E> implements Queue<E>
{
    private final E[] buffer;
    private final int mask;

    // use arrays of size 16 (64 bytes) to avoid false sharing
    private final AtomicLong[] tail = new AtomicLong[16];
    private final AtomicLong[] head = new AtomicLong[16];


    @SuppressWarnings("unchecked")
    public OneToOneConcurrentArrayQueue(final int size)
    {
        buffer = (E[])new Object[size];
        mask = size - 1;
        tail[0] = new AtomicLong(0);
        head[0] = new AtomicLong(0);
    }

    public boolean add(final E e)
    {
        if (offer(e))
        {
            return true;
        }

        throw new IllegalStateException("Queue is full");
    }

    public boolean offer(final E e)
    {
        //in a queue, append at tail
        if (tail[0].get() - head[0].get() >= buffer.length){
            //no space
            return false;
        }

        int slot = (int)(tail[0].get() & mask);
        buffer[slot] = e;
        tail[0].incrementAndGet();

        return true;
    }

    public E poll()
    {
        if (tail[0].get() <= head[0].get()){
            return null;
        }

        int slot = (int)(head[0].get() & mask);
        E val = buffer[slot];
        head[0].incrementAndGet();
        return val;
    }

    public E remove()
    {
        final E e = poll();
        if (null == e)
        {
            throw new IllegalStateException("Queue is empty");
        }

        return e;
    }

    public E element()
    {
        final E e = peek();
        if (null == e)
        {
            throw new NoSuchElementException("Queue is empty");
        }

        return e;
    }

    public E peek()
    {
        return buffer[(int)(head[0].get() % buffer.length)];
    }

    public int size()
    {
        long currentHeadBefore;
        long currentTail;
        long currentHeadAfter = head[0].get();

        do
        {
            currentHeadBefore = currentHeadAfter;
            currentTail = tail[0].get();
            currentHeadAfter = head[0].get();

        }
        while (currentHeadAfter != currentHeadBefore);

        return (int)(currentTail - currentHeadAfter);
    }

    public boolean isEmpty()
    {
        return tail == head;
    }

    public boolean contains(final Object o)
    {
        if (null == o)
        {
            return false;
        }

        for (long i = head[0].get(), limit = tail[0].get(); i < limit; i++)
        {
            final E e = buffer[(int)(i % buffer.length)];
            if (o.equals(e))
            {
                return true;
            }
        }

        return false;
    }

    public Iterator<E> iterator()
    {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray()
    {
        throw new UnsupportedOperationException();
    }

    public <T> T[] toArray(final T[] a)
    {
        throw new UnsupportedOperationException();
    }

    public boolean remove(final Object o)
    {
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(final Collection<?> c)
    {
        for (final Object o : c)
        {
            if (!contains(o))
            {
                return false;
            }
        }

        return true;
    }

    public boolean addAll(final Collection<? extends E> c)
    {
        for (final E o : c)
        {
            add(o);
        }

        return true;
    }

    public boolean removeAll(final Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(final Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    public void clear()
    {
        throw new UnsupportedOperationException();
    }
}
