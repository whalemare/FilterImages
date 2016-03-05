package ru.whalemare.images;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    String TAG = "WHALETAG";
    Context context;
    List<Data> dataList;

    public ListAdapter(List<Data> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(dataList.get(position).getBitmap());
        holder.imageView.setVisibility(View.VISIBLE);
        holder.progressBar.setProgress(dataList.get(position).getProgress());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.itemProgress_progressBar);
            imageView = (ImageView) itemView.findViewById(R.id.itemProgress_previewImage);
            itemView.setTag(itemView);
        }
    }
}
