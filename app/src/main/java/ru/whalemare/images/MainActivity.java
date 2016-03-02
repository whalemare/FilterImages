package ru.whalemare.images;

import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WHALETAG";
    ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        image = (ImageView) findViewById(R.id.imageView_mainImage);
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
