package flipagram.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import flipagram.android.widgets.R;

public class CircleTextView extends TextView {
    private final Paint borderPaint = new Paint();
    private final Paint fillPaint   = new Paint();

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        borderPaint.setStyle(Paint.Style.STROKE);
        fillPaint.setStyle(Paint.Style.FILL);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CircleTextView, defStyle, 0);
        borderPaint.setStrokeWidth(a.getDimension(R.styleable.CircleTextView_circleBorderSize, 0));
        borderPaint.setColor(a.getColor(R.styleable.CircleTextView_circleBorderColor,Color.TRANSPARENT));
        fillPaint.setColor(a.getColor(R.styleable.CircleTextView_circleFillColor, Color.TRANSPARENT));
        a.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Make it a square based on the width
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (fillPaint.getColor()!=Color.TRANSPARENT){
            canvas.drawCircle(
                    ((float) getWidth()) / 2.0f,
                    ((float) getHeight()) / 2.0f,
                    ((float)getWidth())/2.0f,
                    fillPaint);
        }
        if (borderPaint.getStrokeWidth()>0 && borderPaint.getColor()!=Color.TRANSPARENT){
            canvas.drawCircle(
                    ((float) getWidth()) / 2.0f,
                    ((float)getHeight())/2.0f,
                    (getWidth()-borderPaint.getStrokeWidth())/2.0f,
                    borderPaint);
        }
        super.onDraw(canvas);
    }

    public void setBorderStrokeWidth(float width){
        borderPaint.setStrokeWidth(width);
    }

    public void setBorderColor(int color){
        borderPaint.setColor(color);
    }

    public void setFillColor(int color){
        fillPaint.setColor(color);
    }
}
