package com.gillya.cocircle;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class SettingsActivity extends Activity implements View.OnClickListener {
    private Button soundOnOffButton;
    private Button soundVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);

        soundOnOffButton = (Button)findViewById(R.id.soundOn);
        if (MainActivity.soundOn) soundOnOffButton.setBackgroundResource(R.drawable.sound_on_button);
        else soundOnOffButton.setBackgroundResource(R.drawable.sound_off_button);
        soundOnOffButton.setOnClickListener(this);

        Button volumeDownButton = (Button)findViewById(R.id.buttonVolumeDown);
        volumeDownButton.setOnClickListener(this);

        soundVolume = (Button)findViewById(R.id.Volume);
        initiateVolumeImage();

        Button volumeUpButton = (Button)findViewById(R.id.buttonVolumeUp);
        volumeUpButton.setOnClickListener(this);

        Button saveButton = (Button)findViewById(R.id.buttonBack);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.soundOn: {
                if (MainActivity.soundOn) {
                    MainActivity.soundOn = false;
                    soundOnOffButton.setBackgroundResource(R.drawable.sound_off_button);
                }
                else {
                    MainActivity.soundOn = true;
                    soundOnOffButton.setBackgroundResource(R.drawable.sound_on_button);
                }
            }
            break;

            case R.id.buttonVolumeDown: {
                if (MainActivity.volumeIndex <=0) {
                    MainActivity.volumeIndex = 0;
                } else {
                    MainActivity.volumeIndex --;
                    MainActivity.soundVolume = MainActivity.volumes[MainActivity.volumeIndex];
                    initiateVolumeImage();
                }
                if (MainActivity.soundOn) {
                    MainActivity.soundPool.play(MainActivity.yepSound, MainActivity.soundVolume,
                            MainActivity.soundVolume, 1, 0, 1);
                }
            }
            break;

            case R.id.buttonVolumeUp: {
                if (MainActivity.volumeIndex >= 9) {
                    MainActivity.volumeIndex = 9;
                } else {
                    MainActivity.volumeIndex ++;
                    MainActivity.soundVolume = MainActivity.volumes[MainActivity.volumeIndex];
                    initiateVolumeImage();
                }
                if (MainActivity.soundOn) {
                    MainActivity.soundPool.play(MainActivity.yepSound, MainActivity.soundVolume,
                            MainActivity.soundVolume, 1, 0, 1);
                }
            }
            break;

            case R.id.buttonBack: {
                MainActivity.saveSettings();
                finish();
            }
            break;

            default:
                break;
        }
    }

    public void initiateVolumeImage() {
        int image;
        switch (MainActivity.volumeIndex) {
            case 0: image = R.drawable.volume_1; break;
            case 1: image = R.drawable.volume_2; break;
            case 2: image = R.drawable.volume_3; break;
            case 3: image = R.drawable.volume_4; break;
            case 4: image = R.drawable.volume_5; break;
            case 5: image = R.drawable.volume_6; break;
            case 6: image = R.drawable.volume_7; break;
            case 7: image = R.drawable.volume_8; break;
            case 8: image = R.drawable.volume_9; break;
            case 9: image = R.drawable.volume_10; break;
            default: image = R.drawable.volume_5; break;
        }
        soundVolume.setBackgroundResource(image);
    }
}
