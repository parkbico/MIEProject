package jyh.test.android.mie_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class DrawView extends View implements View.OnTouchListener {
    /// Constants
    public static final int SHAPE_PEN = 1;
    public static final int SHAPE_CIRCLE = 2;
    public static final int SHAPE_RECTANGLE = 3;

    /// Fields
    private Path mPath;
    private Paint mPaint;
    private int currentCount = 0;
    private ArrayList<Integer> shapeHistory = new ArrayList<>();
    private ArrayList<Integer> undoneShapeHistory = new ArrayList<>();
    private ArrayList<Object> objectHistory = new ArrayList<>();
    private ArrayList<Object> undoneObjectHistory = new ArrayList<>();
    private ArrayList<Paint> paints = new ArrayList<>();
    private ArrayList<Paint> undonePaints = new ArrayList<>();

    private Point posStart = new Point();
    private RectF rect = new RectF();

    private float mX, mY;

    private int currentShape = SHAPE_PEN;
    private int currentColor = Color.BLUE;
    private int currentWidth = 6;

    private Bitmap cameraPicture;

    /// Flags
    /**
     * {@code true} ColorSelector 활성화 상태 (btnColorSelectorD 버튼 클릭)
     * {@code false} ColorSelector 비활성화 상태
     */
    private boolean isColorSelecting = false;

    /**
     * {@code true} 색상 선택 팝업 활성화 상태 (btnPencilD 버튼 클릭)
     * {@code false} 색상 선택 팝업 비활성화 상태
     */
    private boolean isPopupShown = false;

    /// Properties
    public void setSelecting(boolean selecting) {
        isColorSelecting = selecting;
    }
    public void setPopupShown(boolean popupShown) {
        isPopupShown = popupShown;
    }
    public void setCameraPicture(Bitmap cameraPicture) {
        erase();

        this.cameraPicture = cameraPicture;
    }
    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;

        mPaint.setColor(currentColor);
    }
    public void setCurrentWidth(int currentWidth) {
        this.currentWidth = currentWidth;

        mPaint.setStrokeWidth(currentWidth);
    }
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Constructor
     */
    public DrawView(Context context){
        super(context);

        this.setOnTouchListener(this);

        mPaint = new Paint();
        setPaint(Color.BLUE, 6);

        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (cameraPicture != null)
            canvas.drawBitmap(cameraPicture, 0, 0, null);

        for(int i = 0; i < shapeHistory.size(); i++) {
            switch (shapeHistory.get(i)) {
                case SHAPE_PEN:
                    canvas.drawPath((Path)objectHistory.get(i), paints.get(i));
                    break;

                case SHAPE_CIRCLE:
                    canvas.drawOval((RectF)objectHistory.get(i), paints.get(i));
                    break;

                case SHAPE_RECTANGLE:
                    canvas.drawRect((RectF)objectHistory.get(i), paints.get(i));
                    break;
            }
        }
    }

    private void touch_start(float x, float y){
        shapeHistory.add(currentShape);
        paints.add(mPaint);

        undoneShapeHistory.clear();
        undoneObjectHistory.clear();
        undonePaints.clear();

        switch(currentShape) {
            case SHAPE_PEN:
                mPath.reset();
                mPath.moveTo(x, y);
                mPath.lineTo(x, y);

                objectHistory.add(mPath);

                mX = x;
                mY = y;

                break;

            case SHAPE_CIRCLE:
            case SHAPE_RECTANGLE:
                posStart.set((int) x, (int) y);
                rect = new RectF(posStart.x, posStart.y, x, y);

                objectHistory.add(rect);

                break;
        }
    }

    private void touch_move(float x, float y){
        switch(currentShape) {
            case SHAPE_PEN:
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);

                objectHistory.set(currentCount, mPath);

                mX = x;
                mY = y;

                break;

            case SHAPE_CIRCLE:
            case SHAPE_RECTANGLE:
                rect.set(posStart.x, posStart.y, x, y);
                objectHistory.set(currentCount, rect);

                break;
        }
    }

    private void touch_up(){
        switch(currentShape){
            case SHAPE_PEN:
                mPath.lineTo(mX, mY);

                objectHistory.set(currentCount, mPath);

                mPath = new Path();
                mPaint = new Paint();
                setPaint(currentColor, currentWidth);

                break;

            case SHAPE_CIRCLE:
            case SHAPE_RECTANGLE:
                objectHistory.set(currentCount, rect);

                mPaint = new Paint();
                setPaint(currentColor, currentWidth);

                break;
        }

        currentCount++;
    }

    public void onClickUndo () {
        if (shapeHistory.size() > 0){
            undoneShapeHistory.add(shapeHistory.remove(shapeHistory.size() - 1));
            undoneObjectHistory.add(objectHistory.remove(objectHistory.size() - 1));
            undonePaints.add(paints.remove(paints.size() - 1));

            currentCount--;

            invalidate();
        }
    }

    public void onClickRedo (){
        if (undoneShapeHistory.size() > 0){
            shapeHistory.add(undoneShapeHistory.remove(undoneShapeHistory.size() - 1));
            objectHistory.add((undoneObjectHistory.remove(undoneObjectHistory.size() - 1)));
            paints.add(undonePaints.remove(undonePaints.size() - 1));

            currentCount++;

            invalidate();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(!isPopupShown) {
            if(isColorSelecting) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPath.reset();

                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();

                        Bitmap bitmap = getBitmapFromView(view);

                        currentColor = bitmap.getPixel(x, y);

                        mPaint.setColor(Color.rgb(
                                Color.red(currentColor),
                                Color.green(currentColor),
                                Color.blue(currentColor)));

                        Toast.makeText(getContext(), "색상이 설정되었습니다.", Toast.LENGTH_SHORT).show();

                        mPaint.setColor(currentColor);

                        break;

                    case MotionEvent.ACTION_UP:
                        isColorSelecting = false;
                        break;
                }
            } else {
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
            }
        } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
            isPopupShown = false;
        }

        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void setPaint(int color, int width){
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void erase(){
        mPath.reset();
        shapeHistory.clear();
        objectHistory.clear();
        paints.clear();

        currentCount = 0;

        invalidate();
    }

    public void eraseAll(){
        erase();
        cameraPicture = null;
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();

        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);

        view.draw(canvas);

        return bitmap;
    }
}
