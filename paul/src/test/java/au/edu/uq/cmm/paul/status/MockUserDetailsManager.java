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
package au.edu.uq.cmm.paul.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import au.edu.uq.cmm.aclslib.authenticator.AclsLoginDetails;
import au.edu.uq.cmm.aclslib.config.FacilityConfig;
import au.edu.uq.cmm.eccles.UserDetails;
import au.edu.uq.cmm.eccles.UserDetailsException;
import au.edu.uq.cmm.eccles.UserDetailsManager;

public class MockUserDetailsManager implements UserDetailsManager {
    private Map<String, UserDetails> map;

    public MockUserDetailsManager(UserDetails[] userDetails) {
        map = new HashMap<>();
        for (UserDetails u : userDetails) {
            map.put(u.getUserName(), u);
        }
    }

    @Override
    public UserDetails lookupUser(String userName, boolean fetchCollections)
            throws UserDetailsException {
        UserDetails u = map.get(userName);
        if (u == null) {
            throw new UserDetailsException("User '" + userName + "' not found");
        }
        return u;
    }

    @Override
    public List<String> getUserNames() {
        return new ArrayList<>(map.keySet());
    }

    @Override
    public List<UserDetails> getUsers() {
        return new ArrayList<>(map.values());
    }

    @Override
    public void addUser(UserDetails user) throws UserDetailsException {
        if (map.containsKey(user.getUserName())) {
            throw new UserDetailsException(
            		"User '" + user.getUserName() + "' already exists");
        }
        map.put(user.getUserName(), user);
    }

    @Override
    public void removeUser(String userName) throws UserDetailsException {
        if (!map.containsKey(userName)) {
            throw new UserDetailsException("User '" + userName + "' not found");
        }
        map.remove(userName);
    }

    @Override
    public void refreshUserDetails(EntityManager em, String userName,
            String email, AclsLoginDetails loginDetails) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AclsLoginDetails authenticateAgainstCachedCredentials(
            String userName, String password, FacilityConfig facility) {
        throw new UnsupportedOperationException();
    }

}
