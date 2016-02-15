package com.peirr.filebrowser;

import com.peirr.filebrowser.model.DirectoryItem;

/**
 * Created by kurt on 2016/02/12.
 */
public interface DirectoryContract {

    interface BrowseView {
        void showDirectoryLoading(boolean show);
        void showDirectory(String path, DirectoryItem[] items);
        void showDirectoryError(String path,String message);
        void showDirectoryItem(DirectoryItem item);
    }

    interface BrowseActionsListener {
        void selectDirectoryItem(DirectoryItem directoryItem);
        void listCurrentDirectory();
        void listPreviousDirectory();
    }

}
