package ru.whalemare.images;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    Context context;
    List<Data> dataList;

    public ListAdapter(Context context, List<Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder{
        ImageView image;
        ProgressBar progressBar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = convertView;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_progress, null);

            holder = new ViewHolder();
            holder.image = (ImageView) v.findViewById(R.id.itemProgress_previewImage);
            holder.progressBar = (ProgressBar) v.findViewById(R.id.itemProgress_progressBar);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (holder.progressBar.getProgress() == 0 && holder.progressBar.getVisibility() == View.VISIBLE) {
            ConvertImageTask convertImageTask = new ConvertImageTask(holder.progressBar, holder.image, dataList.get(position).getType());
            convertImageTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, dataList.get(position).getBitmap());
        }

        return v;
    }
}
