package com.peirr.filebrowser;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peirr.filebrowser.dir.model.DirectoryItem;

import java.util.List;

/**
 * Created by kurt on 2016/02/22.
 */
public class DirectoryFragment extends Fragment {
    private RecyclerView recycler;
    private DirectoryAdapter.OnDirectoryItemSelectionListener clickListener;

    @Override
    public void onAttach(Activity activity) {
        try{
            clickListener = (DirectoryAdapter.OnDirectoryItemSelectionListener) activity;
        } catch (ClassCastException e){
            throw new RuntimeException(activity.toString() + " must implement OnDirectoryItemSelectionListener");
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_directory, container, false);
        recycler = (RecyclerView) view.findViewById(R.id.directory_list);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clickListener = null;
    }

    public void refresh(List<DirectoryItem> items){
        DirectoryAdapter adapter = new DirectoryAdapter(items, clickListener);
        recycler.setAdapter(adapter);
    }
}
