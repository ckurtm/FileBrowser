package com.peirr.filebrowser.dir.model;

import java.io.File;

/**
 * Created by kurt on 2016/02/12.
 */
public class DirectoryItem {
    private File file; //the actual file listing entry
    private int type; // the filetype e.g. audio,video,image e.t.c

    public DirectoryItem(File file, int type) {
        this.file = file;
        this.type = type;
    }

    public File getFile() {
        return file;
    }

    public int getType() {
        return type;
    }
}
