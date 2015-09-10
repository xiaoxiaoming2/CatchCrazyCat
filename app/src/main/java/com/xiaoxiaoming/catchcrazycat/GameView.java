package com.xiaoxiaoming.catchcrazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.Vector;

/**
 * Created by Administrator on 2015/9/8.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private static final int ROW = 9;
    private static final int COL = 9;
    private  int block ;
    private static int DOT_WIDTH = 40;
    private Dot matrix[][];
    private Dot cat;


    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        initGame();
    }

    private Dot getDot(int x, int y) {
        return matrix[y][x];
    }

    private void initGame() {

       block= (int) ((Math.random()*1000)%10+9);
        matrix = new Dot[ROW][COL];
        for (int i = 0; i < ROW; ++i)
            for (int j = 0; j < COL; ++j) {
                matrix[i][j] = new Dot(j, i, Dot.STATE_OFF);
            }
        cat = new Dot(4, 4, Dot.STATE_IN);
        getDot(4, 4).setState(Dot.STATE_IN);

        for (int i = 0; i < block; ) {
            int x = (int) ((Math.random() * 1000) % COL);
            int y = (int) ((Math.random() * 1000) % ROW);
            if (getDot(x, y).getState() == Dot.STATE_OFF) {
                getDot(x, y).setState(Dot.STATE_ON);
                ++i;
            }
        }


    }

    private void reDraw() {
        int offset = 0;
        Canvas c = getHolder().lockCanvas();
        c.drawColor(Color.LTGRAY);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < ROW; ++i) {
            offset = (i % 2 == 0 ? 0 : DOT_WIDTH >> 1);
            for (int j = 0; j < COL; ++j) {
                Dot one = matrix[i][j];
                switch (one.getState()) {
                    case Dot.STATE_OFF:
                        paint.setColor(0xffeeeeee);
                        break;
                    case Dot.STATE_IN:
                        paint.setColor(0xffff0000);
                        break;
                    case Dot.STATE_ON:
                        paint.setColor(0xffffaa00);
                        break;
                }
                c.drawOval(new RectF(j * DOT_WIDTH + offset, i * DOT_WIDTH, (j + 1) * DOT_WIDTH + offset, (i + 1) * DOT_WIDTH), paint);

            }
        }


        getHolder().unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        reDraw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        DOT_WIDTH = width / (COL + 1);
        reDraw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private int k = 1;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int y = (int) (event.getY() / DOT_WIDTH);
            int offset = (y % 2 == 1 ? DOT_WIDTH >> 1 : 0);
            int x = (int) ((event.getX() - offset) / DOT_WIDTH);
            if (x + 1 > COL || y + 1 > ROW) {
                initGame();
            } else {
                if (getDot(x, y).getState() == Dot.STATE_OFF) {
                    getDot(x, y).setState(Dot.STATE_ON);
                    move();
                }
            }
            reDraw();

        }

        return true;
    }

    private Boolean isAtEdge(Dot one) {
        if (one.getX() * one.getY() == 0 || one.getX() + 1 == COL || one.getY() + 1 == ROW) {
            return true;
        }
        return false;
    }

    private Dot getNrighbour(Dot one, int dir) {
        Dot dot = null;
        switch (dir) {
            case 1:
                dot = getDot(one.getX() - 1, one.getY());
                break;
            case 2:
                if (one.getY() % 2 == 0) {
                    dot = getDot(one.getX() - 1, one.getY() - 1);
                } else {
                    dot = getDot(one.getX(), one.getY() - 1);
                }
                break;
            case 3:
                if (one.getY() % 2 == 0) {
                    dot = getDot(one.getX(), one.getY() - 1);
                } else {
                    dot = getDot(one.getX() + 1, one.getY() - 1);
                }
                break;
            case 4:
                dot = getDot(one.getX() + 1, one.getY());
                break;
            case 5:
                if (one.getY() % 2 == 0) {
                    dot = getDot(one.getX(), one.getY() + 1);
                } else {
                    dot = getDot(one.getX() + 1, one.getY() + 1);
                }
                break;
            case 6:
                if (one.getY() % 2 == 0) {
                    dot = getDot(one.getX() - 1, one.getY() + 1);
                } else {
                    dot = getDot(one.getX(), one.getY() + 1);
                }
                break;

        }
        return dot;
    }

    private int getDistance(Dot one, int dir) {
        int distance = 0;
        Dot ori = getNrighbour(one, dir);
        while (true) {
            if (ori.getState() == Dot.STATE_ON)
                return -distance;
            if (isAtEdge(ori))
                return distance + 1;
            ++distance;
            ori = getNrighbour(ori, dir);
        }


    }

    private void moveTo(Dot one) {
        getDot(cat.getX(), cat.getY()).setState(Dot.STATE_OFF);
        one.setState(Dot.STATE_IN);
        cat.setX(one.getX());
        cat.setY(one.getY());
    }

    private void move() {
        if (isAtEdge(cat)) {
            lose();
            return;
        }
        int dots[] = new int[7];
        int bestFree = ROW + 1;
        int bestNoFree = 0;
        for (int dir = 1; dir <= 6; ++dir) {
            int tmp = getDistance(cat, dir);
            dots[dir] = tmp;
            if (tmp > 0) {
                bestFree = Math.min(tmp, bestFree);
            } else {
                bestNoFree = Math.min(tmp, bestNoFree);
            }
        }
        Vector<Dot> bestDots = new Vector<>();
        if (bestFree > ROW) {
            if (bestNoFree == 0) {
                win();
                return;
            } else {
                for (int i = 1; i <= 6; ++i) {
                    if (dots[i] == bestNoFree)
                        bestDots.add(getNrighbour(cat, i));
                }
            }
        } else {
            for (int i = 1; i <= 6; ++i) {
                if (dots[i] == bestFree) {
                    bestDots.add(getNrighbour(cat, i));
                }
            }
        }
        int s = (int) ((Math.random() * 1000) % bestDots.size());
        moveTo(bestDots.get(s));
    }

    private void win() {
        Toast.makeText(getContext(), "You Win!", Toast.LENGTH_SHORT).show();
    }

    private void lose() {
        Toast.makeText(getContext(), "You Lose!", Toast.LENGTH_SHORT).show();
    }
}
