package au.edu.uq.cmm.paul.watcher;

import java.io.File;
import java.util.EventObject;

import au.edu.uq.cmm.aclslib.server.Facility;

@SuppressWarnings("serial")
public class FileWatcherEvent extends EventObject {
    private final File file;
    private final boolean create;
    private final long timestamp;
    
    public FileWatcherEvent(Facility facility, File file, boolean create, long timestamp) {
        super(facility);
        this.file = file;
        this.create = create;
        this.timestamp = timestamp;
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

    public long getTimestamp() {
        return timestamp;
    }
}