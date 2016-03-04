package ru.whalemare.images;

import android.graphics.Bitmap;
import android.widget.ProgressBar;

public class Data {

    Bitmap bitmap; // битмап для конвертации
    Bitmap outBitmap; // битмап после конвертации
    int timeout; // время ожидания
    int type; // тип конвертирования: 0 поворот, 1 инверсия, 2 отражение
    volatile private ProgressBar progressBar; // прогресс бар
    volatile private Integer progress; // заполненность бара
    volatile private ConvertedState state = ConvertedState.NOT_STARTED; // состояние конвертации


    public Data(Bitmap bitmap, int timeout, int type) {
        this.bitmap = bitmap;
        this.timeout = timeout;
        this.type = type;

        this.outBitmap = null;
        this.progressBar = null;
        this.progress = 0;
    }

    public enum ConvertedState {
        NOT_STARTED,
        QUEUED,
        IN,
        YES;
    }

    public void setConvertedState(ConvertedState state) {
        this.state = state;
    }

    public ConvertedState getState(){
        return this.state;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getOutBitmap() {
        return outBitmap;
    }

    public void setOutBitmap(Bitmap outBitmap) {
        this.outBitmap = outBitmap;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getType() {
        return type;
    }
}
