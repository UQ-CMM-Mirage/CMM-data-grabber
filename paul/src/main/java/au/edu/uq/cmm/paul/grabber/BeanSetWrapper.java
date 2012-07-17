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

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * The BeanSetWrapper class is an unmodifiable SortedSet wrapper that additionally
 * exposes the first(), last() and size() methods of the underlying set as 
 * JavaBeans compatible getters.
 * 
 * @author scrawley
 *
 * @param <E>
 */
public class BeanSetWrapper<E> extends AbstractSet<E> implements SortedSet<E> {
    private final SortedSet<E> set;
    
   
    public BeanSetWrapper(SortedSet<E> set) {
        super();
        this.set = Collections.unmodifiableSortedSet(set);
    }

    @Override
    public Comparator<? super E> comparator() {
        return set.comparator();
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return new BeanSetWrapper<>(set.subSet(fromElement, toElement));
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return new BeanSetWrapper<>(set.headSet(toElement));
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return new BeanSetWrapper<>(set.tailSet(fromElement));
    }

    @Override
    public E first() {
        return set.first();
    }

    @Override
    public E last() {
        return set.last();
    }

    @Override
    public Iterator<E> iterator() {
        return set.iterator();
    }

    @Override
    public int size() {
        return set.size();
    }
    
    /**
     * This getter mirrors the size() method.
     * 
     * @return see {@link #size()}
     */
    public int getSize() {
        return size();
    }
    
    /**
     * This getter mirrors the first() method.
     *  
     * @return see {@link #first()}
     */
    public E getFirst() {
        return first();
    }
    
    /**
     * This getter mirrors the last() method.
     *  
     * @return see {@link #last()}
     */
    public E getLast() {
        return last();
    }
}
