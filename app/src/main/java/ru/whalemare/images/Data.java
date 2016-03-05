package ru.whalemare.images;

import android.graphics.Bitmap;

public class Data {

    Bitmap bitmap; // изображение после конвертации
    int progress = 20;

    public Data(Bitmap bitmap, int progress) {
        this.bitmap = bitmap;
        this.progress = progress;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
