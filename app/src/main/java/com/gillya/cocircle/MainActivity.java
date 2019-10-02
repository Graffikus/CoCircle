package com.gillya.cocircle;

import android.app.Activity;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.*;

public class MainActivity extends Activity {
    private Game game;
    private Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean inTouch        = false;
    private boolean isGameCreated  = false;
    public static boolean isPaused = false;
    public static boolean soundOn  = true;
    public static float soundVolume;
    public static float[] volumes = {0.005f, 0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.07f, 0.2f, 0.8f, 1.0f}; // десять градаций уровня звука
    public static int volumeIndex = 9;
    public static MediaPlayer startSound;
    public static SoundPool soundPool;
    public static int yepSound, nopSound, winSound;
    public static int count = 0; //Счетчик итерраций
    public static SharedPreferences sPref;
    public final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREF_SOUND = "SoundOn";
    public static final String APP_PREF_VOLUME = "Volume";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (sPref.contains(APP_PREF_SOUND)) soundOn = sPref.getBoolean(APP_PREF_SOUND, true);
        if (sPref.contains(APP_PREF_VOLUME)) soundVolume = sPref.getInt(APP_PREF_VOLUME, 9);
        soundVolume = volumes[volumeIndex];
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new MySurfaceView(this));
        startSound = MediaPlayer.create(this, R.raw.start);
        startSound.setVolume(soundVolume, soundVolume);
        soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        yepSound   = soundPool.load(this, R.raw.yep, 1);
        nopSound   = soundPool.load(this, R.raw.nop, 1);
        winSound   = soundPool.load(this, R.raw.win, 1);
        p.setTextSize(64);
        p.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPaused) {
            goToMenu();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            startActivity(new Intent(MainActivity.this, MenuActivity.class));
            isPaused = true;
            goToMenu();
        }
        return true;
    }

    private void goToMenu() {
        startActivity(new Intent(MainActivity.this, MenuActivity.class));
    }

    class DrawThread extends Thread {
        private boolean running = false;
        private SurfaceHolder surfaceHolder;

        DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            if (!isGameCreated) {
                game = new Game(surfaceHolder.getSurfaceFrame().width(), surfaceHolder.getSurfaceFrame().height());
                isGameCreated = true;
            }
        }

        void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            long lastTime = System.nanoTime();
            long now;
            double ns = 1000000000 / Game.FPS;
            double delta = 0.0;
            while (running) {
                if (isPaused) continue;
                now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                if (delta >= 1)  {
                    if (count > Game.ROUND) count = 0;
                    canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas(null);
                        if (canvas == null) continue;
                        synchronized (surfaceHolder) {
                            canvas.drawRGB(0, 0, 0);
                                game.render(canvas, p, count);
                        }
                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                    delta--;
                    count++;
                }
            }
        }
    }

    class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
        private DrawThread drawThread;

        public MySurfaceView(Context context) {
            super(context);
            init();
        }

        public MySurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            getHolder().addCallback(this);
            setFocusable(true);
            p.setStrokeWidth(1);
            p.setAntiAlias(true);
            p.setDither(true);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int actionMask = event.getActionMasked();
//            int pointerIndex = event.getActionIndex();
//            int pointerID = event.getPointerId(pointerIndex);
            int pointerCount = event.getPointerCount();

            switch (actionMask) {
                case MotionEvent.ACTION_DOWN: // первое касание
                    inTouch = true;
                case MotionEvent.ACTION_POINTER_DOWN: // последующие касания
                    break;

                case MotionEvent.ACTION_UP: // прерывание последнего касания
                    inTouch = false;
                case MotionEvent.ACTION_POINTER_UP: // прерывания касаний
                    break;

                case MotionEvent.ACTION_MOVE: // движение
                    break;
            }
                if (inTouch) {
                    for (int i = 0; i < pointerCount; i++) {
//                        event.getPointerId(i);
                        game.onTouch(event.getX(i), event.getY(i), count);
                    }
                }
           return true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            drawThread = new DrawThread(getHolder());
            drawThread.setRunning(true);
            drawThread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            boolean retry = true;
            drawThread.setRunning(false);
            while (retry) {
                try {
                    drawThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveSettings() {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(APP_PREF_SOUND, soundOn);
        editor.putInt(APP_PREF_VOLUME, volumeIndex);
        editor.apply();
    }
}