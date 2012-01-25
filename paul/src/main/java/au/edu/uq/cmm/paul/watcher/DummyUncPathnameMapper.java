package au.edu.uq.cmm.paul.watcher;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;


/**
 * An UNC path mapper for use in testing.  It ignores the hostname part of the
 * UNC path, and treats the rest as a relative pathname rooted at the supplied
 * base directory.
 * 
 * @author scrawley
 */
public class DummyUncPathnameMapper implements UncPathnameMapper {
    private static final Logger LOG = Logger.getLogger(DummyUncPathnameMapper.class);
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
