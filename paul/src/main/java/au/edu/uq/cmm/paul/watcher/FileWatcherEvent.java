package au.edu.uq.cmm.paul.watcher;

import java.io.File;
import java.util.EventObject;

import au.edu.uq.cmm.aclslib.server.Facility;

@SuppressWarnings("serial")
public class FileWatcherEvent extends EventObject {
    private final File file;
    private final boolean create;
    
    public FileWatcherEvent(Facility facility, File file, boolean create) {
        super(facility);
        this.file = file;
        this.create = create;
    }
    
    public Facility getFacility() {
        return (Facility) getSource();
    }

    public File getFile() {
        return file;
    }

    public boolean isCreate() {
        return create;
    }
}
