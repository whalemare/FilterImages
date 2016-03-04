package ru.whalemare.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

public class ConvertImageTask extends AsyncTask<Bitmap, Integer, Bitmap> {

    private static final String TAG = "WHALETAG";

    private static final int TYPE_ROTATE = 0;
    private static final int TYPE_INVERT = 1;
    private static final int TYPE_MIRROR = 2;

    private final Random random = new Random();
    private ImageView image;
    private LinearLayout layout;
    private Context context;
    private int type = -1;


    /**
     * Такс для работы с изображением. Позволяет поворачивать его на 90 градусов, инвертировать цвета и <br>
     *     отражать по горизонтали.
     * @param image ImageView куда должен сеттится готовый bitmap
     * @param type тип конвертации: <br>
     *             0 - поворот на 90<br>
     *             1 - инвертация цветов <br>
     *             2 - отражение по горизонтали
     * @param layout лайаут, в котором будут находиться данные о прогрессе
     * @param context для создания индикаторов
     */
    public ConvertImageTask(ImageView image, int type, LinearLayout layout, Context context){
        this.image = image;
        this.type = type;
        this.layout = layout;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Button button = new Button(context);
        button.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        layout.addView(button);
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

        int count = (random.nextInt(28) + 3); // диапазон [3;30]
        Log.d(TAG, "doInBackground: задержка в " + count + " секунд.");
        for (int i=1; i<=count; i++) {
            Log.d(TAG, "doInBackground: осталось " + (count-i));
            try {
                // тут паблишПрогресс
                Thread.sleep(1000); // спим 1 секунду
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "doInBackground: вышли из сна");
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.d(TAG, "onPostExecute: закончилась конвертация типа " + type);
        image.setImageBitmap(bitmap);
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
