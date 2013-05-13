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
		this.session = session;
		this.forUser = forUser;
	}
	
	public String getUserName() {
		return (forUser == null) ? session.getUserName() : 
			forUser.getUserName();
	}
	
	public String getAccount() {
		return (forUser == null) ? session.getAccount() : 
			forUser.getAccounts().iterator().next();
	}
	
	public String getFacilityName() {
		return session.getFacilityName();
	}

	public String getEmailAddress() {
		return (forUser == null) ? session.getEmailAddress() : 
			forUser.getEmailAddress();
	}

	public String getOperatorName() {
		return session.getUserName();
	}

	public String getSessionUuid() {
		return session.getSessionUuid();
	}

	public Date getLoginTime() {
		return session.getLoginTime();
	}
	
}
