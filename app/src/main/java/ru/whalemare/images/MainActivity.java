package ru.whalemare.images;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WHALETAG";
    private static final String SHARED_TAG = "WHALE";

    private static final int CAMERA_RESULT = 0;

    SharedPreferences shared;
    ImageView image; // главная фотография
    Button download; // кнопка загрузки фотографии


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        shared = getSharedPreferences(SHARED_TAG, MODE_PRIVATE);

        image = (ImageView) findViewById(R.id.imageView_mainImage);
        download = (Button) findViewById(R.id.button_downloadImage);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_downloadImage:
                showPopup(view);
                Log.d(TAG, "Нажали загрузить изображение");
                break;
            case R.id.imageView_mainImage:
                break;
            case R.id.button_rotate:
                break;
            case R.id.button_invertColors:
                break;
            case R.id.button_mirror:
                break;
        }
    }

    private void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup); // работает на 2.3.7

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.popup_onCamera:
                        Toast.makeText(MainActivity.this, "Сделать снимок", Toast.LENGTH_SHORT).show();
                        takePicture(CAMERA_RESULT);
                        return true;
                    case R.id.popup_onDevice:
                        Toast.makeText(MainActivity.this, "Загрузить с девайса", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.popup_onInternet:
                        Toast.makeText(MainActivity.this, "Скачать из интернета", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        Toast.makeText(MainActivity.this, "Ничего не выбрано", Toast.LENGTH_SHORT).show();
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void takePicture(int actionCode){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, actionCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_RESULT && data.hasExtra("data")) {
            Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
            download.setVisibility(View.GONE); // уберем кнопку
            image.setVisibility(View.VISIBLE); // и поставим на ее место изображение
            image.setImageBitmap(thumbnailBitmap);
            try {
                savePicture(thumbnailBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        // TODO: 02.03.2016 добавить регистрацию в Галерее
    }

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
