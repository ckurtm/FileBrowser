package com.peirr.filebrowser;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;
import com.peirr.filebrowser.dir.DirectoryContract;
import com.peirr.filebrowser.dir.DirectoryPresenter;
import com.peirr.filebrowser.dir.ExternalStorageRepository;
import com.peirr.filebrowser.dir.StorageRepository;
import com.peirr.filebrowser.dir.model.DirectoryItem;
import com.peirr.http.mvp.HttpContract;
import com.peirr.http.mvp.HttpPresenter;
import com.peirr.http.mvp.HttpServer;
import com.peirr.http.mvp.IServerRequest;
import com.peirr.http.service.SimpleHttpInfo;
import com.peirr.http.service.SimpleHttpService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class BrowseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks ,DirectoryAdapter.OnDirectoryItemSelectionListener , DirectoryContract.DirectoryView, HttpContract.View {
    private static final String TAG = "BrowseActivity";
    private final int REQUEST_STORAGE_PERMISSION = 1;
    private DirectoryFragment directoryFragment;
    private DirectoryPresenter directoryPresenter;
    private HttpPresenter httpPresenter;

    SimpleHttpInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        directoryFragment = (DirectoryFragment) getFragmentManager().findFragmentById(R.id.fragment);
        StorageRepository repository = new ExternalStorageRepository(new ExternalStorageRepository.SimpleFileFilter());
        directoryPresenter = new DirectoryPresenter(repository,this);
        IServerRequest server = new HttpServer(this, SimpleHttpService.generatePort());
        httpPresenter = new HttpPresenter(server, this);
        startService(new Intent(this,SimpleHttpService.class));

        DataCastManager.getInstance().addDataCastConsumer(new CastDataMessageListener(){

            @Override
            public void onConnected() {
                Log.d(TAG, "onConnected() : " + "");
                httpPresenter.connect();
            }

            @Override
            public void onDisconnected() {
                Log.d(TAG, "onDisconnected() : " + "");
                httpPresenter.disconnect();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);
        DataCastManager.getInstance().addMediaRouterButton(menu,R.id.action_cast);
        return true;
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
                        EasyPermissions.requestPermissions(BrowseActivity.this,getString(R.string.rationale_storage_permission),REQUEST_STORAGE_PERMISSION,Manifest.permission.READ_EXTERNAL_STORAGE);
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
        Log.d(TAG, "showDirectoryItem() : ");
        if (info != null) {
            JSONObject obj = new JSONObject();
            String url = "http://"+ info.ip + ":" + info.port + "/" + item.getFile().getAbsolutePath();
            Log.d(TAG, "casting: " + url);
            try {
                obj.put("url",url);
                DataCastManager.getInstance().sendDataMessage(obj.toString(),"urn:x-cast:com.peirr.imagecast");
            } catch (JSONException|IOException e) {
                e.printStackTrace();
            }
        }else{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(item.getFile()), URLConnection.guessContentTypeFromName(item.getFile().getAbsolutePath()));
            startActivity(intent);
        }
    }

    @Override
    public void showHttpStatus(int status, SimpleHttpInfo info) {
        Log.d(TAG, "showHttpStatus() : " + "status = [" + status + "], info = [" + info + "]");
        switch (status){
            case SimpleHttpService.STATE_STOPPED:
                this.info = null;
                httpPresenter.bootup();
                break;
            case SimpleHttpService.STATE_ERROR:
                this.info = null;
                break;
            case SimpleHttpService.STATE_RUNNING:
                this.info = info;
                break;
        }

    }
}
