package com.example.pc.potoki;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Circle {
    float x, y, dx = 20, dy = 20, radius;
    int n, color;

    public Circle(float x, float y, float radius, int color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    public void check(float w, float h) {
        if ((x + dx + radius) >= w|| (x + dx - radius) <= 0) {
            dx = dx * -1;
        }
        if ((y + dy + radius) >= h|| (y + dy - radius) <= 0) {
            dy = dy * -1;
        }
        y += dy;
        x += dx;
    }

    public  void draw(Canvas canvas)
    {
        Paint p = new Paint();
        p.setColor(color);
        canvas.drawCircle(x, y, radius, p);
        p.setColor(Color.BLACK);
        canvas.drawText(Integer.toString(n), x, y, p);
    }
}
