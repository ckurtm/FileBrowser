package com.peirr.filebrowser.dir;


import com.peirr.filebrowser.dir.model.DirectoryItem;

import java.io.File;
import java.util.Stack;

/**
 * Created by kurt on 2016/01/28.
 */
public class DirectoryPresenter implements DirectoryContract.DirectoryActionsListener {

    private final StorageRepository repository;
    private final DirectoryContract.DirectoryView view;

    private File currentPath; //the current directory path
    private Stack<String> traversalStack = new Stack<>(); //keeps track of the traversed directories and where we are now.
    private Boolean parentDirectory = true; //we are initialy at the parent directory

    public DirectoryPresenter(StorageRepository repository, DirectoryContract.DirectoryView view) {
        this.repository = repository;
        this.view = view;
        this.currentPath = new File(repository.getRootDirectory());
    }

    public Boolean isParentDirectory() {
        return parentDirectory;
    }

    @Override
    public void selectDirectoryItem(final DirectoryItem item) {
        if (item.getFile().isDirectory()) {
            parentDirectory = false;
            traversalStack.push(item.getFile().getName());
            currentPath = new File(currentPath + "/" + item.getFile().getName());
            listCurrentDirectory();
        } else {
            view.showDirectoryItem(item);
        }
    }

    @Override
    public void listCurrentDirectory() {
        if (currentPath.exists()) {
            view.showDirectoryLoading(true);
            final String path = currentPath.getAbsolutePath();
            repository.getDirectoryListing(path, new StorageRepository.DirectoryListingCallback() {
                @Override
                public void onDirectoryListed(DirectoryItem[] items, Exception e) {
                    view.showDirectoryLoading(false);
                    if (e == null) {
                        view.showDirectory(path, items);
                    } else {
                        view.showDirectoryError(path, e.getMessage() + "");
                    }
                }
            });
        } else {
            view.showDirectoryError(currentPath.getPath(), "failed to list directory");
        }
    }

    @Override
    public void listPreviousDirectory() {
        if (!parentDirectory) {
            String s = traversalStack.pop();
            //get the filename only from the absolute currentPath
            currentPath = new File(currentPath.toString().substring(0, currentPath.toString().lastIndexOf(s)));
            // if there are no more traversalStack in the list, then its the parent directory
            parentDirectory = traversalStack.isEmpty();
            listCurrentDirectory();
        }
    }
}
