package com.peirr.filebrowser.dir;

import com.peirr.filebrowser.dir.model.DirectoryItem;

/**
 * Created by kurt on 2016/02/12.
 */
public interface DirectoryContract {

    interface DirectoryView {
        /**
         * Called to toggle directory traversal progress
         * @param show
         */
        void showDirectoryLoading(boolean show);

        /**
         * callback when we have a listing of the current directory
         * @param path
         * @param items
         */
        void showDirectory(String path, DirectoryItem[] items);

        /**
         * Called when there is an error listing the currently selected directory
         * @param path
         * @param message
         */
        void showDirectoryError(String path,String message);

        /**
         * callback to the view when a directory item is selected
         * @param item
         */
        void showDirectoryItem(DirectoryItem item);
    }

    interface DirectoryActionsListener {

        /**
         * selects an item in the current directory
         * @param directoryItem - the item to select.
         */
        void selectDirectoryItem(DirectoryItem directoryItem);

        /**
         * Request a listing of the current directory
         */
        void listCurrentDirectory();

        /**
         * this does exactly what the name suggests. it attempts to go 1 directory up from the current directory
         */
        void listPreviousDirectory();
    }

}
