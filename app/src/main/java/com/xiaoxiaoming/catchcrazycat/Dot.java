package com.xiaoxiaoming.catchcrazycat;

/**
 * Created by Administrator on 2015/9/8.
 */
public class Dot {
    private int x, y, state;
    public static final int STATE_ON = 1;
    public static final int STATE_OFF = 0;
    public static final int STATE_IN = -1;

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void Dot() {
        x = 0;
        y = 0;
        state = STATE_OFF;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }


    public  Dot(int x, int y) {
        this.x = x;
        this.y = y;
        state = STATE_OFF;
    }

    public  Dot(int x, int y, int state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }


}
