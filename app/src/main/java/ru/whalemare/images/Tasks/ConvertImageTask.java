package ru.whalemare.images.Tasks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import ru.whalemare.images.Data;

public class ConvertImageTask extends AsyncTask<Void, Integer, Bitmap> {

    private static final String TAG = "WHALETAG";

    private static final int TYPE_ROTATE = 0;
    private static final int TYPE_INVERT = 1;
    private static final int TYPE_MIRROR = 2;

    private int type = -1;

//    private final WeakReference<ImageView> imageViewReference;

    ProgressBar bar;
    ImageView image;


    final Data item;
    public ConvertImageTask(Data item) {
        this.item = item;
//        imageViewReference = new WeakReference<ImageView>(item.getImageView());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int i = values[0];
        item.setProgress(i);
        ProgressBar bar = item.getProgressBar();
        if (bar != null) {
            bar.setProgress(item.getProgress());
            bar.invalidate();
        }
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        item.setState(Data.ConvertingState.IN_PROGRESS);
        Bitmap bitmap = item.getBimapForConverting();

        switch(item.getType()){
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
        for (int i=0; i <= item.getTimeout(); ++i) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(i);
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) throws NullPointerException {
        super.onPostExecute(bitmap);
        Log.d(TAG, "onPostExecute: закончилась конвертация типа " + item.getType());
        item.setBitmapOut(bitmap);
        item.setState(Data.ConvertingState.COMPLETE);
        item.getImageView().setImageBitmap(bitmap);
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
