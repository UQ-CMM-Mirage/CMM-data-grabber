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
 * This SessionDetailMapper provides a null mapping of the tendered user
 * name and account name.  Other (optional) mappings return null.
 * 
 * @author scrawley
 */
public class DefaultSessionDetailsMapper implements SessionDetailMapper {

    @Override
    public String mapToUserName(String userName) {
        return userName;
    }

    @Override
    public String mapToAccount(String accountName) {
        return accountName;
    }

    @Override
    public String mapToEmailAddress(String userName)
            throws InvalidSessionException {
        return null;
    }
}
