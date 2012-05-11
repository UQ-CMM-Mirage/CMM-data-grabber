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

package au.edu.uq.cmm.paul.servlet;

import java.util.Map;

public class ValidationResult <T> {
    private final Map<String, String> diags;
    private final T target;
    
    public ValidationResult(Map<String, String> diags, T target) {
        super();
        this.diags = diags;
        this.target = target;
    }

    public Map<String, String> getDiags() {
        return diags;
    }

    public T getTarget() {
        return target;
    }

    public boolean isValid() {
        return diags.isEmpty();
    }
}
