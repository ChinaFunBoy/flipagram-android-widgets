package flipagram.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import flipagram.android.widgets.R;

public class TriangleView extends View {

    public final static int POSITION_NORTH  = 0x1;
    public final static int POSITION_EAST   = 0x2;
    public final static int POSITION_SOUTH  = 0x4;
    public final static int POSITION_WEST   = 0x8;

    private final Paint paint = new Paint();
    private int direction;
    private final Path triangle = new Path();
    private final Point[] points = new Point[3];

    public TriangleView(Context context) {
        this(context, null);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        paint.setStyle(Paint.Style.FILL);
        for (int i=0; i< points.length; i++)
            points[i] = new Point();
        triangle.setFillType(Path.FillType.EVEN_ODD);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TriangleView, defStyle, 0);
        paint.setColor(a.getColor(R.styleable.TriangleView_triangleFillColor, Color.TRANSPARENT));
        direction = a.getInt(R.styleable.TriangleView_triangleDirection,POSITION_NORTH);
        a.recycle();
    }

    public void setDirection(int direction){
        if (
                direction!=POSITION_NORTH &&
                direction!=POSITION_SOUTH &&
                direction!=POSITION_EAST &&
                direction!=POSITION_WEST
        ){
            throw new IllegalArgumentException("Invalid direction");
        }

        this.direction = direction;
    }

    public void setTriangleFillColor(int color){
        paint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (paint.getColor()!=Color.TRANSPARENT){
            switch(direction){
                case POSITION_NORTH:
                    points[0].set(0,getHeight());
                    points[1].set(getWidth(),getHeight());
                    points[2].set(getWidth()>>1,0);
                    break;
                case POSITION_SOUTH:
                    points[0].set(0,0);
                    points[1].set(getWidth(),0);
                    points[2].set(getWidth()>>1,getHeight());
                    break;
                case POSITION_EAST:
                    points[0].set(0,0);
                    points[1].set(0,getHeight());
                    points[2].set(getWidth(),getHeight()>>1);
                    break;
                case POSITION_WEST:
                    points[0].set(getWidth(),0);
                    points[1].set(getWidth(),getHeight());
                    points[2].set(0,getHeight()>>1);
                    break;
                default:
                    throw new IllegalArgumentException("triangleDirection is invalid");
            }
            triangle.reset();
            triangle.moveTo(points[2].x,points[2].y);
            for( int i = 0; i<points.length; i++) {
                triangle.lineTo(points[i].x, points[i].y);
            }
            canvas.drawPath(triangle, paint);
        }
        // Draw the TextView
        super.onDraw(canvas);
    }
}
