package ru.whalemare.images;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.List;

import ru.whalemare.images.Tasks.ConvertImageTask;

public class ListAdapter extends ArrayAdapter<Data> {

    String TAG = "WHALETAG";

    Context context;
    ImageView imageOut;

    public ListAdapter(Context context, int resource, List<Data> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    private static class ViewHolder {
        ProgressBar progressBar;
        ImageView imageView;
        int color;
        Data item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        final Data item = getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_progress, parent, false);

            if (position % 2 == 0)
            {
                item.setColor(context.getResources().getColor(R.color.firstColor));
            }

            holder = new ViewHolder();
            holder.progressBar = (ProgressBar) row.findViewById(R.id.itemProgress_progressBar);
            holder.imageView = (ImageView) row.findViewById(R.id.itemProgress_previewImage);
            holder.item = item;

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();

            holder.item.setProgressBar(null);
//            holder.item.getProgressBar().setVisibility(View.VISIBLE);
            holder.item.setImageView(null);
            holder.item.setColor(-1);
            holder.item = item;
            holder.item.setProgressBar(holder.progressBar);
            holder.item.setImageView(holder.imageView);
            holder.item.setColor(holder.color);
            if (holder.color != -1)
                row.setBackgroundColor(holder.color);
        }

        holder.imageView.setImageBitmap(item.getBitmapOut());
        holder.progressBar.setMax(item.getTimeout());
        holder.progressBar.setProgress(item.getProgress());
        holder.color = item.getColor();

        item.setProgressBar(holder.progressBar);
        item.setImageView(holder.imageView);
        item.setColor(holder.color);

        if (holder.progressBar.getProgress() <= 0) {
            holder.progressBar.setVisibility(View.VISIBLE);
            row.setBackgroundColor(item.getColor());
            item.setState(Data.ConvertingState.QUEUED);
            ConvertImageTask convert = new ConvertImageTask(item);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                convert.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); // в версиях >= 11 есть многопоточность
            else
                convert.execute(); // < 11 нет
        }

        return row;
    }
}
