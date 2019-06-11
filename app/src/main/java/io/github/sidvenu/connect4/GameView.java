package io.github.sidvenu.connect4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class GameView extends View {

    Context context;
    boolean touchEnabled = true;
    Paint xPaint = new Paint(), oPaint = new Paint(), winStrokePaint = new Paint(), gridPaint = new Paint(), nonFilledPaint = new Paint();
    int rows = 6;
    int cols = 7;
    int winStrokeWidth = 10;
    int y;
    int col;
    float diameter;
    int padding = 10;
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
        nonFilledPaint.setColor(getResources().getColor(android.R.color.white));
        winStrokePaint.setColor(getResources().getColor(R.color.yellow));
        winStrokePaint.setStyle(Paint.Style.STROKE);
        winStrokePaint.setStrokeWidth(winStrokeWidth);
        computerPlayer = new MinMax(State.O);
        gridPaint.setColor(getResources().getColor(android.R.color.darker_gray));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(5);
        setGridDimensions(6, 7);
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
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handleTouch(x, y);
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

    private void handleTouch(float x, float y) {
        diameter = getWidth() * 1.0f / cols - padding;
        col = (int) (cols * x / getWidth());
        if(y<getYGrid(0)||y>getYGrid(rows))
            return;

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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (computerPlaying && theBoard.lastLetterPlayed == State.X) {
                                float x = getX(computerPlayer.getNextMove(theBoard).col);
                                MotionEvent computerTouch = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.elapsedRealtime() + 100, MotionEvent.ACTION_DOWN, x, getHeight()/2.0f, 0);
                                onTouchEvent(computerTouch);
                            }
                        }
                    }, 100);
                }
            });
            animator.start();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        diameter = getWidth() * 1.0f / cols - padding;
        gridPaint.setStyle(Paint.Style.FILL);
        //canvas.drawPaint(gridPaint);
        canvas.drawRect(getXGrid(0), getYGrid(0), getXGrid(cols), getYGrid(rows), gridPaint);


        int[][] board = theBoard.gameBoard;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int e = board[row][col];
                if (e == State.X || e == State.O) {
                    Paint p = xPaint;
                    if (e == State.O)
                        p = oPaint;
                    canvas.drawCircle(getX(col), getY(row), diameter / 2, p);
                    if (theBoard.checkGameOver() && theBoard.winner != State.EMPTY) {
                        for (int i = 0; i < 4; i++)
                            if (row == theBoard.winningPositions[i][0] && col == theBoard.winningPositions[i][1])
                                canvas.drawCircle(getX(col), getY(row), (diameter - winStrokeWidth) / 2, winStrokePaint);
                    }
                } else {
                    canvas.drawCircle(getX(col), getY(row), diameter / 2, nonFilledPaint);
                }
            }
        }
        /*//horizontal
        for(int row = 0; row<= rows; row++)
            canvas.drawLine(getXGrid(0), getYGrid(row), getXGrid(cols), getYGrid(row), gridPaint);
        //vertical
        for(int col = 0; col<= cols; col++)
            canvas.drawLine(getXGrid(col), getYGrid(0), getXGrid(col), getYGrid(rows), gridPaint);*/
        if (!touchEnabled && !theBoard.checkGameOver())
            canvas.drawCircle(getX(col), y, diameter / 2, theBoard.lastLetterPlayed == State.X ? oPaint : xPaint);
    }

    private int getX(int columnIndex) {
        return (int) (0.5f * (getWidth() - cols * (diameter+padding)) + 0.5f*(diameter+padding) + (diameter+padding) * columnIndex);
        //return (int) (0.5f * (getWidth() - cols * diameter + diameter) + (diameter+padding) * columnIndex - 2*padding);
        //return (int) (getWidth() / cols * (columnIndex + 1 / 2.0f));
    }

    private int getY(int rowIndex) {
        return (int) (0.5f * (getHeight() - rows * (diameter+padding)) + 0.5f*(diameter+padding) + (diameter+padding) * rowIndex);
        //return (int) (getHeight() / rows * (rowIndex + 1 / 2.0f));
    }
    //gets the grid BEFORE each circle
    private int getXGrid(int columnIndex) {
        return (int) (getX(columnIndex)-(diameter+padding)/2.0f);
    }
    //gets the grid BEFORE each circle
    private int getYGrid(int rowIndex) {
        return (int) (getY(rowIndex)-(diameter+padding)/2.0f);
    }
}
