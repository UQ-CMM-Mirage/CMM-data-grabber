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

import java.util.Iterator;


public class PushbackIterator<E> implements Iterator<E> {
    
    private E buffer;
    private Iterator<E> it;

    public PushbackIterator(Iterator<E> it) {
        this.it = it;
    }

    @Override
    public boolean hasNext() {
        return buffer != null || it.hasNext();
    }

    @Override
    public E next() {
        if (buffer != null) {
            E tmp = buffer;
            buffer = null;
            return tmp;
        } else {
            return it.next();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Can't do 'remove' on a PushbackIterator wrapper");
    }
    
    public void pushback(E e) {
        if (buffer != null) {
            throw new IllegalStateException("Already pushed back");
        }
        buffer = e;
    }

}
