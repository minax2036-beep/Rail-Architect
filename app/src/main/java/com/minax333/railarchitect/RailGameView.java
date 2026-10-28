package com.minax333.railarchitect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import android.os.Looper;

public class RailGameView extends View {
    private static final int COLS = 14, ROWS = 8;
    private static final int ERASE = 0, STRAIGHT = 1, VERTICAL = 2,
            CURVE = 3, SWITCH = 4, STATION = 5, SIGNAL = 6;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int[][] tiles = new int[ROWS][COLS];
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (running && !paused) {
                moveTrain();
                invalidate();
                handler.postDelayed(this, 450);
            }
        }
    };
    private int selected = STRAIGHT;
    private int trainX = 2, trainY = ROWS / 2, delivered = 0;
    private boolean running = false, paused = false;
    private float boardLeft, boardTop, cell, panelTop, panelHeight;
    private int screenW, screenH;
    private String message = "Tap RUN, then build your railway.";

    public RailGameView(Context context) {
        super(context);
        setFocusable(true);
        createStarterLine();
    }

    private void createStarterLine() {
        for (int y = 0; y < ROWS; y++)
            for (int x = 0; x < COLS; x++) tiles[y][x] = ERASE;
        int row = ROWS / 2;
        for (int x = 1; x <= 5; x++) tiles[row][x] = STRAIGHT;
        tiles[row][1] = STATION;
        tiles[row][5] = STATION;
        trainX = 2;
        trainY = row;
        delivered = 0;
        running = false;
        paused = false;
        message = "Starter line ready. Tap RUN to test it.";
        handler.removeCallbacks(tick);
        invalidate();
    }

    private void moveTrain() {
        int nx = trainX + 1;
        if (nx >= COLS || tiles[trainY][nx] == ERASE) {
            running = false;
            message = "Train stopped: no track ahead.";
            handler.removeCallbacks(tick);
            return;
        }
        if (tiles[trainY][nx] == SIGNAL) {
            running = false;
            message = "Red signal: train waiting.";
            handler.removeCallbacks(tick);
            return;
        }
        trainX = nx;
        if (tiles[trainY][trainX] == STATION) {
            delivered++;
            message = "Passenger delivery completed!";
            trainX = 2;
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        screenW = getWidth();
        screenH = getHeight();
        if (screenW <= 0 || screenH <= 0) return;
        canvas.drawColor(Color.rgb(18, 30, 38));
        float headerH = screenH * 0.13f;
        panelTop = screenH * 0.84f;
        panelHeight = screenH - panelTop;
        boardTop = headerH + 8;
        boardLeft = 14;
        cell = Math.min((screenW - 28f) / COLS,
                (panelTop - boardTop - 8f) / ROWS);

        paint.setColor(Color.rgb(26, 43, 53));
        canvas.drawRect(0, 0, screenW, headerH, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(Math.max(18, headerH * 0.31f));
        paint.setFakeBoldText(true);
        canvas.drawText("RAIL ARCHITECT", 16, headerH * 0.43f, paint);
        paint.setFakeBoldText(false);
        paint.setTextSize(Math.max(12, headerH * 0.18f));
        canvas.drawText("DELIVERED " + delivered, 18, headerH * 0.77f, paint);

        float bw = Math.max(64, screenW * 0.09f);
        float bh = headerH * 0.58f;
        float gap = 7, bx = screenW - (bw * 3 + gap * 4), by = headerH * 0.20f;
        drawButton(canvas, bx, by, bw, bh, running ? "PAUSE" : "RUN", Color.rgb(52, 145, 105));
        drawButton(canvas, bx + bw + gap, by, bw, bh, "RESET", Color.rgb(153, 81, 66));
        drawButton(canvas, bx + (bw + gap) * 2, by, bw, bh, "CLEAR", Color.rgb(63, 101, 126));

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                float l = boardLeft + x * cell, t = boardTop + y * cell;
                paint.setColor((x + y) % 2 == 0 ? Color.rgb(54, 91, 68) : Color.rgb(49, 83, 63));
                canvas.drawRect(l, t, l + cell, t + cell, paint);
                paint.setColor(Color.argb(50, 10, 25, 18));
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(l, t, l + cell, t + cell, paint);
                paint.setStyle(Paint.Style.FILL);
                drawTile(canvas, x, y, l, t);
            }
        }

        float tx = boardLeft + (trainX + 0.5f) * cell;
        float ty = boardTop + (trainY + 0.5f) * cell;
        paint.setColor(Color.rgb(245, 189, 72));
        canvas.drawRoundRect(new RectF(tx - cell * .34f, ty - cell * .23f,
                tx + cell * .34f, ty + cell * .23f), 5, 5, paint);
        paint.setColor(Color.rgb(39, 48, 54));
        canvas.drawRect(tx - cell * .14f, ty - cell * .18f,
                tx + cell * .14f, ty + cell * .18f, paint);

        paint.setColor(Color.rgb(24, 39, 48));
        canvas.drawRect(0, panelTop, screenW, screenH, paint);
        String[] names = {"ERASE", "STRAIGHT", "VERTICAL", "CURVE", "SWITCH", "STATION", "SIGNAL"};
        int[] types = {ERASE, STRAIGHT, VERTICAL, CURVE, SWITCH, STATION, SIGNAL};
        float margin = 8, pgap = 5;
        float pw = (screenW - margin * 2 - pgap * 6) / 7f;
        float ph = panelHeight * .55f;
        for (int i = 0; i < names.length; i++) {
            float x = margin + i * (pw + pgap);
            drawButton(canvas, x, panelTop + 7, pw, ph, names[i],
                    selected == types[i] ? Color.rgb(221, 157, 47) : Color.rgb(48, 70, 81));
        }
        paint.setColor(Color.rgb(220, 230, 232));
        paint.setTextSize(Math.max(11, panelHeight * .19f));
        canvas.drawText(message, 12, panelTop + panelHeight * .90f, paint);
    }

    private void drawTile(Canvas canvas, int x, int y, float l, float t) {
        int type = tiles[y][x];
        if (type == ERASE) return;
        float cx = l + cell / 2f, cy = t + cell / 2f;
        paint.setColor(Color.rgb(175, 183, 185));
        paint.setStrokeWidth(cell * .22f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        if (type == STRAIGHT || type == STATION || type == SWITCH) {
            canvas.drawLine(l + cell * .05f, cy, l + cell * .95f, cy, paint);
        } else if (type == VERTICAL) {
            canvas.drawLine(cx, t + cell * .05f, cx, t + cell * .95f, paint);
        } else if (type == CURVE) {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(new RectF(l + cell * .12f, t + cell * .12f,
                    l + cell * .88f, t + cell * .88f), 180, 90, false, paint);
            paint.setStyle(Paint.Style.FILL);
        } else if (type == SIGNAL) {
            canvas.drawLine(cx, cy, cx, t + cell * .18f, paint);
            paint.setColor(Color.rgb(219, 69, 62));
            canvas.drawCircle(cx, t + cell * .20f, cell * .13f, paint);
        }
        if (type == STATION) {
            paint.setColor(Color.rgb(230, 236, 222));
            canvas.drawRoundRect(new RectF(l + cell * .18f, t + cell * .12f,
                    l + cell * .82f, t + cell * .40f), 3, 3, paint);
            paint.setColor(Color.rgb(46, 61, 69));
            paint.setTextSize(cell * .19f);
            canvas.drawText("ST", l + cell * .30f, t + cell * .32f, paint);
        }
        if (type == SWITCH) {
            paint.setColor(Color.rgb(239, 183, 69));
            paint.setStrokeWidth(cell * .10f);
            canvas.drawLine(cx, cy, cx + cell * .32f, t + cell * .20f, paint);
        }
    }

    private void drawButton(Canvas c, float x, float y, float w, float h, String text, int color) {
        paint.setColor(color);
        c.drawRoundRect(new RectF(x, y, x + w, y + h), 8, 8, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(Math.min(h * .34f, 18));
        paint.setFakeBoldText(true);
        float tw = paint.measureText(text);
        c.drawText(text, x + (w - tw) / 2, y + h * .62f, paint);
        paint.setFakeBoldText(false);
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) return true;
        float x = event.getX(), y = event.getY();
        float headerH = screenH * .13f;
        float bw = Math.max(64, screenW * .09f), bh = headerH * .58f;
        float gap = 7, bx = screenW - (bw * 3 + gap * 4), by = headerH * .20f;
        if (y >= by && y <= by + bh) {
            if (x >= bx && x < bx + bw) {
                if (running) {
                    running = false;
                    paused = true;
                    handler.removeCallbacks(tick);
                    message = "Simulation paused.";
                } else {
                    running = true;
                    paused = false;
                    message = "Simulation running.";
                    handler.removeCallbacks(tick);
                    handler.postDelayed(tick, 450);
                }
            } else if (x >= bx + bw + gap && x < bx + 2 * bw + gap) {
                createStarterLine();
            } else if (x >= bx + 2 * (bw + gap) && x < bx + 3 * bw + 2 * gap) {
                for (int row = 0; row < ROWS; row++)
                    for (int col = 0; col < COLS; col++) tiles[row][col] = ERASE;
                running = false;
                handler.removeCallbacks(tick);
                message = "Board cleared.";
            }
            invalidate();
            return true;
        }
        if (y >= panelTop) {
            int[] types = {ERASE, STRAIGHT, VERTICAL, CURVE, SWITCH, STATION, SIGNAL};
            float margin = 8, pgap = 5;
            float pw = (screenW - margin * 2 - pgap * 6) / 7f;
            int index = (int) ((x - margin) / (pw + pgap));
            if (index >= 0 && index < types.length) selected = types[index];
            invalidate();
            return true;
        }
        int gx = (int) ((x - boardLeft) / cell), gy = (int) ((y - boardTop) / cell);
        if (gx >= 0 && gx < COLS && gy >= 0 && gy < ROWS) {
            tiles[gy][gx] = selected;
            message = selected == ERASE ? "Tile erased." : "Track tile placed.";
            invalidate();
        }
        return true;
    }

    @Override protected void onDetachedFromWindow() {
        handler.removeCallbacks(tick);
        super.onDetachedFromWindow();
    }
}
