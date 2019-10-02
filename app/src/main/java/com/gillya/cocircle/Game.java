package com.gillya.cocircle;

import android.graphics.*;
import java.util.*;

public class Game {
    private ArcDraw arcDraw;

    static final double FPS = 30;
    static final double SATURATE_SECONDS     = 4;       //Длительность проявления цветности, по умолчанию 4 сек
    private static final double WAIT_SECONDS = 1;       //Время показа проявившихся цветов, по умолчанию 1 сек
    private static final double DESATURATE_SECONDS = 1; //Время затухания цветности, по умолчанию 1 сек
    private static final int MAX_COLOR_LEVEL = 12;      //Максимальное количество вариантов цветов на одного игрока

    // Количество итерраций в одном цикле:
    static final int ROUND = (int) (FPS * (SATURATE_SECONDS + WAIT_SECONDS + DESATURATE_SECONDS));

    static int playersAmount;
    static List<Player> playersList;
    static boolean hasWinner = false;

    private static final int COLOR_GRADATION    = 4; //Количество цветовых градаций в каждом цвете RGB
    private static final double TIME_SATURATE   = Game.FPS * Game.SATURATE_SECONDS;
    private static final double TIME_WAIT       = Game.DESATURATE_SECONDS * Game.FPS;
    private static final double TIME_DESATURATE = Game.FPS * (Game.SATURATE_SECONDS + Game.WAIT_SECONDS);
    private static Random r = new Random();
    private static int mainColor;
    private static List<int[]> color = new ArrayList<>();
    private static List<int[]> colorTemp = new ArrayList<>();
    private static List<int[]> colorz = new ArrayList<>();
    private static double coef;
    private static final int grey = 128;
    private static int allChose = 0; // Количество игроков, совершивших выбор цвета

    public  Game(int width, int height) {
        initPlayers();
        arcDraw = new ArcDraw();
        arcDraw.init(width, height);
        hasWinner = false;
        allChose = 0;
    }

    private void initPlayers() {
        playersList = new ArrayList<>();
        for (int i = 0; i < playersAmount; i++) {
            playersList.add(new Player(this));
        }
        if (MainActivity.soundOn) {
            MainActivity.startSound.start(); // Гонг в начале игры
        }
    }

    void render(Canvas canvas, Paint p, int count) {
        if (hasWinner) {
            renderWinner(canvas, p);
            return;
        }
        if (count == 0) {
            for (int i = 0; i < playersAmount; i++) {
                playersList.get(i).setIsChosen(false);
                playersList.get(i).setIsFirst(true);
                playersList.get(i).setIsSecond(true);
                playersList.get(i).setIsThird(true);
                playersList.get(i).correctColorLevel();
                allChose = 0;
            }
            initColors();
        }
        if (allChose == playersAmount && count < TIME_SATURATE) MainActivity.count = (int) TIME_SATURATE;
        int colorIterator = 0;
        for (int i = 0; i < playersAmount; i++) {
            int currentColorLevel = playersList.get(i).getColorLevel();
            int arcPlayer = 360 / playersAmount / currentColorLevel;
            for (int j = 0; j < currentColorLevel; j++) {
                arcDraw.drawArcs(canvas, p, getColor(colorz.get(colorIterator++), count),
                        getColor(colorz.get(colorIterator++), count),
                        360 / playersAmount * i + arcPlayer * j, arcPlayer);
            }
        }
        for (int i = 0; i < playersAmount; i++) {
            playersList.get(i).modifyAlpha();
        }
        arcDraw.divideLines(canvas, p);
        arcDraw.drawCircleMain(canvas, p, mainColor);
        arcDraw.divideCircles(canvas, p);
        arcDraw.showScore(canvas, p);
    }

    private void renderWinner(Canvas canvas, Paint p) {
        int colorIterator = 0;
        for (int i = 0; i < playersAmount; i++) {
            int currentColorLevel = playersList.get(i).getColorLevel();
            int arcPlayer = 360 / playersAmount / currentColorLevel;
            for (int j = 0; j < currentColorLevel; j++) {
                int count = playersList.get(i).isWinner() ? (int) (SATURATE_SECONDS * FPS) : (int) FPS / 3;
                arcDraw.drawArcs(canvas, p, getColor(colorz.get(colorIterator++), count),
                        getColor(colorz.get(colorIterator++), count),
                        360 / playersAmount * i + arcPlayer * j, arcPlayer);
            }
        }
        for (int i = 0; i < playersAmount; i++) {
            playersList.get(i).modifyAlpha();
        }
        arcDraw.divideLines(canvas, p);
        arcDraw.drawCircleMain(canvas, p, mainColor);
        arcDraw.divideCircles(canvas, p);
        arcDraw.showScore(canvas, p);
    }

