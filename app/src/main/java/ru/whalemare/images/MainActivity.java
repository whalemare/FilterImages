package ru.whalemare.images;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.whalemare.images.Tasks.DownloadImageTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WHALETAG";
    private static final String SHARED_TAG = "WHALE";
    private static final String KEY_MAIN_BITMAP = "MAIN_BITMAP";

    private static final int ACTION_CAMERA = 0;
    private static final int ACTION_GALLERY = 1;

    final Random random = new Random();
    int timeout; // время для конвертации изображения

    SharedPreferences shared; // для нумерования сделанных в приложении фотографий
    static ImageView image; // главная фотография
    Button downloadButton; // кнопка загрузки фотографии
    static ListView listView;
    static ListAdapter adapter;
    static List<Data> dataList = new ArrayList<>(); // данные о конвертации

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shared = getSharedPreferences(SHARED_TAG, MODE_PRIVATE);

        image = (ImageView) findViewById(R.id.imageView_mainImage);
        downloadButton = (Button) findViewById(R.id.button_downloadImage);
        listView = (ListView) findViewById(R.id.listView);

        Bitmap restoreBitmap; // сохраненный с предыдущего сеанса Bitmap
        if (savedInstanceState != null) { // загрузим данные об предыдущем изображении
            restoreBitmap = savedInstanceState.getParcelable(KEY_MAIN_BITMAP);
            if (restoreBitmap != null)
                setMainImage(restoreBitmap);
        }

        adapter = new ListAdapter(getApplicationContext(), R.id.listView, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                final ImageView imagePreview = (ImageView) view.findViewById(R.id.itemProgress_previewImage);
                final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.itemProgress_progressBar);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Что делаем?")
                        .setPositiveButton("Установить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO: 06.03.2016 если картинка еще обрабатывается, не сеттить ее
                                if (progressBar.getProgress() == progressBar.getMax()) {
                                    Bitmap bitmap = ((BitmapDrawable) imagePreview.getDrawable()).getBitmap();
                                    image.setImageBitmap(bitmap);
                                    Log.d(TAG, "onClick: установили изображение");
                                } else {
                                    Toast.makeText(MainActivity.this, "Изображение еще не обработано", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dataList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_downloadImage:
                showPopup(view);
                Log.d(TAG, "Нажали на кнопку загрузить изображение");
                break;
            case R.id.imageView_mainImage:
                showPopup(view);
                Log.d(TAG, "Нажали на изображение");
                break;
            case R.id.button_rotate:
                if (image.getVisibility() == View.VISIBLE) {
                    timeout = random.nextInt(271)+30;
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap(); // изображение для конвертации
                    dataList.add(new Data(bitmap, timeout, 0));
                    adapter.notifyDataSetChanged();
                    listView.smoothScrollToPosition(dataList.size());
                } else {
                    Toast.makeText(MainActivity.this, "Нет изображения", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_invertColors:
                if (image.getVisibility() == View.VISIBLE) {
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap(); // изображение для конвертации
                    timeout = random.nextInt(271)+30;
                    dataList.add(new Data(bitmap, timeout, 1));
                    adapter.notifyDataSetChanged();
                    listView.smoothScrollToPosition(dataList.size());
                } else {
                    Toast.makeText(MainActivity.this, "Нет изображения", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_mirror:
                if (image.getVisibility() == View.VISIBLE) {
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap(); // изображение для конвертации
                    timeout = random.nextInt(271)+30;
                    dataList.add(new Data(bitmap, timeout, 2));
                    adapter.notifyDataSetChanged();
                    listView.smoothScrollToPosition(dataList.size());
                } else {
                    Toast.makeText(MainActivity.this, "Нет изображения", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO ОБРАБОТАТЬ СЛИШКОМ БОЛЬШИЕ КАРТИНКИ
                switch (item.getItemId()) {
                    case R.id.popup_onCamera:
                        Log.d(TAG, "onMenuItemClick: сделать снимок");
                        makePicture(ACTION_CAMERA);
                        return true;
                    case R.id.popup_onDevice:
                        Log.d(TAG, "onMenuItemClick: загрузить с девайса");
                        makePicture(ACTION_GALLERY);
                        return true;
                    case R.id.popup_onInternet:
                        Log.d(TAG, "onMenuItemClick: Скачать из интернета");
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.download_dialog);
                        dialog.setTitle("Загрузка изображения");
                        Button ok = (Button) dialog.findViewById(R.id.downloadDialog_button_ok);
                        final EditText url = (EditText) dialog.findViewById(R.id.downloadDialog_editText_URL);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                downloadImage(url.getText().toString());
                                dialog.dismiss();
                            }
                        });
                        Button cancel = (Button) dialog.findViewById(R.id.downloadDialog_button_cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        return true;
                    default:
                        Log.d(TAG, "onMenuItemClick: ничего не выбрано");
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void makePicture(int actionCode){
        Intent takePicture;
        switch (actionCode) {
            case ACTION_CAMERA:
                takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                break;
            case ACTION_GALLERY:
                takePicture = new Intent(Intent.ACTION_PICK);
                takePicture.setType("image/*");
                break;
            default:
                takePicture = null;
        }
        if (takePicture !=  null)
            startActivityForResult(takePicture, actionCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;
        switch (requestCode) {
            case ACTION_CAMERA:
                if (data.hasExtra("data")) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    setMainImage(bitmap);
                    try {
                        savePicture(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ACTION_GALLERY:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (bitmap != null) {
                        setMainImage(bitmap);
                        Log.d(TAG, "onActivityResult: selectedImage путь: " + selectedImage.getPath());
                    } else {
                        Toast.makeText(MainActivity.this, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                break;
        }
    }

    /**
     * Установка изображения в основное ImageView. При этом скрывается кнопка, стоящая на ее месте
     * @param bitmap - изображение для вставки
     */
    private void setMainImage(Bitmap bitmap) {
        // FIXME: 03.03.2016 если кнопка gone он повтороно убирает ее в gone
        downloadButton.setVisibility(View.GONE); // уберем кнопку
        image.setImageBitmap(bitmap); // и поставим на ее место изображение
        image.setVisibility(View.VISIBLE);
    }

    /**
     * Сохранет bitmap как картинку в память устройства с именем *.png, где * - число 1, 2, 3 ... n, n+1, ... фотографии.
     * @param bitmap - сохраняем
     * @throws IOException
     */
    private void savePicture(Bitmap bitmap) throws IOException {
        Log.d(TAG, "savePicture: сохраняем картинку");
        int filename = shared.getInt("counter", 0);
        File sd = Environment.getExternalStorageDirectory(); // сохраняем на внутреннюю память todo сделать сохранение в папку приложения
        File destination = new File(sd, filename+".png");

        FileOutputStream out = new FileOutputStream(destination);
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
        out.flush();
        out.close();
        shared.edit().putInt("counter", ++filename).apply();
        shared.edit().putString("imagepath", destination + (filename + ".png")).apply(); // сохраним путь хранения изображения
        // TODO: 02.03.2016 добавить регистрацию в Галерее
        // http://www.cyberforum.ru/android-dev/thread1584902.html

        showEXIF(destination.toString());
    }

    private void showEXIF(String filename) throws IOException {
        ExifInterface exif = new ExifInterface(filename);

        String atributs = "Информация по EXIF: =======\n";
        atributs += getTagString(ExifInterface.TAG_DATETIME, exif);
        atributs += getTagString(ExifInterface.TAG_FLASH, exif);
        atributs += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
        atributs += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        atributs += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
        atributs += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
        atributs += getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif);
        atributs += getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif);
        atributs += getTagString(ExifInterface.TAG_MAKE, exif);
        atributs += getTagString(ExifInterface.TAG_MODEL, exif);
        atributs += getTagString(ExifInterface.TAG_ORIENTATION, exif);
        atributs += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);
        Log.d(TAG, atributs);
    }

    private String getTagString(String tag, ExifInterface exif){
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (image.getVisibility() == View.VISIBLE) {
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            outState.putParcelable(KEY_MAIN_BITMAP, bitmap);
        }
    }

    /**
     * Запускает AsyncTask скачивание картинки по URL
     * @param URL - ссылка на картинку
     */
    private void downloadImage(String URL){
        DownloadImageTask download = new DownloadImageTask(image, downloadButton, MainActivity.this);
        download.execute(URL);
    }
}
