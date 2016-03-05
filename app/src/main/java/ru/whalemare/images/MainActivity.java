package ru.whalemare.images;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WHALETAG";
    private static final String SHARED_TAG = "WHALE";
    private static String image_path = "0";

    private static final int CAMERA_RESULT = 0;
    private static final int GALLERY_RESULT = 1;

    int timeout = 0;

    private final Random random = new Random();

    SharedPreferences shared;
    static ImageView image; // главная фотография
    Button downloadImage; // кнопка загрузки фотографии
    static ListView listView;
    static ListAdapter adapter;
    static List<Data> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        shared = getSharedPreferences(SHARED_TAG, MODE_PRIVATE);

        image = (ImageView) findViewById(R.id.imageView_mainImage);
        downloadImage = (Button) findViewById(R.id.button_downloadImage);
        listView = (ListView) findViewById(R.id.listView);

        adapter = new ListAdapter(getApplicationContext(), dataList);
        listView.setAdapter(adapter);
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
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap(); // изображение для конвертации
                    dataList.add(new Data(bitmap, 0));
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Нет изображения", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_invertColors:
                if (image.getVisibility() == View.VISIBLE) {
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    dataList.add(new Data(bitmap, 1));
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Нет изображения", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_mirror:
                if (image.getVisibility() == View.VISIBLE) {
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    dataList.add(new Data(bitmap, 2));
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Нет изображения", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup); // работает на 2.3.7

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO ОБРАБОТАТЬ СЛИШКОМ БОЛЬШИЕ КАРТИНКИ
                switch (item.getItemId()) {
                    case R.id.popup_onCamera:
                        Log.d(TAG, "onMenuItemClick: сделать снимок");
                        makePicture(CAMERA_RESULT);
                        return true;
                    case R.id.popup_onDevice:
                        Log.d(TAG, "onMenuItemClick: загрузить с девайса");
                        makePicture(GALLERY_RESULT);
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
            case CAMERA_RESULT:
                takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                break;
            case GALLERY_RESULT:
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

        switch (requestCode) {
            case CAMERA_RESULT:
                if (data.hasExtra("data")) {
                    Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
                    setImage(thumbnailBitmap);
                    try {
                        savePicture(thumbnailBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case GALLERY_RESULT:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    Uri selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bitmap != null) {
                        setImage(bitmap);
                        Log.d(TAG, "onActivityResult: selectedImage путь: " + selectedImage.getPath());
//                    shared.edit().putString("imagepath", selectedImage+"").commit(); // сохраним путь хранения изображения
                    } else
                        Toast.makeText(MainActivity.this, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }

    private void setImage(Bitmap bitmap) {
        // FIXME: 03.03.2016 если кнопка gone он повтороно убирает ее в gone
        downloadImage.setVisibility(View.GONE); // уберем кнопку
        image.setVisibility(View.VISIBLE); // и поставим на ее место изображение
        image.setImageBitmap(bitmap);
    }

    private void savePicture(Bitmap bitmap) throws IOException {
        Log.d(TAG, "savePicture: сохраняем картинку");
        int filename = shared.getInt("counter", 0);
        File sd = Environment.getExternalStorageDirectory(); // сохраняем на внутреннюю память todo сделать сохранение в папку приложения
        File destination = new File(sd, filename+".png");

        FileOutputStream out = new FileOutputStream(destination);
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
        out.flush();
        out.close();
        shared.edit().putInt("counter", ++filename).commit();
        shared.edit().putString("imagepath", destination + (filename + ".png")).commit(); // сохраним путь хранения изображения
        // TODO: 02.03.2016 добавить регистрацию в Галерее
        // http://www.cyberforum.ru/android-dev/thread1584902.html
    }

    private void downloadImage(String URL){
        DownloadImageTask download = new DownloadImageTask(image, downloadImage);
        download.execute(URL);
    }

/*    convertImage = new ConvertImageTask(image, 0, MainActivity.this, adapter);
    convertImage.execute(bitmap);*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: сохраняем данные");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: загружаем данные");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}