    private static void initColors() {
        color.clear();
        colorz.clear();
        int[] mainColorInt = new int[]{getRandomInt(), getRandomInt(), getRandomInt()};
        mainColor = Color.rgb(mainColorInt[0], mainColorInt[1], mainColorInt[2]);
        color.add(mainColorInt);
        boolean isNew;
        for (int i = 1; i < Game.MAX_COLOR_LEVEL; i++) {
            isNew = true;
            int[] tempColor = new int[]{getRandomInt(), getRandomInt(), getRandomInt()};
            for (int[] iter : color) {
                if (Arrays.equals(iter, tempColor)) {
                    i--;
                    isNew = false;
                }
            }
            if (isNew) color.add(tempColor);
        }
        for (int i = 0; i < playersAmount; i++) {
            colorTemp.clear();
            int tmp = playersList.get(i).getColorLevel() * 2;
            for (int j = 0; j < tmp; j++) {
                colorTemp.add(color.get(j));
            }
            Collections.shuffle(colorTemp);
            colorz.addAll(colorTemp);
        }
    }

    private static int getColor(int[] currentColor, int count) {
        if (count <= TIME_SATURATE) {
            coef = count / TIME_SATURATE;
        } else if (count <= TIME_DESATURATE) {
            coef = 1;
        } else {
            coef = (TIME_DESATURATE + TIME_WAIT - count) / Game.FPS;
        }
        return Color.rgb(
                grey + (int) ((currentColor[0] - grey) * coef),
                grey + (int) ((currentColor[1] - grey) * coef),
                grey + (int) ((currentColor[2] - grey) * coef)
        );
    }

    private static int getRandomInt() {
        return 255 / COLOR_GRADATION * r.nextInt(COLOR_GRADATION + 1);
    }

    public void onTouch(float dx, float dy, int count) {
        if (hasWinner) return;
        int x = arcDraw.getX(dx);
        int y = arcDraw.getY(dy);
        if (arcDraw.outTouch(x, y)) return;
        int playerID = 0;
        int arca = 360 / playersAmount;
        int alfa = 0;
        if (x <= 0) alfa = (int) ((Math.PI + Math.atan((double) y / x)) / Math.PI * 180);
        else if (y >= 0) alfa = (int) (Math.atan((double) y / x) / Math.PI * 180);
        else alfa = (int) ((2 * Math.PI + Math.atan((double) y / x)) / Math.PI * 180);
        for (int i = 0; i < playersAmount; i++) {
            if (alfa >= i * arca && alfa < (i + 1) * arca) {
                playerID = i;
                break;
            }
        }
        Player player = playersList.get(playerID);
        if (player.isChosen()) return;
        player.setIsChosen(true);
        allChose++;
        alfa %= arca;
        int colorLevel = player.getColorLevel();
        int colorIndex = 0;
        for (int i = 0; i < colorLevel; i++) {
            if (alfa >= i * (arca / colorLevel) && alfa < (i + 1) * (arca / colorLevel)) {
                for (int j = 0; j < playerID; j++) {
                    colorIndex += playersList.get(j).getColorLevel() * 2;
                }
                colorIndex += (i + 1) * 2 - 1;
                if (arcDraw.farTouch(x, y)) colorIndex--;
            }
        }
        int[] testColorMas = colorz.get(colorIndex);
        int testColor = Color.rgb(testColorMas[0], testColorMas[1], testColorMas[2]);
        if (testColor == mainColor) {
            player.scoreUp(true, count);
            player.setAlpha(255);
//            MainActivity.yepSound.start();
            if (MainActivity.soundOn) {
                MainActivity.soundPool.play(MainActivity.yepSound,MainActivity.soundVolume,
                        MainActivity.soundVolume, 1, 0, 1);
            }
            if (player.isFirst()) {
                for (int i = 0; i < playersAmount; i++) {
                    if (playerID == i) continue;
                    playersList.get(i).setIsFirst(false);
                }
            } else if (player.isSecond()) {
                for (int i = 0; i < playersAmount; i++) {
                    if (playerID == i) continue;
                    playersList.get(i).setIsSecond(false);
                }
            } else if (player.isThird()) {
                for (int i = 0; i < playersAmount; i++) {
                    if (playerID == i) continue;
                    playersList.get(i).setIsThird(false);
                }
            }
        } else {
            player.scoreUp(false, count);
            player.setAlpha(255);
//            MainActivity.nopSound.start();
            if (MainActivity.soundOn) {
                MainActivity.soundPool.play(MainActivity.nopSound, MainActivity.soundVolume,
                        MainActivity.soundVolume, 1, 0, 1);
            }
        }
        if (hasWinner) {
//            MainActivity.winSound.start();
            if (MainActivity.soundOn) {
                MainActivity.soundPool.play(MainActivity.winSound, MainActivity.soundVolume,
                        MainActivity.soundVolume, 1, 0, 1);
            }
        }
    }
}