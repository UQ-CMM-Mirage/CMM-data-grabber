/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of Paul.
*
* Paul is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Paul is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Paul. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.paul.grabber;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This is a special-purpose blocking queue class with infinite capacity.
 * The API adds a pause() method that allows you to suspend
 * the methods that remove elements from the front of the queue.  Calls
 * to these methods are blocked until some other thread calls resume().
 * All other method calls are allowed to proceed.
 * <p>
 * BEWARE: the class uses a linked list and simple mutex synchronization.
 * Don't expect good performance if you use it in a use-case where there
 * are lots of producer and/or consumer threads.  (It is designed for a
 * use-case where there is one producer and one consumer ...)
 * 
 * @author scrawley
 *
 * @param <E> the queue element type
 */
public class PausableQueue<E> implements BlockingQueue<E> {
    
    private boolean paused;
    private LinkedList<E> elements = new LinkedList<E>();
    

    @Override
    public synchronized E remove() {
        return elements.removeFirst();
    }

    @Override
    public synchronized E poll() {
        return elements.poll();
    }

    @Override
    public synchronized E element() {
        return elements.element();
    }

    @Override
    public synchronized E peek() {
        return elements.peekFirst();
    }

    @Override
    public synchronized int size() {
        return elements.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public synchronized Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public synchronized Object[] toArray() {
        return elements.toArray();
    }

    @Override
    public synchronized <T> T[] toArray(T[] a) {
        return elements.toArray(a);
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        return elements.containsAll(c);
    }

    @Override
    public synchronized boolean addAll(Collection<? extends E> c) {
        this.notifyAll();
        return elements.addAll(c);
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        return elements.removeAll(c);
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        return elements.retainAll(c);
    }

    @Override
    public synchronized void clear() {
        elements.clear();
    }

    @Override
    public synchronized boolean add(E e) {
        this.notify();
        return elements.add(e);
    }

    @Override
    public synchronized boolean offer(E e) {
        this.notify();
        return elements.add(e);
    }

    @Override
    public synchronized void put(E e) throws InterruptedException {
        this.notify();
        elements.add(e);
    }

    @Override
    public synchronized boolean offer(E e, long timeout, TimeUnit unit)
            throws InterruptedException {
        this.notify();
        elements.add(e);
        return true;
    }

    @Override
    public synchronized E take() throws InterruptedException {
        while (paused || elements.isEmpty()) {
            this.wait();
        }
        return elements.remove();
    }

    @Override
    public synchronized E poll(long timeout, TimeUnit unit) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("negative timeout");
        }
        long now = System.currentTimeMillis();
        long deadline = unit.toMillis(timeout) + now;
        if (deadline < now) {
            deadline = Long.MAX_VALUE;
        }
        while (paused ||
                elements.isEmpty() && (now = System.currentTimeMillis()) < deadline) {
            this.wait(deadline - now);
        }
        return elements.poll();
    }

    @Override
    public synchronized int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public synchronized boolean remove(Object o) {
        return elements.remove(o);
    }

    @Override
    public synchronized boolean contains(Object o) {
        return elements.contains(o);
    }

    @Override
    public synchronized int drainTo(Collection<? super E> c) {
        int size = elements.size();
        c.addAll(elements);
        elements.clear();
        return size;
    }

    @Override
    public synchronized int drainTo(Collection<? super E> c, int maxElements) {
        int count = 0;
        while (count < maxElements && !elements.isEmpty()) {
            c.add(elements.removeFirst());
            count++;
        }
        return count;
    }
    
    public synchronized void pause() {
        if (!paused) {
            paused = true;
        }
    }
    
    public synchronized void resume() {
        if (paused) {
            paused = false;
            this.notifyAll();
        }
    }
    
}
