package ru.whalemare.images;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Data {

    private final static String TAG = "WHALETAG";

    public enum ConvertingState{
        NOT_STARTED,
        QUEUED,
        IN_PROGRESS,
        COMPLETE
    }

    private volatile ConvertingState state = ConvertingState.NOT_STARTED; // состояние конвертации
    private final Bitmap bimapForConverting; // битмап для конвертации
    private final int type; // тип конвертации

    private volatile ProgressBar progressBar; // прогресс бар
    private volatile Integer progress = 0; // состояние прогресс бара
    private Integer timeout = 100; // время конвертации

    private volatile ImageView imageView; // изображение куда будем сеттить bitmap
    private volatile Bitmap bitmapOut; // битмап после конвертации

    /**
     * Для хранения данных о конвертируемом элемента
     * @param bimapForConverting - bitmap для конвертации
     * @param timeout - время, за которое сконвертируется bitmap
     * @param type - тип конвертации, где <br>
     *             0 - поворот <br>
     *             1 - инверсия <br>
     *             2 - зеркальное отражение.
     */
    public Data(Bitmap bimapForConverting, Integer timeout, int type) {
        this.bimapForConverting = bimapForConverting;
        this.timeout = timeout;
        this.type = type;
    }

    public Bitmap getBitmapOut() {
        return bitmapOut;
    }

    public void setBitmapOut(Bitmap bitmapOut) {
        this.bitmapOut = bitmapOut;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public ConvertingState getState() {
        return state;
    }

    public void setState(ConvertingState state) {
        this.state = state;
    }

    public Bitmap getBimapForConverting() {
        return bimapForConverting;
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

    public Integer getTimeout() {
        return timeout;
    }

    public int getType() {
        return type;
    }
}
