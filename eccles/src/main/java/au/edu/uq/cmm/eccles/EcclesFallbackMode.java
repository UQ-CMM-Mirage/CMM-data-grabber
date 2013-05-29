/*
* Copyright 2013, CMM, University of Queensland.
*
* This file is part of Eccles.
*
* Eccles is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Eccles is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Eccles. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.eccles;

public enum EcclesFallbackMode {
	/** No fallback if the ACLS service is not available. */
	NO_FALLBACK,
	
	/** Check against previously cached credentials. */
	USER_PASSWORD,
	
	/** 
	 * Check against previously cached credentials, if available,
	 * or just a username if there is no cached password.
	 */
	USER_PASSWORD_OPTIONAL,
	
	/** Check the user name only.  Ignore the password. */
	USER_ONLY
}
