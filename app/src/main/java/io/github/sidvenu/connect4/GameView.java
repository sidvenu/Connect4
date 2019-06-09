package io.github.sidvenu.connect4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class GameView extends View {


    private static final String LOGTAG = "TAG";
    boolean touchEnabled = true;
    Paint red = new Paint(), blue = new Paint();
    int rows = 6;
    int cols = 7;
    ArrayList<Integer> game = new ArrayList<>();
    Integer current = null;
    int y;
    String PROPERTY_Y = "prop_y";

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        red.setColor(getResources().getColor(android.R.color.holo_red_dark));
        blue.setColor(getResources().getColor(android.R.color.holo_blue_dark));
    }

    public void setGridDimensions(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        game.clear();
        current = null;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchEnabled) {
            float x = event.getX();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(x);
                performClick();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void handleTouch(float x) {
        current = (int) (7 * x / getWidth());

        int times = 0;
        for (Integer i : game) {
            if (i.equals(current))
                times++;
        }
        int maxY = getY(times);
        PropertyValuesHolder propertyY = PropertyValuesHolder.ofInt(PROPERTY_Y, 0, maxY);
        touchEnabled = false;
        ValueAnimator animator = new ValueAnimator();
        animator.setValues(propertyY);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                GameView.this.y = (int) animation.getAnimatedValue(PROPERTY_Y);
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                game.add(current);
                current = null;
                invalidate();
                touchEnabled = true;
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(LOGTAG, "" + getWidth());
        float diameter = getWidth() * 1.0f / cols;

        for (int i = 0; i < game.size(); i++) {
            Integer e = game.get(i);
            int times = 0;
            for (int j = 0; j < i; j++)
                if (e.equals(game.get(j)))
                    times++;
            Paint p = i % 2 == 0 ? red : blue;
            canvas.drawCircle(getX(e), getY(times), diameter / 2, p);
        }
        if (current != null)
            canvas.drawCircle(getX(current), y, diameter / 2, game.size() % 2 == 0 ? red : blue);
    }

    private int getX(int category) {
        return (int) ((category + 1 / 2.0f) * getWidth() / cols);
    }

    private int getY(int times) {
        return (int) (getHeight() * (1.0f - 1.0f / (rows * 2) - times * 1.0f / rows));
    }
}
