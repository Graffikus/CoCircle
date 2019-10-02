package com.gillya.cocircle;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
public class StartActivity extends Activity implements OnClickListener {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // портретная ориентация
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // полноэкранность
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // без заголовка
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_start);
        //setContentView(new GameView(this,null));

        Button twoPlayersButton = (Button)findViewById(R.id.button2);
        twoPlayersButton.setOnClickListener(this);

        Button threePlayersButton = (Button)findViewById(R.id.button3);
        threePlayersButton.setOnClickListener(this);

        Button fourPlayersButton = (Button)findViewById(R.id.button4);
        fourPlayersButton.setOnClickListener(this);

        Button aboutButton = (Button)findViewById(R.id.about);
        aboutButton.setOnClickListener(this);

        Button exitButton = (Button)findViewById(R.id.buttonExit);
        exitButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2: {
                Game.playersAmount = 2;
                start();
            }break;

            case R.id.button3: {
                Game.playersAmount = 3;
                start();
            }break;

            case R.id.button4: {
                Game.playersAmount = 4;
                start();
            }break;

            case R.id.about: {
                startActivity(new Intent(StartActivity.this, AboutActivity.class));
            }break;

            case R.id.buttonExit: {
                finish();
            }break;

            default:
                break;
        }
    }

    public void start() {
        MainActivity.count = 0;
        MainActivity.isPaused = false;
        startActivity(new Intent(StartActivity.this, MainActivity.class));
    }
}