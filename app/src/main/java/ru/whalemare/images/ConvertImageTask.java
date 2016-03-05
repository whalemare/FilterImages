package ru.whalemare.images;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Random;

public class ConvertImageTask extends AsyncTask<Bitmap, Integer, Bitmap> {

    private static final String TAG = "WHALETAG";
    Random random = new Random();
    int timeout = random.nextInt(5)+2; // для искусственного замедления конвертации

    private static final int TYPE_ROTATE = 0;
    private static final int TYPE_INVERT = 1;
    private static final int TYPE_MIRROR = 2;

    private int type = -1;

    ProgressBar bar;
    ImageView image;
    public ConvertImageTask(ProgressBar bar, ImageView imageView, int type) {
        this.bar = bar;
        this.image = imageView;
        this.type = type;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar.setMax(timeout);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int i = values[0];
        bar.setProgress(i);
    }

    @Override
    protected Bitmap doInBackground(Bitmap... bitmaps) {

        Bitmap bitmap = bitmaps[0];

        switch(type){
            case TYPE_ROTATE:
                bitmap = rotateBitmap(bitmap);
                break;
            case TYPE_INVERT:
                bitmap = invertBitmap(bitmap);
                break;
            case TYPE_MIRROR:
                bitmap = mirrorBitmap(bitmap);
                break;
        }

        // Искусственно замедляем конвертацию
        for (int i=1; i<=timeout; i++) {
            Log.d(TAG, "doInBackground: осталось " + (timeout-i));
            try {
                Thread.sleep(1000); // спим 1 секунду
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(i);
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.d(TAG, "onPostExecute: закончилась конвертация типа " + type);
        bar.setVisibility(View.GONE);
        image.setImageBitmap(bitmap);
        image.setVisibility(View.VISIBLE);
    }

    private Bitmap rotateBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap invertBitmap(Bitmap bitmap) {
        ColorMatrix inverted = new ColorMatrix(new float[]{
                -1,  0,  0,  0, 255,
                0, -1,  0,  0, 255,
                0,  0, -1,  0, 255,
                0,  0,  0,  1,   0
        });
        ColorFilter filter = new ColorMatrixColorFilter(inverted);

        Bitmap image = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap mutableImage = image.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableImage);
        Paint paint = new Paint();

        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return mutableImage;
    }

    private Bitmap mirrorBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
