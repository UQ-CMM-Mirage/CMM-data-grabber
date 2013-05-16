/*
* Copyright 2013, CMM, University of Queensland.
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
import au.edu.uq.cmm.eccles.UserDetails;

/**
 * This is a wrapper class that takes care of the cases where
 * the user operates the instruments themselves, and someone else
 * operates the instrument for them.  (Except, that we can only do
 * the latter with a "sealing wax and string" solution ...)
 * 
 * @author steve
 */
public class SessionDetails {
	private final FacilitySession session;
	
	private final UserDetails forUser;

	public SessionDetails(FacilitySession session) {
		this(session, null);
	}

	public SessionDetails(FacilitySession session, UserDetails forUser) {
	    if (session == null) {
	        throw new NullPointerException("Null session");
	    }
		this.session = session;
		if (forUser == null || forUser.getUserName().equals(session.getUserName())) {
		    this.forUser = null;
		} else {
		    this.forUser = forUser;
		}
	}
	
	/**
	 * This is the (ultimate) owner of the data.
	 */
	public String getUserName() {
		return (forUser == null) ? session.getUserName() : 
			forUser.getUserName();
	}
	
	/**
	 * This is the account to which the work was billed.  If the work was
	 * done by a staff operator, this may be inaccurate.
	 */
	public String getAccount() {
		return session.getAccount();
	}
	
	/**
	 * This is the facility which the data was grabbed from.
	 */
	public String getFacilityName() {
		return session.getFacilityName();
	}

	/**
	 * This is the email address for the owner of the data ... as best as we can tell.
	 */
	public String getEmailAddress() {
		return (forUser == null) ? session.getEmailAddress() : 
			forUser.getEmailAddress();
	}

	/**
	 * If the work was done by a staff operator, this will the operator's name.
	 * Otherwise it will be null.
	 */
	public String getOperatorName() {
		return forUser == null ? null : session.getUserName();
	}

	/**
	 * The session id for the user (or operator) session the work was done in.
	 */
	public String getSessionUuid() {
		return session.getSessionUuid();
	}

	/**
	 * The session start time.
	 */
	public Date getLoginTime() {
		return session.getLoginTime();
	}
	
}
