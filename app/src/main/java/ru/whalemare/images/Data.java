package ru.whalemare.images;

import android.graphics.Bitmap;

public class Data {

    Bitmap bitmap; // изображение для конвертации
    int type; // тип конвертации

    public Data(Bitmap bitmap, int type) {
        this.bitmap = bitmap;
        this.type = type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
