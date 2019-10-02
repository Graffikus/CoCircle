package com.gillya.cocircle;

public class Player {
    private int score;                      // счет игрока
    private int colorLevel;                 // текущая сложность количества цветов
    private int increment;                  // прибавка к счету
    private int alpha;                      // прозрачность прибавки к счету
    private int incrementFadeOut;           // время затухания прибавки к счету
    private boolean isRight = true;         // выбор цвета правильный?
    private boolean isColorChosen = false;  // в текущем ходе выбор уже сделан?
    private boolean isFirst = true;         // первым выбрал цвет?
    private boolean isSecond = true;        // вторым выбрал цвет?
    private boolean isThird = true;         // третьим выбрал цвет?
    private boolean isWinner = false;       // появился победитель?
    private Game game;

    public Player(Game game) {
        this.game = game;
        score = 0;
        colorLevel = 1;
        increment = 0;
        incrementFadeOut = (int) (255 / Game.FPS / 2); //затухание две секунды
    }

    public int getColorLevel() {
        return colorLevel;
    }

    public void correctColorLevel() {
        if (score < 15) colorLevel = 1;
        else if (score < 30) colorLevel = 2;
        else if (score < 45) colorLevel = 3;
        else if (score < 60 && Game.playersAmount < 4) colorLevel = 4;
        else if (score < 75 && Game.playersAmount < 3) colorLevel = 5;
        else if (score < 90 && Game.playersAmount < 3) colorLevel = 6;
    }

    public int getScore() {
        return score;
    }

    public void scoreUp(boolean up, int count) {
        if (Game.hasWinner) return;
        if (up) {
            if (count <= Game.SATURATE_SECONDS * Game.FPS) {
                if (Game.playersAmount == 2) increment = isFirst ? 5 : 3;
                else increment = isFirst ? 5 : isSecond ? 4 : isThird ? 3 : 2;
            } else increment = 1;
            score += increment;
            isRight = true;
            if (score >= 100) {
                isWinner = true;
                Game.hasWinner = true;
            }
        } else {
            increment = -3;
            score += increment;
            if (score < 0) score = 0;
            isRight = false;
        }
    }

    public boolean isChosen() {
        return isColorChosen;
    }

    public void setIsChosen(boolean choose) {
        isColorChosen = choose;
    }

    public void setIsFirst(boolean first) {
        isFirst = first;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setIsSecond(boolean second) {
        isSecond = second;
    }

    public boolean isSecond() {
        return isSecond;
    }

    public void setIsThird(boolean third) {
        isThird = third;
    }

    public boolean isThird() {
        return isThird;
    }

    public void setIsWinner(boolean winner) {
        isWinner = winner;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public boolean isRight() {
        return isRight;
    }

    public int getInc() {
        return increment;
    }

    public void setAlpha(int val) {
        alpha = val;
    }

    public int getAlpha() {
        return alpha;
    }

    public void modifyAlpha() {
        if (alpha <= 0) return;
        alpha -= incrementFadeOut;
        if (alpha < 0) alpha = 0;
    }
}