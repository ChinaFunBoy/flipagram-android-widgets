package flipagram.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import flipagram.android.widgets.R;

/**
 * TODO: document your custom view class.
 */
public class CheckerboardView extends View {
    private Paint linePaint = new Paint(Color.BLACK);
    private int horizontalSquares = 3;
    private int verticalSquares = 3;

    public CheckerboardView(Context context) {
        super(context);
        init(null, 0);
    }

    public CheckerboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CheckerboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
            attrs, R.styleable.CheckerboardView, defStyle, 0);

        linePaint.setColor(a.getColor(
            R.styleable.CheckerboardView_lineColor,
            linePaint.getColor()));

        linePaint.setStrokeWidth(a.getDimension(
            R.styleable.CheckerboardView_lineWidth,
            linePaint.getStrokeWidth()));

        horizontalSquares = a.getInteger(
            R.styleable.CheckerboardView_horizontalSquares,
            horizontalSquares);

        verticalSquares = a.getInteger(
            R.styleable.CheckerboardView_verticalSquares,
            verticalSquares);

        a.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        final float paddingLeft = getPaddingLeft();
        final float paddingTop = getPaddingTop();
        final float paddingRight = getPaddingRight();
        final float paddingBottom = getPaddingBottom();

        final float contentWidth = getWidth() - paddingLeft - paddingRight;
        final float contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw vertical lines
        if (verticalSquares>1){
            float vSquares = verticalSquares;
            for(float i=0;i<verticalSquares-1;i++){
                float x=paddingLeft+(contentWidth/vSquares)*(i+1);
                canvas.drawLine(
                    x,
                    paddingTop,
                    x,
                    contentHeight+paddingTop,
                    linePaint
                );
            }
        }

        if (horizontalSquares>1){
            float hSquares = horizontalSquares;
            for(float i=0;i<horizontalSquares-1;i++){
                float y=paddingTop+(contentHeight/hSquares)*(i+1);
                canvas.drawLine(
                    paddingLeft,
                    y,
                    contentWidth+paddingLeft,
                    y,
                    linePaint
                );
            }
        }
    }

    public int getLineColor() {
        return linePaint.getColor();
    }

    public void setLineColor(int lineColor) {
        linePaint.setColor(lineColor);
    }

    public float getLineWidth() {
        return linePaint.getStrokeWidth();
    }

    public void setLineWidth(float lineWidth) {
        linePaint.setStrokeWidth(lineWidth);
    }

    public int getHorizontalSquares() {
        return horizontalSquares;
    }

    public void setHorizontalSquares(int horizontalSquares) {
        this.horizontalSquares = horizontalSquares;
    }

    public int getVerticalSquares() {
        return verticalSquares;
    }

    public void setVerticalSquares(int verticalSquares) {
        this.verticalSquares = verticalSquares;
    }
}
