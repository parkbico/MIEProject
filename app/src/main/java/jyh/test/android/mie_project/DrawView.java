package jyh.test.android.mie_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawView extends View implements View.OnTouchListener{

    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Paint> paints = new ArrayList<>();
    private ArrayList<Path> undonePaths = new ArrayList<>();

    private float mX, mY;

    private int currentColor = Color.BLUE;
    private int currentValue = 6;

    private boolean isPopupShown = false;

    public void setPopupShown(boolean popupShown) {
        isPopupShown = popupShown;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;

        mPaint.setColor(currentColor);
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;

        mPaint.setStrokeWidth(currentValue);
    }

    public DrawView(Context context){
        super(context);

        this.setOnTouchListener(this);

        mPaint = new Paint();
        setPaint(Color.BLUE, 6);

        mCanvas = new Canvas();
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(int i = 0; i < paths.size(); i++)
            canvas.drawPath(paths.get(i), paints.get(i));

        canvas.drawPath(mPath, mPaint);
    }

    private void touch_start(float x, float y){
        undonePaths.clear();

        mPath.reset();
        mPath.moveTo(x, y);
        mPath.lineTo(x, y);

        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y){
        mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);

        mX = x;
        mY = y;
    }

    private void touch_up(){
        mPath.lineTo(mX, mY);

        mCanvas.drawPath(mPath, mPaint);

        paths.add(mPath);
        paints.add(mPaint);

        mPath = new Path();
        mPaint = new Paint();
        setPaint(currentColor, currentValue);
    }

    public void onClickUndo () {
        if (paths.size() > 0){
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate();
        }
    }

    public void onClickRedo (){
        if (undonePaths.size() > 0){
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(!isPopupShown) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
        } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            isPopupShown = false;
        }

        return true;
    }

    private void setPaint(int color, int width){
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }
}
