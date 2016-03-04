package ru.whalemare.images;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Data> {

    String TAG = "WHALETAG";

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<Data> items) {
        super(context, resource, items);
    }

    private static class ViewHolder {
        ProgressBar progressBar;
        ImageView imageView;
        Data data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        final Data item = getItem(position);

        ViewHolder holder = null;

        if (row == null) {
            Log.d(TAG, "getView: row == null");
            LayoutInflater vi = LayoutInflater.from(getContext());
            row = vi.inflate(R.layout.item_progress, null);

            holder = new ViewHolder();
            holder.progressBar = (ProgressBar) row.findViewById(R.id.itemProgress_progressBar);
            holder.imageView = (ImageView) row.findViewById(R.id.itemProgress_previewImage);
            holder.data = item;

            row.setTag(holder);
        } else {
            Log.d(TAG, "getView: row != null");
            holder = (ViewHolder) row.getTag();

            holder.data.setProgressBar(null);
            holder.data = item;
            holder.data.setProgressBar(holder.progressBar);
        }

        holder.progressBar.setProgress(item.getProgress()); // FIXME: 04.03.2016 прогресс нигде не сееттится
        holder.progressBar.setMax(item.getTimeout());
        item.setProgressBar(holder.progressBar);

        if (holder.data.getState() == Data.ConvertedState.NOT_STARTED) {
            Log.d(TAG, "getView: запустим таск");
            item.setConvertedState(Data.ConvertedState.QUEUED);
            ConvertImageTask convertImage = new ConvertImageTask(item);
            convertImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getBitmap()); // FIXME: 04.03.2016 только для апи выше 11
        } else {
            Log.d(TAG, "getView: откажем в запуске таска");
        }


//        if (item != null) {
//
//
//            if (progressBar != null) {
////                ConvertImageTask convertImage = new ConvertImageTask(item.getType(), progressBar, imageView, item.getTimeout());
////                convertImage.execute(item.getBitmap());
//            }
//
//
//
//            if (imageView != null){
//                Log.d(TAG, "getView: imageview = " + item.getBitmap());
//                imageView.setVisibility(View.VISIBLE);
//                imageView.setImageBitmap(item.getBitmap());
//            }
//        }

        return row;
    }

}
