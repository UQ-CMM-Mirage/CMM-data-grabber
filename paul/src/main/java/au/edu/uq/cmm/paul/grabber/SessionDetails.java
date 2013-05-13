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

import java.util.Date;

import au.edu.uq.cmm.eccles.FacilitySession;
import au.edu.uq.cmm.paul.status.Facility;

/**
 * Session details to be used in the Dataset metadata of a grabbed Dataset.
 * This could be real session details, fake details (for a "claimed" or "assigned")
 * Dataset or stuff to say which operator did the work ...
 * 
 * @author scrawley
 */
class SessionDetails {
    private final String userName;
    private final String account;
    private final String sessionUuid;
    private final String emailAddress;
    private final Date loginTime;
    private final String operatorName;
    
    SessionDetails(FacilitySession session) {
        userName = session.getUserName();
        account = session.getAccount();
        sessionUuid = session.getSessionUuid();
        emailAddress = session.getEmailAddress();
        loginTime = session.getLoginTime();
        operatorName = null;
    }
    
    SessionDetails(Facility facility, Date now) {
        this(FacilitySession.makeDummySession(facility.getFacilityName(), now));
    }

    public final String getUserName() {
        return userName;
    }

    public final String getAccount() {
        return account;
    }

    public final String getSessionUuid() {
        return sessionUuid;
    }

    public final String getEmailAddress() {
        return emailAddress;
    }

    public final Date getLoginTime() {
        return loginTime;
    }

    public final String getOperatorName() {
        return operatorName;
    }
}
