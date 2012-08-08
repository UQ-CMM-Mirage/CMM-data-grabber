/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of AclsLib.
*
* AclsLib is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* AclsLib is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with AclsLib. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.paul.queue;

import au.edu.uq.cmm.aclslib.service.Service;
import au.edu.uq.cmm.aclslib.service.ServiceException;
import au.edu.uq.cmm.paul.PaulControl;

public class AtomFeed implements Service {
    
    private PaulControl control;
    
    public AtomFeed(PaulControl control) {
        this.control = control;
    }

    @Override
    public void startup() throws ServiceException, InterruptedException {
        startStartup();
    }

    @Override
    public void shutdown() throws ServiceException, InterruptedException {
        startShutdown();
    }

    @Override
    public void startStartup() throws ServiceException {
    }

    @Override
    public void startShutdown() throws ServiceException {
    }

    @Override
    public void awaitShutdown() throws InterruptedException {
        return;
    }

    @Override
    public State getState() {
        return control.isAtomFeedEnabled() ? State.STARTED : State.STOPPED;
    }
}
