package com.peirr.filebrowser.dir;


import com.peirr.filebrowser.dir.model.DirectoryItem;

/**
 * Created by kurt on 2016/01/28.
 */
public interface StorageRepository {

    interface FileTypeFilter {
        boolean isValidType(String file);
    }

    interface DirectoryListingCallback {
        void onDirectoryListed(DirectoryItem[] items, Exception e);
    }

    void getDirectoryListing(String path, DirectoryListingCallback callback);
    String getRootDirectory();
}
