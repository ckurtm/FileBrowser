package com.peirr.filebrowser;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.peirr.filebrowser.dir.DirectoryContract;
import com.peirr.filebrowser.dir.DirectoryPresenter;
import com.peirr.filebrowser.dir.ExternalStorageRepository;
import com.peirr.filebrowser.dir.StorageRepository;
import com.peirr.filebrowser.dir.model.DirectoryItem;

import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FileBrowser extends AppCompatActivity implements EasyPermissions.PermissionCallbacks ,DirectoryAdapter.OnDirectoryItemSelectionListener , DirectoryContract.DirectoryView{
    private static final String TAG = "FileBrowser";
    private final int REQUEST_STORAGE_PERMISSION = 1;
    private DirectoryFragment directoryFragment;
    private DirectoryPresenter directoryPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        directoryFragment = (DirectoryFragment) getFragmentManager()
                .findFragmentById(R.id.fragment);
        StorageRepository repository = new ExternalStorageRepository(new ExternalStorageRepository.SimpleFileFilter());
        directoryPresenter = new DirectoryPresenter(repository,this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        appRequiresStoragePermission();
    }

    @AfterPermissionGranted(REQUEST_STORAGE_PERMISSION)
    private void appRequiresStoragePermission(){
        Log.d(TAG,"appRequiresStoragePermission()");
        if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            directoryPresenter.listCurrentDirectory();
        }else {
            EasyPermissions.requestPermissions(this,getString(R.string.rationale_storage_permission),REQUEST_STORAGE_PERMISSION,Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(List<String> perms) {}

    @Override
    public void onPermissionsDenied(List<String> perms) {
        Snackbar.make(findViewById(android.R.id.content),getString(R.string.rationale_storage_permission),Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EasyPermissions.requestPermissions(FileBrowser.this,getString(R.string.rationale_storage_permission),REQUEST_STORAGE_PERMISSION,Manifest.permission.READ_EXTERNAL_STORAGE);
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .show();
    }

    @Override
    public void onDirectoryItemSelected(DirectoryItem item) {
        directoryPresenter.selectDirectoryItem(item);
    }

    @Override
    public void onBackPressed() {
        if(directoryPresenter.isParentDirectory()){
            super.onBackPressed();
        }else {
            directoryPresenter.listPreviousDirectory();
        }
    }


    @Override
    public void onDirectoryPreviousSelected() {}

    @Override
    public void showDirectoryLoading(boolean show) {
    }

    @Override
    public void showDirectory(String path, DirectoryItem[] items) {
        setTitle(path);
        directoryFragment.refresh(Arrays.asList(items));
    }

    @Override
    public void showDirectoryError(String path, String message) {

    }

    @Override
    public void showDirectoryItem(DirectoryItem item) {
        //let the device handle looking for an app that can view this file type
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(item.getFile()), URLConnection.guessContentTypeFromName(item.getFile().getAbsolutePath()));
        startActivity(intent);
    }
}
