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
import android.widget.TextView;

import flipagram.android.widgets.R;

public class CoachTextView extends TextView {

    private final Paint paint = new Paint();
    private final float rectRadius;

    public CoachTextView(Context context) {
        this(context, null);
    }

    public CoachTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoachTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        paint.setStyle(Paint.Style.FILL);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CoachTextView, defStyle, 0);
        paint.setColor(a.getColor(R.styleable.CoachTextView_coachFillColor, Color.TRANSPARENT));
        rectRadius = a.getDimension(R.styleable.CoachTextView_coachRadius,0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wide = MeasureSpec.getSize(widthMeasureSpec);
        int high = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(wide,MeasureSpec.getMode(widthMeasureSpec)),
                MeasureSpec.makeMeasureSpec(high,MeasureSpec.getMode(heightMeasureSpec)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the rectangle
        drawRect.set(0, 0, getWidth(), getHeight());
        if (paint.getColor()!=Color.TRANSPARENT){
            canvas.drawRoundRect(drawRect, rectRadius, rectRadius, paint);
        }
        // Draw the TextView
        super.onDraw(canvas);
    }
    private final RectF drawRect = new RectF();
}
