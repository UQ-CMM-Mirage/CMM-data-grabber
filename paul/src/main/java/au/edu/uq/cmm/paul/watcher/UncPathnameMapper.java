package au.edu.uq.cmm.paul.watcher;

import java.io.File;

public interface UncPathnameMapper {

    /**
     * Map a Windows UNC pathname to a local File.
     * 
     * @param uncPathname the pathname to be mapped.
     * @return the File which may or may not exist.
     */
    File mapUncPathname(String uncPathname);
}
