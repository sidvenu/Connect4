package io.github.sidvenu.connect4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class GameView extends View {


    private static final String LOGTAG = "TAG";
    Context context;
    boolean touchEnabled = true;
    Paint xPaint = new Paint(), oPaint = new Paint(), winStrokePaint = new Paint();
    int rows = 6;
    int cols = 7;
    int winStrokeWidth = 10;
    int y;
    int col;
    String PROPERTY_Y = "prop_y";

    boolean computerPlaying = true;
    MinMax computerPlayer;
    State theBoard;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        xPaint.setColor(getResources().getColor(android.R.color.holo_red_dark));
        oPaint.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        winStrokePaint.setColor(getResources().getColor(R.color.yellow));
        winStrokePaint.setStyle(Paint.Style.STROKE);
        winStrokePaint.setStrokeWidth(winStrokeWidth);
        computerPlayer = new MinMax(State.O);
        setGridDimensions(3, 7);
    }

    public void setGridDimensions(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        restartGame();
    }

    public void restartGame() {
        theBoard = new State(rows, cols);
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
        col = (int) (cols * x / getWidth());

        /*int times = 0;
        for (Integer i : game) {
            if (i.equals(current))
                times++;
        }*/
        if (!theBoard.checkFullColumn(col)) {
            int row = theBoard.getRowPosition(col);
            int maxY = getY(row);
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
                    //game.add(current);
                    theBoard.makeMove(col, theBoard.lastLetterPlayed == State.X ? State.O : State.X);
                    if (theBoard.checkGameOver()) {
                        Toast.makeText(context, "Game over", Toast.LENGTH_LONG).show();
                    } else {
                        touchEnabled = true;
                    }
                    //current = null;
                    invalidate();
                    if (computerPlaying && theBoard.lastLetterPlayed == State.X) {
                        float x = getX(computerPlayer.getNextMove(theBoard).col);
                        MotionEvent computerTouch = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.elapsedRealtime() + 100, MotionEvent.ACTION_DOWN, x, 0f, 0);
                        onTouchEvent(computerTouch);
                    }
                }
            });
            animator.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(LOGTAG, "" + getWidth());
        float diameter = getWidth() * 1.0f / cols;
        int[][] board = theBoard.gameBoard;
        for (int row = 0; row < theBoard.rows; row++) {
            for (int col = 0; col < theBoard.cols; col++) {
                int e = board[row][col];
                if (e == State.X || e == State.O) {
                    Paint p = xPaint;
                    if (e == State.O)
                        p = oPaint;
                    canvas.drawCircle(getX(col), getY(row), diameter / 2, p);
                    if (theBoard.checkGameOver()) {
                        for (int i = 0; i < 4; i++)
                            if (row == theBoard.winningPositions[i][0] && col == theBoard.winningPositions[i][1])
                                canvas.drawCircle(getX(col), getY(row), (diameter - winStrokeWidth) / 2, winStrokePaint);
                    }
                }

            }
            /*Integer e = game.get(i);
            int times = 0;
            for (int j = 0; j < i; j++)
                if (e.equals(game.get(j)))
                    times++;
            Paint p = i % 2 == 0 ? xPaint : oPaint;
            canvas.drawCircle(getX(e), getY(times), diameter / 2, p);*/
        }
        if (!touchEnabled && !theBoard.checkGameOver())
            canvas.drawCircle(getX(col), y, diameter / 2, theBoard.lastLetterPlayed == State.X ? oPaint : xPaint);
    }

    private int getX(int columnIndex) {
        return (int) (getWidth() / cols * (columnIndex + 1 / 2.0f));
    }

    private int getY(int rowIndex) {
        return (int) (getHeight() / rows * (rowIndex + 1 / 2.0f));
    }
}
