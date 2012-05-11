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

package au.edu.uq.cmm.paul.watcher;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An UNC path mapper for use in testing.  It ignores the hostname part of the
 * UNC path, and treats the rest as a relative pathname rooted at the supplied
 * base directory.
 * 
 * @author scrawley
 */
public class DummyUncPathnameMapper implements UncPathnameMapper {
    private static final Logger LOG = 
            LoggerFactory.getLogger(DummyUncPathnameMapper.class);
    private static final Pattern UNC_PATTERN = 
            Pattern.compile("//([^/]+)/([^/]+)(?:/+(.*))?");
    private File baseDirectory;
    
    
    public DummyUncPathnameMapper(String baseDirectory) {
        this.baseDirectory = new File(baseDirectory);
        if (!this.baseDirectory.isDirectory()) {
            throw new IllegalArgumentException(
                    "Invalid base directory '" + baseDirectory + "'");
        }
    }

    @Override
    public File mapUncPathname(String uncPathname) {
        String canonicalUncPathname = uncPathname.replace('\\', '/');
        Matcher matcher = UNC_PATTERN.matcher(canonicalUncPathname);
        if (!matcher.matches()) {
            LOG.info("Invalid UNC path: '" + canonicalUncPathname + "'");
            return null;
        }
        File sharePath = new File(baseDirectory, matcher.group(2));
        if (matcher.group(3) == null) {
            return sharePath;
        } else {
            return new File(sharePath, matcher.group(3));
        }
    }

}
