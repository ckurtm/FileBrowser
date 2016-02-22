package com.peirr.filebrowser.dir;

import android.os.Environment;
import android.support.annotation.IntDef;
import android.util.Log;


import com.peirr.filebrowser.dir.model.DirectoryItem;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLConnection;

/**
 * Created by kurt on 2016/01/28.
 */
public class ExternalStorageRepository implements StorageRepository {

    private static final String TAG = "ExternalStorageRepo";
    private final FileTypeFilter filter;

    public ExternalStorageRepository(FileTypeFilter filter) {
        this.filter = filter;
    }


    @Override
    public void getDirectoryListing(String path, DirectoryListingCallback callback) {
        Log.d(TAG, "getDirectoryListing() : [" + path + "]");
        File requestedPath = new File(path);
        DirectoryItem[] items;
        if (requestedPath.exists()) {
            String[] directoryFiles = requestedPath.list(getFileFilter());
            items = new DirectoryItem[directoryFiles.length];
            for (int i = 0; i < directoryFiles.length; i++) {
                File file = new File(requestedPath, directoryFiles[i]);
                items[i] = new DirectoryItem(file,SimpleFileFilter.getFileType(file.getAbsolutePath()));
            }
            callback.onDirectoryListed(items,null);
        } else {
            callback.onDirectoryListed(null,new Exception(path + " not found"));
        }
    }


    @Override
    public String getRootDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private FilenameFilter getFileFilter(){
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File file = new File(dir, filename);
                String path = file.getAbsolutePath();
                // Filters based on whether the file is hidden or not && if its a supported filetype
                return ((file.isFile() && filter.isValidType(path)) || file.isDirectory()) && !file.isHidden();
            }
        };
    }


    public static class SimpleFileFilter implements FileTypeFilter {

        public static final int TYPE_UNKNOWN = 0;
        public static final int TYPE_IMAGE = 1;
        public static final int TYPE_VIDEO = 2;
        public static final int TYPE_AUDIO = 3;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({TYPE_UNKNOWN,TYPE_IMAGE,TYPE_VIDEO, TYPE_AUDIO})
        public @interface ItemType {}


        private static boolean isImageFile(String path) {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.indexOf("image") == 0;
        }

        private static boolean isVideoFile(String path) {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.indexOf("video") == 0;
        }

        private static boolean isAudioFile(String path) {
            String mimeType = URLConnection.guessContentTypeFromName(path);
            return mimeType != null && mimeType.indexOf("audio") == 0;
        }

        @ItemType
        public static int getFileType(String path){

            if(isImageFile(path)) {
                return TYPE_IMAGE;
            }

            if(isAudioFile(path)){
                return TYPE_AUDIO;
            }

            if(isVideoFile(path)) {
                return TYPE_VIDEO;
            }

            return TYPE_UNKNOWN;
        }

        @Override
        public boolean isValidType(String file) {
            return getFileType(file) != TYPE_UNKNOWN;
        }
    }
}
