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

@SuppressWarnings("serial")
public class EcclesException extends RuntimeException {

    public EcclesException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public EcclesException(String msg) {
        super(msg);
    }

    public EcclesException(Throwable ex) {
        super(ex);
    }

}
