/*
* Copyright 2012, CMM, University of Queensland.
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

/**
 * A session details mapper is an extension point that can be used to bridge
 * between the user/account details reported by an instrument (e.g. from ACLS)
 * and details that the repository will recognize 
 * 
 * @author scrawley
 */
public interface SessionDetailMapper {

    /**
     * Map the details to a user name.
     * 
     * @param userName the original user name 
     * @return the mapped name
     * @throws InvalidSessionException
     */
    String mapToUserName(String userName)
            throws InvalidSessionException;
    
    /**
     * Map the details to an account name.
     * 
     * @param accountName the original account name 
     * @return the mapped name
     * @throws InvalidSessionException
     */
    String mapToAccount(String accountName)
            throws InvalidSessionException;
    
    /**
     * Map the details to a user address.
     * 
     * @param userName the original user name 
     * @return the mapped email address or null.
     * @throws InvalidSessionException
     */
    String mapToEmailAddress(String userName) 
            throws InvalidSessionException;
}
