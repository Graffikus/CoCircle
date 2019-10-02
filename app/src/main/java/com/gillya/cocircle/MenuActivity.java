package com.gillya.cocircle;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MenuActivity extends Activity implements OnClickListener {
    static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // портретная ориентация
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // полноэкранность
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // и без заголовка
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_menu);

        Button returnButton = (Button)findViewById(R.id.buttonReturn);
        returnButton.setOnClickListener(this);

        Button settingsButton = (Button)findViewById(R.id.buttonSettings);
        settingsButton.setOnClickListener(this);

        Button mainMenuButton = (Button)findViewById(R.id.buttonMainMenu);
        mainMenuButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonReturn: {
                MainActivity.isPaused = false;
                finish();
            }
            break;

            case R.id.buttonSettings: {
                startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
            }
            break;

            case R.id.buttonMainMenu: {
                Intent intent = new Intent(MenuActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            break;

            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.out.println("!!!!!!!!!!!!!!!!!!! Back");
            MainActivity.isPaused = false;
            finish();
        }
        return true;
    }
}
