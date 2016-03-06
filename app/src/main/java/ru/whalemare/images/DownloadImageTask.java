package ru.whalemare.images;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private final ImageView image;
    private final Button button; // то, что нужно будет скрыть
    private final Context context;
    private ProgressDialog dialog;

    private final String TAG = "WHALETAG";

    public DownloadImageTask(ImageView image, Button button, Context context){
        this.image = image;
        this.button = button;
        this.context = context;
        this.dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setTitle("Идет скачивание");
        dialog.setMessage("Это не займет много времени");
        dialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        Log.d(TAG, "doInBackground: url = " + url);
        Bitmap bitmap = null;

        try {
            InputStream in = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.d(TAG, "onPostExecute: скачивание закончилсь bitmap = " + bitmap);

        button.setVisibility(View.GONE);
        image.setImageBitmap(bitmap);
        image.setVisibility(View.VISIBLE);

        if (dialog.isShowing())
            dialog.dismiss();
    }
}
