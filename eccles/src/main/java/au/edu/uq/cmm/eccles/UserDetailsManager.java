/*
* Copyright 2013-2013, CMM, University of Queensland.
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

import java.util.List;

import javax.persistence.EntityManager;

import au.edu.uq.cmm.aclslib.authenticator.AclsLoginDetails;
import au.edu.uq.cmm.aclslib.authenticator.Authenticator;

public interface UserDetailsManager extends Authenticator {

    UserDetails lookupUser(String userName, boolean fetchCollections)
            throws UserDetailsException;

    List<String> getUserNames();

    List<UserDetails> getUsers();

    void addUser(UserDetails user) throws UserDetailsException;

    void removeUser(String userName) throws UserDetailsException;

    void refreshUserDetails(EntityManager em, String userName, String email,
            AclsLoginDetails loginDetails);
}