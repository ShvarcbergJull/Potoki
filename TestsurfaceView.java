package com.example.pc.potoki;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.graphics.Paint;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class TestsurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder holder;
    MainActivity activity;
    int color = Color.RED;
    int color1 = Color.YELLOW;
    int[] colors = {Color.BLUE, Color.GRAY, Color.CYAN, Color.RED, Color.GREEN, Color.YELLOW, Color.MAGENTA};
    int countCircles = 2;
    boolean visit = false;
    DrawThread thread;
    ArrayList<Circle> circles = new ArrayList<>();
    Rect rect;
    float width;
    float height;
    float x, y, radius;
    int dx = 20, dy = 20;
    public void changeColor() {
        Random r = new Random();
        color = Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255));
        r = new Random();
        color1 = Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255));
    }

    class DrawThread extends Thread {
        boolean runFlag = true;
        public void disableFlag() {
            runFlag = false;
        }
        @Override
        public void run() {
            super.run();
            // задание: реализовать плавную смену цвета
            // палитру выбирайте сами
            while (runFlag) {
                try {
                    sleep(20);
                }
                catch (InterruptedException e) {}
                //changeColor();
                Canvas c = holder.lockCanvas();
                if (c != null) {
                    c.drawColor(Color.BLACK);
                    for (int i = 0;i < countCircles;i++)
                        circles.get(i).draw(c);
                    rect.draw(c);
                    holder.unlockCanvasAndPost(c);
                    if (circles.get(0).color == circles.get(1).color) {
                        thread.disableFlag();
                    }
                    for (int i = 0;i < circles.size();i++) {
                        circles.get(i).check(width, height);
                        for (int j = i;j < circles.size();j++) {
                            float d = (float)Math.sqrt((circles.get(i).x - circles.get(j).x)*(circles.get(i).x - circles.get(j).x) + (circles.get(i).y - circles.get(j).y)*(circles.get(i).y - circles.get(j).y));
                            if (d <= circles.get(i).radius + circles.get(j).radius) {
                                float tempdx = circles.get(i).dx;
                                float tempdy = circles.get(i).dy;
                                circles.get(i).dx = circles.get(j).dx;
                                circles.get(i).dy = circles.get(j).dy;
                                circles.get(j).dy = tempdy;
                                circles.get(j).dx = tempdx;
                            }
                        }
                        if (rect.inRect(circles.get(i).x, circles.get(i).y, circles.get(i).radius)) {
                            circles.get(i).dx *= -1;
                            circles.get(i).dy *= -1;
                            for (int j = 0;j < colors.length;j++) {
                                if (circles.get(i).color == colors[j]) {
                                    if (j == colors.length - 1) {
                                        circles.get(i).color = colors[0];
                                    }
                                    else {
                                        circles.get(i).color = colors[j + 1];
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (!runFlag) {
                Canvas c = holder.lockCanvas();
                Paint p = new Paint();
                p.setColor(Color.WHITE);
                p.setTextSize(96);
                try {
                    sleep(1000);
                }
                catch (InterruptedException e) {}
                c.drawColor(Color.BLACK);
                c.drawText("Game over", 300, height/2, p);
                holder.unlockCanvasAndPost(c);
            }
        }
    }


    public TestsurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // этот класс является обработчиком событий с поверхностью
        Log.d("coord", "his");
        getHolder().addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // запустить поток отрисовки
        width = getWidth();
        height = getHeight();
        x = (int) (Math.random() * width);
        y = (int) (Math.random() * height);
        for (int i = 0;i < countCircles;i++) {
            radius = 50 + (int) (Math.random() * 100);
            Circle circle = new Circle(radius + (int) (Math.random() * (width - (radius + 20))), radius + (int) (Math.random() * (height - (radius + 20))), radius, colors[(int)(Math.random() * 7)]);
            circles.add(circle);
        }
        rect = new Rect((int) width/2, (int) height/2, Color.YELLOW);
        holder = surfaceHolder;
        thread = new DrawThread();
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // перезапустить поток
        thread.disableFlag();
        thread.runFlag = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) { thread.disableFlag(); }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (rect.left <= event.getX() && rect.right >= event.getX() && rect.top <= event.getY() && rect.bottom >= event.getY()) {
                Log.d("coordin", "choose");
                visit = true;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (visit) {
                rect.coord((int) event.getX(), (int) event.getY());
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            visit = false;
        }

        return true;
    }
}
