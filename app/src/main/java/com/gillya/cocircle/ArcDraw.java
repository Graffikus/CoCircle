package com.gillya.cocircle;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ArcDraw {
    private final int FULL_ANGLE = 360;
    private int circleColor = Color.rgb(255, 255, 255);
    private int linePlayerColor = Color.rgb(200, 200, 200);
    private int lineColor = Color.rgb(255, 255, 255);
    private int textScoreSize;
    private int textIncrementScoreSize;

    private int width, height;
    private int widthHalf, heightHalf;
    private int xL, yL, wL, hL;
    private int xS, yS, wS, hS;
    private int wM, hM, xM, yM;
    private int rM;
    private int x1, y1, line;
    private int[] textX, textY, textAngle;

    public ArcDraw() {
    }

    void drawArcs(Canvas canvas, Paint p, int color1, int color2, int startAngle, int angle) {
        p.setColor(color1);
        canvas.drawArc(xL, yL, wL, hL, startAngle, angle, true, p);
        p.setColor(color2);
        canvas.drawArc(xS, yS, wS, hS, startAngle, angle, true, p);
    }

    void drawCircleMain(Canvas canvas, Paint p, int color) {
        p.setColor(color);
        canvas.drawOval(xM, yM, wM, hM, p);
    }

    void divideLines(Canvas canvas, Paint p) {
        for (int i = 0; i < Game.playersAmount; i++) {
            int currentColorLevel = Game.playersList.get(i).getColorLevel();
            int arc = 360 / Game.playersAmount / currentColorLevel;
            p.setColor(lineColor);
            p.setStrokeWidth(15);
            double degree1 = Math.toRadians(360.0 / Game.playersAmount * i);
            int alfa = (int) ((line + 30) * Math.cos(degree1));
            int beta = (int) ((line + 30) * Math.sin(degree1));
            canvas.drawLine(x1, y1, x1 + alfa, y1 - beta, p);
            p.setColor(Color.BLACK);
            p.setStrokeWidth(5);
            canvas.drawLine(x1, y1, x1 + alfa, y1 - beta, p);
            for (int j = 1; j < currentColorLevel; j++) {
                p.setColor(linePlayerColor);
                p.setStrokeWidth(5);
                double degree2 = Math.toRadians(360.0 / Game.playersAmount * i + arc * j);
                canvas.drawLine(x1, y1,
                        x1 + (int) (line * Math.cos(degree2)),
                        y1 + (int) (line * Math.sin(degree2)), p);
            }
        }
    }

    void divideCircles(Canvas canvas, Paint p) {
        p.setColor(circleColor);
        p.setStrokeWidth(5);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawOval(xS, yS, wS, hS, p);
        canvas.drawOval(xM, yM, wM, hM, p);
        canvas.drawOval(xL, yL, wL, hL, p);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    void showScore(Canvas canvas, Paint p) {
        for (int i = 0; i < Game.playersAmount; i++) {
            canvas.save();
            canvas.rotate(textAngle[i], textX[i], textY[i]);
            p.setColor(Game.playersList.get(i).isRight() ? Color.CYAN : Color.LTGRAY);
            if (Game.playersList.get(i).isWinner()) p.setColor(Color.GREEN);
            p.setTextSize(textScoreSize);
            canvas.drawText("" + Game.playersList.get(i).getScore(), textX[i], textY[i], p);
            int alpha = Game.playersList.get(i).getAlpha();
            p.setColor(Game.playersList.get(i).isRight() ? Color.argb(alpha, 0, 255, 0) :
                    Color.argb(alpha, 255, 0, 128));
            p.setTextSize(textIncrementScoreSize);
            canvas.drawText((Game.playersList.get(i).isRight() ? "+" : "") +
                    Game.playersList.get(i).getInc(), textX[i] + (255 - alpha) / 18f,
                    textY[i] - textScoreSize - (255 - alpha) / 12f, p);
            canvas.restore();
        }
    }

    int getX(float dx) {
        return (int) dx - widthHalf;
    }

    int getY(float dy) {
        return (int) dy - heightHalf;
    }

    boolean farTouch(int x, int y) {
        return Math.sqrt(x * x + y * y) >= width / 3;
    }

    boolean outTouch(int x, int y) {
        return Math.sqrt(x * x + y * y) > widthHalf || Math.sqrt(x * x + y * y) < rM;
    }

    void init(int width, int height) {
        this.width = width;
        this.height = height;

        textScoreSize = width / 11;
        textIncrementScoreSize = width / 16;

        widthHalf = width / 2;
        heightHalf = height / 2;

        // определение координат для большого круга
        xL = 0;
        yL = height / 2 - width / 2;
        wL = width;
        hL = height / 2 + width / 2;

        // определение координат для малого круга
        xS = width / 2 - wL / 6 * 2;
        yS = height / 2 - wL / 6 * 2;
        wS = width / 2 + wL / 6 * 2;
        hS = height / 2 + wL / 6 * 2;

        // определение координат для главного (центрального) круга
        xM = width / 2 - wL / 20 * 3;
        yM = height / 2 - wL / 20 * 3;
        wM = width / 2 + wL / 20 * 3;
        hM = height / 2 + wL / 20 * 3;

        rM = wL / 20 * 3;

        x1 = width / 2;
        y1 = height / 2;
        line = width / 2 - 1;

        textX =     new int[Game.playersAmount];
        textY =     new int[Game.playersAmount];
        textAngle = new int[Game.playersAmount];

        // определение углов и координат счетчика очков для каждого игрока
        if (Game.playersAmount == 2) {
            textX[0] = width / 2 - width / 3;
            textY[0] = height - height / 10;
            textAngle[0] = 0;
            textX[1] = width / 2 + width / 3;
            textY[1] = height / 10;
            textAngle[1] = FULL_ANGLE / 2;     // 180 градусов
        } else if (Game.playersAmount == 3) {
            textX[0] = width - width / 10;
            textY[0] = height - height / 5;
            textAngle[0] = FULL_ANGLE * 7 / 8; // 315 градусов
            textX[1] = width / 10;
            textY[1] = height / 5;
            textAngle[1] = FULL_ANGLE * 3 / 8; // 135 градусов
            textX[2] = width - width / 10;
            textY[2] = height / 5;
            textAngle[2] = FULL_ANGLE * 5 / 8; // 225 градусов
        } else {
            textX[0] = width - width / 10;
            textY[0] = height - height / 5;
            textAngle[0] = FULL_ANGLE * 7 / 8; // 315 градусов
            textX[1] = width / 10;
            textY[1] = height - height / 5;
            textAngle[1] = FULL_ANGLE / 8;     //  45 градусов
            textX[2] = width / 10;
            textY[2] = height / 5;
            textAngle[2] = FULL_ANGLE * 3 / 8; // 135 градусов
            textX[3] = width - width / 10;
            textY[3] = height / 5;
            textAngle[3] = FULL_ANGLE * 5 / 8; // 225 градусов
        }
    }
}