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
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ConvertImageTask extends AsyncTask<Bitmap, Integer, Bitmap> {

    private static final String TAG = "WHALETAG";

    private static final int TYPE_ROTATE = 0;
    private static final int TYPE_INVERT = 1;
    private static final int TYPE_MIRROR = 2;

    private int type = -1;
    private ProgressBar progressBar;
    private ImageView image;

    int timeout;



//    /**
//     * Такс для работы с изображением. Позволяет поворачивать его на 90 градусов, инвертировать цвета и <br>
//     *     отражать по горизонтали.
//     * @param type тип конвертации: <br>
//     *             0 - поворот на 90<br>
//     *             1 - инвертация цветов <br>
//     *             2 - отражение по горизонтали
//     * @param progressBar для отображение прогресса
//     * @param image ImageView куда должен сеттится готовый bitmap
//     */
////    public ConvertImageTask(int type, ProgressBar progressBar, ImageView image, int timeout){
////        this.type = type;
////        this.progressBar = progressBar;
////        this.image = image;
////        this.timeout = timeout;
////    }

    final Data item;
    /**
     * Все необхоидмые данные для работы
     * @param item
     */
    public ConvertImageTask(Data item) {
        this.item = item;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        item.setConvertedState(Data.ConvertedState.IN);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int i = values[0];
        Log.d(TAG, "onProgressUpdate: прогресс " + i);
        item.setProgress(i);
        ProgressBar bar = item.getProgressBar();
        if (bar != null){
            bar.setProgress(item.getProgress());
            bar.invalidate();
        }
    }

    @Override
    protected Bitmap doInBackground(Bitmap... bitmaps) {

        Bitmap bitmap = bitmaps[0];

        switch(type){
            case TYPE_ROTATE:
                bitmap = invertBitmap(bitmap);
                break;
            case TYPE_INVERT:
                bitmap = invertBitmap(bitmap);
                break;
            case TYPE_MIRROR:
                bitmap = mirrorBitmap(bitmap);
                break;
        }

        item.setConvertedState(Data.ConvertedState.IN);
        for (int i=1; i<=item.getTimeout(); i++) {
            Log.d(TAG, "doInBackground: осталось " + (item.getTimeout()-i));
            try {
                Thread.sleep(1000); // спим 1 секунду
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(i);
        }

        Log.d(TAG, "doInBackground: вышли из сна");
        item.setConvertedState(Data.ConvertedState.YES);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.d(TAG, "onPostExecute: закончилась конвертация типа " + item.getType());
        item.setOutBitmap(bitmap);
        item.setConvertedState(Data.ConvertedState.YES);
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
