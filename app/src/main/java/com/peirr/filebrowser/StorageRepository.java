package com.peirr.filebrowser;


import com.peirr.filebrowser.model.DirectoryItem;

/**
 * Created by kurt on 2016/01/28.
 */
public interface StorageRepository {

    interface DirectoryListingCallback {
        void onDirectoryListed(DirectoryItem[] items, Exception e);
    }

    void getDirectoryListing(String path, DirectoryListingCallback callback);
    String getRootDirectory();
}
