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
 * This exception is thrown when there is a problem with the session information
 * as reported to us.  For example, we may not recognize the user name or account
 * name (assuming that there is a mapping involved).
 * 
 * @author scrawley
 */
public class InvalidSessionException extends Exception {

    private static final long serialVersionUID = 2081979094442804790L;

    public InvalidSessionException(String message) {
        super(message);
    }

}
