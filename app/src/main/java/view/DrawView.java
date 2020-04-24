package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class DrawView extends View {
    public static final float TOUCH_TOLERANCE = 10;

    private Bitmap bitmap;
    private Canvas bitmaoCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    private HashMap <Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;
    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();


    }

    void init(){
        paintScreen = new Paint();

        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.RED);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(30);
        paintLine.setStrokeCap(Paint.Cap.ROUND);

        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmaoCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap,0,0,paintScreen);

        for (Integer key: pathMap.keySet())
        {
            canvas.drawPath(pathMap.get(key), paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();// Evemt type being revieved
        int actionIndex= event.getActionIndex(); //pointer (finger, mouse...)

        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_UP)
        {
            touchStarted(event.getX(actionIndex),
                    event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        }
        else if(action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP){

            touchEnded(event.getPointerId(actionIndex));
        }
        else
            touchMoved(event);

        invalidate();//redraws the screen
        return true;
    }

    private void touchMoved(MotionEvent event) {

        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);

            if (pathMap.containsKey(pointerId)){
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);

                // Calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                if (deltaX >= TOUCH_TOLERANCE ||
                    deltaY >= TOUCH_TOLERANCE) {
                    //move path to new location
                    path.quadTo(point.x, point.y,(newX + point.x)/2,(newY + point.y)/2);

                    //store new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    public void clear(){
        pathMap.clear(); //removes all of the paths
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate(); // refreshes the screen
    }

    private void touchEnded(int pointerId) {
        Path path = pathMap.get(pointerId);// get the corresponding path
        bitmaoCanvas.drawPath(path, paintLine); //draw to bitMapCanvas
        path.reset();
    }

    private void touchStarted(float x, float y, int pointerId) {
        Path path; // stores the path for a given touch
        Point point; // stores the last point in path

        if (pathMap.containsKey(pointerId)){
            path = pathMap.get(pointerId);
            point  = previousPointMap.get(pointerId);
        }else{
            path = new Path();
            pathMap.put(pointerId, path);
            point = new Point();
            previousPointMap.put(pointerId, point);
        }

        //  move to the coordinates of the touch
        path.moveTo(x,y);
        point.x = (int) x;
        point.y = (int) y;
    }
}
