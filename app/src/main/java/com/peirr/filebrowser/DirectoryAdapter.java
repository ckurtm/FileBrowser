package com.peirr.filebrowser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.peirr.filebrowser.dir.ExternalStorageRepository;
import com.peirr.filebrowser.dir.model.DirectoryItem;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DirectoryItem} and makes a call to the
 * specified {@link OnDirectoryItemSelectionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.ViewHolder> {

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDirectoryItemSelectionListener {
        void onDirectoryItemSelected(DirectoryItem item);
        void onDirectoryPreviousSelected();
    }


    private final List<DirectoryItem> items;
    private final OnDirectoryItemSelectionListener listener;
    private Context context;
    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyy");

    public DirectoryAdapter(List<DirectoryItem> items, OnDirectoryItemSelectionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.directory_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = items.get(position);
        holder.name.setText(holder.item.getFile().getName());
        holder.modified.setText(DateUtils.getRelativeDateTimeString(context,holder.item.getFile().lastModified(),DateUtils.MINUTE_IN_MILLIS,DateUtils.WEEK_IN_MILLIS,0));

        if(holder.item.getFile().isDirectory()){
            Glide.with(context)
                    .load(R.drawable.ic_file_folder)
                    .centerCrop()
                    .crossFade()
                    .into(holder.icon);
        }else {
            int typePlaceholder = R.drawable.ic_file_audio;
            if(holder.item.getType() == ExternalStorageRepository.SimpleFileFilter.TYPE_VIDEO){
                typePlaceholder = R.drawable.ic_file_video;
            }else if(holder.item.getType() == ExternalStorageRepository.SimpleFileFilter.TYPE_IMAGE){
                typePlaceholder = R.drawable.ic_file_image;
            }

            Glide.with(context)
                    .load(holder.item.getFile().getPath())
                    .centerCrop()
                    .placeholder(typePlaceholder)
                    .crossFade()
                    .into(holder.icon);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onDirectoryItemSelected(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView name,modified;
        public final ImageView icon;
        public DirectoryItem item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.directory_name);
            modified = (TextView) view.findViewById(R.id.directory_date);
            icon = (ImageView) view.findViewById(R.id.directory_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }
}
